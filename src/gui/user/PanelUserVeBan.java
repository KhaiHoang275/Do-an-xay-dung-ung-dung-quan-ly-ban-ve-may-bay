package gui.user;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.List;

import bll.ChuyenBayBUS;
import bll.MayBayBUS;
import dal.HangVeDAO;
import model.*;

import java.awt.*;

public class PanelUserVeBan extends JPanel {
    private DatVeSession session;
    private List<GheMayBay> danhSachGheDaChon = new ArrayList<>();
    private JTextField txtGhe;
    private JComboBox<HangVe> cboHangVe;

    private ChuyenBayBUS chuyenBayBUS;
    private MayBayBUS mayBayBUS;

    // CONSTRUCTOR CHỈ NHẬN SESSION
    public PanelUserVeBan(DatVeSession session) {
        this.session = session;
        this.chuyenBayBUS = new ChuyenBayBUS();
        this.mayBayBUS = new MayBayBUS();
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        add(initHeader(), BorderLayout.NORTH);
        add(initFormPanel(), BorderLayout.CENTER); // Đưa thẳng vào form, bỏ các Tab thừa
    }

    private JPanel initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("CHỌN CHỖ NGỒI & HẠNG VÉ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(220, 38, 38));

        JButton btnQuayLai = new JButton(" Quay lại tìm kiếm");
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
            new MainFrame().setVisible(true); // Reset về trang chủ
        });

        pnlHeader.add(title, BorderLayout.WEST);
        pnlHeader.add(btnQuayLai, BorderLayout.EAST);
        return pnlHeader;
    }

    private JPanel initFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            " BƯỚC 1: XÁC NHẬN THÔNG TIN VÉ", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 16), new Color(28, 48, 96))
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. CHUYẾN BAY (Khóa)
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Chuyến bay:"), gbc);
        JTextField txtMaCB = new JTextField(session.maChuyenBay);
        txtMaCB.setEditable(false);
        txtMaCB.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; panel.add(txtMaCB, gbc);

        // 2. LOẠI VÉ (Đồng bộ từ MainFrame - Khóa)
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Loại vé:"), gbc);
        JTextField txtLoaiVe = new JTextField(session.loaiVe != null ? session.loaiVe : "Một chiều");
        txtLoaiVe.setEditable(false);
        txtLoaiVe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtLoaiVe.setForeground(new Color(40, 120, 40));
        gbc.gridx = 1; panel.add(txtLoaiVe, gbc);

        // 3. SỐ LƯỢNG HÀNH KHÁCH (Đồng bộ từ MainFrame - Khóa)
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Hành khách:"), gbc);
        JTextField txtSL = new JTextField(String.format("%d Người lớn, %d Trẻ em, %d Em bé", session.soNguoiLon, session.soTreEm, session.soEmBe));
        txtSL.setEditable(false);
        txtSL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; panel.add(txtSL, gbc);

        // 4. HẠNG VÉ (Mặc định Phổ thông từ CSDL)
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Hạng vé:"), gbc);
        cboHangVe = new JComboBox<>();
        cboHangVe.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        HangVeDAO hvDAO = new HangVeDAO();
        ArrayList<HangVe> ds = hvDAO.selectAll();
        HangVe hangMacDinh = null;
        for(HangVe hv : ds) {
            cboHangVe.addItem(hv);
            if(hv.getTenHang().toLowerCase().contains("phổ thông")) {
                hangMacDinh = hv;
            }
        }
        if(hangMacDinh != null) cboHangVe.setSelectedItem(hangMacDinh);
        gbc.gridx = 1; panel.add(cboHangVe, gbc);

        // 5. CHỌN GHẾ
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Ghế đã chọn:"), gbc);
        txtGhe = new JTextField(20);
        txtGhe.setEditable(false);
        txtGhe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; panel.add(txtGhe, gbc);
        
        JButton btnChonGhe = new JButton("Mở sơ đồ ghế");
        btnChonGhe.setBackground(new Color(59, 130, 246));
        btnChonGhe.setForeground(Color.WHITE);
        btnChonGhe.setFocusPainted(false);
        gbc.gridx = 2; panel.add(btnChonGhe, gbc);

        // 6. NÚT TIẾP TỤC
        JButton btnTiepTuc = new JButton("Tiếp tục nhập thông tin");
        btnTiepTuc.setBackground(new Color(34, 197, 94));
        btnTiepTuc.setForeground(Color.WHITE);
        btnTiepTuc.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnTiepTuc.setPreferredSize(new Dimension(300, 45));
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(btnTiepTuc, gbc);

        // ================= LOGIC SỰ KIỆN =================

        btnChonGhe.addActionListener(e -> {
            ChuyenBay cb = chuyenBayBUS.getChuyenBayById(session.maChuyenBay);
            if(cb == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chuyến bay!");
                return;
            }
            String maMB = cb.getMaMayBay() ; // Lấy từ DB theo chuyến bay thực tế

            MayBay mb = mayBayBUS.getMayBayById(maMB);
            String tenMB = (mb != null) ? mb.getSoHieu() : "Máy bay";

            int tongKhach = session.soNguoiLon + session.soTreEm + session.soEmBe;
            gui.admin.SoDoGhePanel soDo = new gui.admin.SoDoGhePanel(maMB, "Máy bay", tongKhach);
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sơ đồ ghế", true);
            dialog.setSize(900, 650);
            dialog.setLocationRelativeTo(this);
            
            soDo.setListener(new gui.admin.SoDoGhePanel.SoDoGheListener() {
                @Override public void onBack() { dialog.dispose(); }
                @Override public void onSeatsConfirmed(List<GheMayBay> selected) {
                    danhSachGheDaChon = selected;
                    List<String> codes = new ArrayList<>();
                    for(GheMayBay g : selected) codes.add(g.getSoGhe());
                    txtGhe.setText(String.join(", ", codes));
                    dialog.dispose();
                }
            });
            dialog.add(soDo);
            dialog.setVisible(true);
        });

        btnTiepTuc.addActionListener(e -> {
            int tongKhach = session.soNguoiLon + session.soTreEm + session.soEmBe;
            if(danhSachGheDaChon.size() < tongKhach) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ " + tongKhach + " ghế cho hành khách!");
                return;
            }
     
            session.maHangVe = ((HangVe)cboHangVe.getSelectedItem()).getMaHangVe();
            session.danhSachGhe = danhSachGheDaChon;

            this.removeAll();
            this.setLayout(new BorderLayout());
            this.add(new gui.user.NhapHanhKhachGUI(session), BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
        });

        return panel;
    }
}