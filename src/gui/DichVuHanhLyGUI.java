package gui;

import bus.DichVuBoSungBUS;
import model.DichVuBoSung;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DichVuHanhLyGUI extends JPanel {

    private DichVuBoSungBUS dichVuBUS;
    private Color darkBg = new Color(30, 30, 30);
    private Color textColor = Color.WHITE;

    public DichVuHanhLyGUI() {
        dichVuBUS = new DichVuBoSungBUS();
        initComponents();
    }

    private void initComponents() {
        // Dùng BorderLayout để ép toàn bộ nội dung lên phía BẮC (trên cùng), triệt tiêu lỗi kéo giãn
        this.setLayout(new BorderLayout());
        this.setBackground(darkBg);

        // Tạo một Panel chứa toàn bộ nội dung, đặt cách lề 2 bên 50px cho gọn gàng
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(darkBg);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // ================= TIÊU ĐỀ =================
        JLabel lblTitle = new JLabel("DỊCH VỤ & HÀNH LÝ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlContent.add(lblTitle);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // ================= PANEL 1: HÀNH LÝ KÝ GỬI =================
        JPanel pnlHanhLy = new JPanel();
        pnlHanhLy.setLayout(new BoxLayout(pnlHanhLy, BoxLayout.Y_AXIS));
        pnlHanhLy.setBackground(darkBg);
        TitledBorder borderHanhLy = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "1. HÀNH LÝ KÝ GỬI");
        borderHanhLy.setTitleColor(textColor);
        borderHanhLy.setTitleFont(new Font("Arial", Font.BOLD, 12));
        pnlHanhLy.setBorder(borderHanhLy);

        String[] optsHanhLy = {"Không chọn", "20kg (+300.000đ)", "30kg (+500.000đ)"};
        
        // Ép các dòng sát lề trái
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); 
        row1.setBackground(darkBg);
        JLabel lblKhach1 = new JLabel("Hành khách 1: "); lblKhach1.setForeground(textColor);
        JComboBox<String> cbxKhach1 = new JComboBox<>(optsHanhLy); cbxKhach1.setSelectedIndex(1);
        row1.add(lblKhach1); row1.add(cbxKhach1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); 
        row2.setBackground(darkBg);
        JLabel lblKhach2 = new JLabel("Hành khách 2: "); lblKhach2.setForeground(textColor);
        JComboBox<String> cbxKhach2 = new JComboBox<>(optsHanhLy);
        row2.add(lblKhach2); row2.add(cbxKhach2);

        pnlHanhLy.add(row1); 
        pnlHanhLy.add(row2);
        pnlContent.add(pnlHanhLy);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // ================= PANEL 2: SUẤT ĂN & DỊCH VỤ =================
        JPanel pnlDichVu = new JPanel(new BorderLayout());
        pnlDichVu.setBackground(darkBg);
        TitledBorder borderDichVu = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "2. SUẤT ĂN & DỊCH VỤ KHÁC");
        borderDichVu.setTitleColor(textColor);
        borderDichVu.setTitleFont(new Font("Arial", Font.BOLD, 12));
        pnlDichVu.setBorder(borderDichVu);

        // Container ép Checkbox thẳng hàng bên trái
        JPanel chkContainer = new JPanel();
        chkContainer.setLayout(new BoxLayout(chkContainer, BoxLayout.Y_AXIS));
        chkContainer.setBackground(darkBg);
        chkContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        List<DichVuBoSung> listDichVu = dichVuBUS.docDanhSachDichVu();
        List<JCheckBox> chkList = new ArrayList<>();

        if(listDichVu != null) {
            for (DichVuBoSung dv : listDichVu) {
                JCheckBox chk = new JCheckBox(dv.getTenDichVu() + " (+" + dv.getDonGia() + "đ)");
                chk.setBackground(darkBg);
                chk.setForeground(textColor);
                chk.setFocusPainted(false);
                chk.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn lề trái tuyệt đối
                
                chkList.add(chk);
                chkContainer.add(chk);
                chkContainer.add(Box.createRigidArea(new Dimension(0, 5))); // Khoảng cách đều nhau
            }
        }
        pnlDichVu.add(chkContainer, BorderLayout.WEST);
        pnlContent.add(pnlDichVu);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // ================= KHU VỰC NÚT BẤM =================
        JPanel pnlButtons = new JPanel(new BorderLayout());
        pnlButtons.setBackground(darkBg);
        
        JButton btnQuayLai = new JButton("QUAY LẠI");
        btnQuayLai.setBackground(darkBg); btnQuayLai.setForeground(textColor);
        btnQuayLai.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        btnQuayLai.setPreferredSize(new Dimension(120, 35));

        JButton btnTiepTuc = new JButton("TIẾP TỤC");
        btnTiepTuc.setBackground(new Color(40, 70, 40)); btnTiepTuc.setForeground(Color.WHITE);
        btnTiepTuc.setPreferredSize(new Dimension(120, 35));

        pnlButtons.add(btnQuayLai, BorderLayout.WEST);
        pnlButtons.add(btnTiepTuc, BorderLayout.EAST);
        
        pnlContent.add(pnlButtons);

        // Ném nguyên khối nội dung đã gò ép lên trên cùng của Frame
        this.add(pnlContent, BorderLayout.NORTH);

        // Gắn sự kiện để quét dữ liệu khi bấm nút Tiếp Tục
        btnTiepTuc.addActionListener(e -> {
            BigDecimal tongPhuPhi = BigDecimal.ZERO;
            for (int i = 0; i < chkList.size(); i++) {
                if (chkList.get(i).isSelected()) {
                    tongPhuPhi = tongPhuPhi.add(listDichVu.get(i).getDonGia());
                }
            }
            JOptionPane.showMessageDialog(this, "Đã lưu lựa chọn!\nTiền dịch vụ thêm: " + tongPhuPhi + " VNĐ");
        });
    }
}