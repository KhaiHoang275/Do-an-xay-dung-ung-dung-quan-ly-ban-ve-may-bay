package dal;

import java.math.BigDecimal;
import java.sql.*;
import db.DBConnection;

public class SuDungKhuyenMaiDAO {


    public int demSoLanSuDung(String maKhuyenMai, String maHK, Connection conn)
            throws SQLException{
        String sql = """
            SELECT COUNT(*) 
            FROM SuDungKhuyenMai
            WHERE maKhuyenMai = ?
              AND maHK = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            ps.setString(2, maHK);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        }
        return 0;
    }

    /**
     * Thêm lịch sử sử dụng khuyến mãi (dung trong transaction)
     */
    public boolean insertSuDungKhuyenMai(Connection conn,
                                         String maKhuyenMai,
                                         String maHK,
                                         String maGD,
                                         String maPhieuDatVe,
                                         BigDecimal giaTriGiamThucTe)
            throws SQLException{

        String sql = """
            INSERT INTO SuDungKhuyenMai
            (maKhuyenMai, maHK, maGD, maPhieuDatVe,
             ngaySuDung, giaTriGiamThucTe)
            VALUES (?, ?, ?, ?, GETDATE(), ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            ps.setString(2, maHK);
            if (maGD != null && !maGD.trim().isEmpty()){
                ps.setString(3, maGD);
            }else{
                ps.setNull(3, Types.VARCHAR);
            }

            if(maPhieuDatVe != null && !maPhieuDatVe.trim().isEmpty()){
                ps.setString(4, maPhieuDatVe);
            }else{
                ps.setNull(4, Types.VARCHAR);
            }
            ps.setBigDecimal(5, giaTriGiamThucTe);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw e;
        }
    }
}
