# 📋 BẢNG PHÂN CÔNG NHIỆM VỤ & ĐÁNH GIÁ MỨC ĐỘ HOÀN THÀNH
## ĐỒ ÁN TỐT NGHIỆP / CUỐI KỲ: HỆ THỐNG QUẢN TRỊ PHÒNG GYM FITLIFE (FITLIFE PRO)
**Bộ Môn:** Công Nghệ Phần Mềm / Hệ Thống Thông Tin  
**Nhóm Thực Hiện:** Nhóm 12  
**Nền Tảng Công Nghệ:** Java 17 LTS, Java Swing (FlatLaf), Hibernate ORM 6.4, MS SQL Server  

---

## 👥 1. BẢNG TỔNG HỢP PHÂN CÔNG & ĐÁNH GIÁ ĐÓNG GÓP

| Thành viên | Vai trò | Nhiệm vụ chính phụ trách | Đóng góp |
|---|---|---|:---:|
| **Lê Anh Duy** | **Nhóm trưởng** | • Quản lý tiến độ, thiết kế CSDL (`Database.sql`) và Hibernate.<br>• Phát triển Module Xác thực, Phân quyền & Khung `MainDashboard`.<br>• Xây dựng chức năng Check-in hội viên & Log ra/vào. | **100%** |
| **Võ Lê Ngọc Hưng** | **Thành viên** | • Module Quản lý Hội viên (đăng ký, gia hạn, hồ sơ chi tiết).<br>• Module Quản lý Huấn luyện viên & Phân ca nhân sự.<br>• Module Lịch lớp học & Huấn luyện (PT). | **100%** |
| **Võ Tấn Phát** | **Thành viên** | • Module Điểm bán hàng POS, Kho hàng & Hóa đơn.<br>• Module Báo cáo doanh thu & Biểu đồ nhiệt (Heatmap).<br>• Tích hợp tiện ích: Xuất Excel, sinh mã QR & Âm thanh. | **100%** |

---

## 🔍 2. BÁO CÁO CHI TIẾT CÔNG VIỆC CỦA TỪNG THÀNH VIÊN

### 👤 2.1. Lê Anh Duy — Nhóm Trưởng (Đóng Góp: 100%)
*Chịu trách nhiệm tổng thể về kiến trúc hệ thống, cơ sở dữ liệu, luồng điều hướng chính và use case cốt lõi của đồ án.*

#### Các hạng mục công việc đã hoàn thành xuất sắc:
1. **Quản trị dự án & Thiết kế kiến trúc:**
   - Lập kế hoạch phân bổ công việc, theo dõi tiến độ sprint và tích hợp mã nguồn (Git / Maven).
   - Thiết kế kiến trúc 3 tầng chuẩn mực Java Enterprise (**Entity - DAO - Service - GUI - Util**).
2. **Thiết kế Cơ sở Dữ liệu & Hibernate ORM:**
   - Xây dựng file [Database.sql](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/Database.sql) gồm 8 bảng quan hệ chuẩn hóa (khóa chính, khóa ngoại, ràng buộc duy nhất, cascade và dữ liệu mẫu kiểm thử).
   - Cấu hình [hibernate.cfg.xml](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/resources/hibernate.cfg.xml) và xây dựng lớp `HibernateUtil.java` quản lý `SessionFactory`.
3. **Module Xác thực & Phân quyền bảo mật (RBAC):**
   - Xây dựng màn hình đăng nhập sang trọng [LoginForm.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/LoginForm.java) với giao diện FlatLaf và xử lý nền đồ họa.
   - Hiện thực [AuthService.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/service/AuthService.java) phân quyền 5 vai trò: *Chủ hệ thống, Lễ tân, HLV, Quản lý chi nhánh, Hội viên VIP*.
   - Hiện thực cơ chế **Offline Fallback Buffer** tự động chuyển sang chế độ dự phòng khi mất kết nối mạng.
4. **Xây dựng Khung điều phối trung tâm (`MainDashboard.java`):**
   - Phát triển thanh điều hướng 2 tầng **Dual-Tier Ribbon Navigation** cho Chủ hệ thống với 5 Trụ cột chiến lược.
   - Thiết kế không gian làm việc nhúng trực tiếp (**Panoramic Embedded Workspace**) và các thanh Sidebar chuyên biệt cho từng vai trò nhân sự.
5. **Use Case Cốt Lõi: Check-in Hội Viên & Cổng Tự Động:**
   - Phát triển [CheckInForm.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/CheckInForm.java) và [CheckInService.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/service/CheckInService.java).
   - Kiểm tra nghiêm ngặt **5 Business Rules**: Tồn tại thẻ, Không bị khóa, Còn hạn, Đúng chi nhánh, Giờ mở cửa & Chống quét trùng trong 2 phút.
   - Tối ưu hóa hiệu năng quét đạt tốc độ siêu nhanh **15ms - 35ms**.
   - Xử lý yêu cầu ẩn VIP của Anh Đức (Bảo mật mức giá chiết khấu với lễ tân).
   - Tích hợp ghi nhật ký Audit Log thời gian thực (`LichSuCheckInDAO`).

