package dal;

import db.DBConnection;
import model.ThuHang;
import model.TrangThaiThuHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThuHangDAO {

    // Lấy tất cả hạng đang hoạt động (soft delete)
    public List<ThuHang> getAll() {
        List<ThuHang> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuHang WHERE trangThai = 'HOAT_DONG'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThuHang th = new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam"),
                        TrangThaiThuHang.fromGiaTri(rs.getString("trangThai"))
                );
                list.add(th);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tất cả hạng đã xóa mềm (thùng rác)
    public List<ThuHang> getAllDeleted() {
        List<ThuHang> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuHang WHERE trangThai = 'DA_XOA'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThuHang th = new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam"),
                        TrangThaiThuHang.fromGiaTri(rs.getString("trangThai"))
                );
                list.add(th);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy hạng phù hợp theo điểm (chỉ lấy HOAT_DONG)
    public ThuHang getThuHangTheoDiem(int diem) {
        String sql = """
            SELECT TOP 1 *
            FROM ThuHang
            WHERE diemToiThieu <= ? AND trangThai = 'HOAT_DONG'
            ORDER BY diemToiThieu DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diem);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam"),
                        TrangThaiThuHang.fromGiaTri(rs.getString("trangThai"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // INSERT - tự động set trangThai = HOAT_DONG
    public boolean insert(ThuHang th) {
        String sql = """
                INSERT INTO ThuHang 
                (maThuHang, tenThuHang, diemToiThieu, tiLeGiam, trangThai)
                VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, th.getMaThuHang());
            ps.setString(2, th.getTenThuHang());
            ps.setInt(3, th.getDiemToiThieu());
            ps.setDouble(4, th.getTiLeGiam());
            ps.setString(5, th.getTrangThaiGiaTri());   // ← dùng method này từ Model

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE (không thay đổi trangThai, chỉ sửa thông tin hạng)
    public boolean update(ThuHang th) {
        String sql = """
            UPDATE ThuHang 
            SET tenThuHang = ?, diemToiThieu = ?, tiLeGiam = ? 
            WHERE maThuHang = ? AND trangThai = 'HOAT_DONG'
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, th.getTenThuHang());
            ps.setInt(2, th.getDiemToiThieu());
            ps.setDouble(3, th.getTiLeGiam());
            ps.setString(4, th.getMaThuHang());

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // XÓA MỀM
    public boolean xoaMem(String maThuHang) {
        String sql = "UPDATE ThuHang SET trangThai = 'DA_XOA' WHERE maThuHang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maThuHang);
            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // KHÔI PHỤC (từ thùng rác)
    public boolean khoiPhuc(String maThuHang) {
        String sql = "UPDATE ThuHang SET trangThai = 'HOAT_DONG' WHERE maThuHang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maThuHang);
            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // SELECT BY ID (chỉ lấy active)
    public ThuHang selectById(String maHang) {
        String sql = "SELECT * FROM ThuHang WHERE maThuHang = ? AND trangThai = 'HOAT_DONG'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHang);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam"),
                        TrangThaiThuHang.fromGiaTri(rs.getString("trangThai"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // SELECT BY TEN (chỉ lấy active)
    public ThuHang selectByTen(String tenHang) {
        String sql = "SELECT * FROM ThuHang WHERE tenThuHang = ? AND trangThai = 'HOAT_DONG'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenHang);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ThuHang(
                        rs.getString("maThuHang"),
                        rs.getString("tenThuHang"),
                        rs.getInt("diemToiThieu"),
                        rs.getDouble("tiLeGiam"),
                        TrangThaiThuHang.fromGiaTri(rs.getString("trangThai"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}