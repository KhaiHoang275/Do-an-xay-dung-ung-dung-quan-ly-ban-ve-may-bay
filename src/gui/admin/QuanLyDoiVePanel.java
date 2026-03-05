package gui.admin;

import bll.GiaoDichVeBUS;
import dal.ChuyenBayDAO;
import dal.HangVeDAO;
import dal.NguoiDungDAO;
import dal.ThongTinHanhKhachDAO;
import dal.TuyenBayDAO;
import dal.VeBanDAO;
import model.ChuyenBay;
import model.GiaoDichVe;
import model.HangVe;
import model.NguoiDung;
import model.ThongTinHanhKhach;
import model.TuyenBay;
import model.TrangThaiGiaoDich;
import model.VeBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Panel quản lý yêu cầu đổi vé — giao diện đã được cải tiến theo mẫu (hai khung vé cách nhau, chữ to hơn,
 * thẻ thông tin chi tiết bo góc và nền màu nhẹ), với tiêu đề Vé cũ/Vé mới và icon bổ sung.
 */
public class QuanLyDoiVePanel extends JPanel {

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_APPROVE = new Color(34, 197, 94);
    private final Color BTN_REJECT = new Color(239, 68, 68);
    private final Color BTN_REFRESH = new Color(168, 85, 247);
    private final Color DETAIL_BG = new Color(243, 250, 255); // light blue card
    private final int CARD_RADIUS = 14;

    private GiaoDichVeBUS bus;
    private VeBanDAO veBanDAO;
    private ChuyenBayDAO chuyenBayDAO;
    private TuyenBayDAO tuyenBayDAO;
    private HangVeDAO hangVeDAO;
    private ThongTinHanhKhachDAO thongTinHanhKhachDAO;
    private NguoiDungDAO nguoiDungDAO;

    private JTable tblYeuCau;
    private DefaultTableModel tblModel;

    // detail components (will be filled)
    private JLabel lblVeCuRoute;
    private JLabel lblVeCuClass;
    private JLabel lblVeCuPrice;
    private JLabel lblVeMoiRoute;
    private JLabel lblVeMoiClass;
    private JLabel lblVeMoiPrice;
    private JLabel lblNguoiDung;
    private JLabel lblLyDo;

    private JButton btnDuyet;
    private JButton btnTuChoi;
    private JButton btnLamMoi;

    private final NumberFormat moneyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public QuanLyDoiVePanel() {
        bus = new GiaoDichVeBUS();
        veBanDAO = new VeBanDAO();
        chuyenBayDAO = new ChuyenBayDAO();
        tuyenBayDAO = new TuyenBayDAO();
        hangVeDAO = new HangVeDAO();
        thongTinHanhKhachDAO = new ThongTinHanhKhachDAO();
        nguoiDungDAO = new NguoiDungDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        initComponents();
        loadDanhSachYeuCau();
    }

