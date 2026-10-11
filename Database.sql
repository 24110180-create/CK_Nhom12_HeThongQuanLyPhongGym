-- ==============================================================================
-- DATABASE: FitLifeGym - HỆ THỐNG QUẢN TRỊ PHÒNG TẬP THỂ HÌNH TOÀN DIỆN
-- ĐỒ ÁN TỐT NGHIỆP / CUỐI KỲ - NHÓM 12
-- ==============================================================================

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'FitLifeGym')
BEGIN
    CREATE DATABASE FitLifeGym;
END
GO

USE FitLifeGym;
GO

-- 1. XÓA BẢNG CŨ THEO THỨ TỰ PHỤ THUỘC ĐỂ TÁI TẠO ĐỒNG BỘ
DROP TABLE IF EXISTS LichSuCheckIn;
DROP TABLE IF EXISTS HoaDon;
DROP TABLE IF EXISTS DangKyLop;
DROP TABLE IF EXISTS LopHoc;
DROP TABLE IF EXISTS HuanLuyenVien;
DROP TABLE IF EXISTS GoiTap;
DROP TABLE IF EXISTS HoiVien;
DROP TABLE IF EXISTS TaiKhoan;
GO

-- 2. BẢNG TÀI KHOẢN HỆ THỐNG (PHÂN QUYỀN 5 VAI TRÒ CHUẨN)
CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(50) NOT NULL,
    VaiTro NVARCHAR(50) NOT NULL, -- 'Chủ', 'Lễ tân', 'HLV', 'Quản lý', 'Hội viên'
    HoTen NVARCHAR(100) NOT NULL
);
GO

INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, HoTen) VALUES 
('admin',    '123456', N'Chủ',      N'Võ Anh Đức (Chủ Hệ Thống)'),
('letan1',   '123456', N'Lễ tân',   N'Nguyễn Thị Linh (Lễ Tân CN01)'),
('hlv1',     '123456', N'HLV',      N'Trần Tuấn Kiệt (HLV Trưởng)'),
('quanly1',  '123456', N'Quản lý',  N'Lê Thanh Sơn (Quản Lý Chi Nhánh)'),
('hoivien1', '123456', N'Hội viên', N'Lê Anh Duy (Hội Viên VIP Diamond)');
GO

-- 3. BẢNG HỘI VIÊN (HỖ TRỢ MÃ QR/BARCODE VÀ QUẢN LÝ GÓI TẬP)
CREATE TABLE HoiVien (
    MaHoiVien INT IDENTITY(1,1) PRIMARY KEY,
    MaTheQR VARCHAR(50) UNIQUE NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    ChiNhanhDangKy NVARCHAR(100) DEFAULT N'CN01 - Quận 1',
    NgayHetHan DATE NOT NULL,
    TrangThai NVARCHAR(30) DEFAULT N'Active' -- 'Active', 'Tạm khóa', 'Hết hạn'
);
GO

INSERT INTO HoiVien (MaTheQR, HoTen, ChiNhanhDangKy, NgayHetHan, TrangThai) VALUES 
('QR001', N'Lê Anh Duy',       N'CN01 - Quận 1',         '2026-12-31', N'Active'),
('QR002', N'Võ Lê Ngọc Hưng',  N'CN02 - Tân Bình',       '2026-10-15', N'Active'),
('QR003', N'Võ Tấn Phát',      N'CN01 - Quận 1',         '2025-01-01', N'Active'), -- Đã hết hạn để test
('QR004', N'Trần Minh Quân',   N'CN01 - Quận 1',         '2026-11-20', N'Active'),
('QR005', N'Hoàng Thùy Linh',  N'CN02 - Tân Bình',       '2026-08-15', N'Active'),
('VIP007',N'Đỗ Minh Trí',      N'Toàn hệ thống (VIP)',    '2027-12-31', N'Active');
GO

-- 4. BẢNG GÓI TẬP (NHIỀU LOẠI: THÁNG / QUÝ / NĂM / PT)
CREATE TABLE GoiTap (
    MaGoiTap VARCHAR(30) PRIMARY KEY,
    TenGoiTap NVARCHAR(100) NOT NULL,
    ThoiHanThang INT DEFAULT 1,
    GiaTien FLOAT NOT NULL,
    PhamVi NVARCHAR(100) DEFAULT N'Toàn hệ thống',
    MoTa NVARCHAR(255),
    TrangThai NVARCHAR(50) DEFAULT N'Đang mở'
);
GO

