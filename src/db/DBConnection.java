package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Properties props = new Properties();

            InputStream is = DBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("db/db.properties");

            if (is == null) {
                System.out.println("Không tìm thấy file db.properties");
                return null;
            }

            props.load(is);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
