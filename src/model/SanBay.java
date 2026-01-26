package model;

public class SanBay {
    private String maSanBay; 
    private String tenSanBay; 
    private String quocGia; 
    private String thanhPho; 

    public SanBay() {
        maSanBay = ""; 
        tenSanBay = ""; 
        quocGia = ""; 
        thanhPho = "";   
    } 

    public SanBay(String maSanBay, String tenSanBay, String quocGia, String thanhPho) {
        this.maSanBay = maSanBay; 
        this.tenSanBay = tenSanBay; 
        this.quocGia = quocGia; 
        this.thanhPho = thanhPho; 
    } 

    public SanBay(SanBay sb) {
        this.maSanBay = sb.maSanBay; 
        this.tenSanBay = sb.tenSanBay; 
        this.quocGia = sb.quocGia; 
        this.thanhPho = sb.thanhPho; 
    }

    public void setMaSanBay(String maSanBay) {
        this.maSanBay = maSanBay; 
    }
    public String getMaSanBay() {
        return maSanBay; 
    }
    public void setTenSanBay(String tenSanBay) {
        this.tenSanBay = tenSanBay; 
    }
    public String getTenSanBay() {
        return tenSanBay; 
    }
    public void setQuocGia(String quocGia) {
        this.quocGia = quocGia; 
    }
    public String getQuocGia() {
        return quocGia; 
    }
    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho; 
    }
    public String getThanhPho() {
        return thanhPho; 
    }

    public String toString(){
        return maSanBay + " " + tenSanBay + " " + quocGia + " " + thanhPho;
    } 
}
