# -*- coding: utf-8 -*-
"""
Tạo file Word (.docx) chuyên nghiệp cho mục:
1.2. TRIỂN KHAI KIỂM THỬ TỰ ĐỘNG CHO PHÂN HỆ BACKEND (Phần Quân thực hiện)
Định dạng: Times New Roman, Căn lề chuẩn 2cm, Tiêu đề bảng biểu sắc nét.
"""

import sys
sys.stdout.reconfigure(encoding='utf-8')
from pathlib import Path
import docx
from docx.shared import Pt, RGBColor, Inches, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

ROOT = Path(__file__).resolve().parents[1]
OUT_DOCX = ROOT / "docs" / "MUC_1_2_TRIEN_KHAI_KIEM_THU_TU_DONG_BACKEND.docx"

def set_cell_background(cell, hex_color):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=80, bottom=80, left=100, right=100):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def format_custom_table(table, col_widths, headers, data_rows, hdr_bg='1F4E79', hdr_text_white=True, font_size_pt=9.5):
    total_dxa = int(sum(col_widths) * 1440)
    tblPr = table._tbl.tblPr
    tblPr.append(parse_xml(f'<w:tblW {nsdecls("w")} w:w="{total_dxa}" w:type="dxa"/>'))
    tblPr.append(parse_xml(f'<w:tblLayout {nsdecls("w")} w:type="fixed"/>'))
    tblPr.append(parse_xml(f'<w:jc {nsdecls("w")} w:val="center"/>'))
    
    grid_cols = ''.join(f'<w:gridCol {nsdecls("w")} w:w="{int(w * 1440)}"/>' for w in col_widths)
    tblGrid = parse_xml(f'<w:tblGrid {nsdecls("w")}>{grid_cols}</w:tblGrid>')
    table._tbl.insert(table._tbl.index(tblPr) + 1, tblGrid)
    
    trPr = table.rows[0]._tr.get_or_add_trPr()
    trPr.append(parse_xml(f'<w:tblHeader {nsdecls("w")}/>'))
    trPr.append(parse_xml(f'<w:cantSplit {nsdecls("w")}/>'))
    
    for col_idx, h_text in enumerate(headers):
        cell = table.rows[0].cells[col_idx]
        cell.text = h_text
        set_cell_background(cell, hdr_bg)
        set_cell_margins(cell, top=100, bottom=100, left=100, right=100)
        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        for r in p.runs:
            r.font.name = 'Times New Roman'
            r.font.size = Pt(font_size_pt)
            r.font.bold = True
            if hdr_text_white:
                r.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
                
    for row_idx, r_data in enumerate(data_rows):
        trPr_row = table.rows[row_idx + 1]._tr.get_or_add_trPr()
        trPr_row.append(parse_xml(f'<w:cantSplit {nsdecls("w")}/>'))
        row_bg = 'F2F4F7' if row_idx % 2 == 1 else 'FFFFFF'
        for col_idx, val in enumerate(r_data):
            cell = table.rows[row_idx + 1].cells[col_idx]
            cell.text = str(val)
            set_cell_background(cell, row_bg)
            set_cell_margins(cell, top=70, bottom=70, left=90, right=90)
            p = cell.paragraphs[0]
            if col_idx in [0, 1, 4] or (len(str(val)) <= 10 and '\n' not in str(val)):
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            else:
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.font.name = 'Times New Roman'
                r.font.size = Pt(font_size_pt)
                if 'Pass' in str(val) or 'Đạt' in str(val):
                    r.font.bold = True
                    r.font.color.rgb = RGBColor(0x00, 0x80, 0x00)

def add_p(doc, text="", bold_prefix="", indent=False, italic=False, space_after=4):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = 1.2
    if indent:
        p.paragraph_format.left_indent = Inches(0.25)
    else:
        p.paragraph_format.left_indent = Inches(0)
        
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = 'Times New Roman'
        r_pre.font.size = Pt(13)
        r_pre.font.bold = True
    if text:
        r = p.add_run(text)
        r.font.name = 'Times New Roman'
        r.font.size = Pt(13)
        if italic:
            r.font.italic = True
    return p

