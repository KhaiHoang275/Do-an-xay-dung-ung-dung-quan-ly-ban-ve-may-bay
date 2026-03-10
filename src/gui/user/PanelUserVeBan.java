package gui.user;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

import dal.ChuyenBayDAO;
import dal.GheMayBayDAO;
import dal.KhuyenMaiDAO;
import dal.PhieuDatVeDAO;
import dal.ThongTinHanhKhachDAO;
import dal.VeBanDAO;
import db.DBConnection;
import gui.admin.SoDoGhePanel;
import model.GheMayBay;
import model.KhuyenMai;
import model.PhieuDatVe;
import model.VeBan;
import model.TrangThaiGhe;

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
    private ThongTinHanhKhachDAO hkDAO = new ThongTinHanhKhachDAO();

    private JTabbedPane tabs;
    private JTextField txtMaChuyenBay;
    private JTextField txtGhe;
    private JComboBox<String> cboHangVe;
    private JSpinner spNguoiLon;
    private JSpinner spTreEm;
    private JSpinner spEmBe;

    public PanelUserVeBan(String maHK) {
        this.maHK = maHK;
        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));

        add(initHeader(), BorderLayout.NORTH);
        add(initTabs(), BorderLayout.CENTER);
        loadTable();
        loadKhuyenMai();
    }

    // CONSTRUCTOR NHẬN DỮ LIỆU TỪ MAINFRAME
    public PanelUserVeBan(model.DatVeSession session) {
        // 1. Lấy mã khách hàng (người dùng) từ Session
        this.maHK = session.maNguoiDung;

        // 2. Khởi tạo UI (Giao diện)
        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));

        add(initHeader(), BorderLayout.NORTH);

        this.tabs = initTabs(); // Tạo các tab (Trong này sẽ chạy hàm initFormPanel)
        add(this.tabs, BorderLayout.CENTER);

        loadTable();
        loadKhuyenMai();

        // 3. Đổ dữ liệu từ Session vào Form "Tạo vé máy bay"
        if (session != null) {
            this.tabs.setSelectedIndex(1); // Tự động nhảy sang tab Tạo vé

            // Gán các giá trị từ MainFrame sang Form
            this.txtMaChuyenBay.setText(session.maChuyenBay);
            this.spNguoiLon.setValue(session.soNguoiLon);
            this.spTreEm.setValue(session.soTreEm);
            this.spEmBe.setValue(session.soEmBe);

            if ("Khứ hồi".equalsIgnoreCase(session.loaiVe)) {
                this.rdKhuHoi.setSelected(true);
            } else {
                this.rdMotChieu.setSelected(true);
            }

            // Tự động tính tiền luôn cho khách xem
            tinhTongTien(this.txtMaChuyenBay, this.cboHangVe, this.spNguoiLon, this.spTreEm, this.spEmBe);
        }
    }

    // ================= HEADER =================
    private JPanel initHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.setBackground(Color.WHITE);
        String tenHK = hkDAO.getTenHanhKhach(maHK);

        String displayName = (tenHK != null && !tenHK.trim().isEmpty()) ? tenHK.toUpperCase() : "KHÁCH HÀNG";  // Default nếu null

        JLabel title = new JLabel("LỊCH SỬ VÉ CỦA" + " " + displayName);
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

        this.txtSearch = new JTextField(25);
        panel.add(this.txtSearch);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(52,73,140));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        panel.add(btnSearch);

        panel.add(Box.createHorizontalStrut(30));

        panel.add(new JLabel("Trạng thái:"));

        this.cboTrangThai = new JComboBox<>(
                new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy"}
        );
        panel.add(this.cboTrangThai);

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

        JLabel title = new JLabel("DANH SÁCH VÉ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(25,40,80));

        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
                "Mã vé", "Mã PNR", "Chuyến bay",
                "Loại HK", "Loại vé",
                "Giá vé", "Trạng thái"
        };

        this.tableModel = new DefaultTableModel(cols,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.table = new JTable(this.tableModel);

        this.table.getSelectionModel().addListSelectionListener(e -> {

            int row = this.table.getSelectedRow();

            if(row == -1){
                this.btnDoiVe.setEnabled(false);
                return;
            }

            String maVe = this.table.getValueAt(row,0).toString();

            List<VeBan> dsVeDoi = veBanDAO.selectVeCoTheDoi(maHK);

            boolean coTheDoi = false;

            for(VeBan v : dsVeDoi){
                if(v.getMaVe().equals(maVe)){
                    coTheDoi = true;
                    break;
                }
            }

            this.btnDoiVe.setEnabled(coTheDoi);
        });
        this.table.setRowHeight(28);
        this.table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Style header xanh đậm
        this.table.getTableHeader().setBackground(new Color(20,40,90));
        this.table.getTableHeader().setForeground(Color.WHITE);
        this.table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(this.table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButton.setBackground(Color.WHITE);

        this.btnDoiVe = new JButton("Đổi vé");
        this.btnDoiVe.setEnabled(false); // disable mặc định
        this.btnDoiVe.setBackground(new Color(59,130,246));
        this.btnDoiVe.setForeground(Color.WHITE);

        panelButton.add(this.btnDoiVe);

        panel.add(panelButton, BorderLayout.SOUTH);

        this.btnDoiVe.addActionListener(e -> {
            int row = this.table.getSelectedRow();

            if(row == -1) return;

            String maVe = this.table.getValueAt(row,0).toString();

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
        TitledBorder border = BorderFactory.createTitledBorder("ĐẶT VÉ");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        border.setTitleColor(new Color(25,40,80));
        panel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,20,10,20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        this.txtMaChuyenBay = new JTextField(15);
        this.txtMaChuyenBay.setEditable(false);

        this.txtGhe = new JTextField(15);
        this.txtGhe.setEditable(false);

        JButton btnChonCB = new JButton("Chọn");
        JButton btnChonGhe = new JButton("Chọn");

        this.cboHangVe = new JComboBox<>(new String[]{
                "Thương gia",
                "Phổ thông",
                "Hạng nhất",
                "Phổ thông đặc biệt"
        });

        this.spNguoiLon = new JSpinner(new SpinnerNumberModel(1,1,10,1));
        this.spTreEm = new JSpinner(new SpinnerNumberModel(0,0,10,1));
        this.spEmBe = new JSpinner(new SpinnerNumberModel(0,0,10,1));
        ((JSpinner.DefaultEditor) this.spNguoiLon.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) this.spTreEm.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) this.spEmBe.getEditor()).getTextField().setEditable(false);

        this.rdMotChieu = new JRadioButton("Một chiều");
        this.rdKhuHoi = new JRadioButton("Khứ hồi");
        this.rdMotChieu.addActionListener(e ->
                tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));

        this.rdKhuHoi.addActionListener(e ->
                tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));
        ButtonGroup group = new ButtonGroup();
        group.add(this.rdMotChieu);
        group.add(this.rdKhuHoi);
        this.rdMotChieu.setSelected(true);

        JButton btnLuu = new JButton("Lưu");
        JButton btnReset = new JButton("Reset");


        // ===== DÒNG 2 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Chuyến bay:"), gbc);
        gbc.gridx=1; panel.add(this.txtMaChuyenBay, gbc);
        gbc.gridx=2; panel.add(btnChonCB, gbc);

        gbc.gridx=3; panel.add(new JLabel("Ghế:"), gbc);
        gbc.gridx=4; panel.add(this.txtGhe, gbc);
        gbc.gridx=5; panel.add(btnChonGhe, gbc);
        row++;

        // ===== DÒNG 3 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Loại vé:"), gbc);

        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        pLoai.add(this.rdMotChieu);
        pLoai.add(this.rdKhuHoi);

        gbc.gridx=1; panel.add(pLoai, gbc);

        gbc.gridx=3; panel.add(new JLabel("Hạng vé:"), gbc);
        gbc.gridx=4; panel.add(this.cboHangVe, gbc);
        row++;

        // ===== DÒNG 4 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Người lớn:"), gbc);
        gbc.gridx=1; panel.add(this.spNguoiLon, gbc);

        gbc.gridx=3; panel.add(new JLabel("Trẻ em:"), gbc);
        gbc.gridx=4; panel.add(this.spTreEm, gbc);
        row++;

        // ===== DÒNG 5 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Em bé:"), gbc);
        gbc.gridx=1; panel.add(this.spEmBe, gbc);
        row++;

        // ===== DÒNG 6 =====
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("Tổng tiền:"), gbc);

        this.txtTongTien = new JTextField(15);
        this.txtTongTien.setEditable(false);

        gbc.gridx=1;
        panel.add(this.txtTongTien, gbc);

        row++;

        // ===== DÒNG 7 - KHUYẾN MÃI =====
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Khuyến mãi:"), gbc);

        this.cboKhuyenMai = new JComboBox<>();

        gbc.gridx = 1;
        panel.add(this.cboKhuyenMai, gbc);

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
                chonChuyenBay(this.txtMaChuyenBay, this.cboHangVe, this.spNguoiLon, this.spTreEm, this.spEmBe)
        );

        this.cboKhuyenMai.addActionListener(e -> tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));

        // Chọn ghế (demo)
        btnChonGhe.addActionListener(e -> {

            int tongHK =
                    (int) this.spNguoiLon.getValue() +
                            (int) this.spTreEm.getValue() +
                            (int) this.spEmBe.getValue();

            String maCB = this.txtMaChuyenBay.getText().trim();


            if(maCB.isEmpty()){
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn chuyến bay trước khi chọn ghế!");
                return;
            }

            String maMayBay = layMaMayBay(this.txtMaChuyenBay.getText());
            this.giaGhe = BigDecimal.ZERO;
            SoDoGhePanel Panel = new SoDoGhePanel(maMayBay, "Máy bay", tongHK);
            List<String> ds = new ArrayList<>();
            String ghe = this.txtGhe.getText().trim();
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

                @Override
                public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {

                    List<String> dsGhe = new ArrayList<>();
                    giaGhe = BigDecimal.ZERO;

                    for(GheMayBay ghe : selectedSeats){
                        String maGhe = chuanHoaGhe(ghe.getSoGhe());
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
                String maCB = this.txtMaChuyenBay.getText().trim();
                String ghe = this.txtGhe.getText().trim();
                String[] dsGhe = ghe.split(",");

                if (maHK.isEmpty() || maCB.isEmpty() || ghe.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Thiếu thông tin!");
                    return;
                }

                String maHangVe = convertHangVe(this.cboHangVe.getSelectedItem().toString());
                String loaiVe = this.rdMotChieu.isSelected() ? "Một chiều" : "Khứ hồi";

                int soNguoiLon = (int) this.spNguoiLon.getValue();
                int soTreEm = (int) this.spTreEm.getValue();
                int soEmBe = (int) this.spEmBe.getValue();

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
                    System.out.println("Bắt đầu transaction đặt vé...");  // THÊM log

                    PhieuDatVe phieu = new PhieuDatVe();
                    phieu.setNgayDat(LocalDate.now());
                    phieu.setSoLuongVe(tongSoVe);
                    phieu.setTongTien(tongTien);
                    phieu.setTrangThaiThanhToan("Chưa thanh toán");

                    String maPDV = pdv.insert(phieu, conn);
                    if(maPDV == null){
                        throw new RuntimeException("Không tạo được phiếu đặt vé!");
                    }
                    System.out.println("Tạo phiếu đặt vé: " + maPDV);  // THÊM log
                    int index = 0;
                    String maMB = cbDAO.layMaMayBay(conn, maCB);
                    System.out.println("Mã máy bay: " + maMB);  // THÊM log

                    for(int i=0;i<soNguoiLon;i++){
                        String soGhe = chuanHoaGhe(dsGhe[index++]);
                        String maGhe = gmb.timMaGhe(conn, soGhe, maMB);
                        if(maGhe == null){
                            throw new RuntimeException("Không tìm thấy ghế " + soGhe);
                        }
                        System.out.println("Tạo vé người lớn cho ghế: " + maGhe);  // THÊM log
                        taoVe(conn, maPDV, maHK, maCB, maGhe, maHangVe, "Người lớn", loaiVe);

                        // Khang Thêm: Cập nhật trạng thái ghế đã đặt
                        if(!veBanDAO.updateGheTrangThai(conn, maGhe, TrangThaiGhe.DA_DAT)){
                            throw new RuntimeException("Không cập nhật được trạng thái ghế " + soGhe);
                        }
                        System.out.println("Cập nhật ghế " + maGhe + " thành DA_DAT");  // THÊM log
                    }

                    for(int i=0;i<soTreEm;i++){
                        String soGhe = chuanHoaGhe(dsGhe[index++]);
                        String maGhe = gmb.timMaGhe(conn, soGhe, maMB);
                        if(maGhe == null){
                            throw new RuntimeException("Không tìm thấy ghế " + soGhe);
                        }
                        System.out.println("Tạo vé trẻ em cho ghế: " + maGhe);  // THÊM log
                        taoVe(conn, maPDV, maHK, maCB, maGhe, maHangVe, "Trẻ em", loaiVe);
                        // THÊM: Update ghế sang DA_DAT dùng enum
                        if (!veBanDAO.updateGheTrangThai(conn, maGhe, TrangThaiGhe.DA_DAT)) {
                            throw new RuntimeException("Không thể cập nhật trạng thái ghế " + soGhe);
                        }
                        System.out.println("Cập nhật ghế " + maGhe + " thành DA_DAT");
                    }

                    for(int i=0;i<soEmBe;i++){
                        String soGhe = chuanHoaGhe(dsGhe[index++]);
                        String maGhe = gmb.timMaGhe(conn, soGhe, maMB);
                        if(maGhe == null){
                            throw new RuntimeException("Không tìm thấy ghế " + soGhe);
                        }
                        System.out.println("Tạo vé em bé cho ghế: " + maGhe);
                        taoVe(conn, maPDV, maHK, maCB, maGhe, maHangVe, "Em bé", loaiVe);
                        // THÊM: Update ghế sang DA_DAT dùng enum
                        if (!veBanDAO.updateGheTrangThai(conn, maGhe, TrangThaiGhe.DA_DAT)) {
                            throw new RuntimeException("Không thể cập nhật trạng thái ghế " + soGhe);
                        }
                        System.out.println("Cập nhật ghế " + maGhe + " thành DA_DAT");
                    }
                    // cập nhật số lượng khuyến mãi đã dùng
                    KhuyenMai km = (KhuyenMai) this.cboKhuyenMai.getSelectedItem();

                    if(km != null){
                        kmDAO.incrementSoLuongDaDung(conn, km.getMaKhuyenMai());
                        System.out.println("Cập nhật khuyến mãi: " + km.getMaKhuyenMai() + " đã dùng " + km.getSoLuongDaDung() + "/" + km.getSoLuongTong());
                    }

                    conn.commit();
                    System.out.println("Commit transaction thành công!");  // THÊM log

                    JOptionPane.showMessageDialog(this,"Tạo vé thành công!");

                    loadTable();

                }catch(Exception ex){
                    ex.printStackTrace();  // THÊM print stack trace để xem lỗi chi tiết trong console
                    if (conn != null) {
                        try{
                            conn.rollback();
                            System.out.println("Rollback transaction do lỗi: " + ex.getMessage());  // THÊM log
                        }catch(SQLException ez){
                            ez.printStackTrace();
                        }
                    }
                    JOptionPane.showMessageDialog(this,"Lỗi khi lưu vé: " + ex.getMessage());
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                            System.out.println("Đóng connection thành công.");  // THÊM log
                        } catch (SQLException ec) {
                            ec.printStackTrace();
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi tạo vé: " + ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            this.txtMaChuyenBay.setText("");
            this.txtGhe.setText("");
            this.spNguoiLon.setValue(1);
            this.spTreEm.setValue(0);
            this.spEmBe.setValue(0);
            this.txtTongTien.setText("");
            this.giaGhe = BigDecimal.ZERO;
            this.cboKhuyenMai.setSelectedIndex(0);
            this.giamGia = BigDecimal.ZERO;
        });

        this.spNguoiLon.addChangeListener(e ->
                tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));

        this.spTreEm.addChangeListener(e ->
                tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));

        this.spEmBe.addChangeListener(e ->
                tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));

        this.cboHangVe.addActionListener(e ->
                tinhTongTien(this.txtMaChuyenBay,this.cboHangVe,this.spNguoiLon,this.spTreEm,this.spEmBe));

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
        String keyword = this.txtSearch.getText().trim();
        String trangThai = this.cboTrangThai.getSelectedItem().toString().trim();

        if (trangThai.equals("Tất cả")) {
            trangThai = null;
        }

        this.tableModel.setRowCount(0);

        List<VeBan> list = veBanDAO.searchByMaHK(maHK, keyword, trangThai);

        for (VeBan v : list) {
            this.tableModel.addRow(new Object[]{
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
        this.tableModel.setRowCount(0);
        List<VeBan> list = veBanDAO.selectByMaHK(maHK);

        for (VeBan v : list) {
            this.tableModel.addRow(new Object[]{
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

            if(cboHangVe == null || cboHangVe.getSelectedItem() == null) return;  // Thêm check null để an toàn

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
            if(this.rdKhuHoi.isSelected()){
                tongTien = tongTien.multiply(new BigDecimal("1.25"));
            }

            KhuyenMai km = (KhuyenMai) this.cboKhuyenMai.getSelectedItem();

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
            this.txtTongTien.setText(vn.format(tongTien) + " VND");

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

            this.cboKhuyenMai.addItem(null);

            for(KhuyenMai km : list){
                this.cboKhuyenMai.addItem(km);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}