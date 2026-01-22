
-- =============================================
-- 1. NHÓM NGƯỜI DÙNG
-- =============================================
CREATE TABLE NguoiDung (
    maNguoiDung INT PRIMARY KEY IDENTITY(1,1), 
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    sdt VARCHAR(15),
    ngayTao DATETIME DEFAULT GETDATE(),
    phanQuyen VARCHAR(20),     
    trangThaiTk VARCHAR(20)    
);

CREATE TABLE ThuHang (
    maThuHang VARCHAR(10) PRIMARY KEY,
    tenThuHang NVARCHAR(50),
    diem INT,
    uuDai NVARCHAR(MAX)
);

CREATE TABLE ThongTinHanhKhach (
    maHk VARCHAR(20) PRIMARY KEY,
    maNguoiDung INT REFERENCES NguoiDung(maNguoiDung),
    maThuHang VARCHAR(10) REFERENCES ThuHang(maThuHang),
    hoTen NVARCHAR(100),
    cccd VARCHAR(20),
    hoChieu VARCHAR(20),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10),
    diemTichLuy INT DEFAULT 0
);

CREATE TABLE NhanVien (
    maNv VARCHAR(20) PRIMARY KEY,
    maNguoiDung INT REFERENCES NguoiDung(maNguoiDung),
    chucVu NVARCHAR(50),
    ngayVaoLam DATE
);

-- =============================================
-- 2. NHÓM TUYẾN BAY
-- =============================================
CREATE TABLE SanBay (
    maSanBay VARCHAR(10) PRIMARY KEY,
    tenSanBay NVARCHAR(100),
    quocGia NVARCHAR(50),
    thanhPho NVARCHAR(50)
);

CREATE TABLE TuyenBay (
    maTuyenBay INT PRIMARY KEY IDENTITY(1,1),
    sanBayDi VARCHAR(10) REFERENCES SanBay(maSanBay),
    sanBayDen VARCHAR(10) REFERENCES SanBay(maSanBay),
    khoangCachKm DECIMAL(10,2),
    giaGoc DECIMAL(18,2)
);

CREATE TABLE MayBay (
    maMayBay INT PRIMARY KEY IDENTITY(1,1),
    soHieu VARCHAR(20),
    hangSanXuat NVARCHAR(50),
    tongSoGhe INT
);

CREATE TABLE HangVe (
    maHangVe VARCHAR(10) PRIMARY KEY,
    tenHang NVARCHAR(50),
    heSoHangVe DECIMAL(5,2)
);

CREATE TABLE HeSoGia (
    maHeSoGia VARCHAR(20) PRIMARY KEY,
    heSo DECIMAL(5,2),
    soGioDatTruoc DECIMAL(10,2)
);

-- =============================================
-- 3. NHÓM CHUYẾN BAY
-- =============================================
CREATE TABLE ChuyenBay (
    maChuyenBay INT PRIMARY KEY IDENTITY(1,1),
    maTuyenBay INT REFERENCES TuyenBay(maTuyenBay),
    maMayBay INT REFERENCES MayBay(maMayBay),
    maHeSoGia VARCHAR(20) REFERENCES HeSoGia(maHeSoGia),
    ngayGioDi DATETIME,
    ngayGioDen DATETIME,
    trangThai NVARCHAR(50)
);

CREATE TABLE GheMayBay (
    maGhe INT PRIMARY KEY IDENTITY(1,1),
    maMayBay INT REFERENCES MayBay(maMayBay),
    soGhe VARCHAR(10),
    giaGhe DECIMAL(18,2)
);

-- =============================================
-- 4. NHÓM NGHIỆP VỤ BÁN VÉ
-- =============================================
CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(20) PRIMARY KEY,
    tenKm NVARCHAR(100),
    ngayBd DATETIME,
    ngayKt DATETIME,
    giaTri DECIMAL(18,2),
    loaiKm VARCHAR(20)
);

