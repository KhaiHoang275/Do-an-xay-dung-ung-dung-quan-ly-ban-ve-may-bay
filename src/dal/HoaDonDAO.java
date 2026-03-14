package dal;

import java.math.BigDecimal;
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

   // =========================================================================
    // LẤY CHI TIẾT THANH TOÁN (ĐÃ FIX LỖI NHÂN BẢN DỮ LIỆU BẰNG JAVA SET)
    // =========================================================================
    public ThanhToanDTO getChiTietThanhToan(String maPhieu) {
        ThanhToanDTO dto = null;
        try (Connection con = DBConnection.getConnection()) {
            // TRẢ LẠI CÂU SQL GỐC CỦA BẠN (Không đổi JOIN)
            String sql = "SELECT hk.hoTen, nd.sdt, nd.email, sbDi.tenSanBay AS diemDi, sbDen.tenSanBay AS diemDen, " +
                         "v.maVe, v.maGhe, cb.ngayGioDi, cb.ngayGioDen, " +
                         "v.giaVe, " + 
                         "(SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietDichVu WHERE maVe = v.maVe) AS tienDichVu " + 
                         "FROM PhieuDatVe pdv " +
                         "JOIN VeBan v ON pdv.maPhieuDatVe = v.maPhieuDatVe " +
                         "JOIN ChuyenBay cb ON v.maChuyenBay = cb.maChuyenBay " +
                         "JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay " +
                         "JOIN SanBay sbDi ON tb.sanBayDi = sbDi.maSanBay " +
                         "JOIN SanBay sbDen ON tb.sanBayDen = sbDen.maSanBay " +
                         "LEFT JOIN NguoiDung nd ON pdv.maNguoiDung = nd.maNguoiDung " +
                         "LEFT JOIN ThongTinHanhKhach hk ON nd.maNguoiDung = hk.maNguoiDung " +
                         "WHERE pdv.maPhieuDatVe = ?";
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maPhieu);
            ResultSet rs = pst.executeQuery();
            
            // SỬ DỤNG Set ĐỂ LỌC TRÙNG LẶP (Set tự động loại bỏ các phần tử giống nhau)
            java.util.Set<String> danhSachKhach = new java.util.LinkedHashSet<>();
            java.util.Set<String> danhSachGhe = new java.util.LinkedHashSet<>();
            java.util.Set<String> veDaCongTien = new java.util.HashSet<>(); // Dùng để check xem vé đã cộng tiền chưa
            
            BigDecimal tongGiaVe = BigDecimal.ZERO;
            BigDecimal tongDichVu = BigDecimal.ZERO;
            
            boolean isFirst = true;

            while (rs.next()) {
                if (isFirst) {
                    dto = new ThanhToanDTO();
                    dto.sdt = rs.getString("sdt") != null ? rs.getString("sdt") : "Không có";
                    dto.email = rs.getString("email") != null ? rs.getString("email") : "Không có";
                    dto.tuyenBay = rs.getString("diemDi") + " ➔ " + rs.getString("diemDen");
                    
                    if (rs.getTimestamp("ngayGioDi") != null) {
                        dto.gioDi = rs.getTimestamp("ngayGioDi").toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
                    } else dto.gioDi = "N/A";
                    
                    if (rs.getTimestamp("ngayGioDen") != null) {
                        dto.gioDen = rs.getTimestamp("ngayGioDen").toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
                    } else dto.gioDen = "N/A";
                    
                    isFirst = false;
                }
                
                // 1. Lọc trùng tên khách hàng
                String hoTen = rs.getString("hoTen");
                if (hoTen != null && !hoTen.isEmpty()) {
                    danhSachKhach.add(hoTen);
                }
                
                // 2. Lọc trùng danh sách vé/ghế
                String maVe = rs.getString("maVe") != null ? rs.getString("maVe") : "";
                String maGhe = rs.getString("maGhe") != null ? rs.getString("maGhe") : "Chưa xếp";
                
                if (!maVe.isEmpty()) {
                    String thongTinVe = "Vé: <b>" + maVe + "</b> - Ghế: <b>" + maGhe + "</b><br>";
                    danhSachGhe.add(thongTinVe);
                    
                    // 3. CHẶN CỘNG ĐÚP TIỀN: Chỉ cộng nếu mã vé này chưa có trong set veDaCongTien
                    if (!veDaCongTien.contains(maVe)) {
                        tongGiaVe = tongGiaVe.add(rs.getBigDecimal("giaVe") != null ? rs.getBigDecimal("giaVe") : BigDecimal.ZERO);
                        tongDichVu = tongDichVu.add(rs.getBigDecimal("tienDichVu") != null ? rs.getBigDecimal("tienDichVu") : BigDecimal.ZERO);
                        veDaCongTien.add(maVe); // Đánh dấu là vé này đã được cộng tiền
                    }
                }
            }
            
            // Xây dựng chuỗi HTML sau khi đã lọc sạch dữ liệu trùng lặp
            if (dto != null) {
                StringBuilder sbKhach = new StringBuilder("<html>");
                int count = 1;
                for (String kh : danhSachKhach) {
                    sbKhach.append("<b>").append(count++).append(". ").append(kh).append("</b><br>");
                }
                if (danhSachKhach.isEmpty()) sbKhach.append("<b>Khách vãng lai</b><br>");
                sbKhach.append("</html>");
                
                StringBuilder sbGhe = new StringBuilder("<html>");
                for (String ve : danhSachGhe) {
                    sbGhe.append(ve);
                }
                sbGhe.append("</html>");
                
                dto.tenKH = sbKhach.toString();
                dto.veGhe = sbGhe.toString();
                dto.giaVeGoc = tongGiaVe;
                dto.tongTienDichVu = tongDichVu;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
    // LẤY SỐ TIỀN KHUYẾN MÃI ĐÃ ĐƯỢC LƯU TỪ TRƯỚC
    public BigDecimal layTienGiamGiaTuPhieuDat(String maPhieuDatVe) {
        BigDecimal tienGiam = BigDecimal.ZERO;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT giaTriGiamThucTe FROM SuDungKhuyenMai WHERE maPhieuDatVe = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maPhieuDatVe);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                tienGiam = rs.getBigDecimal("giaTriGiamThucTe");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Đảm bảo không bao giờ trả về null, nếu không có thì trả về 0
        return tienGiam != null ? tienGiam : BigDecimal.ZERO; 
    }
    // Dán hàm này vào HoaDonDAO.java
public BigDecimal layTongTienGheCuaHoaDon(String maHoaDon) {
    BigDecimal tongTienGhe = BigDecimal.ZERO;
    try (Connection con = DBConnection.getConnection()) {
        // SQL: Tìm các vé thuộc hóa đơn -> Lấy mã ghế -> Lấy giá ghế và cộng lại
        String sql = "SELECT SUM(g.giaGhe) as tongGiaGhe " +
                     "FROM ChiTietHoaDon ct " +
                     "JOIN VeBan v ON ct.maVe = v.maVe " +
                     "JOIN GheMayBay g ON v.maGhe = g.maGhe " +
                     "WHERE ct.maHoaDon = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, maHoaDon);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            tongTienGhe = rs.getBigDecimal("tongGiaGhe");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return tongTienGhe != null ? tongTienGhe : BigDecimal.ZERO;
}
}