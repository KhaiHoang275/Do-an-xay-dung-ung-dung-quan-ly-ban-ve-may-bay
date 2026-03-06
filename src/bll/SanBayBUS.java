package bll;

import dal.SanBayDAO;
import model.SanBay;
import java.util.ArrayList;

public class SanBayBUS {
    private SanBayDAO dao = new SanBayDAO();

    // 1. Lấy danh sách đang hoạt động
    public ArrayList<SanBay> getAllSanBay() {
        ArrayList<SanBay> all = dao.selectAll();
        ArrayList<SanBay> active = new ArrayList<>();
        for (SanBay sb : all) {
            if (sb.getTrangThai() == SanBay.TrangThai.HOAT_DONG) {
                active.add(sb);
            }
        }
        return active;
    }

    // 2. Lấy danh sách trong thùng rác
    public ArrayList<SanBay> getSanBayTrongThungRac() {
        ArrayList<SanBay> all = dao.selectAll();
        ArrayList<SanBay> trash = new ArrayList<>();
        for (SanBay sb : all) {
            if (sb.getTrangThai() == SanBay.TrangThai.NGUNG_HOAT_DONG) {
                trash.add(sb);
            }
        }
        return trash;
    }

    // 3. Tìm kiếm theo chế độ
    public ArrayList<SanBay> timKiemSanBay(String keyword, boolean isTrash) {
        ArrayList<SanBay> list = isTrash ? getSanBayTrongThungRac() : getAllSanBay();
        ArrayList<SanBay> result = new ArrayList<>();
        keyword = keyword.toLowerCase().trim();
        for (SanBay sb : list) {
            if (sb.getMaSanBay().toLowerCase().contains(keyword) || 
                sb.getTenSanBay().toLowerCase().contains(keyword)) {
                result.add(sb);
            }
        }
        return result;
    }

    public String themSanBay(SanBay sb) {
        if (sb.getMaSanBay() == null || sb.getMaSanBay().trim().isEmpty()) {
            return "Mã sân bay không được để trống!";
        }
        if (sb.getTenSanBay() == null || sb.getTenSanBay().trim().isEmpty()) {
            return "Tên sân bay không được để trống!";
        }
        if (dao.insert(sb)) return "Thêm thành công!";
        return "Thêm thất bại (Có thể mã đã tồn tại)!";
    }

    public String suaSanBay(SanBay sb) {
        if (sb.getTenSanBay() == null || sb.getTenSanBay().trim().isEmpty()) {
            return "Tên sân bay không được để trống!";
        }
        if (dao.update(sb)) return "Cập nhật thành công!";
        return "Cập nhật thất bại!";
    }

    public String xoaSanBay(String maSanBay) {
        if (maSanBay == null || maSanBay.trim().isEmpty()) return "Mã sân bay không hợp lệ!";
        if (dao.delete(maSanBay)) return "Đã đưa sân bay vào thùng rác!";
        return "Đưa vào thùng rác thất bại!";
    }
}