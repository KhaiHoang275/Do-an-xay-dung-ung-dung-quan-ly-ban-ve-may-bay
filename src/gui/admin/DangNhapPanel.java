package gui.admin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

public class DangNhapPanel extends javax.swing.JFrame {

    private Border defaultBorder;
    private boolean isShowPassword = false;

    private final Color BG_MAIN = new Color(240, 244, 248);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color ADMIN_PRIMARY = new Color(13, 110, 253);
    private final Color ERROR_COLOR = new Color(220, 53, 69);

    public DangNhapPanel() {
        initComponents();
        setupUI();
        setupEvents();
    }

    private void setupUI() {
        this.getContentPane().setBackground(BG_MAIN);
        this.setTitle("Cổng Đăng Nhập Quản Trị Viên");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        LoginLable.setBackground(PANEL_COLOR);
        LoginLable.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        LableTitle.setText("ADMIN PORTAL");
        LableTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        LableTitle.setForeground(ADMIN_PRIMARY);

        btnSeeCnPass.setText("👁");
        btnSeeCnPass.setBorderPainted(false);
        btnSeeCnPass.setContentAreaFilled(false);
        btnSeeCnPass.setFocusPainted(false);
        btnSeeCnPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeeCnPass.setForeground(Color.GRAY);

        btnConfirm.setText("ĐĂNG NHẬP HỆ THỐNG");
        btnConfirm.setBackground(ADMIN_PRIMARY);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReturn.setText("Thoát");
        btnReturn.setBackground(Color.WHITE);
        btnReturn.setForeground(Color.DARK_GRAY);
        btnReturn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReturn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        btnReturn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        defaultBorder = NameAccount.getBorder();
        PasswordField1.setText("");
        PasswordField1.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu Quản trị...");
        NameAccount.putClientProperty("JTextField.placeholderText", "Nhập Username...");
    }

