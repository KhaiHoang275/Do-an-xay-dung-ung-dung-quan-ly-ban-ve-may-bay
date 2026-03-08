package gui.admin;

import bll.MayBayBUS;
import model.MayBay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class QuanLyMayBayPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaMayBay, txtSoHieu, txtHangSanXuat, txtTimKiem;
    private JSpinner spinnerTongSoGhe;
    private JComboBox<String> cboTrangThai, cboHienThi;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTimKiem;

    private MayBayBUS mayBayBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 162, 158);

    public QuanLyMayBayPanel() {
        mayBayBUS = new MayBayBUS();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(initForm(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);

        setupActions();
        loadDataToTable(mayBayBUS.getAllMayBay());
    }

    private JPanel initForm() {
        JPanel panelNorth = new JPanel(new BorderLayout(10, 10));
        panelNorth.setBackground(BG_MAIN);

        JLabel lblTitle = new JLabel("QUẢN LÝ MÁY BAY", JLabel.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panelNorth.add(lblTitle, BorderLayout.NORTH);

        // Khai báo 3 hàng, 4 cột (chứa chính xác 12 thành phần)
        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 15, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin Máy bay", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // --- HÀNG 1 ---
        pnlForm.add(new JLabel("Mã Máy Bay (*):"));
        txtMaMayBay = new JTextField();
        pnlForm.add(txtMaMayBay);

        pnlForm.add(new JLabel("Số Hiệu (*):"));
        txtSoHieu = new JTextField();
        pnlForm.add(txtSoHieu);

        // --- HÀNG 2 ---
        pnlForm.add(new JLabel("Hãng Sản Xuất:"));
        txtHangSanXuat = new JTextField();
        pnlForm.add(txtHangSanXuat);

        pnlForm.add(new JLabel("Tổng Số Ghế:"));
        spinnerTongSoGhe = new JSpinner(new SpinnerNumberModel(100, 1, 1000, 1));
        pnlForm.add(spinnerTongSoGhe);
        
        // --- HÀNG 3 ---
        pnlForm.add(new JLabel("Trạng Thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        pnlForm.add(cboTrangThai);

        // Đệm đúng 2 ô trống để lấp đầy 12 slots của hàng 3 (10 ô dữ liệu + 2 ô trống = 12)
        pnlForm.add(new JLabel(""));
        pnlForm.add(new JLabel(""));

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlActions.setBackground(BG_MAIN);

        btnThem = createButton("Thêm", BTN_ADD);
        btnCapNhat = createButton("Cập nhật", BTN_UPDATE);
        btnXoa = createButton("Xóa", BTN_DELETE);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);

        pnlActions.add(btnThem);
        pnlActions.add(btnCapNhat);
        pnlActions.add(btnXoa);
        pnlActions.add(btnLamMoi);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(BG_MAIN);
        
        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setForeground(Color.WHITE);
        
        pnlSearch.add(new JLabel("Chế độ xem: "));
        pnlSearch.add(cboHienThi);
        pnlSearch.add(new JLabel(" | Tìm Mã/Số hiệu: "));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        JPanel pnlCenterOfNorth = new JPanel(new BorderLayout());
        pnlCenterOfNorth.setOpaque(false);
        pnlCenterOfNorth.add(pnlForm, BorderLayout.CENTER);
        pnlCenterOfNorth.add(pnlActions, BorderLayout.SOUTH);

        panelNorth.add(pnlCenterOfNorth, BorderLayout.CENTER);
        panelNorth.add(pnlSearch, BorderLayout.SOUTH);

        return panelNorth;
    }

    private JPanel initTable() {
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] cols = {"Mã Máy Bay", "Số Hiệu", "Hãng Sản Xuất", "Tổng Số Ghế", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(241, 245, 249));
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        return pnlTable;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDataToTable(ArrayList<MayBay> list) {
        tableModel.setRowCount(0);
        for (MayBay mb : list) {
            Object[] row = { 
                mb.getMaMayBay(), 
                mb.getSoHieu(), 
                mb.getHangSanXuat(), 
                mb.getTongSoGhe(),
                mb.getTrangThai().getValue()
            };
            tableModel.addRow(row);
        }
    }

    private void setupActions() {
        cboHienThi.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(mayBayBUS.getMayBayTrongThungRac());
                btnXoa.setEnabled(false);
            } else {
                loadDataToTable(mayBayBUS.getAllMayBay());
                btnXoa.setEnabled(true);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaMayBay.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaMayBay.setEditable(false);
                    txtSoHieu.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                    txtHangSanXuat.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                    
                    try {
                        int soGhe = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
                        spinnerTongSoGhe.setValue(soGhe);
                    } catch (Exception ex) {
                        spinnerTongSoGhe.setValue(100);
                    }
                    
                    cboTrangThai.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaMayBay.setText("");
            txtMaMayBay.setEditable(true);
            txtSoHieu.setText("");
            txtHangSanXuat.setText("");
            spinnerTongSoGhe.setValue(100);
            cboTrangThai.setSelectedIndex(0);
            txtTimKiem.setText("");
            table.clearSelection();
            
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(mayBayBUS.getMayBayTrongThungRac());
            } else {
                loadDataToTable(mayBayBUS.getAllMayBay());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                int tongSoGhe = (Integer) spinnerTongSoGhe.getValue();
                MayBay.TrangThai tt = MayBay.TrangThai.fromString(cboTrangThai.getSelectedItem().toString());
                MayBay mb = new MayBay(txtMaMayBay.getText().trim(), txtSoHieu.getText().trim(), txtHangSanXuat.getText().trim(), tongSoGhe, tt);
                String result = mayBayBUS.themMayBay(mb);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnCapNhat.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn máy bay cần sửa!"); return; }
            try {
                int tongSoGhe = (Integer) spinnerTongSoGhe.getValue();
                MayBay.TrangThai tt = MayBay.TrangThai.fromString(cboTrangThai.getSelectedItem().toString());
                MayBay mb = new MayBay(txtMaMayBay.getText().trim(), txtSoHieu.getText().trim(), txtHangSanXuat.getText().trim(), tongSoGhe, tt);
                String result = mayBayBUS.suaMayBay(mb);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn máy bay cần đưa vào thùng rác!"); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đưa máy bay này vào thùng rác?", "Xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String result = mayBayBUS.xoaMayBay(txtMaMayBay.getText().trim());
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thùng rác")) btnLamMoi.doClick();
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            ArrayList<MayBay> ketQua = mayBayBUS.timKiemMayBay(keyword, isTrash);
            loadDataToTable(ketQua);
        });
    }
}