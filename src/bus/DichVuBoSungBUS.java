package bus;

import dal.DichVuBoSungDAO; 
import model.DichVuBoSung;
import java.math.BigDecimal;
import java.util.List;

public class DichVuBoSungBUS {
    
    private DichVuBoSungDAO dichVuDAO;

    public DichVuBoSungBUS() {
        dichVuDAO = new DichVuBoSungDAO();
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
        
        // SỬA LỖI Ở ĐÂY: Dùng compareTo cho BigDecimal
        if (dv.getDonGia() == null || dv.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Đơn giá dịch vụ không hợp lệ (phải >= 0)!";
        }
        
        if (dichVuDAO.selectById(dv.getMaDichVu()) != null) {
            return "Lỗi: Mã dịch vụ này đã tồn tại trong hệ thống!";
        }

        boolean isSuccess = dichVuDAO.insert(dv);
        return isSuccess ? "Thành công" : "Lỗi: Không thể thêm vào cơ sở dữ liệu!";
    }

    public String capNhatDichVu(DichVuBoSung dv) {
        if (dv.getDonGia() == null || dv.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Đơn giá cập nhật không hợp lệ!";
        }

        boolean isSuccess = dichVuDAO.update(dv);
        return isSuccess ? "Thành công" : "Lỗi: Cập nhật thất bại!";
    }

    public String xoaDichVu(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không hợp lệ!";
        }

        boolean isSuccess = dichVuDAO.delete(maDichVu);
        return isSuccess ? "Thành công" : "Lỗi: Xóa thất bại!";
    }
}