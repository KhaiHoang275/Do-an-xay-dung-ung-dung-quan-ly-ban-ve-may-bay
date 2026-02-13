package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import entity.HeSoGia;

public class HeSoGiaDAO {

    public ArrayList<HeSoGia> selectAll() {
        ArrayList<HeSoGia> list = new ArrayList<>();
        String sql = "SELECT * FROM HeSoGia";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HeSoGia hsg = new HeSoGia();
                hsg.setMaHeSoGia(rs.getString("MaHeSoGia"));
                hsg.setHeSo(rs.getFloat("HeSo"));
                hsg.setSoGioDatTruoc(rs.getFloat("SoGioDatTruoc"));
                list.add(hsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

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

    public boolean insert(HeSoGia hsg) {
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

    public boolean update(HeSoGia hsg) {
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

    public boolean delete(String maHeSoGia) {
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