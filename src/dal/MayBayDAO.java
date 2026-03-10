package dal;

import model.MayBay;
import java.sql.*;
import java.util.ArrayList; 
import db.*; 

public class MayBayDAO {

    public ArrayList<MayBay> selectAll() {
        ArrayList<MayBay> list = new ArrayList<>();
        String sql = "SELECT * FROM MayBay";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MayBay mb = new MayBay();
                mb.setMaMayBay(rs.getString("maMayBay"));
                mb.setSoHieu(rs.getString("soHieu"));
                mb.setHangSanXuat(rs.getString("hangSanXuat"));
                mb.setTongSoGhe(rs.getInt("tongSoGhe"));
                mb.setTrangThai(MayBay.TrangThai.fromString(rs.getString("trangThai")));
                list.add(mb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public MayBay selectById(String maMayBay) {
        String sql = "SELECT * FROM MayBay WHERE maMayBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMayBay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private MayBay mapResultSet(ResultSet rs) throws SQLException {
        MayBay mb = new MayBay();
        mb.setMaMayBay(rs.getString("maMayBay"));
        mb.setSoHieu(rs.getString("soHieu"));
        mb.setHangSanXuat(rs.getString("hangSanXuat"));
        mb.setTongSoGhe(rs.getInt("tongSoGhe"));
        mb.setTrangThai(MayBay.TrangThai.fromString(rs.getString("trangThai")));
        return mb;
    }

    public boolean insert(MayBay mb) {
        String sql = "INSERT INTO MayBay (maMayBay, soHieu, hangSanXuat, tongSoGhe, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mb.getMaMayBay());
            ps.setString(2, mb.getSoHieu());
            ps.setString(3, mb.getHangSanXuat());
            ps.setInt(4, mb.getTongSoGhe());
            ps.setString(5, mb.getTrangThai().getValue());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(MayBay mb) {
        String sql = "UPDATE MayBay SET soHieu=?, hangSanXuat=?, tongSoGhe=?, trangThai=? WHERE maMayBay=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mb.getSoHieu());
            ps.setString(2, mb.getHangSanXuat());
            ps.setInt(3, mb.getTongSoGhe());
            ps.setString(4, mb.getTrangThai().getValue());
            ps.setString(5, mb.getMaMayBay());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // SOFT DELETE: Đưa vào Thùng rác
    public boolean delete(String maMayBay) {
        String sql = "UPDATE MayBay SET trangThai = N'Ngừng hoạt động' WHERE maMayBay=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMayBay);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}