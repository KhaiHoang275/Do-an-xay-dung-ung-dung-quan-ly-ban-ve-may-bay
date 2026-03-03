package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.DBConnection;
import model.TuyenBay;
import model.TrangThaiTuyenBay;

public class TuyenBayDAO {

    public ArrayList<TuyenBay> selectAll() {
        ArrayList<TuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM TuyenBay WHERE TrangThai != 'DA_XOA' OR TrangThai IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTuyenBay(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<TuyenBay> selectThungRac() {
        ArrayList<TuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM TuyenBay WHERE TrangThai = 'DA_XOA'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTuyenBay(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(TuyenBay tb) {
        String sql = "INSERT INTO TuyenBay (MaTuyenBay, SanBayDi, SanBayDen, KhoangCachKM, GiaGoc, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tb.getMaTuyenBay());
            ps.setString(2, tb.getSanBayDi());
            ps.setString(3, tb.getSanBayDen());
            ps.setFloat(4, tb.getKhoangCachKM());
            ps.setBigDecimal(5, tb.getGiaGoc());
            ps.setString(6, tb.getTrangThai() != null ? tb.getTrangThai().name() : TrangThaiTuyenBay.HOAT_DONG.name());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(TuyenBay tb) {
        String sql = "UPDATE TuyenBay SET SanBayDi = ?, SanBayDen = ?, KhoangCachKM = ?, GiaGoc = ?, TrangThai = ? WHERE MaTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tb.getSanBayDi());
            ps.setString(2, tb.getSanBayDen());
            ps.setFloat(3, tb.getKhoangCachKM());
            ps.setBigDecimal(4, tb.getGiaGoc());
            ps.setString(5, tb.getTrangThai() != null ? tb.getTrangThai().name() : TrangThaiTuyenBay.HOAT_DONG.name());
            ps.setString(6, tb.getMaTuyenBay());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(String maTuyenBay) {
        String sql = "UPDATE TuyenBay SET TrangThai = 'DA_XOA' WHERE MaTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTuyenBay);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean restore(String maTuyenBay) {
        String sql = "UPDATE TuyenBay SET TrangThai = 'HOAT_DONG' WHERE MaTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTuyenBay);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public TuyenBay selectById(String id) {
        String sql = "SELECT * FROM TuyenBay WHERE MaTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToTuyenBay(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean isRouteExists(String sanBayDi, String sanBayDen) {
        String sql = "SELECT COUNT(*) FROM TuyenBay WHERE SanBayDi = ? AND SanBayDen = ? AND (TrangThai != 'DA_XOA' OR TrangThai IS NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sanBayDi);
            ps.setString(2, sanBayDen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0; 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // HÀM DÙNG CHUNG ĐỂ MAP DỮ LIỆU
    private TuyenBay mapResultSetToTuyenBay(ResultSet rs) throws SQLException {
        TuyenBay tb = new TuyenBay();
        tb.setMaTuyenBay(rs.getString("MaTuyenBay"));
        tb.setSanBayDi(rs.getString("SanBayDi"));
        tb.setSanBayDen(rs.getString("SanBayDen"));
        tb.setKhoangCachKM(rs.getFloat("KhoangCachKM")); 
        tb.setGiaGoc(rs.getBigDecimal("GiaGoc"));
        
        String statusStr = rs.getString("TrangThai");
        if (statusStr != null) {
            try {
                tb.setTrangThai(TrangThaiTuyenBay.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                tb.setTrangThai(TrangThaiTuyenBay.HOAT_DONG);
            }
        } else {
            tb.setTrangThai(TrangThaiTuyenBay.HOAT_DONG);
        }
        return tb;
    }
}