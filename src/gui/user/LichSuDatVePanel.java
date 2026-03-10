package gui.user;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

import dal.VeBanDAO;
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
    private JButton btnQuayLai; // Thêm nút Quay lại
    private VeBanDAO veBanDAO = new VeBanDAO();

    public LichSuDatVePanel(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // --- TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("LỊCH SỬ ĐẶT VÉ CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY_COLOR);
        add(lblTitle, BorderLayout.NORTH);

        // --- BẢNG DANH SÁCH VÉ ---
        String[] cols = {"Mã Vé", "Chuyến Bay", "Mã PNR", "Ghế", "Hạng Vé", "Giá Vé", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Khóa không cho sửa trực tiếp trên bảng
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa nội dung các cột cho đẹp
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- KHU VỰC NÚT BẤM (SOUTH) ---
        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setOpaque(false);

        // Nút quay lại để bên trái
        btnQuayLai = new JButton("Quay lại trang chủ");
        styleButton(btnQuayLai, new Color(108, 117, 125), Color.WHITE); // Màu xám lịch sự
        btnQuayLai.setPreferredSize(new Dimension(180, 40));
        
        JPanel pnlLeftButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLeftButton.setOpaque(false);
        pnlLeftButton.add(btnQuayLai);
        pnlSouth.add(pnlLeftButton, BorderLayout.WEST);

        // Các nút thao tác để bên phải
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlButtons.setOpaque(false);

        btnXemChiTiet = new JButton("Xem chi tiết");
        btnDoiVe = new JButton("Đổi vé");

        styleButton(btnXemChiTiet, PRIMARY_COLOR, Color.WHITE);
        styleButton(btnDoiVe, ACCENT_COLOR, PRIMARY_COLOR);

        // Mặc định làm mờ 2 nút khi chưa chọn dòng nào
        btnXemChiTiet.setEnabled(false);
        btnDoiVe.setEnabled(false);

        pnlButtons.add(btnXemChiTiet);
        pnlButtons.add(btnDoiVe);

        pnlSouth.add(pnlButtons, BorderLayout.EAST);
        add(pnlSouth, BorderLayout.SOUTH);

        // ================= XỬ LÝ SỰ KIỆN =================

        // Sự kiện Quay lại trang chủ
        btnQuayLai.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                // Tạo lại user ảo mang mã ID hiện tại để giữ trạng thái đăng nhập
                model.NguoiDung dummyUser = new model.NguoiDung();
                dummyUser.setMaNguoiDung(maNguoiDung);
                dummyUser.setUsername(maNguoiDung); 
                
                new MainFrame(dummyUser).setVisible(true);
                window.dispose();
            }
        });

        // Khi click vào 1 dòng trong bảng
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                btnXemChiTiet.setEnabled(true); // Luôn cho phép xem chi tiết
                String maVe = table.getValueAt(row, 0).toString();
                checkDieuKienDoiVe(maVe); // Hàm kiểm tra xem vé có được phép đổi không
            } else {
                btnXemChiTiet.setEnabled(false);
                btnDoiVe.setEnabled(false);
            }
        });

        // Nút Xem chi tiết
        btnXemChiTiet.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                 String maVe = table.getValueAt(row, 0).toString();
                 String chuyenBay = table.getValueAt(row, 1).toString();
                 String pnr = table.getValueAt(row, 2).toString();
                 String ghe = table.getValueAt(row, 3).toString();
                 String hangVe = table.getValueAt(row, 4).toString();
                 String gia = table.getValueAt(row, 5).toString();
                 String trangThai = table.getValueAt(row, 6).toString();
                 
                 // Hiển thị một Dialog đẹp mắt chứa thông tin
                 String msg = String.format(
                     "THÔNG TIN CHI TIẾT VÉ\n\n" +
                     "- Mã vé:\t%s\n" +
                     "- Mã Phiếu (PNR):\t%s\n" +
                     "- Chuyến bay:\t%s\n" +
                     "- Ghế:\t%s\n" +
                     "- Hạng vé:\t%s\n" +
                     "- Giá vé:\t%s\n" +
                     "- Trạng thái:\t%s", 
                     maVe, pnr, chuyenBay, ghe, hangVe, gia, trangThai);
                 JOptionPane.showMessageDialog(this, msg, "Chi Tiết Vé", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Nút Đổi vé
        btnDoiVe.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                String maVe = table.getValueAt(row, 0).toString();
                
                // Chỗ này gọi Form Đổi vé (Nếu bạn đã tạo form DoiVePanel thì gỡ dấu // ra)
                JFrame frame = new JFrame("Yêu cầu Đổi vé");
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                // frame.setContentPane(new DoiVePanel(maVe, maNguoiDung));
                frame.setVisible(true);
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<VeBan> list = veBanDAO.selectByMaHK(maNguoiDung);
        
        for(VeBan v : list) {
            String giaStr = String.format("%,.0f VND", v.getGiaVe());
            tableModel.addRow(new Object[]{
                v.getMaVe(),
                v.getMaChuyenBay(),
                v.getMaPhieuDatVe(),
                v.getMaGhe(),
                v.getMaHangVe(),
                giaStr,
                v.getTrangThaiVe()
            });
        }
    }

    // Hàm check xem vé có thỏa mãn điều kiện đổi vé không
    private void checkDieuKienDoiVe(String maVe) {
        List<VeBan> dsVeDoi = veBanDAO.selectVeCoTheDoi(maNguoiDung);
        boolean coTheDoi = false;
        
        for(VeBan v : dsVeDoi){
            if(v.getMaVe().equals(maVe)){
                coTheDoi = true;
                break;
            }
        }
        btnDoiVe.setEnabled(coTheDoi);
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