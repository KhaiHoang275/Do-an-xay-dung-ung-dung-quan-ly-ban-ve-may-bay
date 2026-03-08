package main;

import gui.DichVuHanhLyGUI;
import gui.ThanhToanGUI;
import gui.QuanLyHoaDonGUI;
import model.DatVeSession;
import model.ThongTinHanhKhach;
import java.util.ArrayList;

import javax.swing.*;

public class MainTestGUI {

    public static void main(String[] args) {
        // Cài đặt giao diện hiện đại FlatLaf (nếu bạn có thư viện này)
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Không tìm thấy FlatLaf, sử dụng giao diện mặc định.");
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TEST HỆ THỐNG BÁN VÉ MÁY BAY");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1150, 700);
            frame.setLocationRelativeTo(null);

            // 1. TẠO DỮ LIỆU GIẢ LẬP (MOCK SESSION) ĐỂ TEST GIAO DIỆN
            DatVeSession mockSession = new DatVeSession();
            mockSession.maNguoiDung = "HK001";
            mockSession.maChuyenBay = "CB001";
            mockSession.soNguoiLon = 2; // Giả sử đặt cho 2 người lớn
            
            // Phải add sẵn hành khách mẫu thì Form Hành Lý mới sinh ra các dòng chọn
            ThongTinHanhKhach hk1 = new ThongTinHanhKhach();
            hk1.setHoTen("Nguyễn Văn A");
            ThongTinHanhKhach hk2 = new ThongTinHanhKhach();
            hk2.setHoTen("Trần Thị B");
            
            mockSession.danhSachHanhKhach.add(hk1);
            mockSession.danhSachHanhKhach.add(hk2);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

            // 2. TRUYỀN SESSION VÀO CÁC PANEL (Sửa lỗi constructor trống)
            // Truyền mockSession vào DichVuHanhLyGUI
            tabbedPane.addTab("1. Dịch vụ & Hành lý", new gui.DichVuHanhLyGUI(mockSession));
            
            // Truyền mockSession vào ThanhToanGUI (Giả định bạn đã sửa Constructor bên đó)
            // Nếu ThanhToanGUI chưa sửa Constructor, bạn tạm thời để trống hoặc sửa file đó sau
            tabbedPane.addTab("2. Thanh toán", new gui.ThanhToanGUI());
            
            tabbedPane.addTab("3. Quản lý hóa đơn", new gui.QuanLyHoaDonGUI());

            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}