package bll;

import dal.MayBayDAO;
import model.MayBay;
import java.util.ArrayList;

public class MayBayBUS {
    private MayBayDAO dao = new MayBayDAO();

    public ArrayList<MayBay> getAllMayBay() {
        ArrayList<MayBay> all = dao.selectAll();
        ArrayList<MayBay> active = new ArrayList<>();
        for (MayBay mb : all) {
            if (mb.getTrangThai() == MayBay.TrangThai.HOAT_DONG) {
                active.add(mb);
            }
        }
        return active;
    }

    public ArrayList<MayBay> getMayBayTrongThungRac() {
        ArrayList<MayBay> all = dao.selectAll();
        ArrayList<MayBay> trash = new ArrayList<>();
        for (MayBay mb : all) {
            if (mb.getTrangThai() == MayBay.TrangThai.NGUNG_HOAT_DONG) {
                trash.add(mb);
            }
        }
        return trash;
    }

    public ArrayList<MayBay> timKiemMayBay(String keyword, boolean isTrash) {
        ArrayList<MayBay> list = isTrash ? getMayBayTrongThungRac() : getAllMayBay();
        ArrayList<MayBay> result = new ArrayList<>();
        keyword = keyword.toLowerCase().trim();
        for (MayBay mb : list) {
            if (mb.getMaMayBay().toLowerCase().contains(keyword) || 
               (mb.getSoHieu() != null && mb.getSoHieu().toLowerCase().contains(keyword))) {
                result.add(mb);
            }
        }
        return result;
    }

    public String themMayBay(MayBay mb) {
        if (mb.getMaMayBay() == null || mb.getMaMayBay().trim().isEmpty()) return "Mã máy bay không được để trống!";
        if (mb.getSoHieu() == null || mb.getSoHieu().trim().isEmpty()) return "Số hiệu máy bay không được để trống!";
        if (mb.getTongSoGhe() <= 0) return "Tổng số ghế phải lớn hơn 0!";
        
        if (dao.insert(mb)) return "Thêm máy bay thành công!";
        return "Thêm máy bay thất bại! (Có thể do mã máy bay đã tồn tại)";
    }

    public String suaMayBay(MayBay mb) {
        if (mb.getSoHieu() == null || mb.getSoHieu().trim().isEmpty()) return "Số hiệu máy bay không được để trống!";
        if (mb.getTongSoGhe() <= 0) return "Tổng số ghế phải lớn hơn 0!";
        
        if (dao.update(mb)) return "Cập nhật máy bay thành công!";
        return "Cập nhật máy bay thất bại!";
    }

    public String xoaMayBay(String maMayBay) {
        if (maMayBay == null || maMayBay.trim().isEmpty()) return "Mã máy bay không hợp lệ!";
        if (dao.delete(maMayBay)) return "Đã đưa máy bay vào thùng rác!";
        return "Đưa vào thùng rác thất bại!";
    }
}