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
            // Lưu ý: Tên bảng CT_Hoa_Don có thể viết khác tùy cách bạn tạo trong SQL (ví dụ: CTHoaDon)
            String sql = "SELECT * FROM CT_Hoa_Don"; 
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                CTHoaDon ct = new CTHoaDon();
                ct.setMaCTHD(rs.getString("maCTHD"));
                ct.setMaHoaDon(rs.getString("maHoaDon"));
                ct.setMaVe(rs.getString("maVe"));
                ct.setSoTien(rs.getBigDecimal("soTien")); // Chuẩn BigDecimal của form nhóm
                ct.setMaNguoiDung(rs.getString("maNguoiDung"));
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
            String sql = "INSERT INTO CT_Hoa_Don (maCTHD, maHoaDon, maVe, soTien, maNguoiDung) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, ct.getMaCTHD());
            pst.setString(2, ct.getMaHoaDon());
            pst.setString(3, ct.getMaVe());
            pst.setBigDecimal(4, ct.getSoTien());
            pst.setString(5, ct.getMaNguoiDung());
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 3. XÓA CHI TIẾT HÓA ĐƠN (Theo mã chi tiết)
    public boolean delete(String maCTHD) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM CT_Hoa_Don WHERE maCTHD = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, maCTHD);
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    
    // Lưu ý: Bảng chi tiết hóa đơn thường không cần hàm UPDATE vì sai thì xóa đi tạo lại, 
    // nhưng nếu hệ thống của bạn yêu cầu, bạn có thể bổ sung sau.
}