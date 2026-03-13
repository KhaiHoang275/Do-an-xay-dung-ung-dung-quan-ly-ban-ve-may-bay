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
     * Lay tat ca khuyen mai tu dtb (chi lay isDeleted = 0)
     */
    public List<KhuyenMai> getAll(Connection conn) throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE isDeleted = 0";
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

    // Thêm: Lấy tất cả đã xóa (isDeleted = 1)
    public List<KhuyenMai> getAllDeleted() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE isDeleted = 1";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToKhuyenMai(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public KhuyenMai getById(String maKhuyenMai){
        String sql = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ? AND isDeleted = 0";
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
            gioiHanMoiKhach, trangThai, nguoiTao, ngayTao, isDeleted)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
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
                WHERE maKhuyenMai = ? AND isDeleted = 0
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

    // Sửa: Soft delete (update isDeleted = 1)
    public boolean delete(KhuyenMai km){
        String sql = "UPDATE KhuyenMai SET isDeleted = 1 WHERE maKhuyenMai = ? AND isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, km.getMaKhuyenMai());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm: Khôi phục (update isDeleted = 0)
    public boolean restore(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET isDeleted = 0 WHERE maKhuyenMai = ? AND isDeleted = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, km.getMaKhuyenMai());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lay ds khuyen mai dang hoat dong
     * - trang thai = 1 (true)
     * - ngay ngay hien tai nam trong khoang ngayBD-ngayKT
     * - ngay soLuongDaDung < soLuongTong
     * - isDeleted = 0
     */
    public List<KhuyenMai> getKhuyenMaiDangHoatDong(Connection conn) throws SQLException{
        List<KhuyenMai> list = new ArrayList<>();
        LocalDate hienTai = LocalDate.now();
        String sql = "SELECT * FROM KhuyenMai WHERE trangThai = 1 " +
                "AND ngayBD <= ? AND ngayKT >= ? " +
                "AND soLuongDaDung < soLuongTong AND isDeleted = 0";
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
        // Thêm cho thùng rác
        km.setDeleted(rs.getBoolean("isDeleted"));
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
                "AND trangThai = 1 AND isDeleted = 0";
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

    
    public KhuyenMai getByIdWithConnection(Connection conn, String maKhuyenMai)
            throws SQLException{
        String sql = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ? AND isDeleted = 0";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return mapResultSetToKhuyenMai(rs);
            }
        }
        return null;
    } 

    public java.util.List<model.KhuyenMai> getKhuyenMaiHopLe() {
        java.util.List<model.KhuyenMai> list = new java.util.ArrayList<>();
        // Lọc: trạng thái = 1, Ngày hiện tại nằm trong khoảng ngayBD và ngayKT, còn số lượng
        String sql = "SELECT * FROM KhuyenMai WHERE trangThai = 1 " +
                     "AND CAST(GETDATE() AS DATE) BETWEEN ngayBD AND ngayKT " +
                     "AND soLuongDaDung < soLuongTong";
                     
        try (java.sql.Connection conn = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                model.KhuyenMai km = new model.KhuyenMai();
                km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
                km.setTenKM(rs.getString("tenKM"));
                km.setLoaiKM(rs.getString("loaiKM"));
                km.setGiaTri(rs.getBigDecimal("giaTri"));
                list.add(km);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi load khuyến mãi: " + e.getMessage());
        }
        return list;
    }
}