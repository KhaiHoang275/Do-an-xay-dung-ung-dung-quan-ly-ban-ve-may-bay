package dal;

import entity.SanBay;
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
                list.add(sb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean insert(SanBay sb) {
        String sql = "INSERT INTO SanBay (maSanBay, tenSanBay, quocGia, thanhPho) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sb.getMaSanBay());
            ps.setString(2, sb.getTenSanBay());
            ps.setString(3, sb.getQuocGia());
            ps.setString(4, sb.getThanhPho());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean update(SanBay sb) {
        String sql = "UPDATE SanBay SET tenSanBay=?, quocGia=?, thanhPho=? WHERE maSanBay=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sb.getTenSanBay());
            ps.setString(2, sb.getQuocGia());
            ps.setString(3, sb.getThanhPho());
            ps.setString(4, sb.getMaSanBay());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maSanBay) {
        String sql = "DELETE FROM SanBay WHERE maSanBay=?";
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