---

### 👤 2.2. Võ Lê Ngọc Hưng — Thành Viên (Đóng Góp: 100%)
*Chịu trách nhiệm toàn bộ các phân hệ liên quan đến Quản trị Hội viên, Đội ngũ Huấn luyện viên và Lịch trình Lớp học Group Fitness.*

#### Các hạng mục công việc đã hoàn thành xuất sắc:
1. **Module Quản lý Hội viên (Member Operations):**
   - Xây dựng giao diện tiếp nhận và đăng ký mới [DangKyHoiVienPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/DangKyHoiVienPanel.java): Form nhập liệu, xem trước phôi thẻ VIP, tính toán giá tự động, áp dụng mã voucher và tùy chọn sinh tài khoản App.
   - Xây dựng màn hình [GiaHanGoiTapPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/GiaHanGoiTapPanel.java): Cơ chế cộng dồn ngày hết hạn thông minh khi thẻ còn hạn.
   - Xây dựng [QuanLyHoiVienPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/QuanLyHoiVienPanel.java) và [ChiTietHoiVienPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/ChiTietHoiVienPanel.java): Tìm kiếm theo tên/SĐT/mã QR, lọc trạng thái, xem lịch sử tập luyện.
   - Hiện thực [HoiVienDAO.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/dao/HoiVienDAO.java) kết nối CSDL và bộ nhớ đệm cache.
2. **Module Quản lý Huấn luyện viên & Phân ca Nhân sự:**
   - Xây dựng [QuanLyNhanSuPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/QuanLyNhanSuPanel.java) và [HuanLuyenVienDAO.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/dao/HuanLuyenVienDAO.java): Danh bạ HLV, số năm kinh nghiệm, chuyên môn (Bodybuilding, Yoga, Boxing, CrossFit), đánh giá sao.
   - Xây dựng màn hình [PhanCaLamViecPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/PhanCaLamViecPanel.java): Phân bổ ca làm việc tuần (Ca sáng, Ca chiều, Ca kiểm kê).
3. **Module Lịch lớp học & Huấn luyện cá nhân (PT):**
   - Xây dựng [LichLopHocPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/LichLopHocPanel.java) và [LichDayHLVPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/LichDayHLVPanel.java): Lịch lớp Yoga Trị Liệu, Zumba, Kickboxing, Pilates Core, Cardio.
   - Xây dựng **Hệ thống Điểm Danh Lớp Học** chuyên biệt (Có mặt / Vắng mặt) và [DangKyLopDAO.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/dao/DangKyLopDAO.java).
   - Xây dựng [GhiChuTienDoPTPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/GhiChuTienDoPTPanel.java): Sổ tay theo dõi số buổi kèm 1:1 và ghi nhận kỷ lục tạ cá nhân (Squat, Bench, Deadlift PR).

---

### 👤 2.3. Võ Tấn Phát — Thành Viên (Đóng Góp: 100%)
*Chịu trách nhiệm toàn bộ các phân hệ Bán hàng POS, Quản lý Kho, Trung tâm Báo cáo Doanh thu, Bản đồ nhiệt và Bộ công cụ Tiện ích hệ thống.*

#### Các hạng mục công việc đã hoàn thành xuất sắc:
1. **Module Điểm bán hàng POS, Kho hàng & Hóa đơn:**
   - Xây dựng [BanHangPOSPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/BanHangPOSPanel.java): Danh mục sản phẩm trực quan kèm ảnh thật (Nước khoáng, Nước tăng lực, Whey isolate, Khăn tập, Găng tay, Chuối nạp năng lượng).
   - Giỏ hàng động, thanh toán Tiền mặt (tính tiền thối) hoặc Chuyển khoản VietQR.
   - Xây dựng [QuanLyKhoHangPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/QuanLyKhoHangPanel.java): Kiểm kê tồn kho và cảnh báo nhập hàng.
   - Xây dựng [HoaDonDAO.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/dao/HoaDonDAO.java) và [LichSuGiaoDichPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/LichSuGiaoDichPanel.java): Quản lý lịch sử hóa đơn tài chính.
