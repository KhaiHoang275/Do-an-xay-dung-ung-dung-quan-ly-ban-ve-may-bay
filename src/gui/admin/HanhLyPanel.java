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

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(130, 35));
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setOpaque(true);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTimKiem.setFocusPainted(false);
        btnTimKiem = createButton("Tìm kiếm", BTN_UPDATE);
        btnTimKiem.setPreferredSize(new Dimension(130, 35));
        try { setButtonIcon(btnTimKiem, "/resources/icons/icons8-search-24.png", 16); } catch (Exception e){}

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
        txtGiaTien.setEditable(false); // Khóa ô nhập
        txtGiaTien.setBackground(new Color(235, 235, 235));
        txtGhiChu = createTextField();
        
       cbTrangThai = new JComboBox<>(new String[]{"Chua Check-in", "Đã Check-in", "Hủy"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
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

        btnThem = createButton("Thêm", BTN_ADD);
        btnSua = createButton("Cập nhật", BTN_UPDATE);
        btnXoa = createButton("Xóa", BTN_DELETE);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);

        try {
            setButtonIcon(btnThem, "/resources/icons/icons8-add-24.png", 20);
            setButtonIcon(btnSua, "/resources/icons/icons8-update-24.png", 20);
            setButtonIcon(btnXoa, "/resources/icons/icons8-delete-24.png", 20);
            setButtonIcon(btnLamMoi, "/resources/icons/icons8-erase-24.png", 20);
        } catch (Exception e) {}

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        formCard.add(buttonPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.SOUTH);

        setupListeners();
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

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return btn;
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

        // --- ĐOẠN SỬA LỖI TRẮNG BÓC TIÊU ĐỀ ---
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(TABLE_HEADER); // Màu xanh đen bạn đã khai báo
        headerRenderer.setForeground(Color.WHITE);  // Chữ trắng
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        // --------------------------------------

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++){
             table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void setButtonIcon(JButton btn, String path, int size) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaled));
        btn.setIconTextGap(8);
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
                hl.getTrangThai(),
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
                    
                    cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "Chưa sử dụng");
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
                    hlKhPhuc.setTrangThai("Chưa sử dụng"); 
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
        // TỰ ĐỘNG NHẢY GIÁ TIỀN KHI NHẬP SỐ KG
        txtSoKg.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tinhTien(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tinhTien(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tinhTien(); }

            private void tinhTien() {
                SwingUtilities.invokeLater(() -> {
                    try {
                        String text = txtSoKg.getText().trim();
                        if (text.isEmpty()) {
                            txtGiaTien.setText("0");
                            return;
                        }
                        
                        double kg = Double.parseDouble(text);
                        long gia = 0;

                        // Áp dụng Kịch bản 2: Tính theo gói
                        if (kg <= 15) gia = 150000;
                        else if (kg <= 20) gia = 200000;
                        else if (kg <= 30) gia = 350000;
                        else {
                            // Quá 30kg: 350k + 50k cho mỗi kg vượt
                            gia = 350000 + (long)((kg - 30) * 50000);
                        }

                        txtGiaTien.setText(String.valueOf(gia));
                    } catch (NumberFormatException ex) {
                        txtGiaTien.setText("0");
                    }
                });
            }
        });
    }

  private HanhLy getFormInput() throws Exception {
        String maHL = txtMaHanhLy.getText().trim();
        if (maHL.isEmpty()) throw new Exception("Mã hành lý không được để trống!");
        
        String maVe = txtMaVe.getText().trim();
        if (maVe.isEmpty()) throw new Exception("Mã vé không được để trống!");
        
        BigDecimal soKg = new BigDecimal(txtSoKg.getText().trim().isEmpty() ? "0" : txtSoKg.getText().trim());
        if (soKg.compareTo(BigDecimal.ZERO) <= 0) throw new Exception("Số Kg phải lớn hơn 0!");

        // Lấy giá tiền đã được tính tự động hiển thị trên ô text
        BigDecimal giaTien = new BigDecimal(txtGiaTien.getText());

        return new HanhLy(maHL, maVe, soKg, txtKichThuoc.getText().trim(), giaTien, 
                          cbTrangThai.getSelectedItem().toString(), txtGhiChu.getText().trim());
    }
}