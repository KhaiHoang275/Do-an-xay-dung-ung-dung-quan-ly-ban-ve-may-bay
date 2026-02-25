package gui.admin;

import bll.ThuHangBUS;
import model.ThuHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class QuanLyThuHangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHang, txtTenHang, txtDiemToiThieu, txtTiLeGiam;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnThoat;
    private ThuHangBUS bus;

    // ====== MÀU HỆ THỐNG ======
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public QuanLyThuHangPanel() {
        bus = new ThuHangBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadDataTable();
    }

    private void initComponents() {

        // ===== TITLE =====
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(
                    new ImageIcon(getClass().getResource("/resources/icons/icons8-ranking-24.png"))
                            .getImage()
                            .getScaledInstance(24, 24, Image.SCALE_SMOOTH)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        JLabel lblTitle = new JLabel("QUẢN LÝ THỨ HẠNG THÀNH VIÊN", icon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);
        add(lblTitle, BorderLayout.NORTH);

        // ===== TABLE CARD =====
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Mã hạng", "Tên hạng", "Điểm tối thiểu", "Tỉ lệ giảm (%)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        // ===== FORM CARD =====
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setOpaque(false);

        txtMaHang = createTextField();
        txtTenHang = createTextField();
        txtDiemToiThieu = createTextField();
        txtTiLeGiam = createTextField();

        formPanel.add(createLabel("Mã hạng"));
        formPanel.add(txtMaHang);
        formPanel.add(createLabel("Tên hạng"));
        formPanel.add(txtTenHang);
        formPanel.add(createLabel("Điểm tối thiểu"));
        formPanel.add(txtDiemToiThieu);
        formPanel.add(createLabel("Tỉ lệ giảm (%)"));
        formPanel.add(txtTiLeGiam);

        formCard.add(formPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnThem = createButton("Thêm", BTN_ADD);
        btnCapNhat = createButton("Cập nhật", BTN_UPDATE);
        btnXoa = createButton("Xóa", BTN_DELETE);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);
        btnThoat = createButton("Thoát", Color.GRAY);

        setButtonIcon(btnThem, "/resources/icons/icons8-add-24.png");
        setButtonIcon(btnCapNhat, "/resources/icons/icons8-update-24.png");
        setButtonIcon(btnXoa, "/resources/icons/icons8-delete-24.png");
        setButtonIcon(btnLamMoi, "/resources/icons/icons8-erase-24.png");
        setButtonIcon(btnThoat, "/resources/icons/icons8-close-24.png");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnThoat);

        formCard.add(buttonPanel, BorderLayout.SOUTH);

        add(formCard, BorderLayout.SOUTH);

        // ===== EVENTS =====
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
                txtMaHang.setEditable(false);
            }
        });

        btnThem.addActionListener(e -> addThuHang());
        btnCapNhat.addActionListener(e -> updateThuHang());
        btnXoa.addActionListener(e -> deleteThuHang());
        btnLamMoi.addActionListener(e -> clearForm());
        btnThoat.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });
    }


    private void setButtonIcon(JButton btn, String path) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaled));
        btn.setIconTextGap(8);
    }
    // ================= UI STYLE METHODS =================

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
        btn.setPreferredSize(new Dimension(120,40));
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
    }

    // ================= LOGIC =================

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

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaHang.setText(tableModel.getValueAt(row, 0).toString());
            txtTenHang.setText(tableModel.getValueAt(row, 1).toString());
            txtDiemToiThieu.setText(tableModel.getValueAt(row, 2).toString());
            txtTiLeGiam.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void clearForm() {
        txtMaHang.setText("");
        txtTenHang.setText("");
        txtDiemToiThieu.setText("");
        txtTiLeGiam.setText("");
        txtMaHang.setEditable(true);
        table.clearSelection();
    }

    private boolean validateInput() {
        if (txtMaHang.getText().trim().isEmpty()) return false;
        if (txtTenHang.getText().trim().isEmpty()) return false;
        return true;
    }

    private void addThuHang() {
        if (!validateInput()) return;

        ThuHang th = new ThuHang();
        th.setMaThuHang(txtMaHang.getText().trim());
        th.setTenThuHang(txtTenHang.getText().trim());
        th.setDiemToiThieu(Integer.parseInt(txtDiemToiThieu.getText().trim()));
        th.setTiLeGiam(Double.parseDouble(txtTiLeGiam.getText().trim()));

        bus.insert(th);
        loadDataTable();
        clearForm();
    }

    private void updateThuHang() {
        if (table.getSelectedRow() == -1) return;

        ThuHang th = new ThuHang();
        th.setMaThuHang(txtMaHang.getText().trim());
        th.setTenThuHang(txtTenHang.getText().trim());
        th.setDiemToiThieu(Integer.parseInt(txtDiemToiThieu.getText().trim()));
        th.setTiLeGiam(Double.parseDouble(txtTiLeGiam.getText().trim()));

        bus.update(th);
        loadDataTable();
        clearForm();
    }

    private void deleteThuHang() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String ma = tableModel.getValueAt(row, 0).toString();
            bus.delete(ma);
            loadDataTable();
            clearForm();
        }
    }
}