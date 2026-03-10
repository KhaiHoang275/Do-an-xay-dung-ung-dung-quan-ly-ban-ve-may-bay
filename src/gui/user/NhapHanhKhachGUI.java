package gui.user;

import dal.ThongTinHanhKhachDAO;
import model.DatVeSession;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NhapHanhKhachGUI extends JPanel {

    // ===== BẢNG MÀU THƯƠNG HIỆU AIRLINER =====
    private final Color PRIMARY_COLOR = new Color(18, 32, 64); // Xanh Navy
    private final Color ACCENT_COLOR = new Color(255, 193, 7); // Vàng Gold
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);

    private DatVeSession session;
    private JPanel pnlContainer;
    private List<HanhKhachCard> listCards = new ArrayList<>();

    public NhapHanhKhachGUI(DatVeSession session) {
        this.session = session;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // ================= 1. HEADER =================
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        pnlHeader.setBackground(BG_MAIN);
        
        JLabel lblTitle = new JLabel("BƯỚC 2: NHẬP THÔNG TIN HÀNH KHÁCH");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(PRIMARY_COLOR);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // ================= 2. FOOTER (GHIM CHẶT Ở ĐÁY) =================
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        pnlFooter.setBackground(BG_MAIN);

        JButton btnBack = new JButton("Quay lại chọn ghế");
        JButton btnNext = new JButton("Tiếp tục chọn hành lý ⮕");

        styleSecondaryButton(btnBack);
        stylePrimaryButton(btnNext);
        btnBack.setPreferredSize(new Dimension(200, 45));
        btnNext.setPreferredSize(new Dimension(250, 45));
        
        pnlFooter.add(btnNext); // Để nút Tới bên phải
        pnlFooter.add(btnBack); // Nút Lui bên trái
        add(pnlFooter, BorderLayout.SOUTH);

        // ================= 3. CONTENT (DANH SÁCH FORM NHẬP) =================
        pnlContainer = new JPanel();
        pnlContainer.setLayout(new BoxLayout(pnlContainer, BoxLayout.Y_AXIS));
        pnlContainer.setBackground(BG_MAIN);
        
        // Thêm khoảng đệm bên trái phải để form không bị bám sát viền màn hình
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BG_MAIN);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 150));
        wrapperPanel.add(pnlContainer, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapperPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ================= 4. THUẬT TOÁN ĐỔ DỮ LIỆU KHÁCH QUEN =================
        List<ThongTinHanhKhach> savedList = new ArrayList<>();
        try {
            ThongTinHanhKhachDAO hkDAO = new ThongTinHanhKhachDAO();
            List<ThongTinHanhKhach> allHK = hkDAO.selectAll(); 
            
            if (allHK != null && session.maNguoiDung != null) {
                for (ThongTinHanhKhach hk : allHK) {
                    String dbUser = hk.getMaNguoiDung() != null ? hk.getMaNguoiDung().trim() : "";
                    if (session.maNguoiDung.equals(dbUser)) {
                        savedList.add(hk);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Lỗi gọi Database ở trang Nhập Khách: " + ex.getMessage());
        }

        // ================= 5. TẠO CÁC FORM =================
        for (int i = 1; i <= session.soNguoiLon; i++) addCard("Hành khách " + i + " (Người lớn)", "Người lớn", popPassenger(savedList));
        for (int i = 1; i <= session.soTreEm; i++) addCard("Hành khách (Trẻ em)", "Trẻ em", popPassenger(savedList));
        for (int i = 1; i <= session.soEmBe; i++) addCard("Hành khách (Em bé)", "Em bé", popPassenger(savedList));

        // ================= 6. SỰ KIỆN NÚT BẤM =================
        btnBack.addActionListener(e -> {
            this.removeAll();
            this.setLayout(new BorderLayout());
            this.add(new PanelUserVeBan(session), BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
        });

        // NÚT "TIẾP TỤC" ĐÃ ĐƯỢC CHỈNH CHUẨN ĐỂ MỞ TRANG HÀNH LÝ
        btnNext.addActionListener(e -> {
            try {
                session.danhSachHanhKhach.clear();
                for(HanhKhachCard card : listCards) {
                    session.danhSachHanhKhach.add(card.getData());
                }
                
                this.removeAll();
                this.setLayout(new BorderLayout());
                
                // GỌI TRANG DỊCH VỤ HÀNH LÝ LÊN
                this.add(new gui.DichVuHanhLyGUI(session), BorderLayout.CENTER);
                
                this.revalidate(); 
                this.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cảnh báo thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private ThongTinHanhKhach popPassenger(List<ThongTinHanhKhach> list) {
        if (list != null && !list.isEmpty()) {
            return list.remove(0); 
        }
        return null;
    }

    private void addCard(String title, String type, ThongTinHanhKhach prefillData) {
        HanhKhachCard card = new HanhKhachCard(title, type, prefillData);
        listCards.add(card);
        pnlContainer.add(card);
        pnlContainer.add(Box.createRigidArea(new Dimension(0, 25))); // Khoảng cách giữa các thẻ
    }

    // ================= HELPER STYLE BUTTONS =================
    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR); 
        btn.setForeground(PRIMARY_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btn.setFocusPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); 
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        btn.setFocusPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ================= KHUNG NHẬP LIỆU (CARD) ĐẸP MẮT =================
    class HanhKhachCard extends JPanel {
        private JTextField txtHoTen, txtCCCD, txtHoChieu, txtNgaySinh;
        private JComboBox<String> cbGioiTinh;
        private String loaiHK;

        public HanhKhachCard(String title, String type, ThongTinHanhKhach prefillData) {
            this.loaiHK = type;
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220), 1, true), 
                BorderFactory.createTitledBorder(
                    BorderFactory.createEmptyBorder(), 
                    " ✈ " + title, TitledBorder.LEFT, TitledBorder.TOP, 
                    new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR
                )
            ));
            
            setMaximumSize(new Dimension(800, 180));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 15, 8, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtHoTen = createTextField();
            txtCCCD = createTextField();
            txtHoChieu = createTextField();
            txtNgaySinh = createTextField();
            txtNgaySinh.setToolTipText("Định dạng: dd/MM/yyyy");
            
            cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
            cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cbGioiTinh.setBackground(Color.WHITE);

            // Bố trí Dòng 1: Họ Tên & Giới tính
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; add(createStyledLabel("Họ Tên (*):"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtHoTen, gbc);
            gbc.gridx = 2; gbc.weightx = 0; add(createStyledLabel("Giới tính:"), gbc);
            gbc.gridx = 3; gbc.weightx = 0.5; add(cbGioiTinh, gbc);

            // Bố trí Dòng 2: CCCD & Hộ chiếu
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; add(createStyledLabel(type.equals("Em bé") ? "Giấy khai sinh:" : "CCCD (*):"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtCCCD, gbc);
            gbc.gridx = 2; gbc.weightx = 0; add(createStyledLabel("Hộ chiếu:"), gbc);
            gbc.gridx = 3; gbc.weightx = 0.5; add(txtHoChieu, gbc);

            // Bố trí Dòng 3: Ngày sinh
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; add(createStyledLabel("Ngày sinh:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtNgaySinh, gbc);

            // ======= TIẾN HÀNH ĐIỀN DỮ LIỆU =======
            if (prefillData != null) {
                try {
                    if (prefillData.getHoTen() != null) txtHoTen.setText(prefillData.getHoTen().trim());
                    if (prefillData.getCccd() != null) txtCCCD.setText(prefillData.getCccd().trim());
                    if (prefillData.getHoChieu() != null) txtHoChieu.setText(prefillData.getHoChieu().trim());
                    if (prefillData.getGioiTinh() != null) {
                        String gt = prefillData.getGioiTinh().toLowerCase();
                        cbGioiTinh.setSelectedItem(gt.contains("nữ") ? "Nữ" : "Nam");
                    }
                    if (prefillData.getNgaySinh() != null) {
                        txtNgaySinh.setText(prefillData.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi điền Form: " + e.getMessage());
                }
            }
        }

        private JLabel createStyledLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(FONT_LABEL);
            lbl.setForeground(new Color(100, 110, 120));
            return lbl;
        }

        private JTextField createTextField() {
            JTextField txt = new JTextField();
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            txt.setPreferredSize(new Dimension(200, 35));
            // Đổi màu viền nhạt lại cho thanh thoát
            txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            return txt;
        }

        public ThongTinHanhKhach getData() throws Exception {
            String ten = txtHoTen.getText().trim();
            String cccd = txtCCCD.getText().trim();

            if(ten.isEmpty()) throw new Exception("Vui lòng nhập Họ và Tên cho tất cả hành khách!");
            if(cccd.isEmpty() && !loaiHK.equals("Em bé")) {
                throw new Exception("Vui lòng nhập số CCCD cho hành khách Người lớn/Trẻ em!");
            }

            ThongTinHanhKhach hk = new ThongTinHanhKhach();
            hk.setMaNguoiDung(session.maNguoiDung);
            hk.setHoTen(ten);
            hk.setCccd(cccd);
            hk.setHoChieu(txtHoChieu.getText().trim());
            hk.setGioiTinh(cbGioiTinh.getSelectedItem().toString());
            hk.setLoaiHanhKhach(this.loaiHK);
            return hk;
        }
    }
}