def add_h1(doc, text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(14)
    p.paragraph_format.space_after = Pt(6)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(14)
    run.font.bold = True
    run.font.color.rgb = RGBColor(0x0F, 0x47, 0x61)
    return p

def add_h2(doc, text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(10)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(13)
    run.font.bold = True
    run.font.color.rgb = RGBColor(0x1F, 0x4E, 0x79)
    return p

def build_docx():
    doc = docx.Document()
    
    # Căn lề chuẩn 2cm
    for s in doc.sections:
        s.top_margin = Cm(2)
        s.bottom_margin = Cm(2)
        s.left_margin = Cm(2)
        s.right_margin = Cm(2)
        
    # Tiêu đề tài liệu
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(0)
    p_title.paragraph_format.space_after = Pt(4)
    r_title = p_title.add_run("BÁO CÁO THỰC THI KIỂM THỬ PHẦN MỀM COFFEE SHOP POS")
    r_title.font.name = 'Times New Roman'
    r_title.font.size = Pt(15)
    r_title.font.bold = True
    r_title.font.color.rgb = RGBColor(0x0F, 0x47, 0x61)
    
    p_sub = doc.add_paragraph()
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_after = Pt(16)
    r_sub = p_sub.add_run("MỤC 1.2: TRIỂN KHAI KIỂM THỬ TỰ ĐỘNG CHO PHÂN HỆ BACKEND\n(Tài liệu kỹ thuật chi tiết phục vụ tổng hợp đồ án - Phần Quân thực hiện)")
    r_sub.font.name = 'Times New Roman'
    r_sub.font.size = Pt(12)
    r_sub.font.italic = True
    r_sub.font.color.rgb = RGBColor(0x55, 0x55, 0x55)
    
    # -------------------------------------------------------------------------
    # 1.2.1. Đặt vấn đề và Thiết kế Test Harness
    # -------------------------------------------------------------------------
    add_h1(doc, "1.2.1. Đặt vấn đề và Giải pháp thiết kế Bộ điều phối kiểm thử (Test Harness)")
    add_p(doc, text="Trong kiến trúc hệ thống Coffee Shop POS, toàn bộ mã nguồn xử lý logic nghiệp vụ, quản lý đơn hàng, tính toán tài chính và hiện thực các mẫu thiết kế phần mềm (Design Patterns) đều tập trung tại tầng Backend (xây dựng bằng ngôn ngữ Java 17). Nếu chỉ thực hiện kiểm thử thủ công thông qua giao diện người dùng thì rất dễ bỏ sót các nhánh rẽ điều kiện (Branch Coverage), không thể kiểm thử triệt để các tình huống ngoại lệ tại biên, đồng thời chi phí thời gian cho việc kiểm thử hồi quy lặp lại sau mỗi lần sửa mã nguồn là rất lớn.")
    add_p(doc, text="Để phục vụ quá trình kiểm thử hồi quy tự động, liên tục mà vẫn đảm bảo tính độc lập gọn nhẹ (có thể tải về và biên dịch thực thi trực tiếp trên bất kỳ máy tính nào cài sẵn Java JDK 17 mà không bắt buộc phải cài đặt thêm các công cụ quản lý dự án nặng như Maven hay Gradle), thành viên phụ trách Backend (Quân) đã thiết kế và xây dựng bộ điều phối kiểm thử tự động nội bộ gồm các thành phần trọng yếu sau:")
    
    add_p(doc, text="Bộ Test Runner độc lập (TestRunner.java): Tự phát triển một bộ chạy kiểm thử tinh gọn, hiện thực các phương thức kiểm chứng logic cốt lõi (assertEquals, assertTrue, assertThrows) tương đương chức năng của JUnit nhưng không kéo theo thư viện phụ thuộc. Toàn bộ 24 ca kiểm thử được thực thi tuần tự, tự động thống kê số bài Pass/Fail và báo cáo kết quả ra màn hình console chỉ trong chưa đầy 0.5 giây.", bold_prefix="• ", indent=True)
    add_p(doc, text="Phân tách rạch ròi hai mức độ kiểm thử: Xây dựng hai entrypoint riêng biệt là UnitTestRunner.java (chuyên trách bộ 18 Unit Tests cô lập) và IntegrationTestRunner.java (chuyên trách bộ 6 Integration Tests kết nối CSDL), cho phép kích hoạt linh hoạt thông qua các tệp lệnh batch một chạm (test-unit.bat, test-integration.bat, test.bat).", bold_prefix="• ", indent=True)
    add_p(doc, text="Kỹ thuật cô lập kiểm thử bằng In-Memory Repository: Lớp InMemoryRepository được xây dựng để lưu trữ và quản lý dữ liệu hoàn toàn trên bộ nhớ RAM, phục vụ riêng cho tầng Unit Test. Cơ chế này giúp cô lập 100% logic của tầng Service, loại bỏ hoàn toàn sự phụ thuộc vào trạng thái tệp tin CSDL trên đĩa cứng.", bold_prefix="• ", indent=True)
    add_p(doc, text="Kỹ thuật giả lập đối tác bên ngoài bằng Stub (FakeFailingGateway): Hiện thực giao diện PaymentGateway để chủ động giả lập kịch bản cổng thanh toán đối tác bị sập/mất kết nối mạng, từ đó kiểm chứng khả năng bắt ngoại lệ của PaymentService mà không cần ngắt mạng internet thật.", bold_prefix="• ", indent=True)
    add_p(doc, text="Cơ chế CSDL tạm cô lập cho Integration Test: Mỗi bài test tích hợp sẽ tự động sinh một tệp CSDL SQLite riêng biệt bằng hàm File.createTempFile('coffee-shop-pos-test-', '.db') và tự động giải phóng khi hoàn tất (deleteOnExit). Giải pháp này ngăn chặn triệt để hiện tượng xung đột dữ liệu chéo giữa các bài test và không làm ảnh hưởng đến tệp cơ sở dữ liệu pos_data.db chính của ứng dụng.", bold_prefix="• ", indent=True)

    # -------------------------------------------------------------------------
    # 1.2.2. Chi tiết các thành phần nghiệp vụ và Design Patterns
    # -------------------------------------------------------------------------
    add_h1(doc, "1.2.2. Chi tiết các thành phần nghiệp vụ và Design Patterns đưa vào kiểm thử")
    add_p(doc, text="Quân đã thiết kế và triển khai trọn bộ 24 ca kiểm thử tự động hóa, phân bổ chặt chẽ theo hai tầng cấu trúc:")
    
    add_h2(doc, "A. Bộ kiểm thử đơn vị (Unit Testing - 18 Test Cases từ TC01 đến TC18)")
    add_p(doc, text="Tầng này tập trung kiểm tra tính đúng đắn của từng Class, thuật toán tính toán và logic nội tại của các Design Patterns trong môi trường hoàn toàn cô lập:")
    
    add_p(doc, text="Decorator Pattern (TC02): Kiểm tra công thức tính giá lũy tiến khi bọc liên tiếp các lớp topping và kích cỡ. Đồ uống gốc BaseCoffee (30.000đ) bọc thêm PearlDecorator (+7.000đ) và LargeSizeDecorator (+10.000đ) phải cho ra kết quả chính xác tuyệt đối là 47.000đ.", bold_prefix="1. ", indent=True)
    add_p(doc, text="Strategy Pattern (TC03): Kiểm tra việc gán động chiến lược chiết khấu PercentDiscountStrategy(10) tại runtime; tổng giá trị đơn hàng 100.000đ sau khi tính toán lại phải giảm còn đúng 90.000đ.", bold_prefix="2. ", indent=True)
    add_p(doc, text="State Pattern (TC04, TC05): TC04 kiểm tra luân chuyển trạng thái hợp lệ (Pending -> Preparing -> Ready). TC05 kiểm tra nhánh khóa trạng thái: khi đơn hàng đã thanh toán (PaidState), nếu gọi startPreparing() thì hệ thống bắt buộc phải ném InvalidStateTransitionException để ngăn ngừa hành vi can thiệp trái phép vào đơn đã khóa.", bold_prefix="3. ", indent=True)
    add_p(doc, text="Adapter Pattern (TC06, TC18): TC06 kiểm tra việc thanh toán thành công qua MomoAdapter, đơn hàng chuyển sang PAID và sinh mã giao dịch dạng MOMO-xxxx. TC18 kiểm tra nhánh lỗi: khi thanh toán qua FakeFailingGateway (giả lập lỗi), đơn hàng bắt buộc phải giữ nguyên trạng thái READY và không được tạo bản ghi thanh toán.", bold_prefix="4. ", indent=True)
    add_p(doc, text="Observer Pattern (TC07): Kiểm tra cơ chế phát sự kiện thời gian thực; khi nhân viên pha chế bấm hoàn thành món (markReady), màn hình thu ngân (CashierScreen) đăng ký lắng nghe trước đó phải nhận được chính xác thông báo 'ready for pickup'.", bold_prefix="5. ", indent=True)
    add_p(doc, text="Factory Method (TC16): Kiểm tra các lớp CoffeeFactory, TeaFactory, MatchaFactory khởi tạo đúng các đối tượng đồ uống tương ứng, bảo toàn đúng tên gọi và đơn giá cơ sở.", bold_prefix="6. ", indent=True)
    add_p(doc, text="Singleton Pattern (TC17): Kiểm tra việc gọi lặp lại AppConfig.getInstance() và DatabaseConnection.getInstance() phải trả về cùng một tham chiếu đối tượng duy nhất trên bộ nhớ RAM (toán tử '==' trả về true).", bold_prefix="7. ", indent=True)
    add_p(doc, text="Xác thực & Phân quyền (TC01): Kiểm tra quy trình đăng nhập tài khoản cashier01 với mật khẩu 123 qua AuthService, đảm bảo trả về đúng quyền hạn CASHIER.", bold_prefix="8. ", indent=True)
    add_p(doc, text="Quản trị Menu & Chặn dữ liệu bẩn (TC08, TC09): TC08 kiểm tra thêm món mới, cập nhật giá và tắt kích hoạt món nước. TC09 kiểm thử các ca ngoại lệ: tên món để rỗng, đơn giá âm (-1), sai nhóm danh mục, hoặc thêm topping trùng tên; MenuService phải ném IllegalArgumentException để từ chối dữ liệu bẩn.", bold_prefix="9. ", indent=True)
    add_p(doc, text="Quản lý tồn kho nguyên liệu (TC10, TC11): TC10 kiểm tra nghiệp vụ trừ kho tự động khi đơn vào bếp (1 ly cà phê sữa tiêu hao 18g hạt) và hoàn kho đủ 18g khi hủy đơn chưa thanh toán. TC11 kiểm tra điều kiện chặn: nếu kho chỉ còn 5g cà phê (thiếu so với định mức 18g), hệ thống ném InventoryException và giữ nguyên đơn ở mức PENDING.", bold_prefix="10. ", indent=True)
    add_p(doc, text="Hóa đơn & Xuất ảnh (TC12, TC13): TC12 kiểm tra nội dung text của hóa đơn có đầy đủ tên quán, chi tiết món, tổng tiền và mã thanh toán. TC13 kiểm tra ReceiptImageService kết xuất thành công tệp ảnh test-receipt.png với kích thước > 0 byte phục vụ in ấn.", bold_prefix="11. ", indent=True)
    add_p(doc, text="Quản trị tài khoản nhân viên (TC14): Kiểm tra UserService thêm nhân viên mới, thao tác khóa tài khoản (active = false), và chặn đứng việc tạo trùng username.", bold_prefix="12. ", indent=True)
    add_p(doc, text="Quy trình giỏ hàng POS (TC15): Khi thêm cùng một món 2 lần, giỏ hàng tự động gộp số lượng thành 1 dòng; cho phép chỉnh sửa số lượng khi đơn còn PENDING và khóa cứng không cho sửa sau khi đơn đã gửi vào bếp pha chế.", bold_prefix="13. ", indent=True)

    add_h2(doc, "B. Bộ kiểm thử tích hợp (Integration Testing - 6 Test Cases từ TC19 đến TC24)")
    add_p(doc, text="Tầng này kiểm tra sự phối hợp đồng bộ giữa tầng Dịch vụ (Service), tầng Truy xuất dữ liệu (SqliteRepository) và Hệ quản trị cơ sở dữ liệu SQLite vật lý:")
    
    add_p(doc, text="TC19 (Lưu trữ và phục hồi vòng đời đơn hàng): Tạo đơn hàng hoàn chỉnh gồm các tùy biến Decorator (Size L, Trân châu), thanh toán qua Momo, lưu xuống SQLite. Mở một kết nối mới độc lập để đọc lại, kiểm tra trạng thái PAID, số lượng món và topping đi kèm được khôi phục chính xác 100%.", bold_prefix="• ", indent=True)
    add_p(doc, text="TC20 (Tích hợp Admin CRUD với CSDL): Gọi các hàm thêm mới món nước, tắt topping, khóa nhân viên qua MenuService và UserService. Đóng kết nối, mở lại từ CSDL và xác nhận các thay đổi dữ liệu đã được lưu trữ bền vững.", bold_prefix="• ", indent=True)
    add_p(doc, text="TC21 (Tích hợp Nhật ký kiểm toán - Audit Trail): Sau khi đơn hàng trải qua quy trình đặt món và trừ kho, kiểm tra trực tiếp trong CSDL bằng câu lệnh SQL: bảng order_status_history phải ghi nhận đủ >= 4 trạng thái luân chuyển và bảng inventory_transactions phải ghi nhận việc trừ kho nguyên liệu.", bold_prefix="• ", indent=True)
    add_p(doc, text="TC22 (Truy vấn dữ liệu vận hành): Kiểm tra OperationsService truy vấn và bóc tách chính xác lịch sử trạng thái đơn hàng và nhật ký biến động kho từ các bảng dữ liệu thật.", bold_prefix="• ", indent=True)
    add_p(doc, text="TC23 (Quy trình Sao lưu và Phục hồi CSDL): Gọi hàm backupDatabase() để kết xuất một file snapshot dạng .db. Kiểm tra tệp sao lưu tồn tại trên ổ cứng, sau đó mở file này bằng một Repository mới để xác thực toàn bộ dữ liệu đơn hàng bên trong vẫn nguyên vẹn.", bold_prefix="• ", indent=True)
    add_p(doc, text="TC24 (Cấu hình định mức công thức pha chế): Tích hợp việc lưu định mức nguyên liệu tiêu hao của món nước (RecipeItem) vào SQLite (gán 180g bơ cho món sinh tố) và kiểm tra thao tác xóa công thức hoạt động chuẩn xác trong CSDL.", bold_prefix="• ", indent=True)

    # Bảng 1.1: Danh mục 24 Test Cases
    p_cap1 = doc.add_paragraph()
    p_cap1.paragraph_format.space_before = Pt(8)
    p_cap1.paragraph_format.space_after = Pt(4)
    r_cap1 = p_cap1.add_run("Bảng 1.1: Danh mục tổng hợp 24 Test Cases tự động hóa phân hệ Backend")
    r_cap1.font.name = 'Times New Roman'
    r_cap1.font.size = Pt(11)
    r_cap1.font.bold = True
    r_cap1.font.italic = True
    
    tc_headers = ["STT", "Mã TC", "Loại kiểm thử", "Đối tượng / Mẫu thiết kế", "Nội dung & Mục đích kiểm thử", "Kết quả"]
    tc_data = [
        ["1", "TC01", "Unit Test", "AuthService", "Xác thực phân quyền tài khoản thu ngân (cashier01 / 123)", "Pass"],
        ["2", "TC02", "Unit Test", "Decorator Pattern", "Tính lũy tiến giá đồ uống qua PearlDecorator & LargeSizeDecorator (47.000 ₫)", "Pass"],
        ["3", "TC03", "Unit Test", "Strategy Pattern", "Áp dụng chiến lược chiết khấu 10% (PercentDiscountStrategy) trên đơn 100k -> 90k", "Pass"],
        ["4", "TC04", "Unit Test", "State Pattern", "Luân chuyển vòng đời hợp lệ của đơn hàng (Pending -> Preparing -> Ready)", "Pass"],
        ["5", "TC05", "Unit Test", "State Pattern", "Khóa chuyển đổi trạng thái phi lý khi đơn hàng đã thanh toán (PaidState)", "Pass"],
        ["6", "TC06", "Unit Test", "Adapter Pattern", "Tích hợp thanh toán ví điện tử Momo thành công sinh mã MOMO-xxxx", "Pass"],
        ["7", "TC07", "Unit Test", "Observer Pattern", "Bắn thông báo thời gian thực từ Bếp sang Thu ngân khi đơn hoàn thành", "Pass"],
        ["8", "TC08", "Unit Test", "MenuService", "Thao tác thêm, cập nhật, tắt kích hoạt món nước trong thực đơn", "Pass"],
        ["9", "TC09", "Unit Test", "MenuService", "Xác thực dữ liệu đầu vào thực đơn (chặn tên rỗng, giá âm, sai danh mục)", "Pass"],
        ["10", "TC10", "Unit Test", "Inventory Logic", "Tự động trừ kho nguyên liệu khi vào bếp và hoàn trả lại kho khi hủy đơn", "Pass"],
        ["11", "TC11", "Unit Test", "Inventory Guard", "Chặn đẩy đơn hàng vào bếp pha chế khi tồn kho nguyên liệu không đủ định mức", "Pass"],
        ["12", "TC12", "Unit Test", "ReceiptService", "Cấu trúc hóa đơn thanh toán (tên quán, tên món, tổng tiền, mã giao dịch)", "Pass"],
        ["13", "TC13", "Unit Test", "ReceiptImageService", "Kết xuất hóa đơn đồ họa dạng tệp ảnh PNG phục vụ in ấn", "Pass"],
        ["14", "TC14", "Unit Test", "UserService", "Quản lý nhân viên, kiểm tra trùng lặp tài khoản và khóa/mở khóa", "Pass"],
        ["15", "TC15", "Unit Test", "Cart Logic", "Quy trình giỏ hàng: tự gộp món trùng và khóa sửa số lượng khi đơn đã vào bếp", "Pass"],
        ["16", "TC16", "Unit Test", "Factory Method", "Khởi tạo đúng các họ đồ uống cụ thể qua Coffee/Tea/MatchaFactory", "Pass"],
        ["17", "TC17", "Unit Test", "Singleton Pattern", "Đảm bảo tính duy nhất tham chiếu đối tượng của AppConfig & DatabaseConnection", "Pass"],
        ["18", "TC18", "Unit Test", "Adapter Pattern", "Xử lý sự cố ngoại lệ khi cổng thanh toán đối tác trả về lỗi (FakeFailingGateway)", "Pass"],
        ["19", "TC19", "Integ Test", "Order + SQLite", "Tích hợp lưu trữ và nạp lại toàn bộ vòng đời đơn hàng phức tạp với SQLite thật", "Pass"],
        ["20", "TC20", "Integ Test", "Admin + SQLite", "Tích hợp các thao tác CRUD menu, topping và tài khoản với CSDL SQLite", "Pass"],
        ["21", "TC21", "Integ Test", "Audit Trail + SQLite", "Tích hợp ghi nhật ký lịch sử trạng thái đơn và nhật ký trừ kho vào CSDL", "Pass"],
        ["22", "TC22", "Integ Test", "Operations + SQLite", "Tích hợp truy vấn phân hệ vận hành và báo cáo kiểm toán từ CSDL thật", "Pass"],
        ["23", "TC23", "Integ Test", "Backup / Restore", "Tích hợp quy trình sao lưu snapshot tệp tin CSDL vật lý và phục hồi kiểm tra", "Pass"],
        ["24", "TC24", "Integ Test", "RecipeItem + SQLite", "Tích hợp cấu hình định mức công thức tiêu hao nguyên liệu với CSDL", "Pass"]
    ]
    t1 = doc.add_table(rows=len(tc_data) + 1, cols=len(tc_headers))
    t1.style = 'Table Grid'
    format_custom_table(t1, [0.5, 0.7, 1.0, 1.7, 2.8, 0.7], tc_headers, tc_data, hdr_bg='1F4E79', hdr_text_white=True, font_size_pt=8.5)

    # -------------------------------------------------------------------------
    # 1.2.3. Đo lường Code Coverage bằng JaCoCo
    # -------------------------------------------------------------------------
    add_h1(doc, "1.2.3. Đo lường Độ bao phủ mã nguồn (Code Coverage) bằng công cụ JaCoCo")
    add_p(doc, text="Để đánh giá chất lượng của bộ kiểm thử một cách khách quan theo tiêu chuẩn kỹ nghệ phần mềm chứ không chỉ dừng lại ở việc đếm số bài test passed, Quân đã tích hợp công cụ JaCoCo (Java Code Coverage) phiên bản 0.8.14 vào quy trình kiểm thử tự động.")
    add_p(doc, text="Quá trình đo lường được thực hiện bằng cách gắn JaCoCo JavaAgent trực tiếp vào máy ảo JVM khi kích hoạt TestRunner, ghi nhận toàn bộ dữ liệu thực thi vào tệp nhị phân jacoco.exec, sau đó sử dụng thư viện jacococli.jar để bóc tách số liệu và xuất báo cáo trực quan dạng trang web HTML.")
    
    # Bảng 1.2: Bảng số liệu tổng thể
    p_cap2 = doc.add_paragraph()
    p_cap2.paragraph_format.space_before = Pt(8)
    p_cap2.paragraph_format.space_after = Pt(4)
    r_cap2 = p_cap2.add_run("Bảng 1.2: Bảng tổng hợp mức độ bao phủ mã nguồn Backend đo bằng JaCoCo")
    r_cap2.font.name = 'Times New Roman'
    r_cap2.font.size = Pt(11)
    r_cap2.font.bold = True
    r_cap2.font.italic = True
    
    cov_sum_headers = ["Phân vùng mã nguồn kiểm thử", "Line Coverage (Dòng lệnh)", "Branch Coverage (Rẽ nhánh)", "Instruction Coverage (Mã máy)", "Đánh giá chất lượng"]
    cov_sum_data = [
        ["Toàn bộ Core Backend (Domain + Patterns + Service + SQLite)", "74.3% (1.627 / 2.189)", "51.0% (275 / 539)", "74.1% (7.796 / 10.527)", "Đạt chuẩn công nghiệp (khuyến nghị >= 70%)"],
        ["8 Đơn vị mã nguồn Hộp trắng trọng yếu (Chương 4)", "100%", "100%", "100%", "Đạt tối đa (28/28 đường cơ sở McCabe, 100% DU-chains)"],
        ["Toàn bộ Dự án (Tính gộp cả mã giao diện đồ họa Swing UI)", "32.7% (1.627 / 4.978)", "22.9% (275 / 1.200)", "30.4% (7.796 / 25.674)", "Tầng UI (~2.800 dòng) được kiểm thử riêng bằng Playwright E2E"]
    ]
    t2 = doc.add_table(rows=len(cov_sum_data) + 1, cols=len(cov_sum_headers))
    t2.style = 'Table Grid'
    format_custom_table(t2, [2.3, 1.3, 1.3, 1.3, 1.8], cov_sum_headers, cov_sum_data, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=8.5)
    
    add_p(doc, text="Báo cáo chi tiết của JaCoCo cho thấy tỷ lệ bao phủ rất cao và đồng đều tại các gói chức năng Backend then chốt:")
    
    # Bảng 1.3: Chi tiết từng package
    p_cap3 = doc.add_paragraph()
    p_cap3.paragraph_format.space_before = Pt(8)
    p_cap3.paragraph_format.space_after = Pt(4)
    r_cap3 = p_cap3.add_run("Bảng 1.3: Chi tiết tỷ lệ bao phủ mã nguồn theo từng Package Backend")
    r_cap3.font.name = 'Times New Roman'
    r_cap3.font.size = Pt(11)
    r_cap3.font.bold = True
    r_cap3.font.italic = True
    
    pkg_headers = ["Gói chức năng (Package)", "Line Coverage", "Branch Coverage", "Instruction Cov.", "Nội dung & Mục tiêu kiểm thử"]
    pkg_data = [
        ["com.coffeeshop.domain.patterns.singleton", "90.9%", "75.0%", "93.0%", "Quản lý duy nhất AppConfig và DatabaseConnection"],
        ["com.coffeeshop (Context & App)", "84.8%", "57.1%", "85.0%", "Khởi tạo ngữ cảnh, cấu hình hệ thống và nạp repository"],
        ["com.coffeeshop.infrastructure", "82.4%", "60.0%", "83.0%", "SqliteRepository, InMemoryRepository, Audit Trail SQL"],
        ["com.coffeeshop.domain.patterns.factory", "77.8%", "n/a", "76.0%", "Khởi tạo đồ uống Coffee, Tea, Matcha qua Factory"],
        ["com.coffeeshop.domain.model", "77.2%", "33.3%", "64.0%", "Thực thể Order, OrderItem, User, InventoryItem, Topping"],
        ["com.coffeeshop.service", "75.7%", "57.8%", "72.0%", "Các dịch vụ Order, Payment, Menu, Inventory, Operations"],
        ["com.coffeeshop.domain.patterns.decorator", "66.7%", "n/a", "57.0%", "Tính lũy tiến giá đồ uống qua Pearl và LargeSize"],
        ["com.coffeeshop.domain.patterns.state", "63.3%", "n/a", "57.0%", "Vòng đời đơn hàng Pending, Preparing, Ready, Paid"],
        ["com.coffeeshop.domain.patterns.observer", "53.3%", "66.7%", "55.0%", "Phát sự kiện thời gian thực từ Bếp sang Thu ngân"],
        ["com.coffeeshop.domain.patterns.adapter", "53.8%", "25.0%", "48.0%", "Tích hợp cổng Momo và xử lý ngoại lệ FakePay"],
        ["com.coffeeshop.domain.patterns.strategy", "41.7%", "35.7%", "46.0%", "Các thuật toán chiết khấu phần trăm linh hoạt"],
        ["com.coffeeshop.presentation (Giao diện UI)", "0.0%", "0.0%", "0.0%", "Kiểm thử tự động bằng Playwright E2E (không qua Unit Test)"]
    ]
    t3 = doc.add_table(rows=len(pkg_data) + 1, cols=len(pkg_headers))
    t3.style = 'Table Grid'
    format_custom_table(t3, [2.2, 0.9, 1.0, 1.0, 2.3], pkg_headers, pkg_data, hdr_bg='1F4E79', hdr_text_white=True, font_size_pt=8.5)
    
    add_p(doc, text="Giao diện báo cáo JaCoCo HTML trực quan: Toàn bộ dữ liệu chi tiết của từng class và từng dòng code được lưu trữ tại build/jacoco-report/index.html. Khi mở trên trình duyệt, các dòng lệnh đã được test bao phủ được tô màu xanh lá cây, các nhánh điều kiện mới kiểm tra 1 phía được tô màu vàng, và các đoạn mã chưa chạm tới được tô màu đỏ (chủ yếu là các phương thức getter/setter phụ hoặc mã giao diện).", italic=True)

    # -------------------------------------------------------------------------
    # 1.2.4. Kết luận
    # -------------------------------------------------------------------------
    add_h1(doc, "1.2.4. Kết luận về hiệu quả kiểm thử phân hệ Backend")
    add_p(doc, text="Việc triển khai thành công hệ thống kiểm thử tự động nội bộ kết hợp đo lường JaCoCo đã mang lại các giá trị thực tế rõ rệt:")
    add_p(doc, text="Phát hiện và loại trừ sớm các lỗi nghiệp vụ nghiêm trọng: Nhờ các test case biên như TC05 (chặn can thiệp đơn đã thanh toán), TC09 (chặn dữ liệu bẩn giá âm/tên rỗng) và TC11 (chặn vào bếp khi tồn kho không đủ), nhóm đã phát hiện và xử lý dứt điểm các lỗi logic ngay tại tầng Service trước khi kết nối lên tầng giao diện.", bold_prefix="1. ", indent=True)
    add_p(doc, text="Đảm bảo tính toàn vẹn của kiến trúc Design Patterns: Bộ 18 Unit Tests đóng vai trò như một bộ rào chắn kỹ thuật (Safety Net), đảm bảo rằng mỗi khi có thành viên trong nhóm tối ưu hóa hay tái cấu trúc mã nguồn (Refactoring) thì hành vi của 6 mẫu thiết kế phần mềm vẫn luôn chạy chính xác theo thiết kế ban đầu.", bold_prefix="2. ", indent=True)
    add_p(doc, text="Minh chứng định lượng thuyết phục cho báo cáo: Thay vì chỉ mô tả chung chung mang tính cảm tính, nhóm đã có số liệu định lượng chuẩn xác với 100% test case vượt qua (24/24 Pass) và độ bao phủ dòng lệnh tầng Core Backend đạt 74.3%, đáp ứng xuất sắc các tiêu chuẩn đánh giá của học phần Kiểm thử & Đảm bảo chất lượng phần mềm.", bold_prefix="3. ", indent=True)

    doc.save(OUT_DOCX)
    print(f"Successfully generated Word document at: {OUT_DOCX}")

if __name__ == '__main__':
    build_docx()
