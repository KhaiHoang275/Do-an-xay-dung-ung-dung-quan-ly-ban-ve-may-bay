package dal;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
        String sql = "INSERT INTO VeBan (maVe, maPhieuDatVe, maChuyenBay, maHK, maHangVe, maGhe, loaiVe, loaiHK, giaVe, trangThaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
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
                return String.format("VB%03d", num);
            }
        }
        return "VB001";
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
}