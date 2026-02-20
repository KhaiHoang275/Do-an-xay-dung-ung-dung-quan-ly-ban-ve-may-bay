package dal;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import db.DBConnection;
import model.KhuyenMai;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class KhuyenMaiDAO {
    /**
     * Lay tat ca khuyen mai tu dtb
     */
    public List<KhuyenMai> getAll(){
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)){
            while (rs.next()){
                KhuyenMai km = mapResultSetToKhuyenMai(rs);
                list.add(km);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
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
            if(km.getNgayBD()!=null){
                ps.setDate(3, Date.valueOf(km.getNgayBD()));
            }else{
                ps.setNull(3, Types.DATE);
            }
            if(km.getNgayKT() != null){
                ps.setDate(4, Date.valueOf(km.getNgayKT()));
            }else{
                ps.setNull(4, Types.DATE);
            }
            ps.setBigDecimal(5, km.getGiaTri());
            ps.setString(6, km.getLoaiKM());
            ps.setString(7, km.getMoTa());
            ps.setBigDecimal(8, km.getDonHangToiThieu());
            ps.setInt(9, km.getSoLuongTong());
            ps.setInt(10, km.getSoLuongDaDung());
            ps.setBoolean(11, km.isApDungChoTatCa());
            ps.setString(12, km.getLoaiKhachApDung());
            ps.setInt(13, km.getGioiHanMoiKhach());
            ps.setString(14, km.getNguoiTao());
            ps.setBoolean(15, km.isTrangThai());
            if (km.getNgayTao() != null){
                ps.setTimestamp(16, Timestamp.valueOf(km.getNgayTao()));
            }else{
                ps.setNull(16, Types.TIMESTAMP);
            }
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
                    nguoiTao = ?,
                    ngayTao = ?
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
            ps.setTimestamp(15, Timestamp.valueOf(km.getNgayTao()));
            ps.setString(16, km.getMaKhuyenMai());
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
    public List<KhuyenMai> getKhuyenMaiDangHoatDong(){
        List<KhuyenMai> list = new ArrayList<>();
        LocalDate hienTai = LocalDate.now();
        String sql = "SELECT * FROM KhuyenMai WHERE trangThai = 1 " +
                "AND ngayBD <= ? AND ngayKT >= ? " +
                "AND soLuongDaDung < soLuongTong";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setDate(1, Date.valueOf(hienTai));
            ps.setDate(2, Date.valueOf(hienTai));
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    KhuyenMai km = mapResultSetToKhuyenMai(rs);
                    list.add(km);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     *  Lấy danh sách khuyến mãi hợp lệ cho một đơn hàng cụ thể
     *  * Khuyến mãi hợp lệ khi:
     *  - trangThai = 1
     *  - ngày hiện tại nằm trong khoảng ngayBD - ngayKT
     *  - soLuongDaDung < soLuongTong
     *  - tongTien >= donHangToiThieu
     *  - Nếu apDungChoTatCa = false thì loaiHanhKhach phải nằm trong loaiKhachApDung
     */

    public List<KhuyenMai> getKhuyenMaiHopLe(BigDecimal tongTien, String loaiHanhKhach){
        List<KhuyenMai> list = new ArrayList<>();
        LocalDate hienTai = LocalDate.now();
        try{
            List<KhuyenMai> tatCaKM = getKhuyenMaiDangHoatDong();
            for(KhuyenMai km : tatCaKM){
                if(tongTien.compareTo(km.getDonHangToiThieu()) < 0 ){
                    continue;
                }
                //kiem tra loai khach hang neu khog ap dung cho tat ca
                if(!km.isApDungChoTatCa()){
                    if(!isLoaiKhachHopLe(km.getLoaiKhachApDung(), loaiHanhKhach)){
                        continue;
                    }
                }
                list.add(km);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     *  Kiểm tra xem loại khách hàng có nằm trong danh sách áp dụng không
     *  Ví dụ: loaiKhachApDung = "Silver,Gold", loaiHanhKhach = "Gold" → true
     */
    private boolean isLoaiKhachHopLe(String loaiKhachApDung, String loaiHanhKhach){
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
}
