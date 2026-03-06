package gui.main;

import gui.admin.*;
import gui.user.DoiVePanel;
import org.apache.xmlbeans.impl.values.JavaHexBinaryHolder;

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
        // ===== MENU PANEL =====
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setLayout(new GridLayout(0, 1, 0, 5));

        JButton btnThuHang = createSidebarButton("Quản lý hạng", "/resources/icons/icons8-certificate-94.png");

        btnThuHang.addActionListener(e -> {
            setActiveButton(btnThuHang);
            showPanel(new QuanLyThuHangPanel());
        });

        JButton btnKhuyenMai = createSidebarButton("Quản lý khuyến mãi", "/resources/icons/voucher.png");
        btnKhuyenMai.addActionListener(e ->{
            setActiveButton(btnKhuyenMai);
            showPanel(new QuanLyKhuyenMaiPanel());
        });

        JButton btnTuyenBay = createSidebarButton("Quản lý tuyến bay", "/resources/icons/tuyenbay.png"); 
        btnTuyenBay.addActionListener(e -> {
            setActiveButton(btnTuyenBay);
            showPanel(new TuyenBayPanel()); 
        });

        JButton btnChuyenBay = createSidebarButton("Quản lý chuyến bay", "/resources/icons/flight.png");
        btnChuyenBay.addActionListener(e -> {
            setActiveButton(btnChuyenBay);
            showPanel(new ChuyenBayPanel());
        });

        JButton btnHeSoGia = createSidebarButton("Quản lý hệ số giá", "/resources/icons/price.png");
        btnHeSoGia.addActionListener(e -> {
            setActiveButton(btnHeSoGia);
            showPanel(new HeSoGiaPanel());
        });

        JButton btnHangVe = createSidebarButton("Quản lý hạng vé", "/resources/icons/hangve.png");
        btnHangVe.addActionListener(e -> {
            setActiveButton(btnHangVe);
            showPanel(new HangVePanel());
        });

        JButton btnGheMayBay = createSidebarButton("Quản lý ghế máy bay", "/resources/icons/chair.png");
        btnGheMayBay.addActionListener(e -> {
            setActiveButton(btnGheMayBay);
            
            GheMayBayPanel gheMayBayPanel = new GheMayBayPanel();
            
            gheMayBayPanel.setPanelSwitchListener((maMayBay, tenMayBay) -> {
                
                SoDoGhePanel soDoPanel = new SoDoGhePanel(maMayBay, tenMayBay);
                
                soDoPanel.setBackListener(() -> {
                    showPanel(gheMayBayPanel);                
});
                showPanel(soDoPanel);
            });
            
            showPanel(gheMayBayPanel);
        });

        JButton btnDoiVe = createSidebarButton("Quản lý đổi vé",
            "/resources/icons/icons8-changeticket-24.png");
        btnDoiVe.addActionListener(e -> {
            setActiveButton(btnDoiVe);
            showPanel(new QuanLyDoiVePanel());
        }); 

        JButton btnNguoiDung = createSidebarButton("Quản Lý Người Dùng", "/resources/icons/user.png");
        
        btnNguoiDung.addActionListener(e -> {
            setActiveButton(btnNguoiDung);
            showPanel(new QuanLyNguoiDungPanel()); 
        }); 

        JButton btnNhanVien = createSidebarButton("Quản Lý Nhân Viên", "/resources/icons/staff.png"); 
        btnNhanVien.addActionListener(e -> {
            setActiveButton(btnNhanVien);
            showPanel(new QuanLyNhanVienPanel()); 
        }); 

        JButton btnSanBay = createSidebarButton("Quản lý Sân Bay", "/resources/icons/airport.png");
        btnSanBay.addActionListener(e -> {
            setActiveButton(btnSanBay);
            showPanel(new QuanLySanBayPanel());
        });  

        JButton btnMayBay = createSidebarButton("Quản lý Máy Bay", "/resources/icons/plane.png");
        btnMayBay.addActionListener(e -> {
            setActiveButton(btnMayBay);
            showPanel(new QuanLyMayBayPanel());
        });

        menuPanel.add(btnChuyenBay);
        menuPanel.add(btnTuyenBay);
        menuPanel.add(btnThuHang);
        menuPanel.add(btnKhuyenMai);
        menuPanel.add(btnHeSoGia);
        menuPanel.add(btnHangVe);
        menuPanel.add(btnGheMayBay);
        menuPanel.add(btnDoiVe);
        menuPanel.add(btnNguoiDung); 
        menuPanel.add(btnNhanVien); 
        menuPanel.add(btnSanBay); 
        menuPanel.add(btnMayBay);
        sidebar.add(menuPanel, BorderLayout.CENTER);

        setActiveButton(btnChuyenBay);

        return sidebar;
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
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