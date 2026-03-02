CREATE PROCEDURE sp_KiemTraDangNhap
    @Username VARCHAR(50),
    @Password VARCHAR(255)
AS
BEGIN
    -- Trả về thông tin user nếu đúng user/pass và tài khoản đang ACTIVE
    SELECT maNguoiDung, username, phanQuyen, trangThaiTK
    FROM NguoiDung
    WHERE username = @Username 
      AND password = @Password 
      AND trangThaiTK = 'ACTIVE' -- Chỉ cho phép tài khoản đang hoạt động
END