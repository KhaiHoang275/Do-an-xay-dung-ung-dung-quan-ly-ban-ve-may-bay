package bll;

import java.util.ArrayList;

import dal.HangVeDAO;
import model.HangVe;
import model.TrangThaiHangVe;

public class HangVeBUS {
    private HangVeDAO hangVeDAO;

    public HangVeBUS() {
        this.hangVeDAO = new HangVeDAO();
    }

    public ArrayList<HangVe> getAllHangVe() {
        return hangVeDAO.selectAll();
    }

    public ArrayList<HangVe> getHangVeTrongThungRac() {
        return hangVeDAO.selectThungRac();
    }

    public HangVe getHangVeById(String maHangVe) {
        if (maHangVe == null || maHangVe.trim().isEmpty()) {
            return null;
        }
        return hangVeDAO.selectById(maHangVe);
    }

    public boolean themHangVe(HangVe hv) throws IllegalArgumentException {
        if (hv.getMaHangVe() == null || hv.getMaHangVe().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hạng vé không được để trống!");
        }
        if (hv.getTenHang() == null || hv.getTenHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hạng vé không được để trống!");
        }
        if (hv.getHeSoHangVe() <= 0) {
            throw new IllegalArgumentException("Hệ số hạng vé phải lớn hơn 0!");
        }
        
        if (hangVeDAO.selectById(hv.getMaHangVe()) != null) {
            throw new IllegalArgumentException("Mã hạng vé '" + hv.getMaHangVe() + "' đã tồn tại trong hệ thống!");
        }

        if (hv.getTrangThai() == null) {
            hv.setTrangThai(TrangThaiHangVe.HOAT_DONG);
        }

        return hangVeDAO.insert(hv);
    }

    public boolean capNhatHangVe(HangVe hv) throws IllegalArgumentException {
        if (hv.getTenHang() == null || hv.getTenHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hạng vé không được để trống!");
        }
        if (hv.getHeSoHangVe() <= 0) {
            throw new IllegalArgumentException("Hệ số hạng vé phải lớn hơn 0!");
        }

        return hangVeDAO.update(hv);
    }

    public boolean xoaHangVe(String maHangVe) {
        if (maHangVe == null || maHangVe.trim().isEmpty()) {
            return false;
        }
        return hangVeDAO.delete(maHangVe);
    }

    public boolean khoiPhucHangVe(String maHangVe) {
        if (maHangVe == null || maHangVe.trim().isEmpty()) {
            return false;
        }
        return hangVeDAO.restore(maHangVe);
    }

    public ArrayList<HangVe> timKiemHangVe(String keyword, boolean isTrash) {
        ArrayList<HangVe> ketQua = new ArrayList<>();
        ArrayList<HangVe> source = isTrash ? getHangVeTrongThungRac() : getAllHangVe();

        if (keyword == null || keyword.trim().isEmpty()) {
            return source;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        for (HangVe hv : source) {
            if (hv.getMaHangVe().toLowerCase().contains(lowerKeyword) ||
                hv.getTenHang().toLowerCase().contains(lowerKeyword)) {
                
                ketQua.add(hv);
            }
        }
        return ketQua;
    }
}