2. **Module Báo cáo Doanh thu & Biểu đồ nhiệt (Heatmap):**
   - Xây dựng [BaoCaoThongKePanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/BaoCaoThongKePanel.java) và [BaoCaoService.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/service/BaoCaoService.java):
     - **Báo cáo Doanh thu:** Tổng hợp doanh thu 3 cơ sở (CN01 Quận 1, CN02 Tân Bình, CN03 Quận 7) kèm biểu đồ so sánh.
     - **Báo cáo Hội viên sắp hết hạn:** Cảnh báo các cấp độ 7 ngày, 3 ngày, quá hạn kèm chức năng gửi SMS/Zalo.
   - Xây dựng [HeatmapGioCaoDiemPanel.java](file:///c:/Users/Anh%20Duy/Desktop/Đồ%20án%20cuối%20kì/GYM/src/main/java/com/nhom12/gym/gui/HeatmapGioCaoDiemPanel.java): Phân tích mật độ lưu lượng khách tập luyện theo khung giờ từ **06:00 đến 22:00**, nhận diện 2 khung giờ vàng sáng và tối.
3. **Tích hợp Bộ Tiện ích Công Nghệ Cao (Utilities):**
   - **`ExcelExportHelper.java`:** Engine xuất file Excel/CSV tích hợp ký tự Byte Order Mark (`\uFEFF`) UTF-8 BOM, đảm bảo mở trực tiếp bằng Excel với 100% tiếng Việt có dấu.
   - **`SoundHelper.java`:** Trình tạo sóng âm PCM tần số 1200Hz (Bíp thành công) và 420Hz (Cảnh báo lỗi) mô phỏng âm thanh máy quét USB Honeywell Voyager.
   - **`ImageHelper.java` & `GuiMaQRDialog.java`:** Xử lý đồ họa ảnh thẻ, tạo mã QR định danh và xuất file ảnh PNG cho hội viên.

---

## 📊 3. MA TRẬN PHÂN CHIA TRÁCH NHIỆM (RACI MATRIX)

| Phân hệ / Tính năng chính | Lê Anh Duy | Võ Lê Ngọc Hưng | Võ Tấn Phát |
|---|:---:|:---:|:---:|
| **Thiết kế CSDL (`Database.sql`) & Hibernate 6.4** | **Responsible (R)** | Consulted (C) | Consulted (C) |
| **Xác thực, Phân quyền RBAC & `LoginForm`** | **Responsible (R)** | Consulted (C) | Consulted (C) |
| **Khung điều hành `MainDashboard` & 5 Trụ cột** | **Responsible (R)** | Support (S) | Support (S) |
| **Use case Lõi: Check-in, Flap Barrier & Audit Log** | **Responsible (R)** | Consulted (C) | Consulted (C) |
| **Quản lý Hội viên (Đăng ký, Gia hạn, Hồ sơ)** | Consulted (C) | **Responsible (R)** | Support (S) |
| **Quản lý Huấn luyện viên & Phân ca trực tuần** | Consulted (C) | **Responsible (R)** | Consulted (C) |
| **Lịch lớp học & Điểm danh học viên** | Consulted (C) | **Responsible (R)** | Consulted (C) |
| **Sổ tay tiến độ PT 1:1 & Theo dõi chỉ số InBody** | Consulted (C) | **Responsible (R)** | Support (S) |
| **Quầy bán hàng POS & Quản lý Kho hàng** | Consulted (C) | Support (S) | **Responsible (R)** |
| **Báo cáo Doanh thu 3 chi nhánh & Hội viên hết hạn** | Consulted (C) | Consulted (C) | **Responsible (R)** |
| **Bản đồ nhiệt giờ cao điểm (Hourly Heatmap)** | Consulted (C) | Consulted (C) | **Responsible (R)** |
| **Tiện ích: Xuất Excel UTF-8, Tạo mã QR, Âm thanh PCM** | Support (S) | Consulted (C) | **Responsible (R)** |

*(Ghi chú: **R** - Responsible: Người trực tiếp phát triển chính; **S** - Support: Hỗ trợ; **C** - Consulted: Tham vấn / Đóng góp ý kiến)*

---

## ✍️ 4. XÁC NHẬN CỦA CÁC THÀNH VIÊN TRONG NHÓM

Tất cả các thành viên trong Nhóm 12 đã tích cực làm việc với tinh thần trách nhiệm cao, hoàn thành **100% khối lượng công việc** được giao đúng tiến độ và đạt chuẩn chất lượng đồ án tốt nghiệp.

| Nhóm Trưởng | Thành Viên | Thành Viên |
|:---:|:---:|:---:|
| *(Ký và ghi rõ họ tên)* | *(Ký và ghi rõ họ tên)* | *(Ký và ghi rõ họ tên)* |
| <br><br>**Lê Anh Duy** | <br><br>**Võ Lê Ngọc Hưng** | <br><br>**Võ Tấn Phát** |

---
*Ngày hoàn thành: Tháng 10 năm 2026 — Nhóm 12 Bộ môn Công nghệ Phần mềm*
