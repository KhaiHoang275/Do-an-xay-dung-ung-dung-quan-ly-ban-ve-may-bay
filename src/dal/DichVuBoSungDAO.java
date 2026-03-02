package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.DichVuBoSung; 

public class DichVuBoSungDAO {

    // 1. Lấy danh sách toàn bộ Dịch vụ bổ sung (ví dụ: Suất ăn, Chọn chỗ ngồi, Phòng chờ thương gia...)
    public ArrayList<DichVuBoSung> selectAll() {
        ArrayList<DichVuBoSung> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM DichVuBoSung";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                DichVuBoSung dv = new DichVuBoSung();
                dv.setMaDichVu(rs.getString("maDichVu"));
                dv.setTenDichVu(rs.getString("tenDichVu"));
                dv.setDonGia(rs.getBigDecimal("donGia")); // Dùng BigDecimal cho kiểu DECIMAL trong SQL
                list.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm một Dịch vụ bổ sung mới vào CSDL
    public boolean insert(DichVuBoSung dv) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO DichVuBoSung (maDichVu, tenDichVu, donGia) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, dv.getMaDichVu());
            pst.setString(2, dv.getTenDichVu());
            pst.setBigDecimal(3, dv.getDonGia());
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 3. Cập nhật thông tin dịch vụ (Ví dụ: Đổi tên dịch vụ hoặc tăng/giảm giá)
    public boolean update(DichVuBoSung dv) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE DichVuBoSung SET tenDichVu = ?, donGia = ? WHERE maDichVu = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, dv.getTenDichVu());
            pst.setBigDecimal(2, dv.getDonGia());
            pst.setString(3, dv.getMaDichVu());
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 4. Xóa một dịch vụ
    public boolean delete(String maDichVu) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM DichVuBoSung WHERE maDichVu = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, maDichVu);
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    // 5. TÌM DỊCH VỤ THEO MÃ (Dùng để tầng BUS check trùng mã trước khi thêm mới)
    public DichVuBoSung selectById(String maDichVu) {
        DichVuBoSung ketQua = null;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM DichVuBoSung WHERE maDichVu=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maDichVu);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                ketQua = new DichVuBoSung();
                ketQua.setMaDichVu(rs.getString("maDichVu"));
                ketQua.setTenDichVu(rs.getString("tenDichVu"));
                ketQua.setDonGia(rs.getBigDecimal("donGia"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }
}