INSERT INTO GoiTap (MaGoiTap, TenGoiTap, ThoiHanThang, GiaTien, PhamVi, MoTa, TrangThai) VALUES
('GT01', N'Gói 1 Tháng Tiêu Chuẩn', 1, 500000, N'Toàn hệ thống', N'Tập không giới hạn khung giờ, tặng 1 buổi PT', N'Đang mở'),
('GT02', N'Gói 3 Tháng Siêu Tiết Kiệm', 3, 1350000, N'Toàn hệ thống', N'Tặng bình lắc FitLife + Miễn phí đo InBody', N'Đang mở'),
('GT03', N'Gói 6 Tháng Bứt Phá', 6, 2500000, N'Toàn hệ thống', N'Tặng áo tập FitLife + Tủ Locker cá nhân', N'Đang mở'),
('GT04', N'Gói 12 Tháng VIP Diamond', 12, 4500000, N'Toàn hệ thống', N'Full dịch vụ: Bể bơi, Xông hơi, Khăn & nước uống', N'Đang mở'),
('GT05', N'Gói PT Cá Nhân 1:1 (30 Buổi)', 3, 7500000, N'Chi nhánh đăng ký', N'Huấn luyện viên riêng thiết kế giáo án chuyên sâu', N'Đang mở'),
('GT06', N'Gói Sinh Viên Siêu Rẻ', 1, 350000, N'Toàn hệ thống', N'Khung giờ vàng 08:00 - 16:00 cho HSSV có thẻ', N'Đang mở');
GO

-- 5. BẢNG HUẤN LUYỆN VIÊN (HLV & LỊCH LÀM VIỆC)
CREATE TABLE HuanLuyenVien (
    MaHLV VARCHAR(30) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(20),
    ChuyenMon NVARCHAR(100),
    KinhNghiemNam INT DEFAULT 1,
    ChiNhanh NVARCHAR(50) DEFAULT N'CN01 - Quận 1',
    CaLamViec NVARCHAR(50),
    DanhGiaSao FLOAT DEFAULT 5.0,
    TrangThai NVARCHAR(30) DEFAULT N'Đang làm việc'
);
GO

INSERT INTO HuanLuyenVien (MaHLV, HoTen, SoDienThoai, ChuyenMon, KinhNghiemNam, ChiNhanh, CaLamViec, DanhGiaSao, TrangThai) VALUES
('HLV01', N'Trần Tuấn Kiệt', '0912345678', N'Bodybuilding / PT Tăng cơ', 6, N'CN01 - Quận 1', N'Ca chiều (14:00 - 22:00)', 4.9, N'Đang làm việc'),
('HLV02', N'Nguyễn Thu Thảo', '0987654321', N'Yoga Thảo dược / Pilates', 5, N'CN01 - Quận 1', N'Ca sáng (06:00 - 14:00)', 5.0, N'Đang làm việc'),
('HLV03', N'Phan Thanh Sơn', '0903112233', N'Kickboxing & HIIT Cardio', 4, N'CN02 - Tân Bình', N'Ca chiều (14:00 - 22:00)', 4.8, N'Đang làm việc'),
('HLV04', N'Lê Mai Hương', '0934556677', N'Zumba Dance & Aerobic', 3, N'CN02 - Tân Bình', N'Ca sáng (06:00 - 14:00)', 4.9, N'Đang làm việc'),
('HLV05', N'Đặng Minh Trí', '0977889900', N'CrossFit & Phục hồi cơ', 7, N'CN02 - Tân Bình', N'Ca sáng (06:00 - 14:00)', 4.9, N'Đang làm việc');
GO

-- 6. BẢNG LỚP HỌC THEO LỊCH (YOGA, ZUMBA, PILATES, CARDIO)
CREATE TABLE LopHoc (
    MaLop VARCHAR(30) PRIMARY KEY,
    TenLop NVARCHAR(100) NOT NULL,
    BoMon NVARCHAR(50),
    HuanLuyenVien NVARCHAR(100),
    ThoiGian NVARCHAR(100),
    PhongTap NVARCHAR(100),
    SiSoToiDa INT DEFAULT 20,
    SiSoHienTai INT DEFAULT 0
);
GO

