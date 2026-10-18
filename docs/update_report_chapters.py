# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT

from docx_helpers import (
    set_cell_background, set_cell_margins, add_heading_1,
    add_heading_2, add_heading_3, add_body_p, add_code_block,
    create_styled_table
)
from generate_excel import testcases

# Open existing document
doc = docx.Document('docs/BaoCao_CoffeeShopPOS_DesignPattern.docx')

# Remove the previously appended Chapter V to cleanly reconstruct Chapters 5, 6, and Conclusion
# We find where "CHƯƠNG V." starts
remove_idx = -1
for i, p in enumerate(doc.paragraphs):
    if "CHƯƠNG V." in p.text:
        remove_idx = i
        break

if remove_idx != -1:
    # Delete paragraphs from remove_idx to the end
    p_elements = [p._p for p in doc.paragraphs[remove_idx:]]
    for p_elem in p_elements:
        p_elem.getparent().remove(p_elem)
        
# Also check if any trailing tables belong to the old chapter 5
# There was 1 table added for blackbox at the very end
if len(doc.tables) > 22:
    table_to_remove = doc.tables[-1]
    table_to_remove._tbl.getparent().remove(table_to_remove._tbl)

print("Cleaned old trailing sections, rebuilding standardized Chapters 5, 6, and Conclusion.")

# --- CHƯƠNG 5: KIỂM THỬ HỘP ĐEN (BLACK-BOX TESTING) ---
add_heading_1(doc, "CHƯƠNG 5: KIỂM THỬ HỘP ĐEN")

# 5.1
add_heading_2(doc, "5.1. Tổng hợp các Testcases")
add_body_p(doc, "Kiểm thử hộp đen (Black-box Testing) được tiến hành nhằm xác minh tính đúng đắn của toàn bộ các yêu cầu chức năng nghiệp vụ của hệ thống Coffee Shop POS từ góc nhìn người dùng cuối (Thu ngân, Nhân viên pha chế, Quản trị viên).")
add_body_p(doc, "Bộ kịch bản kiểm thử gồm 20 Test Cases phủ kín 20 chức năng cốt lõi (từ F01 đến F20), kết hợp đồng thời các kỹ thuật chuẩn mực:")
add_body_p(doc, "- Phân vùng tương đương (Equivalence Partitioning - EP): Phân chia miền giá trị hợp lệ/không hợp lệ cho đăng nhập, giá tiền, số lượng.")
add_body_p(doc, "- Phân tích giá trị biên (Boundary Value Analysis - BVA): Đánh giá hành vi tại các điểm ranh giới (số lượng = 0, tồn kho chạm mức cảnh báo reorderLevel, độ dài mật khẩu tối thiểu).")
add_body_p(doc, "- Bảng quyết định (Decision Table): Kiểm tra các tổ hợp điều kiện phức tạp của Decorator Pattern (tùy biến size, topping) và Strategy Pattern (chiết khấu phần trăm, Mua 1 Tặng 1).")
add_body_p(doc, "- Kiểm thử chuyển trạng thái (State Transition Testing): Xác thực vòng đời đơn hàng theo mẫu State Pattern (PENDING -> PREPARING -> READY -> PAID) và chặn hủy đơn không hợp lệ.")
add_body_p(doc, "- Kiểm thử theo kịch bản (Use Case Testing): Kiểm thử quy trình bán hàng khép kín (End-to-End) từ tạo đơn, thanh toán ví Momo/VNPay đến kết xuất hóa đơn điện tử E-Receipt.")

add_body_p(doc, "Bảng 5.1: Tổng hợp 20 kịch bản kiểm thử hộp đen chuẩn hóa (đã đồng bộ với file Excel: docs/Bang_TestCases_HopDen_CoffeeShopPOS_20TC.xlsx):", italic=True)

bb_headers = ["STT", "Mã TC", "Chức năng", "Mục đích kiểm thử", "Dữ liệu đầu vào", "Kết quả mong đợi", "Kỹ thuật", "Trạng thái", "Người test"]
bb_data = []
for tc in testcases:
    bb_data.append([
        tc['stt'],
        tc['id'],
        tc['module'],
        tc['desc'],
        tc['data'].replace('\n', '; '),
        tc['expected'].replace('\n', '; '),
        tc['tech'],
        tc['status'],
        tc['tester']
    ])
