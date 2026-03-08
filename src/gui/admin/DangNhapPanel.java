package gui.admin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DangNhapPanel extends JFrame {

    private boolean isShowPassword = false;

    private final Color BG_MAIN = new Color(240, 244, 248);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color ADMIN_PRIMARY = new Color(13, 110, 253);
    private final Color BUTTON_HOVER = new Color(10, 88, 202);
    private final Color TEXT_COLOR = new Color(33, 37, 41);

    private gui.custom.RoundedPanel LoginLable; 
    private JLabel LableTitle, jLabel1, LablePassword;
    private JTextField NameAccount;
    private JPasswordField PasswordField1;
    private JButton btnConfirm, btnReturn, btnSeeCnPass;

    public DangNhapPanel() {
        initCustomUI();
        setupEvents();
    }

    private void initCustomUI() {
        this.setTitle("Cổng Đăng Nhập Quản Trị Viên");
        this.setSize(650, 450);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setBackground(BG_MAIN);
        
        this.setLayout(new GridBagLayout()); 

        LoginLable = new gui.custom.RoundedPanel();
        LoginLable.setBackground(PANEL_COLOR);
        LoginLable.setPreferredSize(new Dimension(420, 320));
        LoginLable.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;


        LableTitle = new JLabel("ĐĂNG NHẬP ADMIN", SwingConstants.CENTER);
        LableTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        LableTitle.setForeground(ADMIN_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 25, 20); // Top, Left, Bottom, Right
        LoginLable.add(LableTitle, gbc);

   
        jLabel1 = new JLabel("Tên đăng nhập:");
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel1.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 5, 30);
        LoginLable.add(jLabel1, gbc);

      
        NameAccount = new JTextField();
        NameAccount.setPreferredSize(new Dimension(300, 38));
        NameAccount.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 30, 15, 30);
        LoginLable.add(NameAccount, gbc);


        LablePassword = new JLabel("Mật khẩu:");
        LablePassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        LablePassword.setForeground(TEXT_COLOR);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 5, 30);
        LoginLable.add(LablePassword, gbc);

     
        JPanel pnlPassword = new JPanel(new BorderLayout());
        pnlPassword.setBackground(Color.WHITE);
        pnlPassword.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Vẽ viền giả
        pnlPassword.setPreferredSize(new Dimension(300, 38));

        PasswordField1 = new JPasswordField();
        PasswordField1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        PasswordField1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Xóa viền thật
        
        btnSeeCnPass = new JButton("Hiện");
        btnSeeCnPass.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSeeCnPass.setFocusPainted(false);
        btnSeeCnPass.setBorderPainted(false); 
        btnSeeCnPass.setForeground(new Color(100, 100, 100));
        btnSeeCnPass.setContentAreaFilled(false);
        btnSeeCnPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeeCnPass.setPreferredSize(new Dimension(70, 38));
        pnlPassword.add(PasswordField1, BorderLayout.CENTER);
        pnlPassword.add(btnSeeCnPass, BorderLayout.EAST);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 25, 30);
        LoginLable.add(pnlPassword, gbc);

        // 6. Khung chứa 2 nút bấm (Quay lại & Đăng nhập)
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 15, 0)); // Chia làm 2 nửa bằng nhau
        pnlButtons.setBackground(PANEL_COLOR);
        
        btnReturn = new JButton("Quay lại");
        btnReturn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReturn.setBackground(new Color(108, 117, 125));
        btnReturn.setForeground(Color.WHITE);
        btnReturn.setFocusPainted(false);
        btnReturn.setPreferredSize(new Dimension(0, 40));

        btnConfirm = new JButton("Đăng Nhập");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setBackground(ADMIN_PRIMARY);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);

        pnlButtons.add(btnReturn);
        pnlButtons.add(btnConfirm);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 30, 30);
        LoginLable.add(pnlButtons, gbc);

        // Add khung trắng vào Frame chính
        this.add(LoginLable);
    }

    private void setupEvents() {
        // Sự kiện ấn nút Đăng nhập
        btnConfirm.addActionListener(e -> thucHienDangNhapAdmin());

        // Phím Enter để đăng nhập nhanh
        PasswordField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    thucHienDangNhapAdmin();
                }
            }
        });

    
        btnSeeCnPass.addActionListener(e -> {
                isShowPassword = !isShowPassword;
                if (isShowPassword) {
                        PasswordField1.setEchoChar((char) 0);
                        btnSeeCnPass.setText("Ẩn");
                        btnSeeCnPass.setForeground(ADMIN_PRIMARY);
                } else {
                        PasswordField1.setEchoChar('•');
                        btnSeeCnPass.setText("Hiện");
                        btnSeeCnPass.setForeground(new java.awt.Color(100, 100, 100));
                }
        });
    
        btnReturn.addActionListener(e -> {
            new gui.DangNhapFrm().setVisible(true);
            this.dispose();
        });
    }


    private void thucHienDangNhapAdmin() {
        String user = NameAccount.getText();
        String pass = new String(PasswordField1.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        bll.NguoiDungBUS bus = new bll.NguoiDungBUS();
        model.NguoiDung admin = bus.checkLogin(user, pass); 

        if (admin != null) {
     
            if ("Admin".equalsIgnoreCase(admin.getPhanQuyen()) || "ADMIN".equalsIgnoreCase(admin.getPhanQuyen())) {
                
 
                setupAdminUITheme();
                
           
                gui.main.AdminMainFrame adminFrame = new gui.main.AdminMainFrame();
                adminFrame.setVisible(true);
                this.dispose();
                
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản này không có quyền Quản trị viên (Admin)!", "Truy cập bị từ chối", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void setupAdminUITheme() {
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
            javax.swing.UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            javax.swing.UIManager.put("Panel.background", Color.WHITE);
            javax.swing.UIManager.put("Table.background", Color.WHITE);
            javax.swing.UIManager.put("Table.foreground", Color.BLACK);
            javax.swing.UIManager.put("Table.selectionBackground", new Color(45, 72, 140));
            javax.swing.UIManager.put("Table.selectionForeground", Color.WHITE);
            javax.swing.UIManager.put("Table.showHorizontalLines", true);
            javax.swing.UIManager.put("Table.showVerticalLines", false);
            javax.swing.UIManager.put("Table.rowHeight", 38);
            javax.swing.UIManager.put("TableHeader.background", new Color(28, 48, 96));
            javax.swing.UIManager.put("TableHeader.foreground", Color.WHITE);
            javax.swing.UIManager.put("TableHeader.height", 40);
            javax.swing.UIManager.put("ScrollPane.background", Color.WHITE);
            javax.swing.UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(new Color(220,220,220)));
            javax.swing.UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            javax.swing.UIManager.put("TabbedPane.underlineColor", new Color(45, 72, 140));
            javax.swing.UIManager.put("TabbedPane.tabHeight", 40);
            javax.swing.UIManager.put("Button.arc", 12);
            javax.swing.UIManager.put("Component.arc", 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new DangNhapPanel().setVisible(true));
    }
}