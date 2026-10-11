# 📊 HỆ THỐNG QUẢN LÝ PHÒNG TẬP FITLIFE GYM
## BỘ SƠ ĐỒ THIẾT KẾ KIẾN TRÚC & USE CASE LÕI CHECK-IN (PHỤC VỤ BÁO CÁO & SLIDE BẢO VỆ)

> **Tài liệu học thuật & kỹ thuật chuyên sâu** dành cho buổi bảo vệ đồ án trước Hội đồng chấm thi.
> Toàn bộ sơ đồ được thiết kế bằng chuẩn **Mermaid Diagram** trực quan, có thể chèn trực tiếp vào Slide PowerPoint/Canva hoặc xuất ảnh chất lượng cao.

---

## 1. SƠ ĐỒ THỰC THỂ QUAN HỆ CƠ SỞ DỮ LIỆU (ERD - ENTITY RELATIONSHIP DIAGRAM)

Sơ đồ mô tả cấu trúc 8 bảng chuẩn hóa của CSDL `FitLifeGym`, giải quyết triệt để vấn đề kiểu dữ liệu tiền tệ `DECIMAL(18, 2)` và mật khẩu băm bảo mật `VARCHAR(255)`:

```mermaid
erDiagram
    TaiKhoan {
        VARCHAR(50) TenDangNhap PK "Tên đăng nhập hệ thống"
        VARCHAR(255) MatKhau "Mật khẩu mã hóa BCrypt / SHA-256"
        NVARCHAR(50) VaiTro "Chủ / Lễ tân / HLV / Quản lý / Hội viên"
        NVARCHAR(100) HoTen "Họ và tên người dùng"
    }

    HoiVien {
        INT MaHoiVien PK "Khóa chính tự tăng (Identity)"
        VARCHAR(50) MaTheQR UK "Mã thẻ quét QR / Barcode duy nhất"
        NVARCHAR(100) HoTen "Họ tên hội viên"
        NVARCHAR(100) ChiNhanhDangKy "CN01 - Q1 / CN02 - Tân Bình / VIP"
        DATE NgayHetHan "Ngày hết hạn thẻ tập"
        NVARCHAR(30) TrangThai "Active / Tạm khóa / Hết hạn"
    }

    GoiTap {
        VARCHAR(30) MaGoiTap PK "Mã gói tập (GT01, GT02...)"
        NVARCHAR(100) TenGoiTap "Tên gói tập"
        INT ThoiHanThang "Thời hạn hiệu lực (tháng)"
        DECIMAL(18_2) GiaTien "Giá niêm yết (Chuẩn kế toán)"
        NVARCHAR(100) PhamVi "Toàn hệ thống / Chi nhánh"
        NVARCHAR(255) MoTa "Quyền lợi gói tập"
        NVARCHAR(50) TrangThai "Đang mở / Tạm ngưng"
    }

    HuanLuyenVien {
        VARCHAR(30) MaHLV PK "Mã huấn luyện viên"
        NVARCHAR(100) HoTen "Họ tên HLV"
        VARCHAR(20) SoDienThoai "Số điện thoại liên lạc"
        NVARCHAR(100) ChuyenMon "Bodybuilding, Yoga, Boxing, Pilates"
        INT KinhNghiemNam "Số năm kinh nghiệm"
        NVARCHAR(50) ChiNhanh "Chi nhánh phụ trách"
        NVARCHAR(50) CaLamViec "Ca sáng / Ca chiều"
        FLOAT DanhGiaSao "Đánh giá xếp hạng (Sao)"
        NVARCHAR(30) TrangThai "Đang làm việc"
    }

    LopHoc {
        VARCHAR(30) MaLop PK "Mã lớp nhóm (LH01, LH02...)"
        NVARCHAR(100) TenLop "Tên lớp học"
        NVARCHAR(50) BoMon "Yoga, Zumba, Kickboxing, Pilates"
        NVARCHAR(100) HuanLuyenVien "HLV giảng dạy"
        NVARCHAR(100) ThoiGian "Khung giờ và thứ trong tuần"
        NVARCHAR(100) PhongTap "Studio 1, Studio 2, Ring A"
        INT SiSoToiDa "Sĩ số giới hạn"
        INT SiSoHienTai "Số học viên đã đăng ký"
    }

    DangKyLop {
        INT Id PK "Khóa chính tự tăng"
        VARCHAR(30) MaLop FK "Liên kết lớp học"
        INT MaHoiVien FK "Liên kết hội viên"
        NVARCHAR(100) TenHoiVien "Tên học viên"
        VARCHAR(20) SoDienThoai "SĐT học viên"
        DATETIME NgayDangKy "Thời điểm ghi danh"
        NVARCHAR(50) TrangThaiDiemDanh "Đã điểm danh / Chưa điểm danh / Vắng"
        NVARCHAR(255) GhiChu "Ghi chú đặc biệt"
    }

    LichSuCheckIn {
        INT Id PK "Audit Log ID tự tăng"
        INT MaHoiVien "ID hội viên quét thẻ"
        NVARCHAR(100) TenHoiVien "Họ tên hội viên"
        VARCHAR(50) MaTheQR "Mã thẻ quét thực tế"
        DATETIME ThoiGian "Thời điểm quẹt thẻ chính xác"
        NVARCHAR(50) ChiNhanh "Trạm kiểm soát quẹt thẻ"
        NVARCHAR(50) CuaRaVao "Cổng Flap Barrier A1/B1/VIP Gate"
        NVARCHAR(30) PhuongThuc "Mã QR App / Mã Barcode Thẻ"
        NVARCHAR(30) TrangThai "Hợp lệ / Từ chối"
        NVARCHAR(255) GhiChu "Lý do từ chối hoặc xác nhận"
        BIGINT ThoiGianPhanHoiMs "Độ trễ xử lý (Latency < 50ms)"
    }

    HoaDon {
        VARCHAR(30) MaHoaDon PK "Mã giao dịch tài chính"
        INT MaHoiVien "ID khách hàng thanh toán"
        NVARCHAR(100) TenHoiVien "Tên khách hàng"
        NVARCHAR(50) LoaiGiaoDich "Đăng ký gói / Gia hạn / POS / Locker"
        DECIMAL(18_2) TongTien "Tổng tiền trước giảm giá"
        DECIMAL(18_2) GiamGia "Chiết khấu khuyến mãi"
        DECIMAL(18_2) ThanhTien "Thực thu chuẩn DECIMAL"
        NVARCHAR(50) HinhThucThanhToan "Tiền mặt / VietQR Chuyển khoản"
        VARCHAR(50) NguoiLap "Nhân viên lập phiếu"
        NVARCHAR(50) ChiNhanh "Chi nhánh phát sinh"
        DATETIME NgayLap "Ngày lập hóa đơn"
        NVARCHAR(30) TrangThai "Đã thanh toán"
        NVARCHAR(255) GhiChu "Chi tiết giao dịch"
    }

    HoiVien ||--o{ DangKyLop : "đăng ký & điểm danh"
    LopHoc ||--o{ DangKyLop : "chứa danh sách học viên"
    HoiVien ||--o{ LichSuCheckIn : "ghi nhận nhật ký quét thẻ"
    HoiVien ||--o{ HoaDon : "phát sinh giao dịch"
    GoiTap ||--o{ HoaDon : "sản phẩm giao dịch"
    HuanLuyenVien ||--o{ LopHoc : "phụ trách giảng dạy"
```

