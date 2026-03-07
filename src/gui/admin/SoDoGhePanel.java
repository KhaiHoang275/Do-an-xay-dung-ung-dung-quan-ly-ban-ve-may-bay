package gui.admin;

import bll.GheMayBayBUS;
import model.GheMayBay;
import model.TrangThaiGhe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoDoGhePanel extends JPanel {

    // === NÂNG CẤP INTERFACE (Dùng chung cho cả Admin và Customer) ===
    public interface SoDoGheListener {
        void onBack(); // Dùng khi bấm nút "Trở về danh sách"
        void onSeatSelected(GheMayBay ghe); // Dùng khi bấm "Chọn ghế này"
    }
    private SoDoGheListener listener;

    public void setListener(SoDoGheListener listener) {
        this.listener = listener;
    }
    // ==============================================================

    // Màu sắc đồng bộ hệ thống
    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(30, 41, 59);

    private final Color COLOR_AVAILABLE = new Color(76, 175, 80); // Xanh lá - Trống
    private final Color COLOR_BOOKED = new Color(239, 68, 68);    // Đỏ - Đã đặt
    private final Color COLOR_SELECTED = new Color(59, 130, 246);  // Xanh dương - Đang chọn
    private final Color COLOR_MAINTENANCE = new Color(158, 158, 158); // Xám - Bảo trì/Không tồn tại

    private String maMayBay;
    private String tenMayBay;
    private GheMayBayBUS gheMayBayBUS;
    private Map<String, GheMayBay> mapGhe; 
    private int totalRows = 30; 
    private int maxSeats = 1;

    // Các Component phần dưới (Footer)
    private JLabel lblGiaTien;
    private JButton btnChonGhe;
    private java.util.List<GheMayBay> selectedSeats = new java.util.ArrayList<>();
    private java.util.List<JButton> selectedButtons = new java.util.ArrayList<>();
    private java.util.List<JButton> allSeatButtons = new java.util.ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("###,###,### VNĐ");

    public SoDoGhePanel(String maMayBay, String tenMayBay, int maxSeats) {
        this.maMayBay = maMayBay;
        this.tenMayBay = tenMayBay;
        this.maxSeats = maxSeats;
        this.gheMayBayBUS = new GheMayBayBUS();
        this.mapGhe = new HashMap<>();

        loadDuLieuGheTuDB();

        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        setSelectedSeats(new java.util.ArrayList<>());
    }

    private void loadDuLieuGheTuDB() {
        ArrayList<GheMayBay> danhSachGhe = gheMayBayBUS.getGheByMayBay(this.maMayBay);
        
        for (GheMayBay ghe : danhSachGhe) {
            mapGhe.put(ghe.getSoGhe().toUpperCase(), ghe);
            try {
                String rowStr = ghe.getSoGhe().replaceAll("[^0-9]", ""); 
                if (!rowStr.isEmpty()) {
                    int row = Integer.parseInt(rowStr);
                    if (row > totalRows) {
                        totalRows = row; 
                    }
                }
            } catch (Exception e) {}
        }
    }

    private void initComponents() {
        // ==========================================
        // 1. HEADER
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("SƠ ĐỒ CHỖ NGỒI - " + tenMayBay.toUpperCase() + " (" + maMayBay + ")", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY);

        // NÚT QUAY LẠI
        JButton btnBack = new JButton("Trở về danh sách");
        btnBack.setBackground(TABLE_HEADER);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setFocusPainted(false);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icons/icons8-back-24.png"));
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            btnBack.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {}
        
        btnBack.addActionListener(e -> {
             if (listener != null) {
                 listener.onBack();
             }
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CHÚ GIẢI TRẠNG THÁI (LEGEND) & BẢN ĐỒ GHẾ
        // ==========================================
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setOpaque(false);
        
        legendPanel.add(createLegendItem("Chỗ trống (Đang mở bán)", COLOR_AVAILABLE));
        legendPanel.add(createLegendItem("Chỗ đã đặt (Hết chỗ)", COLOR_BOOKED));
        legendPanel.add(createLegendItem("Đang bảo trì / Không tồn tại", COLOR_MAINTENANCE));
        legendPanel.add(createLegendItem("Đang chọn", COLOR_SELECTED));

        JPanel seatMapContainer = new JPanel(new BorderLayout());
        seatMapContainer.setBackground(Color.WHITE);
        seatMapContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel seatGrid = new JPanel(new GridLayout(totalRows, 7, 5, 5));
        seatGrid.setOpaque(false);

        String[] columns = {"A", "B", "C", "", "D", "E", "F"}; 

        for (int row = 1; row <= totalRows; row++) {
            for (String col : columns) {
                if (col.isEmpty()) {
                    JLabel lblAisle = new JLabel(String.valueOf(row), SwingConstants.CENTER);
                    lblAisle.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    lblAisle.setForeground(Color.GRAY);
                    seatGrid.add(lblAisle);
                } else {
                    String seatNumber1 = row + col; 
                    String seatNumber2 = col + row; 

                    GheMayBay realSeat = mapGhe.get(seatNumber1);
                    if (realSeat == null) {
                        realSeat = mapGhe.get(seatNumber2);
                    }

                    JButton btnSeat = createSeatButton(seatNumber1, realSeat);
                    allSeatButtons.add(btnSeat);
                    seatGrid.add(btnSeat);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(seatGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 

        seatMapContainer.add(legendPanel, BorderLayout.NORTH);
        seatMapContainer.add(scrollPane, BorderLayout.CENTER);
        add(seatMapContainer, BorderLayout.CENTER);

        // ==========================================
        // 3. BOTTOM PANEL (Hiển thị giá & Nút chọn)
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setOpaque(false);
        
        lblGiaTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblGiaTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGiaTien.setForeground(PRIMARY);

        btnChonGhe = new JButton("Đặt chỗ");
        btnChonGhe.setBackground(new Color(255, 152, 0)); // Màu cam nổi bật
        btnChonGhe.setForeground(Color.WHITE);
        btnChonGhe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChonGhe.setPreferredSize(new Dimension(150, 40));
        btnChonGhe.setFocusPainted(false);
        btnChonGhe.setEnabled(false); // Khóa nút lúc ban đầu chưa chọn ghế
        
        // Sự kiện khi bấm Xác Nhận Chọn Ghế
        btnChonGhe.addActionListener(e -> {
            if(selectedSeats.isEmpty()) return;

            for(GheMayBay ghe : selectedSeats){
                listener.onSeatSelected(ghe);
            }

            Window w = SwingUtilities.getWindowAncestor(SoDoGhePanel.this);
            if(w != null) w.dispose();

        });

        bottomPanel.add(lblGiaTien);
        bottomPanel.add(btnChonGhe);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        JLabel colorBox = new JLabel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(colorBox);
        panel.add(lblText);
        return panel;
    }

    private JButton createSeatButton(String displaySeatNumber, GheMayBay realSeat) {
        JButton btn = new JButton(displaySeatNumber);
        btn.setPreferredSize(new Dimension(45, 45)); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(Color.WHITE);

        if (realSeat != null) {
            TrangThaiGhe tt = realSeat.getTrangThai();
            
            if (tt == TrangThaiGhe.TRONG) {
                btn.setBackground(COLOR_AVAILABLE);
            } else if (tt == TrangThaiGhe.DA_DAT) {
                btn.setBackground(COLOR_BOOKED);
            } else if (tt == TrangThaiGhe.BAO_TRI) {
                btn.setBackground(COLOR_MAINTENANCE);
            } else if (tt == TrangThaiGhe.DA_XOA) {
                btn.setBackground(new Color(245, 245, 245)); 
                btn.setForeground(Color.LIGHT_GRAY);
                btn.setEnabled(false);
            }
        } else {
            btn.setBackground(new Color(230, 230, 230)); 
            btn.setForeground(Color.GRAY);
            btn.setEnabled(false);
        }

                btn.addActionListener(e -> {

            if (realSeat == null) return;

            boolean daChon = selectedSeats.stream().anyMatch(g -> g.getSoGhe().equalsIgnoreCase(realSeat.getSoGhe()));

            if (daChon) {

                btn.setBackground(COLOR_AVAILABLE);
                selectedSeats.removeIf(g -> g.getSoGhe().equalsIgnoreCase(realSeat.getSoGhe()));
                selectedButtons.remove(btn);

            } else {

                if (selectedSeats.size() >= maxSeats) {
                    JOptionPane.showMessageDialog(
                            SwingUtilities.getWindowAncestor(SoDoGhePanel.this),
                            "Đã chọn đủ " + maxSeats + " ghế!"
                    );
                    return;
                }

                if (realSeat.getTrangThai() == TrangThaiGhe.DA_DAT) {
                    JOptionPane.showMessageDialog(
                            SoDoGhePanel.this,
                            "Ghế " + realSeat.getSoGhe() + " đã được đặt!"
                    );
                    return;
                }

                if (realSeat.getTrangThai() == TrangThaiGhe.BAO_TRI) {
                    JOptionPane.showMessageDialog(
                            SoDoGhePanel.this,
                            "Ghế " + realSeat.getSoGhe() + " đang bảo trì!"
                    );
                    return;
                }

                btn.setBackground(COLOR_SELECTED);
                selectedSeats.add(realSeat);
                selectedButtons.add(btn);
            }

            BigDecimal tong = BigDecimal.ZERO;

            for (GheMayBay g : selectedSeats) {
                tong = tong.add(g.getGiaGhe());
            }

            lblGiaTien.setText("Tổng tiền: " + formatter.format(tong));
            btnChonGhe.setEnabled(!selectedSeats.isEmpty());
        });

        return btn;
    }

    public void setSelectedSeats(java.util.List<String> seatNumbers){
        selectedSeats.clear();
        selectedButtons.clear();

        for(JButton btn : allSeatButtons){

            String seat = btn.getText();

            if(seatNumbers.contains(seat)){

                btn.setBackground(COLOR_SELECTED);

                GheMayBay g = mapGhe.get(seat);
                if(g != null){
                    selectedSeats.add(g);
                    selectedButtons.add(btn);
                }

            }else{

                GheMayBay g = mapGhe.get(seat);

                if(g != null && g.getTrangThai() == TrangThaiGhe.TRONG){
                    btn.setBackground(COLOR_AVAILABLE);
                }

            }
        }

        btnChonGhe.setEnabled(!selectedSeats.isEmpty());
    }

    // Hàm Main để Test độc lập
    public static void main(String[] args) {
        JFrame testFrame = new JFrame("Test Sơ Đồ Ghế");
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(800, 600);
        testFrame.setLocationRelativeTo(null);

        SoDoGhePanel panel = new SoDoGhePanel("MB001", "Boeing 737", 3);
        panel.setListener(new SoDoGheListener() {
            @Override
            public void onBack() {
                System.out.println("Đã bấm trở về danh sách!");
            }

            @Override
            public void onSeatSelected(GheMayBay ghe) {
                System.out.println("Đã chọn ghế: " + ghe.getSoGhe() + " | Giá: " + ghe.getGiaGhe());
            }
        });

        testFrame.add(panel);
        testFrame.setVisible(true);
    }
}