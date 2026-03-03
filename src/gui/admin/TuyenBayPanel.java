package gui.admin;

import bll.TuyenBayBUS;
import bll.SanBayBUS;
import model.TuyenBay;
import model.SanBay;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

public class TuyenBayPanel extends JPanel {

    // ===== UI COMPONENTS =====
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaTuyenBay, txtKhoangCach, txtGiaGoc;
    private JComboBox<String> cbSanBayDi, cbSanBayDen;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    
    // Thanh tìm kiếm
    private JTextField txtTimKiem;
    private JButton btnTimKiem;

    // ===== BUSINESS LOGIC =====
    private TuyenBayBUS tuyenBayBUS;
    private SanBayBUS sanBayBUS;

    // ====== MÀU HỆ THỐNG (ĐỒNG BỘ VỚI CÁC PANEL KHÁC) ======
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public TuyenBayPanel() {
        tuyenBayBUS = new TuyenBayBUS();
        sanBayBUS = new SanBayBUS();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadSanBayToComboBox(); 
        loadDataToTable(tuyenBayBUS.getAllTuyenBay());
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
            // Nhớ chuẩn bị icon máy bay hoặc bản đồ ở đường dẫn này
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ TUYẾN BAY", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);
        
        // Search
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

        searchPanel.add(new JLabel("Tìm (Mã/Nơi đi/Nơi đến):"));
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

        String[] columns = {"Mã Tuyến Bay", "Sân Bay Đi", "Sân Bay Đến", "Khoảng Cách (km)", "Giá Gốc (VNĐ)"};
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

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        formPanel.setOpaque(false);

        txtMaTuyenBay = createTextField();
        txtKhoangCach = createTextField();
        txtGiaGoc = createTextField();
        cbSanBayDi = new JComboBox<>();
        cbSanBayDi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSanBayDen = new JComboBox<>();
        cbSanBayDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(createLabel("Mã Tuyến Bay:"));
        formPanel.add(txtMaTuyenBay);
        formPanel.add(createLabel("Khoảng Cách (km):"));
        formPanel.add(txtKhoangCach);
        
        formPanel.add(createLabel("Sân Bay Đi:"));
        formPanel.add(cbSanBayDi);
        formPanel.add(createLabel("Giá Gốc (VNĐ):"));
        formPanel.add(txtGiaGoc);

        formPanel.add(createLabel("Sân Bay Đến:"));
        formPanel.add(cbSanBayDen);
        formPanel.add(new JLabel()); // Căn layout
        formPanel.add(new JLabel());

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
    private void loadSanBayToComboBox() {
        cbSanBayDi.removeAllItems();
        cbSanBayDen.removeAllItems();
        
        ArrayList<SanBay> listSanBay = sanBayBUS.getAllSanBay();
        for (SanBay sb : listSanBay) {
            String item = sb.getMaSanBay() + " - " + sb.getTenSanBay();
            cbSanBayDi.addItem(item);
            cbSanBayDen.addItem(item);
        }
    }

    private void loadDataToTable(ArrayList<TuyenBay> list) {
        tableModel.setRowCount(0); 
        for (TuyenBay tb : list) {
            tableModel.addRow(new Object[]{
                tb.getMaTuyenBay(),
                tb.getSanBayDi(),
                tb.getSanBayDen(),
                tb.getKhoangCach(),
                tb.getGiaGoc()
            });
        }
    }
    
    private void setComboBoxSelectedByMa(JComboBox<String> cb, String maSB) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).startsWith(maSB + " -")) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private void setupListeners() {
        // 1. Click bảng đổ dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaTuyenBay.setText(tableModel.getValueAt(row, 0).toString());
                    
                    String maSBDi = tableModel.getValueAt(row, 1).toString();
                    String maSBDen = tableModel.getValueAt(row, 2).toString();
                    setComboBoxSelectedByMa(cbSanBayDi, maSBDi);
                    setComboBoxSelectedByMa(cbSanBayDen, maSBDen);
                    
                    txtKhoangCach.setText(tableModel.getValueAt(row, 3).toString());
                    txtGiaGoc.setText(tableModel.getValueAt(row, 4).toString());
                    
                    txtMaTuyenBay.setEditable(false); 
                }
            }
        });

        // 2. Nút Thêm
        btnThem.addActionListener(e -> {
            try {
                TuyenBay tb = getFormInput();
                if (tuyenBayBUS.themTuyenBay(tb)) {
                    JOptionPane.showMessageDialog(this, "Thêm tuyến bay thành công!");
                    btnLamMoi.doClick(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng ID hoặc lỗi DB)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 3. Nút Sửa
        btnSua.addActionListener(e -> {
            try {
                if (txtMaTuyenBay.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến bay trên bảng để cập nhật!");
                    return;
                }
                TuyenBay tb = getFormInput();
                if (tuyenBayBUS.capNhatTuyenBay(tb)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật tuyến bay thành công!");
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến bay cần xóa!");
                return;
            }
            String maTB = txtMaTuyenBay.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tuyến bay " + maTB + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (tuyenBayBUS.xoaTuyenBay(maTB)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Có thể tuyến bay đang có chuyến bay hoạt động.", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 5. Nút Làm Mới
        btnLamMoi.addActionListener(e -> {
            txtMaTuyenBay.setText("");
            txtKhoangCach.setText("");
            txtGiaGoc.setText("");
            txtTimKiem.setText("");
            if(cbSanBayDi.getItemCount() > 0) cbSanBayDi.setSelectedIndex(0);
            if(cbSanBayDen.getItemCount() > 0) cbSanBayDen.setSelectedIndex(0);
            
            txtMaTuyenBay.setEditable(true);
            table.clearSelection();
            loadDataToTable(tuyenBayBUS.getAllTuyenBay());
        });

        // 6. Nút Tìm Kiếm
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            ArrayList<TuyenBay> ketQua = tuyenBayBUS.timKiemTuyenBay(keyword);
            loadDataToTable(ketQua);
        });
    }

    private TuyenBay getFormInput() throws Exception {
        String maTB = txtMaTuyenBay.getText().trim();
        if (maTB.isEmpty()) throw new Exception("Mã tuyến bay không được để trống!");

        String sbDi = cbSanBayDi.getSelectedItem().toString().split(" - ")[0].trim();
        String sbDen = cbSanBayDen.getSelectedItem().toString().split(" - ")[0].trim();

        float khoangCach = 0;
        try {
            khoangCach = Float.parseFloat(txtKhoangCach.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Khoảng cách phải là số nguyên hoặc số thực!");
        }

        BigDecimal giaGoc = BigDecimal.ZERO;
        try {
            giaGoc = new BigDecimal(txtGiaGoc.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Giá gốc phải là số hợp lệ!");
        }

        return new TuyenBay(maTB, sbDi, sbDen, khoangCach, giaGoc);
    }
}