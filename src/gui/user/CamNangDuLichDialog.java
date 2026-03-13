package gui.user;

import dal.KhuyenMaiDAO;
import dal.ThuHangDAO;
import model.KhuyenMai;
import model.ThuHang;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CamNangDuLichDialog extends JDialog {

    public CamNangDuLichDialog(Frame parent) {
        super(parent, "Cẩm Nang Du Lịch & Ưu Đãi Chuyến Bay", true); // true = Modal (Bắt buộc đóng mới bấm được trang dưới)
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;'>");
        html.append("<h2 style='color: #1c3060; text-align: center; margin-bottom: 5px;'>✈️ CẨM NANG BAY KHAIRLINE ✈️</h2>");
        html.append("<hr style='border: 0.5px solid #ccc; margin-bottom: 20px;'/>");

        // --- PHẦN 1: MẸO DU LỊCH CƠ BẢN ---
        html.append("<h3 style='color: #dc2626;'>1. Mẹo Chuẩn Bị Trước Chuyến Bay</h3>");
        html.append("<ul style='margin-top: 5px;'>");
        html.append("<li><b>Giấy tờ:</b> Luôn mang theo CCCD/Hộ chiếu bản gốc còn hạn.</li>");
        html.append("<li><b>Hành lý:</b> Hành lý xách tay tối đa 7kg. Không mang vật sắc nhọn, chất lỏng > 100ml.</li>");
        html.append("<li><b>Check-in:</b> Khuyến khích check-in online trước 24h. Đến sân bay trước 2 tiếng (Nội địa) hoặc 3 tiếng (Quốc tế).</li>");
        html.append("</ul>");

        // --- PHẦN 2: LẤY CÁC KHUYẾN MÃI TỪ DATABASE ---
        html.append("<h3 style='color: #dc2626;'>2. Ưu Đãi Khuyến Mãi Hôm Nay</h3>");
        try {
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            List<KhuyenMai> listKM = kmDAO.getKhuyenMaiHopLe();
            if (listKM != null && !listKM.isEmpty()) {
                html.append("<ul style='margin-top: 5px;'>");
                for (KhuyenMai km : listKM) {
                    String dvi = "PHAN_TRAM".equals(km.getLoaiKM()) ? "%" : " VNĐ";
                    html.append("<li>Mã <b style='color: #22c55e;'>").append(km.getMaKhuyenMai()).append("</b>: ")
                        .append(km.getTenKM()).append(" (Giảm mạnh <b>").append(km.getGiaTri()).append(dvi).append("</b>)</li>");
                }
                html.append("</ul>");
            } else {
                html.append("<p style='color: #666;'><i>Hiện tại chưa có chương trình khuyến mãi nào. Quý khách vui lòng theo dõi thêm!</i></p>");
            }
        } catch (Exception e) {
            html.append("<p style='color: #666;'><i>Không thể tải ưu đãi lúc này.</i></p>");
        }

        // --- PHẦN 3: ĐẶC QUYỀN HẠNG THÀNH VIÊN TỪ DATABASE ---
        html.append("<h3 style='color: #dc2626;'>3. Đặc Quyền Hạng Thành Viên</h3>");
        try {
            ThuHangDAO thDAO = new ThuHangDAO();
            List<ThuHang> listTH = thDAO.getAll();
            if (listTH != null && !listTH.isEmpty()) {
                html.append("<ul style='margin-top: 5px;'>");
                for (ThuHang th : listTH) {
                    html.append("<li>Hạng <b style='color: #eab308;'>").append(th.getTenThuHang()).append("</b>: Cần tích lũy ")
                        .append(th.getDiemToiThieu()).append(" điểm. (Giảm <b>").append(th.getTiLeGiam() * 100).append("%</b> giá vé)</li>");
                }
                html.append("</ul>");
            }
        } catch (Exception e) {}

        html.append("</body></html>");

        // KHUNG HIỂN THỊ CÓ THANH CUỘN
        JLabel lblContent = new JLabel(html.toString());
        lblContent.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPane = new JScrollPane(lblContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);

        // NÚT ĐÓNG BÊN DƯỚI
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        pnlFooter.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Đã hiểu & Đóng");
        btnClose.setBackground(new Color(220, 38, 38));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(150, 40));
        btnClose.setFocusPainted(false);
        
        // Tắt Dialog khi nhấn Đóng
        btnClose.addActionListener(e -> dispose());
        
        pnlFooter.add(btnClose);
        add(pnlFooter, BorderLayout.SOUTH);
    }
}