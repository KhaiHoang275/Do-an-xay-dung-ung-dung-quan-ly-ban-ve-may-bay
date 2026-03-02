package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            // URL đã trỏ thẳng vào nhà kho V2 chứa 50 khách hàng vừa bơm
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLAirLine_V2;encrypt=true;trustServerCertificate=true";
            String user = "sa";
            String pass = "12345"; // Đảm bảo đây đúng là mật khẩu SQL Server của bạn

            // Đăng ký driver (quan trọng cho một số bản JDK cũ hơn)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("LỖI KẾT NỐI: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Connection c = DBConnection.getConnection();
        if (c != null) {
            System.out.println("✅ THÀNH CÔNG: Đã kết nối Java với Database QLAirLine_V2!");
            System.out.println("🔥 Giờ hãy mở file Main.java lên và chạy thử giao diện nhé!");
        } else {
            System.out.println("❌ THẤT BẠI: Vui lòng kiểm tra lại SQL Server có đang bật không, hoặc sai mật khẩu sa!");
        }
    }
}