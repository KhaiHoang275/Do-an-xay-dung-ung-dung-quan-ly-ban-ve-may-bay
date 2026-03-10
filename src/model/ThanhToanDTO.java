package model;

import java.math.BigDecimal;
import java.util.List;

public class ThanhToanDTO {
    public String tenKH;
    public String sdt;
    public String email;
    public String tuyenBay;
    public String veGhe;
    public String gioDi;
    public String gioDen;
    
    public BigDecimal giaVeGoc;      
    public BigDecimal tongTienDichVu; 
    public List<String> danhSachDichVu; 

    public ThanhToanDTO() {
    }
}