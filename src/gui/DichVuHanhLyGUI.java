package gui;

import bll.DichVuBoSungBUS;
import model.DatVeSession;
import model.DichVuBoSung;
import model.HanhLy;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DichVuHanhLyGUI extends JPanel {

    private final Color PRIMARY_COLOR = new Color(18, 32, 64);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 15);

    private DatVeSession session;
    private DichVuBoSungBUS dichVuBUS;
    
    private List<JComboBox<BaggageItem>> listCboHanhLy = new ArrayList<>();
    private List<JCheckBox> chkListDichVu = new ArrayList<>();
    private List<DichVuBoSung> listDichVuDB = new ArrayList<>();
    
    private JLabel lblFooterTotal;
    private BigDecimal tongTienTamTinh;

    public DichVuHanhLyGUI(DatVeSession session) {
        this.session = session;
        this.dichVuBUS = new DichVuBoSungBUS();
        this.tongTienTamTinh = session.tongTienVe; // Khởi tạo bằng tiền vé cũ
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false); // Xuyên thấu
        
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        pnlHeader.setOpaque(false);
        pnlHeader.add(createStepper());
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setOpaque(false);
        
        // HÀNH LÝ
        JPanel pnlHanhLy = new JPanel(); 
        pnlHanhLy.setLayout(new BoxLayout(pnlHanhLy, BoxLayout.Y_AXIS)); 
        pnlHanhLy.setBackground(Color.WHITE); 
        pnlHanhLy.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), "✈ 1. HÀNH LÝ KÝ GỬI", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR)); 
        pnlHanhLy.setMaximumSize(new Dimension(800, 300));
        BaggageItem[] optsHanhLy = { new BaggageItem("Không mang thêm hành lý", 0, BigDecimal.ZERO), new BaggageItem("Gói 20kg (+300.000 VNĐ)", 20, new BigDecimal("300000")), new BaggageItem("Gói 40kg (+700.000 VNĐ)", 40, new BigDecimal("700000")) };

        if (session.danhSachHanhKhach != null) {
            for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)); row.setBackground(Color.WHITE);
                JLabel lblKhach = new JLabel("Hành khách " + (i + 1) + ": "); lblKhach.setPreferredSize(new Dimension(200, 30)); lblKhach.setFont(FONT_LABEL);
                JComboBox<BaggageItem> cbo = new JComboBox<>(optsHanhLy); cbo.setPreferredSize(new Dimension(300, 35));
                cbo.addActionListener(e -> updateTongTienHienTai()); 
                listCboHanhLy.add(cbo); row.add(lblKhach); row.add(cbo); pnlHanhLy.add(row);
            }
        }
        pnlContent.add(pnlHanhLy); 
        pnlContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // DỊCH VỤ BỔ SUNG
        JPanel pnlDichVu = new JPanel(new BorderLayout()); 
        pnlDichVu.setBackground(Color.WHITE); 
        pnlDichVu.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), "⭐ 2. DỊCH VỤ BỔ SUNG", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR)); 
        pnlDichVu.setMaximumSize(new Dimension(800, 200));
        
        JPanel chkContainer = new JPanel(); 
        chkContainer.setLayout(new BoxLayout(chkContainer, BoxLayout.Y_AXIS)); 
        chkContainer.setBackground(Color.WHITE); 
        chkContainer.setBorder(new EmptyBorder(10,20,10,20));
        
        try { 
            listDichVuDB = dichVuBUS.docDanhSachDichVu(); 
        } catch(Exception e) { 
            listDichVuDB = new ArrayList<>(); 
        }
        
        if(listDichVuDB == null || listDichVuDB.isEmpty()) {
            DichVuBoSung dv1 = new DichVuBoSung(); dv1.setTenDichVu("Bảo hiểm trễ chuyến bay"); dv1.setDonGia(new BigDecimal("80000"));
            DichVuBoSung dv2 = new DichVuBoSung(); dv2.setTenDichVu("Suất ăn nóng trên máy bay"); dv2.setDonGia(new BigDecimal("120000"));
            DichVuBoSung dv3 = new DichVuBoSung(); dv3.setTenDichVu("Phòng chờ thương gia"); dv3.setDonGia(new BigDecimal("350000"));
            listDichVuDB.add(dv1); listDichVuDB.add(dv2); listDichVuDB.add(dv3);
        }

        for (DichVuBoSung dv : listDichVuDB) {
            JCheckBox chk = new JCheckBox(" " + dv.getTenDichVu() + " (+ " + String.format("%,d", dv.getDonGia().longValue()) + " VNĐ)");
            chk.setFont(new Font("Segoe UI", Font.PLAIN, 15)); 
            chk.setBackground(Color.WHITE);
            chk.setForeground(PRIMARY_COLOR);
            chk.setFocusPainted(false);
            chk.addActionListener(e -> updateTongTienHienTai()); 
            chkListDichVu.add(chk); 
            chkContainer.add(chk); 
            chkContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        pnlDichVu.add(chkContainer, BorderLayout.CENTER); 
        pnlContent.add(pnlDichVu);

        pnlContent.add(Box.createVerticalGlue()); 

        JPanel wrapperPanel = new JPanel(new BorderLayout()); wrapperPanel.setOpaque(false); wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 150)); wrapperPanel.add(pnlContent, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(wrapperPanel); scrollPane.setBorder(null); scrollPane.setOpaque(false); scrollPane.getViewport().setOpaque(false); scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        add(createStickyFooter(), BorderLayout.SOUTH);
    }
    
    private void updateTongTienHienTai() {
        BigDecimal tienDichVu = BigDecimal.ZERO;
        for (JComboBox<BaggageItem> cbo : listCboHanhLy) tienDichVu = tienDichVu.add(((BaggageItem) cbo.getSelectedItem()).price);
        for (int i = 0; i < chkListDichVu.size(); i++) if (chkListDichVu.get(i).isSelected()) tienDichVu = tienDichVu.add(listDichVuDB.get(i).getDonGia());
        
        session.tongTienDichVu = tienDichVu;
        tongTienTamTinh = session.tongTienVe.add(tienDichVu);
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblFooterTotal.setText(vn.format(tongTienTamTinh) + " VNĐ");
    }

    // Hàm chuyển trang tiện ích giữ Navbar
    private void switchPage(JPanel newPanel) {
        Container container = SwingUtilities.getAncestorOfClass(gui.user.MainFrame.class, this);
        if (container instanceof gui.user.MainFrame) {
            gui.user.MainFrame mainFrame = (gui.user.MainFrame) container;
            mainFrame.getContentPane().remove(1); 
            newPanel.setOpaque(false);
            mainFrame.getContentPane().add(newPanel, BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }

    private JPanel createStickyFooter() {
        JPanel footer = new JPanel(new BorderLayout()); footer.setBackground(Color.WHITE); footer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)), new EmptyBorder(10, 50, 10, 50)));
        
        JButton btnQuayLai = new JButton("Quay lại"); 
        btnQuayLai.setBackground(Color.WHITE); 
        btnQuayLai.setPreferredSize(new Dimension(150, 45)); 
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btnQuayLai.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ĐÃ FIX: Gọi hàm switchPage để quay lại trang Hành Khách
        btnQuayLai.addActionListener(e -> switchPage(new gui.user.NhapHanhKhachGUI(session)));
        
        footer.add(btnQuayLai, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); pnlRight.setOpaque(false);
        JPanel pnlTotalText = new JPanel(new GridLayout(2, 1)); pnlTotalText.setOpaque(false);
        pnlTotalText.add(new JLabel("Tổng tiền tạm tính:", SwingConstants.RIGHT)); 
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblFooterTotal = new JLabel(vn.format(tongTienTamTinh) + " VNĐ", SwingConstants.RIGHT); lblFooterTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlTotalText.add(lblFooterTotal);
        
        JButton btnNext = new JButton("Đến trang Thanh Toán ➔"); 
        btnNext.setBackground(new Color(255, 193, 7)); 
        btnNext.setPreferredSize(new Dimension(250, 45)); 
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btnNext.setForeground(new Color(18, 32, 64));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnNext.addActionListener(e -> processTiepTuc());
        
        pnlRight.add(pnlTotalText); pnlRight.add(btnNext); footer.add(pnlRight, BorderLayout.EAST);
        return footer;
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
        session.tongTienDichVu = tongTienDichVu;
        
        switchPage(new gui.ThanhToanGUI(session));
    }
    
    private JPanel createStepper() {
        JPanel pnlStepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); pnlStepper.setBackground(new Color(18, 32, 64, 200)); pnlStepper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        Font fontStep = new Font("Segoe UI", Font.BOLD, 16); Font fontArrow = new Font("Segoe UI", Font.BOLD, 18);
        JLabel step1 = new JLabel("1. Chuyến bay"); step1.setForeground(Color.WHITE); step1.setFont(fontStep); JLabel arr1 = new JLabel(" ➔ "); arr1.setForeground(Color.WHITE); arr1.setFont(fontArrow);
        JLabel step2 = new JLabel("2. Hành khách"); step2.setForeground(Color.WHITE); step2.setFont(fontStep); JLabel arr2 = new JLabel(" ➔ "); arr2.setForeground(Color.WHITE); arr2.setFont(fontArrow);
        JLabel step3 = new JLabel("3. Dịch vụ"); step3.setForeground(ACCENT_COLOR); step3.setFont(fontStep); JLabel arr3 = new JLabel(" ➔ "); arr3.setForeground(Color.WHITE); arr3.setFont(fontArrow);
        JLabel step4 = new JLabel("4. Thanh toán"); step4.setForeground(Color.LIGHT_GRAY); step4.setFont(fontStep);
        pnlStepper.add(step1); pnlStepper.add(arr1); pnlStepper.add(step2); pnlStepper.add(arr2); pnlStepper.add(step3); pnlStepper.add(arr3); pnlStepper.add(step4);
        return pnlStepper;
    }

    class BaggageItem {
        String label; int kg; BigDecimal price;
        public BaggageItem(String label, int kg, BigDecimal price) { this.label = label; this.kg = kg; this.price = price; }
        @Override public String toString() { return label; }
    }
}