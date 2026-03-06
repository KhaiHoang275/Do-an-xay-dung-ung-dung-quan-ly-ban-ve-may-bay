package gui.admin;

import bll.NguoiDungBUS;
import model.NguoiDung;

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
import java.util.ArrayList;

public class QuanLyNguoiDungPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaNguoiDung, txtUsername, txtPassword, txtEmail, txtSdt, txtThanhPho, txtTimKiem;
    private JComboBox<String> cboPhanQuyen, cboTrangThai, cboHienThi;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTimKiem;

    private NguoiDungBUS nguoiDungBUS;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 162, 158);

    public QuanLyNguoiDungPanel() {
        nguoiDungBUS = new NguoiDungBUS();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(initForm(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);

        setupActions();
        loadDataToTable(nguoiDungBUS.getAllNguoiDung());
    }

    private JPanel initForm() {
        JPanel panelNorth = new JPanel(new BorderLayout(10, 10));
        panelNorth.setBackground(BG_MAIN);

        JLabel lblTitle = new JLabel("QUẢN LÝ NGƯỜI DÙNG", JLabel.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panelNorth.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridLayout(4, 4, 15, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin người dùng", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        pnlForm.add(new JLabel("Mã Người Dùng (*):"));
        txtMaNguoiDung = new JTextField();
        pnlForm.add(txtMaNguoiDung);

        pnlForm.add(new JLabel("Username (*):"));
        txtUsername = new JTextField();
        pnlForm.add(txtUsername);

        pnlForm.add(new JLabel("Password (*):"));
        txtPassword = new JTextField();
        pnlForm.add(txtPassword);

        pnlForm.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pnlForm.add(txtEmail);

        pnlForm.add(new JLabel("Số Điện Thoại:"));
        txtSdt = new JTextField();
        pnlForm.add(txtSdt);

        pnlForm.add(new JLabel("Thành Phố:"));
        txtThanhPho = new JTextField();
        pnlForm.add(txtThanhPho);

        pnlForm.add(new JLabel("Phân Quyền:"));
        cboPhanQuyen = new JComboBox<>(new String[]{"KhachHang", "NhanVien", "Admin"});
        pnlForm.add(cboPhanQuyen);

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

        // --- TÌM KIẾM VÀ BỘ LỌC THÙNG RÁC ---
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(BG_MAIN);
        
        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setForeground(Color.WHITE);
        
        pnlSearch.add(new JLabel("Chế độ xem: "));
        pnlSearch.add(cboHienThi);
        pnlSearch.add(new JLabel(" | Tìm Username/Email: "));
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

        String[] cols = {"Mã ND", "Username", "Email", "SĐT", "Phân Quyền", "Ngày Tạo", "Thành Phố", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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

    private void loadDataToTable(ArrayList<NguoiDung> list) {
        tableModel.setRowCount(0);
        for (NguoiDung nd : list) {
            Object[] row = {
                nd.getMaNguoiDung(),
                nd.getUsername(),
                nd.getEmail(),
                nd.getSoDienThoai(),
                nd.getPhanQuyen(),
                nd.getNgayTao(),
                nd.getTrangThaiTK().getValue()
            };
            tableModel.addRow(row);
        }
    }

    private void setupActions() {
        cboHienThi.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(nguoiDungBUS.getNguoiDungTrongThungRac());
                btnXoa.setEnabled(false); 
            } else {
                loadDataToTable(nguoiDungBUS.getAllNguoiDung());
                btnXoa.setEnabled(true);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNguoiDung.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaNguoiDung.setEditable(false);
                    txtUsername.setText(tableModel.getValueAt(row, 1).toString());
                    
                    Object email = tableModel.getValueAt(row, 2);
                    txtEmail.setText(email != null ? email.toString() : "");
                    
                    Object sdt = tableModel.getValueAt(row, 3);
                    txtSdt.setText(sdt != null ? sdt.toString() : "");
                    
                    cboPhanQuyen.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                    
                    Object thanhPho = tableModel.getValueAt(row, 6);
                    txtThanhPho.setText(thanhPho != null ? thanhPho.toString() : "");
                    
                    cboTrangThai.setSelectedItem(tableModel.getValueAt(row, 7).toString());
                    txtPassword.setText("");
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaNguoiDung.setText("");
            txtMaNguoiDung.setEditable(true);
            txtUsername.setText("");
            txtPassword.setText("");
            txtEmail.setText("");
            txtSdt.setText("");
            txtThanhPho.setText("");
            cboPhanQuyen.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
            txtTimKiem.setText("");
            table.clearSelection();
            
        
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(nguoiDungBUS.getNguoiDungTrongThungRac());
            } else {
                loadDataToTable(nguoiDungBUS.getAllNguoiDung());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                NguoiDung nd = getFormInput(false);
                String result = nguoiDungBUS.themNguoiDung(nd);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCapNhat.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần cập nhật!");
                return;
            }
            try {
                NguoiDung nd = getFormInput(true);
                String result = nguoiDungBUS.suaNguoiDung(nd);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Đưa người dùng này vào thùng rác?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String result = nguoiDungBUS.xoaNguoiDung(txtMaNguoiDung.getText());
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("thùng rác")) btnLamMoi.doClick();
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            ArrayList<NguoiDung> ketQua = nguoiDungBUS.timKiemNguoiDung(keyword, isTrash);
            loadDataToTable(ketQua);
        });
    }

    private NguoiDung getFormInput(boolean isUpdate) throws Exception {
        String ma = txtMaNguoiDung.getText().trim();
        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();
        
        if (ma.isEmpty()) throw new Exception("Mã người dùng không được để trống!");
        if (user.isEmpty()) throw new Exception("Tên đăng nhập không được để trống!");
        if (!isUpdate && pass.isEmpty()) throw new Exception("Mật khẩu không được để trống khi thêm mới!");

        NguoiDung nd = new NguoiDung();
        nd.setMaNguoiDung(ma);
        nd.setUsername(user);
        nd.setPassword(pass);
        nd.setEmail(txtEmail.getText().trim());
        nd.setSoDienThoai(txtSdt.getText().trim());
        nd.setPhanQuyen(cboPhanQuyen.getSelectedItem().toString());
        nd.setTrangThaiTK(NguoiDung.TrangThai.fromString(cboTrangThai.getSelectedItem().toString()));
        nd.setNgayTao(LocalDate.now());

        return nd;
    }
}