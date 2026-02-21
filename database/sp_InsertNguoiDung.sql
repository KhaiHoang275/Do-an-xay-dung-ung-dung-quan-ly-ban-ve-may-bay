CREATE PROCEDURE sp_InsertNguoiDung
    @MaNguoiDung VARCHAR(10),
    @Username VARCHAR(50),
    @Password VARCHAR(50),
    @Email VARCHAR(100),
    @SDT VARCHAR(15),
    @PhanQuyen INT,
    @TrangThaiTK BIT 
AS 
BEGIN 
    IF EXISTS (SELECT 1 FROM NguoiDung WHERE maNguoiDung = @MaNguoiDung OR username = @Username)
    BEGIN 
        RETURN -1; 
    END 
    INSERT INTO NguoiDung (maNguoiDung, username, password, email, sdt, phanQuyen, trangThaiTK, ngayTao) 
    VALUES (@MaNguoiDung, @Username, @Password, @Email, @SDT, @PhanQuyen, @TrangThaiTK, GETDATE()); 

    RETURN 1; 
END  

    