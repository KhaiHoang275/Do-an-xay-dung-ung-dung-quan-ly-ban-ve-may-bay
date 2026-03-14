package gui.user;

import dal.ThongTinHanhKhachDAO;
import model.DatVeSession;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NhapHanhKhachGUI extends JPanel {

    private final Color PRIMARY_COLOR = new Color(18, 32, 64); 
    private final Color ACCENT_COLOR = new Color(255, 193, 7); 
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 15);

    private DatVeSession session;
    private JPanel pnlContainer;
    private List<HanhKhachCard> listCards = new ArrayList<>();
    
    // Cờ nhận diện chuyến bay Quốc tế
    private boolean isQuocTe = false;

    public NhapHanhKhachGUI(DatVeSession session) {
        this.session = session;
        
        // KIỂM TRA XEM CÓ PHẢI CHUYẾN BAY QUỐC TẾ KHÔNG
        this.isQuocTe = checkIsQuocTe();
        
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);

        JPanel stepperWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        stepperWrapper.setOpaque(false);
        stepperWrapper.add(createStepper());
        scrollContent.add(stepperWrapper);
        scrollContent.add(Box.createVerticalStrut(30));

        List<ThongTinHanhKhach> savedList = loadOldData();

        pnlContainer = new JPanel();
        pnlContainer.setLayout(new BoxLayout(pnlContainer, BoxLayout.Y_AXIS));
        pnlContainer.setOpaque(false); 
        
        for (int i = 1; i <= session.soNguoiLon; i++) addCard("Hành khách " + i + " (Người lớn)", "Người lớn", popData(savedList));
        for (int i = 1; i <= session.soTreEm; i++) addCard("Hành khách (Trẻ em)", "Trẻ em", popData(savedList));
        for (int i = 1; i <= session.soEmBe; i++) addCard("Hành khách (Em bé)", "Em bé", popData(savedList));

        scrollContent.add(pnlContainer);

        JPanel topAlignPanel = new JPanel(new BorderLayout());
        topAlignPanel.setOpaque(false);
        topAlignPanel.add(scrollContent, BorderLayout.NORTH);

        JPanel marginPanel = new JPanel(new BorderLayout()); 
        marginPanel.setOpaque(false); 
        marginPanel.setBorder(BorderFactory.createEmptyBorder(20, 120, 20, 120)); 
        marginPanel.add(topAlignPanel, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(marginPanel); 
        scrollPane.setBorder(null); 
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        add(createStickyFooter(), BorderLayout.SOUTH);
    }
    
    // THUẬT TOÁN NHẬN DIỆN CHUYẾN BAY QUỐC TẾ
    private boolean checkIsQuocTe() {
        if (session == null || session.tenSanBayDi == null || session.tenSanBayDen == null) return false;
        
        String di = session.tenSanBayDi.toLowerCase();
        String den = session.tenSanBayDen.toLowerCase();

        // Các từ khóa nhận diện sân bay nằm trong lãnh thổ Việt Nam
        String[] vnAirports = {
            "tân sơn nhất", "nội bài", "đà nẵng", "cam ranh", "phú quốc",
            "cát bi", "vinh", "phú bài", "chu lai", "liên khương", "phù cát", "tuy hòa",
            "buôn ma thuột", "pleiku", "cần thơ", "cà mau", "côn đảo", "rạch giá",
            "điện biên", "đồng hới", "vân đồn", "thọ xuân", 
            "sgn", "han", "dad", "cxr", "pqc", "hph", "vii", "hui", "vcl", "dli", 
            "uih", "tbb", "bmv", "pxu", "vca", "cah", "vcs", "vkg", "din", "vdh", "vdo", "thd"
        };

        boolean diIsVN = false;
        boolean denIsVN = false;

        for (String kw : vnAirports) {
            if (di.contains(kw)) diIsVN = true;
            if (den.contains(kw)) denIsVN = true;
        }

        // Nếu Cả Đi và Đến đều ở VN -> Nội địa (false). Nếu có 1 cái ở nước ngoài -> Quốc tế (true)
        return !(diIsVN && denIsVN);
    }

    private List<ThongTinHanhKhach> loadOldData() {
        List<ThongTinHanhKhach> savedList = new ArrayList<>();
        try {
            ThongTinHanhKhachDAO hkDAO = new ThongTinHanhKhachDAO();
            List<ThongTinHanhKhach> allHK = hkDAO.selectAll(); 
            if (allHK != null && session.maNguoiDung != null) {
                for (ThongTinHanhKhach hk : allHK) {
                    if (session.maNguoiDung.equals(hk.getMaNguoiDung() != null ? hk.getMaNguoiDung().trim() : "")) {
                        savedList.add(hk);
                    }
                }
            }
        } catch (Exception ex) {}
        return savedList;
    }

    private ThongTinHanhKhach popData(List<ThongTinHanhKhach> list) {
        if(list != null && !list.isEmpty()) return list.remove(0);
        return null;
    }

    private void addCard(String title, String type, ThongTinHanhKhach prefillData) {
        HanhKhachCard card = new HanhKhachCard(title, type, prefillData);
        listCards.add(card); 
        
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(card);
        
        pnlContainer.add(wrapper); 
        pnlContainer.add(Box.createRigidArea(new Dimension(0, 20))); 
    }

    private void switchPage(JPanel newPanel) {
        Container container = SwingUtilities.getAncestorOfClass(gui.user.MainFrame.class, this);
        if (container instanceof gui.user.MainFrame) {
            gui.user.MainFrame mainFrame = (gui.user.MainFrame) container;
            mainFrame.getContentPane().remove(1); 
            newPanel.setOpaque(false);
            mainFrame.getContentPane().add(newPanel, BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }

    private JPanel createStickyFooter() {
        JPanel footer = new JPanel(new BorderLayout()); 
        footer.setBackground(Color.WHITE); 
        footer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)), new EmptyBorder(10, 50, 10, 50)));
        
        // KÍCH THƯỚC NÚT QUAY LẠI 150x45 (ĐÃ ĐỒNG BỘ VỚI NÚT ĐI TIẾP)
        JButton btnQuayLai = new JButton("Quay lại"); 
        btnQuayLai.setBackground(Color.WHITE); 
        btnQuayLai.setForeground(new Color(100, 100, 100)); 
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btnQuayLai.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); 
        btnQuayLai.setPreferredSize(new Dimension(150, 45)); 
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.setFocusPainted(false);
        
        btnQuayLai.addActionListener(e -> switchPage(new PanelUserVeBan(session)));
        footer.add(btnQuayLai, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); pnlRight.setOpaque(false);
        JPanel pnlTotalText = new JPanel(new GridLayout(2, 1)); pnlTotalText.setOpaque(false);
        JLabel lblTo = new JLabel("Tổng tiền tạm tính:", SwingConstants.RIGHT); lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        JLabel lblFooterTotal = new JLabel(vn.format(session.tongTienVe) + " VNĐ", SwingConstants.RIGHT); lblFooterTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlTotalText.add(lblTo); pnlTotalText.add(lblFooterTotal);
        
        // KÍCH THƯỚC NÚT ĐI TIẾP 150x45
        JButton btnNext = new JButton("Đi tiếp"); 
        btnNext.setBackground(new Color(255, 193, 7)); 
        btnNext.setForeground(new Color(18, 32, 64)); 
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        btnNext.setPreferredSize(new Dimension(150, 45));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        btnNext.setFocusPainted(false);
        
        btnNext.addActionListener(e -> {
            try {
                session.danhSachHanhKhach.clear();
                for(HanhKhachCard card : listCards) {
                    session.danhSachHanhKhach.add(card.getData()); 
                }
                switchPage(new gui.DichVuHanhLyGUI(session));
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE); 
            }
        });
        
        pnlRight.add(pnlTotalText); pnlRight.add(btnNext); footer.add(pnlRight, BorderLayout.EAST);
        return footer;
    }

    private JPanel createStepper() {
        // Mũi tên bằng Text để chống lỗi font
        JPanel pnlStepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); pnlStepper.setBackground(new Color(18, 32, 64, 200)); pnlStepper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        Font fontStep = new Font("Segoe UI", Font.BOLD, 16); Font fontArrow = new Font("Segoe UI", Font.BOLD, 18);
        JLabel step1 = new JLabel("1. Chuyến bay"); step1.setForeground(Color.WHITE); step1.setFont(fontStep); JLabel arr1 = new JLabel(" > "); arr1.setForeground(Color.WHITE); arr1.setFont(fontArrow);
        JLabel step2 = new JLabel("2. Hành khách"); step2.setForeground(ACCENT_COLOR); step2.setFont(fontStep); JLabel arr2 = new JLabel(" > "); arr2.setForeground(Color.WHITE); arr2.setFont(fontArrow);
        JLabel step3 = new JLabel("3. Dịch vụ"); step3.setForeground(Color.LIGHT_GRAY); step3.setFont(fontStep); JLabel arr3 = new JLabel(" > "); arr3.setForeground(Color.LIGHT_GRAY); arr3.setFont(fontArrow);
        JLabel step4 = new JLabel("4. Thanh toán"); step4.setForeground(Color.LIGHT_GRAY); step4.setFont(fontStep);
        pnlStepper.add(step1); pnlStepper.add(arr1); pnlStepper.add(step2); pnlStepper.add(arr2); pnlStepper.add(step3); pnlStepper.add(arr3); pnlStepper.add(step4);
        return pnlStepper;
    }

    class HanhKhachCard extends JPanel {
        private JTextField txtHoTen, txtCCCD, txtHoChieu; 
        private JFormattedTextField txtNgaySinh; 
        private JComboBox<String> cbGioiTinh; 
        private String loaiHK;
        
        public HanhKhachCard(String title, String type, ThongTinHanhKhach prefillData) {
            this.loaiHK = type; setLayout(new GridBagLayout()); setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220), 1, true), BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), " - " + title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), PRIMARY_COLOR)));
            
            setPreferredSize(new Dimension(1000, 220));
            setMaximumSize(new Dimension(1000, 250));
            
            GridBagConstraints gbc = new GridBagConstraints(); 
            gbc.insets = new Insets(15, 20, 15, 20); 
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtHoTen = createStyledTextField(); 
            txtCCCD = createStyledTextField(); 
            txtHoChieu = createStyledTextField();
            txtNgaySinh = createStyledDateField();

            // CHẶN NHẬP KÝ TỰ VÀO CCCD
            txtCCCD.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (!Character.isDigit(e.getKeyChar())) e.consume(); // Chỉ cho nhập số
                    if (txtCCCD.getText().length() >= 12) e.consume(); // Giới hạn 12 số
                }
            });
            
            cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
            cbGioiTinh.setPreferredSize(new Dimension(250, 45)); 
            cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            cbGioiTinh.setBackground(Color.WHITE);
            
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; add(createStyledLabel("Họ Tên (*):"), gbc); 
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtHoTen, gbc); 
            gbc.gridx = 2; gbc.weightx = 0; add(createStyledLabel("Giới tính:"), gbc); 
            gbc.gridx = 3; gbc.weightx = 0.5; add(cbGioiTinh, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; add(createStyledLabel(type.equals("Em bé") ? "Khai sinh (*):" : "CCCD (*):"), gbc); 
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtCCCD, gbc); 
            
            // HIỂN THỊ GỢI Ý HỘ CHIẾU NẾU LÀ CHUYẾN BAY QUỐC TẾ
            String hoChieuLabel = isQuocTe ? "Hộ chiếu (*):" : "Hộ chiếu:";
            gbc.gridx = 2; gbc.weightx = 0; add(createStyledLabel(hoChieuLabel), gbc); 
            gbc.gridx = 3; gbc.weightx = 0.5; add(txtHoChieu, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; add(createStyledLabel("Ngày sinh (*):"), gbc); 
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtNgaySinh, gbc);
            
            if (prefillData != null) {
                fillData(prefillData);
            }
        }
        
        private JTextField createStyledTextField() {
            JTextField txt = new JTextField();
            txt.setPreferredSize(new Dimension(250, 45)); 
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            return txt;
        }

        private JFormattedTextField createStyledDateField() {
            JFormattedTextField txt;
            try { 
                txt = new JFormattedTextField(new javax.swing.text.MaskFormatter("##/##/####")); 
            } catch(Exception e){ txt = new JFormattedTextField(); }
            txt.setPreferredSize(new Dimension(250, 45));
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            return txt;
        }

        private JLabel createStyledLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(FONT_LABEL);
            // Highlight màu đỏ cho chữ Hộ chiếu nếu bay quốc tế
            if (text.contains("Hộ chiếu (*):")) {
                lbl.setForeground(new Color(220, 38, 38)); 
            } else {
                lbl.setForeground(new Color(80, 90, 100));
            }
            return lbl;
        }
        
        public void fillData(ThongTinHanhKhach prefillData) {
            try {
                if (prefillData.getHoTen() != null) txtHoTen.setText(prefillData.getHoTen().trim());
                if (prefillData.getCccd() != null) txtCCCD.setText(prefillData.getCccd().trim());
                if (prefillData.getHoChieu() != null) txtHoChieu.setText(prefillData.getHoChieu().trim());
                if (prefillData.getGioiTinh() != null) { String gt = prefillData.getGioiTinh().toLowerCase(); cbGioiTinh.setSelectedItem(gt.contains("nữ") ? "Nữ" : "Nam"); }
                if (prefillData.getNgaySinh() != null) txtNgaySinh.setText(prefillData.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (Exception e) {}
        }
        
        // =========================================================================
        // HÀM KIỂM TRA ĐỊNH DẠNG VÀ RÀNG BUỘC CHẶT CHẼ
        // =========================================================================
        public ThongTinHanhKhach getData() throws Exception {
            String ten = txtHoTen.getText().trim(); 
            String cccd = txtCCCD.getText().trim(); 
            String hoChieu = txtHoChieu.getText().trim();
            String ngaySinhStr = txtNgaySinh.getText().trim();

            // 1. Kiểm tra rỗng
            if(ten.isEmpty() || cccd.isEmpty()) {
                throw new Exception("Vui lòng điền đầy đủ Họ Tên và CCCD/Khai sinh cho khách: " + (ten.isEmpty() ? "Chưa nhập tên" : ten));
            }

            // 2. Ràng buộc Họ tên không được chứa số
            if (ten.matches(".*\\d.*")) {
                throw new Exception("Họ tên không được chứa chữ số! Vui lòng kiểm tra lại thông tin khách: " + ten);
            }

            // 3. Ràng buộc CCCD/Khai sinh phải ĐÚNG 12 chữ số
            if (!cccd.matches("^\\d{12}$")) {
                throw new Exception("CCCD/Khai sinh của khách '" + ten + "' bắt buộc phải có đủ 12 chữ số!");
            }

            // 4. RÀNG BUỘC MỚI: ÉP BUỘC NHẬP HỘ CHIẾU NẾU LÀ BAY QUỐC TẾ
            if (isQuocTe && hoChieu.isEmpty()) {
                throw new Exception("Hành trình có bao gồm chuyến bay Quốc tế. Vui lòng nhập số Hộ chiếu cho hành khách: " + ten);
            }

            // 5. Ràng buộc Ngày sinh
            if(ngaySinhStr.contains("_")) {
                throw new Exception("Vui lòng nhập đầy đủ ngày sinh cho khách: " + ten);
            }

            LocalDate ngaySinh;
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);
                ngaySinh = LocalDate.parse(ngaySinhStr, formatter);
            } catch (java.time.format.DateTimeParseException e) {
                throw new Exception("Ngày sinh của khách '" + ten + "' không hợp lệ (VD: 28/02/2000). Vui lòng nhập đúng ngày thực tế!");
            }

            // 6. Tính tuổi và kiểm tra Logic tuổi
            int age = java.time.Period.between(ngaySinh, LocalDate.now()).getYears();

            if (age > 200 || age < 0) {
                throw new Exception("Năm sinh của khách '" + ten + "' không hợp lý (Tuổi không được âm hoặc vượt quá 200 tuổi).");
            }

            if (loaiHK.equals("Người lớn") && age < 12) {
                throw new Exception("Khách '" + ten + "' là Người lớn, tuổi phải từ 12 trở lên (Tuổi hiện tại: " + age + ").");
            } else if (loaiHK.equals("Trẻ em") && (age < 2 || age >= 12)) { 
                throw new Exception("Khách '" + ten + "' là Trẻ em, tuổi phải từ 2 đến dưới 12 tuổi (Tuổi hiện tại: " + age + ").");
            } else if (loaiHK.equals("Em bé") && age >= 2) {
                throw new Exception("Khách '" + ten + "' là Em bé, tuổi phải dưới 2 tuổi (Tuổi hiện tại: " + age + ").");
            }

            ThongTinHanhKhach hk = new ThongTinHanhKhach(); 
            hk.setMaNguoiDung(session.maNguoiDung); 
            hk.setHoTen(ten); 
            hk.setCccd(cccd); 
            hk.setHoChieu(hoChieu); 
            hk.setGioiTinh(cbGioiTinh.getSelectedItem().toString()); 
            hk.setNgaySinh(ngaySinh); 
            hk.setLoaiHanhKhach(this.loaiHK);
            return hk;
        }
    }
}