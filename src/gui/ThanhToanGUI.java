package gui;

import dal.*;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setOpaque(false); 

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        
        JPanel stepperWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        stepperWrapper.setOpaque(false);
        stepperWrapper.add(createStepper());
        scrollContent.add(stepperWrapper);
        scrollContent.add(Box.createVerticalStrut(30));

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlCenter.setOpaque(false);
        pnlCenter.setPreferredSize(new Dimension(1000, 500)); 

        // ================= PANEL TRÁI =================
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.Y_AXIS));
        pnlSummary.setBackground(Color.WHITE);
        pnlSummary.setBorder(createCustomTitledBorder("THÔNG TIN HÀNH TRÌNH & ĐẶT CHỖ"));

        pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlSummary.add(createSectionLabel("THÔNG TIN CHUYẾN ĐI"));
        
        pnlSummary.add(createSummaryRow("Mã chuyến bay:", session.maChuyenBay != null ? session.maChuyenBay : "Chưa rõ"));
        
        String hangVeDi = "Phổ thông";
        if (session.maHangVe != null) { switch (session.maHangVe) { case "BUS": hangVeDi = "Thương gia"; break; case "FST": hangVeDi = "Hạng nhất"; break; case "PECO": hangVeDi = "Phổ thông đặc biệt"; break; } }
        pnlSummary.add(createSummaryRow("Hạng vé:", hangVeDi));
        
        String loTrinh = (session.tenSanBayDi != null ? session.tenSanBayDi : "") + " -> " + (session.tenSanBayDen != null ? session.tenSanBayDen : "");
        pnlSummary.add(createSummaryRow("Lộ trình:", loTrinh)); 
        pnlSummary.add(createSummaryRow("Khởi hành:", (session.thoiGianDi != null ? session.thoiGianDi : "")));

        if ("Khứ hồi".equals(session.loaiVe)) {
            pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));
            pnlSummary.add(createSectionLabel("THÔNG TIN CHUYẾN VỀ"));
            
            String cbVe = (session.maChuyenBayVe != null && !session.maChuyenBayVe.isEmpty()) ? session.maChuyenBayVe : session.maChuyenBay;
            pnlSummary.add(createSummaryRow("Mã chuyến bay:", cbVe));
            
            String hangVeVe = "Phổ thông";
            String maHVV = (session.maHangVeVe != null && !session.maHangVeVe.isEmpty()) ? session.maHangVeVe : session.maHangVe;
            if (maHVV != null) { switch (maHVV) { case "BUS": hangVeVe = "Thương gia"; break; case "FST": hangVeVe = "Hạng nhất"; break; case "PECO": hangVeVe = "Phổ thông đặc biệt"; break; } }
            pnlSummary.add(createSummaryRow("Hạng vé:", hangVeVe));
            
            String loTrinhVe = (session.tenSanBayDen != null ? session.tenSanBayDen : "") + " -> " + (session.tenSanBayDi != null ? session.tenSanBayDi : "");
            pnlSummary.add(createSummaryRow("Lộ trình:", loTrinhVe)); 
            pnlSummary.add(createSummaryRow("Khởi hành:", (session.thoiGianVe != null ? session.thoiGianVe : "")));
        }

        pnlSummary.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator sep = new JSeparator(); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); pnlSummary.add(sep);
        pnlSummary.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlSummary.add(createSectionLabel("DANH SÁCH HÀNH KHÁCH & GHẾ"));
        
        if(session.danhSachHanhKhach != null) {
            for(int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                String soGhe = "Đi: " + (session.danhSachGhe != null && i < session.danhSachGhe.size() ? session.danhSachGhe.get(i).getSoGhe() : "N/A");
                if ("Khứ hồi".equals(session.loaiVe) && session.danhSachGhe != null && session.danhSachGhe.size() > i + session.getTongSoHanhKhach()) {
                    soGhe += " | Về: " + session.danhSachGhe.get(i + session.getTongSoHanhKhach()).getSoGhe();
                }

                JLabel lblHkInfo = new JLabel("<html><b>" + (i + 1) + ". " + hk.getHoTen() + "</b> (" + hk.getLoaiHanhKhach() + ")<br>&nbsp;&nbsp;&nbsp;Ghế: <font color='red'><b>" + soGhe + "</b></font></html>");
                lblHkInfo.setFont(new Font("Segoe UI", Font.PLAIN, 15)); lblHkInfo.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 0)); lblHkInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlSummary.add(lblHkInfo);
            }
        }
        for (Component c : pnlSummary.getComponents()) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);

        // ================= PANEL PHẢI =================
        JPanel pnlBilling = new JPanel();
        pnlBilling.setLayout(new BoxLayout(pnlBilling, BoxLayout.Y_AXIS));
        pnlBilling.setBackground(Color.WHITE);
        pnlBilling.setBorder(createCustomTitledBorder("CHI TIẾT THANH TOÁN"));

        lblTongVe = createPriceLabel("Tiền vé cơ bản: 0 VNĐ");
        lblTongDV = createPriceLabel("Hành lý & Dịch vụ: 0 VNĐ");
        lblGiamGia = createPriceLabel("Giảm giá: 0 VNĐ");
        lblTongThanhToan = new JLabel("TỔNG CỘNG: 0 VNĐ"); lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 24)); lblTongThanhToan.setForeground(new Color(220, 38, 38));

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
        JLabel step1 = new JLabel("1. Chuyến bay"); step1.setForeground(Color.WHITE); step1.setFont(fontStep); JLabel arr1 = new JLabel(" "); arr1.setForeground(Color.WHITE); arr1.setFont(fontArrow);
        JLabel step2 = new JLabel("2. Hành khách"); step2.setForeground(Color.WHITE); step2.setFont(fontStep); JLabel arr2 = new JLabel(" "); arr2.setForeground(Color.WHITE); arr2.setFont(fontArrow);
        JLabel step3 = new JLabel("3. Dịch vụ"); step3.setForeground(Color.WHITE); step3.setFont(fontStep); JLabel arr3 = new JLabel(" "); arr3.setForeground(Color.WHITE); arr3.setFont(fontArrow);
        JLabel step4 = new JLabel("4. Thanh toán"); step4.setForeground(new Color(255, 193, 7)); step4.setFont(fontStep);
        pnlStepper.add(step1); pnlStepper.add(arr1); pnlStepper.add(step2); pnlStepper.add(arr2); pnlStepper.add(step3); pnlStepper.add(arr3); pnlStepper.add(step4);
        return pnlStepper;
    }

    private void switchPage(JPanel newPanel) {
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

    private JPanel createStickyFooter() {
        JPanel footer = new JPanel(new BorderLayout()); footer.setBackground(Color.WHITE); footer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)), new EmptyBorder(10, 50, 10, 50)));
        
        JButton btnQuayLai = new JButton("Quay lại"); 
        btnQuayLai.setBackground(Color.WHITE); btnQuayLai.setForeground(new Color(100, 100, 100)); btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 16)); btnQuayLai.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); btnQuayLai.setPreferredSize(new Dimension(200, 45));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnQuayLai.addActionListener(e -> switchPage(new gui.DichVuHanhLyGUI(session)));
        
        footer.add(btnQuayLai, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); pnlRight.setOpaque(false);
        JButton btnNext = new JButton("Thanh toán"); btnNext.setBackground(new Color(34, 197, 94)); btnNext.setForeground(Color.WHITE); btnNext.setFont(new Font("Segoe UI", Font.BOLD, 18)); btnNext.setPreferredSize(new Dimension(280, 45));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> processBooking());
        pnlRight.add(btnNext); footer.add(pnlRight, BorderLayout.EAST);
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
        lblTongThanhToan.setText("TỔNG CỘNG (Gồm 10% VAT): " + vn.format(finalAmount) + " VNĐ");
    }

    private void processBooking() {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán số tiền: " + lblTongThanhToan.getText() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        String generatedMaPDV = ""; 
        String generatedMaHD = "";  
        String ptThanhToan = radTienMat.isSelected() ? "Tiền mặt" : (radQR.isSelected() ? "Mã QR" : "Thẻ tín dụng");

        String maBac = "TH001"; // GẮN CHUẨN TH001 TRONG DB

        try {
            conn = db.DBConnection.getConnection();
            conn.setAutoCommit(false); 

            dal.PhieuDatVeDAO pdvDAO = new dal.PhieuDatVeDAO();
            generatedMaPDV = pdvDAO.generateMaPhieuDatVe(conn); 

            String sqlPDV = "INSERT INTO PhieuDatVe (maPhieuDatVe, maNguoiDung, tongTien, ngayDat, soLuongVe, trangThaiThanhToan, maKhuyenMai) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPDV)) {
                ps.setString(1, generatedMaPDV); ps.setString(2, session.maNguoiDung); ps.setBigDecimal(3, finalAmount); ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now())); ps.setInt(5, session.getTongSoHanhKhach()); ps.setString(6, "Đã thanh toán");
                if (session.khuyenMaiApDung != null) ps.setString(7, session.khuyenMaiApDung.getMaKhuyenMai()); else ps.setNull(7, java.sql.Types.VARCHAR);
                ps.executeUpdate();
            }

            String sqlHK = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, hoTen, cccd, hoChieu, gioiTinh, ngaySinh, loaiHanhKhach, maThuHang, diemTichLuy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            for (int i = 0; i < tongKhach; i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                try {
                    String prefixHK = currentMaHK.substring(0, 2); 
                    int numberPartHK = Integer.parseInt(currentMaHK.substring(2)); 
                    numberPartHK++; currentMaHK = String.format("%s%03d", prefixHK, numberPartHK); 
                } catch (Exception e) {}
                hk.setMaHK(currentMaHK);

                // 1. LƯU HỒ SƠ HÀNH KHÁCH
                try (PreparedStatement psHK = conn.prepareStatement(sqlHK)) {
                    psHK.setString(1, hk.getMaHK()); psHK.setString(2, session.maNguoiDung); psHK.setString(3, hk.getHoTen()); psHK.setString(4, hk.getCccd()); psHK.setString(5, hk.getHoChieu()); psHK.setString(6, hk.getGioiTinh());
                    if (hk.getNgaySinh() != null) psHK.setDate(7, java.sql.Date.valueOf(hk.getNgaySinh())); else psHK.setNull(7, java.sql.Types.DATE);
                    psHK.setString(8, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn");
                    psHK.setString(9, maBac); 
                    psHK.setInt(10, 0);
                    psHK.executeUpdate();
                }

                // 2. LƯU VÉ LƯỢT ĐI
                GheMayBay gheDi = session.danhSachGhe.get(i);
                String maHangVeDi = (session.maHangVe != null) ? session.maHangVe : "ECO";
                
                try (PreparedStatement psVe = conn.prepareStatement(sqlVe)) {
                    psVe.setString(1, currentMaVe); psVe.setString(2, generatedMaPDV); psVe.setString(3, hk.getMaHK()); 
                    psVe.setString(4, session.maChuyenBay); psVe.setString(5, gheDi.getMaGhe()); psVe.setString(6, maHangVeDi); 
                    psVe.setString(7, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn"); psVe.setString(8, loaiVeAnToan);
                    BigDecimal giaTungVe = session.tongTienVe.divide(new BigDecimal(tongKhach * ("Khứ hồi".equals(session.loaiVe) ? 2 : 1)), 2, java.math.RoundingMode.HALF_UP);
                    psVe.setBigDecimal(9, giaTungVe); 
                    
                    // ================= ĐÃ SỬA LẠI THÀNH "Đã thanh toán" ĐỂ KHÔNG BỊ LỖI CHECK CONSTRAINT =================
                    psVe.setString(10, "Đã thanh toán"); 
                    psVe.executeUpdate();
                }
                try { String p = currentMaVe.substring(0, 2); int n = Integer.parseInt(currentMaVe.substring(2)); currentMaVe = String.format("%s%03d", p, n + 1); } catch (Exception e) {}
                try (PreparedStatement psGhe = conn.prepareStatement(sqlGhe)) { psGhe.setString(1, gheDi.getMaGhe()); psGhe.executeUpdate(); }

                // 3. LƯU VÉ LƯỢT VỀ (NẾU CÓ KHỨ HỒI)
                if ("Khứ hồi".equals(session.loaiVe) && session.maChuyenBayVe != null && session.danhSachGhe.size() > i + tongKhach) {
                    GheMayBay gheVe = session.danhSachGhe.get(i + tongKhach);
                    String maHangVeVe = (session.maHangVeVe != null && !session.maHangVeVe.isEmpty()) ? session.maHangVeVe : "ECO";
                    String maCBVe = (session.maChuyenBayVe != null && !session.maChuyenBayVe.isEmpty()) ? session.maChuyenBayVe : session.maChuyenBay;

                    try (PreparedStatement psVe = conn.prepareStatement(sqlVe)) {
                        psVe.setString(1, currentMaVe); psVe.setString(2, generatedMaPDV); psVe.setString(3, hk.getMaHK()); 
                        psVe.setString(4, maCBVe); psVe.setString(5, gheVe.getMaGhe()); psVe.setString(6, maHangVeVe); 
                        psVe.setString(7, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Người lớn"); psVe.setString(8, loaiVeAnToan);
                        BigDecimal giaTungVe = session.tongTienVe.divide(new BigDecimal(tongKhach * 2), 2, java.math.RoundingMode.HALF_UP);
                        psVe.setBigDecimal(9, giaTungVe); 
                        
                        // ================= ĐÃ SỬA LẠI THÀNH "Đã thanh toán" =================
                        psVe.setString(10, "Đã thanh toán"); 
                        psVe.executeUpdate();
                    }
                    try { String p = currentMaVe.substring(0, 2); int n = Integer.parseInt(currentMaVe.substring(2)); currentMaVe = String.format("%s%03d", p, n + 1); } catch (Exception e) {}
                    try (PreparedStatement psGhe = conn.prepareStatement(sqlGhe)) { psGhe.setString(1, gheVe.getMaGhe()); psGhe.executeUpdate(); }
                }
            }

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

            JOptionPane.showMessageDialog(this, "🎉 CHÚC MỪNG BẠN ĐÃ ĐẶT VÉ THÀNH CÔNG!\nMã hóa đơn của bạn là: " + generatedMaHD, "Thành công", JOptionPane.INFORMATION_MESSAGE);

            String strNgayLap = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            String strTongTien = vn.format(finalAmount) + " VNĐ";

            switchPage(new gui.user.ThanhToanHoaDonPanel(generatedMaHD, generatedMaPDV, strNgayLap, strTongTien, ptThanhToan, "10%", session));

        } catch (Exception ex) {
            if (conn != null) try{ conn.rollback(); } catch(Exception ez){}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi SQL: Không thể lưu vé! \nChi tiết: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try{ conn.close(); } catch(Exception ez){}
        }
    }

    private JPanel createSummaryRow(String label, String value) { JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false); p.setBackground(Color.WHITE); p.setMaximumSize(new Dimension(800, 30)); JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 15)); l.setPreferredSize(new Dimension(150, 25)); JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.PLAIN, 15)); p.add(l, BorderLayout.WEST); p.add(v, BorderLayout.CENTER); return p; }
    private JLabel createSectionLabel(String text) { JLabel label = new JLabel(text); label.setFont(new Font("Segoe UI", Font.BOLD, 16)); label.setForeground(new Color(28, 48, 96)); label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0)); label.setAlignmentX(Component.LEFT_ALIGNMENT); return label; }
    private JLabel createPriceLabel(String text) { JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.PLAIN, 16)); l.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); return l; }
    private TitledBorder createCustomTitledBorder(String title) { TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), title); b.setTitleFont(new Font("Segoe UI", Font.BOLD, 16)); b.setTitleColor(new Color(28, 48, 96)); b.setBorder(BorderFactory.createCompoundBorder(b.getBorder(), BorderFactory.createEmptyBorder(10, 15, 15, 15))); return b; }
}