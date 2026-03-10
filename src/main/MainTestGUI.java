
package main;

import java.util.prefs.Preferences;
import gui.user.DangNhapFrm;
import gui.user.MainFrame;
import dal.NguoiDungDAO;
import model.NguoiDung;
import javax.swing.*;

public class MainTestGUI {

    public static void main(String[] args) {
        // 1. Thiết lập giao diện
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Sử dụng giao diện mặc định.");
        }

        SwingUtilities.invokeLater(() -> {
            // 2. TRUY XUẤT THÔNG TIN ĐÃ GHI NHỚ
            Preferences prefs = Preferences.userNodeForPackage(DangNhapFrm.class);
            String savedUser = prefs.get("saved_user", null);
            String savedPass = prefs.get("saved_pass", null);

            if (savedUser != null && savedPass != null) {
                // Thử đăng nhập tự động bằng dữ liệu đã lưu
                NguoiDungDAO dao = new NguoiDungDAO();
                NguoiDung user = dao.checkLogin(savedUser, savedPass);

                if (user != null) {
                    System.out.println("Tự động đăng nhập: " + user.getUsername());
                    new MainFrame(user).setVisible(true); // Vào thẳng trang chủ
                    return;
                }
            }

            // 3. NẾU KHÔNG CÓ THÔNG TIN LƯU -> MỞ TRANG ĐĂNG NHẬP
            new DangNhapFrm().setVisible(true);
        });
    }
}
