package gui.admin;

import bll.HoaDonBUS;
import model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HoaDonPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHoaDon, txtMaPhieuDatVe, txtMaNV, txtTongTien, txtThue, txtTimKiem;
    private JSpinner spinnerNgayLap;
    private JComboBox<String> cboPhuongThuc, cboDonViTienTe, cboHienThi, cboTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem; 

    private HoaDonBUS hoaDonBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HoaDonPanel() {
        hoaDonBUS = new HoaDonBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataToTable(hoaDonBUS.docDanhSachHoaDon());
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ HÓA ĐƠN", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);
        try {
            ImageIcon titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png"))
                    .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            lblTitle.setIcon(titleIcon);
        } catch (Exception e) {}

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        txtTimKiem = createTextField();
        txtTimKiem.setPreferredSize(new Dimension(200, 35));

        btnTimKiem = createRoundedButton("Tìm kiếm", TABLE_HEADER, "/resources/icons/icons8-search-24.png", 16);
        btnTimKiem.setPreferredSize(new Dimension(130, 35));

        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        cboHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboHienThi.setPreferredSize(new Dimension(150, 35));

        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Chế độ:"));
        searchPanel.add(cboHienThi);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Mã Hóa Đơn", "Phiếu Đặt", "Mã NV", "Ngày Lập", "Tổng Tiền", "Phương Thức", "Tiền Tệ", "Thuế", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(20, 20));

        JPanel formPanel = new JPanel(new GridLayout(5, 4, 15, 15));
        formPanel.setOpaque(false);

        txtMaHoaDon = createTextField();
        txtMaPhieuDatVe = createTextField();
        txtMaNV = createTextField();
        txtTongTien = createTextField();
        txtThue = createTextField();
        txtThue.setEditable(false); // Khóa ô Thuế, không cho người dùng tự gõ tay
        txtThue.setBackground(new Color(230, 235, 240)); // Đổi màu xám nhẹ để báo hiệu đây là ô tự động
        txtThue.setToolTipText("Thuế VAT được tự động tính = 10% Tổng tiền");

        // Lắng nghe sự kiện mỗi khi người dùng gõ phím vào ô Tổng Tiền
        txtTongTien.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    String input = txtTongTien.getText().trim().replace(",", "");
                    if (!input.isEmpty()) {
                        BigDecimal tongTien = new BigDecimal(input);
                        // Tính thuế VAT 10%
                        BigDecimal vat = tongTien.multiply(new BigDecimal("0.10")); 
                        // Hiển thị ra ô Thuế
                        txtThue.setText(String.format("%.0f", vat));
                    } else {
                        txtThue.setText("0");
                    }
                } catch (NumberFormatException ex) {
                    txtThue.setText("0");
                }
            }
        });
        spinnerNgayLap = createDateTimeSpinner();
        
        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử"});
        cboPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        cboDonViTienTe = new JComboBox<>(new String[]{"VND", "USD"});
        cboDonViTienTe.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboTrangThai = new JComboBox<>(new String[]{"Chưa thanh toán", "Đã thanh toán", "Đã hủy"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        formPanel.add(createLabel("Mã Hóa Đơn (*):"));
        formPanel.add(txtMaHoaDon);
        formPanel.add(createLabel("Ngày Lập:"));
        formPanel.add(spinnerNgayLap);

        formPanel.add(createLabel("Mã Phiếu Đặt (*):"));
        formPanel.add(txtMaPhieuDatVe);
        formPanel.add(createLabel("Tổng Tiền:"));
        formPanel.add(txtTongTien);

        formPanel.add(createLabel("Mã Nhân Viên:"));
        formPanel.add(txtMaNV);
        formPanel.add(createLabel("Thuế:"));
        formPanel.add(txtThue);

        formPanel.add(createLabel("Phương Thức TT:"));
        formPanel.add(cboPhuongThuc);
        formPanel.add(createLabel("Đơn Vị Tiền Tệ:"));
        formPanel.add(cboDonViTienTe);

        formPanel.add(createLabel("Trạng Thái:"));
        formPanel.add(cboTrangThai);

        formPanel.add(new JLabel("")); 
        formPanel.add(new JLabel(""));

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(formPanel, BorderLayout.NORTH);

        formCard.add(formWrapper, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnThem = createRoundedButton("Thêm", BTN_ADD, "/resources/icons/icons8-add-24.png", 20);
        btnSua = createRoundedButton("Cập nhật", BTN_UPDATE, "/resources/icons/icons8-update-24.png", 20);
        btnXoa = createRoundedButton("Xóa", BTN_DELETE, "/resources/icons/icons8-delete-24.png", 20);
        btnLamMoi = createRoundedButton("Làm mới", BTN_REFRESH, "/resources/icons/icons8-erase-24.png", 20);
        

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        formCard.add(buttonPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.SOUTH);

        setupListeners();
    }

    private JButton createRoundedButton(String text, Color bgColor, String iconPath, int iconSize) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g); 
            }
        };
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image scaled = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaled));
                btn.setIconTextGap(8);
            } catch (Exception e) {}
        }
        return btn;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(250, 35));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JSpinner createDateTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(250, 35));
        return spinner;
    }

    private void styleTable() {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(45, 72, 140));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setFocusable(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(TABLE_HEADER); 
                setForeground(Color.WHITE);  
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(60, 70, 90))); 
                return this;
            }
        };

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                if (value != null) {
                    String status = value.toString();
                    if (status.equalsIgnoreCase("Đã thanh toán")) {
                        setForeground(new Color(34, 197, 94)); // Màu Xanh lá
                    } else if (status.equalsIgnoreCase("Chưa thanh toán")) {
                        setForeground(new Color(239, 68, 68)); // Màu Đỏ
                    } else {
                        setForeground(new Color(156, 163, 175)); // Màu Xám
                    }
                }
            
                if (isSelected) {
                    setForeground(Color.WHITE);
                }
                return c;
            }
        };

        for(int i = 0; i < table.getColumnCount(); i++){
            if (i == 8) {
                table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer); // Áp dụng tô màu cho cột số 8 (Trạng thái)
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void loadDataToTable(List<HoaDon> list) {
        tableModel.setRowCount(0);
        if (list == null) return;
        for (HoaDon hd : list) {
            tableModel.addRow(new Object[]{
                    hd.getMaHoaDon(),
                    hd.getMaPhieuDatVe(),
                    hd.getMaNV(),
                    hd.getNgayLap() != null ? hd.getNgayLap().format(formatter) : "",
                    String.format("%,.0f", hd.getTongTien()),
                    hd.getPhuongThuc(),
                    hd.getDonViTienTe(),
                    String.format("%,.0f", hd.getThue()),
                    hd.getTrangThai() != null ? hd.getTrangThai() : "Chưa thanh toán" // Load dữ liệu trạng thái
            });
        }
    }

    private void setupListeners() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaHoaDon.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaHoaDon.setEditable(false);
                    txtMaPhieuDatVe.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                    txtMaNV.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                    
                    try {
                        String dateStr = tableModel.getValueAt(row, 3).toString();
                        if (!dateStr.isEmpty()) {
                            LocalDateTime ldt = LocalDateTime.parse(dateStr, formatter);
                            spinnerNgayLap.setValue(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
                        }
                    } catch (Exception ex) {}

                    String tongTienStr = tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString().replace(",", "") : "0";
                    txtTongTien.setText(tongTienStr);
                    
                    cboPhuongThuc.setSelectedItem(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "Tiền mặt");
                    cboDonViTienTe.setSelectedItem(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "VND");
                    
                    String thueStr = tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString().replace(",", "") : "0";
                    txtThue.setText(thueStr);

                    // Set lại dữ liệu trạng thái khi click vào bảng
                    cboTrangThai.setSelectedItem(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "Chưa thanh toán");
                }
            }
        });

        cboHienThi.addActionListener(e -> {
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            if (isTrash) {
                btnThem.setEnabled(false);
                btnSua.setEnabled(false);
                btnXoa.setEnabled(false);
                tableModel.setRowCount(0); 
            } else {
                btnThem.setEnabled(true);
                btnSua.setEnabled(true);
                btnXoa.setEnabled(true);
                loadDataToTable(hoaDonBUS.docDanhSachHoaDon());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                HoaDon hd = getFormInput();
                String result = hoaDonBUS.themHoaDon(hd);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                if (txtMaHoaDon.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn trên bảng!");
                    return;
                }
                HoaDon hd = getFormInput();
                String result = hoaDonBUS.capNhatHoaDon(hd);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!");
                return;
            }
            String maHD = txtMaHoaDon.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hóa đơn " + maHD + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = hoaDonBUS.xoaHoaDon(maHD);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaHoaDon.setText("");
            txtMaPhieuDatVe.setText("");
            txtMaNV.setText("");
            txtTongTien.setText("");
            txtThue.setText("");
            txtTimKiem.setText("");
            
            spinnerNgayLap.setValue(new Date());
            cboPhuongThuc.setSelectedIndex(0);
            cboDonViTienTe.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0); 
            
            txtMaHoaDon.setEditable(true);
            table.clearSelection();

            if (cboHienThi.getSelectedIndex() == 0) {
                loadDataToTable(hoaDonBUS.docDanhSachHoaDon());
            } else {
                tableModel.setRowCount(0);
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            List<HoaDon> allData = hoaDonBUS.docDanhSachHoaDon();
            
            List<HoaDon> ketQua = allData.stream().filter(hd -> 
                hd.getMaHoaDon().toLowerCase().contains(keyword) || 
                hd.getMaPhieuDatVe().toLowerCase().contains(keyword) ||
                (hd.getMaNV() != null && hd.getMaNV().toLowerCase().contains(keyword))
            ).collect(Collectors.toList());
            
            loadDataToTable(ketQua);
        });
    }

    private HoaDon getFormInput() throws Exception {
        String maHD = txtMaHoaDon.getText().trim();
        if (maHD.isEmpty()) throw new Exception("Mã hóa đơn không được để trống!");

        String maPhieu = txtMaPhieuDatVe.getText().trim();
        if (maPhieu.isEmpty()) throw new Exception("Mã phiếu đặt vé không được để trống!");

        String maNV = txtMaNV.getText().trim();

        Date dateLap = (Date) spinnerNgayLap.getValue();
        LocalDateTime ngayLap = dateLap.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        BigDecimal tongTien = BigDecimal.ZERO;
        try {
            tongTien = new BigDecimal(txtTongTien.getText().trim().isEmpty() ? "0" : txtTongTien.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Tổng tiền phải là số hợp lệ!");
        }
        
        BigDecimal thue = BigDecimal.ZERO;
        try {
            thue = new BigDecimal(txtThue.getText().trim().isEmpty() ? "0" : txtThue.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Thuế phải là số hợp lệ!");
        }

        String phuongThuc = cboPhuongThuc.getSelectedItem().toString();
        String donViTienTe = cboDonViTienTe.getSelectedItem().toString();

        String trangThai = cboTrangThai.getSelectedItem().toString();

        return new HoaDon(maHD, maPhieu, maNV, ngayLap, tongTien, phuongThuc, donViTienTe, thue, trangThai);
    }
    
}