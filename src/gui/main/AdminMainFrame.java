package gui.main;

import gui.admin.*;
import gui.user.DoiVePanel;

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
        showPanel(new ChuyenBayPanel());
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new BorderLayout());

        // ===== PANEL LOGO (CHỈ HÌNH, KHÔNG TEXT) =====
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
        } else {
            System.err.println("LỖI: Không tìm thấy Logo tại /resources/images/logooo.png");
        }

        sidebar.add(logoPanel, BorderLayout.NORTH);

        // ===== MENU PANEL (Chứa các nút) =====
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setLayout(new GridLayout(0, 1, 0, 5));

        JButton btnThuHang = createSidebarButton("Quản lý hạng", "/resources/icons/icons8-certificate-94.png");
        btnThuHang.addActionListener(e -> {
            setActiveButton(btnThuHang);
            showPanel(new QuanLyThuHangPanel());
        });

        JButton btnKhuyenMai = createSidebarButton("Quản lý khuyến mãi", "/resources/icons/voucher.png");
        btnKhuyenMai.addActionListener(e -> {
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

        JButton btnVeBan = createSidebarButton("Quản lý vé bán", "/resources/icons/ticket.png");
        btnVeBan.addActionListener(e -> {
            setActiveButton(btnVeBan);
            showPanel(new VeBanPanel());
        });

        JButton btnPDV = createSidebarButton("Quản lý Phiếu đặt vé", "/resources/icons/pdv.png");
        btnPDV.addActionListener(e -> {
            setActiveButton(btnPDV);
            showPanel(new PhieuDatVePanel());
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

        JButton btnDoiVe = createSidebarButton("Quản lý đổi vé", "/resources/icons/icons8-changeticket-24.png");
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

        // Thêm các nút vào menuPanel
        menuPanel.add(btnChuyenBay);
        menuPanel.add(btnTuyenBay);
        menuPanel.add(btnVeBan);
        menuPanel.add(btnPDV);
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

        // ===== TẠO THANH CUỘN (SCROLL PANE) ĐỂ LƯỚT XUỐNG =====
        // Dùng 1 Panel bọc ngoài để các nút dồn lên trên (không bị kéo giãn nếu ít nút)
        JPanel menuWrapper = new JPanel(new BorderLayout());
        menuWrapper.setBackground(SIDEBAR_COLOR);
        menuWrapper.add(menuPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(menuWrapper);
        scrollPane.setBackground(SIDEBAR_COLOR);
        scrollPane.getViewport().setBackground(SIDEBAR_COLOR);
        scrollPane.setBorder(null); // Xóa viền của thanh cuộn cho mượt
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Tắt cuộn ngang
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Bật cuộn dọc khi cần
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Chỉnh tốc độ lăn chuột mượt hơn

        // Thêm thanh cuộn vào Sidebar thay vì thêm menuPanel trực tiếp
        sidebar.add(scrollPane, BorderLayout.CENTER);

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
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm icon (Đã áp dụng Safe Code chống Crash)
        if (iconPath != null && !iconPath.trim().isEmpty()) {
            URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image scaledIconImage = originalIcon.getImage().getScaledInstance(
                        24, 24, // Kích thước icon
                        Image.SCALE_SMOOTH
                );
                btn.setIcon(new ImageIcon(scaledIconImage));
                btn.setIconTextGap(10); // Khoảng cách giữa icon và text
            } else {
                System.err.println("Lỗi: Không tìm thấy icon tại " + iconPath);
            }
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