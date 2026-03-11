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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ThanhToanGUI extends JPanel {

    private DatVeSession session;
    private KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
    
    private JLabel lblTongVe, lblTongDV, lblGiamGia, lblTongThanhToan;
    private JComboBox<KhuyenMai> cboKhuyenMai;
    private JRadioButton radTienMat, radThe, radQR;
    
    private BigDecimal finalAmount = BigDecimal.ZERO;
    private BigDecimal thueVAT = BigDecimal.ZERO; 

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

        // ================= HEADER =================
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

        // ================= PANEL TRÁI: THÔNG TIN ĐẶT CHỖ =================
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.Y_AXIS));
        pnlSummary.setBackground(Color.WHITE);
        pnlSummary.setBorder(createCustomTitledBorder("THÔNG TIN HÀNH TRÌNH & ĐẶT CHỖ"));

        pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlSummary.add(createSectionLabel("✈ THÔNG TIN CHUYẾN BAY"));
        
        pnlSummary.add(createSummaryRow("Mã chuyến bay:", session.maChuyenBay != null ? session.maChuyenBay : "Chưa rõ"));
        pnlSummary.add(createSummaryRow("Loại vé:", session.loaiVe != null ? session.loaiVe : "Một chiều"));
        
        String loTrinh = (session.tenSanBayDi != null ? session.tenSanBayDi : "") 
                         + " ➔ " + 
                         (session.tenSanBayDen != null ? session.tenSanBayDen : "");
        pnlSummary.add(createSummaryRow("Lộ trình:", loTrinh)); 

        String thoiGian = (session.thoiGianDi != null ? session.thoiGianDi : "");
        pnlSummary.add(createSummaryRow("Khởi hành:", thoiGian));

        pnlSummary.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnlSummary.add(sep);
        pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlSummary.add(createSectionLabel("👤 DANH SÁCH HÀNH KHÁCH & GHẾ"));
        
        if(session.danhSachHanhKhach != null) {
            for(int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                String soGhe = "Chưa chọn";
                if(session.danhSachGhe != null && i < session.danhSachGhe.size()) {
                    soGhe = session.danhSachGhe.get(i).getSoGhe();
                }
                
                JLabel lblHkInfo = new JLabel("<html><b>" + (i + 1) + ". " + hk.getHoTen() + "</b> (" + hk.getLoaiHanhKhach() + ")"
                        + "<br>&nbsp;&nbsp;&nbsp;Ghế đã chọn: <font color='red'><b>" + soGhe + "</b></font></html>");
                lblHkInfo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                lblHkInfo.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 0));
                lblHkInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                pnlSummary.add(lblHkInfo);
            }
        }
        
        for (Component c : pnlSummary.getComponents()) {
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        // ================= PANEL PHẢI: CHI TIẾT THANH TOÁN =================
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

        // ================= FOOTER =================
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
            parent.add(new DichVuHanhLyGUI(session), BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        });

        btnConfirm.addActionListener(e -> processBooking());
    }

    private void loadPromotions() {
        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem(null); 
        try {
            List<KhuyenMai> list = kmDAO.getAll();
            if(list != null) {
                for(KhuyenMai km : list) {
                    cboKhuyenMai.addItem(km); 
                    if (session.khuyenMaiApDung != null && 
                        km.getMaKhuyenMai().equals(session.khuyenMaiApDung.getMaKhuyenMai())) {
                        cboKhuyenMai.setSelectedItem(km);
                    }
                }
            }
        } catch(Exception e) { System.err.println("Lỗi tải KM: " + e.getMessage()); }

        cboKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KhuyenMai) {
                    KhuyenMai km = (KhuyenMai) value;
                    setText(km.getMaKhuyenMai() + " (Giảm " + km.getGiaTri() + ")");
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
        session.khuyenMaiApDung = selected;

        if(selected != null) {
            if("PHAN_TRAM".equals(selected.getLoaiKM()) || "Phần trăm".equalsIgnoreCase(selected.getLoaiKM())) {
                discount = base.add(sv).multiply(selected.getGiaTri()).divide(new BigDecimal("100"));
            } else {
                discount = selected.getGiaTri() != null ? selected.getGiaTri() : BigDecimal.ZERO;
            }
        }

        BigDecimal tongTruocThue = base.add(sv).subtract(discount);
        if(tongTruocThue.compareTo(BigDecimal.ZERO) < 0) tongTruocThue = BigDecimal.ZERO;

        thueVAT = tongTruocThue.multiply(new BigDecimal("0.1"));
        finalAmount = tongTruocThue.add(thueVAT); 

        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblTongVe.setText("Tiền vé cơ bản: " + vn.format(base) + " VNĐ");
        lblTongDV.setText("Hành lý & Dịch vụ: " + vn.format(sv) + " VNĐ");
        lblGiamGia.setText("Giảm giá: -" + vn.format(discount) + " VNĐ");
        lblTongThanhToan.setText("TỔNG CỘNG (Gồm 10% VAT): " + vn.format(finalAmount) + " VNĐ");
    }

    private void processBooking() {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán số tiền: " + lblTongThanhToan.getText() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        
        String generatedMaPDV = ""; 
        String generatedMaHD = "";  
        String ptThanhToan = radTienMat.isSelected() ? "Tiền mặt" : (radQR.isSelected() ? "Mã QR" : "Thẻ tín dụng");

        // KHỞI TẠO BUS ĐỂ TÍNH ĐIỂM TÍCH LŨY
        bll.ThuHangBUS thuHangBUS = new bll.ThuHangBUS();
        String maBac = "TH01"; // Mặc định nếu không tìm thấy DB
        try {
            // Tự động tìm Mã hạng cho người 0 điểm (Hạng Bạc)
            model.ThuHang thBac = thuHangBUS.xacDinhThuHang(0);
            if (thBac != null && thBac.getMaThuHang() != null) {
                maBac = thBac.getMaThuHang();
            }
        } catch (Exception e) {}

        try {
            conn = db.DBConnection.getConnection();
            conn.setAutoCommit(false); 

            dal.PhieuDatVeDAO pdvDAO = new dal.PhieuDatVeDAO();
            generatedMaPDV = pdvDAO.generateMaPhieuDatVe(conn); 

            String sqlPDV = "INSERT INTO PhieuDatVe (maPhieuDatVe, maNguoiDung, tongTien, ngayDat, soLuongVe, trangThaiThanhToan, maKhuyenMai) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPDV)) {
                ps.setString(1, generatedMaPDV);
                ps.setString(2, session.maNguoiDung);
                ps.setBigDecimal(3, finalAmount);
                ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps.setInt(5, session.getTongSoHanhKhach());
                ps.setString(6, "Đã thanh toán");
                
                if (session.khuyenMaiApDung != null) {
                    ps.setString(7, session.khuyenMaiApDung.getMaKhuyenMai());
                } else {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                }
                ps.executeUpdate();
            }

            // ĐÃ SỬA LẠI: Thêm đu đủ loaiHanhKhach, maThuHang, diemTichLuy (Tổng cộng 10 biến)
            String sqlHK = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, hoTen, cccd, hoChieu, gioiTinh, ngaySinh, loaiHanhKhach, maThuHang, diemTichLuy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlVe = "INSERT INTO VeBan (maVe, maPhieuDatVe, maHK, maChuyenBay, maGhe, maHangVe, loaiHK, loaiVe, giaVe, trangThaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlGhe = "UPDATE GheMayBay SET trangThai = 'DA_DAT' WHERE maGhe = ?";

            String maHangVeAnToan = (session.maHangVe != null) ? session.maHangVe : "ECO";
            String loaiVeAnToan = (session.loaiVe != null) ? session.loaiVe : "Một chiều";

            dal.VeBanDAO veBanDAO = new dal.VeBanDAO();
            String currentMaVe = veBanDAO.generateMaVe(conn); 
            
            String sqlMaxHK = "SELECT MAX(maHK) FROM ThongTinHanhKhach";
            String currentMaHK = "HK000"; 
            try (PreparedStatement psMaxHK = conn.prepareStatement(sqlMaxHK);
                 java.sql.ResultSet rsMaxHK = psMaxHK.executeQuery()) {
                if (rsMaxHK.next() && rsMaxHK.getString(1) != null) {
                    currentMaHK = rsMaxHK.getString(1); 
                }
            }

            for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                GheMayBay ghe = session.danhSachGhe.get(i);

                try {
                    String prefixHK = currentMaHK.substring(0, 2); 
                    int numberPartHK = Integer.parseInt(currentMaHK.substring(2)); 
                    numberPartHK++; 
                    currentMaHK = String.format("%s%03d", prefixHK, numberPartHK); 
                } catch (Exception e) {}
                
                hk.setMaHK(currentMaHK);

                // LƯU HỒ SƠ HÀNH KHÁCH
                try (PreparedStatement psHK = conn.prepareStatement(sqlHK)) {
                    psHK.setString(1, hk.getMaHK());
                    psHK.setString(2, session.maNguoiDung);
                    psHK.setString(3, hk.getHoTen());
                    psHK.setString(4, hk.getCccd());
                    psHK.setString(5, hk.getHoChieu());
                    psHK.setString(6, hk.getGioiTinh());
                    
                    if (hk.getNgaySinh() != null) psHK.setDate(7, java.sql.Date.valueOf(hk.getNgaySinh()));
                    else psHK.setNull(7, java.sql.Types.DATE);
                    
                    // Cột 8: Loại hành khách
                    psHK.setString(8, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn");
                    
                    // Cột 9: Mã thứ hạng
                    psHK.setString(9, maBac);
                    
                    // Cột 10: Điểm tích lũy
                    psHK.setInt(10, 0);

                    psHK.executeUpdate();
                }

                // LƯU VÉ BÁN
                try (PreparedStatement psVe = conn.prepareStatement(sqlVe)) {
                    psVe.setString(1, currentMaVe); 
                    psVe.setString(2, generatedMaPDV);
                    psVe.setString(3, hk.getMaHK()); 
                    psVe.setString(4, session.maChuyenBay);
                    psVe.setString(5, ghe.getMaGhe());
                    psVe.setString(6, maHangVeAnToan);
                    psVe.setString(7, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn");
                    psVe.setString(8, loaiVeAnToan);
                    
                    BigDecimal giaTungVe = session.tongTienVe.divide(new BigDecimal(session.getTongSoHanhKhach()), 2, java.math.RoundingMode.HALF_UP);
                    psVe.setBigDecimal(9, giaTungVe);
                    psVe.setString(10, "Đã thanh toán"); 
                    psVe.executeUpdate();
                }

                try {
                    String prefix = currentMaVe.substring(0, 2); 
                    int numberPart = Integer.parseInt(currentMaVe.substring(2)); 
                    numberPart++; 
                    currentMaVe = String.format("%s%03d", prefix, numberPart); 
                } catch (Exception e) {}

                try (PreparedStatement psGhe = conn.prepareStatement(sqlGhe)) {
                    psGhe.setString(1, ghe.getMaGhe());
                    psGhe.executeUpdate();
                }
            }

            String sqlMaxHD = "SELECT MAX(maHoaDon) FROM HoaDon";
            try (PreparedStatement psMax = conn.prepareStatement(sqlMaxHD);
                 java.sql.ResultSet rsMax = psMax.executeQuery()) {
                if (rsMax.next() && rsMax.getString(1) != null) {
                    String lastHD = rsMax.getString(1).substring(2); 
                    int num = Integer.parseInt(lastHD) + 1;
                    generatedMaHD = String.format("HD%03d", num);
                } else {
                    generatedMaHD = "HD001"; 
                }
            }

            String sqlHD = "INSERT INTO HoaDon (maHoaDon, maPhieuDatVe, ngayLap, tongTien, phuongThuc, trangThai, donViTienTe, thue) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, generatedMaHD);
                psHD.setString(2, generatedMaPDV);
                psHD.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                psHD.setBigDecimal(4, finalAmount); 
                psHD.setString(5, ptThanhToan);
                psHD.setString(6, "Đã thanh toán"); 
                psHD.setString(7, "VND"); 
                psHD.setBigDecimal(8, thueVAT); 
                psHD.executeUpdate();
            }

            // GIAO DỊCH DATABASE THÀNH CÔNG -> COMMIT
            conn.commit(); 

            // CỘNG ĐIỂM TÍCH LŨY
            double tienMoiKhach = finalAmount.doubleValue() / session.getTongSoHanhKhach(); 
            for (ThongTinHanhKhach hk : session.danhSachHanhKhach) {
                try {
                    thuHangBUS.capNhatDiemVaThuHang(hk.getMaHK(), tienMoiKhach);
                } catch (Exception ex) {}
            }

            JOptionPane.showMessageDialog(this, "🎉 CHÚC MỪNG BẠN ĐÃ ĐẶT VÉ THÀNH CÔNG!\nMã hóa đơn của bạn là: " + generatedMaHD, "Thành công", JOptionPane.INFORMATION_MESSAGE);

            String strNgayLap = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            String strTongTien = vn.format(finalAmount) + " VNĐ";
            String strThue = "10%";

            Container parent = this.getParent();
            if(parent != null) {
                parent.removeAll();
                parent.setLayout(new BorderLayout());
                
                gui.user.ThanhToanHoaDonPanel pnlHoaDon = new gui.user.ThanhToanHoaDonPanel(
                    generatedMaHD,   
                    generatedMaPDV,  
                    strNgayLap,      
                    strTongTien,     
                    ptThanhToan,     
                    strThue,
                    session
                );
                
                parent.add(pnlHoaDon, BorderLayout.CENTER); 
                parent.revalidate();
                parent.repaint();
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
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(800, 30));
        JLabel l = new JLabel(label); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setPreferredSize(new Dimension(130, 25));
        JLabel v = new JLabel(value); 
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.WEST); 
        p.add(v, BorderLayout.CENTER);
        return p;
    }
    
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(28, 48, 96));
        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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