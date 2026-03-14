package gui.user;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DangKyFrm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DangKyFrm.class.getName());
    private int passStrength = 0; 
    private Border defaultBorder;
    
    // UI Components
    private gui.custom.RoundedPanel LoginLable;
    private JLabel LableTitle;
    private JTextField NameAccount, NameUser, txtEmail, txtSdt;
    private JPasswordField PasswordField, confirmPass;
    private JButton btnSeePass, btnSeeCnPass;
    private JButton btnConfirm, btnReturn, btnExit;
    private JLabel lbErrorUsNa, lbErrorName, lbErrorEmail, lbErrorSdt, lbErrorPass, lbErrorConPass;

    public DangKyFrm() {
        initComponents();  
        
        Color PRIMARY_COLOR = new Color(18, 32, 64);
        Color SECONDARY_COLOR = new Color(45, 72, 140);
        Color SUCCESS_COLOR = new Color(76, 175, 80);
        Color DANGER_COLOR = new Color(244, 67, 54);
        Color BG_MAIN = new Color(245, 247, 250); 

        this.getContentPane().setBackground(BG_MAIN);
        
        btnConfirm.setBackground(SUCCESS_COLOR);
        btnConfirm.setForeground(Color.WHITE);
        btnReturn.setBackground(SECONDARY_COLOR);
        btnReturn.setForeground(Color.WHITE);
        btnExit.setBackground(DANGER_COLOR);
        btnExit.setForeground(Color.WHITE);
        
        defaultBorder = NameAccount.getBorder();
        
        clearErrors();

        this.pack(); 
        this.setSize(800, 750);
        this.setResizable(false); 
        this.setLocationRelativeTo(null); 
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); 

        NameAccount.putClientProperty("JTextField.placeholderText", "Viết liền, không dấu cách..."); 
        txtEmail.putClientProperty("JTextField.placeholderText", "VD: nguyenvan@gmail.com"); 
        txtSdt.putClientProperty("JTextField.placeholderText", "Gồm 10 số, VD: 0912345678"); 

        btnSeePass.setText("👁"); 
        btnSeeCnPass.setText("👁"); 

        btnSeePass.addActionListener(e -> togglePassword(PasswordField, btnSeePass));
        btnSeeCnPass.addActionListener(e -> togglePassword(confirmPass, btnSeeCnPass));
        
        btnExit.addActionListener(e -> {
            new MainFrame().setVisible(true); 
            this.dispose();
        });
        
        btnReturn.addActionListener(e -> {
            DangNhapFrm frm = new DangNhapFrm();
            frm.setLocationRelativeTo(null);
            frm.setVisible(true);
            this.dispose(); 
        });
        
        btnConfirm.addActionListener(e -> thucHienDangKy()); 

        setupRealtimeValidation();
    }

    private void initComponents() {
        LoginLable = new gui.custom.RoundedPanel();
        LoginLable.setBackground(Color.WHITE);
        LoginLable.setLayout(new GridBagLayout());
        
        LableTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        LableTitle.setFont(new Font("Times New Roman", Font.BOLD, 40));
        LableTitle.setForeground(new Color(18, 32, 64)); // Màu xanh thương hiệu
        
        // Khởi tạo TextFields
        NameAccount = new JTextField();
        NameUser = new JTextField();
        txtEmail = new JTextField();
        txtSdt = new JTextField();
        PasswordField = new JPasswordField();
        confirmPass = new JPasswordField();
        
        Dimension fieldSize = new Dimension(250, 40);
        NameAccount.setPreferredSize(fieldSize);
        NameUser.setPreferredSize(fieldSize);
        txtEmail.setPreferredSize(fieldSize);
        txtSdt.setPreferredSize(fieldSize);
        
        Dimension passSize = new Dimension(195, 40);
        PasswordField.setPreferredSize(passSize);
        confirmPass.setPreferredSize(passSize);

        // Khởi tạo Nút
        btnSeePass = new JButton();
        btnSeeCnPass = new JButton();
        Dimension eyeSize = new Dimension(50, 40);
        btnSeePass.setPreferredSize(eyeSize);
        btnSeeCnPass.setPreferredSize(eyeSize);

        btnConfirm = new JButton("Xác nhận");
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirm.setPreferredSize(new Dimension(150, 45));
        
        btnReturn = new JButton("Quay lại");
        btnReturn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnReturn.setPreferredSize(new Dimension(120, 40));
        
        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExit.setPreferredSize(new Dimension(120, 40));

        // Khởi tạo Error Labels
        lbErrorUsNa = createErrorLabel();
        lbErrorName = createErrorLabel();
        lbErrorEmail = createErrorLabel();
        lbErrorSdt = createErrorLabel();
        lbErrorPass = createErrorLabel();
        lbErrorConPass = createErrorLabel();

        // CHẶN NHẬP KÝ TỰ SAI NGAY LẬP TỨC
        NameAccount.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == ' ') e.consume(); // Chặn dấu cách
            }
        });
        
        txtSdt.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume(); // Chỉ cho nhập số
            }
        });

        // BỐ TRÍ GIAO DIỆN VỚI GRIDBAGLAYOUT (Đẹp và thẳng hàng)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; 
        gbc.insets = new Insets(20, 10, 30, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        LoginLable.add(LableTitle, gbc);
        
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 0, 10);

        // 1. Username
        gbc.gridx = 0; gbc.gridy = 1; LoginLable.add(createTitleLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; LoginLable.add(NameAccount, gbc);
        gbc.gridx = 1; gbc.gridy = 2; LoginLable.add(lbErrorUsNa, gbc);

        // 2. Họ Tên
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; LoginLable.add(createTitleLabel("Họ và tên:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; LoginLable.add(NameUser, gbc);
        gbc.gridx = 1; gbc.gridy = 4; LoginLable.add(lbErrorName, gbc);

        // 3. Email
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; LoginLable.add(createTitleLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; LoginLable.add(txtEmail, gbc);
        gbc.gridx = 1; gbc.gridy = 6; LoginLable.add(lbErrorEmail, gbc);

        // 4. Số điện thoại
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1; LoginLable.add(createTitleLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; LoginLable.add(txtSdt, gbc);
        gbc.gridx = 1; gbc.gridy = 8; LoginLable.add(lbErrorSdt, gbc);

        // 5. Password
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 1; LoginLable.add(createTitleLabel("Password:"), gbc);
        
        JPanel pnlPass = new JPanel(new BorderLayout(5, 0)); pnlPass.setOpaque(false);
        pnlPass.add(PasswordField, BorderLayout.CENTER); pnlPass.add(btnSeePass, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridwidth = 2; LoginLable.add(pnlPass, gbc);
        gbc.gridx = 1; gbc.gridy = 10; LoginLable.add(lbErrorPass, gbc);

        // 6. Confirm Password
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 1; LoginLable.add(createTitleLabel("Xác nhận password:"), gbc);
        
        JPanel pnlCPass = new JPanel(new BorderLayout(5, 0)); pnlCPass.setOpaque(false);
        pnlCPass.add(confirmPass, BorderLayout.CENTER); pnlCPass.add(btnSeeCnPass, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridwidth = 2; LoginLable.add(pnlCPass, gbc);
        gbc.gridx = 1; gbc.gridy = 12; LoginLable.add(lbErrorConPass, gbc);

        // 7. Buttons
        gbc.gridx = 0; gbc.gridy = 13; gbc.gridwidth = 3; 
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        LoginLable.add(btnConfirm, gbc);

        JPanel pnlBottomOpts = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        pnlBottomOpts.setOpaque(false);
        pnlBottomOpts.add(btnExit);
        pnlBottomOpts.add(btnReturn);

        gbc.gridy = 14; 
        gbc.insets = new Insets(10, 10, 30, 10);
        LoginLable.add(pnlBottomOpts, gbc);

        this.setLayout(new BorderLayout());
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        wrapper.setOpaque(false);
        wrapper.add(LoginLable);
        this.add(wrapper, BorderLayout.CENTER);
    }

    private JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        return lbl;
    }

    private JLabel createErrorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("Arial", Font.ITALIC, 14));
        lbl.setForeground(new Color(255, 0, 51));
        return lbl;
    }

    private void setupRealtimeValidation() {
        PasswordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkPassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkPassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkPassword(); }
        });
        
        confirmPass.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkConfirmPassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkConfirmPassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkConfirmPassword(); }
        });

        NameAccount.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { clearError(NameAccount, lbErrorUsNa); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { clearError(NameAccount, lbErrorUsNa); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { clearError(NameAccount, lbErrorUsNa); }
        });

        NameUser.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { clearError(NameUser, lbErrorName); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { clearError(NameUser, lbErrorName); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { clearError(NameUser, lbErrorName); }
        });
        
        txtEmail.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { clearError(txtEmail, lbErrorEmail); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { clearError(txtEmail, lbErrorEmail); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { clearError(txtEmail, lbErrorEmail); }
        });
        
        txtSdt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { clearError(txtSdt, lbErrorSdt); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { clearError(txtSdt, lbErrorSdt); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { clearError(txtSdt, lbErrorSdt); }
        });
    }

    private void clearError(JComponent comp, JLabel lbl) {
        comp.setBorder(defaultBorder);
        lbl.setText(" ");
    }

    private void clearErrors() {
        clearError(NameAccount, lbErrorUsNa);
        clearError(NameUser, lbErrorName);
        clearError(txtEmail, lbErrorEmail);
        clearError(txtSdt, lbErrorSdt);
        clearError(PasswordField, lbErrorPass);
        clearError(confirmPass, lbErrorConPass);
    }

    private void togglePassword(JPasswordField pf, JButton btn) {
        if (pf.getEchoChar() != (char) 0) {
            pf.setEchoChar((char) 0);
            btn.setText("🙈");
        } else {
            pf.setEchoChar('•');
            btn.setText("👁");
        }
    } 

    private void checkConfirmPassword() {
        String p = new String(PasswordField.getPassword());
        String cP = new String(confirmPass.getPassword());
        if (cP.isEmpty()) {
            confirmPass.setBorder(defaultBorder);
            lbErrorConPass.setText(" ");
        } else if (!p.equals(cP)) {
            confirmPass.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorConPass.setText("* Xác nhận mật khẩu không khớp");
        } else {
            confirmPass.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 51), 2)); 
            lbErrorConPass.setText(" ");
        }
    }

    private void checkPassword() {
        String p = new String(PasswordField.getPassword());
        passStrength = 0;
        
        if (p.length() > 0) passStrength++; 
        if (p.length() >= 6) passStrength++; 
        if (p.matches(".*[a-z].*") && p.matches(".*[A-Z].*")) passStrength++; 
        if (p.matches(".*\\d.*")) passStrength++; 
        if (p.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) passStrength++; 
        if (p.length() >= 8 && passStrength == 5) passStrength++; 
        
        if (p.isEmpty()) {
            PasswordField.setBorder(defaultBorder);
            lbErrorPass.setText(" ");
        } else if (passStrength < 3) {
            PasswordField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorPass.setText("* Mật khẩu quá yếu (Cần chữ hoa, số, ký tự đ.biệt)");
        } else {
            PasswordField.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 51), 2)); 
            lbErrorPass.setText(" ");
        }
        
        checkConfirmPassword(); 
    }
    
    // HÀM TỰ ĐỘNG SINH MÃ NỐI TIẾP TRONG DATABASE (Ví dụ: ND001 -> ND002)
    private String getNextId(String tableName, String idColumn, String prefix) {
        String newId = prefix + "001";
        String sql = "SELECT MAX(" + idColumn + ") FROM " + tableName;
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1); // VD: ND015
                String numPart = maxId.replaceAll("\\D+", ""); // Tách lấy số: "015"
                if (!numPart.isEmpty()) {
                    int num = Integer.parseInt(numPart) + 1;
                    newId = String.format("%s%03d", prefix, num); // Fomat lại: ND016
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newId;
    }
    
    private void thucHienDangKy() {
        String user = NameAccount.getText().trim();
        String fullName = NameUser.getText().trim(); 
        String email = txtEmail.getText().trim();
        String sdt = txtSdt.getText().trim();
        String pass = new String(PasswordField.getPassword());
        String cPass = new String(confirmPass.getPassword());
        
        boolean hasError = false;

        // Validation Username
        if (user.isEmpty()) {
            NameAccount.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorUsNa.setText("* Chưa nhập Username");
            hasError = true;
        } else if (user.contains(" ")) {
            NameAccount.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorUsNa.setText("* Username không được chứa khoảng trắng");
            hasError = true;
        }

        // Validation Họ tên
        if (fullName.isEmpty()) {
            NameUser.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorName.setText("* Chưa nhập Họ và tên");
            hasError = true;
        }

        // Validation Email (Biểu thức chính quy Regex)
        if (email.isEmpty()) {
            txtEmail.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorEmail.setText("* Chưa nhập Email");
            hasError = true;
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            txtEmail.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorEmail.setText("* Email không đúng định dạng (VD: abc@gmail.com)");
            hasError = true;
        }

        // Validation SĐT (Bắt đầu bằng số 0, đủ 10 số)
        if (sdt.isEmpty()) {
            txtSdt.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorSdt.setText("* Chưa nhập Số điện thoại");
            hasError = true;
        } else if (!sdt.matches("^0\\d{9}$")) {
            txtSdt.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            lbErrorSdt.setText("* SĐT phải gồm 10 chữ số và bắt đầu bằng số 0");
            hasError = true;
        }

        // Validation Mật khẩu
        if (pass.isEmpty() || passStrength < 3 || cPass.isEmpty() || !pass.equals(cPass)) {
            hasError = true;
            checkPassword(); 
        }

        if (hasError) return; 
        
        // TỰ ĐỘNG SINH MÃ THEO DATABASE
        String maND = getNextId("NguoiDung", "maNguoiDung", "ND"); 
        String maHK = getNextId("ThongTinHanhKhach", "maHK", "HK");
        
        bll.NguoiDungBUS bus = new bll.NguoiDungBUS();
        model.NguoiDung ndNew = new model.NguoiDung(maND, user, pass, email, sdt, java.time.LocalDate.now(), "KhachHang", model.NguoiDung.TrangThai.HOAT_DONG);
        String result = bus.themNguoiDung(ndNew);
        
        if (result.contains("thành công")) {
            try {
                dal.ThongTinHanhKhachDAO tthkDao = new dal.ThongTinHanhKhachDAO();
                model.ThongTinHanhKhach hkNew = new model.ThongTinHanhKhach();
                hkNew.setMaHK(maHK);
                hkNew.setMaNguoiDung(maND);
                hkNew.setHoTen(fullName);
                hkNew.setMaThuHang("SILVER");
                hkNew.setCccd("");
                hkNew.setHoChieu("");
                hkNew.setNgaySinh(java.time.LocalDate.of(2000, 1, 1)); 
                hkNew.setGioiTinh("Khác");
                hkNew.setDiemTichLuy(0);
                tthkDao.insert(hkNew); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!\nChào mừng " + fullName + " đến với AirLiner.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            DangNhapFrm frm = new DangNhapFrm();
            frm.setLocationRelativeTo(null);
            frm.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại!\nLý do: " + result + "\n(Có thể Username hoặc Email này đã tồn tại)", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
            UIManager.put("Button.arc", 12); 
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new DangKyFrm().setVisible(true));
    }
}