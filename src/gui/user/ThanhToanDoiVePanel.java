package gui.user;

import bll.GiaoDichVeBUS;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ThanhToanDoiVePanel extends JPanel {

    private final Color PRIMARY = new Color(220, 38, 38);
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color BTN_CONFIRM = new Color(34, 197, 94);
    private final Color BTN_CANCEL = Color.GRAY;

    private final NumberFormat moneyFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    public ThanhToanDoiVePanel(
            String maVeCu,
            String maVeMoi,
            String lyDo,
            BigDecimal phiGD,
            BigDecimal phiCL,
            BigDecimal tong,
            GiaoDichVeBUS bus
    ) {

        setLayout(new BorderLayout(20,20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("XÁC NHẬN THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(PRIMARY);
        add(lblTitle, BorderLayout.NORTH);

        // ===== CARD =====
        JPanel card = new JPanel(new GridLayout(8,2,20,20));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(30,30,30,30)
        ));

        // Hàng 1: Mã vé
        card.add(createLabel("Mã vé cũ:"));
        card.add(createValue(maVeCu));

        card.add(createLabel("Mã vé mới:"));
        card.add(createValue(maVeMoi));

        // Hàng 2: Giá vé cũ
        BigDecimal giaVeCu = bus.tinhGiaVe(maVeCu);
        card.add(createLabel("Giá vé cũ:"));
        card.add(createValue(moneyFormat.format(giaVeCu) + " VNĐ"));

        // Hàng 3: Giá vé mới
        BigDecimal giaVeMoi = bus.tinhGiaVe(maVeMoi);
        card.add(createLabel("Giá vé mới:"));
        card.add(createValue(moneyFormat.format(giaVeMoi) + " VNĐ"));

        // Hàng 4: Phí đổi vé
        card.add(createLabel("Phí đổi vé:"));
        card.add(createValue(moneyFormat.format(phiGD) + " VNĐ"));

        // Hàng 5: Phí chênh lệch
        card.add(createLabel("Phí chênh lệch:"));
        card.add(createValue(moneyFormat.format(phiCL) + " VNĐ"));

        // Hàng 6: Tổng phụ thu
        card.add(createLabel("Tổng phụ thu:"));
        JLabel lblTong = createValue(moneyFormat.format(tong) + " VNĐ");
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTong.setForeground(Color.RED);
        card.add(lblTong);

        // Hàng 7: Lý do
        card.add(createLabel("Lý do:"));
        JLabel lblLyDo = createValue("<html>"+lyDo+"</html>");
        card.add(lblLyDo);

        add(card, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,10));
        buttonPanel.setOpaque(false);

        JButton btnConfirm = createButton("Xác nhận cuối cùng", BTN_CONFIRM);
        setButtonIcon(btnConfirm, "/resources/icons/icons8-check-24.png");
        JButton btnCancel = createButton("Hủy", BTN_CANCEL);
        setButtonIcon(btnCancel, "/resources/icons/icons8-close-24.png");

        btnConfirm.addActionListener(e -> {
            try {
                String maGD = bus.taoYeuCauDoiVe(maVeMoi, maVeCu, lyDo);
                JOptionPane.showMessageDialog(this,
                        "Đổi vé thành công!\nMã giao dịch: " + maGD);
                SwingUtilities.getWindowAncestor(this).dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnCancel.addActionListener(e ->
                SwingUtilities.getWindowAncestor(this).dispose()
        );

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ===== UI STYLE =====

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(33,37,41));
        return lbl;
    }

    private JLabel createValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200,45));
        btn.setFont(new Font("Segoe UI", Font.BOLD,14));
        return btn;
    }

    private void setButtonIcon(JButton btn, String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
            btn.setIconTextGap(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}