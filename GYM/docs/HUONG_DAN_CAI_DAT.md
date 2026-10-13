# 🚀 HƯỚNG DẪN CÀI ĐẶT & TRIỂN KHAI HỆ THỐNG
## PHẦN MỀM QUẢN TRỊ & VẬN HÀNH PHÒNG GYM FITLIFE (FITLIFE PRO)
**Đồ Án Chuyên Ngành / Cuối Kỳ Công Nghệ Thông Tin — Nhóm 12**  
*Nền tảng: Java 17 LTS • Java Swing (FlatLaf 3.4) • Hibernate ORM 6.4 • Microsoft SQL Server*

---

> [!TIP]
> **DÀNH CHO NGƯỜI CHẤM BÀI / MUỐN CHẠY NHANH:**
> 1. Mở SSMS -> Chạy file script [GYM/database/Database.sql](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/database/Database.sql) (F5).
> 2. Mở file [GYM/src/main/resources/hibernate.cfg.xml](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/resources/hibernate.cfg.xml) sửa username/password SQL Server của máy bạn.
> 3. Click đúp vào file [GYM/run.bat](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/run.bat) để khởi chạy ngay giao diện đăng nhập!

---

## 📑 MỤC LỤC CHI TIẾT
1. [Sơ Đồ Quy Trình Triển Khai 4 Bước](#1-sơ-đồ-quy-trình-triển-khai-4-bước)
2. [Yêu Cầu Môi Trường & Công Cụ Nền Tảng](#2-yêu-cầu-môi-trường--công-cụ-nền-tảng)
3. [Bước 1: Thiết Lập Cơ Sở Dữ Liệu SQL Server](#bước-1-thiết-lập-cơ-sở-dữ-liệu-sql-server)
   - 3.1. Chạy File Script Khởi Tạo `Database.sql`
   - 3.2. Bật Chế Độ Xác Thực Mixed Mode (SQL Server Authentication)
   - 3.3. Kích Hoạt Cổng Mạng TCP/IP 1433
4. [Bước 2: Cấu Hình Kết Nối Hibernate (hibernate.cfg.xml)](#bước-2-cấu-hình-kết-nối-hibernate-hibernatecfgxml)
   - 4.1. Vị Trí & Nội Dung File Cấu Hình Chuẩn
   - 4.2. Các Dòng Cần Chỉnh Sửa Phù Hợp Máy Cá Nhân
5. [Bước 3: Khởi Chạy Ứng Dụng (3 Cách Chi Tiết)](#bước-3-khởi-chạy-ứng-dụng-3-cách-chi-tiết)
   - 5.1. Cách 1: Chạy 1-Chạm Bằng File `run.bat` (Khuyên Dùng)
   - 5.2. Cách 2: Khởi Chạy Bằng Lệnh Maven CLI
   - 5.3. Cách 3: Mở & Chạy Trên IntelliJ IDEA / Eclipse / NetBeans
6. [Bước 4: Tài Khoản Kiểm Thử & Kịch Bản Demo](#bước-4-tài-khoản-kiểm-thử--kịch-bản-demo)
   - 6.1. Danh Sách 5 Tài Khoản Phân Quyền (Pass: `123456`)
   - 6.2. Kiểm Thử Use Case Lõi: Check-In Cổng Tự Động Flap Barrier
7. [Bảng Khắc Phục Lỗi Thường Gặp (Troubleshooting FAQs)](#bảng-khắc-phục-lỗi-thường-gặp-troubleshooting-faqs)

---

## 1. SƠ ĐỒ QUY TRÌNH TRIỂN KHAI 4 BƯỚC

```text
┌───────────────────────┐       ┌───────────────────────┐       ┌───────────────────────┐       ┌───────────────────────┐
│        BƯỚC 1         │       │        BƯỚC 2         │       │        BƯỚC 3         │       │        BƯỚC 4         │
│  THIẾT LẬP CSDL SQL   │ ────> │   CẤU HÌNH HIBERNATE  │ ────> │   KHỞI CHẠY APP       │ ────> │   KIỂM THỬ DEMO       │
│  Mở SSMS, F5 file     │       │   Sửa user/pass trong │       │   Chạy run.bat hoặc   │       │   Đăng nhập 5 role    │
│  Database.sql         │       │   hibernate.cfg.xml   │       │   IntelliJ IDEA       │       │   Mật khẩu: 123456    │
└───────────────────────┘       └───────────────────────┘       └───────────────────────┘       └───────────────────────┘
```

---

## 2. YÊU CẦU MÔI TRƯỜNG & CÔNG CỤ NỀN TẢNG

Trước khi cài đặt, hãy đảm bảo máy tính của bạn đã chuẩn bị sẵn các công cụ sau:

| STT | Công cụ / Phần mềm | Phiên bản khuyến nghị | Mục đích sử dụng | Link tải chính thức |
|:---:|---|---|---|---|
| **1** | **Java Development Kit** | **JDK 17 LTS** (hoặc 21) | Biên dịch & thực thi mã nguồn Java Swing, Hibernate | [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17) |
| **2** | **Microsoft SQL Server** | Bản 2019 / 2022 / Express | Hệ quản trị cơ sở dữ liệu lưu trữ dữ liệu FitLifeGym | [MS SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) |
| **3** | **SSMS** | SSMS v18.x / v19.x / v20.x | Công cụ đồ họa quản trị CSDL, chạy file `.sql` | [Tải SSMS](https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms) |
| **4** | **Apache Maven** | Maven 3.8+ (hoặc có sẵn trong IDE) | Quản lý thư viện phụ thuộc (FlatLaf, Hibernate, ZXing) | [Apache Maven](https://maven.apache.org/download.cgi) |
| **5** | **IDE Lập Trình** | IntelliJ IDEA / Eclipse | Môi trường đọc code, debug và chấm điểm | [IntelliJ IDEA](https://www.jetbrains.com/idea/) |

> [!WARNING]
> **Yêu cầu bắt buộc về Java:** Dự án sử dụng Hibernate ORM 6.4 và Java 17 LTS. Không sử dụng Java 8 hoặc Java 11 vì sẽ phát sinh lỗi `UnsupportedClassVersionError (class file version 61.0)`.

---

## BƯỚC 1: THIẾT LẬP CƠ SỞ DỮ LIỆU SQL SERVER

### 3.1. Chạy File Script Khởi Tạo `Database.sql`

1. Khởi động công cụ **SQL Server Management Studio (SSMS)**.
2. Tại cửa sổ **Connect to Server**:
   - **Server type:** `Database Engine`
   - **Server name:** `localhost` (hoặc `.\SQLEXPRESS` tùy máy của bạn)
   - **Authentication:** Chọn `Windows Authentication` rồi nhấn **Connect**.
3. Mở file script tạo CSDL của dự án:
   - Nhấn **Ctrl + O** (hoặc menu **File -> Open -> File...**).
   - Chọn file script tại đường dẫn:
     ```
     GYM/database/Database.sql
     ```
4. Nhấn phím **F5** (hoặc click nút **Execute** trên thanh công cụ).
5. Quan sát khung thông báo bên dưới: Xuất hiện dòng chữ **`Commands completed successfully.`** là CSDL đã được khởi tạo 100% thành công!

> [!NOTE]
> File [Database.sql](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/database/Database.sql) đã được chuẩn hóa tự động:
> - Tự tạo CSDL `FitLifeGym`.
> - Tự xóa bảng cũ theo đúng thứ tự an toàn khóa ngoại.
> - Tạo 8 bảng chuẩn quan hệ (`TaiKhoan`, `HoiVien`, `GoiTap`, `HuanLuyenVien`, `LopHoc`, `DangKyLop`, `LichSuCheckIn`, `HoaDon`).
> - Nạp sẵn toàn bộ dữ liệu mẫu với mật khẩu **đã được băm SHA-256 an toàn**.

---

### 3.2. Bật Chế Độ Xác Thực Mixed Mode (SQL Server Authentication)

Ứng dụng Java kết nối thông qua tài khoản Database (`username`/`password`). Bạn cần kích hoạt chế độ **Mixed Mode**:

1. Trong cửa sổ **Object Explorer** của SSMS, click chuột phải vào **Tên Server** (mục trên cùng) -> Chọn **Properties**.
2. Chọn mục **Security** ở danh mục bên trái.
3. Tại phần **Server authentication**, tích chọn:
   - 🔘 **SQL Server and Windows Authentication mode**
4. Bấm **OK**.
5. **Kích hoạt tài khoản kết nối (Chọn 1 trong 2 cách):**
   - **Cách A: Dùng user `admin123` (Trùng khớp 100% cấu hình mặc định của dự án):**
     1. Mở nhánh **Security -> Logins**.
     2. Chuột phải vào thư mục **Logins** -> Chọn **New Login...**
     3. **Login name:** Nhập `admin123`.
     4. Chọn **SQL Server authentication**, đặt mật khẩu: `123456`.
     5. Bỏ tích ô *Enforce password policy* (để tránh yêu cầu đổi pass phức tạp).
     6. Trang **Server Roles:** Tích chọn `sysadmin`.
     7. Trang **User Mapping:** Tích chọn database `FitLifeGym` và cấp quyền `db_owner`.
     8. Bấm **OK**.
   - **Cách B: Dùng tài khoản `sa` có sẵn của SQL Server:**
     1. Mở **Security -> Logins -> Chuột phải vào `sa` -> Properties**.
     2. Trang **General:** Đặt mật khẩu (ví dụ `123456`), bỏ tích *Enforce password policy*.
     3. Trang **Status:**
        - Permission to connect: Chọn **Grant**.
        - Login: Chọn **Enabled**.
     4. Bấm **OK**.
6. **Khởi động lại dịch vụ SQL Server:**
   - Chuột phải vào **Tên Server** trong SSMS -> Chọn **Restart** -> Bấm **Yes** để áp dụng thay đổi.

---

### 3.3. Kích Hoạt Cổng Mạng TCP/IP 1433

Để Hibernate có thể giao tiếp với SQL Server qua cổng JDBC nội bộ, cổng TCP/IP 1433 phải được kích hoạt:

1. Nhấn tổ hợp phím **Windows + R**, gõ lệnh: `SQLServerManager16.msc` (hoặc tìm kiếm ứng dụng **SQL Server Configuration Manager** trong Start Menu).
2. Mở nhánh: **SQL Server Network Configuration -> Protocols for MSSQLSERVER** (hoặc `Protocols for SQLEXPRESS`).
3. Nhìn sang bên phải, nếu **TCP/IP** đang là `Disabled`, click đúp và đổi thành **Enabled = Yes**.
4. Click đúp vào **TCP/IP**, chuyển sang tab **IP Addresses**:
   - Cuộn xuống dưới cùng tìm mục **IPAll**.
   - Mục **TCP Port:** Điền số **`1433`**.
   - Mục **TCP Dynamic Ports:** Xóa trống (để trống hoàn toàn).
5. Bấm **Apply** -> **OK**.
6. Chọn mục **SQL Server Services** ở danh mục bên trái -> Chuột phải vào **SQL Server (MSSQLSERVER)** -> Chọn **Restart**.

---

## BƯỚC 2: CẤU HÌNH KẾT NỐI HIBERNATE (hibernate.cfg.xml)

### 4.1. Vị Trí File Cấu Hình

File cấu hình Hibernate nằm tại:
```
GYM/src/main/resources/hibernate.cfg.xml
```

### 4.2. Nội Dung Cấu Hình & Các Dòng Cần Điều Chỉnh

Mở file [hibernate.cfg.xml](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/resources/hibernate.cfg.xml) và kiểm tra 3 thông số then chốt:

```xml
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- 1. Driver Microsoft JDBC -->
        <property name="hibernate.connection.driver_class">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>

        <!-- 2. Chuỗi kết nối JDBC (Chỉ định localhost, port 1433, CSDL FitLifeGym) -->
        <property name="hibernate.connection.url">jdbc:sqlserver://localhost:1433;databaseName=FitLifeGym;encrypt=true;trustServerCertificate=true;</property>

        <!-- 3. TÀI KHOẢN SQL SERVER: Đổi thành 'sa' hoặc 'admin123' tùy máy bạn -->
        <property name="hibernate.connection.username">admin123</property>

        <!-- 4. MẬT KHẨU SQL SERVER: Đổi thành mật khẩu tài khoản SQL trên máy bạn -->
        <property name="hibernate.connection.password">123456</property>

        <!-- 5. Dialect Hibernate 6 cho SQL Server -->
        <property name="hibernate.dialect">org.hibernate.dialect.SQLServerDialect</property>
        
        <!-- 6. Hiển thị câu lệnh SQL ra console -->
        <property name="hibernate.show_sql">true</property>

        <!-- 7. Khai báo 8 Entity ánh xạ bảng -->
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

> [!IMPORTANT]
> **2 lưu ý quan trọng khi sửa file cấu hình:**
> - Nếu bạn dùng tài khoản `sa` với pass là `123456`, hãy sửa dòng 3 thành `sa` và dòng 4 thành `123456`.
> - Tham số `;encrypt=true;trustServerCertificate=true;` ở cuối chuỗi URL là bắt buộc để tránh lỗi từ chối chứng chỉ SSL trên các phiên bản SQL Server đời mới.

---

## BƯỚC 3: KHỞI CHẠY ỨNG DỤNG (3 CÁCH CHI TIẾT)

### 5.1. Cách 1: Khởi Chạy 1-Chạm Bằng File `run.bat` (Khuyên Dùng Nhất)

Đây là cách nhanh nhất để thầy cô hoặc người dùng trải nghiệm phần mềm mà không cần cài đặt hay mở IDE nặng máy:

1. Mở thư mục `GYM`:
   ```
   c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM\
   ```
2. Click đúp chuột vào file **`run.bat`**.
3. Cửa sổ Command Prompt sẽ tự động nạp dependencies Maven, cấu hình UTF-8 tiếng Việt và bật cửa sổ đăng nhập `LoginForm` lên màn hình chỉ sau 2-3 giây!

---

### 5.2. Cách 2: Khởi Chạy Bằng Lệnh Maven CLI

Nếu bạn sử dụng Terminal / Command Prompt / PowerShell:

1. Mở PowerShell và di chuyển vào thư mục dự án `GYM`:
   ```powershell
   cd "c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM"
   ```
2. Biên dịch mã nguồn dự án:
   ```powershell
   mvn clean compile
   ```
3. Khởi chạy ứng dụng với class chính `LoginForm`:
   ```powershell
   mvn exec:java -Dexec.mainClass="com.nhom12.gym.gui.LoginForm"
   ```

---

### 5.3. Cách 3: Mở & Chạy Trên IntelliJ IDEA / Eclipse / NetBeans

Dành cho Thầy/Cô chấm điểm source code, xem cấu trúc package và debug logic:

1. Khởi động **IntelliJ IDEA**.
2. Chọn **Open** -> Điều hướng và chọn thư mục **`Đồ án cuối kì`** (hoặc thư mục con `GYM`).
3. Chờ IntelliJ đọc file `pom.xml` và tự động đồng bộ thư viện (Indexing & Sync Maven dependencies).
4. **Cấu hình JDK 17:**
   - Vào menu: **File -> Project Structure... -> Project**.
   - Tại mục **SDK**, chọn **JDK 17** (hoặc cao hơn).
   - Tại mục **Language level**, chọn **17 - Sealed types, always-strict floating-point, etc.**
   - Nhấn **Apply** -> **OK**.
5. **Chạy ứng dụng:**
   - Điều hướng trên cây thư mục mã nguồn:
     ```
     GYM -> src -> main -> java -> com -> nhom12 -> gym -> gui -> LoginForm.java
     ```
   - Click chuột phải vào file **`LoginForm.java`** -> Chọn **Run 'LoginForm.main()'** (hoặc tổ hợp phím **Shift + F10**).

---

## BƯỚC 4: TÀI KHOẢN KIỂM THỬ & KỊCH BẢN DEMO

### 6.1. Danh Sách 5 Tài Khoản Phân Quyền (RBAC)

> **Mật khẩu dùng chung cho tất cả các tài khoản demo:** **`123456`**

| Tên Đăng Nhập | Mật Khẩu | Vai Trò (Role) | Chức Năng Chính Được Trải Nghiệm |
|---|---|---|---|
| **`admin`** | **`123456`** | **Chủ Hệ Thống** | **Executive Cockpit 5 Trụ Cột:** Báo cáo doanh thu 2 cơ sở (Quận 1, Tân Bình), Biểu đồ lưu lượng Heatmap (06:00 - 22:00), Live KPI tài chính, Quản lý tài khoản, Bảo mật giá ẩn của VIP. |
| **`letan1`** | **`123456`** | **Lễ Tân** | **Front-Desk Workstation:** Trạm Check-in âm thanh Honeywell, Bán hàng POS xuất bill, Đăng ký hội viên mới, Gia hạn gói tập, Quản lý sơ đồ 20 tủ Locker, Nhắc hạn SMS/Zalo. |
| **`hlv1`** | **`123456`** | **Huấn Luyện Viên** | **Trainer Studio:** Lịch dạy các lớp Yoga/Zumba/Kickboxing, Điểm danh học viên lớp học, Sổ tay theo dõi tiến độ PT 1:1, Đo đạc và phân tích chỉ số InBody. |
| **`quanly1`** | **`123456`** | **Quản Lý Chi Nhánh** | **Operations Center:** Giám sát sức chứa phòng tập, Bảng xếp ca trực tuần cho nhân viên, Quản lý kho hàng tồn kho, Theo dõi vận hành cơ sở. |
| **`hoivien1`** | **`123456`** | **Hội Viên VIP** | **Member VIP Portal:** Thẻ điện tử hiển thị mã QR định danh, Đặt chỗ lớp học theo lịch, Xem biểu đồ thể trạng InBody, Gia hạn gói tập bằng quét mã VietQR. |

---

### 6.2. Kiểm Thử Use Case Lõi: Check-In Cổng Tự Động Flap Barrier

Sau khi đăng nhập bằng tài khoản `letan1` hoặc `admin`, mở màn hình **Trạm Quét Check-in**:
1. Con trỏ chuột tự động focus vào ô nhập mã thẻ.
2. Dùng súng quét mã vạch USB Honeywell quét thẻ QR của khách (hoặc click các nút test nhanh có sẵn trên màn hình):
   - **`QR001` (Hợp lệ):** Hệ thống phát tiếng bíp âm cao 1200Hz, cổng Flap Barrier chuyển màu **XANH LÁ (OPEN)**, hiện đầy đủ hồ sơ hội viên và ghi nhận log vào bảng bên dưới.
   - **`QR002` (Hết hạn):** Hệ thống phát âm báo lỗi 420Hz, cổng chuyển **ĐỎ (LOCKED)**, hiển thị nút màu vàng **`[ ⚡ Gia Hạn Gói Tập Ngay ]`** để gia hạn nhanh trong 5 giây!
   - **`QR003` (Bị khóa):** Cảnh báo thẻ bị tạm khóa do vi phạm nội quy.
   - **`QR004` (Sai chi nhánh):** Cảnh báo thẻ chi nhánh Tân Bình không được tập tại chi nhánh Quận 1.
   - **`VIP007` (Khách VIP Chủ Phòng):** Ẩn mức giá chiết khấu đặc biệt để bảo mật đối với nhân viên lễ tân.

---

## BẢNG KHẮC PHỤC LỖI THƯỜNG GẶP (TROUBLESHOOTING FAQS)

| Hiện tượng / Thông báo lỗi | Nguyên nhân cốt lõi | Cách khắc phục siêu nhanh (30 giây) |
|---|---|---|
| **`Cannot open database "FitLifeGym" requested by the login.`** | Chưa chạy script `Database.sql` trong SQL Server hoặc database chưa được tạo. | Mở SSMS, mở file `GYM/database/Database.sql` và nhấn **F5 (Execute)** để tạo CSDL `FitLifeGym`. |
| **`Login failed for user 'admin123'` hoặc `'sa'`** | Sai mật khẩu SQL Server, hoặc chưa bật Mixed Mode, hoặc user đang bị Disabled. | Xem lại **Mục 3.2**: Bật *SQL Server and Windows Authentication*, Enable login cho user và Restart lại SQL Server. |
| **`The driver could not establish a secure connection using SSL encryption.`** | SQL Server yêu cầu mã hóa kết nối TLS/SSL nhưng JDBC chưa tin cậy chứng chỉ. | Đảm bảo trong `hibernate.cfg.xml` chuỗi `connection.url` có chứa đuôi: `;encrypt=true;trustServerCertificate=true;`. |
| **`The TCP/IP connection to the host localhost, port 1433 has failed.`** | Giao thức TCP/IP bị tắt trong SQL Server Configuration Manager, hoặc chưa mở port 1433. | Xem lại **Mục 3.3**: Mở `SQLServerManager16.msc` -> Network Configuration -> Bật **TCP/IP = Enabled** -> Đặt **Port 1433** tại mục IPAll -> Restart dịch vụ. |
| **`UnsupportedClassVersionError: class file has wrong version 61.0, should be 55.0`** | Đang chạy với phiên bản Java 8 hoặc 11 cũ, trong khi dự án yêu cầu Java 17 (Class version 61.0). | Cài đặt **JDK 17 LTS**, cấu hình biến môi trường `JAVA_HOME` trỏ tới JDK 17, hoặc trong IntelliJ chọn *File -> Project Structure -> SDK: Java 17*. |
| **Thông báo khi mở app:** `(Đang chạy chế độ Offline)` | Ứng dụng không kết nối được tới SQL Server lúc khởi động. Bộ nhớ đệm Fallback Cache tự động bật để chống crash. | Kiểm tra lại mật khẩu SQL Server trong `hibernate.cfg.xml`. Ứng dụng vẫn cho phép đăng nhập và quét thẻ offline bình thường! |
| **Mở ứng dụng bị lỗi font chữ tiếng Việt trên console** | Console Windows mặc định dùng mã trang cũ (Code page 437/936). | Gõ lệnh `chcp 65001` trước khi chạy Maven, hoặc khởi chạy bằng file `run.bat` (đã tích hợp sẵn UTF-8). |

---

## 📞 THÔNG TIN HỖ TRỢ ĐỒ ÁN

- **Đơn Vị Thực Hiện:** Nhóm 12 — Khoa Công Nghệ Thông Tin
- **Thành Viên Nhóm:** Lê Anh Duy (Nhóm trưởng) • Võ Lê Ngọc Hưng • Võ Tấn Phát
- **Mã Nguồn GitHub:** [CK_Nhom12_HeThongQuanLyPhongGym](https://github.com/24110180-create/CK_Nhom12_HeThongQuanLyPhongGym)

---
*Chúc Quý Thầy/Cô và các bạn cài đặt thành công và có trải nghiệm vận hành tuyệt vời cùng FitLife Gym Management System!*
