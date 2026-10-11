import os
import shutil
import docx
from generate_docx_helpers import (
    add_header_footer, format_doc_title, add_heading_1, add_heading_2,
    add_paragraph, add_bullet, add_code_block, add_callout, create_table, safe_save
)

def build_installation_guide():
    doc = docx.Document()
    add_header_footer(doc, "Tài Liệu Hướng Dẫn Cài Đặt & Triển Khai")
    
    format_doc_title(
        doc,
        "HƯỚNG DẪN CÀI ĐẶT & TRIỂN KHAI HỆ THỐNG",
        "Hệ Thống Quản Trị & Vận Hành Phòng Gym FitLife (FitLife Pro) - Nhóm 12"
    )
    
    add_paragraph(
        doc,
        "Tài liệu này cung cấp hướng dẫn từng bước chi tiết giúp Hội đồng chấm thi, Giảng viên hướng dẫn và các kỹ sư triển khai có thể thiết lập cơ sở dữ liệu, cấu hình kết nối Hibernate và khởi chạy phần mềm Quản lý Phòng Gym FitLife một cách thuận lợi và chính xác 100% trên môi trường máy tính Windows.",
        bold_prefix="Mục đích tài liệu: "
    )
    
    # Section 1
    add_heading_1(doc, "1. Yêu Cầu Môi Trường & Phần Mềm")
    add_paragraph(doc, "Trước khi tiến hành triển khai, máy tính cần đáp ứng các điều kiện tiên quyết sau:")
    
    headers_req = ["Thành phần", "Phiên bản khuyến nghị", "Mục đích sử dụng"]
    rows_req = [
        ["Hệ Điều Hành", "Windows 10 / 11 (64-bit)", "Chạy ứng dụng và các file script thực thi .bat"],
        ["Java Development Kit", "JDK 17 LTS (hoặc JDK 21)", "Môi trường thực thi Java Swing & Hibernate 6.4"],
        ["Hệ quản trị CSDL", "MS SQL Server 2019 / 2022", "Lưu trữ dữ liệu quan hệ (Database: FitLifeGym)"],
        ["Công cụ quản lý DB", "SSMS 18.x / 19.x+", "Giao diện trực quan để chạy script Database.sql"],
        ["Công cụ đóng gói", "Apache Maven 3.8+", "Tải và quản lý thư viện dependencies tự động"],
        ["IDE Lập Trình", "IntelliJ IDEA / Eclipse / NetBeans", "Mở xem mã nguồn và kiểm thử chức năng"]
    ]
    create_table(doc, headers_req, rows_req, [1.5, 2.0, 3.2])
    
    add_callout(
        doc,
        "Hibernate 6.4.4 yêu cầu tối thiểu Java 17. Nếu máy tính đang dùng Java 8 hoặc 11, cần chuyển JAVA_HOME trỏ tới JDK 17 để tránh lỗi UnsupportedClassVersionError.",
        title="LƯU Ý VỀ PHIÊN BẢN JAVA"
    )
    
    # Section 2
    add_heading_1(doc, "2. Bước 1: Thiết Lập Cơ Sở Dữ Liệu SQL Server")
    add_heading_2(doc, "2.1. Mở SSMS và Chạy Script Database.sql")
    add_bullet(doc, "Khởi động SQL Server Management Studio (SSMS), chọn kết nối bằng Windows Authentication và bấm Connect.", "1. Kết nối SSMS: ")
    add_bullet(doc, "Nhấn Ctrl + O (hoặc File -> Open -> File...) và mở file Database.sql nằm ở thư mục gốc của đồ án.", "2. Mở file script: ")
    add_bullet(doc, "File script đã được thiết kế chuẩn mực theo đúng thứ tự an toàn khóa ngoại:", "3. Thứ tự thực thi script: ")
    add_bullet(doc, "Tự động kiểm tra và tạo cơ sở dữ liệu FitLifeGym.", "   • Tạo Database: ")
    add_bullet(doc, "Xóa các bảng cũ theo thứ tự phụ thuộc khóa ngoại (LichSuCheckIn -> HoaDon -> DangKyLop -> LopHoc -> HuanLuyenVien -> GoiTap -> HoiVien -> TaiKhoan) để chống xung đột.", "   • Xóa bảng cũ: ")
    add_bullet(doc, "Tạo 8 bảng dữ liệu chuẩn với đầy đủ khóa chính, khóa ngoại, ràng buộc duy nhất và giá trị mặc định.", "   • Tạo 8 bảng: ")
    add_bullet(doc, "Nạp dữ liệu mẫu gồm 5 tài khoản tương ứng 5 vai trò, các gói tập, HLV, lớp học, lịch sử check-in và hóa đơn.", "   • Nạp dữ liệu mẫu: ")
    add_bullet(doc, "Nhấn phím F5 (Execute) để chạy toàn bộ script. Khi thấy thông báo 'Commands completed successfully.' là hoàn tất.", "4. Thực thi: ")
    
    add_heading_2(doc, "2.2. Bật Chế Độ Xác Thực Mixed Mode (SQL Server Authentication)")
    add_paragraph(doc, "Để ứng dụng kết nối tới CSDL qua username/password, cần kích hoạt chế độ Mixed Mode:")
    add_bullet(doc, "Trong SSMS Object Explorer, chuột phải vào Tên Server trên cùng -> Chọn Properties.", "Bước 1: ")
    add_bullet(doc, "Chọn thẻ Security -> Tại mục Server authentication, chọn radio button: 'SQL Server and Windows Authentication mode'.", "Bước 2: ")
    add_bullet(doc, "Mở nhánh Security -> Logins trong SSMS. Có 2 cách chọn:", "Bước 3: ")
    add_bullet(doc, "Chuột phải user 'sa' -> Properties -> Đặt mật khẩu (ví dụ: 123456) -> Thẻ Status chọn Grant và Login = Enabled.", "   • Cách 1 (Dùng sa): ")
    add_bullet(doc, "Tạo New Login 'admin123', mật khẩu '123456', gán quyền sysadmin và db_owner cho FitLifeGym (Trùng khớp mặc định dự án).", "   • Cách 2 (User admin123): ")
    add_bullet(doc, "Chuột phải vào Tên Server trong SSMS -> Chọn Restart để áp dụng cài đặt.", "Bước 4: ")
    
    add_heading_2(doc, "2.3. Cấu Hình Cổng Mạng TCP/IP 1433")
    add_bullet(doc, "Nhấn Windows + R, gõ SQLServerManager16.msc (hoặc mở SQL Server Configuration Manager).", "Bước 1: ")
    add_bullet(doc, "Mở SQL Server Network Configuration -> Protocols for MSSQLSERVER (hoặc SQLEXPRESS) -> Đảm bảo TCP/IP là Enabled.", "Bước 2: ")
    add_bullet(doc, "Click đúp vào TCP/IP -> Chuyển sang tab IP Addresses -> Cuộn xuống mục IPAll dưới cùng -> Điền TCP Port = 1433, xóa trắng ô TCP Dynamic Ports.", "Bước 3: ")
    add_bullet(doc, "Khởi động lại dịch vụ SQL Server (MSSQLSERVER) để kích hoạt cổng 1433.", "Bước 4: ")
    
    # Section 3
    add_heading_1(doc, "3. Bước 2: Cấu Hình Kết Nối Hibernate (hibernate.cfg.xml)")
    add_paragraph(doc, "File cấu hình Hibernate nằm tại: GYM/src/main/resources/hibernate.cfg.xml. Dưới đây là nội dung chuẩn:")
    
    hibernate_code = """<hibernate-configuration>
    <session-factory>
        <!-- 1. Driver kết nối JDBC SQL Server -->
        <property name="hibernate.connection.driver_class">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>

        <!-- 2. Chuỗi URL kết nối: Cổng 1433, CSDL FitLifeGym, Bật TrustServerCertificate -->
        <property name="hibernate.connection.url">jdbc:sqlserver://localhost:1433;databaseName=FitLifeGym;encrypt=true;trustServerCertificate=true;</property>

        <!-- 3. Tài khoản và mật khẩu SQL Server -->
        <property name="hibernate.connection.username">admin123</property>
        <property name="hibernate.connection.password">123456</property>

        <!-- 4. Dialect & Debug SQL -->
        <property name="hibernate.dialect">org.hibernate.dialect.SQLServerDialect</property>
        <property name="hibernate.show_sql">true</property>

        <!-- 5. Khai báo 8 Entity JPA -->
        <mapping class="com.nhom12.gym.entity.HoiVien"/>
        <mapping class="com.nhom12.gym.entity.TaiKhoan"/>
        <mapping class="com.nhom12.gym.entity.GoiTap"/>
        <mapping class="com.nhom12.gym.entity.LopHoc"/>
        <mapping class="com.nhom12.gym.entity.LichSuCheckIn"/>
        <mapping class="com.nhom12.gym.entity.HoaDon"/>
        <mapping class="com.nhom12.gym.entity.HuanLuyenVien"/>
        <mapping class="com.nhom12.gym.entity.DangKyLop"/>
    </session-factory>
</hibernate-configuration>"""
    add_code_block(doc, hibernate_code)
    
    add_callout(
        doc,
        "Nếu máy tính của Thầy/Cô sử dụng tài khoản sa, chỉ cần sửa dòng username thành 'sa' và password thành mật khẩu của tài khoản sa tương ứng. Đảm bảo giữ nguyên tham số ';encrypt=true;trustServerCertificate=true;' để tránh lỗi chứng chỉ bảo mật SSL.",
        title="ĐIỀU CHỈNH USERNAME & PASSWORD"
    )
    
    # Section 4
    add_heading_1(doc, "4. Bước 3: Hướng Dẫn Build & Khởi Chạy Ứng Dụng")
    add_heading_2(doc, "4.1. Phương Pháp 1: Khởi Chạy 1 Chạm Bằng File run.bat (Khuyên Dùng)")
    add_paragraph(doc, "Tại thư mục gốc của đồ án, chỉ cần click đúp vào file run.bat:")
    add_bullet(doc, "Script tự động thiết lập bảng mã UTF-8 tiếng Việt hoàn chỉnh.", "• Tự động hóa: ")
    add_bullet(doc, "Tự động liên kết các thư viện .jar trong thư mục kho Maven nội bộ.", "• Classpath: ")
    add_bullet(doc, "Khởi chạy trực tiếp lớp giao diện chính: com.nhom12.gym.gui.LoginForm.", "• Khởi chạy: ")
    
    add_heading_2(doc, "4.2. Phương Pháp 2: Khởi Chạy Bằng Lệnh Maven")
    add_paragraph(doc, "Mở PowerShell hoặc Command Prompt tại thư mục đồ án và thực thi lệnh sau:")
    add_code_block(doc, "cd GYM\nmvn clean compile\nmvn exec:java -Dexec.mainClass=\"com.nhom12.gym.gui.LoginForm\"")
    
    add_heading_2(doc, "4.3. Phương Pháp 3: Chạy Trên IntelliJ IDEA / Eclipse")
    add_bullet(doc, "Mở thư mục đồ án bằng IntelliJ IDEA, chờ Maven nạp xong dependencies.", "Bước 1: ")
    add_bullet(doc, "Vào File -> Project Structure -> Chọn Project SDK là Java 17.", "Bước 2: ")
    add_bullet(doc, "Điều hướng tới file src/main/java/com/nhom12/gym/gui/LoginForm.java, chuột phải chọn Run 'LoginForm.main()'.", "Bước 3: ")
    
    # Section 5
    add_heading_1(doc, "5. Bước 4: Danh Sách Tài Khoản Đăng Nhập & Bộ Mã Test")
    add_paragraph(doc, "Dưới đây là 5 tài khoản mặc định ứng với 5 không gian làm việc phân quyền (Mật khẩu chung: 123456):")
    
    headers_acc = ["Tài khoản", "Mật khẩu", "Vai trò (Role)", "Không gian làm việc & Chức năng trọng tâm"]
    rows_acc = [
        ["admin", "123456", "Chủ Hệ Thống", "Executive Cockpit 5 Trụ cột, Báo cáo Doanh thu, Heatmap giờ, Live KPI"],
        ["letan1", "123456", "Lễ Tân", "Trạm Check-in âm thanh Honeywell, Bán hàng POS, Đăng ký thẻ, Tủ Locker"],
        ["hlv1", "123456", "Huấn Luyện Viên", "Trainer Studio, Lịch dạy lớp, Điểm danh Yoga/Zumba, Đo InBody, Sổ tay PT"],
        ["quanly1", "123456", "Quản Lý Chi Nhánh", "Operations Center, Giám sát sức chứa cơ sở, Phân ca trực, Quản lý kho"],
        ["hoivien1", "123456", "Hội Viên VIP", "Member Portal, Thẻ điện tử QR, Đặt chỗ lớp, Xem InBody, Gia hạn VietQR"]
    ]
    create_table(doc, headers_acc, rows_acc, [1.0, 1.0, 1.6, 3.1])
    
    add_heading_2(doc, "Bộ Mã Test Use Case Lõi Check-In Nhanh:")
    headers_test = ["Mã Quét", "Hội Viên Đại Diện", "Kết Quả Nghiệp Vụ", "Phản Hồi Hệ Thống"]
    rows_test = [
        ["QR001", "Lê Anh Duy", "HỢP LỆ (VIP Diamond)", "Bíp 1200Hz, Cổng MỞ XANH, Hiện hồ sơ chi tiết"],
        ["QR002", "Võ Lê Ngọc Hưng", "HẾT HẠN (15/10/2026)", "Cảnh báo 420Hz, Cổng KHÓA ĐỎ, Hiện nút ⚡ Gia Hạn Ngay"],
        ["QR003", "Võ Tấn Phát", "TẠM KHÓA VI PHẠM", "Cảnh báo 420Hz, Cổng KHÓA ĐỎ, Từ chối vào cửa"],
        ["QR004", "Trần Minh Quân", "SAI CHI NHÁNH ĐĂNG KÝ", "Cảnh báo 420Hz, Cổng KHÓA ĐỎ, Báo về cơ sở CN01"],
        ["VIP007", "Đỗ Minh Trí", "KHÁCH VIP CHỦ PHÒNG", "Bíp 1200Hz, Cổng MỞ XANH, Bảo mật giá ẩn với Lễ tân"]
    ]
    create_table(doc, headers_test, rows_test, [1.0, 1.8, 2.0, 2.4])
    
    # Section 6
    add_heading_1(doc, "6. Bảng Xử Lý Sự Cố Thường Gặp (Troubleshooting FAQs)")
    headers_faq = ["Thông báo lỗi", "Nguyên nhân chính", "Cách khắc phục tức thì"]
    rows_faq = [
        ["Cannot open database FitLifeGym", "Chưa chạy file Database.sql trong SSMS.", "Mở SSMS, mở file Database.sql và nhấn F5 để khởi tạo CSDL."],
        ["Login failed for user 'admin123' / 'sa'", "Sai mật khẩu hoặc chưa bật Mixed Mode.", "Bật SQL Server and Windows Authentication mode, set pass 123456, Restart SQL Server."],
        ["SSL encryption / handshake exception", "Driver JDBC đòi hỏi chứng chỉ TLS.", "Đảm bảo trong hibernate.cfg.xml có tham số ;encrypt=true;trustServerCertificate=true;"],
        ["TCP/IP port 1433 connection failed", "Cổng 1433 bị tắt trong Config Manager.", "Mở SQLServerManager, bật TCP/IP = Enabled, kiểm tra port 1433 ở IPAll, Restart dịch vụ."],
        ["UnsupportedClassVersionError (version 61.0)", "Máy đang chạy Java 8 hoặc Java 11.", "Cấu hình JAVA_HOME trỏ tới JDK 17 LTS (Class version 61.0)."],
        ["(Đang chạy chế độ Offline)", "Mất mạng CSDL lúc đăng nhập.", "Cơ chế đệm Fallback Cache tự kích hoạt để lễ tân vẫn quét thẻ được bình thường."]
    ]
    create_table(doc, headers_faq, rows_faq, [2.0, 2.0, 2.7])
    
    output_path = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\HUONG_DAN_CAI_DAT.docx"
    copy_path = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM\docs\HUONG_DAN_CAI_DAT.docx"
    safe_save(doc, output_path, copy_path)

