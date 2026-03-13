package gui.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bll.GiaoDichVeBUS;
import dal.ThongTinHanhKhachDAO;
import dal.VeBanDAO;
import db.DBConnection;
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
    private ThongTinHanhKhachDAO thongTinHanhKhachDAO = new ThongTinHanhKhachDAO();

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

        btnXemChiTiet.addActionListener(e -> { 
            int row = table.getSelectedRow();
            if (row >= 0) {
                String maVe = table.getValueAt(row, 0).toString();
                showChiTietVeDialog(maVe);
            }
        });

        btnDoiVe.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn vé trước!");
                return;
            }
            String maVe = table.getValueAt(row, 0).toString();
            String ketQua = giaoDichVeBUS.kiemTraDieuKienDoiVe(maVe, maNguoiDung);

            if (!ketQua.equals("OK")) {
                JOptionPane.showMessageDialog(this, ketQua, "Không thể đổi vé", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFrame doiVeFrame = new JFrame("Đổi Vé Máy Bay");
            doiVeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            doiVeFrame.add(new JLabel("Màn hình đổi vé (Đang tích hợp...)", SwingConstants.CENTER));

            doiVeFrame.setSize(1050, 800);
            doiVeFrame.setLocationRelativeTo(null);
            doiVeFrame.setResizable(false);
            doiVeFrame.setVisible(true);
        });
    }

    // ====================== HÀM TIỆN ÍCH DỊCH DỮ LIỆU ======================
    // Cắt lấy số ghế (Ví dụ: G_MB012_3B -> 3B)
    private String formatSoGhe(String maGheTho) {
        if (maGheTho == null) return "";
        if (maGheTho.contains("_")) {
            return maGheTho.substring(maGheTho.lastIndexOf("_") + 1);
        }
        return maGheTho;
    }

    // Dịch mã hạng vé sang tên Tiếng Việt
    private String formatHangVe(String maHang) {
        if (maHang == null) return "Phổ thông";
        switch (maHang) {
            case "BUS": return "Thương gia";
            case "FST": return "Hạng nhất";
            case "PECO": return "Phổ thông ĐB";
            case "ECO": return "Phổ thông";
            default: return maHang;
        }
    }

    private void loadData(String filter) {
        if (filter == null) filter = "Tất cả";
        tableModel.setRowCount(0);

        List<ThongTinHanhKhach> dsHK = thongTinHanhKhachDAO.selectAllByMaNguoiDung(maNguoiDung);
        List<VeBan> basicList = new ArrayList<>();

        for (ThongTinHanhKhach hk : dsHK) {
            List<VeBan> veHK = veBanDAO.selectByMaHK(hk.getMaHK());
            basicList.addAll(veHK);
        }

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

        for (VeBan v : listToShow) {
            String giaStr = String.format("%,.0f VND", v.getGiaVe());
            // ĐÃ SỬA: Hiển thị trên bảng đẹp mắt hơn
            String gheDisplay = formatSoGhe(v.getMaGhe());
            String hangVeDisplay = formatHangVe(v.getMaHangVe());

            tableModel.addRow(new Object[]{
                    v.getMaVe(),
                    v.getMaChuyenBay(),
                    v.getMaPhieuDatVe(),
                    gheDisplay,
                    hangVeDisplay,
                    giaStr,
                    v.getTrangThaiVe()
            });
        }
    }

    private void checkDieuKienDoiVe(String maVe) {
        btnDoiVe.setEnabled(true); 
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // =========================================================================
    // HÀM HIỂN THỊ DIALOG XEM CHI TIẾT VÉ
    // =========================================================================
    private void showChiTietVeDialog(String maVe) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Vé Máy Bay - " + maVe, true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT v.maVe, v.maPhieuDatVe, v.maGhe, v.maHangVe, v.giaVe, v.trangThaiVe, " +
                         "hk.hoTen, hk.cccd, hk.hoChieu, hk.loaiHanhKhach, hk.gioiTinh, " +
                         "cb.maChuyenBay, cb.ngayGioDi, cb.ngayGioDen, " +
                         "tb.sanBayDi, tb.sanBayDen " +
                         "FROM VeBan v " +
                         "JOIN ThongTinHanhKhach hk ON v.maHK = hk.maHK " +
                         "JOIN ChuyenBay cb ON v.maChuyenBay = cb.maChuyenBay " +
                         "JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay " +
                         "WHERE v.maVe = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maVe);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
                NumberFormat moneyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                
                String hoTen = rs.getString("hoTen") + " (" + rs.getString("loaiHanhKhach") + ")";
                String giayTo = rs.getString("cccd") != null ? rs.getString("cccd") : (rs.getString("hoChieu") != null ? rs.getString("hoChieu") : "N/A");
                String gioiTinh = rs.getString("gioiTinh");
                
                // ĐÃ SỬA: Thay dấu mũi tên lạ thành dấu " -> " an toàn
                String tuyenBay = rs.getString("sanBayDi") + " -> " + rs.getString("sanBayDen");
                String maCB = rs.getString("maChuyenBay");
                String tgDi = rs.getTimestamp("ngayGioDi").toLocalDateTime().format(timeFormatter);
                String tgDen = rs.getTimestamp("ngayGioDen").toLocalDateTime().format(timeFormatter);
                
                String pnr = rs.getString("maPhieuDatVe");
                // ĐÃ SỬA: Format ghế và hạng vé
                String ghe = formatSoGhe(rs.getString("maGhe"));
                String hangVe = formatHangVe(rs.getString("maHangVe"));
                String giaVe = moneyFormatter.format(rs.getBigDecimal("giaVe")) + " VNĐ";
                String trangThai = rs.getString("trangThaiVe");

                // --- Panel Khách Hàng ---
                JPanel pnlKhach = createDetailGroup("THÔNG TIN HÀNH KHÁCH");
                pnlKhach.add(createDetailRow("Họ và tên:", hoTen));
                pnlKhach.add(createDetailRow("Giấy tờ (CCCD/HC):", giayTo));
                pnlKhach.add(createDetailRow("Giới tính:", gioiTinh));
                mainPanel.add(pnlKhach);
                mainPanel.add(Box.createVerticalStrut(15));

                // --- Panel Chuyến Bay ---
                JPanel pnlChuyenBay = createDetailGroup("THÔNG TIN CHUYẾN BAY");
                pnlChuyenBay.add(createDetailRow("Mã chuyến bay:", maCB));
                pnlChuyenBay.add(createDetailRow("Tuyến bay:", tuyenBay));
                pnlChuyenBay.add(createDetailRow("Khởi hành:", tgDi));
                pnlChuyenBay.add(createDetailRow("Đến nơi:", tgDen));
                mainPanel.add(pnlChuyenBay);
                mainPanel.add(Box.createVerticalStrut(15));

                // --- Panel Vé ---
                JPanel pnlVe = createDetailGroup("CHI TIẾT VÉ & THANH TOÁN");
                pnlVe.add(createDetailRow("Mã PNR (Đặt chỗ):", pnr));
                pnlVe.add(createDetailRow("Hạng vé:", hangVe));
                pnlVe.add(createDetailRow("Số ghế:", ghe));
                pnlVe.add(createDetailRow("Giá vé:", giaVe));
                pnlVe.add(createDetailRow("Trạng thái:", "<html><font color='#dc2626'><b>" + trangThai + "</b></font></html>"));
                mainPanel.add(pnlVe);

            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho vé này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi truy xuất cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        JButton btnClose = new JButton("Đóng");
        styleButton(btnClose, new Color(108, 117, 125), Color.WHITE);
        btnClose.addActionListener(e -> dialog.dispose());
        bottomPanel.add(btnClose);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createDetailGroup(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createCompoundBorder(
                        new TitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15), PRIMARY_COLOR),
                        new EmptyBorder(10, 15, 15, 15)
                )
        ));
        return panel;
    }

    private JPanel createDetailRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel lblLabel = new JLabel(labelText);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblLabel.setForeground(new Color(100, 116, 139));
        lblLabel.setPreferredSize(new Dimension(150, 30));
        
        JLabel lblValue = new JLabel(valueText);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValue.setForeground(PRIMARY_COLOR);
        
        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.CENTER);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        return row;
    }
}