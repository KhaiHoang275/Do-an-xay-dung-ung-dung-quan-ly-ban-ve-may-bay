package bll;

import dal.CTHoaDonDAO;
import dal.ChiTietDichVuDAO;
import model.CTHoaDon;
import java.math.BigDecimal;
import java.util.List;

public class CTHoaDonBUS {

    private CTHoaDonDAO ctHoaDonDAO;
    private ChiTietDichVuDAO chiTietDVDAO = new ChiTietDichVuDAO();
    private dal.HanhLyDAO hanhLyDAO = new dal.HanhLyDAO();
    
    public CTHoaDonBUS() {
        ctHoaDonDAO = new CTHoaDonDAO();
    }

    // Lấy toàn bộ chi tiết hóa đơn
    public List<CTHoaDon> docDanhSachCTHoaDon() {
        return ctHoaDonDAO.selectAll();
    }

    // Bổ sung: Lấy danh sách chi tiết của một hóa đơn cụ thể (Rất hay dùng ở giao diện)
    public List<CTHoaDon> docDanhSachCTHoaDonTheoMa(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return null; // Hoặc trả về list rỗng tùy logic của bạn
        }
        return ctHoaDonDAO.selectByMaHoaDon(maHoaDon);
    }

    // Thêm chi tiết hóa đơn
    public String themCTHoaDon(CTHoaDon ct) {
        if (ct.getMaHoaDon() == null || ct.getMaHoaDon().trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không được để trống!";
        }

        if (ct.getMaVe() == null || ct.getMaVe().trim().isEmpty()) {
            return "Lỗi: Mã vé không được để trống!";
        }

        // Kiểm tra tính hợp lệ của các trường tiền (không được null và phải >= 0)
        if (ct.getDonGiaVe() == null || ct.getDonGiaVe().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Đơn giá vé không hợp lệ!";
        }
        if (ct.getTienDichVu() == null || ct.getTienDichVu().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Tiền dịch vụ không hợp lệ!";
        }
        if (ct.getThueVAT() == null || ct.getThueVAT().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Thuế VAT không hợp lệ!";
        }
        if (ct.getThanhTien() == null || ct.getThanhTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Thành tiền không hợp lệ!";
        }
      // 1. Tính tiền dịch vụ (Hành lý + Dịch vụ phụ)
        BigDecimal tienDichVuThat = tinhTongTienDichVuCuaVe(ct.getMaVe());
        ct.setTienDichVu(tienDichVuThat);

        // 2. CÔNG THỨC MỚI: Thành tiền = Đơn giá vé + Tiền dịch vụ + Thuế VAT
        BigDecimal thanhTienMoi = ct.getDonGiaVe()
                                    .add(ct.getTienDichVu())
                                    .add(ct.getThueVAT());
        ct.setThanhTien(thanhTienMoi);

        boolean isSuccess = ctHoaDonDAO.insert(ct);
        if(isSuccess) {
            // Cập nhật lại hóa đơn tổng
            new HoaDonBUS().capNhatTongTienVaThueTuDong(ct.getMaHoaDon());
        }
        return isSuccess ? "Thành công" : "Lỗi: Thêm chi tiết thất bại!";
    }

    // Xóa chi tiết hóa đơn (Dùng kết hợp mã hóa đơn và mã vé vì không còn maCTHD)
    public String xoaCTHoaDon(String maHoaDon, String maVe) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return "Lỗi: Mã hóa đơn không hợp lệ!";
        }
        if (maVe == null || maVe.trim().isEmpty()) {
            return "Lỗi: Mã vé không hợp lệ!";
        }
        
        boolean isSuccess = ctHoaDonDAO.delete(maHoaDon, maVe);
        if (isSuccess) {
        new HoaDonBUS().capNhatTongTienVaThueTuDong(maHoaDon);
    }
        return isSuccess ? "Thành công" : "Lỗi: Xóa chi tiết hóa đơn thất bại!";
    }
    public BigDecimal tinhTongTienDichVuCuaVe(String maVe) {
        BigDecimal tong = BigDecimal.ZERO;

        // 1. Cộng tiền các dịch vụ phụ (Giữ nguyên logic cũ của bạn)
        List<model.ChiTietDichVu> dsDichVu = chiTietDVDAO.selectByMaVe(maVe);
        if (dsDichVu != null) {
            for (model.ChiTietDichVu dv : dsDichVu) {
                if (dv.getThanhTien() != null) {
                    tong = tong.add(dv.getThanhTien());
                }
            }
        }

        // 2. BỔ SUNG MỚI: Cộng thêm tiền Hành Lý ký gửi
        // Giả sử trong HanhLyDAO bạn đã có hàm lấy danh sách hành lý theo mã vé
        List<model.HanhLy> dsHanhLy = hanhLyDAO.selectByMaVe(maVe);
        if (dsHanhLy != null) {
            for (model.HanhLy hl : dsHanhLy) {
                if (hl.getGiaTien() != null) {
                    tong = tong.add(hl.getGiaTien());
                }
            }
        }

        return tong;
    }
}