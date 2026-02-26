package gui.main;

import gui.user.PanelUserProfile;

import javax.swing.*;

public class UserMainFrame extends JFrame {

    public UserMainFrame(String maHK) {
        setTitle("USER - Tài khoản");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new PanelUserProfile(maHK));
    }
}