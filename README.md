# FITLIFE GYM MANAGEMENT SYSTEM
Hệ thống Quản lý và Vận hành Phòng Gym - Nhóm 12 (Trạng thái: Đã hoàn thành 99%)

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

## 4. PHẠM VI TRUY CẬP & TÀI KHOẢN KIỂM THỬ (USER ROLES & DEMO CREDENTIALS)

Mật khẩu mặc định cho tất cả tài khoản nhân sự: **123456**

### A. Những người ĐƯỢC QUYỀN đăng nhập và sử dụng (Nhân sự nội bộ):

1. **Chủ phòng tập (Anh Đức)** - Tài khoản: `admin` / Mật khẩu: `123456`
   - Đăng nhập để xem các báo cáo tổng hợp: doanh thu theo ngày/tuần/tháng, doanh thu từng chi nhánh (CN01, CN02) và tần suất sử dụng phòng tập.
   - Xem danh sách cảnh báo tự động các hội viên sắp hết hạn trong vòng 7 ngày tới để chăm sóc khách hàng.
   - Quản trị nhân sự & đội ngũ toàn chuỗi: thêm/bớt/sửa nhân sự, phân cơ sở công tác (CN01 & CN02), phân ca trực, khóa/mở khóa tài khoản, cấp lại mật khẩu (123456), theo dõi quỹ lương & thưởng KPI tự động, xuất bảng lương ra Excel.
   - Cấu hình và xem giá thực tế của các hội viên VIP (chính sách giảm 50% theo yêu cầu bảo mật).

2. **Lễ tân (Chị Linh)** - Tài khoản: `letan1` (CN01) hoặc `letan2` (CN02) / Mật khẩu: `123456`
   - Đăng nhập tại quầy lễ tân để xử lý nghiệp vụ vận hành hàng ngày.
   - Thao tác quét mã / nhập mã check-in cho hội viên với tốc độ dưới 2 giây.
   - Tạo mới hồ sơ hội viên, bán gói tập, thực hiện gia hạn gói và ghi nhận các khoản thu tiền mặt/chuyển khoản.
   - Đăng ký hội viên vào các lịch lớp học cụ thể (Yoga, Zumba, Cardio).

3. **Huấn luyện viên (Anh Tuấn)** - Tài khoản: `hlv1` (CN01) hoặc `hlv2` (CN02) / Mật khẩu: `123456`
   - Đăng nhập để xem và cập nhật lịch làm việc cá nhân.
   - Thực hiện điểm danh danh sách học viên có mặt trong các buổi dạy lớp.
   - Theo dõi thông tin chỉ số InBody và tiến độ tập luyện của học viên PT kèm 1:1.

4. **Quản lý chi nhánh (Anh Sơn)** - Tài khoản: `quanly1` / Mật khẩu: `123456`
   - Điều hành ca trực nhân viên, kiểm kê xuất nhập tồn kho hàng và theo dõi báo cáo chi nhánh phụ trách.

### B. Ai KHÔNG ĐƯỢC đăng nhập vào ứng dụng?

- **Hội viên (Khách tập) - KHÔNG ĐƯỢC ĐĂNG NHẬP:**
  - Tài liệu dự án xác định rõ: **App di động riêng cho hội viên đặt lịch nằm ngoài phạm vi (Out of Scope)** vì quy mô tính năng quá lớn.
  - Dù mục bảo mật có liệt kê 4 role lý thuyết (trong đó có role "Hội viên nếu có app riêng"), nhưng trong phiên bản triển khai thực tế này, **hội viên không có giao diện đăng nhập**.
  - Hội viên chỉ tương tác **gián tiếp**: xuất trình thẻ vật lý hoặc mã QR để lễ tân quét mã check-in tại quầy, và nhờ lễ tân thao tác đăng ký gói hoặc giữ chỗ lớp học trên hệ thống.

| Tài khoản | Mật khẩu | Vai trò (Role) 
| :--- | :--- | :--- | :--- |
| `admin` | 123456 | Chủ phòng (Anh Đức) 
| `letan1` | 123456 | Lễ tân (Chị Linh) 
| `hlv1` | 123456 | Huấn luyện viên (Anh Tuấn) 
| `quanly1` | 123456 | Quản lý chi nhánh 
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

