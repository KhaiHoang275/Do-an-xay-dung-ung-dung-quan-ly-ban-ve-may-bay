package gui.admin;

import bll.GheMayBayBUS;
import model.GheMayBay;
import model.TrangThaiGhe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoDoGhePanel extends JPanel {

    // === THÊM INTERFACE BACK ===
    public interface BackListener {
        void onBack();
    }
    private BackListener backListener;

    public void setBackListener(BackListener listener) {
        this.backListener = listener;
    }
    // ===========================

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

    public SoDoGhePanel(String maMayBay, String tenMayBay) {
        this.maMayBay = maMayBay;
        this.tenMayBay = tenMayBay;
        this.gheMayBayBUS = new GheMayBayBUS();
        this.mapGhe = new HashMap<>();

        loadDuLieuGheTuDB();

        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
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
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icons/icons8-back-24.png")); // Thêm icon back nếu có
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            btnBack.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {}
        
        // BẮT SỰ KIỆN QUAY LẠI Ở ĐÂY
        btnBack.addActionListener(e -> {
             if (backListener != null) {
                 backListener.onBack();
             }
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

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

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (realSeat == null) return;

                if (btn.getBackground().equals(COLOR_AVAILABLE)) {
                    btn.setBackground(COLOR_SELECTED);
                    System.out.println("Đã chọn ghế: " + realSeat.getSoGhe() + " - Giá: " + realSeat.getGiaGhe());
                } else if (btn.getBackground().equals(COLOR_SELECTED)) {
                    btn.setBackground(COLOR_AVAILABLE);
                    System.out.println("Bỏ chọn ghế: " + realSeat.getSoGhe());
                } else if (btn.getBackground().equals(COLOR_BOOKED)) {
                    JOptionPane.showMessageDialog(SoDoGhePanel.this, 
                        "Ghế " + realSeat.getSoGhe() + " đã được đặt, vui lòng chọn ghế khác!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                } else if (btn.getBackground().equals(COLOR_MAINTENANCE)) {
                    JOptionPane.showMessageDialog(SoDoGhePanel.this, 
                        "Ghế " + realSeat.getSoGhe() + " đang bảo trì!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        JFrame testFrame = new JFrame("Test Sơ Đồ Ghế");
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(800, 600);
        testFrame.setLocationRelativeTo(null);

        SoDoGhePanel panel = new SoDoGhePanel("MB001", "Boeing 737");
        panel.setBackListener(() -> {
            System.out.println("Đã bấm trở về danh sách!");
        });

        testFrame.add(panel);
        testFrame.setVisible(true);
    }
}