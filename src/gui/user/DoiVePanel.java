package gui.user;

import bll.GiaoDichVeBUS;
import dal.ChuyenBayDAO;
import dal.HangVeDAO;
import dal.TuyenBayDAO;
import dal.MayBayDAO; // THÊM: Giả định bạn có MayBayDAO để lấy thông tin máy bay. Nếu không, thêm class này.
import gui.admin.SoDoGhePanel; // THÊM: Import SoDoGhePanel
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DoiVePanel extends JPanel {

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_CONFIRM = new Color(34, 197, 94);
    private final Color BTN_REFRESH = new Color(168, 85, 247);
    private final Color BTN_EXIT = new Color(120,120,120);

    private GiaoDichVeBUS bus;
    private ChuyenBayDAO chuyenBayDAO;
    private TuyenBayDAO tuyenBayDAO;
    private HangVeDAO hangVeDAO;
    private MayBayDAO mayBayDAO; // THÊM: Để lấy thông tin máy bay (ví dụ: tenMayBay)

    private String maVeCu;
    private String maNguoiDung;

    private JTable tblVeCu;
    private DefaultTableModel tblModelVeCu;
    private JComboBox<String> cboChuyenBayMoi;
    private JComboBox<String> cboHangVeMoi;
    private JTextField txtTuyenBay; // Non-editable
    private JTextArea txtLyDo;

    // THAY ĐỔI: Thay txtMaGheMoi bằng button và label để hiển thị ghế đã chọn
    private JButton btnChonGheMoi;
    private JLabel lblGheDaChon;
    private String maGheMoi = ""; // Biến private để lưu mã ghế mới

    private JButton btnXacNhan;
    private JButton btnHuy;
    private JButton btnLamMoi;

    private final NumberFormat moneyFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    public DoiVePanel(String maVeCu, String maNguoiDung) {
        this.maVeCu = maVeCu;
        this.maNguoiDung = maNguoiDung;

        this.bus = new GiaoDichVeBUS();
        this.chuyenBayDAO = new ChuyenBayDAO();
        this.tuyenBayDAO = new TuyenBayDAO();
        this.hangVeDAO = new HangVeDAO();
        this.mayBayDAO = new MayBayDAO(); // THÊM: Khởi tạo MayBayDAO

        setLayout(new BorderLayout(30, 30));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initComponents();
        loadThongTinBanDau();
    }

    private void initComponents() {

        // ===== TITLE =====
        ImageIcon leftIcon = null;
        ImageIcon rightIcon = null;

        try {
            leftIcon = new ImageIcon(
                    new ImageIcon(getClass().getResource("/resources/icons/icons8-update-24.png"))
                            .getImage()
                            .getScaledInstance(22, 22, Image.SCALE_SMOOTH)
            );

            rightIcon = new ImageIcon(
                    new ImageIcon(getClass().getResource("/resources/icons/icons8-plane-24.png"))
                            .getImage()
                            .getScaledInstance(22, 22, Image.SCALE_SMOOTH)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel chứa title (căn giữa, không chiếm nhiều chiều cao)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel lblLblLeftIcon = new JLabel(leftIcon);

        JLabel lblTitle = new JLabel("ĐỔI VÉ MÁY BAY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY);

        JLabel lblRightIcon = new JLabel(rightIcon);

        titlePanel.add(lblLblLeftIcon);
        titlePanel.add(lblTitle);
        titlePanel.add(lblRightIcon);

        // Wrapper kiểm soát padding trên dưới
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setOpaque(false);
        northWrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        northWrapper.add(titlePanel, BorderLayout.CENTER);

        add(northWrapper, BorderLayout.NORTH);

        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(25, 25));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ================= Vé hiện tại =================
        JPanel panelVeCu = new JPanel(new BorderLayout(10,10));
        panelVeCu.setOpaque(false);

        panelVeCu.add(createSectionLabel("Vé hiện tại"), BorderLayout.NORTH);

        String[] columns = {"Mã vé", "Ngày giờ bay", "Giá", "Hạng"};
        tblModelVeCu = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVeCu = new JTable(tblModelVeCu);
        styleTable(tblVeCu);

        JScrollPane scroll = new JScrollPane(tblVeCu);
        scroll.setPreferredSize(new Dimension(700, 110));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));

        panelVeCu.add(scroll, BorderLayout.CENTER);

        content.add(panelVeCu);
        content.add(Box.createVerticalStrut(20));

        JPanel panelVeMoi = new JPanel(new BorderLayout(10,10));
        panelVeMoi.setOpaque(false);
        panelVeMoi.add(createSectionLabel("Chọn vé mới"), BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));  // Sửa: BoxLayout vertical để stack hàng ngang
        selectionPanel.setOpaque(false);

    // Row 1: Tuyến bay + Chuyến bay (ngang nhau)
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));  // THÊM: Nested panel ngang, space 20 giữa component
        row1.setOpaque(false);

    // Tuyen bay (non-editable)
        JLabel lblTuyenBay = new JLabel("Tuyến bay:");
        lblTuyenBay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTuyenBay = new JTextField();
        txtTuyenBay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTuyenBay.setPreferredSize(new Dimension(250, 40));  // Sửa: Giảm width để fit ngang (tùy chỉnh nếu cần)
        txtTuyenBay.setEditable(false);
        txtTuyenBay.putClientProperty("JComponent.roundRect", true);
        row1.add(lblTuyenBay);
        row1.add(txtTuyenBay);

    // Chuyen bay moi
        JLabel lblChuyenBayMoi = new JLabel("Chuyến bay mới:");
        lblChuyenBayMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cboChuyenBayMoi = new JComboBox<>();
        cboChuyenBayMoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboChuyenBayMoi.setPreferredSize(new Dimension(350, 40));  // Sửa: Adjust width để fit ngang
        cboChuyenBayMoi.putClientProperty("JComponent.roundRect", true);
        row1.add(lblChuyenBayMoi);
        row1.add(cboChuyenBayMoi);

        selectionPanel.add(row1);
        selectionPanel.add(Box.createVerticalStrut(20));  // THÊM: Space giữa row

    // Row 2: Hang ve + Ma ghe (ngang nhau)
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));  // THÊM: Nested panel ngang tương tự
        row2.setOpaque(false);

    // Hang ve moi
        JLabel lblHangVeMoi = new JLabel("Hạng vé mới:");
        lblHangVeMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cboHangVeMoi = new JComboBox<>();
        cboHangVeMoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboHangVeMoi.setPreferredSize(new Dimension(250, 40));  // Sửa: Giảm width
        cboHangVeMoi.putClientProperty("JComponent.roundRect", true);
        row2.add(lblHangVeMoi);
        row2.add(cboHangVeMoi);

    // Ma ghe moi
        JLabel lblMaGheMoi = new JLabel("Mã ghế mới:");
        lblMaGheMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        row2.add(lblMaGheMoi);

        JPanel ghePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ghePanel.setOpaque(false);

        btnChonGheMoi = createButton("Chọn ghế mới", new Color(59, 130, 246));
        btnChonGheMoi.setPreferredSize(new Dimension(150, 40));
        setButtonIcon(btnChonGheMoi, "/resources/icons/icons8-seat-24.png");
        addHoverEffect(btnChonGheMoi, new Color(59, 130, 246));

        lblGheDaChon = new JLabel("Ghế đã chọn: Chưa chọn");
        lblGheDaChon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGheDaChon.setForeground(new Color(33, 37, 41));

        ghePanel.add(btnChonGheMoi);
        ghePanel.add(lblGheDaChon);

        row2.add(ghePanel);

        selectionPanel.add(row2);
        selectionPanel.add(Box.createVerticalStrut(20));  // THÊM: Space trước lý do

        // Để fill space dưới khi full screen (tránh dồn lên trên)
        selectionPanel.add(Box.createVerticalGlue());  // THÊM: Glue để push content lên giữa, space dưới tự động

        panelVeMoi.add(selectionPanel, BorderLayout.CENTER);

        content.add(panelVeMoi);
        content.add(Box.createVerticalStrut(20));

        // ================= Lý do =================
        JPanel panelLyDo = new JPanel(new BorderLayout(10,10));
        panelLyDo.setOpaque(false);
        panelLyDo.add(createSectionLabel("Lý do đổi vé"), BorderLayout.NORTH);

        txtLyDo = new JTextArea(4, 20);
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(new EmptyBorder(10,10,10,10));

        addPlaceholder();

        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        scrollLyDo.setPreferredSize(new Dimension(700, 90));
        scrollLyDo.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        panelLyDo.add(scrollLyDo, BorderLayout.CENTER);

        content.add(panelLyDo);

        card.add(content, BorderLayout.CENTER);

        // ================= BUTTON PANEL =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);

        btnXacNhan = createButton("Xác nhận & Thanh toán", BTN_CONFIRM);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);
        btnHuy = createButton("Hủy", BTN_EXIT);

        setButtonIcon(btnXacNhan, "/resources/icons/icons8-check-24.png");
        setButtonIcon(btnLamMoi, "/resources/icons/icons8-reset-24.png");
        setButtonIcon(btnHuy, "/resources/icons/icons8-close-24.png");

        addHoverEffect(btnXacNhan, BTN_CONFIRM);
        addHoverEffect(btnLamMoi, BTN_REFRESH);
        addHoverEffect(btnHuy, BTN_EXIT);

        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnHuy);

        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        // EVENTS
        btnChonGheMoi.addActionListener(e -> moSoDoGhePanel()); // THÊM event cho btnChonGheMoi
        btnXacNhan.addActionListener(e -> moPanelThanhToan());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnHuy.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        // Load HangVe when ChuyenBay changes (placeholder, assume all HangVe for simplicity)
        cboChuyenBayMoi.addActionListener(e -> loadHangVeMoi());
    }

    private void addPlaceholder() {
        txtLyDo.setText("Nhập lý do đổi vé của bạn...");
        txtLyDo.setForeground(Color.GRAY);

        txtLyDo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtLyDo.getText().equals("Nhập lý do đổi vé của bạn...")) {
                    txtLyDo.setText("");
                    txtLyDo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (txtLyDo.getText().isEmpty()) {
                    txtLyDo.setText("Nhập lý do đổi vé của bạn...");
                    txtLyDo.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void setButtonIcon(JButton btn, String path) {
        ImageIcon icon = loadIcon(path, 18, 18);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }
    }

    private ImageIcon loadIcon(String resourcePath, int w, int h) {
        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(resourcePath));
            Image im = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(im);
        } catch (Exception e) {
            // không tìm thấy icon -> trả về null không gây chết
            return null;
        }
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(25, 25, 25, 25)
        ));
        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(33,37,41));
        return lbl;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(190, 45));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }

    private void addHoverEffect(JButton btn, Color base) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(base.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(base);
            }
        });
    }

    private void styleTable(JTable table) {
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

    private void lamMoi() {
        cboChuyenBayMoi.setSelectedIndex(-1);
        cboHangVeMoi.setSelectedIndex(-1);
        maGheMoi = ""; // THAY ĐỔI: Reset maGheMoi
        lblGheDaChon.setText("Ghế đã chọn: Chưa chọn");
        txtLyDo.setText("Nhập lý do đổi vé của bạn...");
        txtLyDo.setForeground(Color.GRAY);
    }

    private void loadThongTinBanDau() {

        String check = bus.kiemTraDieuKienDoiVe(maVeCu, maNguoiDung);
        if (!check.equals("OK")) {
            JOptionPane.showMessageDialog(this, check);
            btnXacNhan.setEnabled(false);
            return;
        }

        VeBan veCu = bus.veBanDAO.selectById(maVeCu);
        if (veCu == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé cũ.");
            btnXacNhan.setEnabled(false);
            return;
        }

        ChuyenBay cb = chuyenBayDAO.selectById(veCu.getMaChuyenBay());
        HangVe hv = hangVeDAO.selectById(veCu.getMaHangVe());

        if (cb == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến bay.");
            btnXacNhan.setEnabled(false);
            return;
        }

        TuyenBay tb = tuyenBayDAO.selectById(cb.getMaTuyenBay());
        if (tb != null) {
            txtTuyenBay.setText(tb.getSanBayDi() + " -> " + tb.getSanBayDen());
        }

        String ngayGioBay = cb.getNgayGioDi()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String tenHang = hv != null ? hv.getTenHang() : "N/A";
        String giaVeFormatted = moneyFormat.format(veCu.getGiaVe()) + " VNĐ";

        tblModelVeCu.setRowCount(0);
        tblModelVeCu.addRow(new Object[]{
                veCu.getMaVe(),
                ngayGioBay,
                giaVeFormatted,
                tenHang
        });

        // ===== Load chuyến bay mới =====
        cboChuyenBayMoi.removeAllItems();
        List<ChuyenBay> listChuyenBay = bus.danhSachChuyenBayCoTheDoi(maVeCu);

        boolean hasValid = false;

        for (ChuyenBay cbNew : listChuyenBay) {
            TuyenBay tbNew = tuyenBayDAO.selectById(cbNew.getMaTuyenBay());
            if (tbNew != null) {
                String format = cbNew.getMaChuyenBay() + " - " +
                        tbNew.getSanBayDi() + " -> " +
                        tbNew.getSanBayDen() + " - " +
                        cbNew.getNgayGioDi()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                cboChuyenBayMoi.addItem(format);
                hasValid = true;
            }
        }

        if (!hasValid) {
            JOptionPane.showMessageDialog(this, "Không có chuyến bay khả dụng để đổi.");
            btnXacNhan.setEnabled(false);
        }

        // ===== Load tất cả hạng vé (giả định tất cả hạng vé khả dụng cho mọi chuyến bay)
        loadHangVeMoi();
    }

    private void loadHangVeMoi() {
        cboHangVeMoi.removeAllItems();
        List<HangVe> listHangVe = hangVeDAO.selectAll(); // Giả định findAll() trả về tất cả hạng vé hoạt động

        for (HangVe hv : listHangVe) {
            if (hv.getTrangThai() == TrangThaiHangVe.HOAT_DONG) { // Giả định enum HOAT_DONG
                cboHangVeMoi.addItem(hv.getMaHangVe() + " - " + hv.getTenHang());
            }
        }
    }

    // THÊM: Method mở SoDoGhePanel (sửa syntax split, thêm truyền maHangVeMoi, đổi getTenMayBay)
    private void moSoDoGhePanel() {
        if (cboChuyenBayMoi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay mới trước!");
            return;
        }

        String selectedChuyenBay = cboChuyenBayMoi.getSelectedItem().toString();
        String maChuyenBayMoi = selectedChuyenBay.split(" - ")[0];

        ChuyenBay cbMoi = chuyenBayDAO.selectById(maChuyenBayMoi);
        if (cbMoi == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chuyến bay mới!");
            return;
        }

        String maMayBay = cbMoi.getMaMayBay();
        MayBay mayBay = mayBayDAO.selectById(maMayBay);
        if (mayBay == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin máy bay!");
            return;
        }
        String tenMayBay = mayBay.getHangSanXuat();  // Sửa: Dùng getHangSanXuat() dựa trên model và DB

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Sơ đồ chỗ ngồi", true);

        SoDoGhePanel soDoPanel = new SoDoGhePanel(maMayBay, tenMayBay, 1);  // Giữ constructor cũ, không filter hạng vé

        soDoPanel.setListener(new SoDoGhePanel.SoDoGheListener() {
            @Override
            public void onBack() {
                dialog.dispose();
            }

            @Override
            public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                if (!selectedSeats.isEmpty()) {
                    maGheMoi = selectedSeats.get(0).getSoGhe();
                    lblGheDaChon.setText("Ghế đã chọn: " + maGheMoi);
                }
                dialog.dispose();
            }
        });

        dialog.add(soDoPanel);
        dialog.pack();
        dialog.setSize(850, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void moPanelThanhToan() {

        if (cboChuyenBayMoi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay mới!");
            return;
        }

        if (cboHangVeMoi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hạng vé mới!");
            return;
        }

        if (maGheMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế mới!");
            return;
        }

        String selectedChuyenBay = cboChuyenBayMoi.getSelectedItem().toString();
        String maChuyenBayMoi = selectedChuyenBay.split(" - ")[0];  // SỬA: Xóa "regex "

        String selectedHangVe = cboHangVeMoi.getSelectedItem().toString();
        String maHangVeMoi = selectedHangVe.split(" - ")[0];  // SỬA: Xóa "regex " nếu có

        String lyDo = txtLyDo.getText().trim();

        if (lyDo.isEmpty() || lyDo.equals("Nhập lý do đổi vé của bạn...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do!");
            return;
        }

        if (lyDo.length() > 500) {
            JOptionPane.showMessageDialog(this, "Lý do tối đa 500 ký tự!");
            return;
        }

        try {
            BigDecimal phiGD = bus.tinhPhiGiaoDich(maVeCu);
            BigDecimal phiCL = bus.tinhPhiChenhLech(maChuyenBayMoi, maHangVeMoi, maVeCu);
            BigDecimal tong = phiGD.add(phiCL);

            Window parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof JFrame frame) {

                frame.setContentPane(
                        new ThanhToanDoiVePanel(
                                maVeCu,
                                maChuyenBayMoi,
                                maHangVeMoi,
                                maGheMoi,
                                lyDo,
                                phiGD,
                                phiCL,
                                tong,
                                bus
                        )
                );

                frame.revalidate();
                frame.repaint();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}