---

## 2. SƠ ĐỒ USE CASE TỔNG THỂ HỆ THỐNG (SYSTEM USE CASE DIAGRAM)

Mô tả phân quyền Role-Based Access Control (RBAC) 5 cấp bậc cùng tương tác phần cứng:

```mermaid
graph TD
    subgraph Actors [Tác nhân người dùng & Thiết bị]
        Member((Hội viên VIP))
        Receptionist((Lễ tân))
        Trainer((Huấn luyện viên))
        Manager((Quản lý chi nhánh))
        Owner((Chủ hệ thống))
        FlapBarrier[[Cổng Flap Barrier & Máy Quét]]
    end

    subgraph System [Hệ Thống Quản Lý FitLife Gym]
        UC_Login(Đăng nhập an toàn BCrypt / Offline Fallback)
        UC_CheckIn(Quét thẻ QR & Tự động mở cổng Check-in)
        UC_RegisterMember(Đăng ký & Cấp thẻ hội viên mới)
        UC_RenewPackage(Gia hạn & Nâng cấp gói tập)
        UC_POS(Bán hàng POS nước uống / phụ kiện)
        UC_Locker(Quản lý 20 tủ đồ Locker thông minh)
        UC_AttendanceClass(Đăng ký & Điểm danh lớp học)
        UC_InBody(Theo dõi chỉ số InBody & Tiến độ PT)
        UC_RevenueReport(Báo cáo doanh thu & Dòng tiền chi nhánh)
        UC_Heatmap(Phân tích Heatmap mật độ giờ cao điểm)
        UC_EmployeeMgmt(Quản lý tài khoản & Phân quyền nhân viên)
    end

    %% Hội viên
    Member --> UC_Login
    Member --> UC_CheckIn
    Member --> UC_AttendanceClass

    %% Cổng phần cứng
    FlapBarrier -.->|Tín hiệu quét mã QR/Barcode| UC_CheckIn

    %% Lễ tân
    Receptionist --> UC_Login
    Receptionist --> UC_CheckIn
    Receptionist --> UC_RegisterMember
    Receptionist --> UC_RenewPackage
    Receptionist --> UC_POS
    Receptionist --> UC_Locker

    %% Huấn luyện viên
    Trainer --> UC_Login
    Trainer --> UC_AttendanceClass
    Trainer --> UC_InBody

    %% Quản lý
    Manager --> UC_Login
    Manager --> UC_CheckIn
    Manager --> UC_RegisterMember
    Manager --> UC_RevenueReport
    Manager --> UC_Heatmap

    %% Chủ hệ thống (Full Access)
    Owner --> UC_Login
    Owner --> UC_RevenueReport
    Owner --> UC_Heatmap
    Owner --> UC_EmployeeMgmt
    Owner -.->|Bao gồm quyền| Manager
```

