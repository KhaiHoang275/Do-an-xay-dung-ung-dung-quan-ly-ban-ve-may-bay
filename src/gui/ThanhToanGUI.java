package gui;

import dal.*;
import model.*;
import bll.ThuHangBUS; // Thêm import thư viện BUS của bạn
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ThanhToanGUI extends JPanel {

    private DatVeSession session;
    private KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
    private VeBanDAO veBanDAO = new VeBanDAO(); 
    private ThuHangBUS thuHangBUS = new ThuHangBUS(); // Khởi tạo BUS xử lý hạng và điểm
    
    private JLabel lblTongVe, lblTongDV, lblGiamGia, lblTongThanhToan;
    private JComboBox<KhuyenMai> cboKhuyenMai;
    private JRadioButton radTienMat, radThe, radQR;
    
    private BigDecimal finalAmount = BigDecimal.ZERO;
    private BigDecimal thueVAT = BigDecimal.ZERO; 
    
    private Timer countdownTimer;
    private int timeLeft = 600; 
    private JLabel lblTimer;

    public ThanhToanGUI(DatVeSession session) {
        this.session = session;
        if(session.tongTienVe == null || session.tongTienVe.compareTo(BigDecimal.ZERO) == 0) {
            session.tongTienVe = new BigDecimal("1200000").multiply(new BigDecimal(session.getTongSoHanhKhach()));
        }
        initComponents();
        loadPromotions();
        updatePriceDisplay();
        startCountdown(); 
    }

    private void initComponents() {
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

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 30, 0));
        pnlCenter.setOpaque(false);
        pnlCenter.setPreferredSize(new Dimension(1100, 500)); 

        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        String hangVeDi = (session.maHangVe != null && !session.maHangVe.isEmpty()) ? session.maHangVe : "ECO";
        BigDecimal refGiaDi = BigDecimal.ZERO;
        try { refGiaDi = veBanDAO.tinhGiaVeFull(session.maChuyenBay, hangVeDi, "Người lớn"); } catch (Exception e){}
        String strGiaDi = vn.format(refGiaDi) + " VNĐ";

        // ================= PANEL TRÁI =================
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.Y_AXIS));
        pnlSummary.setBackground(Color.WHITE);
        pnlSummary.setBorder(createCustomTitledBorder("THONG TIN HANH TRINH & DAT CHO"));

        pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlSummary.add(createSectionLabel("- THONG TIN CHUYEN DI"));
        
        pnlSummary.add(createSummaryRow("Mã chuyến bay:", session.maChuyenBay != null ? session.maChuyenBay : "Chưa rõ"));
        
        String tenHangVeDi = "Phổ thông";
        switch (hangVeDi) { case "BUS": tenHangVeDi = "Thương gia"; break; case "FST": tenHangVeDi = "Hạng nhất"; break; case "PECO": tenHangVeDi = "Phổ thông đặc biệt"; break; }
        pnlSummary.add(createSummaryRow("Hạng vé:", tenHangVeDi));
        
        String loTrinh = (session.tenSanBayDi != null ? session.tenSanBayDi : "") + " -> " + (session.tenSanBayDen != null ? session.tenSanBayDen : "");
        pnlSummary.add(createSummaryRow("Lộ trình:", loTrinh)); 
        pnlSummary.add(createSummaryRow("Khởi hành:", (session.thoiGianDi != null ? session.thoiGianDi : "")));
        pnlSummary.add(createSummaryRow("Giá vé/Người lớn:", strGiaDi)); 

        if ("Khứ hồi".equals(session.loaiVe)) {
            pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));
            pnlSummary.add(createSectionLabel("- THONG TIN CHUYEN VE"));
            
            String cbVe = (session.maChuyenBayVe != null && !session.maChuyenBayVe.isEmpty()) ? session.maChuyenBayVe : session.maChuyenBay;
            pnlSummary.add(createSummaryRow("Mã chuyến bay:", cbVe));
            
            String hangVeVe = (session.maHangVeVe != null && !session.maHangVeVe.isEmpty()) ? session.maHangVeVe : hangVeDi;
            String tenHangVeVe = "Phổ thông";
            switch (hangVeVe) { case "BUS": tenHangVeVe = "Thương gia"; break; case "FST": tenHangVeVe = "Hạng nhất"; break; case "PECO": tenHangVeVe = "Phổ thông đặc biệt"; break; }
            pnlSummary.add(createSummaryRow("Hạng vé:", tenHangVeVe));
            
            String loTrinhVe = (session.tenSanBayDen != null ? session.tenSanBayDen : "") + " -> " + (session.tenSanBayDi != null ? session.tenSanBayDi : "");
            pnlSummary.add(createSummaryRow("Lộ trình:", loTrinhVe)); 
            pnlSummary.add(createSummaryRow("Khởi hành:", (session.thoiGianVe != null ? session.thoiGianVe : "")));
            
            BigDecimal refGiaVe = BigDecimal.ZERO;
            try { refGiaVe = veBanDAO.tinhGiaVeFull(cbVe, hangVeVe, "Người lớn"); } catch (Exception e){}
            pnlSummary.add(createSummaryRow("Giá vé/Người lớn:", vn.format(refGiaVe) + " VNĐ")); 
        }

        pnlSummary.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator sep = new JSeparator(); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); pnlSummary.add(sep);
        pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlSummary.add(createSectionLabel("- DANH SACH HANH KHACH & GHE"));
        
        if(session.danhSachHanhKhach != null) {
            for(int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                String soGhe = "Đi: " + (session.danhSachGhe != null && i < session.danhSachGhe.size() ? session.danhSachGhe.get(i).getSoGhe() : "N/A");
                if ("Khứ hồi".equals(session.loaiVe) && session.danhSachGhe != null && session.danhSachGhe.size() > i + session.getTongSoHanhKhach()) {
                    soGhe += " | Về: " + session.danhSachGhe.get(i + session.getTongSoHanhKhach()).getSoGhe();
                }

                JLabel lblHkInfo = new JLabel("<html><b>" + (i + 1) + ". " + hk.getHoTen() + "</b> (" + hk.getLoaiHanhKhach() + ")<br>&nbsp;&nbsp;&nbsp;Ghế: <font color='#dc2626'><b>" + soGhe + "</b></font></html>");
                lblHkInfo.setFont(new Font("Segoe UI", Font.PLAIN, 15)); lblHkInfo.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 0)); lblHkInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlSummary.add(lblHkInfo);
            }
        }
        for (Component c : pnlSummary.getComponents()) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);

        // ================= PANEL PHẢI =================
        JPanel pnlBilling = new JPanel();
        pnlBilling.setLayout(new BoxLayout(pnlBilling, BoxLayout.Y_AXIS));
        pnlBilling.setBackground(Color.WHITE);
        pnlBilling.setBorder(createCustomTitledBorder("CHI TIET THANH TOAN"));

        lblTongVe = createPriceLabel("Tiền vé cơ bản: 0 VNĐ");
        lblTongDV = createPriceLabel("Hành lý & Dịch vụ: 0 VNĐ");
        lblGiamGia = createPriceLabel("Giảm giá: 0 VNĐ");
        lblTongThanhToan = new JLabel("<html>TỔNG CỘNG (Gồm 10% VAT):<br><span style='font-size:24px; color:#dc2626;'>0 VNĐ</span></html>"); 

        cboKhuyenMai = new JComboBox<>(); cboKhuyenMai.setMaximumSize(new Dimension(400, 45)); 
        cboKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 15)); cboKhuyenMai.setBackground(Color.WHITE);
        cboKhuyenMai.addActionListener(e -> updatePriceDisplay());

        pnlBilling.add(lblTongVe); pnlBilling.add(lblTongDV); pnlBilling.add(Box.createRigidArea(new Dimension(0, 15)));
        JLabel lblKM = new JLabel("Mã giảm giá (nếu có):"); lblKM.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pnlBilling.add(lblKM); pnlBilling.add(Box.createVerticalStrut(5)); pnlBilling.add(cboKhuyenMai); pnlBilling.add(lblGiamGia);
        pnlBilling.add(new JSeparator()); pnlBilling.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlBilling.add(lblTongThanhToan); pnlBilling.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JLabel lblPT = new JLabel("Phương thức thanh toán:"); lblPT.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pnlBilling.add(lblPT);
        radTienMat = new JRadioButton("Tiền mặt", true); radTienMat.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        radThe = new JRadioButton("Thẻ tín dụng / ATM"); radThe.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        radQR = new JRadioButton("Quét mã QR"); radQR.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        radTienMat.setBackground(Color.WHITE); radThe.setBackground(Color.WHITE); radQR.setBackground(Color.WHITE);
        ButtonGroup grp = new ButtonGroup(); grp.add(radTienMat); grp.add(radThe); grp.add(radQR);
        pnlBilling.add(radTienMat); pnlBilling.add(radThe); pnlBilling.add(radQR);

        pnlCenter.add(pnlSummary); pnlCenter.add(pnlBilling);
        
        JPanel centerAlignPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerAlignPanel.setOpaque(false);
        centerAlignPanel.add(pnlCenter);
        scrollContent.add(centerAlignPanel);

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

    private JPanel createStepper() {
        JPanel pnlStepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); pnlStepper.setBackground(new Color(18, 32, 64, 200)); pnlStepper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        Font fontStep = new Font("Segoe UI", Font.BOLD, 16); Font fontArrow = new Font("Segoe UI", Font.BOLD, 18);
        JLabel step1 = new JLabel("1. Chuyen bay"); step1.setForeground(Color.WHITE); step1.setFont(fontStep); JLabel arr1 = new JLabel(" > "); arr1.setForeground(Color.WHITE); arr1.setFont(fontArrow);
        JLabel step2 = new JLabel("2. Hanh khach"); step2.setForeground(Color.WHITE); step2.setFont(fontStep); JLabel arr2 = new JLabel(" > "); arr2.setForeground(Color.WHITE); arr2.setFont(fontArrow);
        JLabel step3 = new JLabel("3. Dich vu"); step3.setForeground(Color.WHITE); step3.setFont(fontStep); JLabel arr3 = new JLabel(" > "); arr3.setForeground(Color.WHITE); arr3.setFont(fontArrow);
        JLabel step4 = new JLabel("4. Thanh toan"); step4.setForeground(new Color(255, 193, 7)); step4.setFont(fontStep);
        pnlStepper.add(step1); pnlStepper.add(arr1); pnlStepper.add(step2); pnlStepper.add(arr2); pnlStepper.add(step3); pnlStepper.add(arr3); pnlStepper.add(step4);
        return pnlStepper;
    }

    private void switchPage(JPanel newPanel) {
        if (countdownTimer != null) countdownTimer.stop(); 
        Container container = SwingUtilities.getAncestorOfClass(gui.user.MainFrame.class, this);
        if (container instanceof gui.user.MainFrame) {
            gui.user.MainFrame mainFrame = (gui.user.MainFrame) container;
            LayoutManager layout = mainFrame.getContentPane().getLayout();
            if (layout instanceof BorderLayout) {
                BorderLayout bl = (BorderLayout) layout;
                Component centerComp = bl.getLayoutComponent(BorderLayout.CENTER);
                if (centerComp instanceof JPanel) {
                    JPanel wrapper = (JPanel) centerComp;
                    wrapper.removeAll();
                    newPanel.setOpaque(false);
                    wrapper.add(newPanel, BorderLayout.CENTER);
                    wrapper.revalidate();
                    wrapper.repaint();
                }
            }
        }
    }

    private void startCountdown() {
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 0) {
                countdownTimer.stop();
                handleTimeout();
            } else {
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                lblTimer.setText(String.format("Thời gian giữ vé còn lại %02d:%02d", minutes, seconds));
            }
        });
        countdownTimer.start();
    }

    private void handleTimeout() {
        JOptionPane.showMessageDialog(this, "Đã hết thời gian giữ vé (10 phút). Giao dịch đã bị hủy, vui lòng đặt lại vé!", "Hết thời gian", JOptionPane.WARNING_MESSAGE);
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            model.NguoiDung userDaDangNhap = null;
            if (session != null && session.maNguoiDung != null && !session.maNguoiDung.equals("KHACH_LE")) {
                try {
                    for (model.NguoiDung nd : new dal.NguoiDungDAO().selectAll()) {
                        if (nd.getMaNguoiDung().equals(session.maNguoiDung)) {
                            userDaDangNhap = nd; break;
                        }
                    }
                } catch (Exception ex) {}
            }
            if (userDaDangNhap != null) { new gui.user.MainFrame(userDaDangNhap).setVisible(true); } 
            else { new gui.user.MainFrame().setVisible(true); }
            window.dispose();
        }
    }

    private JPanel createStickyFooter() {
        JPanel footer = new JPanel(new BorderLayout()); footer.setBackground(Color.WHITE); footer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)), new EmptyBorder(10, 50, 10, 50)));
        JButton btnQuayLai = new JButton("Quay lai Dich vu"); 
        btnQuayLai.setBackground(Color.WHITE); btnQuayLai.setForeground(new Color(100, 100, 100)); btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 16)); btnQuayLai.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); btnQuayLai.setPreferredSize(new Dimension(200, 45));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> switchPage(new gui.DichVuHanhLyGUI(session)));
        footer.add(btnQuayLai, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); pnlRight.setOpaque(false);
        JPanel pnlAction = new JPanel(); pnlAction.setLayout(new BoxLayout(pnlAction, BoxLayout.Y_AXIS)); pnlAction.setOpaque(false);

        lblTimer = new JLabel("Thời gian giữ vé còn lại 10:00");
        lblTimer.setFont(new Font("Segoe UI", Font.PLAIN, 14)); lblTimer.setForeground(new Color(245, 158, 11)); lblTimer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnNext = new JButton("Thanh toan"); 
        btnNext.setBackground(new Color(244, 67, 54)); btnNext.setForeground(Color.WHITE); btnNext.setFont(new Font("Segoe UI", Font.BOLD, 18)); btnNext.setPreferredSize(new Dimension(280, 45)); btnNext.setMaximumSize(new Dimension(280, 45)); btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR)); btnNext.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNext.addActionListener(e -> processBooking());

        pnlAction.add(lblTimer); pnlAction.add(Box.createVerticalStrut(5)); pnlAction.add(btnNext);
        pnlRight.add(pnlAction); footer.add(pnlRight, BorderLayout.EAST);
        return footer;
    }

    private void loadPromotions() {
        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem(null); 
        try {
            List<KhuyenMai> list = kmDAO.getAll();
            if(list != null) {
                for(KhuyenMai km : list) {
                    cboKhuyenMai.addItem(km); 
                    if (session.khuyenMaiApDung != null && km.getMaKhuyenMai().equals(session.khuyenMaiApDung.getMaKhuyenMai())) cboKhuyenMai.setSelectedItem(km);
                }
            }
        } catch(Exception e) {}

        cboKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KhuyenMai) { KhuyenMai km = (KhuyenMai) value; setText(km.getMaKhuyenMai() + " (Giảm " + km.getGiaTri() + ")"); } 
                else { setText("--- Không áp dụng mã ---"); }
                return this;
            }
        });
    }

    private void updatePriceDisplay() {
        BigDecimal base = session.tongTienVe;
        BigDecimal sv = (session.tongTienDichVu != null) ? session.tongTienDichVu : BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        KhuyenMai selected = (KhuyenMai) cboKhuyenMai.getSelectedItem();
        session.khuyenMaiApDung = selected;

        if(selected != null) {
            if("PHAN_TRAM".equals(selected.getLoaiKM()) || "Phần trăm".equalsIgnoreCase(selected.getLoaiKM())) {
                discount = base.add(sv).multiply(selected.getGiaTri()).divide(new BigDecimal("100"));
            } else { discount = selected.getGiaTri() != null ? selected.getGiaTri() : BigDecimal.ZERO; }
        }

        BigDecimal tongTruocThue = base.add(sv).subtract(discount);
        if(tongTruocThue.compareTo(BigDecimal.ZERO) < 0) tongTruocThue = BigDecimal.ZERO;

        thueVAT = tongTruocThue.multiply(new BigDecimal("0.1"));
        finalAmount = tongTruocThue.add(thueVAT); 

        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblTongVe.setText("Tiền vé cơ bản: " + vn.format(base) + " VNĐ");
        lblTongDV.setText("Hành lý & Dịch vụ: " + vn.format(sv) + " VNĐ");
        if (session.khuyenMaiApDung != null) { lblGiamGia.setText("Giảm giá (" + session.khuyenMaiApDung.getMaKhuyenMai() + "): -" + vn.format(discount) + " VNĐ"); } 
        else { lblGiamGia.setText("Giảm giá: 0 VNĐ"); }
        lblTongThanhToan.setText("<html>TỔNG CỘNG (Gồm 10% VAT):<br><span style='font-size:24px; color:#dc2626;'>" + vn.format(finalAmount) + " VNĐ</span></html>");
    }

    private void processBooking() {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán số tiền: " + lblTongThanhToan.getText().replaceAll("<[^>]*>", "") + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        if (countdownTimer != null) countdownTimer.stop(); 

        Connection conn = null;
        String generatedMaPDV = ""; 
        String generatedMaHD = "";  
        String ptThanhToan = radTienMat.isSelected() ? "Tiền mặt" : (radQR.isSelected() ? "Mã QR" : "Thẻ tín dụng");

        try {
            conn = db.DBConnection.getConnection();
            conn.setAutoCommit(false); 

            dal.PhieuDatVeDAO pdvDAO = new dal.PhieuDatVeDAO();
            generatedMaPDV = pdvDAO.generateMaPhieuDatVe(conn); 

            // 1. TẠO PHIẾU ĐẶT VÉ
            String sqlPDV = "INSERT INTO PhieuDatVe (maPhieuDatVe, maNguoiDung, tongTien, ngayDat, soLuongVe, trangThaiThanhToan, maKhuyenMai) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPDV)) {
                ps.setString(1, generatedMaPDV); ps.setString(2, session.maNguoiDung); ps.setBigDecimal(3, finalAmount); ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now())); ps.setInt(5, session.getTongSoHanhKhach()); ps.setString(6, "Đã thanh toán");
                if (session.khuyenMaiApDung != null) ps.setString(7, session.khuyenMaiApDung.getMaKhuyenMai()); else ps.setNull(7, java.sql.Types.VARCHAR);
                ps.executeUpdate();
            }

            // ================= SQL QUERIES CHO HÀNH KHÁCH & VÉ =================
            String sqlCheckCCCD = "SELECT maHK, hoTen, diemTichLuy FROM ThongTinHanhKhach WHERE cccd = ?";
            String sqlUpdateKhachCu = "UPDATE ThongTinHanhKhach SET diemTichLuy = ?, maThuHang = ? WHERE maHK = ?";
            String sqlInsertKhachMoi = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, hoTen, cccd, hoChieu, gioiTinh, ngaySinh, loaiHanhKhach, maThuHang, diemTichLuy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            String sqlVe = "INSERT INTO VeBan (maVe, maPhieuDatVe, maHK, maChuyenBay, maGhe, maHangVe, loaiHK, loaiVe, giaVe, trangThaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlGhe = "UPDATE GheMayBay SET trangThai = 'DA_DAT' WHERE maGhe = ?";

            String loaiVeAnToan = (session.loaiVe != null) ? session.loaiVe : "Một chiều";
            dal.VeBanDAO veBanDAO = new dal.VeBanDAO();
            String currentMaVe = veBanDAO.generateMaVe(conn); 
            
            String sqlMaxHK = "SELECT MAX(maHK) FROM ThongTinHanhKhach";
            String currentMaHK = "HK000"; 
            try (PreparedStatement psMaxHK = conn.prepareStatement(sqlMaxHK); java.sql.ResultSet rsMaxHK = psMaxHK.executeQuery()) {
                if (rsMaxHK.next() && rsMaxHK.getString(1) != null) currentMaHK = rsMaxHK.getString(1); 
            }

            int tongKhach = session.getTongSoHanhKhach();
            String maHangVeDi = (session.maHangVe != null && !session.maHangVe.isEmpty()) ? session.maHangVe : "ECO";
            boolean isKhuHoi = "Khứ hồi".equals(session.loaiVe);
            String maCBVe = isKhuHoi ? ((session.maChuyenBayVe != null && !session.maChuyenBayVe.isEmpty()) ? session.maChuyenBayVe : session.maChuyenBay) : null;
            String maHangVeVe = isKhuHoi ? ((session.maHangVeVe != null && !session.maHangVeVe.isEmpty()) ? session.maHangVeVe : maHangVeDi) : null;

            for (int i = 0; i < tongKhach; i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                // A. TÍNH TỔNG TIỀN VÉ CỦA KHÁCH NÀY ĐỂ QUY ĐỔI ĐIỂM
                GheMayBay gheDi = session.danhSachGhe.get(i);
                BigDecimal giaThucTeDi = BigDecimal.ZERO;
                try { giaThucTeDi = veBanDAO.tinhGiaVeFull(session.maChuyenBay, maHangVeDi, hk.getLoaiHanhKhach()); } catch(Exception e){}
                
                BigDecimal giaThucTeVe = BigDecimal.ZERO;
                if (isKhuHoi && session.danhSachGhe.size() > i + tongKhach) {
                    try { giaThucTeVe = veBanDAO.tinhGiaVeFull(maCBVe, maHangVeVe, hk.getLoaiHanhKhach()); } catch(Exception e){}
                }

                BigDecimal tongTienKhachNay = giaThucTeDi.add(giaThucTeVe);
                
                // DÙNG HÀM BUS ĐỂ QUY ĐỔI ĐIỂM
                int diemCongThem = thuHangBUS.tinhDiemCongThem(tongTienKhachNay.doubleValue());

                // B. KIỂM TRA TRÙNG CCCD (TÌM KHÁCH CŨ)
                boolean isOldCustomer = false;
                int diemHienTai = 0;
                
                try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckCCCD)) {
                    psCheck.setString(1, hk.getCccd());
                    try (ResultSet rsCheck = psCheck.executeQuery()) {
                        if (rsCheck.next()) {
                            isOldCustomer = true;
                            hk.setMaHK(rsCheck.getString("maHK"));
                            hk.setHoTen(rsCheck.getString("hoTen")); // Ép tên theo CSDL
                            diemHienTai = rsCheck.getInt("diemTichLuy");
                        }
                    }
                }

                // C. CỘNG ĐIỂM & CHỐT HẠNG
                if (isOldCustomer) {
                    int diemMoi = diemHienTai + diemCongThem;
                    // Dùng BUS xác định hạng mới dựa trên tổng điểm mới
                    model.ThuHang hangMoi = thuHangBUS.xacDinhThuHang(diemMoi);
                    String maHangMoi = (hangMoi != null) ? hangMoi.getMaThuHang() : "SILVER";

                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateKhachCu)) {
                        psUpdate.setInt(1, diemMoi);
                        psUpdate.setString(2, maHangMoi);
                        psUpdate.setString(3, hk.getMaHK());
                        psUpdate.executeUpdate();
                    }
                } else {
                    // Khách hoàn toàn mới
                    try {
                        String prefixHK = currentMaHK.substring(0, 2); 
                        int numberPartHK = Integer.parseInt(currentMaHK.substring(2)); 
                        numberPartHK++; currentMaHK = String.format("%s%03d", prefixHK, numberPartHK); 
                    } catch (Exception e) {}
                    hk.setMaHK(currentMaHK);

                    model.ThuHang hangKhoiDau = thuHangBUS.xacDinhThuHang(diemCongThem);
                    String maHangMoi = (hangKhoiDau != null) ? hangKhoiDau.getMaThuHang() : "SILVER";

                    // INSERT KHÁCH MỚI
                    try (PreparedStatement psHK = conn.prepareStatement(sqlInsertKhachMoi)) {
                        psHK.setString(1, hk.getMaHK()); psHK.setString(2, session.maNguoiDung); psHK.setString(3, hk.getHoTen()); psHK.setString(4, hk.getCccd()); psHK.setString(5, hk.getHoChieu()); psHK.setString(6, hk.getGioiTinh());
                        if (hk.getNgaySinh() != null) psHK.setDate(7, java.sql.Date.valueOf(hk.getNgaySinh())); else psHK.setNull(7, java.sql.Types.DATE);
                        psHK.setString(8, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn");
                        psHK.setString(9, maHangMoi); 
                        psHK.setInt(10, diemCongThem); // Điểm khởi đầu
                        psHK.executeUpdate();
                    }
                }

                // D. LƯU VÉ LƯỢT ĐI
                try (PreparedStatement psVe = conn.prepareStatement(sqlVe)) {
                    psVe.setString(1, currentMaVe); psVe.setString(2, generatedMaPDV); psVe.setString(3, hk.getMaHK()); 
                    psVe.setString(4, session.maChuyenBay); psVe.setString(5, gheDi.getMaGhe()); psVe.setString(6, maHangVeDi); 
                    psVe.setString(7, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn"); psVe.setString(8, loaiVeAnToan);
                    psVe.setBigDecimal(9, giaThucTeDi); // LƯU GIÁ THẬT
                    psVe.setString(10, "Đã thanh toán"); 
                    psVe.executeUpdate();
                }
                try { String p = currentMaVe.substring(0, 2); int n = Integer.parseInt(currentMaVe.substring(2)); currentMaVe = String.format("%s%03d", p, n + 1); } catch (Exception e) {}
                try (PreparedStatement psGhe = conn.prepareStatement(sqlGhe)) { psGhe.setString(1, gheDi.getMaGhe()); psGhe.executeUpdate(); }

                // E. LƯU VÉ LƯỢT VỀ (NẾU CÓ)
                if (isKhuHoi && session.danhSachGhe.size() > i + tongKhach) {
                    GheMayBay gheVe = session.danhSachGhe.get(i + tongKhach);
                    try (PreparedStatement psVe = conn.prepareStatement(sqlVe)) {
                        psVe.setString(1, currentMaVe); psVe.setString(2, generatedMaPDV); psVe.setString(3, hk.getMaHK()); 
                        psVe.setString(4, maCBVe); psVe.setString(5, gheVe.getMaGhe()); psVe.setString(6, maHangVeVe); 
                        psVe.setString(7, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn"); psVe.setString(8, loaiVeAnToan);
                        psVe.setBigDecimal(9, giaThucTeVe); // LƯU GIÁ THẬT
                        psVe.setString(10, "Đã thanh toán"); 
                        psVe.executeUpdate();
                    }
                    try { String p = currentMaVe.substring(0, 2); int n = Integer.parseInt(currentMaVe.substring(2)); currentMaVe = String.format("%s%03d", p, n + 1); } catch (Exception e) {}
                    try (PreparedStatement psGhe = conn.prepareStatement(sqlGhe)) { psGhe.setString(1, gheVe.getMaGhe()); psGhe.executeUpdate(); }
                }
            }

            // LƯU HÓA ĐƠN
            String sqlMaxHD = "SELECT MAX(maHoaDon) FROM HoaDon";
            try (PreparedStatement psMax = conn.prepareStatement(sqlMaxHD); java.sql.ResultSet rsMax = psMax.executeQuery()) {
                if (rsMax.next() && rsMax.getString(1) != null) { String lastHD = rsMax.getString(1).substring(2); int num = Integer.parseInt(lastHD) + 1; generatedMaHD = String.format("HD%03d", num); } 
                else { generatedMaHD = "HD001"; }
            }

            String sqlHD = "INSERT INTO HoaDon (maHoaDon, maPhieuDatVe, ngayLap, tongTien, phuongThuc, trangThai, donViTienTe, thue) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, generatedMaHD); psHD.setString(2, generatedMaPDV); psHD.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now())); psHD.setBigDecimal(4, finalAmount); psHD.setString(5, ptThanhToan); psHD.setString(6, "Đã thanh toán"); psHD.setString(7, "VND"); psHD.setBigDecimal(8, thueVAT); psHD.executeUpdate();
            }

            conn.commit(); 

            JOptionPane.showMessageDialog(this, "CHUC MUNG BAN DA DAT VE THANH CONG!\nMã hóa đơn của bạn là: " + generatedMaHD, "Thành công", JOptionPane.INFORMATION_MESSAGE);

            String strNgayLap = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            java.text.NumberFormat formatTien = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            String strTongTien = formatTien.format(finalAmount) + " VNĐ";

            switchPage(new gui.user.ThanhToanHoaDonPanel(generatedMaHD, generatedMaPDV, strNgayLap, strTongTien, ptThanhToan, "10%", session));

        } catch (Exception ex) {
            if (conn != null) try{ conn.rollback(); } catch(Exception ez){}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi SQL: Không thể lưu vé! \nChi tiết: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try{ conn.close(); } catch(Exception ez){}
        }
    }

    private JPanel createSummaryRow(String label, String value) { 
        JPanel p = new JPanel(new BorderLayout(10, 0)); 
        p.setOpaque(false); 
        p.setBackground(Color.WHITE); 
        p.setMaximumSize(new Dimension(800, Integer.MAX_VALUE)); 
        
        JLabel l = new JLabel(label); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        l.setPreferredSize(new Dimension(150, 25)); 
        l.setVerticalAlignment(SwingConstants.TOP); 
        
        JLabel v = new JLabel("<html><div style='width:260px;'>" + value + "</div></html>"); 
        v.setFont(new Font("Segoe UI", Font.PLAIN, 15)); 
        v.setVerticalAlignment(SwingConstants.TOP); 
        
        p.add(l, BorderLayout.WEST); 
        p.add(v, BorderLayout.CENTER); 
        p.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return p; 
    }
    
    private JLabel createSectionLabel(String text) { JLabel label = new JLabel(text); label.setFont(new Font("Segoe UI", Font.BOLD, 16)); label.setForeground(new Color(28, 48, 96)); label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0)); label.setAlignmentX(Component.LEFT_ALIGNMENT); return label; }
    private JLabel createPriceLabel(String text) { JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.PLAIN, 16)); l.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); return l; }
    private TitledBorder createCustomTitledBorder(String title) { TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), title); b.setTitleFont(new Font("Segoe UI", Font.BOLD, 16)); b.setTitleColor(new Color(28, 48, 96)); b.setBorder(BorderFactory.createCompoundBorder(b.getBorder(), BorderFactory.createEmptyBorder(10, 15, 15, 15))); return b; }
}