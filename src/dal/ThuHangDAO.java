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

    public boolean insert(ThuHang th){
        String sql = """
                INSERT INTO ThuHang 
                (maThuHang, tenThuHang,
                diemToiThieu, tiLeGiam)
                VALUES (?, ?, ?, ?)
        """;
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, th.getMaThuHang());
            ps.setString(2, th.getTenThuHang());
            ps.setInt(3, th.getDiemToiThieu());
            ps.setDouble(4, th.getTiLeGiam());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ThuHang th){
        String sql = "UPDATE ThuHang SET tenThuHang = ?, diemToiThieu = ?, tiLeGiam = ? WHERE maThuHang = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, th.getTenThuHang());
            ps.setInt(2, th.getDiemToiThieu());
            ps.setDouble(3, th.getTiLeGiam());
            ps.setString(4, th.getMaThuHang());

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maThuHang ){
        String sql = "DELETE FROM ThuHang WHERE maThuHang = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, maThuHang);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ThuHang selectById(String maHang) {
        ThuHang th = null;

        String sql = "SELECT * FROM ThuHang WHERE maThuHang = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHang);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                th = new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return th;
    }

    public ThuHang selectByTen(String tenHang) {
        ThuHang th = null;

        String sql = "SELECT * FROM ThuHang WHERE tenThuHang = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenHang);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                th = new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return th;
    }
}