---

## 3. SƠ ĐỒ TUẦN TỰ CHO USE CASE LÕI "QUÉT THẺ & CHECK-IN" (SEQUENCE DIAGRAM)

Use case trung tâm của đồ án — thực thi **5 Quy Tắc Nghiệp Vụ Kinh Doanh (5 Core Business Rules)** với độ trễ phản hồi cực thấp (< 50ms):

```mermaid
sequenceDiagram
    autonumber
    actor HV as Hội Viên / Lễ Tân
    participant HW as Máy Quét QR / Cổng Barrier
    participant UI as Giao Diện CheckInForm
    participant Svc as CheckInService
    participant HvDAO as HoiVienDAO
    participant LogDAO as LichSuCheckInDAO
    participant DB as CSDL SQL Server (FitLifeGym)
    participant Sound as SoundHelper (Âm Thanh)

    HV->>HW: Đưa thẻ Barcode / Mã QR App vào camera quét
    HW->>UI: Truyền chuỗi mã thẻ (VD: "QR001" hoặc "VIP007")
    UI->>Svc: thucHienCheckIn(maQR, currentBranch)
    Note over Svc: Bắt đầu đo thời gian phản hồi (Latency Timer)

    %% Bước 1: Tra cứu hồ sơ
    Svc->>HvDAO: timKiemThongMinh(maQR)
    alt CSDL Online
        HvDAO->>DB: SELECT * FROM HoiVien WHERE MaTheQR = :code
        DB-->>HvDAO: Trả về Entity HoiVien
    else CSDL Offline (Mất mạng)
        HvDAO-->>Svc: Lấy từ Memory Fallback Cache
    end
    HvDAO-->>Svc: Trả về đối tượng HoiVien (hoặc null)

    %% Bước 2: Kiểm tra 5 Business Rules
    alt Rule 1: Thẻ không tồn tại (hv == null)
        Svc->>LogDAO: luuCheckIn(status: 'Từ chối', note: 'Không tồn tại')
        Svc-->>UI: CheckInResult(success: false, gateStatus: 'LOCKED')
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Hiển thị cảnh báo đỏ: "MÃ THẺ KHÔNG TỒN TẠI"
    else Rule 2: Thẻ đang bị Tạm Khóa (trangThai == 'Tạm khóa')
        Svc->>LogDAO: luuCheckIn(status: 'Từ chối', note: 'Thẻ bị khóa')
        Svc-->>UI: CheckInResult(success: false, gateStatus: 'LOCKED')
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Hiển thị cảnh báo đỏ: "THẺ ĐANG BỊ TẠM KHÓA"
    else Rule 3: Gói tập Hết Hạn (ngayHetHan < Ngày hiện tại)
        Svc->>LogDAO: luuCheckIn(status: 'Từ chối', note: 'Thẻ hết hạn')
        Svc-->>UI: CheckInResult(success: false, gateStatus: 'LOCKED', canRenew: true)
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Hiển thị cảnh báo: "GÓI TẬP HẾT HẠN - GỢI Ý GIA HẠN"
    else Rule 4: Sai Chi Nhánh (Thẻ đơn cơ sở != Chi nhánh hiện tại)
        Svc->>LogDAO: luuCheckIn(status: 'Từ chối', note: 'Sai cơ sở đăng ký')
        Svc-->>UI: CheckInResult(success: false, gateStatus: 'LOCKED')
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Hiển thị cảnh báo: "SAI CHI NHÁNH ĐĂNG KÝ"
    else Rule 5: Chống quét trùng lặp (< 2 phút từ lần quét trước)
        Svc->>LogDAO: luuCheckIn(status: 'Từ chối', note: 'Quét trùng lặp')
        Svc-->>UI: CheckInResult(success: false, gateStatus: 'LOCKED')
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Cảnh báo: "THẺ VỪA QUÉT TRƯỚC ĐÓ - VUI LÒNG KHÔNG QUÉT TRÙNG"
    else HỢP LỆ TOÀN BỘ (ĐẠT 5 QUY TẮC)
        Svc->>LogDAO: luuCheckIn(status: 'Hợp lệ', gate: 'Flap Barrier A1', latency: <50ms)
        LogDAO->>DB: INSERT INTO LichSuCheckIn (...)
        Svc-->>UI: CheckInResult(success: true, gateStatus: 'OPEN', memberName, isVip)
        UI->>Sound: phatAmThanhThanhCong()
        UI->>HW: Gửi tín hiệu mở cổng tự động (Flap Barrier OPEN)
        UI->>HV: Hiển thị bảng chào mừng xanh: "CHECK-IN THÀNH CÔNG - MỜI VÀO"
    end
```

