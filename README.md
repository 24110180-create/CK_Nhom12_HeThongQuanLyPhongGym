# FITLIFE GYM MANAGEMENT SYSTEM
Hệ thống Quản lý và Vận hành Phòng Gym - Nhóm 12

---

## 1. THÔNG TIN NHÓM VÀ BẢNG PHÂN CÔNG CÔNG VIỆC

Nhóm thực hiện: Nhóm 12

| Thành viên | Vai trò | Nhiệm vụ chính phụ trách | Tỷ lệ đóng góp |
| :--- | :--- | :--- | :---: |
| Lê Anh Duy | Thành Viên | - Quản lý tiến độ, thiết kế CSDL (Database.sql) và Hibernate.<br>- Phát triển Module Xác thực, Phân quyền và Khung MainDashboard.<br>- Xây dựng chức năng Check-in hội viên và ghi nhận Log ra/vào.
| Võ Lê Ngọc Hưng | Thành viên | - Module Quản lý Hội viên (đăng ký, gia hạn, hồ sơ chi tiết).<br>- Module Quản lý Huấn luyện viên và Phân ca nhân sự.<br>- Module Lịch lớp học và Huấn luyện (PT).
| Võ Tấn Phát | Thành viên | - Module Điểm bán hàng POS, Kho hàng và Hóa đơn.<br>- Module Báo cáo doanh thu và Biểu đồ nhiệt (Heatmap).<br>- Tích hợp tiện ích: Xuất Excel, sinh mã QR và Âm thanh.

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
| hoivien1 | 123456 | Hội viên | Cổng thông tin hội viên, thẻ QR điện tử, đặt chỗ lớp học. |
## 5. HƯỚNG DẪN CÀI ĐẶT VÀ CHẠY ỨNG DỤNG

Yêu cầu trước khi chạy:
- Máy đã cài đặt JDK 17 trở lên.

Bước 1: Cài đặt cơ sở dữ liệu
1. Mở SQL Server Management Studio (SSMS).
2. Mở file Database.sql trong thư mục GYM/database/ của dự án.
3. Chọn Execute (F5) để tạo Database 'FitLifeGym' và nạp dữ liệu mẫu.

Bước 2: Cấu hình kết nối
Mở file: GYM/src/main/resources/hibernate.cfg.xml
Kiểm tra và sửa username/password phù hợp với tài khoản SQL Server trên máy:
- hibernate.connection.username: điền username (ví dụ: sa hoặc admin123)
- hibernate.connection.password: điền mật khẩu SQL Server của máy bạn

Bước 3: Khởi chạy ứng dụng
Chọn 1 trong 3 cách:
- Cách 1: Click đúp vào file run.bat trong thư mục GYM để chạy nhanh.
- Cách 2: Sử dụng lệnh Maven qua terminal:
  cd GYM
  mvn clean compile
  mvn exec:java -Dexec.mainClass="com.nhom12.gym.gui.LoginForm"
- Cách 3: Mở thư mục GYM bằng IntelliJ IDEA hoặc Eclipse -> Tìm file LoginForm.java -> Run.

---

## 6. QUY ƯỚC LÀM VIỆC (WORKFLOW VÀ GIT CONVENTION)

Để đảm bảo tính đồng bộ giữa các thành viên trong nhóm:

1. Quy tắc rẽ nhánh (Branching Strategy):
- main: Chỉ chứa code ổn định nhất dùng để báo cáo và demo.
- develop: Nhánh phát triển tích hợp code chung của cả nhóm.
- feature/<ten-chuc-nang>: Nhánh làm việc riêng của từng thành viên (ví dụ: feature/checkin, feature/quanly-hoivien, feature/pos).

2. Quy trình đồng bộ code:
- Trước khi bắt đầu làm tính năng mới: git pull origin develop
- Sau khi hoàn thành: Đẩy lên nhánh feature của mình và tạo Pull Request (PR) vào nhánh develop để nhóm trưởng kiểm tra trước khi gộp code.

