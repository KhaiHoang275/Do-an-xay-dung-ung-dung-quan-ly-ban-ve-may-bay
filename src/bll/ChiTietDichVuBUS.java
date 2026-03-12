package bll;

import dal.ChiTietDichVuDAO;
import dal.DichVuBoSungDAO; // Thêm thư viện này để lấy đơn giá
import model.ChiTietDichVu;
import model.DichVuBoSung;

import java.math.BigDecimal;
import java.util.List;

public class ChiTietDichVuBUS {

    private ChiTietDichVuDAO chiTietDAO;
    private DichVuBoSungDAO dichVuBoSungDAO; 

    public ChiTietDichVuBUS() {
        chiTietDAO = new ChiTietDichVuDAO();
        dichVuBoSungDAO = new DichVuBoSungDAO(); 
    }

    public List<ChiTietDichVu> docDanhSachChiTiet() {
        return chiTietDAO.selectAll();
    }

    public List<ChiTietDichVu> docDanhSachTheoMaVe(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return null; 
        }
        return chiTietDAO.selectByMaVe(maVe); 
    }

    // =========================================================
    // HÀM NÀY ĐÃ ĐƯỢC NÂNG CẤP ĐỂ TỰ ĐỘNG TÍNH THÀNH TIỀN
    // =========================================================
    public String themChiTiet(ChiTietDichVu ct) {
        // 1. Kiểm tra đầu vào cơ bản
        if (ct.getMaVe() == null || ct.getMaVe().trim().isEmpty()) {
            return "Lỗi: Mã vé không được để trống!";
        }
        if (ct.getMaDichVu() == null || ct.getMaDichVu().trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không được để trống!";
        }
        if (ct.getSoLuong() <= 0) {
            return "Lỗi: Số lượng phải lớn hơn 0!";
        }

        // 2. Tự động tính Thành tiền = Đơn giá * Số lượng
        DichVuBoSung dv = dichVuBoSungDAO.selectById(ct.getMaDichVu());
        if (dv == null) {
            return "Lỗi: Không tìm thấy dịch vụ bổ sung này trong cơ sở dữ liệu!";
        }

        BigDecimal donGia = dv.getDonGia();
        BigDecimal thanhTien = donGia.multiply(new BigDecimal(ct.getSoLuong())); // Phép nhân tự động
        ct.setThanhTien(thanhTien); // Gắn tiền vừa tính vào đối tượng

        // 3. Đẩy xuống DAO để lưu vào CSDL
        boolean isSuccess = chiTietDAO.insert(ct);
        return isSuccess ? "Thành công" : "Lỗi: Thêm chi tiết dịch vụ thất bại!";
    }

    public String xoaChiTiet(String maVe, String maDichVu) {
        if (maVe == null || maVe.trim().isEmpty() || maDichVu == null || maDichVu.trim().isEmpty()) {
            return "Lỗi: Mã vé hoặc mã dịch vụ không hợp lệ!";
        }
        
        boolean isSuccess = chiTietDAO.delete(maVe, maDichVu);
        return isSuccess ? "Thành công" : "Lỗi: Xóa chi tiết dịch vụ thất bại!";
    }
}