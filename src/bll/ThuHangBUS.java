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

        if (th == null) {
            return tongTien;
        }

        double tiLeGiam = th.getTiLeGiam();
        return tongTien - (tongTien * tiLeGiam / 100);
    }

    /**
     * Cộng điểm tích lũy cho khách sau khi thanh toán
     * Ví dụ: cứ 10.000 VND = 1 điểm
     */
    public int tinhDiemCongThem(double soTienThanhToan) {
        return (int) (soTienThanhToan / 10000);
    }

    /**
     * Cập nhật điểm + hạng mới cho khách
     */
    public void capNhatDiemVaThuHang(String maHanhKhach, double soTienThanhToan) {

        // 1️⃣ Lấy điểm hiện tại
        int diemHienTai = hanhKhachDAO.getDiemTichLuy(maHanhKhach);

        // 2️⃣ Tính điểm cộng thêm
        int diemCongThem = tinhDiemCongThem(soTienThanhToan);

        int tongDiemMoi = diemHienTai + diemCongThem;

        // 3️⃣ Xác định hạng mới
        ThuHang thuHangMoi = xacDinhThuHang(tongDiemMoi);

        // 4️⃣ Update vào database
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

    // Các phương thức CRUD để hỗ trợ GUI
    public List<ThuHang> getAll() {
        return thuHangDAO.getAll();
    }

    public boolean insert(ThuHang th) {
        // Có thể thêm logic business nếu cần, ví dụ kiểm tra điều kiện
        return thuHangDAO.insert(th);
    }

    public boolean update(ThuHang th) {
        // Có thể thêm logic business nếu cần
        return thuHangDAO.update(th);
    }

    public boolean delete(String maThuHang) {
        // Có thể thêm logic business, ví dụ kiểm tra nếu có khách hàng đang dùng hạng này
        return thuHangDAO.delete(maThuHang);
    }

    public boolean isExist(String ma) {
        return thuHangDAO.selectById(ma) != null;
    }

    public boolean isTenExist(String ten) {
        return thuHangDAO.selectByTen(ten) != null;
    }
}