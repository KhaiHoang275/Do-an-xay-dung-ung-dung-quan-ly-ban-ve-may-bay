package bll;
import dal.GiaoDichVeDAO;
import dal.VeBanDAO;
import entity.GiaoDichVe;
import entity.VeBan;
import entity.TrangThaiGiaoDich;
import db.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Business Logic Layer cho Giao Dịch Vé
 * Xử lý các nghiệp vụ: Tạo yêu cầu đổi vé, Duyệt, Từ chối, Thanh toán
 *
 * @author Hoàng Khang
 * @version 1.0
 */

public class GiaoDichVeBUS {
    private GiaoDichVeDAO giaoDichVeDAO;
    private VeBanDAO veBanDAO;

    public GiaoDichVeBUS() {
        this.giaoDichVeDAO = new GiaoDichVeDAO();
        this.veBanDAO = new VeBanDAO();
    }

    private boolean kiemTraVeTonTai(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) return false;
        VeBan ve = veBanDAO.selectById(maVe);
        return ve != null;
    }

    private boolean validateVeCuTonTai(String maVeCu) {
        if (!kiemTraVeTonTai(maVeCu)) {
            throw new IllegalArgumentException("Vé cũ không tồn tại trong hệ thống!");
        }
        return true;
    }

    private boolean validateVeMoiTonTai(String maVeMoi) {
        if (kiemTraVeTonTai(maVeMoi)) {
            throw new IllegalArgumentException("Vé mới không tồn tại trong hệ thống!");
        }
        return true;
    }

    // check xem trang thai giao dich co hop le de thuc hien hanh dong hay khong
    private void validateTrangThai(GiaoDichVe gd, TrangThaiGiaoDich trangThaiYeuCau) {
        if (gd.getTrangThai() != trangThaiYeuCau) {
            throw new IllegalStateException(
                    "Trạng thái giao dịch không hợp lệ! Yêu cầu: " + trangThaiYeuCau +
                            ", Hiện tại: " + gd.getTrangThai()
            );
        }
    }

    // check xem Giao dich da duoc DUYET hay THANH TOAN hay chua
    private boolean validateChuaHoanThanh(String maGD){
        GiaoDichVe gd = giaoDichVeDAO.findById(maGD);
        if(gd == null) {
            throw new IllegalArgumentException("Không tìm thấy giao dịch nào với 'mã giao dịch' là: " + maGD);
        }
        TrangThaiGiaoDich trangThai = gd.getTrangThai();
        if(trangThai == TrangThaiGiaoDich.DA_DUYET ||
            trangThai == TrangThaiGiaoDich.DA_THANH_TOAN){
            throw new IllegalStateException(
                    "Không thể thay đổi giao dịch đã được duyệt hoặc thanh toán!"
            );
        }
        return true;
    }
}
