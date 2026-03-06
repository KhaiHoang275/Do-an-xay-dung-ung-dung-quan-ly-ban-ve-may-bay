package dal;

import model.SanBay;
import java.sql.*;
import java.util.ArrayList;
import db.*; 

public class SanBayDAO {

    public ArrayList<SanBay> selectAll() {
        ArrayList<SanBay> list = new ArrayList<>();
        String sql = "SELECT * FROM SanBay";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                SanBay sb = new SanBay();
                sb.setMaSanBay(rs.getString("maSanBay"));
                sb.setTenSanBay(rs.getString("tenSanBay"));
                sb.setQuocGia(rs.getString("quocGia"));
                sb.setThanhPho(rs.getString("thanhPho"));
                sb.setTrangThai(SanBay.TrangThai.fromString(rs.getString("trangThai")));
                list.add(sb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(SanBay sb) {
        String sql = "INSERT INTO SanBay (maSanBay, tenSanBay, quocGia, thanhPho, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sb.getMaSanBay());
            ps.setString(2, sb.getTenSanBay());
            ps.setString(3, sb.getQuocGia());
            ps.setString(4, sb.getThanhPho());
            ps.setString(5, sb.getTrangThai().getValue());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(SanBay sb) {
        String sql = "UPDATE SanBay SET tenSanBay=?, quocGia=?, thanhPho=?, trangThai=? WHERE maSanBay=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sb.getTenSanBay());
            ps.setString(2, sb.getQuocGia());
            ps.setString(3, sb.getThanhPho());
            ps.setString(4, sb.getTrangThai().getValue());
            ps.setString(5, sb.getMaSanBay());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // SOFT DELETE: Chuyển trạng thái thay vì xóa vĩnh viễn
    public boolean delete(String maSanBay) {
        String sql = "UPDATE SanBay SET trangThai = N'Ngừng hoạt động' WHERE maSanBay=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maSanBay);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}