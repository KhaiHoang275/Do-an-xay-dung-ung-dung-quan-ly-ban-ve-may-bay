package gui;

import dal.*;
import model.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class ThanhToanGUI extends JPanel {

    private DatVeSession session;
    private KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
    
    private JLabel lblTongVe, lblTongDV, lblGiamGia, lblTongThanhToan;
    private JComboBox<KhuyenMai> cboKhuyenMai;
    private JRadioButton radTienMat, radThe, radQR;
    private BigDecimal finalAmount = BigDecimal.ZERO;

    public ThanhToanGUI(DatVeSession session) {
        this.session = session;
        if(session.tongTienVe == null || session.tongTienVe.compareTo(BigDecimal.ZERO) == 0) {
            session.tongTienVe = new BigDecimal("1200000").multiply(new BigDecimal(session.getTongSoHanhKhach()));
        }
        initComponents();
        loadPromotions();
        updatePriceDisplay();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        JLabel lblTitle = new JLabel("BƯỚC 4: XÁC NHẬN HÀNH TRÌNH & THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(220, 38, 38));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlCenter.setBackground(new Color(245, 247, 250));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.Y_AXIS));
        pnlSummary.setBackground(Color.WHITE);
        pnlSummary.setBorder(createCustomTitledBorder("THÔNG TIN ĐẶT CHỖ"));

        pnlSummary.add(createSummaryRow("Chuyến bay:", session.maChuyenBay));
        pnlSummary.add(createSummaryRow("Loại vé:", session.loaiVe));
        
        StringBuilder sbGhe = new StringBuilder();
        if(session.danhSachGhe != null) {
            for(GheMayBay g : session.danhSachGhe) sbGhe.append(g.getSoGhe()).append(" ");
        }
        pnlSummary.add(createSummaryRow("Ghế đã chọn:", sbGhe.toString()));
        
        pnlSummary.add(Box.createRigidArea(new Dimension(0, 15)));
        JLabel lblHK = new JLabel("Danh sách hành khách:");
        lblHK.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlSummary.add(lblHK);
        
        if(session.danhSachHanhKhach != null) {
            for(ThongTinHanhKhach hk : session.danhSachHanhKhach) {
                JLabel l = new JLabel("  • " + hk.getHoTen() + " [" + hk.getLoaiHanhKhach() + "]");
                l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                pnlSummary.add(l);
            }
        }

        JPanel pnlBilling = new JPanel();
        pnlBilling.setLayout(new BoxLayout(pnlBilling, BoxLayout.Y_AXIS));
        pnlBilling.setBackground(Color.WHITE);
        pnlBilling.setBorder(createCustomTitledBorder("CHI TIẾT THANH TOÁN"));

        lblTongVe = createPriceLabel("Tiền vé cơ bản: 0 VNĐ");
        lblTongDV = createPriceLabel("Hành lý & Dịch vụ: 0 VNĐ");
        lblGiamGia = createPriceLabel("Giảm giá: 0 VNĐ");
        
        lblTongThanhToan = new JLabel("TỔNG CỘNG: 0 VNĐ");
        lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongThanhToan.setForeground(new Color(220, 38, 38));

        cboKhuyenMai = new JComboBox<>();
        cboKhuyenMai.setMaximumSize(new Dimension(400, 35));
        cboKhuyenMai.addActionListener(e -> updatePriceDisplay());

        pnlBilling.add(lblTongVe);
        pnlBilling.add(lblTongDV);
        pnlBilling.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlBilling.add(new JLabel("Mã giảm giá (nếu có):"));
        pnlBilling.add(cboKhuyenMai);
        pnlBilling.add(lblGiamGia);
        pnlBilling.add(new JSeparator());
        pnlBilling.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlBilling.add(lblTongThanhToan);
        
        pnlBilling.add(Box.createRigidArea(new Dimension(0, 25)));
        pnlBilling.add(new JLabel("Phương thức thanh toán:"));
        radTienMat = new JRadioButton("Tiền mặt", true);
        radThe = new JRadioButton("Thẻ tín dụng / ATM");
        radQR = new JRadioButton("Quét mã QR");
        ButtonGroup grp = new ButtonGroup();
        grp.add(radTienMat); grp.add(radThe); grp.add(radQR);
        pnlBilling.add(radTienMat); pnlBilling.add(radThe); pnlBilling.add(radQR);

        pnlCenter.add(pnlSummary);
        pnlCenter.add(pnlBilling);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnBack = new JButton("⬅ Quay lại Dịch vụ");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setPreferredSize(new Dimension(180, 45));

        JButton btnConfirm = new JButton("XÁC NHẬN THANH TOÁN");
        btnConfirm.setBackground(new Color(34, 197, 94));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirm.setPreferredSize(new Dimension(250, 45));

        pnlFooter.add(btnBack);
        pnlFooter.add(btnConfirm);
        add(pnlFooter, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> {
            Container parent = this.getParent();
            parent.removeAll();
            parent.setLayout(new BorderLayout());
            parent.add(new gui.DichVuHanhLyGUI(session), BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        });

        // BẮT SỰ KIỆN GỌI ĐẾN TRÙM CUỐI
        btnConfirm.addActionListener(e -> processBooking());
    }

    private void loadPromotions() {
        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem(null); 

        try {
      
            List<KhuyenMai> list = kmDAO.getKhuyenMaiHopLe(); 
            if(list != null && !list.isEmpty()) {
                for(KhuyenMai km : list) {
                    cboKhuyenMai.addItem(km);
                }
            } else {
                System.out.println("Không có mã KM nào khả dụng trong thời gian này.");
            }
        } catch(Exception e) { 
            e.printStackTrace(); 
        }

        cboKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KhuyenMai) {
                    KhuyenMai km = (KhuyenMai) value;
                    String dvi = "PHAN_TRAM".equals(km.getLoaiKM()) ? "%" : " VNĐ";
                    setText(km.getMaKhuyenMai() + " - Giảm " + km.getGiaTri() + dvi);
                } else {
                    setText("--- Không áp dụng mã ---");
                }
                return this;
            }
        });
    }
    
    private void updatePriceDisplay() {
        BigDecimal base = session.tongTienVe;
        BigDecimal sv = session.tongTienDichVu;
        BigDecimal discount = BigDecimal.ZERO;

        KhuyenMai selected = (KhuyenMai) cboKhuyenMai.getSelectedItem();
        if(selected != null) {
            if("PHAN_TRAM".equals(selected.getLoaiKM()) || "Phần trăm".equalsIgnoreCase(selected.getLoaiKM())) {
                discount = base.multiply(selected.getGiaTri()).divide(new BigDecimal("100"));
            } else {
                discount = selected.getGiaTri() != null ? selected.getGiaTri() : BigDecimal.ZERO;
            }
        }

        finalAmount = base.add(sv).subtract(discount);
        if(finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;

        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblTongVe.setText("Tiền vé cơ bản: " + vn.format(base) + " VNĐ");
        lblTongDV.setText("Hành lý & Dịch vụ: " + vn.format(sv) + " VNĐ");
        lblGiamGia.setText("Giảm giá: -" + vn.format(discount) + " VNĐ");
        lblTongThanhToan.setText("TỔNG CỘNG: " + vn.format(finalAmount) + " VNĐ");
    }

    // =========================================================================================
    // HÀM TRÙM CUỐI - LƯU TẤT CẢ VÀO DATABASE (KHÔNG BAO GIỜ LỖI KHÓA NGOẠI NỮA)
    // =========================================================================================
    private void processBooking() {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán số tiền: " + lblTongThanhToan.getText() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = db.DBConnection.getConnection();
            conn.setAutoCommit(false); 

            // 1. TẠO PHIẾU ĐẶT VÉ
            String maPDV = "P" + (System.currentTimeMillis() % 1000000);
            String sqlPDV = "INSERT INTO PhieuDatVe (maPhieuDatVe, maNguoiDung, tongTien, ngayDat, soLuongVe, trangThaiThanhToan) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPDV)) {
                ps.setString(1, maPDV);
                ps.setString(2, session.maNguoiDung);
                ps.setBigDecimal(3, finalAmount);
                ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps.setInt(5, session.getTongSoHanhKhach());
                ps.setString(6, "Đã thanh toán");
                ps.executeUpdate();
            }

            // Các câu lệnh SQL chuẩn bị
            String sqlHK = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, hoTen, cccd, hoChieu, gioiTinh, ngaySinh, loaiHanhKhach) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlVe = "INSERT INTO VeBan (maVe, maPhieuDatVe, maHK, maChuyenBay, maGhe, maHangVe, loaiHK, loaiVe, giaVe, trangThaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlGhe = "UPDATE GheMayBay SET trangThai = 'DA_DAT' WHERE maGhe = ?";

            String maHangVeAnToan = (session.maHangVe != null) ? session.maHangVe : "ECO";
            String loaiVeAnToan = (session.loaiVe != null) ? session.loaiVe : "Một chiều";

            // 2. VÒNG LẶP: LƯU HÀNH KHÁCH -> LƯU VÉ -> CẬP NHẬT GHẾ
            for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                GheMayBay ghe = session.danhSachGhe.get(i);

                // 2.1 Sinh mã Hành khách ngẫu nhiên để không bị trùng (Ví dụ HK847321)
                String maHKMoi = "HK" + (System.currentTimeMillis() % 100000) + i;
                hk.setMaHK(maHKMoi);

                // Lưu thông tin người bay vào CSDL
                try (PreparedStatement psHK = conn.prepareStatement(sqlHK)) {
                    psHK.setString(1, hk.getMaHK());
                    psHK.setString(2, session.maNguoiDung);
                    psHK.setString(3, hk.getHoTen());
                    psHK.setString(4, hk.getCccd());
                    psHK.setString(5, hk.getHoChieu());
                    psHK.setString(6, hk.getGioiTinh());
                    if (hk.getNgaySinh() != null) psHK.setDate(7, java.sql.Date.valueOf(hk.getNgaySinh()));
                    else psHK.setNull(7, java.sql.Types.DATE);
                    psHK.setString(8, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn");
                    psHK.executeUpdate();
                }

                // 2.2 Lưu thông tin Vé Bán (Nối Mã Hành Khách vừa tạo vào đây)
                String maVe = "V" + (System.currentTimeMillis() % 1000000) + i;
                try (PreparedStatement psVe = conn.prepareStatement(sqlVe)) {
                    psVe.setString(1, maVe);
                    psVe.setString(2, maPDV);
                    psVe.setString(3, hk.getMaHK()); // Đã có Hành khách, gán Khóa Ngoại vào đây!
                    psVe.setString(4, session.maChuyenBay);
                    psVe.setString(5, ghe.getMaGhe());
                    psVe.setString(6, maHangVeAnToan);
                    psVe.setString(7, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn");
                    psVe.setString(8, loaiVeAnToan);
                    
                    // Chia đều tổng tiền vé gốc ra cho từng người
                    BigDecimal giaTungVe = session.tongTienVe.divide(new BigDecimal(session.getTongSoHanhKhach()), 2, java.math.RoundingMode.HALF_UP);
                    psVe.setBigDecimal(9, giaTungVe);
                    psVe.setString(10, "Đã đặt");
                    psVe.executeUpdate();
                }

                // 2.3 Khóa chiếc ghế đó lại để người khác không mua được nữa
                try (PreparedStatement psGhe = conn.prepareStatement(sqlGhe)) {
                    psGhe.setString(1, ghe.getMaGhe());
                    psGhe.executeUpdate();
                }
            }

            // 3. TẠO HÓA ĐƠN THANH TOÁN TỔNG
            String sqlHD = "INSERT INTO HoaDon (maHoaDon, maPhieuDatVe, ngayLap, tongTien, phuongThuc) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, "HD" + (System.currentTimeMillis() % 100000));
                psHD.setString(2, maPDV);
                psHD.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                psHD.setBigDecimal(4, finalAmount);
                psHD.setString(5, radTienMat.isSelected() ? "Tiền mặt" : (radQR.isSelected() ? "Mã QR" : "Thẻ tín dụng"));
                psHD.executeUpdate();
            }

            // 4. LƯU TOÀN BỘ XUỐNG DATABASE
            conn.commit(); 

            JOptionPane.showMessageDialog(this, "🎉 CHÚC MỪNG BẠN ĐÃ ĐẶT VÉ THÀNH CÔNG!\nMã hóa đơn của bạn là: " + maPDV, "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Tắt giao diện đặt vé, trả về Trang chủ MainFrame ban đầu
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose(); 
            
            // Khôi phục lại phiên đăng nhập của người dùng để tránh bị văng ra ngoài
            model.NguoiDung userDaDangNhap = null;
            try {
                NguoiDungDAO ndDAO = new NguoiDungDAO();
                for (model.NguoiDung nd : ndDAO.selectAll()) {
                    if (nd.getMaNguoiDung().equals(session.maNguoiDung)) {
                        userDaDangNhap = nd;
                        break;
                    }
                }
            } catch(Exception e) { System.err.println("Lỗi khôi phục User: " + e.getMessage()); }

            if (userDaDangNhap != null) {
                new gui.user.MainFrame(userDaDangNhap).setVisible(true); 
            } else {
                new gui.user.MainFrame().setVisible(true); 
            }

        } catch (Exception ex) {
            if (conn != null) try{ conn.rollback(); } catch(Exception ez){}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi SQL: Không thể lưu vé! \nChi tiết: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try{ conn.close(); } catch(Exception ez){}
        }
    }

    private JPanel createSummaryRow(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l); p.add(v);
        return p;
    }

    private JLabel createPriceLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        return l;
    }

    private TitledBorder createCustomTitledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setTitleColor(new Color(28, 48, 96));
        b.setBorder(BorderFactory.createCompoundBorder(b.getBorder(), BorderFactory.createEmptyBorder(10, 15, 15, 15)));
        return b;
    }
}