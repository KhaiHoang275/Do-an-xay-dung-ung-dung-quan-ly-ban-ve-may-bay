package model;

public class ThuHang {
    private String maThuHang;
    private String tenThuHang;
    private int diemToiThieu;
    private double tiLeGiam;
    private TrangThaiThuHang trangThai;

    public ThuHang() {}

    // Constructor đầy đủ (5 tham số) - đã dùng ở mọi nơi trong DAO mới
    public ThuHang(String maThuHang, String tenThuHang, int diemToiThieu, double tiLeGiam, TrangThaiThuHang trangThai) {
        this.maThuHang = maThuHang;
        this.tenThuHang = tenThuHang;
        this.diemToiThieu = diemToiThieu;
        this.tiLeGiam = tiLeGiam;
        this.trangThai = trangThai;
    }

    // ==================== GETTER / SETTER ====================
    public String getMaThuHang() { return maThuHang; }
    public void setMaThuHang(String maThuHang) { this.maThuHang = maThuHang; }

    public String getTenThuHang() { return tenThuHang; }
    public void setTenThuHang(String tenThuHang) { this.tenThuHang = tenThuHang; }

    public int getDiemToiThieu() { return diemToiThieu; }
    public void setDiemToiThieu(int diemToiThieu) { this.diemToiThieu = diemToiThieu; }

    public double getTiLeGiam() { return tiLeGiam; }
    public void setTiLeGiam(double tiLeGiam) { this.tiLeGiam = tiLeGiam; }

    public TrangThaiThuHang getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiThuHang trangThai) { this.trangThai = trangThai; }

    // ==================== METHOD MỚI (BẮT BUỘC) ====================
    /**
     * Trả về giá trị String để lưu vào database (HOAT_DONG hoặc DA_XOA)
     * Dùng trong INSERT và các chỗ cần String
     */
    public String getTrangThaiGiaTri() {
        return trangThai != null ? trangThai.getGiaTri() : "HOAT_DONG";
    }

    // getter setter (để IDE không cảnh báo)
}