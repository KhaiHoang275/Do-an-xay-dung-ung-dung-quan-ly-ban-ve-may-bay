package bll;

import dal.NguoiDungDAO;
import model.NguoiDung;
import java.util.ArrayList;

public class NguoiDungBUS {
    private NguoiDungDAO dao = new NguoiDungDAO();

    public ArrayList<NguoiDung> getAllNguoiDung() {
        ArrayList<NguoiDung> all = dao.selectAll();
        ArrayList<NguoiDung> active = new ArrayList<>();
        for (NguoiDung nd : all) {
            if (nd.getTrangThaiTK() == NguoiDung.TrangThai.HOAT_DONG) {
                active.add(nd);
            }
        }
        return active;
    }

    public ArrayList<NguoiDung> getNguoiDungTrongThungRac() {
        ArrayList<NguoiDung> all = dao.selectAll();
        ArrayList<NguoiDung> trash = new ArrayList<>();
        for (NguoiDung nd : all) {
            if (nd.getTrangThaiTK() == NguoiDung.TrangThai.NGUNG_HOAT_DONG) {
                trash.add(nd);
            }
        }
        return trash;
    }

    public ArrayList<NguoiDung> timKiemNguoiDung(String keyword, boolean isTrash) {
        ArrayList<NguoiDung> list = isTrash ? getNguoiDungTrongThungRac() : getAllNguoiDung();
        ArrayList<NguoiDung> result = new ArrayList<>();
        keyword = keyword.toLowerCase().trim();
        for (NguoiDung nd : list) {
            if (nd.getUsername().toLowerCase().contains(keyword) || 
               (nd.getEmail() != null && nd.getEmail().toLowerCase().contains(keyword))) {
                result.add(nd);
            }
        }
        return result;
    }

    public NguoiDung checkLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) return null;
        return dao.checkLogin(username, password);
    }

    public String themNguoiDung(NguoiDung nd) {
        if (nd.getMaNguoiDung().isEmpty() || nd.getUsername().isEmpty()) {
            return "Mã người dùng và tên đăng nhập không được để trống";
        }
        if (dao.insert(nd)) return "Thêm thành công";
        return "Thêm thất bại";
    }

    public String suaNguoiDung(NguoiDung nd) {
        if (dao.update(nd)) return "Cập nhật thành công";
        return "Cập nhật thất bại";
    }

    public String xoaNguoiDung(String maNguoiDung) {
        if (dao.delete(maNguoiDung)) return "Đã chuyển tài khoản vào thùng rác!";
        return "Xóa thất bại";
    }
}