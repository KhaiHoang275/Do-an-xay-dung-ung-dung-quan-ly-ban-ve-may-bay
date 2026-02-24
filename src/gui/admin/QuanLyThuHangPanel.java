package gui.admin;

import bll.ThuHangBUS;
import model.ThuHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class QuanLyThuHangPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHang;
    private JTextField txtTenHang;
    private JTextField txtDiemToiThieu;
    private JTextField txtTiLeGiam;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnThoat;
    private ThuHangBUS bus;

    public QuanLyThuHangPanel() {
        bus = new ThuHangBUS();
        setLayout(new BorderLayout());
        initComponents();
        loadDataTable();
    }

    private void initComponents() {
        // Thử set Look and Feel để giao diện đẹp hơn
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Phần tiêu đề
        JLabel lblTitle = new JLabel("\uD83D\uDC68\u200D QUẢN LÝ THỨ HẠNG THÀNH VIÊN ✈\uFE0F");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(255, 215, 0));
        lblTitle.setBackground(new Color(215, 25, 32));
        lblTitle.setOpaque(true);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Phần bảng - Chiếm nhiều không gian hơn
        String[] columns = {"Mã hạng", "Tên hạng", "Điểm tối thiểu", "Tỉ lệ giảm (%)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setBackground(Color.DARK_GRAY);
        table.getTableHeader().setForeground(new Color(33, 33, 33));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(255, 255, 224)); // Vàng nhạt
        table.setAutoCreateRowSorter(true);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Font lớn hơn cho bảng

        // Sắp xếp theo điểm tối thiểu tăng dần mặc định
        table.getRowSorter().toggleSortOrder(2); // Cột 2: Điểm tối thiểu

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 300)); // Tăng chiều cao bảng
        add(scrollPane, BorderLayout.CENTER);

        // Phần form - Làm lớn hơn
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel lblMaHang = new JLabel("Mã hạng:");
        lblMaHang.setFont(new Font("SansSerif", Font.BOLD, 16));
        formPanel.add(lblMaHang);
        txtMaHang = new JTextField();
        txtMaHang.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(txtMaHang);

        JLabel lblTenHang = new JLabel("Tên hạng:");
        lblTenHang.setFont(new Font("SansSerif", Font.BOLD, 16));
        formPanel.add(lblTenHang);
        txtTenHang = new JTextField();
        txtTenHang.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(txtTenHang);

        JLabel lblDiem = new JLabel("Điểm tối thiểu:");
        lblDiem.setFont(new Font("SansSerif", Font.BOLD, 16));
        formPanel.add(lblDiem);
        txtDiemToiThieu = new JTextField();
        txtDiemToiThieu.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(txtDiemToiThieu);

        JLabel lblTiLe = new JLabel("Tỉ lệ giảm (%):");
        lblTiLe.setFont(new Font("SansSerif", Font.BOLD, 16));
        formPanel.add(lblTiLe);
        txtTiLeGiam = new JTextField();
        txtTiLeGiam.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(txtTiLeGiam);

        // Phần nút - Làm nút to hơn
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnThem = new JButton("Thêm");
        btnThem.setBackground(new Color(215, 25, 32)); // Đỏ
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnThem.setPreferredSize(new Dimension(120, 40)); // To hơn
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addThuHang();
            }
        });
        buttonPanel.add(btnThem);

        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.setBackground(Color.YELLOW);
        btnCapNhat.setForeground(Color.BLACK);
        btnCapNhat.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCapNhat.setPreferredSize(new Dimension(120, 40));
        btnCapNhat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateThuHang();
            }
        });
        buttonPanel.add(btnCapNhat);

        btnXoa = new JButton("Xóa");
        btnXoa.setBackground(new Color(46, 204, 113));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnXoa.setPreferredSize(new Dimension(120, 40));
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteThuHang();
            }
        });
        buttonPanel.add(btnXoa);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setBackground(new Color(115, 89, 182));
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLamMoi.setPreferredSize(new Dimension(120, 40));
        btnLamMoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        buttonPanel.add(btnLamMoi);

        btnThoat = new JButton("Thoát");
        btnThoat.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnThoat.setPreferredSize(new Dimension(120, 40));
        btnThoat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(QuanLyThuHangPanel.this);
                if (window != null) {
                    window.dispose();
                }
            }
        });
        buttonPanel.add(btnThoat);

        // Panel south chứa form và button - Giới hạn chiều cao để cân đối
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        southPanel.setPreferredSize(new Dimension(0, 250)); // Điều chỉnh chiều cao south
        add(southPanel, BorderLayout.SOUTH);

        // Sự kiện click bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
                txtMaHang.setEditable(false); // Không cho sửa mã khi cập nhật
            }
        });

        // Phân quyền: Giả sử có biến role
        String role = "Admin"; // Thay bằng logic thực tế
        if (!"Admin".equals(role)) {
            btnThem.setEnabled(false);
            btnCapNhat.setEnabled(false);
            btnXoa.setEnabled(false);
            btnLamMoi.setEnabled(false);
        }
    }

    public void loadDataTable() {
        tableModel.setRowCount(0);
        List<ThuHang> list = bus.getAll();
        for (ThuHang th : list) {
            tableModel.addRow(new Object[]{
                    th.getMaThuHang(),
                    th.getTenThuHang(),
                    th.getDiemToiThieu(),
                    th.getTiLeGiam()
            });
        }
    }

    public void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaHang.setText((String) tableModel.getValueAt(row, 0));
            txtTenHang.setText((String) tableModel.getValueAt(row, 1));
            txtDiemToiThieu.setText(String.valueOf(tableModel.getValueAt(row, 2)));
            txtTiLeGiam.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        }
    }

    public void clearForm() {
        txtMaHang.setText("");
        txtTenHang.setText("");
        txtDiemToiThieu.setText("");
        txtTiLeGiam.setText("");
        table.clearSelection();
        txtMaHang.setEditable(true);
    }

    private boolean validateInput() {
        if (txtMaHang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã hạng không được trống");
            return false;
        }
        if (txtTenHang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên hạng không được trống");
            return false;
        }
        try {
            int diem = Integer.parseInt(txtDiemToiThieu.getText().trim());
            if (diem < 0) {
                JOptionPane.showMessageDialog(this, "Điểm tối thiểu phải >= 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm tối thiểu phải là số nguyên");
            return false;
        }
        try {
            double tiLe = Double.parseDouble(txtTiLeGiam.getText().trim());
            if (tiLe < 0 || tiLe > 100) {
                JOptionPane.showMessageDialog(this, "Tỉ lệ giảm phải >= 0 và <= 100");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tỉ lệ giảm phải là số thực");
            return false;
        }
        // Kiểm tra mã không trùng khi thêm mới
        if (txtMaHang.isEditable()) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (txtMaHang.getText().equals(tableModel.getValueAt(i, 0))) {
                    JOptionPane.showMessageDialog(this, "Mã hạng đã tồn tại");
                    return false;
                }
            }
        }
        return true;
    }

    public void addThuHang() {
        if (validateInput()) {
            ThuHang th = new ThuHang();
            th.setMaThuHang(txtMaHang.getText().trim());
            th.setTenThuHang(txtTenHang.getText().trim());
            th.setDiemToiThieu(Integer.parseInt(txtDiemToiThieu.getText().trim()));
            th.setTiLeGiam(Double.parseDouble(txtTiLeGiam.getText().trim()));
            bus.insert(th);
            loadDataTable();
            clearForm();
        }
    }

    public void updateThuHang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để cập nhật");
            return;
        }
        if (validateInput()) {
            ThuHang th = new ThuHang();
            th.setMaThuHang(txtMaHang.getText().trim());
            th.setTenThuHang(txtTenHang.getText().trim());
            th.setDiemToiThieu(Integer.parseInt(txtDiemToiThieu.getText().trim()));
            th.setTiLeGiam(Double.parseDouble(txtTiLeGiam.getText().trim()));
            bus.update(th);
            loadDataTable();
            clearForm();
        }
    }

    public void deleteThuHang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String ma = (String) tableModel.getValueAt(row, 0);
            bus.delete(ma);
            loadDataTable();
            clearForm();
        }
    }
}