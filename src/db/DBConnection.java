package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QLAirLine;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";       
    private static final String PASSWORD = "123";  

    public static Connection getConnection() {
        Connection conn = null;
        try {
       
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
         
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Lỗi: Không tìm thấy Driver JDBC!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi: Sai thông tin kết nối (URL/User/Pass)!");
            e.printStackTrace();
        }
        return conn;
    }
    

    public static void main(String[] args) {
        if (getConnection() != null) {
            System.out.println("Kết nối SQL Server thành công!");
        }
    }
}