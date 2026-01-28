-- 1. TẠO DATABASE VÀ SỬ DỤNG
CREATE DATABASE QLAirLine;
GO
USE QLAirLine
GO

-- =======================================================
-- 2. TẠO CÁC BẢNG DANH MỤC & ĐỐI TƯỢNG ĐỘC LẬP (LEVEL 1)
-- =======================================================

CREATE TABLE NguoiDung (
    maNguoiDung VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    sdt VARCHAR(20),
    ngayTao DATETIME DEFAULT GETDATE(),
    phanQuyen NVARCHAR(50), -- Admin, NhanVien, KhachHang
    trangThaiTK NVARCHAR(50)
);

CREATE TABLE ThuHang (
    maThuHang VARCHAR(20) PRIMARY KEY,
    tenThuHang NVARCHAR(50),
    diem INT,
    uuDai NVARCHAR(MAX)
);

CREATE TABLE SanBay (
    maSanBay VARCHAR(10) PRIMARY KEY, -- VD: SGN, HAN
    tenSanBay NVARCHAR(100),
    quocGia NVARCHAR(50),
    thanhPho NVARCHAR(50)
);

CREATE TABLE MayBay (
    maMayBay VARCHAR(20) PRIMARY KEY,
    soHieu VARCHAR(20), -- VD: VN-A321
    hangSanXuat NVARCHAR(50),
    tongSoGhe INT
);

CREATE TABLE HangVe (
    maHangVe VARCHAR(10) PRIMARY KEY, -- VD: ECO, BUS
    tenHang NVARCHAR(50),
    heSoHangVe DECIMAL(5, 2)
);

CREATE TABLE HeSoGia (
    maHeSoGia VARCHAR(50) PRIMARY KEY, 
    heSo DECIMAL(5, 2),
    soGioDatTruoc DECIMAL(18, 2)
);

CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(50) PRIMARY KEY,
    tenKM NVARCHAR(100),
    ngayBD DATETIME,
    ngayKT DATETIME,
    giaTri DECIMAL(18, 2), 
    loaiKM VARCHAR(20) -- 'TIEN', 'PHAN_TRAM'
);

CREATE TABLE DichVuBoSung (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(100),
    donGia DECIMAL(18, 2)
);

-- =======================================================
-- 3. TẠO CÁC BẢNG PHỤ THUỘC CẤP 1 (LEVEL 2)
-- =======================================================

CREATE TABLE ThongTinHanhKhach (
    maHK VARCHAR(20) PRIMARY KEY,
    maNguoiDung VARCHAR(20),
    maThuHang VARCHAR(20),
    hoTen NVARCHAR(100),
    cccd VARCHAR(20),
    hoChieu VARCHAR(20),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10),
    diemTichLuy INT,
    FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung),
    FOREIGN KEY (maThuHang) REFERENCES ThuHang(maThuHang)
);

CREATE TABLE NhanVien (
    maNV VARCHAR(20) PRIMARY KEY,
    maNguoiDung VARCHAR(20),
    chucVu NVARCHAR(50),
    ngayVaoLam DATE,
    FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung)
);

CREATE TABLE TuyenBay (
    maTuyenBay VARCHAR(20) PRIMARY KEY,
    sanBayDi VARCHAR(10),
    sanBayDen VARCHAR(10),
    khoangCachKM DECIMAL(18, 2),
    giaGoc DECIMAL(18, 2),
    FOREIGN KEY (sanBayDi) REFERENCES SanBay(maSanBay),
    FOREIGN KEY (sanBayDen) REFERENCES SanBay(maSanBay)
);

CREATE TABLE GheMayBay (
    maGhe VARCHAR(20) PRIMARY KEY,
    maMayBay VARCHAR(20),
    soGhe VARCHAR(10),
    giaGhe DECIMAL(18, 2),
    FOREIGN KEY (maMayBay) REFERENCES MayBay(maMayBay)
);

-- =======================================================
-- 4. TẠO CÁC BẢNG NGHIỆP VỤ CHÍNH (LEVEL 3)
-- =======================================================

CREATE TABLE ChuyenBay (
    maChuyenBay VARCHAR(20) PRIMARY KEY,
    maTuyenBay VARCHAR(20),
    maMayBay VARCHAR(20),
    maHeSoGia VARCHAR(50),
    ngayGioDi DATETIME,
    ngayGioDen DATETIME,
    trangThai NVARCHAR(50),
    FOREIGN KEY (maTuyenBay) REFERENCES TuyenBay(maTuyenBay),
    FOREIGN KEY (maMayBay) REFERENCES MayBay(maMayBay),
    FOREIGN KEY (maHeSoGia) REFERENCES HeSoGia(maHeSoGia)
);

