package gui.admin;

import bll.KhuyenMaiBUS;
import model.KhuyenMai;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class QuanLyKhuyenMaiPanel extends JPanel {

    // ===== BUSINESS LOGIC =====
    private KhuyenMaiBUS khuyenMaiBUS;

    // ===== UI COMPONENTS =====
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem, txtMaKM, txtTenKM, txtMoTa, txtGiaTri, txtDonHangToiThieu,
            txtSoLuongTong, txtSoLuongDaDung, txtGioiHanMoiKhach, txtLoaiKhachApDung;
    private JComboBox<String> cboLoaiKM, cboTrangThai, cboApDungChoTatCa, cboLocTrangThai;
    private JSpinner spinnerNgayBD, spinnerNgayKT;
    private JButton btnThem, btnLuu, btnXoa, btnLamMoi;

    // ===== COLORS =====
    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color SECONDARY_COLOR = new Color(45, 72, 140);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color DANGER_COLOR = new Color(244, 67, 54);
    private final Color TABLE_HEADER_BG = new Color(28, 48, 96);
    private final Color TABLE_ROW_EVEN = new Color(245, 247, 250);
    private final Color TABLE_ROW_ODD = Color.WHITE;

    public QuanLyKhuyenMaiPanel() {
        khuyenMaiBUS = new KhuyenMaiBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== HEADER =====
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ===== CENTER: TABLE =====
        add(createTablePanel(), BorderLayout.CENTER);

        // ===== SOUTH: FORM =====
        add(createFormPanel(), BorderLayout.SOUTH);
    }

    // ========================================
    // HEADER PANEL
    // ========================================
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("⚡ QUẢN LÝ VOUCHER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);

        // ===== SEARCH & FILTER PANEL =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton btnTimKiem = createStyledButton("🔍 Tìm kiếm", SECONDARY_COLOR);
        btnTimKiem.addActionListener(e -> timKiem());

        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Không hoạt động"});
        cboLocTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboLocTrangThai.addActionListener(e -> locTheoTrangThai());

        searchPanel.add(new JLabel("Tìm (Mã/Tên):"));
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(cboLocTrangThai);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    // ========================================
    // TABLE PANEL
    // ========================================
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // ===== TABLE TITLE =====
        JLabel lblTableTitle = new JLabel("📋 DANH SÁCH MÃ KHUYẾN MÃI");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(PRIMARY_COLOR);

        // ===== TABLE =====
        String[] columns = {"Mã KM", "Tên KM", "Loại", "Giá trị", "ĐH tối thiểu",
                "SL tổng", "Đã dùng", "Ngày BĐ", "Ngày KT", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);

        // ===== TABLE HEADER =====
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // ===== RENDERER (Zebra Striping) =====
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // ===== CLICK EVENT =====
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    hienThiChiTiet();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        tablePanel.add(lblTableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ========================================
    // FORM PANEL
    // ========================================
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // ===== FORM TITLE =====
        JLabel lblFormTitle = new JLabel("✏️ FORM TẠO / CHỈNH SỬA KHUYẾN MÃI");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(PRIMARY_COLOR);

        // ===== FORM FIELDS =====
        JPanel fieldsPanel = new JPanel(new GridLayout(7, 4, 15, 12));
        fieldsPanel.setBackground(Color.WHITE);

        txtMaKM = createTextField();
        txtTenKM = createTextField();
        txtMoTa = createTextField();
        txtGiaTri = createTextField();
        txtDonHangToiThieu = createTextField();
        txtSoLuongTong = createTextField();
        txtSoLuongDaDung = createTextField();
        txtSoLuongDaDung.setEditable(false);
        txtSoLuongDaDung.setBackground(new Color(240, 240, 240));
        txtGioiHanMoiKhach = createTextField();
        txtLoaiKhachApDung = createTextField();

        cboLoaiKM = new JComboBox<>(new String[]{"PHAN_TRAM", "TIEN"});
        cboLoaiKM.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboApDungChoTatCa = new JComboBox<>(new String[]{"Có", "Không"});
        cboApDungChoTatCa.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Không hoạt động"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Date Spinners
        spinnerNgayBD = createDateSpinner();
        spinnerNgayKT = createDateSpinner();

        // ===== ADD FIELDS =====
        fieldsPanel.add(createLabel("Mã KM:"));
        fieldsPanel.add(txtMaKM);
        fieldsPanel.add(createLabel("Tên KM:"));
        fieldsPanel.add(txtTenKM);

        fieldsPanel.add(createLabel("Mô tả:"));
        fieldsPanel.add(txtMoTa);
        fieldsPanel.add(createLabel("Loại KM:"));
        fieldsPanel.add(cboLoaiKM);

        fieldsPanel.add(createLabel("Giá trị:"));
        fieldsPanel.add(txtGiaTri);
        fieldsPanel.add(createLabel("ĐH tối thiểu:"));
        fieldsPanel.add(txtDonHangToiThieu);

        fieldsPanel.add(createLabel("SL tổng:"));
        fieldsPanel.add(txtSoLuongTong);
        fieldsPanel.add(createLabel("SL đã dùng:"));
        fieldsPanel.add(txtSoLuongDaDung);

        fieldsPanel.add(createLabel("Ngày BĐ:"));
        fieldsPanel.add(spinnerNgayBD);
        fieldsPanel.add(createLabel("Ngày KT:"));
        fieldsPanel.add(spinnerNgayKT);

        fieldsPanel.add(createLabel("Áp dụng tất cả:"));
        fieldsPanel.add(cboApDungChoTatCa);
        fieldsPanel.add(createLabel("Loại khách:"));
        fieldsPanel.add(txtLoaiKhachApDung);

        fieldsPanel.add(createLabel("Giới hạn/khách:"));
        fieldsPanel.add(txtGioiHanMoiKhach);
        fieldsPanel.add(createLabel("Trạng thái:"));
        fieldsPanel.add(cboTrangThai);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnThem = createStyledButton("➕ Thêm", SUCCESS_COLOR);
        btnLuu = createStyledButton("💾 Lưu", SECONDARY_COLOR);
        btnXoa = createStyledButton("🗑️ Xóa", DANGER_COLOR);
        btnLamMoi = createStyledButton("🔄 Làm mới", ACCENT_COLOR);

        btnThem.addActionListener(e -> themKhuyenMai());
        btnLuu.addActionListener(e -> luuKhuyenMai());
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        btnLamMoi.addActionListener(e -> lamMoi());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        formPanel.add(lblFormTitle, BorderLayout.NORTH);
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        return formPanel;
    }

    // ========================================
    // HELPER METHODS: UI CREATION
    // ========================================
    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(PRIMARY_COLOR);
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return spinner;
    }

    // ========================================
    // DATA LOADING
    // ========================================
    private void loadData() {
        tableModel.setRowCount(0);
        List<KhuyenMai> list = khuyenMaiBUS.getAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (KhuyenMai km : list) {
            tableModel.addRow(new Object[]{
                    km.getMaKhuyenMai(),
                    km.getTenKM(),
                    km.getLoaiKM(),
                    km.getGiaTri(),
                    km.getDonHangToiThieu(),
                    km.getSoLuongTong(),
                    km.getSoLuongDaDung(),
                    km.getNgayBD() != null ? km.getNgayBD().format(dtf) : "",
                    km.getNgayKT() != null ? km.getNgayKT().format(dtf) : "",
                    km.isTrangThai() ? "Hoạt động" : "Không hoạt động"
            });
        }
    }

    // ========================================
    // EVENT HANDLERS
    // ========================================
    private void hienThiChiTiet() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        String maKM = tableModel.getValueAt(selectedRow, 0).toString();
        KhuyenMai km = khuyenMaiBUS.getById(maKM);

        if (km != null) {
            txtMaKM.setText(km.getMaKhuyenMai());
            txtMaKM.setEditable(false);
            txtTenKM.setText(km.getTenKM());
            txtMoTa.setText(km.getMoTa());
            cboLoaiKM.setSelectedItem(km.getLoaiKM());
            txtGiaTri.setText(km.getGiaTri().toString());
            txtDonHangToiThieu.setText(km.getDonHangToiThieu().toString());
            txtSoLuongTong.setText(String.valueOf(km.getSoLuongTong()));
            txtSoLuongDaDung.setText(String.valueOf(km.getSoLuongDaDung()));
            txtGioiHanMoiKhach.setText(String.valueOf(km.getGioiHanMoiKhach()));
            txtLoaiKhachApDung.setText(km.getLoaiKhachApDung());

            if (km.getNgayBD() != null) {
                spinnerNgayBD.setValue(java.sql.Date.valueOf(km.getNgayBD()));
            }
            if (km.getNgayKT() != null) {
                spinnerNgayKT.setValue(java.sql.Date.valueOf(km.getNgayKT()));
            }

            cboApDungChoTatCa.setSelectedItem(km.isApDungChoTatCa() ? "Có" : "Không");
            cboTrangThai.setSelectedItem(km.isTrangThai() ? "Hoạt động" : "Không hoạt động");
        }
    }

    private void themKhuyenMai() {
        try {
            KhuyenMai km = layThongTinTuForm();
            km.setNgayTao(LocalDateTime.now());
            km.setNguoiTao("admin"); // Có thể lấy từ session
            if (khuyenMaiBUS.insert(km)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void luuKhuyenMai() {
        try {
            KhuyenMai km = layThongTinTuForm();

            dal.KhuyenMaiDAO dao = new dal.KhuyenMaiDAO();
            if (dao.update(km)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhuyenMai() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khuyến mãi này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String maKM = tableModel.getValueAt(selectedRow, 0).toString();
                KhuyenMai km = new KhuyenMai();
                km.setMaKhuyenMai(maKM);

                dal.KhuyenMaiDAO dao = new dal.KhuyenMaiDAO();
                if (dao.delete(km)) {
                    JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    lamMoi();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lamMoi() {
        txtMaKM.setText("");
        txtMaKM.setEditable(true);
        txtTenKM.setText("");
        txtMoTa.setText("");
        txtGiaTri.setText("");
        txtDonHangToiThieu.setText("");
        txtSoLuongTong.setText("");
        txtSoLuongDaDung.setText("0");
        txtGioiHanMoiKhach.setText("");
        txtLoaiKhachApDung.setText("");
        cboLoaiKM.setSelectedIndex(0);
        cboApDungChoTatCa.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        spinnerNgayBD.setValue(new java.util.Date());
        spinnerNgayKT.setValue(new java.util.Date());
        table.clearSelection();
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        List<KhuyenMai> list = khuyenMaiBUS.getAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (KhuyenMai km : list) {
            if (km.getMaKhuyenMai().toLowerCase().contains(keyword) ||
                    km.getTenKM().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{
                        km.getMaKhuyenMai(),
                        km.getTenKM(),
                        km.getLoaiKM(),
                        km.getGiaTri(),
                        km.getDonHangToiThieu(),
                        km.getSoLuongTong(),
                        km.getSoLuongDaDung(),
                        km.getNgayBD() != null ? km.getNgayBD().format(dtf) : "",
                        km.getNgayKT() != null ? km.getNgayKT().format(dtf) : "",
                        km.isTrangThai() ? "Hoạt động" : "Không hoạt động"
                });
            }
        }
    }

    private void locTheoTrangThai() {
        String selected = cboLocTrangThai.getSelectedItem().toString();
        if (selected.equals("Tất cả")) {
            loadData();
            return;
        }

        boolean trangThai = selected.equals("Hoạt động");
        tableModel.setRowCount(0);
        List<KhuyenMai> list = khuyenMaiBUS.getAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (KhuyenMai km : list) {
            if (km.isTrangThai() == trangThai) {
                tableModel.addRow(new Object[]{
                        km.getMaKhuyenMai(),
                        km.getTenKM(),
                        km.getLoaiKM(),
                        km.getGiaTri(),
                        km.getDonHangToiThieu(),
                        km.getSoLuongTong(),
                        km.getSoLuongDaDung(),
                        km.getNgayBD() != null ? km.getNgayBD().format(dtf) : "",
                        km.getNgayKT() != null ? km.getNgayKT().format(dtf) : "",
                        km.isTrangThai() ? "Hoạt động" : "Không hoạt động"
                });
            }
        }
    }

    private KhuyenMai layThongTinTuForm() {
        KhuyenMai km = new KhuyenMai();
        km.setMaKhuyenMai(txtMaKM.getText().trim());
        km.setTenKM(txtTenKM.getText().trim());
        km.setMoTa(txtMoTa.getText().trim());
        km.setLoaiKM(cboLoaiKM.getSelectedItem().toString());
        km.setGiaTri(new BigDecimal(txtGiaTri.getText().trim()));
        km.setDonHangToiThieu(new BigDecimal(txtDonHangToiThieu.getText().trim()));
        km.setSoLuongTong(Integer.parseInt(txtSoLuongTong.getText().trim()));
        km.setSoLuongDaDung(Integer.parseInt(txtSoLuongDaDung.getText().trim()));
        km.setGioiHanMoiKhach(Integer.parseInt(txtGioiHanMoiKhach.getText().trim()));

        km.setLoaiKhachApDung(txtLoaiKhachApDung.getText().trim());

        //chuyen doi Date tu JSpinner sang LocalDate
        java.util.Date dateNgayBD = (java.util.Date) spinnerNgayBD.getValue();
        km.setNgayBD(new java.sql.Date(dateNgayBD.getTime()).toLocalDate());

        java.util.Date dateNgayKT  =(java.util.Date) spinnerNgayKT.getValue();
        km.setNgayKT(new java.sql.Date(dateNgayKT.getTime()).toLocalDate());

        km.setApDungChoTatCa(cboApDungChoTatCa.getSelectedItem().toString().equals("Có"));
        km.setTrangThai(cboTrangThai.getSelectedItem().toString().equals("Hoạt động"));

        return km;
    }
}
