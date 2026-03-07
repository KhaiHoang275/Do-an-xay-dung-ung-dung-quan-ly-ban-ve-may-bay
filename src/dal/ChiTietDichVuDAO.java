package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.ChiTietDichVu;

public class ChiTietDichVuDAO {

    public ArrayList<ChiTietDichVu> selectAll() {
        ArrayList<ChiTietDichVu> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ChiTietDichVu";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ChiTietDichVu ct = new ChiTietDichVu();
                ct.setMaVe(rs.getString("maVe"));
                ct.setMaDichVu(rs.getString("maDichVu"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setThanhTien(rs.getBigDecimal("thanhTien"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietDichVu ct) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ChiTietDichVu (maVe, maDichVu, soLuong, thanhTien) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, ct.getMaVe());
            pst.setString(2, ct.getMaDichVu());
            pst.setInt(3, ct.getSoLuong());
            pst.setBigDecimal(4, ct.getThanhTien());
            
            if (pst.executeUpdate() > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}