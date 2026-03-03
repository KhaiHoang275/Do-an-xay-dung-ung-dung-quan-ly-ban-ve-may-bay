package gui.admin;

import bll.GheMayBayBUS;
import bll.MayBayBUS;
import model.GheMayBay;
import model.MayBay;
import model.TrangThaiGhe; 

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

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbMayBay, cbTrangThai, cboHienThi;
    private JTextField txtGiaGhe, txtTienTo, txtSoLuong, txtMaGhe, txtSoGhe, txtTimKiem;
    private JButton btnTaoHangLoat, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private GheMayBayBUS gheMayBayBUS;
    private MayBayBUS mayBayBUS;

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
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        ImageIcon titleIcon = null;
        try {
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/icons8-airline-seat-24.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ GHẾ MÁY BAY", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        txtTimKiem = createTextField();
        txtTimKiem.setPreferredSize(new Dimension(180, 35));
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(130, 35));
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTimKiem.setFocusPainted(false);
        try { setButtonIcon(btnTimKiem, "/resources/icons/icons8-search-24.png", 16); } catch (Exception e){}

        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        cboHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboHienThi.setPreferredSize(new Dimension(130, 35));

        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Chế độ:"));
        searchPanel.add(cboHienThi);

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Mã Ghế", "Thuộc Máy Bay", "Số Ghế", "Giá Cơ Bản (VNĐ)", "Trạng Thái"};
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

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        formPanel.setOpaque(false);

        cbMayBay = new JComboBox<>();
        cbMayBay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGiaGhe = createTextField();
        
        txtTienTo = createTextField();
        txtSoLuong = createTextField();
        
        txtMaGhe = createTextField();
        txtMaGhe.setBackground(new Color(240, 240, 240)); 
        txtMaGhe.setEditable(false);
        txtSoGhe = createTextField();
        
        cbTrangThai = new JComboBox<>(new String[]{"Trống", "Đã đặt", "Bảo trì", "Đã xóa"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(createLabel("Chọn Máy Bay:"));
        formPanel.add(cbMayBay);
        formPanel.add(createLabel("Giá Ghế Cơ Bản (VNĐ):"));
        formPanel.add(txtGiaGhe);
        
        formPanel.add(createLabel("Tiền Tố Dãy (VD: A, B):"));
        formPanel.add(txtTienTo);
        formPanel.add(createLabel("Số Lượng Ghế:"));
        formPanel.add(txtSoLuong);

        formPanel.add(createLabel("Mã Ghế (Chỉ xem):"));
        formPanel.add(txtMaGhe);
        
        JPanel pnlGheStatus = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlGheStatus.setOpaque(false);
        pnlGheStatus.add(txtSoGhe);
        pnlGheStatus.add(cbTrangThai);
        
        formPanel.add(createLabel("Số Ghế & Trạng Thái:"));
        formPanel.add(pnlGheStatus);

        formCard.add(formPanel, BorderLayout.CENTER);

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

    private String hienThiTrangThai(TrangThaiGhe status) {
        if (status == null) return "Trống";
        switch (status) {
            case TRONG: return "Trống";
            case DA_DAT: return "Đã đặt";
            case BAO_TRI: return "Bảo trì";
            case DA_XOA: return "Đã xóa";
            default: return "Trống";
        }
    }

    private TrangThaiGhe layTrangThaiTuUI(String uiValue) {
        if (uiValue == null) return TrangThaiGhe.TRONG;
        switch (uiValue) {
            case "Trống": return TrangThaiGhe.TRONG;
            case "Đã đặt": return TrangThaiGhe.DA_DAT;
            case "Bảo trì": return TrangThaiGhe.BAO_TRI;
            case "Đã xóa": return TrangThaiGhe.DA_XOA;
            default: return TrangThaiGhe.TRONG;
        }
    }

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
                ghe.getGiaGhe(),
                hienThiTrangThai(ghe.getTrangThai())
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
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaGhe.setText(tableModel.getValueAt(row, 0).toString());
                    setComboBoxSelectedByPrefix(cbMayBay, tableModel.getValueAt(row, 1).toString());
                    txtSoGhe.setText(tableModel.getValueAt(row, 2).toString());
                    txtGiaGhe.setText(tableModel.getValueAt(row, 3).toString());
                    
                    String trangThaiTrenBang = tableModel.getValueAt(row, 4).toString();
                    cbTrangThai.setSelectedItem(trangThaiTrenBang);
                    
                    txtTienTo.setText("");
                    txtSoLuong.setText("");
                    txtTienTo.setEnabled(false);
                    txtSoLuong.setEnabled(false);
                }
            }
        });

        cboHienThi.addActionListener(e -> {
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            if (isTrash) {
                btnTaoHangLoat.setEnabled(false);
                btnSua.setText("Khôi phục ghế");
                btnSua.setBackground(new Color(76, 175, 80)); 
                btnXoa.setEnabled(false);
                cbTrangThai.setEnabled(false);
                loadDataToTable(gheMayBayBUS.getGheTrongThungRac());
            } else {
                btnTaoHangLoat.setEnabled(true);
                btnSua.setText("Cập nhật 1 ghế");
                btnSua.setBackground(BTN_UPDATE);
                btnXoa.setEnabled(true);
                cbTrangThai.setEnabled(true);
                loadDataToTable(gheMayBayBUS.getAllGheMayBay());
            }
        });

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

                gheMayBayBUS.taoGheHangLoat(maMB, soLuong, tienTo, gia);
                
                JOptionPane.showMessageDialog(this, "Đã sinh hàng loạt ghế thành công!");
                btnLamMoi.doClick(); 
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                String maGhe = txtMaGhe.getText().trim();
                if (maGhe.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 ghế trên bảng!");
                    return;
                }

                if (cboHienThi.getSelectedIndex() == 1) {
                    if (gheMayBayBUS.khoiPhucGhe(maGhe)) {
                        JOptionPane.showMessageDialog(this, "Khôi phục ghế thành công!");
                        btnLamMoi.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
                
                String maMB = cbMayBay.getSelectedItem().toString().split(" - ")[0].trim();
                String soGhe = txtSoGhe.getText().trim();
                BigDecimal gia = new BigDecimal(txtGiaGhe.getText().trim());
                
                TrangThaiGhe trangThai = layTrangThaiTuUI(cbTrangThai.getSelectedItem().toString());
                
                GheMayBay ghe = new GheMayBay(maGhe, maMB, soGhe, gia, trangThai);
                
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
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaGhe.setText("");
            txtSoGhe.setText("");
            txtTienTo.setText("");
            txtSoLuong.setText("");
            txtGiaGhe.setText("");
            txtTimKiem.setText("");
            cbTrangThai.setSelectedIndex(0); 
            
            txtTienTo.setEnabled(true);
            txtSoLuong.setEnabled(true);
            
            if (cbMayBay.getItemCount() > 0) {
                cbMayBay.setSelectedIndex(0);
                cbMayBay.getActionListeners()[0].actionPerformed(null);
            }
            
            table.clearSelection();
            
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(gheMayBayBUS.getGheTrongThungRac());
            } else {
                loadDataToTable(gheMayBayBUS.getAllGheMayBay());
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            ArrayList<GheMayBay> ketQua = gheMayBayBUS.timKiemGhe(keyword, isTrash);
            loadDataToTable(ketQua);
        });
    }
}