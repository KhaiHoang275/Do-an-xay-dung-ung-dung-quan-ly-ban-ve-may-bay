package gui.admin;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestHoaDonPanel {

    public static void main(String[] args) {
        // Cài đặt giao diện Look and Feel theo hệ điều hành (Windows/Mac/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy giao diện trên Event Dispatch Thread (EDT) để đảm bảo luồng an toàn cho Swing
        SwingUtilities.invokeLater(() -> {
            // Khởi tạo cửa sổ Frame
            JFrame frame = new JFrame("Test Giao Diện Quản Lý Hóa Đơn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Tắt chương trình khi đóng cửa sổ
            frame.setSize(1100, 750); // Set kích thước phù hợp để hiển thị hết bảng và form
            frame.setLocationRelativeTo(null); // Hiển thị cửa sổ ở chính giữa màn hình

            try {
                // Khởi tạo panel của bạn
                HoaDonPanel hoaDonPanel = new HoaDonPanel();
                
                // Thêm panel vào giữa Frame
                frame.add(hoaDonPanel);
                
                // Hiển thị Frame
                frame.setVisible(true);
            } catch (Exception ex) {
                System.out.println("Lỗi khi khởi tạo giao diện. Hãy kiểm tra kết nối Database!");
                ex.printStackTrace();
            }
        });
    }
}