---

## 4. HƯỚNG DẪN THUYẾT TRÌNH BẢO VỆ ĐỒ ÁN (TỔNG KẾT ĐIỂM CỘNG A+)

Khi bảo vệ trước Hội đồng chấm thi, sinh viên hãy trình bày theo 4 luận điểm kỹ thuật then chốt sau:

1. **Bảo Mật Chuẩn Doanh Nghiệp (Enterprise Security):**
   - Mật khẩu băm bằng **BCrypt (Work factor = 12)** kết hợp cơ chế kiểm soát salt động, thay thế toàn bộ chuỗi plaintext.
   - Thông tin kết nối CSDL được tách biệt hoàn toàn vào [`database.properties`](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/resources/database.properties) và được bảo vệ qua `.gitignore`.

2. **Cơ Chế Khả Dụng Cao (High Availability & Offline Buffer):**
   - Hệ thống không bị treo hay văng lỗi khi máy chủ CSDL mất kết nối nhờ kiến trúc **Lazy Singleton Null-Safe** trong `HibernateUtil`.
   - Khi chạy ngoại tuyến, giao diện hiển thị **Banner Cảnh Báo Đỏ Rõ Ràng**, biến lỗi mạng thành điểm cộng UX/Kiến trúc dự phòng.

3. **Toàn Vẹn Dữ Liệu & Chuẩn Kế Toán (Financial Accuracy):**
   - Sử dụng kiểu dữ liệu `DECIMAL(18, 2)` chống hiện tượng sai số dấu phẩy động (Floating point rounding error) trong phân hệ Bán hàng POS và Hóa đơn.
   - Sĩ số lớp học và điểm danh được đồng bộ qua bảng `DangKyLop` với quan hệ toàn vẹn khóa ngoại FK.

4. **Kiểm Thử Tự Động (Automated Testing with JUnit 5):**
   - Dự án đã tích hợp sẵn **41 Test Cases tự động** vượt qua 100% kiểm thử, bao phủ toàn bộ 5 Business Rules của quy trình Check-in và các biểu thức chuẩn hóa dữ liệu đầu vào.
