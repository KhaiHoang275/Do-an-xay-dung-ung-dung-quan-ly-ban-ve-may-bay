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

                list.add(tthk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ThongTinHanhKhach tthk){
        String sql = "INSERT INTO ThongTinHanhKhach (maHK, maNguoiDung, maThuHang, hoTen, cccd, hoChieu, ngaySinh, gioiTinh, diemTichLuy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, tthk.getMaHK());
                ps.setString(2, tthk.getMaNguoiDung());
                ps.setString(3, tthk.getMaThuHang());
                ps.setString(4, tthk.getHoTen());
                ps.setString(5, tthk.getCccd());
                ps.setString(6, tthk.getHoChieu());
                ps.setDate(7, java.sql.Date.valueOf(tthk.getNgaySinh()));
                ps.setString(8, tthk.getGioiTinh());
                ps.setInt(9, tthk.getDiemTichLuy());

                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean update(ThongTinHanhKhach tthk){
        String sql = "UPDATE ThongTinHanhKhach SET maNguoiDung=?, maThuHang=?, hoTen=?, cccd=?, hoChieu=?, ngaySinh=?, gioiTinh=?, diemTichLuy=? WHERE maHK=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, tthk.getMaNguoiDung());
                ps.setString(2, tthk.getMaThuHang());
                ps.setString(3, tthk.getHoTen());
                ps.setString(4, tthk.getCccd());
                ps.setString(5, tthk.getHoChieu());
                ps.setDate(6, java.sql.Date.valueOf(tthk.getNgaySinh()));
                ps.setString(7, tthk.getGioiTinh());
                ps.setInt(8, tthk.getDiemTichLuy());
                ps.setString(9, tthk.getMaHK());

                return ps.executeUpdate() > 0;    
        } catch (Exception e) {
            e.printStackTrace();
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

                //tu dong tinh thu hang
                tthk.capNhatLoaiHanhKhach();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tthk;
    }
}
