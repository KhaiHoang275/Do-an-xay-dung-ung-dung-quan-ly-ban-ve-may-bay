package gui.admin;

import bll.ChuyenBayBUS;
import bll.MayBayBUS;
import bll.TuyenBayBUS;
import model.ChuyenBay;
import model.MayBay;
import model.TrangThaiChuyenBay;
import model.TuyenBay;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ChuyenBayPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaChuyenBay, txtTimKiem;
    private JComboBox<String> cbTuyenBay, cbMayBay, cbTrangThai, cbTimKiemTrangThai, cboHienThi;
    private JSpinner spinnerGioDi, spinnerGioDen;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private ChuyenBayBUS chuyenBayBUS;
    private TuyenBayBUS tuyenBayBUS;
    private MayBayBUS mayBayBUS; 

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_ADD = new Color(34, 197, 94);
    private final Color BTN_UPDATE = new Color(59, 130, 246);
    private final Color BTN_DELETE = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ChuyenBayPanel() {
        chuyenBayBUS = new ChuyenBayBUS();
        tuyenBayBUS = new TuyenBayBUS();
        mayBayBUS = new MayBayBUS(); 

        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadComboBoxData();
        loadDataToTable(chuyenBayBUS.getAllChuyenBay());
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        ImageIcon titleIcon = null;
        try {
            titleIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/icons8-airplane-take-off-24.png"))
                    .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        JLabel lblTitle = new JLabel("QUẢN LÝ CHUYẾN BAY", titleIcon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        
        txtTimKiem = createTextField();
        txtTimKiem.setPreferredSize(new Dimension(140, 35));
        
        cbTimKiemTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Chưa khởi hành", "Đang bay", "Đã hạ cánh", "Bị hoãn", "Đã hủy", "Đã xóa"});
        cbTimKiemTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTimKiemTrangThai.setPreferredSize(new Dimension(140, 35));

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(110, 35));
        btnTimKiem.setBackground(TABLE_HEADER);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTimKiem.setFocusPainted(false);
        try { setButtonIcon(btnTimKiem, "/resources/icons/icons8-search-24.png", 16); } catch (Exception e){}

        cboHienThi = new JComboBox<>(new String[]{"Đang hiển thị", "Thùng rác"});
        cboHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboHienThi.setPreferredSize(new Dimension(120, 35));

        searchPanel.add(txtTimKiem);
        searchPanel.add(cbTimKiemTrangThai);
        searchPanel.add(btnTimKiem);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(cboHienThi);

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Mã CB", "Tuyến Bay", "Máy Bay", "Giờ Khởi Hành", "Giờ Hạ Cánh", "Trạng Thái"};
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

        txtMaChuyenBay = createTextField();
        
        cbTuyenBay = new JComboBox<>();
        cbTuyenBay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        cbMayBay = new JComboBox<>();
        cbMayBay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        spinnerGioDi = createDateTimeSpinner();
        spinnerGioDen = createDateTimeSpinner();
        
        cbTrangThai = new JComboBox<>(new String[]{"Chưa khởi hành", "Đang bay", "Đã hạ cánh", "Bị hoãn", "Đã hủy", "Đã xóa"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(createLabel("Mã Chuyến Bay:"));
        formPanel.add(txtMaChuyenBay);
        formPanel.add(createLabel("Ngày Giờ Cất Cánh:"));
        formPanel.add(spinnerGioDi);

        formPanel.add(createLabel("Tuyến Bay:"));
        formPanel.add(cbTuyenBay);
        formPanel.add(createLabel("Ngày Giờ Hạ Cánh:"));
        formPanel.add(spinnerGioDen);

        formPanel.add(createLabel("Máy Bay:"));
        formPanel.add(cbMayBay);
        formPanel.add(createLabel("Trạng Thái:"));
        formPanel.add(cbTrangThai);

        formCard.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnThem = createButton("Thêm", BTN_ADD);
        btnSua = createButton("Cập nhật", BTN_UPDATE);
        btnXoa = createButton("Xóa", BTN_DELETE);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);

        try {
            setButtonIcon(btnThem, "/resources/icons/icons8-add-24.png", 20);
            setButtonIcon(btnSua, "/resources/icons/icons8-update-24.png", 20);
            setButtonIcon(btnXoa, "/resources/icons/icons8-delete-24.png", 20);
            setButtonIcon(btnLamMoi, "/resources/icons/icons8-erase-24.png", 20);
        } catch (Exception e) {}

        buttonPanel.add(btnThem);
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
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return btn;
    }

    private JSpinner createDateTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return spinner;
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
        for(int i = 0; i < table.getColumnCount(); i++) {
             table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void setButtonIcon(JButton btn, String path, int size) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaled));
        btn.setIconTextGap(8);
    }

    private String hienThiTrangThai(TrangThaiChuyenBay status) {
        if (status == null) return "Chưa khởi hành";
        switch (status) {
            case CHUA_KHOI_HANH: return "Chưa khởi hành";
            case DANG_BAY: return "Đang bay";
            case DA_HA_CANH: return "Đã hạ cánh";
            case BI_HOAN: return "Bị hoãn";
            case DA_HUY: return "Đã hủy";
            case DA_XOA: return "Đã xóa";
            default: return "Chưa khởi hành";
        }
    }

    private TrangThaiChuyenBay layTrangThaiTuUI(String uiValue) {
        if (uiValue == null) return TrangThaiChuyenBay.CHUA_KHOI_HANH;
        switch (uiValue) {
            case "Chưa khởi hành": return TrangThaiChuyenBay.CHUA_KHOI_HANH;
            case "Đang bay": return TrangThaiChuyenBay.DANG_BAY;
            case "Đã hạ cánh": return TrangThaiChuyenBay.DA_HA_CANH;
            case "Bị hoãn": return TrangThaiChuyenBay.BI_HOAN;
            case "Đã hủy": return TrangThaiChuyenBay.DA_HUY;
            case "Đã xóa": return TrangThaiChuyenBay.DA_XOA;
            default: return TrangThaiChuyenBay.CHUA_KHOI_HANH;
        }
    }

    private void loadComboBoxData() {
        cbTuyenBay.removeAllItems();
        for (TuyenBay tb : tuyenBayBUS.getAllTuyenBay()) {
            cbTuyenBay.addItem(tb.getMaTuyenBay() + " - " + tb.getSanBayDi() + " tới " + tb.getSanBayDen());
        }

        cbMayBay.removeAllItems();
        for (MayBay mb : mayBayBUS.getAllMayBay()) {
            cbMayBay.addItem(mb.getMaMayBay() + " - " + mb.getSoHieu());
        }
    }

    private void loadDataToTable(ArrayList<ChuyenBay> list) {
        tableModel.setRowCount(0);
        for (ChuyenBay cb : list) {
            tableModel.addRow(new Object[]{
                cb.getMaChuyenBay(),
                cb.getMaTuyenBay(),
                cb.getMaMayBay(),
                cb.getNgayGioDi() != null ? cb.getNgayGioDi().format(formatter) : "",
                cb.getNgayGioDen() != null ? cb.getNgayGioDen().format(formatter) : "",
                hienThiTrangThai(cb.getTrangThai())
            });
        }
    }

    private void setComboBoxSelectedByPrefix(JComboBox<String> cb, String ma) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).startsWith(ma + " -")) {
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
                    String maCB = tableModel.getValueAt(row, 0).toString();
                    ChuyenBay cb = chuyenBayBUS.getChuyenBayById(maCB);
                    
                    if (cb != null) {
                        txtMaChuyenBay.setText(cb.getMaChuyenBay());
                        txtMaChuyenBay.setEditable(false);
                        
                        setComboBoxSelectedByPrefix(cbTuyenBay, cb.getMaTuyenBay());
                        setComboBoxSelectedByPrefix(cbMayBay, cb.getMaMayBay());
                        cbTrangThai.setSelectedItem(hienThiTrangThai(cb.getTrangThai()));
                        
                        if (cb.getNgayGioDi() != null) {
                            spinnerGioDi.setValue(Date.from(cb.getNgayGioDi().atZone(ZoneId.systemDefault()).toInstant()));
                        }
                        if (cb.getNgayGioDen() != null) {
                            spinnerGioDen.setValue(Date.from(cb.getNgayGioDen().atZone(ZoneId.systemDefault()).toInstant()));
                        }
                    }
                }
            }
        });

        cboHienThi.addActionListener(e -> {
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            if (isTrash) {
                btnThem.setEnabled(false);
                btnSua.setText("Khôi phục");
                btnSua.setBackground(new Color(76, 175, 80)); 
                btnXoa.setEnabled(false);
                cbTrangThai.setEnabled(false);
                loadDataToTable(chuyenBayBUS.getChuyenBayTrongThungRac());
            } else {
                btnThem.setEnabled(true);
                btnSua.setText("Cập nhật");
                btnSua.setBackground(BTN_UPDATE);
                btnXoa.setEnabled(true);
                cbTrangThai.setEnabled(true);
                loadDataToTable(chuyenBayBUS.getAllChuyenBay());
            }
        });

        btnThem.addActionListener(e -> {
            try {
                ChuyenBay cb = getFormInput();
                if (chuyenBayBUS.themChuyenBay(cb)) {
                    JOptionPane.showMessageDialog(this, "Thêm chuyến bay thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng ID hoặc lỗi DB)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                String maChuyenBay = txtMaChuyenBay.getText().trim();
                if (maChuyenBay.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay trên bảng!");
                    return;
                }

                if (cboHienThi.getSelectedIndex() == 1) {
                    if (chuyenBayBUS.khoiPhucChuyenBay(maChuyenBay)) {
                        JOptionPane.showMessageDialog(this, "Khôi phục chuyến bay thành công!");
                        btnLamMoi.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }

                ChuyenBay cb = getFormInput();
                if (chuyenBayBUS.capNhatChuyenBay(cb)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay cần xóa!");
                return;
            }
            String maCB = txtMaChuyenBay.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chuyến bay " + maCB + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (chuyenBayBUS.xoaChuyenBay(maCB)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaChuyenBay.setText("");
            txtMaChuyenBay.setEditable(true);
            txtTimKiem.setText("");
            
            if(cbTuyenBay.getItemCount() > 0) cbTuyenBay.setSelectedIndex(0);
            if(cbMayBay.getItemCount() > 0) cbMayBay.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            cbTimKiemTrangThai.setSelectedIndex(0);
            
            spinnerGioDi.setValue(new Date());
            spinnerGioDen.setValue(new Date());
            
            table.clearSelection();
            
            if (cboHienThi.getSelectedIndex() == 1) {
                loadDataToTable(chuyenBayBUS.getChuyenBayTrongThungRac());
            } else {
                loadDataToTable(chuyenBayBUS.getAllChuyenBay());
            }
        });
        
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText();
            boolean isTrash = cboHienThi.getSelectedIndex() == 1;
            
            String selectedStatusStr = cbTimKiemTrangThai.getSelectedItem().toString();
            TrangThaiChuyenBay filterStatus = null;
            if (!selectedStatusStr.equals("Tất cả trạng thái")) {
                filterStatus = layTrangThaiTuUI(selectedStatusStr);
            }
            
            ArrayList<ChuyenBay> ketQua = chuyenBayBUS.timKiemChuyenBay(keyword, filterStatus, isTrash);
            loadDataToTable(ketQua);
        });
    }

    private ChuyenBay getFormInput() throws Exception {
        String maCB = txtMaChuyenBay.getText().trim();
        if (maCB.isEmpty()) throw new Exception("Mã chuyến bay không được để trống!");

        String maTuyenBay = cbTuyenBay.getSelectedItem().toString().split(" - ")[0].trim();
        String maMayBay = cbMayBay.getSelectedItem().toString().split(" - ")[0].trim();
        TrangThaiChuyenBay trangThai = layTrangThaiTuUI(cbTrangThai.getSelectedItem().toString());

        Date dateDi = (Date) spinnerGioDi.getValue();
        LocalDateTime ldtDi = dateDi.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        Date dateDen = (Date) spinnerGioDen.getValue();
        LocalDateTime ldtDen = dateDen.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (ldtDi.isAfter(ldtDen) || ldtDi.isEqual(ldtDen)) {
            throw new Exception("Giờ hạ cánh phải sau Giờ cất cánh!");
        }

        return new ChuyenBay(maCB, maTuyenBay, maMayBay, null, ldtDi, ldtDen, trangThai);
    }
}