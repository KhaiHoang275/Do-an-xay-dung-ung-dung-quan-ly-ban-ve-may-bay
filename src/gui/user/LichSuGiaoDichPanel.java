package gui.user;

import dal.HoaDonDAO;
import dal.PhieuDatVeDAO;
import model.HoaDon;
import model.NguoiDung;
import model.PhieuDatVe;
import model.ThanhToanDTO;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class LichSuGiaoDichPanel extends JPanel {

    private NguoiDung currentUser;
    private JTable tblGiaoDich;
    private DefaultTableModel model;

    public LichSuGiaoDichPanel(NguoiDung user) {
        this.currentUser = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // HEADER
        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH & HÓA ĐƠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(28, 48, 96));
        add(lblTitle, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"Mã Hóa Đơn", "Mã Đặt Chỗ (PNR)", "Ngày Thanh Toán", "Tổng Tiền", "Phương Thức"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGiaoDich = new JTable(model);
        tblGiaoDich.setRowHeight(35);
        tblGiaoDich.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblGiaoDich.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblGiaoDich.setSelectionBackground(new Color(28, 48, 96));
        tblGiaoDich.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tblGiaoDich);
        add(scroll, BorderLayout.CENTER);

        // FOOTER
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(Color.WHITE);
        JButton btnChiTiet = new JButton("Xem chi tiết & In Hóa Đơn");
        btnChiTiet.setBackground(new Color(34, 197, 94));
        btnChiTiet.setForeground(Color.WHITE);
        btnChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlFooter.add(btnChiTiet);
        add(pnlFooter, BorderLayout.SOUTH);

        // SỰ KIỆN XEM CHI TIẾT
        btnChiTiet.addActionListener(e -> {
            int row = tblGiaoDich.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 giao dịch trên bảng để xem!");
                return;
            }
            String maHD = tblGiaoDich.getValueAt(row, 0).toString();
            String maPNR = tblGiaoDich.getValueAt(row, 1).toString();
            String ngay = tblGiaoDich.getValueAt(row, 2).toString();
            String tongTien = tblGiaoDich.getValueAt(row, 3).toString();
            String phuongThuc = tblGiaoDich.getValueAt(row, 4).toString();

            hienThiPopupChiTiet(maHD, maPNR, ngay, tongTien, phuongThuc);
        });
    }

    private void loadData() {
        if(currentUser == null) return;
        try {
            PhieuDatVeDAO pdvDAO = new PhieuDatVeDAO();
            HoaDonDAO hdDAO = new HoaDonDAO();
            ArrayList<PhieuDatVe> allPhieu = pdvDAO.selectAll();
            ArrayList<HoaDon> allHD = hdDAO.selectAll();
            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (PhieuDatVe pdv : allPhieu) {
                if (currentUser.getMaNguoiDung().equals(pdv.getMaNguoiDung())) {
                    for (HoaDon hd : allHD) {
                        if (pdv.getMaPhieuDatVe().equals(hd.getMaPhieuDatVe())) {
                            model.addRow(new Object[]{
                                hd.getMaHoaDon(), pdv.getMaPhieuDatVe(),
                                hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "",
                                vn.format(hd.getTongTien()) + " VNĐ", hd.getPhuongThuc()
                            });
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // GIAO DIỆN POPUP CHI TIẾT HÓA ĐƠN
    private void hienThiPopupChiTiet(String maHD, String maPNR, String ngay, String tongTien, String phuongThuc) {
        
        // ĐOẠN ĐÃ ĐƯỢC FIX LỖI CLASS CAST EXCEPTION
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Hóa Đơn Điện Tử", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Hóa Đơn Điện Tử", true);
        } else {
            dialog = new JDialog();
            dialog.setModal(true);
        }
        
        dialog.setSize(480, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        ThanhToanDTO dto = null;
        try {
            dto = new HoaDonDAO().getChiTietThanhToan(maPNR);
        } catch(Exception ex) { ex.printStackTrace(); }

        String html = "<html><body style='font-family: Arial; padding: 15px;'>";
        html += "<h2 style='color: #1c3060; text-align: center;'>VÉ MÁY BAY & HÓA ĐƠN</h2><hr/>";
        html += "<b>Mã Hóa Đơn:</b> " + maHD + "<br/><br/>";
        html += "<b>Mã Đặt Chỗ (PNR):</b> <span style='color: green; font-size: 14px;'><b>" + maPNR + "</b></span><br/><br/>";
        html += "<b>Ngày thanh toán:</b> " + ngay + "<br/><br/>";
        
        // NẾU CÓ DỮ LIỆU TỪ DTO THÌ HIỂN THỊ
        if (dto != null) {
            html += "<b>Khách hàng:</b> " + dto.tenKH + "<br/><br/>";
            html += "<b>Số điện thoại:</b> " + dto.sdt + "<br/><br/>";
            html += "<b>Tuyến bay:</b> " + dto.tuyenBay + "<br/><br/>";
            html += "<b>Vé / Ghế:</b> " + dto.veGhe + "<br/><br/>";
            html += "<b>Giờ đi:</b> " + dto.gioDi + "<br/><br/>";
        } else {
            html += "<i>(Đang cập nhật chi tiết chuyến bay...)</i><br/><br/>";
        }
        
        html += "<hr/><b>Phương thức:</b> " + phuongThuc + "<br/><br/>";
        html += "<b>TỔNG TIỀN:</b> <span style='color: red; font-size: 18px;'><b>" + tongTien + "</b></span>";
        html += "</body></html>";

        JLabel lblContent = new JLabel(html);
        lblContent.setVerticalAlignment(SwingConstants.TOP);
        dialog.add(new JScrollPane(lblContent), BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlFooter.setBackground(Color.WHITE);
        JButton btnExport = new JButton("In Hóa Đơn (PDF)");
        btnExport.setBackground(new Color(220, 38, 38));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setPreferredSize(new Dimension(200, 40));
        
        final ThanhToanDTO finalDto = dto;
        btnExport.addActionListener(e -> {
            xuatPDF(maHD, maPNR, ngay, tongTien, phuongThuc, finalDto);
            dialog.dispose(); // Tự động đóng popup sau khi in xong
        });

        pnlFooter.add(btnExport);
        dialog.add(pnlFooter, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    // THUẬT TOÁN XUẤT PDF
    private void xuatPDF(String maHD, String maPNR, String ngay, String tongTien, String phuongThuc, ThanhToanDTO dto) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Hóa Đơn (PDF)");
        fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maHD + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                document.open();
                
                // Note: PDF mặc định sử dụng font không dấu để tránh lỗi Font chữ Tiếng Việt
                com.itextpdf.text.Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                com.itextpdf.text.Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);
                
                document.add(new Paragraph("HOA DON DIEN TU - VEMAYBAY", fontTitle));
                document.add(new Paragraph("--------------------------------------------------"));
                document.add(new Paragraph("Ma Hoa Don: " + maHD, fontNormal));
                document.add(new Paragraph("Ma Dat Cho (PNR): " + maPNR, fontNormal));
                document.add(new Paragraph("Ngay thanh toan: " + ngay, fontNormal));
                
                if (dto != null) {
                    document.add(new Paragraph("--------------------------------------------------"));
                    document.add(new Paragraph("Khach hang: " + dto.tenKH, fontNormal));
                    document.add(new Paragraph("SDT: " + dto.sdt, fontNormal));
                    document.add(new Paragraph("Tuyen bay: " + dto.tuyenBay, fontNormal));
                    document.add(new Paragraph("Ve / Ghe: " + dto.veGhe, fontNormal));
                    document.add(new Paragraph("Gio di: " + dto.gioDi, fontNormal));
                }
                
                document.add(new Paragraph("--------------------------------------------------"));
                document.add(new Paragraph("Phuong thuc: " + phuongThuc, fontNormal));
                document.add(new Paragraph("TONG TIEN: " + tongTien, fontTitle));
                
                document.close();
                JOptionPane.showMessageDialog(this, "Xuất hóa đơn PDF thành công!", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}