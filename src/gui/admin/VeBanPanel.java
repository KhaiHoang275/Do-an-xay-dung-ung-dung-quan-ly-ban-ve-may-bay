package gui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dal.ChuyenBayDAO;
import dal.GheMayBayDAO;
import dal.PhieuDatVeDAO;
import dal.VeBanDAO;
import db.DBConnection;
import model.GheMayBay;
import model.PhieuDatVe;
import model.VeBan;

public class VeBanPanel extends JPanel {

    // ĐỒNG BỘ MÀU SẮC
    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color BG_MAIN = new Color(245, 247, 250);
    
    // Đã làm nhỏ font chữ lại 1 xíu (từ 15 -> 14)
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTable table;
    private DefaultTableModel tableModel;
    private final VeBanDAO veBanDAO = new VeBanDAO();
    private final PhieuDatVeDAO pdv = new PhieuDatVeDAO();
    private final GheMayBayDAO gmb = new GheMayBayDAO();
    private final ChuyenBayDAO cbDAO = new ChuyenBayDAO();

    private JTextField txtSearch;
    private JComboBox<String> cboTrangThai;

    // COMPONENTS CHUNG
    private JTextField txtTenNguoiDat, txtMaHK;
    private JRadioButton rdMotChieu, rdKhuHoi;
    private JSpinner spNguoiLon, spTreEm, spEmBe;
    private JTextField txtTongTien;

    // LƯỢT ĐI
    private JTextField txtNgayDi;
    private JComboBox<String> cboHangVeDi;
    private JTextField txtMaChuyenBayDi, txtGheDi;
    private JButton btnChonCBDi;
    private BigDecimal giaGheDi = BigDecimal.ZERO;
    private List<GheMayBay> selectedGheDi = new ArrayList<>();
    private LocalDateTime arrivalTimeDi; // Giờ ĐẾN của chuyến đi
    private String sanBayDenDi; // Sân bay ĐẾN của chuyến đi

    // LƯỢT VỀ
    private JPanel pnlLuotVe;
    private JTextField txtNgayVe;
    private JComboBox<String> cboHangVeVe;
    private JTextField txtMaChuyenBayVe, txtGheVe;
    private JButton btnChonCBVe;
    private BigDecimal giaGheVe = BigDecimal.ZERO;
    private List<GheMayBay> selectedGheVe = new ArrayList<>();

    public VeBanPanel() {
        setLayout(new BorderLayout(10, 10)); // Giảm khoảng cách tổng thể
        setBackground(BG_MAIN);
        add(initHeader(), BorderLayout.NORTH);
        add(initTabs(), BorderLayout.CENTER);
        loadTable();
    }

    private JPanel initHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Làm gọn border header
        panel.setBackground(Color.WHITE);
        JLabel title = new JLabel("QUẢN LÝ VÉ BÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Thu nhỏ title
        title.setForeground(PRIMARY_COLOR);
        panel.add(title, BorderLayout.NORTH);
        panel.add(initSearchBar(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel initSearchBar() {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE); panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(createStyledLabel("Tìm (Mã vé / PNR): "));
        txtSearch = new JTextField(); txtSearch.setMaximumSize(new Dimension(200, 30)); panel.add(txtSearch);
        panel.add(Box.createHorizontalStrut(10));
        JButton btnSearch = new JButton("Tìm kiếm"); btnSearch.setBackground(ACCENT_COLOR); btnSearch.setFont(FONT_BOLD);
        panel.add(btnSearch); panel.add(Box.createHorizontalStrut(20));
        panel.add(createStyledLabel("Trạng thái: "));
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy"});
        cboTrangThai.setMaximumSize(new Dimension(150, 30)); panel.add(cboTrangThai);
        btnSearch.addActionListener(e -> searchVe());
        return panel;
    }

    private JTabbedPane initTabs() {
        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("Danh sách vé", initTablePanel());
        tabs.addTab("Tạo vé máy bay (Admin)", new JScrollPane(initFormPanel()));
        return tabs;
    }

    private JPanel initTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        String[] cols = {"Mã vé", "Mã PNR", "Chuyến bay", "Loại HK", "Loại vé", "Giá vé", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel); table.setRowHeight(25); table.setFont(FONT_PLAIN); // Thu nhỏ dòng table
        table.getTableHeader().setBackground(PRIMARY_COLOR); table.getTableHeader().setForeground(Color.WHITE); table.getTableHeader().setFont(FONT_BOLD);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel initFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15)); // Gọn form lại
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Khoảng cách giữa các ô thu nhỏ lại
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;
        int row = 0;

