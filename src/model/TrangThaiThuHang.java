package model;   // hoặc package enum của bạn

public enum TrangThaiThuHang {
    HOAT_DONG("HOAT_DONG", "Hoạt động"),
    DA_XOA("DA_XOA", "Đã xóa mềm");

    private final String giaTri;        // giá trị lưu trong database
    private final String tenHienThi;    // tên hiển thị trên giao diện

    TrangThaiThuHang(String giaTri, String tenHienThi) {
        this.giaTri = giaTri;
        this.tenHienThi = tenHienThi;
    }

    public String getGiaTri() {
        return giaTri;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    // Phương thức siêu tiện: chuyển từ String trong DB sang Enum
    public static TrangThaiThuHang fromGiaTri(String giaTri) {
        for (TrangThaiThuHang tt : values()) {
            if (tt.giaTri.equals(giaTri)) {
                return tt;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + giaTri);
    }

    @Override
    public String toString() {
        return tenHienThi;   // Khi đưa vào JComboBox, JTable sẽ hiển thị "Hoạt động"
    }
}