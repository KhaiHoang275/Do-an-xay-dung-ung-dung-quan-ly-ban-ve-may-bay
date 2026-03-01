package bll;

import java.util.ArrayList;

import dal.HeSoGiaDAO;
import model.HeSoGia;

public class HeSoGiaBUS {
    private HeSoGiaDAO heSoGiaDAO;

    public HeSoGiaBUS() {
        this.heSoGiaDAO = new HeSoGiaDAO();
    }

    // 1. Lấy toàn bộ danh sách Hệ số giá
    public ArrayList<HeSoGia> getAllHeSoGia() {
        return heSoGiaDAO.selectAll();
    }

    // 2. Lấy Hệ số giá theo Mã
    public HeSoGia getHeSoGiaById(String maHeSoGia) {
        if (maHeSoGia == null || maHeSoGia.trim().isEmpty()) {
            return null;
        }
        return heSoGiaDAO.selectById(maHeSoGia);
    }

    // 3. Thêm Hệ số giá mới
    public boolean themHeSoGia(HeSoGia hsg) throws IllegalArgumentException {
        // Kiểm tra dữ liệu rỗng
        if (hsg.getMaHeSoGia() == null || hsg.getMaHeSoGia().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hệ số giá không được để trống!");
        }
        // Kiểm tra số âm/số 0
        if (hsg.getHeSo() <= 0) {
            throw new IllegalArgumentException("Hệ số giá phải lớn hơn 0!");
        }
        if (hsg.getSoGioDatTruoc() < 0) {
            throw new IllegalArgumentException("Số giờ đặt trước không được là số âm!");
        }
        
        // Kiểm tra trùng Mã hệ số giá
        if (heSoGiaDAO.selectById(hsg.getMaHeSoGia()) != null) {
            throw new IllegalArgumentException("Mã hệ số giá '" + hsg.getMaHeSoGia() + "' đã tồn tại trong hệ thống!");
        }

        return heSoGiaDAO.insert(hsg);
    }

    // 4. Cập nhật Hệ số giá
    public boolean capNhatHeSoGia(HeSoGia hsg) throws IllegalArgumentException {
        if (hsg.getHeSo() <= 0) {
            throw new IllegalArgumentException("Hệ số giá phải lớn hơn 0!");
        }
        if (hsg.getSoGioDatTruoc() < 0) {
            throw new IllegalArgumentException("Số giờ đặt trước không được là số âm!");
        }

        return heSoGiaDAO.update(hsg);
    }

    // 5. Xóa Hệ số giá
    public boolean xoaHeSoGia(String maHeSoGia) {
        if (maHeSoGia == null || maHeSoGia.trim().isEmpty()) {
            return false;
        }
        return heSoGiaDAO.delete(maHeSoGia);
    }

    // 6. Tìm kiếm Hệ số giá (Dùng cho thanh Search)
    public ArrayList<HeSoGia> timKiemHeSoGia(String keyword) {
        ArrayList<HeSoGia> ketQua = new ArrayList<>();
        ArrayList<HeSoGia> tatCaHSG = getAllHeSoGia();

        if (keyword == null || keyword.trim().isEmpty()) {
            return tatCaHSG;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        for (HeSoGia hsg : tatCaHSG) {
            // Tìm kiếm theo Mã Hệ Số Giá
            if (hsg.getMaHeSoGia().toLowerCase().contains(lowerKeyword) ||
                String.valueOf(hsg.getSoGioDatTruoc()).contains(lowerKeyword)) {
                
                ketQua.add(hsg);
            }
        }
        return ketQua;
    }
}