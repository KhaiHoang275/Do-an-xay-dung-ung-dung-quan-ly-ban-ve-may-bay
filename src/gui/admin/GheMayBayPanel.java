package gui.admin;

import bll.GheMayBayBUS;
import bll.MayBayBUS;
import model.GheMayBay;
import model.MayBay;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

public class GheMayBayPanel extends JPanel {

    // ===== UI COMPONENTS =====
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbMayBay;
    private JTextField txtGiaGhe;
    
    // Cụm tạo hàng loạt
    private JTextField txtTienTo, txtSoLuong;
    
    // Cụm thao tác đơn lẻ
    private JTextField txtMaGhe, txtSoGhe;
    
    private JButton btnTaoHangLoat, btnSua, btnXoa, btnLamMoi;
    
    // Thanh tìm kiếm
    private JTextField txtTimKiem;
    private JButton btnTimKiem;

    // ===== BUSINESS LOGIC =====
    private GheMayBayBUS gheMayBayBUS;
    private MayBayBUS mayBayBUS;

    // ====== MÀU HỆ THỐNG (ĐỒNG BỘ) ======
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public GheMayBayPanel() {
        gheMayBayBUS = new GheMayBayBUS();
        mayBayBUS = new MayBayBUS();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadMayBayToComboBox();
        loadDataToTable(gheMayBayBUS.getAllGheMayBay());
    }

