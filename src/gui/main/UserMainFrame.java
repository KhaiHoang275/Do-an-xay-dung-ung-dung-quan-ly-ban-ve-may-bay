package gui.main;

import gui.user.PanelUserProfile;
import javax.swing.*;
import java.awt.*;

public class UserMainFrame extends JFrame {

    public UserMainFrame(String maHK) {
        setTitle("KH AirLine - Tài khoản của tôi");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set icon (optional)
        // setIconImage(new ImageIcon("path/to/icon.png").getImage());

        // Add panel
        add(new PanelUserProfile(maHK));

        setVisible(true);
    }


}
