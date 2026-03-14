package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import bll.HoaDonBUS;
import dal.HoaDonDAO; // Đã import thêm DAO để lấy tiền giảm giá
import model.ThanhToanDTO;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ThanhToanHoaDonPanel extends JPanel {

    private String maHD, maPhieu, ngayLap, tongTien, phuongThuc, thue;

    private JLabel valMaPhieu, valTenKH, valSDT, valEmail;
    private JLabel valTuyenBay, valMaVe, valGioDi, valGioDen;
    // BỔ SUNG: Thêm biến lblGiamGia
    private JLabel lblGiaVe, lblThue, lblDichVu, lblGiamGia, lblTongTien;
    private JComboBox<String> cboPhuongThuc;
    private JButton btnXuatPDF, btnDong;
    private JLabel lblTienGhe;

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_PDF = new Color(234, 179, 8);       
    private final Color BTN_DELETE = new Color(239, 68, 68);    

    public ThanhToanHoaDonPanel(String maHD, String maPhieu, String ngayLap, String tongTien, String phuongThuc, String thue) {
        this.maHD = maHD;
        this.maPhieu = maPhieu;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        this.thue = thue;
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDynamicData(); 
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("CHI TIẾT HÓA ĐƠN - " + maHD, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/icons/voucher.png")).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
            lblTitle.setIcon(icon);
        } catch (Exception e) {}
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setOpaque(false);

        // ================= CỘT TRÁI =================
        JPanel leftPanel = new JPanel(new BorderLayout(20, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(600, 0));

        JPanel pnlKhachHang = createCardPanel("THÔNG TIN KHÁCH HÀNG");
        pnlKhachHang.setLayout(new BoxLayout(pnlKhachHang, BoxLayout.Y_AXIS)); 
        
        valMaPhieu = createValueLabel(); valTenKH = createValueLabel(); 
        valSDT = createValueLabel(); valEmail = createValueLabel(); 
        
        pnlKhachHang.add(createDetailRow("Mã Phiếu Đặt:", valMaPhieu));
        pnlKhachHang.add(createDetailRow("Danh sách Khách:", valTenKH)); 
        pnlKhachHang.add(createDetailRow("Số Điện Thoại:", valSDT));
        pnlKhachHang.add(createDetailRow("Email:", valEmail));

        JPanel pnlChuyenBay = createCardPanel("THÔNG TIN CHUYẾN BAY");
        pnlChuyenBay.setLayout(new BoxLayout(pnlChuyenBay, BoxLayout.Y_AXIS));
        
        valTuyenBay = createValueLabel(); valMaVe = createValueLabel(); 
        valGioDi = createValueLabel(); valGioDen = createValueLabel(); 
        
        pnlChuyenBay.add(createDetailRow("Tuyến Bay:", valTuyenBay));
        pnlChuyenBay.add(createDetailRow("Danh Sách Ghế:", valMaVe)); 
        pnlChuyenBay.add(createDetailRow("Thời Gian Đi:", valGioDi));
        pnlChuyenBay.add(createDetailRow("Thời Gian Đến:", valGioDen));

        leftPanel.add(pnlKhachHang, BorderLayout.NORTH);
        leftPanel.add(pnlChuyenBay, BorderLayout.CENTER);

        // ================= CỘT PHẢI =================
        JPanel rightPanel = createCardPanel("TỔNG KẾT THANH TOÁN");
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        lblGiaVe = createPriceLabel("0 VND");
        lblTienGhe = createPriceLabel("0 VND");
        lblDichVu = createPriceLabel("0 VND");
        lblThue = createPriceLabel("0 VND");
        lblGiamGia = createPriceLabel("0 VND"); // BỔ SUNG: Khởi tạo nhãn Giảm giá
        lblTongTien = createPriceLabel("0 VND");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTongTien.setForeground(PRIMARY);

        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển Khoản", "Thẻ tín dụng", "Ví điện tử"});
        cboPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cboPhuongThuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cboPhuongThuc.setEnabled(false);

        rightPanel.add(createSummaryRow("Tổng Giá vé gốc:", lblGiaVe)); rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(createSummaryRow("Tổng tiền ghế:", lblTienGhe));
        rightPanel.add(createSummaryRow("Tổng DV bổ sung:", lblDichVu)); rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(createSummaryRow("Thuế VAT (10%):", lblThue)); rightPanel.add(Box.createVerticalStrut(15));
        
        // BỔ SUNG: Thêm dòng Giảm giá vào giao diện
        rightPanel.add(createSummaryRow("Giảm giá / Khuyến mãi:", lblGiamGia)); rightPanel.add(Box.createVerticalStrut(15));
        
        JSeparator sep = new JSeparator(); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        rightPanel.add(sep); rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(createSummaryRow("TỔNG TIỀN:", lblTongTien)); rightPanel.add(Box.createVerticalStrut(30));
        
        JLabel lblPT = new JLabel("Phương thức thanh toán:");
        lblPT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightPanel.add(lblPT); rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(cboPhuongThuc); rightPanel.add(Box.createVerticalGlue()); 

        btnXuatPDF = createRoundedButton("Xem & Xuất PDF", BTN_PDF, null, 20);
        btnDong = createRoundedButton("Đóng Khung", BTN_DELETE, null, 20);

        JPanel pnlButtons = new JPanel(new GridLayout(2, 1, 10, 15));
        pnlButtons.setOpaque(false);
        pnlButtons.add(btnXuatPDF);
        pnlButtons.add(btnDong);
        rightPanel.add(pnlButtons);

        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);
        add(mainContent, BorderLayout.CENTER);

        lblTienGhe = createPriceLabel("0 VND");
        rightPanel.add(createSummaryRow("Tổng tiền ghế:", lblTienGhe)); 
        rightPanel.add(Box.createVerticalStrut(15));

        setupListeners();
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel(""); lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); lbl.setForeground(new Color(15, 23, 42)); return lbl;
    }

    private JPanel createDetailRow(String title, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(10, 0)); row.setOpaque(false);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15)); lblTitle.setForeground(new Color(100, 116, 139)); lblTitle.setPreferredSize(new Dimension(150, 30));
        row.add(lblTitle, BorderLayout.WEST); row.add(valLabel, BorderLayout.CENTER);
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)), new EmptyBorder(5, 0, 5, 0)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        return row;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), BorderFactory.createCompoundBorder(new TitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), TABLE_HEADER), new EmptyBorder(15, 20, 15, 20))));
        return panel;
    }

    private JLabel createPriceLabel(String text) {
        JLabel lbl = new JLabel(text); lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); lbl.setForeground(new Color(30, 41, 59)); lbl.setHorizontalAlignment(SwingConstants.RIGHT); return lbl;
    }

    private JPanel createSummaryRow(String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout()); row.setOpaque(false);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        row.add(lblTitle, BorderLayout.WEST); row.add(valueLabel, BorderLayout.EAST); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return row;
    }

    private JButton createRoundedButton(String text, Color bgColor, String iconPath, int iconSize) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bgColor.darker()); else if (getModel().isRollover()) g2.setColor(bgColor.brighter()); else g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); g2.dispose(); super.paintComponent(g); 
            }
        };
        btn.setPreferredSize(new Dimension(0, 45)); btn.setForeground(Color.WHITE); btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDynamicData() {
        valMaPhieu.setText(maPhieu);
        if (phuongThuc != null) {
            for (int i = 0; i < cboPhuongThuc.getItemCount(); i++) {
                if (cboPhuongThuc.getItemAt(i).equalsIgnoreCase(phuongThuc.trim())) {
                    cboPhuongThuc.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        HoaDonBUS bus = new HoaDonBUS();
        ThanhToanDTO data = bus.layChiTietThanhToan(maPhieu); 
        
        if (data != null) {
            valSDT.setText(data.sdt);
            valEmail.setText(data.email);
            valTuyenBay.setText(data.tuyenBay);
            valGioDi.setText(data.gioDi);
            valGioDen.setText(data.gioDen);
            valTenKH.setText(data.tenKH); 
            valMaVe.setText(data.veGhe);  

            BigDecimal veGoc = data.giaVeGoc != null ? data.giaVeGoc : BigDecimal.ZERO;
            BigDecimal dvBS = data.tongTienDichVu != null ? data.tongTienDichVu : BigDecimal.ZERO;
            
            lblGiaVe.setText(String.format("%,.0f VND", veGoc));
            lblDichVu.setText(String.format("%,.0f VND", dvBS));
            
            String rawThue = this.thue.replaceAll("[^0-9]", "");
            BigDecimal thueVal = new BigDecimal(rawThue.isEmpty() ? "0" : rawThue);
            lblThue.setText(String.format("%,.0f VND", thueVal));
            
            // --- PHẦN CẬP NHẬT MỚI ---
            HoaDonDAO dao = new HoaDonDAO();
            BigDecimal tienGhe = dao.layTongTienGheCuaHoaDon(maHD);
            lblTienGhe.setText(String.format("%,.0f VND", tienGhe));

            BigDecimal tienGiam = dao.layTienGiamGiaTuPhieuDat(maPhieu);
            if (tienGiam.compareTo(BigDecimal.ZERO) > 0) {
                lblGiamGia.setText(String.format("-%,.0f VND", tienGiam));
                lblGiamGia.setForeground(new Color(34, 197, 94));
            } else {
                lblGiamGia.setText("0 VND");
            }
            // -------------------------
            
            String rawTongTien = this.tongTien.replaceAll("[^0-9]", ""); 
            BigDecimal finalTotal = new BigDecimal(rawTongTien.isEmpty() ? "0" : rawTongTien);
            lblTongTien.setText(String.format("%,.0f VND", finalTotal));
            
        } else {
            valTenKH.setText("Dữ liệu Lỗi/Trống");
            valMaVe.setText("Dữ liệu Lỗi/Trống");
        }
    }

    private void setupListeners() {
        btnDong.addActionListener(e -> {
            Window currentWindow = SwingUtilities.getWindowAncestor(this);
            if (currentWindow != null) { 
                currentWindow.dispose(); 
            }
        });

        btnXuatPDF.addActionListener(e -> xuatVaMoFilePDF());
    }

    private void xuatVaMoFilePDF() {
        JFileChooser fileChooser = new JFileChooser(); 
        fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maHD + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) { fileToSave = new File(filePath + ".pdf"); }
            
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                BaseFont bf;
                try {
                    bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (Exception ex) {
                    bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                }
                
                com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 20, com.itextpdf.text.Font.BOLD, BaseColor.RED);
                com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD, new BaseColor(30, 41, 59)); 
                com.itextpdf.text.Font fontKey = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL, new BaseColor(100, 116, 139)); 
                com.itextpdf.text.Font fontValue = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD, new BaseColor(15, 23, 42)); 

                Paragraph title = new Paragraph("CHI TIẾT HÓA ĐƠN - " + maHD, fontTitle);
                title.setAlignment(Element.ALIGN_LEFT); 
                document.add(title);
                
                String thoiGianIn = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                Paragraph subTitle = new Paragraph("Mã PNR: " + valMaPhieu.getText() + " | In lúc: " + thoiGianIn, fontKey);
                subTitle.setAlignment(Element.ALIGN_LEFT); 
                subTitle.setSpacingAfter(20); 
                document.add(subTitle);

                document.add(new Paragraph("THÔNG TIN KHÁCH HÀNG", fontHeader));
                PdfPTable tblKH = new PdfPTable(2);
                tblKH.setWidthPercentage(100); tblKH.setSpacingBefore(10); tblKH.setSpacingAfter(20); tblKH.setWidths(new float[]{3f, 7f});
                
                String pdfKhach = valTenKH.getText().replaceAll("<[^>]*>", ""); 
                
                addPdfRow(tblKH, "Mã Phiếu Đặt:", valMaPhieu.getText(), fontKey, fontValue);
                addPdfRow(tblKH, "Danh sách Khách:", pdfKhach, fontKey, fontValue);
                addPdfRow(tblKH, "Số Điện Thoại:", valSDT.getText(), fontKey, fontValue);
                addPdfRow(tblKH, "Email:", valEmail.getText(), fontKey, fontValue);
                document.add(tblKH);

                document.add(new Paragraph("THÔNG TIN CHUYẾN BAY", fontHeader));
                PdfPTable tblCB = new PdfPTable(2);
                tblCB.setWidthPercentage(100); tblCB.setSpacingBefore(10); tblCB.setSpacingAfter(20); tblCB.setWidths(new float[]{3f, 7f});
                
                String pdfGhe = valMaVe.getText().replaceAll("<[^>]*>", "");
                
                addPdfRow(tblCB, "Tuyến Bay:", valTuyenBay.getText(), fontKey, fontValue);
                addPdfRow(tblCB, "Danh Sách Ghế:", pdfGhe, fontKey, fontValue);
                addPdfRow(tblCB, "Thời Gian Đi:", valGioDi.getText(), fontKey, fontValue);
                addPdfRow(tblCB, "Thời Gian Đến:", valGioDen.getText(), fontKey, fontValue);
                document.add(tblCB);

                document.add(new Paragraph("TỔNG KẾT THANH TOÁN", fontHeader));
                PdfPTable tblTT = new PdfPTable(2);
                tblTT.setWidthPercentage(100); tblTT.setSpacingBefore(10); tblTT.setSpacingAfter(20); tblTT.setWidths(new float[]{6f, 4f});

                addPdfRow(tblTT, "Phương thức TT:", cboPhuongThuc.getSelectedItem().toString(), fontKey, fontValue);
                addPdfRow(tblTT, "Tổng Giá vé gốc:", lblGiaVe.getText(), fontKey, fontValue);
                addPdfRow(tblTT, "Tổng DV bổ sung:", lblDichVu.getText(), fontKey, fontValue);
                addPdfRow(tblTT, "Thuế VAT (10%):", lblThue.getText(), fontKey, fontValue);
                
                // BỔ SUNG: Thêm dòng hiển thị Giảm giá vào File PDF
                addPdfRow(tblTT, "Giảm giá / Khuyến mãi:", lblGiamGia.getText(), fontKey, fontValue);

                PdfPCell c1 = new PdfPCell(new Phrase("TỔNG TIỀN:", new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD, BaseColor.BLACK))); 
                c1.setBorder(Rectangle.BOTTOM); c1.setBorderColor(new BaseColor(241, 245, 249)); c1.setPaddingTop(12); c1.setPaddingBottom(12);
                
                PdfPCell c2 = new PdfPCell(new Phrase(lblTongTien.getText(), new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD, BaseColor.RED))); 
                c2.setBorder(Rectangle.BOTTOM); c2.setBorderColor(new BaseColor(241, 245, 249)); c2.setPaddingTop(12); c2.setPaddingBottom(12);
                
                tblTT.addCell(c1);
                tblTT.addCell(c2);
                document.add(tblTT);

                Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ của AirLiner!", new com.itextpdf.text.Font(bf, 11, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY));
                footer.setAlignment(Element.ALIGN_CENTER);
                footer.setSpacingBefore(30);
                document.add(footer);

                document.close(); 

                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (fileToSave.exists()) {
                        desktop.open(fileToSave);
                    }
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void addPdfRow(PdfPTable table, String key, String value, com.itextpdf.text.Font fKey, com.itextpdf.text.Font fValue) {
        PdfPCell cellKey = new PdfPCell(new Phrase(key, fKey));
        cellKey.setBorder(Rectangle.BOTTOM);
        cellKey.setBorderColor(new BaseColor(241, 245, 249)); 
        cellKey.setPaddingTop(10);
        cellKey.setPaddingBottom(10);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(value, fValue));
        cellValue.setBorder(Rectangle.BOTTOM);
        cellValue.setBorderColor(new BaseColor(241, 245, 249));
        cellValue.setPaddingTop(10);
        cellValue.setPaddingBottom(10);

        table.addCell(cellKey);
        table.addCell(cellValue);
    }
}