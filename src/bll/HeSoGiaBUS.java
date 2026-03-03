package bll;

import java.util.ArrayList;

import dal.HeSoGiaDAO;
import model.HeSoGia;
import model.TrangThaiHeSoGia;

public class HeSoGiaBUS {
    private HeSoGiaDAO heSoGiaDAO;

    public HeSoGiaBUS() {
        this.heSoGiaDAO = new HeSoGiaDAO();
    }

    public ArrayList<HeSoGia> getAllHeSoGia() {
        return heSoGiaDAO.selectAll();
    }

    public ArrayList<HeSoGia> getHeSoGiaTrongThungRac() {
        return heSoGiaDAO.selectThungRac();
    }

    public HeSoGia getHeSoGiaById(String maHeSoGia) {
        if (maHeSoGia == null || maHeSoGia.trim().isEmpty()) {
            return null;
        }
        return heSoGiaDAO.selectById(maHeSoGia);
    }

    public boolean themHeSoGia(HeSoGia hsg) throws IllegalArgumentException {
        if (hsg.getMaHeSoGia() == null || hsg.getMaHeSoGia().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hệ số giá không được để trống!");
        }
        if (hsg.getHeSo() <= 0) {
            throw new IllegalArgumentException("Hệ số giá phải lớn hơn 0!");
        }
        if (hsg.getSoGioDatTruoc() < 0) {
            throw new IllegalArgumentException("Số giờ đặt trước không được là số âm!");
        }
        
        if (heSoGiaDAO.selectById(hsg.getMaHeSoGia()) != null) {
            throw new IllegalArgumentException("Mã hệ số giá '" + hsg.getMaHeSoGia() + "' đã tồn tại trong hệ thống!");
        }

        if (hsg.getTrangThai() == null) {
            hsg.setTrangThai(TrangThaiHeSoGia.HOAT_DONG);
        }

        return heSoGiaDAO.insert(hsg);
    }

    public boolean capNhatHeSoGia(HeSoGia hsg) throws IllegalArgumentException {
        if (hsg.getHeSo() <= 0) {
            throw new IllegalArgumentException("Hệ số giá phải lớn hơn 0!");
        }
        if (hsg.getSoGioDatTruoc() < 0) {
            throw new IllegalArgumentException("Số giờ đặt trước không được là số âm!");
        }

        return heSoGiaDAO.update(hsg);
    }

    public boolean xoaHeSoGia(String maHeSoGia) {
        if (maHeSoGia == null || maHeSoGia.trim().isEmpty()) {
            return false;
        }
        return heSoGiaDAO.delete(maHeSoGia);
    }

    public boolean khoiPhucHeSoGia(String maHeSoGia) {
        if (maHeSoGia == null || maHeSoGia.trim().isEmpty()) {
            return false;
        }
        return heSoGiaDAO.restore(maHeSoGia);
    }

    public ArrayList<HeSoGia> timKiemHeSoGia(String keyword, boolean isTrash) {
        ArrayList<HeSoGia> ketQua = new ArrayList<>();
        ArrayList<HeSoGia> source = isTrash ? getHeSoGiaTrongThungRac() : getAllHeSoGia();

        if (keyword == null || keyword.trim().isEmpty()) {
            return source;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        for (HeSoGia hsg : source) {
            if (hsg.getMaHeSoGia().toLowerCase().contains(lowerKeyword) ||
                String.valueOf(hsg.getSoGioDatTruoc()).contains(lowerKeyword)) {
                
                ketQua.add(hsg);
            }
        }
        return ketQua;
    }
}