package gui.user;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import bll.GiaoDichVeBUS;
import dal.ThongTinHanhKhachDAO;   // ← THÊM
import dal.VeBanDAO;
import model.ThongTinHanhKhach;
import model.VeBan;

public class LichSuDatVePanel extends JPanel {

    // Bảng màu đồng bộ thương hiệu
    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color BG_MAIN = new Color(245, 247, 250);

    private String maNguoiDung;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnXemChiTiet;
    private JButton btnDoiVe;
    private JButton btnQuayLai;
    private JComboBox<String> cboLocVe;

    private VeBanDAO veBanDAO = new VeBanDAO();
    private GiaoDichVeBUS giaoDichVeBUS = new GiaoDichVeBUS();
    private ThongTinHanhKhachDAO thongTinHanhKhachDAO = new ThongTinHanhKhachDAO(); // ← THÊM

    public LichSuDatVePanel(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
        initUI();
        loadData("Tất cả");
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ====================== TIÊU ĐỀ + COMBOBOX ======================
        JPanel pnlNorth = new JPanel(new BorderLayout());
        pnlNorth.setOpaque(false);

        JLabel lblTitle = new JLabel("LỊCH SỬ ĐẶT VÉ CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY_COLOR);

        cboLocVe = new JComboBox<>(new String[]{"Tất cả", "Có thể đổi"});
        cboLocVe.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cboLocVe.setPreferredSize(new Dimension(160, 40));
        cboLocVe.setBackground(Color.WHITE);
        cboLocVe.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlNorth.add(lblTitle, BorderLayout.WEST);
        pnlNorth.add(cboLocVe, BorderLayout.EAST);
        add(pnlNorth, BorderLayout.NORTH);

        // ====================== BẢNG ======================
        String[] cols = {"Mã Vé", "Chuyến Bay", "Mã PNR", "Ghế", "Hạng Vé", "Giá Vé", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ====================== NÚT ======================
        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setOpaque(false);

        btnQuayLai = new JButton("Quay lại trang chủ");
        styleButton(btnQuayLai, new Color(108, 117, 125), Color.WHITE);
        btnQuayLai.setPreferredSize(new Dimension(180, 40));

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnQuayLai);
        pnlSouth.add(pnlLeft, BorderLayout.WEST);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlButtons.setOpaque(false);

        btnXemChiTiet = new JButton("Xem chi tiết");
        btnDoiVe = new JButton("Đổi vé");
        styleButton(btnXemChiTiet, PRIMARY_COLOR, Color.WHITE);
        styleButton(btnDoiVe, ACCENT_COLOR, PRIMARY_COLOR);
        btnXemChiTiet.setEnabled(false);
        btnDoiVe.setEnabled(false);

        pnlButtons.add(btnXemChiTiet);
        pnlButtons.add(btnDoiVe);
        pnlSouth.add(pnlButtons, BorderLayout.EAST);
        add(pnlSouth, BorderLayout.SOUTH);

        // ====================== SỰ KIỆN ======================
        cboLocVe.addActionListener(e -> loadData((String) cboLocVe.getSelectedItem()));

        btnQuayLai.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                model.NguoiDung dummy = new model.NguoiDung();
                dummy.setMaNguoiDung(maNguoiDung);
                dummy.setUsername(maNguoiDung);
                new MainFrame(dummy).setVisible(true);
                window.dispose();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                btnXemChiTiet.setEnabled(true);
                String maVe = table.getValueAt(row, 0).toString();
                checkDieuKienDoiVe(maVe);
            } else {
                btnXemChiTiet.setEnabled(false);
                btnDoiVe.setEnabled(false);
            }
        });

        // Xem chi tiết & Đổi vé giữ nguyên như cũ
        btnXemChiTiet.addActionListener(e -> { /* giữ nguyên code cũ của bạn */ });
        // Nút Đổi vé
        // Nút Đổi vé → Chuyển thẳng sang DoiVePanel trong cùng cửa sổ
        // Nút Đổi vé → Mở DoiVePanel với size & cấu hình GIỐNG HỆT file Test
        btnDoiVe.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String maVe = table.getValueAt(row, 0).toString();

                // Tạo cửa sổ MỚI
                JFrame doiVeFrame = new JFrame("Đổi Vé Máy Bay");
                doiVeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // chỉ đóng cửa sổ này, không đóng app

                DoiVePanel doiVePanel = new DoiVePanel(maVe, maNguoiDung);

                doiVeFrame.add(doiVePanel, BorderLayout.CENTER);

                // === GIỐNG HỆT FILE TEST ===
                doiVeFrame.setSize(1050, 800);
                doiVeFrame.setLocationRelativeTo(null);   // giữa màn hình
                doiVeFrame.setResizable(false);          // không cho thay đổi kích thước (tùy chọn)

                doiVeFrame.setVisible(true);
            }
        });
    }

    // ====================== LẤY maHK TỪ maNguoiDung ======================
    private String getMaHK() {
        ThongTinHanhKhach tthk = thongTinHanhKhachDAO.selectByMaNguoiDung(maNguoiDung);
        if (tthk == null) {
            System.out.println("❌ [Lỗi] Không tìm thấy hành khách cho maNguoiDung = " + maNguoiDung);
            return null;
        }
        System.out.println("✅ Tìm thấy maHK = " + tthk.getMaHK() + " cho user " + maNguoiDung);
        return tthk.getMaHK();
    }

    // ====================== LOAD DATA (ĐÃ SỬA) ======================
    private void loadData(String filter) {
        if (filter == null) filter = "Tất cả";
        tableModel.setRowCount(0);

        String maHK = getMaHK();
        if (maHK == null) return;

        System.out.println("🔍 Đang load vé cho maHK: " + maHK + " | Filter: " + filter);

        List<VeBan> basicList = veBanDAO.selectByMaHK(maHK);
        System.out.println("   → selectByMaHK trả về: " + basicList.size() + " vé");

        List<VeBan> fullList = new ArrayList<>();
        for (VeBan b : basicList) {
            VeBan full = veBanDAO.selectById(b.getMaVe());
            if (full != null) fullList.add(full);
        }

        List<VeBan> listToShow = fullList;
        if ("Có thể đổi".equals(filter)) {
            listToShow = new ArrayList<>();
            for (VeBan v : fullList) {
                String ketQua = giaoDichVeBUS.kiemTraDieuKienDoiVe(v.getMaVe(), maNguoiDung);
                if ("OK".equals(ketQua)) listToShow.add(v);
            }
        }

        System.out.println("   → Số vé hiển thị cuối cùng: " + listToShow.size());

        for (VeBan v : listToShow) {
            String giaStr = String.format("%,.0f VND", v.getGiaVe());
            tableModel.addRow(new Object[]{
                    v.getMaVe(),
                    v.getMaChuyenBay(),
                    v.getMaPhieuDatVe(),
                    v.getMaGhe() != null ? v.getMaGhe() : "",
                    v.getMaHangVe() != null ? v.getMaHangVe() : "",
                    giaStr,
                    v.getTrangThaiVe()
            });
        }
    }

    private void checkDieuKienDoiVe(String maVe) {
        String ketQua = giaoDichVeBUS.kiemTraDieuKienDoiVe(maVe, maNguoiDung);
        btnDoiVe.setEnabled("OK".equals(ketQua));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
    }
}