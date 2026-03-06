package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.DBConnection;
import model.HeSoGia;
import model.TrangThaiHeSoGia;

public class HeSoGiaDAO {

    public ArrayList<HeSoGia> selectAll() {
        ArrayList<HeSoGia> list = new ArrayList<>();
        String sql = "SELECT * FROM HeSoGia WHERE TrangThai != 'DA_XOA' OR TrangThai IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToHeSoGia(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<HeSoGia> selectThungRac() {
        ArrayList<HeSoGia> list = new ArrayList<>();
        String sql = "SELECT * FROM HeSoGia WHERE TrangThai = 'DA_XOA'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToHeSoGia(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public HeSoGia selectById(String maHeSoGia) {
        String sql = "SELECT * FROM HeSoGia WHERE MaHeSoGia = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHeSoGia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToHeSoGia(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(HeSoGia hsg) {
        String sql = "INSERT INTO HeSoGia (MaHeSoGia, HeSo, SoGioDatTruoc, TrangThai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hsg.getMaHeSoGia());
            ps.setFloat(2, hsg.getHeSo());
            ps.setFloat(3, hsg.getSoGioDatTruoc());
            ps.setString(4, hsg.getTrangThai() != null ? hsg.getTrangThai().name() : TrangThaiHeSoGia.HOAT_DONG.name());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(HeSoGia hsg) {
        String sql = "UPDATE HeSoGia SET HeSo = ?, SoGioDatTruoc = ?, TrangThai = ? WHERE MaHeSoGia = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, hsg.getHeSo());
            ps.setFloat(2, hsg.getSoGioDatTruoc());
            ps.setString(3, hsg.getTrangThai() != null ? hsg.getTrangThai().name() : TrangThaiHeSoGia.HOAT_DONG.name());
            ps.setString(4, hsg.getMaHeSoGia());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(String maHeSoGia) {
        String sql = "UPDATE HeSoGia SET TrangThai = 'DA_XOA' WHERE MaHeSoGia = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHeSoGia);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean restore(String maHeSoGia) {
        String sql = "UPDATE HeSoGia SET TrangThai = 'HOAT_DONG' WHERE MaHeSoGia = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHeSoGia);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // HÀM DÙNG CHUNG ĐỂ MAP DỮ LIỆU
    private HeSoGia mapResultSetToHeSoGia(ResultSet rs) throws SQLException {
        HeSoGia hsg = new HeSoGia();
        hsg.setMaHeSoGia(rs.getString("MaHeSoGia"));
        hsg.setHeSo(rs.getFloat("HeSo"));
        hsg.setSoGioDatTruoc(rs.getFloat("SoGioDatTruoc"));
        
        String statusStr = rs.getString("TrangThai");
        if (statusStr != null) {
            try {
                hsg.setTrangThai(TrangThaiHeSoGia.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                hsg.setTrangThai(TrangThaiHeSoGia.HOAT_DONG);
            }
        } else {
            hsg.setTrangThai(TrangThaiHeSoGia.HOAT_DONG);
        }
        return hsg;
    }

    public BigDecimal layHeSoTheoSoGio(long soGio) {
        String sql = """
            SELECT TOP 1 heSo
            FROM HeSoGia
            WHERE soGioDatTruoc <= ?
            AND trangThai = N'Hoạt động'
            ORDER BY soGioDatTruoc DESC
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, soGio);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("heSo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ONE;
    }
}