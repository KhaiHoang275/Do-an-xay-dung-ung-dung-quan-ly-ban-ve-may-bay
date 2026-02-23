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

    public boolean themChuyenBay(ChuyenBay chuyenBay) {
        return chuyenBayDAO.insert(chuyenBay);
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
        if (cb == null) {
            throw new IllegalArgumentException("Chuyến bay không tồn tại");
        }

        TuyenBay tb = tuyenBayDAO.selectById(cb.getMaTuyenBay());
        if (tb == null) {
            throw new IllegalArgumentException("Tuyến bay không tồn tại");
        }
        BigDecimal giaGoc = tb.getGiaGoc();

        HeSoGia hsg = heSoGiaDAO.selectById(cb.getMaHeSoGia());
        BigDecimal heSoGia = (hsg != null) ? BigDecimal.valueOf(hsg.getHeSo()) : BigDecimal.ONE;

        HangVe hv = hangVeDAO.selectById(maHangVe);
        BigDecimal heSoHangVe = (hv != null) ? BigDecimal.valueOf(hv.getHeSoHangVe()) : BigDecimal.ONE;

        BigDecimal giaVe = giaGoc.multiply(heSoGia).multiply(heSoHangVe);
        
        return giaVe;
    }

    public ArrayList<ChuyenBay> getAllChuyenBay() {
        return chuyenBayDAO.selectAll();
    }

    public ChuyenBay getChuyenBayById(String maChuyenBay) {
        return chuyenBayDAO.selectById(maChuyenBay);
    }
}
