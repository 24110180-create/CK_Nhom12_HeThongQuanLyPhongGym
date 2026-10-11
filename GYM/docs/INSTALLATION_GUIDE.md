# 📘 TÀI LIỆU HƯỚNG DẪN CÀI ĐẶT & TRIỂN KHAI HỆ THỐNG
## HỆ THỐNG QUẢN TRỊ & VẬN HÀNH PHÒNG GYM FITLIFE (FITLIFE PRO)
**Đồ án Chuyên ngành / Tốt nghiệp Công nghệ Thông tin - Nhóm 12**  
*Mã đề tài: Phần mềm Quản lý Phòng Gym đa chi nhánh*

---

## 📑 MỤC LỤC
1. [Yêu Cầu Môi Trường & Phần Mềm Cần Thiết](#1-yêu-cầu-môi-trường--phần-mềm-cần-thiết)
2. [Bước 1: Thiết Lập Cơ Sở Dữ Liệu SQL Server](#bước-1-thiết-lập-cơ-sở-dữ-liệu-sql-server)
   - 2.1. Mở SQL Server Management Studio (SSMS) & Chạy Script
   - 2.2. Kích Hoạt Chế Độ Xác Thực Mixed Mode (SQL Server Authentication)
   - 2.3. Cấu Hình TCP/IP & Cổng Mạng 1433
3. [Bước 2: Cấu Hình Kết Nối Hibernate (hibernate.cfg.xml)](#bước-2-cấu-hình-kết-nối-hibernate-hibernatecfgxml)
   - 3.1. Vị Trí File Cấu Hình
   - 3.2. Giải Thích Các Tham Số Cốt Lõi
   - 3.3. Các Lưu Ý Tránh Lỗi Kết Nối
4. [Bước 3: Hướng Dẫn Build & Khởi Chạy Ứng Dụng](#bước-3-hướng-dẫn-build--khởi-chạy-ứng-dụng)
   - 4.1. Phương Pháp 1: Khởi Chạy 1 Chạm Bằng File `run.bat` (Khuyên Dùng)
   - 4.2. Phương Pháp 2: Khởi Chạy Bằng Lệnh Maven CLI
   - 4.3. Phương Pháp 3: Khởi Chạy Trên IntelliJ IDEA / Eclipse / NetBeans
5. [Bước 4: Kiểm Thử Đăng Nhập Sau Khi Khởi Chạy](#bước-4-kiểm-thử-đăng-nhập-sau-khi-khởi-chạy)
6. [Bảng Xử Lý Lỗi Thường Gặp (Troubleshooting FAQs)](#bảng-xử-lý-lỗi-thường-gặp-troubleshooting-faqs)

---

## 1. YÊU CẦU MÔI TRƯỜNG & PHẦN MỀM CẦN THIẾT

Trước khi tiến hành cài đặt, máy tính của Quý Thầy/Cô hoặc người triển khai cần cài sẵn các phần mềm nền tảng sau:

| STT | Phần mềm / Công cụ | Phiên bản khuyến nghị | Mục đích sử dụng |
|:---:|---|---|---|
| **1** | **Java Development Kit (JDK)** | **JDK 17 LTS** (hoặc JDK 21) | Biên dịch và thực thi ứng dụng Java Swing & Hibernate 6.x |
| **2** | **Microsoft SQL Server** | SQL Server 2019 hoặc 2022 | Hệ quản trị cơ sở dữ liệu quan hệ lưu trữ dữ liệu FitLifeGym |
| **3** | **SQL Server Management Studio** | SSMS v18.x hoặc v19.x+ | Giao diện đồ họa để chạy script CSDL và quản lý người dùng |
| **4** | **Apache Maven** | Maven 3.8+ (đã tích hợp trong IDE) | Quản lý thư viện phụ thuộc (FlatLaf, Hibernate, MSSQL-JDBC) |
| **5** | **IDE Lập Trình (Tùy chọn)** | IntelliJ IDEA / Eclipse / NetBeans | Xem mã nguồn, debug hoặc chạy thử nghiệm dự án |

> ⚠️ **Lưu ý quan trọng về Java:** Hibernate 6.4.4 yêu cầu tối thiểu **Java 17**. Nếu máy tính đang dùng Java 8 hoặc Java 11, cần nâng cấp hoặc cấu hình `JAVA_HOME` trỏ tới JDK 17 để tránh lỗi `UnsupportedClassVersionError`.

---

## BƯỚC 1: THIẾT LẬP CƠ SỞ DỮ LIỆU SQL SERVER

### 2.1. Mở SSMS & Chạy File Script `Database.sql`

1. Khởi động công cụ **SQL Server Management Studio (SSMS)**.
2. Tại màn hình **Connect to Server**:
   - **Server type:** `Database Engine`
   - **Server name:** `localhost` hoặc tên máy tính của bạn (ví dụ: `.\SQLEXPRESS` hoặc `.` hoặc `localhost`)
   - **Authentication:** Chọn `Windows Authentication` rồi bấm **Connect**.
3. Nhấn tổ hợp phím **Ctrl + O** (hoặc chọn menu **File -> Open -> File...**), điều hướng đến thư mục đồ án và chọn file:
   ```
   c:\Users\Anh Duy\Desktop\Đồ án cuối kì\Database.sql
   ```
4. Kiểm tra cấu trúc script. Script đã được viết chuẩn hóa theo thứ tự nghiệp vụ an toàn:
   - **Phần 1: Tạo Database:** Kiểm tra và tự động tạo cơ sở dữ liệu `FitLifeGym`.
   - **Phần 2: Xóa bảng cũ (Drop table):** Xóa các bảng theo đúng chiều quan hệ phụ thuộc khóa ngoại (`LichSuCheckIn` -> `HoaDon` -> `DangKyLop` -> `LopHoc` -> `HuanLuyenVien` -> `GoiTap` -> `HoiVien` -> `TaiKhoan`) để tránh lỗi xung đột ràng buộc.
   - **Phần 3: Tạo bảng (Create table):** Định nghĩa cấu trúc 8 bảng với các ràng buộc `PRIMARY KEY`, `FOREIGN KEY`, `IDENTITY`, `UNIQUE`, `DEFAULT`.
   - **Phần 4: Chèn dữ liệu mẫu (Insert Data):** Nạp đầy đủ 5 tài khoản tương ứng 5 vai trò, các gói tập, lịch lớp học, huấn luyện viên, nhật ký check-in và hóa đơn tài chính.
5. Nhấn phím **F5** (hoặc click nút **Execute** trên thanh công cụ).
6. Quan sát khung thông báo bên dưới: Nếu xuất hiện dòng chữ **`Commands completed successfully.`** là CSDL đã được khởi tạo hoàn tất 100%!

---

### 2.2. Kích Hoạt Chế Độ Xác Thực Mixed Mode (SQL Server Authentication)

Ứng dụng kết nối tới SQL Server thông qua cơ chế tài khoản người dùng (`username` & `password`). Do đó, SQL Server phải được bật chế độ **Mixed Mode**:

1. Trong cửa sổ **Object Explorer** của SSMS, click chuột phải vào **Tên Server** (mục đầu tiên trên cây thư mục) -> Chọn **Properties**.
2. Chọn trang **Security** ở danh mục bên trái.
3. Tại mục **Server authentication**, chọn radio button:
   - 🔘 **SQL Server and Windows Authentication mode**
4. Bấm **OK**. Hộp thoại nhắc khởi động lại dịch vụ SQL Server sẽ hiện ra, bấm **OK**.
5. **Kích hoạt tài khoản người dùng:**
   - Mở nhánh: **Security -> Logins**.
   - **Cách 1: Sử dụng tài khoản `sa` có sẵn:**
     - Chuột phải vào user **`sa`** -> Chọn **Properties**.
     - Trang **General:** Đặt mật khẩu mới (ví dụ: `123456`) và bỏ tích ô *Enforce password policy* (nếu muốn đặt pass ngắn dễ nhớ).
     - Trang **Status:**
       - Permission to connect to database engine: Chọn **Grant**.
       - Login: Chọn **Enabled**.
     - Bấm **OK**.
   - **Cách 2: Tạo tài khoản riêng `admin123` (Trùng khớp mặc định của đồ án):**
     - Chuột phải vào thư mục **Logins** -> Chọn **New Login...**
     - **Login name:** Nhập `admin123`.
     - Chọn **SQL Server authentication**, nhập mật khẩu: `123456`.
     - Trang **Server Roles:** Tích chọn `sysadmin`.
     - Trang **User Mapping:** Tích chọn CSDL `FitLifeGym` và cấp quyền `db_owner`.
     - Bấm **OK**.
6. **Khởi động lại dịch vụ SQL Server:**
   - Chuột phải vào **Tên Server** trong SSMS -> Chọn **Restart** -> Bấm **Yes** để xác nhận.

---

### 2.3. Cấu Hình TCP/IP & Kiểm Tra Cổng Mạng 1433

Để ứng dụng Java kết nối qua giao thức JDBC mạng nội bộ, cổng TCP/IP 1433 phải được kích hoạt:

1. Nhấn phím **Windows + R**, gõ lệnh: `SQLServerManager16.msc` (hoặc tìm kiếm ứng dụng **SQL Server Configuration Manager** trong menu Start).
2. Mở rộng nhánh: **SQL Server Network Configuration** -> Chọn **Protocols for MSSQLSERVER** (hoặc `Protocols for SQLEXPRESS`).
3. Đảm bảo giao thức **TCP/IP** đang ở trạng thái **`Enabled`**. Nếu đang là `Disabled`, click đúp chuột vào TCP/IP và chuyển thành **Enabled = Yes**.
4. Click đúp vào **TCP/IP**, chuyển sang tab **IP Addresses**:
   - Cuộn xuống mục dưới cùng là **IPAll**.
   - Kiểm tra ô **TCP Port:** Điền chính xác là **`1433`**.
   - Ô **TCP Dynamic Ports:** Xóa trống (để trống không điền số).
5. Bấm **Apply** -> **OK**.
6. Chọn mục **SQL Server Services** ở danh mục bên trái -> Chuột phải vào **SQL Server (MSSQLSERVER)** -> Chọn **Restart**.

---

## BƯỚC 2: CẤU HÌNH KẾT NỐI HIBERNATE (hibernate.cfg.xml)

### 3.1. Vị Trí File Cấu Hình

File cấu hình kết nối Hibernate nằm tại đường dẫn:
```
GYM/src/main/resources/hibernate.cfg.xml
```

### 3.2. Nội Dung Cấu Hình Chi Tiết & Các Dòng Cần Lưu Ý

Dưới đây là toàn bộ nội dung file cấu hình chuẩn của hệ thống:

```xml
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- 1. Driver kết nối Microsoft JDBC cho SQL Server -->
        <property name="hibernate.connection.driver_class">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>

        <!-- 2. Chuỗi kết nối JDBC: Chỉ định máy chủ localhost, Cổng 1433, CSDL FitLifeGym, Bật Trust Certificate -->
        <!-- [QUAN TRỌNG]: Thay đổi cổng hoặc tên CSDL tại đây nếu môi trường của bạn khác biệt -->
        <property name="hibernate.connection.url">jdbc:sqlserver://localhost:1433;databaseName=FitLifeGym;encrypt=true;trustServerCertificate=true;</property>

        <!-- 3. Tài khoản đăng nhập SQL Server (Thay bằng 'sa' hoặc tài khoản bạn đã tạo ở Bước 1) -->
        <property name="hibernate.connection.username">admin123</property>

        <!-- 4. Mật khẩu tương ứng của tài khoản SQL Server -->
        <property name="hibernate.connection.password">123456</property>

        <!-- 5. Dialect tối ưu hóa cho Microsoft SQL Server Dialect trong Hibernate 6 -->
        <property name="hibernate.dialect">org.hibernate.dialect.SQLServerDialect</property>
        
        <!-- 6. Hiển thị câu lệnh HQL / SQL đã sinh ra console để kiểm tra luồng nghiệp vụ -->
        <property name="hibernate.show_sql">true</property>

        <!-- 7. Khai báo 8 lớp Entity JPA ánh xạ bảng CSDL -->
        <mapping class="com.nhom12.gym.entity.HoiVien"/>
        <mapping class="com.nhom12.gym.entity.TaiKhoan"/>
        <mapping class="com.nhom12.gym.entity.GoiTap"/>
        <mapping class="com.nhom12.gym.entity.LopHoc"/>
        <mapping class="com.nhom12.gym.entity.LichSuCheckIn"/>
        <mapping class="com.nhom12.gym.entity.HoaDon"/>
        <mapping class="com.nhom12.gym.entity.HuanLuyenVien"/>
        <mapping class="com.nhom12.gym.entity.DangKyLop"/>
    </session-factory>
</hibernate-configuration>
```

### 3.3. Các Lưu Ý Vàng Khi Chỉnh Sửa File Cấu Hình:
- **Trường hợp dùng SQL Server Express:** Nếu cài bản Express, chuỗi kết nối có thể dạng `jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=FitLifeGym;...` hoặc chỉ cần `localhost:1433` sau khi đã cấu hình port cố định 1433 tại mục 2.3.
- **Trường hợp tài khoản `sa`:** Sửa dòng `<property name="hibernate.connection.username">sa</property>` và điền mật khẩu của bạn vào ô password.
- **Tham số `encrypt=true;trustServerCertificate=true;`:** Bắt buộc phải có để tránh lỗi từ chối bắt tay chứng chỉ SSL (SSL Handshake Exception) rất phổ biến trong các bản SQL Server mới.

---

## BƯỚC 3: HƯỚNG DẪN BUILD & KHỞI CHẠY ỨNG DỤNG

### 4.1. Phương Pháp 1: Khởi Chạy 1 Chạm Bằng File `run.bat` (Khuyên Dùng Nhất)

Đây là phương pháp thuận tiện nhất dành cho Thầy/Cô chấm bài mà không cần mở IDE phức tạp:
1. Mở thư mục gốc của đồ án:
   ```
   c:\Users\Anh Duy\Desktop\Đồ án cuối kì\
   ```
2. Click đúp chuột vào file **`run.bat`**.
3. Cửa sổ Command Prompt sẽ tự động:
   - Chuyển mã ký tự sang UTF-8 (`chcp 65001`).
   - Tự động nạp toàn bộ thư viện jar trong kho Maven local.
   - Ưu tiên kích hoạt JDK 17.
   - Khởi chạy trực tiếp lớp giao diện đăng nhập: `com.nhom12.gym.gui.LoginForm`.
4. Màn hình đăng nhập FitLife Gym hiện đại sẽ xuất hiện trong vòng 2 - 3 giây!

---

### 4.2. Phương Pháp 2: Khởi Chạy Bằng Lệnh Maven CLI

Nếu bạn quen thao tác với dòng lệnh terminal:
1. Mở **PowerShell** hoặc **Command Prompt**.
2. Điều hướng vào thư mục con `GYM`:
   ```powershell
   cd "c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM"
   ```
3. Chạy lệnh dọn dẹp và biên dịch mã nguồn:
   ```powershell
   mvn clean compile
   ```
4. Chạy ứng dụng thông qua plugin exec của Maven:
   ```powershell
   mvn exec:java -Dexec.mainClass="com.nhom12.gym.gui.LoginForm"
   ```

---

### 4.3. Phương Pháp 3: Khởi Chạy Trên IntelliJ IDEA / Eclipse / NetBeans

Dành cho việc chấm điểm mã nguồn, xem cấu trúc packages và debug:

#### Đối với IntelliJ IDEA (Khuyên dùng):
1. Khởi động **IntelliJ IDEA**.
2. Chọn **Open** -> Chọn thư mục **`Đồ án cuối kì`** (hoặc thư mục con `GYM`).
3. Chờ IntelliJ quét file `pom.xml` và tự động đồng bộ hóa các thư viện dependencies.
4. Cấu hình Project SDK:
   - Vào menu **File -> Project Structure... -> Project**.
   - Tại mục **SDK**, chọn **JDK 17** (hoặc cao hơn).
   - Language level: Chọn **17 - Sealed types, always-strict floating-point, etc.**.
   - Bấm **Apply -> OK**.
5. Điều hướng cây thư mục mã nguồn:
   ```
   GYM -> src -> main -> java -> com -> nhom12 -> gym -> gui -> LoginForm.java
   ```
6. Click chuột phải vào file **`LoginForm.java`** -> Chọn **Run 'LoginForm.main()'** (hoặc phím tắt **Shift + F10**).

---

## BƯỚC 4: KIỂM THỬ ĐĂNG NHẬP SAU KHI KHỞI CHẠY

Sau khi khởi chạy ứng dụng thành công, màn hình đăng nhập giao diện FlatLaf chuyên nghiệp sẽ hiển thị. Bạn có thể đăng nhập bằng một trong 5 tài khoản thử nghiệm sau:

| Tên Đăng Nhập | Mật Khẩu | Vai Trò | Chức Năng Chính Được Trải Nghiệm |
|---|---|---|---|
| **`admin`** | **`123456`** | **Chủ Hệ Thống** | **Executive Cockpit 5 Trụ Cột:** Báo cáo doanh thu đa chi nhánh (2 cơ sở), Biểu đồ lưu lượng Heatmap, Xem Live KPI tài chính, Quản trị hệ thống, Chính sách giá ẩn của khách VIP. |
| **`letan1`** | **`123456`** | **Lễ Tân** | **Front-Desk Workstation:** Quét thẻ Check-in âm thanh máy quét Honeywell, Quầy bán hàng POS in bill, Đăng ký hội viên mới, Quản lý sơ đồ 20 tủ Locker, Nhắc hạn SMS. |
| **`hlv1`** | **`123456`** | **Huấn Luyện Viên** | **Trainer Studio:** Lịch dạy lớp Yoga/Zumba/Boxing, Điểm danh học viên, Sổ tay theo dõi tiến độ PT 1:1, Đo đạc và cập nhật chỉ số InBody. |
| **`quanly1`** | **`123456`** | **Quản Lý Chi Nhánh** | **Operations Center:** Giám sát sức chứa cơ sở, Xếp lịch phân ca trực tuần, Quản lý kho hàng xuất nhập tồn, Quản lý bảo trì thiết bị máy móc. |
| **`hoivien1`** | **`123456`** | **Hội Viên VIP** | **Member VIP Portal:** Thẻ điện tử tích hợp mã QR cá nhân, Đặt chỗ lớp học, Theo dõi biến thiên chỉ số InBody, Gia hạn gói tập bằng quét mã VietQR. |

> 🌟 **Kiểm tra Use Case Lõi Check-in:**  
> Đăng nhập tài khoản `letan1` hoặc `admin` -> Vào **Trạm Quét Check-in** -> Click vào các nút test nhanh **`QR001`**, **`QR002`**, **`QR003`**, **`QR004`**, **`VIP007`** để thấy hiệu ứng âm thanh PCM 1200Hz/420Hz và trạng thái đóng/mở của cổng Flap Barrier!

---

## BẢNG XỬ LÝ LỖI THƯỜNG GẶP (TROUBLESHOOTING FAQS)

| Triệu chứng lỗi (Error Message) | Nguyên nhân cốt lõi | Cách khắc phục tức thì |
|---|---|---|
| **`Cannot open database "FitLifeGym" requested by the login.`** | Chưa chạy script `Database.sql` hoặc tên database trong SQL Server bị gõ sai khác với `FitLifeGym`. | Mở SSMS, mở lại file `Database.sql` và nhấn **Execute (F5)** để tạo CSDL `FitLifeGym`. |
| **`Login failed for user 'admin123'` hoặc `'sa'`** | Sai mật khẩu SQL Server, hoặc chưa kích hoạt chế độ **Mixed Mode Authentication**, hoặc user đang bị `Disabled`. | Thực hiện lại mục **2.2**: Bật Mixed Mode trong Server Properties, kích hoạt Status = Enabled cho user `sa` hoặc `admin123`, sau đó Restart lại SQL Server. |
| **`The driver could not establish a secure connection using SSL encryption.`** | SQL Server yêu cầu chứng chỉ mã hóa TLS/SSL nhưng client JDBC chưa tin cậy chứng chỉ tự ký. | Đảm bảo trong `hibernate.cfg.xml` chuỗi `connection.url` có chứa tham số: `;encrypt=true;trustServerCertificate=true;`. |
| **`The TCP/IP connection to the host localhost, port 1433 has failed.`** | Giao thức TCP/IP chưa được bật trong SQL Server Configuration Manager, hoặc dịch vụ đang chạy trên cổng khác 1433. | Mở `SQLServerManager.msc` -> Network Configuration -> Bật **TCP/IP = Enabled** -> Đặt **TCP Port = 1433** tại tab IP Addresses (mục IPAll) -> Restart dịch vụ SQL Server (Mục 2.3). |
| **`UnsupportedClassVersionError: class file has wrong version 61.0, should be 55.0`** | Đang chạy với phiên bản Java cũ (Java 8 hoặc 11), trong khi dự án yêu cầu Java 17 (Class version 61.0). | Cài đặt **JDK 17 LTS**, cấu hình biến môi trường `JAVA_HOME` trỏ tới JDK 17, hoặc trong IntelliJ IDEA chọn File -> Project Structure -> Project SDK: Java 17. |
| **Chế độ Offline hiển thị khi đăng nhập:** `(Đang chạy chế độ Offline)` | Ứng dụng không thể kết nối tới SQL Server lúc khởi động, cơ chế Fallback Cache tự động bật để chống treo ứng dụng. | Kiểm tra lại các bước kết nối CSDL ở trên. Hệ thống vẫn cho phép đăng nhập và quét thẻ offline thông qua bộ nhớ đệm cục bộ! |

---

## 📞 HỖ TRỢ KỸ THUẬT

Nếu Quý Thầy/Cô hoặc bạn đọc gặp bất kỳ khó khăn nào trong quá trình cài đặt và cấu hình, vui lòng liên hệ nhóm phát triển:
- **Nhóm Sinh Viên:** Nhóm 12 - Chuyên ngành Công nghệ Phần mềm
- **Email Hỗ Trợ:** support.fitlife@gymsystem.local
- **Dự Án:** Hệ Thống Quản Trị & Vận Hành Phòng Gym FitLife (FitLife Pro)

---
*Tài liệu được biên soạn công phu và cập nhật theo chuẩn triển khai đồ án công nghệ.*
