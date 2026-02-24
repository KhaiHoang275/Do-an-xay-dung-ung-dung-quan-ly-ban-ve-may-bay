package test;
import gui.main.AdminMainFrame;

import javax.swing.*;

public class TestAdminPanels {
    public static void main(String[] args) {

        // Làm UI mượt hơn
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new AdminMainFrame().setVisible(true);
        });
    }
}