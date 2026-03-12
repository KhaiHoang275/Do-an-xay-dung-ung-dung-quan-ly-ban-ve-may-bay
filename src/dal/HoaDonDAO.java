package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import db.DBConnection;
import model.HoaDon;
import model.ThanhToanDTO;

public class HoaDonDAO {

    // 1. Lấy danh sách toàn bộ hóa đơn
    public ArrayList<HoaDon> selectAll() {
        ArrayList<HoaDon> ketQua = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM HoaDon";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("maHoaDon"));
                hd.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                hd.setMaNV(rs.getString("maNV"));
                
                if (rs.getTimestamp("ngayLap") != null) {
                     hd.setNgayLap(rs.getTimestamp("ngayLap").toLocalDateTime());
                }
                hd.setTongTien(rs.getBigDecimal("tongTien"));
                hd.setPhuongThuc(rs.getString("phuongThuc"));
                hd.setDonViTienTe(rs.getString("donViTienTe"));
                hd.setThue(rs.getBigDecimal("thue"));
                // Thêm dòng setTrangThai
                hd.setTrangThai(rs.getString("trangThai"));
                
                ketQua.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // 2. Thêm một hóa đơn mới vào CSDL
    public boolean insert(HoaDon hd) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            // Thêm trường trangThai vào câu lệnh INSERT
            String sql = "INSERT INTO HoaDon (maHoaDon, maPhieuDatVe, maNV, ngayLap, tongTien, phuongThuc, donViTienTe, thue, trangThai) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, hd.getMaHoaDon());
            pst.setString(2, hd.getMaPhieuDatVe());
            pst.setString(3, hd.getMaNV());
            
            if(hd.getNgayLap() != null) {
                pst.setTimestamp(4, java.sql.Timestamp.valueOf(hd.getNgayLap()));
            } else {
                pst.setNull(4, java.sql.Types.TIMESTAMP);
            }
            
            pst.setBigDecimal(5, hd.getTongTien());
            pst.setString(6, hd.getPhuongThuc());
            pst.setString(7, hd.getDonViTienTe());
            pst.setBigDecimal(8, hd.getThue());
            // Set giá trị cho trường trangThai
            pst.setString(9, hd.getTrangThai());
            
            int soDongAnhHuong = pst.executeUpdate();
            if (soDongAnhHuong > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    
    // 3. TÌM HÓA ĐƠN THEO MÃ
    public HoaDon selectById(String maHoaDon) {
        HoaDon hd = null;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM HoaDon WHERE maHoaDon=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHoaDon);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("maHoaDon"));
                hd.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                hd.setMaNV(rs.getString("maNV"));
                
                if (rs.getTimestamp("ngayLap") != null) {
                    hd.setNgayLap(rs.getTimestamp("ngayLap").toLocalDateTime());
                }
                
                hd.setTongTien(rs.getBigDecimal("tongTien"));
                hd.setPhuongThuc(rs.getString("phuongThuc"));
                hd.setDonViTienTe(rs.getString("donViTienTe"));
                hd.setThue(rs.getBigDecimal("thue"));
                // Thêm dòng lấy trangThai
                hd.setTrangThai(rs.getString("trangThai"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hd;
    }

    // 4. CẬP NHẬT HÓA ĐƠN
    public boolean update(HoaDon hd) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            // Thêm trường trangThai vào câu lệnh UPDATE
            String sql = "UPDATE HoaDon SET maPhieuDatVe=?, maNV=?, ngayLap=?, tongTien=?, phuongThuc=?, donViTienTe=?, thue=?, trangThai=? WHERE maHoaDon=?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, hd.getMaPhieuDatVe());
            pst.setString(2, hd.getMaNV());
            
            if(hd.getNgayLap() != null) {
                pst.setTimestamp(3, java.sql.Timestamp.valueOf(hd.getNgayLap()));
            } else {
                pst.setNull(3, java.sql.Types.TIMESTAMP);
            }
            
            pst.setBigDecimal(4, hd.getTongTien());
            pst.setString(5, hd.getPhuongThuc());
            pst.setString(6, hd.getDonViTienTe());
            pst.setBigDecimal(7, hd.getThue());
            // Cập nhật trường trangThai
            pst.setString(8, hd.getTrangThai());
            // Khóa chính nằm cuối cùng (vị trí số 9)
            pst.setString(9, hd.getMaHoaDon()); 
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 5. XÓA HÓA ĐƠN
    public boolean delete(String maHoaDon) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM HoaDon WHERE maHoaDon=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHoaDon);
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // Lấy chi tiết thanh toán (Giữ nguyên cấu trúc của bạn, mình chỉ format lại chút cho dễ nhìn)
    public ThanhToanDTO getChiTietThanhToan(String maPhieu) {
        ThanhToanDTO dto = null;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT hk.hoTen, nd.sdt, nd.email, sbDi.tenSanBay AS diemDi, sbDen.tenSanBay AS diemDen, " +
                         "v.maVe, v.maGhe, cb.ngayGioDi, cb.ngayGioDen, " +
                         "v.giaVe, " + 
                         "(SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietDichVu WHERE maVe = v.maVe) AS tienDichVu " + 
                         "FROM PhieuDatVe pdv " +
                         "JOIN ThongTinHanhKhach hk ON pdv.maNguoiDung = hk.maNguoiDung " +
                         "JOIN NguoiDung nd ON hk.maNguoiDung = nd.maNguoiDung " +
                         "JOIN VeBan v ON pdv.maPhieuDatVe = v.maPhieuDatVe " +
                         "JOIN ChuyenBay cb ON v.maChuyenBay = cb.maChuyenBay " +
                         "JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay " +
                         "JOIN SanBay sbDi ON tb.sanBayDi = sbDi.maSanBay " +
                         "JOIN SanBay sbDen ON tb.sanBayDen = sbDen.maSanBay " +
                         "WHERE pdv.maPhieuDatVe = ?";
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maPhieu);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                dto = new ThanhToanDTO();
                dto.tenKH = rs.getString("hoTen");
                dto.sdt = rs.getString("sdt");
                dto.email = rs.getString("email");
                dto.tuyenBay = rs.getString("diemDi") + " -> " + rs.getString("diemDen");
                dto.veGhe = rs.getString("maVe") + " / Ghế " + rs.getString("maGhe");
                dto.gioDi = rs.getTimestamp("ngayGioDi").toString();
                dto.gioDen = rs.getTimestamp("ngayGioDen").toString();
                dto.giaVeGoc = rs.getBigDecimal("giaVe");
                dto.tongTienDichVu = rs.getBigDecimal("tienDichVu");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}