package gui.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dal.VeBanDAO;
import db.DBConnection;
import gui.admin.SoDoGhePanel;
import model.GheMayBay;
import model.KhuyenMai;
import model.DatVeSession;
import dal.KhuyenMaiDAO;

public class PanelUserVeBan extends JPanel {

    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_VALUE = new Font("Segoe UI", Font.PLAIN, 16);

    private final VeBanDAO veBanDAO = new VeBanDAO();
    private final KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();

    private DatVeSession session;
    private BigDecimal giamGia = BigDecimal.ZERO;
    
    // Dữ liệu lượt Đi
    private BigDecimal giaVeDi = BigDecimal.ZERO;
    private BigDecimal giaGheDi = BigDecimal.ZERO;
    private JTextField txtGheDi;
    private List<GheMayBay> selectedGheDi = new ArrayList<>();
    private String maMayBayDi = "";
    
    // Dữ liệu lượt Về
    private BigDecimal giaVeVe = BigDecimal.ZERO;
    private BigDecimal giaGheVe = BigDecimal.ZERO;
    private JTextField txtGheVe;
    private List<GheMayBay> selectedGheVe = new ArrayList<>();
    private String maMayBayVe = "";

    private JTextField txtTongTien;
    private JComboBox<KhuyenMai> cboKhuyenMai;
    private boolean isKhuHoi = false;
    private JLabel lblFooterTotal;

