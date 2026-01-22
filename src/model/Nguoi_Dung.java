public class Nguoi_Dung {
    private String ma_nguoi_dung; 
    private String username; 
    private String password; 
    private String email; 
    private String so_dien_thoai; 
    private int ngay_tao; 
    private int thang_tao; 
    private int nam_tao; 
    private String phan_quyen;
    private String trang_thai_tk; 

    public Nguoi_Dung() {
        ma_nguoi_dung = ""; 
        username = ""; 
        password = ""; 
        email = ""; 
        so_dien_thoai = ""; 
        ngay_tao = 1; 
        thang_tao = 1; 
        nam_tao = 1; 
        phan_quyen = ""; 
        trang_thai_tk = ""; 
    }
    public Nguoi_Dung(String ma_nguoi_dung, String username, String password, String email, String so_dien_thoai, int ngay_tao, int thang_tao, int nam_tao, String phan_quyen, String trang_thai_tk) {
        this.ma_nguoi_dung = ma_nguoi_dung; 
        this.username = username; 
        this.password = password; 
        this.email = email; 
        this.so_dien_thoai = so_dien_thoai; 
        this.ngay_tao = ngay_tao; 
        this.thang_tao = thang_tao; 
        this.nam_tao = nam_tao; 
        this.phan_quyen = phan_quyen; 
        this.trang_thai_tk = trang_thai_tk; 
    } 

    public Nguoi_Dung(Nguoi_Dung nd) {
        this.ma_nguoi_dung = nd.ma_nguoi_dung; 
        this.username = nd.username; 
        this.password = nd.password; 
        this.email = nd.email; 
        this.so_dien_thoai = nd.so_dien_thoai; 
        this.ngay_tao = nd.ngay_tao; 
        this.thang_tao = nd.thang_tao; 
        this.nam_tao = nd.nam_tao; 
        this.phan_quyen = nd.phan_quyen; 
        this.trang_thai_tk = nd.trang_thai_tk; 
    } 

    public void setMaNguoiDung(String ma_nguoi_dung) {
        this.ma_nguoi_dung = ma_nguoi_dung; 
    }
    
    public String getMaNguoiDung() {
        return ma_nguoi_dung; 
    }
    public void setUsername(String username) {
        this.username = username; 
    }
    public String getUsername() {
        return username; 
    }
    public void setPassword(String password) {
        this.password = password; 
    }
    public String getPassword() {
        return password; 
    }
    public void setEmail(String email) {
        this.email = email; 
    }
    public String getEmail() {
        return email; 
    }
    public void setSoDienThoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai; 
    }
    public String getSoDienThoai() {
        return so_dien_thoai; 
    }
    public void setNgayTao(int ngay_tao) {
        this.ngay_tao = ngay_tao; 
    }
    public int getNgayTao() {
        return ngay_tao; 
    }
    public void setThangTao(int thang_tao) {
        this.thang_tao = thang_tao; 
    }
    public int getThangTao() {
        return thang_tao; 
    }
    public void setNamTao(int nam_tao) {
        this.nam_tao = nam_tao; 
    }
    public int getNamTao() {
        return nam_tao; 
    }
    public void setPhanQuyen(String phan_quyen) {
        this.phan_quyen = phan_quyen; 
    }
    public String getPhanQuyen() {
        return phan_quyen; 
    }
    public void setTrangThaiTK(String trang_thai_tk) {
        this.trang_thai_tk = trang_thai_tk; 
    }
    public String getTrangThaiTK() {
        return trang_thai_tk; 
    }

    
}