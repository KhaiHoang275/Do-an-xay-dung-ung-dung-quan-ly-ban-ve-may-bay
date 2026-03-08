package gui.user;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import dal.VeBanDAO;
import gui.admin.SoDoGhePanel;
import model.DatVeSession;
import model.GheMayBay;
import model.VeBan;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import db.DBConnection;

public class PanelUserVeBan extends JPanel {
    private String maHK;
    private String maChuyenBayDuocChon; // Nhận từ MainFrame
    
    private JTable table;
    private DefaultTableModel tableModel;
    private final VeBanDAO veBanDAO = new VeBanDAO();
    private JTextField txtSearch;
    private JComboBox<String> cboTrangThai;
    private JRadioButton rdMotChieu, rdKhuHoi;
    private JButton btnDoiVe;
    
    // Lưu tạm danh sách ghế khách chọn để truyền vào Session
    private List<GheMayBay> danhSachGheDaChon = new ArrayList<>();

    // CONSTRUCTOR MỚI: Nhận cả Mã Hành Khách và Mã Chuyến Bay
    public PanelUserVeBan(String maHK, String maChuyenBay) {
        this.maHK = maHK;
        this.maChuyenBayDuocChon = maChuyenBay;
        
        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));

        add(initHeader(), BorderLayout.NORTH);
        add(initTabs(), BorderLayout.CENTER);
        loadTable();
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

        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy"});
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
        // Tự động mở Tab Tạo vé vì người dùng đang ở luồng Đặt vé
        tabs.setSelectedIndex(1); 
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

        String[] cols = {"Mã vé", "Mã PNR", "Chuyến bay", "Loại HK", "Loại vé", "Giá vé", "Trạng thái"};
        tableModel = new DefaultTableModel(cols,0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1){ btnDoiVe.setEnabled(false); return; }
            String trangThai = table.getValueAt(row,6).toString();
            btnDoiVe.setEnabled(trangThai.equalsIgnoreCase("Đã hủy"));
        });
        
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(28, 48, 96));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20,40,90));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButton.setBackground(Color.WHITE);

        btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.setEnabled(false);
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

    // ================= FORM PANEL (ĐÃ LÀM SẠCH) =================
    private JPanel initFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        TitledBorder border = BorderFactory.createTitledBorder("THÔNG TIN ĐẶT CHỖ");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        border.setTitleColor(new Color(25,40,80)); 
        panel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,20,15,20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        JTextField txtMaChuyenBay = new JTextField(15);
        txtMaChuyenBay.setEditable(false);
        txtMaChuyenBay.setText(this.maChuyenBayDuocChon); // ĐIỀN SẴN MÃ CHUYẾN BAY

        JTextField txtGhe = new JTextField(15);
        txtGhe.setEditable(false);

        JButton btnChonGhe = new JButton("Chọn");

        JComboBox<String> cboHangVe = new JComboBox<>(new String[]{"Thương gia", "Phổ thông", "Hạng nhất", "Phổ thông đặc biệt"});

        JSpinner spNguoiLon = new JSpinner(new SpinnerNumberModel(1,1,10,1));
        JSpinner spTreEm = new JSpinner(new SpinnerNumberModel(0,0,10,1));
        JSpinner spEmBe = new JSpinner(new SpinnerNumberModel(0,0,10,1));
        ((JSpinner.DefaultEditor) spNguoiLon.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) spTreEm.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) spEmBe.getEditor()).getTextField().setEditable(false);

        rdMotChieu = new JRadioButton("Một chiều");
        rdKhuHoi = new JRadioButton("Khứ hồi");
        ButtonGroup group = new ButtonGroup();
        group.add(rdMotChieu);
        group.add(rdKhuHoi);
        rdMotChieu.setSelected(true);

        JButton btnTiepTuc = new JButton("Tiếp tục");
        JButton btnReset = new JButton("Làm mới");

        // ===== DÒNG 1 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Chuyến bay:"), gbc);
        gbc.gridx=1; panel.add(txtMaChuyenBay, gbc);

        gbc.gridx=3; panel.add(new JLabel("Ghế:"), gbc);
        gbc.gridx=4; panel.add(txtGhe, gbc);
        gbc.gridx=5; panel.add(btnChonGhe, gbc);
        row++;

        // ===== DÒNG 2 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Loại vé:"), gbc);
        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        pLoai.setBackground(Color.WHITE);
        pLoai.add(rdMotChieu); pLoai.add(rdKhuHoi);
        gbc.gridx=1; panel.add(pLoai, gbc);

        gbc.gridx=3; panel.add(new JLabel("Hạng vé:"), gbc);
        gbc.gridx=4; panel.add(cboHangVe, gbc);
        row++;

        // ===== DÒNG 3 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Người lớn:"), gbc);
        gbc.gridx=1; panel.add(spNguoiLon, gbc);

        gbc.gridx=3; panel.add(new JLabel("Trẻ em:"), gbc);
        gbc.gridx=4; panel.add(spTreEm, gbc);
        row++;

        // ===== DÒNG 4 =====
        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Em bé:"), gbc);
        gbc.gridx=1; panel.add(spEmBe, gbc);
        row++;

        // ===== NÚT BẤM =====
        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pButton.setBackground(Color.WHITE);
        btnTiepTuc.setPreferredSize(new Dimension(120, 40));
        btnTiepTuc.setBackground(new Color(34, 197, 94));
        btnTiepTuc.setForeground(Color.WHITE);
        btnTiepTuc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnReset.setPreferredSize(new Dimension(120, 40));
        btnReset.setBackground(new Color(168, 162, 158));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pButton.add(btnReset);
        pButton.add(btnTiepTuc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 6;
        panel.add(pButton, gbc);

        // ================= LOGIC =================

        // MỞ SƠ ĐỒ GHẾ
        btnChonGhe.addActionListener(e -> {
            int tongHK = (int) spNguoiLon.getValue() + (int) spTreEm.getValue() + (int) spEmBe.getValue();        
            String maCB = txtMaChuyenBay.getText().trim();
            if(maCB.isEmpty()){
                JOptionPane.showMessageDialog(this, "Chưa có thông tin chuyến bay!");
                return;
            }

            String maMayBay = layMaMayBay(maCB);
            SoDoGhePanel soDoPanel = new SoDoGhePanel(maMayBay, "Máy bay", tongHK);
            
            // Nạp lại ghế cũ
            List<String> ds = new ArrayList<>();
            String gheStr = txtGhe.getText().trim();
            if(!gheStr.isEmpty()){
                for(String g : gheStr.split(",")){
                    ds.add(chuanHoaGhe(g));
                }
            }
            soDoPanel.setSelectedSeats(ds);

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn ghế", true);
            dialog.setSize(900,600);
            dialog.setLocationRelativeTo(this);

            soDoPanel.setListener(new SoDoGhePanel.SoDoGheListener() {
                @Override
                public void onBack() { dialog.dispose(); }

                @Override
                public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                    danhSachGheDaChon.clear();
                    danhSachGheDaChon.addAll(selectedSeats); // Lưu vào list tạm
                    
                    List<String> tenGheMoi = new ArrayList<>();
                    for(GheMayBay g : selectedSeats) {
                        tenGheMoi.add(chuanHoaGhe(g.getSoGhe()));
                    }
                    txtGhe.setText(String.join(",", tenGheMoi));
                    dialog.dispose();
                }
            });
            dialog.add(soDoPanel);
            dialog.setVisible(true);
        });

        // TIẾP TỤC ĐẾN BƯỚC NHẬP HÀNH KHÁCH
        btnTiepTuc.addActionListener(e -> {
            String maCB = txtMaChuyenBay.getText().trim();
            String ghe = txtGhe.getText().trim();
            String[] dsGhe = ghe.isEmpty() ? new String[0] : ghe.split(",");

            int soNguoiLon = (int) spNguoiLon.getValue();
            int soTreEm = (int) spTreEm.getValue();
            int soEmBe = (int) spEmBe.getValue();
            int tongSoVe = soNguoiLon + soTreEm + soEmBe;

            if (maCB.isEmpty() || ghe.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế trước khi tiếp tục!");
                return;
            }
            if(dsGhe.length != tongSoVe){
                JOptionPane.showMessageDialog(this, "Bạn cần chọn đủ " + tongSoVe + " ghế cho " + tongSoVe + " hành khách!");
                return;
            }

            // GÓI DỮ LIỆU VÀO GIỎ HÀNG
            DatVeSession session = new DatVeSession();
            session.maNguoiDung = this.maHK;
            session.maChuyenBay = maCB;
            session.loaiVe = rdMotChieu.isSelected() ? "Một chiều" : "Khứ hồi";
            session.maHangVe = convertHangVe(cboHangVe.getSelectedItem().toString());
            session.soNguoiLon = soNguoiLon;
            session.soTreEm = soTreEm;
            session.soEmBe = soEmBe;
            session.danhSachGhe.addAll(this.danhSachGheDaChon);

            // CHUYỂN TRANG MƯỢT MÀ
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new NhapHanhKhachGUI(session));
            frame.revalidate();
            frame.repaint();
        });

        btnReset.addActionListener(e -> {
            txtGhe.setText("");
            spNguoiLon.setValue(1);
            spTreEm.setValue(0);
            spEmBe.setValue(0);
            danhSachGheDaChon.clear();
        });

        return panel;
    }

    private void searchVe() {
        String keyword = txtSearch.getText().trim();
        String trangThai = cboTrangThai.getSelectedItem().toString().trim();
        if (trangThai.equals("Tất cả")) trangThai = null;

        tableModel.setRowCount(0);
        List<VeBan> list = veBanDAO.searchByMaHK(maHK, keyword, trangThai);
        for (VeBan v : list) {
            tableModel.addRow(new Object[]{ v.getMaVe(), v.getMaPhieuDatVe(), v.getMaChuyenBay(), v.getLoaiHK(), v.getLoaiVe(), v.getGiaVe(), v.getTrangThaiVe() });
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<VeBan> list = veBanDAO.selectByMaHK(maHK);
        for (VeBan v : list) {
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

    private String layMaMayBay(String maCB){
        try(Connection conn = DBConnection.getConnection()){
            String sql = "SELECT maMayBay FROM ChuyenBay WHERE maChuyenBay = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,maCB);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){ return rs.getString("maMayBay"); }
        }catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    private String chuanHoaGhe(String ghe){
        ghe = ghe.trim().toUpperCase();
        if(Character.isLetter(ghe.charAt(0))){ return ghe.substring(1) + ghe.charAt(0); }
        return ghe;
    }
}