    private void initComponents() {

        // ========================================
        // 1. HEADER (TITLE & SEARCH)
        // ========================================
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        ImageIcon titleIcon = null;
        try {
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/icons8-airline-seat-24.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ GHẾ MÁY BAY (TẠO HÀNG LOẠT)", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
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

        searchPanel.add(new JLabel("Tìm (Mã Ghế/Số/Mã MB):"));
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

        String[] columns = {"Mã Ghế", "Thuộc Máy Bay", "Số Ghế", "Giá Ghế Cơ Bản (VNĐ)"};
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

        // Layout 3 hàng x 2 cột cho ngay ngắn
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setOpaque(false);

        cbMayBay = new JComboBox<>();
        cbMayBay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGiaGhe = createTextField();
        
        txtTienTo = createTextField();
        txtSoLuong = createTextField();
        
        txtMaGhe = createTextField();
        txtMaGhe.setBackground(new Color(240, 240, 240)); // Màu nền xám để biết là ReadOnly
        txtMaGhe.setEditable(false);
        txtSoGhe = createTextField();

        // Hàng 1: Dùng chung
        formPanel.add(createLabel("Chọn Máy Bay:"));
        formPanel.add(cbMayBay);
        formPanel.add(createLabel("Giá Ghế Cơ Bản (VNĐ):"));
        formPanel.add(txtGiaGhe);
        
        // Hàng 2: Dùng cho tạo hàng loạt
        formPanel.add(createLabel("Tiền Tố Dãy (VD: A, B, VIP) - Để tạo loạt:"));
        formPanel.add(txtTienTo);
        formPanel.add(createLabel("Số Lượng Ghế Cần Tạo (VD: 50):"));
        formPanel.add(txtSoLuong);

        // Hàng 3: Dùng cho thao tác đơn lẻ
        formPanel.add(createLabel("Mã Ghế (Chỉ xem/xóa):"));
        formPanel.add(txtMaGhe);
        formPanel.add(createLabel("Số Ghế (Dùng khi sửa 1 ghế):"));
        formPanel.add(txtSoGhe);

        // Ép width cho form đẹp hơn
        JPanel wrapperFormPanel = new JPanel(new BorderLayout());
        wrapperFormPanel.setOpaque(false);
        wrapperFormPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel rightPadding = new JPanel();
        rightPadding.setOpaque(false);
        rightPadding.setPreferredSize(new Dimension(150, 0)); 
        wrapperFormPanel.add(rightPadding, BorderLayout.EAST);

        formCard.add(wrapperFormPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnTaoHangLoat = createButton("Tạo Hàng Loạt", BTN_ADD);
        btnTaoHangLoat.setPreferredSize(new Dimension(160, 40)); 
        btnSua = createButton("Cập nhật 1 ghế", BTN_UPDATE);
        btnSua.setPreferredSize(new Dimension(160, 40));
        btnXoa = createButton("Xóa 1 ghế", BTN_DELETE);
        btnLamMoi = createButton("Làm mới form", BTN_REFRESH);

        try {
            setButtonIcon(btnTaoHangLoat, "/resources/icons/icons8-add-24.png", 20);
            setButtonIcon(btnSua, "/resources/icons/icons8-update-24.png", 20);
            setButtonIcon(btnXoa, "/resources/icons/icons8-delete-24.png", 20);
            setButtonIcon(btnLamMoi, "/resources/icons/icons8-erase-24.png", 20);
        } catch (Exception e) {}

        buttonPanel.add(btnTaoHangLoat);
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

    // ========================================
    // LOGIC & DATA METHODS
    // ========================================
    private void loadMayBayToComboBox() {
        cbMayBay.removeAllItems();
        ArrayList<MayBay> listMayBay = mayBayBUS.getAllMayBay();
        for (MayBay mb : listMayBay) {
            cbMayBay.addItem(mb.getMaMayBay() + " - " + mb.getSoHieu() + " (" + mb.getHangSanXuat() + ")");
        }
    }

    private void loadDataToTable(ArrayList<GheMayBay> list) {
        tableModel.setRowCount(0); 
        for (GheMayBay ghe : list) {
            tableModel.addRow(new Object[]{
                ghe.getMaGhe(),
                ghe.getMaMayBay(),
                ghe.getSoGhe(),
                ghe.getGiaGhe()
            });
        }
    }

    private void setComboBoxSelectedByPrefix(JComboBox<String> cb, String prefix) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).startsWith(prefix + " -")) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private void setupListeners() {
        // 1. Click bảng (Phục vụ cho tính năng Sửa/Xóa đơn lẻ)
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaGhe.setText(tableModel.getValueAt(row, 0).toString());
                    setComboBoxSelectedByPrefix(cbMayBay, tableModel.getValueAt(row, 1).toString());
                    txtSoGhe.setText(tableModel.getValueAt(row, 2).toString());
                    txtGiaGhe.setText(tableModel.getValueAt(row, 3).toString());
                    
                    // Khóa các ô tạo hàng loạt để tránh nhầm lẫn
                    txtTienTo.setText("");
                    txtSoLuong.setText("");
                    txtTienTo.setEnabled(false);
                    txtSoLuong.setEnabled(false);
                }
            }
        });

        // 2. Bắt sự kiện khi đổi Máy Bay -> Tự động điền số lượng ghế tối đa
        cbMayBay.addActionListener(e -> {
            if (cbMayBay.getSelectedItem() != null && txtSoLuong.isEnabled()) {
                String maMB = cbMayBay.getSelectedItem().toString().split(" - ")[0].trim();
                
                ArrayList<MayBay> dsMayBay = mayBayBUS.getAllMayBay();
                for (MayBay mb : dsMayBay) {
                    if (mb.getMaMayBay().equals(maMB)) {
                        txtSoLuong.setText(String.valueOf(mb.getTongSoGhe()));
                        break;
                    }
                }
            }
        });

        // 3. Nút Tạo Hàng Loạt
        btnTaoHangLoat.addActionListener(e -> {
            try {
                if (cbMayBay.getSelectedItem() == null) throw new Exception("Vui lòng chọn máy bay!");
                String maMB = cbMayBay.getSelectedItem().toString().split(" - ")[0].trim();
                
                String tienTo = txtTienTo.getText().trim();
                if (tienTo.isEmpty()) throw new Exception("Vui lòng nhập tiền tố (VD: A, B)!");
                
                int soLuong = 0;
                try { soLuong = Integer.parseInt(txtSoLuong.getText().trim()); } 
                catch (Exception ex) { throw new Exception("Số lượng phải là số nguyên dương!"); }
                
                BigDecimal gia = BigDecimal.ZERO;
                try { gia = new BigDecimal(txtGiaGhe.getText().trim()); } 
                catch (Exception ex) { throw new Exception("Giá ghế phải là số hợp lệ!"); }

                // Gọi hàm tạo hàng loạt trong BUS
                gheMayBayBUS.taoGheHangLoat(maMB, soLuong, tienTo, gia);
                
                JOptionPane.showMessageDialog(this, "Đã sinh hàng loạt ghế thành công!");
                btnLamMoi.doClick(); 
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 4. Nút Sửa (Cập nhật 1 ghế đã chọn)
        btnSua.addActionListener(e -> {
            try {
                String maGhe = txtMaGhe.getText().trim();
                if (maGhe.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 ghế trên bảng để cập nhật!");
                    return;
                }
                
                String maMB = cbMayBay.getSelectedItem().toString().split(" - ")[0].trim();
                String soGhe = txtSoGhe.getText().trim();
                BigDecimal gia = new BigDecimal(txtGiaGhe.getText().trim());
                
                GheMayBay ghe = new GheMayBay(maGhe, maMB, soGhe, gia);
                
                if (gheMayBayBUS.capNhatGhe(ghe)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin ghế thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 5. Nút Xóa (Xóa 1 ghế đã chọn)
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 ghế cần xóa!");
                return;
            }
            String maGhe = txtMaGhe.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ghế " + maGhe + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (gheMayBayBUS.xoaGhe(maGhe)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Ghế này có thể đang có vé đặt.", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 6. Nút Làm Mới
        btnLamMoi.addActionListener(e -> {
            txtMaGhe.setText("");
            txtSoGhe.setText("");
            txtTienTo.setText("");
            txtSoLuong.setText("");
            txtGiaGhe.setText("");
            txtTimKiem.setText("");
            
            txtTienTo.setEnabled(true);
            txtSoLuong.setEnabled(true);
            
            if (cbMayBay.getItemCount() > 0) {
                cbMayBay.setSelectedIndex(0);
                // Trigger lại action để tự điền số lượng cho index 0
                cbMayBay.getActionListeners()[0].actionPerformed(null);
            }
            
            table.clearSelection();
            loadDataToTable(gheMayBayBUS.getAllGheMayBay());
        });

        // 7. Nút Tìm Kiếm
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            ArrayList<GheMayBay> ketQua = gheMayBayBUS.timKiemGhe(keyword);
            loadDataToTable(ketQua);
        });
    }
}