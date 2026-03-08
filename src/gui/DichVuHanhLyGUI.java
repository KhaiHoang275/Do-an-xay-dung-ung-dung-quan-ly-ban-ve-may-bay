package gui;

import bll.DichVuBoSungBUS;
import model.DatVeSession;
import model.DichVuBoSung;
import model.HanhLy;
import model.ThongTinHanhKhach;
import gui.user.NhapHanhKhachGUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DichVuHanhLyGUI extends JPanel {

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
        setBackground(new Color(245, 247, 250));

        // ================= HEADER =================
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        JLabel lblTitle = new JLabel("BƯỚC 3: CHỌN HÀNH LÝ & DỊCH VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(220, 38, 38));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(new Color(245, 247, 250));
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- HÀNH LÝ KÝ GỬI ---
        JPanel pnlHanhLy = new JPanel();
        pnlHanhLy.setLayout(new BoxLayout(pnlHanhLy, BoxLayout.Y_AXIS));
        pnlHanhLy.setBackground(Color.WHITE);
        pnlHanhLy.setBorder(createCustomTitledBorder("1. HÀNH LÝ KÝ GỬI"));

        BaggageItem[] optsHanhLy = {
                new BaggageItem("Không mang thêm hành lý (+0 VNĐ)", 0, BigDecimal.ZERO),
                new BaggageItem("Gói 20kg (+ 300.000 VNĐ)", 20, new BigDecimal("300000")),
                new BaggageItem("Gói 30kg (+ 500.000 VNĐ)", 30, new BigDecimal("500000")),
                new BaggageItem("Gói 40kg (+ 700.000 VNĐ)", 40, new BigDecimal("700000"))
        };

        if (session.danhSachHanhKhach != null) {
            for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); 
                row.setBackground(Color.WHITE);
                
                JLabel lblKhach = new JLabel("Hành khách " + (i + 1) + " (" + hk.getHoTen() + "): ");
                lblKhach.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblKhach.setPreferredSize(new Dimension(280, 30));
                
                JComboBox<BaggageItem> cbo = new JComboBox<>(optsHanhLy);
                cbo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cbo.setPreferredSize(new Dimension(300, 35));
                
                listCboHanhLy.add(cbo);
                row.add(lblKhach); row.add(cbo);
                pnlHanhLy.add(row);
            }
        }
        pnlContent.add(pnlHanhLy);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- DỊCH VỤ BỔ SUNG ---
        JPanel pnlDichVu = new JPanel(new BorderLayout());
        pnlDichVu.setBackground(Color.WHITE);
        pnlDichVu.setBorder(createCustomTitledBorder("2. DỊCH VỤ BỔ SUNG"));

        JPanel chkContainer = new JPanel();
        chkContainer.setLayout(new BoxLayout(chkContainer, BoxLayout.Y_AXIS));
        chkContainer.setBackground(Color.WHITE);

        // Giả lập Dịch vụ nếu Database của bạn trống
        try {
            listDichVuDB = dichVuBUS.docDanhSachDichVu();
        } catch(Exception e) { listDichVuDB = new ArrayList<>(); }
        
        if(listDichVuDB == null || listDichVuDB.isEmpty()) {
            DichVuBoSung dv1 = new DichVuBoSung(); dv1.setTenDichVu("Bảo hiểm trễ chuyến bay"); dv1.setDonGia(new BigDecimal("80000"));
            DichVuBoSung dv2 = new DichVuBoSung(); dv2.setTenDichVu("Suất ăn nóng trên máy bay"); dv2.setDonGia(new BigDecimal("120000"));
            listDichVuDB.add(dv1); listDichVuDB.add(dv2);
        }

        for (DichVuBoSung dv : listDichVuDB) {
            JCheckBox chk = new JCheckBox(dv.getTenDichVu() + " (+ " + String.format("%,d", dv.getDonGia().longValue()) + " VNĐ)");
            chk.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            chk.setBackground(Color.WHITE);
            chk.setFocusPainted(false);
            chkListDichVu.add(chk);
            chkContainer.add(chk);
            chkContainer.add(Box.createRigidArea(new Dimension(0, 10))); 
        }
        
        pnlDichVu.add(chkContainer, BorderLayout.WEST);
        pnlContent.add(pnlDichVu);

        JScrollPane scrollPane = new JScrollPane(pnlContent);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        JButton btnQuayLai = new JButton("⬅ Quay lại nhập thông tin");
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuayLai.setPreferredSize(new Dimension(220, 40));

        JButton btnTiepTuc = new JButton("Đến trang thanh toán ⮕");
        btnTiepTuc.setBackground(new Color(34, 197, 94));
        btnTiepTuc.setForeground(Color.WHITE);
        btnTiepTuc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTiepTuc.setPreferredSize(new Dimension(220, 40));

        pnlFooter.add(btnQuayLai);
        pnlFooter.add(btnTiepTuc);
        add(pnlFooter, BorderLayout.SOUTH);

        // ================= EVENTS =================
        btnQuayLai.addActionListener(e -> {
            Container parent = this.getParent();
            parent.removeAll();
            parent.setLayout(new BorderLayout());
            parent.add(new gui.user.NhapHanhKhachGUI(session), BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
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
        session.tongTienDichVu = tongTienDichVu;
        
        // CHUYỂN TRANG CHỐNG LỖI TRẮNG MÀN HÌNH
        Container parent = this.getParent();
        if(parent != null) {
            parent.removeAll();
            parent.setLayout(new BorderLayout());
            parent.add(new gui.ThanhToanGUI(session), BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        }
    }

    private TitledBorder createCustomTitledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setTitleColor(new Color(28, 48, 96));
        b.setBorder(BorderFactory.createCompoundBorder(b.getBorder(), BorderFactory.createEmptyBorder(10, 15, 15, 15)));
        return b;
    }

    class BaggageItem {
        String label; int kg; BigDecimal price;
        public BaggageItem(String label, int kg, BigDecimal price) { this.label = label; this.kg = kg; this.price = price; }
        @Override public String toString() { return label; }
    }
}