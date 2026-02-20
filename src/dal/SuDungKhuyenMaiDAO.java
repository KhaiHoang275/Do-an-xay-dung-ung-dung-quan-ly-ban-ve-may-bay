package dal;

import java.math.BigDecimal;
import java.sql.*;
import db.DBConnection;

public class SuDungKhuyenMaiDAO {

    /**
     * Đếm số lần khách đã sử dụng một khuyến mãi
     */
    public int demSoLanSuDung(String maKhuyenMai, String maHK) {
        String sql = "SELECT COUNT(*) FROM SuDungKhuyenMai " +
                "WHERE maKhuyenMai = ? AND maHK = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhuyenMai);
            ps.setString(2, maHK);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Thêm lịch sử sử dụng khuyến mãi (CẬP NHẬT THEO BẢNG MỚI)
     */
    public boolean insertSuDungKhuyenMai(String id, String maKhuyenMai, String maHK,
                                         String maGD, BigDecimal giaTriGiamThucTe) {

        String sql = "INSERT INTO SuDungKhuyenMai " +
                "(id, maKhuyenMai, maHK, maGD, ngaySuDung, giaTriGiamThucTe) " +
                "VALUES (?, ?, ?, ?, GETDATE(), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, maKhuyenMai);
            ps.setString(3, maHK);
            ps.setString(4, maGD);  // Có thể null (khi đặt vé)
            ps.setBigDecimal(5, giaTriGiamThucTe);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
