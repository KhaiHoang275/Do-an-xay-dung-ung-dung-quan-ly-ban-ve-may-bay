package gui.user;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dal.ChuyenBayDAO;
import dal.GheMayBayDAO;
import dal.KhuyenMaiDAO;
import dal.PhieuDatVeDAO;
import dal.VeBanDAO;
import db.DBConnection;
import gui.admin.SoDoGhePanel;
import model.GheMayBay;
import model.KhuyenMai;
import model.PhieuDatVe;
import model.VeBan;
import model.TrangThaiGhe;
import model.DatVeSession;

public class PanelUserVeBan extends JPanel {

    // ===== BẢNG MÀU THƯƠNG HIỆU AIRLINER =====
    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_VALUE = new Font("Segoe UI", Font.PLAIN, 16); 

    private final VeBanDAO veBanDAO = new VeBanDAO();
    private final PhieuDatVeDAO pdv = new PhieuDatVeDAO();
    private final KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
    private final GheMayBayDAO gmb = new GheMayBayDAO();

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
        JLabel formTitle = new JLabel("XÁC NHẬN THÔNG TIN ĐẶT VÉ", SwingConstants.CENTER);
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
        gbcAct.gridx = 0; gbcAct.gridy = 2; actionPanel.add(createStyledLabel("TỔNG THANH TOÁN:"), gbcAct);
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

        JButton btnLuu = new JButton("Xác nhận & Lưu vé");
        JButton btnHuy = new JButton("Quay lại tìm kiếm");
        stylePrimaryButton(btnLuu);
        styleSecondaryButton(btnHuy);
        btnLuu.setPreferredSize(new Dimension(220, 45));
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
            int tongHK = session.soNguoiLon + session.soTreEm + session.soEmBe;
            giaGhe = BigDecimal.ZERO;

            SoDoGhePanel sodoPanel = new SoDoGhePanel(maMayBayHienTai, "Máy bay", tongHK);
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn ghế", true);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);

            sodoPanel.setListener(new SoDoGhePanel.SoDoGheListener() {
                @Override public void onBack() { dialog.dispose(); }
                @Override public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
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

        btnLuu.addActionListener(e -> xulyLuuVe(btnHuy));
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

        // BỘ LỌC CHỐNG LỖI SẬP NULL POINTER NẾU QUÊN TRUYỀN HẠNG VÉ
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
        // Đảm bảo không bị null phần Loại Vé
        pnl.add(createSummaryRow("Loại vé:", (session.loaiVe != null ? session.loaiVe : "Một chiều"), false));

        pnl.add(createSummaryRow("Giờ khởi hành:", thoiGian, true));
        pnl.add(createSummaryRow("Hạng ghế:", tenHangVe, false));

        return pnl;
    }

    private JPanel createSummaryRow(String label, String value, boolean isHighlight) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(FONT_LABEL);
        lblTitle.setForeground(new Color(100, 110, 120)); 
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", isHighlight ? Font.BOLD : Font.PLAIN, 16));
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
            
            // Xử lý mặc định nếu hạng vé bị null từ MainFrame truyền sang
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

    private void xulyLuuVe(JButton btnHuy) {
        try {
            String maHK = session.maNguoiDung;
            String gheStr = txtGhe.getText().trim();
            if (gheStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sơ đồ ghế trước khi thanh toán!");
                return;
            }

            String[] dsGhe = gheStr.split(", ");
            int tongSoVe = session.soNguoiLon + session.soTreEm + session.soEmBe;
            if(dsGhe.length != tongSoVe){
                JOptionPane.showMessageDialog(this, "Số lượng ghế đã chọn ("+dsGhe.length+") không khớp với tổng số hành khách (" + tongSoVe + ")!");
                return;
            }

            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);
                
                String tienStr = txtTongTien.getText().replaceAll("[^0-9]", "");
                BigDecimal tongTienFinal = new BigDecimal(tienStr);

                PhieuDatVe phieu = new PhieuDatVe();
                phieu.setNgayDat(LocalDate.now());
                phieu.setSoLuongVe(tongSoVe);
                phieu.setTongTien(tongTienFinal);
                phieu.setTrangThaiThanhToan("Chưa thanh toán");

                String maPDV = pdv.insert(phieu, conn);
                if(maPDV == null) throw new RuntimeException("Không tạo được phiếu đặt vé!");
                
                String maHangVeAnToan = (session.maHangVe != null) ? session.maHangVe : "ECO";
                String loaiVeAnToan = (session.loaiVe != null) ? session.loaiVe : "Một chiều";
                int index = 0;

                for(int i=0;i<session.soNguoiLon;i++){
                    String maGhe = gmb.timMaGhe(conn, chuanHoaGhe(dsGhe[index++]), maMayBayHienTai);
                    taoVe(conn, maPDV, maHK, session.maChuyenBay, maGhe, maHangVeAnToan, "Người lớn", loaiVeAnToan);
                    veBanDAO.updateGheTrangThai(conn, maGhe, TrangThaiGhe.DA_DAT);
                }
                for(int i=0;i<session.soTreEm;i++){
                    String maGhe = gmb.timMaGhe(conn, chuanHoaGhe(dsGhe[index++]), maMayBayHienTai);
                    taoVe(conn, maPDV, maHK, session.maChuyenBay, maGhe, maHangVeAnToan, "Trẻ em", loaiVeAnToan);
                    veBanDAO.updateGheTrangThai(conn, maGhe, TrangThaiGhe.DA_DAT);
                }
                for(int i=0;i<session.soEmBe;i++){
                    String maGhe = gmb.timMaGhe(conn, chuanHoaGhe(dsGhe[index++]), maMayBayHienTai);
                    taoVe(conn, maPDV, maHK, session.maChuyenBay, maGhe, maHangVeAnToan, "Em bé", loaiVeAnToan);
                    veBanDAO.updateGheTrangThai(conn, maGhe, TrangThaiGhe.DA_DAT);
                }
                
                KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
                if(km != null) kmDAO.incrementSoLuongDaDung(conn, km.getMaKhuyenMai());

                conn.commit();
                JOptionPane.showMessageDialog(this,"🎉 Chúc mừng! Đặt vé thành công.");
                btnHuy.doClick(); 

            } catch(Exception ex) {
                if (conn != null) try{ conn.rollback(); } catch(Exception ez){}
                JOptionPane.showMessageDialog(this,"Lỗi khi lưu vé: " + ex.getMessage());
            } finally {
                if (conn != null) try { conn.close(); } catch (Exception ec){}
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    // ================= HELPER =================
    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR); btn.setForeground(PRIMARY_COLOR);
        btn.setFont(FONT_LABEL); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(PRIMARY_COLOR); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text); lbl.setFont(FONT_LABEL); lbl.setForeground(PRIMARY_COLOR); return lbl;
    }
    private void taoVe(Connection conn, String maPDV, String maHK, String maCB, String ghe, String maHangVe, String loaiHK, String loaiVe) throws SQLException {
        VeBan v = new VeBan();
        v.setMaPhieuDatVe(maPDV); v.setMaHK(maHK); v.setMaChuyenBay(maCB); v.setMaGhe(ghe); 
        v.setMaHangVe(maHangVe); v.setLoaiHK(loaiHK); v.setLoaiVe(loaiVe); 
        v.setGiaVe(veBanDAO.tinhGiaVeFull(maCB, maHangVe, loaiHK)); v.setTrangThaiVe("Đã đặt");
        veBanDAO.insert(v, conn);
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