def build_user_manual():
    doc = docx.Document()
    add_header_footer(doc, "Sổ Tay Hướng Dẫn Sử Dụng & Vận Hành")
    
    format_doc_title(
        doc,
        "SỔ TAY HƯỚNG DẪN SỬ DỤNG & VẬN HÀNH",
        "Hệ Thống Quản Trị & Vận Hành Phòng Gym FitLife (FitLife Pro) - Nhóm 12"
    )
    
    add_paragraph(
        doc,
        "Sổ tay này hướng dẫn chi tiết quy trình thao tác nghiệp vụ dành cho 5 nhóm người dùng cuối trong hệ sinh thái phòng gym: Chủ phòng tập (Admin), Nhân viên Lễ tân (Front-Desk), Huấn luyện viên (HLV/PT), Quản lý chi nhánh và Hội viên thành viên.",
        bold_prefix="Đối tượng sử dụng: "
    )
    
    # Section 1
    add_heading_1(doc, "1. Hướng Dẫn Đăng Nhập & Phân Quyền Hệ Thống")
    add_paragraph(doc, "Màn hình đăng nhập được trang bị giao diện FlatLaf cao cấp cùng cơ chế tự động nhận diện vai trò:")
    add_bullet(doc, "Mở ứng dụng qua file run.bat. Nhập Tên đăng nhập và Mật khẩu.", "Thao tác: ")
    add_bullet(doc, "Bấm Enter hoặc click nút 'ĐĂNG NHẬP HỆ THỐNG'. Hệ thống kiểm tra quyền và điều hướng đến Workstation riêng biệt.", "Xác thực: ")
    add_bullet(doc, "Nếu kết nối mạng SQL Server bị gián đoạn, hệ thống tự động kích hoạt bộ nhớ đệm cục bộ Offline Fallback, cho phép trạm lễ tân tiếp tục vận hành bình thường.", "Bảo toàn dữ liệu: ")
    
    # Section 2
    add_heading_1(doc, "2. Dành Cho Chủ Phòng Tập (Executive Command Cockpit)")
    add_paragraph(doc, "Chủ phòng tập (tài khoản: admin) sở hữu giao diện điều hành toàn cảnh với thanh menu Ribbon 2 tầng hiện đại:")
    
    add_heading_2(doc, "2.1. Năm Trụ Cột Chiến Lược (Strategic Pillars)")
    add_bullet(doc, "Báo cáo Doanh thu đa chi nhánh (2 cơ sở), Phân tích Giờ cao điểm Heatmap, Tổng quan sức chứa mặt sàn, Lịch sử hóa đơn.", "Trụ Cột 1 - Báo Cáo & Thống Kê: ")
    add_bullet(doc, "Trạm quét Check-in, Quản lý hồ sơ hội viên, Đăng ký hội viên mới, Gia hạn thẻ, Quản lý gói tập, Lịch lớp học.", "Trụ Cột 2 - Vận Hành Phòng Tập: ")
    add_bullet(doc, "Quầy bán hàng POS, Quản lý kho hàng xuất nhập tồn, Sơ đồ tủ Locker, Quản lý khuyến mãi, CSKH Zalo/SMS.", "Trụ Cột 3 - Kinh Doanh & Dịch Vụ: ")
    add_bullet(doc, "Theo dõi chỉ số sinh học InBody, Lịch phân ca dạy HLV, Sổ tay giáo án PT 1:1, Hồ sơ học viên cá nhân.", "Trụ Cột 4 - Huấn Luyện Viên: ")
    add_bullet(doc, "Quản lý nhân sự, Cấp tài khoản ứng dụng hội viên, Quản lý thiết bị máy tập, Xếp ca làm việc, Phân quyền 5 vai trò.", "Trụ Cột 5 - Quản Trị Hệ Thống: ")
    
    add_heading_2(doc, "2.2. Trung Tâm Báo Cáo Doanh Thu Đa Chi Nhánh (2 Cơ Sở)")
    add_paragraph(doc, "Cho phép Chủ phòng theo dõi dòng tiền minh bạch:")
    add_bullet(doc, "Lọc doanh thu theo ngày, tuần, tháng, quý hoặc cả năm.", "• Bộ lọc linh hoạt: ")
    add_bullet(doc, "Xem doanh thu độc lập của CN01 - Quận 1 và CN02 - Tân Bình kèm biểu đồ tỷ trọng phần trăm.", "• So sánh đa cơ sở: ")
    add_bullet(doc, "Click nút '[ Xuất Excel UTF-8 ]' để xuất file bảng tính mở trực tiếp bằng Excel với 100% tiếng Việt có dấu.", "• Xuất báo cáo: ")
    
    add_heading_2(doc, "2.3. Bản Đồ Nhiệt Lưu Lượng (Hourly Traffic Heatmap 06:00 - 22:00)")
    add_paragraph(doc, "Phân tích mật độ khách hàng theo từng khung giờ trong ngày dựa trên dữ liệu check-in:")
    add_bullet(doc, "Màu Đỏ Cam - Đỉnh điểm khách tập trước giờ làm việc công sở.", "• Khung Giờ Vàng Sáng (06:00 - 08:30): ")
    add_bullet(doc, "Màu Đỏ Đậm - Đỉnh điểm đông đúc nhất ngày, cần tối đa nhân sự sàn.", "• Khung Giờ Vàng Tối (17:30 - 20:30): ")
    add_bullet(doc, "Màu Xanh Nhạt - Thích hợp áp dụng chính sách giảm giá thu hút học sinh sinh viên.", "• Khung Giờ Thấp Điểm (09:00 - 11:30 & 14:00 - 16:00): ")
    
    # Section 3
    add_heading_1(doc, "3. Dành Cho Lễ Tân (Front-Desk Workstation)")
    add_paragraph(doc, "Lễ tân (tài khoản: letan1) chịu trách nhiệm các tác vụ có tần suất cao tại quầy tiếp đón.")
    
    add_heading_2(doc, "3.1. Use Case Cốt Lõi: Trạm Quét Check-In Tự Động")
    add_bullet(doc, "Ô nhập mã thẻ luôn được tự động focus. Tương thích trực tiếp máy quét USB Honeywell Voyager 1450g.", "Cơ chế quét: ")
    add_bullet(doc, "Hệ thống tự động kiểm tra 5 điều kiện kinh doanh trong 15ms - 35ms:", "5 Business Rules: ")
    add_bullet(doc, "Mã thẻ có tồn tại trong hệ thống CSDL?", "   • Rule 1 (Tồn tại): ")
    add_bullet(doc, "Thẻ có đang bị tạm khóa vi phạm nội quy không?", "   • Rule 2 (Khóa thẻ): ")
    add_bullet(doc, "Gói tập của hội viên còn thời hạn tính đến hôm nay?", "   • Rule 3 (Hạn dùng): ")
    add_bullet(doc, "Đúng chi nhánh đăng ký (Thẻ cơ sở chỉ tập tại chi nhánh đó; Thẻ VIP Diamond tập toàn hệ thống)?", "   • Rule 4 (Chi nhánh): ")
    add_bullet(doc, "Phòng tập trong giờ mở cửa (05:30 - 22:00) và chống quét thẻ trùng lặp trong 2 phút?", "   • Rule 5 (Giờ mở & Trùng lặp): ")
    add_bullet(doc, "Phát âm bíp cao 1200Hz, Cổng Flap Barrier chuyển XANH LÁ (OPEN), hiện đầy đủ thẻ hội viên và ghi audit log.", "Phản hồi hợp lệ: ")
    add_bullet(doc, "Phát âm cảnh báo thấp 420Hz, Cổng Flap Barrier chuyển ĐỎ (LOCKED), báo lý do từ chối cụ thể.", "Phản hồi vi phạm: ")
    add_bullet(doc, "Khi thẻ hết hạn, màn hình hiển thị ngay nút '⚡ Gia Hạn Gói Tập Ngay' để chuyển nhanh sang thu phí.", "Xử lý thông minh: ")
    
    add_heading_2(doc, "3.2. Quầy Bán Hàng POS & Thanh Toán VietQR")
    add_bullet(doc, "Click vào ảnh sản phẩm (Nước khoáng 10k, Nước tăng lực 45k, Whey isolate 65k, Khăn tập 35k, Găng tay 120k, Chuối 15k).", "Chọn hàng: ")
    add_bullet(doc, "Hệ thống tự tính tổng tiền, hỗ trợ tiền mặt (tính tiền thối) hoặc quét mã chuyển khoản VietQR tĩnh chuẩn ngân hàng.", "Thanh toán: ")
    add_bullet(doc, "Bấm 'Thanh Toán & In Hóa Đơn' để hoàn tất và lưu vết tài chính.", "In hóa đơn: ")
    
    add_heading_2(doc, "3.3. Tiếp Nhận & Đăng Ký Hội Viên Mới")
    add_bullet(doc, "Nhập họ tên, SĐT, ngày sinh, giới tính, chọn chi nhánh và gói tập mong muốn.", "Hồ sơ: ")
    add_bullet(doc, "Khung bên phải hiển thị bản phôi thẻ VIP với mã QR động thời gian thực.", "Xem trước phôi thẻ: ")
    add_bullet(doc, "Bấm 'Đăng Ký & Tạo Thẻ' -> Mở hộp thoại cho phép in thẻ, lưu ảnh thẻ PNG hoặc gửi mã QR qua Zalo/Email cho khách.", "Cấp thẻ: ")
    
    add_heading_2(doc, "3.4. Quản Lý Sơ Đồ 20 Tủ Đồ Locker Thông Minh")
    add_bullet(doc, "Màu Xanh Lá: Tủ trống. Click vào tủ -> Nhập mã hội viên -> Bấm 'Cấp Tủ Cho Khách'.", "Cấp tủ: ")
    add_bullet(doc, "Màu Xanh Dương: Tủ đang dùng. Khi khách về -> Bấm 'Thu Hồi & Mở Khóa Tủ'.", "Trả tủ: ")
    add_bullet(doc, "Màu Cam / Đỏ: Tủ đang bảo trì hoặc tạm niêm phong an ninh.", "Bảo trì: ")
    
    # Section 4
    add_heading_1(doc, "4. Dành Cho Huấn Luyện Viên (Trainer Studio)")
    add_paragraph(doc, "Huấn luyện viên (tài khoản: hlv1) sở hữu không gian làm việc chuyên biệt:")
    
    add_heading_2(doc, "4.1. Lịch Dạy Lớp Nhóm & Điểm Danh Học Viên")
    add_bullet(doc, "Xem danh sách ca dạy trong ngày (Yoga Trị Liệu, Zumba Đốt Mỡ, Kickboxing Tự Vệ, Pilates Core, HIIT Cardio).", "Ca dạy: ")
    add_bullet(doc, "Chọn lớp -> Bảng học viên đăng ký hiển thị -> Tích chọn trạng thái 'Đã điểm danh' hoặc 'Vắng mặt' -> Bấm Lưu.", "Điểm danh: ")
    
    add_heading_2(doc, "4.2. Sổ Tay Tiến Độ PT 1:1 & Kỷ Lục Tạ PR")
    add_bullet(doc, "Ghi chép số buổi tập đã hoàn thành / còn lại của từng khách hàng cá nhân.", "Theo dõi buổi: ")
    add_bullet(doc, "Ghi nhận mức tạ kỷ lục cá nhân (Personal Record - PR): Squat (kg), Bench Press (kg), Deadlift (kg).", "Kỷ lục tạ: ")
    add_bullet(doc, "Lưu ghi chú dinh dưỡng và bài tập về nhà cho từng học viên.", "Ghi chú: ")
    
    add_heading_2(doc, "4.3. Đo Đạc & Phân Tích Chỉ Số Cơ Thể InBody")
    add_bullet(doc, "Nhập Chiều cao, Cân nặng, Tỷ lệ mỡ (Body Fat %), Khối lượng cơ bắp (SMM kg), Chỉ số trao đổi chất (BMR kcal).", "Nhập thông số: ")
    add_bullet(doc, "Hệ thống tự động tính chỉ số BMI và xếp loại thể trạng khoa học (Gầy, Cân đối, Thể thao, Thừa cân, Béo phì).", "Đánh giá thể trạng: ")
    
    # Section 5
    add_heading_1(doc, "5. Dành Cho Quản Lý Chi Nhánh & Hội Viên")
    add_heading_2(doc, "5.1. Quản Lý Chi Nhánh (Tài khoản: quanly1)")
    add_bullet(doc, "Giám sát sức chứa cơ sở theo thời gian thực, cảnh báo khi mật độ vượt quá 85%.", "• Tải trọng sàn: ")
    add_bullet(doc, "Phân bổ nhân sự vào 3 ca làm việc (Sáng 05:30-13:30, Chiều 13:30-21:30, Tối kiểm kê).", "• Xếp ca trực tuần: ")
    add_bullet(doc, "Quản lý xuất nhập tồn kho thực phẩm bổ sung và nhật ký bảo trì máy móc thiết bị gym.", "• Kho & Thiết bị: ")
    
    add_heading_2(doc, "5.2. Hội Viên Thành Viên (Tài khoản: hoivien1)")
    add_bullet(doc, "Hiển thị phôi thẻ VIP với mã QR động cá nhân để đưa vào máy quét tự động mở cổng.", "• Thẻ điện tử & QR: ")
    add_bullet(doc, "Xem lịch lớp học Yoga, Zumba, Boxing và bấm 'Đặt Chỗ Ngay' để giữ chỗ.", "• Đặt chỗ lớp: ")
    add_bullet(doc, "Xem biểu đồ biến thiên chỉ số InBody qua các lần đo định kỳ.", "• Theo dõi InBody: ")
    add_bullet(doc, "Quét mã VietQR ngân hàng để gia hạn hợp đồng gói tập nhanh chóng.", "• Gia hạn online: ")
    add_bullet(doc, "Chấm điểm dịch vụ (1 - 5 sao) và gửi góp ý trực tiếp đến Chủ phòng tập.", "• Đánh giá & Góp ý: ")
    
    # Section 6
    add_heading_1(doc, "6. Bảng Tóm Tắt Phím Tắt & Thao Tác Chuẩn")
    headers_keys = ["Thao tác", "Phím tắt / Cử chỉ", "Màn hình áp dụng", "Tác dụng"]
    rows_keys = [
        ["Đăng nhập nhanh", "Phím Enter", "Màn hình LoginForm", "Kích hoạt kiểm tra đăng nhập ngay"],
        ["Quét mã thẻ", "Đưa mã vào mắt quét USB", "Màn hình CheckInForm", "Tự động nhận diện Enter và kiểm tra 5 rules"],
        ["Gia hạn nhanh", "Click [⚡ Gia Hạn Ngay]", "Màn hình CheckInForm", "Chuyển thẳng sang form gia hạn khi thẻ hết hạn"],
        ["Đóng hộp thoại", "Phím Esc hoặc nút Hủy", "Các dialog In thẻ, Gửi QR", "Đóng cửa sổ phụ an toàn"],
        ["Xuất dữ liệu", "Click [Xuất Excel UTF-8]", "Báo cáo, Danh sách HV", "Lưu file Excel chuẩn tiếng Việt không lỗi font"],
        ["Làm mới màn hình", "Click nút [Làm Mới]", "Các màn hình bảng biểu", "Đồng bộ dữ liệu mới nhất từ cơ sở dữ liệu"]
    ]
    create_table(doc, headers_keys, rows_keys, [1.3, 1.8, 1.8, 2.3])
    
    output_path = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\USER_MANUAL.docx"
    copy_path = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM\docs\USER_MANUAL.docx"
    safe_save(doc, output_path, copy_path)

