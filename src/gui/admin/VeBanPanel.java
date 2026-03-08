package gui.admin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

import dal.ChuyenBayDAO;
import dal.GheMayBayDAO;
import dal.PhieuDatVeDAO;
import dal.VeBanDAO;
import db.DBConnection;
import model.GheMayBay;
import model.PhieuDatVe;
import model.VeBan;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class VeBanPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final VeBanDAO veBanDAO = new VeBanDAO();
    private JTextField txtSearch;
    private JComboBox<String> cboTrangThai;
    private JRadioButton rdMotChieu;
    private JRadioButton rdKhuHoi;
    private PhieuDatVeDAO pdv = new PhieuDatVeDAO();
    private JTextField txtTongTien;
    private BigDecimal giaGhe = BigDecimal.ZERO;
    private GheMayBayDAO gmb = new GheMayBayDAO();

    public VeBanPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        add(initHeader(), BorderLayout.NORTH);
        add(initTabs(), BorderLayout.CENTER);
        loadTable();
    }

    private JPanel initHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("QUẢN LÝ VÉ BÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(220, 38, 38));

        panel.add(title, BorderLayout.NORTH);
        panel.add(initSearchBar(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel initSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(25);
        panel.add(txtSearch);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(52, 73, 140));
        btnSearch.setForeground(Color.WHITE);
        panel.add(btnSearch);

        panel.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy"});
        panel.add(cboTrangThai);

        btnSearch.addActionListener(e -> searchVe());
        return panel;
    }

    private JTabbedPane initTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("Danh sách vé", initTablePanel());
        tabs.addTab("Tạo vé máy bay", initFormPanel());
        return tabs;
    }

    private JPanel initTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã vé", "Mã PNR", "Chuyến bay", "Loại HK", "Loại vé", "Giá vé", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        
        // Cập nhật màu chọn dòng xanh đậm và chữ trắng
        table.setSelectionBackground(new Color(28, 48, 96));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        // Ép thanh cuộn luôn hiển thị
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel initFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(null, "FORM TẠO VÉ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtMaChuyenBay = new JTextField(15);
        txtMaChuyenBay.setEditable(false);
        JTextField txtGhe = new JTextField(15);
        txtGhe.setEditable(false);

        JButton btnChonCB = new JButton("Chọn");
        JButton btnChonGhe = new JButton("Chọn");

        JComboBox<String> cboHangVe = new JComboBox<>(new String[]{"Thương gia", "Phổ thông", "Hạng nhất", "Phổ thông đặc biệt"});
        JSpinner spNguoiLon = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JSpinner spTreEm = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner spEmBe = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        rdMotChieu = new JRadioButton("Một chiều", true);
        rdKhuHoi = new JRadioButton("Khứ hồi");
        ButtonGroup group = new ButtonGroup();
        group.add(rdMotChieu); group.add(rdKhuHoi);

        txtTongTien = new JTextField(15);
        txtTongTien.setEditable(false);

        // Bố trí GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Chuyến bay:"), gbc);
        gbc.gridx = 1; panel.add(txtMaChuyenBay, gbc);
        gbc.gridx = 2; panel.add(btnChonCB, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Ghế:"), gbc);
        gbc.gridx = 1; panel.add(txtGhe, gbc);
        gbc.gridx = 2; panel.add(btnChonGhe, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Loại vé:"), gbc);
        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pLoai.setBackground(Color.WHITE); pLoai.add(rdMotChieu); pLoai.add(rdKhuHoi);
        gbc.gridx = 1; panel.add(pLoai, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1; panel.add(txtTongTien, gbc);

        JButton btnLuu = new JButton("Lưu vé");
        btnLuu.setBackground(new Color(34, 197, 94));
        btnLuu.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(btnLuu, gbc);

        // LOGIC SỰ KIỆN
        // Đã sửa: Truyền txtGhe vào để reset khi chọn chuyến bay mới
        btnChonCB.addActionListener(e -> chonChuyenBay(txtMaChuyenBay, txtGhe, cboHangVe, spNguoiLon, spTreEm, spEmBe));

        btnChonGhe.addActionListener(e -> {
            int tongHK = (int) spNguoiLon.getValue() + (int) spTreEm.getValue() + (int) spEmBe.getValue();
            String maCB = txtMaChuyenBay.getText();
            if (maCB.isEmpty()) { JOptionPane.showMessageDialog(this, "Chọn chuyến bay trước!"); return; }

            String maMB = layMaMayBay(maCB);
            SoDoGhePanel soDo = new SoDoGhePanel(maMB, "Máy bay", tongHK);
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn ghế", true);
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(this);

            // Đã sửa: Sử dụng onSeatsConfirmed thay vì onSeatSelected
            soDo.setListener(new SoDoGhePanel.SoDoGheListener() {
                @Override
                public void onBack() { dialog.dispose(); }

                @Override
                public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                    List<String> tenGheList = new ArrayList<>();
                    giaGhe = BigDecimal.ZERO;
                    for (GheMayBay g : selectedSeats) {
                        tenGheList.add(chuanHoaGhe(g.getSoGhe()));
                        giaGhe = giaGhe.add(g.getGiaGhe());
                    }
                    txtGhe.setText(String.join(", ", tenGheList));
                    tinhTongTien(txtMaChuyenBay, cboHangVe, spNguoiLon, spTreEm, spEmBe);
                    dialog.dispose();
                }
            });

            dialog.add(soDo);
            dialog.setVisible(true);
        });

        return panel;
    }

    private void chonChuyenBay(JTextField txtMaCB, JTextField txtGhe, JComboBox<String> cboHangVe, JSpinner spNguoiLon, JSpinner spTreEm, JSpinner spEmBe) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn chuyến bay", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Mã CB", "Sân bay đi", "Sân bay đến", "Ngày đi"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable tb = new JTable(model);
        
        // Đồng bộ màu chọn cho bảng phụ
        tb.setSelectionBackground(new Color(28, 48, 96));
        tb.setSelectionForeground(Color.WHITE);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT maChuyenBay, maTuyenBay, ngayGioDi FROM ChuyenBay WHERE trangThai = N'CHUA_KHOI_HANH'";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), "SB01", "SB02", rs.getTimestamp(3)});
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        tb.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtMaCB.setText(tb.getValueAt(tb.getSelectedRow(), 0).toString());
                    // Reset ghế khi đổi chuyến bay để tránh lỗi lệch dữ liệu
                    txtGhe.setText("");
                    giaGhe = BigDecimal.ZERO;
                    dialog.dispose();
                }
            }
        });

        dialog.add(new JScrollPane(tb));
        dialog.setVisible(true);
    }

    private void tinhTongTien(JTextField txtMaCB, JComboBox<String> cboHangVe, JSpinner spNL, JSpinner spTE, JSpinner spEB) {
        try {
            String maCB = txtMaCB.getText();
            if (maCB.isEmpty()) return;
            
            BigDecimal tong = giaGhe.add(new BigDecimal("1000000")); // Giá giả định
            if (rdKhuHoi.isSelected()) tong = tong.multiply(new BigDecimal("1.25"));

            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTongTien.setText(vn.format(tong) + " VND");
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<VeBan> list = veBanDAO.selectAll();
        for (VeBan v : list) {
            tableModel.addRow(new Object[]{v.getMaVe(), v.getMaPhieuDatVe(), v.getMaChuyenBay(), v.getLoaiHK(), v.getLoaiVe(), v.getGiaVe(), v.getTrangThaiVe()});
        }
    }

    private void searchVe() { /* Logic tìm kiếm */ }
    private String layMaMayBay(String maCB) { return "MB001"; } // Demo
    private String chuanHoaGhe(String ghe) { return ghe.trim().toUpperCase(); }
}