package test;
import dal.*;
import model.NguoiDung;
import java.util.ArrayList;

public class TestNguoiDung {
    public static void main(String[] args) {
        NguoiDungDAO dao = new NguoiDungDAO();

        System.out.println("--------- BẮT ĐẦU TEST ---------");

        System.out.println("\n1. Test Insert:");
        NguoiDung newUser = new NguoiDung();
        newUser.setMaNguoiDung("ND001"); 
        newUser.setUsername("admin_test");
        newUser.setPassword("123456");
        newUser.setEmail("admin@gmail.com");
        newUser.setSoDienThoai("0909123456");
        newUser.setPhanQuyen("ADMIN");
        newUser.setTrangThaiTK("ACTIVE");

        if (dao.insert(newUser)) {
            System.out.println("✅ Thêm thành công!");
        } else {
            System.out.println("❌ Thêm thất bại (Có thể trùng mã hoặc lỗi SQL).");
        }

        System.out.println("\n2. Test SelectAll:");
        ArrayList<NguoiDung> list = dao.selectAll();
        if (list.isEmpty()) {
            System.out.println("⚠️ Danh sách trống.");
        } else {
            for (NguoiDung nd : list) {
                System.out.println(" -> Tìm thấy: " + nd.getUsername() + " - " + nd.getEmail());
            }
            System.out.println("✅ Lấy dữ liệu thành công! Tổng số: " + list.size());
        }

        System.out.println("\n3. Test Check Login:");
        NguoiDung loginUser = dao.checkLogin("admin_test", "123456");
        if (loginUser != null) {
            System.out.println("✅ Đăng nhập thành công! Xin chào: " + loginUser.getUsername());
        } else {
            System.out.println("❌ Đăng nhập thất bại.");
        }

        NguoiDung wrongUser = dao.checkLogin("admin_test", "sai_pass");
        if (wrongUser == null) {
            System.out.println("✅ Test nhập sai pass hoạt động đúng (trả về null).");
        }

        System.out.println("\n4. Test Update:");
        newUser.setSoDienThoai("0999999999"); 
        newUser.setEmail("email_moi@gmail.com");
        
        if (dao.update(newUser)) {
            System.out.println("✅ Cập nhật thành công!");
            
        } else {
            System.out.println("❌ Cập nhật thất bại.");
        }

        System.out.println("\n--------- KẾT THÚC TEST ---------");
    }
}