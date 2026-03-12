package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import model.CTHoaDon;

public class CTHoaDonDAO {

    // 1. LẤY DANH SÁCH CHI TIẾT HÓA ĐƠN
    public List<CTHoaDon> selectAll() {
        List<CTHoaDon> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            // Tên bảng dựa theo database của bạn (thường là ChiTietHoaDon)
            String sql = "SELECT * FROM ChiTietHoaDon"; 
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                CTHoaDon ct = new CTHoaDon();
                ct.setMaHoaDon(rs.getString("maHoaDon"));
                ct.setMaVe(rs.getString("maVe"));
                ct.setDonGiaVe(rs.getBigDecimal("donGiaVe"));
                ct.setTienDichVu(rs.getBigDecimal("tienDichVu"));
                ct.setThueVAT(rs.getBigDecimal("thueVAT"));
                ct.setThanhTien(rs.getBigDecimal("thanhTien"));
                
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THÊM CHI TIẾT HÓA ĐƠN MỚI
    public boolean insert(CTHoaDon ct) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maVe, donGiaVe, tienDichVu, thueVAT, thanhTien) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, ct.getMaHoaDon());
            pst.setString(2, ct.getMaVe());
            pst.setBigDecimal(3, ct.getDonGiaVe());
            pst.setBigDecimal(4, ct.getTienDichVu());
            pst.setBigDecimal(5, ct.getThueVAT());
            pst.setBigDecimal(6, ct.getThanhTien());
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 3. XÓA CHI TIẾT HÓA ĐƠN 
    // Vì không còn maCTHD, ta dùng kết hợp maHoaDon và maVe để xóa chính xác 1 dòng
    public boolean delete(String maHoaDon, String maVe) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, maHoaDon);
            pst.setString(2, maVe);
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    
    // 4. TÌM CHI TIẾT HÓA ĐƠN THEO MÃ HÓA ĐƠN (Thường dùng để hiển thị danh sách vé trong 1 hóa đơn)
    public List<CTHoaDon> selectByMaHoaDon(String maHoaDon) {
        List<CTHoaDon> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon = ?"; 
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHoaDon);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                CTHoaDon ct = new CTHoaDon();
                ct.setMaHoaDon(rs.getString("maHoaDon"));
                ct.setMaVe(rs.getString("maVe"));
                ct.setDonGiaVe(rs.getBigDecimal("donGiaVe"));
                ct.setTienDichVu(rs.getBigDecimal("tienDichVu"));
                ct.setThueVAT(rs.getBigDecimal("thueVAT"));
                ct.setThanhTien(rs.getBigDecimal("thanhTien"));
                
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}