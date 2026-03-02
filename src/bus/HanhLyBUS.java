package bus;

import dal.HanhLyDAO;
import model.HanhLy;
import java.math.BigDecimal;
import java.util.List;

public class HanhLyBUS {

    private HanhLyDAO hanhLyDAO;

    public HanhLyBUS() {
        hanhLyDAO = new HanhLyDAO();
    }

    public List<HanhLy> docDanhSachHanhLy() {
        return hanhLyDAO.selectAll();
    }

    public String themHanhLy(HanhLy hl) {
        if (hl.getMaHanhLy() == null || hl.getMaHanhLy().trim().isEmpty()) {
            return "Lỗi: Mã hành lý không được để trống!";
        }
        
        // Đã sửa thành getSoKg() cho khớp model
        if (hl.getSoKg() == null || hl.getSoKg().compareTo(BigDecimal.ZERO) <= 0) {
            return "Lỗi: Số Kg hành lý phải lớn hơn 0!";
        }

        if (hl.getGiaTien() == null || hl.getGiaTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Giá tiền hành lý không hợp lệ!";
        }

        if (hanhLyDAO.selectById(hl.getMaHanhLy()) != null) {
            return "Lỗi: Mã hành lý này đã tồn tại!";
        }

        boolean isSuccess = hanhLyDAO.insert(hl);
        return isSuccess ? "Thành công" : "Lỗi: Thêm hành lý thất bại!";
    }

    public String capNhatHanhLy(HanhLy hl) {
        if (hl.getGiaTien() == null || hl.getGiaTien().compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗi: Giá tiền cập nhật không hợp lệ!";
        }

        boolean isSuccess = hanhLyDAO.update(hl);
        return isSuccess ? "Thành công" : "Lỗi: Cập nhật hành lý thất bại!";
    }

    public String xoaHanhLy(String maHanhLy) {
        if (maHanhLy == null || maHanhLy.trim().isEmpty()) {
            return "Lỗi: Mã hành lý không hợp lệ!";
        }

        boolean isSuccess = hanhLyDAO.delete(maHanhLy);
        return isSuccess ? "Thành công" : "Lỗi: Xóa hành lý thất bại!";
    }
    
}