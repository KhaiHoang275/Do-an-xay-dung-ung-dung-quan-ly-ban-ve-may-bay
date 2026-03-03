package gui.admin;

import bll.ChuyenBayBUS;
import dal.VeBanDAO;
import model.VeBan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class VeBanPanel extends JPanel {
    private JComboBox<String> cboChuyenBay;
    private JComboBox<String> cboLoaiHK;
    private JTextField txtGiaVe;
    private JRadioButton rdoMotChieu, rdoKhuHoi;
    private ButtonGroup grpLoaiVe;

    private JTable table;
    private DefaultTableModel tableModel;

    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();
    private final VeBanDAO veBanDAO = new VeBanDAO();

    public VeBanPanel() {
        setLayout(new BorderLayout(10, 10));
        add(initForm(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);
        loadTable();
    }

    private JPanel initForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Bán vé"));

        JPanel pCB = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pCB.add(new JLabel("Chuyến bay:"));
        cboChuyenBay = new JComboBox<>(
                chuyenBayBUS.getDanhSachMaChuyenBay().toArray(new String[0])
        );
        cboChuyenBay.addActionListener(e -> capNhatGiaVe());
        pCB.add(cboChuyenBay);
        panel.add(pCB);

        JPanel pLoaiVe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pLoaiVe.add(new JLabel("Loại vé:"));

        rdoMotChieu = new JRadioButton("Một chiều", true);
        rdoKhuHoi = new JRadioButton("Khứ hồi");

        grpLoaiVe = new ButtonGroup();
        grpLoaiVe.add(rdoMotChieu);
        grpLoaiVe.add(rdoKhuHoi);

        pLoaiVe.add(rdoMotChieu);
        pLoaiVe.add(rdoKhuHoi);

        panel.add(pLoaiVe);

        JPanel pLoai = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pLoai.add(new JLabel("Loại hành khách:"));
        cboLoaiHK = new JComboBox<>(new String[]{
                "NGUOILON", "TREEM", "EMBE"
        });
        cboLoaiHK.addActionListener(e -> capNhatGiaVe());
        pLoai.add(cboLoaiHK);
        panel.add(pLoai);

        JPanel pGia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pGia.add(new JLabel("Giá vé:"));
        txtGiaVe = new JTextField(15);
        txtGiaVe.setEditable(false);
        txtGiaVe.setFont(new Font("Arial", Font.BOLD, 14));
        pGia.add(txtGiaVe);
        panel.add(pGia);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu");
        JButton btnDoiVe = new JButton("Đổi vé");
        JButton btnReset = new JButton("Reset");

        btnLuu.addActionListener(e -> luuVe());
        btnReset.addActionListener(e -> resetForm());

        pBtn.add(btnLuu);
        pBtn.add(btnDoiVe);
        pBtn.add(btnReset);
        panel.add(pBtn);

        capNhatGiaVe();
        return panel;
    }

    private void capNhatGiaVe() {
        if (cboChuyenBay.getSelectedItem() == null) return;

        String maCB = cboChuyenBay.getSelectedItem().toString();
        String loaiHK = cboLoaiHK.getSelectedItem().toString();

        BigDecimal gia = chuyenBayBUS.tinhGiaVeDon(maCB, loaiHK);
        txtGiaVe.setText(gia.toPlainString());
    }

    private JScrollPane initTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Mã vé", "Chuyến bay", "Loại HK", "Loại vé", "Giá vé", "Trạng thái"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setFocusable(false);
        table.setSurrendersFocusOnKeystroke(true);
        table.putClientProperty("terminateEditOnFocusLost", true);

        return new JScrollPane(table);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<VeBan> list = veBanDAO.selectAll();

        for (VeBan v : list) {
            tableModel.addRow(new Object[]{
                    v.getMaVe(),
                    v.getMaChuyenBay(),
                    v.getLoaiHK(),
                    v.getLoaiVe(),
                    v.getGiaVe(),
                    v.getTrangThaiVe()
            });
        }
    }

    private void luuVe() {
        try {
            VeBan v = new VeBan();
            v.setMaChuyenBay(cboChuyenBay.getSelectedItem().toString());
            v.setLoaiHK(cboLoaiHK.getSelectedItem().toString());
            v.setLoaiVe("Mot chieu");
            v.setGiaVe(new BigDecimal(txtGiaVe.getText()));
            v.setTrangThaiVe("CHUA_SU_DUNG");

            v.setMaHK(null);
            v.setMaHangVe(null);
            v.setMaGhe(null);

            boolean ok = veBanDAO.insert(v);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Bán vé thất bại");
                return;
            }

            loadTable();
            JOptionPane.showMessageDialog(this, "Bán vé thành công");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void resetForm() {
        cboLoaiHK.setSelectedIndex(0);
        capNhatGiaVe();
    }
}