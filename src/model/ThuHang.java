package model;
public class ThuHang {
    private String maThuHang;
    private String tenThuHang;
    private int diemToiThieu;
    private double tiLeGiam;

    public ThuHang() {}

    public ThuHang(String maThuHang, String tenThuHang, int diemToiThieu, double tiLeGiam) {
        this.maThuHang = maThuHang;
        this.tenThuHang = tenThuHang;
        this.diemToiThieu = diemToiThieu;
        this.tiLeGiam = tiLeGiam;
    }

    public String getMaThuHang() {
        return maThuHang;
    }

    public void setMaThuHang(String maThuHang) {
        this.maThuHang = maThuHang;
    }

    public String getTenThuHang() {
        return tenThuHang;
    }

    public void setTenThuHang(String tenThuHang) {
        this.tenThuHang = tenThuHang;
    }

    public int getDiemToiThieu() {
        return diemToiThieu;
    }

    public void setDiemToiThieu(int diemToiThieu) {
        this.diemToiThieu = diemToiThieu;
    }

    public double getTiLeGiam() {
        return tiLeGiam;
    }

    public void setTiLeGiam(double tiLeGiam) {
        this.tiLeGiam = tiLeGiam;
    }

    // getter setter

}