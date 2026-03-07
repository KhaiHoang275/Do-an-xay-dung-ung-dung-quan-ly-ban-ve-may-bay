package main;

import gui.DichVuHanhLyGUI;
import gui.ThanhToanGUI;
import gui.QuanLyHoaDonGUI;

import javax.swing.*;

public class MainTestGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TEST HỆ THỐNG BÁN VÉ MÁY BAY");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 650);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabbedPane = new JTabbedPane();

            tabbedPane.addTab("Dịch vụ & Hành lý", new DichVuHanhLyGUI());
            tabbedPane.addTab("Thanh toán", new ThanhToanGUI());
            tabbedPane.addTab("Quản lý hóa đơn", new QuanLyHoaDonGUI());

            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}