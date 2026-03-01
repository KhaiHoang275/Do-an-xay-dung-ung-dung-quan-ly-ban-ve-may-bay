package gui.admin;

import bll.HeSoGiaBUS;
import model.HeSoGia;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class HeSoGiaPanel extends JPanel {

    // ===== UI COMPONENTS =====
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHeSoGia, txtHeSo, txtSoGioDatTruoc;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    
    // Thanh tìm kiếm
    private JTextField txtTimKiem;
    private JButton btnTimKiem;

    // ===== BUSINESS LOGIC =====
    private HeSoGiaBUS heSoGiaBUS;

    // ====== MÀU HỆ THỐNG (ĐỒNG BỘ) ======
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public HeSoGiaPanel() {
        heSoGiaBUS = new HeSoGiaBUS();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadDataToTable(heSoGiaBUS.getAllHeSoGia());
    }

    private void initComponents() {

        // ========================================
        // 1. HEADER (TITLE & SEARCH)
        // ========================================
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        // Title
        ImageIcon titleIcon = null;
        try {
            // Thay icon tag/price cho phù hợp
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ HỆ SỐ GIÁ", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);
        
        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        txtTimKiem = createTextField();
        txtTimKiem.setPreferredSize(new Dimension(200, 35));
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(100, 35));
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTimKiem.setFocusPainted(false);
        try { setButtonIcon(btnTimKiem, "/resources/icons/icons8-search-24.png", 16); } catch (Exception e){}

        searchPanel.add(new JLabel("Tìm (Mã HSG/Giờ):"));
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // ========================================
        // 2. TABLE CARD (CENTER)
        // ========================================
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Mã Hệ Số Giá", "Hệ Số (Tỷ lệ nhân)", "Số Giờ Đặt Trước (h)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        // ========================================
        // 3. FORM CARD (SOUTH)
        // ========================================
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        formPanel.setOpaque(false);

        txtMaHeSoGia = createTextField();
        txtHeSo = createTextField();
        txtSoGioDatTruoc = createTextField();

        // Hàng 1
        formPanel.add(createLabel("Mã Hệ Số Giá:"));
        formPanel.add(txtMaHeSoGia);
        formPanel.add(createLabel("Hệ Số (VD: 1.2):"));
        formPanel.add(txtHeSo);
        
        // Hàng 2
        formPanel.add(createLabel("Số Giờ Đặt Trước (h):"));
        formPanel.add(txtSoGioDatTruoc);
        formPanel.add(new JLabel()); // Căn layout
        formPanel.add(new JLabel()); // Căn layout

        formCard.add(formPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
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

        // ========================================
        // 4. EVENTS
        // ========================================
        setupListeners();
    }

    // ========================================
    // UI STYLE METHODS
    // ========================================
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
        
        // Căn giữa nội dung cột
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

    // ========================================
    // LOGIC & DATA METHODS
    // ========================================
    private void loadDataToTable(ArrayList<HeSoGia> list) {
        tableModel.setRowCount(0); 
        for (HeSoGia hsg : list) {
            tableModel.addRow(new Object[]{
                hsg.getMaHeSoGia(),
                hsg.getHeSo(),
                hsg.getSoGioDatTruoc()
            });
        }
    }

    private void setupListeners() {
        // 1. Click bảng đổ dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaHeSoGia.setText(tableModel.getValueAt(row, 0).toString());
                    txtHeSo.setText(tableModel.getValueAt(row, 1).toString());
                    txtSoGioDatTruoc.setText(tableModel.getValueAt(row, 2).toString());
                    
                    txtMaHeSoGia.setEditable(false); 
                }
            }
        });

        // 2. Nút Thêm
        btnThem.addActionListener(e -> {
            try {
                HeSoGia hsg = getFormInput();
                if (heSoGiaBUS.themHeSoGia(hsg)) {
                    JOptionPane.showMessageDialog(this, "Thêm hệ số giá thành công!");
                    btnLamMoi.doClick(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Lỗi DB)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 3. Nút Sửa
        btnSua.addActionListener(e -> {
            try {
                if (txtMaHeSoGia.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hệ số giá trên bảng để cập nhật!");
                    return;
                }
                HeSoGia hsg = getFormInput();
                if (heSoGiaBUS.capNhatHeSoGia(hsg)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật hệ số giá thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 4. Nút Xóa
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hệ số giá cần xóa!");
                return;
            }
            String maHSG = txtMaHeSoGia.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hệ số giá " + maHSG + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (heSoGiaBUS.xoaHeSoGia(maHSG)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Có thể hệ số giá này đang được sử dụng ở Chuyến bay.", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 5. Nút Làm Mới
        btnLamMoi.addActionListener(e -> {
            txtMaHeSoGia.setText("");
            txtHeSo.setText("");
            txtSoGioDatTruoc.setText("");
            txtTimKiem.setText("");
            
            txtMaHeSoGia.setEditable(true);
            table.clearSelection();
            loadDataToTable(heSoGiaBUS.getAllHeSoGia());
        });

        // 6. Nút Tìm Kiếm
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            ArrayList<HeSoGia> ketQua = heSoGiaBUS.timKiemHeSoGia(keyword);
            loadDataToTable(ketQua);
        });
    }

    private HeSoGia getFormInput() throws Exception {
        String maHSG = txtMaHeSoGia.getText().trim();
        if (maHSG.isEmpty()) throw new Exception("Mã hệ số giá không được để trống!");

        float heSo = 0;
        try {
            heSo = Float.parseFloat(txtHeSo.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Hệ số phải là số (Ví dụ: 1.2, 0.8)!");
        }

        float soGio = 0;
        try {
            soGio = Float.parseFloat(txtSoGioDatTruoc.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Số giờ đặt trước phải là số hợp lệ!");
        }

        return new HeSoGia(maHSG, heSo, soGio);
    }
}