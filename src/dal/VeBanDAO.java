package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import db.DBConnection;
import entity.VeBan;

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
}