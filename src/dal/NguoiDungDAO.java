package dal;

import entity.NguoiDung;
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
                nd.setTrangThaiTK(rs.getString("trangThaiTK"));
                list.add(nd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NguoiDung nd) {
        String sql = "INSERT INTO NguoiDung (maNguoiDung, username, password, email, sdt, phanQuyen, trangThaiTK) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Lưu ý: ngayTao để mặc định SQL tự sinh (DEFAULT GETDATE()) nên không cần insert
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nd.getMaNguoiDung());
            ps.setString(2, nd.getUsername());
            ps.setString(3, nd.getPassword()); // Nên mã hóa trước khi truyền vào đây
            ps.setString(4, nd.getEmail());
            ps.setString(5, nd.getSoDienThoai());
            ps.setString(6, nd.getPhanQuyen());
            ps.setString(7, nd.getTrangThaiTK());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NguoiDung nd) {
        String sql = "UPDATE NguoiDung SET email=?, sdt=?, phanQuyen=?, trangThaiTK=? WHERE maNguoiDung=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nd.getEmail());
            ps.setString(2, nd.getSoDienThoai());
            ps.setString(3, nd.getPhanQuyen());
            ps.setString(4, nd.getTrangThaiTK());
            ps.setString(5, nd.getMaNguoiDung());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm kiểm tra đăng nhập
    public NguoiDung checkLogin(String username, String password) {
        String sql = "SELECT * FROM NguoiDung WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NguoiDung nd = new NguoiDung();
                    nd.setMaNguoiDung(rs.getString("maNguoiDung"));
                    nd.setUsername(rs.getString("username"));
                    nd.setPhanQuyen(rs.getString("phanQuyen"));
                    nd.setTrangThaiTK(rs.getString("trangThaiTK"));
                    return nd; // Trả về đối tượng nếu đúng
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu sai
    }
}
