package bll;

import java.math.BigDecimal;
import java.util.ArrayList;

import dal.GheMayBayDAO;
import model.GheMayBay;

public class GheMayBayBUS {
    private GheMayBayDAO gheMayBayDAO;

    public GheMayBayBUS() {
        this.gheMayBayDAO = new GheMayBayDAO();
    }

    public ArrayList<GheMayBay> getAllGheMayBay() {
        return gheMayBayDAO.selectAll();
    }

    // Hàm quan trọng để GUI vẽ sơ đồ ghế
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
            throw new IllegalArgumentException("Giá ghế phải lớn hơn hoặc bằng 0!");
        }

        // Logic chống trùng lặp: Kiểm tra xem máy bay này đã có số ghế (VD: "1A") này chưa
        ArrayList<GheMayBay> danhSachGheHienTai = gheMayBayDAO.selectByMayBay(ghe.getMaMayBay());
        for (GheMayBay g : danhSachGheHienTai) {
            if (g.getSoGhe().equalsIgnoreCase(ghe.getSoGhe())) {
                throw new IllegalArgumentException("Số ghế " + ghe.getSoGhe() + " đã tồn tại trên máy bay " + ghe.getMaMayBay() + "!");
            }
        }

        // Kiểm tra xem Mã Ghế (PK) đã tồn tại trong DB chưa
        if (gheMayBayDAO.selectById(ghe.getMaGhe()) != null) {
            throw new IllegalArgumentException("Mã ghế (ID) đã tồn tại trong hệ thống!");
        }

        return gheMayBayDAO.insert(ghe);
    }

    public boolean capNhatGhe(GheMayBay ghe) throws IllegalArgumentException {
        if (ghe.getMaGhe() == null || ghe.getMaGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã ghế không hợp lệ!");
        }
        if (ghe.getSoGhe() == null || ghe.getSoGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống!");
        }
        if (ghe.getGiaGhe() == null || ghe.getGiaGhe().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá ghế phải lớn hơn hoặc bằng 0!");
        }

        // Logic chống trùng lặp (khi sửa số ghế, phải đảm bảo số ghế mới không trùng với các ghế KHÁC trên cùng máy bay)
        ArrayList<GheMayBay> danhSachGheHienTai = gheMayBayDAO.selectByMayBay(ghe.getMaMayBay());
        for (GheMayBay g : danhSachGheHienTai) {
            // Bỏ qua chính cái ghế đang được cập nhật (so sánh qua MaGhe)
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

    // Hàm tìm kiếm phục vụ thanh Search trên giao diện
    public ArrayList<GheMayBay> timKiemGhe(String keyword) {
        ArrayList<GheMayBay> ketQua = new ArrayList<>();
        ArrayList<GheMayBay> tatCaGhe = getAllGheMayBay();

        if (keyword == null || keyword.trim().isEmpty()) {
            return tatCaGhe;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        for (GheMayBay ghe : tatCaGhe) {
            if (ghe.getMaGhe().toLowerCase().contains(lowerKeyword) ||
                ghe.getMaMayBay().toLowerCase().contains(lowerKeyword) ||
                ghe.getSoGhe().toLowerCase().contains(lowerKeyword)) {
                
                ketQua.add(ghe);
            }
        }
        return ketQua;
    }

    // Thêm hàm này vào GheMayBayBUS
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
            String soGhe = tienTo + i; // Tạo số ghế, VD: A1, A2...
            String maGhe = maMayBay + "_" + soGhe; // Tạo mã ghế duy nhất, VD: MB01_A1
            
            GheMayBay ghe = new GheMayBay(maGhe, maMayBay, soGhe, giaGhe);
            
            try {
                themGhe(ghe); 
            } catch (IllegalArgumentException e) {
                // Nếu gặp lỗi trùng (do đã tạo trước đó), có thể bỏ qua hoặc thông báo
                System.out.println("Bỏ qua ghế bị trùng: " + soGhe);
            }
        }
    }
}