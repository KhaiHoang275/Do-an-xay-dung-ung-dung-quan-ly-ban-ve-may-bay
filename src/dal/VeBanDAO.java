package dal;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import model.VeBan;

public class VeBanDAO {
    public ArrayList<VeBan> selectAll(){
        ArrayList<VeBan> list = new ArrayList<>();
        String sql = "SELECT * FROM VeBan";
        try (Connection con = DBConnection.getConnection();
            var ps = con.prepareStatement(sql);
            var rs = ps.executeQuery()){
                while(rs.next()){
                    VeBan vb = new VeBan();
                    vb.setMaVe(rs.getString("maVe"));
                    vb.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                    vb.setMaChuyenBay(rs.getString("maChuyenBay"));
                    vb.setMaHK(rs.getString("maHK"));
                    vb.setMaHangVe(rs.getString("maHangVe"));
                    vb.setMaGhe(rs.getString("maGhe"));
                    vb.setLoaiVe(rs.getString("loaiVe"));
                    vb.setLoaiHK(rs.getString("loaiHK"));
                    vb.setGiaVe(rs.getBigDecimal("giaVe"));
                    vb.setTrangThaiVe(rs.getString("trangThaiVe"));
                    list.add(vb);
                }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(VeBan vb){
        String sql = "INSERT INTO VeBan (maVe, maPhieuDatve, maChuyenBay, maHK, maHangVe, maGhe, loaiVe, loaiHK, giaVe, trangThaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection()){
            String maVe = generateMaVe(con);
            vb.setMaVe(maVe);
            PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, vb.getMaVe());
                ps.setString(2, vb.getMaPhieuDatVe());
                ps.setString(3, vb.getMaChuyenBay());
                ps.setString(4, vb.getMaHK());
                ps.setString(5, vb.getMaHangVe());
                ps.setString(6, vb.getMaGhe());
                ps.setString(7, vb.getLoaiVe());
                ps.setString(8, vb.getLoaiHK());
                ps.setBigDecimal(9, vb.getGiaVe());
                ps.setString(10, vb.getTrangThaiVe());

                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insert(VeBan v, Connection conn) throws SQLException {
        String sql = """
            INSERT INTO VeBan
            (maVe, maPhieuDatVe, maChuyenBay, maHK, maGhe,
            maHangVe, loaiVe, loaiHK, giaVe, trangThaiVe)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String maVe = generateMaVe(conn);
        v.setMaVe(maVe);

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, v.getMaVe());
        ps.setString(2, v.getMaPhieuDatVe());
        ps.setString(3, v.getMaChuyenBay());
        ps.setString(4, v.getMaHK());
        ps.setString(5, v.getMaGhe());
        ps.setString(6, v.getMaHangVe());
        ps.setString(7, v.getLoaiVe());
        ps.setString(8, v.getLoaiHK());
        ps.setBigDecimal(9, v.getGiaVe());
        ps.setString(10, v.getTrangThaiVe());

        return ps.executeUpdate() > 0;
    }

    public boolean update(VeBan vb){
        String sql = "UPDATE VeBan SET maPhieuDatVe=?, maChuyenBay=?, maHK=?, maHangVe=?, maGhe=?, loaiVe=?, loaiHK=?, giaVe=?, trangThaiVe=? WHERE maVe=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, vb.getMaPhieuDatVe());
                ps.setString(2, vb.getMaChuyenBay());
                ps.setString(3, vb.getMaHK());
                ps.setString(4, vb.getMaHangVe());
                ps.setString(5, vb.getMaGhe());
                ps.setString(6, vb.getLoaiVe());
                ps.setString(7, vb.getLoaiHK());
                ps.setBigDecimal(8, vb.getGiaVe());
                ps.setString(9, vb.getTrangThaiVe());
                ps.setString(10, vb.getMaVe());

                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maVe){
        String sql = "DELETE FROM VeBan WHERE maVe=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, maVe);
                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public VeBan selectById(String maVe){
        String sql = "SELECT * FROM VeBan WHERE maVe=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, maVe);
                var rs = ps.executeQuery();
                if(rs.next()){
                    VeBan vb = new VeBan();
                    vb.setMaVe(rs.getString("maVe"));
                    vb.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                    vb.setMaChuyenBay(rs.getString("maChuyenBay"));
                    vb.setMaHK(rs.getString("maHK"));
                    vb.setMaHangVe(rs.getString("maHangVe"));
                    vb.setMaGhe(rs.getString("maGhe"));
                    vb.setLoaiVe(rs.getString("loaiVe"));
                    vb.setLoaiHK(rs.getString("loaiHK"));
                    vb.setGiaVe(rs.getBigDecimal("giaVe"));
                    vb.setTrangThaiVe(rs.getString("trangThaiVe"));
                    return vb;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkAvailable(String maChuyenBay, String maGhe){
        String sql = """
                SELECT COUNT(*)
                FROM VeBan
                WHERE MaChuyenBay = ?
                AND MaGhe = ?
                AND TrangThaiVe <> 'Đã hủy'
                """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maChuyenBay);
                ps.setString(2, maGhe);

                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getInt(1) == 0;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(Connection conn, String maVe, String trangThai) throws SQLException{
        String sql = "UPDATE VeBan SET TrangThaiVe = ? WHERE MaVe = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maVe);

            return ps.executeUpdate() > 0;
        }
    } 

    public boolean updateSoGheCon(Connection conn, String maCB, int soGheMoi) throws SQLException {
        String sql = "UPDATE ChuyenBay SET SoGheCon = ? WHERE MaChuyenBay = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soGheMoi);
            ps.setString(2, maCB);
            return ps.executeUpdate() > 0;
        }
    }

    public String generateMaVe(Connection conn) throws SQLException {
        String sql = "SELECT MAX(MaVe) FROM VeBan";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getString(1) != null) {
                String last = rs.getString(1).substring(2);
                int num = Integer.parseInt(last) + 1;
                return String.format("VE%03d", num);
            }
        }
        return "VE001";
    }

    public int getSoGheCon(Connection conn, String maCB) throws SQLException {
        String sql = "SELECT SoGheCon FROM ChuyenBay WHERE MaChuyenBay = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCB);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    public Timestamp getThoiGianKhoiHanh(Connection conn, String maCB) throws SQLException {
        String sql = "SELECT ThoiGianKhoiHanh FROM ChuyenBay WHERE MaChuyenBay = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCB);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getTimestamp(1);
        }
        return null;
    }

    public boolean chuyenBayExists(Connection conn, String maCB) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChuyenBay WHERE MaChuyenBay = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCB);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        }
        return false;
    }

     public boolean checkSeatAvailable(String maChuyenBay, String maGhe) {

        String sql = """
                SELECT COUNT(*) 
                FROM VeBan 
                WHERE MaChuyenBay = ? 
                AND MaGhe = ? 
                AND TrangThai <> 'HUY'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maChuyenBay);
            ps.setString(2, maGhe);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<VeBan> selectVeCoTheDoi(String maHK) {
        List<VeBan> list = new ArrayList<>();
        String sql = """
                       SELECT v.*
                       FROM VeBan v
                       JOIN ChuyenBay cb ON v.maChuyenBay = cb.maChuyenBay
                       JOIN PhieuDatVe pdv ON v.maPhieuDatVe = pdv.maPhieuDatVe
                       WHERE v.maHK = ?
                         AND v.trangThaiVe IN (N'Đã xuất', N'Chưa sử dụng')
                         AND pdv.trangThaiThanhToan = N'Đã thanh toán'
                         AND cb.trangThai <> N'Đã bay'
                         AND cb.ngayGioDi > GETDATE()       
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHK);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VeBan ve = new VeBan();

                ve.setMaVe(rs.getString("maVe"));
                ve.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                ve.setMaChuyenBay(rs.getString("maChuyenBay"));
                ve.setMaHK(rs.getString("maHK"));
                ve.setMaHangVe(rs.getString("maHangVe"));
                ve.setMaGhe(rs.getString("maGhe"));
                ve.setLoaiVe(rs.getString("loaiVe"));
                ve.setLoaiHK(rs.getString("loaiHK"));
                ve.setGiaVe(rs.getBigDecimal("giaVe"));
                ve.setTrangThaiVe(rs.getString("trangThaiVe"));

                list.add(ve);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<VeBan> selectByMaPhieuDatVe(String maPhieu) {
        List<VeBan> list = new ArrayList<>();
        String sql = """
        SELECT 
            vb.maVe,
            vb.maPhieuDatVe,
            vb.maChuyenBay,
            vb.loaiHK,
            vb.loaiVe,
            vb.giaVe,
            vb.trangThaiVe,

            tb.sanBayDi,
            tb.sanBayDen,
            cb.ngayGioDi

        FROM VeBan vb
        JOIN ChuyenBay cb ON vb.maChuyenBay = cb.maChuyenBay
        JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay

        WHERE vb.maPhieuDatVe = ?
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPhieu);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VeBan v = new VeBan();
                v.setMaVe(rs.getString("maVe"));
                v.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                v.setMaChuyenBay(rs.getString("maChuyenBay"));
                v.setLoaiHK(rs.getString("loaiHK"));
                v.setLoaiVe(rs.getString("loaiVe"));
                v.setGiaVe(rs.getBigDecimal("giaVe"));
                v.setTrangThaiVe(rs.getString("trangThaiVe"));

                v.setSanBayDi(rs.getString("sanBayDi"));
                v.setSanBayDen(rs.getString("sanBayDen"));
                v.setNgayGioDi(rs.getTimestamp("ngayGioDi"));

                list.add(v);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return list;
    }

    public List<VeBan> search(String keyword, String trangThai) {
    List<VeBan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM VeBan WHERE 1=1 ");

        // Nếu có keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (maVe LIKE ? OR maPhieuDatVe LIKE ?) ");
        }

        // Nếu có trạng thái
        if (trangThai != null && !trangThai.equals("Tất cả") && !trangThai.isEmpty()) {
            sql.append("AND trangThaiVe LIKE ? ");
        }

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            // Set keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(index++, "%" + keyword.trim() + "%");
                ps.setString(index++, "%" + keyword.trim() + "%");
            }

