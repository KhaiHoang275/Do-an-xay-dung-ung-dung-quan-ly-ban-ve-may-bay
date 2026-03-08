package gui.admin;

import bll.SanBayBUS;
import model.SanBay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class QuanLySanBayPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaSanBay, txtTenSanBay, txtQuocGia, txtThanhPho, txtTimKiem;
    private JComboBox<String> cboTrangThai, cboHienThi;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTimKiem;

    private SanBayBUS sanBayBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 162, 158);

    public QuanLySanBayPanel() {
        sanBayBUS = new SanBayBUS();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(initForm(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);

        setupActions();
        loadDataToTable(sanBayBUS.getAllSanBay());
    }

    private JPanel initForm() {
        JPanel panelNorth = new JPanel(new BorderLayout(10, 10));
        panelNorth.setBackground(BG_MAIN);

        JLabel lblTitle = new JLabel("QUẢN LÝ SÂN BAY", JLabel.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panelNorth.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 15, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin Sân bay", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        pnlForm.add(new JLabel("Mã Sân Bay (*):"));
        txtMaSanBay = new JTextField();
        pnlForm.add(txtMaSanBay);

        pnlForm.add(new JLabel("Tên Sân Bay (*):"));
        txtTenSanBay = new JTextField();
        pnlForm.add(txtTenSanBay);

        pnlForm.add(new JLabel("Quốc Gia:"));
        txtQuocGia = new JTextField();
        pnlForm.add(txtQuocGia);

        pnlForm.add(new JLabel("Thành Phố:"));
        txtThanhPho = new JTextField();
        pnlForm.add(txtThanhPho);

        pnlForm.add(new JLabel("Trạng Thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        pnlForm.add(cboTrangThai);
        
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
        pnlSearch.add(new JLabel(" | Tìm Tên/Mã SB: "));
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

        String[] cols = {"Mã Sân Bay", "Tên Sân Bay", "Quốc Gia", "Thành Phố", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        
    
        table.setSelectionBackground(new Color(28, 48, 96)); 
        table.setSelectionForeground(Color.WHITE);
      
        
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
        
   
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   
        
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

    private void loadDataToTable(ArrayList<SanBay> list) {
        tableModel.setRowCount(0);
        for (SanBay sb : list) {
            Object[] row = { 
                sb.getMaSanBay(), 
                sb.getTenSanBay(), 
                sb.getQuocGia(), 
                sb.getThanhPho(), 
                sb.getTrangThai().getValue() 
            };
            tableModel.addRow(row);
        }
    }

    private void setupActions() {
        cboHienThi.addActionListener(e -> {
            txtTimKiem.setText("");
            btnLamMoi.doClick();
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaSanBay.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaSanBay.setEditable(false);
                    txtTenSanBay.setText(tableModel.getValueAt(row, 1).toString());
                    txtQuocGia.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                    txtThanhPho.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                    cboTrangThai.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaSanBay.setText("");
            txtMaSanBay.setEditable(true);
            txtTenSanBay.setText("");
            txtQuocGia.setText("");
            txtThanhPho.setText("");
            cboTrangThai.setSelectedIndex(0);
            txtTimKiem.setText("");
            table.clearSelection();
            
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(sanBayBUS.getSanBayTrongThungRac());
                btnXoa.setText("Khôi phục");
                btnXoa.setBackground(BTN_ADD);
            } else {
                loadDataToTable(sanBayBUS.getAllSanBay());
                btnXoa.setText("Xóa");
                btnXoa.setBackground(BTN_DELETE);
            }
        });

        btnThem.addActionListener(e -> {
            if(!validateInput()) return;
            try {
                SanBay.TrangThai tt = SanBay.TrangThai.fromString(cboTrangThai.getSelectedItem().toString());
                SanBay sb = new SanBay(txtMaSanBay.getText().trim().toUpperCase(), txtTenSanBay.getText().trim(), txtQuocGia.getText().trim(), txtThanhPho.getText().trim(), tt);
                String result = sanBayBUS.themSanBay(sb);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage()); }
        });

        btnCapNhat.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sân bay từ danh sách!"); return; }
            if(!validateInput()) return;
            try {
                SanBay.TrangThai tt = SanBay.TrangThai.fromString(cboTrangThai.getSelectedItem().toString());
                SanBay sb = new SanBay(txtMaSanBay.getText().trim().toUpperCase(), txtTenSanBay.getText().trim(), txtQuocGia.getText().trim(), txtThanhPho.getText().trim(), tt);
                String result = sanBayBUS.suaSanBay(sb);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage()); }
        });

        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sân bay!"); return; }
            String ma = txtMaSanBay.getText().trim();
            
            if (cboHienThi.getSelectedIndex() == 1) { 
                SanBay sb = sanBayBUS.getSanBayTrongThungRac().stream().filter(s -> s.getMaSanBay().equals(ma)).findFirst().orElse(null);
                if (sb != null) {
                    sb.setTrangThai(SanBay.TrangThai.HOAT_DONG);
                    sanBayBUS.suaSanBay(sb);
                    JOptionPane.showMessageDialog(this, "Khôi phục sân bay thành công!");
                    btnLamMoi.doClick();
                }
            } else { 
                int confirm = JOptionPane.showConfirmDialog(this, "Đưa sân bay này vào thùng rác?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String result = sanBayBUS.xoaSanBay(ma);
                    JOptionPane.showMessageDialog(this, result);
                    btnLamMoi.doClick();
                }
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            loadDataToTable(sanBayBUS.timKiemSanBay(keyword, isTrash));
        });

        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) btnTimKiem.doClick();
            }
        });
    }

    private boolean validateInput() {
        String ma = txtMaSanBay.getText().trim();
        String ten = txtTenSanBay.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã và Tên sân bay không được để trống!");
            return false;
        }
        if (ma.length() != 3) {
            JOptionPane.showMessageDialog(this, "Mã sân bay (IATA) phải đúng 3 ký tự (VD: SGN, HAN)!");
            return false;
        }
        return true;
    }
}