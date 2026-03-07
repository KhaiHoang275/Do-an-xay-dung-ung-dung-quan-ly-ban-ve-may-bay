package gui.admin;

import dal.PhieuDatVeDAO;
import dal.VeBanDAO;
import model.PhieuDatVe;
import model.VeBan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PhieuDatVePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JTable tableVe;
    private DefaultTableModel modelVe;

    private JScrollPane spVe;  
    private JPanel panelVe;

    private JTextField txtSearchPNR;
    private JComboBox<String> cboTrangThai;

    private final PhieuDatVeDAO phieuDAO = new PhieuDatVeDAO();
    private final VeBanDAO veBanDAO = new VeBanDAO();

    public PhieuDatVePanel() {
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("QUẢN LÝ PHIẾU ĐẶT VÉ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(220,38,38));
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));

        add(title, BorderLayout.NORTH);
        add(initSearchBar(), BorderLayout.BEFORE_FIRST_LINE);

       JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(initTable()); 
        centerPanel.add(initTableVe());   

        add(centerPanel, BorderLayout.CENTER);

        loadTable();
        ganSuKienClickPhieu();
    }

    private JPanel initTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("DANH SÁCH PHIẾU ĐẶT VÉ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(25,40,80));
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));

        panel.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"Mã PNR", "Ngày đặt", "Số vé", "Tổng tiền", "Trạng thái"},0
        ){
            public boolean isCellEditable(int r,int c){
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));

        table.getTableHeader().setBackground(new Color(20,40,90));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel initTableVe() {
        modelVe = new DefaultTableModel(
                new String[]{
                "Mã vé",
                "Sân bay đi",
                "Sân bay đến",
                "Ngày giờ đi",
                "Loại HK",
                "Loại vé",
                "Giá vé",
                "Trạng thái"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableVe = new JTable(modelVe);

        tableVe.setRowHeight(28);
        tableVe.setFont(new Font("Segoe UI",Font.PLAIN,13));

        tableVe.getTableHeader().setBackground(new Color(20,40,90));
        tableVe.getTableHeader().setForeground(Color.WHITE);
        tableVe.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));

        spVe = new JScrollPane(tableVe);

        JButton btnThoat = new JButton("Thoát");
        btnThoat.addActionListener(e -> {
            modelVe.setRowCount(0);
            panelVe.setVisible(false); 
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Danh sách Phiếu đặt vé"), BorderLayout.WEST);
        top.add(btnThoat, BorderLayout.EAST);

        panelVe = new JPanel(new BorderLayout());
        panelVe.setBorder(BorderFactory.createTitledBorder(""));
        panelVe.add(top, BorderLayout.NORTH);
        panelVe.add(spVe, BorderLayout.CENTER);

        panelVe.setVisible(false);

        return panelVe;
    }

    private JPanel initSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));

        JLabel lblSearch = new JLabel("Tìm (Mã PNR):");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtSearchPNR = new JTextField(20);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(40,70,130));
        btnSearch.setForeground(Color.WHITE);

        JLabel lblTrangThai = new JLabel("Trạng thái:");

        cboTrangThai = new JComboBox<>(new String[]{
                "Tất cả",
                "Chưa thanh toán",
                "Đã thanh toán",
                "Đã hủy"
        });

        btnSearch.addActionListener(e -> timPhieu());

        panel.add(lblSearch);
        panel.add(txtSearchPNR);
        panel.add(btnSearch);
        panel.add(lblTrangThai);
        panel.add(cboTrangThai);

        return panel;
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<PhieuDatVe> list = phieuDAO.selectAll();
        for (PhieuDatVe p : list) {
            tableModel.addRow(new Object[]{
                    p.getMaPhieuDatVe(),
                    p.getNgayDat(),
                    p.getSoLuongVe(),
                    String.format("%,.0f VND", p.getTongTien()),
                    p.getTrangThaiThanhToan()
            });
        }
    }

    private void ganSuKienClickPhieu() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int row = table.getSelectedRow();
            if (row == -1) {
                panelVe.setVisible(false);
                return;
            }

            String maPhieu = tableModel.getValueAt(row, 0).toString();

            loadVeTheoPhieu(maPhieu);

            panelVe.setVisible(true);
            panelVe.revalidate();
            panelVe.repaint();
        });
    }

    private void loadVeTheoPhieu(String maPhieu) {
        modelVe.setRowCount(0);

        List<VeBan> list = veBanDAO.selectByMaPhieuDatVe(maPhieu);

        for (VeBan v : list) {
            modelVe.addRow(new Object[]{
            v.getMaVe(),
            v.getSanBayDi(),
            v.getSanBayDen(),
            v.getNgayGioDi(),
            v.getLoaiHK(),
            v.getLoaiVe(),
            String.format("%,.0f VND", v.getGiaVe()),
            v.getTrangThaiVe()
        });
        }
    }

    private void timPhieu() {
        String maPNR = txtSearchPNR.getText().trim();
        String trangThai = cboTrangThai.getSelectedItem().toString();

        tableModel.setRowCount(0);

        List<PhieuDatVe> list = phieuDAO.locPhieu(maPNR, trangThai);

        for (PhieuDatVe p : list) {
            tableModel.addRow(new Object[]{
                    p.getMaPhieuDatVe(),
                    p.getNgayDat(),
                    p.getSoLuongVe(),
                    String.format("%,.0f VND", p.getTongTien()),
                    p.getTrangThaiThanhToan()
            });
        }
}
}