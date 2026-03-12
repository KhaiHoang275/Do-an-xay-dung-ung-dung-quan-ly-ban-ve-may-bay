package bll;

import dal.ChiTietDichVuDAO;
import dal.DichVuBoSungDAO; 
import model.DichVuBoSung;
import java.math.BigDecimal;
import java.util.List;

public class DichVuBoSungBUS {
    
    private DichVuBoSungDAO dichVuDAO;
    private ChiTietDichVuDAO chiTietDAO; // Thêm DAO này để check khóa ngoại khi xóa

    public DichVuBoSungBUS() {
        dichVuDAO = new DichVuBoSungDAO();
        chiTietDAO = new ChiTietDichVuDAO();
    }

    public List<DichVuBoSung> docDanhSachDichVu() {
        return dichVuDAO.selectAll();
    }

    public String themDichVu(DichVuBoSung dv) {
        if (dv.getMaDichVu() == null || dv.getMaDichVu().trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không được để trống!";
        }
        if (dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            return "Lỗi: Tên dịch vụ không được để trống!";
        }
        if (dv.getDonGia() == null || dv.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Đơn giá dịch vụ không hợp lệ (phải >= 0)!";
        }
        // Bổ sung check trạng thái
        if (dv.getTrangThai() == null || dv.getTrangThai().trim().isEmpty()) {
            return "Lỗi: Trạng thái không được để trống!";
        }
        
        if (dichVuDAO.selectById(dv.getMaDichVu()) != null) {
            return "Lỗi: Mã dịch vụ này đã tồn tại trong hệ thống!";
        }

        boolean isSuccess = dichVuDAO.insert(dv);
        return isSuccess ? "Thành công" : "Lỗi: Không thể thêm vào cơ sở dữ liệu!";
    }

    public String capNhatDichVu(DichVuBoSung dv) {
        // Bổ sung các validate cơ bản cho an toàn
        if (dv.getMaDichVu() == null || dv.getMaDichVu().trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không hợp lệ!";
        }
        if (dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            return "Lỗi: Tên dịch vụ không được để trống!";
        }
        if (dv.getDonGia() == null || dv.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Đơn giá cập nhật không hợp lệ!";
        }
        if (dv.getTrangThai() == null || dv.getTrangThai().trim().isEmpty()) {
            return "Lỗi: Trạng thái không được để trống!";
        }

        boolean isSuccess = dichVuDAO.update(dv);
        return isSuccess ? "Thành công" : "Lỗi: Cập nhật thất bại!";
    }

    public String xoaDichVu(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không hợp lệ!";
        }

        // Check khóa ngoại: Xem dịch vụ này đã có ai đặt chưa (nếu DAO của bạn có hàm selectByMaDichVu)
        // Nếu dự án của bạn làm chặt, hãy mở comment đoạn này:
        /*
        if (!chiTietDAO.selectByMaDichVu(maDichVu).isEmpty()) {
            return "Lỗi: Không thể xóa vì đã có hành khách đặt dịch vụ này. Vui lòng chuyển trạng thái thành 'Ngừng hoạt động'!";
        }
        */

        boolean isSuccess = dichVuDAO.delete(maDichVu);
        return isSuccess ? "Thành công" : "Lỗi: Xóa thất bại!";
    }
}