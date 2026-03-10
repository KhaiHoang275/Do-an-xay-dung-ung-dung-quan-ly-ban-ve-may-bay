package gui.admin;

import bll.HanhLyBUS;
import model.HanhLy;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class HanhLyPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHanhLy, txtMaVe, txtSoKg, txtKichThuoc, txtGiaTien, txtGhiChu, txtTimKiem;
    private JComboBox<String> cbTrangThai, cboHienThi;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private HanhLyBUS hanhLyBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public HanhLyPanel() {
        hanhLyBUS = new HanhLyBUS();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataToTable(hanhLyBUS.docDanhSachHanhLy());
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        ImageIcon titleIcon = null;
        try {
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ HÀNH LÝ", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);

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

        String[] columns = {"Mã Hành Lý", "Mã Vé", "Số Kg", "Kích Thước", "Giá Tiền (VNĐ)", "Trạng Thái", "Ghi Chú"};
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

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 15, 15));
        formPanel.setOpaque(false);

        txtMaHanhLy = createTextField();
        txtMaVe = createTextField();
        txtSoKg = createTextField();
        txtKichThuoc = createTextField();
        txtGiaTien = createTextField();
        txtGhiChu = createTextField();
        
        cbTrangThai = new JComboBox<>(new String[]{"Chưa Check-in", "Đã Check-in", "Đã Check-out", "Hủy"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        formPanel.add(createLabel("Mã Hành Lý:"));
        formPanel.add(txtMaHanhLy);
        formPanel.add(createLabel("Giá Tiền (VNĐ):"));
        formPanel.add(txtGiaTien);

        formPanel.add(createLabel("Mã Vé:"));
        formPanel.add(txtMaVe);
        formPanel.add(createLabel("Trạng Thái:"));
        formPanel.add(cbTrangThai);

        formPanel.add(createLabel("Số Kg:"));
        formPanel.add(txtSoKg);
        formPanel.add(createLabel("Ghi Chú:"));
        formPanel.add(txtGhiChu);

        formPanel.add(createLabel("Kích Thước:"));
        formPanel.add(txtKichThuoc);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        formCard.add(formPanel, BorderLayout.CENTER);

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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
                    if (status.equalsIgnoreCase("Đã Check-in")) {
                        setForeground(new Color(59, 130, 246)); // Xanh dương
                    } else if (status.equalsIgnoreCase("Đã Check-out")) {
                        setForeground(new Color(34, 197, 94)); // Xanh lá
                    } else if (status.equalsIgnoreCase("Chưa Check-in")) {
                        setForeground(new Color(239, 68, 68)); // Đỏ
                    } else {
                        setForeground(new Color(156, 163, 175)); // Xám
                    }
                }
                
                if (isSelected) setForeground(Color.WHITE);
                return c;
            }
        };

        for(int i = 0; i < table.getColumnCount(); i++){
            if (i == 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer); 
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void loadDataToTable(List<HanhLy> list) {
        tableModel.setRowCount(0); 
        if (list == null) return;
        for (HanhLy hl : list) {
            tableModel.addRow(new Object[]{
                hl.getMaHanhLy(),
                hl.getMaVe(),
                hl.getSoKg(),
                hl.getKichThuoc(),
                String.format("%,.0f", hl.getGiaTien()),
                hl.getTrangThai() != null ? hl.getTrangThai() : "Chưa Check-in",
                hl.getGhiChu()
            });
        }
    }

    private void setupListeners() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaHanhLy.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaHanhLy.setEditable(false); 
                    txtMaVe.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                    txtSoKg.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                    txtKichThuoc.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                    
                    String giaTienStr = tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString().replace(",", "") : "0";
                    txtGiaTien.setText(giaTienStr);
                    
                    String status = tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "Chưa Check-in";
                    cbTrangThai.setSelectedItem(status);

                    txtGhiChu.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
                }
            }
        });

        cboHienThi.addActionListener(e -> {
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            if (isTrash) {
                btnThem.setEnabled(false);
                btnSua.setText("Khôi phục");
                btnSua.setBackground(new Color(76, 175, 80)); 
                btnXoa.setEnabled(false);
                cbTrangThai.setEnabled(false);
                
                List<HanhLy> trashData = hanhLyBUS.docDanhSachHanhLy().stream()
                        .filter(hl -> "Hủy".equalsIgnoreCase(hl.getTrangThai()))
                        .collect(Collectors.toList());
                loadDataToTable(trashData);
            } else {
                btnThem.setEnabled(true);
                btnSua.setText("Cập nhật");
                btnSua.setBackground(BTN_UPDATE);
                btnXoa.setEnabled(true);
                cbTrangThai.setEnabled(true);
                loadDataToTable(hanhLyBUS.docDanhSachHanhLy());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                HanhLy hl = getFormInput();
                String result = hanhLyBUS.themHanhLy(hl);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                String maHL = txtMaHanhLy.getText().trim();
                if (maHL.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hành lý trên bảng!");
                    return;
                }

                if (cboHienThi.getSelectedIndex() == 1) {
                    HanhLy hlKhPhuc = getFormInput();
                    hlKhPhuc.setTrangThai("Chưa Check-in"); 
                    String result = hanhLyBUS.capNhatHanhLy(hlKhPhuc);
                    JOptionPane.showMessageDialog(this, "Khôi phục " + result.toLowerCase());
                    if (result.contains("Thành công")) btnLamMoi.doClick();
                    return;
                }

                HanhLy hl = getFormInput();
                String result = hanhLyBUS.capNhatHanhLy(hl);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hành lý cần xóa!");
                return;
            }
            String maHL = txtMaHanhLy.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hành lý " + maHL + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                String result = hanhLyBUS.xoaHanhLy(maHL);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaHanhLy.setText("");
            txtMaVe.setText("");
            txtSoKg.setText("");
            txtKichThuoc.setText("");
            txtGiaTien.setText("");
            txtGhiChu.setText("");
            txtTimKiem.setText("");
            cbTrangThai.setSelectedIndex(0);
            
            txtMaHanhLy.setEditable(true);
            table.clearSelection();

            if (cboHienThi.getSelectedIndex() == 1) {
                List<HanhLy> trashData = hanhLyBUS.docDanhSachHanhLy().stream()
                        .filter(hl -> "Hủy".equalsIgnoreCase(hl.getTrangThai()))
                        .collect(Collectors.toList());
                loadDataToTable(trashData);
            } else {
                loadDataToTable(hanhLyBUS.docDanhSachHanhLy());
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            
            List<HanhLy> allData = hanhLyBUS.docDanhSachHanhLy();
            List<HanhLy> ketQua = allData.stream().filter(hl -> {
                boolean matchKeyword = hl.getMaHanhLy().toLowerCase().contains(keyword) || 
                                       hl.getMaVe().toLowerCase().contains(keyword);
                boolean matchStatus = isTrash ? "Hủy".equalsIgnoreCase(hl.getTrangThai()) 
                                              : !"Hủy".equalsIgnoreCase(hl.getTrangThai());
                return matchKeyword && matchStatus;
            }).collect(Collectors.toList());
            
            loadDataToTable(ketQua);
        });
    }

    private HanhLy getFormInput() throws Exception {
        String maHL = txtMaHanhLy.getText().trim();
        if (maHL.isEmpty()) throw new Exception("Mã hành lý không được để trống!");
        
        String maVe = txtMaVe.getText().trim();
        
        BigDecimal soKg = BigDecimal.ZERO;
        try {
            soKg = new BigDecimal(txtSoKg.getText().trim().isEmpty() ? "0" : txtSoKg.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Số Kg phải là số hợp lệ!");
        }

        String kichThuoc = txtKichThuoc.getText().trim();
        
        BigDecimal giaTien = BigDecimal.ZERO;
        try {
            giaTien = new BigDecimal(txtGiaTien.getText().trim().isEmpty() ? "0" : txtGiaTien.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Giá tiền phải là số hợp lệ!");
        }

        String trangThai = cbTrangThai.getSelectedItem().toString();
        String ghiChu = txtGhiChu.getText().trim();

        return new HanhLy(maHL, maVe, soKg, kichThuoc, giaTien, trangThai, ghiChu);
    }
    
   
}