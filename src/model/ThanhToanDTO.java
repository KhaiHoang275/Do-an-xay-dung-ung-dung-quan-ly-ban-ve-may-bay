package model;

import java.math.BigDecimal;
import java.util.List;

public class ThanhToanDTO {
    public String tenKH, sdt, email, tuyenBay, veGhe, gioDi, gioDen;
    
    // Thêm 3 dòng này để sửa lỗi bạn đang gặp
    public BigDecimal giaVeGoc;      
    public BigDecimal tongTienDichVu; 
    public List<String> danhSachDichVu; 
}