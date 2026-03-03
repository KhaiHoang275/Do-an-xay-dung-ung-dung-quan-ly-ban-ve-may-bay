package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.HangVe;
import model.TrangThaiHangVe;

public class HangVeDAO {
    
    public ArrayList<HangVe> selectAll() {
        ArrayList<HangVe> list = new ArrayList<>();
        String sql = "SELECT * FROM HangVe WHERE TrangThai != 'DA_XOA' OR TrangThai IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HangVe hv = new HangVe();
                hv.setMaHangVe(rs.getString("MaHangVe"));
                hv.setTenHang(rs.getString("TenHang"));
                hv.setHeSoHangVe(rs.getFloat("HeSoHangVe"));
                
                String statusStr = rs.getString("TrangThai");
                if (statusStr != null) {
                    hv.setTrangThai(TrangThaiHangVe.valueOf(statusStr));
                } else {
                    hv.setTrangThai(TrangThaiHangVe.HOAT_DONG);
                }
                list.add(hv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<HangVe> selectThungRac() {
        ArrayList<HangVe> list = new ArrayList<>();
        String sql = "SELECT * FROM HangVe WHERE TrangThai = 'DA_XOA'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HangVe hv = new HangVe();
                hv.setMaHangVe(rs.getString("MaHangVe"));
                hv.setTenHang(rs.getString("TenHang"));
                hv.setHeSoHangVe(rs.getFloat("HeSoHangVe"));
                
                String statusStr = rs.getString("TrangThai");
                if (statusStr != null) {
                    hv.setTrangThai(TrangThaiHangVe.valueOf(statusStr));
                } else {
                    hv.setTrangThai(TrangThaiHangVe.HOAT_DONG);
                }
                list.add(hv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public HangVe selectById(String maHangVe) {
        String sql = "SELECT * FROM HangVe WHERE MaHangVe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHangVe);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HangVe hv = new HangVe();
                    hv.setMaHangVe(rs.getString("MaHangVe"));
                    hv.setTenHang(rs.getString("TenHang"));
                    hv.setHeSoHangVe(rs.getFloat("HeSoHangVe"));
                    
                    String statusStr = rs.getString("TrangThai");
                    if (statusStr != null) {
                        hv.setTrangThai(TrangThaiHangVe.valueOf(statusStr));
                    } else {
                        hv.setTrangThai(TrangThaiHangVe.HOAT_DONG);
                    }
                    return hv;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(HangVe hv) {
        String sql = "INSERT INTO HangVe (MaHangVe, TenHang, HeSoHangVe, TrangThai) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hv.getMaHangVe());
            ps.setString(2, hv.getTenHang());
            ps.setFloat(3, hv.getHeSoHangVe());
            ps.setString(4, hv.getTrangThai() != null ? hv.getTrangThai().name() : TrangThaiHangVe.HOAT_DONG.name());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(HangVe hv) {
        String sql = "UPDATE HangVe SET TenHang = ?, HeSoHangVe = ?, TrangThai = ? WHERE MaHangVe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hv.getTenHang());
            ps.setFloat(2, hv.getHeSoHangVe());
            ps.setString(3, hv.getTrangThai() != null ? hv.getTrangThai().name() : TrangThaiHangVe.HOAT_DONG.name());
            ps.setString(4, hv.getMaHangVe());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maHangVe) {
        String sql = "UPDATE HangVe SET TrangThai = 'DA_XOA' WHERE MaHangVe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHangVe);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restore(String maHangVe) {
        String sql = "UPDATE HangVe SET TrangThai = 'HOAT_DONG' WHERE MaHangVe = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHangVe);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}