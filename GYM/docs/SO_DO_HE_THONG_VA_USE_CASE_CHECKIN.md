# 📊 HỆ THỐNG QUẢN TRỊ CHUỖI PHÒNG TẬP FITLIFE GYM (MULTI-BRANCH CHAIN)
## BỘ SƠ ĐỒ THIẾT KẾ KIẾN TRÚC & USE CASE LÕI CHECK-IN (PHỤC VỤ BÁO CÁO & SLIDE BẢO VỆ)

> **Tài liệu học thuật & kỹ thuật chuyên sâu** dành cho buổi bảo vệ đồ án trước Hội đồng chấm thi.
> Thiết kế bài toán vận hành **Chuỗi Phòng Tập Thể Hình Đa Chi Nhánh (Multi-Branch Gym Chain Architecture)**, giải quyết triệt để bài toán phân quyền cơ sở, roaming thẻ tập liên chi nhánh và đối soát doanh thu.
> Toàn bộ sơ đồ được thiết kế bằng chuẩn **Mermaid Diagram** trực quan, tương thích 100% với trình xem Markdown của VS Code, GitHub, Notion và Canva.

---

## 1. SƠ ĐỒ THỰC THỂ QUAN HỆ CƠ SỞ DỮ LIỆU (ERD - ENTITY RELATIONSHIP DIAGRAM)

Trong mô hình chuỗi thể hình hiện đại, **`ChiNhanh` (Branch)** đóng vai trò là **Thực Thể Trung Tâm Điều Phối (Core Anchor Entity)**. Mọi hoạt động nghiệp vụ (Hội viên ghi danh, Nhân sự làm việc, Lịch lớp học, Thiết bị phòng tập, Giao dịch tài chính và Lượt quét cổng Check-in) đều được chuẩn hóa liên kết chặt chẽ qua khóa ngoại:

