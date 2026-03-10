package gui.user;

import dal.ThongTinHanhKhachDAO;
import model.DatVeSession;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NhapHanhKhachGUI extends JPanel {
    private DatVeSession session;
    private JPanel pnlContainer;
    private List<HanhKhachCard> listCards = new ArrayList<>();

    public NhapHanhKhachGUI(DatVeSession session) {
        this.session = session;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // ================= 1. HEADER =================
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        JLabel lblTitle = new JLabel("BƯỚC 2: NHẬP THÔNG TIN HÀNH KHÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(220, 38, 38));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // ================= 2. FOOTER (GHIM CHẶT Ở ĐÁY) =================
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnBack = new JButton("⬅ Quay lại chọn ghế");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setPreferredSize(new Dimension(190, 40));

        JButton btnNext = new JButton("Tiếp tục chọn hành lý ⮕");
        btnNext.setBackground(new Color(34, 197, 94));
        btnNext.setForeground(Color.WHITE);
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext.setPreferredSize(new Dimension(220, 40));
        
        pnlFooter.add(btnBack); 
        pnlFooter.add(btnNext);
        add(pnlFooter, BorderLayout.SOUTH);

        // ================= 3. CONTENT (KHÓA KÍCH THƯỚC CHỐNG TRÀN) =================
        pnlContainer = new JPanel();
        pnlContainer.setLayout(new BoxLayout(pnlContainer, BoxLayout.Y_AXIS));
        pnlContainer.setBackground(new Color(245, 247, 250));
        pnlContainer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JScrollPane scroll = new JScrollPane(pnlContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        // DÒNG NÀY SẼ CỨU CÁI GIAO DIỆN CỦA BẠN (Ép cuộn, không cho phình to làm mất nút)
        scroll.setPreferredSize(new Dimension(800, 400));
        add(scroll, BorderLayout.CENTER);

        // ================= 4. THUẬT TOÁN ĐỔ DỮ LIỆU "KHÁCH QUEN" CỰC KỲ DỄ DÃI =================
        List<ThongTinHanhKhach> savedList = new ArrayList<>();
        try {
            ThongTinHanhKhachDAO hkDAO = new ThongTinHanhKhachDAO();
            List<ThongTinHanhKhach> allHK = hkDAO.selectAll(); 
            System.out.println("---- DEBUG THÔNG TIN KHÁCH ----"); // In ra Terminal để kiểm tra
            
            if (allHK != null && session.maNguoiDung != null) {
                for (ThongTinHanhKhach hk : allHK) {
                    // Dọn dẹp khoảng trắng dư thừa trong DB để so sánh cho chắc ăn
                    String dbUser = hk.getMaNguoiDung() != null ? hk.getMaNguoiDung().trim() : "";
                    if (session.maNguoiDung.equals(dbUser)) {
                        savedList.add(hk);
                    }
                }
            }
            System.out.println("Đã tìm thấy " + savedList.size() + " hồ sơ khách hàng cho tài khoản " + session.maNguoiDung);
        } catch (Exception ex) {
            System.err.println("Lỗi gọi Database ở trang Nhập Khách: " + ex.getMessage());
        }

        // ================= 5. TẠO CÁC FORM =================
        // Dùng hàm popPassenger để lấy đại bất kỳ khách nào đã lưu, khỏi cần phân biệt Loại
        for (int i = 1; i <= session.soNguoiLon; i++) addCard("NGƯỜI LỚN " + i, "Người lớn", popPassenger(savedList));
        for (int i = 1; i <= session.soTreEm; i++) addCard("TRẺ EM " + i, "Trẻ em", popPassenger(savedList));
        for (int i = 1; i <= session.soEmBe; i++) addCard("EM BÉ " + i, "Em bé", popPassenger(savedList));

        // ================= 6. SỰ KIỆN NÚT BẤM CỰC KỲ AN TOÀN =================
        btnBack.addActionListener(e -> {
            this.removeAll();
            this.setLayout(new BorderLayout());
            this.add(new PanelUserVeBan(session), BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
        });

        btnNext.addActionListener(e -> {
            try {
                session.danhSachHanhKhach.clear();
                for(HanhKhachCard card : listCards) {
                    session.danhSachHanhKhach.add(card.getData());
                }
                
                this.removeAll();
                this.setLayout(new BorderLayout());
                this.add(new gui.DichVuHanhLyGUI(session), BorderLayout.CENTER);
                this.revalidate(); 
                this.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Hàm lấy khách lưu sẵn: Không cần soi xét quá khắt khe, có là bốc ra điền luôn!
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
        pnlContainer.add(Box.createRigidArea(new Dimension(0, 20))); 
    }

    // ================= KHUNG NHẬP LIỆU (KHÔNG BỊ PHỒNG TO) =================
    class HanhKhachCard extends JPanel {
        private JTextField txtHoTen, txtCCCD, txtHoChieu, txtNgaySinh;
        private JComboBox<String> cbGioiTinh;
        private String loaiHK;

        public HanhKhachCard(String title, String type, ThongTinHanhKhach prefillData) {
            this.loaiHK = type;
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200,200,200)), 
                    title, TitledBorder.LEFT, TitledBorder.TOP, 
                    new Font("Segoe UI", Font.BOLD, 14), new Color(28, 48, 96)
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            // Khóa chiều cao tối đa của form lại, không cho nó phình to làm mất nút
            setMaximumSize(new Dimension(900, 170));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 15, 5, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtHoTen = createTextField();
            txtCCCD = createTextField();
            txtHoChieu = createTextField();
            txtNgaySinh = createTextField();
            cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
            cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Bố trí Dòng 1
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; add(new JLabel("Họ Tên (*):"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtHoTen, gbc);
            gbc.gridx = 2; gbc.weightx = 0; add(new JLabel("Giới tính:"), gbc);
            gbc.gridx = 3; gbc.weightx = 0.5; add(cbGioiTinh, gbc);

            // Bố trí Dòng 2
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; add(new JLabel("CCCD (*):"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; add(txtCCCD, gbc);
            gbc.gridx = 2; gbc.weightx = 0; add(new JLabel("Hộ chiếu:"), gbc);
            gbc.gridx = 3; gbc.weightx = 0.5; add(txtHoChieu, gbc);

            // Bố trí Dòng 3
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; add(new JLabel("Ngày sinh:"), gbc);
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

        private JTextField createTextField() {
            JTextField txt = new JTextField();
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txt.setPreferredSize(new Dimension(200, 30));
            return txt;
        }

        public ThongTinHanhKhach getData() throws Exception {
            String ten = txtHoTen.getText().trim();
            if(ten.isEmpty()) throw new Exception("Vui lòng nhập Họ Tên!");

            ThongTinHanhKhach hk = new ThongTinHanhKhach();
            hk.setMaNguoiDung(session.maNguoiDung);
            hk.setHoTen(ten);
            hk.setCccd(txtCCCD.getText().trim());
            hk.setHoChieu(txtHoChieu.getText().trim());
            hk.setGioiTinh(cbGioiTinh.getSelectedItem().toString());
            hk.setLoaiHanhKhach(this.loaiHK);
            // Bỏ qua validate ngày sinh nếu để trống để người dùng dễ thở
            return hk;
        }
    }
}