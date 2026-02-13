package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.HeSoGia;

public class HeSoGiaDAO {

    // 1. Lấy danh sách tất cả Hệ số giá
    public ArrayList<HeSoGia> getAllHeSoGia() {
        ArrayList<HeSoGia> list = new ArrayList<>();
        String sql = "SELECT * FROM HeSoGia";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HeSoGia hsg = new HeSoGia();
                hsg.setMaHeSoGia(rs.getString("MaHeSoGia"));
                
                // Sử dụng getFloat để khớp với Entity
                hsg.setHeSo(rs.getFloat("HeSo"));
                hsg.setSoGioDatTruoc(rs.getFloat("SoGioDatTruoc"));
                
                list.add(hsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Tìm Hệ số giá theo Mã (ID)
    public HeSoGia selectById(String maHeSoGia) {
        String sql = "SELECT * FROM HeSoGia WHERE MaHeSoGia = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHeSoGia);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HeSoGia hsg = new HeSoGia();
                    hsg.setMaHeSoGia(rs.getString("MaHeSoGia"));
                    hsg.setHeSo(rs.getFloat("HeSo"));
                    hsg.setSoGioDatTruoc(rs.getFloat("SoGioDatTruoc"));
                    return hsg;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. Thêm Hệ số giá mới
    public boolean insertHeSoGia(HeSoGia hsg) {
        String sql = "INSERT INTO HeSoGia (MaHeSoGia, HeSo, SoGioDatTruoc) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hsg.getMaHeSoGia());
            ps.setFloat(2, hsg.getHeSo());
            ps.setFloat(3, hsg.getSoGioDatTruoc());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Cập nhật Hệ số giá
    public boolean updateHeSoGia(HeSoGia hsg) {
        String sql = "UPDATE HeSoGia SET HeSo = ?, SoGioDatTruoc = ? WHERE MaHeSoGia = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setFloat(1, hsg.getHeSo());
            ps.setFloat(2, hsg.getSoGioDatTruoc());
            ps.setString(3, hsg.getMaHeSoGia());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Xóa Hệ số giá
    public boolean deleteHeSoGia(String maHeSoGia) {
        String sql = "DELETE FROM HeSoGia WHERE MaHeSoGia = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHeSoGia);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}