# 🏋️ FITLIFE GYM MANAGEMENT SYSTEM (FitLife Pro)
> **Hệ Thống Quản Trị & Vận Hành Phòng Tập Thể Hình Toàn Diện Chuẩn Doanh Nghiệp**  
> *Đồ án Chuyên ngành / Tốt nghiệp Công nghệ Thông tin - Nhóm 12*

[![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.4.4.Final-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![SQL Server](https://img.shields.io/badge/MS%20SQL%20Server-2019%20%7C%202022-CC292B?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)](https://www.microsoft.com/sql-server)
[![GUI](https://img.shields.io/badge/Swing%20GUI-FlatLaf%203.4-007ACC?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![Build](https://img.shields.io/badge/Build-Maven%203.x-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

---

## 🌟 1. Điểm Nhấn Công Nghệ & Tính Năng Nổi Bật

- **Use Case Lõi - Trạm Quét Check-In Tốc Độ Cao:** Kiểm tra tự động **5 Business Rules**, mô phỏng âm thanh bíp tần số PCM 1200Hz / 420Hz máy quét Honeywell, mô phỏng cổng an ninh Flap Barrier đóng/mở tự động với độ trễ siêu thấp **15ms - 35ms**.
- **Cơ Chế Offline Fallback Buffer:** Tự động đệm dữ liệu vào bộ nhớ cục bộ khi mạng Wifi chi nhánh suy yếu hoặc mất kết nối SQL Server, đảm bảo hoạt động không bị gián đoạn.
- **Phân Quyền Đa Không Gian (Role-Based Access Control):** 5 vai trò độc lập với giao diện làm việc tối ưu chuyên biệt: **Chủ Hệ Thống (Executive Cockpit 5 Trụ Cột)**, **Lễ Tân (Front-Desk)**, **Huấn Luyện Viên (Trainer Studio)**, **Quản Lý Chi Nhánh (Operations Center)**, **Hội Viên VIP (Member Portal)**.
- **Báo Cáo Đa Chi Nhánh Chuyên Sâu:** Biểu đồ doanh thu 2 cơ sở (Quận 1, Tân Bình), cảnh báo hội viên sắp hết hạn tích hợp gửi SMS/Zalo, Bản đồ nhiệt mật độ phòng tập (Hourly Heatmap 06:00 - 22:00).
- **Xuất Báo Cáo UTF-8 BOM Chuẩn:** Tích hợp bộ xử lý xuất bảng dữ liệu ra file Excel/CSV tiếng Việt 100% không lỗi font ký tự.
- **Đồng Bộ Gói Tập Thời Gian Thực (Observer Pattern):** Chủ phòng tự do định nghĩa tên gói, giá tiền, thời hạn; hệ thống tự động cập nhật ngay tức thì sang Menu Đăng Ký và Gia Hạn.

---

## 🖥️ 2. Giao Diện Demo Trực Quan (System Preview)

```
+---------------------------------------------------------------------------------------------------+
|  [FITLIFE GYM PRO]  👑 QUẢN TRỊ CHỦ HỆ THỐNG (EXECUTIVE COMMAND COCKPIT)           [CN01 - Q.1]   |
+---------------------------------------------------------------------------------------------------+
| [TRỤ CỘT 1: BÁO CÁO & THỐNG KÊ] [TRỤ CỘT 2: VẬN HÀNH] [TRỤ CỘT 3: KINH DOANH] [TRỤ CỘT 4: HLV]... |
| ------------------------------------------------------------------------------------------------- |
| [Báo Cáo Doanh Thu]  [Phân Tích Giờ Cao Điểm]  [Tổng Quan Sức Chứa]  [Lịch Sử Giao Dịch]         |
+---------------------------------------------------------------------------------------------------+
|  DOANH SỐ HÔM NAY: 18,500,000 đ | HỘI VIÊN ĐANG TẬP: 142/200 | LƯỢT CHECK-IN: 328 | TỶ LỆ: 71%   |
| +---------------------------------------------------+ +-----------------------------------------+ |
| |  BIỂU ĐỒ DOANH THU ĐA CHI NHÁNH (THÁNG HIỆN TẠI)  | |  HOURLY TRAFFIC HEATMAP (06:00 - 22:00) | |
| |  - Cơ Sở 1 (Quận 1):    ████████████ 175.2M (62%) | |  06:00 - 08:00: [██████████] Đỉnh Sáng  | |
| |  - Cơ Sở 2 (Tân Bình):  ████████     108.5M (38%)  | |  11:00 - 13:00: [████░░░░░░] Bình Thường| |
| +---------------------------------------------------+ +-----------------------------------------+ |
| [ Bảng Thống Kê Chi Tiết ] [ Cảnh Báo 12 HV Sắp Hết Hạn ] [ Xuất Báo Cáo Excel UTF-8 BOM ]        |
+---------------------------------------------------------------------------------------------------+
```

```
+---------------------------------------------------------------------------------------------------+
|  [TRẠM CHECK-IN TỰ ĐỘNG]  Honeywell Scanner USB Emulation • Phản Hồi: 18ms • [Online Đồng Bộ]    |
+-----------------------------------------------------------------+---------------------------------+
| Ô NHẬP / QUÉT MÃ QR THẺ: [ QR001                      ] [QUÉT] | THẺ HỘI VIÊN ĐƯỢC NHẬN DIỆN     |
| Nút Test Nhanh: [QR001 - Hợp lệ] [QR002 - Hết hạn] [QR003 - Khóa] | Họ tên: LÊ ANH DUY              |
|                 [QR004 - Sai CN] [VIP007 - Ẩn Giá Anh Đức]     | Gói: 12 THÁNG VIP DIAMOND       |
| --------------------------------------------------------------- | Hạn dùng: 31/12/2026 (Còn 82 ngày)
|   TRẠNG THÁI CỔNG TỰ ĐỘNG FLAP BARRIER:                         | Chi nhánh: Toàn Hệ Thống        |
|   ┌─────────────────────────────────────────────────────────┐   | Trạng thái: ACTIVE              |
|   │     ✔ CỔNG ĐÃ MỞ (OPEN) - XIN MỜI QUÝ KHÁCH VÀO CỬA     │   | ------------------------------- |
|   └─────────────────────────────────────────────────────────┘   | [ Gửi Mã QR ] [ In Thẻ Thép ]  |
+-----------------------------------------------------------------+---------------------------------+
```

---

## 👥 3. Thông Tin Nhóm & Bảng Phân Công Nhiệm Vụ

**Nhóm thực hiện:** Nhóm 12

| Thành viên | Vai trò | Nhiệm vụ chính phụ trách | Tỷ lệ đóng góp |
| :--- | :--- | :--- | :---: |
| **Lê Anh Duy** | **Thành viên** | • Quản lý tiến độ, thiết kế CSDL (`Database.sql`) và Hibernate.<br>• Phát triển Module Xác thực, Phân quyền và Khung `MainDashboard`.<br>• Xây dựng chức năng Check-in hội viên và ghi nhận Log ra/vào. | **100%** |
| **Võ Lê Ngọc Hưng** | **Thành viên** | • Module Quản lý Hội viên (đăng ký, gia hạn, hồ sơ chi tiết).<br>• Module Quản lý Huấn luyện viên và Phân ca nhân sự.<br>• Module Lịch lớp học và Huấn luyện (PT). | **100%** |
| **Võ Tấn Phát** | **Thành viên** | • Module Điểm bán hàng POS, Kho hàng và Hóa đơn.<br>• Module Báo cáo doanh thu và Biểu đồ nhiệt (Heatmap).<br>• Tích hợp tiện ích: Xuất Excel, sinh mã QR và Âm thanh. | **100%** |

---

## 🛠️ 4. Công Nghệ Sử Dụng (Tech Stack)

- **Ngôn ngữ:** Java (JDK 17 LTS)
- **Giao diện người dùng:** Java Swing, FlatLaf UI 3.4
- **Truy cập dữ liệu (ORM):** Hibernate 6.4.4.Final
- **Hệ quản trị CSDL:** Microsoft SQL Server 2019 / 2022
- **Công cụ Build & Dependency:** Apache Maven 3.x
- **Xuất file & Mã hóa:** ZXing (QR Code), Excel/CSV UTF-8 BOM, Java Sound PCM

---

## 🏗️ 5. Kiến Trúc Hệ Thống (3-Tier Layered Architecture)

Dự án áp dụng kiến trúc phân tầng 3-Tier chuẩn mực:

```text
[ GUI / Presentation Layer ]   (MainDashboard, CheckInForm, 35 Panel giao diện FlatLaf)
             |
[ Service / Business Layer ]   (AuthService, CheckInService, HoiVienService, BaoCaoService)
             |
[ DAO / Data Access Layer ]    (HoiVienDAO, GoiTapDAO, HoaDonDAO - Dual Cache Buffer)
             |
[ Database Layer ]             (MS SQL Server: FitLifeGym - 8 Quan hệ thực thể)
```

---

## 🔑 6. Tài Khoản Kiểm Thử Mặc Định (Demo Credentials)

> Mật khẩu mặc định cho tất cả tài khoản: **`123456`**

| Tài khoản | Mật khẩu | Vai trò (Role) | Chức năng kiểm thử chính |
| :--- | :--- | :--- | :--- |
| **`admin`** | `123456` | **Chủ hệ thống** | Toàn quyền hệ thống, xem KPI, báo cáo doanh thu, biểu đồ giờ cao điểm. |
| **`letan1`** | `123456` | **Lễ tân** | Check-in thẻ/QR, bán hàng POS, đăng ký hội viên, sơ đồ tủ Locker. |
| **`hlv1`** | `123456` | **Huấn luyện viên** | Lịch dạy cá nhân, điểm danh lớp học, theo dõi chỉ số InBody hội viên. |
| **`quanly1`** | `123456` | **Quản lý chi nhánh** | Phân ca làm việc nhân viên, quản lý kho hàng, giám sát thiết bị. |
| **`hoivien1`** | `123456` | **Hội viên VIP** | Cổng thông tin hội viên, thẻ QR điện tử, đặt chỗ lớp học. |

---

## ⚡ 7. Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### Yêu cầu trước khi chạy:
- Máy đã cài đặt **JDK 17 LTS** trở lên.
- Microsoft SQL Server 2019/2022 chạy cổng mặc định `1433`.

### Bước 1: Cài đặt cơ sở dữ liệu
1. Mở **SQL Server Management Studio (SSMS)**.
2. Mở file [Database.sql](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/Database.sql) tại thư mục gốc của dự án.
3. Chọn **Execute (F5)** để tạo Database `FitLifeGym` và nạp dữ liệu mẫu.

### Bước 2: Cấu hình kết nối
Mở file: [GYM/src/main/resources/hibernate.cfg.xml](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/resources/hibernate.cfg.xml)  
Kiểm tra và sửa username/password phù hợp với tài khoản SQL Server của máy:
- `hibernate.connection.username`: điền username (ví dụ: `sa` hoặc `admin123`)
- `hibernate.connection.password`: điền mật khẩu SQL Server của máy bạn

### Bước 3: Khởi chạy ứng dụng
Chọn 1 trong 3 cách:
- **Cách 1 (Nhanh nhất):** Click đúp vào file [run.bat](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/run.bat) tại thư mục gốc.
- **Cách 2 (Lệnh Maven qua terminal):**
  ```powershell
  cd GYM
  mvn clean compile
  mvn exec:java -Dexec.mainClass="com.nhom12.gym.gui.LoginForm"
  ```
- **Cách 3 (IntelliJ IDEA):** Mở thư mục `GYM` bằng IntelliJ IDEA -> Tìm file `LoginForm.java` -> Chuột phải chọn **Run 'LoginForm.main()'**.

---

## 🌿 8. Quy Ước Làm Việc (Git Convention & Workflow)

1. **Quy tắc rẽ nhánh (Branching Strategy):**
   - `main`: Chỉ chứa bản phát hành ổn định nhất dùng để báo cáo và chấm điểm demo.
   - `develop`: Nhánh phát triển tích hợp code chung liên tục của nhóm.
   - `feature/<ten-chuc-nang>`: Nhánh làm việc riêng của từng thành viên.

2. **Quy trình đồng bộ code:**
   - Trước khi bắt đầu làm tính năng mới: `git pull origin develop`
   - Sau khi hoàn thành: Đẩy lên nhánh feature của mình và tạo Pull Request (PR) vào nhánh `develop` để nhóm kiểm tra trước khi gộp code.
