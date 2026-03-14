package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dal.ChuyenBayDAO;
import dal.GheMayBayDAO;
import dal.PhieuDatVeDAO;
import dal.VeBanDAO;
import db.DBConnection;
import model.GheMayBay;
import model.PhieuDatVe;
import model.VeBan;

public class VeBanPanel extends JPanel {

    // ĐỒNG BỘ MÀU SẮC VỚI GIAO DIỆN USER
    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 15);

    private JTable table;
    private DefaultTableModel tableModel;
    private final VeBanDAO veBanDAO = new VeBanDAO();
    private final PhieuDatVeDAO pdv = new PhieuDatVeDAO();
    private final GheMayBayDAO gmb = new GheMayBayDAO();
    private final ChuyenBayDAO cbDAO = new ChuyenBayDAO();

    private JTextField txtSearch;
    private JComboBox<String> cboTrangThai;

    // COMPONENTS CHO FORM ĐẶT VÉ
    private JTextField txtTenNguoiDat, txtMaHK;
    private JRadioButton rdMotChieu, rdKhuHoi;
    private JComboBox<String> cboHangVe;
    private JSpinner spNguoiLon, spTreEm, spEmBe;
    private JTextField txtTongTien;

    // CHUYẾN ĐI
    private JTextField txtMaChuyenBayDi, txtGheDi;
    private BigDecimal giaGheDi = BigDecimal.ZERO;
    private List<GheMayBay> selectedGheDi = new ArrayList<>();

    // CHUYẾN VỀ (Dùng cho Khứ Hồi)
    private JPanel pnlChuyenVe;
    private JTextField txtMaChuyenBayVe, txtGheVe;
    private BigDecimal giaGheVe = BigDecimal.ZERO;
    private List<GheMayBay> selectedGheVe = new ArrayList<>();

    public VeBanPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_MAIN);
        add(initHeader(), BorderLayout.NORTH);
        add(initTabs(), BorderLayout.CENTER);
        loadTable();
    }

    // ================= HEADER =================
    private JPanel initHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("QUẢN LÝ VÉ BÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(PRIMARY_COLOR);

        panel.add(title, BorderLayout.NORTH);
        panel.add(initSearchBar(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel initSearchBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblTim = new JLabel("Tìm (Mã vé / PNR): ");
        lblTim.setFont(FONT_BOLD);
        panel.add(lblTim);

        txtSearch = new JTextField();
        txtSearch.setMaximumSize(new Dimension(200, 35));
        txtSearch.setFont(FONT_PLAIN);
        panel.add(txtSearch);

        panel.add(Box.createHorizontalStrut(15));

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(ACCENT_COLOR);
        btnSearch.setForeground(PRIMARY_COLOR);
        btnSearch.setFont(FONT_BOLD);
        btnSearch.setFocusPainted(false);
        panel.add(btnSearch);

        panel.add(Box.createHorizontalStrut(30));

        JLabel lblTrangThai = new JLabel("Trạng thái: ");
        lblTrangThai.setFont(FONT_BOLD);
        panel.add(lblTrangThai);

        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy"});
        cboTrangThai.setMaximumSize(new Dimension(150, 35));
        cboTrangThai.setFont(FONT_PLAIN);
        panel.add(cboTrangThai);

        btnSearch.addActionListener(e -> searchVe());

        return panel;
    }

    // ================= TABS =================
    private JTabbedPane initTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("Danh sách vé", initTablePanel());
        tabs.addTab("Tạo vé máy bay (Admin)", initFormPanel());
        return tabs;
    }

    // ================= TABLE PANEL =================
    private JPanel initTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        String[] cols = {"Mã vé", "Mã PNR", "Chuyến bay", "Loại HK", "Loại vé", "Giá vé", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ================= FORM PANEL (ĐÃ LÀM LẠI GIỐNG USER LỚP) =================
    private JPanel initFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        int row = 0;

        // --- KHÁCH HÀNG ---
        txtTenNguoiDat = new JTextField(20); txtTenNguoiDat.setFont(FONT_PLAIN);
        txtMaHK = new JTextField(15); txtMaHK.setEditable(false); txtMaHK.setFont(FONT_PLAIN);
        JButton btnTimHK = new JButton("Tìm kiếm HK"); btnTimHK.setBackground(PRIMARY_COLOR); btnTimHK.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Tên Khách Hàng:"), gbc);
        gbc.gridx = 1; panel.add(txtTenNguoiDat, gbc);
        gbc.gridx = 2; panel.add(btnTimHK, gbc);
        gbc.gridx = 3; panel.add(createStyledLabel("Mã HK:"), gbc);
        gbc.gridx = 4; panel.add(txtMaHK, gbc);
        row++;

        // --- LOẠI VÉ & HẠNG VÉ ---
        rdMotChieu = new JRadioButton("Một chiều", true); rdMotChieu.setBackground(Color.WHITE); rdMotChieu.setFont(FONT_BOLD);
        rdKhuHoi = new JRadioButton("Khứ hồi"); rdKhuHoi.setBackground(Color.WHITE); rdKhuHoi.setFont(FONT_BOLD);
        ButtonGroup group = new ButtonGroup(); group.add(rdMotChieu); group.add(rdKhuHoi);
        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT)); pLoai.setBackground(Color.WHITE); pLoai.add(rdMotChieu); pLoai.add(rdKhuHoi);
        
        cboHangVe = new JComboBox<>(new String[]{"Phổ thông", "Phổ thông đặc biệt", "Thương gia", "Hạng nhất"});
        cboHangVe.setFont(FONT_PLAIN);

        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Hành trình:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(pLoai, gbc); gbc.gridwidth = 1;
        gbc.gridx = 3; panel.add(createStyledLabel("Hạng vé:"), gbc);
        gbc.gridx = 4; panel.add(cboHangVe, gbc);
        row++;

        // --- SỐ LƯỢNG HÀNH KHÁCH ---
        spNguoiLon = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spTreEm = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spEmBe = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        
        JPanel pnlHanhKhach = new JPanel(new GridLayout(1, 6, 10, 0)); pnlHanhKhach.setBackground(Color.WHITE);
        pnlHanhKhach.add(createStyledLabel("Ng.Lớn:")); pnlHanhKhach.add(spNguoiLon);
        pnlHanhKhach.add(createStyledLabel("Trẻ em:")); pnlHanhKhach.add(spTreEm);
        pnlHanhKhach.add(createStyledLabel("Em bé:")); pnlHanhKhach.add(spEmBe);

        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Số hành khách:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4; panel.add(pnlHanhKhach, gbc); gbc.gridwidth = 1;
        row++;

        // --- CHUYẾN ĐI ---
        txtMaChuyenBayDi = new JTextField(); txtMaChuyenBayDi.setEditable(false);
        txtGheDi = new JTextField(); txtGheDi.setEditable(false);
        JButton btnChonCBDi = new JButton("Chọn CB Đi"); btnChonCBDi.setBackground(PRIMARY_COLOR); btnChonCBDi.setForeground(Color.WHITE);
        JButton btnChonGheDi = new JButton("Sơ đồ Ghế"); btnChonGheDi.setBackground(ACCENT_COLOR); btnChonGheDi.setForeground(PRIMARY_COLOR);

        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Chuyến Đi:"), gbc);
        gbc.gridx = 1; panel.add(txtMaChuyenBayDi, gbc);
        gbc.gridx = 2; panel.add(btnChonCBDi, gbc);
        gbc.gridx = 3; panel.add(txtGheDi, gbc);
        gbc.gridx = 4; panel.add(btnChonGheDi, gbc);
        row++;

        // --- CHUYẾN VỀ (Bọc trong 1 Panel để ẩn hiện) ---
        txtMaChuyenBayVe = new JTextField(); txtMaChuyenBayVe.setEditable(false);
        txtGheVe = new JTextField(); txtGheVe.setEditable(false);
        JButton btnChonCBVe = new JButton("Chọn CB Về"); btnChonCBVe.setBackground(PRIMARY_COLOR); btnChonCBVe.setForeground(Color.WHITE);
        JButton btnChonGheVe = new JButton("Sơ đồ Ghế"); btnChonGheVe.setBackground(ACCENT_COLOR); btnChonGheVe.setForeground(PRIMARY_COLOR);
        
        gbc.gridx = 0; gbc.gridy = row; JLabel lblVe = createStyledLabel("Chuyến Về:"); panel.add(lblVe, gbc);
        gbc.gridx = 1; panel.add(txtMaChuyenBayVe, gbc);
        gbc.gridx = 2; panel.add(btnChonCBVe, gbc);
        gbc.gridx = 3; panel.add(txtGheVe, gbc);
        gbc.gridx = 4; panel.add(btnChonGheVe, gbc);
        
        // Mặc định ẩn chuyến về
        lblVe.setVisible(false); txtMaChuyenBayVe.setVisible(false); btnChonCBVe.setVisible(false); txtGheVe.setVisible(false); btnChonGheVe.setVisible(false);
        row++;

        // --- TỔNG TIỀN ---
        txtTongTien = new JTextField();
        txtTongTien.setEditable(false);
        txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        txtTongTien.setForeground(new Color(220, 38, 38));
        txtTongTien.setBackground(new Color(255, 245, 245));
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);

        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("TỔNG TIỀN:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(txtTongTien, gbc); gbc.gridwidth = 1;
        row++;

        // --- BUTTONS ---
        JButton btnLuu = new JButton("Tạo Phiếu & Lưu Vé"); btnLuu.setBackground(ACCENT_COLOR); btnLuu.setFont(FONT_BOLD);
        JButton btnReset = new JButton("Làm mới"); btnReset.setFont(FONT_BOLD);
        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); pButton.setBackground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(200, 45)); btnReset.setPreferredSize(new Dimension(120, 45));
        pButton.add(btnLuu); pButton.add(btnReset);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 5; panel.add(pButton, gbc);

        // ================= LOGIC SỰ KIỆN =================
        
        // Ẩn hiện chuyến về theo loại vé
        java.awt.event.ActionListener rbListener = e -> {
            boolean isKhuHoi = rdKhuHoi.isSelected();
            lblVe.setVisible(isKhuHoi); txtMaChuyenBayVe.setVisible(isKhuHoi); btnChonCBVe.setVisible(isKhuHoi); txtGheVe.setVisible(isKhuHoi); btnChonGheVe.setVisible(isKhuHoi);
            if (!isKhuHoi) { txtMaChuyenBayVe.setText(""); txtGheVe.setText(""); selectedGheVe.clear(); giaGheVe = BigDecimal.ZERO; }
            tinhTongTien();
        };
        rdMotChieu.addActionListener(rbListener); rdKhuHoi.addActionListener(rbListener);

        // Lắng nghe thay đổi để tính lại tiền
        javax.swing.event.ChangeListener spinnerListener = e -> tinhTongTien();
        spNguoiLon.addChangeListener(spinnerListener); spTreEm.addChangeListener(spinnerListener); spEmBe.addChangeListener(spinnerListener);
        cboHangVe.addActionListener(e -> tinhTongTien());

        // Focus out tự tìm khách
        txtTenNguoiDat.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { btnTimHK.doClick(); }
        });

        btnTimHK.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT maHK FROM ThongTinHanhKhach WHERE hoTen = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtTenNguoiDat.getText().trim());
                ResultSet rs = ps.executeQuery();
                if(rs.next()) txtMaHK.setText(rs.getString("maHK"));
                else JOptionPane.showMessageDialog(this,"Không tìm thấy hành khách!");
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Lỗi tìm hành khách: " + ex.getMessage()); }
        });

        // Chọn chuyến bay
        btnChonCBDi.addActionListener(e -> chonChuyenBay(txtMaChuyenBayDi));
        btnChonCBVe.addActionListener(e -> chonChuyenBay(txtMaChuyenBayVe));

        // Chọn ghế
        btnChonGheDi.addActionListener(e -> chonGhe(txtMaChuyenBayDi, txtGheDi, true));
        btnChonGheVe.addActionListener(e -> chonGhe(txtMaChuyenBayVe, txtGheVe, false));

        btnReset.addActionListener(e -> resetForm());

        // Xử lý LƯU
        btnLuu.addActionListener(e -> xuLyLuuVe());

        return panel; // Bọc Scrollpane cho giao diện nhỏ
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(PRIMARY_COLOR);
        return lbl;
    }

    // ================= CHỌN CHUYẾN BAY =================
    private void chonChuyenBay(JTextField targetTextField) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn chuyến bay", true);
        dialog.setSize(800, 450); dialog.setLocationRelativeTo(this);

        String[] cols = {"Mã CB", "Sân bay đi", "Sân bay đến", "Máy bay", "Giờ khởi hành"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tb = new JTable(model); tb.setRowHeight(25); tb.setFont(FONT_PLAIN);

        try(Connection conn = DBConnection.getConnection()){
            String sql = "SELECT cb.maChuyenBay, tb.sanBayDi, tb.sanBayDen, cb.maMayBay, cb.ngayGioDi " +
                         "FROM ChuyenBay cb JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while(rs.next()){
                model.addRow(new Object[]{ rs.getString("maChuyenBay"), rs.getString("sanBayDi"), rs.getString("sanBayDen"), rs.getString("maMayBay"), sdf.format(rs.getTimestamp("ngayGioDi")) });
            }
        }catch(Exception ex){ JOptionPane.showMessageDialog(this, "Lỗi load chuyến bay!"); }

        tb.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                int row = tb.getSelectedRow();
                targetTextField.setText(tb.getValueAt(row, 0).toString());
                // Xóa ghế cũ nếu đổi chuyến bay
                if (targetTextField == txtMaChuyenBayDi) { txtGheDi.setText(""); selectedGheDi.clear(); giaGheDi = BigDecimal.ZERO; }
                else { txtGheVe.setText(""); selectedGheVe.clear(); giaGheVe = BigDecimal.ZERO; }
                tinhTongTien(); dialog.dispose();
            }
        });
        dialog.add(new JScrollPane(tb)); dialog.setVisible(true);
    }

    // ================= CHỌN GHẾ =================
    private void chonGhe(JTextField txtCB, JTextField txtGhe, boolean isDi) {
        String maCB = txtCB.getText().trim();
        if(maCB.isEmpty()){ JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay trước!"); return; }

        int tongHK = (int)spNguoiLon.getValue() + (int)spTreEm.getValue() + (int)spEmBe.getValue();
        String maMayBay = layMaMayBay(maCB);

        SoDoGhePanel panelSodo = new SoDoGhePanel(maMayBay, isDi ? "Chuyến Đi" : "Chuyến Về", tongHK);
        
        // Pass các ghế cũ vào panel (nếu đã chọn)
        List<String> currentSelectedStr = new ArrayList<>();
        if (!txtGhe.getText().isEmpty()) {
            for (String g : txtGhe.getText().split(",")) currentSelectedStr.add(g.trim());
        }
        panelSodo.setSelectedSeats(currentSelectedStr);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn Sơ đồ ghế", true);
        dialog.setSize(900, 600); dialog.setLocationRelativeTo(this);

        panelSodo.setListener(new SoDoGhePanel.SoDoGheListener() {
            @Override public void onBack() { dialog.dispose(); }
            @Override public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                List<String> dsGhe = new ArrayList<>();
                BigDecimal tongGiaGhe = BigDecimal.ZERO;
                for(GheMayBay ghe : selectedSeats){
                    dsGhe.add(chuanHoaGhe(ghe.getSoGhe()));
                    tongGiaGhe = tongGiaGhe.add(ghe.getGiaGhe());
                }
                txtGhe.setText(String.join(", ", dsGhe));
                if (isDi) { selectedGheDi = new ArrayList<>(selectedSeats); giaGheDi = tongGiaGhe; } 
                else { selectedGheVe = new ArrayList<>(selectedSeats); giaGheVe = tongGiaGhe; }
                
                tinhTongTien(); dialog.dispose();
            }
        });
        dialog.add(panelSodo); dialog.setVisible(true);
    }

    // ================= TÍNH TOÁN (ĐỒNG BỘ VỚI USER) =================
    private void tinhTongTien() {
        try {
            int soNL = (int) spNguoiLon.getValue();
            int soTE = (int) spTreEm.getValue();
            int soEB = (int) spEmBe.getValue();
            String maHangVe = convertHangVe(cboHangVe.getSelectedItem().toString());

            BigDecimal tongTien = BigDecimal.ZERO;

            // 1. Tính tiền LƯỢT ĐI
            String cbDi = txtMaChuyenBayDi.getText().trim();
            if (!cbDi.isEmpty()) {
                tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbDi, maHangVe, "Người lớn").multiply(BigDecimal.valueOf(soNL)));
                if(soTE > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbDi, maHangVe, "Trẻ em").multiply(BigDecimal.valueOf(soTE)));
                if(soEB > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbDi, maHangVe, "Em bé").multiply(BigDecimal.valueOf(soEB)));
                tongTien = tongTien.add(giaGheDi);
            }

            // 2. Tính tiền LƯỢT VỀ (NẾU CÓ)
            if (rdKhuHoi.isSelected()) {
                String cbVe = txtMaChuyenBayVe.getText().trim();
                if (!cbVe.isEmpty()) {
                    tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbVe, maHangVe, "Người lớn").multiply(BigDecimal.valueOf(soNL)));
                    if(soTE > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbVe, maHangVe, "Trẻ em").multiply(BigDecimal.valueOf(soTE)));
                    if(soEB > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbVe, maHangVe, "Em bé").multiply(BigDecimal.valueOf(soEB)));
                    tongTien = tongTien.add(giaGheVe);
                }
            }

            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTongTien.setText(tongTien.compareTo(BigDecimal.ZERO) == 0 ? "" : vn.format(tongTien) + " VNĐ");

        } catch (Exception e) { txtTongTien.setText("Lỗi tính giá"); }
    }

    // ================= XỬ LÝ LƯU VÉ =================
    private void xuLyLuuVe() {
        String maHK = txtMaHK.getText().trim();
        String cbDi = txtMaChuyenBayDi.getText().trim();
        boolean isKhuHoi = rdKhuHoi.isSelected();
        int tongHK = (int) spNguoiLon.getValue() + (int) spTreEm.getValue() + (int) spEmBe.getValue();

        if (maHK.isEmpty() || cbDi.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng điền đủ thông tin Khách hàng và Chuyến đi!"); return; }
        if (selectedGheDi.size() != tongHK) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ " + tongHK + " ghế cho Chuyến Đi!"); return; }
        
        String cbVe = txtMaChuyenBayVe.getText().trim();
        if (isKhuHoi) {
            if (cbVe.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn Chuyến Về!"); return; }
            if (selectedGheVe.size() != tongHK) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ " + tongHK + " ghế cho Chuyến Về!"); return; }
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String tongTienStr = txtTongTien.getText().replaceAll("[^0-9]", "");
            BigDecimal tongTienDB = new BigDecimal(tongTienStr);

            // 1. Lưu Phiếu
            PhieuDatVe phieu = new PhieuDatVe();
            phieu.setNgayDat(LocalDate.now());
            phieu.setSoLuongVe(isKhuHoi ? tongHK * 2 : tongHK);
            phieu.setTongTien(tongTienDB);
            phieu.setTrangThaiThanhToan("Chưa thanh toán");

            String maPDV = pdv.insert(phieu, conn);
            if(maPDV == null) throw new RuntimeException("Lỗi tạo Phiếu Đặt Vé");

            String loaiVe = isKhuHoi ? "Khứ hồi" : "Một chiều";
            String maHangVe = convertHangVe(cboHangVe.getSelectedItem().toString());

            // 2. Lưu vé Lượt Đi
            luuDanhSachVe(conn, maPDV, maHK, cbDi, maHangVe, loaiVe, selectedGheDi);
            
            // 3. Lưu vé Lượt Về (Nếu có)
            if (isKhuHoi) {
                luuDanhSachVe(conn, maPDV, maHK, cbVe, maHangVe, loaiVe, selectedGheVe);
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Tạo vé thành công!");
            resetForm();
            loadTable();

        } catch (Exception ex) {
            ex.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu vé: " + ex.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void luuDanhSachVe(Connection conn, String maPDV, String maHK, String maCB, String maHangVe, String loaiVe, List<GheMayBay> seats) throws Exception {
        int soNL = (int) spNguoiLon.getValue();
        int soTE = (int) spTreEm.getValue();
        int idx = 0;
        String maMB = cbDAO.layMaMayBay(conn, maCB);

        for (GheMayBay ghe : seats) {
            String loaiHK = "Em bé";
            if (idx < soNL) loaiHK = "Người lớn";
            else if (idx < soNL + soTE) loaiHK = "Trẻ em";
            
            String maGheDB = gmb.timMaGhe(conn, ghe.getSoGhe(), maMB);
            if(maGheDB == null) throw new RuntimeException("Không tìm thấy mã ghế DB cho: " + ghe.getSoGhe());

            VeBan v = new VeBan();
            v.setMaPhieuDatVe(maPDV);
            v.setMaHK(maHK);
            v.setMaChuyenBay(maCB);
            v.setMaGhe(maGheDB);
            v.setMaHangVe(maHangVe);
            v.setLoaiHK(loaiHK);
            v.setLoaiVe(loaiVe);
            v.setGiaVe(veBanDAO.tinhGiaVeFull(maCB, maHangVe, loaiHK));
            v.setTrangThaiVe("Đã đặt");

            veBanDAO.insert(v, conn);
            idx++;
        }
    }

    private void resetForm() {
        txtTenNguoiDat.setText(""); txtMaHK.setText("");
        rdMotChieu.setSelected(true); cboHangVe.setSelectedIndex(0);
        spNguoiLon.setValue(1); spTreEm.setValue(0); spEmBe.setValue(0);
        
        txtMaChuyenBayDi.setText(""); txtGheDi.setText(""); selectedGheDi.clear(); giaGheDi = BigDecimal.ZERO;
        txtMaChuyenBayVe.setText(""); txtGheVe.setText(""); selectedGheVe.clear(); giaGheVe = BigDecimal.ZERO;
        
        txtTongTien.setText("");
        // Kích hoạt sự kiện đổi radio để ẩn panel chuyến về
        for (java.awt.event.ActionListener a : rdMotChieu.getActionListeners()) { a.actionPerformed(new java.awt.event.ActionEvent(rdMotChieu, 0, "")); }
    }

    // ================= TIỆN ÍCH =================
    private void searchVe() {
        String kw = txtSearch.getText().trim();
        String stt = cboTrangThai.getSelectedItem().toString();
        if (stt.equals("Tất cả")) stt = null;
        tableModel.setRowCount(0);
        for (VeBan v : veBanDAO.search(kw, stt)) {
            tableModel.addRow(new Object[]{ v.getMaVe(), v.getMaPhieuDatVe(), v.getMaChuyenBay(), v.getLoaiHK(), v.getLoaiVe(), v.getGiaVe(), v.getTrangThaiVe() });
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        for (VeBan v : veBanDAO.selectAll()) {
            tableModel.addRow(new Object[]{ v.getMaVe(), v.getMaPhieuDatVe(), v.getMaChuyenBay(), v.getLoaiHK(), v.getLoaiVe(), v.getGiaVe(), v.getTrangThaiVe() });
        }
    }

    private String convertHangVe(String ten) {
        switch (ten) {
            case "Thương gia": return "BUS";
            case "Phổ thông": return "ECO";
            case "Hạng nhất": return "FST";
            case "Phổ thông đặc biệt": return "PECO";
        }
        return "ECO";
    }

    private String layMaMayBay(String maCB) {
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT maMayBay FROM ChuyenBay WHERE maChuyenBay = ?");
            ps.setString(1, maCB); ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getString("maMayBay");
        }catch(Exception e){} return null;
    }

    private String chuanHoaGhe(String ghe) {
        ghe = ghe.trim().toUpperCase();
        if(Character.isLetter(ghe.charAt(0))) return ghe.substring(1) + ghe.charAt(0);
        return ghe;
    }
}