        // 1. THÔNG TIN CHUNG
        txtTenNguoiDat = new JTextField(15); txtMaHK = new JTextField(10); txtMaHK.setEditable(false);
        JButton btnTimHK = new JButton("Tìm HK"); btnTimHK.setBackground(PRIMARY_COLOR); btnTimHK.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Tên Khách:"), gbc);
        gbc.gridx = 1; panel.add(txtTenNguoiDat, gbc);
        gbc.gridx = 2; panel.add(btnTimHK, gbc);
        gbc.gridx = 3; panel.add(createStyledLabel("Mã HK:"), gbc);
        gbc.gridx = 4; panel.add(txtMaHK, gbc);
        row++;

        rdMotChieu = new JRadioButton("Một chiều", true); rdMotChieu.setBackground(Color.WHITE); rdMotChieu.setFont(FONT_BOLD);
        rdKhuHoi = new JRadioButton("Khứ hồi"); rdKhuHoi.setBackground(Color.WHITE); rdKhuHoi.setFont(FONT_BOLD);
        ButtonGroup group = new ButtonGroup(); group.add(rdMotChieu); group.add(rdKhuHoi);
        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT)); pLoai.setBackground(Color.WHITE); pLoai.add(rdMotChieu); pLoai.add(rdKhuHoi);

        gbc.gridx = 0; gbc.gridy = row; 
        gbc.gridwidth = 1;
        panel.add(createStyledLabel("Hành trình:"), gbc);

        gbc.gridx = 1; 
        gbc.gridwidth = 4;
        panel.add(pLoai, gbc);
        row++; 
        
        spNguoiLon = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spTreEm = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spEmBe = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JPanel pHK = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Dùng FlowLayout cho đẹp và thẳng hàng
        pHK.setBackground(Color.WHITE);
        pHK.add(createStyledLabel("Người lớn:")); pHK.add(spNguoiLon); 
        pHK.add(Box.createHorizontalStrut(10));
        pHK.add(createStyledLabel("Trẻ em:")); pHK.add(spTreEm); 
        pHK.add(Box.createHorizontalStrut(10));
        pHK.add(createStyledLabel("Em bé:")); pHK.add(spEmBe);

        gbc.gridx = 0; gbc.gridy = row; 
        gbc.gridwidth = 1; 
        panel.add(createStyledLabel("Số lượng:"), gbc);

        gbc.gridx = 1; 
        gbc.gridwidth = 4; // Trải dài ra 4 cột
        panel.add(pHK, gbc);

        gbc.gridwidth = 1; // Luôn reset gridwidth về 1 sau khi dùng xong để tránh ảnh hưởng dòng sau
        row++;
        
        // 2. LƯỢT ĐI
        JLabel lblTieuDeDi = new JLabel("--- THÔNG TIN LƯỢT ĐI ---"); lblTieuDeDi.setFont(new Font("Segoe UI", Font.BOLD, 15)); lblTieuDeDi.setForeground(new Color(220, 38, 38));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 5; panel.add(lblTieuDeDi, gbc); gbc.gridwidth = 1; row++;

        txtNgayDi = new JTextField(); txtNgayDi.setEditable(false); txtNgayDi.setBackground(Color.WHITE); txtNgayDi.setText(LocalDate.now().format(dateFormatter));
        setupDatePicker(txtNgayDi, true);
        cboHangVeDi = new JComboBox<>(new String[]{"Phổ thông", "Phổ thông đặc biệt", "Thương gia", "Hạng nhất"});
        
        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Ngày đi:"), gbc);
        gbc.gridx = 1; panel.add(txtNgayDi, gbc);
        gbc.gridx = 3; panel.add(createStyledLabel("Hạng vé đi:"), gbc);
        gbc.gridx = 4; panel.add(cboHangVeDi, gbc);
        row++;

        txtMaChuyenBayDi = new JTextField(); txtMaChuyenBayDi.setEditable(false);
        txtGheDi = new JTextField(); txtGheDi.setEditable(false);
        btnChonCBDi = new JButton("Chọn CB Đi"); btnChonCBDi.setBackground(PRIMARY_COLOR); btnChonCBDi.setForeground(Color.WHITE);
        JButton btnChonGheDi = new JButton("Sơ đồ Ghế"); btnChonGheDi.setBackground(ACCENT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("Chuyến bay:"), gbc);
        gbc.gridx = 1; panel.add(txtMaChuyenBayDi, gbc);
        gbc.gridx = 2; panel.add(btnChonCBDi, gbc);
        gbc.gridx = 3; panel.add(txtGheDi, gbc);
        gbc.gridx = 4; panel.add(btnChonGheDi, gbc);
        row++;

        // 3. LƯỢT VỀ (Bọc Panel)
        pnlLuotVe = new JPanel(new GridBagLayout()); pnlLuotVe.setBackground(Color.WHITE);
        GridBagConstraints gbcVe = new GridBagConstraints(); gbcVe.insets = new Insets(5, 0, 5, 10); gbcVe.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblTieuDeVe = new JLabel("--- THÔNG TIN LƯỢT VỀ ---"); lblTieuDeVe.setFont(new Font("Segoe UI", Font.BOLD, 15)); lblTieuDeVe.setForeground(new Color(220, 38, 38));
        gbcVe.gridx = 0; gbcVe.gridy = 0; gbcVe.gridwidth = 5; pnlLuotVe.add(lblTieuDeVe, gbcVe); gbcVe.gridwidth = 1;

        txtNgayVe = new JTextField(); txtNgayVe.setEditable(false); txtNgayVe.setBackground(Color.WHITE);
        setupDatePicker(txtNgayVe, false);
        cboHangVeVe = new JComboBox<>(new String[]{"Phổ thông", "Phổ thông đặc biệt", "Thương gia", "Hạng nhất"});
        
        gbcVe.gridx = 0; gbcVe.gridy = 1; pnlLuotVe.add(createStyledLabel("Ngày về:"), gbcVe);
        gbcVe.gridx = 1; gbcVe.weightx=1.0; pnlLuotVe.add(txtNgayVe, gbcVe);
        gbcVe.gridx = 2; gbcVe.weightx=0; pnlLuotVe.add(Box.createHorizontalStrut(10), gbcVe); // Spacer
        gbcVe.gridx = 3; pnlLuotVe.add(createStyledLabel("Hạng vé về:"), gbcVe);
        gbcVe.gridx = 4; gbcVe.weightx=1.0; pnlLuotVe.add(cboHangVeVe, gbcVe); gbcVe.weightx=0;

        txtMaChuyenBayVe = new JTextField(); txtMaChuyenBayVe.setEditable(false);
        txtGheVe = new JTextField(); txtGheVe.setEditable(false);
        btnChonCBVe = new JButton("Chọn CB Về"); btnChonCBVe.setBackground(PRIMARY_COLOR); btnChonCBVe.setForeground(Color.WHITE);
        JButton btnChonGheVe = new JButton("Sơ đồ Ghế"); btnChonGheVe.setBackground(ACCENT_COLOR);
        
        gbcVe.gridx = 0; gbcVe.gridy = 2; pnlLuotVe.add(createStyledLabel("Chuyến bay:"), gbcVe);
        gbcVe.gridx = 1; pnlLuotVe.add(txtMaChuyenBayVe, gbcVe);
        gbcVe.gridx = 2; pnlLuotVe.add(btnChonCBVe, gbcVe);
        gbcVe.gridx = 3; pnlLuotVe.add(txtGheVe, gbcVe);
        gbcVe.gridx = 4; pnlLuotVe.add(btnChonGheVe, gbcVe);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 5; panel.add(pnlLuotVe, gbc); gbc.gridwidth = 1; row++;
        pnlLuotVe.setVisible(false);

        // 4. TỔNG TIỀN & LƯU
        txtTongTien = new JTextField(); txtTongTien.setEditable(false); txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18)); txtTongTien.setForeground(new Color(220, 38, 38)); txtTongTien.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 0; gbc.gridy = row; panel.add(createStyledLabel("TỔNG TIỀN:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(txtTongTien, gbc); gbc.gridwidth = 1; row++;

        JButton btnLuu = new JButton("Tạo Phiếu & Lưu Vé"); btnLuu.setBackground(ACCENT_COLOR); btnLuu.setFont(FONT_BOLD);
        JButton btnReset = new JButton("Làm mới"); btnReset.setFont(FONT_BOLD);
        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); pButton.setBackground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(180, 40)); btnReset.setPreferredSize(new Dimension(100, 40));
        pButton.add(btnLuu); pButton.add(btnReset);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 5; panel.add(pButton, gbc);

        // --- SỰ KIỆN ---
        rdKhuHoi.addActionListener(e -> { 
            pnlLuotVe.setVisible(true); 
            if(txtNgayVe.getText().isEmpty()) txtNgayVe.setText(txtNgayDi.getText());
            tinhTongTien(); 
        });
        rdMotChieu.addActionListener(e -> { pnlLuotVe.setVisible(false); tinhTongTien(); });
        
        javax.swing.event.ChangeListener spinnerListener = e -> tinhTongTien();
        spNguoiLon.addChangeListener(spinnerListener); spTreEm.addChangeListener(spinnerListener); spEmBe.addChangeListener(spinnerListener);
        cboHangVeDi.addActionListener(e -> tinhTongTien()); cboHangVeVe.addActionListener(e -> tinhTongTien());
        
        btnTimHK.addActionListener(e -> timKhachHang());
        txtTenNguoiDat.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusLost(java.awt.event.FocusEvent e) { btnTimHK.doClick(); } });
        
        btnChonCBDi.addActionListener(e -> chonChuyenBay(txtMaChuyenBayDi, true));
        btnChonCBVe.addActionListener(e -> chonChuyenBay(txtMaChuyenBayVe, false));
        btnChonGheDi.addActionListener(e -> chonGhe(txtMaChuyenBayDi, txtGheDi, true));
        btnChonGheVe.addActionListener(e -> chonGhe(txtMaChuyenBayVe, txtGheVe, false));
        btnReset.addActionListener(e -> resetForm());
        btnLuu.addActionListener(e -> xuLyLuuVe());

        return panel;
    }

    private JLabel createStyledLabel(String text) { JLabel lbl = new JLabel(text); lbl.setFont(FONT_BOLD); lbl.setForeground(PRIMARY_COLOR); return lbl; }

    // ================= CHỌN NGÀY (Cập nhật cho phép chuyển tháng) =================
    private void setupDatePicker(JTextField txtField, boolean isDi) {
        txtField.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { hienThiLich(txtField, isDi); }
        });
    }

    private void hienThiLich(JTextField txtField, boolean isDi) {
        JPopupMenu popup = new JPopupMenu(); 
        JPanel pnlMain = new JPanel(new BorderLayout()); 
        pnlMain.setBackground(Color.WHITE);
        
        JPanel pnlDays = new JPanel(new GridLayout(0, 7)); 
        pnlDays.setBackground(Color.WHITE);
        
        LocalDate initialMonth = LocalDate.now();
        try { if(!txtField.getText().isEmpty()) initialMonth = LocalDate.parse(txtField.getText(), dateFormatter); } catch(Exception ignored){}
        
        // Header chứa nút chuyển tháng
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JButton btnPrev = new JButton("<"); btnPrev.setFocusPainted(false);
        JButton btnNext = new JButton(">"); btnNext.setFocusPainted(false);
        JLabel lblHeader = new JLabel("", SwingConstants.CENTER);
        lblHeader.setFont(FONT_BOLD);
        
        pnlHeader.add(btnPrev, BorderLayout.WEST);
        pnlHeader.add(lblHeader, BorderLayout.CENTER);
        pnlHeader.add(btnNext, BorderLayout.EAST);
        
        pnlMain.add(pnlHeader, BorderLayout.NORTH);
        pnlMain.add(pnlDays, BorderLayout.CENTER);
        popup.add(pnlMain);
        
        // Mảng 1 phần tử để thay đổi giá trị trong Lambda
        final LocalDate[] currentMonthArr = {initialMonth};
        
        Runnable updateCalendar = () -> {
            pnlDays.removeAll();
            LocalDate currentMonth = currentMonthArr[0];
            lblHeader.setText("Tháng " + currentMonth.getMonthValue() + "/" + currentMonth.getYear());
            
            LocalDate minDate = LocalDate.now();
            if (!isDi && !txtNgayDi.getText().isEmpty()) {
                minDate = LocalDate.parse(txtNgayDi.getText(), dateFormatter);
            }

            LocalDate startOfMonth = currentMonth.withDayOfMonth(1);
            int startDayOfWeek = startOfMonth.getDayOfWeek().getValue(); if (startDayOfWeek == 7) startDayOfWeek = 0;
            int daysInMonth = currentMonth.lengthOfMonth();

            String[] dows = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
            for (String d : dows) { JLabel l = new JLabel(d, SwingConstants.CENTER); l.setFont(new Font("Arial", Font.BOLD, 12)); pnlDays.add(l); }

            for (int i = 0; i < 42; i++) {
                if (i < startDayOfWeek || i >= startDayOfWeek + daysInMonth) { pnlDays.add(new JLabel("")); continue; }
                int day = i - startDayOfWeek + 1;
                LocalDate cellDate = currentMonth.withDayOfMonth(day);
                JButton btnDay = new JButton(String.valueOf(day)); btnDay.setMargin(new Insets(2,2,2,2)); btnDay.setFocusPainted(false);
                
                if (cellDate.isBefore(minDate)) {
                    btnDay.setEnabled(false); btnDay.setForeground(Color.LIGHT_GRAY); btnDay.setBackground(Color.WHITE);
                } else {
                    btnDay.setBackground(Color.WHITE); btnDay.setForeground(Color.BLACK);
                    btnDay.addActionListener(ev -> {
                        txtField.setText(cellDate.format(dateFormatter)); popup.setVisible(false);
                        if(isDi) {
                            txtMaChuyenBayDi.setText(""); txtGheDi.setText(""); selectedGheDi.clear(); arrivalTimeDi = null; sanBayDenDi = null;
                            if(rdKhuHoi.isSelected() && !txtNgayVe.getText().isEmpty()) {
                                LocalDate nVe = LocalDate.parse(txtNgayVe.getText(), dateFormatter);
                                if(cellDate.isAfter(nVe)) txtNgayVe.setText(cellDate.format(dateFormatter));
                            }
                        } else { txtMaChuyenBayVe.setText(""); txtGheVe.setText(""); selectedGheVe.clear(); }
                        tinhTongTien();
                    });
                }
                pnlDays.add(btnDay);
            }
            pnlDays.revalidate(); pnlDays.repaint(); popup.pack();
        };

        btnPrev.addActionListener(e -> { currentMonthArr[0] = currentMonthArr[0].minusMonths(1); updateCalendar.run(); });
        btnNext.addActionListener(e -> { currentMonthArr[0] = currentMonthArr[0].plusMonths(1); updateCalendar.run(); });
        
        updateCalendar.run();
        popup.show(txtField, 0, txtField.getHeight());
    }

    // ================= TÌM KHÁCH HÀNG (Đã fix hiển thị lỗi rõ ràng) =================
    private void timKhachHang() {
        String tuKhoa = txtTenNguoiDat.getText().trim();
        if(tuKhoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên hành khách cần tìm!");
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            // Lưu ý: Nếu dùng SQL Server và tên có tiếng Việt, có thể bạn cần sửa truy vấn thành N'%' + ? + '%'
            // Cách viết an toàn cho SQL Server
            PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 maHK FROM ThongTinHanhKhach WHERE hoTen LIKE N'%' + ? + '%'");
            ps.setString(1, tuKhoa);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                txtMaHK.setText(rs.getString("maHK")); 
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy Hành Khách nào chứa từ khóa: " + tuKhoa + "\nVui lòng gõ đúng tên hoặc kiểm tra lại Data.");
            }
        } catch(Exception ex){
            // Hiển thị lỗi ra UI để dễ debug (Thường lỗi do sai tên bảng 'ThongTinHanhKhach' hoặc rớt mạng)
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi truy vấn CSDL: \n" + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= CHỌN CHUYẾN BAY =================
    private void chonChuyenBay(JTextField targetTextField, boolean isDi) {
        String strDate = isDi ? txtNgayDi.getText() : txtNgayVe.getText();
        if(strDate.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày trước!"); return; }
        LocalDate searchDate = LocalDate.parse(strDate, dateFormatter);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn chuyến bay " + (isDi ? "Lượt Đi" : "Lượt Về"), true);
        dialog.setSize(800, 400); // Thu nhỏ khung dialog một chút
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Mã CB", "Sân bay đi", "Sân bay đến", "Máy bay", "Giờ khởi hành", "Giờ đến"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable tb = new JTable(model); tb.setRowHeight(25); tb.setFont(FONT_PLAIN);

        try(Connection conn = DBConnection.getConnection()){
            String sql = "SELECT cb.maChuyenBay, tb.sanBayDi, tb.sanBayDen, cb.maMayBay, cb.ngayGioDi, cb.ngayGioDen " +
                         "FROM ChuyenBay cb JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay " +
                         "WHERE CAST(cb.ngayGioDi AS DATE) = ?";

            boolean isSameDayReturn = !isDi && searchDate.isEqual(LocalDate.parse(txtNgayDi.getText(), dateFormatter));
            
            if (!isDi && sanBayDenDi != null) {
                sql += " AND tb.sanBayDi = ?"; 
                if (isSameDayReturn && arrivalTimeDi != null) {
                    sql += " AND cb.ngayGioDi >= ?"; 
                }
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(searchDate));
            int paramIdx = 2;
            if (!isDi && sanBayDenDi != null) {
                ps.setString(paramIdx++, sanBayDenDi);
                if (isSameDayReturn && arrivalTimeDi != null) {
                    ps.setTimestamp(paramIdx, java.sql.Timestamp.valueOf(arrivalTimeDi.plusHours(3)));
                }
            }

            ResultSet rs = ps.executeQuery(); boolean hasData=false;
            while(rs.next()){
                hasData=true;
                model.addRow(new Object[]{ rs.getString("maChuyenBay"), rs.getString("sanBayDi"), rs.getString("sanBayDen"), 
                    rs.getString("maMayBay"), rs.getTimestamp("ngayGioDi").toLocalDateTime().format(dateTimeFormatter), 
                    rs.getTimestamp("ngayGioDen").toLocalDateTime().format(dateTimeFormatter) });
            }
            if(!hasData) JOptionPane.showMessageDialog(dialog, "Không có chuyến bay nào phù hợp tiêu chí!");
        } catch(Exception ex){ ex.printStackTrace(); }

        tb.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                int row = tb.getSelectedRow();
                targetTextField.setText(tb.getValueAt(row, 0).toString());
                
                if (isDi) {
                    txtGheDi.setText(""); selectedGheDi.clear(); giaGheDi = BigDecimal.ZERO;
                    sanBayDenDi = tb.getValueAt(row, 2).toString();
                    arrivalTimeDi = LocalDateTime.parse(tb.getValueAt(row, 5).toString(), dateTimeFormatter);
                    txtMaChuyenBayVe.setText(""); txtGheVe.setText(""); selectedGheVe.clear();
                } else {
                    txtGheVe.setText(""); selectedGheVe.clear(); giaGheVe = BigDecimal.ZERO;
                }
                tinhTongTien(); dialog.dispose();
            }
        });
        dialog.add(new JScrollPane(tb)); dialog.setVisible(true);
    }

    // ================= CHỌN GHẾ =================
    private void chonGhe(JTextField txtCB, JTextField txtGhe, boolean isDi) {
        String maCB = txtCB.getText().trim();
        if(maCB.isEmpty()){ JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay trước!"); return; }

        int tongGheCanChon = (int)spNguoiLon.getValue() + (int)spTreEm.getValue();
        if(tongGheCanChon == 0) { JOptionPane.showMessageDialog(this, "Chỉ có em bé thì không thể chọn ghế!"); return; }

        String maMayBay = cbDAO.layMaMayBay(maCB); 
        gui.admin.SoDoGhePanel panelSodo = new gui.admin.SoDoGhePanel(maMayBay, isDi ? "Chuyến Đi" : "Chuyến Về", tongGheCanChon);
        
        List<String> currentSelectedStr = new ArrayList<>();
        if (!txtGhe.getText().isEmpty()) for (String g : txtGhe.getText().split(",")) currentSelectedStr.add(g.trim());
        panelSodo.setSelectedSeats(currentSelectedStr);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sơ đồ ghế", true);
        dialog.setSize(850, 550); // Thu nhỏ hơn cũ xíu
        dialog.setLocationRelativeTo(this);

        panelSodo.setListener(new gui.admin.SoDoGhePanel.SoDoGheListener() {
            @Override public void onBack() { dialog.dispose(); }
            @Override public void onSeatsConfirmed(List<GheMayBay> selectedSeats) {
                List<String> dsGhe = new ArrayList<>(); BigDecimal tongGiaGhe = BigDecimal.ZERO;
                for(GheMayBay ghe : selectedSeats){
                    dsGhe.add(chuanHoaGhe(ghe.getSoGhe())); tongGiaGhe = tongGiaGhe.add(ghe.getGiaGhe());
                }
                txtGhe.setText(String.join(", ", dsGhe));
                if (isDi) { selectedGheDi = new ArrayList<>(selectedSeats); giaGheDi = tongGiaGhe; } 
                else { selectedGheVe = new ArrayList<>(selectedSeats); giaGheVe = tongGiaGhe; }
                tinhTongTien(); dialog.dispose();
            }
        });
        dialog.add(panelSodo); dialog.setVisible(true);
    }

    // ================= TÍNH TIỀN =================
    private void tinhTongTien() {
        try {
            int soNL = (int) spNguoiLon.getValue(), soTE = (int) spTreEm.getValue(), soEB = (int) spEmBe.getValue();
            String maHangVeDi = convertHangVe(cboHangVeDi.getSelectedItem().toString());
            BigDecimal tongTien = BigDecimal.ZERO;

            String cbDi = txtMaChuyenBayDi.getText().trim();
            if (!cbDi.isEmpty()) {
                tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbDi, maHangVeDi, "Người lớn").multiply(BigDecimal.valueOf(soNL)));
                if(soTE > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbDi, maHangVeDi, "Trẻ em").multiply(BigDecimal.valueOf(soTE)));
                if(soEB > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbDi, maHangVeDi, "Em bé").multiply(BigDecimal.valueOf(soEB)));
                tongTien = tongTien.add(giaGheDi);
            }

            if (rdKhuHoi.isSelected()) {
                String maHangVeVe = convertHangVe(cboHangVeVe.getSelectedItem().toString());
                String cbVe = txtMaChuyenBayVe.getText().trim();
                if (!cbVe.isEmpty()) {
                    tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbVe, maHangVeVe, "Người lớn").multiply(BigDecimal.valueOf(soNL)));
                    if(soTE > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbVe, maHangVeVe, "Trẻ em").multiply(BigDecimal.valueOf(soTE)));
                    if(soEB > 0) tongTien = tongTien.add(veBanDAO.tinhGiaVeFull(cbVe, maHangVeVe, "Em bé").multiply(BigDecimal.valueOf(soEB)));
                    tongTien = tongTien.add(giaGheVe);
                }
            }
            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTongTien.setText(tongTien.compareTo(BigDecimal.ZERO) == 0 ? "0" : vn.format(tongTien));
        } catch (Exception e) {}
    }

    // ================= LƯU VÉ =================
    private void xuLyLuuVe() {
        String maHK = txtMaHK.getText().trim(); String cbDi = txtMaChuyenBayDi.getText().trim();
        int tongHK = (int)spNguoiLon.getValue() + (int)spTreEm.getValue() + (int)spEmBe.getValue();
        int tongGhe = (int)spNguoiLon.getValue() + (int)spTreEm.getValue();

        if (maHK.isEmpty() || cbDi.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Khách và chọn chuyến đi!"); return; }
        if (selectedGheDi.size() != tongGhe) { JOptionPane.showMessageDialog(this, "Chọn đủ " + tongGhe + " ghế lượt đi (Không tính em bé)!"); return; }
        
        boolean isKhuHoi = rdKhuHoi.isSelected(); String cbVe = txtMaChuyenBayVe.getText().trim();
        if (isKhuHoi) {
            if (cbVe.isEmpty() || selectedGheVe.size() != tongGhe) { JOptionPane.showMessageDialog(this, "Chọn đủ chuyến bay và ghế lượt về!"); return; }
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection(); conn.setAutoCommit(false);
            
            String tienStr = txtTongTien.getText().replaceAll("[^0-9]", "");
            if(tienStr.isEmpty()) tienStr = "0";

            PhieuDatVe phieu = new PhieuDatVe();
            phieu.setNgayDat(LocalDate.now()); phieu.setSoLuongVe(isKhuHoi ? tongHK * 2 : tongHK);
            phieu.setTongTien(new BigDecimal(tienStr)); phieu.setTrangThaiThanhToan("Chưa thanh toán");
            String maPDV = pdv.insert(phieu, conn); if(maPDV == null) throw new RuntimeException("Lỗi tạo PDV");

            String loaiVe = isKhuHoi ? "Khứ hồi" : "Một chiều";
            luuDanhSachVe(conn, maPDV, maHK, cbDi, convertHangVe(cboHangVeDi.getSelectedItem().toString()), loaiVe, selectedGheDi);
            if (isKhuHoi) luuDanhSachVe(conn, maPDV, maHK, cbVe, convertHangVe(cboHangVeVe.getSelectedItem().toString()), loaiVe, selectedGheVe);

            conn.commit(); JOptionPane.showMessageDialog(this, "Tạo vé thành công!"); resetForm(); loadTable();
        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (SQLException e) {}
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        } finally { try { if (conn != null) conn.close(); } catch (SQLException e) {} }
    }

    private void luuDanhSachVe(Connection conn, String maPDV, String maHK, String maCB, String maHangVe, String loaiVe, List<GheMayBay> seats) throws Exception {
        int soNL = (int)spNguoiLon.getValue(), soTE = (int)spTreEm.getValue(), soEB = (int)spEmBe.getValue();
        String maMB = cbDAO.layMaMayBay(conn, maCB);
        int gheIdx = 0;

        for (int i=0; i < soNL + soTE + soEB; i++) {
            String loaiHK = (i < soNL) ? "Người lớn" : (i < soNL + soTE) ? "Trẻ em" : "Em bé";
            VeBan v = new VeBan(); v.setMaPhieuDatVe(maPDV); v.setMaHK(maHK); v.setMaChuyenBay(maCB);
            v.setMaHangVe(maHangVe); v.setLoaiHK(loaiHK); v.setLoaiVe(loaiVe);
            v.setGiaVe(veBanDAO.tinhGiaVeFull(maCB, maHangVe, loaiHK)); v.setTrangThaiVe("Đã đặt");

            if (loaiHK.equals("Em bé")) {
                v.setMaGhe(null); // Em bé không có ghế
            } else {
                String maGheDB = gmb.timMaGhe(conn, seats.get(gheIdx++).getSoGhe(), maMB);
                v.setMaGhe(maGheDB);
            }
            veBanDAO.insert(v, conn);
        }
    }

    private void resetForm() {
        txtTenNguoiDat.setText(""); txtMaHK.setText(""); rdMotChieu.setSelected(true); pnlLuotVe.setVisible(false);
        txtNgayDi.setText(LocalDate.now().format(dateFormatter)); cboHangVeDi.setSelectedIndex(0); cboHangVeVe.setSelectedIndex(0);
        spNguoiLon.setValue(1); spTreEm.setValue(0); spEmBe.setValue(0);
        txtMaChuyenBayDi.setText(""); txtGheDi.setText(""); selectedGheDi.clear(); giaGheDi = BigDecimal.ZERO; arrivalTimeDi = null; sanBayDenDi = null;
        txtMaChuyenBayVe.setText(""); txtGheVe.setText(""); selectedGheVe.clear(); giaGheVe = BigDecimal.ZERO;
        txtTongTien.setText("0");
    }

    private void searchVe() {
        String kw = txtSearch.getText().trim();
        String stt = cboTrangThai.getSelectedItem().toString();
        if (stt.equals("Tất cả")) stt = null;
        tableModel.setRowCount(0);
        for (VeBan v : veBanDAO.search(kw, stt)) {
            tableModel.addRow(new Object[]{ v.getMaVe(), v.getMaPhieuDatVe(), v.getMaChuyenBay(), v.getLoaiHK(), v.getLoaiVe(), v.getGiaVe(), v.getTrangThaiVe() });
        }
    }
    
    private void loadTable() {
        tableModel.setRowCount(0);
        for (VeBan v : veBanDAO.selectAll()) {
            tableModel.addRow(new Object[]{ v.getMaVe(), v.getMaPhieuDatVe(), v.getMaChuyenBay(), v.getLoaiHK(), v.getLoaiVe(), v.getGiaVe(), v.getTrangThaiVe() });
        }
    }

    private String convertHangVe(String ten) { switch (ten) { case "Thương gia": return "BUS"; case "Hạng nhất": return "FST"; case "Phổ thông đặc biệt": return "PECO"; default: return "ECO"; } }
    private String chuanHoaGhe(String ghe) { ghe = ghe.trim().toUpperCase(); if(Character.isLetter(ghe.charAt(0))) return ghe.substring(1) + ghe.charAt(0); return ghe; }
}