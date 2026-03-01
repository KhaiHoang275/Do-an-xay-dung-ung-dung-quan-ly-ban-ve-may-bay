package gui.user;

import bll.GiaoDichVeBUS;
import dal.ChuyenBayDAO;
import dal.HangVeDAO;
import dal.TuyenBayDAO;
import model.ChuyenBay;
import model.HangVe;
import model.TuyenBay;
import model.VeBan;

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

    private String maVeCu;
    private String maNguoiDung;

    private JTable tblVeCu;
    private DefaultTableModel tblModelVeCu;
    private JComboBox<String> cboVeMoi;
    private JTextArea txtLyDo;

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

        setLayout(new BorderLayout(30, 30));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initComponents();
        loadThongTinBanDau();
    }

    private void initComponents() {

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("ĐỔI VÉ MÁY BAY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY);
        add(lblTitle, BorderLayout.NORTH);

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

        // ================= Vé mới =================
        JPanel panelVeMoi = new JPanel(new BorderLayout(10,10));
        panelVeMoi.setOpaque(false);
        panelVeMoi.add(createSectionLabel("Chọn vé mới"), BorderLayout.NORTH);

        cboVeMoi = new JComboBox<>();
        cboVeMoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboVeMoi.setPreferredSize(new Dimension(400, 40));
        cboVeMoi.putClientProperty("JComponent.roundRect", true);

        panelVeMoi.add(cboVeMoi, BorderLayout.CENTER);

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

        content.add(panelVeCu);
        content.add(Box.createVerticalStrut(20));
        content.add(panelVeMoi);
        content.add(Box.createVerticalStrut(20));
        content.add(panelLyDo);

        // ================= BUTTON PANEL =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);

        btnXacNhan = createButton("Xác nhận & Thanh toán", BTN_CONFIRM);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);
        btnHuy = createButton("Hủy", BTN_EXIT);

        addHoverEffect(btnXacNhan, BTN_CONFIRM);
        addHoverEffect(btnLamMoi, BTN_REFRESH);
        addHoverEffect(btnHuy, BTN_EXIT);

        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnHuy);

        card.add(content, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        // EVENTS
        btnXacNhan.addActionListener(e -> moPanelThanhToan());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnHuy.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });
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
        txtLyDo.setText("Nhập lý do đổi vé của bạn...");
        txtLyDo.setForeground(Color.GRAY);
        cboVeMoi.setSelectedIndex(-1);
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

        // ===== Load vé mới =====
        cboVeMoi.removeAllItems();
        List<VeBan> list = bus.danhSachVeCoTheDoi(maNguoiDung);

        boolean hasValid = false;

        for (VeBan ve : list) {

            if (!ve.getMaVe().equals(maVeCu)) {

                ChuyenBay cbNew = chuyenBayDAO.selectById(ve.getMaChuyenBay());
                if (cbNew != null) {

                    TuyenBay tb = tuyenBayDAO.selectById(cbNew.getMaTuyenBay());
                    if (tb != null) {

                        String format =
                                ve.getMaVe() + " - " +
                                        tb.getSanBayDi() + " -> " +
                                        tb.getSanBayDen() + " - " +
                                        cbNew.getNgayGioDi()
                                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                        cboVeMoi.addItem(format);
                        hasValid = true;
                    }
                }
            }
        }

        if (!hasValid) {
            JOptionPane.showMessageDialog(this, "Không có vé khả dụng để đổi.");
            btnXacNhan.setEnabled(false);
        }
    }

    private void moPanelThanhToan() {

        if (cboVeMoi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé mới!");
            return;
        }

        String selected = cboVeMoi.getSelectedItem().toString();
        String maVeMoi = selected.split(" - ")[0];

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
            BigDecimal phiCL = bus.tinhPhiChenhLech(maVeCu, maVeMoi);
            BigDecimal tong = phiGD.add(phiCL);

            Window parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof JFrame frame) {

                frame.setContentPane(
                        new ThanhToanDoiVePanel(
                                maVeCu,
                                maVeMoi,
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