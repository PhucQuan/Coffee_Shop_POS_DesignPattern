# -*- coding: utf-8 -*-
"""
Script cập nhật nội dung Kiểm thử đơn vị (Unit Testing) và Kiểm thử tích hợp (Integration Testing)
vào tài liệu báo cáo chính thức: docs/Nhom15_FinalProject.docx.
Đảm bảo định dạng chuẩn Times New Roman, cỡ chữ chuẩn, bảng biểu sắc nét.
"""

import sys
sys.stdout.reconfigure(encoding='utf-8')
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

def set_cell_background(cell, hex_color):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=60, bottom=60, left=80, right=80):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def format_custom_table(table, col_widths, headers, data_rows, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=9.5):
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
        set_cell_margins(cell, top=80, bottom=80, left=100, right=100)
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
            set_cell_margins(cell, top=60, bottom=60, left=90, right=90)
            p = cell.paragraphs[0]
            if col_idx in [0, 1, 7] or (len(str(val)) <= 8 and '\n' not in str(val)):
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            else:
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.font.name = 'Times New Roman'
                r.font.size = Pt(font_size_pt)
                if 'Pass' in str(val) or 'Đạt' in str(val):
                    r.font.bold = True
                    r.font.color.rgb = RGBColor(0x00, 0x80, 0x00)

def set_p_content(p, text="", bold_prefix="", indent=False, italic=False, space_after=4, style=None):
    if style:
        p.style = style
    p.text = ""
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
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

def set_h2(p, text):
    p.style = 'Heading 2'
    p.text = ""
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(12)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(13.5)
    run.font.bold = True
    run.font.color.rgb = RGBColor(0x0F, 0x47, 0x61)
    return p

def set_h3(p, text):
    p.style = 'Heading 3'
    p.text = ""
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(13)
    run.font.bold = True
    return p