    private void setupEvents() {
        btnConfirm.addActionListener(e -> thucHienDangNhap());
        btnReturn.addActionListener(e -> {
            new gui.MainFrame().setVisible(true);
            this.dispose();
        });
        
        btnSeeCnPass.addActionListener(e -> {
            isShowPassword = !isShowPassword;
            if (isShowPassword) {
                PasswordField1.setEchoChar((char) 0);
                btnSeeCnPass.setForeground(ADMIN_PRIMARY);
            } else {
                PasswordField1.setEchoChar('•');
                btnSeeCnPass.setForeground(Color.GRAY);
            }
        });

        KeyAdapter enterEvent = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) thucHienDangNhap();
            }
        };
        NameAccount.addKeyListener(enterEvent);
        PasswordField1.addKeyListener(enterEvent);
    }

    private void thucHienDangNhap() {
        NameAccount.setBorder(defaultBorder);
        PasswordField1.setBorder(defaultBorder);
        lblErrorUser.setText(" ");
        lblErrorPass.setText(" ");

        String user = NameAccount.getText().trim();
        String pass = new String(PasswordField1.getPassword());
        boolean hasError = false;

        if (user.isEmpty()) {
            NameAccount.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
            lblErrorUser.setText("* Chưa nhập tài khoản");
            hasError = true;
        }
        if (pass.isEmpty()) {
            PasswordField1.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
            lblErrorPass.setText("* Chưa nhập mật khẩu");
            hasError = true;
        }

        if (hasError) return;

        bll.NguoiDungBUS bus = new bll.NguoiDungBUS();
        model.NguoiDung nd = bus.checkLogin(user, pass);

        if (nd != null) {
            if (nd.getPhanQuyen() != null && nd.getPhanQuyen().equalsIgnoreCase("Admin")) {
                JOptionPane.showMessageDialog(this, 
                    "Đăng nhập hệ thống Quản Trị thành công!\nXin chào Admin: " + nd.getUsername(), 
                    "Ủy quyền thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                for (java.awt.Window window : java.awt.Window.getWindows()) {
                    window.dispose();
                }
                
                applyAdminTheme();
                new gui.main.AdminMainFrame().setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "TRUY CẬP BỊ TỪ CHỐI!\nTài khoản của bạn không có quyền truy cập vào cổng Quản trị viên.", 
                    "Cảnh báo bảo mật", 
                    JOptionPane.ERROR_MESSAGE);
                PasswordField1.setText("");
            }
            
        } else {
            NameAccount.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
            PasswordField1.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
            lblErrorPass.setText("* Sai thông tin đăng nhập Quản trị");
            PasswordField1.setText("");
            NameAccount.requestFocus();
        }
    }

    private void applyAdminTheme() {
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
            javax.swing.UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            javax.swing.UIManager.put("Panel.background", java.awt.Color.WHITE);
            javax.swing.UIManager.put("Table.background", java.awt.Color.WHITE);
            javax.swing.UIManager.put("Table.foreground", java.awt.Color.BLACK);
            javax.swing.UIManager.put("Table.selectionBackground", new java.awt.Color(45, 72, 140));
            javax.swing.UIManager.put("Table.selectionForeground", java.awt.Color.WHITE);
            javax.swing.UIManager.put("Table.showHorizontalLines", true);
            javax.swing.UIManager.put("Table.showVerticalLines", false);
            javax.swing.UIManager.put("Table.rowHeight", 38);
            javax.swing.UIManager.put("TableHeader.background", new java.awt.Color(28, 48, 96));
            javax.swing.UIManager.put("TableHeader.foreground", java.awt.Color.WHITE);
            javax.swing.UIManager.put("TableHeader.height", 40);
            javax.swing.UIManager.put("ScrollPane.background", java.awt.Color.WHITE);
            javax.swing.UIManager.put("ScrollPane.border", javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));
            javax.swing.UIManager.put("TabbedPane.selectedBackground", java.awt.Color.WHITE);
            javax.swing.UIManager.put("TabbedPane.underlineColor", new java.awt.Color(45, 72, 140));
            javax.swing.UIManager.put("TabbedPane.tabHeight", 40);
            javax.swing.UIManager.put("Button.arc", 12);
            javax.swing.UIManager.put("Component.arc", 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        LoginLable = new gui.custom.RoundedPanel();
        LableTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        NameAccount = new javax.swing.JTextField();
        LablePassword = new javax.swing.JLabel();
        PasswordField1 = new javax.swing.JPasswordField();
        btnSeeCnPass = new javax.swing.JButton();
        lblErrorUser = new javax.swing.JLabel();
        lblErrorPass = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();

        jLabel1.setFont(new Font("Arial", Font.BOLD, 18));
        jLabel1.setText("Tài khoản:");

        LablePassword.setFont(new Font("Arial", Font.BOLD, 18));
        LablePassword.setText("Mật khẩu:");

        lblErrorUser.setFont(new Font("Arial", Font.ITALIC, 13));
        lblErrorUser.setForeground(ERROR_COLOR);
        lblErrorUser.setText(" ");

        lblErrorPass.setFont(new Font("Arial", Font.ITALIC, 13));
        lblErrorPass.setForeground(ERROR_COLOR);
        lblErrorPass.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(LoginLable);
        LoginLable.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(LableTitle)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(LablePassword))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblErrorUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblErrorPass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(NameAccount)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(PasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSeeCnPass, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnConfirm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReturn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(60, 60, 60))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(LableTitle)
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(NameAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblErrorUser)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LablePassword)
                    .addComponent(PasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSeeCnPass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblErrorPass)
                .addGap(30, 30, 30)
                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        javax.swing.GroupLayout formLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(formLayout);
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addContainerGap(80, Short.MAX_VALUE)
                .addComponent(LoginLable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );
        formLayout.setVerticalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .addComponent(LoginLable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );
        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new DangNhapPanel().setVisible(true));
    }

    private javax.swing.JLabel LablePassword;
    private javax.swing.JLabel LableTitle;
    private gui.custom.RoundedPanel LoginLable;
    private javax.swing.JTextField NameAccount;
    private javax.swing.JPasswordField PasswordField1;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnReturn;
    private javax.swing.JButton btnSeeCnPass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblErrorPass;
    private javax.swing.JLabel lblErrorUser;
}