            // Set trạng thái
            if (trangThai != null && !trangThai.equals("Tất cả") && !trangThai.isEmpty()) {
                ps.setString(index++, trangThai.trim() + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VeBan v = new VeBan();

                v.setMaVe(rs.getString("maVe"));
                v.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                v.setMaChuyenBay(rs.getString("maChuyenBay"));
                v.setLoaiHK(rs.getString("loaiHK"));
                v.setLoaiVe(rs.getString("loaiVe"));
                v.setGiaVe(rs.getBigDecimal("giaVe"));
                v.setTrangThaiVe(rs.getString("trangThaiVe"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public BigDecimal tinhGiaVeFull(String maChuyenBay,
                                String maHangVe,
                                String loaiHK) {
        
        ChuyenBayDAO cbDAO = new ChuyenBayDAO();
        HangVeDAO hvDAO = new HangVeDAO();
        HeSoGiaDAO hsgDAO = new HeSoGiaDAO();
        

        BigDecimal giaGoc = cbDAO.layGiaCoBan(maChuyenBay);
        BigDecimal heSoHang = hvDAO.layHeSoHangVe(maHangVe);

        // Lấy ngày bay
        LocalDateTime ngayBay = cbDAO.layNgayBay(maChuyenBay);
        LocalDateTime ngayDat = LocalDateTime.now();

        long soGio = ChronoUnit.HOURS.between(ngayDat, ngayBay);

        BigDecimal heSoThoiDiem = hsgDAO.layHeSoTheoSoGio(soGio);

        BigDecimal heSoLoaiHK;

        switch (loaiHK) {
            case "Trẻ em":
                heSoLoaiHK = new BigDecimal("0.5");
                break;
            case "Em bé":
                heSoLoaiHK = new BigDecimal("0.2");
                break;
            default:
                heSoLoaiHK = BigDecimal.ONE;
        }
        

        return giaGoc
                .multiply(heSoHang)
                .multiply(heSoThoiDiem)
                .multiply(heSoLoaiHK)
                .setScale(0, RoundingMode.HALF_UP);
    }
}