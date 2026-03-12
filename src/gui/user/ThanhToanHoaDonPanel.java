package gui.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import bll.HoaDonBUS;
import model.ThanhToanDTO;
import model.DatVeSession;
import model.ThongTinHanhKhach;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Thư viện iTextPDF
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
    private DatVeSession session; 

    private JLabel valMaPhieu, valTenKH, valSDT, valEmail;
    private JLabel valTuyenBay, valMaVe, valGioDi;
    private JLabel lblGiaVe, lblThue, lblDichVu, lblTongTien;
    private JComboBox<String> cboPhuongThuc;
    private JButton btnXuatPDF, btnDong;

    // Biến lưu trữ chuỗi để in PDF đồng bộ với Giao diện
    private String pdfKhachStr = "";
    private String pdfTuyenBayStr = "";
    private String pdfKhoiHanhStr = "";
    private String pdfChiTietVeStr = "";

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_PDF = new Color(234, 179, 8);       
    private final Color BTN_DELETE = new Color(239, 68, 68);    

    public ThanhToanHoaDonPanel(String maHD, String maPhieu, String ngayLap, String tongTien, String phuongThuc, String thue, DatVeSession session) {
        this.maHD = maHD;
        this.maPhieu = maPhieu;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        this.thue = thue;
        this.session = session;
        
        setLayout(new BorderLayout(20, 20));
        setOpaque(false); // XUYÊN THẤU ẢNH NỀN TỪ MAINFRAME
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDynamicData(); 
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("CHI TIẾT HÓA ĐƠN & VÉ - " + maHD, JLabel.LEFT);
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
        leftPanel.setPreferredSize(new Dimension(650, 0));

        JPanel pnlKhachHang = createCardPanel("THÔNG TIN NGƯỜI ĐẶT");
        pnlKhachHang.setLayout(new BoxLayout(pnlKhachHang, BoxLayout.Y_AXIS)); 
        
        valMaPhieu = createValueLabel(); valTenKH = createValueLabel(); 
        valSDT = createValueLabel(); valEmail = createValueLabel(); 
        
        pnlKhachHang.add(createDetailRow("Mã PNR:", valMaPhieu));
        pnlKhachHang.add(createDetailRow("Số Điện Thoại:", valSDT));
        pnlKhachHang.add(createDetailRow("Email:", valEmail));

        JPanel pnlChuyenBay = createCardPanel("THÔNG TIN HÀNH TRÌNH & CHI TIẾT VÉ");
        pnlChuyenBay.setLayout(new BoxLayout(pnlChuyenBay, BoxLayout.Y_AXIS));
        
        valTuyenBay = createValueLabel(); valGioDi = createValueLabel(); valMaVe = createValueLabel(); 
        
        pnlChuyenBay.add(createDetailRow("Tuyến Bay:", valTuyenBay));
        pnlChuyenBay.add(createDetailRow("Khởi Hành:", valGioDi)); 
        pnlChuyenBay.add(createDetailRow("Chi Tiết Vé:", valMaVe)); // Sẽ chứa thông tin Hành khách, Số ghế, Hành lý chi tiết

        leftPanel.add(pnlKhachHang, BorderLayout.NORTH);
        
        JScrollPane scrollLeft = new JScrollPane(pnlChuyenBay);
        scrollLeft.setBorder(null); scrollLeft.setOpaque(false); scrollLeft.getViewport().setOpaque(false);
        scrollLeft.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(scrollLeft, BorderLayout.CENTER);

        // ================= CỘT PHẢI =================
        JPanel rightPanel = createCardPanel("TỔNG KẾT THANH TOÁN");
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        lblGiaVe = createPriceLabel("0 VND");
        lblDichVu = createPriceLabel("0 VND");
        lblThue = createPriceLabel("0 VND");
        lblTongTien = createPriceLabel("0 VND");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTongTien.setForeground(PRIMARY);

        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển Khoản", "Thẻ tín dụng", "Ví điện tử"});
        cboPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cboPhuongThuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cboPhuongThuc.setEnabled(false);

        rightPanel.add(createSummaryRow("Giá vé gốc:", lblGiaVe)); rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(createSummaryRow("Dịch vụ bổ sung:", lblDichVu)); rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(createSummaryRow("Thuế VAT (10%):", lblThue)); rightPanel.add(Box.createVerticalStrut(15));
        JSeparator sep = new JSeparator(); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        rightPanel.add(sep); rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(createSummaryRow("TỔNG TIỀN:", lblTongTien)); rightPanel.add(Box.createVerticalStrut(30));
        
        JLabel lblPT = new JLabel("Phương thức thanh toán:");
        lblPT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightPanel.add(lblPT); rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(cboPhuongThuc); rightPanel.add(Box.createVerticalGlue()); 

        btnXuatPDF = createRoundedButton("Tải Hóa Đơn (PDF)", BTN_PDF, null, 20);
        btnDong = createRoundedButton("Về Trang Chủ", BTN_DELETE, null, 20);

        JPanel pnlButtons = new JPanel(new GridLayout(2, 1, 10, 15));
        pnlButtons.setOpaque(false);
        pnlButtons.add(btnXuatPDF);
        pnlButtons.add(btnDong);
        rightPanel.add(pnlButtons);

        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);
        add(mainContent, BorderLayout.CENTER);

        setupListeners();
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel(""); lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); lbl.setForeground(new Color(15, 23, 42)); return lbl;
    }

    private JPanel createDetailRow(String title, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(10, 0)); row.setOpaque(false);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15)); lblTitle.setForeground(new Color(100, 116, 139)); lblTitle.setPreferredSize(new Dimension(130, 30));
        lblTitle.setVerticalAlignment(SwingConstants.TOP); // Để chữ tiêu đề ép lên trên cùng nếu nội dung bên phải dài nhiều dòng
        row.add(lblTitle, BorderLayout.WEST); row.add(valLabel, BorderLayout.CENTER);
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)), new EmptyBorder(10, 0, 10, 0)));
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

    private String catLaySoGhe(String maGheTho) {
        if (maGheTho == null) return "";
        if (maGheTho.contains("_")) {
            return maGheTho.substring(maGheTho.lastIndexOf("_") + 1);
        }
        return maGheTho;
    }

    private String getTenHangVe(String maHang) {
        if (maHang == null) return "Phổ thông";
        switch (maHang) {
            case "BUS": return "Thương gia";
            case "FST": return "Hạng nhất";
            case "PECO": return "Phổ thông ĐB";
            case "ECO": return "Phổ thông";
            default: return "Phổ thông";
        }
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
            valSDT.setText(data.sdt != null ? data.sdt : "Chưa cập nhật");
            valEmail.setText(data.email != null ? data.email : "Chưa cập nhật");
        }

        if (session != null) {
            boolean isKhuHoi = "Khứ hồi".equalsIgnoreCase(session.loaiVe);
            
            // XỬ LÝ TUYẾN BAY HIỂN THỊ
            String tuyenDi = (session.tenSanBayDi != null ? session.tenSanBayDi : "") + " -> " + (session.tenSanBayDen != null ? session.tenSanBayDen : "");
            if (isKhuHoi) {
                String tuyenVe = (session.tenSanBayDen != null ? session.tenSanBayDen : "") + " -> " + (session.tenSanBayDi != null ? session.tenSanBayDi : "");
                valTuyenBay.setText("<html><font color='#dc2626'><b>[ĐI]</b></font> " + tuyenDi + "<br><font color='#0ea5e9'><b>[VỀ]</b></font> " + tuyenVe + "</html>");
                pdfTuyenBayStr = "[ĐI] " + tuyenDi + "\n[VỀ] " + tuyenVe;
                
                String tgDi = session.thoiGianDi != null ? session.thoiGianDi : "";
                String tgVe = session.thoiGianVe != null ? session.thoiGianVe : "";
                valGioDi.setText("<html><font color='#dc2626'><b>[ĐI]</b></font> " + tgDi + "<br><font color='#0ea5e9'><b>[VỀ]</b></font> " + tgVe + "</html>");
                pdfKhoiHanhStr = "[ĐI] " + tgDi + "\n[VỀ] " + tgVe;
            } else {
                valTuyenBay.setText(tuyenDi);
                pdfTuyenBayStr = tuyenDi;
                valGioDi.setText(session.thoiGianDi != null ? session.thoiGianDi : "");
                pdfKhoiHanhStr = session.thoiGianDi != null ? session.thoiGianDi : "";
            }

            // XỬ LÝ TÍNH TIỀN
            BigDecimal veGoc = (session.tongTienVe != null) ? session.tongTienVe : BigDecimal.ZERO;
            BigDecimal dvBS = (session.tongTienDichVu != null) ? session.tongTienDichVu : BigDecimal.ZERO;
            BigDecimal giamGia = BigDecimal.ZERO;
            
            if (session.khuyenMaiApDung != null) {
                if ("PHAN_TRAM".equals(session.khuyenMaiApDung.getLoaiKM()) || "Phần trăm".equalsIgnoreCase(session.khuyenMaiApDung.getLoaiKM())) {
                    giamGia = veGoc.add(dvBS).multiply(session.khuyenMaiApDung.getGiaTri()).divide(new BigDecimal("100"));
                } else {
                    giamGia = session.khuyenMaiApDung.getGiaTri() != null ? session.khuyenMaiApDung.getGiaTri() : BigDecimal.ZERO;
                }
            }

            BigDecimal tongTruocThue = veGoc.add(dvBS).subtract(giamGia);
            if (tongTruocThue.compareTo(BigDecimal.ZERO) < 0) tongTruocThue = BigDecimal.ZERO;
            
            String rawTongTien = this.tongTien.replaceAll("[^0-9]", ""); 
            BigDecimal finalTotal = new BigDecimal(rawTongTien.isEmpty() ? "0" : rawTongTien);
            
            BigDecimal vat = finalTotal.subtract(tongTruocThue);
            if(vat.compareTo(BigDecimal.ZERO) < 0) vat = BigDecimal.ZERO;

            lblGiaVe.setText(String.format("%,.0f VND", veGoc));
            lblDichVu.setText(String.format("%,.0f VND", dvBS)); 
            if (giamGia.compareTo(BigDecimal.ZERO) > 0) {
                lblThue.setText(String.format("%,.0f VND", vat) + " (Đã giảm " + String.format("%,.0f", giamGia) + ")");
            } else {
                lblThue.setText(String.format("%,.0f VND", vat));
            }
            lblTongTien.setText(String.format("%,.0f VND", finalTotal));

            // ========================================================
            // XỬ LÝ CHI TIẾT DANH SÁCH KHÁCH (BÓC TÁCH GHẾ & HÀNH LÝ & HẠNG VÉ)
            // ========================================================
            if (session.danhSachHanhKhach != null) {
                StringBuilder sbGhe = new StringBuilder("<html>");
                StringBuilder pdfGhe = new StringBuilder(); 

                String hangVeDi = getTenHangVe(session.maHangVe);
                String hangVeVe = getTenHangVe((session.maHangVeVe != null && !session.maHangVeVe.isEmpty()) ? session.maHangVeVe : session.maHangVe);

                for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                    ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                    
                    String gheDi = "Chưa chọn";
                    String gheVe = "Chưa chọn";
                    if (session.danhSachGhe != null && i < session.danhSachGhe.size()) {
                        gheDi = catLaySoGhe(session.danhSachGhe.get(i).getSoGhe());
                    }
                    if (isKhuHoi && session.danhSachGhe != null && i + session.getTongSoHanhKhach() < session.danhSachGhe.size()) {
                        gheVe = catLaySoGhe(session.danhSachGhe.get(i + session.getTongSoHanhKhach()).getSoGhe());
                    }

                    // TÌM HÀNH LÝ
                    String hanhLy = "0kg";
                    if(session.danhSachHanhLy != null && i < session.danhSachHanhLy.size()) {
                        BigDecimal kg = session.danhSachHanhLy.get(i).getSoKg();
                        if (kg != null && kg.compareTo(BigDecimal.ZERO) > 0) {
                            hanhLy = kg.intValue() + "kg";
                        }
                    }

                    // BUILD CHUỖI HTML CHO GIAO DIỆN
                    sbGhe.append("<div style='margin-bottom:8px;'><b>").append(i+1).append(". ").append(hk.getHoTen()).append("</b> (").append(hk.getLoaiHanhKhach()).append(")<br>");
                    
                    // BUILD CHUỖI STRING CHO PDF
                    pdfGhe.append(i+1).append(". ").append(hk.getHoTen()).append(" (").append(hk.getLoaiHanhKhach()).append(")\n");
                    
                    if (isKhuHoi) {
                        sbGhe.append("&nbsp;&nbsp;<font color='#dc2626'>[ĐI]</font> Hạng: ").append(hangVeDi).append(" | Ghế: <b>").append(gheDi).append("</b> | Hành lý: ").append(hanhLy).append("<br>");
                        sbGhe.append("&nbsp;&nbsp;<font color='#0ea5e9'>[VỀ]</font> Hạng: ").append(hangVeVe).append(" | Ghế: <b>").append(gheVe).append("</b> | Hành lý: ").append(hanhLy).append("</div>");
                        
                        pdfGhe.append("   [ĐI] Hạng: ").append(hangVeDi).append(" | Ghế: ").append(gheDi).append(" | Hành lý: ").append(hanhLy).append("\n");
                        pdfGhe.append("   [VỀ] Hạng: ").append(hangVeVe).append(" | Ghế: ").append(gheVe).append(" | Hành lý: ").append(hanhLy);
                    } else {
                        sbGhe.append("&nbsp;&nbsp;Hạng: ").append(hangVeDi).append(" | Ghế: <b>").append(gheDi).append("</b> | Hành lý: ").append(hanhLy).append("</div>");
                        pdfGhe.append("   Hạng: ").append(hangVeDi).append(" | Ghế: ").append(gheDi).append(" | Hành lý: ").append(hanhLy);
                    }
                    
                    if (i < session.danhSachHanhKhach.size() - 1) pdfGhe.append("\n\n");
                }
                sbGhe.append("</html>");
                
                valMaVe.setText(sbGhe.toString());
                pdfChiTietVeStr = pdfGhe.toString();
            }
        }
    }

    private void setupListeners() {
        btnDong.addActionListener(e -> {
            Window currentWindow = SwingUtilities.getWindowAncestor(this);
            if (currentWindow != null) { currentWindow.dispose(); }
            
            for (Window w : Window.getWindows()) {
                if (w.isDisplayable() && w instanceof JFrame) {
                    w.dispose();
                }
            }
            
            if (session != null && session.maNguoiDung != null && !session.maNguoiDung.equals("KHACH_LE")) {
                model.NguoiDung userDaDangNhap = null;
                try {
                    dal.NguoiDungDAO ndDAO = new dal.NguoiDungDAO();
                    for (model.NguoiDung nd : ndDAO.selectAll()) {
                        if (nd.getMaNguoiDung().equals(session.maNguoiDung)) {
                            userDaDangNhap = nd;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi khôi phục tài khoản: " + ex.getMessage());
                }

                if (userDaDangNhap != null) {
                    gui.user.MainFrame newHome = new gui.user.MainFrame(userDaDangNhap); 
                    newHome.setLocationRelativeTo(null);
                    newHome.setVisible(true);
                } else {
                    gui.user.MainFrame newHome = new gui.user.MainFrame();
                    newHome.setLocationRelativeTo(null);
                    newHome.setVisible(true); 
                }
            } else {
                gui.user.MainFrame newHome = new gui.user.MainFrame();
                newHome.setLocationRelativeTo(null);
                newHome.setVisible(true); 
            }
        });

        btnXuatPDF.addActionListener(e -> xuatVaMoFilePDF());
    }

    private void xuatVaMoFilePDF() {
        JFileChooser fileChooser = new JFileChooser(); 
        fileChooser.setSelectedFile(new java.io.File("VeMayBay_" + maHD + ".pdf"));
        
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

                Paragraph title = new Paragraph("CHI TIẾT VÉ MÁY BAY - " + maHD, fontTitle);
                title.setAlignment(Element.ALIGN_LEFT); 
                document.add(title);
                
                String thoiGianIn = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                Paragraph subTitle = new Paragraph("Mã PNR: " + valMaPhieu.getText() + " | In lúc: " + thoiGianIn, fontKey);
                subTitle.setAlignment(Element.ALIGN_LEFT); 
                subTitle.setSpacingAfter(20); 
                document.add(subTitle);

                document.add(new Paragraph("THÔNG TIN NGƯỜI ĐẶT", fontHeader));
                PdfPTable tblKH = new PdfPTable(2);
                tblKH.setWidthPercentage(100); 
                tblKH.setSpacingBefore(10); 
                tblKH.setSpacingAfter(20); 
                tblKH.setWidths(new float[]{3f, 7f});
                
                addPdfRow(tblKH, "Mã Phiếu Đặt:", valMaPhieu.getText(), fontKey, fontValue);
                addPdfRow(tblKH, "Số Điện Thoại:", valSDT.getText(), fontKey, fontValue);
                addPdfRow(tblKH, "Email:", valEmail.getText(), fontKey, fontValue);
                document.add(tblKH);

                document.add(new Paragraph("THÔNG TIN HÀNH TRÌNH & VÉ", fontHeader));
                PdfPTable tblCB = new PdfPTable(2);
                tblCB.setWidthPercentage(100); 
                tblCB.setSpacingBefore(10); 
                tblCB.setSpacingAfter(20);
                tblCB.setWidths(new float[]{3f, 7f});
                
                addPdfRow(tblCB, "Tuyến Bay:", pdfTuyenBayStr, fontKey, fontValue);
                addPdfRow(tblCB, "Khởi Hành:", pdfKhoiHanhStr, fontKey, fontValue);
                addPdfRow(tblCB, "Chi Tiết Vé:", pdfChiTietVeStr, fontKey, fontValue);
                document.add(tblCB);

                document.add(new Paragraph("TỔNG KẾT THANH TOÁN", fontHeader));
                PdfPTable tblTT = new PdfPTable(2);
                tblTT.setWidthPercentage(100); 
                tblTT.setSpacingBefore(10); 
                tblTT.setSpacingAfter(20); 
                tblTT.setWidths(new float[]{6f, 4f});

                addPdfRow(tblTT, "Phương thức TT:", cboPhuongThuc.getSelectedItem().toString(), fontKey, fontValue);
                addPdfRow(tblTT, "Giá vé gốc:", lblGiaVe.getText(), fontKey, fontValue);
                addPdfRow(tblTT, "Dịch vụ bổ sung:", lblDichVu.getText(), fontKey, fontValue);
                addPdfRow(tblTT, "Thuế VAT (10%):", lblThue.getText(), fontKey, fontValue);

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