import java.util.Scanner; 

public class Nhan_Vien {
    private String ma_nv; 
    private String ma_nguoi_dung; 
    private String ho_ten; 
    private String chuc_vu; 
    private int ngay; 
    private int thang; 
    private int nam; 

    public Nhan_Vien() {
        ma_nv = ""; 
        ma_nguoi_dung = ""; 
        ho_ten = ""; 
        chuc_vu = "";
        ngay = 1; 
        thang = 1;
        nam = 1;   
    } 

    public Nhan_Vien(String ma_nv, String ma_nguoi_dung, String ho_ten, String chuc_vu, int ngay, int thang, int nam) {
        this.ma_nv = ma_nv; 
        this.ma_nguoi_dung = ma_nguoi_dung; 
        this.ho_ten = ho_ten; 
        this.chuc_vu = chuc_vu; 
        this.ngay = ngay; 
        this.thang = thang; 
        this.nam = nam; 
    } 

    public Nhan_Vien(Nhan_Vien nv) {
        this.ma_nv = nv.ma_nv; 
        this.ma_nguoi_dung = nv.ma_nguoi_dung; 
        this.ho_ten = nv.ho_ten; 
        this.chuc_vu = nv.chuc_vu; 
        this.ngay = nv.ngay; 
        this.thang = nv.thang; 
        this.nam = nv.nam; 
    } 

    public void setMaNV(String ma_nv) {
        this.ma_nv = ma_nv; 
    } 
    public String getMaNV() {
        return ma_nv; 
    } 
    public void setMaNguoiDung(String ma_nguoi_dung) {
        this.ma_nguoi_dung = ma_nguoi_dung; 
    } 
    public String getMaNguoiDung() {
        return ma_nguoi_dung; 
    } 
    public void setHoTen(String ho_ten) {
        this.ho_ten = ho_ten; 
    } 
    public String getHoTen() {
        return ho_ten; 
    } 
    public void setChucVu(String chuc_vu) {
        this.chuc_vu = chuc_vu; 
    }
    public String getChucVu() {
        return chuc_vu; 
    } 
    public void setNgay(int ngay) {
        this.ngay = ngay; 
    }
    public int getNgay() {
        return ngay; 
    } 
    public void setThang(int thang) {
        this.thang = thang; 
    } 
    public int getThang() {
        return thang; 
    } 
    public void setNam(int nam) {
        this.nam = nam; 
    }
    public int getNam() {
        return nam; 
    } 


    
}