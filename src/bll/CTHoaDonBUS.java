package bll;

import dal.CTHoaDonDAO;
import dal.ChiTietDichVuDAO;
import model.CTHoaDon;
import java.math.BigDecimal;
import java.util.List;

public class CTHoaDonBUS {

    private CTHoaDonDAO ctHoaDonDAO;
    private ChiTietDichVuDAO chiTietDVDAO = new ChiTietDichVuDAO();

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

        boolean isSuccess = ctHoaDonDAO.insert(ct);
        return isSuccess ? "Thành công" : "Lỗi: Thêm chi tiết hóa đơn thất bại!";
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
        return isSuccess ? "Thành công" : "Lỗi: Xóa chi tiết hóa đơn thất bại!";
    }
        public BigDecimal tinhTongTienDichVuCuaVe(String maVe) {
        List<model.ChiTietDichVu> ds = chiTietDVDAO.selectByMaVe(maVe);
        BigDecimal tong = BigDecimal.ZERO;
        for (model.ChiTietDichVu dv : ds) {
            if (dv.getThanhTien() != null) {
                tong = tong.add(dv.getThanhTien());
            }
        }
        return tong;
    }
}