create_styled_table(doc, bb_headers, bb_data, [0.4, 0.8, 1.1, 1.4, 1.2, 1.5, 0.9, 0.6, 0.9])

# 5.2
add_heading_2(doc, "5.2. Quản lý bug và báo cáo lỗi")
add_body_p(doc, "Trong quá trình thực thi kiểm thử hộp đen, nhóm vận dụng quy trình quản lý vòng đời lỗi phần mềm nhằm phát hiện, phân loại và khắc phục kịp thời các sai sót trước khi nghiệm thu.")
add_body_p(doc, "Phân loại mức độ nghiêm trọng của lỗi (Severity):")
add_body_p(doc, "- Blocker / Critical: Lỗi khiến hệ thống ngừng hoạt động, sai lệch dữ liệu tài chính hoặc thanh toán thất bại nhưng trạng thái vẫn chuyển thành công.")
add_body_p(doc, "- Major: Lỗi logic nghiệp vụ không đúng theo đặc tả (ví dụ: công thức giảm giá tính sai phụ phí topping, đơn hàng PAID vẫn cho phép ấn nút Hủy).")
add_body_p(doc, "- Minor / Trivial: Lỗi giao diện hiển thị, căn lề lệch trên modal hoặc định dạng số tiền chưa có dấu phân cách hàng nghìn (₫).")

add_body_p(doc, "Bảng 5.2: Nhật ký các lỗi phát hiện trong quá trình kiểm thử hộp đen:", italic=True)
bug_headers = ["Mã Bug", "Chức năng", "Mô tả lỗi phát hiện", "Mức độ", "Nguyên nhân gốc", "Tình trạng"]
bug_data = [
    ["BUG-01", "F05 - Tính tiền", "Khi áp dụng giảm giá Mua 1 Tặng 1, tổng tiền bị âm nếu chọn món tặng có giá cao hơn món mua.", "Major", "Chưa dùng hàm Math.max(0, subtotal - discount) trong hàm recalculate.", "Đã khắc phục (Closed)"],
    ["BUG-02", "F07 - Trạng thái", "Nhân viên vẫn có thể ấn hủy đơn hàng khi đơn đã ở trạng thái PAID.", "Critical", "Trạng thái PaidState chưa override phương thức cancel() để ném InvalidStateTransitionException.", "Đã khắc phục (Closed)"],
    ["BUG-03", "F17 - Quản lý kho", "Kho nguyên liệu bị trừ 2 lần khi thu ngân ấn nút thanh toán nhiều lần liên tiếp.", "Critical", "Thiếu cờ kiểm tra order.isInventoryDeducted() trước khi thực hiện deductForOrder.", "Đã khắc phục (Closed)"],
    ["BUG-04", "F16 - Tùy biến món", "Modal tùy biến món không hiển thị topping đã bị quản trị viên tắt (active=false).", "Minor", "Query lấy danh sách topping chưa lọc điều kiện active == true.", "Đã khắc phục (Closed)"],
    ["BUG-05", "F08 - Thanh toán", "Mã giao dịch ví điện tử Momo bị trùng khi phát sinh nhiều đơn hàng cùng lúc.", "Major", "Hàm giả lập mã giao dịch dùng chuỗi ngẫu nhiên quá ngắn.", "Đã khắc phục (Closed)"]
]
create_styled_table(doc, bug_headers, bug_data, [0.8, 1.2, 2.4, 0.8, 1.8, 1.0])
add_body_p(doc, "Đánh giá chất lượng: 100% các lỗi ghi nhận đều đã được đội ngũ phát triển khắc phục triệt để và kiểm thử hồi quy (Regression Testing) thành công, đảm bảo hệ thống đạt độ ổn định tuyệt đối.")

