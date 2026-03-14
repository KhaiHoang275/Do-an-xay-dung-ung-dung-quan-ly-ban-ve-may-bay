package gui.admin;

import bll.DichVuBoSungBUS;
import model.DichVuBoSung;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class DichVuBoSungPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaDichVu, txtTenDichVu, txtDonGia, txtTimKiem;
    private JComboBox<String> cboHienThi;
    private JComboBox<String> cboTrangThaiForm;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private DichVuBoSungBUS dichVuBUS;

    // Bảng màu chuẩn dự án
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public DichVuBoSungPanel() {
        dichVuBUS = new DichVuBoSungBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataToTable(dichVuBUS.docDanhSachDichVu());
    }

    private void initComponents() {
        // ================= HEADER & SEARCH =================
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ DỊCH VỤ BỔ SUNG", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);
        try {
            ImageIcon titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png"))
                    .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            lblTitle.setIcon(titleIcon);
        } catch (Exception e) {}

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        txtTimKiem = createTextField();
        txtTimKiem.setPreferredSize(new Dimension(200, 35));

        // NÚT TÌM KIẾM BO TRÒN
        btnTimKiem = createRoundedButton("Tìm kiếm", TABLE_HEADER, "/resources/icons/icons8-search-24.png", 16);
        btnTimKiem.setPreferredSize(new Dimension(100, 35));

        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        cboHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboHienThi.setPreferredSize(new Dimension(150, 35));
        cboTrangThaiForm = new JComboBox<>(new String[]{"Đang hoạt động", "Ngừng hoạt động"});
        cboTrangThaiForm.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Chế độ:"));
        searchPanel.add(cboHienThi);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Đơn Giá (VNĐ)", "Trạng thái"}; // Thêm cột thứ 4
        tableModel = new DefaultTableModel(columns, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // ================= FORM =================
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 5  , 15, 15));
        formPanel.setOpaque(false);

        txtMaDichVu = createTextField();
        txtTenDichVu = createTextField();
        txtDonGia = createTextField();

        formPanel.add(createLabel("Mã Dịch Vụ (*):"));
        formPanel.add(txtMaDichVu);
        formPanel.add(createLabel("Tên Dịch Vụ (*):"));
        formPanel.add(txtTenDichVu);

        formPanel.add(createLabel("Đơn Giá (VNĐ):"));
        formPanel.add(txtDonGia);
        formPanel.add(createLabel("Trạng thái:"));
        formPanel.add(cboTrangThaiForm);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(formPanel, BorderLayout.NORTH);

        formCard.add(formWrapper, BorderLayout.CENTER);

        // ================= BUTTONS CÁC NÚT BO TRÒN =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        // Gọi hàm tạo nút bo tròn siêu cấp
        btnThem = createRoundedButton("Thêm", BTN_ADD, "/resources/icons/icons8-add-24.png", 20);
        btnSua = createRoundedButton("Cập nhật", BTN_UPDATE, "/resources/icons/icons8-update-24.png", 20);
        btnXoa = createRoundedButton("Xóa", BTN_DELETE, "/resources/icons/icons8-delete-24.png", 20);
        btnLamMoi = createRoundedButton("Làm mới", BTN_REFRESH, "/resources/icons/icons8-erase-24.png", 20);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        formCard.add(buttonPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.SOUTH);

        setupListeners();
    }

    // =========================================================================
    // HÀM TẠO NÚT BO TRÒN (KHẮC PHỤC TRIỆT ĐỂ LỖI TRẮNG BÓC)
    // =========================================================================
    private JButton createRoundedButton(String text, Color bgColor, String iconPath, int iconSize) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Xử lý hiệu ứng Hover cực mượt
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter()); // Sáng lên nhẹ khi đưa chuột vào
                } else {
                    g2.setColor(bgColor);
                }
                
                // Vẽ hình chữ nhật bo góc 15px
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g); // Vẽ chữ và Icon đè lên trên nền
            }
        };
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // QUAN TRỌNG: Tắt nền mặc định gây lỗi của Java
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Nạp icon
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image scaled = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaled));
                btn.setIconTextGap(8);
            } catch (Exception e) {}
        }
        return btn;
    }

    // Các hàm helper giao diện khác giữ nguyên form chuẩn
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

  private void styleTable() {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(45, 72, 140));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(TABLE_HEADER); // Ép nền màu tối
        headerRenderer.setForeground(Color.WHITE);  // Ép chữ màu trắng
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER); // Căn giữa chữ
        
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        header.setDefaultRenderer(headerRenderer);

        for (int i = 0; i < table.getColumnCount(); i++) {
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer(); 
            cellRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        } 
    }

        private void loadDataToTable(List<DichVuBoSung> list) {
        tableModel.setRowCount(0);
        if (list == null) return;
        for (DichVuBoSung dv : list) {
            tableModel.addRow(new Object[]{
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    String.format("%,.0f", dv.getDonGia()),
                    dv.getTrangThai() // THÊM DÒNG NÀY
            });
        }
    }

    private void setupListeners() {
        table.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaDichVu.setText(tableModel.getValueAt(row, 0).toString());
                txtTenDichVu.setText(tableModel.getValueAt(row, 1).toString());
                txtDonGia.setText(tableModel.getValueAt(row, 2).toString().replace(",", ""));
                
                // Hiển thị đúng trạng thái lên ComboBox
                String tt = tableModel.getValueAt(row, 3).toString();
                cboTrangThaiForm.setSelectedItem(tt);
                
                txtMaDichVu.setEditable(false);
            }
        }
    });

        cboHienThi.addActionListener(e -> {
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            if (isTrash) {
                btnThem.setEnabled(false);
                btnSua.setEnabled(false); // Vì DB không có Trạng thái nên ko có khái niệm khôi phục
                btnXoa.setEnabled(false);
                tableModel.setRowCount(0); // Rỗng vì chưa có trong DB
            } else {
                btnThem.setEnabled(true);
                btnSua.setEnabled(true);
                btnXoa.setEnabled(true);
                loadDataToTable(dichVuBUS.docDanhSachDichVu());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                DichVuBoSung dv = getFormInput();
                String result = dichVuBUS.themDichVu(dv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                if (txtMaDichVu.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ trên bảng!");
                    return;
                }
                DichVuBoSung dv = getFormInput();
                String result = dichVuBUS.capNhatDichVu(dv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!");
                return;
            }
            String maDV = txtMaDichVu.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ " + maDV + " không?\nHành động này xóa vĩnh viễn khỏi CSDL!", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = dichVuBUS.xoaDichVu(maDV);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaDichVu.setText("");
            txtTenDichVu.setText("");
            txtDonGia.setText("");
            txtTimKiem.setText("");
            txtMaDichVu.setEditable(true);
            table.clearSelection();

            if (cboHienThi.getSelectedIndex() == 0) {
                loadDataToTable(dichVuBUS.docDanhSachDichVu());
            } else {
                tableModel.setRowCount(0);
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            List<DichVuBoSung> allData = dichVuBUS.docDanhSachDichVu();
            
            List<DichVuBoSung> ketQua = allData.stream().filter(dv -> 
                dv.getMaDichVu().toLowerCase().contains(keyword) || 
                dv.getTenDichVu().toLowerCase().contains(keyword)
            ).collect(Collectors.toList());
            
            loadDataToTable(ketQua);
        });
    }

    private DichVuBoSung getFormInput() throws Exception {
        String maDV = txtMaDichVu.getText().trim();
        if (maDV.isEmpty()) throw new Exception("Mã dịch vụ không được để trống!");

        String tenDV = txtTenDichVu.getText().trim();
        if (tenDV.isEmpty()) throw new Exception("Tên dịch vụ không được để trống!");

        BigDecimal donGia = BigDecimal.ZERO;
        try {
            donGia = new BigDecimal(txtDonGia.getText().trim().isEmpty() ? "0" : txtDonGia.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Đơn giá phải là số hợp lệ!");
        }

      DichVuBoSung dv = new DichVuBoSung(); 
    dv.setMaDichVu(maDV);
    dv.setTenDichVu(tenDV);
    dv.setDonGia(donGia);
    dv.setTrangThai(cboTrangThaiForm.getSelectedItem().toString()); 

    return dv;
    }

}