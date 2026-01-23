package main;

import utils.ExcelUtils; // Import cái class bạn vừa viết ở trên

public class Main {
    public static void main(String[] args) {
        try {
            // Gọi hàm check để xem mọi thứ đã ổn chưa
            ExcelUtils.checkLibrary();

            System.out.println("Chúc mừng! Thư viện đã hoạt động hoàn hảo.");

            // Sau khi check xong, bạn có thể gọi GUI của bạn ở đây
            // new FlightGUI().setVisible(true);

        } catch (Exception e) {
            System.err.println("Lỗi: Có thể bạn chưa tạo file 'data/test.xlsx' hoặc thiếu file JAR.");
            e.printStackTrace();
        }
    }
}