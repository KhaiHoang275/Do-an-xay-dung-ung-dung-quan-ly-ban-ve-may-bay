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
                nv.setHoTen(rs.getString("hoTen"));
                nv.setChucVu(rs.getString("chucVu"));
                
                if (rs.getDate("ngayVaoLam") != null) {
                    nv.setNgayVaoLam(rs.getDate("ngayVaoLam").toLocalDate());
                }
                
                nv.setTrangThaiLamViec(NhanVien.TrangThai.fromString(rs.getString("trangThaiLamViec")));
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, maNguoiDung, hoTen, chucVu, ngayVaoLam, trangThaiLamViec) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getMaNguoiDung()); 
            ps.setString(3, nv.getHoTen());
            ps.setString(4, nv.getChucVu());
            ps.setDate(5, java.sql.Date.valueOf(nv.getNgayVaoLam())); 
            ps.setString(6, nv.getTrangThaiLamViec().getValue());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET maNguoiDung=?, hoTen=?, chucVu=?, ngayVaoLam=?, trangThaiLamViec=? WHERE maNV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getMaNguoiDung());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getChucVu());
            ps.setDate(4, java.sql.Date.valueOf(nv.getNgayVaoLam()));
            ps.setString(5, nv.getTrangThaiLamViec().getValue());
            ps.setString(6, nv.getMaNV());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maNV) {
        String sql = "UPDATE NhanVien SET trangThaiLamViec = N'Ngừng hoạt động' WHERE maNV=?";
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