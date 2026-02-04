package bus;

import dal.NguoiDungDAO;
import model.NguoiDung;
import java.util.ArrayList;

public class NguoiDungBUS {
    private NguoiDungDAO dao = new NguoiDungDAO();

    public ArrayList<NguoiDung> getAllNguoiDung() {
        return dao.selectAll();
    }

    public NguoiDung checkLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return dao.checkLogin(username, password);
    }

    public String themNguoiDung(NguoiDung nd) {
        if (nd.getMaNguoiDung().isEmpty() || nd.getUsername().isEmpty()) {
            return "Mã người dùng và Tên đăng nhập không được để trống";
        }
        if (dao.insert(nd)) {
            return "Thêm thành công";
        }
        return "Thêm thất bại";
    }

    public String suaNguoiDung(NguoiDung nd) {
        if (dao.update(nd)) {
            return "Cập nhật thành công";
        }
        return "Cập nhật thất bại";
    }

    
    public String xoaNguoiDung(String maNguoiDung) {
        if (dao.delete(maNguoiDung)) {
            return "Xóa thành công";
        }
        return "Xóa thất bại";
    }
    
}