CREATE TABLE PhieuDatVe (
    maPhieuDatVe VARCHAR(20) PRIMARY KEY,
    maNguoiDung INT REFERENCES NguoiDung(maNguoiDung),
    maNv VARCHAR(20) REFERENCES NhanVien(maNv),
    thoiLuong INT,
    ngayDat DATETIME DEFAULT GETDATE(),
    soLuongVe INT,
    tongTien DECIMAL(18,2),
    trangThaiThanhToan NVARCHAR(50),
    maKhuyenMai VARCHAR(20) REFERENCES KhuyenMai(maKhuyenMai)
);

CREATE TABLE VeBan (
    maVe VARCHAR(20) PRIMARY KEY,
    maPhieuDatVe VARCHAR(20) REFERENCES PhieuDatVe(maPhieuDatVe),
    maChuyenBay INT REFERENCES ChuyenBay(maChuyenBay),
    maHk VARCHAR(20) REFERENCES ThongTinHanhKhach(maHk),
    maHangVe VARCHAR(10) REFERENCES HangVe(maHangVe),
    maGhe INT REFERENCES GheMayBay(maGhe),
    loaiVe NVARCHAR(50),
    loaiHk NVARCHAR(50),
    giaVe DECIMAL(18,2),
    trangThaiVe NVARCHAR(50)
);

CREATE TABLE DichVuBoSung (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(100),
    donGia DECIMAL(18,2)
);

CREATE TABLE ChiTietDichVu (
    maVe VARCHAR(20) REFERENCES VeBan(maVe),
    maDichVu VARCHAR(20) REFERENCES DichVuBoSung(maDichVu),
    soLuong INT,
    thanhTien DECIMAL(18,2),
    PRIMARY KEY (maVe, maDichVu)
);

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    maPhieuDatVe VARCHAR(20) REFERENCES PhieuDatVe(maPhieuDatVe),
    maNv VARCHAR(20) REFERENCES NhanVien(maNv),
    ngayLap DATETIME DEFAULT GETDATE(),
    tongTien DECIMAL(18,2),
    phuongThuc NVARCHAR(50),
    donViTienTe VARCHAR(10),
    thue DECIMAL(18,2)
);

CREATE TABLE CTHoaDon (
    maCtHd INT PRIMARY KEY IDENTITY(1,1),
    maHoaDon VARCHAR(20) REFERENCES HoaDon(maHoaDon),
    maVe VARCHAR(20) REFERENCES VeBan(maVe),
    soTien DECIMAL(18,2),
    maNguoiDung INT REFERENCES NguoiDung(maNguoiDung)
);

CREATE TABLE HoanVe (
    maHoanVe VARCHAR(20) PRIMARY KEY,
    maVe VARCHAR(20) REFERENCES VeBan(maVe),
    nguoiPhuTrach VARCHAR(20) REFERENCES NhanVien(maNv),
    trangThai NVARCHAR(50),
    ngayYeuCau DATETIME,
    ngayXuLy DATETIME,
    lyDoHoan NVARCHAR(255)
);

CREATE TABLE HanhLy (
    maHanhLy VARCHAR(20) PRIMARY KEY,
    maVe VARCHAR(20) REFERENCES VeBan(maVe),
    soKg DECIMAL(5,2),
    kichThuoc VARCHAR(50),
    giaTien DECIMAL(18,2),
    trangThai NVARCHAR(50),
    ghiChu NVARCHAR(255)
);

CREATE TABLE LichSuDoiVe (
    maLichSu INT PRIMARY KEY IDENTITY(1,1),
    maVe VARCHAR(20) REFERENCES VeBan(maVe),
    maChuyenBayCu INT REFERENCES ChuyenBay(maChuyenBay),
    maChuyenBayMoi INT REFERENCES ChuyenBay(maChuyenBay),
    maGheCu INT REFERENCES GheMayBay(maGhe),
    maGheMoi INT REFERENCES GheMayBay(maGhe),
    phiChenhLech DECIMAL(18,2),
    phiDoiVe DECIMAL(18,2),
    lyDoDoi NVARCHAR(255),
    ngayThucHien DATETIME DEFAULT GETDATE()
);
