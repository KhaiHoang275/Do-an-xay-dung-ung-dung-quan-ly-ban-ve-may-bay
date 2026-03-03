package bll;

import java.math.BigDecimal;
import java.util.ArrayList;

import dal.TuyenBayDAO;
import model.TrangThaiTuyenBay;
import model.TuyenBay;

public class TuyenBayBUS {
    private TuyenBayDAO tuyenBayDAO;

    public TuyenBayBUS() {
        this.tuyenBayDAO = new TuyenBayDAO();
    }

    public ArrayList<TuyenBay> getAllTuyenBay() {
        return tuyenBayDAO.selectAll();
    }

    public ArrayList<TuyenBay> getTuyenBayTrongThungRac() {
        return tuyenBayDAO.selectThungRac();
    }

    public TuyenBay getTuyenBayById(String maTuyenBay) {
        if (maTuyenBay == null || maTuyenBay.trim().isEmpty()) {
            return null;
        }
        return tuyenBayDAO.selectById(maTuyenBay);
    }

    public boolean themTuyenBay(TuyenBay tb) throws IllegalArgumentException {
        if (tb.getSanBayDi() == null || tb.getSanBayDen() == null) {
            throw new IllegalArgumentException("Sân bay đi và sân bay đến không được để trống!");
        }

        if (tb.getSanBayDi().equalsIgnoreCase(tb.getSanBayDen())) {
            throw new IllegalArgumentException("Sân bay đi và sân bay đến không được trùng nhau!");
        }

        if (tuyenBayDAO.isRouteExists(tb.getSanBayDi(), tb.getSanBayDen())) {
            throw new IllegalArgumentException("Tuyến bay từ " + tb.getSanBayDi() + " đến " + tb.getSanBayDen() + " đã tồn tại!");
        }

        if (tb.getKhoangCachKM() <= 0) {
            throw new IllegalArgumentException("Khoảng cách phải lớn hơn 0!");
        }
        if (tb.getGiaGoc() == null || tb.getGiaGoc().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0!");
        }

        if (tb.getTrangThai() == null) {
            tb.setTrangThai(TrangThaiTuyenBay.HOAT_DONG);
        }

        return tuyenBayDAO.insert(tb);
    }

    public boolean capNhatTuyenBay(TuyenBay tb) throws IllegalArgumentException {
        if (tb.getSanBayDi().equalsIgnoreCase(tb.getSanBayDen())) {
            throw new IllegalArgumentException("Sân bay đi và sân bay đến không được trùng nhau!");
        }

        if (tb.getKhoangCachKM() <= 0) {
            throw new IllegalArgumentException("Khoảng cách phải lớn hơn 0!");
        }
        
        if (tb.getGiaGoc() == null || tb.getGiaGoc().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0!");
        }

        return tuyenBayDAO.update(tb);
    }

    public boolean xoaTuyenBay(String maTuyenBay) {
        if (maTuyenBay == null || maTuyenBay.trim().isEmpty()) {
            return false;
        }
        
        return tuyenBayDAO.delete(maTuyenBay);
    }

    public boolean khoiPhucTuyenBay(String maTuyenBay) {
        if (maTuyenBay == null || maTuyenBay.trim().isEmpty()) {
            return false;
        }
        return tuyenBayDAO.restore(maTuyenBay);
    }

    public ArrayList<TuyenBay> timKiemTuyenBay(String keyword, boolean isTrash) {
        ArrayList<TuyenBay> ketQua = new ArrayList<>();
        ArrayList<TuyenBay> source = isTrash ? getTuyenBayTrongThungRac() : getAllTuyenBay();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return source;
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();

        for (TuyenBay tb : source) {
            if (tb.getMaTuyenBay().toLowerCase().contains(lowerKeyword) ||
                tb.getSanBayDi().toLowerCase().contains(lowerKeyword) ||
                tb.getSanBayDen().toLowerCase().contains(lowerKeyword)) {
                
                ketQua.add(tb);
            }
        }
        return ketQua;
    }
}