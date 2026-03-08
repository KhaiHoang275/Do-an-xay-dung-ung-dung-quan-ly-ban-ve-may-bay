package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.ThongTinHanhKhach;

public class ThongTinHanhKhachDAO {
    public ArrayList<ThongTinHanhKhach> selectAll(){
        ArrayList<ThongTinHanhKhach> list = new ArrayList<>();
        String sql = "SELECT * FROM ThongTinHanhKhach";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                ThongTinHanhKhach tthk = new ThongTinHanhKhach();
                tthk.setMaHK(rs.getString("maHK"));
                tthk.setMaNguoiDung(rs.getString("maNguoiDung"));
                tthk.setMaThuHang(rs.getString("maThuHang"));
                tthk.setHoTen(rs.getString("hoTen"));
                tthk.setCccd(rs.getString("cccd"));
                tthk.setHoChieu(rs.getString("hoChieu"));
                
                if(rs.getDate("ngaySinh") != null){
                    tthk.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
                }
                tthk.setGioiTinh(rs.getString("gioiTinh"));
                tthk.setDiemTichLuy(rs.getInt("diemTichLuy"));
                tthk.setLoaiHanhKhach(rs.getString("loaiHanhKhach"));

                list.add(tthk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ThongTinHanhKhach hk) {
        // Bổ sung thêm cột trangThai và loaiHanhKhach để tránh lỗi NOT NULL nếu SQL yêu cầu
        String sql = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, hoTen, gioiTinh, ngaySinh, loaiHanhKhach, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.Connection con = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hk.getMaHK());
            ps.setString(2, hk.getMaNguoiDung());
            ps.setString(3, hk.getHoTen());
            ps.setString(4, hk.getGioiTinh());
            
            if (hk.getNgaySinh() != null) {
                ps.setDate(5, java.sql.Date.valueOf(hk.getNgaySinh()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            
            ps.setString(6, hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "Nguoi lon");
            ps.setString(7, "Hoạt động"); 

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Lỗi SQL (Thêm Hành Khách):\n" + e.getMessage(), "SQL Server từ chối", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean update(ThongTinHanhKhach hk) {
        String sql = "UPDATE ThongTinHanhKhach SET hoTen=?, gioiTinh=?, ngaySinh=? WHERE maHK=?";
        try (java.sql.Connection con = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hk.getHoTen());
            ps.setString(2, hk.getGioiTinh());
            
            if (hk.getNgaySinh() != null) {
                ps.setDate(3, java.sql.Date.valueOf(hk.getNgaySinh()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, hk.getMaHK());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Lỗi SQL (Sửa Hành Khách):\n" + e.getMessage(), "SQL Server từ chối", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    public boolean delete(String maHK){
        String sql = "DELETE FROM ThongTinHanhKhach WHERE maHK=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, maHK);
                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getDiemTichLuy(String maHanhKhach) {
        String sql = "SELECT diemTichLuy FROM ThongTinHanhKhach WHERE maHK = ?";
        int diem = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHanhKhach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                diem = rs.getInt("diemTichLuy");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diem;
    }

    public void updateDiemVaThuHang(String maHanhKhach, int diemMoi, String maThuHang) {

        String sql = """
            UPDATE ThongTinHanhKhach
            SET diemTichLuy = ?,
                maThuHang = ?
            WHERE maHK = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diemMoi);
            ps.setString(2, maThuHang);
            ps.setString(3, maHanhKhach);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMaThuHang(String maHanhKhach) {

        String sql = "SELECT maThuHang FROM ThongTinHanhKhach WHERE maHK = ?";
        String maThuHang = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHanhKhach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                maThuHang = rs.getString("maThuHang");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return maThuHang;
    }

    public ThongTinHanhKhach getByMaHK(String maHK){
        String sql = "SELECT * FROM ThongTinHanhKhach WHERE maHK = ?";
        ThongTinHanhKhach tthk = null;
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, maHK);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                tthk = new ThongTinHanhKhach();
                tthk.setMaHK(rs.getString("maHK"));
                tthk.setMaNguoiDung(rs.getString("maNguoiDung"));
                tthk.setMaThuHang(rs.getString("maThuHang"));
                tthk.setHoTen(rs.getString("hoTen"));
                tthk.setCccd(rs.getString("cccd"));
                tthk.setHoChieu(rs.getString("hoChieu"));

                if(rs.getDate("ngaySinh") != null){
                    tthk.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
                }
                tthk.setGioiTinh(rs.getString("gioiTinh"));
                tthk.setDiemTichLuy(rs.getInt("diemTichLuy"));
                tthk.setLoaiHanhKhach(rs.getString("loaiHanhKhach"));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tthk;
    } 



    public ThongTinHanhKhach selectByMaNguoiDung(String maNguoiDung){
        String sql = "SELECT * FROM ThongTinHanhKhach WHERE maNguoiDung = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, maNguoiDung);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ThongTinHanhKhach tthk = new ThongTinHanhKhach();

                tthk.setMaHK(rs.getString("maHK"));
                tthk.setMaNguoiDung(rs.getString("maNguoiDung"));
                tthk.setMaThuHang(rs.getString("maThuHang"));
                tthk.setHoTen(rs.getString("hoTen"));
                tthk.setCccd(rs.getString("cccd"));
                tthk.setHoChieu(rs.getString("hoChieu"));
                if(rs.getDate("ngaySinh") != null){
                    tthk.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
                }
                tthk.setGioiTinh(rs.getString("gioiTinh"));
                tthk.setDiemTichLuy(rs.getInt("diemTichLuy"));
                tthk.setLoaiHanhKhach(rs.getString("loaiHanhKhach"));

                return tthk;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    } 

    public ArrayList<ThongTinHanhKhach> selectAllByMaNguoiDung(String maNguoiDung) {
    ArrayList<ThongTinHanhKhach> list = new ArrayList<>();
    String sql = "SELECT * FROM ThongTinHanhKhach WHERE maNguoiDung = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, maNguoiDung);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ThongTinHanhKhach tthk = new ThongTinHanhKhach();
            tthk.setMaHK(rs.getString("maHK"));
            tthk.setMaNguoiDung(rs.getString("maNguoiDung"));
            tthk.setMaThuHang(rs.getString("maThuHang"));
            tthk.setHoTen(rs.getString("hoTen"));
            tthk.setCccd(rs.getString("cccd"));
            tthk.setHoChieu(rs.getString("hoChieu"));
            if (rs.getDate("ngaySinh") != null) {
                tthk.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
            }
            tthk.setGioiTinh(rs.getString("gioiTinh"));
            tthk.setDiemTichLuy(rs.getInt("diemTichLuy"));
            tthk.setLoaiHanhKhach(rs.getString("loaiHanhKhach"));
            list.add(tthk);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    } 

    public boolean saveOrUpdate(ThongTinHanhKhach hk) {
    String checkSql = "SELECT COUNT(*) FROM ThongTinHanhKhach WHERE maHK = ?";
    boolean isExist = false;
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
        psCheck.setString(1, hk.getMaHK());
        ResultSet rs = psCheck.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            isExist = true;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }

    if (isExist) {
        String updateSql = "UPDATE ThongTinHanhKhach SET hoTen=?, cccd=?, hoChieu=?, gioiTinh=?, ngaySinh=?, loaiHanhKhach=? WHERE maHK=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
            psUpdate.setString(1, hk.getHoTen());
            psUpdate.setString(2, hk.getCccd());
            psUpdate.setString(3, hk.getHoChieu());
            psUpdate.setString(4, hk.getGioiTinh());
            
            if (hk.getNgaySinh() != null) {
                psUpdate.setDate(5, java.sql.Date.valueOf(hk.getNgaySinh()));
            } else {
                psUpdate.setNull(5, java.sql.Types.DATE);
            }
            
            psUpdate.setString(6, hk.getLoaiHanhKhach());
            psUpdate.setString(7, hk.getMaHK());
            return psUpdate.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    } else {
        String insertSql = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, hoTen, cccd, hoChieu, gioiTinh, ngaySinh, loaiHanhKhach, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Hoạt động')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
            psInsert.setString(1, hk.getMaHK());
            psInsert.setString(2, hk.getMaNguoiDung());
            psInsert.setString(3, hk.getHoTen());
            psInsert.setString(4, hk.getCccd());
            psInsert.setString(5, hk.getHoChieu());
            psInsert.setString(6, hk.getGioiTinh());
            
            if (hk.getNgaySinh() != null) {
                psInsert.setDate(7, java.sql.Date.valueOf(hk.getNgaySinh()));
            } else {
                psInsert.setNull(7, java.sql.Types.DATE);
            }
            
            psInsert.setString(8, hk.getLoaiHanhKhach());
            return psInsert.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}



}
