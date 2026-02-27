package gui.admin;

import bll.ChuyenBayBUS;
import dal.PhieuDatVeDAO;
import dal.VeBanDAO;
import model.PhieuDatVe;
import model.VeBan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PhieuDatVePanel extends JPanel {

    private JComboBox<String> cboChuyenBay;
    private JLabel lblNguoiLon, lblTreEm, lblEmBe;
    private JTextField txtTongTien;

    private int nguoiLon = 1;
    private int treEm = 0;
    private int emBe = 0;

    private JTable table;
    private DefaultTableModel tableModel;

    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();
    private final PhieuDatVeDAO phieuDAO = new PhieuDatVeDAO();
    private final VeBanDAO veBanDAO = new VeBanDAO();

    public PhieuDatVePanel() {
        setLayout(new BorderLayout(10, 10));
        add(initForm(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);
        loadTable();
        capNhatTongTien();
    }

    private JPanel initForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Tạo phiếu đặt vé"));

        JPanel pCB = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pCB.add(new JLabel("Chuyến bay:"));
        cboChuyenBay = new JComboBox<>(
                chuyenBayBUS.getDanhSachMaChuyenBay().toArray(new String[0])
        );
        cboChuyenBay.addActionListener(e -> capNhatTongTien());
        pCB.add(cboChuyenBay);
        panel.add(pCB);

        lblNguoiLon = new JLabel("1");
        lblTreEm = new JLabel("0");
        lblEmBe = new JLabel("0");

        panel.add(dongSoLuong("Người lớn", lblNguoiLon, true));
        panel.add(dongSoLuong("Trẻ em", lblTreEm, false));
        panel.add(dongSoLuong("Em bé", lblEmBe, false));

        JPanel pTien = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTien.add(new JLabel("Tổng tiền:"));
        txtTongTien = new JTextField(18);
        txtTongTien.setEditable(false);
        txtTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        pTien.add(txtTongTien);
        panel.add(pTien);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu");
        JButton btnReset = new JButton("Reset");

        btnLuu.addActionListener(e -> luuPhieu());
        btnReset.addActionListener(e -> resetForm());

        pBtn.add(btnLuu);
        pBtn.add(btnReset);
        panel.add(pBtn);

        return panel;
    }

    private JPanel dongSoLuong(String ten, JLabel lbl, boolean min1) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(ten + ":"));

        JButton minus = new JButton("-");
        JButton plus = new JButton("+");

        minus.addActionListener(e -> {
            int v = Integer.parseInt(lbl.getText());
            if ((min1 && v <= 1) || (!min1 && v <= 0)) return;
            lbl.setText(String.valueOf(v - 1));
            dongBo();
        });

        plus.addActionListener(e -> {
            int v = Integer.parseInt(lbl.getText());
            lbl.setText(String.valueOf(v + 1));
            dongBo();
        });

        p.add(minus);
        p.add(lbl);
        p.add(plus);
        return p;
    }

    private void dongBo() {
        nguoiLon = Integer.parseInt(lblNguoiLon.getText());
        treEm = Integer.parseInt(lblTreEm.getText());
        emBe = Integer.parseInt(lblEmBe.getText());
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        if (cboChuyenBay.getSelectedItem() == null) return;
        BigDecimal tong = chuyenBayBUS.tinhGiaVe(
                cboChuyenBay.getSelectedItem().toString(),
                nguoiLon, treEm, emBe
        );
        txtTongTien.setText(tong.toPlainString());
    }

    private JScrollPane initTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Mã PNR", "Ngày đặt", "Số vé", "Tổng tiền", "Trạng thái"}, 0
        );
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<PhieuDatVe> list = phieuDAO.selectAll();
        for (PhieuDatVe p : list) {
            tableModel.addRow(new Object[]{
                    p.getMaPhieuDatVe(),
                    p.getNgayDat(),
                    p.getSoLuongVe(),
                    p.getTongTien(),
                    p.getTrangThaiThanhToan()
            });
        }
    }

    private void luuPhieu() {
        try {
            String maCB = cboChuyenBay.getSelectedItem().toString();

            PhieuDatVe p = new PhieuDatVe();
            p.setNgayDat(LocalDate.now());
            p.setSoLuongVe(nguoiLon + treEm + emBe);
            p.setTongTien(new BigDecimal(txtTongTien.getText()));
            p.setTrangThaiThanhToan("CHUA_THANH_TOAN");

            boolean ok = phieuDAO.insert(p);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Tạo phiếu thất bại");
                return;
            }

            String maPhieu = p.getMaPhieuDatVe();
            if (maPhieu == null) {
                JOptionPane.showMessageDialog(this, "Không lấy được mã phiếu");
                return;
            }

            taoVe(maPhieu, maCB, "NGUOILON", nguoiLon);
            taoVe(maPhieu, maCB, "TREEM", treEm);
            taoVe(maPhieu, maCB, "EMBE", emBe);

            loadTable(); 
            JOptionPane.showMessageDialog(this, "Tạo phiếu & vé thành công");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void taoVe(String maPhieu, String maCB, String loaiHK, int soLuong) {
        for (int i = 0; i < soLuong; i++) {
            VeBan v = new VeBan();

            v.setMaPhieuDatVe(maPhieu);  
            v.setMaChuyenBay(maCB);
            v.setLoaiHK(loaiHK);
            v.setLoaiVe("Mot chieu");
            v.setGiaVe(chuyenBayBUS.tinhGiaVeDon(maCB, loaiHK));
            v.setTrangThaiVe("CHUA_SU_DUNG");

            v.setMaHK(null);
            v.setMaHangVe(null);
            v.setMaGhe(null);

            veBanDAO.insert(v);
        }
    }

    private void resetForm() {
        lblNguoiLon.setText("1");
        lblTreEm.setText("0");
        lblEmBe.setText("0");
        dongBo();
    }
}