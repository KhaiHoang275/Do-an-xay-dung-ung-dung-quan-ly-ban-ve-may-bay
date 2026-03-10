package gui.admin;

import bll.DichVuBoSungBUS;
import model.DichVuBoSung;

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

public class DichVuBoSungPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaDichVu, txtTenDichVu, txtDonGia, txtTimKiem;
    private JComboBox<String> cboHienThi;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private DichVuBoSungBUS dichVuBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public DichVuBoSungPanel() {
        dichVuBUS = new DichVuBoSungBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataToTable(dichVuBUS.docDanhSachDichVu());
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ DỊCH VỤ BỔ SUNG", JLabel.LEFT);
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

        String[] columns = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Đơn Giá (VNĐ)"};
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

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        formPanel.setOpaque(false);

        txtMaDichVu = createTextField();
        txtTenDichVu = createTextField();
        txtDonGia = createTextField();

        formPanel.add(createLabel("Mã Dịch Vụ (*):"));
        formPanel.add(txtMaDichVu);
        formPanel.add(createLabel("Tên Dịch Vụ (*):"));
        formPanel.add(txtTenDichVu);

        formPanel.add(createLabel("Đơn Giá (VNĐ):"));
        formPanel.add(txtDonGia);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel()); 

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
        for(int i = 0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadDataToTable(List<DichVuBoSung> list) {
        tableModel.setRowCount(0);
        if (list == null) return;
        for (DichVuBoSung dv : list) {
            tableModel.addRow(new Object[]{
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    String.format("%,.0f", dv.getDonGia())
            });
        }
    }

    private void setupListeners() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaDichVu.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaDichVu.setEditable(false);
                    txtTenDichVu.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");

                    String donGiaStr = tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString().replace(",", "") : "0";
                    txtDonGia.setText(donGiaStr);
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
                loadDataToTable(dichVuBUS.docDanhSachDichVu());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                DichVuBoSung dv = getFormInput();
                String result = dichVuBUS.themDichVu(dv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                if (txtMaDichVu.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ trên bảng!");
                    return;
                }
                DichVuBoSung dv = getFormInput();
                String result = dichVuBUS.capNhatDichVu(dv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!");
                return;
            }
            String maDV = txtMaDichVu.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ " + maDV + " không?\nHành động này xóa vĩnh viễn khỏi CSDL!", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = dichVuBUS.xoaDichVu(maDV);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaDichVu.setText("");
            txtTenDichVu.setText("");
            txtDonGia.setText("");
            txtTimKiem.setText("");
            txtMaDichVu.setEditable(true);
            table.clearSelection();

            if (cboHienThi.getSelectedIndex() == 0) {
                loadDataToTable(dichVuBUS.docDanhSachDichVu());
            } else {
                tableModel.setRowCount(0);
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            List<DichVuBoSung> allData = dichVuBUS.docDanhSachDichVu();
            
            List<DichVuBoSung> ketQua = allData.stream().filter(dv -> 
                dv.getMaDichVu().toLowerCase().contains(keyword) || 
                dv.getTenDichVu().toLowerCase().contains(keyword)
            ).collect(Collectors.toList());
            
            loadDataToTable(ketQua);
        });
    }

    private DichVuBoSung getFormInput() throws Exception {
        String maDV = txtMaDichVu.getText().trim();
        if (maDV.isEmpty()) throw new Exception("Mã dịch vụ không được để trống!");

        String tenDV = txtTenDichVu.getText().trim();
        if (tenDV.isEmpty()) throw new Exception("Tên dịch vụ không được để trống!");

        BigDecimal donGia = BigDecimal.ZERO;
        try {
            donGia = new BigDecimal(txtDonGia.getText().trim().isEmpty() ? "0" : txtDonGia.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Đơn giá phải là số hợp lệ!");
        }

        return new DichVuBoSung(maDV, tenDV, donGia);
    }

}