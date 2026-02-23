package gui.main;

import gui.admin.QuanLyThuHangPanel;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {

    private JPanel contentPanel;

    public AdminMainFrame() {
        setTitle("ADMIN - Quản lý hệ thống");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.decode("#F4F4F6"));
        add(contentPanel, BorderLayout.CENTER);

        showPanel(new QuanLyThuHangPanel());
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(Color.decode("#D91E18"));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("QL AIRLINE", SwingConstants.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(logo);

        JButton btnThuHang = createSidebarButton("Quản lý hạng");
        btnThuHang.addActionListener(e -> showPanel(new QuanLyThuHangPanel()));
        sidebar.add(btnThuHang);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setBackground(Color.decode("#D91E18"));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}