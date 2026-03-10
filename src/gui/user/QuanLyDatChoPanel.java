package gui.user;

import dal.PhieuDatVeDAO;
import model.NguoiDung;
import model.PhieuDatVe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class QuanLyDatChoPanel extends JPanel {

    private NguoiDung currentUser;
    private JTable tblDatCho;
    private DefaultTableModel model;

    public QuanLyDatChoPanel(NguoiDung user) {
        this.currentUser = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT CHỖ CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(220, 38, 38));
        add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Mã Đặt Chỗ (PNR)", "Ngày Đặt", "Số Lượng Vé", "Trạng Thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDatCho = new JTable(model);
        tblDatCho.setRowHeight(35);
        tblDatCho.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblDatCho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tblDatCho);
        add(scroll, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlFooter.setBackground(Color.WHITE);
        
        JButton btnChiTiet = new JButton("Xem hành trình");
        JButton btnDoiVe = new JButton("Yêu cầu Đổi Vé");
        btnDoiVe.setBackground(new Color(220, 38, 38));
        btnDoiVe.setForeground(Color.WHITE);
        btnDoiVe.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlFooter.add(btnChiTiet);
        pnlFooter.add(btnDoiVe);
        add(pnlFooter, BorderLayout.SOUTH);

        btnDoiVe.addActionListener(e -> {
            int row = tblDatCho.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 mã đặt chỗ để đổi vé!");
                return;
            }
            String maPNR = tblDatCho.getValueAt(row, 0).toString();
            JOptionPane.showMessageDialog(this, "Chức năng đổi vé cho PNR: " + maPNR);
        });
    }

    private void loadData() {
        if(currentUser == null) return;
        try {
            PhieuDatVeDAO pdvDAO = new PhieuDatVeDAO();
            ArrayList<PhieuDatVe> allPhieu = pdvDAO.selectAll();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (PhieuDatVe pdv : allPhieu) {
                if (currentUser.getMaNguoiDung().equals(pdv.getMaNguoiDung())) {
                    model.addRow(new Object[]{
                        pdv.getMaPhieuDatVe(),
                        pdv.getNgayDat() != null ? pdv.getNgayDat().format(dtf) : "",
                        pdv.getSoLuongVe(),
                        pdv.getTrangThaiThanhToan()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}