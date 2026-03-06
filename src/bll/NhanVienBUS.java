package bll;

import dal.NhanVienDAO;
import model.NhanVien;
import java.util.ArrayList;

public class NhanVienBUS {
    private NhanVienDAO dao = new NhanVienDAO();

    public ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> all = dao.selectAll();
        ArrayList<NhanVien> active = new ArrayList<>();
        for (NhanVien nv : all) {
            if (nv.getTrangThaiLamViec() == NhanVien.TrangThai.HOAT_DONG) {
                active.add(nv);
            }
        }
        return active;
    }

    public ArrayList<NhanVien> getNhanVienTrongThungRac() {
        ArrayList<NhanVien> all = dao.selectAll();
        ArrayList<NhanVien> trash = new ArrayList<>();
        for (NhanVien nv : all) {
            if (nv.getTrangThaiLamViec() == NhanVien.TrangThai.NGUNG_HOAT_DONG) {
                trash.add(nv);
            }
        }
        return trash;
    }

    public ArrayList<NhanVien> timKiemNhanVien(String keyword, boolean isTrash) {
        ArrayList<NhanVien> list = isTrash ? getNhanVienTrongThungRac() : getAllNhanVien();
        ArrayList<NhanVien> result = new ArrayList<>();
        keyword = keyword.toLowerCase().trim();
        for (NhanVien nv : list) {
            if (nv.getMaNV().toLowerCase().contains(keyword) || 
               (nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(keyword))) {
                result.add(nv);
            }
        }
        return result;
    }

    public String themNhanVien(NhanVien nv) {
        if (nv.getMaNV() == null || nv.getMaNV().trim().isEmpty()) return "Mã nhân viên không được để trống";
        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()) return "Họ tên không được để trống";
        if (dao.insert(nv)) return "Thêm nhân viên thành công";
        return "Thêm nhân viên thất bại (Mã NV có thể đã tồn tại)";
    }

    public String suaNhanVien(NhanVien nv) {
        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()) return "Họ tên không được để trống";
        if (dao.update(nv)) return "Cập nhật nhân viên thành công";
        return "Cập nhật thất bại";
    }

    public String xoaNhanVien(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return "Mã nhân viên không hợp lệ";
        if (dao.delete(maNV)) return "Đã đưa nhân viên vào thùng rác";
        return "Đưa vào thùng rác thất bại";
    }
}