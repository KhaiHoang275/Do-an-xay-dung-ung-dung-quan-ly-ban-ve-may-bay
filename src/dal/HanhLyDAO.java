package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.HanhLy;

public class HanhLyDAO {

    public ArrayList<HanhLy> selectAll() {
        ArrayList<HanhLy> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM HanhLy";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                HanhLy hl = new HanhLy();
                hl.setMaHanhLy(rs.getString("maHanhLy"));
                hl.setMaVe(rs.getString("maVe"));
                hl.setSoKg(rs.getBigDecimal("soKg"));
                hl.setKichThuoc(rs.getString("kichThuoc"));
                hl.setGiaTien(rs.getBigDecimal("giaTien"));
                hl.setTrangThai(rs.getString("trangThai"));
                hl.setGhiChu(rs.getString("ghiChu"));
                list.add(hl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(HanhLy hl) {
        boolean isSuccess = false;
        try (Connection con =DBConnection.getConnection()) {
            String sql = "INSERT INTO HanhLy (maHanhLy, maVe, soKg, kichThuoc, giaTien, trangThai, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, hl.getMaHanhLy());
            pst.setString(2, hl.getMaVe());
            pst.setBigDecimal(3, hl.getSoKg());
            pst.setString(4, hl.getKichThuoc());
            pst.setBigDecimal(5, hl.getGiaTien());
            pst.setString(6, hl.getTrangThai());
            pst.setString(7, hl.getGhiChu());
            
            if (pst.executeUpdate() > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    // ==========================================================
    // TÌM HÀNH LÝ THEO MÃ (Dùng để check trùng mã)
    // ==========================================================
    public HanhLy selectById(String maHanhLy) {
        HanhLy ketQua = null;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM HanhLy WHERE maHanhLy=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHanhLy);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                ketQua = new HanhLy();
                ketQua.setMaHanhLy(rs.getString("maHanhLy"));
                ketQua.setMaVe(rs.getString("maVe"));
                ketQua.setSoKg(rs.getBigDecimal("soKg"));
                ketQua.setKichThuoc(rs.getString("kichThuoc"));
                ketQua.setGiaTien(rs.getBigDecimal("giaTien"));
                ketQua.setTrangThai(rs.getString("trangThai"));
                ketQua.setGhiChu(rs.getString("ghiChu"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // ==========================================================
    // CẬP NHẬT THÔNG TIN HÀNH LÝ
    // ==========================================================
    public boolean update(HanhLy hl) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE HanhLy SET maVe=?, soKg=?, kichThuoc=?, giaTien=?, trangThai=?, ghiChu=? WHERE maHanhLy=?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, hl.getMaVe());
            pst.setBigDecimal(2, hl.getSoKg());
            pst.setString(3, hl.getKichThuoc());
            pst.setBigDecimal(4, hl.getGiaTien());
            pst.setString(5, hl.getTrangThai());
            pst.setString(6, hl.getGhiChu());
            pst.setString(7, hl.getMaHanhLy());
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // ==========================================================
    // XÓA HÀNH LÝ
    // ==========================================================
    public boolean delete(String maHanhLy) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM HanhLy WHERE maHanhLy=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHanhLy);
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}