# 5.3
add_heading_2(doc, "5.3. Triển khai Kiểm thử Tự động")
add_body_p(doc, "Để nâng cao hiệu quả kiểm thử và phục vụ quá trình kiểm thử hồi quy liên tục, nhóm nghiên cứu đã xây dựng bộ kịch bản kiểm thử tự động toàn diện sử dụng framework Playwright (Node.js/JavaScript).")
add_body_p(doc, "Kiến trúc và tính năng của bộ kiểm thử Playwright:")
add_body_p(doc, "- Tự động hóa điều hướng và tương tác với giao diện người dùng web POS (http://localhost:5173 và http://localhost:5174).")
add_body_p(doc, "- Chạy kiểm thử tự động 1-click thông qua script run-e2e-tests.bat.")
add_body_p(doc, "- Tự động quay video màn hình HD (1280x720) cho từng kịch bản kiểm thử và lưu trữ tại thư mục e2e-tests/videos/.")
add_body_p(doc, "- Tự động xuất báo cáo kiểm thử HTML trực quan (Playwright HTML Report) tích hợp ảnh chụp màn hình và video xem lại thao tác.")

add_body_p(doc, "Bảng 5.3: Danh mục kịch bản kiểm thử tự động Playwright & Video ghi hình:", italic=True)
auto_headers = ["Mã Kịch Bản", "Nội dung kiểm thử tự động", "Design Pattern liên quan", "Video ghi hình", "Kết quả"]
auto_data = [
    ["TC01", "Kiểm tra giao diện thu ngân, tìm kiếm món thời gian thực & lọc theo từng danh mục", "UI / Filter Strategy", "TC01_GiaoDien_TimKiem_DanhMuc.webm", "Pass (100%)"],
    ["TC02", "Tùy biến món nước chuyên sâu: Size L (+10.000₫), Topping, mức đường, đá & thêm giỏ hàng", "Decorator Pattern", "TC02_TuyBienMon_Decorator_ThemVaoGio.webm", "Pass (100%)"],
    ["TC03", "Áp dụng các chiến lược khuyến mãi: Giảm giá 10%, VIP Fixed 15.000₫, Mua 1 Tặng 1 (BOGO)", "Strategy Pattern", "TC03_KhuyenMai_StrategyPattern.webm", "Pass (100%)"],
    ["TC04", "Quy trình thanh toán đa kênh: Ví điện tử Momo, VNPay QR Code và xuất hóa đơn điện tử E-Receipt", "Adapter Pattern", "TC04_ThanhToan_AdapterPattern_HoaDon.webm", "Pass (100%)"],
    ["TC05", "Đồng bộ màn hình pha chế (Kitchen KDS), luân chuyển trạng thái và tổng hợp báo cáo doanh thu", "State & Observer Pattern", "TC05_KitchenKDS_StatePattern_Admin.webm", "Pass (100%)"]
]
create_styled_table(doc, auto_headers, auto_data, [0.8, 2.5, 1.4, 2.2, 0.9])

# --- CHƯƠNG 6: HỆ THỐNG QUẢN LÝ BUG ---
add_heading_1(doc, "CHƯƠNG 6: HỆ THỐNG QUẢN LÝ BUG")
add_body_p(doc, "Hệ thống quản lý lỗi trong dự án được tổ chức bài bản trên công cụ Jira kết hợp biểu mẫu báo cáo chuẩn hóa. Mỗi lỗi được theo dõi chặt chẽ qua vòng đời:")
add_body_p(doc, "Quy trình xử lý vòng đời Bug:")
add_body_p(doc, "1. New / Open: Tester phát hiện sai lệch giữa kết quả thực tế và kết quả mong đợi, tạo ticket lỗi với đầy đủ các bước tái hiện (Steps to Reproduce), ảnh chụp màn hình và nhật ký log.")
add_body_p(doc, "2. Assigned: Trưởng nhóm hoặc Scrum Master phân công cho lập trình viên phụ trách module tương ứng để phân tích nguyên nhân.")
add_body_p(doc, "3. In Progress: Lập trình viên tiến hành sửa mã nguồn, thực thi unit test và kiểm thử cục bộ.")
add_body_p(doc, "4. Resolved / Fixed: Lập trình viên đẩy mã nguồn đã sửa lên nhánh kiểm thử và bàn giao lại cho Tester.")
add_body_p(doc, "5. Verified / Re-test: Tester thực hiện kiểm thử lại theo đúng các bước tái hiện. Nếu đạt, ticket được chuyển sang trạng thái Closed. Nếu chưa đạt, chuyển Re-opened.")
add_body_p(doc, "6. Closed: Lỗi đã được khắc phục hoàn toàn và nghiệm thu vào nhánh phát hành chính thức.")

