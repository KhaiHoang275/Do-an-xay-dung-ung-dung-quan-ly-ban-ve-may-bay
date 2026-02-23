package bus;

import dal.SanBayDAO;
import model.SanBay;
import java.util.ArrayList;

public class SanBayBUS {
    private SanBayDAO dao = new SanBayDAO();

    public ArrayList<SanBay> getAllSanBay() {
        return dao.selectAll();
    }

    public String themSanBay(SanBay sb) {
        if (sb.getMaSanBay().isEmpty()) return "Mã sân bay không được để trống";
        if (dao.insert(sb)) return "Thêm thành công";
        return "Thêm thất bại";
    }

    public String suaSanBay(SanBay sb) {
        if (dao.update(sb)) return "Cập nhật thành công";
        return "Cập nhật thất bại";
    }

    public String xoaSanBay(String maSanBay) {
        if (dao.delete(maSanBay)) return "Xóa thành công";
        return "Xóa thất bại";
    }
}