package bll;

import dal.ThuHangDAO;
import dal.ThongTinHanhKhachDAO;
import model.ThuHang;

import java.util.List;

public class ThuHangBUS {

    private ThuHangDAO thuHangDAO;
    private ThongTinHanhKhachDAO hanhKhachDAO;

    public ThuHangBUS() {
        thuHangDAO = new ThuHangDAO();
        hanhKhachDAO = new ThongTinHanhKhachDAO();
    }

    // ====================== BUSINESS LOGIC CŨ (KHÔNG ĐỔI) ======================
    /**
     * Xác định hạng dựa vào điểm tích lũy
     */
    public ThuHang xacDinhThuHang(int diemTichLuy) {
        return thuHangDAO.getThuHangTheoDiem(diemTichLuy);
    }

    /**
     * Tính tiền sau khi giảm theo hạng
     */
    public double tinhTienSauGiam(double tongTien, int diemTichLuy) {
        ThuHang th = xacDinhThuHang(diemTichLuy);
        if (th == null) return tongTien;

        double tiLeGiam = th.getTiLeGiam();
        return tongTien - (tongTien * tiLeGiam / 100);
    }

    /**
     * Cộng điểm tích lũy (10.000 VND = 1 điểm)
     */
    public int tinhDiemCongThem(double soTienThanhToan) {
        return (int) (soTienThanhToan / 10000);
    }

    /**
     * Cập nhật điểm + hạng mới cho khách sau thanh toán
     */
    public void capNhatDiemVaThuHang(String maHanhKhach, double soTienThanhToan) {
        int diemHienTai = hanhKhachDAO.getDiemTichLuy(maHanhKhach);
        int diemCongThem = tinhDiemCongThem(soTienThanhToan);
        int tongDiemMoi = diemHienTai + diemCongThem;

        ThuHang thuHangMoi = xacDinhThuHang(tongDiemMoi);

        if (thuHangMoi != null) {
            hanhKhachDAO.updateDiemVaThuHang(
                    maHanhKhach,
                    tongDiemMoi,
                    thuHangMoi.getMaThuHang()
            );
        }
    }

    /**
     * Kiểm tra khách có được nâng hạng không
     */
    public boolean kiemTraLenHang(String maHanhKhach) {
        int diem = hanhKhachDAO.getDiemTichLuy(maHanhKhach);
        ThuHang th = xacDinhThuHang(diem);
        String hangHienTai = hanhKhachDAO.getMaThuHang(maHanhKhach);

        return th != null && !th.getMaThuHang().equals(hangHienTai);
    }

    // ====================== CRUD MỚI (SOFT DELETE) ======================
    public List<ThuHang> getAll() {
        return thuHangDAO.getAll();           // chỉ lấy HOAT_DONG
    }

    public List<ThuHang> getAllDeleted() {
        return thuHangDAO.getAllDeleted();    // thùng rác
    }

    public boolean insert(ThuHang th) {
        return thuHangDAO.insert(th);
    }

    public boolean update(ThuHang th) {
        return thuHangDAO.update(th);
    }

    /**
     * XÓA MỀM + KIỂM TRA BUSINESS
     * Không cho xóa nếu có khách hàng đang dùng hạng này
     */
    public boolean xoaMem(String maThuHang) {
        // Kiểm tra xem có khách hàng nào đang dùng hạng này không
        if (hanhKhachDAO.coKhachDungThuHang(maThuHang)) {
            System.out.println("Không thể xóa mềm: Có khách hàng đang sử dụng hạng " + maThuHang);
            return false;   // hoặc bạn có thể throw exception nếu muốn
        }

        return thuHangDAO.xoaMem(maThuHang);
    }

    /**
     * KHÔI PHỤC từ thùng rác
     */
    public boolean khoiPhuc(String maThuHang) {
        return thuHangDAO.khoiPhuc(maThuHang);
    }

    public boolean isExist(String ma) {
        return thuHangDAO.selectById(ma) != null;
    }

    public boolean isTenExist(String ten) {
        return thuHangDAO.selectByTen(ten) != null;
    }

    // ====================== METHOD CŨ (để tránh lỗi code khác gọi delete) ======================
    @Deprecated
    public boolean delete(String maThuHang) {
        return xoaMem(maThuHang);   // giữ lại để code cũ không bị lỗi
    }
}