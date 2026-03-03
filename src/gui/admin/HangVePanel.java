package gui.admin;

import bll.HangVeBUS;
import model.HangVe;
import model.TrangThaiHangVe;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class HangVePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHangVe, txtTenHang, txtHeSoHangVe, txtTimKiem;
    private JComboBox<String> cbTrangThai, cboHienThi;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    
    private HangVeBUS hangVeBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public HangVePanel() {
        hangVeBUS = new HangVeBUS();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadDataToTable(hangVeBUS.getAllHangVe());
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        ImageIcon titleIcon = null;
        try {
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ HẠNG VÉ", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        txtTimKiem = createTextField();
        txtTimKiem.setPreferredSize(new Dimension(200, 35));
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(130, 35));
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTimKiem.setFocusPainted(false);
        try { setButtonIcon(btnTimKiem, "/resources/icons/icons8-search-24.png", 16); } catch (Exception e){}

        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        cboHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboHienThi.setPreferredSize(new Dimension(150, 35));

        // searchPanel.add(new JLabel("Tìm (Mã/Tên hạng):"));
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

        String[] columns = {"Mã Hạng Vé", "Tên Hạng Vé", "Hệ Số Hạng Vé", "Trạng Thái"};
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

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setOpaque(false);

        txtMaHangVe = createTextField();
        txtTenHang = createTextField();
        txtHeSoHangVe = createTextField();
        
        cbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Tạm ngưng", "Đã xóa"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(createLabel("Mã Hạng Vé:"));
        formPanel.add(txtMaHangVe);
        
        formPanel.add(createLabel("Tên Hạng Vé (VD: Phổ thông, Thương gia):"));
        formPanel.add(txtTenHang);
        
        formPanel.add(createLabel("Hệ Số Hạng Vé (VD: 1.0, 1.5):"));
        formPanel.add(txtHeSoHangVe);

        formPanel.add(createLabel("Trạng Thái:"));
        formPanel.add(cbTrangThai);

        JPanel wrapperFormPanel = new JPanel(new BorderLayout());
        wrapperFormPanel.setOpaque(false);
        wrapperFormPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel rightPadding = new JPanel();
        rightPadding.setOpaque(false);
        rightPadding.setPreferredSize(new Dimension(300, 0));
        wrapperFormPanel.add(rightPadding, BorderLayout.EAST);

        formCard.add(wrapperFormPanel, BorderLayout.CENTER);

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
        btn.setPreferredSize(new Dimension(130, 40));
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
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
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

    private String hienThiTrangThai(TrangThaiHangVe status) {
        if (status == null) return "Hoạt động";
        switch (status) {
            case HOAT_DONG: return "Hoạt động";
            case TAM_NGUNG: return "Tạm ngưng";
            case DA_XOA: return "Đã xóa";
            default: return "Hoạt động";
        }
    }

    private TrangThaiHangVe layTrangThaiTuUI(String uiValue) {
        if (uiValue == null) return TrangThaiHangVe.HOAT_DONG;
        switch (uiValue) {
            case "Hoạt động": return TrangThaiHangVe.HOAT_DONG;
            case "Tạm ngưng": return TrangThaiHangVe.TAM_NGUNG;
            case "Đã xóa": return TrangThaiHangVe.DA_XOA;
            default: return TrangThaiHangVe.HOAT_DONG;
        }
    }

    private void loadDataToTable(ArrayList<HangVe> list) {
        tableModel.setRowCount(0); 
        for (HangVe hv : list) {
            tableModel.addRow(new Object[]{
                hv.getMaHangVe(),
                hv.getTenHang(),
                hv.getHeSoHangVe(),
                hienThiTrangThai(hv.getTrangThai())
            });
        }
    }

    private void setupListeners() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaHangVe.setText(tableModel.getValueAt(row, 0).toString());
                    txtTenHang.setText(tableModel.getValueAt(row, 1).toString());
                    txtHeSoHangVe.setText(tableModel.getValueAt(row, 2).toString());
                    
                    String trangThaiTrenBang = tableModel.getValueAt(row, 3).toString();
                    cbTrangThai.setSelectedItem(trangThaiTrenBang);
                    
                    txtMaHangVe.setEditable(false); 
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
                loadDataToTable(hangVeBUS.getHangVeTrongThungRac());
            } else {
                btnThem.setEnabled(true);
                btnSua.setText("Cập nhật");
                btnSua.setBackground(BTN_UPDATE);
                btnXoa.setEnabled(true);
                cbTrangThai.setEnabled(true);
                loadDataToTable(hangVeBUS.getAllHangVe());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                HangVe hv = getFormInput();
                if (hangVeBUS.themHangVe(hv)) {
                    JOptionPane.showMessageDialog(this, "Thêm hạng vé thành công!");
                    btnLamMoi.doClick(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                if (txtMaHangVe.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hạng vé trên bảng để cập nhật!");
                    return;
                }

                String maHangVe = txtMaHangVe.getText().trim();

                if (cboHienThi.getSelectedIndex() == 1) {
                    if (hangVeBUS.khoiPhucHangVe(maHangVe)) {
                        JOptionPane.showMessageDialog(this, "Khôi phục hạng vé thành công!");
                        btnLamMoi.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }

                HangVe hv = getFormInput();
                if (hangVeBUS.capNhatHangVe(hv)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật hạng vé thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hạng vé cần xóa!");
                return;
            }
            String maHangVe = txtMaHangVe.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hạng vé " + maHangVe + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (hangVeBUS.xoaHangVe(maHangVe)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaHangVe.setText("");
            txtTenHang.setText("");
            txtHeSoHangVe.setText("");
            txtTimKiem.setText("");
            cbTrangThai.setSelectedIndex(0);
            
            txtMaHangVe.setEditable(true);
            table.clearSelection();

            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(hangVeBUS.getHangVeTrongThungRac());
            } else {
                loadDataToTable(hangVeBUS.getAllHangVe());
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            ArrayList<HangVe> ketQua = hangVeBUS.timKiemHangVe(keyword, isTrash);
            loadDataToTable(ketQua);
        });
    }

    private HangVe getFormInput() throws Exception {
        String maHangVe = txtMaHangVe.getText().trim();
        if (maHangVe.isEmpty()) throw new Exception("Mã hạng vé không được để trống!");

        String tenHang = txtTenHang.getText().trim();
        if (tenHang.isEmpty()) throw new Exception("Tên hạng vé không được để trống!");

        float heSo = 0;
        try {
            heSo = Float.parseFloat(txtHeSoHangVe.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Hệ số hạng vé phải là số (Ví dụ: 1.0, 1.5)!");
        }

        TrangThaiHangVe trangThai = layTrangThaiTuUI(cbTrangThai.getSelectedItem().toString());

        return new HangVe(maHangVe, tenHang, heSo, trangThai);
    }
}