    public PanelUserVeBan(String maHK) {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        add(new JLabel("Trang Lịch sử đặt vé đang được dời sang giao diện khác...", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    public PanelUserVeBan(DatVeSession session) {
        this.session = session;
        this.isKhuHoi = "Khứ hồi".equalsIgnoreCase(session.loaiVe);
        
        tinhGiaVeCoBan();
        initPanel();
    }

    private void tinhGiaVeCoBan() {
        try {
            String hangVe = (session.maHangVe != null) ? session.maHangVe : "ECO";
            
            BigDecimal tongDi = BigDecimal.ZERO;
            tongDi = tongDi.add(veBanDAO.tinhGiaVeFull(session.maChuyenBay, hangVe, "Người lớn").multiply(BigDecimal.valueOf(session.soNguoiLon)));
            if(session.soTreEm > 0) tongDi = tongDi.add(veBanDAO.tinhGiaVeFull(session.maChuyenBay, hangVe, "Trẻ em").multiply(BigDecimal.valueOf(session.soTreEm)));
            if(session.soEmBe > 0) tongDi = tongDi.add(veBanDAO.tinhGiaVeFull(session.maChuyenBay, hangVe, "Em bé").multiply(BigDecimal.valueOf(session.soEmBe)));
            this.giaVeDi = tongDi;

            if (isKhuHoi) {
                this.giaVeVe = tongDi; 
            }
        } catch(Exception e) {}
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setOpaque(false); // Xuyên thấu hình nền

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);
        
        centerWrapper.add(createStepper());
        centerWrapper.add(Box.createVerticalStrut(20));

        // TIÊU ĐỀ
        JLabel formTitle = new JLabel("XÁC NHẬN THÔNG TIN CHUYẾN BAY", SwingConstants.CENTER);
        formTitle.setFont(FONT_TITLE);
        formTitle.setForeground(PRIMARY_COLOR);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrapper.add(formTitle);
        centerWrapper.add(Box.createVerticalStrut(20));

        // DANH SÁCH CHUYẾN BAY
        JPanel pnlListFlights = new JPanel();
        pnlListFlights.setLayout(new BoxLayout(pnlListFlights, BoxLayout.Y_AXIS));
        pnlListFlights.setOpaque(false);
        
        pnlListFlights.add(createFlightAccordion("CHUYẾN ĐI", session.maChuyenBay, true));
        pnlListFlights.add(Box.createVerticalStrut(15));
        
        if (isKhuHoi) {
            pnlListFlights.add(createFlightAccordion("CHUYẾN VỀ", session.maChuyenBay, false));
            pnlListFlights.add(Box.createVerticalStrut(15));
        }

        // KHUYẾN MÃI
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        GridBagConstraints gbcAct = new GridBagConstraints();
        gbcAct.insets = new Insets(10, 15, 10, 15);
        gbcAct.fill = GridBagConstraints.HORIZONTAL;

        gbcAct.gridx = 0; gbcAct.gridy = 0; actionPanel.add(createStyledLabel("Mã khuyến mãi:"), gbcAct);
        cboKhuyenMai = new JComboBox<>(); cboKhuyenMai.setPreferredSize(new Dimension(250, 40)); cboKhuyenMai.setFont(FONT_VALUE); cboKhuyenMai.setBackground(Color.WHITE);
        loadKhuyenMai();
        gbcAct.gridx = 1; actionPanel.add(cboKhuyenMai, gbcAct);

        gbcAct.gridx = 0; gbcAct.gridy = 1; actionPanel.add(createStyledLabel("TỔNG TIỀN VÉ:"), gbcAct);
        txtTongTien = new JTextField(); txtTongTien.setPreferredSize(new Dimension(250, 45)); txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 20)); txtTongTien.setForeground(new Color(220, 38, 38)); txtTongTien.setEditable(false); txtTongTien.setBackground(new Color(255, 245, 245)); 
        gbcAct.gridx = 1; actionPanel.add(txtTongTien, gbcAct);

        pnlListFlights.add(actionPanel);
        centerWrapper.add(pnlListFlights);
        
        JPanel scrollWrapper = new JPanel(new BorderLayout()); scrollWrapper.setOpaque(false);
        scrollWrapper.setBorder(BorderFactory.createEmptyBorder(0, 150, 20, 150));
        scrollWrapper.add(centerWrapper, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(scrollWrapper); scrollPane.setBorder(null); scrollPane.setOpaque(false); scrollPane.getViewport().setOpaque(false); scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // THÊM FOOTER CỐ ĐỊNH
        add(createStickyFooter(), BorderLayout.SOUTH);
        tinhTongTien();

        cboKhuyenMai.addActionListener(e -> tinhTongTien());
    }
    
    private JPanel createFlightAccordion(String tieuDe, String maCB, boolean isLuotDi) {
        JPanel wrapper = new JPanel(); wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS)); wrapper.setBackground(Color.WHITE); wrapper.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        JPanel pnlHeader = new JPanel(new BorderLayout()); pnlHeader.setBackground(new Color(248, 250, 252)); pnlHeader.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)), new EmptyBorder(15, 20, 15, 20))); pnlHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblTitle = new JLabel("▼ " + tieuDe + " (" + maCB + ")"); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblTitle.setForeground(PRIMARY_COLOR); pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        JPanel pnlContent = new JPanel(new GridBagLayout()); pnlContent.setBackground(Color.WHITE); pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20)); pnlContent.setVisible(true);
        
        String hanhTrinh = ""; String thoiGian = ""; String maMB = "";
        try(Connection conn = DBConnection.getConnection()){
            String sql = "SELECT cb.maMayBay, tb.sanBayDi, tb.sanBayDen, cb.ngayGioDi, cb.ngayGioDen FROM ChuyenBay cb JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay WHERE cb.maChuyenBay = ?";
            PreparedStatement ps = conn.prepareStatement(sql); ps.setString(1, maCB); ResultSet rs = ps.executeQuery();
            if(rs.next()){
                maMB = rs.getString("maMayBay"); hanhTrinh = rs.getString("sanBayDi") + " -> " + rs.getString("sanBayDen");
                thoiGian = rs.getTimestamp("ngayGioDi").toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
            }
        } catch(Exception e){}
        if (isLuotDi) maMayBayDi = maMB; else maMayBayVe = maMB;

        String tenHangVe = "Phổ thông";
        if (session.maHangVe != null) { switch (session.maHangVe) { case "BUS": tenHangVe = "Thương gia"; break; case "FST": tenHangVe = "Hạng nhất"; break; case "PECO": tenHangVe = "Phổ thông đặc biệt"; break; } }
        String slKhach = String.format("%d Người lớn", session.soNguoiLon); if(session.soTreEm > 0) slKhach += String.format(", %d Trẻ em", session.soTreEm); if(session.soEmBe > 0) slKhach += String.format(", %d Em bé", session.soEmBe);

        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridy = 0; gbc.gridx = 0; pnlContent.add(createStyledLabel("Hành trình:"), gbc); gbc.gridx = 1; pnlContent.add(new JLabel(hanhTrinh), gbc);
        gbc.gridy = 1; gbc.gridx = 0; pnlContent.add(createStyledLabel("Khởi hành:"), gbc); gbc.gridx = 1; pnlContent.add(new JLabel(thoiGian), gbc);
        gbc.gridy = 2; gbc.gridx = 0; pnlContent.add(createStyledLabel("Hạng vé:"), gbc); gbc.gridx = 1; pnlContent.add(new JLabel(tenHangVe), gbc);
        gbc.gridy = 3; gbc.gridx = 0; pnlContent.add(createStyledLabel("Hành khách:"), gbc); gbc.gridx = 1; pnlContent.add(new JLabel(slKhach), gbc);
        gbc.gridy = 4; gbc.gridx = 0; pnlContent.add(createStyledLabel("Ghế đã chọn:"), gbc);
        
        JPanel pnlGhe = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pnlGhe.setOpaque(false);
        JTextField txtGhe = new JTextField(); txtGhe.setPreferredSize(new Dimension(250, 40)); txtGhe.setEditable(false);
        JButton btnChonGhe = new JButton("Chọn Sơ Đồ Ghế"); btnChonGhe.setBackground(PRIMARY_COLOR); btnChonGhe.setForeground(Color.WHITE); btnChonGhe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        if (isLuotDi) txtGheDi = txtGhe; else txtGheVe = txtGhe;
        pnlGhe.add(txtGhe); pnlGhe.add(Box.createHorizontalStrut(10)); pnlGhe.add(btnChonGhe);
        gbc.gridx = 1; pnlContent.add(pnlGhe, gbc);

        pnlHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean isVis = pnlContent.isVisible(); pnlContent.setVisible(!isVis);
                lblTitle.setText((!isVis ? "▼ " : "► ") + tieuDe + " (" + maCB + ")"); wrapper.revalidate(); wrapper.repaint();
            }
        });
        btnChonGhe.addActionListener(e -> moSoDoGhe(isLuotDi));

        wrapper.add(pnlHeader); wrapper.add(pnlContent);
        return wrapper;
    }
    
    private void moSoDoGhe(boolean isLuotDi) {
        int tongHK = session.getTongSoHanhKhach(); 
        String mb = isLuotDi ? maMayBayDi : maMayBayVe;
        SoDoGhePanel sodoPanel = new SoDoGhePanel(mb, isLuotDi ? "Chuyến Đi" : "Chuyến Về", tongHK);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn ghế " + (isLuotDi ? "Lượt đi" : "Lượt về"), true);
        dialog.setSize(900, 600); dialog.setLocationRelativeTo(this);
        sodoPanel.setListener(new SoDoGhePanel.SoDoGheListener() {
            @Override public void onBack() { dialog.dispose(); }
            @Override public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                List<String> dsGheStr = new ArrayList<>(); BigDecimal tongGia = BigDecimal.ZERO;
                for(GheMayBay g : selectedSeats){ dsGheStr.add(chuanHoaGhe(g.getSoGhe())); tongGia = tongGia.add(g.getGiaGhe()); }
                if (isLuotDi) { selectedGheDi = new ArrayList<>(selectedSeats); giaGheDi = tongGia; txtGheDi.setText(String.join(", ", dsGheStr)); } 
                else { selectedGheVe = new ArrayList<>(selectedSeats); giaGheVe = tongGia; txtGheVe.setText(String.join(", ", dsGheStr)); }
                tinhTongTien(); dialog.dispose();
            }
        });
        dialog.add(sodoPanel); dialog.setVisible(true);
    }

    private JPanel createStickyFooter() {
        JPanel footer = new JPanel(new BorderLayout()); footer.setBackground(Color.WHITE); footer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)), new EmptyBorder(10, 50, 10, 50)));
        JButton btnQuayLai = new JButton("Quay lại Trang Chủ"); btnQuayLai.setBackground(Color.WHITE); btnQuayLai.setForeground(new Color(100, 100, 100)); btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 16)); btnQuayLai.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); btnQuayLai.setPreferredSize(new Dimension(200, 45)); btnQuayLai.setFocusPainted(false); btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                model.NguoiDung dummyUser = new model.NguoiDung(); dummyUser.setMaNguoiDung(session.maNguoiDung); dummyUser.setUsername(session.maNguoiDung);
                new MainFrame(dummyUser).setVisible(true); window.dispose();
            }
        });
        footer.add(btnQuayLai, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); pnlRight.setOpaque(false);
        JPanel pnlTotalText = new JPanel(new GridLayout(2, 1)); pnlTotalText.setOpaque(false);
        JLabel lblTo = new JLabel("Tổng tiền tạm tính:", SwingConstants.RIGHT); lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblFooterTotal = new JLabel("0 VNĐ", SwingConstants.RIGHT); lblFooterTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlTotalText.add(lblTo); pnlTotalText.add(lblFooterTotal);
        
        JButton btnNext = new JButton("Đi tiếp ➔"); btnNext.setBackground(new Color(255, 193, 7)); btnNext.setForeground(new Color(18, 32, 64)); btnNext.setFont(new Font("Segoe UI", Font.BOLD, 18)); btnNext.setPreferredSize(new Dimension(150, 45)); btnNext.setFocusPainted(false); btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> xulyChuyenTrang());
        
        pnlRight.add(pnlTotalText); pnlRight.add(btnNext); footer.add(pnlRight, BorderLayout.EAST);
        return footer;
    }

    private JPanel createStepper() {
        JPanel pnlStepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); pnlStepper.setBackground(new Color(18, 32, 64, 200)); pnlStepper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        Font fontStep = new Font("Segoe UI", Font.BOLD, 16); Font fontArrow = new Font("Segoe UI", Font.BOLD, 18);
        JLabel step1 = new JLabel("1. Chuyến bay"); step1.setForeground(ACCENT_COLOR); step1.setFont(fontStep); JLabel arr1 = new JLabel(" ➔ "); arr1.setForeground(Color.WHITE); arr1.setFont(fontArrow);
        JLabel step2 = new JLabel("2. Hành khách"); step2.setForeground(Color.LIGHT_GRAY); step2.setFont(fontStep); JLabel arr2 = new JLabel(" ➔ "); arr2.setForeground(Color.LIGHT_GRAY); arr2.setFont(fontArrow);
        JLabel step3 = new JLabel("3. Dịch vụ"); step3.setForeground(Color.LIGHT_GRAY); step3.setFont(fontStep); JLabel arr3 = new JLabel(" ➔ "); arr3.setForeground(Color.LIGHT_GRAY); arr3.setFont(fontArrow);
        JLabel step4 = new JLabel("4. Thanh toán"); step4.setForeground(Color.LIGHT_GRAY); step4.setFont(fontStep);
        pnlStepper.add(step1); pnlStepper.add(arr1); pnlStepper.add(step2); pnlStepper.add(arr2); pnlStepper.add(step3); pnlStepper.add(arr3); pnlStepper.add(step4);
        return pnlStepper;
    }

    private void tinhTongTien() {
        try {
            if(session == null) return;
            BigDecimal tongTien = giaVeDi.add(giaVeVe).add(giaGheDi).add(giaGheVe);
            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
            giamGia = BigDecimal.ZERO;
            if(km != null){
                if(km.getLoaiKM().equals("PHAN_TRAM")){ giamGia = tongTien.multiply(km.getGiaTri()).divide(new BigDecimal("100")); }
                else if(km.getLoaiKM().equals("TIEN")){ giamGia = km.getGiaTri(); }
                tongTien = tongTien.subtract(giamGia);
                if(tongTien.compareTo(BigDecimal.ZERO) < 0) tongTien = BigDecimal.ZERO;
            }
            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            String text = vn.format(tongTien) + " VND";
            txtTongTien.setText(text);
            lblFooterTotal.setText(text);
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void xulyChuyenTrang() {
        int tongSoVe = session.getTongSoHanhKhach();
        if (selectedGheDi.isEmpty() || selectedGheDi.size() != tongSoVe) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ " + tongSoVe + " ghế cho lượt đi!"); return; }
        if (isKhuHoi && (selectedGheVe.isEmpty() || selectedGheVe.size() != tongSoVe)) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ " + tongSoVe + " ghế cho lượt về!"); return; }

        session.danhSachGhe = new ArrayList<>(selectedGheDi);
        if (isKhuHoi) { session.danhSachGhe.addAll(selectedGheVe); }

        try {
            String tienStr = txtTongTien.getText().replaceAll("[^0-9]", "");
            session.tongTienVe = new BigDecimal(tienStr);
            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
            session.khuyenMaiApDung = km; 
        } catch (Exception e) {}

        // ĐÃ SỬA: CHUYỂN TRANG MÀ KHÔNG LÀM MẤT NAVBAR GỐC BÊN MAINFRAME
        Container parent = this.getParent();
        if (parent != null) {
            parent.removeAll();
            parent.add(new NhapHanhKhachGUI(session), BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        }
    }

    private JLabel createStyledLabel(String text) { JLabel lbl = new JLabel(text); lbl.setFont(FONT_LABEL); lbl.setForeground(PRIMARY_COLOR); return lbl; }
    private String chuanHoaGhe(String ghe){ ghe = ghe.trim().toUpperCase(); if(Character.isLetter(ghe.charAt(0))) return ghe.substring(1) + ghe.charAt(0); return ghe; }
    private void loadKhuyenMai(){ try(Connection conn = DBConnection.getConnection()){ List<KhuyenMai> list = kmDAO.getKhuyenMaiDangHoatDong(conn); for(KhuyenMai km : list) cboKhuyenMai.addItem(km); }catch(Exception e){} }
}