# 📖 SỔ TAY HƯỚNG DẪN SỬ DỤNG & VẬN HÀNH HỆ THỐNG
## HỆ THỐNG QUẢN TRỊ PHÒNG TẬP FITLIFE GYM (FITLIFE PRO)
**Đồ án Chuyên ngành / Tốt nghiệp Công nghệ Thông tin - Nhóm 12**  
*Dành cho: Chủ phòng tập, Ban quản lý, Lễ tân, Huấn luyện viên (PT) và Hội viên*

---

## 📑 MỤC LỤC
1. [Giới Thiệu & Đăng Nhập Hệ Thống](#1-giới-thiệu--đăng-nhập-hệ-thống)
2. [Hướng Dẫn Dành Cho Chủ Phòng Tập (Executive Command Cockpit)](#2-hướng-dẫn-dành-cho-chủ-phòng-tập-executive-command-cockpit)
   - 2.1. Thanh Điều Hành 2 Tầng Ribbon & Dải Live KPI
   - 2.2. Báo Cáo Doanh Thu Đa Chi Nhánh (2 Cơ Sở)
   - 2.3. Bản Đồ Nhiệt Mật Độ Giờ Cao Điểm (Heatmap 06:00 - 22:00)
   - 2.4. Chính Sách Khách VIP Ẩn Giá
3. [Hướng Dẫn Dành Cho Lễ Tân (Front-Desk Workstation)](#3-hướng-dẫn-dành-cho-lễ-tân-front-desk-workstation)
   - 3.1. Use Case Lõi: Trạm Quét Check-In & Cổng Tự Động Flap Barrier
   - 3.2. Quầy Bán Hàng POS & Thanh Toán VietQR
   - 3.3. Tiếp Nhận & Đăng Ký Hội Viên Mới
   - 3.4. Gia Hạn Gói Tập & Áp Dụng Voucher Chiết Khấu
   - 3.5. Quản Lý Sơ Đồ 20 Tủ Đồ Thông Minh (Locker)
   - 3.6. Chăm Sóc Khách Hàng & Gửi Tin Nhắn Nhắc Hạn
4. [Hướng Dẫn Dành Cho Huấn Luyện Viên (Trainer Studio)](#4-hướng-dẫn-dành-cho-huấn-luyện-viên-trainer-studio)
   - 4.1. Xem Lịch Dạy Ca & Điểm Danh Học Viên Lớp Nhóm
   - 4.2. Sổ Tay Tiến Độ PT 1:1 & Kỷ Lục Tạ Cá Nhân (PR)
   - 4.3. Đo Đạc & Phân Tích Chỉ Số Cơ Thể InBody
5. [Hướng Dẫn Dành Cho Quản Lý Chi Nhánh (Operations Center)](#5-hướng-dẫn-dành-cho-quản-lý-chi-nhánh-operations-center)
   - 5.1. Giám Sát Sức Chứa Phòng Tập & Tỷ Lệ Lấp Đầy
   - 5.2. Bảng Xếp Ca Trực Tuần Cho Nhân Sự
   - 5.3. Quản Lý Kho Hàng & Vật Phẩm Tồn Kho
6. [Hướng Dẫn Dành Cho Hội Viên (Member VIP Portal)](#6-hướng-dẫn-dành-cho-hội-viên-member-vip-portal)
   - 6.1. Thẻ Điện Tử & Check-In QR Cá Nhân
   - 6.2. Đặt Chỗ Lớp Học Theo Thời Khóa Biểu
   - 6.3. Theo Dõi Biến Thiên Thể Trạng InBody
   - 6.4. Đánh Giá Chất Lượng Dịch Vụ & Gửi Góp Ý
7. [Tổng Hợp Phím Tắt & Quy Tắc Vận Hành Chuẩn](#7-tổng-hợp-phím-tắt--quy-tắc-vận-hành-chuẩn)

---

## 1. GIỚI THIỆU & ĐĂNG NHẬP HỆ THỐNG

### 1.1. Khởi Động Phần Mềm
- Khởi động ứng dụng bằng cách nhấp đúp vào file `run.bat` ở thư mục gốc của dự án hoặc khởi chạy class `LoginForm.java` từ IDE.
- Màn hình đăng nhập hiển thị giao diện đồ họa hiện đại với hai phân vùng:
  - **Phân vùng bên trái:** Hình ảnh thương hiệu phòng gym cao cấp và cam kết chất lượng của FitLife.
  - **Phân vùng bên phải:** Khung nhập thông tin tài khoản, mật khẩu và trạng thái kết nối máy chủ.

### 1.2. Thao Tác Đăng Nhập
1. Nhập **Tên đăng nhập** vào ô tài khoản.
2. Nhập **Mật khẩu** vào ô mật khẩu.
3. Nhấn phím **Enter** trên bàn phím hoặc click nút **ĐĂNG NHẬP HỆ THỐNG**.
4. Hệ thống sẽ xác thực thông tin với cơ sở dữ liệu SQL Server:
   - **Đăng nhập thành công:** Hiển thị lời chào mừng cá nhân hóa kèm vai trò được cấp và tự động chuyển vào không gian làm việc chuyên biệt tương ứng.
   - **Chế độ Offline:** Nếu máy chủ cơ sở dữ liệu tạm thời ngắt kết nối, hệ thống sẽ kích hoạt bộ nhớ đệm an toàn và hiển thị thông báo `(Đang chạy chế độ Offline)` để không làm gián đoạn việc quét thẻ ra vào của khách hàng!

---

## 2. HƯỚNG DẪN DÀNH CHO CHỦ PHÒNG TẬP (EXECUTIVE COMMAND COCKPIT)
*(Tài khoản: `admin` | Mật khẩu: `123456`)*

Giao diện của Chủ phòng tập được thiết kế theo phong cách **Executive Command Cockpit** cao cấp, trao quyền điều hành toàn cảnh hệ thống chuỗi phòng gym.

### 2.1. Thanh Điều Hành 2 Tầng Ribbon & Dải Live KPI
- **Tầng 1 (5 Trụ Cột Chiến Lược):**
  - **Trụ cột 1: Báo Cáo & Thống Kê:** Trung tâm dữ liệu tài chính, biểu đồ lưu lượng và lịch sử giao dịch.
  - **Trụ cột 2: Vận Hành Phòng Tập:** Trạm kiểm soát check-in, quản lý danh bạ hội viên, hợp đồng và lịch lớp.
  - **Trụ cột 3: Kinh Doanh & Dịch Vụ:** Quầy bán hàng POS, quản lý kho hàng, sơ đồ tủ locker và CSKH.
  - **Trụ cột 4: Huấn Luyện Viên:** Quản lý chỉ số InBody, sổ tay PT 1:1 và hồ sơ học viên.
  - **Trụ cột 5: Quản Trị Hệ Thống:** Quản lý nhân sự, phân ca trực, tài khoản app và phân quyền bảo mật.
- **Tầng 2 (Dải Module Nhanh):** Hiển thị các tính năng chi tiết thuộc trụ cột đang chọn, cho phép chuyển đổi tức thì chỉ với một cú nhấp chuột.
- **Dải Live KPI Thời Gian Thực:**
  - **Doanh Số Hôm Nay:** Tổng số tiền thu được từ đăng ký gói, gia hạn và bán lẻ POS trong ngày.
  - **Hội Viên Đang Tập:** Tỷ lệ lấp đầy mặt sàn (ví dụ: `142/200 khách`).
  - **Lượt Check-in:** Tổng số lượt quét thẻ hợp lệ qua cửa kiểm soát trong ngày.

---

### 2.2. Báo Cáo Doanh Thu Đa Chi Nhánh (2 Cơ Sở)
1. Chọn **Trụ cột Báo Cáo & Thống Kê** -> Click chọn module **Báo Cáo Doanh Thu**.
2. **Bộ Lọc Báo Cáo:**
   - Chọn chu kỳ xem: *Hôm nay*, *Tuần này*, *Tháng hiện tại*, *Quý này*, hoặc *Cả năm*.
   - Chọn phạm vi: *Toàn hệ thống* hoặc từng cơ sở cụ thể (*CN01 - Quận 1*, *CN02 - Tân Bình*).
3. **Biểu Đồ So Sánh:** Quan sát tỷ trọng đóng góp doanh thu giữa các chi nhánh được trực quan hóa bằng các thanh đo màu sắc chuyên nghiệp.
4. **Xuất Báo Cáo Ra Excel:**
   - Nhấn nút **`[ 📊 Xuất Excel UTF-8 ]`**.
   - Chọn thư mục lưu file. Hệ thống sử dụng engine UTF-8 BOM độc quyền, đảm bảo mở trực tiếp bằng Microsoft Excel với 100% tiếng Việt có dấu hoàn hảo.

---

### 2.3. Bản Đồ Nhiệt Mật Độ Giờ Cao Điểm (Heatmap 06:00 - 22:00)
1. Trong menu Báo Cáo, chọn module **Phân Tích Giờ Cao Điểm**.
2. Hệ thống phân tích toàn bộ lịch sử quét thẻ check-in và thể hiện mật độ khách hàng theo từng khung giờ từ **06:00 đến 22:00**:
   - **Khung Giờ Vàng Sáng (06:00 - 08:30):** Cao điểm người đi làm tập buổi sáng (Màu Đỏ Cam).
   - **Khung Giờ Trưa (11:30 - 13:30):** Mật độ trung bình (Màu Vàng).
   - **Khung Giờ Vàng Tối (17:30 - 20:30):** Đỉnh điểm đông đúc nhất ngày (Màu Đỏ Đậm - Yêu cầu tăng cường HLV và nhân viên sàn tập).
   - **Khung Giờ Thấp Điểm (09:00 - 11:00 & 14:00 - 16:00):** Thích hợp triển khai các gói khuyến mãi giảm giá giờ thấp điểm (*Gói Sinh Viên GT06*).

---

### 2.4. Chính Sách Khách VIP Ẩn Giá (Yêu Cầu Đặc Thù)
- Hệ thống hỗ trợ thiết lập các mã khách đặc biệt (ví dụ: `VIP007` - Khách của Chủ phòng tập).
- Khi nhân viên lễ tân quét thẻ `VIP007`, màn hình lễ tân chỉ hiển thị thông tin thẻ tiêu chuẩn kèm ghi chú: *"Khách đặc biệt của Chủ phòng (Giá thực tế được bảo mật với Lễ tân)"*.
- Mức giá giảm 50% hoặc miễn phí của khách hàng này sẽ được bảo mật tuyệt đối, chỉ hiển thị chính xác trong báo cáo doanh thu tài chính khi tài khoản `admin` đăng nhập!

---

## 3. HƯỚNG DẪN DÀNH CHO LỄ TÂN (FRONT-DESK WORKSTATION)
*(Tài khoản: `letan1` | Mật khẩu: `123456`)*

Lễ tân là trạm vận hành tiền tuyến với các tác vụ có tần suất hoạt động cao nhất trong ngày.

### 3.1. Use Case Lõi: Trạm Quét Check-In & Cổng Tự Động Flap Barrier

Trạm quét được tối ưu hóa cho tốc độ phản hồi cực nhanh (**15ms - 35ms**), tương thích tuyệt đối với máy quét mã vạch USB Honeywell Voyager 1450g.

```
       [ HỘI VIÊN ĐƯA THẺ / MÃ QR ]
                    │
                    ▼
       [ MÁY QUÉT HONEYWELL USB ]
                    │
                    ▼
     [ HỆ THỐNG KIỂM TRA 5 RULES ]
     ├── 1. Mã thẻ có tồn tại?
     ├── 2. Thẻ có bị tạm khóa?
     ├── 3. Gói tập còn thời hạn?
     ├── 4. Đúng chi nhánh đăng ký?
     └── 5. Trong giờ mở cửa & không quét trùng?
                    │
         ┌──────────┴──────────┐
         ▼                     ▼
   [ ĐỦ 5 RULES ]        [ VI PHẠM RULE ]
         │                     │
   - Âm bíp cao 1200Hz   - Âm cảnh báo 420Hz
   - Cổng Flap MỞ XANH   - Cổng Flap KHÓA ĐỎ
   - Chào mừng hội viên  - Hiện nguyên nhân
                         - Hiện nút [Gia Hạn]
```

#### Quy Trình Quét Thẻ Tiêu Chuẩn:
1. Con trỏ chuột luôn được **tự động focus** vào ô nhập mã thẻ.
2. Hội viên đưa thẻ vật lý hoặc mã QR trên ứng dụng điện thoại vào trước mắt đọc của máy quét USB.
3. Máy quét tự động đọc chuỗi ký tự và gửi tín hiệu phím `Enter` kết thúc.
4. Hệ thống tức thì đối soát **5 Business Rules**:
   - **Thành công:** Phát âm thanh bíp tần số 1200Hz trong trẻo, biểu tượng cổng chuyển màu **XANH LÁ (OPEN)**, hiển thị toàn bộ hồ sơ hội viên và tự động ghi nhật ký vào bảng Audit Log bên dưới.
   - **Thất bại:** Phát âm cảnh báo tần số 420Hz, biểu tượng cổng chuyển màu **ĐỎ (LOCKED)**, thông báo rõ nguyên nhân (Hết hạn / Khóa thẻ / Sai chi nhánh).
5. **Xử lý nhanh khi thẻ hết hạn:** Nếu khách quét thẻ đã hết hạn, hệ thống hiển thị ngay nút nổi bật **`[ ⚡ Gia Hạn Gói Tập Ngay ]`**. Lễ tân chỉ cần click vào nút này để mở thẳng form gia hạn mà không cần nhập lại thông tin khách hàng!

---

### 3.2. Quầy Bán Hàng POS & Thanh Toán VietQR
1. Trên thanh menu bên trái, chọn **Quầy Bán Hàng POS**.
2. **Chọn Sản Phẩm:** Nhấp chuột vào hình ảnh các mặt hàng thật hiển thị trên kệ hàng:
   - *Nước Khoáng Tinh Khiết* (10,000 đ)
   - *Nước Tăng Lực Thể Thao* (45,000 đ)
   - *Whey Protein Isolate* (65,000 đ)
   - *Khăn Tập Microfiber* (35,000 đ)
   - *Găng Tay Tập Tạ Da Bò* (120,000 đ)
   - *Chuối Tươi Nạp Năng Lượng* (15,000 đ)
3. Sản phẩm đã chọn sẽ được cộng vào bảng Hóa đơn bên phải, tự động tính tổng tiền.
4. **Phương Thức Thanh Toán:**
   - **Tiền mặt:** Nhập số tiền khách đưa, hệ thống tự động tính tiền thối lại cho khách.
   - **Chuyển khoản VietQR:** Bấm nút chuyển khoản, màn hình sẽ hiển thị mã VietQR tiêu chuẩn ngân hàng để khách hàng quét app chuyển tiền tức thì.
5. Bấm nút **`[ 🖨️ Thanh Toán & In Hóa Đơn ]`** để hoàn tất đơn hàng và lưu dữ liệu vào hệ thống.

---

### 3.3. Tiếp Nhận & Đăng Ký Hội Viên Mới
1. Chọn menu **Đăng Ký Hội Viên Mới**.
2. **Nhập Thông Tin Cá Nhân:**
   - Họ và tên khách hàng (ví dụ: *Nguyễn Văn An*).
   - Số điện thoại liên hệ (10 chữ số).
   - Ngày sinh & Giới tính (Nam/Nữ).
   - Chi nhánh đăng ký thường trực (*CN01 - Quận 1*).
3. **Lựa Chọn Gói Dịch Vụ:**
   - Chọn loại gói: *1 Tháng Tiêu Chuẩn*, *3 Tháng*, *6 Tháng*, *12 Tháng VIP Diamond*, hoặc *Gói PT 1:1*.
   - Hệ thống tự động tính ngày hết hạn dựa theo thời lượng của gói.
   - Chọn Huấn luyện viên cá nhân (nếu đăng ký kèm PT).
   - Nhập mã giảm giá Voucher (nếu có, ví dụ: `FITLIFE10` để giảm 10%).
4. **Tùy Chọn Cấp Tài Khoản App:** Tích chọn ô *"Tạo tài khoản Cổng Hội Viên"* để sinh ngay tên đăng nhập và mật khẩu cho khách.
5. **Xem Trước Phôi Thẻ & Kích Hoạt:**
   - Khung bên phải hiển thị bản demo thẻ thành viên cao cấp thời gian thực.
   - Bấm nút **`[ 💾 Đăng Ký & Tạo Thẻ ]`**.
   - Hộp thoại **Gửi Mã QR & Thẻ Điện Tử** xuất hiện: Cho phép Lễ tân *Lưu ảnh thẻ PNG*, *Gửi mã QR qua Zalo/Email* hoặc *In phôi thẻ* cho khách mang về.

---

### 3.4. Gia Hạn Gói Tập & Áp Dụng Voucher
1. Chọn menu **Gia Hạn Gói Tập**.
2. Nhập Mã hội viên hoặc Số điện thoại để tìm kiếm hồ sơ.
3. Hệ thống hiển thị gói tập hiện tại và ngày hết hạn cũ.
4. Chọn gói tập gia hạn mới:
   - **Quy tắc cộng dồn thông minh:** Nếu thẻ của khách *vẫn còn hạn*, hệ thống sẽ tự động lấy ngày hết hạn cũ cộng thêm thời hạn gói mới (không làm mất ngày tập còn lại của khách).
   - Nếu thẻ *đã quá hạn*, ngày hết hạn mới sẽ được tính bắt đầu từ ngày hôm nay.
5. Chọn hình thức thanh toán và bấm **`[ Xác Nhận Gia Hạn ]`**.

---

### 3.5. Quản Lý Sơ Đồ 20 Tủ Đồ Thông Minh (Locker)
1. Chọn menu **Quản Lý Tủ Đồ Locker**.
2. Màn hình mô phỏng trực quan sơ đồ 20 tủ đồ cá nhân:
   - 🟩 **Màu Xanh Lá (Trống):** Tủ đang sẵn sàng giao cho khách.
   - 🟦 **Màu Xanh Dương (Đang Sử Dụng):** Tủ đang có đồ của khách (hiển thị kèm mã thẻ và tên khách).
   - 🟧 **Màu Cam / Đỏ (Bảo Trì / Khóa):** Tủ đang hư hỏng hoặc đang niêm phong.
3. **Thao tác cấp tủ:** Click vào tủ màu xanh lá -> Nhập mã hội viên -> Bấm **Cấp Tủ Cho Khách**.
4. **Thao tác trả tủ:** Khi khách ra về, click vào tủ đang sử dụng -> Bấm **Thu Hồi & Mở Khóa Tủ**.

---

### 3.6. Chăm Sóc Khách Hàng & Gửi Tin Nhắn Nhắc Hạn
1. Chọn menu **Chăm Sóc Khách Hàng**.
2. Bấm lọc danh sách **Hội viên sắp hết hạn trong 7 ngày tới**.
3. Chọn hội viên cần liên hệ trong danh sách.
4. Bấm nút **`[ 📲 Gửi Zalo ZNS / SMS Tự Động ]`**:
   - Hệ thống tự động điền mẫu tin nhắn chuẩn thương hiệu:  
     *“FitLife Gym xin chào anh/chị [Tên_HV], gói tập [Tên_Gói] của anh/chị sẽ hết hạn vào ngày [Ngày_Hết_Hạn]. Kính mời anh/chị ghé quầy lễ tân để nhận ưu đãi giảm 10% khi gia hạn sớm!”*
5. Bấm **Gửi Tin Nhắn** để hoàn tất.

---

## 4. HƯỚNG DẪN DÀNH CHO HUẤN LUYỆN VIÊN (TRAINER STUDIO)
*(Tài khoản: `hlv1` | Mật khẩu: `123456`)*

Huấn luyện viên sở hữu không gian làm việc chuyên sâu phục vụ công tác giảng dạy và theo dõi chỉ số học viên.

### 4.1. Xem Lịch Dạy Ca & Điểm Danh Học Viên Lớp Nhóm
1. Đăng nhập tài khoản HLV, hệ thống tự động mở màn hình **Lịch Dạy & Điểm Danh**.
2. **Xem Ca Dạy Trong Tuần:**
   - Lớp Yoga Trị Liệu Cột Sống (06:30 - 07:30, T2-T4-T6, Studio 1).
   - Lớp Zumba Cardio Đốt Mỡ (18:00 - 19:00, T3-T5-T7, Studio 2).
   - Lớp Kickboxing Nâng Cao (19:15 - 20:15, Ring A).
3. **Thực Hiện Điểm Danh Lớp:**
   - Chọn lớp học đang diễn ra. Bảng danh sách học viên đăng ký sẽ hiển thị.
   - Nhấp vào cột trạng thái để chuyển đổi: **Đã điểm danh (Có mặt)** hoặc **Vắng mặt**.
   - Bấm **`[ 💾 Lưu Kết Quả Điểm Danh ]`** để hệ thống chốt sĩ số lớp.

---

### 4.2. Sổ Tay Tiến Độ PT 1:1 & Kỷ Lục Tạ Cá Nhân (PR)
1. Chọn menu **Sổ Tay Tiến Độ PT**.
2. Chọn học viên kèm riêng trong danh bạ.
3. Xem thông tin hợp đồng: *Tổng số buổi đăng ký*, *Số buổi đã hoàn thành*, *Số buổi còn lại*.
4. **Ghi Nhận Kỷ Lục Tạ Cá Nhân (Personal Record - PR):**
   - Mức tạ Squat tối đa (ví dụ: `110 kg`).
   - Mức tạ Bench Press ngực (ví dụ: `85 kg`).
   - Mức tạ Deadlift kéo lưng (ví dụ: `135 kg`).
5. Nhập ghi chú giáo án của buổi tập hôm nay (ví dụ: *"Học viên hoàn thành tốt bài Dropset cơ đùi trước, cần chú ý siết cơ bụng khi thở ra"*).
6. Bấm **Lưu Tiến Độ Buổi Tập**.

---

### 4.3. Đo Đạc & Phân Tích Chỉ Số Cơ Thể InBody
1. Chọn menu **Đo Đạc Chỉ Số InBody**.
2. Tìm kiếm học viên cần đo chỉ số.
3. Nhập các thông số đọc được từ máy đo sinh học InBody:
   - Chiều cao (cm) và Cân nặng (kg).
   - Tỷ lệ mỡ cơ thể (**Body Fat %**).
   - Khối lượng cơ bắp (**Skeletal Muscle Mass - SMM**).
   - Lượng nước cơ thể và Chỉ số trao đổi chất cơ bản (**BMR** kcal).
4. Hệ thống sẽ tự động tính toán chỉ số **BMI** và xếp loại thể trạng: *Gầy*, *Cân đối*, *Nhiều cơ ít mỡ (Athletic)*, *Thừa cân*, hoặc *Béo phì*.
5. Bấm **Lưu Phiếu Đo InBody** để lưu lịch sử biến thiên cho học viên theo dõi qua từng tháng.

---

## 5. HƯỚNG DẪN DÀNH CHO QUẢN LÝ CHI NHÁNH (OPERATIONS CENTER)
*(Tài khoản: `quanly1` | Mật khẩu: `123456`)*

Dành cho quản lý cơ sở chịu trách nhiệm điều phối nhân sự và máy móc tại chi nhánh.

### 5.1. Giám Sát Sức Chứa Phòng Tập & Tỷ Lệ Lấp Đầy
- Xem đồng hồ đo tải trọng mặt sàn theo thời gian thực.
- Cảnh báo sức chứa khi số lượng hội viên vượt quá 85% công suất tối đa của phòng tập để lễ tân có biện pháp điều tiết.

### 5.2. Bảng Xếp Ca Trực Tuần Cho Nhân Sự
1. Chọn menu **Lịch Trực & Phân Ca**.
2. Chọn tuần làm việc cần xếp ca.
3. Phân bổ nhân sự vào 3 ca vận hành tiêu chuẩn:
   - **Ca Sáng (05:30 - 13:30):** 1 Quản lý, 2 Lễ tân, 3 HLV sàn.
   - **Ca Chiều (13:30 - 21:30):** 1 Quản lý, 3 Lễ tân, 5 HLV sàn.
   - **Ca Tối / Đóng cửa (21:30 - 22:30):** Kiểm kê kho và niêm phong cửa.
4. Bấm **Xuất Lịch Trực Ra File Excel** để gửi vào nhóm thông báo chung.

### 5.3. Quản Lý Kho Hàng & Vật Phẩm Tồn Kho
- **Kho hàng:** Quản lý số lượng tồn kho của các mặt hàng nước uống và thực phẩm bổ sung. Hệ thống tự động cảnh báo khi tồn kho dưới 5 đơn vị.

---

## 6. HƯỚNG DẪN DÀNH CHO HỘI VIÊN (MEMBER VIP PORTAL)
*(Tài khoản: `hoivien1` | Mật khẩu: `123456`)*

Hội viên sở hữu không gian số tự phục vụ tiện lợi không cần qua quầy lễ tân.

### 6.1. Thẻ Điện Tử & Check-In QR Cá Nhân
- Hiển thị phôi thẻ điện tử đẳng cấp với đầy đủ thông tin: Họ tên, Cấp bậc thành viên (VIP Diamond), Chi nhánh áp dụng, và Thời hạn sử dụng.
- **Mã QR Động:** Hội viên chỉ cần mở màn hình này và đưa vào máy quét trước cửa phòng tập để cửa tự động mở.

### 6.2. Đặt Chỗ Lớp Học Theo Thời Khóa Biểu
- Xem danh sách các lớp học sắp mở trong tuần.
- Xem sĩ số lớp hiện tại (ví dụ: `18/20 chỗ`).
- Bấm **`[ Đặt Chỗ Ngay ]`** để giữ vị trí thảm tập trong studio.

### 6.3. Theo Dõi Biến Thiên Thể Trạng InBody
- Xem biểu đồ đường biến thiên cân nặng và tỷ lệ mỡ qua các lần đo.
- Tải về phiếu kết quả phân tích cơ bắp dạng PDF/Ảnh để theo dõi hành trình thay đổi vóc dáng.

### 6.4. Đánh Giá Chất Lượng Dịch Vụ & Gửi Góp Ý
- Chấm điểm số sao (1 đến 5 sao) cho huấn luyện viên hoặc cơ sở vật chất phòng tập.
- Viết ý kiến phản hồi ẩn danh. Ban quản lý và Chủ phòng tập sẽ nhận được ý kiến trực tiếp trên trung tâm điều hành.

---

## 7. TỔNG HỢP PHÍM TẮT & QUY TẮC VẬN HÀNH CHUẨN

| Thao tác | Phím tắt / Cử chỉ | Áp dụng tại màn hình |
|---|---|---|
| **Đăng nhập nhanh** | Phím **Enter** | Màn hình Đăng Nhập (`LoginForm`) |
| **Quét mã thẻ tự động** | Đưa thẻ vào máy quét USB (tự Enter) | Màn hình Check-in (`CheckInForm`) |
| **Gia hạn nhanh khi thẻ hết hạn** | Click **⚡ Gia Hạn Ngay** | Màn hình Check-in |
| **Đóng cửa sổ phụ (Dialog)** | Phím **Esc** | Các hộp thoại In thẻ, Gửi QR |
| **Làm mới dữ liệu** | Click nút **Làm Mới** | Tất cả các màn hình bảng biểu |
| **Xuất dữ liệu Excel** | Click nút **Xuất Excel** | Báo cáo, Danh sách hội viên, Điểm danh |

---
*Chúc Quý Thầy/Cô và các bạn có trải nghiệm vận hành trơn tru và hoàn hảo cùng FitLife Gym Management System!*
