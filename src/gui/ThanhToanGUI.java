package gui;

import bus.HoaDonBUS;
import model.HoaDon;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ThanhToanGUI extends JPanel {

    private HoaDonBUS hoaDonBUS;
    private Color darkBg = new Color(30, 30, 30);
    private Color textColor = Color.WHITE;
    private Color goldText = new Color(255, 204, 0); 
    private Color confirmBtnColor = new Color(100, 50, 50);

    private JRadioButton radTienMat, radThe, radVi, radQR;
    private JTextField txtSoThe, txtCW, txtTenChuThe;

    // CHÍNH LÀ HÀM NÀY ĐÂY - NÓ SẼ GIẢI QUYẾT LỖI BÊN MAIN.JAVA
    public ThanhToanGUI() {
        hoaDonBUS = new HoaDonBUS();
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(darkBg);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("THANH TOÁN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblTitle);
        this.add(Box.createRigidArea(new Dimension(0, 15)));

        // ================= KHUNG TỔNG THANH TOÁN =================
        JPanel pnlTongTien = new JPanel(new BorderLayout());
        pnlTongTien.setBackground(darkBg);
        pnlTongTien.setBorder(BorderFactory.createLineBorder(goldText, 1));
        pnlTongTien.setMaximumSize(new Dimension(800, 50));

        JLabel lblTongTien = new JLabel("TỔNG THANH TOÁN: 5.480.000 VNĐ", SwingConstants.CENTER);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(goldText);
        lblTongTien.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlTongTien.add(lblTongTien, BorderLayout.CENTER);

        this.add(pnlTongTien);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        // ================= PHƯƠNG THỨC THANH TOÁN =================
        JPanel pnlPhuongThuc = new JPanel();
        pnlPhuongThuc.setLayout(new BoxLayout(pnlPhuongThuc, BoxLayout.Y_AXIS));
        pnlPhuongThuc.setBackground(darkBg);
        TitledBorder borderPT = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "CHỌN PHƯƠNG THỨC THANH TOÁN");
        borderPT.setTitleColor(textColor);
        pnlPhuongThuc.setBorder(borderPT);

        radTienMat = new JRadioButton("Tiền mặt (Thanh toán tại quầy)");
        radThe = new JRadioButton("Thẻ ATM / Visa / Master Card");
        radVi = new JRadioButton("Ví điện tử MoMo / ZaloPay");
        radQR = new JRadioButton("Quét mã VNPAY-QR");

        ButtonGroup bg = new ButtonGroup();
        JRadioButton[] radios = {radTienMat, radThe, radVi, radQR};
        for (JRadioButton rad : radios) {
            rad.setBackground(darkBg); rad.setForeground(textColor); rad.setFocusPainted(false);
            bg.add(rad); pnlPhuongThuc.add(rad);
        }
        radThe.setSelected(true); 

        pnlPhuongThuc.add(Box.createRigidArea(new Dimension(0, 15)));

        // ================= KHUNG NHẬP THẺ (CHỮ TRẮNG) =================
        JPanel pnlTheInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTheInfo.setBackground(darkBg);
        
        JLabel lblSoThe = new JLabel("Số thẻ: "); 
        lblSoThe.setForeground(textColor); 
        pnlTheInfo.add(lblSoThe);
        txtSoThe = new JTextField(15); 
        pnlTheInfo.add(txtSoThe);
        
        JLabel lblCW = new JLabel(" CW: "); 
        lblCW.setForeground(textColor); 
        pnlTheInfo.add(lblCW);
        txtCW = new JTextField(5); 
        pnlTheInfo.add(txtCW);
        
        JLabel lblTenChuThe = new JLabel(" Tên chủ thẻ: "); 
        lblTenChuThe.setForeground(textColor); 
        pnlTheInfo.add(lblTenChuThe);
        txtTenChuThe = new JTextField(15); 
        pnlTheInfo.add(txtTenChuThe);

        pnlPhuongThuc.add(pnlTheInfo);
        this.add(pnlPhuongThuc);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        // ================= NÚT BẤM =================
        JPanel pnlButtons = new JPanel(new BorderLayout());
        pnlButtons.setBackground(darkBg);
        pnlButtons.setMaximumSize(new Dimension(800, 40));
        
        JButton btnQuayLai = new JButton("QUAY LẠI");
        btnQuayLai.setBackground(darkBg); btnQuayLai.setForeground(textColor);
        btnQuayLai.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        btnQuayLai.setPreferredSize(new Dimension(150, 35));

        JButton btnXacNhan = new JButton("XÁC NHẬN & THANH TOÁN");
        btnXacNhan.setBackground(confirmBtnColor); btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setPreferredSize(new Dimension(200, 35));

        pnlButtons.add(btnQuayLai, BorderLayout.WEST);
        pnlButtons.add(btnXacNhan, BorderLayout.EAST);
        this.add(pnlButtons);

        // GẮN SỰ KIỆN XÁC NHẬN
        btnXacNhan.addActionListener(e -> {
            String phuongThuc = "Thẻ tín dụng";
            if(radTienMat.isSelected()) phuongThuc = "Tiền mặt";
            else if(radVi.isSelected()) phuongThuc = "Ví điện tử";
            else if(radQR.isSelected()) phuongThuc = "Quét mã QR";

            HoaDon hd = new HoaDon();
            String maHDAuto = "HD" + System.currentTimeMillis() % 10000;
            hd.setMaHoaDon(maHDAuto);
            hd.setMaPhieuDatVe("PDV_1"); 
            hd.setMaNV("NV01");
            hd.setNgayLap(LocalDateTime.now());
            hd.setTongTien(new BigDecimal("5480000"));
            hd.setPhuongThuc(phuongThuc);
            hd.setDonViTienTe("VNĐ");
            hd.setThue(BigDecimal.ZERO);

            String ketQua = hoaDonBUS.themHoaDon(hd);
            if(ketQua.equals("Thành công")) {
                JOptionPane.showMessageDialog(this, "THANH TOÁN THÀNH CÔNG!\nĐã lưu hóa đơn: " + maHDAuto);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ketQua, "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}