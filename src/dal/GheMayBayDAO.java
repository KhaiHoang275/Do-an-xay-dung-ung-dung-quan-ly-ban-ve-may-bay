package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.DBConnection;
import model.GheMayBay;
import model.TrangThaiGhe;

public class GheMayBayDAO {

    public ArrayList<GheMayBay> selectAll() {
        ArrayList<GheMayBay> list = new ArrayList<>();
        String sql = "SELECT * FROM GheMayBay WHERE TrangThai != 'DA_XOA' OR TrangThai IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToGheMayBay(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<GheMayBay> selectByMayBay(String maMayBay) {
        ArrayList<GheMayBay> list = new ArrayList<>();
        String sql = "SELECT * FROM GheMayBay WHERE MaMayBay = ? AND (TrangThai != 'DA_XOA' OR TrangThai IS NULL) ORDER BY SoGhe ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMayBay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToGheMayBay(rs));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<GheMayBay> selectThungRac() {
        ArrayList<GheMayBay> list = new ArrayList<>();
        String sql = "SELECT * FROM GheMayBay WHERE TrangThai = 'DA_XOA'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToGheMayBay(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public GheMayBay selectById(String maGhe) {
        String sql = "SELECT * FROM GheMayBay WHERE MaGhe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGhe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToGheMayBay(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(GheMayBay ghe) {
        String sql = "INSERT INTO GheMayBay (MaGhe, MaMayBay, SoGhe, GiaGhe, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ghe.getMaGhe());
            ps.setString(2, ghe.getMaMayBay());
            ps.setString(3, ghe.getSoGhe());
            ps.setBigDecimal(4, ghe.getGiaGhe());
            ps.setString(5, ghe.getTrangThai() != null ? ghe.getTrangThai().name() : TrangThaiGhe.TRONG.name());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(GheMayBay ghe) {
        String sql = "UPDATE GheMayBay SET MaMayBay = ?, SoGhe = ?, GiaGhe = ?, TrangThai = ? WHERE MaGhe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ghe.getMaMayBay());
            ps.setString(2, ghe.getSoGhe());
            ps.setBigDecimal(3, ghe.getGiaGhe());
            ps.setString(4, ghe.getTrangThai() != null ? ghe.getTrangThai().name() : TrangThaiGhe.TRONG.name());
            ps.setString(5, ghe.getMaGhe());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(String maGhe) {
        String sql = "UPDATE GheMayBay SET TrangThai = 'DA_XOA' WHERE MaGhe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGhe);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean restore(String maGhe) {
        String sql = "UPDATE GheMayBay SET TrangThai = 'TRONG' WHERE MaGhe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGhe);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // HÀM DÙNG CHUNG ĐỂ MAP DỮ LIỆU TỪ RESULTSET
    private GheMayBay mapResultSetToGheMayBay(ResultSet rs) throws SQLException {
        GheMayBay ghe = new GheMayBay();
        ghe.setMaGhe(rs.getString("MaGhe"));
        ghe.setMaMayBay(rs.getString("MaMayBay"));
        ghe.setSoGhe(rs.getString("SoGhe"));
        ghe.setGiaGhe(rs.getBigDecimal("GiaGhe")); 
        
        String statusStr = rs.getString("TrangThai");
        if (statusStr != null) {
            try {
                ghe.setTrangThai(TrangThaiGhe.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                ghe.setTrangThai(TrangThaiGhe.TRONG); // Giá trị mặc định nếu DB lỗi chuỗi
            }
        } else {
            ghe.setTrangThai(TrangThaiGhe.TRONG);
        }
        return ghe;
    }

    public String timMaGhe(Connection conn, String soGhe, String maMayBay) {
        try{
            String sql = "SELECT maGhe FROM GheMayBay WHERE soGhe = ? AND maMayBay = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, soGhe.trim());
            ps.setString(2, maMayBay);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getString("maGhe");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}