```mermaid
erDiagram
    ChiNhanh {
        string MaChiNhanh PK "Mã cơ sở: CN01, CN02..."
        string TenChiNhanh "Tên cơ sở: FitLife Quận 1 / Tân Bình"
        string DiaChi "Địa chỉ thực tế cơ sở"
        string SoDienThoai "Hotline tiếp nhận khách hàng"
        string QuanLyPhuTrach "Họ tên Quản lý chi nhánh phụ trách"
        int SucChuaToiDa "Giới hạn sức chứa (180 / 120 khách)"
        string TrangThai "Đang hoạt động / Nâng cấp"
    }

    TaiKhoan {
        string TenDangNhap PK "Tên đăng nhập hệ thống"
        string MatKhau "Mật khẩu mã hóa BCrypt (255 ký tự)"
        string VaiTro "Chủ / Lễ tân / HLV / Quản lý / Hội viên"
        string HoTen "Họ và tên người dùng"
        string ChiNhanh FK "Chi nhánh phân công công tác"
    }

    HoiVien {
        int MaHoiVien PK "Khóa chính tự tăng (Identity)"
        string MaTheQR UK "Mã thẻ quét QR hoặc Barcode duy nhất"
        string HoTen "Họ tên hội viên"
        string ChiNhanhDangKy FK "Cơ sở đăng ký ban đầu (CN01/CN02/VIP)"
        date NgayHetHan "Ngày hết hạn thẻ tập"
        string TrangThai "Active / Tạm khóa / Hết hạn"
    }

    GoiTap {
        string MaGoiTap PK "Mã gói tập (GT01, GT02...)"
        string TenGoiTap "Tên gói tập"
        int ThoiHanThang "Thời hạn hiệu lực (tháng)"
        decimal GiaTien "Giá niêm yết (DECIMAL chuẩn kế toán)"
        string PhamVi "Toàn hệ thống (VIP) / Chi nhánh đơn lẻ"
        string MoTa "Quyền lợi gói tập"
        string TrangThai "Đang mở / Tạm ngưng"
    }

    HuanLuyenVien {
        string MaHLV PK "Mã huấn luyện viên"
        string HoTen "Họ tên HLV"
        string SoDienThoai "Số điện thoại liên lạc"
        string ChuyenMon "Bodybuilding, Yoga, Boxing, Pilates"
        int KinhNghiemNam "Số năm kinh nghiệm"
        string ChiNhanh FK "Chi nhánh công tác chính"
        string CaLamViec "Ca sáng / Ca chiều"
        float DanhGiaSao "Đánh giá xếp hạng (Sao)"
        string TrangThai "Đang làm việc"
    }

    LopHoc {
        string MaLop PK "Mã lớp nhóm (LH01, LH02...)"
        string TenLop "Tên lớp học"
        string BoMon "Yoga, Zumba, Kickboxing, Pilates"
        string HuanLuyenVien "HLV phụ trách giảng dạy"
        string ThoiGian "Khung giờ và thứ trong tuần"
        string PhongTap "Studio 1, Studio 2, Ring A"
        string ChiNhanh FK "Địa điểm cơ sở tổ chức lớp"
        int SiSoToiDa "Sĩ số giới hạn"
        int SiSoHienTai "Số học viên đã đăng ký"
    }

    DangKyLop {
        int Id PK "Khóa chính tự tăng"
        string MaLop FK "Liên kết lớp học"
        int MaHoiVien FK "Liên kết hội viên"
        string TenHoiVien "Tên học viên"
        string SoDienThoai "SĐT học viên"
        datetime NgayDangKy "Thời điểm ghi danh"
        string TrangThaiDiemDanh "Đã điểm danh / Chưa điểm danh / Vắng"
        string GhiChu "Ghi chú đặc biệt"
    }

    LichSuCheckIn {
        int Id PK "Audit Log ID tự tăng"
        int MaHoiVien "ID hội viên quét thẻ"
        string TenHoiVien "Họ tên hội viên"
        string MaTheQR "Mã thẻ quét thực tế"
        datetime ThoiGian "Thời điểm quẹt thẻ chính xác"
        string ChiNhanh FK "Trạm kiểm soát quẹt thẻ (Cơ sở quét)"
        string CuaRaVao "Cổng Flap Barrier A1 / B1 / VIP Gate"
        string PhuongThuc "Mã QR App / Mã Barcode Thẻ"
        string TrangThai "Hợp lệ / Từ chối"
        string GhiChu "Lý do từ chối hoặc xác nhận"
        long ThoiGianPhanHoiMs "Độ trễ xử lý (Latency dưới 50ms)"
    }

    HoaDon {
        string MaHoaDon PK "Mã giao dịch tài chính"
        int MaHoiVien "ID khách hàng thanh toán"
        string TenHoiVien "Tên khách hàng"
        string LoaiGiaoDich "Đăng ký gói / Gia hạn / POS / Locker"
        decimal TongTien "Tổng tiền trước giảm giá"
        decimal GiamGia "Chiết khấu khuyến mãi"
        decimal ThanhTien "Thực thu chuẩn DECIMAL"
        string HinhThucThanhToan "Tiền mặt / VietQR Chuyển khoản"
        string NguoiLap "Nhân viên lập phiếu"
        string ChiNhanh FK "Chi nhánh phát sinh doanh thu"
        datetime NgayLap "Ngày lập hóa đơn"
        string TrangThai "Đã thanh toán"
        string GhiChu "Chi tiết giao dịch"
    }

    %% Quan hệ trung tâm Chi Nhánh (Multi-Branch Hub)
    ChiNhanh ||--o{ TaiKhoan : "nhan vien truc thuoc"
    ChiNhanh ||--o{ HoiVien : "co so ghi danh ban dau"
    ChiNhanh ||--o{ HuanLuyenVien : "hlv phu trach"
    ChiNhanh ||--o{ LopHoc : "dia diem to chuc lop"
    ChiNhanh ||--o{ LichSuCheckIn : "tram kiem soat cua"
    ChiNhanh ||--o{ HoaDon : "doanh thu phat sinh"

    %% Quan hệ nghiệp vụ xuyên suốt
    HoiVien ||--o{ DangKyLop : "dang ky va diem danh"
    LopHoc ||--o{ DangKyLop : "danh sach lop"
    HoiVien ||--o{ LichSuCheckIn : "lich su check-in"
    HoiVien ||--o{ HoaDon : "hoa don thanh toan"
    GoiTap ||--o{ HoaDon : "chi tiet goi"
    HuanLuyenVien ||--o{ LopHoc : "giang day"
```

---

## 2. SƠ ĐỒ USE CASE PHÂN CẤP ĐA CHI NHÁNH (SYSTEM USE CASE DIAGRAM)

