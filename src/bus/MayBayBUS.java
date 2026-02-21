package bus;

import dal.MayBayDAO;
import model.MayBay;
import java.util.ArrayList;

public class MayBayBUS {
    private MayBayDAO dao = new MayBayDAO();

    public ArrayList<MayBay> getAllMayBay() {
        return dao.selectAll();
    }

    public String themMayBay(MayBay mb) {
        if (mb.getMaMayBay().isEmpty()) return "Mã máy bay không được để trống";
        if (dao.insert(mb)) return "Thêm thành công";
        return "Thêm thất bại";
    }

    public String suaMayBay(MayBay mb) {
        if (dao.update(mb)) return "Cập nhật thành công";
        return "Cập nhật thất bại";
    }

    public String xoaMayBay(String maMayBay) {
        if (dao.delete(maMayBay)) return "Xóa thành công"; // Lưu ý sửa lại tham số bên DAO nếu cần
        return "Xóa thất bại";
    }
}