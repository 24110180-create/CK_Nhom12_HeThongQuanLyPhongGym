# 🏋️ FitLife Gym Management System (FitLife Pro)
> **Hệ Thống Quản Trị & Vận Hành Phòng Tập Thể Hình Toàn Diện Chuẩn Doanh Nghiệp**  
> *Đồ án Chuyên ngành / Tốt nghiệp Công nghệ Thông tin - Nhóm 12*

[![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.4.4.Final-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![SQL Server](https://img.shields.io/badge/MS%20SQL%20Server-2019%20%7C%202022-CC292B?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)](https://www.microsoft.com/sql-server)
[![GUI](https://img.shields.io/badge/Swing%20GUI-FlatLaf%203.4-007ACC?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![Build](https://img.shields.io/badge/Build-Maven%203.x-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic%20Project-green?style=for-the-badge)]()

---

## 📌 Giới Thiệu Tổng Quan Dự Án

**FitLife Gym Management System** là phần mềm quản lý và kiểm soát phòng tập thể hình thông minh, được xây dựng theo mô hình phân tầng **3-Tier Enterprise Architecture (Entity - DAO - Service - GUI)**. Dự án giải quyết trọn vẹn bài toán vận hành chuỗi phòng gym đa chi nhánh, từ kiểm soát lượt ra vào bằng mã vạch/mã QR, quản lý hợp đồng hội viên, điều hành lớp học Group Fitness, giám sát chỉ số InBody học viên, trạm bán hàng POS đến trung tâm báo cáo tài chính đa chiều cho Chủ phòng tập.

### 🌟 Điểm Nhấn Công Nghệ & Tính Năng Nổi Bật:
- **Use Case Lõi - Trạm Quét Check-In Tốc Độ Cao:** Kiểm tra tự động **5 Business Rules**, mô phỏng âm thanh bíp tần số PCM 1200Hz / 420Hz máy quét Honeywell, mô phỏng cổng an ninh Flap Barrier đóng/mở tự động với độ trễ siêu thấp **15ms - 35ms**.
- **Cơ Chế Offline Fallback Buffer:** Tự động đệm dữ liệu vào bộ nhớ cục bộ khi mạng Wifi chi nhánh suy yếu hoặc mất kết nối SQL Server, đảm bảo hoạt động không bị gián đoạn.
- **Phân Quyền Đa Không Gian (Role-Based Access Control):** 5 vai trò độc lập với giao diện làm việc tối ưu chuyên biệt: **Chủ Hệ Thống (Executive Cockpit 5 Trụ Cột)**, **Lễ Tân (Front-Desk)**, **Huấn Luyện Viên (Trainer Studio)**, **Quản Lý Chi Nhánh (Operations Center)**, **Hội Viên VIP (Member Portal)**.
- **Báo Cáo Đa Chi Nhánh Chuyên Sâu:** Biểu đồ doanh thu 2 cơ sở (Quận 1, Tân Bình), cảnh báo hội viên sắp hết hạn tích hợp gửi SMS/Zalo, Bản đồ nhiệt mật độ phòng tập (Hourly Heatmap 06:00 - 22:00).
- **Xuất Báo Cáo UTF-8 BOM Chuẩn:** Tích hợp bộ xử lý xuất bảng dữ liệu ra file Excel/CSV tiếng Việt 100% không bao giờ lỗi font ký tự.

---

## 🖥️ Giao Diện Demo Trực Quan (System Preview)

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

## 🛠️ Yêu Cầu Môi Trường (Prerequisites)

Để cài đặt và vận hành hệ thống trơn tru trên môi trường máy tính cá nhân hoặc hội đồng chấm thi, cần chuẩn bị:

| Thành phần | Phiên bản yêu cầu | Ghi chú kiểm thử |
|---|---|---|
| **Hệ Điều Hành** | Windows 10 / 11 (64-bit) | Tương thích hoàn hảo với script thực thi `.bat` |
| **Java Development Kit** | **JDK 17 LTS** (Khuyên dùng) hoặc JDK 21 | Sử dụng các tính năng Java 17 hiện đại |
| **Hệ Quản Trị CSDL** | **Microsoft SQL Server 2019 / 2022** | Chạy trên cổng mặc định `1433` |
| **Công Cụ Quản Lý DB** | SQL Server Management Studio (SSMS) 19.x+ | Dùng để chạy script `Database.sql` |
| **Công Cụ Build** | **Apache Maven 3.8+** | Tự động tải dependencies Hibernate, FlatLaf, MSSQL JDBC |
| **Môi Trường IDE** | IntelliJ IDEA (Khuyên dùng) / Eclipse / NetBeans | Mở thư mục dự án dưới dạng Maven Project |

---

## ⚡ Hướng Dẫn Cài Đặt Nhanh (Quick Start)

### Bước 1: Khởi Tạo Cơ Sở Dữ Liệu
1. Mở công cụ **SQL Server Management Studio (SSMS)** và kết nối tới SQL Server của bạn.
2. Mở file [Database.sql](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/Database.sql) nằm ở thư mục gốc của đồ án.
3. Nhấn **Execute (F5)** để khởi tạo:
   - Tự động tạo cơ sở dữ liệu `FitLifeGym`.
   - Tạo cấu trúc 8 bảng chuẩn quan hệ khóa ngoại: `TaiKhoan`, `HoiVien`, `GoiTap`, `HuanLuyenVien`, `LopHoc`, `DangKyLop`, `LichSuCheckIn`, `HoaDon`.
   - Nạp toàn bộ dữ liệu mẫu kiểm thử thực tế.

### Bước 2: Cấu Hình Kết Nối Hibernate
Mở file cấu hình kết nối tại đường dẫn:  
👉 [GYM/src/main/resources/hibernate.cfg.xml](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/resources/hibernate.cfg.xml)

Kiểm tra và sửa đổi tài khoản/mật khẩu SQL Server phù hợp với máy của bạn:
```xml
<!-- Địa chỉ máy chủ, cổng 1433 và tên Database FitLifeGym -->
<property name="hibernate.connection.url">jdbc:sqlserver://localhost:1433;databaseName=FitLifeGym;encrypt=true;trustServerCertificate=true;</property>

<!-- Tài khoản và mật khẩu SQL Server (Ví dụ user 'admin123' hoặc 'sa') -->
<property name="hibernate.connection.username">admin123</property>
<property name="hibernate.connection.password">123456</property>
```

> 💡 **Mẹo:** Nếu sử dụng tài khoản `sa`, hãy sửa username thành `sa` và password thành mật khẩu `sa` bạn đã đặt khi cài đặt SQL Server.

### Bước 3: Build & Chạy Ứng Dụng (1 Click)
Dự án đã chuẩn bị sẵn file thực thi tự động. Bạn có thể khởi chạy theo một trong 3 cách:

- **Cách 1 (Nhanh nhất - Khuyên dùng):**  
  Click đúp chuột vào file [run.bat](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/run.bat) ngay tại thư mục gốc. Hệ thống sẽ tự động liên kết thư viện Maven và khởi chạy màn hình đăng nhập.
- **Cách 2 (Sử dụng lệnh Maven):**
  ```powershell
  cd "c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM"
  mvn clean compile
  mvn exec:java -Dexec.mainClass="com.nhom12.gym.gui.LoginForm"
  ```
- **Cách 3 (Chạy trong IntelliJ IDEA):**
  - Mở thư mục `GYM` bằng IntelliJ IDEA.
  - Chờ Maven tự động load dependencies từ `pom.xml`.
  - Tìm đến file `src/main/java/com/nhom12/gym/gui/LoginForm.java` -> Chuột phải chọn **Run 'LoginForm.main()'**.

---

## 🔑 Bảng Tài Khoản Kiểm Thử Mặc Định

Hệ thống được thiết lập sẵn 5 tài khoản kiểm thử đại diện cho 5 nhóm đối tượng sử dụng khác nhau.  
*(Tất cả tài khoản đều sử dụng chung mật khẩu mặc định: **`123456`**)*

| Tên Đăng Nhập | Mật Khẩu | Vai Trò (Role) | Người Đại Diện & Không Gian Làm Việc |
|---|---|---|---|
| **`admin`** | `123456` | **Chủ Hệ Thống** | **Võ Anh Đức**: Executive Cockpit toàn quyền 5 trụ cột, xem Live KPI, Báo cáo Doanh thu, chính sách giá VIP ẩn. |
| **`letan1`** | `123456` | **Lễ Tân** | **Nguyễn Thị Linh (CN01)**: Front-Desk Check-in thẻ, Bán hàng POS, Tiếp nhận đăng ký, Sơ đồ tủ Locker, CSKH. |
| **`hlv1`** | `123456` | **Huấn Luyện Viên** | **Trần Tuấn Kiệt (HLV Trưởng)**: Trainer Studio, Lịch dạy lớp, Điểm danh Yoga/Zumba, Đo InBody, Sổ tay PR 1:1. |
| **`quanly1`** | `123456` | **Quản Lý Chi Nhánh** | **Lê Thanh Sơn**: Operations Center, Giám sát sức chứa cơ sở, Phân ca làm việc tuần, Quản lý kho hàng & thiết bị. |
| **`hoivien1`** | `123456` | **Hội Viên VIP** | **Lê Anh Duy (VIP Diamond)**: Member Portal, Thẻ điện tử QR, Đặt chỗ lớp học, Xem chỉ số InBody, Gia hạn VietQR. |

---

## 🎯 Bộ Mã Test Kiểm Thử Use Case Lõi (Check-In 1 Chạm)

Tại màn hình **Trạm Quét Check-In**, giảng viên có thể click trực tiếp vào các nút badge test nhanh để kiểm tra xử lý nghiệp vụ:

| Mã Thẻ / QR | Tên Hội Viên | Kết Quả Nghiệp Vụ Mong Đợi | Phản Hồi Âm Thanh & Cổng Flap |
|---|---|---|---|
| **`QR001`** | Lê Anh Duy | **Hợp lệ (VIP Diamond)** - Còn hạn đến 31/12/2026, toàn hệ thống. | Âm bíp cao 1200Hz • Cổng mở `✔ OPEN [XANH]` |
| **`QR002`** | Võ Lê Ngọc Hưng | **Hết Hạn** - Thẻ quá hạn ngày 15/10/2026. | Âm cảnh báo 420Hz • Cổng `✖ LOCKED [ĐỎ]` • Kích hoạt nút *⚡ Gia Hạn Ngay* |
| **`QR003`** | Võ Tấn Phát | **Tạm Khóa** - Thẻ bị khóa vi phạm chính sách phòng tập. | Âm cảnh báo 420Hz • Cổng `✖ LOCKED [ĐỎ]` • Từ chối ra vào |
| **`QR004`** | Trần Minh Quân | **Sai Chi Nhánh** - Thẻ cơ sở CN01 quét tại trạm CN02. | Âm cảnh báo 420Hz • Cổng `✖ LOCKED [ĐỎ]` • Yêu cầu về cơ sở gốc |
| **`VIP007`** | Đỗ Minh Trí | **Khách Đặc Biệt (Yêu cầu ẩn của Anh Đức)** - Giảm giá 50% được ẩn bảo mật với Lễ tân. | Âm bíp cao 1200Hz • Cổng mở `✔ OPEN [XANH]` • Ghi chú chính sách ẩn |

---

## 📂 Cấu Trúc Mã Nguồn (Directory Structure)

```
Đồ án cuối kì/
├── Database.sql               # Script khởi tạo CSDL FitLifeGym & dữ liệu mẫu
├── HUONG_DAN_DO_AN.md         # Báo cáo phạm vi đề tài & kiến trúc chi tiết
├── INSTALLATION_GUIDE.md      # Tài liệu hướng dẫn cài đặt & triển khai chi tiết
├── USER_MANUAL.md             # Sổ tay hướng dẫn vận hành cho người dùng cuối
├── HUONG_DAN_CAI_DAT.docx     # File Word hướng dẫn cài đặt nộp hội đồng
├── USER_MANUAL.docx           # File Word hướng dẫn sử dụng nộp hội đồng
├── run.bat                    # Script khởi chạy nhanh 1 chạm ở thư mục gốc
└── GYM/                       # Thư mục mã nguồn chính của ứng dụng
    ├── pom.xml                # Cấu hình Maven dependencies & compiler
    ├── run.bat                # Script chạy class Java với classpath .m2
    └── src/
        └── main/
            ├── resources/
            │   ├── hibernate.cfg.xml   # Cấu hình kết nối SQL Server & ORM
            │   └── images/             # Tài nguyên hình ảnh, background, POS
            └── java/com/nhom12/gym/
                ├── entity/             # 8 Lớp thực thể dữ liệu Hibernate
                ├── dao/                # Data Access Objects (Hỗ trợ Cache)
                ├── service/            # Xử lý nghiệp vụ lõi (Check-in, Báo cáo)
                ├── util/               # Tiện ích: Âm thanh PCM, Excel UTF-8, QR
                └── gui/                # 35 Màn hình giao diện FlatLaf chuyên sâu
```

---

## 👥 Danh Sách Thành Viên & Bảng Phân Công Nhiệm Vụ (Nhóm 12)

- **Đề Tài:** Phần Mềm Quản Lý & Vận Hành Phòng Gym FitLife (FitLife Gym Management System)
- **Nhóm Sinh Viên:** Nhóm 12
- **Giảng Viên Hướng Dẫn:** Bộ Môn Công Nghệ Phần Mềm / Hệ Thống Thông Tin
- **Khoa:** Công Nghệ Thông Tin

### 📋 Bảng Phân Công Nhiệm Vụ Chi Tiết:

| Thành viên | Vai trò | Nhiệm vụ chính phụ trách | Đóng góp |
|---|---|---|:---:|
| **Lê Anh Duy** | **Nhóm trưởng** | • Quản lý tiến độ, thiết kế CSDL (`Database.sql`) và Hibernate.<br>• Phát triển Module Xác thực, Phân quyền & Khung `MainDashboard`.<br>• Xây dựng chức năng Check-in hội viên & Log ra/vào. | **100%** |
| **Võ Lê Ngọc Hưng** | **Thành viên** | • Module Quản lý Hội viên (đăng ký, gia hạn, hồ sơ chi tiết).<br>• Module Quản lý Huấn luyện viên & Phân ca nhân sự.<br>• Module Lịch lớp học & Huấn luyện (PT). | **100%** |
| **Võ Tấn Phát** | **Thành viên** | • Module Điểm bán hàng POS, Kho hàng & Hóa đơn.<br>• Module Báo cáo doanh thu & Biểu đồ nhiệt (Heatmap).<br>• Tích hợp tiện ích: Xuất Excel, sinh mã QR & Âm thanh. | **100%** |

---
*FitLife Gym Management System - Giải pháp quản trị số hóa thể hình chuyên nghiệp.*
