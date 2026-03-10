package dal;

import model.NguoiDung;
import java.sql.*;
import java.util.ArrayList; 
import db.*; 

public class NguoiDungDAO {

    public ArrayList<NguoiDung> selectAll() {
        ArrayList<NguoiDung> list = new ArrayList<>();
        String sql = "SELECT * FROM NguoiDung";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                NguoiDung nd = new NguoiDung();
                nd.setMaNguoiDung(rs.getString("maNguoiDung"));
                nd.setUsername(rs.getString("username"));
                nd.setPassword(rs.getString("password"));
                nd.setEmail(rs.getString("email"));
                nd.setSoDienThoai(rs.getString("sdt"));
                
                if (rs.getTimestamp("ngayTao") != null) {
                    nd.setNgayTao(rs.getTimestamp("ngayTao").toLocalDateTime().toLocalDate());
                }
                
                nd.setPhanQuyen(rs.getString("phanQuyen"));
                nd.setTrangThaiTK(NguoiDung.TrangThai.fromString(rs.getString("trangThaiTK")));
                list.add(nd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NguoiDung nd) {
        String sql = "INSERT INTO NguoiDung (maNguoiDung, username, password, email, sdt, ngayTao, phanQuyen, trangThaiTK) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nd.getMaNguoiDung());
            ps.setString(2, nd.getUsername());
            ps.setString(3, nd.getPassword());
            ps.setString(4, nd.getEmail());
            ps.setString(5, nd.getSoDienThoai());
            ps.setDate(6, java.sql.Date.valueOf(nd.getNgayTao()));
            ps.setString(7, nd.getPhanQuyen());
            ps.setString(8, nd.getTrangThaiTK().getValue());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NguoiDung nd) {
        String sql = "UPDATE NguoiDung SET username=?, password=?, email=?, sdt=?, phanQuyen=?, trangThaiTK=? WHERE maNguoiDung=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nd.getUsername());
            ps.setString(2, nd.getPassword());
            ps.setString(3, nd.getEmail());
            ps.setString(4, nd.getSoDienThoai());
            ps.setString(5, nd.getPhanQuyen());
            ps.setString(6, nd.getTrangThaiTK().getValue());
            ps.setString(7, nd.getMaNguoiDung());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maNguoiDung) {
        String sql = "UPDATE NguoiDung SET trangThaiTK = N'Ngừng hoạt động' WHERE maNguoiDung = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public NguoiDung getByMaNguoiDung(String maNguoiDung) {
        String sql = "SELECT * FROM NguoiDung WHERE maNguoiDung = ?";
        NguoiDung nd = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNguoiDung);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nd = new NguoiDung();
                nd.setMaNguoiDung(rs.getString("maNguoiDung"));
                nd.setUsername(rs.getString("username"));
                nd.setPassword(rs.getString("password"));
                nd.setEmail(rs.getString("email"));
                nd.setSoDienThoai(rs.getString("sdt"));
                if (rs.getTimestamp("ngayTao") != null) {
                    nd.setNgayTao(rs.getTimestamp("ngayTao").toLocalDateTime().toLocalDate());
                } 
                nd.setPhanQuyen(rs.getString("phanQuyen"));
                nd.setTrangThaiTK(NguoiDung.TrangThai.fromString(rs.getString("trangThaiTK")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nd;
    }

    public NguoiDung checkLogin(String username, String password) {
        String sql = "SELECT * FROM NguoiDung WHERE username = ? AND password = ? AND trangThaiTK = 'HOAT_DONG'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NguoiDung nd = new NguoiDung();
                nd.setMaNguoiDung(rs.getString("maNguoiDung"));
                nd.setUsername(rs.getString("username"));
                nd.setPassword(rs.getString("password"));
                nd.setEmail(rs.getString("email"));
                nd.setSoDienThoai(rs.getString("sdt"));
                if (rs.getTimestamp("ngayTao") != null) {
                    nd.setNgayTao(rs.getTimestamp("ngayTao").toLocalDateTime().toLocalDate());
                }
                nd.setPhanQuyen(rs.getString("phanQuyen"));
                nd.setTrangThaiTK(NguoiDung.TrangThai.fromString(rs.getString("trangThaiTK")));
                return nd;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}