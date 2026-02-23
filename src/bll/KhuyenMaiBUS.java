package bll;

import dal.KhuyenMaiDAO;
import dal.SuDungKhuyenMaiDAO;
import db.DBConnection;
import model.KhuyenMai;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class KhuyenMaiBUS {
    private KhuyenMaiDAO khuyenMaiDAO;
    private SuDungKhuyenMaiDAO suDungKhuyenMaiDAO;

    public KhuyenMaiBUS(){
        this.khuyenMaiDAO = new KhuyenMaiDAO();
        this.suDungKhuyenMaiDAO = new SuDungKhuyenMaiDAO();
    }
    //===================================
    //A. METHOD NGHIEP VU TRA CUU CO BAN
    //===================================
    /**
     * Lay tat ca khuyen mai tu database
     */
    public List<KhuyenMai> getAll(){
        return khuyenMaiDAO.getAll();
    }

    // lay theo ID
    public KhuyenMai getById(String maKhuyenMai){
        return khuyenMaiDAO.getById(maKhuyenMai);
    }

    //===============================
    //B. METHOD KIEM TRA VA TINH TOAN
    //===============================
    /**
     * Kiem tra khuyen mai co hop le voi so lan su dung khong
     */
    private boolean kiemTraSoLanSuDung(KhuyenMai km, String maHK, Connection conn)
        throws SQLException{
        int soLanSuDung = suDungKhuyenMaiDAO.demSoLanSuDung( km.getMaKhuyenMai(), maHK, conn);
        return soLanSuDung < km.getGioiHanMoiKhach();
    }

    /**
     * Tinh so tien giam dua tren loai voucher
     *
     * Dung RoudingMode.HALF_UP (khog dung  deprecated ROUND_HALF_UP)
     */
    public BigDecimal tinhTienGiam (KhuyenMai km, BigDecimal tongTien){
        if(km == null || tongTien == null || tongTien.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;
        BigDecimal tienGiam = BigDecimal.ZERO;
        if("PHAN_TRAM".equalsIgnoreCase(km.getLoaiKM())){
            //Loai phan tram: tienGiam = tongTien * giaTri / 100
            tienGiam = tongTien
                    .multiply(km.getGiaTri())
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }else if ("TIEN".equalsIgnoreCase(km.getLoaiKM())){
            //Loai tien co dinh
            tienGiam = km.getGiaTri();
        }

        if(tienGiam.compareTo(tongTien) > 0)
            tienGiam = tongTien;
        return tienGiam;
    }

    /**
     * Lay danh sach khuyen mai hop le
     */
    public List<KhuyenMai> getKhuyenMaiHople (BigDecimal tongTien,
                                              String loaiKhach,
                                              String maHK){
        List<KhuyenMai> danhSachHopLe = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()){
            List<KhuyenMai> tatCa = khuyenMaiDAO.getKhuyenMaiDangHoatDong(conn);

            for(KhuyenMai km : tatCa){
                if(tongTien.compareTo(km.getDonHangToiThieu()) < 0)
                    continue;
                if(!km.isApDungChoTatCa()){
                    if(!khuyenMaiDAO.isLoaiKhachHopLe(km.getLoaiKhachApDung(), loaiKhach))
                        continue;
                }
                if(!kiemTraSoLanSuDung(km, maHK, conn))
                     continue;

                danhSachHopLe.add(km);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return danhSachHopLe;
    }
    /**
     * Chọn khuyến mãi tốt nhất (Giảm nhiều nhất)
     */
    public KhuyenMai chonKhuyenMaiTotNhat (List<KhuyenMai> danhSachHopLe,
                                           BigDecimal tongTien){
        if(danhSachHopLe == null || danhSachHopLe.isEmpty()){
            return null;
        }
        KhuyenMai kmTotNhat = null;
        BigDecimal tienGiamToiDa = BigDecimal.ZERO;
        for(KhuyenMai km : danhSachHopLe){
            BigDecimal tienGiam = tinhTienGiam(km, tongTien);
            if(tienGiam.compareTo(tienGiamToiDa) > 0) {
                tienGiamToiDa = tienGiam;
                kmTotNhat = km;
            }
        }
        return kmTotNhat;
    }

    // ========================================
    // C. NGHIỆP VỤ CHÍNH - ÁP DỤNG KHUYẾN MÃI
    // ========================================

    /**
     * Áp dụng khuyến mãi cho giao dịch
     *
     * ✅ TRANSACTION ĐÚNG CHUẨN
     * ✅ RACE CONDITION PREVENTION (SQL atomic)
     * ✅ CONCURRENCY SAFE (không dùng timestamp)
     * ✅ PROPER ERROR HANDLING
     *
     * Điều kiện:
     * - Một trong hai: maGiaoDichVe hoặc maPhieuDatVe phải khác null
     * - Khuyến mãi phải tồn tại và còn hiệu lực
     * - Khách chưa vượt quá giới hạn sử dụng
     *
     *  Transaction bao gồm:
     *  1. Insert vào SuDungKhuyenMai
     *  2. Update KhuyenMai (soLuongDaDung++)
     *  3. Commit/Rollback
     *
     *  @param maKhuyenMai Mã khuyến mãi
     *  @param maHK Mã hành khách
     *  @param maGD Mã giao dịch đổi vé (có thể null)
     *  @param maPhieuDatVe Mã phiếu đặt vé (có thể null)
     *  @param giaTriGiamThucTe Số tiền giảm thực tế
     *  @return true nếu áp dụng thành công
     *  @throws IllegalArgumentException nếu tham số không hợp lệ
     *  @throws SQLException nếu có lỗi transaction
     */
    public boolean apDungKhuyenMai(String maKhuyenMai,
                                   String maHK,
                                   String maGD,
                                   String maPhieuDatVe,
                                   BigDecimal giaTriGiamThucTe)
            throws SQLException{
        //===========B1. VALIDATE INPUT ===============
        validateInput(maKhuyenMai, maHK, maGD, maPhieuDatVe, giaTriGiamThucTe);
        Connection conn = null;
        //===========B2: BEGIN TRANSACTION============
        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            //Thêm isolation level (đẹp cho bảo vệ đồ án)
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            //============B3: KIEM TRA KHUYEN MAI TON TAI==========
            KhuyenMai km = khuyenMaiDAO.getByIdWithConnection(conn, maKhuyenMai);
            if(km == null){
                throw new IllegalArgumentException("Khuyến mãi không tồn tại");
            }

            // Kiểm tra điều kiện
            validateKhuyenMaiConditions(conn, km, maHK);

            // Chiếm slot
            boolean updateSuccess = khuyenMaiDAO.incrementSoLuongDaDung(conn, maKhuyenMai);
            if(!updateSuccess){
                throw new SQLException("Khuyến mãi đã hết lượt sử dụng");
            }

            //Insert lich su
            boolean insertSuccess = suDungKhuyenMaiDAO.insertSuDungKhuyenMai(
                    conn,
                    maKhuyenMai,
                    maHK,
                    maGD,
                    maPhieuDatVe,
                    giaTriGiamThucTe
            );
            if(!insertSuccess){
                throw new SQLException("Không thể lưu lịch sử sử dụng.");
            }

            conn.commit();
            return true;
        }catch (Exception e){
            if( conn != null){
                try{
                    conn.rollback();
                }catch (SQLException ignored){}
            }

            if(e instanceof  IllegalArgumentException){
                throw (IllegalArgumentException) e;
            }else if(e instanceof SQLException){
                throw (SQLException) e;
            }else{
                throw new SQLException("Lỗi áp dụng khuyến mãi.", e);
            }
        }finally {
            if(conn != null){
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException ignored){}
            }
        }
    }

    //======================
    // D. VALIDATION HELPERS
    //======================
    /**
     * Kiem tra tinh hop le cua input parameters: PHAI CO DUNG MOT TRONG HAI (maGD hoac maPhieuDatVe)
     */
    private void validateInput(String maKhuyenMai,
                               String maHK,
                               String maGD,
                               String maPhieuDatVe,
                               BigDecimal giaTriGiamThucTe){
        if(maKhuyenMai == null || maKhuyenMai.trim().isEmpty()){
            throw new IllegalArgumentException(
                    "Mã Khuyến mãi không được để trống."
            );
        }
        if(maHK == null || maHK.trim().isEmpty()){
            throw new IllegalArgumentException(
                    "mã Hành Khách không được để trống."
            );
        }

        //PHẢI CÓ ĐÚNG 1 TRONG 2 KHÁC NULL VÀ KHÔNG RỖNG
        boolean hasGiaoDich = maGD != null && !maGD.trim().isEmpty();
        boolean hasPhieu = maPhieuDatVe != null && !maPhieuDatVe.trim().isEmpty();

        if(hasPhieu == hasGiaoDich) { // ca 2 cung false or cung true => error
            throw new IllegalArgumentException(
                    "Phải cung cấp đúng một trong hai: maGD hoặc maPhieuDatVe (không được cả 2 cùng có hoặc cùng thiếu.)"
            );
        }

        if(giaTriGiamThucTe == null || giaTriGiamThucTe.compareTo(BigDecimal.ZERO) <= 0){
            throw  new IllegalArgumentException("Tiền giảm phải là số dương.");
        }
    }

    private void validateKhuyenMaiConditions(Connection conn, KhuyenMai km, String maHK)
        throws SQLException{
        // check Trang Thai
        if(!km.isTrangThai()){
            throw  new IllegalArgumentException(
                    "Khuyến mãi đã bị vô hiệu hóa."
            );
        }
        // Check han su dung
        if(!km.isActive()){
            throw new IllegalArgumentException(
                    "Khuyến mã đã hết hoặc hết hạn sữ dụng."
            );
        }

        int soLanDaDung = suDungKhuyenMaiDAO.demSoLanSuDung(km.getMaKhuyenMai(), maHK, conn);
        if(soLanDaDung >= km.getGioiHanMoiKhach()){
            throw new IllegalArgumentException(
                    "Khách hàng đã vượt quá giới hạn sử dụng voucher này"
            );
        }
    }
}
