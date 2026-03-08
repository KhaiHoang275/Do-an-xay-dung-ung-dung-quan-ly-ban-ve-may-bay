package gui;

import bus.HoaDonBUS;
import model.HoaDon;

// Import thư viện iTextPDF
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class QuanLyHoaDonGUI extends JPanel {

    private HoaDonBUS hoaDonBUS;
    private DefaultTableModel tableModel;
    private JTable tableHoaDon;
    private JTextField txtTuNgay, txtDenNgay, txtPnr;

    // Bảng màu chuẩn theo thiết kế của bạn
    private Color darkBg = new Color(40, 42, 45); // Màu xám đen nền
    private Color textColor = Color.WHITE;
    private Color btnColor = new Color(70, 70, 70); // Xám cho nút bấm

    public QuanLyHoaDonGUI() {
        hoaDonBUS = new HoaDonBUS(); 
        initComponents();
        loadDataToTable(); // Tự động load 50 hóa đơn từ DB lên
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(darkBg);
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ================= 1. PANEL TÌM KIẾM (TOP) CĂN CHỈNH Y HỆT ẢNH =================
        JPanel pnlSearch = new JPanel(new GridBagLayout());
        pnlSearch.setBackground(darkBg);
        TitledBorder searchBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "QUẢN LÝ HÓA ĐƠN (ADMIN)"
        );
        searchBorder.setTitleColor(textColor);
        searchBorder.setTitleFont(new Font("Arial", Font.BOLD, 12));
        pnlSearch.setBorder(searchBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // Khoảng cách giữa các ô rộng rãi
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1
        JLabel lblTuNgay = new JLabel("Từ ngày (dd/mm/yyyy):"); lblTuNgay.setForeground(textColor); lblTuNgay.setFont(new Font("Arial", Font.BOLD, 13));
        txtTuNgay = new JTextField(15);
        JLabel lblDenNgay = new JLabel("Đến ngày:"); lblDenNgay.setForeground(textColor); lblDenNgay.setFont(new Font("Arial", Font.BOLD, 13));
        txtDenNgay = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; pnlSearch.add(lblTuNgay, gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnlSearch.add(txtTuNgay, gbc);
        gbc.gridx = 2; gbc.gridy = 0; pnlSearch.add(lblDenNgay, gbc);
        gbc.gridx = 3; gbc.gridy = 0; pnlSearch.add(txtDenNgay, gbc);

        // Dòng 2
        JLabel lblPnr = new JLabel("Tìm theo Mã PNR:"); lblPnr.setForeground(textColor); lblPnr.setFont(new Font("Arial", Font.BOLD, 13));
        txtPnr = new JTextField(15);
        JButton btnTimKiem = new JButton("TÌM KIẾM");
        btnTimKiem.setBackground(btnColor); btnTimKiem.setForeground(textColor); btnTimKiem.setFont(new Font("Arial", Font.BOLD, 12));
        btnTimKiem.setFocusPainted(false); btnTimKiem.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnTimKiem.setPreferredSize(new Dimension(150, 25));

        gbc.gridx = 0; gbc.gridy = 1; pnlSearch.add(lblPnr, gbc);
        gbc.gridx = 1; gbc.gridy = 1; pnlSearch.add(txtPnr, gbc);
        gbc.gridx = 3; gbc.gridy = 1; pnlSearch.add(btnTimKiem, gbc); // Đặt nút tìm kiếm ở cột 4

        this.add(pnlSearch, BorderLayout.NORTH);

        // ================= 2. BẢNG DỮ LIỆU ĐỘNG (CENTER) =================
        String[] columnNames = {"Mã HĐ", "Mã PNR", "Ngày Lập", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableHoaDon = new JTable(tableModel);
        
        // Style bảng y hệt ảnh
        tableHoaDon.setBackground(new Color(60, 60, 60));
        tableHoaDon.setForeground(textColor);
        tableHoaDon.setGridColor(Color.GRAY);
        tableHoaDon.setRowHeight(25);
        
        // Header bảng
        tableHoaDon.getTableHeader().setBackground(new Color(30, 30, 30));
        tableHoaDon.getTableHeader().setForeground(textColor);
        tableHoaDon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        scrollPane.getViewport().setBackground(darkBg);
        this.add(scrollPane, BorderLayout.CENTER);

        // ================= 3. KHU VỰC 3 NÚT BẤM (BOTTOM) =================
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        pnlButtons.setBackground(darkBg);

        JButton btnChiTiet = new JButton("XEM CHI TIẾT");
        JButton btnIn = new JButton("IN HÓA ĐƠN");
        JButton btnPdf = new JButton("XUẤT PDF"); // Thay Excel thành PDF

        Dimension btnSize = new Dimension(140, 35); // Kích thước nút chuẩn vuông vức
        JButton[] buttons = {btnChiTiet, btnIn, btnPdf};
        for (JButton btn : buttons) {
            btn.setBackground(btnColor);
            btn.setForeground(textColor);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            btn.setPreferredSize(btnSize);
            pnlButtons.add(btn);
        }
        this.add(pnlButtons, BorderLayout.SOUTH);

        // ==================== GẮN SỰ KIỆN CHỨC NĂNG ====================

        // CHỨC NĂNG 1: TÌM KIẾM THEO PNR
        btnTimKiem.addActionListener(e -> {
            String pnr = txtPnr.getText().trim();
            tableModel.setRowCount(0); 
            List<HoaDon> danhSach = hoaDonBUS.docDanhSachHoaDon();
            for (HoaDon hd : danhSach) {
                if (pnr.isEmpty() || (hd.getMaPhieuDatVe() != null && hd.getMaPhieuDatVe().contains(pnr))) {
                    Object[] row = { 
                        hd.getMaHoaDon(), hd.getMaPhieuDatVe(), 
                        hd.getNgayLap(), hd.getTongTien() + " VNĐ", "Đã thanh toán" 
                    };
                    tableModel.addRow(row);
                }
            }
        });

        // CHỨC NĂNG 2: XUẤT PDF
        btnPdf.addActionListener(e -> {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu trên bảng để xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String filePath = file.getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".pdf")) filePath += ".pdf";

                    // Dùng iTextPDF để tạo Document
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(filePath));
                    document.open();
                    
                    // Tiêu đề file PDF
                    com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                    Paragraph title = new Paragraph("DANH SACH HOA DON", titleFont);
                    title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    title.setSpacingAfter(20);
                    document.add(title);
                    
                    // Tạo bảng PDF
                    PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
                    pdfTable.setWidthPercentage(100);
                    
                    // Bơm Header
                    for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i)));
                        cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                        pdfTable.addCell(cell);
                    }
                    
                    // Bơm Data từ JTable vào PDF
                    for (int rows = 0; rows < tableModel.getRowCount(); rows++) {
                        for (int cols = 0; cols < tableModel.getColumnCount(); cols++) {
                            pdfTable.addCell(tableModel.getValueAt(rows, cols).toString());
                        }
                    }
                    
                    document.add(pdfTable);
                    document.close();
                    
                    JOptionPane.showMessageDialog(this, "Đã xuất PDF thành công tại:\n" + filePath, "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        // CHỨC NĂNG 3: XEM CHI TIẾT (Demo popup)
        btnChiTiet.addActionListener(e -> {
            int row = tableHoaDon.getSelectedRow();
            if(row == -1) JOptionPane.showMessageDialog(this, "Vui lòng click chọn 1 hóa đơn trên bảng!");
            else JOptionPane.showMessageDialog(this, "Mở chi tiết Hóa Đơn: " + tableModel.getValueAt(row, 0));
        });
    }

    // Đổ dữ liệu thật từ SQL lên Table
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<HoaDon> danhSach = hoaDonBUS.docDanhSachHoaDon();
        if(danhSach != null) {
            for (HoaDon hd : danhSach) {
                Object[] row = {
                    hd.getMaHoaDon(), 
                    hd.getMaPhieuDatVe(), 
                    hd.getNgayLap(), 
                    hd.getTongTien() + " VNĐ", 
                    "Đã thanh toán" 
                };
                tableModel.addRow(row);
            }
        }
    }
}