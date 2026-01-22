public class San_Bay {
    private String ma_san_bay; 
    private String ten_san_bay; 
    private String quoc_gia; 
    private String thanh_pho; 

    public San_Bay() {
        ma_san_bay = ""; 
        ten_san_bay = ""; 
        quoc_gia = ""; 
        thanh_pho = "";   
    } 

    public San_Bay(String ma_san_bay, String ten_san_bay, String quoc_gia, String thanh_pho) {
        this.ma_san_bay = ma_san_bay; 
        this.ten_san_bay = ten_san_bay; 
        this.quoc_gia = quoc_gia; 
        this.thanh_pho = thanh_pho; 
    } 

    public San_Bay(San_Bay sb) {
        this.ma_san_bay = sb.ma_san_bay; 
        this.ten_san_bay = sb.ten_san_bay; 
        this.quoc_gia = sb.quoc_gia; 
        this.thanh_pho = sb.thanh_pho; 
    }

    public void setMaSanBay(String ma_san_bay) {
        this.ma_san_bay = ma_san_bay; 
    }
    public String getMaSanBay() {
        return ma_san_bay; 
    }
    public void setTenSanBay(String ten_san_bay) {
        this.ten_san_bay = ten_san_bay; 
    }
    public String getTenSanBay() {
        return ten_san_bay; 
    }
    public void setQuocGia(String quoc_gia) {
        this.quoc_gia = quoc_gia; 
    }
    public String getQuocGia() {
        return quoc_gia; 
    }
    public void setThanhPho(String thanh_pho) {
        this.thanh_pho = thanh_pho; 
    }
    public String getThanhPho() {
        return thanh_pho; 
    }

    
}
