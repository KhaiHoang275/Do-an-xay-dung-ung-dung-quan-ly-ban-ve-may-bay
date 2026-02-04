package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.DBConnection;
import model.PhieuDatVe;

public class PhieuDatVeDAO {
    public ArrayList<PhieuDatVe> selectAll(){
        ArrayList<PhieuDatVe> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatVe";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                PhieuDatVe dv = new PhieuDatVe();
                dv.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                dv.setMaNguoiDung(rs.getString("maNguoiDung"));
                dv.setMaNV(rs.getString("maNV"));
                dv.setMaKhuyenMai(rs.getString("maKhuyenMai"));
                dv.setThoiLuong(rs.getInt("thoiLuong"));
                if (rs.getDate("ngayDat") != null) {
                    dv.setNgayDat(rs.getDate("ngayDat").toLocalDate());
                }
                dv.setSoLuongVe(rs.getInt("soLuongVe"));
                dv.setTongTien(rs.getBigDecimal("tongTien"));
                dv.setTrangThaiThanhToan(rs.getString("trangThaiThanhToan"));
                list.add(dv);
            }
        } catch (SQLException e){
                e.printStackTrace();
        }
        return list;
    } 

    public boolean insert(PhieuDatVe dv){
        String sql = "INSERT INTO PhieuDatVe (maPhieuDatVe, maNguoiDung, maNV, maKhuyenMai, thoiLuong, ngayDat, soLuongVe, tongTien, trangThaiThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, dv.getMaPhieuDatVe());
                ps.setString(2, dv.getMaNguoiDung());
                ps.setString(3, dv.getMaNV());
                ps.setString(4, dv.getMaKhuyenMai());
                ps.setInt(5, dv.getThoiLuong());
                ps.setDate(6, java.sql.Date.valueOf(dv.getNgayDat()));
                ps.setInt(7, dv.getSoLuongVe());
                ps.setBigDecimal(8, dv.getTongTien());
                ps.setString(9, dv.getTrangThaiThanhToan());

                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PhieuDatVe dv){
        String sql = "UPDATE PhieuDatVe SET maNguoiDung=?, maNV=?, maKhuyenMai=?, thoiLuong=?, ngayDat=?, soLuongVe=?, tongTien=?, trangThaiThanhToan=? WHERE maPhieuDatVe=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, dv.getMaNguoiDung());
                ps.setString(2, dv.getMaNV());
                ps.setString(3, dv.getMaKhuyenMai());
                ps.setInt(4, dv.getThoiLuong());
                ps.setDate(5, java.sql.Date.valueOf(dv.getNgayDat()));
                ps.setInt(6, dv.getSoLuongVe());
                ps.setBigDecimal(7, dv.getTongTien());
                ps.setString(8, dv.getTrangThaiThanhToan());
                ps.setString(9, dv.getMaPhieuDatVe());

                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPhieuDatVe){
        String sql = "DELETE FROM PhieuDatVe WHERE maPhieuDatVe=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, maPhieuDatVe);
                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}