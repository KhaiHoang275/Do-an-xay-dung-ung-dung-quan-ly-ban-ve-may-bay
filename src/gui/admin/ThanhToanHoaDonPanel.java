package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import bll.HoaDonBUS;
import model.ThanhToanDTO;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThanhToanHoaDonPanel extends JPanel {

    // Các biến lưu dữ liệu động truyền từ form Hóa Đơn sang
    private String maHD, maPhieu, ngayLap, tongTien, phuongThuc, thue;

    // Các thành phần UI hiển thị (Giữ nguyên các biến chuẩn của bạn)
    private JLabel valMaPhieu, valTenKH, valSDT, valEmail;
    private JLabel valTuyenBay, valMaVe, valGioDi, valGioDen;
    
    private JLabel lblGiaVe, lblThue, lblDichVu, lblTongTien;
    private JComboBox<String> cboPhuongThuc;
    private JButton btnThanhToan, btnXuatPDF, btnDong;

    // Bảng màu đồng bộ
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);
    private final Color BTN_PAY = new Color(34, 197, 94);       
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
        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("CHI TIẾT THANH TOÁN - " + maHD, JLabel.LEFT);
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

        // ================= CỘT TRÁI: THÔNG TIN KH & CHUYẾN BAY =================
        JPanel leftPanel = new JPanel(new BorderLayout(20, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(600, 0));

        JPanel pnlKhachHang = createCardPanel("THÔNG TIN KHÁCH HÀNG");
        pnlKhachHang.setLayout(new GridLayout(4, 1, 0, 10));
        
        valMaPhieu = createValueLabel(); 
        valTenKH = createValueLabel(); 
        valSDT = createValueLabel(); 
        valEmail = createValueLabel(); 
        
        pnlKhachHang.add(createDetailRow("Mã Phiếu Đặt:", valMaPhieu));
        pnlKhachHang.add(createDetailRow("Họ Tên Khách Hàng:", valTenKH));
        pnlKhachHang.add(createDetailRow("Số Điện Thoại:", valSDT));
        pnlKhachHang.add(createDetailRow("Email:", valEmail));

        JPanel pnlChuyenBay = createCardPanel("THÔNG TIN CHUYẾN BAY");
        pnlChuyenBay.setLayout(new GridLayout(4, 1, 0, 10));
        
        valTuyenBay = createValueLabel(); 
        valMaVe = createValueLabel(); 
        valGioDi = createValueLabel(); 
        valGioDen = createValueLabel(); 

        pnlChuyenBay.add(createDetailRow("Tuyến Bay:", valTuyenBay));
        pnlChuyenBay.add(createDetailRow("Mã Vé / Ghế:", valMaVe));
        pnlChuyenBay.add(createDetailRow("Thời Gian Đi:", valGioDi));
        pnlChuyenBay.add(createDetailRow("Thời Gian Đến:", valGioDen));

        leftPanel.add(pnlKhachHang, BorderLayout.NORTH);
        leftPanel.add(pnlChuyenBay, BorderLayout.CENTER);

        // ================= CỘT PHẢI: TỔNG TIỀN & NÚT =================
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

        // BỐ CỤC LẠI KHOẢNG CÁCH DÒNG TIỀN ĐỀU NHAU (15px)
        rightPanel.add(createSummaryRow("Giá vé gốc:", lblGiaVe));
        rightPanel.add(Box.createVerticalStrut(15));
        
        rightPanel.add(createSummaryRow("Dịch vụ bổ sung:", lblDichVu));
        rightPanel.add(Box.createVerticalStrut(15));
        
        rightPanel.add(createSummaryRow("Thuế VAT (10%):", lblThue));
        rightPanel.add(Box.createVerticalStrut(15));
        
        JSeparator sep = new JSeparator(); 
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        rightPanel.add(sep);
        rightPanel.add(Box.createVerticalStrut(15));
        
        rightPanel.add(createSummaryRow("TỔNG TIỀN:", lblTongTien));
        rightPanel.add(Box.createVerticalStrut(30));
        
        JLabel lblPT = new JLabel("Phương thức thanh toán:");
        lblPT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPT.setForeground(new Color(50, 50, 50));
        rightPanel.add(lblPT);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(cboPhuongThuc);
        rightPanel.add(Box.createVerticalGlue()); 

        btnThanhToan = createRoundedButton("Xác nhận TT", BTN_PAY, "/resources/icons/icons8-add-24.png", 20);
        btnXuatPDF = createRoundedButton("Xem & Xuất PDF", BTN_PDF, "/resources/icons/icons8-pdf-24.png", 20);
        btnDong = createRoundedButton("Đóng", BTN_DELETE, "/resources/icons/icons8-erase-24.png", 20);

        JPanel pnlButtons = new JPanel(new GridLayout(3, 1, 10, 15));
        pnlButtons.setOpaque(false);
        pnlButtons.add(btnThanhToan);
        pnlButtons.add(btnXuatPDF);
        pnlButtons.add(btnDong);
        
        rightPanel.add(pnlButtons);

        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);
        add(mainContent, BorderLayout.CENTER);

        setupListeners();
    }

    // ================= HÀM TẠO GIAO DIỆN CON =================
    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(15, 23, 42)); 
        return lbl;
    }

    private JPanel createDetailRow(String title, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTitle.setForeground(new Color(100, 116, 139));
        lblTitle.setPreferredSize(new Dimension(150, 30));
        
        row.add(lblTitle, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.CENTER);
        
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
            new EmptyBorder(5, 0, 5, 0)
        ));
        return row;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), BorderFactory.createCompoundBorder(new TitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), TABLE_HEADER), new EmptyBorder(15, 20, 15, 20))));
        return panel;
    }

    private JLabel createPriceLabel(String text) {
        JLabel lbl = new JLabel(text); lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); lbl.setForeground(new Color(30, 41, 59)); lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        return lbl;
    }

    private JPanel createSummaryRow(String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout()); row.setOpaque(false);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15)); lblTitle.setForeground(new Color(50, 50, 50));
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
        if (iconPath != null && !iconPath.isEmpty()) { try { ImageIcon icon = new ImageIcon(getClass().getResource(iconPath)); Image scaled = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH); btn.setIcon(new ImageIcon(scaled)); btn.setIconTextGap(10); } catch (Exception e) {} }
        return btn;
    }

    // ================= NẠP DỮ LIỆU ĐỘNG TỪ SQL =================
    private void loadDynamicData() {
        // Gán Mã Phiếu và set Phương thức thanh toán theo dữ liệu bảng truyền qua
        valMaPhieu.setText(maPhieu);
        if(phuongThuc != null) {
            for(int i = 0; i < cboPhuongThuc.getItemCount(); i++) {
                if(cboPhuongThuc.getItemAt(i).equalsIgnoreCase(phuongThuc.trim())) {
                    cboPhuongThuc.setSelectedIndex(i); 
                    break;
                }
            }
        }

        // GỌI DATABASE LẤY DỮ LIỆU THẬT
        HoaDonBUS bus = new HoaDonBUS();
        ThanhToanDTO data = bus.layChiTietThanhToan(maPhieu); 
        
        if (data != null) {
            valTenKH.setText(data.tenKH != null ? data.tenKH : "N/A");
            valSDT.setText(data.sdt != null ? data.sdt : "N/A");
            valEmail.setText(data.email != null ? data.email : "N/A");
            valTuyenBay.setText(data.tuyenBay != null ? data.tuyenBay : "N/A");
            valMaVe.setText(data.veGhe != null ? data.veGhe : "N/A");
            valGioDi.setText(data.gioDi != null ? data.gioDi : "N/A");
            valGioDen.setText(data.gioDen != null ? data.gioDen : "N/A");

            // LOGIC TÍNH TOÁN TIỀN (Vé Gốc + Dịch vụ + Thuế)
            BigDecimal veGoc = (data.giaVeGoc != null) ? data.giaVeGoc : BigDecimal.ZERO;
            BigDecimal dvBS = (data.tongTienDichVu != null) ? data.tongTienDichVu : BigDecimal.ZERO;
            
            BigDecimal tongTruocThue = veGoc.add(dvBS);
            BigDecimal vat = tongTruocThue.multiply(new BigDecimal("0.1")); // Thuế VAT 10%
            BigDecimal finalTotal = tongTruocThue.add(vat);

            // HIỂN THỊ LÊN GIAO DIỆN
            lblGiaVe.setText(String.format("%,.0f VND", veGoc));
            lblDichVu.setText(String.format("%,.0f VND", dvBS)); 
            lblThue.setText(String.format("%,.0f VND", vat));
            lblTongTien.setText(String.format("%,.0f VND", finalTotal));
            
        } else {
            valTenKH.setText("Không tìm thấy dữ liệu trong CSDL!");
        }
    }

    // ================= EVENTS & LOGIC =================
    private void setupListeners() {
        btnDong.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        btnThanhToan.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cập nhật thanh toán thành công cho Hóa đơn: " + maHD, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            btnDong.doClick(); 
        });

        btnXuatPDF.addActionListener(e -> showPDFPreviewDialog());
    }

    private void showPDFPreviewDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog previewDialog = new JDialog(parentWindow, "Xem trước Hóa Đơn PDF", Dialog.ModalityType.APPLICATION_MODAL);
        previewDialog.setSize(750, 850); previewDialog.setLocationRelativeTo(this); previewDialog.setLayout(new BorderLayout());

        JTextPane pdfPage = new JTextPane(); pdfPage.setContentType("text/html"); pdfPage.setEditable(false); pdfPage.setBackground(Color.WHITE);
        pdfPage.setText(buildHtmlInvoice());
        
        JScrollPane scrollPane = new JScrollPane(pdfPage); scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); scrollPane.getViewport().setBackground(new Color(210, 210, 210)); 

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15)); bottomPanel.setBackground(Color.WHITE);
        JButton btnLuuPdf = createRoundedButton("Xác nhận Lưu PDF", BTN_PAY, null, 0); btnLuuPdf.setPreferredSize(new Dimension(180, 40));
        JButton btnThoat = createRoundedButton("Thoát", BTN_DELETE, null, 0); btnThoat.setPreferredSize(new Dimension(120, 40));

        btnThoat.addActionListener(e -> previewDialog.dispose());
        btnLuuPdf.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(); fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maHD + ".pdf"));
            if (fileChooser.showSaveDialog(previewDialog) == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(previewDialog, "Đã lưu PDF thành công tại:\n" + fileChooser.getSelectedFile().getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                previewDialog.dispose();
            }
        });

        bottomPanel.add(btnLuuPdf); bottomPanel.add(btnThoat);
        previewDialog.add(scrollPane, BorderLayout.CENTER); previewDialog.add(bottomPanel, BorderLayout.SOUTH); previewDialog.setVisible(true);
    }

    // ĐÃ SỬA LẠI PDF ĐỂ HIỆN ĐỦ 2 DÒNG GIÁ VÉ VÀ DỊCH VỤ
    private String buildHtmlInvoice() {
        String thoiGianIn = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return "<html><body style='font-family: Arial, sans-serif; padding: 20px; color: #333;'><div style='text-align: center;'><h1 style='color: #dc2626; margin-bottom: 0px;'>HÓA ĐƠN ĐIỆN TỬ VÉ MÁY BAY</h1><p style='color: #666; margin-top: 5px;'>Mã HĐ: <b>" + maHD + "</b> | Mã tra cứu: <b>" + valMaPhieu.getText() + "</b></p><p style='color: #666; margin-top: 0px;'>Ngày in: " + thoiGianIn + "</p></div><hr style='border: 1px solid #ccc; margin: 20px 0;'><h3>1. THÔNG TIN KHÁCH HÀNG</h3><table width='100%' style='font-size: 14px; margin-bottom: 20px;'><tr><td width='30%'>Họ và tên:</td><td><b>" + valTenKH.getText() + "</b></td></tr><tr><td>Số điện thoại:</td><td><b>" + valSDT.getText() + "</b></td></tr><tr><td>Email:</td><td><b>" + valEmail.getText() + "</b></td></tr></table><h3>2. THÔNG TIN CHUYẾN BAY</h3><table width='100%' border='1' cellspacing='0' cellpadding='8' style='border-collapse: collapse; font-size: 14px; text-align: left; margin-bottom: 20px; border-color: #ddd;'><tr style='background-color: #f8f9fa;'><th>Tuyến bay</th><th>Mã vé / Ghế</th><th>Giờ đi</th><th>Giờ đến</th></tr><tr><td>" + valTuyenBay.getText() + "</td><td>" + valMaVe.getText() + "</td><td>" + valGioDi.getText() + "</td><td>" + valGioDen.getText() + "</td></tr></table><h3>3. CHI TIẾT THANH TOÁN</h3><table width='100%' style='font-size: 14px;'><tr><td width='70%' align='right'>Phương thức TT:</td><td align='right'><b>" + cboPhuongThuc.getSelectedItem().toString() + "</b></td></tr><tr><td align='right'>Giá vé gốc:</td><td align='right'>" + lblGiaVe.getText() + "</td></tr><tr><td align='right'>Dịch vụ bổ sung:</td><td align='right'>" + lblDichVu.getText() + "</td></tr><tr><td align='right'>Thuế VAT:</td><td align='right'>" + lblThue.getText() + "</td></tr><tr><td colspan='2'><hr style='border: 0.5px solid #ccc;'></td></tr><tr><td align='right'><b style='font-size: 18px;'>TỔNG TIỀN:</b></td><td align='right'><b style='color: #dc2626; font-size: 18px;'>" + lblTongTien.getText() + "</b></td></tr></table><br><br><p style='text-align: center; font-size: 12px; color: #888;'>Cảm ơn quý khách đã sử dụng dịch vụ của KH AirLine!</p></body></html>";
    }
}