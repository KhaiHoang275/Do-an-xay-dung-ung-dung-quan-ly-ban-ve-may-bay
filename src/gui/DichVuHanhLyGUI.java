package gui;

import bll.DichVuBoSungBUS;
import model.DatVeSession;
import model.DichVuBoSung;
import model.HanhLy;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DichVuHanhLyGUI extends JPanel {

    // ===== BẢNG MÀU THƯƠNG HIỆU AIRLINER =====
    private final Color PRIMARY_COLOR = new Color(18, 32, 64); // Xanh Navy
    private final Color ACCENT_COLOR = new Color(255, 193, 7); // Vàng Gold
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 15);

    private DatVeSession session;
    private DichVuBoSungBUS dichVuBUS;
    
    private List<JComboBox<BaggageItem>> listCboHanhLy = new ArrayList<>();
    private List<JCheckBox> chkListDichVu = new ArrayList<>();
    private List<DichVuBoSung> listDichVuDB = new ArrayList<>();

    public DichVuHanhLyGUI(DatVeSession session) {
        this.session = session;
        this.dichVuBUS = new DichVuBoSungBUS();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // ================= HEADER =================
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        pnlHeader.setBackground(BG_MAIN);
        
        JLabel lblTitle = new JLabel("BƯỚC 3: CHỌN HÀNH LÝ & DỊCH VỤ");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(PRIMARY_COLOR);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(BG_MAIN);
        
        // --- HÀNH LÝ KÝ GỬI ---
        JPanel pnlHanhLy = new JPanel();
        pnlHanhLy.setLayout(new BoxLayout(pnlHanhLy, BoxLayout.Y_AXIS));
        pnlHanhLy.setBackground(Color.WHITE);
        pnlHanhLy.setBorder(createCustomTitledBorder("✈ 1. HÀNH LÝ KÝ GỬI"));
        pnlHanhLy.setMaximumSize(new Dimension(800, 300)); // Ép form không bị giãn quá đà

        BaggageItem[] optsHanhLy = {
                new BaggageItem("Không mang thêm hành lý (+0 VNĐ)", 0, BigDecimal.ZERO),
                new BaggageItem("Gói 20kg (+ 300.000 VNĐ)", 20, new BigDecimal("300000")),
                new BaggageItem("Gói 30kg (+ 500.000 VNĐ)", 30, new BigDecimal("500000")),
                new BaggageItem("Gói 40kg (+ 700.000 VNĐ)", 40, new BigDecimal("700000"))
        };

        if (session.danhSachHanhKhach != null) {
            for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)); 
                row.setBackground(Color.WHITE);
                
                JLabel lblKhach = new JLabel("Hành khách " + (i + 1) + " (" + hk.getHoTen() + "): ");
                lblKhach.setFont(FONT_LABEL);
                lblKhach.setForeground(new Color(100, 110, 120));
                lblKhach.setPreferredSize(new Dimension(350, 30));
                
                JComboBox<BaggageItem> cbo = new JComboBox<>(optsHanhLy);
                cbo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                cbo.setPreferredSize(new Dimension(300, 35));
                cbo.setBackground(Color.WHITE);
                
                listCboHanhLy.add(cbo);
                row.add(lblKhach); row.add(cbo);
                pnlHanhLy.add(row);
            }
        }
        pnlContent.add(pnlHanhLy);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- DỊCH VỤ BỔ SUNG ---
        JPanel pnlDichVu = new JPanel(new BorderLayout());
        pnlDichVu.setBackground(Color.WHITE);
        pnlDichVu.setBorder(createCustomTitledBorder("⭐ 2. DỊCH VỤ BỔ SUNG"));
        pnlDichVu.setMaximumSize(new Dimension(800, 200));

        JPanel chkContainer = new JPanel();
        chkContainer.setLayout(new BoxLayout(chkContainer, BoxLayout.Y_AXIS));
        chkContainer.setBackground(Color.WHITE);
        chkContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Giả lập Dịch vụ nếu Database của bạn trống
        try {
            listDichVuDB = dichVuBUS.docDanhSachDichVu();
        } catch(Exception e) { listDichVuDB = new ArrayList<>(); }
        
        if(listDichVuDB == null || listDichVuDB.isEmpty()) {
            DichVuBoSung dv1 = new DichVuBoSung(); dv1.setTenDichVu("Bảo hiểm trễ chuyến bay"); dv1.setDonGia(new BigDecimal("80000"));
            DichVuBoSung dv2 = new DichVuBoSung(); dv2.setTenDichVu("Suất ăn nóng trên máy bay"); dv2.setDonGia(new BigDecimal("120000"));
            DichVuBoSung dv3 = new DichVuBoSung(); dv3.setTenDichVu("Phòng chờ thương gia"); dv3.setDonGia(new BigDecimal("350000"));
            listDichVuDB.add(dv1); listDichVuDB.add(dv2); listDichVuDB.add(dv3);
        }

        for (DichVuBoSung dv : listDichVuDB) {
            JCheckBox chk = new JCheckBox("  " + dv.getTenDichVu() + " (+ " + String.format("%,d", dv.getDonGia().longValue()) + " VNĐ)");
            chk.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            chk.setForeground(PRIMARY_COLOR);
            chk.setBackground(Color.WHITE);
            chk.setFocusPainted(false);
            chkListDichVu.add(chk);
            chkContainer.add(chk);
            chkContainer.add(Box.createRigidArea(new Dimension(0, 15))); 
        }
        
        pnlDichVu.add(chkContainer, BorderLayout.CENTER);
        pnlContent.add(pnlDichVu);

        // Đệm lùi vào giữa để giống với NhapHanhKhachGUI
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BG_MAIN);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 150));
        wrapperPanel.add(pnlContent, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        pnlFooter.setBackground(BG_MAIN);
        
        JButton btnQuayLai = new JButton("Quay lại nhập thông tin");
        // ĐÃ SỬA: Đổi tên nút thành "Thanh toán"
        JButton btnTiepTuc = new JButton("Thanh toán");

        styleSecondaryButton(btnQuayLai);
        stylePrimaryButton(btnTiepTuc);
        btnQuayLai.setPreferredSize(new Dimension(220, 45));
        btnTiepTuc.setPreferredSize(new Dimension(250, 45));

        pnlFooter.add(btnTiepTuc); // Nút tiếp tục bên phải
        pnlFooter.add(btnQuayLai); // Nút quay lại bên trái
        add(pnlFooter, BorderLayout.SOUTH);

        // ================= EVENTS =================
        btnQuayLai.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null) {
                parent.removeAll();
                parent.setLayout(new BorderLayout());
                parent.add(new gui.user.NhapHanhKhachGUI(session), BorderLayout.CENTER);
                parent.revalidate();
                parent.repaint();
            }
        });

        btnTiepTuc.addActionListener(e -> processTiepTuc());
    }

    private void processTiepTuc() {
        BigDecimal tongTienDichVu = BigDecimal.ZERO;
        session.danhSachHanhLy.clear(); 
        
        for (int i = 0; i < listCboHanhLy.size(); i++) {
            BaggageItem selected = (BaggageItem) listCboHanhLy.get(i).getSelectedItem();
            if (selected.kg > 0) {
                tongTienDichVu = tongTienDichVu.add(selected.price);
                HanhLy hl = new HanhLy();
                hl.setSoKg(new BigDecimal(selected.kg));
                hl.setGiaTien(selected.price);
                hl.setGhiChu("Khách: " + session.danhSachHanhKhach.get(i).getHoTen());
                session.danhSachHanhLy.add(hl);
            }
        }

        for (int i = 0; i < chkListDichVu.size(); i++) {
            if (chkListDichVu.get(i).isSelected()) {
                tongTienDichVu = tongTienDichVu.add(listDichVuDB.get(i).getDonGia());
            }
        }
        
        // LƯU TIỀN VÀO SESSION
        session.tongTienDichVu = tongTienDichVu;
        
        // CHUYỂN TRANG SANG THANH TOÁN
        Container parent = this.getParent();
        if(parent != null) {
            parent.removeAll();
            parent.setLayout(new BorderLayout());
            // Bỏ comment dòng dưới khi bạn tạo file ThanhToanGUI
            parent.add(new gui.ThanhToanGUI(session), BorderLayout.CENTER);
            
            parent.revalidate();
            parent.repaint();
        }
    }

    // ================= HELPER STYLE =================
    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR); 
        btn.setForeground(PRIMARY_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btn.setFocusPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); 
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        btn.setFocusPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private TitledBorder createCustomTitledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setTitleColor(PRIMARY_COLOR);
        b.setBorder(BorderFactory.createCompoundBorder(b.getBorder(), BorderFactory.createEmptyBorder(10, 15, 15, 15)));
        return b;
    }

    class BaggageItem {
        String label; int kg; BigDecimal price;
        public BaggageItem(String label, int kg, BigDecimal price) { this.label = label; this.kg = kg; this.price = price; }
        @Override public String toString() { return label; }
    }
    
}