package gui.user;

import dal.NguoiDungDAO;
import dal.ThongTinHanhKhachDAO;
import model.NguoiDung;
import model.ThongTinHanhKhach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class PanelUserProfile extends JPanel {

    // Colors matching Admin Panel
    private static final Color HEADER_COLOR = new Color(211, 47, 47); // #D32F2F
    private static final Color BACKGROUND_COLOR = new Color(26, 35, 126); // #1A237E
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    // Tier Colors
    private static final Color SILVER_COLOR = new Color(192, 192, 192);
    private static final Color GOLD_COLOR = new Color(255, 193, 7);
    private static final Color PLATINUM_COLOR = new Color(229, 228, 226);
    private static final Color DIAMOND_COLOR = new Color(185, 242, 255);

    // Components
    private JLabel lblAvatar;
    private JLabel lblHoTen;
    private JLabel lblUsername;
    private JLabel lblMaHK;
    private JLabel lblEmail;
    private JLabel lblSDT;
    private JLabel lblNgaySinh;
    private JLabel lblGioiTinh;
    private JLabel lblCCCD;

    // Tier components
    private JLabel lblCurrentTier;
    private JLabel lblTierIcon;
    private JLabel lblCurrentPoints;
    private JLabel lblPointsToNext;
    private JProgressBar progressBar;
    private JLabel lblProgressText;
    private JTextArea txtBenefits;

    // Data
    private String maHK;
    private ThongTinHanhKhach hanhKhach;
    private NguoiDung nguoiDung;

    public PanelUserProfile(String maHK) {
        this.maHK = maHK;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initComponents();
        loadData();
    }

    private void initComponents() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(BACKGROUND_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        mainContainer.add(createHeader(), BorderLayout.NORTH);

        // Content Area with Cards
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Profile Card
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.4;
        contentPanel.add(createProfileCard(), gbc);

        // Tier Card
        gbc.gridy = 1;
        gbc.weighty = 0.6;
        contentPanel.add(createTierCard(), gbc);

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("THÔNG TIN TÀI KHOẢN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Xem thông tin cá nhân và hạng thành viên của bạn");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createProfileCard() {
        JPanel card = createCard("THÔNG TIN CÁ NHÂN");
        card.setLayout(new BorderLayout(20, 0));

        // Avatar Panel (Left)
        JPanel avatarPanel = new JPanel(new GridBagLayout());
        avatarPanel.setOpaque(false);

        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(120, 120));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(new Color(240, 240, 240));

        avatarPanel.add(lblAvatar);
        card.add(avatarPanel, BorderLayout.WEST);

        // Info Panel (Center)
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 12));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Initialize labels
        lblHoTen = new JLabel();
        lblUsername = new JLabel();
        lblMaHK = new JLabel();
        lblEmail = new JLabel();
        lblSDT = new JLabel();
        lblNgaySinh = new JLabel();
        lblGioiTinh = new JLabel();
        lblCCCD = new JLabel();

        // Add fields
        addInfoRow(infoPanel, "Họ và tên:", lblHoTen);
        addInfoRow(infoPanel, "Tên đăng nhập:", lblUsername);
        addInfoRow(infoPanel, "Mã hành khách:", lblMaHK);
        addInfoRow(infoPanel, "Email:", lblEmail);
        addInfoRow(infoPanel, "Số điện thoại:", lblSDT);
        addInfoRow(infoPanel, "Ngày sinh:", lblNgaySinh);
        addInfoRow(infoPanel, "Giới tính:", lblGioiTinh);
        addInfoRow(infoPanel, "CCCD:", lblCCCD);

        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTierCard() {
        JPanel card = createCard("HẠNG THÀNH VIÊN & ƯU ĐÃI");
        card.setLayout(new BorderLayout(0, 15));

        // Top: Current Tier Display
        JPanel tierDisplayPanel = new JPanel(new BorderLayout(15, 0));
        tierDisplayPanel.setOpaque(false);
        tierDisplayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tier Icon
        lblTierIcon = new JLabel();
        lblTierIcon.setPreferredSize(new Dimension(80, 80));
        lblTierIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblTierIcon.setFont(new Font("Segoe UI", Font.BOLD, 36));
        tierDisplayPanel.add(lblTierIcon, BorderLayout.WEST);

        // Tier Info
        JPanel tierInfoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        tierInfoPanel.setOpaque(false);

        lblCurrentTier = new JLabel();
        lblCurrentTier.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblCurrentTier.setForeground(TEXT_PRIMARY);

        lblCurrentPoints = new JLabel();
        lblCurrentPoints.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCurrentPoints.setForeground(TEXT_SECONDARY);

        tierInfoPanel.add(lblCurrentTier);
        tierInfoPanel.add(lblCurrentPoints);
        tierDisplayPanel.add(tierInfoPanel, BorderLayout.CENTER);

        card.add(tierDisplayPanel, BorderLayout.NORTH);

        // Middle: Progress Bar Section
        JPanel progressPanel = new JPanel(new BorderLayout(0, 8));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblPointsToNext = new JLabel();
        lblPointsToNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPointsToNext.setForeground(TEXT_PRIMARY);
        progressPanel.add(lblPointsToNext, BorderLayout.NORTH);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 30));
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        progressPanel.add(progressBar, BorderLayout.CENTER);

        lblProgressText = new JLabel();
        lblProgressText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblProgressText.setForeground(TEXT_SECONDARY);
        lblProgressText.setHorizontalAlignment(SwingConstants.RIGHT);
        progressPanel.add(lblProgressText, BorderLayout.SOUTH);

        card.add(progressPanel, BorderLayout.CENTER);

        // Bottom: Benefits
        JPanel benefitsPanel = new JPanel(new BorderLayout(0, 8));
        benefitsPanel.setOpaque(false);
        benefitsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblBenefitsTitle = new JLabel("Quyền lợi hiện tại:");
        lblBenefitsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBenefitsTitle.setForeground(TEXT_PRIMARY);
        benefitsPanel.add(lblBenefitsTitle, BorderLayout.NORTH);

        txtBenefits = new JTextArea();
        txtBenefits.setEditable(false);
        txtBenefits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBenefits.setForeground(TEXT_SECONDARY);
        txtBenefits.setBackground(CARD_COLOR);
        txtBenefits.setBorder(new EmptyBorder(5, 5, 5, 5));
        txtBenefits.setLineWrap(true);
        txtBenefits.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(txtBenefits);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.setPreferredSize(new Dimension(0, 100));
        benefitsPanel.add(scrollPane, BorderLayout.CENTER);

        card.add(benefitsPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Card Title
        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(HEADER_COLOR);
        cardTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(cardTitle, BorderLayout.NORTH);

        return card;
    }

    private void addInfoRow(JPanel panel, String label, JLabel valueLabel) {
        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblKey.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(TEXT_PRIMARY);

        panel.add(lblKey);
        panel.add(valueLabel);
    }

    private void loadData() {
        try {
            ThongTinHanhKhachDAO hkDAO = new ThongTinHanhKhachDAO();
            hanhKhach = hkDAO.getByMaHK(maHK);

            if (hanhKhach == null) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy thông tin hành khách!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Load NguoiDung info
            NguoiDungDAO ndDAO = new NguoiDungDAO();
            nguoiDung = ndDAO.getByMaNguoiDung(hanhKhach.getMaNguoiDung());

            // Update UI
            updateProfileInfo();
            updateTierInfo();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProfileInfo() {
        lblHoTen.setText(hanhKhach.getHoTen());
        lblUsername.setText(nguoiDung != null ? nguoiDung.getUsername() : "N/A");
        lblMaHK.setText(hanhKhach.getMaHK());
        lblEmail.setText(nguoiDung != null ? nguoiDung.getEmail() : "N/A");
        lblSDT.setText(nguoiDung != null ? nguoiDung.getSoDienThoai() : "N/A");

        if (hanhKhach.getNgaySinh() != null) {
            lblNgaySinh.setText(hanhKhach.getNgaySinh()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblNgaySinh.setText("N/A");
        }

        lblGioiTinh.setText(hanhKhach.getGioiTinh() != null ? hanhKhach.getGioiTinh() : "N/A");
        lblCCCD.setText(hanhKhach.getCccd() != null ? hanhKhach.getCccd() : "N/A");

        // Set avatar icon
        String gioiTinh = hanhKhach.getGioiTinh();
        if ("Nam".equalsIgnoreCase(gioiTinh)) {
            lblAvatar.setText("👨");
            lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        } else if ("Nữ".equalsIgnoreCase(gioiTinh) || "Nu".equalsIgnoreCase(gioiTinh)) {
            lblAvatar.setText("👩");
            lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        } else {
            lblAvatar.setText("👤");
            lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        }
    }

    private void updateTierInfo() {
        int currentPoints = hanhKhach.getDiemTichLuy();
        String tier = hanhKhach.getLoaiHanhKhach();

        // Tier thresholds
        int silverThreshold = 0;
        int goldThreshold = 2000;
        int platinumThreshold = 5000;
        int diamondThreshold = 10000;

        // Current tier display
        lblCurrentTier.setText(tier.toUpperCase());
        lblCurrentPoints.setText(String.format("Điểm hiện tại: %,d điểm", currentPoints));

        // Set tier icon and color
        Color tierColor;
        String tierIcon;
        switch (tier.toUpperCase()) {
            case "GOLD":
                tierColor = GOLD_COLOR;
                tierIcon = "🥇";
                break;
            case "PLATINUM":
                tierColor = PLATINUM_COLOR;
                tierIcon = "💎";
                break;
            case "DIAMOND":
                tierColor = DIAMOND_COLOR;
                tierIcon = "💠";
                break;
            default: // SILVER
                tierColor = SILVER_COLOR;
                tierIcon = "🥈";
                break;
        }

        lblTierIcon.setText(tierIcon);
        lblTierIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblCurrentTier.setForeground(tierColor);
        progressBar.setForeground(tierColor);

        // Calculate progress
        int nextThreshold;
        String nextTier;
        int pointsInCurrentTier;
        int pointsNeededForTier;

        if (currentPoints < goldThreshold) {
            nextThreshold = goldThreshold;
            nextTier = "Gold";
            pointsInCurrentTier = currentPoints - silverThreshold;
            pointsNeededForTier = goldThreshold - silverThreshold;
        } else if (currentPoints < platinumThreshold) {
            nextThreshold = platinumThreshold;
            nextTier = "Platinum";
            pointsInCurrentTier = currentPoints - goldThreshold;
            pointsNeededForTier = platinumThreshold - goldThreshold;
        } else if (currentPoints < diamondThreshold) {
            nextThreshold = diamondThreshold;
            nextTier = "Diamond";
            pointsInCurrentTier = currentPoints - platinumThreshold;
            pointsNeededForTier = diamondThreshold - platinumThreshold;
        } else {
            // Already at max tier
            lblPointsToNext.setText("🎉 Bạn đã đạt hạng cao nhất!");
            progressBar.setValue(100);
            lblProgressText.setText("100%");
            updateBenefits(tier);
            return;
        }

        int pointsNeeded = nextThreshold - currentPoints;
        int progressPercent = (int) ((pointsInCurrentTier * 100.0) / pointsNeededForTier);

        lblPointsToNext.setText(String.format(
                "Còn %,d điểm nữa để lên hạng %s",
                pointsNeeded,
                nextTier
        ));

        progressBar.setValue(progressPercent);

        lblProgressText.setText(String.format(
                "%,d / %,d điểm (%d%%)",
                pointsInCurrentTier,
                pointsNeededForTier,
                progressPercent
        ));

        // Update benefits
        updateBenefits(tier);
    }

    private void updateBenefits(String tier) {
        String benefits;
        switch (tier.toUpperCase()) {
            case "GOLD":
                benefits = "✓ Ưu tiên check-in\n" +
                        "✓ Miễn phí hành lý ký gửi 30kg\n" +
                        "✓ Giảm 5% giá vé\n" +
                        "✓ Tích điểm x1.5\n" +
                        "✓ Phòng chờ tiêu chuẩn";
                break;
            case "PLATINUM":
                benefits = "✓ Ưu tiên check-in cao cấp\n" +
                        "✓ Miễn phí hành lý ký gửi 40kg\n" +
                        "✓ Giảm 10% giá vé\n" +
                        "✓ Tích điểm x2\n" +
                        "✓ Phòng chờ VIP\n" +
                        "✓ Đổi lịch bay miễn phí 1 lần";
                break;
            case "DIAMOND":
                benefits = "✓ Ưu tiên tuyệt đối\n" +
                        "✓ Miễn phí hành lý ký gửi 50kg\n" +
                        "✓ Giảm 15% giá vé\n" +
                        "✓ Tích điểm x3\n" +
                        "✓ Phòng chờ VIP Premium\n" +
                        "✓ Đổi lịch bay không giới hạn\n" +
                        "✓ Nâng hạng ghế miễn phí\n" +
                        "✓ Hỗ trợ 24/7 riêng biệt";
                break;
            default: // SILVER
                benefits = "✓ Tích điểm cơ bản x1\n" +
                        "✓ Miễn phí hành lý ký gửi 20kg\n" +
                        "✓ Ưu đãi sinh nhật\n" +
                        "✓ Thông báo khuyến mãi sớm";
                break;
        }
        txtBenefits.setText(benefits);
    }
}
