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
        JLabel lblTitle = new JLabel("CHỌN HÀNH LÝ & DỊCH VỤ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(28, 48, 96));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ================= CONTENT (Có thanh cuộn) =================
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- PANEL 1: HÀNH LÝ KÝ GỬI (Tự động sinh theo số hành khách) ---
        JPanel pnlHanhLy = new JPanel();
        pnlHanhLy.setLayout(new BoxLayout(pnlHanhLy, BoxLayout.Y_AXIS));
        pnlHanhLy.setBackground(Color.WHITE);
        pnlHanhLy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "1. HÀNH LÝ KÝ GỬI", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(28, 48, 96)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Tạo mảng lựa chọn Hành lý
        BaggageItem[] optsHanhLy = {
                new BaggageItem("Không mang thêm hành lý ký gửi (+0 VNĐ)", 0, BigDecimal.ZERO),
                new BaggageItem("Gói 20kg (+ 300.000 VNĐ)", 20, new BigDecimal("300000")),
                new BaggageItem("Gói 30kg (+ 500.000 VNĐ)", 30, new BigDecimal("500000")),
                new BaggageItem("Gói 40kg (+ 700.000 VNĐ)", 40, new BigDecimal("700000"))
        };

        // Sinh dòng chọn hành lý cho từng khách
        if (session.danhSachHanhKhach != null) {
            for (int i = 0; i < session.danhSachHanhKhach.size(); i++) {
                ThongTinHanhKhach hk = session.danhSachHanhKhach.get(i);
                
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); 
                row.setBackground(Color.WHITE);
                
                JLabel lblKhach = new JLabel("Hành khách " + (i + 1) + " (" + hk.getHoTen() + "): ");
                lblKhach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblKhach.setPreferredSize(new Dimension(250, 30));
                
                JComboBox<BaggageItem> cbo = new JComboBox<>(optsHanhLy);
                cbo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cbo.setPreferredSize(new Dimension(300, 30));
                
                listCboHanhLy.add(cbo);
                row.add(lblKhach); 
                row.add(cbo);
                pnlHanhLy.add(row);
            }
        }
        
        pnlContent.add(pnlHanhLy);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- PANEL 2: SUẤT ĂN & DỊCH VỤ BỔ SUNG ---
        JPanel pnlDichVu = new JPanel(new BorderLayout());
        pnlDichVu.setBackground(Color.WHITE);
        pnlDichVu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "2. DỊCH VỤ BỔ SUNG (Bảo hiểm, Suất ăn...)", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(28, 48, 96)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel chkContainer = new JPanel();
        chkContainer.setLayout(new BoxLayout(chkContainer, BoxLayout.Y_AXIS));
        chkContainer.setBackground(Color.WHITE);

        listDichVuDB = dichVuBUS.docDanhSachDichVu();
        if(listDichVuDB != null && !listDichVuDB.isEmpty()) {
            for (DichVuBoSung dv : listDichVuDB) {
                JCheckBox chk = new JCheckBox(dv.getTenDichVu() + " (+ " + String.format("%,d", dv.getDonGia().longValue()) + " VNĐ)");
                chk.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                chk.setBackground(Color.WHITE);
                chk.setFocusPainted(false);
                
                chkListDichVu.add(chk);
                chkContainer.add(chk);
                chkContainer.add(Box.createRigidArea(new Dimension(0, 10))); 
            }
        } else {
            chkContainer.add(new JLabel("Hiện không có dịch vụ bổ sung nào."));
        }
        
        pnlDichVu.add(chkContainer, BorderLayout.WEST);
        pnlContent.add(pnlDichVu);

        // Đưa Content vào ScrollPane chống tràn màn hình
        JScrollPane scrollPane = new JScrollPane(pnlContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ================= FOOTER (NÚT BẤM) =================
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlButtons.setBackground(Color.WHITE);
        
        JButton btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setPreferredSize(new Dimension(120, 40));
        btnQuayLai.setBackground(new Color(108, 117, 125));
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnTiepTuc = new JButton("Tiếp tục");
        btnTiepTuc.setPreferredSize(new Dimension(120, 40));
        btnTiepTuc.setBackground(new Color(34, 197, 94));
        btnTiepTuc.setForeground(Color.WHITE);
        btnTiepTuc.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlButtons.add(btnQuayLai);
        pnlButtons.add(btnTiepTuc);
        add(pnlButtons, BorderLayout.SOUTH);

        // ================= SỰ KIỆN =================
        btnQuayLai.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new NhapHanhKhachGUI(session));
            frame.revalidate();
            frame.repaint();
        });

        btnTiepTuc.addActionListener(e -> processTiepTuc());
    }

    private void processTiepTuc() {
        BigDecimal tongTienDichVu = BigDecimal.ZERO;
        session.danhSachHanhLy.clear(); // Reset phòng khi user back lại r ấn tiếp tục
        
        // 1. Cộng tiền và lưu thông tin Hành Lý Ký Gửi
        for (int i = 0; i < listCboHanhLy.size(); i++) {
            BaggageItem selected = (BaggageItem) listCboHanhLy.get(i).getSelectedItem();
            if (selected.kg > 0) {
                tongTienDichVu = tongTienDichVu.add(selected.price);
                
                HanhLy hl = new HanhLy();
                hl.setSoKg(new BigDecimal(selected.kg));
                hl.setGiaTien(selected.price);
                hl.setGhiChu("Của hành khách: " + session.danhSachHanhKhach.get(i).getHoTen());
                session.danhSachHanhLy.add(hl);
            }
        }

        // 2. Cộng tiền Dịch Vụ Bổ Sung
        List<String> tenDichVuDaChon = new ArrayList<>();
        for (int i = 0; i < chkListDichVu.size(); i++) {
            if (chkListDichVu.get(i).isSelected()) {
                tongTienDichVu = tongTienDichVu.add(listDichVuDB.get(i).getDonGia());
                tenDichVuDaChon.add(listDichVuDB.get(i).getTenDichVu());
            }
        }

        // 3. Ghi tổng tiền dịch vụ vào Session
        session.tongTienDichVu = tongTienDichVu;
        
        // Chuyển sang form Thanh Toán cuối cùng
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // NẾU BẠN CHƯA KÉO THANHTOANGUI VÀO PACKAGE GUI.USER, HÃY DÙNG CHỮ "gui.ThanhToanGUI"
        JOptionPane.showMessageDialog(this, "Đã lưu Dịch vụ & Hành lý!\nTổng tiền DV thêm: " + String.format("%,d", tongTienDichVu.longValue()) + " VNĐ\nChuyển sang Thanh Toán...");
        
        // frame.setContentPane(new gui.ThanhToanGUI(session)); // <--- Bỏ comment dòng này khi ráp xong Thanh Toán
        // frame.revalidate();
        // frame.repaint();
    }

    // Inner class hỗ trợ hiển thị Combobox Hành Lý
    class BaggageItem {
        String label;
        int kg;
        BigDecimal price;

        public BaggageItem(String label, int kg, BigDecimal price) {
            this.label = label;
            this.kg = kg;
            this.price = price;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}