package bll;

import dal.ChiTietDichVuDAO;
import dal.DichVuBoSungDAO;
import dal.VeBanDAO;
import model.ChiTietDichVu;
import model.DichVuBoSung;

import java.math.BigDecimal;
import java.util.List;

public class ChiTietDichVuBUS {

    private ChiTietDichVuDAO chiTietDAO;
    private DichVuBoSungDAO dichVuBoSungDAO; 
    private VeBanDAO veBanDAO;

    public ChiTietDichVuBUS() {
        chiTietDAO = new ChiTietDichVuDAO();
        dichVuBoSungDAO = new DichVuBoSungDAO(); 
        veBanDAO = new VeBanDAO();
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

   public String themChiTiet(ChiTietDichVu ct) {
        // 1. Kiểm tra đầu vào cơ bản
       // 1. Kiểm tra đầu vào cơ bản
        if (ct.getMaVe() == null || ct.getMaVe().trim().isEmpty()) {
            return "Lỗi: Mã vé không được để trống!";
        }
        if (veBanDAO.selectById(ct.getMaVe()) == null) {
            return "Lỗi: Mã vé '" + ct.getMaVe() + "' không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
        }

        if (ct.getMaDichVu() == null || ct.getMaDichVu().trim().isEmpty()) {
            return "Lỗi: Mã dịch vụ không được để trống!";
        }
        if (ct.getSoLuong() <= 0) {
            return "Lỗi: Số lượng thêm mới phải lớn hơn 0!";
        }

        // 2. Lấy thông tin dịch vụ để tính tiền
        DichVuBoSung dv = dichVuBoSungDAO.selectById(ct.getMaDichVu());
        if (dv == null || dv.getDonGia() == null) { // Chống lỗi NullPointerException
            return "Lỗi: Không tìm thấy dịch vụ hoặc dịch vụ chưa có giá!";
        }
        BigDecimal donGia = dv.getDonGia();

        // 3. KIỂM TRA XEM VÉ ĐÃ CÓ DỊCH VỤ NÀY CHƯA
        List<ChiTietDichVu> dsDaCo = chiTietDAO.selectByMaVe(ct.getMaVe());
        ChiTietDichVu chiTietTonTai = null;
        for (ChiTietDichVu item : dsDaCo) {
            if (item.getMaDichVu().equals(ct.getMaDichVu())) {
                chiTietTonTai = item;
                break;
            }
        }

        // 4. XỬ LÝ LƯU XUỐNG CSDL
        if (chiTietTonTai != null) {
            // TRƯỜNG HỢP ĐÃ TỒN TẠI: Cộng dồn số lượng và cập nhật
            int soLuongMoi = chiTietTonTai.getSoLuong() + ct.getSoLuong();
            BigDecimal thanhTienMoi = donGia.multiply(new BigDecimal(soLuongMoi));
            
            ct.setSoLuong(soLuongMoi);
            ct.setThanhTien(thanhTienMoi);
            
            boolean isSuccess = chiTietDAO.update(ct); // Gọi hàm Update mới viết
            return isSuccess ? "Thành công (Đã cộng dồn số lượng)" : "Lỗi: Cập nhật dịch vụ thất bại!";
        } else {
            // TRƯỜNG HỢP MUA MỚI: Tính tiền và Insert bình thường
            BigDecimal thanhTien = donGia.multiply(new BigDecimal(ct.getSoLuong()));
            ct.setThanhTien(thanhTien);
            
            boolean isSuccess = chiTietDAO.insert(ct);
            return isSuccess ? "Thành công" : "Lỗi: Thêm chi tiết dịch vụ thất bại!";
        }
    }
    public String xoaChiTiet(String maVe, String maDichVu) {
        if (maVe == null || maVe.trim().isEmpty() || maDichVu == null || maDichVu.trim().isEmpty()) {
            return "Lỗi: Mã vé hoặc mã dịch vụ không hợp lệ!";
        }
        
        boolean isSuccess = chiTietDAO.delete(maVe, maDichVu);
        return isSuccess ? "Thành công" : "Lỗi: Xóa chi tiết dịch vụ thất bại!";
    }
}