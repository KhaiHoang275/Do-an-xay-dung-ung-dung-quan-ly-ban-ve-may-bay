package gui.user;

import model.DatVeSession;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List; 
import gui.*;

public class NhapHanhKhachGUI extends JPanel {

    private DatVeSession session;
    private JPanel pnlListKhach;
    private List<HanhKhachForm> listForms = new ArrayList<>(); 

    public NhapHanhKhachGUI(DatVeSession session) {
        this.session = session;
        initComponents();
        buildDynamicForms();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // HEADER
        JLabel lblTitle = new JLabel("NHẬP THÔNG TIN HÀNH KHÁCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(28, 48, 96));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // CONTENT (Cuộn dọc)
        pnlListKhach = new JPanel();
        pnlListKhach.setLayout(new BoxLayout(pnlListKhach, BoxLayout.Y_AXIS));
        pnlListKhach.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnlListKhach);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // FOOTER (Nút bấm)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlFooter.setBackground(Color.WHITE);

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

        pnlFooter.add(btnQuayLai);
        pnlFooter.add(btnTiepTuc);
        add(pnlFooter, BorderLayout.SOUTH);

        // SỰ KIỆN NÚT BẤM
        btnQuayLai.addActionListener(e -> {
            // Quay lại form chọn vé (PanelUserVeBan)
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new PanelUserVeBan(session.maNguoiDung, session.maChuyenBay));
            frame.revalidate();
            frame.repaint();
        });

        btnTiepTuc.addActionListener(e -> processTiepTuc());
    }

    private void buildDynamicForms() {
        pnlListKhach.removeAll();
        listForms.clear();

        // Tạo form cho Người lớn
        for (int i = 1; i <= session.soNguoiLon; i++) {
            HanhKhachForm form = new HanhKhachForm("Người lớn " + i, "Người lớn");
            listForms.add(form);
            pnlListKhach.add(form);
            pnlListKhach.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        // Tạo form cho Trẻ em
        for (int i = 1; i <= session.soTreEm; i++) {
            HanhKhachForm form = new HanhKhachForm("Trẻ em " + i, "Trẻ em");
            listForms.add(form);
            pnlListKhach.add(form);
            pnlListKhach.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        // Tạo form cho Em bé
        for (int i = 1; i <= session.soEmBe; i++) {
            HanhKhachForm form = new HanhKhachForm("Em bé " + i, "Em bé");
            listForms.add(form);
            pnlListKhach.add(form);
            pnlListKhach.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        pnlListKhach.revalidate();
        pnlListKhach.repaint();
    }

    private void processTiepTuc() {
        session.danhSachHanhKhach.clear();
        
        try {
            for (HanhKhachForm form : listForms) {
                ThongTinHanhKhach hk = form.getHanhKhachData();
                session.danhSachHanhKhach.add(hk);
            }
            
            // XONG BƯỚC 3 -> CHUYỂN SANG BƯỚC 4 (HÀNH LÝ)
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
           
            frame.setContentPane(new gui.DichVuHanhLyGUI(session)); 
            frame.revalidate();
            frame.repaint();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Inner class: Khung nhập liệu cho 1 hành khách
    class HanhKhachForm extends JPanel {
        private JTextField txtHoTen, txtCCCD, txtHoChieu, txtNgaySinh;
        private JComboBox<String> cboGioiTinh;
        private String loaiHK;

        public HanhKhachForm(String title, String loaiHK) {
            this.loaiHK = loaiHK;
            setLayout(new GridLayout(3, 4, 15, 15));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(28, 48, 96)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            txtHoTen = new JTextField();
            txtCCCD = new JTextField();
            txtHoChieu = new JTextField();
            txtNgaySinh = new JTextField("DD/MM/YYYY");
            cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});

            add(new JLabel("Họ và Tên (*):")); add(txtHoTen);
            add(new JLabel("Giới tính:")); add(cboGioiTinh);
            
            add(new JLabel("CCCD (*NL):")); add(txtCCCD);
            add(new JLabel("Hộ chiếu:")); add(txtHoChieu);
            
            add(new JLabel("Ngày sinh (*TE/EB):")); add(txtNgaySinh);
        }

        public ThongTinHanhKhach getHanhKhachData() throws Exception {
            String ten = txtHoTen.getText().trim();
            if (ten.isEmpty()) throw new Exception("Vui lòng nhập Họ Tên cho tất cả hành khách!");

            ThongTinHanhKhach hk = new ThongTinHanhKhach();
            hk.setMaNguoiDung(session.maNguoiDung);
            hk.setHoTen(ten);
            hk.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
            hk.setCccd(txtCCCD.getText().trim());
            hk.setHoChieu(txtHoChieu.getText().trim());
            hk.setLoaiHanhKhach(this.loaiHK);

            String ns = txtNgaySinh.getText().trim();
            if (!ns.isEmpty() && !ns.equals("DD/MM/YYYY")) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    hk.setNgaySinh(LocalDate.parse(ns, formatter));
                } catch (Exception e) {
                    throw new Exception("Ngày sinh của hành khách " + ten + " sai định dạng DD/MM/YYYY!");
                }
            }
            return hk;
        }
    }
}