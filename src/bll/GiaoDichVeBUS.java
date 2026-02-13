package bll;
import dal.GiaoDichVeDAO;
import dal.VeBanDAO;
import dal.HangVeDAO;
import entity.GiaoDichVe;
import entity.HangVe;
import entity.VeBan;
import entity.TrangThaiGiaoDich;
import db.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private HangVeDAO hangVeDAO;

    public GiaoDichVeBUS() {
        this.giaoDichVeDAO = new GiaoDichVeDAO();
        this.veBanDAO = new VeBanDAO();
        this.hangVeDAO = new HangVeDAO();
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

    // ============================================
    // B. NGHIỆP VỤ PHỤ TRỢ
    // ============================================

    /**
     * Tính phí giao dịch dựa trên hạng vé cũ
     * @param maVeCu Mã vé cũ
     * @return Phí giao dịch
     */
    private BigDecimal tinhPhiGiaoDich(String maVeCu){
        VeBan veCu = veBanDAO.selectById(maVeCu);
        if(veCu == null) return BigDecimal.ZERO;

        HangVe hv = hangVeDAO.selectById(veCu.getMaHangVe());
        if(hv == null) return BigDecimal.ZERO;

        BigDecimal phiCoBan = new BigDecimal("100000");
        return phiCoBan.multiply(BigDecimal.valueOf(hv.getHeSoHangVe()));
    }

    /**
     * Tính phí chênh lệch giữa vé mới và vé cũ
     * Nếu âm (vé mới rẻ hơn) thì = 0
     * @param maVeMoi Mã vé mới
     * @param maVeCu Mã vé cũ
     * @return Phí chênh lệch
     */
    private BigDecimal tinhPhiChenhLech(String maVeMoi, String maVeCu){
        VeBan veMoi = veBanDAO.selectById(maVeMoi);
        VeBan veCu = veBanDAO.selectById(maVeCu);

        if(veMoi == null || veCu == null){
            return BigDecimal.ZERO;
        }

        BigDecimal chenhLech = veMoi.getGiaVe().subtract(veCu.getGiaVe()); // veMoi - veCu

        // nếu âm (vé mới rẻ hơn) thì trả về 0
        return chenhLech.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : chenhLech;
    }

    /**
     *  Tạo mã giao dịch tự động
     *  Format: GDxxx (xxx = số tự tăng)
     *  @return Mã giao dịch mới
     */
    private String taoMaGD(){
        List<GiaoDichVe> danhSach = giaoDichVeDAO.findAll();
        int maxNum = 0;

        for(GiaoDichVe gd : danhSach){
            String ma = gd.getMaGD();
            if(ma != null && ma.startsWith("GD")){
                try{
                    int number = Integer.parseInt(ma.substring(2));
                    if(number > maxNum) maxNum = number;
                } catch (NumberFormatException e){
                    // bo qua cac ma khong dung format
                }
            }
        }

        int newNum = maxNum + 1;
        return String.format("GD%03d", newNum);
    }

    //========================
    // C.NGHIỆP VỤ CHÍNH
    //========================

    /**
     *  1. Tạo yêu cầu đổi vé
     *  - Kiểm tra vé cũ và vé mới tồn tại
     *  - Tính phí giao dịch và phí chênh lệch
     *  - Trạng thái ban đầu: CHO_XU_LY
     *  - Ngày yêu cầu: ngày hiện tại
     *
     *  @param maVeMoi Mã vé mới muốn đổi sang
     *  @param maVeCu Mã vé cũ cần đổi
     *  @param lyDo Lý do đổi vé
     *  @return Mã giao dịch vừa tạo
     *  @throws IllegalArgumentException nếu vé không tồn tại
     *  @throws SQLException nếu lỗi database
     */

    public String taoYeuCauDoiVe(String maVeMoi, String maVeCu, String lyDo)
            throws SQLException{
        //b1: Validate veCu va veMoi
        validateVeCuTonTai(maVeCu);
        validateVeMoiTonTai(maVeMoi);

        //b2: tao ma giao dich tu dong
        String maGD = taoMaGD();

        //b3: tinh cac loai phi
        BigDecimal phi = tinhPhiGiaoDich(maVeCu);
        BigDecimal phiChenhLech = tinhPhiChenhLech(maVeMoi, maVeCu);

        //b4: Tao doi tuong GiaoDichVe
        GiaoDichVe gd = new GiaoDichVe();
        gd.setMaGD(maGD);
        gd.setMaVeMoi(maVeMoi);
        gd.setMaVeCu(maVeCu);
        gd.setTrangThai(TrangThaiGiaoDich.CHO_XU_LY);
        gd.setPhi(phi);
        gd.setPhiChenhLech(phiChenhLech);
        gd.setLyDo(lyDo);
        gd.setNgayYeuCau(LocalDate.now());
        gd.setNgayXuLi(null);

        //b5: luu vao database
        boolean ketQua = giaoDichVeDAO.insert(gd);

        if(!ketQua){
            throw new SQLException("Không thể lưu yêu cầu đổi vé vào database!");
        }
        System.out.println("✅ Tao y/c thanh cong. Ma GD: " + maGD);
        return maGD;
    }

    /**
     *  2. Duyệt yêu cầu đổi vé
     *  - Chỉ duyệt khi trạng thái = CHO_XU_LY
     *  - Cập nhật trạng thái thành DA_DUYET
     *  - Cập nhật ngày xử lý
     *
     *  @param maGD Mã giao dịch cần duyệt
     *  @return true nếu duyệt thành công
     *  @throws IllegalArgumentException nếu không tìm thấy giao dịch
     *  @throws IllegalStateException nếu trạng thái không hợp lệ
     */

    public boolean duyetYeuCau(String maGD){
        //b1: lay thong tin giao dich
        GiaoDichVe giaoDich = giaoDichVeDAO.findById(maGD);
        if(giaoDich == null) throw new IllegalArgumentException("Không tìm thấy giao dịch với mã: " + maGD);

        //b2: Kiem tra trang thai phai la CHO_XU_LY
        validateTrangThai(giaoDich, TrangThaiGiaoDich.CHO_XU_LY);

        //b3: Cap nhat trang thai va ngay xu ly
        giaoDich.setTrangThai(TrangThaiGiaoDich.DA_DUYET);
        giaoDich.setNgayXuLi(LocalDate.now());

        // b4: luu vao database
        boolean ketQua = giaoDichVeDAO.update(giaoDich);

        if (ketQua){
            System.out.println("✅ Duyệt yêu cầu đổi vé thành công. Mã GD: " + maGD);
        }
        return ketQua;
    }

    /**
     *  3. Từ chối yêu cầu đổi vé
     *  - Chỉ từ chối khi trạng thái = CHO_XU_LY
     *  - Cập nhật trạng thái thành TU_CHOI
     *  - Cập nhật ngày xử lý
     *  * @param maGD Mã giao dịch cần từ chối
     *  @return true nếu từ chối thành công
     *  @throws IllegalArgumentException nếu không tìm thấy giao dịch
     *  @throws IllegalStateException nếu trạng thái không hợp lệ
     */
    public boolean tuChoiYeuCau(String maGD){
        GiaoDichVe giaoDich = giaoDichVeDAO.findById(maGD);
        if(giaoDich == null) throw new IllegalArgumentException("Không tìm thấy giao dịch với mã: " + maGD);

        validateTrangThai(giaoDich, TrangThaiGiaoDich.CHO_XU_LY);

        giaoDich.setTrangThai(TrangThaiGiaoDich.TU_CHOI);
        giaoDich.setNgayXuLi(LocalDate.now());

        boolean ketQua = giaoDichVeDAO.update(giaoDich);

        if (ketQua){
            System.out.println("❌ Từ chối yêu cầu đổi vé thành công. Mã GD: " + maGD);
        }
        return ketQua;
    }
    /**
     *  4. Xử lý thanh toán đổi vé
     *  - Chỉ thực hiện khi trạng thái = DA_DUYET
     *  - Sử dụng TRANSACTION để đảm bảo tính toàn vẹn dữ liệu
     *  - Cập nhật 3 bảng: GiaoDichVe, VeBan (vé cũ), VeBan (vé mới)
     *
     *  Các bước trong transaction:
     *  1. Cập nhật GiaoDichVe -> DA_THANH_TOAN
     *  2. Cập nhật Vé cũ -> trangThaiVe = "DA_DOI"
     *  3. Cập nhật Vé mới -> trangThaiVe = "DA_THANH_TOAN"
     *  @param maGD Mã giao dịch cần thanh toán
     *  @return true nếu thanh toán thành công
     *  @throws IllegalArgumentException nếu không tìm thấy giao dịch
     *  @throws IllegalStateException nếu trạng thái không hợp lệ
     *  @throws SQLException nếu có lỗi trong quá trình transaction
     */
    public boolean xuLyThanhToan(String maGD) throws SQLException{
        Connection conn = null;
        try {
            // b1: Lay connection va tat auto-commit
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            System.out.println("🔄 Bắt đầu transaction thanh toán...");

            //b2: lay thong tin giao dich
            GiaoDichVe giaoDich = giaoDichVeDAO.findById(maGD);
            if(giaoDich == null) throw new IllegalArgumentException("Không tìm thấy giao dịch với mã: "+ maGD);

            //b3: kiem tra trang thai phai la DA_DUYET
            validateTrangThai(giaoDich, TrangThaiGiaoDich.DA_DUYET);

            //b4: Cap nhat trang thai GiaoDichVe --> DA_THANH_TOAN
            giaoDich.setTrangThai(TrangThaiGiaoDich.DA_THANH_TOAN);
            boolean updateGD = giaoDichVeDAO.update(giaoDich);

            if(!updateGD){
                throw new SQLException("Không thể cập nhật trạng thái giao dịch!");
            }
            System.out.println("  ✓ Cập nhật giao dịch -> DA_THANH_TOAN");

            //b5: cap nhat ve cu -> DA_DOI
            VeBan veCu = veBanDAO.selectById(giaoDich.getMaVeCu());
            if(veCu == null) throw new SQLException("Khong tim thay ve cu tren he thong!");

            veCu.setTrangThaiVe("DA_DOI");
            boolean updateVeCu = veBanDAO.update(veCu);

            if (!updateVeCu) {
                throw new SQLException("Không thể cập nhật trạng thái vé cũ!");
            }
            System.out.println("  ✓ Cập nhật vé cũ -> DA_DOI");

            //b6: cap nhat ve moi -> DA_THANHTOAN
            VeBan veMoi = veBanDAO.selectById(giaoDich.getMaVeMoi());
            if(veMoi == null) throw new SQLException("Không tìm thấy vé mới trong hệ thống !");

            veMoi.setTrangThaiVe("DA_THANH_TOAN");
            boolean updateVeMoi = veBanDAO.update(veMoi);
            if (!updateVeMoi) {
                throw new SQLException("Không thể cập nhật trạng thái vé mới!");
            }
            System.out.println("  ✓ Cập nhật vé mới -> DA_THANH_TOAN");

            //b7: commit transaction
            conn.commit();
            System.out.println("✅ Thanh toán đổi vé thành công! Mã GD: " + maGD);
            return true;

        } catch (Exception e){
            //ROLLBACK neu co loi (rollback: quay lai trang thai nhu luc dau (veCu, veMoi))
            if(conn != null){
                try{
                    conn.rollback(); // chỉ có tác dụng nếu đã conn.setAutoCommit(false)
                    System.err.println("⚠️ Đã rollback transaction do lỗi!"  );
                } catch (SQLException rollbackEx){
                    System.err.println("❌ Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            }

            System.err.println("❌ Lỗi thanh toán: " + e.getMessage());
            throw new SQLException("Thanh toán thất bại: " + e.getMessage(), e);
        } finally {
            // khoi phuc auto-commmit va dong connection
            if(conn != null){
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e){
                    System.err.println("Lỗi đóng connection: " + e.getMessage());
                }
            }
        }
    }

    //====================================
    // D.METHODS TRA CỨU & TIỆN ÍCH
    //====================================
    /**
     * Lấy tất cả giao dịch
     * @return Danh sách tất cả giao dịch
     */
    public List<GiaoDichVe> getAllGiaoDich(){
        return giaoDichVeDAO.findAll();
    }

    /**
     * Lấy giao dịch theo mã
     * @param maGD Mã giao dịch
     * @return Đối tượng GiaoDichVe hoặc null
     */
    public GiaoDichVe getGiaoDichById(String maGD){
        return giaoDichVeDAO.findById(maGD);
    }

    /**
     * Lấy danh sách giao dịch theo mã vé cũ
     * @param maVeCu Mã vé cũ
     * @return Danh sách giao dịch
     */
    public List<GiaoDichVe> getGiaoDichByMaVeCu (String maVeCu){
        return giaoDichVeDAO.findByMaVeCu(maVeCu);
    }

    /**
     * Lấy danh sách giao dịch theo trạng thái
     * @param trangThai Trạng thái cần lọc
     * @return Danh sách giao dịch
     */
    public List<GiaoDichVe> getGiaoDichByTrangThai (TrangThaiGiaoDich trangThai){
        List<GiaoDichVe> result = new ArrayList<>();
        List<GiaoDichVe> all  = giaoDichVeDAO.findAll();

        for (GiaoDichVe gd : all){
            if(gd.getTrangThai() == trangThai){
                result.add(gd);
            }
        }
        return result;
    }

    /**
     * Xóa giao dịch (chỉ cho phép xóa khi chưa thanh toán)
     * @param maGD Mã giao dịch cần xóa
     * @return true nếu xóa thành công
     * @throws IllegalStateException nếu giao dịch đã hoàn thành
     */
    public boolean xoaGiaoDich(String maGD){
        // kiem tra trang thai trc khi xoa
        validateChuaHoanThanh(maGD);
        return giaoDichVeDAO.delete(maGD);
    }
}