Mô tả sự phân định rạch ròi giữa **Chủ Chuỗi Hệ Thống (Chain Owner)** có góc nhìn toàn cảnh đa cơ sở và **Quản Lý Chi Nhánh (Branch Operations Manager)** quản lý chuyên biệt theo cơ sở:

```mermaid
flowchart TD
    subgraph Actors [Tác nhân Người Dùng & Thiết Bị]
        Member["Hội viên (Member VIP)"]
        Receptionist["Lễ tân cơ sở (CN01 / CN02)"]
        Trainer["Huấn luyện viên (PT/Coach)"]
        BranchManager["Quản lý chi nhánh (Branch Manager)"]
        ChainOwner["Chủ chuỗi hệ thống (Chain Owner)"]
        FlapBarrier["Cổng Flap Barrier & Máy Quét USB"]
    end

    subgraph BranchOps [Nghiệp Vụ Cấp Chi Nhánh Cơ Sở]
        UC_CheckIn["Quét thẻ QR & Mở cổng Check-in"]
        UC_RegisterMember["Đăng ký & Cấp thẻ tại cơ sở"]
        UC_RenewPackage["Gia hạn gói tập tại quầy"]
        UC_POS["Bán hàng POS nước uống & phụ kiện"]
        UC_Locker["Quản lý tủ Locker chi nhánh"]
        UC_ClassAttendance["Điểm danh lớp học Studio cơ sở"]
        UC_ShiftSchedule["Xếp lịch trực ca tuần nhân viên cơ sở"]
        UC_BranchEquip["Quản lý & Bảo trì máy móc cơ sở"]
        UC_BranchCapacity["Theo dõi sức chứa & lưu lượng cơ sở"]
    end

    subgraph ChainLevel [Nghiệp Vụ Chuỗi Cấp Cao Hệ Thống]
        UC_CrossBranchReport["Báo cáo so sánh doanh thu đa chi nhánh"]
        UC_PeakHeatmap["Phân tích Heatmap mật độ giờ cao điểm 2 cơ sở"]
        UC_ChainEmployee["Quản lý & Điều chuyển nhân sự liên chi nhánh"]
        UC_BranchCatalog["Quản lý danh mục & Thông số các chi nhánh"]
        UC_PackageScope["Cấu hình phạm vi gói tập (Đơn cơ sở vs Toàn chuỗi)"]
    end

    %% Hội viên
    Member --> UC_CheckIn
    Member --> UC_ClassAttendance

    %% Thiết bị phần cứng
    FlapBarrier --> UC_CheckIn

    %% Lễ tân chi nhánh
    Receptionist --> UC_CheckIn
    Receptionist --> UC_RegisterMember
    Receptionist --> UC_RenewPackage
    Receptionist --> UC_POS
    Receptionist --> UC_Locker

    %% HLV
    Trainer --> UC_ClassAttendance

    %% Quản lý chi nhánh
    BranchManager --> UC_ShiftSchedule
    BranchManager --> UC_BranchEquip
    BranchManager --> UC_BranchCapacity
    BranchManager --> UC_CheckIn
    BranchManager --> UC_RegisterMember

    %% Chủ chuỗi hệ thống
    ChainOwner --> UC_CrossBranchReport
    ChainOwner --> UC_PeakHeatmap
    ChainOwner --> UC_ChainEmployee
    ChainOwner --> UC_BranchCatalog
    ChainOwner --> UC_PackageScope
```

---

## 3. SƠ ĐỒ TUẦN TỰ: USE CASE LÕI CHECK-IN LIÊN CHI NHÁNH (SEQUENCE DIAGRAM)

Use case trung tâm của đồ án — thực thi **5 Quy Tắc Nghiệp Vụ Kinh Doanh (5 Core Business Rules)** với độ trễ phản hồi cực thấp (< 50ms), đặc biệt làm nổi bật **Quy tắc 4: So khớp thẻ cơ sở đơn lẻ vs Thẻ VIP toàn chuỗi**:

