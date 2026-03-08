package gui.custom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import gui.user.DangNhapFrm;
import gui.user.DangKyFrm;
import gui.user.MainFrame;
import gui.user.UserInfoFrm;
import model.NguoiDung;

public class HeaderHelper {
    
    public static final Color PRIMARY_COLOR = new Color(18, 32, 64);
    public static final Color ACCENT_COLOR = new Color(255, 193, 7);

    public static void applyHeaderTheme(JPanel[] panels, JButton... buttons) {
        if (panels != null) {
            for (JPanel pnl : panels) {
                if (pnl != null) pnl.setBackground(PRIMARY_COLOR);
            }
        }
        
        if (buttons != null) {
            for (JButton btn : buttons) {
                if (btn != null) {
                    btn.setBackground(PRIMARY_COLOR);
                    btn.setForeground(Color.WHITE);
                    btn.setBorderPainted(false);
                    btn.setFocusPainted(false);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        }
    }

    public static void setupLogo(JLabel lblLogo, int height, JFrame currentFrame, NguoiDung currentUser) {
        lblLogo.setText(""); 
        try {
            ImageIcon originalIcon = new ImageIcon("src/resources/images/finalLogo.png");
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImg = originalIcon.getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scaledImg));
            } else {
                lblLogo.setText("AirLiner");
                lblLogo.setForeground(Color.WHITE);
                lblLogo.setFont(new Font("VNI-Franko", Font.BOLD, 30));
            }
        } catch (Exception e) {
            lblLogo.setText("AirLiner");
        } 

        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(currentFrame instanceof MainFrame)) {
                    MainFrame main = new MainFrame();
                    
                    main.setVisible(true);
                    currentFrame.dispose();
                } else {
                    System.out.println("Đang ở trang chủ rồi, không nhân đôi trang!");
                }
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setupCurrencyCombo(JComboBox cbDonViTienTe) {
        cbDonViTienTe.removeAllItems();
        cbDonViTienTe.addItem(new CurrencyItem("VND | VI", "svgmaterials/flags/1x1/vn.svg"));
        cbDonViTienTe.addItem(new CurrencyItem("USD | EN", "svgmaterials/flags/1x1/us.svg"));
        
        cbDonViTienTe.setBackground(PRIMARY_COLOR);
        cbDonViTienTe.setForeground(Color.WHITE);
        cbDonViTienTe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cbDonViTienTe.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CurrencyItem) {
                    CurrencyItem item = (CurrencyItem) value;
                    label.setText(item.getText());
                    label.setIcon(item.getIcon());
                    label.setIconTextGap(8); 
                }
                return label;
            }
        });
    }

    static class CurrencyItem {
        private String text;
        private Icon icon;

        public CurrencyItem(String text, String iconPath) {
            this.text = text;
            try {
                FlatSVGIcon svgIcon = new FlatSVGIcon(iconPath, 24, 18);
                if (svgIcon.hasFound()) this.icon = svgIcon;
            } catch (Exception e) { this.icon = null; }
        }
        public String getText() { return text; }
        public Icon getIcon() { return icon; }
        @Override public String toString() { return text; } 
    }

    public static void setupUserMenu(JButton btnLogin, JButton btnSignin, NguoiDung currentUser, JFrame currentFrame) {
        for (ActionListener al : btnLogin.getActionListeners()) btnLogin.removeActionListener(al);
        if (btnSignin != null) {
            for (ActionListener al : btnSignin.getActionListeners()) btnSignin.removeActionListener(al);
        }

        if (currentUser == null) {
            if (btnSignin != null) {
                btnSignin.setVisible(true);
                btnSignin.setBackground(ACCENT_COLOR);
                btnSignin.setForeground(PRIMARY_COLOR);
                btnSignin.setBorderPainted(false);
                btnSignin.addActionListener(e -> {
                    DangKyFrm frm = new DangKyFrm();
                    frm.setLocationRelativeTo(currentFrame);
                    frm.setVisible(true);
                    currentFrame.dispose();
                });
            }
            
            btnLogin.setText("Đăng nhập");
            btnLogin.setIcon(null); 
            btnLogin.setBackground(PRIMARY_COLOR);
            btnLogin.setForeground(Color.WHITE);
            btnLogin.setBorderPainted(false);
            btnLogin.addActionListener(e -> {
                DangNhapFrm frm = new DangNhapFrm();
                frm.setLocationRelativeTo(currentFrame);
                frm.setVisible(true);
                currentFrame.dispose();
            });
        } else {
            if (btnSignin != null) btnSignin.setVisible(false);
            
            String chuCaiDau = "U";
            if (currentUser.getUsername() != null && !currentUser.getUsername().isEmpty()) {
                chuCaiDau = currentUser.getUsername().substring(0, 1).toUpperCase();
            }
            
            Color[] palette = {
                new Color(234, 67, 53), new Color(52, 168, 83), new Color(66, 133, 244),
                new Color(251, 188, 5), new Color(156, 39, 176), new Color(0, 150, 136)
            };
            int randomIndex = new Random().nextInt(palette.length);
            
            btnLogin.setText(chuCaiDau);
            btnLogin.setBackground(palette[randomIndex]);
            btnLogin.setForeground(Color.WHITE);
            btnLogin.setFont(new Font("Arial", Font.BOLD, 22));
            btnLogin.putClientProperty("JButton.buttonType", "roundRect");
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String ngayThamGia = (currentUser.getNgayTao() != null) ? currentUser.getNgayTao().format(dtf) : "Chưa rõ";
            btnLogin.setToolTipText("Hồ sơ của: " + currentUser.getUsername() + " | Tham gia từ: " + ngayThamGia);
            
            JPopupMenu avatarMenu = new JPopupMenu();
            JMenuItem itemThongTin = new JMenuItem("Thông tin người dùng");
            JMenuItem itemLichSu = new JMenuItem("Lịch sử đặt vé");
            JMenuItem itemDangXuat = new JMenuItem("Đăng xuất");
            
            itemThongTin.addActionListener(e -> {
                if (!(currentFrame instanceof UserInfoFrm)) {
                    UserInfoFrm infoFrm = new UserInfoFrm();
                    infoFrm.loadDataToForm(currentUser);
                    infoFrm.setVisible(true);
                    currentFrame.dispose();
                }
            });
            itemLichSu.addActionListener(e -> JOptionPane.showMessageDialog(currentFrame, "Đang xây dựng form Lịch sử đặt vé..."));
            itemDangXuat.addActionListener(e -> {
                Preferences prefs = Preferences.userNodeForPackage(DangNhapFrm.class);
                prefs.remove("saved_user");
                prefs.remove("saved_pass");
                new MainFrame().setVisible(true);
                currentFrame.dispose();
            });

            avatarMenu.add(itemThongTin);
            avatarMenu.add(itemLichSu);
            avatarMenu.addSeparator();
            avatarMenu.add(itemDangXuat);
            
            btnLogin.addActionListener(e -> avatarMenu.show(btnLogin, 0, btnLogin.getHeight()));
        }
    }
}