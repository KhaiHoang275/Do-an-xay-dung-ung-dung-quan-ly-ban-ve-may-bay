package bll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import dal.ChuyenBayDAO;
import dal.TuyenBayDAO;
import dal.HeSoGiaDAO;
import dal.HangVeDAO;
import model.ChuyenBay;
import model.TuyenBay;
import model.TrangThaiChuyenBay;
import model.HeSoGia;
import model.HangVe;

public class ChuyenBayBUS {
    private ChuyenBayDAO chuyenBayDAO;
    private TuyenBayDAO tuyenBayDAO; 
    private HeSoGiaDAO heSoGiaDAO;   
    private HangVeDAO hangVeDAO;     

    public ChuyenBayBUS() {
        this.chuyenBayDAO = new ChuyenBayDAO();
        this.tuyenBayDAO = new TuyenBayDAO();
        this.heSoGiaDAO = new HeSoGiaDAO();
        this.hangVeDAO = new HangVeDAO();
    }

    
    public boolean themChuyenBay(ChuyenBay cb) throws IllegalArgumentException {
        if (cb.getMaChuyenBay() == null || cb.getMaChuyenBay().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chuyến bay không được để trống!");
        }
        if (chuyenBayDAO.selectById(cb.getMaChuyenBay()) != null) {
            throw new IllegalArgumentException("Mã chuyến bay đã tồn tại!");
        }
        if (cb.getTrangThai() == null) {
            cb.setTrangThai(TrangThaiChuyenBay.CHUA_KHOI_HANH);
        }
        return chuyenBayDAO.insert(cb);
    }

    public ArrayList<ChuyenBay> getAllChuyenBay() {
        return chuyenBayDAO.selectAll();
    }

    public ArrayList<ChuyenBay> getChuyenBayTrongThungRac() {
        return chuyenBayDAO.selectThungRac();
    }

    public ChuyenBay getChuyenBayById(String maChuyenBay) {
        return chuyenBayDAO.selectById(maChuyenBay);
    }

    public boolean capNhatChuyenBay(ChuyenBay cb) {
        return chuyenBayDAO.update(cb);
    }

    public boolean xoaChuyenBay(String maChuyenBay) {
        if (maChuyenBay == null || maChuyenBay.trim().isEmpty()) return false;
        return chuyenBayDAO.delete(maChuyenBay);
    }

    public boolean khoiPhucChuyenBay(String maChuyenBay) {
        if (maChuyenBay == null || maChuyenBay.trim().isEmpty()) return false;
        return chuyenBayDAO.restore(maChuyenBay);
    }

    public ArrayList<ChuyenBay> timKiemChuyenBay(String keyword, TrangThaiChuyenBay filterStatus, boolean isTrash) {
        ArrayList<ChuyenBay> source = isTrash ? getChuyenBayTrongThungRac() : getAllChuyenBay();
        ArrayList<ChuyenBay> result = new ArrayList<>();
        
        String lowerKeyword = (keyword == null) ? "" : keyword.toLowerCase().trim();

        for (ChuyenBay cb : source) {
            boolean matchKeyword = cb.getMaChuyenBay().toLowerCase().contains(lowerKeyword) ||
                                   cb.getMaTuyenBay().toLowerCase().contains(lowerKeyword) ||
                                   cb.getMaMayBay().toLowerCase().contains(lowerKeyword);
                                   
            boolean matchStatus = (filterStatus == null) || (cb.getTrangThai() == filterStatus);

            if (matchKeyword && matchStatus) {
                result.add(cb);
            }
        }
        return result;
    }


    public ArrayList<ChuyenBay> searchChuyenBay(String from, String to, String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return chuyenBayDAO.searchFlight(from, to, date);
        } catch (Exception e) {
            System.err.println("Lỗi định dạng ngày. Vui lòng nhập yyyy-MM-dd");
            return new ArrayList<>(); 
        }
    }

    public BigDecimal tinhGiaVe(String maChuyenBay, String maHangVe) {
        ChuyenBay cb = chuyenBayDAO.selectById(maChuyenBay);
        if (cb == null) throw new IllegalArgumentException("Chuyến bay không tồn tại");

        TuyenBay tb = tuyenBayDAO.selectById(cb.getMaTuyenBay());
        if (tb == null) throw new IllegalArgumentException("Tuyến bay không tồn tại");
        BigDecimal giaGoc = tb.getGiaGoc();

        HeSoGia hsg = heSoGiaDAO.selectById(cb.getMaHeSoGia());
        BigDecimal heSoGia = (hsg != null) ? BigDecimal.valueOf(hsg.getHeSo()) : BigDecimal.ONE;

        HangVe hv = hangVeDAO.selectById(maHangVe);
        BigDecimal heSoHangVe = (hv != null) ? BigDecimal.valueOf(hv.getHeSoHangVe()) : BigDecimal.ONE;

        return giaGoc.multiply(heSoGia).multiply(heSoHangVe);
    }

    public BigDecimal tinhGiaVeDon(String maChuyenBay, String loaiHK) {
        // 1. Lấy chuyến bay
        ChuyenBay cb = chuyenBayDAO.selectById(maChuyenBay);
        if (cb == null) return BigDecimal.ZERO;

        // 2. Lấy tuyến bay → giá gốc
        TuyenBay tb = tuyenBayDAO.selectById(cb.getMaTuyenBay());
        if (tb == null) return BigDecimal.ZERO;
        BigDecimal giaGoc = tb.getGiaGoc();

        // 3. Lấy hệ số giá
        HeSoGia hsg = heSoGiaDAO.selectById(cb.getMaHeSoGia());
        BigDecimal heSoGia = (hsg != null) ? BigDecimal.valueOf(hsg.getHeSo()) : BigDecimal.ONE;

        BigDecimal giaSauHeSo = giaGoc.multiply(heSoGia);

        // 4. Nhân theo loại hành khách
        switch (loaiHK) {
            case "NGUOILON":
                return giaSauHeSo;
            case "TREEM":
                return giaSauHeSo.multiply(BigDecimal.valueOf(0.75));
            case "EMBE":
                return giaSauHeSo.multiply(BigDecimal.valueOf(0.10));
            default:
                return BigDecimal.ZERO;
        }
    }

    public BigDecimal tinhGiaVe(String maChuyenBay, int nguoiLon, int treEm, int emBe) {
        BigDecimal tong = BigDecimal.ZERO;

        tong = tong.add(tinhGiaVeDon(maChuyenBay, "NGUOILON").multiply(BigDecimal.valueOf(nguoiLon)));
        tong = tong.add(tinhGiaVeDon(maChuyenBay, "TREEM").multiply(BigDecimal.valueOf(treEm)));
        tong = tong.add(tinhGiaVeDon(maChuyenBay, "EMBE").multiply(BigDecimal.valueOf(emBe)));

        return tong;
    }

    public ArrayList<String> getDanhSachMaChuyenBay() {
        ArrayList<String> listMaCB = new ArrayList<>();
        ArrayList<ChuyenBay> listCB = chuyenBayDAO.selectAll();
        for (ChuyenBay cb : listCB) {
            listMaCB.add(cb.getMaChuyenBay());
        }
        return listMaCB;
    }
}