```mermaid
sequenceDiagram
    autonumber
    actor HV as Hội Viên / Khách Quẹt Thẻ
    participant HW as Máy Quét QR / Cổng Barrier
    participant UI as Giao Diện CheckInForm (Trạm Cơ Sở)
    participant Svc as CheckInService (Rule Engine)
    participant HvDAO as HoiVienDAO
    participant LogDAO as LichSuCheckInDAO
    participant DB as CSDL SQL Server (FitLifeGym)
    participant Sound as SoundHelper (Còi Báo PCM)

    HV->>HW: Đưa thẻ Barcode / Mã QR App vào camera quét
    HW->>UI: Truyền chuỗi mã thẻ (VD: "QR001", "QR004", "VIP007")
    UI->>Svc: thucHienCheckIn(maQR, currentBranch = "CN01 - Quận 1")
    Note over Svc: Bắt đầu đo thời gian phản hồi (Latency Timer)

    %% Bước 1: Tra cứu hồ sơ
    Svc->>HvDAO: timKiemThongMinh(maQR)
    alt CSDL Online
        HvDAO->>DB: SELECT * FROM HoiVien WHERE MaTheQR = :code
        DB-->>HvDAO: Trả về Entity HoiVien
    else CSDL Offline (Sự cố mạng tại chi nhánh phụ)
        HvDAO-->>Svc: Lấy từ Dual Memory Fallback Cache
    end
    HvDAO-->>Svc: Trả về đối tượng HoiVien (hoặc null)

    %% Bước 2: Kiểm tra 5 Business Rules
    alt Rule 1: Thẻ không tồn tại trên hệ thống
        Svc->>LogDAO: luuCheckIn(Từ chối - Không tồn tại)
        Svc-->>UI: CheckInResult(success: false, gateStatus: LOCKED)
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Cảnh báo đỏ: MÃ THẺ KHÔNG TỒN TẠI TRÊN HỆ THỐNG
    else Rule 2: Thẻ đang bị Tạm Khóa (vi phạm nội quy)
        Svc->>LogDAO: luuCheckIn(Từ chối - Thẻ bị tạm khóa)
        Svc-->>UI: CheckInResult(success: false, gateStatus: LOCKED)
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Cảnh báo đỏ: THẺ ĐANG BỊ TẠM KHÓA
    else Rule 3: Gói tập Hết Hạn
        Svc->>LogDAO: luuCheckIn(Từ chối - Gói tập hết hạn)
        Svc-->>UI: CheckInResult(success: false, gateStatus: LOCKED, canRenew: true)
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Cảnh báo: GÓI TẬP ĐÃ HẾT HẠN - GỢI Ý GIA HẠN NGAY
    else Rule 4: Sai Chi Nhánh (Thẻ đơn cơ sở CN02 quẹt tại CN01)
        Svc->>LogDAO: luuCheckIn(Từ chối - Sai chi nhánh đăng ký)
        Svc-->>UI: CheckInResult(success: false, gateStatus: LOCKED)
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Cảnh báo: THẺ CƠ SỞ ĐƠN LẺ! ĐĂNG KÝ TẠI TÂN BÌNH KHÔNG THỂ VÀO QUẬN 1 (Gợi ý nâng cấp VIP Diamond)
    else Rule 5: Chống quét trùng lặp (< 2 phút từ lần quét trước)
        Svc->>LogDAO: luuCheckIn(Từ chối - Quét trùng lặp)
        Svc-->>UI: CheckInResult(success: false, gateStatus: LOCKED)
        UI->>Sound: phatAmThanhTuChoi()
        UI->>HV: Cảnh báo: THẺ VỪA QUÉT TRƯỚC ĐÓ - CHỐNG QUÉT TRÙNG (ANTI-PASSBACK)
    else Hợp lệ toàn bộ (Đạt 5 quy tắc - Thẻ đúng cơ sở HOẶC Thẻ VIP toàn hệ thống)
        Svc->>LogDAO: luuCheckIn(Hợp lệ - Cổng mở, latency < 50ms)
        LogDAO->>DB: INSERT INTO LichSuCheckIn (...)
        Svc-->>UI: CheckInResult(success: true, gateStatus: OPEN, isVip)
        UI->>Sound: phatAmThanhThanhCong()
        UI->>HW: Gửi tín hiệu mở cổng tự động Barrier (GPIO / Serial Command)
        UI->>HV: Bảng chào mừng xanh: CHECK-IN THÀNH CÔNG - MỜI VÀO TẬP!
    end
```

