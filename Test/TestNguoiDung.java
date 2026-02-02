
import dal.NguoiDungDAO;
import model.NguoiDung;
import java.util.ArrayList;
import java.time.LocalDate;

public class TestNguoiDung {
    public static void main(String[] args) {
        NguoiDungDAO dao = new NguoiDungDAO();

        System.out.println("--------- BẮT ĐẦU TEST ---------");

        // ==========================================
        // TEST 1: THÊM NGƯỜI DÙNG MỚI (INSERT)
        // ==========================================
        System.out.println("\n1. Test Insert:");
        NguoiDung newUser = new NguoiDung();
        newUser.setMaNguoiDung("ND001"); // Đảm bảo mã này chưa có trong DB
        newUser.setUsername("admin_test");
        newUser.setPassword("123456");
        newUser.setEmail("admin@gmail.com");
        newUser.setSoDienThoai("0909123456");
        newUser.setPhanQuyen("ADMIN");
        newUser.setTrangThaiTK("ACTIVE");
        // NgayTao database tự sinh, không cần set

        if (dao.insert(newUser)) {
            System.out.println("✅ Thêm thành công!");
        } else {
            System.out.println("❌ Thêm thất bại (Có thể trùng mã hoặc lỗi SQL).");
        }

        // ==========================================
        // TEST 2: HIỂN THỊ DANH SÁCH (SELECT ALL)
        // ==========================================
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

        // ==========================================
        // TEST 3: ĐĂNG NHẬP (CHECK LOGIN)
        // ==========================================
        System.out.println("\n3. Test Check Login:");
        // Thử đăng nhập đúng
        NguoiDung loginUser = dao.checkLogin("admin_test", "123456");
        if (loginUser != null) {
            System.out.println("✅ Đăng nhập thành công! Xin chào: " + loginUser.getUsername());
        } else {
            System.out.println("❌ Đăng nhập thất bại.");
        }

        // Thử đăng nhập sai
        NguoiDung wrongUser = dao.checkLogin("admin_test", "sai_pass");
        if (wrongUser == null) {
            System.out.println("✅ Test nhập sai pass hoạt động đúng (trả về null).");
        }

        // ==========================================
        // TEST 4: CẬP NHẬT (UPDATE)
        // ==========================================
        System.out.println("\n4. Test Update:");
        // Sửa số điện thoại của user vừa tạo
        newUser.setSoDienThoai("0999999999"); 
        // Sửa email
        newUser.setEmail("email_moi@gmail.com");
        
        if (dao.update(newUser)) {
            System.out.println("✅ Cập nhật thành công!");
            
            // Kiểm tra lại xem trong list có đổi chưa (Optional)
            // Bạn có thể gọi lại dao.selectAll() để xem sự thay đổi
        } else {
            System.out.println("❌ Cập nhật thất bại.");
        }

        System.out.println("\n--------- KẾT THÚC TEST ---------");
    }
}