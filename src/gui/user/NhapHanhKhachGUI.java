package gui.user;

import dal.ThongTinHanhKhachDAO;
import model.DatVeSession;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
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
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);

    private DatVeSession session;
    private JPanel pnlContainer;
    private List<HanhKhachCard> listCards = new ArrayList<>();

    public NhapHanhKhachGUI(DatVeSession session) {
        this.session = session;
        setLayout(new BorderLayout());
        setOpaque(false); // Trong suốt để thấy nền từ MainFrame

        // Tạo khung bọc Stepper
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        pnlHeader.setOpaque(false);
        pnlHeader.add(createStepper());
        add(pnlHeader, BorderLayout.NORTH);

        // Khung bọc danh sách thẻ (Khắc phục lỗi giãn dọc)
        pnlContainer = new JPanel();
        pnlContainer.setLayout(new BoxLayout(pnlContainer, BoxLayout.Y_AXIS));
        pnlContainer.setOpaque(false); 
        
        for (int i = 1; i <= session.soNguoiLon; i++) addCard("Hành khách " + i + " (Người lớn)", "Người lớn", null);
        for (int i = 1; i <= session.soTreEm; i++) addCard("Hành khách (Trẻ em)", "Trẻ em", null);
        for (int i = 1; i <= session.soEmBe; i++) addCard("Hành khách (Em bé)", "Em bé", null);

        // Đẩy toàn bộ danh sách lên trên cùng
        JPanel pushUpPanel = new JPanel(new BorderLayout());
        pushUpPanel.setOpaque(false);
        pushUpPanel.add(pnlContainer, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pushUpPanel); 
        scrollPane.setBorder(null); 
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        add(createStickyFooter(), BorderLayout.SOUTH);
        
        loadOldData();
    }

    private void loadOldData() {
        List<ThongTinHanhKhach> savedList = new ArrayList<>();
        try {
            ThongTinHanhKhachDAO hkDAO = new ThongTinHanhKhachDAO();
            List<ThongTinHanhKhach> allHK = hkDAO.selectAll(); 
            if (allHK != null && session.maNguoiDung != null) {
                for (ThongTinHanhKhach hk : allHK) {
                    if (session.maNguoiDung.equals(hk.getMaNguoiDung() != null ? hk.getMaNguoiDung().trim() : "")) savedList.add(hk);
                }
            }
        } catch (Exception ex) {}
        
        for(HanhKhachCard card : listCards) {
            if(savedList.isEmpty()) break;
            card.fillData(savedList.remove(0));
        }
    }

    // ĐÃ SỬA: Bọc card vào FlowLayout để khóa kích thước cứng
    private void addCard(String title, String type, ThongTinHanhKhach prefillData) {
        HanhKhachCard card = new HanhKhachCard(title, type, prefillData);
        listCards.add(card); 
        
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.add(card);
        
        pnlContainer.add(wrapper); 
        pnlContainer.add(Box.createRigidArea(new Dimension(0, 10))); 
    }

    // Hàm chuyển trang mượt mà
    private void switchPage(JPanel newPanel) {
        Container container = SwingUtilities.getAncestorOfClass(MainFrame.class, this);
        if (container instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) container;
            // Xóa Component Content cũ
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
        
        JButton btnQuayLai = new JButton("Quay lại"); 
        btnQuayLai.setBackground(Color.WHITE); btnQuayLai.setForeground(new Color(100, 100, 100)); btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 16)); btnQuayLai.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); btnQuayLai.setPreferredSize(new Dimension(150, 45));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnQuayLai.addActionListener(e -> switchPage(new PanelUserVeBan(session)));
        footer.add(btnQuayLai, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); pnlRight.setOpaque(false);
        JPanel pnlTotalText = new JPanel(new GridLayout(2, 1)); pnlTotalText.setOpaque(false);
        JLabel lblTo = new JLabel("Tổng tiền tạm tính:", SwingConstants.RIGHT); lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        JLabel lblFooterTotal = new JLabel(vn.format(session.tongTienVe) + " VNĐ", SwingConstants.RIGHT); lblFooterTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlTotalText.add(lblTo); pnlTotalText.add(lblFooterTotal);
        
        JButton btnNext = new JButton("Đi tiếp ➔"); btnNext.setBackground(new Color(255, 193, 7)); btnNext.setForeground(new Color(18, 32, 64)); btnNext.setFont(new Font("Segoe UI", Font.BOLD, 18)); btnNext.setPreferredSize(new Dimension(150, 45));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.setFocusPainted(false);
        
        btnNext.addActionListener(e -> {
            try {
                session.danhSachHanhKhach.clear();
                for(HanhKhachCard card : listCards) session.danhSachHanhKhach.add(card.getData()); 
                switchPage(new gui.DichVuHanhLyGUI(session));
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE); }
        });
        pnlRight.add(pnlTotalText); pnlRight.add(btnNext); footer.add(pnlRight, BorderLayout.EAST);
        return footer;
    }

    private JPanel createStepper() {
        JPanel pnlStepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); pnlStepper.setBackground(new Color(18, 32, 64, 200)); pnlStepper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        Font fontStep = new Font("Segoe UI", Font.BOLD, 16); Font fontArrow = new Font("Segoe UI", Font.BOLD, 18);
        JLabel step1 = new JLabel("1. Chuyến bay"); step1.setForeground(Color.WHITE); step1.setFont(fontStep); JLabel arr1 = new JLabel(" ➔ "); arr1.setForeground(Color.WHITE); arr1.setFont(fontArrow);
        JLabel step2 = new JLabel("2. Hành khách"); step2.setForeground(ACCENT_COLOR); step2.setFont(fontStep); JLabel arr2 = new JLabel(" ➔ "); arr2.setForeground(Color.WHITE); arr2.setFont(fontArrow);
        JLabel step3 = new JLabel("3. Dịch vụ"); step3.setForeground(Color.LIGHT_GRAY); step3.setFont(fontStep); JLabel arr3 = new JLabel(" ➔ "); arr3.setForeground(Color.LIGHT_GRAY); arr3.setFont(fontArrow);
        JLabel step4 = new JLabel("4. Thanh toán"); step4.setForeground(Color.LIGHT_GRAY); step4.setFont(fontStep);
        pnlStepper.add(step1); pnlStepper.add(arr1); pnlStepper.add(step2); pnlStepper.add(arr2); pnlStepper.add(step3); pnlStepper.add(arr3); pnlStepper.add(step4);
        return pnlStepper;
    }

    class HanhKhachCard extends JPanel {
        private JTextField txtHoTen, txtCCCD, txtHoChieu; private JFormattedTextField txtNgaySinh; private JComboBox<String> cbGioiTinh; private String loaiHK;
        public HanhKhachCard(String title, String type, ThongTinHanhKhach prefillData) {
            this.loaiHK = type; setLayout(new GridBagLayout()); setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220), 1, true), BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), " ✈ " + title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR)));
            
            // Cấu hình cứng chiều cao
            setPreferredSize(new Dimension(800, 160));
            
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 15, 8, 15); gbc.fill = GridBagConstraints.HORIZONTAL;

            txtHoTen = new JTextField(); txtCCCD = new JTextField(); txtHoChieu = new JTextField();
            try { txtNgaySinh = new JFormattedTextField(new javax.swing.text.MaskFormatter("##/##/####")); } catch(Exception e){ txtNgaySinh = new JFormattedTextField(); }
            cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
            
            gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Họ Tên (*):"), gbc); gbc.gridx = 1; add(txtHoTen, gbc); gbc.gridx = 2; add(new JLabel("Giới tính:"), gbc); gbc.gridx = 3; add(cbGioiTinh, gbc);
            gbc.gridx = 0; gbc.gridy = 1; add(new JLabel(type.equals("Em bé") ? "Khai sinh (*):" : "CCCD (*):"), gbc); gbc.gridx = 1; add(txtCCCD, gbc); gbc.gridx = 2; add(new JLabel("Hộ chiếu:"), gbc); gbc.gridx = 3; add(txtHoChieu, gbc);
            gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Ngày sinh (*):"), gbc); gbc.gridx = 1; add(txtNgaySinh, gbc);
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
        
        public ThongTinHanhKhach getData() throws Exception {
            String ten = txtHoTen.getText().trim(); String cccd = txtCCCD.getText().trim(); String ngaySinhStr = txtNgaySinh.getText().trim();
            if(ten.isEmpty() || cccd.isEmpty() || ngaySinhStr.contains("_")) throw new Exception("Vui lòng điền đầy đủ thông tin cho khách: " + ten);
            ThongTinHanhKhach hk = new ThongTinHanhKhach(); hk.setMaNguoiDung(session.maNguoiDung); hk.setHoTen(ten); hk.setCccd(cccd); hk.setHoChieu(txtHoChieu.getText().trim()); hk.setGioiTinh(cbGioiTinh.getSelectedItem().toString()); hk.setNgaySinh(LocalDate.parse(ngaySinhStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))); hk.setLoaiHanhKhach(this.loaiHK);
            return hk;
        }
    }
}