CREATE TABLE PhieuDatVe (
    maPhieuDatVe VARCHAR(20) PRIMARY KEY,
    maNguoiDung VARCHAR(20),
    maNV VARCHAR(20), -- Có thể NULL nếu đặt online
    thoiLuong INT,
    ngayDat DATETIME,
    soLuongVe INT,
    tongTien DECIMAL(18, 2),
    trangThaiThanhToan NVARCHAR(50),
    maKhuyenMai VARCHAR(50),
    FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung),
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
);

-- =======================================================
-- 5. TẠO CÁC BẢNG CHI TIẾT & GIAO DỊCH (LEVEL 4)
-- =======================================================

CREATE TABLE VeBan (
    maVe VARCHAR(20) PRIMARY KEY,
    maPhieuDatVe VARCHAR(20),
    maChuyenBay VARCHAR(20),
    maHK VARCHAR(20),
    maHangVe VARCHAR(10),
    maGhe VARCHAR(20),
    loaiVe NVARCHAR(50),
    loaiHK NVARCHAR(50),
    giaVe DECIMAL(18, 2),
    trangThaiVe NVARCHAR(50),
    FOREIGN KEY (maPhieuDatVe) REFERENCES PhieuDatVe(maPhieuDatVe),
    FOREIGN KEY (maChuyenBay) REFERENCES ChuyenBay(maChuyenBay),
    FOREIGN KEY (maHK) REFERENCES ThongTinHanhKhach(maHK),
    FOREIGN KEY (maHangVe) REFERENCES HangVe(maHangVe),
    FOREIGN KEY (maGhe) REFERENCES GheMayBay(maGhe)
);

CREATE TABLE ChiTietDichVu (
    maVe VARCHAR(20),
    maDichVu VARCHAR(20),
    soLuong INT,
    thanhTien DECIMAL(18, 2),
    PRIMARY KEY (maVe, maDichVu),
    FOREIGN KEY (maVe) REFERENCES VeBan(maVe),
    FOREIGN KEY (maDichVu) REFERENCES DichVuBoSung(maDichVu)
);

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    maPhieuDatVe VARCHAR(20),
    maNV VARCHAR(20),
    ngayLap DATETIME,
    tongTien DECIMAL(18, 2),
    phuongThuc NVARCHAR(50),
    donViTienTe VARCHAR(10),
    thue DECIMAL(18, 2),
    FOREIGN KEY (maPhieuDatVe) REFERENCES PhieuDatVe(maPhieuDatVe),
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);

CREATE TABLE CTHoaDon (
    maCTHD VARCHAR(20) PRIMARY KEY,
    maHoaDon VARCHAR(20),
    maVe VARCHAR(20),
    soTien DECIMAL(18, 2),
    maNguoiDung VARCHAR(20),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maVe) REFERENCES VeBan(maVe),
    FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung)
);

CREATE TABLE HoanVe (
    maHoanVe VARCHAR(20) PRIMARY KEY,
    maVe VARCHAR(20),
    nguoiPhuTrach VARCHAR(20),
    trangThai NVARCHAR(50),
    ngayYeuCau DATETIME,
    ngayXuLy DATETIME,
    lyDoHoan NVARCHAR(255),
    FOREIGN KEY (maVe) REFERENCES VeBan(maVe),
    FOREIGN KEY (nguoiPhuTrach) REFERENCES NhanVien(maNV)
);

CREATE TABLE HanhLy (
    maHanhLy VARCHAR(20) PRIMARY KEY,
    maVe VARCHAR(20),
    soKg DECIMAL(5, 2),
    kichThuoc VARCHAR(50),
    giaTien DECIMAL(18, 2),
    trangThai NVARCHAR(50),
    ghiChu NVARCHAR(255),
    FOREIGN KEY (maVe) REFERENCES VeBan(maVe)
);

CREATE TABLE LichSuDoiVe (
    maLichSu VARCHAR(20) PRIMARY KEY,
    maVe VARCHAR(20),
    maChuyenBayCu VARCHAR(20),
    maChuyenBayMoi VARCHAR(20),
    maGheCu VARCHAR(20),
    maGheMoi VARCHAR(20),
    phiChenhLech DECIMAL(18, 2),
    phiDoiVe DECIMAL(18, 2),
    lyDoDoi NVARCHAR(100),
    ngayThucHien DATETIME,
    FOREIGN KEY (maVe) REFERENCES VeBan(maVe),
    FOREIGN KEY (maChuyenBayCu) REFERENCES ChuyenBay(maChuyenBay),
    FOREIGN KEY (maChuyenBayMoi) REFERENCES ChuyenBay(maChuyenBay),
    FOREIGN KEY (maGheCu) REFERENCES GheMayBay(maGhe),
    FOREIGN KEY (maGheMoi) REFERENCES GheMayBay(maGhe)
);