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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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

    // ================= GIAO DIỆN POPUP HTML (CÓ VẼ BẢNG) =================
    private void hienThiPopupChiTiet(String maHD, String maPNR, String ngay, String tongTien, String phuongThuc) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof Frame) dialog = new JDialog((Frame) parentWindow, "Hóa Đơn Điện Tử", true);
        else if (parentWindow instanceof Dialog) dialog = new JDialog((Dialog) parentWindow, "Hóa Đơn Điện Tử", true);
        else { dialog = new JDialog(); dialog.setModal(true); }
        
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        ThanhToanDTO dto = null;
        try { dto = new HoaDonDAO().getChiTietThanhToan(maPNR); } catch(Exception ex) { ex.printStackTrace(); }

        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        String html = "<html><body style='font-family: Arial; padding: 10px;'>";
        html += "<h2 style='color: #1c3060; text-align: center;'>HÓA ĐƠN ĐIỆN TỬ</h2><hr/>";
        html += "<b>Mã Hóa Đơn:</b> " + maHD + "&nbsp;&nbsp;&nbsp;&nbsp;<b>PNR:</b> <span style='color: green; font-size: 14px;'><b>" + maPNR + "</b></span><br/><br/>";
        html += "<b>Ngày thanh toán:</b> " + ngay + "<br/><br/>";
        
        if (dto != null) {
            html += "<b>Khách hàng:</b> " + (dto.tenKH!=null?dto.tenKH:"") + " - <b>SĐT:</b> " + (dto.sdt!=null?dto.sdt:"") + "<br/><br/>";
            html += "<b>Hành trình:</b> " + (dto.tuyenBay!=null?dto.tuyenBay:"") + " (" + (dto.gioDi!=null?dto.gioDi:"") + ")<br/><br/>";
            
            // Vẽ bảng HTML trên Popup
            if (dto.danhSachChiTiet != null && !dto.danhSachChiTiet.isEmpty()) {
                html += "<table border='1' style='border-collapse: collapse; width: 100%;'>";
                html += "<tr style='background-color: #f2f2f2;'><th style='padding:5px;'>Nội dung</th><th>SL</th><th>Đơn giá</th><th>Thành tiền</th></tr>";
                for(ThanhToanDTO.MucHoaDon muc : dto.danhSachChiTiet) {
                    html += "<tr>";
                    html += "<td style='padding:5px;'>" + muc.tenMuc + "</td>";
                    html += "<td style='text-align:center;'>" + muc.soLuong + "</td>";
                    html += "<td style='text-align:right; padding-right:5px;'>" + vn.format(muc.donGia) + "</td>";
                    html += "<td style='text-align:right; padding-right:5px;'>" + vn.format(muc.thanhTien) + "</td>";
                    html += "</tr>";
                }
                html += "</table><br/>";
            }
        }
        
        html += "<div style='text-align: right; font-size: 16px;'><b>TỔNG TIỀN:</b> <span style='color: red; font-size: 18px;'><b>" + tongTien + "</b></span></div>";
        html += "</body></html>";

        JLabel lblContent = new JLabel(html);
        lblContent.setVerticalAlignment(SwingConstants.TOP);
        dialog.add(new JScrollPane(lblContent), BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlFooter.setBackground(Color.WHITE);
        JButton btnExport = new JButton("Xuất Hóa Đơn (PDF)");
        btnExport.setBackground(new Color(220, 38, 38)); btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        final ThanhToanDTO finalDto = dto;
        btnExport.addActionListener(e -> {
            xuatPDF(maHD, maPNR, ngay, tongTien, phuongThuc, finalDto);
            dialog.dispose();
        });

        pnlFooter.add(btnExport);
        dialog.add(pnlFooter, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ================= XUẤT PDF CÓ KẺ BẢNG VÀ FONT TIẾNG VIỆT =================
    private void xuatPDF(String maHD, String maPNR, String ngay, String tongTien, String phuongThuc, ThanhToanDTO dto) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Hóa Đơn PDF");
        fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maHD + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".pdf")) file = new java.io.File(file.getAbsolutePath() + ".pdf");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                com.itextpdf.text.Font fontTitle, fontBold, fontNormal;
                try {
                    com.itextpdf.text.pdf.BaseFont arial = com.itextpdf.text.pdf.BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", com.itextpdf.text.pdf.BaseFont.IDENTITY_H, com.itextpdf.text.pdf.BaseFont.EMBEDDED);
                    fontTitle = new com.itextpdf.text.Font(arial, 20, com.itextpdf.text.Font.BOLD);
                    fontBold = new com.itextpdf.text.Font(arial, 12, com.itextpdf.text.Font.BOLD);
                    fontNormal = new com.itextpdf.text.Font(arial, 12, com.itextpdf.text.Font.NORMAL);
                } catch (Exception ex) {
                    fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                    fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                    fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);
                }
                
                document.add(new Paragraph("HÓA ĐƠN ĐIỆN TỬ - VÉ MÁY BAY", fontTitle));
                document.add(new Paragraph("---------------------------------------------------------------------------------"));
                document.add(new Paragraph("Mã Hóa Đơn: " + maHD + "   |   PNR: " + maPNR, fontBold));
                document.add(new Paragraph("Ngày thanh toán: " + ngay, fontNormal));
                
                if (dto != null) {
                    document.add(new Paragraph("Khách hàng: " + (dto.tenKH!=null?dto.tenKH:"") + "  - SĐT: " + (dto.sdt!=null?dto.sdt:""), fontNormal));
                    document.add(new Paragraph("Hành trình: " + (dto.tuyenBay!=null?dto.tuyenBay:"") + " (" + (dto.gioDi!=null?dto.gioDi:"") + ")", fontNormal));
                    
                    document.add(new Paragraph(" ")); 
                    
                    // VẼ BẢNG CHI TIẾT
                    if (dto.danhSachChiTiet != null && !dto.danhSachChiTiet.isEmpty()) {
                        PdfPTable table = new PdfPTable(4);
                        table.setWidthPercentage(100);
                        table.setWidths(new float[]{5f, 1f, 2f, 2f});
                        
                        String[] headers = {"Nội dung dịch vụ", "SL", "Đơn giá", "Thành tiền"};
                        for (String h : headers) {
                            PdfPCell cell = new PdfPCell(new com.itextpdf.text.Phrase(h, fontBold));
                            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                            cell.setPadding(5);
                            table.addCell(cell);
                        }
                        
                        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
                        for (ThanhToanDTO.MucHoaDon muc : dto.danhSachChiTiet) {
                            PdfPCell c1 = new PdfPCell(new Paragraph(muc.tenMuc, fontNormal)); c1.setPadding(5);
                            PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(muc.soLuong), fontNormal)); c2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER); c2.setPadding(5);
                            PdfPCell c3 = new PdfPCell(new Paragraph(vn.format(muc.donGia), fontNormal)); c3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT); c3.setPadding(5);
                            PdfPCell c4 = new PdfPCell(new Paragraph(vn.format(muc.thanhTien), fontNormal)); c4.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT); c4.setPadding(5);
                            table.addCell(c1); table.addCell(c2); table.addCell(c3); table.addCell(c4);
                        }
                        document.add(table);
                    }
                }
                
                document.add(new Paragraph("---------------------------------------------------------------------------------"));
                document.add(new Paragraph("Phương thức thanh toán: " + phuongThuc, fontNormal));
                
                Paragraph pTong = new Paragraph("TỔNG TIỀN: " + tongTien, fontTitle);
                pTong.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                document.add(pTong);
                
                document.close();
                JOptionPane.showMessageDialog(this, "Xuất hóa đơn thành công!\nĐã lưu tại: " + file.getAbsolutePath(), "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}