package gui.user;

import javax.swing.*;
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

    // ===== BẢNG MÀU THƯƠNG HIỆU AIRLINER =====
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
    private BigDecimal giaGhe = BigDecimal.ZERO;
    private String maMayBayHienTai = "";

    private JTextField txtGhe;
    private JTextField txtTongTien;
    private JComboBox<KhuyenMai> cboKhuyenMai;

    // Giữ lại Constructor cũ để không bị lỗi đỏ nếu có module khác gọi tới
    public PanelUserVeBan(String maHK) {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        JLabel lbl = new JLabel("Trang Lịch sử đặt vé đang được dời sang giao diện khác...", SwingConstants.CENTER);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(PRIMARY_COLOR);
        add(lbl, BorderLayout.CENTER);
    }

    // CONSTRUCTOR CHÍNH NHẬN SESSION
    public PanelUserVeBan(DatVeSession session) {
        this.session = session;
        initPanel();
    }

    private void initPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
        setBackground(BG_MAIN);

        JPanel mainCard = new JPanel(new GridBagLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // --- 1. TIÊU ĐỀ ---
        JLabel formTitle = new JLabel("BƯỚC 1: XÁC NHẬN THÔNG TIN CHUYẾN BAY", SwingConstants.CENTER);
        formTitle.setFont(FONT_TITLE);
        formTitle.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 20, 0);
        mainCard.add(formTitle, gbc);
        row++;

        // --- 2. PANEL HIỂN THỊ THÔNG TIN CHI TIẾT ---
        JPanel infoPanel = createInfoSummaryPanel();
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 25, 0);
        mainCard.add(infoPanel, gbc);
        row++;

        // --- 3. PHẦN TƯƠNG TÁC (CHỌN GHẾ, KHUYẾN MÃI, TỔNG TIỀN) ---
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcAct = new GridBagConstraints();
        gbcAct.insets = new Insets(10, 15, 10, 15);
        gbcAct.fill = GridBagConstraints.HORIZONTAL;
        gbcAct.anchor = GridBagConstraints.CENTER;

        // Dòng Chọn ghế
        gbcAct.gridx = 0; gbcAct.gridy = 0; actionPanel.add(createStyledLabel("Ghế ngồi:"), gbcAct);
        txtGhe = new JTextField();
        txtGhe.setPreferredSize(new Dimension(250, 40));
        txtGhe.setFont(FONT_VALUE);
        txtGhe.setEditable(false);
        txtGhe.setBackground(Color.WHITE);
        gbcAct.gridx = 1; actionPanel.add(txtGhe, gbcAct);

        JButton btnChonGhe = new JButton("Chọn Sơ Đồ Ghế");
        styleSecondaryButton(btnChonGhe);
        gbcAct.gridx = 2; actionPanel.add(btnChonGhe, gbcAct);

        // Dòng Khuyến mãi
        gbcAct.gridx = 0; gbcAct.gridy = 1; actionPanel.add(createStyledLabel("Mã khuyến mãi:"), gbcAct);
        cboKhuyenMai = new JComboBox<>();
        cboKhuyenMai.setPreferredSize(new Dimension(250, 40));
        cboKhuyenMai.setFont(FONT_VALUE);
        cboKhuyenMai.setBackground(Color.WHITE);
        loadKhuyenMai();
        gbcAct.gridx = 1; gbcAct.gridwidth = 2; actionPanel.add(cboKhuyenMai, gbcAct);
        gbcAct.gridwidth = 1;

        // Dòng Tổng tiền
        gbcAct.gridx = 0; gbcAct.gridy = 2; actionPanel.add(createStyledLabel("TỔNG TIỀN VÉ:"), gbcAct);
        txtTongTien = new JTextField();
        txtTongTien.setPreferredSize(new Dimension(250, 45));
        txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
        txtTongTien.setForeground(new Color(220, 38, 38));
        txtTongTien.setEditable(false);
        txtTongTien.setBackground(new Color(255, 245, 245)); 
        gbcAct.gridx = 1; gbcAct.gridwidth = 2; actionPanel.add(txtTongTien, gbcAct);

        gbc.gridy = row;
        mainCard.add(actionPanel, gbc);
        row++;

        // --- 4. NÚT BẤM XÁC NHẬN ---
        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pButton.setBackground(Color.WHITE);

        JButton btnLuu = new JButton("Tiếp tục nhập Hành khách ⮕");
        JButton btnHuy = new JButton("Quay lại tìm kiếm");
        
        stylePrimaryButton(btnLuu);
        styleSecondaryButton(btnHuy);
        
        btnLuu.setBackground(new Color(34, 197, 94)); // Nút màu xanh lá đi tiếp
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(300, 45));
        btnHuy.setPreferredSize(new Dimension(180, 45));
        btnHuy.setBackground(new Color(108, 117, 125)); 

        pButton.add(btnLuu);
        pButton.add(btnHuy);

        gbc.gridy = row;
        mainCard.add(pButton, gbc);

        add(mainCard);

        tinhTongTien();

        // ================= XỬ LÝ SỰ KIỆN =================
        cboKhuyenMai.addActionListener(e -> tinhTongTien());

        btnHuy.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                model.NguoiDung dummyUser = new model.NguoiDung();
                dummyUser.setMaNguoiDung(session.maNguoiDung);
                dummyUser.setUsername(session.maNguoiDung);
                new MainFrame(dummyUser).setVisible(true);
                window.dispose();
            }
        });

        btnChonGhe.addActionListener(e -> {
            int tongHK = session.getTongSoHanhKhach(); // Đã dùng method getTongSoHanhKhach()
            giaGhe = BigDecimal.ZERO;

            SoDoGhePanel sodoPanel = new SoDoGhePanel(maMayBayHienTai, "Máy bay", tongHK);
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn ghế", true);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);

            sodoPanel.setListener(new SoDoGhePanel.SoDoGheListener() {
                @Override public void onBack() { dialog.dispose(); }
                
                @Override public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                    // LƯU TRỰC TIẾP DANH SÁCH GHẾ VÀO SESSION
                    session.danhSachGhe = new ArrayList<>(selectedSeats);
                    
                    List<String> dsGhe = new ArrayList<>();
                    giaGhe = BigDecimal.ZERO;
                    for(GheMayBay g : selectedSeats){
                        String maGheStr = chuanHoaGhe(g.getSoGhe());
                        dsGhe.add(maGheStr);
                        giaGhe = giaGhe.add(g.getGiaGhe());
                    }
                    txtGhe.setText(String.join(", ", dsGhe));
                    tinhTongTien();
                    dialog.dispose();
                }
            });
            dialog.add(sodoPanel);
            dialog.setVisible(true);
        });

        // CHUYỂN TRANG
        btnLuu.addActionListener(e -> xulyChuyenTrang());
    }

    // ================= TẠO KHUNG HIỂN THỊ THÔNG TIN TỔNG HỢP =================
    private JPanel createInfoSummaryPanel() {
        JPanel pnl = new JPanel(new GridLayout(5, 2, 20, 15));
        pnl.setBackground(new Color(248, 250, 252)); 
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        String hanhTrinh = "Đang tải dữ liệu...";
        String thoiGian = "Đang tải dữ liệu...";

        try(Connection conn = DBConnection.getConnection()){
            String sql = "SELECT cb.maMayBay, tb.sanBayDi, tb.sanBayDen, cb.ngayGioDi, cb.ngayGioDen " +
                         "FROM ChuyenBay cb JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay " +
                         "WHERE cb.maChuyenBay = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, session.maChuyenBay);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                maMayBayHienTai = rs.getString("maMayBay");
                hanhTrinh = rs.getString("sanBayDi") + "  ->  " + rs.getString("sanBayDen");
                
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
                thoiGian = rs.getTimestamp("ngayGioDi").toLocalDateTime().format(fmt);
            }
        }catch(Exception e){ e.printStackTrace(); }

        String tenHangVe = "Phổ thông";
        if (session.maHangVe != null) {
            switch (session.maHangVe) {
                case "BUS": tenHangVe = "Thương gia"; break;
                case "FST": tenHangVe = "Hạng nhất"; break;
                case "PECO": tenHangVe = "Phổ thông đặc biệt"; break;
            }
        }

        String slKhach = String.format("%d Người lớn", session.soNguoiLon);
        if(session.soTreEm > 0) slKhach += String.format(", %d Trẻ em", session.soTreEm);
        if(session.soEmBe > 0) slKhach += String.format(", %d Em bé", session.soEmBe);

        pnl.add(createSummaryRow("Mã chuyến bay:", session.maChuyenBay, true));
        pnl.add(createSummaryRow("Hành khách:", slKhach, false));

        pnl.add(createSummaryRow("Hành trình:", hanhTrinh, true));
        pnl.add(createSummaryRow("Loại vé:", (session.loaiVe != null ? session.loaiVe : "Một chiều"), false));

        pnl.add(createSummaryRow("Giờ khởi hành:", thoiGian, true));
        pnl.add(createSummaryRow("Hạng ghế:", tenHangVe, false));

        return pnl;
    }

    private JPanel createSummaryRow(String label, String value, boolean isHighlight) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(new Color(100, 110, 120)); 
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", isHighlight ? Font.BOLD : Font.PLAIN, 16));
        lblValue.setForeground(PRIMARY_COLOR);

        p.add(lblTitle);
        p.add(lblValue);
        return p;
    }

    // ================= LOGIC XỬ LÝ =================
    private void tinhTongTien() {
        try {
            if(session == null) return;

            BigDecimal tongTien = BigDecimal.ZERO;
            String maHangVeAnToan = (session.maHangVe != null) ? session.maHangVe : "ECO";
            
            tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(session.maChuyenBay, maHangVeAnToan, "Người lớn").multiply(BigDecimal.valueOf(session.soNguoiLon)));
            if(session.soTreEm > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(session.maChuyenBay, maHangVeAnToan, "Trẻ em").multiply(BigDecimal.valueOf(session.soTreEm)));
            if(session.soEmBe > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(session.maChuyenBay, maHangVeAnToan, "Em bé").multiply(BigDecimal.valueOf(session.soEmBe)));
            
            tongTien = tongTien.add(giaGhe);

            if("Khứ hồi".equalsIgnoreCase(session.loaiVe)){ 
                tongTien = tongTien.multiply(new BigDecimal("1.25")); 
            }

            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
            giamGia = BigDecimal.ZERO;
            if(km != null){
                if(km.getLoaiKM().equals("PHAN_TRAM")){ giamGia = tongTien.multiply(km.getGiaTri()).divide(new BigDecimal("100")); }
                else if(km.getLoaiKM().equals("TIEN")){ giamGia = km.getGiaTri(); }
                tongTien = tongTien.subtract(giamGia);
                if(tongTien.compareTo(BigDecimal.ZERO) < 0) tongTien = BigDecimal.ZERO;
            }

            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTongTien.setText(vn.format(tongTien) + " VND");
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    // HÀM MỚI: CHỈ LƯU VÀO SESSION VÀ CHUYỂN TRANG
    private void xulyChuyenTrang() {
        if (session.danhSachGhe == null || session.danhSachGhe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sơ đồ ghế trước khi tiếp tục!");
            return;
        }

        int tongSoVe = session.getTongSoHanhKhach();
        if(session.danhSachGhe.size() != tongSoVe){
            JOptionPane.showMessageDialog(this, "Số lượng ghế đã chọn (" + session.danhSachGhe.size() + ") không khớp với tổng số hành khách (" + tongSoVe + ")!");
            return;
        }

        // Đổ dữ liệu Tiền và Khuyến mãi vào Session chuẩn
        try {
            String tienStr = txtTongTien.getText().replaceAll("[^0-9]", "");
            session.tongTienVe = new BigDecimal(tienStr);
            
            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
            session.khuyenMaiApDung = km; // Cập nhật nguyên object Khuyến Mãi
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chuyển trang sang Form nhập hành khách
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(new NhapHanhKhachGUI(session), BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    // ================= HELPER =================
    private void stylePrimaryButton(JButton btn) {
        btn.setFont(FONT_LABEL); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(PRIMARY_COLOR); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text); lbl.setFont(FONT_LABEL); lbl.setForeground(PRIMARY_COLOR); return lbl;
    }
    private String chuanHoaGhe(String ghe){
        ghe = ghe.trim().toUpperCase(); if(Character.isLetter(ghe.charAt(0))) return ghe.substring(1) + ghe.charAt(0); return ghe;
    }
    private void loadKhuyenMai(){
        try(Connection conn = DBConnection.getConnection()){
            List<KhuyenMai> list = kmDAO.getKhuyenMaiDangHoatDong(conn);
            for(KhuyenMai km : list) cboKhuyenMai.addItem(km);
        }catch(Exception e){ e.printStackTrace(); }
    }
}