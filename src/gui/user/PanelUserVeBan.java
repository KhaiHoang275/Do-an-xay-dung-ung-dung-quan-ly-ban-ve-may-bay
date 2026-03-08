package gui.user;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

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

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PanelUserVeBan extends JPanel {
    private String maHK;
    private JTable table;
    private DefaultTableModel tableModel;
    private final VeBanDAO veBanDAO = new VeBanDAO();
    private JTextField txtSearch;
    private JComboBox<String> cboTrangThai;
    private JRadioButton rdMotChieu;
    private JRadioButton rdKhuHoi;
    private JButton btnDoiVe;
    private PhieuDatVeDAO pdv = new PhieuDatVeDAO();
    private JTextField txtTongTien;
    private KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
    private JComboBox<KhuyenMai> cboKhuyenMai;
    private BigDecimal giamGia = BigDecimal.ZERO;
    private BigDecimal giaGhe = BigDecimal.ZERO;
    private GheMayBayDAO gmb = new GheMayBayDAO();
    private ChuyenBayDAO cbDAO = new ChuyenBayDAO();

    public PanelUserVeBan(String maHK) {
        this.maHK = maHK;
        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));

        add(initHeader(), BorderLayout.NORTH);
        add(initTabs(), BorderLayout.CENTER);
        loadTable();
        loadKhuyenMai();
    }

    // ================= HEADER =================
    private JPanel initHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("QUẢN LÝ VÉ BÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(220, 38, 38));

        panel.add(title, BorderLayout.NORTH);
        panel.add(initSearchBar(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel initSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Tìm (Mã vé / Mã PNR):"));

        txtSearch = new JTextField(25);
        panel.add(txtSearch);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(52,73,140));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        panel.add(btnSearch);

        panel.add(Box.createHorizontalStrut(30));

        panel.add(new JLabel("Trạng thái:"));

        cboTrangThai = new JComboBox<>(
                new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy"}
        );
        panel.add(cboTrangThai);

        btnSearch.addActionListener(e -> searchVe());

        return panel;
    }

    // ================= TABS =================
    private JTabbedPane initTabs() {

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.addTab("Danh sách vé", initTablePanel());
        tabs.addTab("Tạo vé máy bay", initFormPanel());

        return tabs;
    }

    // ================= TABLE PANEL =================
    private JPanel initTablePanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("DANH SÁCH VÉ BÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(25,40,80));

        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
                "Mã vé", "Mã PNR", "Chuyến bay",
                "Loại HK", "Loại vé",
                "Giá vé", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getSelectionModel().addListSelectionListener(e -> {
        int row = table.getSelectedRow();

        if(row == -1){
            btnDoiVe.setEnabled(false);
            return;
        }

        String trangThai = table.getValueAt(row,6).toString();

        if(trangThai.equalsIgnoreCase("Đã hủy")){
            btnDoiVe.setEnabled(true);
        } else {
            btnDoiVe.setEnabled(false);
        }

    });
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Style header xanh đậm
        table.getTableHeader().setBackground(new Color(20,40,90));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButton.setBackground(Color.WHITE);

        btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.setEnabled(false); // disable mặc định
        btnDoiVe.setBackground(new Color(59,130,246));
        btnDoiVe.setForeground(Color.WHITE);

        panelButton.add(btnDoiVe);

        panel.add(panelButton, BorderLayout.SOUTH);

        btnDoiVe.addActionListener(e -> {
        int row = table.getSelectedRow();

        if(row == -1) return;

        String maVe = table.getValueAt(row,0).toString();

        JFrame frame = new JFrame("Đổi vé");
        frame.setSize(900,600);
        frame.setLocationRelativeTo(null);

        frame.setContentPane(new DoiVePanel(maVe, maHK));

        frame.setVisible(true);

    });

        return panel;
    }

    // ================= FORM PANEL =================
    private JPanel initFormPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        TitledBorder border = BorderFactory.createTitledBorder("FORM TẠO VÉ BÁN");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        border.setTitleColor(new Color(25,40,80)); 
        panel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,20,10,20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        JTextField txtMaChuyenBay = new JTextField(15);
        txtMaChuyenBay.setEditable(false);

        JTextField txtGhe = new JTextField(15);
        txtGhe.setEditable(false);

        JButton btnChonCB = new JButton("Chọn");
        JButton btnChonGhe = new JButton("Chọn");

        JComboBox<String> cboHangVe = new JComboBox<>(new String[]{
                "Thương gia",
                "Phổ thông",
                "Hạng nhất",
                "Phổ thông đặc biệt"
        });

        JSpinner spNguoiLon = new JSpinner(new SpinnerNumberModel(1,1,10,1));
        JSpinner spTreEm = new JSpinner(new SpinnerNumberModel(0,0,10,1));
        JSpinner spEmBe = new JSpinner(new SpinnerNumberModel(0,0,10,1));
        ((JSpinner.DefaultEditor) spNguoiLon.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) spTreEm.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) spEmBe.getEditor()).getTextField().setEditable(false);

        rdMotChieu = new JRadioButton("Một chiều");
        rdKhuHoi = new JRadioButton("Khứ hồi");
        rdMotChieu.addActionListener(e ->
            tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));

        rdKhuHoi.addActionListener(e ->
            tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));
        ButtonGroup group = new ButtonGroup();
        group.add(rdMotChieu);
        group.add(rdKhuHoi);
        rdMotChieu.setSelected(true);

        JButton btnLuu = new JButton("Lưu");
        JButton btnReset = new JButton("Reset");


        // ===== DÒNG 2 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Chuyến bay:"), gbc);
        gbc.gridx=1; panel.add(txtMaChuyenBay, gbc);
        gbc.gridx=2; panel.add(btnChonCB, gbc);

        gbc.gridx=3; panel.add(new JLabel("Ghế:"), gbc);
        gbc.gridx=4; panel.add(txtGhe, gbc);
        gbc.gridx=5; panel.add(btnChonGhe, gbc);
        row++;

        // ===== DÒNG 3 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Loại vé:"), gbc);

        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        pLoai.add(rdMotChieu);
        pLoai.add(rdKhuHoi);

        gbc.gridx=1; panel.add(pLoai, gbc);

        gbc.gridx=3; panel.add(new JLabel("Hạng vé:"), gbc);
        gbc.gridx=4; panel.add(cboHangVe, gbc);
        row++;

        // ===== DÒNG 4 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Người lớn:"), gbc);
        gbc.gridx=1; panel.add(spNguoiLon, gbc);

        gbc.gridx=3; panel.add(new JLabel("Trẻ em:"), gbc);
        gbc.gridx=4; panel.add(spTreEm, gbc);
        row++;

        // ===== DÒNG 5 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Em bé:"), gbc);
        gbc.gridx=1; panel.add(spEmBe, gbc);
        row++;

        // ===== DÒNG 6 =====
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("Tổng tiền:"), gbc);

        txtTongTien = new JTextField(15);
        txtTongTien.setEditable(false);

        gbc.gridx=1;
        panel.add(txtTongTien, gbc);

        row++;

        // ===== DÒNG 7 - KHUYẾN MÃI =====
        gbc.gridx = 0; 
        gbc.gridy = row;
        panel.add(new JLabel("Khuyến mãi:"), gbc);

        cboKhuyenMai = new JComboBox<>();

        gbc.gridx = 1; 
        panel.add(cboKhuyenMai, gbc);

