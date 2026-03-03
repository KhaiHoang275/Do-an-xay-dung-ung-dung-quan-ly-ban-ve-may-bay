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

    public ArrayList<ChuyenBay> searchChuyenBay(String from, String to, String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return chuyenBayDAO.searchFlight(from, to, date);
        } catch (Exception e) {
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
}