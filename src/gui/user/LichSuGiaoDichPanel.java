package gui.user;

import dal.HoaDonDAO;
import dal.PhieuDatVeDAO;
import model.HoaDon;
import model.NguoiDung;
import model.PhieuDatVe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class LichSuGiaoDichPanel extends JPanel {

    private NguoiDung currentUser;
    private JTable tblGiaoDich;
    private DefaultTableModel model;

    public LichSuGiaoDichPanel(NguoiDung user) {
        this.currentUser = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH & HÓA ĐƠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(28, 48, 96));
        add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Mã Hóa Đơn", "Mã Đặt Chỗ (PNR)", "Ngày Thanh Toán", "Tổng Tiền", "Phương Thức"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGiaoDich = new JTable(model);
        tblGiaoDich.setRowHeight(35);
        tblGiaoDich.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblGiaoDich.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblGiaoDich.setSelectionBackground(new Color(28, 48, 96));
        tblGiaoDich.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tblGiaoDich);
        add(scroll, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(Color.WHITE);
        JButton btnChiTiet = new JButton("Xem chi tiết Hóa Đơn");
        btnChiTiet.setBackground(new Color(34, 197, 94));
        btnChiTiet.setForeground(Color.WHITE);
        btnChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlFooter.add(btnChiTiet);
        add(pnlFooter, BorderLayout.SOUTH);

        btnChiTiet.addActionListener(e -> {
            int row = tblGiaoDich.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 giao dịch để xem!");
                return;
            }
            String maPNR = tblGiaoDich.getValueAt(row, 1).toString();
            JOptionPane.showMessageDialog(this, "Đang mở chi tiết cho mã đặt chỗ: " + maPNR + "\n(Chứa thông tin chuyến bay, dịch vụ bổ sung...)");
        });
    }

    private void loadData() {
        if(currentUser == null) return;
        
        try {
            PhieuDatVeDAO pdvDAO = new PhieuDatVeDAO();
            HoaDonDAO hdDAO = new HoaDonDAO();
            
            ArrayList<PhieuDatVe> allPhieu = pdvDAO.selectAll();
            ArrayList<HoaDon> allHD = hdDAO.selectAll();
            
            NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (PhieuDatVe pdv : allPhieu) {
                if (currentUser.getMaNguoiDung().equals(pdv.getMaNguoiDung())) {
                    for (HoaDon hd : allHD) {
                        if (pdv.getMaPhieuDatVe().equals(hd.getMaPhieuDatVe())) {
                            model.addRow(new Object[]{
                                hd.getMaHoaDon(),
                                pdv.getMaPhieuDatVe(),
                                hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "",
                                vn.format(hd.getTongTien()) + " VNĐ",
                                hd.getPhuongThuc()
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}