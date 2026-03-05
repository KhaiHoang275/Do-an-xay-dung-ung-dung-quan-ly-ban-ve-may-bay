package bll;
import dal.*;
import model.*;
import db.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public VeBanDAO veBanDAO;
    private HangVeDAO hangVeDAO;
    private ChuyenBayDAO chuyenBayDAO;
    private ThongTinHanhKhachDAO thongTinHanhKhachDAO;
    private NguoiDungDAO nguoiDungDAO;
    private ThuHangDAO thuHangDAO;
    private GheMayBayDAO gheMayBayDAO;
    private TuyenBayDAO tuyenBayDAO;
    private HeSoGiaDAO heSoGiaDAO;
    private MayBayDAO mayBayDAO;

    public GiaoDichVeBUS() {
        this.giaoDichVeDAO = new GiaoDichVeDAO();
        this.veBanDAO = new VeBanDAO();
        this.hangVeDAO = new HangVeDAO();
        this.chuyenBayDAO = new ChuyenBayDAO();
        this.tuyenBayDAO = new TuyenBayDAO();
        this.heSoGiaDAO = new HeSoGiaDAO();
        this.thongTinHanhKhachDAO = new ThongTinHanhKhachDAO();
        this.nguoiDungDAO = new NguoiDungDAO();
        this.thuHangDAO = new ThuHangDAO();
        this.gheMayBayDAO = new GheMayBayDAO();
        this.mayBayDAO = new MayBayDAO();
    }

    private boolean kiemTraVeTonTai(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) return false;
        VeBan ve = veBanDAO.selectById(maVe);
        return ve != null;
    }

    private boolean kiemTraChuyenBayTonTai(String maChuyenBay) {
        if (maChuyenBay == null || maChuyenBay.trim().isEmpty()) return false;
        ChuyenBay cb = chuyenBayDAO.selectById(maChuyenBay);
        return cb != null;
    }

    private boolean kiemTraHangVeTonTai(String maHangVe) {
        if (maHangVe == null || maHangVe.trim().isEmpty()) return false;
        HangVe hv = hangVeDAO.selectById(maHangVe);
        return hv != null;
    }

    private boolean kiemTraGheMoiTonTai(String maGhe){
        if(maGhe == null || maGhe.trim().isEmpty()) return false;
        GheMayBay gheMayBay = gheMayBayDAO.selectById(maGhe);
        return gheMayBay != null;
    }

    private boolean validateVeCuTonTai(String maVeCu) {
        if (!kiemTraVeTonTai(maVeCu)) {
            throw new IllegalArgumentException("Vé cũ không tồn tại trong hệ thống!");
        }
        // Thêm kiểm tra trạng thái vé cũ phải là ĐÃ THANH TOÁN
        VeBan veCu = veBanDAO.selectById(maVeCu);
        if (!"Đã thanh toán".equalsIgnoreCase(veCu.getTrangThaiVe())) {
            throw new IllegalArgumentException("Vé cũ phải ở trạng thái đã thanh toán để đổi!");
        }
        return true;
    }

    private boolean validateChuyenBayMoiTonTai(String maChuyenBayMoi) {
        if (!kiemTraChuyenBayTonTai(maChuyenBayMoi)) {
            throw new IllegalArgumentException("Chuyến bay mới không tồn tại trong hệ thống!");
        }
        return true;
    }

    private boolean validateHangVeMoiTonTai(String maHangVeMoi) {
        if (!kiemTraHangVeTonTai(maHangVeMoi)) {
            throw new IllegalArgumentException("Hạng vé mới không tồn tại trong hệ thống!");
        }
        return true;
    }

    private void validateCungTuyenBay(String maChuyenBayMoi, String maVeCu) {
        VeBan veCu = veBanDAO.selectById(maVeCu);
        ChuyenBay cbCu = chuyenBayDAO.selectById(veCu.getMaChuyenBay());
        ChuyenBay cbMoi = chuyenBayDAO.selectById(maChuyenBayMoi);
        if (!cbCu.getMaTuyenBay().equals(cbMoi.getMaTuyenBay())) {
            throw new IllegalArgumentException("Chuyến bay mới phải cùng tuyến bay với vé cũ!");
        }
    }

    private boolean validateGheMoiTonTai(String maGheMoi){
        if(!kiemTraGheMoiTonTai(maGheMoi)){
            throw new IllegalArgumentException("Ghế mới không tồn tại trong hệ thống");
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
    public BigDecimal tinhPhiGiaoDich(String maVeCu){
        VeBan veCu = veBanDAO.selectById(maVeCu);
        if(veCu == null) return BigDecimal.ZERO;

        HangVe hv = hangVeDAO.selectById(veCu.getMaHangVe());
        if(hv == null) return BigDecimal.ZERO;

        BigDecimal phiCoBan = new BigDecimal("100000");
        return phiCoBan.multiply(BigDecimal.valueOf(hv.getHeSoHangVe()));
    }

    /**
     * Tính giá vé tiềm năng cho vé mới dựa trên chuyến bay và hạng vé
     * Giả định: ChuyenBay có phương thức getGiaVeCoBan() trả về giá cơ bản
     * Nếu không có, bạn cần điều chỉnh logic tính giá (ví dụ: từ TuyenBay hoặc công thức khác)
     * @param maChuyenBayMoi Mã chuyến bay mới
     * @param maHangVeMoi Mã hạng vé mới
     * @return Giá vé mới
     */
    public BigDecimal tinhGiaVeMoi(String maChuyenBayMoi, String maHangVeMoi) {
        ChuyenBay cbMoi = chuyenBayDAO.selectById(maChuyenBayMoi);
        if (cbMoi == null) return BigDecimal.ZERO;

        TuyenBay tb = tuyenBayDAO.selectById(cbMoi.getMaTuyenBay());
        if (tb == null) return BigDecimal.ZERO;

        HeSoGia hsg = heSoGiaDAO.selectById(cbMoi.getMaHeSoGia()); // Giả định model HeSoGia có getHeSoGia() trả về float
        float heSoGia = (hsg == null) ? 1.0f : hsg.getHeSo();

        HangVe hvMoi = hangVeDAO.selectById(maHangVeMoi);
        if (hvMoi == null) return BigDecimal.ZERO;

        BigDecimal giaGoc = tb.getGiaGoc();
        float heSoHangVe = hvMoi.getHeSoHangVe();

        return giaGoc.multiply(BigDecimal.valueOf(heSoGia).multiply(BigDecimal.valueOf(heSoHangVe)));
    }

    /**
     * Tính phí chênh lệch giữa vé mới và vé cũ
     * Nếu âm (vé mới rẻ hơn) thì = 0
     * @param maChuyenBayMoi Mã chuyến bay mới
     * @param maHangVeMoi Mã hạng vé mới
     * @param maVeCu Mã vé cũ
     * @return Phí chênh lệch
     */
    public BigDecimal tinhPhiChenhLech(String maChuyenBayMoi, String maHangVeMoi, String maVeCu){
        VeBan veCu = veBanDAO.selectById(maVeCu);
        if (veCu == null) return BigDecimal.ZERO;

        BigDecimal giaMoi = tinhGiaVeMoi(maChuyenBayMoi, maHangVeMoi);
        BigDecimal giaCu = veCu.getGiaVe();

        BigDecimal chenhLech = giaMoi.subtract(giaCu);
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

    /**
     * Tạo mã vé tự động cho vé mới
     * Format: VExxx (giả định, điều chỉnh nếu khác)
     * @return Mã vé mới
     */
    private String taoMaVe() {
        List<VeBan> danhSach = veBanDAO.selectAll();
        int maxNum = 0;

        for (VeBan ve : danhSach) {
            String ma = ve.getMaVe();
            if (ma != null && ma.startsWith("VE")) {
                try {
                    int number = Integer.parseInt(ma.substring(2));
                    if (number > maxNum) maxNum = number;
                } catch (NumberFormatException e) {
                    // Bỏ qua
                }
            }
        }

        int newNum = maxNum + 1;
        return String.format("VE%03d", newNum);
    }

    //========================
    // C.NGHIỆP VỤ CHÍNH
    //========================

    /**
     *  1. Tạo yêu cầu đổi vé
     *  - Kiểm tra vé cũ tồn tại và ở trạng thái đã thanh toán
     *  - Kiểm tra chuyến bay mới, hạng vé mới tồn tại
     *  - Tính phí giao dịch và phí chênh lệch
     *  - Trạng thái ban đầu: CHO_XU_LY
     *  - Ngày yêu cầu: ngày hiện tại
     *
     *  @param maChuyenBayMoi Mã chuyến bay mới
     *  @param maHangVeMoi Mã hạng vé mới
     *  @param maGheMoi Mã ghế mới
     *  @param maVeCu Mã vé cũ cần đổi
     *  @param lyDo Lý do đổi vé
     *  @return Mã giao dịch vừa tạo
     *  @throws IllegalArgumentException nếu vé không tồn tại hoặc không hợp lệ
     *  @throws SQLException nếu lỗi database
     */

    public String taoYeuCauDoiVe(String maChuyenBayMoi, String maHangVeMoi, String maGheMoi, String maVeCu, String lyDo)
            throws SQLException{
        //b1: Validate veCu, chuyenBayMoi, hangVeMoi
        validateVeCuTonTai(maVeCu);
        validateChuyenBayMoiTonTai(maChuyenBayMoi);
        validateHangVeMoiTonTai(maHangVeMoi);
        validateGheMoiTonTai(maGheMoi);


        //b2: tao ma giao dich tu dong
        String maGD = taoMaGD();

        //b3: tinh cac loai phi
        BigDecimal phi = tinhPhiGiaoDich(maVeCu);
        BigDecimal phiChenhLech = tinhPhiChenhLech(maChuyenBayMoi, maHangVeMoi, maVeCu);

        //b4: Tao doi tuong GiaoDichVe
        GiaoDichVe gd = new GiaoDichVe();
        gd.setMaGD(maGD);
        gd.setMaVeCu(maVeCu);
        gd.setMaChuyenBayMoi(maChuyenBayMoi);
        gd.setMaHangVeMoi(maHangVeMoi);
        gd.setMaGheMoi(maGheMoi);
        gd.setTrangThai( TrangThaiGiaoDich.CHO_XU_LY);
        gd.setPhi(phi);
        gd.setPhiChenhLech(phiChenhLech);
        gd.setLyDoDoi(lyDo);
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
     *  - Cập nhật trạng thái thành DA_THANH_TOAN
     *  - Cập nhật ngày xử lý
     *  - Hủy vé cũ và tạo vé mới trong transaction
     *
     *  @param maGD Mã giao dịch cần duyệt
     *  @return true nếu duyệt thành công
     *  @throws IllegalArgumentException nếu không tìm thấy giao dịch
     *  @throws IllegalStateException nếu trạng thái không hợp lệ
     *  @throws SQLException nếu có lỗi trong quá trình transaction
     */

    public boolean duyetYeuCau(String maGD) throws SQLException {
        Connection conn = null;
        try {
            // b1: Lay connection va tat auto-commit
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            System.out.println("🔄 Bắt đầu transaction duyệt yêu cầu...");

            //b2: lay thong tin giao dich
            GiaoDichVe giaoDich = giaoDichVeDAO.findById(maGD);
            if(giaoDich == null) throw new IllegalArgumentException("Không tìm thấy giao dịch với mã: "+ maGD);

            //b3: kiem tra trang thai phai la CHO_XU_LY
            validateTrangThai(giaoDich, TrangThaiGiaoDich.CHO_XU_LY);

            //b4: Cap nhat trang thai GiaoDichVe --> DA_THANH_TOAN
            giaoDich.setTrangThai(TrangThaiGiaoDich.DA_THANH_TOAN);
            giaoDich.setNgayXuLi(LocalDate.now());
            boolean updateGD = giaoDichVeDAO.update(giaoDich);

            if(!updateGD){
                throw new SQLException("Không thể cập nhật trạng thái giao dịch!");
            }
            System.out.println("  ✓ Cập nhật giao dịch -> Đã thanh toán");

            //b5: cap nhat ve cu -> DA_HUY
            VeBan veCu = veBanDAO.selectById(giaoDich.getMaVeCu());
            if(veCu == null) throw new SQLException("Khong tim thay ve cu tren he thong!");

            veCu.setTrangThaiVe("Đã hủy");
            boolean updateVeCu = veBanDAO.update(veCu);

            if (!updateVeCu) {
                throw new SQLException("Không thể cập nhật trạng thái vé cũ!");
            }
            System.out.println("  ✓ Cập nhật vé cũ -> Đã hủy");

            //b6: tao va insert ve moi -> DA_THANH_TOAN
            VeBan veMoi = new VeBan();
            veMoi.setMaVe(taoMaVe()); // Tạo mã vé mới
            veMoi.setMaPhieuDatVe(null);
            veMoi.setMaChuyenBay(giaoDich.getMaChuyenBayMoi());
            veMoi.setMaHK(veCu.getMaHK());
            veMoi.setMaHangVe(giaoDich.getMaHangVeMoi());
            veMoi.setMaGhe(giaoDich.getMaGheMoi());
            veMoi.setLoaiVe(veCu.getLoaiVe());
            veMoi.setLoaiHK(veCu.getLoaiHK());
            veMoi.setGiaVe(tinhGiaVeMoi(giaoDich.getMaChuyenBayMoi(), giaoDich.getMaHangVeMoi())); // Tính giá mới
            veMoi.setTrangThaiVe("Đã thanh toán");

            boolean insertVeMoi = veBanDAO.insert(veMoi);
            if (!insertVeMoi) {
                throw new SQLException("Không thể tạo vé mới!");
            }
            System.out.println("  ✓ Tạo vé mới -> Đã thanh toán");

            //b7: commit transaction
            conn.commit();
            System.out.println("✅ Duyệt yêu cầu đổi vé thành công! Mã GD: " + maGD);
            return true;

        } catch (Exception e){
            //ROLLBACK neu co loi
            if(conn != null){
                try{
                    conn.rollback();
                    System.err.println("⚠️ Đã rollback transaction do lỗi!"  );
                } catch (SQLException rollbackEx){
                    System.err.println("❌ Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            }

            System.err.println("❌ Lỗi duyệt: " + e.getMessage());
            throw new SQLException("Duyệt thất bại: " + e.getMessage(), e);
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

    public String kiemTraDieuKienDoiVe(String maVe, String maNguoiDung){
        VeBan ve = veBanDAO.selectById(maVe);
        if(ve == null){
            return "Vé không tồn tại";
        }
        ThongTinHanhKhach tthkCuaVe = thongTinHanhKhachDAO.getByMaHK(ve.getMaHK());
        if (tthkCuaVe == null) {
            return "Không tìm thấy thông tin hành khách của vé.";
        }
        if(!tthkCuaVe.getMaNguoiDung().equals(maNguoiDung)){
            return "Vé không thuộc về người dùng này";
        }

        NguoiDung nd = nguoiDungDAO.getByMaNguoiDung(maNguoiDung);
        if(nd == null) return "Người dùng không tồn tại";

        ThuHang th = thuHangDAO.selectById(tthkCuaVe.getMaThuHang());
        if(th == null) return "Không xác định được hạng thành viên";

        ChuyenBay cb = chuyenBayDAO.selectById(ve.getMaChuyenBay());
        if(cb == null) return "Không tìm thấy chuyến bay";

        LocalDate ngayBay = cb.getNgayGioDi().toLocalDate();
        LocalDate homNay = LocalDate.now();

        long soNgay = ChronoUnit.DAYS.between(homNay, ngayBay);
        if(soNgay < 0){
            return "Chuyến bay đã khởi hành";
        }

        BigDecimal giaVe = ve.getGiaVe();
        String tenHang = th.getTenThuHang().toLowerCase();

        // ================= SILVER =================
        if (tenHang.equals("silver")) {

            if (soNgay < 5) {
                return "Hạng Silver chỉ được đổi vé khi còn ít nhất 5 ngày.";
            }

            if (giaVe.compareTo(new BigDecimal("2000000")) <= 0) {
                return "Hạng Silver chỉ đổi vé khi giá vé trên 2.000.000.";
            }

        }
        // ================= GOLD =================
        else if (tenHang.equals("gold")) {

            if (soNgay < 3) {
                return "Hạng Gold chỉ được đổi vé khi còn ít nhất 3 ngày.";
            }

            if (giaVe.compareTo(new BigDecimal("1000000")) <= 0) {
                return "Hạng Gold chỉ đổi vé khi giá vé trên 1.000.000.";
            }

        }
        // ================= PLATINUM =================
        else if (tenHang.equals("platinum")) {

            if (soNgay < 2) {
                return "Hạng Platinum chỉ được đổi vé khi còn ít nhất 2 ngày.";
            }
        }

        return "OK";
    }

    public List<ChuyenBay> danhSachChuyenBayCoTheDoi(String maVeCu) {

        List<ChuyenBay> result = new ArrayList<>();

        VeBan veCu = veBanDAO.selectById(maVeCu);
        if (veCu == null) return result;

        ChuyenBay cbCu = chuyenBayDAO.selectById(veCu.getMaChuyenBay());
        if (cbCu == null) return result;

        // chuyến bay cũ phải chưa khởi hành
        if (cbCu.getTrangThai() != TrangThaiChuyenBay.CHUA_KHOI_HANH)
            return result;

        String maTuyenBay = cbCu.getMaTuyenBay();
        LocalDateTime ngayGioDiCu = cbCu.getNgayGioDi();

        List<ChuyenBay> allChuyenBay = chuyenBayDAO.findByMaTuyenBay(maTuyenBay);

        for (ChuyenBay cb : allChuyenBay) {

            // bỏ chính chuyến bay cũ
            if (cb.getMaChuyenBay().equals(cbCu.getMaChuyenBay()))
                continue;

            // phải là chuyến bay sau chuyến cũ
            if (!cb.getNgayGioDi().isAfter(ngayGioDiCu))
                continue;

            // phải chưa khởi hành
            if (cb.getTrangThai() != TrangThaiChuyenBay.CHUA_KHOI_HANH)
                continue;

            MayBay mb = mayBayDAO.selectById(cb.getMaMayBay());
            if (mb == null)
                continue;

            int tongGhe = mb.getTongSoGhe();
            int soVeDaBan = veBanDAO.countByChuyenBay(cb.getMaChuyenBay());

            if (soVeDaBan < tongGhe) {
                result.add(cb);
            }
        }

        return result;
    }

}