def run_update():
    doc_path = 'docs/Nhom15_FinalProject.docx'
    print(f"Loading document: {doc_path}")
    doc = docx.Document(doc_path)
    
    # -------------------------------------------------------------------------
    # 1. UPDATE CHƯƠNG 2: KẾ HOẠCH KIỂM THỬ (paragraphs 185 - 197)
    # -------------------------------------------------------------------------
    ch2_idx = None
    for i, p in enumerate(doc.paragraphs):
        if 'CHƯƠNG 2: KẾ HOẠCH KIỂM THỬ' in p.text:
            ch2_idx = i
            break
            
    if ch2_idx is not None:
        print(f"Found Chương 2 at paragraph index {ch2_idx}")
        # Paragraphs to replace: 186 to 197
        # We will populate them with structured headings and content
        ch2_contents = [
            ("h2", "2.1. Chiến lược kiểm thử phần mềm (Testing Strategy)"),
            ("body", "Để bảo đảm chất lượng toàn diện của hệ thống Coffee Shop POS trước khi đưa vào vận hành thực tế, nhóm nghiên cứu xây dựng chiến lược kiểm thử phân tầng theo mô hình Kim tự tháp kiểm thử (Testing Pyramid) kết hợp mô hình chữ V (V-Model). Chiến lược này chia hoạt động kiểm thử thành 3 tầng cốt lõi:"),
            ("bullet", "Tầng đáy - Kiểm thử đơn vị (Unit Testing): Tập trung kiểm thử độc lập từng thành phần mã nguồn, hàm logic nghiệp vụ và 6 mẫu thiết kế phần mềm với môi trường cô lập tuyệt đối bằng Repository giả lập (In-Memory Repository). Đây là tầng có số lượng ca kiểm thử lớn nhất nhằm phát hiện lỗi sớm với chi phí sửa chữa thấp nhất."),
            ("bullet", "Tầng giữa - Kiểm thử tích hợp (Integration Testing): Đánh giá khả năng kết hợp và tương tác dữ liệu giữa các dịch vụ nghiệp vụ (OrderService, MenuService, UserService, OperationsService) với hệ quản trị cơ sở dữ liệu vật lý SQLite (pos_data.db), cơ chế giao dịch kiểm toán (Audit Trail) và sao lưu tệp tin snapshot."),
            ("bullet", "Tầng đỉnh - Kiểm thử hệ thống & Hộp đen tự động (System / E2E Testing): Đóng vai trò người dùng cuối (Thu ngân, Pha chế, Quản trị viên) thao tác trên giao diện để kiểm thử 20 kịch bản nghiệp vụ cốt lõi và thực thi tự động qua framework Microsoft Playwright kèm ghi hình video minh chứng HD."),
            ("h2", "2.2. Các cấp độ và loại hình kiểm thử áp dụng"),
            ("body", "Trong khuôn khổ đồ án, nhóm triển khai đồng bộ 6 kiểu kiểm thử chuyên sâu được giảng dạy trong học phần Kiểm thử & Đảm bảo chất lượng phần mềm:"),
            ("bullet", "Kiểm thử chức năng (Functional Testing): Đánh giá tính chính xác của các yêu cầu nghiệp vụ quản lý bán hàng theo đặc tả Use Case."),
            ("bullet", "Kiểm thử đơn vị (Unit Testing): Kiểm thử độc lập từng phương thức, thuật toán tính giá lũy tiến (Decorator), công thức chiết khấu (Strategy) và chuyển trạng thái đơn (State)."),
            ("bullet", "Kiểm thử tích hợp (Integration Testing): Kiểm thử sự phối hợp giữa Service và tầng dữ liệu vật lý SQLite JDBC."),
            ("bullet", "Kiểm thử hộp trắng (White-box Testing): Phân tích chi tiết đồ thị dòng điều khiển (CFG), độ phức tạp chu trình McCabe, đồ thị dòng dữ liệu (DFG) và kiểm thử đời sống biến (DU-chain) cho 8 đơn vị mã nguồn trọng yếu."),
            ("bullet", "Kiểm thử hộp đen (Black-box Testing): Xây dựng bộ 20 Test Cases chuẩn hóa vận dụng phân vùng tương đương (EP), phân tích giá trị biên (BVA), bảng quyết định và chuyển trạng thái."),
            ("bullet", "Kiểm thử tự động (Automated Testing): Tự động hóa kiểm thử hồi quy giao diện web và tạo video phục vụ nghiệm thu."),
            ("h2", "2.3. Công cụ và công nghệ kiểm thử"),
            ("body", "Hệ thống công cụ được lựa chọn đồng bộ, đảm bảo tính hiện đại và khả năng tự động hóa cao:"),
            ("bullet", "Môi trường lập trình & Test Harness: Java JDK 17 LTS, tự phát triển bộ điều phối kiểm thử TestRunner, UnitTestRunner, IntegrationTestRunner siêu nhẹ, tối ưu thời gian thực thi chỉ trong vài mili-giây."),
            ("bullet", "Cơ sở dữ liệu: SQLite 3 kết hợp SQLite JDBC Driver (sqlite-jdbc-3.46.1.3.jar) phục vụ kiểm thử tích hợp dữ liệu bền vững."),
            ("bullet", "Khung kiểm thử tự động: Microsoft Playwright (Node.js/JavaScript), trình duyệt Chromium Headless/Headed, tích hợp bộ quay video tự động HD 720p."),
            ("bullet", "Quản lý kịch bản và lỗi: Microsoft Excel để xây dựng ma trận Test Cases và phần mềm quản trị lỗi Jira để theo dõi vòng đời bug."),
            ("h2", "2.4. Môi trường và điều kiện thực thi kiểm thử"),
            ("body", "Các bài kiểm thử được thực thi trên môi trường tiêu chuẩn: Hệ điều hành Windows 11 64-bit; Runtime Node.js v20.x, Java SE 17; Mạng cục bộ Localhost cổng 5173 (Frontend React Vite) và cổng 8088 (Web API Backend); Tệp tin cơ sở dữ liệu mẫu pos_data.db đã được nạp sẵn danh mục thực đơn và dữ liệu tồn kho khởi tạo."),
            ("h2", "2.5. Kế hoạch và tiến độ phân công kiểm thử (Test Plan Schedule)"),
            ("body", "Toàn bộ tiến độ thực thi kiểm thử, phân bổ nhân lực và thời hạn hoàn thành các đầu mục kiểm thử hộp trắng, kiểm thử hộp đen và kiểm thử tự động được tổng hợp chi tiết trong Bảng 2.1 (Bảng 26) dưới đây:")
        ]
        
        # Check available paragraphs between ch2_idx + 1 and Table 26
        # Let's see: paragraphs 186 to 197 are 12 paragraphs. We have more content, so we will insert paragraphs.
        current_p_idx = ch2_idx + 1
        # Target paragraph before Table 26 is paragraph 198
        # We can update existing paragraphs 186-197 and insert extra ones right before Table 26
        for item_idx, (c_type, text_val) in enumerate(ch2_contents):
            if item_idx < 12:
                target_p = doc.paragraphs[ch2_idx + 1 + item_idx]
                if c_type == "h2":
                    set_h2(target_p, text_val)
                elif c_type == "h3":
                    set_h3(target_p, text_val)
                elif c_type == "bullet":
                    set_p_content(target_p, text=text_val, bold_prefix="• ", indent=True, space_after=3)
                else:
                    set_p_content(target_p, text=text_val, space_after=4)
            else:
                # Insert before paragraph 198 (which is now pushed down)
                anchor_p = doc.paragraphs[ch2_idx + 1 + item_idx]
                new_p = anchor_p.insert_paragraph_before()
                if c_type == "h2":
                    set_h2(new_p, text_val)
                elif c_type == "h3":
                    set_h3(new_p, text_val)
                elif c_type == "bullet":
                    set_p_content(new_p, text=text_val, bold_prefix="• ", indent=True, space_after=3)
                else:
                    set_p_content(new_p, text=text_val, space_after=4)

    # -------------------------------------------------------------------------
    # 2. UPDATE CHƯƠNG 3: MỤC 3.2 MỨC ĐỘ KIỂM THỬ (UNIT & INTEGRATION TESTING)
    # -------------------------------------------------------------------------
    sec32_idx = None
    for i, p in enumerate(doc.paragraphs):
        if '3.2. Mức độ kiểm thử' in p.text:
            sec32_idx = i
            break
            
    if sec32_idx is not None:
        print(f"Found section 3.2 at paragraph index {sec32_idx}")
        # The paragraphs under 3.2 currently are:
        # sec32_idx + 1: "Trong quá trình phát triển phần mềm..."
        # sec32_idx + 2: "Kiểm thử đơn vị (Unit Testing) tập trung..."
        # sec32_idx + 3: "Kiểm thử tích hợp (Integration Testing)..."
        # sec32_idx + 4: "Kiểm thử hệ thống (System Testing)..."
        # sec32_idx + 5: "Các mức độ kiểm thử có mối quan hệ bổ trợ..."
        # Followed by 3.3. Kỹ thuật kiểm thử
        
        # We will update these paragraphs and insert academic content and comparison tables!
        p_intro = doc.paragraphs[sec32_idx + 1]
        set_p_content(p_intro, text="Trong kỹ nghệ phần mềm và quy trình đảm bảo chất lượng, kiểm thử được phân định thành các cấp độ phân tầng chặt chẽ từ nhỏ đến lớn nhằm kiểm soát chất lượng ở từng chặng phát triển. Đối với hệ thống Coffee Shop POS, hai cấp độ kiểm thử kỹ thuật then chốt được giảng dạy và áp dụng sâu sắc là Kiểm thử đơn vị (Unit Testing) và Kiểm thử tích hợp (Integration Testing), kết hợp cùng Kiểm thử hệ thống (System Testing).")
        
        # We will insert new content before paragraph sec32_idx + 2
        anchor_32 = doc.paragraphs[sec32_idx + 2]
        
        unit_integ_sections = [
            ("h3", "3.2.1. Kiểm thử đơn vị (Unit Testing)"),
            ("body", "Khái niệm & Bản chất: Kiểm thử đơn vị (Unit Testing) là cấp độ kiểm thử mức thấp nhất trong quy trình kiểm thử phần mềm, tập trung kiểm tra tính đúng đắn của từng đơn vị mã nguồn nhỏ nhất có thể cô lập và thực thi độc lập (như một hàm, phương thức hoặc một lớp cụ thể)."),
            ("body", "Mục tiêu và Tầm quan trọng:"),
            ("bullet", "Phát hiện sai sót sớm ngay trong quá trình viết mã nguồn, bao gồm lỗi tính toán sai, rẽ nhánh điều kiện if-else không đầy đủ, vòng lặp vô hạn, hoặc xử lý thiếu các giá trị biên null/rỗng."),
            ("bullet", "Tiết kiệm chi phí khắc phục lỗi: Theo định luật chi phí sửa lỗi của Boehm (Cost of Change Curve), chi phí để sửa một lỗi phát hiện tại tầng Unit Testing là rẻ nhất (chỉ tốn vài phút sửa mã nguồn); nếu lỗi này lọt qua các tầng tích hợp hoặc hệ thống, chi phí sửa chữa có thể tăng gấp hàng chục đến hàng trăm lần do ảnh hưởng dây chuyền."),
            ("bullet", "Hỗ trợ tái cấu trúc mã nguồn (Refactoring): Bộ kiểm thử đơn vị đóng vai trò như một lưới an toàn (Safety Net), cho phép lập trình viên tối ưu hóa thuật toán mà không sợ làm hỏng các hành vi nghiệp vụ đã hoạt động đúng trước đó."),
            ("body", "Nguyên lý F.I.R.S.T trong Kiểm thử đơn vị: Để đạt hiệu quả tối đa, các ca kiểm thử đơn vị trong dự án được thiết kế tuân thủ nghiêm ngặt 5 nguyên tắc F.I.R.S.T:"),
            ("bullet", "Fast (Nhanh): Thời gian thực thi của mỗi bài test tính bằng mili-giây, toàn bộ 18 bài test chỉ mất dưới 0.1 giây để hoàn tất."),
            ("bullet", "Independent / Isolated (Độc lập / Cô lập): Không có bài test nào phụ thuộc vào kết quả hoặc dữ liệu của bài test khác."),
            ("bullet", "Repeatable (Có thể lặp lại): Có thể chạy trên bất kỳ máy tính nào, trong bất kỳ môi trường nào đều cho ra kết quả đồng nhất."),
            ("bullet", "Self-validating (Tự xác thực): Mỗi bài test tự động đưa ra kết luận Pass hoặc Fail thông qua các câu lệnh xác nhận (assertEquals, assertTrue, assertThrows) mà không cần con người đọc log kiểm tra bằng mắt."),
            ("bullet", "Timely (Kịp thời): Được viết song song và nghiệm thu ngay cùng các module nghiệp vụ."),
            ("body", "Kỹ thuật cô lập phụ thuộc bằng Test Doubles (Mocks / Stubs / Fakes): Trong thực tế, các lớp nghiệp vụ thường phụ thuộc vào cơ sở dữ liệu hoặc dịch vụ mạng bên ngoài. Để bảo đảm kiểm thử đơn vị thuần túy không bị ảnh hưởng bởi lỗi hạ tầng, nhóm đã áp dụng kỹ thuật Test Doubles:"),
            ("bullet", "In-Memory Repository (Fake Data Store): Thay vì kết nối tới cơ sở dữ liệu SQLite thật trên đĩa cứng, lớp InMemoryRepository được sử dụng để lưu trữ dữ liệu tạm thời trên bộ nhớ RAM, giúp cô lập hoàn toàn logic nghiệp vụ của OrderService, MenuService, UserService."),
            ("bullet", "Fake Gateway (Stubbing): Xây dựng lớp FakeFailingGateway để chủ động mô phỏng tình huống cổng thanh toán đối tác bị sập/mất kết nối mạng, từ đó xác thực logic xử lý ngoại lệ của PaymentService mà không cần ngắt mạng thật."),
            ("body", "Triển khai trong dự án Coffee Shop POS:"),
            ("bullet", "Bộ 18 Unit Test Cases (TC01 đến TC18) bao phủ trọn vẹn 6 mẫu thiết kế Design Patterns (Factory Method, Decorator, Strategy, State, Adapter, Singleton) và toàn bộ các quy tắc nghiệp vụ đơn lẻ."),
            ("bullet", "8 đơn vị mã nguồn hộp trắng chuyên sâu tại Chương 4 được lập đồ thị dòng điều khiển (CFG), tính độ phức tạp McCabe C = P + 1, thiết kế ca kiểm thử cho từng đường thi hành độc lập, lập đồ thị dòng dữ liệu (DFG) và kiểm thử đời sống biến (DU-chain)."),
            
            ("h3", "3.2.2. Kiểm thử tích hợp (Integration Testing)"),
            ("body", "Khái niệm & Bản chất: Kiểm thử tích hợp (Integration Testing) là cấp độ kiểm thử tiếp theo sau Kiểm thử đơn vị, tập trung vào việc ghép nối hai hoặc nhiều đơn vị/module phần mềm đã vượt qua Unit Test lại với nhau nhằm kiểm tra giao diện (Interface), luồng dữ liệu truyền nhận và sự phối hợp tương tác giữa chúng."),
            ("body", "Các loại lỗi mà Unit Testing bỏ sót nhưng Integration Testing phát hiện được:"),
            ("bullet", "Lỗi bất đồng bộ giao diện & kiểu dữ liệu (Interface & Data Type Mismatch): Tham số đầu ra của module này không khớp với định dạng hoặc kỳ vọng của module nhận."),
            ("bullet", "Lỗi xử lý giao dịch cơ sở dữ liệu (Database Transaction Management): Trạng thái dữ liệu bị mất tính toàn vẹn (ACID) khi một thao tác ghi thành công nhưng thao tác liên đới thất bại mà không được Rollback kịp thời."),
            ("bullet", "Lỗi xung đột tệp tin vật lý & khóa bảng (Locking & Resource Contention): Xung đột khi nhiều tiến trình cùng truy cập tệp tin cơ sở dữ liệu SQLite (SQLite Database Lock) hoặc rò rỉ tài nguyên kết nối (Connection Leak)."),
            ("bullet", "Lỗi chuỗi tương tác đa tầng (Multi-layer Coordination): Sai lệch khi luồng nghiệp vụ di chuyển xuyên suốt qua các tầng: Presentation (Giao diện) -> Service (Nghiệp vụ) -> Repository (Truy xuất dữ liệu) -> CSDL vật lý."),
            ("body", "Các chiến lược kiểm thử tích hợp kinh điển:"),
            ("bullet", "Chiến lược Tích hợp đồng thời (Big Bang Integration): Tích hợp tất cả các module cùng một lúc rồi tiến hành kiểm thử. Phương pháp này chỉ phù hợp với các dự án nhỏ, nhược điểm là khi phát sinh lỗi thì cực kỳ khó khoanh vùng nguyên nhân."),
            ("bullet", "Chiến lược Tích hợp từ trên xuống (Top-Down Integration): Bắt đầu kiểm thử từ các module tầng cao nhất (Service, Controller) đi dần xuống các tầng thấp. Đòi hỏi phải viết các mô-đun giả lập tầng dưới (Stubs)."),
            ("bullet", "Chiến lược Tích hợp từ dưới lên (Bottom-Up Integration): Bắt đầu kiểm thử từ các module tầng thấp nhất (Database Driver, Helper, Repository) lên dần các tầng trên. Đòi hỏi phải viết các chương trình mồi điều khiển (Drivers)."),
            ("bullet", "Chiến lược Tích hợp hỗn hợp (Sandwich / Hybrid Integration): Kết hợp cả hai hướng tiếp cận: tích hợp Bottom-Up cho tầng cơ sở dữ liệu nền tảng và tích hợp Top-Down cho các luồng nghiệp vụ người dùng. Đây chính là chiến lược tối ưu nhất mà nhóm áp dụng cho hệ thống Coffee Shop POS."),
            ("body", "Triển khai trong dự án Coffee Shop POS:"),
            ("bullet", "Nhóm xây dựng bộ 6 Integration Test Cases (TC19 đến TC24) kết nối trực tiếp với hệ quản trị cơ sở dữ liệu vật lý SQLite (pos_data.db) thông qua thư viện SqliteRepository và driver JDBC sqlite-jdbc-3.46.1.3.jar."),
            ("bullet", "Kiểm thử tích hợp lưu trữ vòng đời đơn hàng, xác thực việc ghi nhận và phục hồi dữ liệu từ CSDL vật lý."),
            ("bullet", "Kiểm thử tích hợp tự động trừ kho nguyên liệu và ghi nhật ký kiểm toán vào 2 bảng CSDL thật: order_status_history (lịch sử trạng thái đơn) và inventory_transactions (nhật ký biến động kho)."),
            ("bullet", "Kiểm thử tích hợp quy trình sao lưu (Backup Snapshot) tệp tin .db vật lý và phục hồi kiểm tra tính toàn vẹn dữ liệu."),
            
            ("h3", "3.2.3. Kiểm thử hệ thống (System Testing) & Kiểm thử chấp nhận (Acceptance Testing)"),
            ("body", "Kiểm thử hệ thống (System Testing) đánh giá toàn bộ hệ thống phần mềm hoàn chỉnh trong môi trường mô phỏng thực tế để bảo đảm hệ thống đáp ứng đầy đủ các yêu cầu đặc tả chức năng và phi chức năng."),
            ("body", "Trong dự án Coffee Shop POS, cấp độ này được hiện thực hóa qua bộ 20 kịch bản kiểm thử hộp đen E2E từ góc nhìn người dùng thực tế và được tự động hóa hoàn toàn bằng công cụ Microsoft Playwright kèm ghi hình video minh chứng.")
        ]
        
        for c_type, text_val in unit_integ_sections:
            new_p = anchor_32.insert_paragraph_before()
            if c_type == "h3":
                set_h3(new_p, text_val)
            elif c_type == "bullet":
                set_p_content(new_p, text=text_val, bold_prefix="• ", indent=True, space_after=3)
            else:
                set_p_content(new_p, text=text_val, space_after=4)
                
        # Remove old placeholder paragraphs (sec32_idx + 2 to sec32_idx + 5)
        # Note: anchor_32 is at original sec32_idx + 2. We can remove it and the next 3 paragraphs.
        curr_elem = anchor_32._p
        elems_to_remove = [curr_elem]
        for _ in range(3):
            next_elem = curr_elem.getnext()
            if next_elem is not None and next_elem.tag.endswith('p'):
                elems_to_remove.append(next_elem)
                curr_elem = next_elem
            else:
                break
        for el in elems_to_remove:
            el.getparent().remove(el)
            
        print("Updated section 3.2 text and cleaned old paragraphs.")
        
        # Now add Table 3.1 (Comparison Table) and Table 3.2 (24 Test Cases Table)
        # We find paragraph with '3.3. Kỹ thuật kiểm thử' to insert before it!
        sec33_p = None
        for p in doc.paragraphs:
            if '3.3. Kỹ thuật kiểm thử' in p.text:
                sec33_p = p
                break
                
        if sec33_p is not None:
            # 1. Heading and Table 3.1
            p_cap1 = sec33_p.insert_paragraph_before()
            set_p_content(p_cap1, text="Bảng 3.1: So sánh đối chiếu toàn diện giữa Kiểm thử đơn vị (Unit Testing) và Kiểm thử tích hợp (Integration Testing)", bold_prefix="", italic=True, space_after=4)
            
            comparison_headers = ["Tiêu chí so sánh", "Kiểm thử đơn vị (Unit Testing)", "Kiểm thử tích hợp (Integration Testing)"]
            comparison_data = [
                ["1. Khái niệm & Mục tiêu", "Kiểm tra tính đúng đắn của từng đơn vị mã nguồn nhỏ nhất (hàm, method, class) một cách độc lập.", "Kiểm tra giao diện kết nối (interfaces) và sự tương tác qua lại giữa nhiều thành phần/module kết hợp."],
                ["2. Phạm vi kiểm thử", "Phạm vi hẹp nhất: từng hàm, class, thuật toán logic nội tại.", "Phạm vi trung bình: giữa Service - Repository - CSDL SQLite - File System."],
                ["3. Mức độ phụ thuộc", "Hoàn toàn cô lập; dùng Stubs, Mocks, Fakes (InMemoryRepository, FakeGateway) thay cho hạ tầng ngoài.", "Tương tác với môi trường và tài nguyên thật (CSDL SQLite vật lý, Driver JDBC, tệp sao lưu snapshot)."],
                ["4. Các lỗi phát hiện", "Lỗi cú pháp, tính toán số học, điều kiện rẽ nhánh sai, ngoại lệ logic, vòng lặp vô hạn.", "Lỗi không khớp kiểu dữ liệu (Data Mismatch), lỗi giao dịch SQL (Transaction Rollback), lỗi khóa file/CSDL."],
                ["5. Kỹ thuật áp dụng", "Kiểm thử hộp trắng (CFG, McCabe, DFG, Statement & Branch Coverage), phân tích giá trị biên (BVA).", "Chiến lược tích hợp Top-Down (dùng Stubs), Bottom-Up (dùng Drivers), hoặc Sandwich/Hybrid."],
                ["6. Tốc độ thực thi", "Cực kỳ nhanh (vài mili-giây cho mỗi test case), có thể chạy hàng trăm lần mỗi ngày.", "Chậm hơn do phải thiết lập kết nối I/O, mở tệp tin CSDL, đọc/ghi đĩa cứng."],
                ["7. Vị trí Kim tự tháp", "Tầng Đáy (chiếm số lượng lớn nhất, chi phí sửa lỗi rẻ nhất).", "Tầng Giữa (kết nối các khối độc lập trước khi chuyển lên System Testing)."],
                ["8. Minh chứng dự án", "18 Test Cases (TC01 - TC18) bao phủ 6 Design Patterns và 8 đơn vị mã nguồn hộp trắng (Chương 4).", "6 Test Cases (TC19 - TC24) tích hợp CSDL SQLite thật, bảng Audit Logs và quy trình Backup."]
            ]
            
            t1 = doc.add_table(rows=len(comparison_data) + 1, cols=len(comparison_headers))
            t1.style = 'Table Grid'
            format_custom_table(t1, [1.5, 2.7, 2.8], comparison_headers, comparison_data, hdr_bg='1F4E79', hdr_text_white=True, font_size_pt=9.5)
            # Move t1 right after p_cap1
            p_cap1._p.addnext(t1._tbl)
            
            # Spacer
            p_space = sec33_p.insert_paragraph_before()
            set_p_content(p_space, text="", space_after=6)
            
            # 2. Heading and Table 3.2
            p_cap2 = sec33_p.insert_paragraph_before()
            set_p_content(p_cap2, text="Bảng 3.2: Danh mục đối chiếu 24 Test Cases của hệ thống Coffee Shop POS (18 Unit Tests & 6 Integration Tests)", bold_prefix="", italic=True, space_after=4)
            
            tc_headers = ["STT", "Mã TC", "Loại kiểm thử", "Đối tượng kiểm thử", "Nội dung & Mục đích kiểm thử", "Dữ liệu kiểm thử", "Kết quả mong đợi", "Kết quả"]
            testcase_24_data = [
                ["1", "TC01", "Unit Test", "AuthService", "Mô hình xác thực phân quyền người dùng (Cashier/Barista/Admin)", "cashier01 / 123", "Đăng nhập thành công, trả về vai trò CASHIER", "Pass"],
                ["2", "TC02", "Unit Test", "Decorator Pattern", "Tính lũy tiến giá đồ uống qua PearlDecorator & LargeSizeDecorator", "BaseCoffee 30k + Trân châu 7k + Size L 10k", "Tổng giá tính đúng 47.000 ₫", "Pass"],
                ["3", "TC03", "Unit Test", "Strategy Pattern", "Áp dụng chiến lược chiết khấu phần trăm (PercentDiscountStrategy)", "Combo 100k, giảm giá 10%", "Tổng tiền sau giảm còn 90.000 ₫", "Pass"],
                ["4", "TC04", "Unit Test", "State Pattern", "Luân chuyển vòng đời hợp lệ của đơn hàng (Pending -> Preparing -> Ready)", "sendToKitchen() & markReady()", "Trạng thái đơn hàng chuyển thành READY", "Pass"],
                ["5", "TC05", "Unit Test", "State Pattern", "Khóa chuyển đổi trạng thái phi lý khi đơn hàng đã thanh toán (PaidState)", "Gọi startPreparing() khi đơn đã PAID", "Ném InvalidStateTransitionException", "Pass"],
                ["6", "TC06", "Unit Test", "Adapter Pattern", "Tích hợp cổng thanh toán ví điện tử Momo qua MomoAdapter", "Đơn hàng Ready, thanh toán qua MomoAdapter", "Trạng thái đơn PAID, sinh mã MOMO-xxxx", "Pass"],
                ["7", "TC07", "Unit Test", "Observer Pattern", "Phát thông báo thời gian thực từ bếp đến màn hình thu ngân khi món xong", "subscribe(cashier) & markReady()", "CashierScreen nhận chuỗi 'ready for pickup'", "Pass"],
                ["8", "TC08", "Unit Test", "MenuService", "Thao tác thêm, cập nhật, tắt kích hoạt món nước trong thực đơn", "addBeverage, updateBeverage, disableBeverage", "Dữ liệu thực đơn cập nhật chính xác, active = false", "Pass"],
                ["9", "TC09", "Unit Test", "MenuService", "Xác thực dữ liệu đầu vào thực đơn (tên rỗng, đơn giá âm, sai danh mục)", "Tên '', giá -1, category 'INVALID'", "Ném IllegalArgumentException chặn dữ liệu bẩn", "Pass"],
                ["10", "TC10", "Unit Test", "Inventory Logic", "Tự động trừ kho nguyên liệu khi vào bếp và hoàn trả khi hủy đơn", "1 ly cà phê sữa tiêu hao 18g hạt; lệnh cancel()", "Kho giảm 18g khi vào bếp, phục hồi khi hủy", "Pass"],
                ["11", "TC11", "Unit Test", "Inventory Guard", "Chặn đẩy đơn hàng vào bếp pha chế khi tồn kho nguyên liệu không đủ", "Tồn kho còn 5g, yêu cầu 18g cà phê hạt", "Ném InventoryException, giữ nguyên PENDING", "Pass"],
                ["12", "TC12", "Unit Test", "ReceiptService", "Cấu trúc hóa đơn thanh toán (tên quán, tên món, tổng tiền, mã giao dịch)", "Đơn hàng đã thanh toán 30.000 ₫ qua Momo", "Chuỗi hóa đơn chứa 'COFFEE SHOP POS', '30,000 VND'", "Pass"],
                ["13", "TC13", "Unit Test", "ReceiptImageService", "Kết xuất hóa đơn đồ họa dạng tệp ảnh PNG phục vụ in ấn", "Chuỗi text hóa đơn hoàn chỉnh", "Tạo tệp ảnh test-receipt.png với dung lượng > 0", "Pass"],
                ["14", "TC14", "Unit Test", "UserService", "Quản lý tài khoản nhân viên, kiểm tra trùng lặp và khóa/mở khóa", "Thêm cashier02, khóa active=false, thử trùng", "Khóa tài khoản thành công, ném ngoại lệ khi trùng", "Pass"],
                ["15", "TC15", "Unit Test", "Cart Logic", "Quy trình giỏ hàng: tự gộp món trùng và cập nhật số lượng khi Pending", "Thêm cùng món 2 lần, tăng số lượng lên 3", "Gộp thành 1 dòng, số lượng 3, thành tiền 90.000 ₫", "Pass"],
                ["16", "TC16", "Unit Test", "Factory Method", "Khởi tạo đúng các họ đồ uống cụ thể qua Coffee/Tea/MatchaFactory", "Americano, Trà sữa Ô Long, Matcha latte", "Tạo đúng đối tượng Beverage, bảo toàn giá và mô tả", "Pass"],
                ["17", "TC17", "Unit Test", "Singleton Pattern", "Đảm bảo tính duy nhất và chia sẻ đối tượng cấu hình & kết nối", "AppConfig.getInstance(), DatabaseConnection", "Trả về cùng 1 tham chiếu đối tượng (== identity)", "Pass"],
                ["18", "TC18", "Unit Test", "Adapter Pattern", "Cô lập và xử lý sự cố khi cổng thanh toán đối tác trả về thất bại", "Thanh toán qua FakeFailingGateway (giả lập lỗi)", "Đơn hàng giữ nguyên READY, không tạo payment", "Pass"],
                ["19", "TC19", "Integration Test", "Order + SqliteRepo", "Tích hợp lưu trữ và nạp lại toàn bộ vòng đời đơn hàng với CSDL SQLite thật", "Tạo đơn, Decorator, chuyển trạng thái, trả Momo -> DB", "Đọc lại từ tệp .db: trạng thái PAID, Topping đầy đủ", "Pass"],
                ["20", "TC20", "Integration Test", "Admin + SqliteRepo", "Tích hợp các thao tác CRUD danh mục món, topping và tài khoản với CSDL SQLite", "MenuService & UserService thao tác trên SqliteRepo", "Mở lại từ CSDL: món, topping bị tắt, user bị khóa", "Pass"],
                ["21", "TC21", "Integration Test", "Audit Trail + SQLite", "Tích hợp ghi nhật ký lịch sử trạng thái đơn và nhật ký trừ kho vào CSDL", "Vòng đời đơn hàng -> order_status_history, inventory_tx", "Truy vấn SQL: >= 4 bản ghi lịch sử, >= 1 log kho", "Pass"],
                ["22", "TC22", "Integration Test", "Operations + SQLite", "Tích hợp truy vấn phân hệ vận hành và báo cáo kiểm toán từ CSDL thật", "Order đã thanh toán -> gọi OperationsService kiểm tra", "Lịch sử mới nhất là PAID, giao dịch kho có change < 0", "Pass"],
                ["23", "TC23", "Integration Test", "Backup / Restore", "Tích hợp quy trình sao lưu (Snapshot) tệp tin CSDL vật lý và phục hồi kiểm tra", "Tạo đơn PREPARING -> backupDatabase() -> nạp DB", "Tệp backup tồn tại > 0 byte, nạp lại đúng PREPARING", "Pass"],
                ["24", "TC24", "Integration Test", "RecipeItem + SQLite", "Tích hợp cấu hình định mức công thức tiêu hao nguyên liệu món nước với CSDL", "Thiết lập công thức Bơ (180g), sau đó xóa công thức", "Lưu định lượng 180g vào SQLite, xóa chuẩn xác", "Pass"]
            ]
            
            t2 = doc.add_table(rows=len(testcase_24_data) + 1, cols=len(tc_headers))
            t2.style = 'Table Grid'
            format_custom_table(t2, [0.4, 0.6, 0.9, 1.1, 1.8, 1.2, 1.5, 0.5], tc_headers, testcase_24_data, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=8.5)
            # Move t2 right after p_cap2
            p_cap2._p.addnext(t2._tbl)
            print("Successfully added Table 3.1 and Table 3.2!")

    # Save document
    doc.save(doc_path)
    print(f"Document successfully updated and saved to: {doc_path}")

if __name__ == '__main__':
    run_update()
