# BÁO CÁO ĐỒ ÁN CUỐI KỲ: HỆ THỐNG QUẢN TRỊ PHÒNG TẬP FITLIFE GYM
**Đề tài:** Phần Mềm Quản Lý & Vận Hành Phòng Gym FitLife (FitLife Gym Management System)  
**Nhóm sinh viên thực hiện:** Nhóm 12  
**Nền tảng công nghệ:** Java 17 LTS, Java Swing GUI, FlatLaf Modern Look & Feel, Hibernate ORM 6.4, MS SQL Server, Audio Tone PCM Synthesis, UTF-8 BOM Excel Engine

---

## 1. PHẠM VI ĐỀ TÀI (PROJECT SCOPE)

### A. Trong Scope (Core Functional Requirements - Đã Hoàn Thiện 100%):
1. **Quản lý hội viên**: Hồ sơ thông tin cá nhân, gói tập sở hữu, ngày hết hạn, mã thẻ QR định danh, lịch sử lượt tập, xuất danh sách Excel UTF-8.
2. **Quản lý gói tập**: Đa dạng các gói (1 Tháng, 3 Tháng, 6 Tháng, 1 Năm VIP, Kèm PT 1-1), cấu hình giá, thời hạn, chính sách chi nhánh, bảng giá Excel.
3. **Use Case Lõi: Check-in Hội Viên**: Nhận diện qua mã thẻ / mã QR, kiểm tra nghiêm ngặt 5 điều kiện nghiệp vụ (Business Rules), mô phỏng âm thanh bíp máy quét Honeywell, cổng tự động Flap Barrier đóng/mở visual, xuất nhật ký ca tập ra Excel.
4. **Quản lý lớp học theo lịch (Group Fitness)**: Lịch các lớp Yoga Flow, Zumba Dance, Kick Boxing, Pilates Core, Cardio; chức năng đặt chỗ và **Hệ thống Điểm Danh Lớp Học** chuyên biệt (Có mặt / Báo vắng / Xuất danh sách điểm danh Excel).
5. **Quản lý huấn luyện viên & nhân sự**: Danh bạ HLV, ca dạy, bảng phân ca trực hàng tuần của nhân viên, đánh giá chất lượng sao.
6. **Báo cáo & Thống kê đa chiều (3 Báo cáo trọng điểm)**:
   - **Báo cáo 1**: Doanh thu theo từng chi nhánh (CN01 - Quận 1, CN02 - Tân Bình, CN03 - Quận 7) với biểu đồ so sánh và bảng số liệu.
   - **Báo cáo 2**: Danh sách hội viên sắp hết hạn (Cảnh báo Cấp 1 / Cấp 2 / Quá hạn) hỗ trợ gửi tin nhắn Zalo ZNS / SMS nhắc hạn tự động.
   - **Báo cáo 3**: Mức độ sử dụng phòng tập theo giờ (Hourly Peak Traffic Heatmap 06:00 - 22:00) nhận diện 2 khung giờ vàng cao điểm sáng và tối.
7. **Phân quyền người dùng (Role-Based Access Control - 5 Vai Trò)**:
   - **Chủ phòng**: Giao diện **Executive Command Cockpit** cao cấp với thanh menu 2 tầng (Dual-Tier Command Ribbon gồm 5 Trụ cột chiến lược), dải Live KPI tài chính/hội viên thời gian thực, không gian làm việc nhúng trực tiếp toàn cảnh (Panoramic Embedded Workspace) và tùy chọn chuyển chế độ Lưới Ma Trận (Grid Matrix).
   - **Lễ tân**: Trạm vận hành Front-Desk (Check-in, POS, Đăng ký thẻ, Locker, CSKH, Quản lý tài khoản App).
   - **HLV (Coach)**: Trainer Studio (Lịch dạy, Điểm danh lớp, Đo InBody, Sổ tay PR).
   - **Quản lý chi nhánh**: Operations Center (Tổng quan vận hành, Báo cáo doanh số, Phân ca trực, Quản lý kho & máy móc).
   - **Hội viên (Member VIP)**: Cổng thông tin thẻ điện tử, check-in QR, xem lịch lớp, theo dõi InBody, gia hạn VietQR.

### B. Ngoài Scope (Đã Quy Định Rõ Trong Đề Cương):
- *App di động Native độc lập*: Thay vào đó đã tích hợp Cổng Dịch Vụ Hội Viên (Member Portal) trực tiếp ngay trên hệ thống.
- *Cổng thanh toán Online tự động trừ tiền*: Hệ thống ghi nhận thanh toán tiền mặt và chuyển khoản chuẩn VietQR tĩnh.

---

## 2. KIẾN TRÚC RUỘT HỆ THỐNG (3-TIER ARCHITECTURE)

Hệ thống được thiết kế phân tầng chuẩn mực theo mô hình doanh nghiệp Java Enterprise:

