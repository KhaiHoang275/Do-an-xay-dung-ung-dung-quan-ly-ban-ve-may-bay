package bus;

import dal.NhanVienDAO;
import model.NhanVien;
import java.util.ArrayList;

public class NhanVienBUS {
    private NhanVienDAO dao = new NhanVienDAO();

    public ArrayList<NhanVien> getAllNhanVien() {
        return dao.selectAll();
    }

    public String themNhanVien(NhanVien nv) {
        if (nv.getMaNV().isEmpty()) return "Mã nhân viên không được để trống";
        if (dao.insert(nv)) return "Thêm thành công";
        return "Thêm thất bại";
    }

    public String suaNhanVien(NhanVien nv) {
        if (dao.update(nv)) return "Cập nhật thành công";
        return "Cập nhật thất bại";
    }

    public String xoaNhanVien(String maNV) {
        if (dao.delete(maNV)) return "Xóa thành công";
        return "Xóa thất bại";
    }
}