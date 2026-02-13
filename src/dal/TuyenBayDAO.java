package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;


import db.DBConnection;
import model.TuyenBay;

public class TuyenBayDAO {
    public ArrayList<TuyenBay> getAllTuyenBay() {
        ArrayList<TuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM TuyenBay";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                TuyenBay tb = new TuyenBay();
                tb.setMaTuyenBay(rs.getString("MaTuyenBay"));
                tb.setSanBayDi(rs.getString("SanBayDi"));
                tb.setSanBayDen(rs.getString("SanBayDen"));
                tb.setKhoangCach(rs.getFloat("KhoangCach"));
                tb.setGiaGoc(rs.getBigDecimal("GiaGoc"));
                list.add(tb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertTuyenBay(TuyenBay tb) {
        String sql = "INSERT INTO TuyenBay (MaTuyenBay, SanBayDi, SanBayDen, KhoangCach, GiaGoc) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tb.getMaTuyenBay());
            ps.setString(2, tb.getSanBayDi());
            ps.setString(3, tb.getSanBayDen());
            ps.setFloat(4, tb.getKhoangCach());
            ps.setBigDecimal(5, tb.getGiaGoc());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTuyenBay(TuyenBay tb) {
        String sql = "UPDATE TuyenBay SET SanBayDi = ?, SanBayDen = ?, KhoangCach = ?, GiaGoc = ? WHERE MaTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tb.getSanBayDi());
            ps.setString(2, tb.getSanBayDen());
            ps.setFloat(3, tb.getKhoangCach());
            ps.setBigDecimal(4, tb.getGiaGoc());
            ps.setString(5, tb.getMaTuyenBay());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTuyenBay(String maTuyenBay) {
        String sql = "DELETE FROM TuyenBay WHERE MaTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTuyenBay);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
