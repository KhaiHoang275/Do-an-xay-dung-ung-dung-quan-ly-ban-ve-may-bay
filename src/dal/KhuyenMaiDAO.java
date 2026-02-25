package dal;

import java.math.BigDecimal;
import java.sql.*;
import db.DBConnection;
import model.KhuyenMai;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class KhuyenMaiDAO {
    /**
     * Lay tat ca khuyen mai tu dtb
     */
    public List<KhuyenMai> getAll(Connection conn) throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToKhuyenMai(rs));
            }
        }
        return list;
    }

    // Giữ overload cũ cho các chỗ gọi không transaction
    public List<KhuyenMai> getAll() {
        try (Connection conn = DBConnection.getConnection()) {
            return getAll(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public KhuyenMai getById(String maKhuyenMai){
        String sql = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ?";
        KhuyenMai km = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, maKhuyenMai);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    km = mapResultSetToKhuyenMai(rs);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return km;
    }

    public boolean insert (KhuyenMai km){
        String sql = """ 
            INSERT INTO KhuyenMai 
            (maKhuyenMai, tenKM, moTa, loaiKM, giaTri,
            donHangToiThieu, soLuongTong, soLuongDaDung,
            ngayBD, ngayKT, apDungChoTatCa, loaiKhachApDung,
            gioiHanMoiKhach, trangThai, nguoiTao, ngayTao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getTenKM());
            ps.setString(3, km.getMoTa());
            ps.setString(4, km.getLoaiKM());
            ps.setBigDecimal(5, km.getGiaTri());
            ps.setBigDecimal(6, km.getDonHangToiThieu());
            ps.setInt(7, km.getSoLuongTong());
            ps.setInt(8, km.getSoLuongDaDung());

            if (km.getNgayBD() != null)
                ps.setDate(9, Date.valueOf(km.getNgayBD()));
            else
                ps.setNull(9, Types.DATE);

            if (km.getNgayKT() != null)
                ps.setDate(10, Date.valueOf(km.getNgayKT()));
            else
                ps.setNull(10, Types.DATE);

            ps.setBoolean(11, km.isApDungChoTatCa());
            ps.setString(12, km.getLoaiKhachApDung());
            ps.setInt(13, km.getGioiHanMoiKhach());
            ps.setBoolean(14, km.isTrangThai());
            ps.setString(15, km.getNguoiTao());

            if (km.getNgayTao() != null)
                ps.setTimestamp(16, Timestamp.valueOf(km.getNgayTao()));
            else
                ps.setNull(16, Types.TIMESTAMP);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean update(KhuyenMai km){
        String sql = """
                UPDATE KhuyenMai
                SET tenKM = ?,
                    moTa = ?,
                    loaiKM = ?,
                    giaTri = ?,
                    donHangToiThieu = ?,
                    soLuongTong = ?,
                    soLuongDaDung = ?,
                    ngayBD = ?,
                    ngayKT = ?,
                    apDungChoTatCa = ?,
                    loaiKhachApDung = ?,
                    gioiHanMoiKhach = ?,
                    trangThai = ?,
                    nguoiTao = ?
                WHERE maKhuyenMai = ?
                """;
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, km.getTenKM());
            ps.setString(2, km.getMoTa());
            ps.setString(3, km.getLoaiKM());
            ps.setBigDecimal(4, km.getGiaTri());
            ps.setBigDecimal(5, km.getDonHangToiThieu());
            ps.setInt(6, km.getSoLuongTong());
            ps.setInt(7, km.getSoLuongDaDung());
            ps.setDate(8, java.sql.Date.valueOf(km.getNgayBD()));
            ps.setDate(9, java.sql.Date.valueOf(km.getNgayKT()));
            ps.setBoolean(10, km.isApDungChoTatCa());
            ps.setString(11, km.getLoaiKhachApDung());
            ps.setInt(12, km.getGioiHanMoiKhach());
            ps.setBoolean(13, km.isTrangThai());
            ps.setString(14, km.getNguoiTao());
            ps.setString(15, km.getMaKhuyenMai());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }

    public boolean delete(KhuyenMai km){
        String sql = "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, km.getMaKhuyenMai());
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lay ds khuyen mai dang hoat dong
     * - trang thai = 1 (true)
     * - ngay hien tai nam trong khoang ngayBD-ngayKT
     * -soLuongDaDung < soLuongTong
     */
    public List<KhuyenMai> getKhuyenMaiDangHoatDong(Connection conn) throws SQLException{
        List<KhuyenMai> list = new ArrayList<>();
        LocalDate hienTai = LocalDate.now();
        String sql = "SELECT * FROM KhuyenMai WHERE trangThai = 1 " +
                "AND ngayBD <= ? AND ngayKT >= ? " +
                "AND soLuongDaDung < soLuongTong";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setDate(1, Date.valueOf(hienTai));
            ps.setDate(2, Date.valueOf(hienTai));
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    KhuyenMai km = mapResultSetToKhuyenMai(rs);
                    list.add(km);
                }
            }
        }
        return list;
    }

    /**
     *  Kiểm tra xem loại khách hàng có nằm trong danh sách áp dụng không
     *  Ví dụ: loaiKhachApDung = "Silver,Gold", loaiHanhKhach = "Gold" → true
     */
    public boolean isLoaiKhachHopLe(String loaiKhachApDung, String loaiHanhKhach){
        if(loaiKhachApDung == null || loaiKhachApDung.trim().isEmpty()) return false;
        String[] ds = loaiKhachApDung.split(",");
        for(String loai : ds){
            if(loai.trim().equalsIgnoreCase(loaiHanhKhach)) return true;
        }
        return false;
    }


    private KhuyenMai mapResultSetToKhuyenMai(ResultSet rs) throws SQLException{
        KhuyenMai km = new KhuyenMai();
        km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
        km.setTenKM(rs.getString("tenKM"));
        km.setMoTa(rs.getString("moTa"));
        km.setLoaiKM(rs.getString("loaiKM"));
        km.setGiaTri(rs.getBigDecimal("giaTri"));
        km.setDonHangToiThieu(rs.getBigDecimal("donHangToiThieu"));
        km.setSoLuongTong(rs.getInt("soLuongTong"));
        km.setSoLuongDaDung(rs.getInt("soLuongDaDung"));
        Date sqlDate = rs.getDate("ngayBD");
        if(sqlDate != null){
            km.setNgayBD(sqlDate.toLocalDate());
        }
        sqlDate = rs.getDate("ngayKT");
        if(sqlDate != null){
            km.setNgayKT(sqlDate.toLocalDate());
        }
        km.setApDungChoTatCa(rs.getBoolean("apDungChoTatCa"));
        km.setLoaiKhachApDung(rs.getString("loaiKhachApDung"));
        km.setGioiHanMoiKhach(rs.getInt("gioiHanMoiKhach"));
        km.setTrangThai(rs.getBoolean("trangThai"));
        km.setNguoiTao(rs.getString("nguoiTao"));
        Timestamp sqlTimestamp = rs.getTimestamp("ngayTao");
        if (sqlTimestamp != null){
            km.setNgayTao(sqlTimestamp.toLocalDateTime());
        }
        return km;
    }

    /**
     * Cập nhật soLuongDaDung trong Transaction
     * - dùng SQL atomic: UPDATE .. SET soLuongDaDung = soLuongDaDung + 1
     * - Tránh race condition (không read-then-write)
     * - Kiểm tra điều kiện trực tiếp trong SQL
     * - Nhận Connection từ BUS
     *
     *  @param conn Connection từ BUS
     *  @param maKhuyenMai Mã khuyến mãi
     *  @return true nếu update thành công (1 dòng affected)
     *  @throws SQLException nếu có lỗi
     */
    public boolean incrementSoLuongDaDung(Connection conn, String maKhuyenMai)
            throws SQLException{
        String sql = "UPDATE KhuyenMai " +
                     "SET soLuongDaDung = soLuongDaDung + 1 " +
                     "WHERE maKhuyenMai = ? " +
                     "AND soLuongDaDung < soLuongTong " +
                     "AND trangThai = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, maKhuyenMai);
            int rowsAffected = ps.executeUpdate();
            // kiem tra neu khong co dong nao bi update
            if(rowsAffected == 0){
                throw new SQLException(
                        "Không thể cập nhật khuyến mãi, có thể khuyến mãi đã hết hoặc không còn hiệu lực"
                );
            }
            return true;
        }
    }

    /**
     * Lấy thông tin khuyến mãi (để kiểm tra điều kiện trước khi bị áp dụng)
     * Dùng trong transaction để tránh dirty read
     */
    public KhuyenMai getByIdWithConnection(Connection conn, String maKhuyenMai)
            throws SQLException{
        String sql = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return mapResultSetToKhuyenMai(rs);
            }
        }
        return null;
    }
}