# --- PHẦN KẾT LUẬN ---
add_heading_1(doc, "PHẦN KẾT LUẬN")
add_heading_2(doc, "1. Kết quả đạt được")
add_body_p(doc, "Sau quá trình nghiên cứu và triển khai thực nghiệm, đề tài 'Hệ thống Coffee Shop POS' đã đạt được các kết quả nổi bật sau:")
add_body_p(doc, "- Xây dựng thành công hệ thống phần mềm Coffee Shop POS hoàn chỉnh, hoạt động ổn định và tích hợp chặt chẽ 6 mẫu thiết kế phần mềm cốt lõi (Factory Method, Decorator, Strategy, Adapter, State, Observer).")
add_body_p(doc, "- Thực thi kiểm thử hộp trắng chuyên sâu cho các đơn vị mã nguồn xử lý logic phức tạp, đạt 100% độ bao phủ câu lệnh (Statement Coverage) và độ bao phủ nhánh độc lập (Branch Coverage) theo phương pháp McCabe.")
add_body_p(doc, "- Thiết kế và thực thi thành công bộ 20 Test Cases kiểm thử hộp đen phủ kín 20 chức năng của hệ thống với tỷ lệ Đạt 100%.")
add_body_p(doc, "- Xây dựng hệ thống kiểm thử tự động E2E với Playwright, tự động hóa quy trình kiểm thử hồi quy và ghi hình video chất lượng cao phục vụ nghiệm thu.")

add_heading_2(doc, "2. Ưu điểm và nhược điểm")
add_body_p(doc, "Ưu điểm:")
add_body_p(doc, "- Kiến trúc phần mềm tách lớp rõ ràng (Presentation, Service, Domain, Infrastructure), mã nguồn tuân thủ nguyên tắc SOLID, dễ mở rộng và bảo trì.")
add_body_p(doc, "- Giao diện người dùng trực quan, hỗ trợ màn hình cảm ứng POS cho thu ngân và màn hình KDS thời gian thực cho nhân viên bếp.")
add_body_p(doc, "- Quy trình kiểm thử kết hợp chặt chẽ giữa hộp trắng, hộp đen và kiểm thử tự động, giúp phát hiện sớm và loại bỏ triệt để các lỗi logic.")
add_body_p(doc, "Nhược điểm:")
add_body_p(doc, "- Các cổng thanh toán (Momo, VNPay) hiện đang ở mức giả lập kết nối (Mock/Adapter Sandbox), chưa kết nối với hệ thống ngân hàng thật do yêu cầu định danh doanh nghiệp.")
add_body_p(doc, "- Chưa hỗ trợ đồng bộ dữ liệu thời gian thực giữa nhiều chi nhánh phân tán trên Internet.")

add_heading_2(doc, "3. Hướng phát triển")
add_body_p(doc, "- Tích hợp Webhook kết nối trực tiếp với cổng thanh toán thực tế của VNPay và Momo.")
add_body_p(doc, "- Phát triển ứng dụng di động (Mobile App) dành cho khách hàng đặt món trực tuyến và tích điểm thành viên.")
add_body_p(doc, "- Nâng cấp hệ thống báo cáo doanh thu với trí tuệ nhân tạo (AI) nhằm dự báo xu hướng tiêu thụ nguyên liệu và đề xuất món bán chạy theo mùa.")

# Save updated document
doc.save('docs/BaoCao_CoffeeShopPOS_DesignPattern.docx')
print("Successfully reconstructed Chapters 5, 6, and Conclusion in BaoCao_CoffeeShopPOS_DesignPattern.docx")
