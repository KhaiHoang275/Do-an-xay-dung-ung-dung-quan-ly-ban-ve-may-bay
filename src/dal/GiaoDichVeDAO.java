package dal;
import model.GiaoDichVe;
import model.TrangThaiGiaoDich;
import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaoDichVeDAO {
    // === insert === : lưu một giao dịch vé mới vào database (sql)
    public boolean insert(GiaoDichVe gd){
        String sql = """
            INSERT INTO GiaoDichVe  
            (maGD, maVeCu, maChuyenBayMoi, maHangVeMoi, maGheMoi, trangThai, phi, phiChenhLech, lyDoDoi, ngayYeuCau, ngayXuLi)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, gd.getMaGD());
            ps.setString(2, gd.getMaVeCu());
            ps.setString(3, gd.getMaChuyenBayMoi());
            ps.setString(4, gd.getMaHangVeMoi());
            ps.setString(5, gd.getMaGheMoi());
            ps.setString(6, gd.getTrangThai().name());
            ps.setBigDecimal(7, gd.getPhi());
            ps.setBigDecimal(8, gd.getPhiChenhLech());
            ps.setString(9, gd.getLyDoDoi());

            if(gd.getNgayYeuCau() != null)
                ps.setDate(10, Date.valueOf(gd.getNgayYeuCau()));
            else
                ps.setNull(10, Types.DATE);

            if(gd.getNgayXuLi() != null)
                ps.setDate(11, Date.valueOf(gd.getNgayXuLi()));
            else
                ps.setNull(11, Types.DATE);

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // === find all ===
    public List<GiaoDichVe> findAll(){
        List<GiaoDichVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichVe";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(mapResultSet(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    // === find by maVeCu ===
    public List<GiaoDichVe> findByMaVeCu(String maVeCu){
        List<GiaoDichVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichVe WHERE maVeCu = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maVeCu);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(mapResultSet(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    // === find by trang thai ===
    public List<GiaoDichVe> findByTrangThai(TrangThaiGiaoDich tt){
        List<GiaoDichVe> list  = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichVe WHERE trangThai = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, tt.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public GiaoDichVe findById(String maGD){
        String sql = "SELECT * FROM GiaoDichVe WHERE maGD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maGD);
            try (ResultSet rs  = ps.executeQuery()){
                if(rs.next()) return mapResultSet(rs);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(GiaoDichVe gd){
        String sql = """
                UPDATE GiaoDichVe
                SET maVeCu = ?,
                    maChuyenBayMoi = ?,
                    maHangVeMoi = ?,
                    maGheMoi = ?,
                    trangThai = ?,
                    phi = ?,
                    phiChenhLech = ?,
                    lyDoDoi = ?,
                    ngayYeuCau = ?,
                    ngayXuLi = ?
                 WHERE maGD = ?
               """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, gd.getMaVeCu());
            ps.setString(2, gd.getMaChuyenBayMoi());
            ps.setString(3, gd.getMaHangVeMoi());
            ps.setString(4, gd.getMaGheMoi());
            ps.setString(5, gd.getTrangThai().name());
            ps.setBigDecimal(6, gd.getPhi());
            ps.setBigDecimal(7, gd.getPhiChenhLech());
            ps.setString(8, gd.getLyDoDoi());

            if(gd.getNgayYeuCau() != null){
                ps.setDate(9, Date.valueOf(gd.getNgayYeuCau()));
            }else {
                ps.setNull(9, Types.DATE);
            }

            if(gd.getNgayXuLi() != null){
                ps.setDate(10, Date.valueOf(gd.getNgayXuLi()));
            }else{
                ps.setNull(10, Types.DATE);
            }

            ps.setString(11, gd.getMaGD());

            return ps.executeUpdate() > 0;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maGD) {
        String sql = "DELETE FROM GiaoDichVe WHERE maGD = ?";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, maGD);
            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private GiaoDichVe mapResultSet(ResultSet rs) throws SQLException{
        GiaoDichVe gd = new GiaoDichVe();
        gd.setMaGD(rs.getString("maGD"));
        gd.setMaVeCu(rs.getString("maVeCu"));
        gd.setMaChuyenBayMoi(rs.getString("maChuyenBayMoi"));
        gd.setMaHangVeMoi(rs.getString("maHangVeMoi"));
        gd.setMaGheMoi(rs.getString("maGheMoi"));
        gd.setTrangThai(TrangThaiGiaoDich.valueOf(rs.getString("trangThai")));
        gd.setPhi(rs.getBigDecimal("phi"));
        gd.setPhiChenhLech(rs.getBigDecimal("phiChenhLech"));
        gd.setLyDoDoi(rs.getString("lyDoDoi"));

        Date ngayYC = rs.getDate("ngayYeuCau");
        if(ngayYC != null)
            gd.setNgayYeuCau(ngayYC.toLocalDate());

        Date ngayXL = rs.getDate("ngayXuLi");
        if(ngayXL != null)
            gd.setNgayXuLi(ngayXL.toLocalDate());

        return gd;
    }
}