INSERT INTO LopHoc (MaLop, TenLop, BoMon, HuanLuyenVien, ThoiGian, PhongTap, SiSoToiDa, SiSoHienTai) VALUES
('LH01', N'Yoga Trị Liệu Cột Sống', N'Yoga', N'Nguyễn Thu Thảo', N'06:30 - 07:30 (T2-T4-T6)', N'Studio 1 (Tầng 2)', 20, 18),
('LH02', N'Zumba Cardio Đốt Mỡ', N'Zumba', N'Lê Mai Hương', N'18:00 - 19:00 (T3-T5-T7)', N'Studio 2 (Tầng 2)', 25, 22),
('LH03', N'Kickboxing Tự Vệ Nâng Cao', N'Boxing', N'Phan Thanh Sơn', N'19:15 - 20:15 (T2-T4-T6)', N'Khu Võ Thuật Ring A', 15, 12),
('LH04', N'HIIT Cardio Kháng Lực', N'Cardio', N'Trần Tuấn Kiệt', N'07:00 - 08:00 (T3-T5)', N'Khu Functional', 15, 10),
('LH05', N'Pilates Dẻo Dai & Thon Gọn', N'Pilates', N'Nguyễn Thu Thảo', N'17:30 - 18:30 (T2-T4-T6)', N'Studio 1 (Tầng 2)', 18, 15);
GO

-- 7. BẢNG ĐĂNG KÝ VÀ ĐIỂM DANH LỚP HỌC
CREATE TABLE DangKyLop (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    MaLop VARCHAR(30) FOREIGN KEY REFERENCES LopHoc(MaLop),
    MaHoiVien INT FOREIGN KEY REFERENCES HoiVien(MaHoiVien),
    TenHoiVien NVARCHAR(100),
    SoDienThoai VARCHAR(20),
    NgayDangKy DATETIME DEFAULT GETDATE(),
    TrangThaiDiemDanh NVARCHAR(50) DEFAULT N'Chưa điểm danh', -- 'Đã điểm danh', 'Chưa điểm danh', 'Vắng mặt'
    GhiChu NVARCHAR(255)
);
GO

INSERT INTO DangKyLop (MaLop, MaHoiVien, TenHoiVien, SoDienThoai, TrangThaiDiemDanh, GhiChu) VALUES
('LH01', 1, N'Lê Anh Duy', '0901234567', N'Đã điểm danh', N'Đi học đúng giờ'),
('LH01', 2, N'Võ Lê Ngọc Hưng', '0902345678', N'Đã điểm danh', N'Mang thảm riêng'),
('LH01', 5, N'Hoàng Thùy Linh', '0905678901', N'Chưa điểm danh', N''),
('LH02', 1, N'Lê Anh Duy', '0901234567', N'Chưa điểm danh', N''),
('LH03', 4, N'Trần Minh Quân', '0904567890', N'Đã điểm danh', N'Đầy đủ găng tập');
GO

-- 8. BẢNG NHẬT KÝ CHECK-IN (AUDIT LOG - CORE USE CASE)
CREATE TABLE LichSuCheckIn (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    MaHoiVien INT,
    TenHoiVien NVARCHAR(100),
    MaTheQR VARCHAR(50),
    ThoiGian DATETIME DEFAULT GETDATE(),
    ChiNhanh NVARCHAR(50),
    CuaRaVao NVARCHAR(50),
    PhuongThuc NVARCHAR(30), -- 'Mã QR App', 'Mã Thẻ Barcode', 'Mở thủ công'
    TrangThai NVARCHAR(30),  -- 'Hợp lệ', 'Từ chối'
    GhiChu NVARCHAR(255),
    ThoiGianPhanHoiMs BIGINT
);
GO

