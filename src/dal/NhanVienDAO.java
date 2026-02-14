package dal;

import model.NhanVien;
import java.sql.*;
import java.util.ArrayList;
import db.*; 

public class NhanVienDAO {

    public ArrayList<NhanVien> selectAll() {
        ArrayList<NhanVien> list = new ArrayList<>();
 
        String sql = "SELECT * FROM NhanVien";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setMaNguoiDung(rs.getString("maNguoiDung"));
                nv.setChucVu(rs.getString("chucVu"));
                
                if (rs.getDate("ngayVaoLam") != null) {
                    nv.setNgayVaoLam(rs.getDate("ngayVaoLam").toLocalDate());
                }
                
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, maNguoiDung, chucVu, ngayVaoLam) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getMaNguoiDung()); 
            ps.setString(3, nv.getChucVu());
            ps.setDate(4, java.sql.Date.valueOf(nv.getNgayVaoLam())); 
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET chucVu=?, ngayVaoLam=? WHERE maNV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getChucVu());
            ps.setDate(2, java.sql.Date.valueOf(nv.getNgayVaoLam()));
            ps.setString(3, nv.getMaNV());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
