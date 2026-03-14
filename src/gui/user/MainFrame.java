package gui.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class MainFrame extends javax.swing.JFrame { 
    // KHU VỰC CHỨA NỘI DUNG CHÍNH (Đã tách riêng khỏi Header)
    private JPanel pnlContentWrapper;
    
    private JPanel pnlKetQuaContainer;
    private javax.swing.JScrollPane scrollPaneKetQua;
    
    private int soNL = 1, soTE = 0, soEB = 0; 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName()); 
    
    private javax.swing.JPanel pnlTabsKhuHoi;
    private javax.swing.JButton btnTabDi;
    private javax.swing.JButton btnTabVe;
    private javax.swing.JPanel pnlDateRibbon;
    
    private java.time.LocalDate currentSearchDateDi;
    private java.time.LocalDate currentSearchDateVe;
    private boolean isSearchingVe = false; 

    // =========================================================
    // BIẾN LƯU TRỮ GIỎ HÀNG (SHOPPING CART STATE)
    // =========================================================
    private JPanel pnlStickyFooter;
    private JLabel lblFooterTotal;
    private JButton btnFooterNext;
    
    private model.ChuyenBay cbSelectedDi = null;
    private String hangVeSelectedDi = "";
    private BigDecimal giaVeDi = BigDecimal.ZERO;
    
    private model.ChuyenBay cbSelectedVe = null;
    private String hangVeSelectedVe = "";
    private BigDecimal giaVeVe = BigDecimal.ZERO;

    public MainFrame() {
        initComponents();     
        initKetQuaPanel();
        setupLogo(); 
        applyTheme();
        gui.custom.UIHelper.loadSanBayToComboBox(cbCities, cbCities1);
        setupCurrencyComboBox(); 
        setupHanhKhachPopup();  
        setupDatePicker(jFormattedTextField1); 
        setupDatePicker(jFormattedTextField2);
        setupBusinessLogic();  
        
        setLocationRelativeTo(null);
        
        jButton2.setText(""); 
        try { jButton2.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon("svgmaterials/icons/bx-transfer.svg", 24, 24)); } catch (Exception e) {} 
        for(java.awt.event.ActionListener al : btnLogin.getActionListeners()) btnLogin.removeActionListener(al);
        btnLogin.addActionListener(e -> {
            DangNhapFrm frm = new DangNhapFrm(); frm.setLocationRelativeTo(this); frm.setVisible(true); this.dispose();
        });
    } 

    private model.NguoiDung userHienTai;

    public MainFrame(model.NguoiDung nd) {
        initComponents();    
        initKetQuaPanel();
        applyTheme();
        setupLogo();
        gui.custom.UIHelper.loadSanBayToComboBox(cbCities, cbCities1);
        setupCurrencyComboBox(); 
        setupHanhKhachPopup(); 
        setupDatePicker(jFormattedTextField1); 
        setupDatePicker(jFormattedTextField2); 
        
        setupBusinessLogic();
        this.userHienTai = nd;
        setTitle("AirLiner - Xin chào: " + userHienTai.getUsername()); 
        
        setLocationRelativeTo(null);
        
        String chuCaiDau = "U";
        if (userHienTai.getUsername() != null && !userHienTai.getUsername().isEmpty()) {
            chuCaiDau = userHienTai.getUsername().substring(0, 1).toUpperCase();
        }
        
        java.awt.Color[] palette = {
            new java.awt.Color(234, 67, 53), new java.awt.Color(52, 168, 83),
            new java.awt.Color(66, 133, 244), new java.awt.Color(251, 188, 5),
            new java.awt.Color(156, 39, 176), new java.awt.Color(0, 150, 136),
            new java.awt.Color(255, 112, 67)
        };
        int randomIndex = new java.util.Random().nextInt(palette.length);
        
        btnLogin.setText(chuCaiDau); 
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngayThamGia = (userHienTai.getNgayTao() != null) ? userHienTai.getNgayTao().format(dtf) : "Chưa rõ";
        
        btnLogin.setToolTipText("Hồ sơ của: " + userHienTai.getUsername() + " | Tham gia từ: " + ngayThamGia);
        btnLogin.setBackground(palette[randomIndex]); 
        btnLogin.setForeground(java.awt.Color.WHITE); 
        btnLogin.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22)); 
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        
        javax.swing.JPopupMenu avatarMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem itemThongTin = new javax.swing.JMenuItem("Thông tin người dùng");
        javax.swing.JMenuItem itemLichSu = new javax.swing.JMenuItem("Lịch sử đặt vé");
        javax.swing.JMenuItem itemDangXuat = new javax.swing.JMenuItem("Đăng xuất");

        itemThongTin.addActionListener(e -> {
            UserInfoFrm infoFrm = new UserInfoFrm(); infoFrm.loadDataToForm(this.userHienTai); infoFrm.setLocationRelativeTo(this); infoFrm.setVisible(true); this.dispose(); 
        });
        
        itemLichSu.addActionListener(e -> {
            if (userHienTai != null) {
                switchPanel(new gui.user.LichSuDatVePanel(userHienTai.getMaNguoiDung()));
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để xem lịch sử!");
            }
        });   

        itemDangXuat.addActionListener(e -> {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(gui.user.DangNhapFrm.class);
            prefs.remove("saved_user"); prefs.remove("saved_pass");
            new MainFrame().setVisible(true); this.dispose();
        });

        avatarMenu.add(itemThongTin); avatarMenu.add(itemLichSu);
        avatarMenu.addSeparator(); avatarMenu.add(itemDangXuat);

        for(java.awt.event.ActionListener al : btnLogin.getActionListeners()) btnLogin.removeActionListener(al);
        btnLogin.addActionListener(e -> avatarMenu.show(btnLogin, 0, btnLogin.getHeight()));

        jButton2.setText(""); 
        try { jButton2.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon("svgmaterials/icons/bx-transfer.svg", 24, 24)); } catch (Exception e) {}
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        btnGChuyenBay = new javax.swing.ButtonGroup();
        pnlHeader = new javax.swing.JPanel();
        
        pnlContentWrapper = new javax.swing.JPanel() {
            Image bgImage;
            {
                try {
                    java.net.URL url = getClass().getResource("/resources/images/khairline.jpg");
                    if (url != null) bgImage = new ImageIcon(url).getImage();
                    else bgImage = new ImageIcon("src/resources/images/khairline.jpg").getImage();
                } catch (Exception e) {}
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        pnlContentWrapper.setOpaque(true);
        pnlContentWrapper.setLayout(new java.awt.BorderLayout());
        
        pnlContent = new javax.swing.JPanel();
        pnlContent.setOpaque(false);
        
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbCities = new javax.swing.JComboBox<>();
        cbCities1 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        btnHanhKhach = new javax.swing.JButton();
        pnlMultiCities = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pnlMenuHeader = new javax.swing.JPanel();
        btnKhuyenMai = new javax.swing.JButton();
        btnDatCho = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        cbDonViTienTe = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlHeader.setBackground(new java.awt.Color(18, 32, 64));
        
        jLabel1.setFont(new java.awt.Font("VNI-Franko", 1, 32)); 
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("✈ AirLiner");

        btnKhuyenMai.setBackground(new java.awt.Color(18, 32, 64));
        btnKhuyenMai.setFont(new java.awt.Font("Segoe UI", 1, 16)); 
        btnKhuyenMai.setForeground(Color.WHITE);
        btnKhuyenMai.setText("Khuyến mãi");
        btnKhuyenMai.setContentAreaFilled(false);
        btnKhuyenMai.setBorderPainted(false);

        btnDatCho.setBackground(new java.awt.Color(18, 32, 64));
        btnDatCho.setFont(new java.awt.Font("Segoe UI", 1, 16)); 
        btnDatCho.setForeground(Color.WHITE);
        btnDatCho.setText("Đặt chỗ của tôi");
        btnDatCho.setContentAreaFilled(false);
        btnDatCho.setBorderPainted(false);

        btnLogin.setBackground(new java.awt.Color(255, 255, 51));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 16)); 
        btnLogin.setForeground(new java.awt.Color(18, 32, 64));
        btnLogin.setText("Đăng nhập / Đăng ký"); // ĐÃ SỬA: Đổi tên nút thành Đăng nhập / Đăng ký cho tiện lợi

        cbDonViTienTe.setBackground(new java.awt.Color(18, 32, 64));
        cbDonViTienTe.setForeground(Color.WHITE);
        cbDonViTienTe.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
        cbDonViTienTe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " VND | VI", " USD | EN" }));

        // ĐÃ SỬA: Bỏ hoàn toàn btnSignin khỏi GroupLayout
        javax.swing.GroupLayout pnlMenuHeaderLayout = new javax.swing.GroupLayout(pnlMenuHeader);
        pnlMenuHeader.setLayout(pnlMenuHeaderLayout);
        pnlMenuHeader.setOpaque(false);
        pnlMenuHeaderLayout.setHorizontalGroup(
            pnlMenuHeaderLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(cbDonViTienTe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKhuyenMai)
                .addGap(18, 18, 18)
                .addComponent(btnDatCho)
                .addGap(18, 18, 18)
                .addComponent(btnLogin)
                .addContainerGap(20, Short.MAX_VALUE)
        );
        pnlMenuHeaderLayout.setVerticalGroup(
            pnlMenuHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlMenuHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDonViTienTe, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKhuyenMai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDatCho, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300, Short.MAX_VALUE)
                .addComponent(pnlMenuHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addGroup(pnlHeaderLayout.createSequentialGroup()
                    .addGap(15, 15, 15)
                    .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel1)
                        .addComponent(pnlMenuHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(15, 15, 15))
        );

        getContentPane().add(pnlHeader, java.awt.BorderLayout.NORTH);
        
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 20)); jLabel2.setText("Từ");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 20)); jLabel3.setText("Đến");

        cbCities.setFont(new java.awt.Font("Segoe UI", 1, 18)); cbCities.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chọn thành phố " }));
        cbCities1.setFont(new java.awt.Font("Segoe UI", 1, 18)); cbCities1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chọn thành phố " }));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 20)); jButton2.setText("<=>");

        jRadioButton1.setFont(new java.awt.Font("Arial", 1, 18)); jRadioButton1.setText("một chiều / khứ hồi");
        jRadioButton2.setFont(new java.awt.Font("Arial", 1, 18)); jRadioButton2.setText("Nhiều thành phố");

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 18)); jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Phổ thông " }));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); jLabel4.setText("Ngày khởi hành");

        jFormattedTextField1.setText("12 th 2 2026"); jFormattedTextField1.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jFormattedTextField2.setText("15 th 2 2026"); jFormattedTextField2.setFont(new java.awt.Font("Segoe UI", 1, 18)); 

        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 1, 20)); jCheckBox1.setText("Khứ hồi");
        jCheckBox1.addActionListener(this::jCheckBox1ActionPerformed);

        jButton3.setBackground(new java.awt.Color(255, 255, 51)); jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); jButton3.setText("Tìm kiếm");

        btnHanhKhach.setFont(new java.awt.Font("Segoe UI", 1, 18)); btnHanhKhach.setText("1 Người lớn, 0 Trẻ em, 0 Em bé");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cbCities1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbCities, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnHanhKhach)
                        .addGap(20, 20, 20)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton3))
                            .addComponent(jCheckBox1))))
                .addContainerGap(112, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHanhKhach, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jCheckBox1))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbCities, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCities1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE)
        );

        pnlMultiCities.setMaximumSize(new java.awt.Dimension(250, 300));
        pnlMultiCities.setMinimumSize(new java.awt.Dimension(250, 300));
        pnlMultiCities.setPreferredSize(new java.awt.Dimension(1000, 50));
        pnlMultiCities.setVerifyInputWhenFocusTarget(false);
        pnlMultiCities.setOpaque(false);

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createSequentialGroup()
                .addGap(219, 219, 219)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlMultiCities, javax.swing.GroupLayout.PREFERRED_SIZE, 1357, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMultiCities, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(186, Short.MAX_VALUE)
        );

        pnlContentWrapper.add(pnlContent, java.awt.BorderLayout.CENTER);
        getContentPane().add(pnlContentWrapper, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        boolean isKhuHoi = jCheckBox1.isSelected();
        jFormattedTextField2.setEnabled(isKhuHoi); 
        if (isKhuHoi) {
            try {
                java.time.LocalDate ngayDi = java.time.LocalDate.parse(jFormattedTextField1.getText(), dateFormatter);
                jFormattedTextField2.setText(ngayDi.format(dateFormatter));
            } catch (Exception ex) {}
        } else {
            if (pnlTabsKhuHoi != null) pnlTabsKhuHoi.setVisible(false);
        }
    }                   
    
    private void applyTheme() {
        java.awt.Color PRIMARY_COLOR = new java.awt.Color(18, 32, 64);
        java.awt.Color SECONDARY_COLOR = new java.awt.Color(45, 72, 140);
        java.awt.Color ACCENT_COLOR = new java.awt.Color(255, 193, 7);

        jButton3.setBackground(ACCENT_COLOR);
        jButton3.setForeground(PRIMARY_COLOR); 

        jLabel2.setForeground(Color.WHITE);
        jLabel3.setForeground(Color.WHITE);
        jLabel4.setForeground(Color.WHITE);
        jCheckBox1.setForeground(Color.WHITE);
        jRadioButton1.setForeground(Color.WHITE);
        jRadioButton2.setForeground(Color.WHITE);
        jPanel3.setBackground(new Color(220, 38, 38)); 

        cbCities.setBackground(java.awt.Color.WHITE);
        cbCities.setForeground(PRIMARY_COLOR);
        cbCities1.setBackground(java.awt.Color.WHITE);
        cbCities1.setForeground(PRIMARY_COLOR);
        jComboBox1.setBackground(java.awt.Color.WHITE);
        jComboBox1.setForeground(PRIMARY_COLOR);
        btnHanhKhach.setBackground(java.awt.Color.WHITE);
        btnHanhKhach.setForeground(PRIMARY_COLOR);

        jButton2.setBackground(SECONDARY_COLOR); 
        jButton2.setForeground(java.awt.Color.WHITE);
    }  

    private void initKetQuaPanel() {
        if (scrollPaneKetQua != null) return;
        
        pnlKetQuaContainer = new JPanel();
        pnlKetQuaContainer.setLayout(new BoxLayout(pnlKetQuaContainer, BoxLayout.Y_AXIS));
        pnlKetQuaContainer.setOpaque(false);
        
        JPanel pnlGridHeader = new JPanel(new GridLayout(1, 5, 2, 0));
        pnlGridHeader.setBackground(new Color(18, 32, 64)); 
        pnlGridHeader.setPreferredSize(new Dimension(1200, 45));
        pnlGridHeader.setMaximumSize(new Dimension(1200, 45));
        
        JLabel lbl1 = new JLabel("Chuyến bay", SwingConstants.CENTER);
        JLabel lbl2 = new JLabel("Phổ thông (ECO)", SwingConstants.CENTER);
        JLabel lbl3 = new JLabel("Phổ thông ĐB (PECO)", SwingConstants.CENTER);
        JLabel lbl4 = new JLabel("Thương gia (BUS)", SwingConstants.CENTER);
        JLabel lbl5 = new JLabel("Hạng nhất (FST)", SwingConstants.CENTER);
        
        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
        lbl1.setFont(headerFont); lbl1.setForeground(Color.WHITE);
        lbl2.setFont(headerFont); lbl2.setForeground(Color.WHITE);
        lbl3.setFont(headerFont); lbl3.setForeground(Color.WHITE);
        lbl4.setFont(headerFont); lbl4.setForeground(Color.WHITE);
        lbl5.setFont(headerFont); lbl5.setForeground(Color.WHITE);
        
        pnlGridHeader.add(lbl1); pnlGridHeader.add(lbl2); pnlGridHeader.add(lbl3); pnlGridHeader.add(lbl4); pnlGridHeader.add(lbl5);
        
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.add(pnlGridHeader, BorderLayout.NORTH);
        pnlWrapper.add(pnlKetQuaContainer, BorderLayout.CENTER);
        
        scrollPaneKetQua = new JScrollPane(pnlWrapper);
        scrollPaneKetQua.setOpaque(false); scrollPaneKetQua.getViewport().setOpaque(false);
        scrollPaneKetQua.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        scrollPaneKetQua.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneKetQua.setPreferredSize(new Dimension(1200, 380)); 
        scrollPaneKetQua.setMaximumSize(new Dimension(1200, 380));
        scrollPaneKetQua.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneKetQua.getVerticalScrollBar().setUnitIncrement(16);
        
        pnlStickyFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 15));
        pnlStickyFooter.setBackground(Color.WHITE);
        pnlStickyFooter.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)));
        pnlStickyFooter.setVisible(false);
        
        JPanel pnlTotalText = new JPanel(new GridLayout(2, 1));
        pnlTotalText.setOpaque(false);
        JLabel lblTo = new JLabel("Tổng tiền vé:", SwingConstants.RIGHT);
        lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblFooterTotal = new JLabel("0 VNĐ", SwingConstants.RIGHT);
        lblFooterTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlTotalText.add(lblTo); pnlTotalText.add(lblFooterTotal);
        
        btnFooterNext = new JButton("Đi tiếp");
        btnFooterNext.setBackground(new Color(255, 193, 7));
        btnFooterNext.setForeground(new Color(18, 32, 64));
        btnFooterNext.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnFooterNext.setPreferredSize(new Dimension(200, 45));
        btnFooterNext.setFocusPainted(false);
        btnFooterNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        pnlStickyFooter.add(pnlTotalText);
        pnlStickyFooter.add(btnFooterNext);
        
        btnFooterNext.addActionListener(e -> processToNextStep());
    }

    private void updateFooterTotal() {
        BigDecimal tong = giaVeDi.add(giaVeVe);
        if (tong.compareTo(BigDecimal.ZERO) > 0) {
            pnlStickyFooter.setVisible(true);
            lblFooterTotal.setText(String.format("%,d VNĐ", tong.longValue()));
            
            if (jCheckBox1.isSelected() && !isSearchingVe) {
                btnFooterNext.setText("Đi tiếp");
            } else {
                btnFooterNext.setText("Tiếp tục");
            }
        } else {
            pnlStickyFooter.setVisible(false);
            lblFooterTotal.setText("0 VNĐ"); 
        }
        pnlContentWrapper.revalidate();
    }

    private void switchPanel(JPanel newPanel) {
        pnlContentWrapper.removeAll();
        newPanel.setOpaque(false); 
        pnlContentWrapper.add(newPanel, BorderLayout.CENTER);
        pnlContentWrapper.revalidate();
        pnlContentWrapper.repaint();
    }

    private void processToNextStep() {
        if (jCheckBox1.isSelected()) {
            if (!isSearchingVe) {
                isSearchingVe = true;
                setActiveTab(btnTabVe, btnTabDi);
                updateSearchUI();
                return;
            } else if (cbSelectedVe == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cho chuyến về!");
                return;
            }
        }

        model.DatVeSession newSession = new model.DatVeSession();
        newSession.maNguoiDung = (userHienTai != null) ? userHienTai.getMaNguoiDung() : "KHACH_LE"; 
        
        newSession.maChuyenBay = cbSelectedDi.getMaChuyenBay();
        newSession.maHangVe = hangVeSelectedDi; 
        
        if (jCheckBox1.isSelected() && cbSelectedVe != null) {
            newSession.maChuyenBayVe = cbSelectedVe.getMaChuyenBay();
            newSession.maHangVeVe = hangVeSelectedVe;
            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            newSession.thoiGianVe = cbSelectedVe.getNgayGioDi().format(timeFmt) + " (" + cbSelectedVe.getNgayGioDi().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";
        }
        
        newSession.soNguoiLon = MainFrame.this.soNL;
        newSession.soTreEm = MainFrame.this.soTE;
        newSession.soEmBe = MainFrame.this.soEB;
        newSession.loaiVe = jCheckBox1.isSelected() ? "Khứ hồi" : "Một chiều"; 
        
        model.SanBay sbDi = (model.SanBay) cbCities1.getSelectedItem();
        model.SanBay sbDen = (model.SanBay) cbCities.getSelectedItem();
        newSession.tenSanBayDi = sbDi.getTenSanBay();
        newSession.tenSanBayDen = sbDen.getTenSanBay();
        
        java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        newSession.thoiGianDi = cbSelectedDi.getNgayGioDi().format(timeFmt) + " (" + cbSelectedDi.getNgayGioDi().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";

        switchPanel(new gui.user.PanelUserVeBan(newSession));
    }
    
    private void setupCurrencyComboBox() {
        javax.swing.JComboBox rawCombo = cbDonViTienTe;
        rawCombo.removeAllItems();

        rawCombo.addItem(new CurrencyItem("VND | VI", "svgmaterials/flags/1x1/vn.svg"));
        rawCombo.addItem(new CurrencyItem("USD | EN", "svgmaterials/flags/1x1/us.svg"));
        
        rawCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                javax.swing.JLabel label = (javax.swing.JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
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

    class CurrencyItem {
        private String text;
        private javax.swing.Icon icon; 

        public CurrencyItem(String text, String iconPath) {
            this.text = text;
            try {
                com.formdev.flatlaf.extras.FlatSVGIcon svgIcon = new com.formdev.flatlaf.extras.FlatSVGIcon(iconPath, 24, 18);
                if (svgIcon.hasFound()) this.icon = svgIcon;
                else this.icon = null;
            } catch (Exception e) { this.icon = null; }
        }
        public String getText() { return text; }
        public javax.swing.Icon getIcon() { return icon; }
        @Override public String toString() { return text; } 
    }  

    private void setupHanhKhachPopup() {
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
        btnHanhKhach.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        javax.swing.JPanel pnl = new javax.swing.JPanel(new java.awt.GridLayout(3, 2, 10, 10));
        pnl.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        javax.swing.JSpinner spnNL = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 9, 1));
        javax.swing.JSpinner spnTE = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(0, 0, 9, 1));
        javax.swing.JSpinner spnEB = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(0, 0, 9, 1));

        pnl.add(new javax.swing.JLabel("Người lớn (>=12t):")); pnl.add(spnNL);
        pnl.add(new javax.swing.JLabel("Trẻ em (2-11t):")); pnl.add(spnTE);
        pnl.add(new javax.swing.JLabel("Em bé (<2t):")); pnl.add(spnEB);
        popup.add(pnl);

        javax.swing.event.ChangeListener updateText = e -> {
            this.soNL = (int) spnNL.getValue();
            this.soTE = (int) spnTE.getValue();
            this.soEB = (int) spnEB.getValue();
            btnHanhKhach.setText(soNL + " Người lớn, " + soTE + " Trẻ em, " + soEB + " Em bé");
            
            giaVeDi = BigDecimal.ZERO; 
            giaVeVe = BigDecimal.ZERO; 
            cbSelectedDi = null; 
            cbSelectedVe = null; 
            hangVeSelectedDi = ""; 
            hangVeSelectedVe = "";
            updateFooterTotal();
            
            if (pnlKetQuaContainer != null && pnlKetQuaContainer.getComponentCount() > 0) {
                updateSearchUI();
            }
        };

        spnNL.addChangeListener(updateText);
        spnTE.addChangeListener(updateText);
        spnEB.addChangeListener(updateText);
        
        btnHanhKhach.addActionListener(e -> {
            pnl.setPreferredSize(new java.awt.Dimension(btnHanhKhach.getWidth(), pnl.getPreferredSize().height));
            popup.show(btnHanhKhach, 0, btnHanhKhach.getHeight());
        });
    }
    
    private java.time.LocalDate currentMonth = java.time.LocalDate.now();
    private java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd / MM / yyyy");
    private java.util.List<javax.swing.JTextField> listNgayExtra = new java.util.ArrayList<>();
    private java.util.List<javax.swing.JButton> listBtnXoa = new java.util.ArrayList<>(); 
    private java.util.List<model.SanBay> danhSachSanBayGoc = new java.util.ArrayList<>();
    private boolean isUpdatingCombo = false; 
    private javax.swing.JButton btnSearchMulti;  
    private javax.swing.JButton btnMoreCities; 

    private void loadDanhSachSanBay() {
        danhSachSanBayGoc = new bll.SanBayBUS().getAllSanBay();
    }

    private void setupHoTroChonSanBay(javax.swing.JComboBox cbTu, javax.swing.JComboBox cbDen, javax.swing.JButton btnSwap) {
        capNhatHaiComboBox(cbTu, cbDen, null, null);
        java.awt.event.ItemListener listener = e -> {
            if (isUpdatingCombo) return;
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                model.SanBay selTu = (model.SanBay) cbTu.getSelectedItem();
                model.SanBay selDen = (model.SanBay) cbDen.getSelectedItem();
                capNhatHaiComboBox(cbTu, cbDen, selTu, selDen);
            }
        };
        cbTu.addItemListener(listener);
        cbDen.addItemListener(listener);

        btnSwap.addActionListener(e -> {
            if (isUpdatingCombo) return;
            model.SanBay selTu = (model.SanBay) cbTu.getSelectedItem();
            model.SanBay selDen = (model.SanBay) cbDen.getSelectedItem();
            capNhatHaiComboBox(cbTu, cbDen, selDen, selTu); 
        });
    }

    private void capNhatHaiComboBox(javax.swing.JComboBox cbTu, javax.swing.JComboBox cbDen, model.SanBay selTu, model.SanBay selDen) {
        isUpdatingCombo = true;
        cbTu.removeAllItems();
        cbTu.addItem(new model.SanBay("", "", "", "Chọn thành phố", model.SanBay.TrangThai.HOAT_DONG));
        for (model.SanBay sb : danhSachSanBayGoc) {
            if (selDen == null || selDen.getMaSanBay().isEmpty() || !sb.getMaSanBay().equals(selDen.getMaSanBay())) {
                cbTu.addItem(sb);
            }
        }
        setComboSelection(cbTu, selTu);
        
        cbDen.removeAllItems();
        cbDen.addItem(new model.SanBay("", "", "", "Chọn thành phố", model.SanBay.TrangThai.HOAT_DONG));
        for (model.SanBay sb : danhSachSanBayGoc) {
            if (selTu == null || selTu.getMaSanBay().isEmpty() || !sb.getMaSanBay().equals(selTu.getMaSanBay())) {
                cbDen.addItem(sb);
            }
        }
        setComboSelection(cbDen, selDen);
        isUpdatingCombo = false;
    }
    
    private void setComboSelection(javax.swing.JComboBox cb, model.SanBay target) {
        if (target == null || target.getMaSanBay().isEmpty()) { cb.setSelectedIndex(0); return; }
        for (int i = 0; i < cb.getItemCount(); i++) {
            model.SanBay item = (model.SanBay) cb.getItemAt(i);
            if (item.getMaSanBay().equals(target.getMaSanBay())) { cb.setSelectedIndex(i); return; }
        }
        cb.setSelectedIndex(0);
    }
    
    private void setupDatePicker(javax.swing.JTextField txtField) {
        txtField.setEditable(false); 
        txtField.setBackground(java.awt.Color.WHITE);
        txtField.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        txtField.setText(java.time.LocalDate.now().format(dateFormatter));
        txtField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!txtField.isEnabled()) return; 
                hienThiLich(txtField);
            }
        });
    }

    private void hienThiLich(javax.swing.JTextField txtField) {
        try { currentMonth = java.time.LocalDate.parse(txtField.getText(), dateFormatter); } 
        catch (Exception ex) { currentMonth = java.time.LocalDate.now(); }

        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu(); 
        popup.setLightWeightPopupEnabled(false);
        javax.swing.JPanel pnlMain = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlMain.setBackground(java.awt.Color.WHITE);
        pnlMain.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        
        javax.swing.JPanel pnlHeader = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 5));
        pnlHeader.setBackground(java.awt.Color.WHITE);
        
        javax.swing.JButton btnPrev = new javax.swing.JButton("<");
        javax.swing.JButton btnNext = new javax.swing.JButton(">");
        btnPrev.setFocusPainted(false); btnPrev.setBackground(java.awt.Color.WHITE);
        btnNext.setFocusPainted(false); btnNext.setBackground(java.awt.Color.WHITE);
        
        javax.swing.JLabel lblMonth = new javax.swing.JLabel("Tháng " + currentMonth.getMonthValue());
        lblMonth.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblMonth.setPreferredSize(new java.awt.Dimension(70, 25));
        lblMonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); 

        javax.swing.JSpinner spnYear = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(currentMonth.getYear(), 2024, 2100, 1));
        spnYear.setEditor(new javax.swing.JSpinner.NumberEditor(spnYear, "#"));
        
        pnlHeader.add(btnPrev); pnlHeader.add(lblMonth); pnlHeader.add(spnYear); pnlHeader.add(btnNext);
        pnlMain.add(pnlHeader, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel pnlDays = new javax.swing.JPanel(new java.awt.GridLayout(0, 7, 0, 0));
        pnlDays.setBackground(java.awt.Color.WHITE);
        String[] daysOfWeek = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};

        final boolean[] isUpdatingUI = {false};

        Runnable drawDays = () -> {
            pnlDays.removeAll();
            isUpdatingUI[0] = true;
            lblMonth.setText("Tháng " + currentMonth.getMonthValue());
            spnYear.setValue(currentMonth.getYear());
            isUpdatingUI[0] = false;

            for (String d : daysOfWeek) {
                javax.swing.JLabel lbl = new javax.swing.JLabel(d, javax.swing.SwingConstants.CENTER);
                lbl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
                lbl.setForeground(java.awt.Color.GRAY);
                pnlDays.add(lbl);
            }
            
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate minDate = today; 
            
            javax.swing.JTextField txtPhuThuoc = null;
            int soNgayCach = 0;

            if (txtField == jFormattedTextField2) {
                txtPhuThuoc = jFormattedTextField1; 
                soNgayCach = 0; 
            } else if (listNgayExtra.contains(txtField)) {
                int index = listNgayExtra.indexOf(txtField);
                txtPhuThuoc = (index == 0) ? null : listNgayExtra.get(index - 1);
                soNgayCach = (index == 0) ? 0 : 1;
            }

            if (txtPhuThuoc != null) {
                try {
                    minDate = java.time.LocalDate.parse(txtPhuThuoc.getText(), dateFormatter).plusDays(soNgayCach); 
                } catch (Exception ex) { minDate = today; }
            }

            int daysInMonth = currentMonth.lengthOfMonth();
            java.time.LocalDate prevMonthDate = currentMonth.minusMonths(1);
            java.time.LocalDate nextMonthDate = currentMonth.plusMonths(1);
            int startDay = currentMonth.withDayOfMonth(1).getDayOfWeek().getValue(); 
            if (startDay == 7) startDay = 0; 

            for (int i = 0; i < 42; i++) {
                java.time.LocalDate cellDate;
                boolean isCurrentMonth = false;

                if (i < startDay) {
                    cellDate = java.time.LocalDate.of(prevMonthDate.getYear(), prevMonthDate.getMonthValue(), prevMonthDate.lengthOfMonth() - startDay + 1 + i);
                } else if (i < startDay + daysInMonth) {
                    cellDate = java.time.LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), i - startDay + 1);
                    isCurrentMonth = true;
                } else {
                    cellDate = java.time.LocalDate.of(nextMonthDate.getYear(), nextMonthDate.getMonthValue(), i - (startDay + daysInMonth) + 1);
                }

                javax.swing.JButton btnDay = new javax.swing.JButton(String.valueOf(cellDate.getDayOfMonth()));
                btnDay.setMargin(new java.awt.Insets(5, 5, 5, 5));
                btnDay.setFocusPainted(false); btnDay.setBorderPainted(false);
                btnDay.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
                
                if (cellDate.isBefore(minDate)) {
                    btnDay.setEnabled(false);
                    btnDay.setBackground(java.awt.Color.WHITE);
                    btnDay.setForeground(new java.awt.Color(220, 220, 220)); 
                } else {
                    java.time.LocalDate selectedDate = null;
                    try { selectedDate = java.time.LocalDate.parse(txtField.getText(), dateFormatter); } catch(Exception e){}

                        if (!isCurrentMonth) {
                            btnDay.setForeground(new java.awt.Color(160, 160, 160));
                            btnDay.setBackground(new java.awt.Color(250, 250, 250));
                        } else if (selectedDate != null && cellDate.isEqual(selectedDate)) {
                            btnDay.setBackground(new java.awt.Color(18, 32, 64)); 
                            btnDay.setForeground(java.awt.Color.WHITE);
                            btnDay.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                        } else if (cellDate.isEqual(today)) {
                            btnDay.setBackground(new java.awt.Color(220, 240, 255));
                            btnDay.setForeground(java.awt.Color.BLUE);
                        } else {
                            btnDay.setBackground(java.awt.Color.WHITE);
                            btnDay.setForeground(java.awt.Color.BLACK);
                        }
                    
                    btnDay.addActionListener(ev -> {
                        txtField.setText(cellDate.format(dateFormatter));
                        popup.setVisible(false); 
                        
                        if (txtField == jFormattedTextField1 && jCheckBox1.isSelected()) {
                            try {
                                java.time.LocalDate currentReturn = java.time.LocalDate.parse(jFormattedTextField2.getText(), dateFormatter);
                                if (currentReturn.isBefore(cellDate)) { 
                                    jFormattedTextField2.setText(cellDate.format(dateFormatter));
                                }
                            } catch (Exception ex) {}
                        } else {
                            int startIndex = (txtField == jFormattedTextField1) ? 0 : (listNgayExtra.indexOf(txtField) + 1);
                            if (startIndex >= 0) {
                                java.time.LocalDate prevDate = cellDate;
                                for (int k = startIndex; k < listNgayExtra.size(); k++) {
                                    javax.swing.JTextField txtSau = listNgayExtra.get(k);
                                    try {
                                        java.time.LocalDate dateSau = java.time.LocalDate.parse(txtSau.getText(), dateFormatter);
                                        if (dateSau.isBefore(prevDate.plusDays(1))) {
                                            prevDate = prevDate.plusDays(1);
                                            txtSau.setText(prevDate.format(dateFormatter));
                                        } else {
                                            prevDate = dateSau;
                                        }
                                    } catch (Exception ex) {}
                                }
                            }
                        }
                    });
                }
                pnlDays.add(btnDay);
            }
            pnlMain.revalidate(); pnlMain.repaint();
        };
        
        spnYear.addChangeListener(ev -> { if (!isUpdatingUI[0]) { currentMonth = currentMonth.withYear((Integer) spnYear.getValue()); drawDays.run(); }});
        btnPrev.addActionListener(ev -> { currentMonth = currentMonth.minusMonths(1); drawDays.run(); });
        btnNext.addActionListener(ev -> { currentMonth = currentMonth.plusMonths(1); drawDays.run(); });

        drawDays.run();
        pnlMain.add(pnlDays, java.awt.BorderLayout.CENTER); popup.add(pnlMain);
        popup.show(txtField, 0, txtField.getHeight());
    }

    private void taoHangChuyenBayMoi() {
        if (listNgayExtra.size() >= 5) { 
            javax.swing.JOptionPane.showMessageDialog(this, "Chỉ được đặt tối đa 5 chuyến bay cùng lúc!");
            return;
        }

        javax.swing.JPanel pnlRow = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 10));
        pnlRow.setOpaque(false); 
        pnlRow.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 65));
        javax.swing.JComboBox cbTuEx = new javax.swing.JComboBox<>();
        javax.swing.JComboBox cbDenEx = new javax.swing.JComboBox<>();
        javax.swing.JFormattedTextField txtNgayEx = new javax.swing.JFormattedTextField();
        javax.swing.JButton btnXoa = new javax.swing.JButton("Xóa");
        javax.swing.JButton btnSwapEx = new javax.swing.JButton("<=>"); 

        cbTuEx.setPreferredSize(new java.awt.Dimension(232, 56));
        cbDenEx.setPreferredSize(new java.awt.Dimension(232, 56));
        txtNgayEx.setPreferredSize(new java.awt.Dimension(150, 56));
        btnXoa.setPreferredSize(new java.awt.Dimension(70, 56));
        
        btnSwapEx.setPreferredSize(new java.awt.Dimension(60, 56));
        btnSwapEx.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        try { btnSwapEx.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon("svgmaterials/icons/bx-transfer.svg", 24, 24)); btnSwapEx.setText(""); } catch(Exception ex){}

        btnXoa.setBackground(new java.awt.Color(255, 100, 100));
        btnXoa.setForeground(java.awt.Color.WHITE);
        btnXoa.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16)); 
        btnSwapEx.setBackground(new java.awt.Color(45, 72, 140));
        btnSwapEx.setForeground(java.awt.Color.WHITE);

        btnXoa.setBackground(new java.awt.Color(244, 67, 54)); 
        btnXoa.setForeground(java.awt.Color.WHITE);

        cbTuEx.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        cbDenEx.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        txtNgayEx.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));

        setupHoTroChonSanBay(cbTuEx, cbDenEx, btnSwapEx);
        setupDatePicker(txtNgayEx); 

        javax.swing.JTextField txtTruocDo = listNgayExtra.isEmpty() ? jFormattedTextField1 : listNgayExtra.get(listNgayExtra.size() - 1);
        try {
            java.time.LocalDate ngayTruoc = java.time.LocalDate.parse(txtTruocDo.getText(), dateFormatter);
            txtNgayEx.setText(ngayTruoc.plusDays(1).format(dateFormatter));
        } catch(Exception e) {}

        listNgayExtra.add(txtNgayEx);
        listBtnXoa.add(btnXoa);

        btnXoa.addActionListener(e -> {
            pnlMultiCities.remove(pnlRow);
            listNgayExtra.remove(txtNgayEx);
            listBtnXoa.remove(btnXoa);
            capNhatGiaoDienMultiCity(); 
        });

        javax.swing.JLabel lblTu = new javax.swing.JLabel("Từ");
        lblTu.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        javax.swing.JLabel lblDen = new javax.swing.JLabel("Đến");
        lblDen.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        javax.swing.JLabel lblNgay = new javax.swing.JLabel("Ngày đi");
        lblNgay.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));

        pnlRow.add(lblTu); pnlRow.add(cbTuEx);
        pnlRow.add(btnSwapEx); 
        pnlRow.add(lblDen); pnlRow.add(cbDenEx);
        pnlRow.add(lblNgay); pnlRow.add(txtNgayEx);
        pnlRow.add(btnXoa);

        pnlMultiCities.add(pnlRow, pnlMultiCities.getComponentCount() - 1);
        capNhatGiaoDienMultiCity();
    }

    private void capNhatGiaoDienMultiCity() { 
        boolean hienNutXoa = listBtnXoa.size() > 2; 
        for (javax.swing.JButton btn : listBtnXoa) { btn.setVisible(hienNutXoa); } 
        pnlMultiCities.revalidate(); pnlMultiCities.repaint();
        if (this.getExtendedState() != java.awt.Frame.MAXIMIZED_BOTH) { this.pack(); } 
        else { this.revalidate(); this.repaint(); }
    } 

    private void setupLogo() {
        jLabel1.setText(""); 
        try {
            javax.swing.ImageIcon originalIcon = new javax.swing.ImageIcon("src/resources/images/logos.png");
            if (originalIcon.getIconWidth() > 0) {
                java.awt.Image scaledImg = originalIcon.getImage().getScaledInstance(-1, 80, java.awt.Image.SCALE_SMOOTH);
                jLabel1.setIcon(new javax.swing.ImageIcon(scaledImg));
            } else {
                jLabel1.setText("AirLiner");
            }
        } catch (Exception e) {
            jLabel1.setText("AirLiner");
        } 

        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); 
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (userHienTai != null) new MainFrame(userHienTai).setVisible(true);
                else new MainFrame().setVisible(true);
                dispose(); 
            }
        });
    }

    private void setupBusinessLogic() { 
        jComboBox1.setVisible(false); 
        jFormattedTextField2.setEnabled(false); 
        loadDanhSachSanBay(); 
        setupHoTroChonSanBay(cbCities, cbCities1, jButton2);

        btnGChuyenBay.add(jRadioButton1); 
        btnGChuyenBay.add(jRadioButton2);
        jRadioButton1.setSelected(true);

        pnlMultiCities.setLayout(new javax.swing.BoxLayout(pnlMultiCities, javax.swing.BoxLayout.Y_AXIS));
        pnlMultiCities.setVisible(false); 
        pnlMultiCities.setPreferredSize(null); 
        pnlMultiCities.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); 
    
        java.util.function.Consumer<Boolean> anHienHangGoc = (isHien) -> {
            jLabel2.setVisible(isHien);  
            jLabel3.setVisible(isHien); 
            cbCities.setVisible(isHien);     
            jButton2.setVisible(isHien);     
            cbCities1.setVisible(isHien);     
            jLabel4.setVisible(isHien);      
            jFormattedTextField1.setVisible(isHien); 
            jCheckBox1.setVisible(isHien);   
            jFormattedTextField2.setVisible(isHien); 
            jButton3.setVisible(isHien);    
        };

        javax.swing.JPanel pnlButtonWrapper = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 10));
        pnlButtonWrapper.setOpaque(false);
        pnlButtonWrapper.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 65));

        pnlMultiCities.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        btnMoreCities = new javax.swing.JButton("(+) Thêm chuyến bay khác");
        btnMoreCities.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        btnMoreCities.setPreferredSize(new java.awt.Dimension(300, 45)); 

        btnSearchMulti = new javax.swing.JButton("Tìm kiếm");
        btnSearchMulti.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        btnSearchMulti.setPreferredSize(new java.awt.Dimension(200, 45));  

        btnMoreCities.setBackground(new java.awt.Color(245, 247, 250)); 
        btnMoreCities.setForeground(new java.awt.Color(18, 32, 64)); 

        btnSearchMulti.setBackground(new java.awt.Color(255, 193, 7)); 
        btnSearchMulti.setForeground(new java.awt.Color(18, 32, 64)); 

        pnlButtonWrapper.add(btnMoreCities);
        pnlButtonWrapper.add(btnSearchMulti);
   
        jRadioButton2.addActionListener(e -> {
            if(jRadioButton2.isSelected()) {
                anHienHangGoc.accept(false); 
                pnlMultiCities.removeAll(); 
                listNgayExtra.clear();
                listBtnXoa.clear();
                pnlMultiCities.setOpaque(true);
                pnlMultiCities.setBackground(jPanel3.getBackground());
                pnlMultiCities.add(pnlButtonWrapper);
                pnlMultiCities.setVisible(true);
                taoHangChuyenBayMoi(); 
                taoHangChuyenBayMoi(); 
            }
        });
        
        jRadioButton1.addActionListener(e -> {
            if(jRadioButton1.isSelected()) {
                anHienHangGoc.accept(true); 
                jCheckBox1.setSelected(false);
                jFormattedTextField2.setEnabled(false);
                pnlMultiCities.setVisible(false);
            }
        });

        for (java.awt.event.ActionListener al : btnMoreCities.getActionListeners()) { btnMoreCities.removeActionListener(al); }
        btnMoreCities.addActionListener(e -> taoHangChuyenBayMoi());

        pnlTabsKhuHoi = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
        pnlTabsKhuHoi.setBackground(new java.awt.Color(245, 247, 250));
        btnTabDi = new javax.swing.JButton("CHUYẾN ĐI");
        btnTabVe = new javax.swing.JButton("CHUYẾN VỀ");
        
        btnTabDi.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        btnTabVe.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        btnTabDi.setPreferredSize(new java.awt.Dimension(350, 45));
        btnTabVe.setPreferredSize(new java.awt.Dimension(350, 45));
        btnTabDi.setFocusPainted(false);
        btnTabVe.setFocusPainted(false);
        
        pnlTabsKhuHoi.add(btnTabDi);
        pnlTabsKhuHoi.add(btnTabVe);
        pnlTabsKhuHoi.setVisible(false); 
        
        pnlDateRibbon = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 5));
        pnlDateRibbon.setBackground(new java.awt.Color(245, 247, 250));
        
        btnTabDi.addActionListener(e -> {
            isSearchingVe = false;
            setActiveTab(btnTabDi, btnTabVe);
            updateSearchUI();
        });

        btnTabVe.addActionListener(e -> {
            isSearchingVe = true;
            setActiveTab(btnTabVe, btnTabDi);
            updateSearchUI(); 
        });

        pnlContent.setLayout(new javax.swing.BoxLayout(pnlContent, javax.swing.BoxLayout.Y_AXIS));

        int contentWidth = 1200;
        
        jPanel3.setMaximumSize(new java.awt.Dimension(contentWidth, jPanel3.getPreferredSize().height)); 
        pnlMultiCities.setMaximumSize(new java.awt.Dimension(contentWidth, Integer.MAX_VALUE)); 
        jPanel3.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        pnlMultiCities.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        pnlContent.add(javax.swing.Box.createVerticalStrut(20)); 
        pnlContent.add(jPanel3);          
        pnlContent.add(javax.swing.Box.createVerticalStrut(10));
        
        pnlContent.add(pnlTabsKhuHoi); 
        pnlContent.add(javax.swing.Box.createVerticalStrut(5));
        pnlContent.add(pnlDateRibbon); 
        pnlContent.add(javax.swing.Box.createVerticalStrut(10));
        
        pnlContent.add(scrollPaneKetQua);                  
        pnlContent.add(pnlMultiCities);      
        
        pnlContentWrapper.add(pnlStickyFooter, BorderLayout.SOUTH);
        pnlContent.revalidate();
        pnlContent.repaint();
    
        jButton3.addActionListener(e -> {
            try {
                currentSearchDateDi = java.time.LocalDate.parse(jFormattedTextField1.getText(), dateFormatter);
                if (jCheckBox1.isSelected()) {
                    currentSearchDateVe = java.time.LocalDate.parse(jFormattedTextField2.getText(), dateFormatter);
                }
                
                isSearchingVe = false; 
                if (jCheckBox1.isSelected()) {
                    pnlTabsKhuHoi.setVisible(true);
                    btnTabDi.setText("CHUYẾN ĐI (" + jFormattedTextField1.getText() + ")");
                    btnTabVe.setText("CHUYẾN VỀ (" + jFormattedTextField2.getText() + ")");
                    setActiveTab(btnTabDi, btnTabVe);
                } else {
                    pnlTabsKhuHoi.setVisible(false);
                }
                
                updateSearchUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!");
            }
        });
    }   

    private void setActiveTab(javax.swing.JButton active, javax.swing.JButton inactive) {
        active.setBackground(new java.awt.Color(18, 32, 64)); 
        active.setForeground(java.awt.Color.WHITE);
        inactive.setBackground(new java.awt.Color(220, 220, 220)); 
        inactive.setForeground(new java.awt.Color(100, 100, 100));
    }

    private void renderDateRibbon(java.time.LocalDate centerDate) {
        pnlDateRibbon.removeAll();
        java.time.format.DateTimeFormatter dayFmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
        java.time.format.DateTimeFormatter dowFmt = java.time.format.DateTimeFormatter.ofPattern("E", new java.util.Locale("vi", "VN"));

        for (int i = -2; i <= 2; i++) {
            java.time.LocalDate d = centerDate.plusDays(i);
            if (d.isBefore(java.time.LocalDate.now())) continue; 

            javax.swing.JButton btnDay = new javax.swing.JButton();
            btnDay.setPreferredSize(new java.awt.Dimension(140, 55));
            btnDay.setFocusPainted(false);
            
            boolean isSelected = d.isEqual(centerDate);
            if (isSelected) {
                btnDay.setBackground(new java.awt.Color(255, 193, 7)); 
                btnDay.setForeground(new java.awt.Color(18, 32, 64)); 
                btnDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(18, 32, 64), 2));
            } else {
                btnDay.setBackground(java.awt.Color.WHITE);
                btnDay.setForeground(java.awt.Color.DARK_GRAY);
                btnDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220), 1));
            }

            btnDay.setText("<html><center><b>" + d.format(dowFmt) + "</b><br>" + d.format(dayFmt) + "</center></html>");
            btnDay.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            
            btnDay.addActionListener(e -> {
                if (isSearchingVe) currentSearchDateVe = d;
                else currentSearchDateDi = d;
                updateSearchUI();
            });

            pnlDateRibbon.add(btnDay);
        }
        pnlDateRibbon.revalidate();
        pnlDateRibbon.repaint();
    }

    private void updateSearchUI() {
        model.SanBay sbDi = (model.SanBay) cbCities1.getSelectedItem();
        model.SanBay sbDen = (model.SanBay) cbCities.getSelectedItem();
        
        if (sbDi == null || sbDen == null || sbDi.getMaSanBay().isEmpty() || sbDen.getMaSanBay().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Điểm đi và Điểm đến!");
            return;
        }

        java.time.LocalDate dateToSearch = isSearchingVe ? currentSearchDateVe : currentSearchDateDi;
        String from = isSearchingVe ? sbDen.getMaSanBay() : sbDi.getMaSanBay();
        String to = isSearchingVe ? sbDi.getMaSanBay() : sbDen.getMaSanBay();
        String fromName = isSearchingVe ? sbDen.getTenSanBay() : sbDi.getTenSanBay();
        String toName = isSearchingVe ? sbDi.getTenSanBay() : sbDen.getTenSanBay();

        renderDateRibbon(dateToSearch);
        renderMatrixFlights(dateToSearch, from, to, fromName, toName);
    }

    private void renderMatrixFlights(java.time.LocalDate searchDate, String maSanBayDi, String maSanBayDen, String tenSanBayDi, String tenSanBayDen) {
        pnlKetQuaContainer.removeAll(); 
        
        bll.ChuyenBayBUS cbBUS = new bll.ChuyenBayBUS();
        String ngayChuan = searchDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        java.util.ArrayList<model.ChuyenBay> dsCB = cbBUS.searchChuyenBay(maSanBayDi, maSanBayDen, ngayChuan);

        if (dsCB == null || dsCB.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không tìm thấy chuyến bay nào trong ngày này.", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lblEmpty.setBorder(new EmptyBorder(50, 0, 50, 0));
            pnlKetQuaContainer.add(lblEmpty);
            pnlKetQuaContainer.revalidate();
            pnlKetQuaContainer.repaint();
            return;
        }

        for (model.ChuyenBay cb : dsCB) {
            pnlKetQuaContainer.add(new FlightCard(cb, cbBUS, maSanBayDi, maSanBayDen, tenSanBayDi, tenSanBayDen));
        }
        
        pnlKetQuaContainer.revalidate();
        pnlKetQuaContainer.repaint();
    }

    class FlightCard extends JPanel {
        private JPanel pnlDetails;
        
        public FlightCard(model.ChuyenBay cb, bll.ChuyenBayBUS cbBUS, String sbDi, String sbDen, String tenSanBayDi, String tenSanBayDen) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220))); 
            
            JPanel pnlMainRow = new JPanel(new GridLayout(1, 5, 2, 0));
            pnlMainRow.setBackground(Color.WHITE);
            pnlMainRow.setPreferredSize(new Dimension(1200, 80));
            pnlMainRow.setMaximumSize(new Dimension(1200, 80));

            FlightInfoCell cellInfo = new FlightInfoCell(cb);
            pnlMainRow.add(cellInfo);

            PriceCell cellEco = new PriceCell(cb, cbBUS, "ECO", "#4CAF50", "Phổ thông", new Color(240, 255, 240));
            PriceCell cellPeco = new PriceCell(cb, cbBUS, "PECO", "#2196F3", "Phổ thông ĐB", new Color(240, 248, 255));
            PriceCell cellBus = new PriceCell(cb, cbBUS, "BUS", "#F44336", "Thương gia", new Color(255, 240, 240));
            PriceCell cellFst = new PriceCell(cb, cbBUS, "FST", "#FFC107", "Hạng nhất", new Color(255, 253, 230));

            pnlMainRow.add(cellEco); pnlMainRow.add(cellPeco); pnlMainRow.add(cellBus); pnlMainRow.add(cellFst);

            pnlDetails = new JPanel(new BorderLayout());
            pnlDetails.setVisible(false);
            pnlDetails.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(18, 32, 64)),
                new EmptyBorder(15, 20, 15, 20)
            ));

            JLabel lblDetailsContent = new JLabel();
            lblDetailsContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pnlDetails.add(lblDetailsContent, BorderLayout.CENTER);

            add(pnlMainRow);
            add(pnlDetails);

            long totalMinutes = java.time.Duration.between(cb.getNgayGioDi(), cb.getNgayGioDen()).toMinutes();
            String durationStr = (totalMinutes / 60) + " giờ " + (totalMinutes % 60) + " phút";
            java.time.format.DateTimeFormatter dateFmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

            String htmlFlightInfo = "<html><div style='padding: 5px;'>" +
                "<b style='color:#dc2626; font-size:16px;'> Chuyến bay: " + cb.getMaChuyenBay() + "</b><br><br>" +
                "<table cellpadding='4'><tr><td valign='top'><b style='color:#122040;'>Khởi hành:</b></td><td><b style='color:black; font-size:14px;'>" + cb.getNgayGioDi().format(timeFmt) + ", " + cb.getNgayGioDi().format(dateFmt) + "</b><br>" + tenSanBayDi + " (" + sbDi + ")</td></tr>" +
                "<tr><td valign='top'><b style='color:#122040;'>Đến:</b></td><td><b style='color:black; font-size:14px;'>" + cb.getNgayGioDen().format(timeFmt) + ", " + cb.getNgayGioDen().format(dateFmt) + "</b><br>" + tenSanBayDen + " (" + sbDen + ")</td></tr></table><br>" +
                "<span style='color:#d97706; font-weight:bold; font-size:13px;'>Thời gian bay: " + durationStr + " | Loại máy bay: Phản lực thương mại</span></div></html>";

            cellInfo.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    String currentSelectedClass = isSearchingVe ? hangVeSelectedVe : hangVeSelectedDi;
                    
                    if (pnlDetails.isVisible() && currentSelectedClass.equals("")) {
                        pnlDetails.setVisible(false);
                        cellInfo.resetUI();
                    } else {
                        cellEco.resetUI(); cellPeco.resetUI(); cellBus.resetUI(); cellFst.resetUI();
                        cellInfo.setAsSelected();
                        if(isSearchingVe) hangVeSelectedVe = ""; else hangVeSelectedDi = "";
                        pnlDetails.setBackground(new Color(250, 250, 250)); 
                        lblDetailsContent.setText(htmlFlightInfo);
                        pnlDetails.setVisible(true);
                    }
                    FlightCard.this.revalidate();
                    FlightCard.this.repaint();
                }
            });

            java.awt.event.MouseAdapter clickAdapter = new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    PriceCell clickedCell = (PriceCell) e.getSource();
                    if (clickedCell.isSoldOut) return;

                    String currentSelectedClass = isSearchingVe ? hangVeSelectedVe : hangVeSelectedDi;

                    if (pnlDetails.isVisible() && currentSelectedClass.equals(clickedCell.hangVeCode)) {
                        pnlDetails.setVisible(false);
                        clickedCell.resetUI();
                        
                        if (isSearchingVe) {
                            cbSelectedVe = null; hangVeSelectedVe = ""; giaVeVe = BigDecimal.ZERO;
                        } else {
                            cbSelectedDi = null; hangVeSelectedDi = ""; giaVeDi = BigDecimal.ZERO;
                        }
                        updateFooterTotal(); 
                    } else {
                        cellInfo.resetUI(); 
                        cellEco.resetUI(); cellPeco.resetUI(); cellBus.resetUI(); cellFst.resetUI();
                        clickedCell.setAsSelected();
                        pnlDetails.setBackground(clickedCell.highlightBgColor);

                        String htmlTienIch = "<html><b style='font-size:16px; color:#122040;'>Bao gồm:</b><br><ul style='margin-left:20px;'>";
                        if (clickedCell.hangVeCode.equals("ECO")) htmlTienIch += "<li>07kg hành lý xách tay</li><li>Không bao gồm hành lý ký gửi</li><li>Không có phòng chờ</li>";
                        else if (clickedCell.hangVeCode.equals("PECO")) htmlTienIch += "<li>10kg hành lý xách tay</li><li>20kg hành lý ký gửi</li><li>Ưu tiên làm thủ tục</li>";
                        else if (clickedCell.hangVeCode.equals("BUS")) htmlTienIch += "<li>14kg hành lý xách tay</li><li>30kg hành lý ký gửi</li><li>Sử dụng phòng chờ cao cấp</li>";
                        else if (clickedCell.hangVeCode.equals("FST")) htmlTienIch += "<li>18kg hành lý xách tay</li><li>40kg hành lý ký gửi</li><li>Phòng chờ hạng sang VIP</li>";
                        lblDetailsContent.setText(htmlTienIch + "</ul></html>");
                        
                        pnlDetails.setVisible(true);
                        
                        if (isSearchingVe) {
                            cbSelectedVe = cb; hangVeSelectedVe = clickedCell.hangVeCode; giaVeVe = clickedCell.basePrice.multiply(BigDecimal.valueOf(soNL)); 
                        } else {
                            cbSelectedDi = cb; hangVeSelectedDi = clickedCell.hangVeCode; giaVeDi = clickedCell.basePrice.multiply(BigDecimal.valueOf(soNL)); 
                        }
                        updateFooterTotal();
                    }
                    FlightCard.this.revalidate();
                    FlightCard.this.repaint();
                }
            };

            cellEco.addMouseListener(clickAdapter); cellPeco.addMouseListener(clickAdapter);
            cellBus.addMouseListener(clickAdapter); cellFst.addMouseListener(clickAdapter);
        }
    }

    class FlightInfoCell extends JPanel {
        public FlightInfoCell(model.ChuyenBay cb) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            JLabel lblInfo = new JLabel("<html><center><b style='font-size:18px; color:#122040;'>" + 
                                        cb.getNgayGioDi().format(timeFmt) + "  " + cb.getNgayGioDen().format(timeFmt) + 
                                        "</b><br><span style='color:gray; font-size:12px;'>Chuyến bay: " + cb.getMaChuyenBay() + "</span></center></html>");
            lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
            add(lblInfo, BorderLayout.CENTER);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { if(getBackground().equals(Color.WHITE)) setBackground(new Color(250, 250, 250)); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { if(getBackground().equals(new Color(250, 250, 250))) setBackground(Color.WHITE); }
            });
        }
        public void resetUI() { setBackground(Color.WHITE); setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230))); }
        public void setAsSelected() { setBackground(new Color(245, 247, 250)); setBorder(BorderFactory.createLineBorder(new Color(18, 32, 64), 2)); }
    }

    class PriceCell extends JPanel {
        public boolean isSoldOut = false;
        public String hangVeCode;
        public Color highlightBgColor;
        public BigDecimal basePrice; 

        public PriceCell(model.ChuyenBay cb, bll.ChuyenBayBUS cbBUS, String hangVeCode, String textColor, String title, Color highlightBgColor) {
            this.hangVeCode = hangVeCode; this.highlightBgColor = highlightBgColor;
            setLayout(new BorderLayout()); setBackground(Color.WHITE); setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230))); setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblContent = new JLabel(); lblContent.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                this.basePrice = cbBUS.tinhGiaVe(cb.getMaChuyenBay(), hangVeCode);
                BigDecimal tongTien = this.basePrice.multiply(BigDecimal.valueOf(soNL));
                lblContent.setText("<html><center><span style='color:" + textColor + "; font-size:13px; font-weight:bold;'>" + title + "</span><br>" +
                    "<b style='color:#333; font-size:18px;'>" + String.format("%,d", tongTien.longValue()) + "</b><br><span style='color:gray; font-size:11px;'>VNĐ</span></center></html>");
            } catch (Exception e) {
                isSoldOut = true; setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); lblContent.setText("<html><center><b style='color:#ccc; font-size:15px;'>Hết vé</b></center></html>");
            }
            add(lblContent, BorderLayout.CENTER);
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { if(!isSoldOut && getBackground().equals(Color.WHITE)) setBackground(new Color(248, 248, 248)); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { if(!isSoldOut && getBackground().equals(new Color(248, 248, 248))) setBackground(Color.WHITE); }
            });
        }
        public void resetUI() { if(!isSoldOut) { setBackground(Color.WHITE); setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230))); } }
        public void setAsSelected() { setBackground(highlightBgColor); setBorder(BorderFactory.createLineBorder(new Color(18, 32, 64), 2));  }
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf()); UIManager.put("Button.arc", 12); } catch (Exception ex) {}
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    private javax.swing.JButton btnDatCho;
    private javax.swing.ButtonGroup btnGChuyenBay;
    private javax.swing.JButton btnHanhKhach;
    private javax.swing.JButton btnKhuyenMai;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnSignin;
    private javax.swing.JComboBox<String> cbCities;
    private javax.swing.JComboBox<String> cbCities1;
    private javax.swing.JComboBox<String> cbDonViTienTe;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<Object> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMenuHeader;
    private javax.swing.JPanel pnlMultiCities;
}