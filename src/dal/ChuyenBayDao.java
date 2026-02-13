package dal;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import db.DBConnection;
import model.ChuyenBay;

public class ChuyenBayDAO {

    public ArrayList<ChuyenBay> getAllChuyenBay() {
        ArrayList<ChuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenBay";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToChuyenBay(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertChuyenBay(ChuyenBay cb) {
        String sql = "INSERT INTO ChuyenBay (MaChuyenBay, MaTuyenBay, MaMayBay, MaHeSoGia, NgayGioDi, NgayGioDen, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cb.getMaChuyenBay());
            ps.setString(2, cb.getMaTuyenBay());
            ps.setString(3, cb.getMaMayBay());
            ps.setString(4, cb.getMaHeSoGia());
            
            ps.setTimestamp(5, Timestamp.valueOf(cb.getNgayGioDi()));
            ps.setTimestamp(6, Timestamp.valueOf(cb.getNgayGioDen()));
            
            ps.setBoolean(7, cb.isTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateChuyenBay(ChuyenBay cb) {
        String sql = "UPDATE ChuyenBay SET MaTuyenBay=?, MaMayBay=?, MaHeSoGia=?, NgayGioDi=?, NgayGioDen=?, TrangThai=? WHERE MaChuyenBay=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cb.getMaTuyenBay());
            ps.setString(2, cb.getMaMayBay());
            ps.setString(3, cb.getMaHeSoGia());
            
            ps.setTimestamp(4, Timestamp.valueOf(cb.getNgayGioDi()));
            ps.setTimestamp(5, Timestamp.valueOf(cb.getNgayGioDen()));
            
            ps.setBoolean(6, cb.isTrangThai());
            
            ps.setString(7, cb.getMaChuyenBay());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteChuyenBay(String maChuyenBay) {
        String sql = "DELETE FROM ChuyenBay WHERE MaChuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChuyenBay);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ChuyenBay selectById(String id) {
        String sql = "SELECT * FROM ChuyenBay WHERE MaChuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChuyenBay(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 6. Tìm kiếm chuyến bay (Chức năng quan trọng nhất cho khách hàng)
    // Đầu vào là LocalDate (Ngày), nhưng so sánh với LocalDateTime trong DB
    public ArrayList<ChuyenBay> searchFlight(String sanBayDi, String sanBayDen, LocalDate ngayDi) {
        ArrayList<ChuyenBay> list = new ArrayList<>();
        
        // JOIN với bảng TuyenBay để lọc theo tên sân bay
        // Dùng hàm DATE() trong SQL để lấy phần ngày của cột NgayGioDi
        String sql = "SELECT cb.* FROM ChuyenBay cb " +
                     "JOIN TuyenBay tb ON cb.MaTuyenBay = tb.MaTuyenBay " +
                     "WHERE tb.SanBayDi = ? AND tb.SanBayDen = ? " +
                     "AND DATE(cb.NgayGioDi) = ? " +
                     "AND cb.TrangThai = 1"; // Chỉ lấy chuyến đang hoạt động (true)

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sanBayDi);
            ps.setString(2, sanBayDen);
            // Convert LocalDate -> java.sql.Date
            ps.setDate(3, java.sql.Date.valueOf(ngayDi));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToChuyenBay(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private ChuyenBay mapResultSetToChuyenBay(ResultSet rs) throws SQLException {
        ChuyenBay cb = new ChuyenBay();
        cb.setMaChuyenBay(rs.getString("MaChuyenBay"));
        cb.setMaTuyenBay(rs.getString("MaTuyenBay"));
        cb.setMaMayBay(rs.getString("MaMayBay"));
        cb.setMaHeSoGia(rs.getString("MaHeSoGia"));
        
        Timestamp tsDi = rs.getTimestamp("NgayGioDi");
        if (tsDi != null) {
            cb.setNgayGioDi(tsDi.toLocalDateTime());
        }

        Timestamp tsDen = rs.getTimestamp("NgayGioDen");
        if (tsDen != null) {
            cb.setNgayGioDen(tsDen.toLocalDateTime());
        }

        cb.setTrangThai(rs.getBoolean("TrangThai"));
        
        return cb;
    }
}