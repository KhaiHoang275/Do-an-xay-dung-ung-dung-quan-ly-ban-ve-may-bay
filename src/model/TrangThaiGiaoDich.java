package model;

public enum TrangThaiGiaoDich {
    CHO_XU_LY,    // Chờ xử lý
    DA_DUYET,     // Đã duyệt
    TU_CHOI,      // Từ chối
    DA_THANH_TOAN; // Đã thanh toán

    // Method mới để trả string hiển thị (thân thiện tiếng Việt)
    public String getMoTa() {
        switch (this) {
            case CHO_XU_LY:
                return "Chờ xử lý";
            case DA_DUYET:
                return "Đã duyệt";
            case TU_CHOI:
                return "Từ chối";
            case DA_THANH_TOAN:
                return "Đã thanh toán";
            default:
                return this.name(); // Fallback raw enum
        }
    }
}