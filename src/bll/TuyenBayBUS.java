package bll;

import java.math.BigDecimal;
import java.util.ArrayList;

import dal.TuyenBayDAO;
import model.TuyenBay;

public class TuyenBayBUS {
    private TuyenBayDAO tuyenBayDAO;

    public TuyenBayBUS() {
        this.tuyenBayDAO = new TuyenBayDAO();
    }

    public ArrayList<TuyenBay> getAllTuyenBay() {
        return tuyenBayDAO.selectAll();
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

        if (tb.getKhoangCach() <= 0) {
            throw new IllegalArgumentException("Khoảng cách phải lớn hơn 0!");
        }
        if (tb.getGiaGoc() == null || tb.getGiaGoc().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0!");
        }

        return tuyenBayDAO.insert(tb);
    }

    public boolean capNhatTuyenBay(TuyenBay tb) throws IllegalArgumentException {
        if (tb.getSanBayDi().equalsIgnoreCase(tb.getSanBayDen())) {
            throw new IllegalArgumentException("Sân bay đi và sân bay đến không được trùng nhau!");
        }

        if (tb.getKhoangCach() <= 0) {
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

    // 6. Tìm kiếm tuyến bay (Theo Mã, Sân bay đi, Sân bay đến)
    public ArrayList<TuyenBay> timKiemTuyenBay(String keyword) {
        ArrayList<TuyenBay> ketQua = new ArrayList<>();
        ArrayList<TuyenBay> tatCaTuyenBay = getAllTuyenBay(); // Lấy tất cả lên
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return tatCaTuyenBay; // Nếu không nhập gì thì trả về toàn bộ
        }
        
        // Đưa từ khóa về chữ thường để tìm kiếm không phân biệt hoa/thường
        String lowerKeyword = keyword.toLowerCase().trim();

        for (TuyenBay tb : tatCaTuyenBay) {
            // Nếu từ khóa xuất hiện trong Mã, Sân bay đi, hoặc Sân bay đến thì đưa vào danh sách kết quả
            if (tb.getMaTuyenBay().toLowerCase().contains(lowerKeyword) ||
                tb.getSanBayDi().toLowerCase().contains(lowerKeyword) ||
                tb.getSanBayDen().toLowerCase().contains(lowerKeyword)) {
                
                ketQua.add(tb);
            }
        }
        return ketQua;
    }
}