package dal;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import model.ChuyenBay;
import model.TrangThaiChuyenBay;

public class ChuyenBayDAO {

    public ArrayList<ChuyenBay> selectAll() {
        ArrayList<ChuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenBay WHERE TrangThai != 'DA_XOA' OR TrangThai IS NULL";
        
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

    public ArrayList<ChuyenBay> selectThungRac() {
        ArrayList<ChuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenBay WHERE TrangThai = 'DA_XOA'";
        
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

    public boolean insert(ChuyenBay cb) {
        String sql = "INSERT INTO ChuyenBay (MaChuyenBay, MaTuyenBay, MaMayBay, MaHeSoGia, NgayGioDi, NgayGioDen, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cb.getMaChuyenBay());
            ps.setString(2, cb.getMaTuyenBay());
            ps.setString(3, cb.getMaMayBay());
            ps.setString(4, cb.getMaHeSoGia());
            
            if (cb.getNgayGioDi() != null) ps.setTimestamp(5, Timestamp.valueOf(cb.getNgayGioDi()));
            else ps.setNull(5, Types.TIMESTAMP);

            if (cb.getNgayGioDen() != null) ps.setTimestamp(6, Timestamp.valueOf(cb.getNgayGioDen()));
            else ps.setNull(6, Types.TIMESTAMP);

            ps.setString(7, cb.getTrangThai() != null ? cb.getTrangThai().name() : TrangThaiChuyenBay.CHUA_KHOI_HANH.name());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChuyenBay cb) {
        String sql = "UPDATE ChuyenBay SET MaTuyenBay=?, MaMayBay=?, MaHeSoGia=?, NgayGioDi=?, NgayGioDen=?, TrangThai=? WHERE MaChuyenBay=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cb.getMaTuyenBay());
            ps.setString(2, cb.getMaMayBay());
            ps.setString(3, cb.getMaHeSoGia());
            
            if (cb.getNgayGioDi() != null) ps.setTimestamp(4, Timestamp.valueOf(cb.getNgayGioDi()));
            else ps.setNull(4, Types.TIMESTAMP);

            if (cb.getNgayGioDen() != null) ps.setTimestamp(5, Timestamp.valueOf(cb.getNgayGioDen()));
            else ps.setNull(5, Types.TIMESTAMP);
            
            ps.setString(6, cb.getTrangThai() != null ? cb.getTrangThai().name() : TrangThaiChuyenBay.CHUA_KHOI_HANH.name());
            ps.setString(7, cb.getMaChuyenBay());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maChuyenBay) {
        String sql = "UPDATE ChuyenBay SET TrangThai = 'DA_XOA' WHERE MaChuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChuyenBay);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restore(String maChuyenBay) {
        String sql = "UPDATE ChuyenBay SET TrangThai = 'CHUA_KHOI_HANH' WHERE MaChuyenBay = ?";
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

    public ArrayList<ChuyenBay> searchFlight(String sanBayDi, String sanBayDen, LocalDate ngayDi) {
        ArrayList<ChuyenBay> list = new ArrayList<>();
        String sql = "SELECT cb.* FROM ChuyenBay cb " +
                     "JOIN TuyenBay tb ON cb.MaTuyenBay = tb.MaTuyenBay " +
                     "WHERE tb.SanBayDi = ? AND tb.SanBayDen = ? " +
                     "AND CAST(cb.NgayGioDi AS DATE) = ? " +
                     "AND cb.TrangThai = 'CHUA_KHOI_HANH'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sanBayDi);
            ps.setString(2, sanBayDen);
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

    public List<ChuyenBay> findByMaTuyenBay(String maTuyenBay) {
        List<ChuyenBay> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenBay WHERE maTuyenBay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTuyenBay);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToChuyenBay(rs)); // Giả định có phương thức mapResultSet để chuyển ResultSet thành ChuyenBay
            }
        } catch (SQLException e) {
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
        if (tsDi != null) cb.setNgayGioDi(tsDi.toLocalDateTime());

        Timestamp tsDen = rs.getTimestamp("NgayGioDen");
        if (tsDen != null) cb.setNgayGioDen(tsDen.toLocalDateTime());

        String statusStr = rs.getString("TrangThai");
        if (statusStr != null) {
            try {

                cb.setTrangThai(TrangThaiChuyenBay.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                cb.setTrangThai(TrangThaiChuyenBay.CHUA_KHOI_HANH);
            }
        } else {
            cb.setTrangThai(TrangThaiChuyenBay.CHUA_KHOI_HANH);
        }
        
        return cb;
    }

    public BigDecimal layGiaCoBan(String maChuyenBay) {
        String sql = """
            SELECT tb.giaGoc
            FROM ChuyenBay cb
            JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay
            WHERE cb.maChuyenBay = ?
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChuyenBay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("giaGoc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public LocalDateTime layNgayBay(String maChuyenBay) {

        String sql = "SELECT ngayGioDi FROM ChuyenBay WHERE maChuyenBay = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maChuyenBay);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("ngayGioDi");
                return ts.toLocalDateTime();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String layMaMayBay(Connection conn, String maChuyenBay){
        try{
            String sql = "SELECT maMayBay FROM ChuyenBay WHERE maChuyenBay = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maChuyenBay);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getString("maMayBay");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public String layMaMayBay(String maChuyenBay) {
        // Tự mở kết nối ở đây
        try (Connection conn = db.DBConnection.getConnection()) { 
            String sql = "SELECT maMayBay FROM ChuyenBay WHERE maChuyenBay = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maChuyenBay);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("maMayBay");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}