INSERT INTO LichSuCheckIn (MaHoiVien, TenHoiVien, MaTheQR, ThoiGian, ChiNhanh, CuaRaVao, PhuongThuc, TrangThai, GhiChu, ThoiGianPhanHoiMs) VALUES
(1, N'Lê Anh Duy', 'QR001', DATEADD(MINUTE, -180, GETDATE()), N'CN01 - Quận 1', N'Cổng Flap A1', N'Mã QR App', N'Hợp lệ', N'Vào cửa thành công', 18),
(2, N'Võ Lê Ngọc Hưng', 'QR002', DATEADD(MINUTE, -120, GETDATE()), N'CN02 - Tân Bình', N'Cổng Flap B1', N'Mã Barcode Thẻ', N'Hợp lệ', N'Vào cửa thành công', 22),
(3, N'Võ Tấn Phát', 'QR003', DATEADD(MINUTE, -60, GETDATE()), N'CN01 - Quận 1', N'Cổng Flap A2', N'Mã QR App', N'Từ chối', N'Thẻ đã hết hạn 01/01/2025', 25),
(4, N'Trần Minh Quân', 'QR004', DATEADD(MINUTE, -30, GETDATE()), N'CN02 - Tân Bình', N'Cổng Flap B2', N'Mã Barcode Thẻ', N'Từ chối', N'Sai chi nhánh đăng ký (CN01)', 20),
(6, N'Đỗ Minh Trí', 'VIP007', DATEADD(MINUTE, -10, GETDATE()), N'CN01 - Quận 1', N'Cổng VIP Gate', N'Mã QR VIP Pass', N'Hợp lệ', N'VIP President - Miễn phí trọn đời', 16);
GO

-- 9. BẢNG HÓA ĐƠN & GIAO DỊCH TÀI CHÍNH
CREATE TABLE HoaDon (
    MaHoaDon VARCHAR(30) PRIMARY KEY,
    MaHoiVien INT,
    TenHoiVien NVARCHAR(100),
    LoaiGiaoDich NVARCHAR(50), -- 'Đăng ký gói tập', 'Gia hạn gói tập', 'Bán hàng POS', 'Thuê tủ Locker'
    TongTien FLOAT NOT NULL,
    GiamGia FLOAT DEFAULT 0,
    ThanhTien FLOAT NOT NULL,
    HinhThucThanhToan NVARCHAR(50), -- 'Tiền mặt', 'Chuyển khoản VietQR'
    NguoiLap VARCHAR(50),
    ChiNhanh NVARCHAR(50),
    NgayLap DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(30) DEFAULT N'Đã thanh toán',
    GhiChu NVARCHAR(255)
);
GO

INSERT INTO HoaDon (MaHoaDon, MaHoiVien, TenHoiVien, LoaiGiaoDich, TongTien, GiamGia, ThanhTien, HinhThucThanhToan, NguoiLap, ChiNhanh, NgayLap, TrangThai, GhiChu) VALUES
('HD2026-001', 1, N'Lê Anh Duy', N'Đăng ký gói tập VIP Năm', 4500000, 0, 4500000, N'Chuyển khoản VietQR', 'letan1', N'CN01 - Quận 1', DATEADD(DAY, -10, GETDATE()), N'Đã thanh toán', N'Gói VIP Diamond toàn hệ thống'),
('HD2026-002', 2, N'Võ Lê Ngọc Hưng', N'Gia hạn gói 3 Tháng', 1350000, 100000, 1250000, N'Tiền mặt', 'letan1', N'CN02 - Tân Bình', DATEADD(DAY, -5, GETDATE()), N'Đã thanh toán', N'Khuyến mãi voucher 100k'),
('HD2026-003', 4, N'Trần Minh Quân', N'Đăng ký Gói PT 1:1', 7500000, 500000, 7000000, N'Chuyển khoản VietQR', 'letan1', N'CN01 - Quận 1', DATEADD(DAY, -3, GETDATE()), N'Đã thanh toán', N'30 buổi cùng HLV Trần Tuấn Kiệt'),
('HD2026-004', 5, N'Hoàng Thùy Linh', N'Thuê tủ Locker 6 tháng', 600000, 0, 600000, N'Tiền mặt', 'letan1', N'CN02 - Tân Bình', DATEADD(DAY, -2, GETDATE()), N'Đã thanh toán', N'Locker VIP A12'),
('HD2026-005', 6, N'Đỗ Minh Trí', N'Đăng ký gói 6 Tháng', 2500000, 200000, 2300000, N'Chuyển khoản VietQR', 'admin', N'CN02 - Tân Bình', DATEADD(DAY, -1, GETDATE()), N'Đã thanh toán', N'Khách VIP hệ thống');
GO