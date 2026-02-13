package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.math.BigDecimal;

import db.DBConnection;
import model.GheMayBay;

public class GheMayBayDAO {

    public ArrayList<GheMayBay> getAllGheMayBay() {
        ArrayList<GheMayBay> list = new ArrayList<>();
        String sql = "SELECT * FROM GheMayBay";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                GheMayBay ghe = new GheMayBay();
                ghe.setMaGhe(rs.getString("MaGhe"));
                ghe.setMaMayBay(rs.getString("MaMayBay"));
                ghe.setSoGhe(rs.getString("SoGhe"));
                
                // CẬP NHẬT: Lấy BigDecimal từ SQL
                ghe.setGiaGhe(rs.getBigDecimal("GiaGhe")); 
                
                list.add(ghe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<GheMayBay> selectByMayBay(String maMayBay) {
        ArrayList<GheMayBay> list = new ArrayList<>();
        // Sắp xếp theo số ghế để hiển thị cho đẹp (VD: 1A, 1B, 2A...)
        String sql = "SELECT * FROM GheMayBay WHERE MaMayBay = ? ORDER BY SoGhe ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maMayBay);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GheMayBay ghe = new GheMayBay();
                    ghe.setMaGhe(rs.getString("MaGhe"));
                    ghe.setMaMayBay(rs.getString("MaMayBay"));
                    ghe.setSoGhe(rs.getString("SoGhe"));
                    
                    // CẬP NHẬT: Lấy BigDecimal
                    ghe.setGiaGhe(rs.getBigDecimal("GiaGhe"));
                    
                    list.add(ghe);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Tìm ghế theo Mã Ghế
    public GheMayBay selectById(String maGhe) {
        String sql = "SELECT * FROM GheMayBay WHERE MaGhe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maGhe);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GheMayBay ghe = new GheMayBay();
                    ghe.setMaGhe(rs.getString("MaGhe"));
                    ghe.setMaMayBay(rs.getString("MaMayBay"));
                    ghe.setSoGhe(rs.getString("SoGhe"));
                    ghe.setGiaGhe(rs.getBigDecimal("GiaGhe"));
                    return ghe;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 4. Thêm ghế mới
    public boolean insertGheMayBay(GheMayBay ghe) {
        String sql = "INSERT INTO GheMayBay (MaGhe, MaMayBay, SoGhe, GiaGhe) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ghe.getMaGhe());
            ps.setString(2, ghe.getMaMayBay());
            ps.setString(3, ghe.getSoGhe());
            
            // CẬP NHẬT: Truyền BigDecimal vào SQL
            ps.setBigDecimal(4, ghe.getGiaGhe());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Cập nhật thông tin ghế
    public boolean updateGheMayBay(GheMayBay ghe) {
        String sql = "UPDATE GheMayBay SET MaMayBay = ?, SoGhe = ?, GiaGhe = ? WHERE MaGhe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ghe.getMaMayBay());
            ps.setString(2, ghe.getSoGhe());
            
            // CẬP NHẬT: Truyền BigDecimal vào SQL
            ps.setBigDecimal(3, ghe.getGiaGhe());
            
            ps.setString(4, ghe.getMaGhe());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. Xóa ghế
    public boolean deleteGheMayBay(String maGhe) {
        String sql = "DELETE FROM GheMayBay WHERE MaGhe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maGhe);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}