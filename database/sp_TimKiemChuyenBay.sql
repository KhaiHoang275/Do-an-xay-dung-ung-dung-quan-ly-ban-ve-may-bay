CREATE PROCEDURE sp_TimKiemChuyenBay
    @SanBayDi VARCHAR(10),   
    @SanBayDen VARCHAR(10), 
    @NgayDi DATE             
AS
BEGIN
    SELECT 
        cb.maChuyenBay,
        mb.soHieu,
        cb.ngayGioDi,
        cb.ngayGioDen,
        tb.giaGoc
    FROM ChuyenBay cb
    JOIN TuyenBay tb ON cb.maTuyenBay = tb.maTuyenBay
    JOIN MayBay mb ON cb.maMayBay = mb.maMayBay
    WHERE tb.sanBayDi = @SanBayDi 
      AND tb.sanBayDen = @SanBayDen
      AND CAST(cb.ngayGioDi AS DATE) = @NgayDi 
      AND cb.trangThai = 'SCHEDULED' 
END