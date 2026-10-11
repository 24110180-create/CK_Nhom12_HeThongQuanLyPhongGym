import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
import os

def render_diagram():
    # Set high DPI and figure size for ultra-sharp output (3200 x 1850 px)
    fig, ax = plt.subplots(figsize=(20, 12), dpi=160)
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 100)
    ax.axis('off')
    fig.patch.set_facecolor('#F8FAFC')

    font_main = 'Segoe UI'

    # Title Banner
    ax.text(50, 97.2, "KIẾN TRÚC PHÂN TẦNG HỆ THỐNG FITLIFE GYM PRO", 
            ha='center', va='center', fontsize=20, fontweight='bold', 
            color='#0F172A', fontname=font_main)
    ax.text(50, 94.8, "Mô Hình Chuẩn Doanh Nghiệp (3-Tier Enterprise) • Chuỗi 2 Chi Nhánh • Hibernate ORM 6.4 • MS SQL Server", 
            ha='center', va='center', fontsize=11, fontstyle='italic', 
            color='#475569', fontname=font_main)

    # -------------------------------------------------------------
    # Helper to draw rounded container box
    # -------------------------------------------------------------
    def draw_container(x, y, w, h, bg_color, border_color, title, subtitle="", title_color="#1E293B"):
        # Shadow
        shadow = FancyBboxPatch((x + 0.3, y - 0.3), w, h,
                                boxstyle="round,pad=0.3,rounding_size=1.2",
                                facecolor='#E2E8F0', edgecolor='none', zorder=1)
        ax.add_patch(shadow)
        
        # Main box
        box = FancyBboxPatch((x, y), w, h,
                             boxstyle="round,pad=0.3,rounding_size=1.2",
                             facecolor=bg_color, edgecolor=border_color, linewidth=2, zorder=2)
        ax.add_patch(box)

        # Title
        ax.text(x + w/2, y + h - 2.0, title, ha='center', va='center', 
                fontsize=13.5, fontweight='bold', color=title_color, fontname=font_main, zorder=3)
        if subtitle:
            ax.text(x + w/2, y + h - 3.8, subtitle, ha='center', va='center', 
                    fontsize=9.5, fontstyle='italic', color='#64748B', fontname=font_main, zorder=3)

    # Helper to draw inner card
    def draw_card(x, y, w, h, bg_color, title, subtitle="", text_color="white", border_color=None):
        # Shadow
        shadow = FancyBboxPatch((x + 0.15, y - 0.15), w, h,
                                boxstyle="round,pad=0.2,rounding_size=0.8",
                                facecolor='#CBD5E1', edgecolor='none', zorder=3)
        ax.add_patch(shadow)

        bc = border_color if border_color else bg_color
        box = FancyBboxPatch((x, y), w, h,
                             boxstyle="round,pad=0.2,rounding_size=0.8",
                             facecolor=bg_color, edgecolor=bc, linewidth=1.5, zorder=4)
        ax.add_patch(box)

        if subtitle:
            ax.text(x + w/2, y + h/2 + 0.8, title, ha='center', va='center', 
                    fontsize=10.5, fontweight='bold', color=text_color, fontname=font_main, zorder=5)
            ax.text(x + w/2, y + h/2 - 1.0, subtitle, ha='center', va='center', 
                    fontsize=8.5, color='#F1F5F9', fontname=font_main, zorder=5)
        else:
            ax.text(x + w/2, y + h/2, title, ha='center', va='center', 
                    fontsize=10.5, fontweight='bold', color=text_color, fontname=font_main, zorder=5)

    # -------------------------------------------------------------
    # 1. TẦNG GIAO DIỆN (GUI LAYER - PRESENTATION)
    # -------------------------------------------------------------
    draw_container(4, 76.5, 92, 16.0, '#F0F9FF', '#7DD3FC', 
                   "TẦNG GIAO DIỆN NGƯỜI DÙNG (GUI LAYER - PRESENTATION)", 
                   "Java Swing • FlatLaf 3.4 • com.nhom12.gym.gui (MainForm, CheckInForm) & gui.panels (31 Panels)",
                   title_color='#0369A1')

    ui_cards = [
        ("Giao diện chính", "(MainDashboard / MainForm)", 5.8, 16.5),
        ("Trạm Check-in", "(CheckInForm)", 24.3, 16.5),
        ("31 Panels Nghiệp Vụ", "(gui.panels.*)", 42.8, 16.5),
        ("Biểu đồ & Thống kê", "(BaoCaoThongKePanel)", 61.3, 16.5),
        ("Phân quyền 5 vai trò", "(Chủ / Lễ tân / HLV / QL / HV)", 79.8, 16.5)
    ]
    for title, sub, cx, cw in ui_cards:
        draw_card(cx, 78.5, cw, 8.5, '#0284C7', title, sub)

    # -------------------------------------------------------------
    # Mũi tên UI <--> BLL (5 mũi tên đối xứng)
    # -------------------------------------------------------------
    for arrow_x in [14.0, 32.5, 51.0, 69.5, 88.0]:
        ax.annotate('', xy=(arrow_x, 69.5), xytext=(arrow_x, 76.5),
                    arrowprops=dict(arrowstyle='<->', color='#0284C7', lw=2.2, mutation_scale=14),
                    zorder=10)

    # -------------------------------------------------------------
    # 2. TẦNG ĐIỀU PHỐI & NGHIỆP VỤ (CONTROLLER & SERVICE LAYER - BLL)
    # -------------------------------------------------------------
    draw_container(4, 53.5, 92, 16.0, '#ECFDF5', '#6EE7B7', 
                   "TẦNG ĐIỀU PHỐI & NGHIỆP VỤ (CONTROLLER & SERVICE LAYER - BLL)", 
                   "Controller điều phối MVC • Service đóng gói 100% Business Rules • Phân quyền RBAC • Auth Session",
                   title_color='#047857')

    bll_cards = [
        ("CheckIn Controller/Service", "(5 Rules • VIP Secret • Flap Barrier)", 6.0, 20.5),
        ("HoiVien Controller/Service", "(Hồ sơ hội viên • Gia hạn • Cấp QR)", 28.5, 20.5),
        ("BaoCao & Product Controller", "(Doanh thu 2 CN • POS • Heatmap)", 51.0, 20.5),
        ("Auth Controller/Service", "(RBAC 5 Roles • Băm Mật Khẩu SHA-256)", 73.5, 20.5)
    ]
    for title, sub, cx, cw in bll_cards:
        draw_card(cx, 55.5, cw, 8.5, '#0D9488', title, sub)

    # -------------------------------------------------------------
    # Mũi tên BLL <--> DAL (4 mũi tên)
    # -------------------------------------------------------------
    for arrow_x in [16.2, 38.7, 61.2, 83.7]:
        ax.annotate('', xy=(arrow_x, 46.5), xytext=(arrow_x, 53.5),
                    arrowprops=dict(arrowstyle='<->', color='#0D9488', lw=2.2, mutation_scale=14),
                    zorder=10)

    # -------------------------------------------------------------
    # 3. TẦNG TRUY XUẤT DỮ LIỆU (DATA ACCESS LAYER - DAL)
    # -------------------------------------------------------------
    draw_container(4, 30.5, 92, 16.0, '#FFF7ED', '#FDBA74', 
                   "TẦNG TRUY XUẤT DỮ LIỆU (DATA ACCESS LAYER - DAL)", 
                   "Data Access Object Pattern • HQL Query • Điểm Sáng Tạo: Bộ Nhớ Đệm Kép (Dual Memory Fallback Cache)",
                   title_color='#C2410C')

    dal_cards = [
        ("HoiVienDAO", "(Cache Fallback)", 5.5, 13.5),
        ("LichSuCheckInDAO", "(Dual Memory Cache)", 20.5, 14.5),
        ("GoiTapDAO", "(Cấu hình gói)", 36.5, 13.5),
        ("DangKyLopDAO", "& LopHocDAO", 51.5, 13.5),
        ("HuanLuyenVienDAO", "(HLV & Phân ca)", 66.5, 13.5),
        ("TaiKhoanDAO", "(Xác thực & Quyền)", 81.5, 13.5)
    ]
    for title, sub, cx, cw in dal_cards:
        draw_card(cx, 32.5, cw, 8.5, '#EA580C', title, sub)

    # -------------------------------------------------------------
    # Mũi tên DAL <--> ORM
    # -------------------------------------------------------------
    for arrow_x in [27.7, 43.2, 58.2, 73.2]:
        ax.annotate('', xy=(arrow_x, 24.5), xytext=(arrow_x, 30.5),
                    arrowprops=dict(arrowstyle='<->', color='#EA580C', lw=2.2, mutation_scale=14),
                    zorder=10)

    # -------------------------------------------------------------
    # 4. TẦNG ÁNH XẠ ĐỐI TƯỢNG - QUAN HỆ (PERSISTENCE / ORM LAYER)
    # -------------------------------------------------------------
    draw_container(4, 15.0, 92, 9.5, '#F5F3FF', '#C4B5FD', 
                   "TẦNG ÁNH XẠ ĐỐI TƯỢNG - QUAN HỆ (PERSISTENCE / ORM LAYER - HIBERNATE 6.4)", 
                   title_color='#6D28D9')

    draw_card(6.0, 16.0, 42.0, 5.0, '#7C3AED', 
              "Tầng Ánh Xạ ORM (Hibernate 6.4.4.Final)", 
              "Singleton SessionFactory • HibernateUtil • hibernate.cfg.xml • SQLServerDialect")

    draw_card(50.0, 16.0, 44.0, 5.0, '#6D28D9', 
              "8 Thực Thể JPA (Domain Entity Model Xuyên Suốt)", 
              "HoiVien • TaiKhoan • GoiTap • LopHoc • HuanLuyenVien • DangKyLop • LichSuCheckIn • HoaDon")

    # -------------------------------------------------------------
    # Mũi tên ORM <--> CSDL & Utilities
    # -------------------------------------------------------------
    ax.annotate('', xy=(27.0, 10.0), xytext=(27.0, 15.0),
                arrowprops=dict(arrowstyle='<->', color='#0284C7', lw=2.4, mutation_scale=14),
                zorder=10)
    ax.annotate('', xy=(73.5, 10.0), xytext=(73.5, 15.0),
                arrowprops=dict(arrowstyle='<->', color='#D97706', lw=2.4, mutation_scale=14),
                zorder=10)

    # -------------------------------------------------------------
    # 5. TẦNG CƠ SỞ DỮ LIỆU & TIỆN ÍCH DÙNG CHUNG (BOTTOM)
    # -------------------------------------------------------------
    # DB Container
    draw_container(4, 1.2, 45.0, 8.8, '#0F172A', '#38BDF8', 
                   "CƠ SỞ DỮ LIỆU QUAN HỆ (RDBMS)", 
                   title_color='#38BDF8')
    ax.text(26.5, 4.2, "Microsoft SQL Server 2019 / 2022 • Database: FitLifeGym\nChuỗi 2 Chi Nhánh (CN01 - Quận 1 • CN02 - Tân Bình) • Chỉ Mục < 5ms", 
            ha='center', va='center', fontsize=10.0, fontweight='bold', color='#F8FAFC', fontname=font_main, zorder=5)

    # Utilities Container
    draw_container(51.0, 1.2, 45.0, 8.8, '#1E293B', '#F59E0B', 
                   "TẦNG TIỆN ÍCH DÙNG CHUNG (CROSS-CUTTING UTILITIES)", 
                   title_color='#F59E0B')
    ax.text(73.5, 4.2, "PasswordUtil (Băm Mật Khẩu SHA-256) • SoundHelper (Sóng âm PCM)\nImageHelper (Sinh QR Card) • ExcelExportHelper • ValidationHelper", 
            ha='center', va='center', fontsize=9.8, fontweight='bold', color='#F8FAFC', fontname=font_main, zorder=5)

    # Đường kết nối Cross-cutting phía bên phải
    ax.annotate('', xy=(97.2, 85.0), xytext=(97.2, 6.0),
                arrowprops=dict(arrowstyle='->', color='#F59E0B', lw=1.8, linestyle='dashed', mutation_scale=14),
                zorder=10)
    ax.text(98.3, 45.0, "Domain Entity & Dịch Vụ Cắt Ngang (Cross-Cutting)", 
            rotation=-90, ha='center', va='center', fontsize=9.0, fontweight='bold', color='#D97706', fontname=font_main, zorder=10)

    # Lưu file
    output_png = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\kien_truc_phan_tang.png"
    plt.tight_layout()
    plt.savefig(output_png, dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor(), edgecolor='none')
    plt.close()

    # Copy sang docs
    dest_png = r"c:\Users\Anh Duy\Desktop\Đồ án cuối kì\GYM\docs\kien_truc_phan_tang.png"
    import shutil
    shutil.copyfile(output_png, dest_png)
    print(f"Rendered successfully to: {output_png}")
    print(f"Copied to: {dest_png}")

if __name__ == "__main__":
    render_diagram()