---

## 4. CHUYÊN ĐỀ PHÂN TÍCH: BÀI TOÁN QUẢN TRỊ CHUỖI ĐA CHI NHÁNH FITLIFE GYM

Khi bảo vệ trước Hội đồng, sinh viên giải trình sâu 5 khía cạnh kiến trúc đa chi nhánh để khẳng định đẳng cấp phần mềm:

### 4.1. Kiến Trúc Chuỗi 2 Cơ Sở Trọng Điểm:
- **Cơ sở 1 (CN01 - Flagship Center - Quận 1):** Sức chứa 180 khách, trang bị đầy đủ Studio Yoga, Ring Boxing, khu Functional, phục vụ phân khúc khách hàng văn phòng và hội viên cao cấp.
- **Cơ sở 2 (CN02 - Premium Branch - Tân Bình):** Sức chứa 120 khách, tập trung vào Pilates, Zumba và CrossFit, phục vụ cư dân và sinh viên khu vực Tây Bắc TP.HCM.

### 4.2. Chính Sách Phân Quyền Dữ Liệu Theo Chi Nhánh (Branch-Level Data Isolation):
- Nhân viên Lễ tân (`letan1`) chỉ thao tác bán hàng POS, giao tủ Locker và duyệt check-in cho khách tại quầy cơ sở của mình.
- Quản lý chi nhánh (`quanly1`) theo dõi chỉ số sức chứa, lập bảng xếp ca trực tuần cho nhân sự cơ sở mình phụ trách.
- Chủ hệ thống (`admin`) sở hữu thanh công cụ **Active Branch Selector**, cho phép chuyển đổi nhanh giữa:
  * `🏢 Toàn hệ thống (2 cơ sở)`: Tổng hợp doanh số, đối soát dòng tiền và cân đối nhân sự toàn chuỗi.
  * `📍 CN01 - Quận 1 (Flagship)`: Đi sâu vào chi tiết vận hành trụ sở chính.
  * `📍 CN02 - Tân Bình (Premium)`: Giám sát riêng cơ sở phụ.

### 4.3. Chính Sách Phân Cấp Thẻ Tập (Membership Tiering & Roaming):
- **Thẻ Cơ Sở Đơn Lẻ (Single-Branch Pass - GT01/GT02/GT06):** Giá vé tiết kiệm (350k - 500k/tháng), hội viên chỉ được check-in tại chi nhánh đã ghi danh. Nếu sang cơ sở khác, cổng Barrier sẽ tự động khóa và gợi ý nâng cấp thẻ.
- **Thẻ VIP Diamond Toàn Chuỗi (All-Branch VIP Pass - GT04):** Giá 4.500.000 VNĐ/năm, cho phép ra vào tự do mọi cơ sở trong chuỗi FitLife Gym với quyền ưu tiên lối đi VIP Gate riêng.

### 4.4. Giải Pháp Dự Phòng Khi Mạng Chi Nhánh Chập Chờn (Dual Cache Circuit Breaker):
- Đặc thù hạ tầng mạng tại các chi nhánh thể thao thường xuyên gặp sự cố đứt kết nối internet tới SQL Server trung tâm.
- Nhóm 12 đã hiện thực kiến trúc **Bộ Nhớ Đệm Kép Thread-Safe (`CopyOnWriteArrayList`)** trong `ChiNhanhDAO`, `HoiVienDAO`, `LichSuCheckInDAO`. Khi mất mạng, trạm quét tự động chuyển sang chế độ đệm nội bộ, duy trì tốc độ mở cổng < 50ms mà không làm ách tắc hàng dài khách tập vào giờ cao điểm.

### 4.5. Đối Soát Doanh Thu & Báo Cáo Tài Chính Chuỗi (Cross-Branch Financial Consolidation):
- Mỗi hóa đơn (`HoaDon`) và lịch sử quét thẻ (`LichSuCheckIn`) đều gắn chặt với mã chi nhánh phát sinh `ChiNhanh`.
- Trung tâm Báo cáo Thống kê hỗ trợ xuất file Excel chuẩn UTF-8 BOM, phân tách rạch ròi doanh thu từng cơ sở, hỗ trợ tính lương thưởng KPIs cho nhân viên và phân tích điểm hòa vốn cho từng chi nhánh.
