package gui.user;

import dal.KhuyenMaiDAO;
import model.KhuyenMai;
import model.NguoiDung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {

    private NguoiDung currentUser;

    public KhuyenMaiPanel(NguoiDung user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initComponents();
    }

    private void initComponents() {
        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(18, 32, 64)); // Màu xanh đậm hàng không
        pnlHeader.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel("🎉 DANH SÁCH KHUYẾN MÃI ĐANG DIỄN RA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        
        JLabel lblSub = new JLabel("Hãy copy mã và dán vào bước Thanh toán để nhận ưu đãi nhé!");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(new Color(200, 200, 200));
        pnlHeader.add(lblSub, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // --- DANH SÁCH KHUYẾN MÃI ---
        JPanel pnlList = new JPanel();
        pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.Y_AXIS));
        pnlList.setBackground(new Color(245, 247, 250));
        pnlList.setBorder(new EmptyBorder(20, 20, 20, 20));

        try {
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            List<KhuyenMai> list = kmDAO.getKhuyenMaiHopLe(); // Lấy các mã còn hiệu lực

            if (list == null || list.isEmpty()) {
                JLabel lblEmpty = new JLabel("Hiện tại chưa có chương trình khuyến mãi nào.");
                lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                pnlList.add(lblEmpty);
            } else {
                // THUẬT TOÁN SẮP XẾP: Mã nào có giá trị giảm (giaTri) cao nhất sẽ nổi lên trên cùng
                list.sort((km1, km2) -> km2.getGiaTri().compareTo(km1.getGiaTri()));

                for (int i = 0; i < list.size(); i++) {
                    // Cờ (Flag) xác định thẻ đầu tiên (Ưu tiên)
                    boolean isTop1 = (i == 0); 
                    pnlList.add(createVoucherCard(list.get(i), isTop1));
                    pnlList.add(Box.createVerticalStrut(15)); // Khoảng cách giữa các thẻ
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(pnlList);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    // THIẾT KẾ CARD HIỂN THỊ KHUYẾN MÃI
    private JPanel createVoucherCard(KhuyenMai km, boolean isTop1) {
        JPanel cardContainer = new JPanel(new BorderLayout());
        cardContainer.setOpaque(false);
        cardContainer.setMaximumSize(new Dimension(800, isTop1 ? 140 : 120));

        // NẾU LÀ TOP 1 -> THÊM TIÊU ĐỀ HOT
        if (isTop1) {
            JLabel lblHot = new JLabel("🔥 ƯU ĐÃI HOT NHẤT");
            lblHot.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblHot.setForeground(new Color(239, 68, 68)); // Màu đỏ tươi
            lblHot.setBorder(new EmptyBorder(0, 0, 5, 0));
            cardContainer.add(lblHot, BorderLayout.NORTH);
        }

        JPanel card = new JPanel(new BorderLayout(15, 0));
        // Nếu là thẻ Ưu tiên: Nền vàng nhạt, Viền cam đậm x2. Nếu thẻ thường: Nền trắng, viền xám mỏng
        card.setBackground(isTop1 ? new Color(255, 250, 240) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isTop1 ? new Color(255, 193, 7) : new Color(220, 220, 220), isTop1 ? 2 : 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Cột trái: Giá trị giảm
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(140, 100));
        pnlLeft.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(240, 240, 240))); 
        
        String dvi = "PHAN_TRAM".equals(km.getLoaiKM()) ? "%" : "K";
        String giaTri = "PHAN_TRAM".equals(km.getLoaiKM()) ? 
                        km.getGiaTri().toString() : 
                        String.valueOf(km.getGiaTri().intValue() / 1000);

        JLabel lblGiaTri = new JLabel("GIẢM " + giaTri + dvi);
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGiaTri.setForeground(isTop1 ? new Color(220, 38, 38) : new Color(30, 64, 175)); // Top 1 chữ đỏ, thường chữ xanh
        lblGiaTri.setHorizontalAlignment(SwingConstants.CENTER);
        pnlLeft.add(lblGiaTri, BorderLayout.CENTER);

        // Cột giữa: Thông tin chi tiết
        JPanel pnlCenter = new JPanel(new GridLayout(3, 1));
        pnlCenter.setOpaque(false);
        
        JLabel lblTen = new JLabel(km.getTenKM());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(18, 32, 64));
        
        JLabel lblDieuKien = new JLabel("Đơn tối thiểu: " + String.format("%,.0f", km.getDonHangToiThieu()) + " VNĐ");
        lblDieuKien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDieuKien.setForeground(Color.DARK_GRAY);
        
        JLabel lblMa = new JLabel("Mã: " + km.getMaKhuyenMai());
        lblMa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMa.setForeground(new Color(34, 197, 94)); // Màu xanh lá
        
        pnlCenter.add(lblTen);
        pnlCenter.add(lblDieuKien);
        pnlCenter.add(lblMa);

        // Cột phải: Nút Copy Mã
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setOpaque(false);
        JButton btnCopy = new JButton("Copy Mã");
        // Nút top 1 màu Vàng chữ Xanh đậm. Nút thường màu Xanh đậm chữ Trắng
        btnCopy.setBackground(isTop1 ? new Color(255, 193, 7) : new Color(28, 48, 96));
        btnCopy.setForeground(isTop1 ? new Color(18, 32, 64) : Color.WHITE);
        btnCopy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCopy.setFocusPainted(false);
        btnCopy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCopy.addActionListener(e -> {
            // Copy mã vào Clipboard (Bộ nhớ tạm) của máy tính
            StringSelection stringSelection = new StringSelection(km.getMaKhuyenMai());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            
            JOptionPane.showMessageDialog(this, 
                "Đã copy mã: " + km.getMaKhuyenMai() + "\nBây giờ bạn có thể dán (Paste) vào ô khuyến mãi ở trang Thanh toán!", 
                "Copy thành công", JOptionPane.INFORMATION_MESSAGE);
        });
        pnlRight.add(btnCopy);

        card.add(pnlLeft, BorderLayout.WEST);
        card.add(pnlCenter, BorderLayout.CENTER);
        card.add(pnlRight, BorderLayout.EAST);

        cardContainer.add(card, BorderLayout.CENTER);

        return cardContainer;
    }
}