row++;

        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        Dimension btnSize = new Dimension(120, 35);
        btnLuu.setPreferredSize(btnSize);
        btnReset.setPreferredSize(btnSize);

        pButton.add(btnLuu);
        pButton.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.gridwidth = 6;   // cho nó chiếm hết hàng
        panel.add(pButton, gbc);

        // ================= LOGIC =================

        btnChonCB.addActionListener(e -> 
            chonChuyenBay(txtMaChuyenBay, cboHangVe, spNguoiLon, spTreEm, spEmBe)
        );

        cboKhuyenMai.addActionListener(e -> tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));

        // Chọn ghế (demo)
        btnChonGhe.addActionListener(e -> {

        int tongHK =
                (int) spNguoiLon.getValue() +
                (int) spTreEm.getValue() +
                (int) spEmBe.getValue();        
        
        String maCB = txtMaChuyenBay.getText().trim();


        if(maCB.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chuyến bay trước khi chọn ghế!");
            return;
        }

        String maMayBay = layMaMayBay(txtMaChuyenBay.getText());
        giaGhe = BigDecimal.ZERO;
        SoDoGhePanel Panel = new SoDoGhePanel(maMayBay, "Máy bay", tongHK);
        List<String> ds = new ArrayList<>();
        String ghe = txtGhe.getText().trim();
        if(!ghe.isEmpty()){
            for(String g : ghe.split(",")){
                ds.add(chuanHoaGhe(g));
            }
        }
        Panel.setSelectedSeats(ds);

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn ghế",
                true
        );

        dialog.setSize(900,600);
        dialog.setLocationRelativeTo(this);

        Panel.setListener(new SoDoGhePanel.SoDoGheListener() {

            @Override
            public void onBack() {
                dialog.dispose();
            }

            public void onSeatSelected(GheMayBay ghe) {

                String maGhe = chuanHoaGhe(ghe.getSoGhe());
                String gheHienTai = txtGhe.getText().trim();

                List<String> dsGhe = new ArrayList<>();

                if (!gheHienTai.isEmpty()) {
                    for(String g : gheHienTai.split(",")){
                        dsGhe.add(chuanHoaGhe(g));
                    }
                }

                int tongHK =
                        (int) spNguoiLon.getValue() +
                        (int) spTreEm.getValue() +
                        (int) spEmBe.getValue();

                if (dsGhe.contains(maGhe)) {
                    dsGhe.remove(maGhe);
                    giaGhe = giaGhe.subtract(ghe.getGiaGhe());
                }
                else {

                    if (dsGhe.size() >= tongHK) {
                        JOptionPane.showMessageDialog(dialog,
                                "Chỉ được chọn tối đa " + tongHK + " ghế!");
                        return;
                    }

                    dsGhe.add(maGhe);
                    giaGhe = giaGhe.add(ghe.getGiaGhe());
                }

                txtGhe.setText(String.join(",", dsGhe));

                tinhTongTien(txtMaChuyenBay, cboHangVe, spNguoiLon, spTreEm, spEmBe);
            }
        });

        dialog.add(Panel);

        dialog.setVisible(true);
    });

        // LƯU VÉ
        btnLuu.addActionListener(e -> {
            try {

                String maHK = this.maHK;
                String maCB = txtMaChuyenBay.getText().trim();
                String ghe = txtGhe.getText().trim();
                String[] dsGhe = ghe.split(",");

                if (maHK.isEmpty() || maCB.isEmpty() || ghe.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Thiếu thông tin!");
                    return;
                }

                String maHangVe = convertHangVe(cboHangVe.getSelectedItem().toString());
                String loaiVe = rdMotChieu.isSelected() ? "Một chiều" : "Khứ hồi";

                int soNguoiLon = (int) spNguoiLon.getValue();
                int soTreEm = (int) spTreEm.getValue();
                int soEmBe = (int) spEmBe.getValue();

                int tongSoVe = soNguoiLon + soTreEm + soEmBe;
                if (tongSoVe == 0) {
                    JOptionPane.showMessageDialog(this, "Phải có ít nhất 1 hành khách!");
                    return;
                }

                if(dsGhe.length != tongSoVe){
                    JOptionPane.showMessageDialog(this,
                            "Số ghế phải bằng số hành khách!");
                    return;
                }

                BigDecimal tongTien = BigDecimal.ZERO;

                for(int i=0;i<soNguoiLon;i++)
                    tongTien = tongTien.add(
                            veBanDAO.tinhGiaVeFull(maCB, maHangVe, "Người lớn"));

                for(int i=0;i<soTreEm;i++)
                    tongTien = tongTien.add(
                            veBanDAO.tinhGiaVeFull(maCB, maHangVe, "Trẻ em"));

                for(int i=0;i<soEmBe;i++)
                    tongTien = tongTien.add(
                            veBanDAO.tinhGiaVeFull(maCB, maHangVe, "Em bé"));
                
                tongTien = tongTien.add(giaGhe);
                tongTien = tongTien.subtract(giamGia);
                Connection conn = null;
                try{
                conn = DBConnection.getConnection();    
                conn.setAutoCommit(false);

                PhieuDatVe phieu = new PhieuDatVe();
                phieu.setNgayDat(LocalDate.now());
                phieu.setSoLuongVe(tongSoVe);
                phieu.setTongTien(tongTien);
                phieu.setTrangThaiThanhToan("Chưa thanh toán");

                String maPDV = pdv.insert(phieu, conn);
                if(maPDV == null){
                    throw new RuntimeException("Không tạo được phiếu đặt vé!");
                }
                int index = 0;
                String maMB = cbDAO.layMaMayBay(conn, maCB);

                for(int i=0;i<soNguoiLon;i++){
                    String soGhe = chuanHoaGhe(dsGhe[index++]);
                    String maGhe = gmb.timMaGhe(conn, soGhe, maMB);
                    if(maGhe == null){
                        throw new RuntimeException("Không tìm thấy ghế " + soGhe);
                    }
                    taoVe(conn, maPDV, maHK, maCB, maGhe, maHangVe, "Người lớn", loaiVe);
                }

                for(int i=0;i<soTreEm;i++){
                    String soGhe = chuanHoaGhe(dsGhe[index++]);
                    String maGhe = gmb.timMaGhe(conn, soGhe, maMB);
                    if(maGhe == null){
                        throw new RuntimeException("Không tìm thấy ghế " + soGhe);
                    }
                    taoVe(conn, maPDV, maHK, maCB, maGhe, maHangVe, "Trẻ em", loaiVe);
                }

                for(int i=0;i<soEmBe;i++){
                    String soGhe = chuanHoaGhe(dsGhe[index++]);
                    String maGhe = gmb.timMaGhe(conn, soGhe, maMB);
                    if(maGhe == null){
                        throw new RuntimeException("Không tìm thấy ghế " + soGhe);
                    }
                    taoVe(conn, maPDV, maHK, maCB, maGhe, maHangVe, "Em bé", loaiVe);
                }
                // cập nhật số lượng khuyến mãi đã dùng
                KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();

                if(km != null){
                    kmDAO.incrementSoLuongDaDung(conn, km.getMaKhuyenMai());
                }

                conn.commit();

                JOptionPane.showMessageDialog(this,"Tạo vé thành công!");

                loadTable();

            }catch(Exception ex){
                ex.printStackTrace();
                try{
                    conn.rollback();
                }catch(Exception ez){
                    ez.printStackTrace();
                }
                JOptionPane.showMessageDialog(this,"Lỗi khi lưu vé: " + ex.getMessage());
            }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi tạo vé!");
            }
        });

        btnReset.addActionListener(e -> {
            txtMaChuyenBay.setText("");
            txtGhe.setText("");
            spNguoiLon.setValue(1);
            spTreEm.setValue(0);
            spEmBe.setValue(0);
            txtTongTien.setText("");
            giaGhe = BigDecimal.ZERO;
            cboKhuyenMai.setSelectedIndex(0);
            giamGia = BigDecimal.ZERO;
        });

        spNguoiLon.addChangeListener(e ->
            tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));

        spTreEm.addChangeListener(e ->
            tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));

        spEmBe.addChangeListener(e ->
            tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));

        cboHangVe.addActionListener(e ->
            tinhTongTien(txtMaChuyenBay,cboHangVe,spNguoiLon,spTreEm,spEmBe));

        return panel;
    }

    private void taoVe(Connection conn,
                   String maPDV,
                   String maHK,
                   String maCB,
                   String ghe,
                   String maHangVe,
                   String loaiHK,
                   String loaiVe) throws SQLException {

        VeBan v = new VeBan();

        BigDecimal gia = veBanDAO.tinhGiaVeFull(maCB, maHangVe, loaiHK);

        v.setMaPhieuDatVe(maPDV);
        v.setMaHK(maHK);
        v.setMaChuyenBay(maCB);
        v.setMaGhe(ghe);
        v.setMaHangVe(maHangVe);
        v.setLoaiHK(loaiHK);
        v.setLoaiVe(loaiVe);
        v.setGiaVe(gia);
        v.setTrangThaiVe("Đã đặt");

        veBanDAO.insert(v, conn);
    }

    private void searchVe() {
        String keyword = txtSearch.getText().trim();
        String trangThai = cboTrangThai.getSelectedItem().toString().trim();

        if (trangThai.equals("Tất cả")) {
            trangThai = null;
        }

        tableModel.setRowCount(0);

        List<VeBan> list = veBanDAO.searchByMaHK(maHK, keyword, trangThai);

        for (VeBan v : list) {
            tableModel.addRow(new Object[]{
                v.getMaVe(),
                v.getMaPhieuDatVe(),
                v.getMaChuyenBay(),
                v.getLoaiHK(),
                v.getLoaiVe(),
                v.getGiaVe(),
                v.getTrangThaiVe()
            });
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<VeBan> list = veBanDAO.selectByMaHK(maHK);

        for (VeBan v : list) {
            tableModel.addRow(new Object[]{
                v.getMaVe(),
                v.getMaPhieuDatVe(),
                v.getMaChuyenBay(),
                v.getLoaiHK(),
                v.getLoaiVe(),
                v.getGiaVe(),
                v.getTrangThaiVe()
            });
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

    private void chonChuyenBay(JTextField txtMaCB,
                           JComboBox<String> cboHangVe,
                           JSpinner spNguoiLon,
                           JSpinner spTreEm,
                           JSpinner spEmBe){
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn chuyến bay", true);
        dialog.setSize(700,400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {
                "Mã CB",
                "Sân bay đi",
                "Sân bay đến",
                "Máy bay",
                "Giờ khởi hành"
        };

        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable tb = new JTable(model);
        JScrollPane scroll = new JScrollPane(tb);

        dialog.add(scroll);
        

        try(Connection conn = DBConnection.getConnection()){

            String sql = """
            SELECT 
                cb.maChuyenBay,
                tb.sanBayDi,
                tb.sanBayDen,
                cb.maMayBay,
                cb.ngayGioDi
            FROM ChuyenBay cb
            JOIN TuyenBay tb 
            ON cb.maTuyenBay = tb.maTuyenBay
            WHERE tb.trangThai = N'Hoạt động'
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getString("maChuyenBay"),
                    rs.getString("sanBayDi"),
                    rs.getString("sanBayDen"),
                    rs.getString("maMayBay"),
                    rs.getTimestamp("ngayGioDi")
                });
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        tb.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                int row = tb.getSelectedRow();
                String maCB = tb.getValueAt(row,0).toString();
                txtMaCB.setText(maCB);
                tinhTongTien(txtMaCB,cboHangVe,spNguoiLon,spTreEm,spEmBe);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void tinhTongTien(JTextField txtMaChuyenBay,
                          JComboBox<String> cboHangVe,
                          JSpinner spNguoiLon,
                          JSpinner spTreEm,
                          JSpinner spEmBe) {
        try {

            String maCB = txtMaChuyenBay.getText().trim();
            if(maCB.isEmpty()) return;

            if(cboHangVe.getSelectedItem() == null) return;

            String maHangVe = convertHangVe(cboHangVe.getSelectedItem().toString());

            int soNguoiLon = (int) spNguoiLon.getValue();
            int soTreEm = (int) spTreEm.getValue();
            int soEmBe = (int) spEmBe.getValue();

            BigDecimal tongTien = BigDecimal.ZERO;

            tongTien = tongTien.add(
                    veBanDAO.tinhGiaVeFull(maCB, maHangVe, "Người lớn")
                            .multiply(BigDecimal.valueOf(soNguoiLon)));

            tongTien = tongTien.add(
                    veBanDAO.tinhGiaVeFull(maCB, maHangVe, "Trẻ em")
                            .multiply(BigDecimal.valueOf(soTreEm)));

            tongTien = tongTien.add(
                    veBanDAO.tinhGiaVeFull(maCB, maHangVe, "Em bé")
                            .multiply(BigDecimal.valueOf(soEmBe)));

            tongTien = tongTien.add(giaGhe);
            if(rdKhuHoi.isSelected()){
                tongTien = tongTien.multiply(new BigDecimal("1.25"));
            }

            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();

            if(km != null){

                if(km.getLoaiKM().equals("PHAN_TRAM")){
                    giamGia = tongTien.multiply(km.getGiaTri())
                            .divide(new BigDecimal("100"));
                }
                else if(km.getLoaiKM().equals("TIEN")){
                    giamGia = km.getGiaTri();
                }

                tongTien = tongTien.subtract(giamGia);
                if(tongTien.compareTo(BigDecimal.ZERO) < 0){
                    tongTien = BigDecimal.ZERO;
                }
            }

            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTongTien.setText(vn.format(tongTien) + " VND");

        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String layMaMayBay(String maCB){
        try(Connection conn = DBConnection.getConnection()){

            String sql = "SELECT maMayBay FROM ChuyenBay WHERE maChuyenBay = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,maCB);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getString("maMayBay");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private String chuanHoaGhe(String ghe){
        ghe = ghe.trim().toUpperCase();

        if(Character.isLetter(ghe.charAt(0))){
            return ghe.substring(1) + ghe.charAt(0);
        }
        return ghe;
    }

    private void loadKhuyenMai(){
        try(Connection conn = DBConnection.getConnection()){

            List<KhuyenMai> list =
                    kmDAO.getKhuyenMaiDangHoatDong(conn);

            cboKhuyenMai.addItem(null);

            for(KhuyenMai km : list){
                cboKhuyenMai.addItem(km);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test Panel User Vé Bán");

            frame.setSize(1200,700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // mã hành khách test
            String maHK = "HK010";

            PanelUserVeBan panel = new PanelUserVeBan(maHK);

            frame.add(panel);

            frame.setVisible(true);

        });
    }
}