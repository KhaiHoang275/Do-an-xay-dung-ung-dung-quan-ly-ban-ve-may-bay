package bll;

import java.math.BigDecimal;
import java.util.ArrayList;

import dal.GheMayBayDAO;
import model.GheMayBay;
import model.TrangThaiGhe;

public class GheMayBayBUS {
    private GheMayBayDAO gheMayBayDAO;

    public GheMayBayBUS() {
        this.gheMayBayDAO = new GheMayBayDAO();
    }

    public ArrayList<GheMayBay> getAllGheMayBay() {
        return gheMayBayDAO.selectAll();
    }

    public ArrayList<GheMayBay> getGheTrongThungRac() {
        return gheMayBayDAO.selectThungRac();
    }

    public ArrayList<GheMayBay> getGheByMayBay(String maMayBay) {
        if (maMayBay == null || maMayBay.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return gheMayBayDAO.selectByMayBay(maMayBay);
    }

    public GheMayBay getGheById(String maGhe) {
        if (maGhe == null || maGhe.trim().isEmpty()) {
            return null;
        }
        return gheMayBayDAO.selectById(maGhe);
    }

    public boolean themGhe(GheMayBay ghe) throws IllegalArgumentException {
        if (ghe.getMaGhe() == null || ghe.getMaGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã ghế không được để trống!");
        }
        if (ghe.getMaMayBay() == null || ghe.getMaMayBay().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã máy bay không được để trống!");
        }
        if (ghe.getSoGhe() == null || ghe.getSoGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống!");
        }
        if (ghe.getGiaGhe() == null || ghe.getGiaGhe().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá ghế không được nhỏ hơn 0!");
        }

        if (gheMayBayDAO.selectById(ghe.getMaGhe()) != null) {
            throw new IllegalArgumentException("Mã ghế '" + ghe.getMaGhe() + "' đã tồn tại!");
        }

        ArrayList<GheMayBay> danhSachGheHienTai = gheMayBayDAO.selectByMayBay(ghe.getMaMayBay());
        for (GheMayBay g : danhSachGheHienTai) {
            if (g.getSoGhe().equalsIgnoreCase(ghe.getSoGhe())) {
                throw new IllegalArgumentException("Số ghế " + ghe.getSoGhe() + " đã tồn tại trên máy bay này!");
            }
        }

        if (ghe.getTrangThai() == null) {
            ghe.setTrangThai(TrangThaiGhe.TRONG);
        }

        return gheMayBayDAO.insert(ghe);
    }

    public void taoGheHangLoat(String maMayBay, int soLuong, String tienTo, BigDecimal giaGhe) throws IllegalArgumentException {
        if (maMayBay == null || maMayBay.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn máy bay!");
        }
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng ghế phải lớn hơn 0!");
        }
        if (tienTo == null || tienTo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tiền tố ghế không được để trống!");
        }
        
        for (int i = 1; i <= soLuong; i++) {
            String soGhe = tienTo + i; 
            String maGhe = maMayBay + "_" + soGhe; 
            
            GheMayBay ghe = new GheMayBay(maGhe, maMayBay, soGhe, giaGhe, TrangThaiGhe.TRONG);
            
            try {
                themGhe(ghe); 
            } catch (IllegalArgumentException e) {
                System.out.println("Bỏ qua ghế: " + soGhe);
            }
        }
    }

    public boolean capNhatGhe(GheMayBay ghe) throws IllegalArgumentException {
        if (ghe.getSoGhe() == null || ghe.getSoGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống!");
        }
        if (ghe.getGiaGhe() == null || ghe.getGiaGhe().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá ghế không được nhỏ hơn 0!");
        }

        ArrayList<GheMayBay> danhSachGheHienTai = gheMayBayDAO.selectByMayBay(ghe.getMaMayBay());
        for (GheMayBay g : danhSachGheHienTai) {
            if (!g.getMaGhe().equals(ghe.getMaGhe()) && g.getSoGhe().equalsIgnoreCase(ghe.getSoGhe())) {
                throw new IllegalArgumentException("Số ghế " + ghe.getSoGhe() + " đã bị trùng với một ghế khác trên máy bay này!");
            }
        }

        return gheMayBayDAO.update(ghe);
    }

    public boolean xoaGhe(String maGhe) {
        if (maGhe == null || maGhe.trim().isEmpty()) {
            return false;
        }
        return gheMayBayDAO.delete(maGhe);
    }

    public boolean khoiPhucGhe(String maGhe) {
        if (maGhe == null || maGhe.trim().isEmpty()) {
            return false;
        }
        return gheMayBayDAO.restore(maGhe);
    }

    public ArrayList<GheMayBay> timKiemGhe(String keyword, boolean isTrash) {
        ArrayList<GheMayBay> ketQua = new ArrayList<>();
        ArrayList<GheMayBay> source = isTrash ? getGheTrongThungRac() : getAllGheMayBay();

        if (keyword == null || keyword.trim().isEmpty()) {
            return source;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        for (GheMayBay ghe : source) {
            if (ghe.getMaGhe().toLowerCase().contains(lowerKeyword) ||
                ghe.getMaMayBay().toLowerCase().contains(lowerKeyword) ||
                ghe.getSoGhe().toLowerCase().contains(lowerKeyword)) {
                
                ketQua.add(ghe);
            }
        }
        return ketQua;
    }
}