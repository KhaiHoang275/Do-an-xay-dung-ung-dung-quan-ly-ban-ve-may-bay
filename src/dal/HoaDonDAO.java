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
        // Giả sử JDBCUtil là class kết nối SQL Server của nhóm
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM HoaDon";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("maHoaDon"));
                hd.setMaPhieuDatVe(rs.getString("maPhieuDatVe"));
                hd.setMaNV(rs.getString("maNV"));
                // Xử lý ngày tháng từ SQL Server sang Java LocalDate/Date tùy bạn thiết kế
                // hd.setNgayLap(rs.getDate("ngayLap").toLocalDate()); 
                if (rs.getTimestamp("NgayLap") != null) {
                     hd.setNgayLap(rs.getTimestamp("NgayLap").toLocalDateTime());
                    }
                hd.setTongTien(rs.getBigDecimal("tongTien"));
                hd.setPhuongThuc(rs.getString("phuongThuc"));
                hd.setDonViTienTe(rs.getString("donViTienTe"));
                hd.setThue(rs.getBigDecimal("thue"));
                
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
            String sql = "INSERT INTO HoaDon (maHoaDon, maPhieuDatVe, maNV, ngayLap, tongTien, phuongThuc, donViTienTe, thue) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, hd.getMaHoaDon());
            pst.setString(2, hd.getMaPhieuDatVe());
            pst.setString(3, hd.getMaNV());
            // Lấy ngày hiện tại hoặc ngày từ đối tượng hd
            // Giả sử biến hóa đơn là hd
            pst.setTimestamp(4, java.sql.Timestamp.valueOf(hd.getNgayLap()));
            pst.setBigDecimal(5, hd.getTongTien());
            pst.setString(6, hd.getPhuongThuc());
            pst.setString(7, hd.getDonViTienTe());
            pst.setBigDecimal(8, hd.getThue());
            
            int soDongAnhHuong = pst.executeUpdate();
            if (soDongAnhHuong > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    
    // ==========================================================
    // 3. TÌM HÓA ĐƠN THEO MÃ (Dùng để check trùng mã ở tầng BUS)
    // ==========================================================
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
                
                // Xử lý đọc ngày tháng (tránh lỗi NullPointerException)
                if (rs.getTimestamp("ngayLap") != null) {
                    hd.setNgayLap(rs.getTimestamp("ngayLap").toLocalDateTime());
                }
                
                hd.setTongTien(rs.getBigDecimal("tongTien"));
                hd.setPhuongThuc(rs.getString("phuongThuc"));
                hd.setDonViTienTe(rs.getString("donViTienTe"));
                hd.setThue(rs.getBigDecimal("thue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hd;
    }

    // ==========================================================
    // 4. CẬP NHẬT HÓA ĐƠN
    // ==========================================================
    public boolean update(HoaDon hd) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE HoaDon SET maPhieuDatVe=?, maNV=?, ngayLap=?, tongTien=?, phuongThuc=?, donViTienTe=?, thue=? WHERE maHoaDon=?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, hd.getMaPhieuDatVe());
            pst.setString(2, hd.getMaNV());
            
            // Xử lý cập nhật ngày tháng
            if(hd.getNgayLap() != null) {
                pst.setTimestamp(3, java.sql.Timestamp.valueOf(hd.getNgayLap()));
            } else {
                pst.setNull(3, java.sql.Types.TIMESTAMP); // Đẩy giá trị NULL xuống SQL nếu rỗng
            }
            
            pst.setBigDecimal(4, hd.getTongTien());
            pst.setString(5, hd.getPhuongThuc());
            pst.setString(6, hd.getDonViTienTe());
            pst.setBigDecimal(7, hd.getThue());
            pst.setString(8, hd.getMaHoaDon()); // Khóa chính nằm cuối cùng
            
            if (pst.executeUpdate() > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // ==========================================================
    // 5. XÓA HÓA ĐƠN
    // ==========================================================
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

    public ThanhToanDTO getChiTietThanhToan(String maPhieu) {
        ThanhToanDTO dto = new ThanhToanDTO();
        
        dto.giaVeGoc = java.math.BigDecimal.ZERO;
        dto.tongTienDichVu = java.math.BigDecimal.ZERO;
        if (dto.danhSachChiTiet == null) {
            dto.danhSachChiTiet = new java.util.ArrayList<>();
        }
        
        try (Connection con = DBConnection.getConnection()) {

            String sqlBase = "SELECT TOP 1 hk.hoTen, nd.sdt, nd.email, " +
                             "sbDi.tenSanBay AS diemDi, sbDen.tenSanBay AS diemDen, " +
                             "v.maVe, v.maGhe, cb.ngayGioDi, cb.ngayGioDen " +
                             "FROM PhieuDatVe pdv " +
                             "JOIN ThongTinHanhKhach hk ON pdv.maNguoiDung = hk.maNguoiDung " +
                             "JOIN NguoiDung nd ON hk.maNguoiDung = nd.maNguoiDung " +
                             "JOIN VeBan v ON pdv.maPhieuDatVe = v.maPhieuDatVe " +
                             "JOIN ChuyenBay cb ON v.maChuyenBay = cb.maChuyenBay " +
                             "JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay " +
                             "JOIN SanBay sbDi ON tb.sanBayDi = sbDi.maSanBay " +
                             "JOIN SanBay sbDen ON tb.sanBayDen = sbDen.maSanBay " +
                             "WHERE pdv.maPhieuDatVe = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlBase)) {
                pst.setString(1, maPhieu);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    dto.tenKH = rs.getString("hoTen");
                    dto.sdt = rs.getString("sdt");
                    dto.email = rs.getString("email");
                    dto.tuyenBay = rs.getString("diemDi") + " -> " + rs.getString("diemDen");
                    dto.veGhe = rs.getString("maVe") + " / Ghế " + rs.getString("maGhe");
                    
                    if (rs.getTimestamp("ngayGioDi") != null) {
                        dto.gioDi = rs.getTimestamp("ngayGioDi").toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    }
                }
            }

            String sqlVe = "SELECT loaiVe, maGhe, giaVe FROM VeBan WHERE maPhieuDatVe = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlVe)) {
                pst.setString(1, maPhieu);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String tenMuc = "Vé máy bay hạng " + (rs.getString("loaiVe") != null ? rs.getString("loaiVe") : "Thường") + " (Ghế " + rs.getString("maGhe") + ")";
                    java.math.BigDecimal gia = rs.getBigDecimal("giaVe");
                    if (gia == null) gia = java.math.BigDecimal.ZERO;

                    dto.giaVeGoc = dto.giaVeGoc.add(gia);
                    dto.danhSachChiTiet.add(new ThanhToanDTO.MucHoaDon(tenMuc, 1, gia, gia));
                }
            }

            String sqlHanhLy = "SELECT hl.soKg, hl.giaTien FROM HanhLy hl JOIN VeBan v ON hl.maVe = v.maVe WHERE v.maPhieuDatVe = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlHanhLy)) {
                pst.setString(1, maPhieu);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String tenMuc = "Hành lý ký gửi (" + rs.getInt("soKg") + " kg)";
                    java.math.BigDecimal gia = rs.getBigDecimal("giaTien");
                    if (gia == null) gia = java.math.BigDecimal.ZERO;

                    dto.tongTienDichVu = dto.tongTienDichVu.add(gia);
                    dto.danhSachChiTiet.add(new ThanhToanDTO.MucHoaDon(tenMuc, 1, gia, gia));
                }
            }

            String sqlDichVu = "SELECT dv.tenDichVu, ct.soLuong, ct.thanhTien FROM ChiTietDichVu ct JOIN DichVuBoSung dv ON ct.maDichVu = dv.maDichVu JOIN VeBan v ON ct.maVe = v.maVe WHERE v.maPhieuDatVe = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlDichVu)) {
                pst.setString(1, maPhieu);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String tenMuc = "Dịch vụ: " + rs.getString("tenDichVu");
                    int sl = rs.getInt("soLuong");
                    java.math.BigDecimal tt = rs.getBigDecimal("thanhTien");
                    if (tt == null) tt = java.math.BigDecimal.ZERO;
                    
                    java.math.BigDecimal donGia = sl > 0 ? tt.divide(new java.math.BigDecimal(sl), java.math.RoundingMode.HALF_UP) : tt;
                   
                    dto.tongTienDichVu = dto.tongTienDichVu.add(tt);
                    dto.danhSachChiTiet.add(new ThanhToanDTO.MucHoaDon(tenMuc, sl, donGia, tt));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}