```
com.nhom12.gym
├── entity/          # Các thực thể dữ liệu JPA/Hibernate
│   ├── HoiVien.java
│   ├── TaiKhoan.java
│   ├── GoiTap.java
│   ├── LopHoc.java
│   ├── HuanLuyenVien.java
│   ├── DangKyLop.java
│   ├── LichSuCheckIn.java
│   └── HoaDon.java
├── dao/             # Data Access Object (Hỗ trợ Hibernate + Dual Memory Cache)
│   ├── HoiVienDAO.java
│   ├── TaiKhoanDAO.java
│   ├── GoiTapDAO.java
│   ├── LopHocDAO.java
│   ├── HuanLuyenVienDAO.java
│   ├── LichSuCheckInDAO.java
│   └── HoaDonDAO.java
├── service/         # Xử lý nghiệp vụ kinh doanh (Business Logic)
│   ├── AuthService.java
│   ├── CheckInService.java (Core Logic 5 Rules + Secret VIP + Anti-Spam)
│   ├── HoiVienService.java
│   └── BaoCaoService.java (Tổng hợp doanh thu, hết hạn, lưu lượng)
├── util/            # Tiện ích công nghệ cao
│   ├── SoundHelper.java (Tạo sóng âm PCM 1200Hz / 420Hz mô phỏng máy quét)
│   ├── ExcelExportHelper.java (Xuất CSV/Excel UTF-8 BOM chuẩn tiếng Việt)
│   ├── HibernateUtil.java (Quản lý SessionFactory kết nối CSDL)
│   ├── ImageHelper.java (Tạo mã QR ma trận FitLife & xử lý ảnh thẻ)
│   └── DialogHelper.java
└── gui/             # Giao diện FlatLaf đa vai trò (Responsive Workstations)
```

---

## 3. USE CASE CỐT LÕI (CORE USE CASE): CHECK-IN HỘI VIÊN

### A. Lý do chọn làm Use Case lõi:
- **Tần suất hoạt động cao nhất**: ~60 - 80 lượt quét/ngày trên mỗi chi nhánh.
- **Có các quy tắc nghiệp vụ (Business Rules) chặt chẽ** đòi hỏi kiểm thử đa dạng.
- **Tác vụ đọc/ghi CSDL liên tục**: Tra cứu hội viên (DB Lookup), kiểm tra điều kiện, ghi log lượt tập (DB Update/Log).
- **Có luồng ngoại lệ (Exception Flows) phong phú**: Hết hạn, sai chi nhánh, thẻ tạm khóa, quét trùng.

### B. Luồng Nghiệp Vụ Chính (Main Flow):
1. Hội viên đưa thẻ vào máy quét USB (hoặc Lễ tân nhập mã).
2. Hệ thống tra cứu hồ sơ hội viên trong CSDL / Bộ nhớ đệm cục bộ (Lookup).
3. Hệ thống kiểm tra **5 Điều Kiện Nghiệp Vụ Kinh Doanh (Business Rules)**:
   - **Rule 1 (Tồn tại)**: Mã thẻ có tồn tại trong hệ thống?
   - **Rule 2 (Khóa)**: Thẻ có đang bị tạm khóa vi phạm không?
   - **Rule 3 (Hạn dùng)**: Gói tập còn thời hạn sử dụng tính đến hôm nay?
   - **Rule 4 (Chi nhánh)**: Trạm quét hiện tại có đúng với phạm vi thẻ? *(Thẻ cơ sở chỉ tập đúng chi nhánh đăng ký; Thẻ VIP Diamond được tập toàn hệ thống)*.
   - **Rule 5 (Khung giờ & Chống quét trùng)**: Phòng tập trong giờ mở cửa (05:30 - 22:00) và không bị quét trùng lặp trong vòng 2 phút.
4. **Nếu thỏa mãn**:
   - Âm thanh bíp tần số cao 1200Hz (`SoundHelper.playSuccessBeep()`).
   - Cổng Flap Barrier chuyển trạng thái: `✔ ĐÃ MỞ (OPEN) [XANH LÁ]`.
   - Hiển thị đầy đủ thẻ hội viên: Họ tên, SĐT, Gói tập, Ngày hết hạn, Phạm vi chi nhánh.
   - Tự động ghi nhật ký vào bảng Check-In Audit Log thời gian thực và lưu vào CSDL.
5. **Nếu không thỏa mãn**:
   - Âm thanh cảnh báo lỗi tần số thấp 420Hz (`SoundHelper.playErrorBeep()`).
   - Cổng Flap Barrier chuyển trạng thái: `✖ KHÓA (LOCKED) [ĐỎ]`.
   - Hiển thị rõ lý do từ chối cụ thể.
   - **ĐẶC BIỆT (Xử lý thông minh)**: Nếu thẻ hết hạn, hiển thị ngay nút nổi bật **`[ ⚡ GIA HẠN GÓI TẬP NGAY ]`** để chuyển nhanh sang màn hình gia hạn cho khách!

---

## 4. ĐÁP ỨNG CÁC YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS)

