package bll;

import java.util.ArrayList;

import dal.HangVeDAO;
import model.HangVe;

public class HangVeBUS {
    private HangVeDAO hangVeDAO;

    public HangVeBUS() {
        this.hangVeDAO = new HangVeDAO();
    }

    // 1. Lấy toàn bộ danh sách Hạng vé
    public ArrayList<HangVe> getAllHangVe() {
        return hangVeDAO.selectAll();
    }

    // 2. Tìm Hạng vé theo Mã
    public HangVe getHangVeById(String maHangVe) {
        if (maHangVe == null || maHangVe.trim().isEmpty()) {
            return null;
        }
        return hangVeDAO.selectById(maHangVe);
    }

    // 3. Thêm Hạng vé mới
    public boolean themHangVe(HangVe hv) throws IllegalArgumentException {
        // Kiểm tra dữ liệu rỗng
        if (hv.getMaHangVe() == null || hv.getMaHangVe().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hạng vé không được để trống!");
        }
        if (hv.getTenHang() == null || hv.getTenHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hạng vé không được để trống!");
        }
        // Hệ số hạng vé phải lớn hơn 0 (Ví dụ: 1.0, 1.5, 2.0...)
        if (hv.getHeSoHangVe() <= 0) {
            throw new IllegalArgumentException("Hệ số hạng vé phải lớn hơn 0!");
        }
        
        // Kiểm tra trùng Mã hạng vé trong Database
        if (hangVeDAO.selectById(hv.getMaHangVe()) != null) {
            throw new IllegalArgumentException("Mã hạng vé '" + hv.getMaHangVe() + "' đã tồn tại trong hệ thống!");
        }

        return hangVeDAO.insert(hv);
    }

    // 4. Cập nhật Hạng vé
    public boolean capNhatHangVe(HangVe hv) throws IllegalArgumentException {
        if (hv.getTenHang() == null || hv.getTenHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hạng vé không được để trống!");
        }
        if (hv.getHeSoHangVe() <= 0) {
            throw new IllegalArgumentException("Hệ số hạng vé phải lớn hơn 0!");
        }

        return hangVeDAO.update(hv);
    }

    // 5. Xóa Hạng vé
    public boolean xoaHangVe(String maHangVe) {
        if (maHangVe == null || maHangVe.trim().isEmpty()) {
            return false;
        }
        return hangVeDAO.delete(maHangVe);
    }

    // 6. Tìm kiếm Hạng vé (Dùng cho thanh Search trên GUI)
    public ArrayList<HangVe> timKiemHangVe(String keyword) {
        ArrayList<HangVe> ketQua = new ArrayList<>();
        ArrayList<HangVe> tatCaHangVe = getAllHangVe();

        if (keyword == null || keyword.trim().isEmpty()) {
            return tatCaHangVe;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        for (HangVe hv : tatCaHangVe) {
            if (hv.getMaHangVe().toLowerCase().contains(lowerKeyword) ||
                hv.getTenHang().toLowerCase().contains(lowerKeyword)) {
                
                ketQua.add(hv);
            }
        }
        return ketQua;
    }
}