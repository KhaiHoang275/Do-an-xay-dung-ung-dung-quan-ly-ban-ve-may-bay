package dal;

import db.DBConnection;
import model.ThuHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThuHangDAO {
    public List<ThuHang> getAll() {
        List<ThuHang> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuHang";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThuHang th = new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam")
                );
                list.add(th);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public ThuHang getThuHangTheoDiem(int diem) {
        String sql = """
            SELECT TOP 1 *
            FROM ThuHang
            WHERE diemToiThieu <= ?
            ORDER BY diemToiThieu DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diem);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