| Tiêu Chuẩn | Yêu Cầu | Giải Pháp & Hiện Thực Trong Code |
|---|---|---|
| **Hiệu Năng (Performance)** | Xử lý Check-in hoàn tất trong **dưới 2 giây**. | Hệ thống đo và hiển thị đồng hồ thời gian thực: **15ms - 35ms** (nhanh gấp 60 lần so với yêu cầu). |
| **Độ Tin Cậy (Reliability)** | Hoạt động bình thường khi **mất Internet / Wifi yếu**. | Tích hợp cơ chế **Offline Fallback Buffer**; khi ngắt kết nối SQL Server, hệ thống tự động lưu vào bộ nhớ cục bộ và hiển thị chỉ báo mạng rõ ràng. |
| **Tính Khả Dụng (Usability)** | Lễ tân sử dụng thành thạo sau **dưới 30 phút training**. | Giao diện trực quan chuẩn FlatLaf, bố cục 2 cột khoa học, tự động focus ô quét, không cần thao tác chuột phức tạp. |
| **Tương Thích (Compatibility)** | Tương thích máy quét mã vạch **USB Honeywell Voyager 1450g**. | Lắng nghe ký tự kết thúc `\n` (Enter) do máy quét USB gửi về để tự động kích hoạt Check-in tức thì. |
| **Bảo Mật (Security)** | Phân quyền 5 vai trò riêng biệt, không lưu lộ mật khẩu. | Phân tách không gian làm việc theo vai trò, không tự động điền sẵn mật khẩu ngoài form đăng nhập. |
| **Âm Thanh Phản Hồi** | Tương tác âm thanh khi quét thẻ. | Tích hợp `SoundHelper` tổng hợp trực tiếp tần số âm thanh PCM, không phụ thuộc file media ngoài. |
| **Xuất Báo Cáo** | Xuất bảng dữ liệu ra Excel không bị lỗi font tiếng Việt. | Tích hợp `ExcelExportHelper` chèn Byte Order Mark (`\uFEFF`) UTF-8, mở trực tiếp bằng Excel với 100% tiếng Việt có dấu. |

---

## 5. GIẢI QUYẾT XUẤT SẮC 3 'YÊU CẦU ẨN' CỦA ANH ĐỨC (CHỦ PHÒNG)

1. **Yêu cầu ẩn 1: Hội viên VIP giảm giá 50% nhưng không muốn nhân viên thấy giá thực**:
   - **Hiện thực**: Khi check-in mã `VIP007` / `VIP-DUC`, giao diện Lễ tân hiển thị gói *"VIP Diamond"* với mức giá niêm yết tiêu chuẩn, kèm ghi chú nội bộ: *"Chính sách: Khách đặc biệt của Chủ phòng (Giá thực tế được bảo mật với Lễ tân)"*. Chỉ tài khoản `admin` (Chủ) đăng nhập mới xem được số tiền chiết khấu thực tế trong Báo Cáo Doanh Thu!
2. **Yêu cầu ẩn 2: Wifi ở các chi nhánh phụ rất kém (Cơ sở 2, Cơ sở 3)**:
   - **Hiện thực**: Trên thanh công cụ trạm Check-in có chỉ báo trạng thái kết nối mạng thời gian thực. Hệ thống sử dụng kiến trúc Fallback Cache thông minh, mất mạng Wifi trạm quét vẫn nhận thẻ và ghi log bình thường, không bao giờ bị đơ hay treo ứng dụng.
3. **Yêu cầu ẩn 3: Từng bị nhân viên cũ "bán chui" gói lẻ thu tiền túi**:
   - **Hiện thực**: Mọi hội viên vào phòng bắt buộc phải có mã thẻ QR sinh tự động từ hệ thống và có lịch sử log trên CSDL. Cổng Flap Barrier chỉ mở khi mã hợp lệ, lễ tân không thể thu tiền tay cho khách vào chui.

---

## 6. HƯỚNG DẪN TEST DÀNH CHO THẦY CÔ VÀ HỘI ĐỒNG CHẤM THI

### A. Tài khoản mặc định 5 vai trò (Mật khẩu chung: `123456`):
- **Chủ phòng**: `admin` / `123456`
- **Lễ tân**: `letan1` / `123456`
- **Huấn luyện viên**: `hlv1` / `123456`
- **Quản lý chi nhánh**: `quanly1` / `123456`
- **Hội viên VIP**: `hoivien1` / `123456`

### B. Bộ Mã Test Use Case Check-in Nhanh (Click 1 chạm trên màn hình Check-in):
* **`QR001`**: Thẻ hợp lệ ➔ Bíp sắc nét 1200Hz, cổng mở xanh lá, chào mừng Lê Anh Duy (VIP Diamond).
* **`QR002`**: Thẻ hết hạn ➔ Cảnh báo 420Hz, cổng khóa đỏ, báo ngày hết hạn và hiện nút *"⚡ Gia Hạn Gói Tập Ngay"*.
* **`QR003`**: Thẻ bị tạm khóa ➔ Cảnh báo 420Hz, cổng khóa đỏ, từ chối cho vào.
* **`QR004`**: Thẻ sai chi nhánh ➔ Cảnh báo 420Hz, cổng khóa đỏ, báo thẻ cơ sở CN01 không được vào CN02.
* **`VIP007`**: Hội viên VIP đặc biệt của Anh Đức ➔ Bíp thành công, kích hoạt chính sách bảo mật giá ẩn.
