package test;

import gui.main.AdminMainFrame;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class TestAdminPanels {

    public static void main(String[] args) {

        try {
            // Set FlatLaf
            UIManager.setLookAndFeel(new FlatIntelliJLaf());

            // ===== GLOBAL STYLE CONFIG =====

            // Font chung
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

            // Panel background
            UIManager.put("Panel.background", Color.WHITE);

            // Table
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.foreground", Color.BLACK);
            UIManager.put("Table.selectionBackground", new Color(45, 72, 140));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.rowHeight", 38);

            // Table Header
            UIManager.put("TableHeader.background", new Color(28, 48, 96));
            UIManager.put("TableHeader.foreground", Color.WHITE);
            UIManager.put("TableHeader.height", 40);

            // ScrollPane
            UIManager.put("ScrollPane.background", Color.WHITE);
            UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(new Color(220,220,220)));

            // TabbedPane
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("TabbedPane.underlineColor", new Color(45, 72, 140));
            UIManager.put("TabbedPane.tabHeight", 40);

            // Button
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new AdminMainFrame().setVisible(true);
        });

        System.out.println(UIManager.getLookAndFeel());
    }
}