    private void initComponents() {
        // Title
        ImageIcon icon = loadIcon("/resources/icons/icons8-change-24.png", 24, 24);
        JLabel lblTitle = new JLabel("QUẢN LÝ YÊU CẦU ĐỔI VÉ", icon, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 6, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Card chính chứa table + detail
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(0, 18));

        // Table danh sách yêu cầu
        String[] columns = {"Mã GD", "Vé Cũ", "Vé Mới", "Phí GD", "Phí Chênh Lệch", "Lý Do", "Ngày Yêu Cầu", "Trạng Thái"};
        tblModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblYeuCau = new JTable(tblModel);
        styleTable(tblYeuCau);
        JScrollPane scrollTable = new JScrollPane(tblYeuCau);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.setPreferredSize(new Dimension(100, 220));
        formCard.add(scrollTable, BorderLayout.CENTER);

        // Khi chọn row -> hiển thị chi tiết
        tblYeuCau.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblYeuCau.getSelectedRow() >= 0) {
                String maGD = (String) tblModel.getValueAt(tblYeuCau.getSelectedRow(), 0);
                hienThiChiTiet(maGD);
            }
        });

        // Panel chi tiết: bo góc, nền nhẹ, bên trong hai cột vé (song song)
        RoundedPanel detailCard = new RoundedPanel(CARD_RADIUS, DETAIL_BG);
        detailCard.setLayout(new BorderLayout());
        detailCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        // two-columns container
        JPanel twoCols = new JPanel(new GridLayout(1, 2, 18, 0));
        twoCols.setOpaque(false);

        // left (Vé cũ) panel
        JPanel left = createTicketPanel("Vé cũ", "/resources/icons/icons8-plane-24.png");
        // right (Vé mới) panel
        JPanel right = createTicketPanel("Vé mới", "/resources/icons/icons8-plane-24.png");

        // lấy controls tham chiếu để fill dữ liệu
        lblVeCuRoute = (JLabel) left.getClientProperty("route");
        lblVeCuClass = (JLabel) left.getClientProperty("hang");
        lblVeCuPrice = (JLabel) left.getClientProperty("price");

        lblVeMoiRoute = (JLabel) right.getClientProperty("route");
        lblVeMoiClass = (JLabel) right.getClientProperty("hang");
        lblVeMoiPrice = (JLabel) right.getClientProperty("price");

        twoCols.add(left);
        twoCols.add(right);

        // dưới hai cột: user + ly do trong 1 hàng (chiếm full width)
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(12, 6, 0, 6));

        lblNguoiDung = new JLabel("");
        lblNguoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNguoiDung.setIcon(loadIcon("/resources/icons/icons8-location-24.png", 18, 18));
        lblNguoiDung.setBorder(new EmptyBorder(6, 0, 6, 0));

        lblLyDo = new JLabel("");
        lblLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLyDo.setIcon(loadIcon("/resources/icons/icons8-note-24.png", 18, 18));
        lblLyDo.setBorder(new EmptyBorder(6, 0, 0, 0));

        bottom.add(lblNguoiDung);
        bottom.add(lblLyDo);

        detailCard.add(twoCols, BorderLayout.CENTER);
        detailCard.add(bottom, BorderLayout.SOUTH);

        formCard.add(detailCard, BorderLayout.SOUTH);

        add(formCard, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        buttonPanel.setOpaque(false);
        btnDuyet = createButton("Duyệt", BTN_APPROVE);
        btnTuChoi = createButton("Từ chối", BTN_REJECT);
        btnLamMoi = createButton("Làm mới", BTN_REFRESH);

        setButtonIcon(btnDuyet, "/resources/icons/icons8-check-24.png");
        setButtonIcon(btnTuChoi, "/resources/icons/icons8-close-24.png");
        setButtonIcon(btnLamMoi, "/resources/icons/icons8-reset-24.png");

        buttonPanel.add(btnDuyet);
        buttonPanel.add(btnTuChoi);
        buttonPanel.add(btnLamMoi);

        add(buttonPanel, BorderLayout.SOUTH);

        // events
        btnDuyet.addActionListener(e -> xuLyDuyet());
        btnTuChoi.addActionListener(e -> xuLyTuChoi());
        btnLamMoi.addActionListener(e -> loadDanhSachYeuCau());
    }

    // tạo panel nhỏ cho 1 vé (trả về panel có 3 label route/hang/price lưu trong client properties)
    private JPanel createTicketPanel(String titleText, String iconPath) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        // title
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 41, 59));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        title.setIcon(loadIcon(iconPath, 20, 20));
        title.setIconTextGap(8);

        // content box (white background with slight round inside card)
        RoundedPanel box = new RoundedPanel(10, Color.WHITE);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(12, 12, 12, 12));

        // big route label
        JLabel route = new JLabel("MÃ - SGN → DAD (dd/MM/yyyy HH:mm)");
        route.setFont(new Font("Segoe UI", Font.BOLD, 16));
        route.setForeground(new Color(18, 32, 64));
        route.setBorder(new EmptyBorder(6, 0, 8, 0));

        // hạng
        JLabel hang = new JLabel("Hạng: Hạng Phổ Thông");
        hang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hang.setBorder(new EmptyBorder(6, 0, 6, 0));
        hang.setIcon(loadIcon("/resources/icons/icons8-prize-24.png", 16, 16));
        hang.setIconTextGap(6);

        // giá
        JLabel price = new JLabel("Giá: 1.300.000 VNĐ");
        price.setFont(new Font("Segoe UI", Font.BOLD, 15));
        price.setForeground(new Color(34, 97, 140));
        price.setBorder(new EmptyBorder(6, 0, 6, 0));
        price.setIcon(loadIcon("/resources/icons/icons8-expensive-price-24.png", 16, 16));
        price.setIconTextGap(6);

        box.add(route);
        box.add(hang);
        box.add(price);

        p.add(title, BorderLayout.NORTH);
        p.add(box, BorderLayout.CENTER);

        // lưu tham chiếu
        p.putClientProperty("route", route);
        p.putClientProperty("hang", hang);
        p.putClientProperty("price", price);

        return p;
    }

    private void loadDanhSachYeuCau() {
        tblModel.setRowCount(0);
        List<GiaoDichVe> list = bus.getAllGiaoDich();
        for (GiaoDichVe gd : list) {
            String phi = formatAmount(gd.getPhi());
            String phiCL = formatAmount(gd.getPhiChenhLech());
            tblModel.addRow(new Object[]{
                    gd.getMaGD(),
                    gd.getMaVeCu(),
                    gd.getMaChuyenBayMoi(),
                    phi + " VNĐ",
                    phiCL + " VNĐ",
                    gd.getLyDoDoi() != null ? shorten(gd.getLyDoDoi(), 30) : "",
                    gd.getNgayYeuCau() != null ? gd.getNgayYeuCau().format(dateFormat) : "N/A",
                    gd.getTrangThai() != null ? gd.getTrangThai().getMoTa() : ""
            });
        }
        clearChiTiet();
    }

    private void hienThiChiTiet(String maGD) {
        GiaoDichVe gd = bus.getGiaoDichById(maGD);
        if (gd == null) return;

        VeBan veCu = veBanDAO.selectById(gd.getMaVeCu());

        // lấy thông tin user/hành khách an toàn
        ThongTinHanhKhach hkhCu = null;
        NguoiDung nd = null;
        if (veCu != null && veCu.getMaHK() != null) {
            hkhCu = thongTinHanhKhachDAO.getByMaHK(veCu.getMaHK());
            if (hkhCu != null) {
                nd = nguoiDungDAO.getByMaNguoiDung(hkhCu.getMaNguoiDung());
            }
        }

        // chuyến & tuyến & hạng - có kiểm tra null
        ChuyenBay cbCu = veCu != null ? chuyenBayDAO.selectById(veCu.getMaChuyenBay()) : null;
        TuyenBay tbCu = cbCu != null ? tuyenBayDAO.selectById(cbCu.getMaTuyenBay()) : null;
        HangVe hvCu = veCu != null ? hangVeDAO.selectById(veCu.getMaHangVe()) : null;

        ChuyenBay cbMoi = chuyenBayDAO.selectById(gd.getMaChuyenBayMoi());
        TuyenBay tbMoi = cbMoi != null ? tuyenBayDAO.selectById(cbMoi.getMaTuyenBay()) : null;
        HangVe hvMoi = hangVeDAO.selectById(gd.getMaHangVeMoi());

        // gắn text an toàn (nếu null thì hiển thị -)
        if (lblVeCuRoute != null) {
            if (veCu != null && tbCu != null && cbCu != null) {
                lblVeCuRoute.setText("<html><b>" + veCu.getMaVe() + "</b> - " +
                        tbCu.getSanBayDi() + " → " + tbCu.getSanBayDen() + " (" +
                        cbCu.getNgayGioDi().format(datetimeFormat) + ")</html>");
            } else if (veCu != null) {
                lblVeCuRoute.setText(veCu.getMaVe());
            } else {
                lblVeCuRoute.setText("-");
            }
        }

        if (lblVeCuClass != null) {
            lblVeCuClass.setText("Hạng: " + (hvCu != null ? hvCu.getTenHang() : "-"));
        }
        if (lblVeCuPrice != null) {
            lblVeCuPrice.setText("Giá: " + (veCu != null && veCu.getGiaVe() != null ? formatAmount(veCu.getGiaVe()) + " VNĐ" : "-"));
        }

        if (lblVeMoiRoute != null) {
            if (tbMoi != null && cbMoi != null) {
                lblVeMoiRoute.setText("<html><b>" + gd.getMaChuyenBayMoi() + "</b> - " +
                        tbMoi.getSanBayDi() + " → " + tbMoi.getSanBayDen() + " (" +
                        cbMoi.getNgayGioDi().format(datetimeFormat) + ")</html>");
            } else {
                lblVeMoiRoute.setText("-");
            }
        }

        if (lblVeMoiClass != null) {
            lblVeMoiClass.setText("Hạng: " + (hvMoi != null ? hvMoi.getTenHang() : "-"));
        }
        if (lblVeMoiPrice != null) {
            BigDecimal giaMoi = bus.tinhGiaVeMoi(gd.getMaChuyenBayMoi(), gd.getMaHangVeMoi());
            lblVeMoiPrice.setText("Giá: " + formatAmount(giaMoi) + " VNĐ");
        }

        // người dùng + hành khách
        String userInfo = "-";
        if (nd != null) {
            userInfo = "Người dùng: " + nd.getUsername() + " (" + nd.getEmail() + ")";
        }
        if (hkhCu != null && hkhCu.getHoTen() != null) {
            userInfo += " - Hành khách: " + hkhCu.getHoTen();
        }
        lblNguoiDung.setText(userInfo);

        lblLyDo.setText("Lý do: " + (gd.getLyDoDoi() != null ? gd.getLyDoDoi() : ""));

        // enable/disable button
        btnDuyet.setEnabled(gd.getTrangThai() == TrangThaiGiaoDich.CHO_XU_LY);
        btnTuChoi.setEnabled(gd.getTrangThai() == TrangThaiGiaoDich.CHO_XU_LY);
    }

    private void xuLyDuyet() {
        int row = tblYeuCau.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu!");
            return;
        }
        String maGD = (String) tblModel.getValueAt(row, 0);
        try {
            bus.duyetYeuCau(maGD);
            JOptionPane.showMessageDialog(this, "Duyệt thành công!");
            loadDanhSachYeuCau();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void xuLyTuChoi() {
        int row = tblYeuCau.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu!");
            return;
        }
        String maGD = (String) tblModel.getValueAt(row, 0);
        try {
            bus.tuChoiYeuCau(maGD);
            JOptionPane.showMessageDialog(this, "Từ chối thành công!");
            loadDanhSachYeuCau();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void clearChiTiet() {
        if (lblVeCuRoute != null) lblVeCuRoute.setText("");
        if (lblVeCuClass != null) lblVeCuClass.setText("");
        if (lblVeCuPrice != null) lblVeCuPrice.setText("");
        if (lblVeMoiRoute != null) lblVeMoiRoute.setText("");
        if (lblVeMoiClass != null) lblVeMoiClass.setText("");
        if (lblVeMoiPrice != null) lblVeMoiPrice.setText("");
        if (lblNguoiDung != null) lblNguoiDung.setText("");
        if (lblLyDo != null) lblLyDo.setText("");
        btnDuyet.setEnabled(false);
        btnTuChoi.setEnabled(false);
    }

    // ----------------- UI helper -----------------

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(45, 72, 140));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return btn;
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

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0";
        return moneyFormat.format(amount);
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    // Rounded panel for nicer card look
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        RoundedPanel(int radius, Color bg) {
            super();
            cornerRadius = radius;
            backgroundColor = bg != null ? bg : getBackground();
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            int width = getWidth();
            int height = getHeight();
            RoundRectangle2D round = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
            g2.fill(round);
            g2.setColor(new Color(220, 220, 220));
            g2.draw(round);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }
}