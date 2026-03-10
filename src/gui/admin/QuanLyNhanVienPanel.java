package gui.admin;

import bll.NhanVienBUS;
import model.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class QuanLyNhanVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaNV, txtMaNguoiDung, txtHoTen, txtChucVu, txtTimKiem;
    private JSpinner spinnerNgayVaoLam;
    private JComboBox<String> cboTrangThai, cboHienThi;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTimKiem;

    private NhanVienBUS nhanVienBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 162, 158);

    public QuanLyNhanVienPanel() {
        nhanVienBUS = new NhanVienBUS();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(initForm(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);

        setupActions();
        loadDataToTable(nhanVienBUS.getAllNhanVien());
    }

    private JPanel initForm() {
        JPanel panelNorth = new JPanel(new BorderLayout(10, 10));
        panelNorth.setBackground(BG_MAIN);

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panelNorth.add(lblTitle, BorderLayout.NORTH);

  
        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 15, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin Nhân viên", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(15, 15, 15, 15)
        ));

    
        pnlForm.add(new JLabel("Mã Nhân Viên (*):"));
        txtMaNV = new JTextField();
        pnlForm.add(txtMaNV);

        pnlForm.add(new JLabel("Mã Người Dùng:"));
        txtMaNguoiDung = new JTextField();
        pnlForm.add(txtMaNguoiDung);

  
        pnlForm.add(new JLabel("Họ và Tên (*):"));
        txtHoTen = new JTextField();
        pnlForm.add(txtHoTen);

        pnlForm.add(new JLabel("Chức Vụ:"));
        txtChucVu = new JTextField();
        pnlForm.add(txtChucVu);

   
        pnlForm.add(new JLabel("Ngày Vào Làm:"));
        spinnerNgayVaoLam = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerNgayVaoLam, "dd/MM/yyyy");
        spinnerNgayVaoLam.setEditor(dateEditor);
        pnlForm.add(spinnerNgayVaoLam);

        pnlForm.add(new JLabel("Trạng Thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        pnlForm.add(cboTrangThai);
        


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
        pnlSearch.add(new JLabel(" | Tìm Mã NV/Họ Tên: "));
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

                String[] cols = {"Mã NV", "Mã User", "Họ Tên", "Chức Vụ", "Ngày Vào Làm", "Trạng Thái"};
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

    private void loadDataToTable(ArrayList<NhanVien> list) {
        tableModel.setRowCount(0);
        for (NhanVien nv : list) {
            Object[] row = { 
                nv.getMaNV(), 
                nv.getMaNguoiDung(), 
                nv.getHoTen(), 
                nv.getChucVu(), 
                nv.getNgayVaoLam(),
                nv.getTrangThaiLamViec().getValue()
            };
            tableModel.addRow(row);
        }
    }

    private void setupActions() {
        cboHienThi.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(nhanVienBUS.getNhanVienTrongThungRac());
                btnXoa.setEnabled(false);
            } else {
                loadDataToTable(nhanVienBUS.getAllNhanVien());
                btnXoa.setEnabled(true);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaNV.setEditable(false);
                    txtMaNguoiDung.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                    txtHoTen.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                    txtChucVu.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                    
                    Object dateObj = tableModel.getValueAt(row, 4);
                    if (dateObj instanceof LocalDate) {
                        Date date = Date.from(((LocalDate) dateObj).atStartOfDay(ZoneId.systemDefault()).toInstant());
                        spinnerNgayVaoLam.setValue(date);
                    }
                    
                    cboTrangThai.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaNV.setText("");
            txtMaNV.setEditable(true);
            txtMaNguoiDung.setText("");
            txtHoTen.setText("");
            txtChucVu.setText("");
            spinnerNgayVaoLam.setValue(new Date());
            cboTrangThai.setSelectedIndex(0);
            txtTimKiem.setText("");
            table.clearSelection();
            
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(nhanVienBUS.getNhanVienTrongThungRac());
            } else {
                loadDataToTable(nhanVienBUS.getAllNhanVien());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                Date d = (Date) spinnerNgayVaoLam.getValue();
                LocalDate ngayVaoLam = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                NhanVien.TrangThai tt = NhanVien.TrangThai.fromString(cboTrangThai.getSelectedItem().toString());
                
                NhanVien nv = new NhanVien(txtMaNV.getText().trim(), txtMaNguoiDung.getText().trim(), txtHoTen.getText().trim(), txtChucVu.getText().trim(), ngayVaoLam, tt);
                String result = nhanVienBUS.themNhanVien(nv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnCapNhat.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Chọn nhân viên cần sửa!"); return; }
            try {
                Date d = (Date) spinnerNgayVaoLam.getValue();
                LocalDate ngayVaoLam = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                NhanVien.TrangThai tt = NhanVien.TrangThai.fromString(cboTrangThai.getSelectedItem().toString());
                
                NhanVien nv = new NhanVien(txtMaNV.getText().trim(), txtMaNguoiDung.getText().trim(), txtHoTen.getText().trim(), txtChucVu.getText().trim(), ngayVaoLam, tt);
                String result = nhanVienBUS.suaNhanVien(nv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Chọn nhân viên cần đưa vào thùng rác!"); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đưa nhân viên này vào thùng rác?", "Xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String result = nhanVienBUS.xoaNhanVien(txtMaNV.getText().trim());
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thùng rác")) btnLamMoi.doClick();
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            ArrayList<NhanVien> ketQua = nhanVienBUS.timKiemNhanVien(keyword, isTrash);
            loadDataToTable(ketQua);
        });
    }
}