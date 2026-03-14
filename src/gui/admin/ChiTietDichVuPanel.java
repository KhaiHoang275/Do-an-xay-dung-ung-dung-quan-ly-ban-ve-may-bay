package gui.admin;

import bll.ChiTietDichVuBUS;
import model.ChiTietDichVu;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class ChiTietDichVuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaVe, txtMaDichVu, txtSoLuong, txtThanhTien, txtTimKiem;
    private JComboBox<String> cboHienThi;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    // Gọi đúng tên biến BUS của bạn
    private ChiTietDichVuBUS ctdvBUS;

    // Bảng màu chuẩn dự án (Khớp 100% với form Hóa Đơn)
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    public ChiTietDichVuPanel() {
        ctdvBUS = new ChiTietDichVuBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        // Gọi đúng hàm docDanhSachChiTiet() trong file BUS của bạn
        loadDataToTable(ctdvBUS.docDanhSachChiTiet()); 
    }

    private void initComponents() {
        // ================= HEADER & SEARCH =================
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ CHI TIẾT DỊCH VỤ", JLabel.LEFT);
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

        btnTimKiem = createRoundedButton("Tìm kiếm", TABLE_HEADER, "/resources/icons/icons8-search-24.png", 16);
        btnTimKiem.setPreferredSize(new Dimension(130, 35));

        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        cboHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboHienThi.setPreferredSize(new Dimension(150, 35));

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

        // 4 Cột chuẩn theo file Model
        String[] columns = {"Mã Vé", "Mã Dịch Vụ", "Số Lượng", "Thành Tiền (VNĐ)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(); // Đã có sẵn fix lỗi tàng hình tiêu đề

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // ================= FORM NHẬP LIỆU =================
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        formPanel.setOpaque(false);

        txtMaVe = createTextField();
        txtMaDichVu = createTextField();
        txtSoLuong = createTextField();
        
        // Cột Thành Tiền sẽ bị khóa màu xám (Vì BUS đã tự động tính)
        txtThanhTien = createTextField();
        txtThanhTien.setEditable(false);
        txtThanhTien.setBackground(new Color(235, 235, 235)); 

        formPanel.add(createLabel("Mã Vé (*):"));
        formPanel.add(txtMaVe);
        formPanel.add(createLabel("Mã Dịch Vụ (*):"));
        formPanel.add(txtMaDichVu);

        formPanel.add(createLabel("Số Lượng (*):"));
        formPanel.add(txtSoLuong);
        formPanel.add(createLabel("Thành Tiền:"));
        formPanel.add(txtThanhTien);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(formPanel, BorderLayout.NORTH);

        formCard.add(formWrapper, BorderLayout.CENTER);

        // ================= BUTTONS =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

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

    // ====================================================================
    // CÁC HÀM UI HELPER 
    // ====================================================================
    private JButton createRoundedButton(String text, Color bgColor, String iconPath, int iconSize) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g); 
            }
        };
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        headerRenderer.setBackground(TABLE_HEADER);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ====================================================================
    // XỬ LÝ DỮ LIỆU & SỰ KIỆN GỌI XUỐNG BUS
    // ====================================================================
    private void loadDataToTable(List<ChiTietDichVu> list) {
        tableModel.setRowCount(0);
        if (list == null) return;
        for (ChiTietDichVu ct : list) {
            tableModel.addRow(new Object[]{
                    ct.getMaVe(),
                    ct.getMaDichVu(),
                    ct.getSoLuong(),
                    ct.getThanhTien() != null ? String.format("%,.0f", ct.getThanhTien()) : "0" // Format dấu phẩy
            });
        }
    }

    private void setupListeners() {
        // Sự kiện Click Chuột
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaVe.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaVe.setEditable(false); // Khóa chính không cho sửa
                    
                    txtMaDichVu.setText(tableModel.getValueAt(row, 1).toString());
                    txtMaDichVu.setEditable(false); // Khóa chính không cho sửa

                    txtSoLuong.setText(tableModel.getValueAt(row, 2).toString());
                    
                    String tienStr = tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString().replace(",", "") : "0";
                    txtThanhTien.setText(tienStr);
                }
            }
        });

        // NÚT THÊM
        btnThem.addActionListener(e -> {
            try {
                ChiTietDichVu ct = getFormInput();
                String result = ctdvBUS.themChiTiet(ct); // Gọi hàm themChiTiet
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        // NÚT CẬP NHẬT (Do bảng khóa kép thường k có Update, nên mình chặn lại)
        btnSua.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Đối với Chi Tiết Dịch Vụ, vui lòng XÓA và THÊM MỚI thay vì cập nhật trực tiếp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // NÚT XÓA
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!");
                return;
            }
            String maVe = txtMaVe.getText();
            String maDV = txtMaDichVu.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ " + maDV + " của vé " + maVe + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = ctdvBUS.xoaChiTiet(maVe, maDV); // Gọi hàm xoaChiTiet với 2 tham số
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });

        // NÚT LÀM MỚI
        btnLamMoi.addActionListener(e -> {
            txtMaVe.setText("");
            txtMaDichVu.setText("");
            txtSoLuong.setText("");
            txtThanhTien.setText("");
            txtTimKiem.setText("");
            
            txtMaVe.setEditable(true); // Mở khóa lại
            txtMaDichVu.setEditable(true); // Mở khóa lại
            table.clearSelection();
            
            loadDataToTable(ctdvBUS.docDanhSachChiTiet()); // Load lại bảng
        });

        // NÚT TÌM KIẾM
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            List<ChiTietDichVu> allData = ctdvBUS.docDanhSachChiTiet();
            
            List<ChiTietDichVu> ketQua = allData.stream().filter(ct -> 
                ct.getMaVe().toLowerCase().contains(keyword) || 
                ct.getMaDichVu().toLowerCase().contains(keyword)
            ).collect(Collectors.toList());
            
            loadDataToTable(ketQua);
        });
    }

    private ChiTietDichVu getFormInput() throws Exception {
        String maVe = txtMaVe.getText().trim();
        if (maVe.isEmpty()) throw new Exception("Mã vé không được để trống!");

        String maDV = txtMaDichVu.getText().trim();
        if (maDV.isEmpty()) throw new Exception("Mã dịch vụ không được để trống!");

        int soLuong = 0;
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) throw new Exception();
        } catch (Exception e) {
            throw new Exception("Số lượng phải là số nguyên dương lớn hơn 0!");
        }

        // Tạo model trống
        ChiTietDichVu ct = new ChiTietDichVu();
        ct.setMaVe(maVe);
        ct.setMaDichVu(maDV);
        ct.setSoLuong(soLuong);
        
        // Không set Thành Tiền vì tầng BUS của bạn đã code tự tính rồi!
        return ct;
    }
}