package main;

import java.util.prefs.Preferences;
import gui.user.DangNhapFrm;
import gui.user.MainFrame;
import dal.NguoiDungDAO;
import model.NguoiDung;
import javax.swing.*;

public class MainTestGUI {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Sử dụng giao diện mặc định.");
        }

        SwingUtilities.invokeLater(() -> {

            Preferences prefs = Preferences.userNodeForPackage(DangNhapFrm.class);
            String savedUser = prefs.get("saved_user", null);
            String savedPass = prefs.get("saved_pass", null);

            if (savedUser != null && savedPass != null) {

                NguoiDungDAO dao = new NguoiDungDAO();
                NguoiDung user = dao.checkLogin(savedUser, savedPass);

                if (user != null) {
                    System.out.println("Tự động đăng nhập: " + user.getUsername());
                    new MainFrame(user).setVisible(true); 
                    return;
                }
            }

            new DangNhapFrm().setVisible(true);
        });
    }
}