def build_task_assignment():
    doc = docx.Document()
    add_header_footer(doc, "Bảng Phân Công Nhiệm Vụ Đồ Án")
    
    format_doc_title(
        doc,
        "BẢNG PHÂN CÔNG NHIỆM VỤ & ĐÁNH GIÁ ĐÓNG GÓP",
        "Hệ Thống Quản Trị & Vận Hành Phòng Gym FitLife (FitLife Pro) - Nhóm 12"
    )
    
    add_paragraph(
        doc,
        "Đồ án tốt nghiệp / Cuối kỳ: Phần mềm Quản trị & Vận hành Phòng Gym FitLife (FitLife Gym Management System). Báo cáo phân công công việc chi tiết và mức độ đóng góp của từng thành viên trong Nhóm 12.",
        bold_prefix="Thông tin đề tài: "
    )
    
    # 1. Bảng tổng hợp
    add_heading_1(doc, "1. Bảng Tổng Hợp Phân Công Nhiệm Vụ & Đánh Giá Đóng Góp")
    headers = ["Thành viên", "Vai trò", "Nhiệm vụ chính phụ trách", "Đóng góp"]
    rows = [
        [
            "Lê Anh Duy",
            "Nhóm trưởng",
            "• Quản lý tiến độ, thiết kế CSDL (Database.sql) và Hibernate.\n• Phát triển Module Xác thực, Phân quyền & Khung MainDashboard.\n• Xây dựng chức năng Check-in hội viên & Log ra/vào.",
            "100%"
        ],
        [
            "Võ Lê Ngọc Hưng",
            "Thành viên",
            "• Module Quản lý Hội viên (đăng ký, gia hạn, hồ sơ chi tiết).\n• Module Quản lý Huấn luyện viên & Phân ca nhân sự.\n• Module Lịch lớp học & Huấn luyện (PT).",
            "100%"
        ],
        [
            "Võ Tấn Phát",
            "Thành viên",
            "• Module Điểm bán hàng POS, Kho hàng & Hóa đơn.\n• Module Báo cáo doanh thu & Biểu đồ nhiệt (Heatmap).\n• Tích hợp tiện ích: Xuất Excel, sinh mã QR & Âm thanh.",
            "100%"
        ]
    ]
    create_table(doc, headers, rows, [1.5, 1.2, 3.8, 0.9])
    
    # 2. Chi tiết công việc
    add_heading_1(doc, "2. Chi Tiết Khối Lượng Công Việc Của Từng Thành Viên")
    
    add_heading_2(doc, "2.1. Lê Anh Duy — Nhóm Trưởng (Đóng góp: 100%)")
    add_bullet(doc, "Lập kế hoạch phân bổ công việc, điều phối tiến độ, kiểm soát chất lượng mã nguồn.", "• Quản lý tiến độ: ")
    add_bullet(doc, "Xây dựng Database.sql (8 bảng chuẩn quan hệ), cấu hình Hibernate ORM 6.4.4 (hibernate.cfg.xml, HibernateUtil).", "• Thiết kế CSDL & ORM: ")
    add_bullet(doc, "Màn hình LoginForm, dịch vụ AuthService phân quyền 5 vai trò (Chủ, Lễ tân, HLV, Quản lý, Hội viên), cơ chế Offline Fallback Buffer.", "• Xác thực & Phân quyền: ")
    add_bullet(doc, "Khung MainDashboard với thanh menu 2 tầng Ribbon 5 Trụ cột chiến lược và không gian nhúng đa vai trò.", "• Khung điều hành: ")
    add_bullet(doc, "Use case lõi CheckInForm & CheckInService: kiểm tra 5 Business Rules, âm thanh PCM 1200Hz/420Hz, cổng Flap Barrier, tối ưu hiệu năng 15-35ms, chính sách ẩn giá VIP Anh Đức, Audit Log thời gian thực.", "• Use Case Check-in: ")
    
    add_heading_2(doc, "2.2. Võ Lê Ngọc Hưng — Thành Viên (Đóng góp: 100%)")
    add_bullet(doc, "Xây dựng DangKyHoiVienPanel (xem trước phôi thẻ VIP, tính giá, voucher), GiaHanGoiTapPanel (cộng dồn hạn thông minh), QuanLyHoiVienPanel, ChiTietHoiVienPanel và HoiVienDAO.", "• Quản lý Hội viên: ")
    add_bullet(doc, "Xây dựng QuanLyNhanSuPanel, HuanLuyenVienDAO (danh bạ HLV, kinh nghiệm, chuyên môn, sao đánh giá), PhanCaLamViecPanel (xếp ca trực tuần).", "• Huấn luyện viên & Phân ca: ")
    add_bullet(doc, "Xây dựng LichLopHocPanel, LichDayHLVPanel, Hệ thống Điểm Danh Lớp Học (Có mặt / Vắng mặt), DangKyLopDAO, GhiChuTienDoPTPanel (sổ tay PT 1:1, kỷ lục tạ cá nhân Squat/Bench/Deadlift PR).", "• Lớp học & Huấn luyện PT: ")
    
    add_heading_2(doc, "2.3. Võ Tấn Phát — Thành Viên (Đóng góp: 100%)")
    add_bullet(doc, "Xây dựng QuanLyGoiTapPanel (danh mục gói tập, chính sách giá, thời hạn), HoaDonDAO và LichSuGiaoDichPanel (quản lý giao dịch thu ngân, VietQR).", "• Gói tập & Hóa đơn: ")
    add_bullet(doc, "Xây dựng BaoCaoThongKePanel & BaoCaoService (Doanh thu 2 chi nhánh CN01, CN02; Cảnh báo hội viên sắp hết hạn kèm SMS/Zalo), HeatmapGioCaoDiemPanel (biểu đồ nhiệt lưu lượng 06:00 - 22:00, 2 khung giờ vàng).", "• Báo cáo & Heatmap: ")
    add_bullet(doc, "Xây dựng ExcelExportHelper (xuất Excel/CSV UTF-8 BOM chuẩn tiếng Việt 100%), SoundHelper (sóng âm PCM 1200Hz/420Hz mô phỏng máy quét Honeywell), ImageHelper & GuiMaQRDialog (sinh mã QR định danh và xuất ảnh thẻ PNG).", "• Bộ tiện ích hệ thống: ")
    
    # 3. RACI Matrix
    add_heading_1(doc, "3. Ma Trận Phân Chia Trách Nhiệm (RACI Matrix)")
    headers_raci = ["Phân hệ chức năng", "Lê Anh Duy", "Võ Lê Ngọc Hưng", "Võ Tấn Phát"]
    rows_raci = [
        ["Thiết kế CSDL & Hibernate ORM", "Responsible (R)", "Consulted (C)", "Consulted (C)"],
        ["Xác thực, Phân quyền & LoginForm", "Responsible (R)", "Consulted (C)", "Consulted (C)"],
        ["Khung điều hành MainDashboard (5 Trụ cột)", "Responsible (R)", "Support (S)", "Support (S)"],
        ["Use Case Lõi: Check-in, Flap Barrier & Log", "Responsible (R)", "Consulted (C)", "Consulted (C)"],
        ["Quản lý Hội viên (Đăng ký, Gia hạn, Hồ sơ)", "Consulted (C)", "Responsible (R)", "Support (S)"],
        ["Quản lý Huấn luyện viên & Phân ca trực", "Consulted (C)", "Responsible (R)", "Consulted (C)"],
        ["Lịch lớp học & Hệ thống Điểm danh lớp", "Consulted (C)", "Responsible (R)", "Consulted (C)"],
        ["Sổ tay PT 1:1 & Theo dõi chỉ số InBody", "Consulted (C)", "Responsible (R)", "Support (S)"],
        ["Quản lý Gói tập & Hóa đơn giao dịch", "Consulted (C)", "Support (S)", "Responsible (R)"],
        ["Báo cáo Doanh thu & Cảnh báo hết hạn", "Consulted (C)", "Consulted (C)", "Responsible (R)"],
        ["Biểu đồ nhiệt Giờ cao điểm (Heatmap)", "Consulted (C)", "Consulted (C)", "Responsible (R)"],
        ["Tiện ích: Xuất Excel UTF-8, Mã QR, Âm thanh", "Support (S)", "Consulted (C)", "Responsible (R)"]
    ]
    create_table(doc, headers_raci, rows_raci, [3.2, 1.4, 1.4, 1.4])
    
    # 4. Ký tên
    add_heading_1(doc, "4. Xác Nhận Của Các Thành Viên Trong Nhóm")
    add_paragraph(doc, "Các thành viên cam kết nội dung phân công phản ánh trung thực và khách quan quá trình thực hiện đồ án.")
    
    headers_sign = ["Nhóm Trưởng", "Thành Viên", "Thành Viên"]
    rows_sign = [
        ["(Ký và ghi rõ họ tên)\n\n\n\nLê Anh Duy", "(Ký và ghi rõ họ tên)\n\n\n\nVõ Lê Ngọc Hưng", "(Ký và ghi rõ họ tên)\n\n\n\nVõ Tấn Phát"]
    ]
    create_table(doc, headers_sign, rows_sign, [2.4, 2.5, 2.5])
    
    output_path = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\PHAN_CONG_NHIEM_VU.docx"
    copy_path = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM\docs\PHAN_CONG_NHIEM_VU.docx"
    safe_save(doc, output_path, copy_path)

from build_architecture_doc import build_architecture_doc, build_project_report_doc

if __name__ == "__main__":
    build_installation_guide()
    build_user_manual()
    build_task_assignment()
    build_architecture_doc()
    build_project_report_doc()
    print("ALL DOCUMENTS GENERATED SUCCESSFULLY!")


