# FITLIFE GYM MANAGEMENT SYSTEM (FitLife Pro)
Hệ thống Quản lý và Vận hành Phòng Gym - Nhóm 12

---

## 1. THÔNG TIN NHÓM VÀ BẢNG PHÂN CÔNG CÔNG VIỆC

Đề tài: Phần mềm Quản lý Phòng Gym FitLife
Nhóm thực hiện: Nhóm 12

| Thành viên | Vai trò | Nhiệm vụ chính phụ trách | Tỷ lệ đóng góp |
| :--- | :--- | :--- | :---: |
| Lê Anh Duy | Nhóm trưởng | - Quản lý tiến độ, thiết kế CSDL (Database.sql) và Hibernate.<br>- Phát triển Module Xác thực, Phân quyền và Khung MainDashboard.<br>- Xây dựng chức năng Check-in hội viên và ghi nhận Log ra/vào. | 100% |
| Võ Lê Ngọc Hưng | Thành viên | - Module Quản lý Hội viên (đăng ký, gia hạn, hồ sơ chi tiết).<br>- Module Quản lý Huấn luyện viên và Phân ca nhân sự.<br>- Module Lịch lớp học và Huấn luyện (PT). | 100% |
| Võ Tấn Phát | Thành viên | - Module Điểm bán hàng POS, Kho hàng và Hóa đơn.<br>- Module Báo cáo doanh thu và Biểu đồ nhiệt (Heatmap).<br>- Tích hợp tiện ích: Xuất Excel, sinh mã QR và Âm thanh. | 100% |

---

## 2. CÔNG NGHỆ SỬ DỤNG (TECH STACK)

- Ngôn ngữ: Java (JDK 17 LTS)
- Giao diện người dùng: Java Swing, FlatLaf UI
- Truy cập dữ liệu (ORM): Hibernate 6.x
- Hệ quản trị CSDL: Microsoft SQL Server 2019 / 2022
- Công cụ Build và Dependency: Apache Maven 3.x
- Xuất file và Mã hóa: Apache POI (Excel), ZXing (QR Code)

---

## 3. KIẾN TRÚC HỆ THỐNG

Dự án áp dụng kiến trúc phân tầng 3-Tier (Layered Architecture):

[GUI / Presentation Layer] (Dashboard, Form, Panel giao diện)
          |
[Service / Business Layer] (AuthService, CheckInService, BaoCaoService)
          |
[DAO / Data Access Layer] (Hibernate Session, Query CRUD)
          |
[Database Layer] (MS SQL Server: FitLifeGym)

---

## 4. TÀI KHOẢN KIỂM THỬ (DEMO CREDENTIALS)

Mật khẩu mặc định cho tất cả tài khoản: 123456

| Tài khoản | Mật khẩu | Vai trò (Role) | Chức năng kiểm thử chính |
| :--- | :--- | :--- | :--- |
| admin | 123456 | Chủ hệ thống | Toàn quyền hệ thống, xem KPI, báo cáo doanh thu, biểu đồ giờ cao điểm. |
| letan1 | 123456 | Lễ tân | Check-in thẻ/QR, bán hàng POS, đăng ký hội viên, sơ đồ tủ Locker. |
| hlv1 | 123456 | Huấn luyện viên | Lịch dạy cá nhân, điểm danh lớp học, theo dõi chỉ số InBody hội viên. |
| quanly1 | 123456 | Quản lý chi nhánh | Phân ca làm việc nhân viên, quản lý kho hàng, giám sát thiết bị. |
| hoivien1 | 123456 | Hội viên VIP | Cổng thông tin hội viên, thẻ QR điện tử, đặt chỗ lớp học. |

