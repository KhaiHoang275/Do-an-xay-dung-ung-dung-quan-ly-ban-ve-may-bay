package gui.main;

import gui.admin.QuanLyThuHangPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class AdminMainFrame extends JFrame {

    private JPanel contentPanel;
    private JButton currentActiveButton;

    // ===== MÀU CHỦ ĐẠO =====
    private final Color SIDEBAR_COLOR = new Color(18, 32, 64);
    private final Color BUTTON_COLOR = new Color(28, 48, 96);
    private final Color BUTTON_HOVER = new Color(45, 72, 140);
    private final Color BUTTON_ACTIVE = new Color(255, 193, 7);
    private final Color ACTIVE_TEXT = new Color(18, 32, 64);

    public AdminMainFrame() {
        setTitle("ADMIN - Quản lý hệ thống");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== CONTENT PANEL =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 250));
        add(contentPanel, BorderLayout.CENTER);

        // ===== SIDEBAR =====
        add(createSidebar(), BorderLayout.WEST);

        // ===== MỞ MẶC ĐỊNH PANEL =====
        showPanel(new QuanLyThuHangPanel());
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new BorderLayout());

        // ===== PANEL LOGO (CHỈ HÌNH, KHÔNG TEXT) =====
        // ===== LOGO =====
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(SIDEBAR_COLOR);
        logoPanel.setPreferredSize(new Dimension(230, 200)); // chiều cao logo

        URL logoURL = getClass().getResource("/resources/images/logooo.png");

        if (logoURL != null) {

            ImageIcon logoIcon = new ImageIcon(logoURL);

            JLabel logoLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    Image img = logoIcon.getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            };

            logoPanel.add(logoLabel, BorderLayout.CENTER);
        }

        sidebar.add(logoPanel, BorderLayout.NORTH);
//        JPanel logoPanel = new JPanel();
//        logoPanel.setBackground(SIDEBAR_COLOR);
//        logoPanel.setLayout(new BorderLayout());
//        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
//
//        ImageIcon icon = new ImageIcon(
//                getClass().getResource("/resources/images/logox120.png") // nhớ: KHÔNG /resources
//        );
//
//        Image scaledImage = icon.getImage().getScaledInstance(
//                120, 120, // kích thước đẹp cho sidebar 230px
//                Image.SCALE_SMOOTH
//        );
//
//        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
//        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
//
//        logoPanel.add(logoLabel, BorderLayout.CENTER);
//
//        sidebar.add(logoPanel, BorderLayout.NORTH);

        // ===== MENU PANEL =====
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setLayout(new GridLayout(10, 1, 0, 5));

        JButton btnThuHang = createSidebarButton("Quản lý hạng", "/resources/icons/icons8-certificate-94.png");

        btnThuHang.addActionListener(e -> {
            setActiveButton(btnThuHang);
            showPanel(new QuanLyThuHangPanel());
        });

        menuPanel.add(btnThuHang);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        setActiveButton(btnThuHang);

        return sidebar;
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm icon
        if (iconPath != null) {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledIconImage = originalIcon.getImage().getScaledInstance(
                    24, 24, // Kích thước icon nhỏ để vừa button (tùy chỉnh nếu cần)
                    Image.SCALE_SMOOTH
            );
            btn.setIcon(new ImageIcon(scaledIconImage));
            btn.setIconTextGap(10); // Khoảng cách giữa icon và text
        }

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != currentActiveButton) {
                    btn.setBackground(BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentActiveButton) {
                    btn.setBackground(BUTTON_COLOR);
                }
            }
        });

        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (currentActiveButton != null) {
            currentActiveButton.setBackground(BUTTON_COLOR);
            currentActiveButton.setForeground(Color.WHITE);
        }

        currentActiveButton = btn;
        btn.setBackground(BUTTON_ACTIVE);
        btn.setForeground(ACTIVE_TEXT);
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}