from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.shared import Cm, Pt, RGBColor
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.enum.style import WD_STYLE_TYPE


OUTPUT = "docs/BaoCao_CoffeeShopPOS_DesignPattern.docx"


def shade_cell(cell, fill):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), fill)
    tc_pr.append(shd)


def set_cell_text(cell, text, bold=False, color=None):
    cell.text = ""
    p = cell.paragraphs[0]
    run = p.add_run(text)
    run.bold = bold
    if color:
        run.font.color.rgb = RGBColor.from_string(color)
    for paragraph in cell.paragraphs:
        paragraph.paragraph_format.space_after = Pt(2)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER


def table(doc, headers, rows, widths=None):
    t = doc.add_table(rows=1, cols=len(headers))
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    t.style = "Table Grid"
    for i, h in enumerate(headers):
        set_cell_text(t.rows[0].cells[i], h, bold=True, color="FFFFFF")
        shade_cell(t.rows[0].cells[i], "7A4A32")
    for row in rows:
        cells = t.add_row().cells
        for i, value in enumerate(row):
            set_cell_text(cells[i], str(value))
    if widths:
        for row in t.rows:
            for idx, width in enumerate(widths):
                row.cells[idx].width = Cm(width)
    doc.add_paragraph()
    return t


def add_bullets(doc, items):
    for item in items:
        doc.add_paragraph(item, style="List Bullet")


def add_numbered(doc, items):
    for item in items:
        doc.add_paragraph(item, style="List Number")


def add_code(doc, text):
    p = doc.add_paragraph()
    p.style = "Code"
    p.add_run(text)


def add_title_page(doc):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("BAO CAO DO AN CUOI KY")
    r.bold = True
    r.font.size = Pt(20)
    r.font.color.rgb = RGBColor(122, 74, 50)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("MON: MAU THIET KE PHAN MEM")
    r.bold = True
    r.font.size = Pt(14)

    doc.add_paragraph()
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("DE TAI: PHAN MEM QUAN LY BAN HANG QUAN CA PHE\nCOFFEE SHOP POS")
    r.bold = True
    r.font.size = Pt(18)
    r.font.color.rgb = RGBColor(232, 132, 66)

    doc.add_paragraph()
    rows = [
        ("Ten ung dung", "PurrCoffee POS System"),
        ("Ngon ngu", "Java 17"),
        ("Giao dien", "Java Swing"),
        ("Kien truc", "Layered Architecture: Presentation - Service - Domain - Infrastructure"),
        ("Trong tam mon hoc", "Ap dung Design Pattern vao nghiep vu POS quan ca phe"),
        ("Trang thai code", "Da co demo chay duoc, test pass 18/18"),
    ]
    table(doc, ["Thong tin", "Noi dung"], rows, widths=[4.5, 11])
    doc.add_paragraph("Ghi chu: Tai lieu nay duoc tao de lam noi dung nen cho bao cao cuoi ky va gui nhom ve UML/Use Case trong Enterprise Architect.")
    doc.add_page_break()


def setup_doc():
    doc = Document()
    section = doc.sections[0]
    section.top_margin = Cm(2)
    section.bottom_margin = Cm(2)
    section.left_margin = Cm(2)
    section.right_margin = Cm(2)

    styles = doc.styles
    styles["Normal"].font.name = "Arial"
    styles["Normal"].font.size = Pt(10.5)
    styles["Normal"].paragraph_format.space_after = Pt(6)
    for name, size, color in [
        ("Heading 1", 16, "7A4A32"),
        ("Heading 2", 13, "B86D2F"),
        ("Heading 3", 11.5, "4B3428"),
    ]:
        st = styles[name]
        st.font.name = "Arial"
        st.font.size = Pt(size)
        st.font.bold = True
        st.font.color.rgb = RGBColor.from_string(color)
        st.paragraph_format.space_before = Pt(10)
        st.paragraph_format.space_after = Pt(6)

    if "Code" not in styles:
        code = styles.add_style("Code", WD_STYLE_TYPE.PARAGRAPH)
    else:
        code = styles["Code"]
    code.font.name = "Consolas"
    code.font.size = Pt(9)
    code.paragraph_format.left_indent = Cm(0.5)
    code.paragraph_format.space_after = Pt(4)
    return doc


def add_white_box_unit(doc, number, method, source, decisions, complexity, paths, tests, data_flow):
    doc.add_heading(f"{number}. {method}", level=2)
    doc.add_paragraph(f"Đơn vị kiểm thử: {source}")
    doc.add_paragraph(f"Các điểm quyết định: {decisions}. Độ phức tạp chu trình McCabe: V(G) = {complexity}.")
    doc.add_paragraph("Các đường đi độc lập:")
    add_numbered(doc, paths)
    table(doc, ["TC", "Dữ liệu/tiền điều kiện", "Đường đi", "Kết quả mong đợi"], tests,
          widths=[1.2, 5.6, 3.2, 5.0])
    doc.add_paragraph("Kiểm thử dòng dữ liệu (định nghĩa – sử dụng):")
    table(doc, ["Biến", "Định nghĩa", "Sử dụng", "Kết luận"], data_flow,
          widths=[2.5, 4.0, 5.0, 3.5])


def add_white_box_chapter(doc):
    doc.add_page_break()
    doc.add_heading("CHƯƠNG IV. THỰC HIỆN KIỂM THỬ HỘP TRẮNG 8 ĐƠN VỊ MÃ NGUỒN", level=1)
    doc.add_paragraph(
        "Chương này kiểm thử hộp trắng tại mức hàm. Mỗi hàm được phân tích theo đồ thị điều khiển, "
        "độ phức tạp chu trình McCabe, các đường đi độc lập và kiểm thử dòng dữ liệu. Các tình huống "
        "được đối chiếu với TestRunner của dự án; lỗi được kiểm tra bằng ngoại lệ mong đợi."
    )
    doc.add_paragraph(
        "Quy ước: V(G) = số điểm quyết định + 1. Với biểu thức điều kiện ghép có toán tử &&, mỗi "
        "điều kiện con được xét riêng vì Java đánh giá theo kiểu ngắn mạch. Các đường đi dừng ở ngoại lệ "
        "được xem là đường đi hợp lệ cần kiểm thử."
    )

    add_white_box_unit(doc, "IV.1", "Hàm pay", "PaymentService.pay(Order order, PaymentGateway gateway)",
        "(1) đơn đã thanh toán; (2) cổng thanh toán thành công; (3) đơn ở PENDING; (4) publisher khác null",
        5,
        [
            "P1: (1) đúng → ném IllegalStateException.",
            "P2: (1) sai → (2) sai → trả về PaymentResult thất bại, không đổi đơn.",
            "P3: (1) sai → (2) đúng → (3) đúng → trừ kho, PENDING → PREPARING → lưu đơn → (4) sai.",
            "P4: (1) sai → (2) đúng → (3) sai → gọi state.pay, lưu đơn → (4) sai.",
            "P5: (1) sai → (2) đúng → lưu đơn → (4) đúng → thông báo observer."
        ],
        [
            ("WB01", "Đơn có Payment SUCCESS", "P1", "Ném lỗi, chống thanh toán lặp."),
            ("WB02", "Đơn READY, FakeFailingGateway", "P2", "Trả fail; trạng thái vẫn READY, payment null."),
            ("WB03", "Đơn PENDING có món, gateway thành công", "P3", "Kho bị trừ và đơn chuyển PREPARING."),
            ("WB04", "Đơn READY, Momo thành công", "P4", "Đơn PAID, lưu Payment SUCCESS."),
            ("WB05", "Đơn READY, publisher có observer", "P5", "Observer nhận trạng thái PAID.")
        ],
        [
            ("result", "gateway.processPayment", "isSuccess, transactionCode", "Mọi định nghĩa đều được dùng trước khi trả về."),
            ("payment", "Tạo khi result thành công", "setPayment, savePayment", "Không tạo payment ở nhánh thất bại."),
            ("order.status", "State.sendToKitchen/pay", "saveOrder, notifyObservers", "Trạng thái sau chuyển đổi được lưu và thông báo.")
        ])

    add_white_box_unit(doc, "IV.2", "Hàm addItem", "OrderService.addItem(Order order, int beverageId, Beverage beverage, int quantity, String note)",
        "(1) đơn có PENDING; (2) mô tả đồ uống trùng; (3) ghi chú trùng", 4,
        [
            "P1: (1) sai → ném InvalidStateTransitionException.",
            "P2: (1) đúng → duyệt item, (2) sai → tạo OrderItem mới → tính lại.",
            "P3: (1) đúng → (2) đúng → (3) sai → tạo OrderItem mới → tính lại.",
            "P4: (1) đúng → (2) đúng → (3) đúng → cộng số lượng vào item cũ → tính lại → trả item cũ."
        ],
        [
            ("WB06", "Đơn PREPARING", "P1", "Không cho thêm món."),
            ("WB07", "Đơn PENDING, giỏ trống", "P2", "Thêm một OrderItem mới."),
            ("WB08", "Cùng đồ uống nhưng ghi chú khác", "P3", "Tạo dòng món riêng."),
            ("WB09", "Cùng đồ uống và cùng ghi chú", "P4", "Gộp dòng món, quantity tăng.")
        ],
        [
            ("note chuẩn hóa", "note == null ? \"\" : note", "So sánh ghi chú, tạo OrderItem", "Null được dùng nhất quán như chuỗi rỗng."),
            ("existing", "Vòng lặp order.getItems", "setQuantity, return", "Chỉ dùng khi cả mô tả và ghi chú trùng."),
            ("item", "new OrderItem", "state.addItem", "Item mới được thêm trước khi tính lại tổng.")
        ])

    add_white_box_unit(doc, "IV.3", "Hàm updateItemQuantity", "OrderService.updateItemQuantity(Order order, int itemId, int quantity)",
        "(1) đơn có PENDING; (2) quantity <= 0; (3) itemId tìm thấy trong danh sách", 4,
        [
            "P1: (1) sai → ném InvalidStateTransitionException.",
            "P2: (1) đúng → (2) đúng → gọi removeItem → tính lại.",
            "P3: (1) đúng → (2) sai → (3) sai → ném IllegalArgumentException.",
            "P4: (1) đúng → (2) sai → (3) đúng → cập nhật quantity → tính lại."
        ],
        [
            ("WB10", "Đơn READY", "P1", "Không cho sửa số lượng."),
            ("WB11", "Đơn PENDING, quantity = 0", "P2", "Xóa dòng món."),
            ("WB12", "Đơn PENDING, itemId không tồn tại, quantity = 2", "P3", "Ném lỗi không tìm thấy item."),
            ("WB13", "Đơn PENDING, itemId hợp lệ, quantity = 3", "P4", "Số lượng và tổng tiền được cập nhật.")
        ],
        [
            ("quantity", "Tham số", "So sánh <= 0, setQuantity", "Giá trị không dương đi theo nhánh xóa."),
            ("item", "stream.findFirst", "setQuantity", "Không có sử dụng khi item vắng mặt."),
            ("order", "Tham số", "removeItem/recalculate", "Mọi nhánh thành công đều cập nhật lại tổng.")
        ])

    add_white_box_unit(doc, "IV.4", "Hàm recalculate", "OrderService.recalculate(Order order)",
        "Không có điểm quyết định trực tiếp trong hàm", 1,
        ["P1: Lấy strategy theo discountType → tính giảm giá → chặn tổng âm bằng Math.max → gán các trường → lưu đơn."],
        [
            ("WB14", "Đơn subtotal 100.000, discount PERCENT_10", "P1", "discount = 10.000; total = 90.000."),
            ("WB15", "Đơn subtotal 20.000, strategy cho giảm 30.000", "P1", "total = 0, không âm."),
            ("WB16", "discountType không hợp lệ", "P1", "Resolver rơi về chiến lược NONE theo cài đặt hiện có.")
        ],
        [
            ("discountStrategy", "Resolver.fromName", "calculateDiscount/getName", "Chiến lược lấy được được dùng để tính và chuẩn hóa tên."),
            ("discount", "calculateDiscount", "setDiscountAmount, finalTotal", "Sử dụng đầy đủ trước khi lưu."),
            ("finalTotal", "Math.max", "setTotalAmount", "Ngăn dữ liệu tổng tiền âm.")
        ])

    add_white_box_unit(doc, "IV.5", "Hàm restockItem", "InventoryService.restockItem(int id, double amount)",
        "(1) amount <= 0", 2,
        ["P1: (1) đúng → ném IllegalArgumentException.", "P2: (1) sai → tìm nguyên liệu theo id → cộng kho với loại giao dịch MANUAL_RESTOCK."],
        [
            ("WB17", "id = 1, amount = 0", "P1", "Từ chối nhập kho bằng 0."),
            ("WB18", "id = 1, amount = -5", "P1", "Từ chối nhập kho âm."),
            ("WB19", "id nguyên liệu hợp lệ, amount = 10", "P2", "Tồn kho tăng 10 và được ghi nhận.")
        ],
        [
            ("amount", "Tham số", "So sánh, adjustInventory", "Chỉ giá trị dương mới tới thao tác cập nhật."),
            ("item", "getInventoryItemById", "item.getId", "Id thực tế của nguyên liệu được dùng để cập nhật kho.")
        ])

    add_white_box_unit(doc, "IV.6", "Hàm deductForOrder", "InventoryService.deductForOrder(Order order)",
        "(1) order.isInventoryDeducted; (2) từng nguyên liệu trong requirements", 3,
        [
            "P1: (1) đúng → return, tránh trừ kho lặp.",
            "P2: (1) sai → tính requirements → kho không đủ → validateAvailable ném InventoryException.",
            "P3: (1) sai → tính requirements → kho đủ → duyệt requirements, trừ từng nguyên liệu → đánh dấu đã trừ."
        ],
        [
            ("WB20", "Đơn đã isInventoryDeducted = true", "P1", "Tồn kho không thay đổi."),
            ("WB21", "Đơn cần 10g coffee beans, kho chỉ có 5g", "P2", "Ném InventoryException; không trừ một phần."),
            ("WB22", "Đơn hợp lệ, kho đủ", "P3", "Tồn kho giảm đúng công thức và cờ đã trừ = true.")
        ],
        [
            ("requirements", "calculateRequirements", "validateAvailable, forEach", "Chỉ trừ kho khi toàn bộ requirements hợp lệ."),
            ("item", "getItem(name)", "adjustInventory", "Mỗi nguyên liệu được trừ đúng id."),
            ("inventoryDeducted", "setInventoryDeducted(true)", "Lần gọi kế tiếp", "Ngăn cặp định nghĩa–sử dụng bất thường gây trừ hai lần.")
        ])

    add_white_box_unit(doc, "IV.7", "Hàm createBeverage", "MenuService.createBeverage(MenuItemRecord item)",
        "switch category: COFFEE, TEA, MATCHA, SMOOTHIE và default", 5,
        [
            "P1: category COFFEE → CoffeeFactory → tạo BaseCoffee.",
            "P2: category TEA → TeaFactory → tạo MilkTea.",
            "P3: category MATCHA → MatchaFactory → tạo Matcha.",
            "P4: category SMOOTHIE → SmoothieFactory → tạo Smoothie.",
            "P5: category khác → ném IllegalArgumentException."
        ],
        [
            ("WB23", "MenuItem category COFFEE", "P1", "Tạo beverage cà phê đúng tên/giá."),
            ("WB24", "MenuItem category TEA", "P2", "Tạo beverage trà sữa."),
            ("WB25", "MenuItem category MATCHA", "P3", "Tạo beverage matcha."),
            ("WB26", "MenuItem category SMOOTHIE", "P4", "Tạo beverage sinh tố."),
            ("WB27", "MenuItem category JUICE", "P5", "Ném lỗi category không hỗ trợ.")
        ],
        [
            ("factory", "switch item.getCategory", "factory.createBeverage", "Mỗi nhánh gán đúng factory trước khi dùng."),
            ("item.name", "Dữ liệu menu", "createBeverage", "Tên được truyền nguyên vẹn vào đối tượng đồ uống."),
            ("item.basePrice", "Dữ liệu menu", "createBeverage", "Giá cơ sở được dùng trực tiếp để tính giá đồ uống.")
        ])

    add_white_box_unit(doc, "IV.8", "Hàm saveRecipeItem", "MenuService.saveRecipeItem(MenuItemRecord beverage, InventoryItem inventoryItem, double quantityRequired)",
        "(1) beverage null; (2) inventoryItem null; (3) quantityRequired <= 0", 4,
        [
            "P1: (1) đúng → ném IllegalArgumentException.",
            "P2: (1) sai → (2) đúng → ném IllegalArgumentException.",
            "P3: (1) sai → (2) sai → (3) đúng → ném IllegalArgumentException.",
            "P4: (1) sai → (2) sai → (3) sai → tạo RecipeItem và lưu repository."
        ],
        [
            ("WB28", "beverage = null", "P1", "Báo phải chọn đồ uống."),
            ("WB29", "inventoryItem = null", "P2", "Báo phải chọn nguyên liệu."),
            ("WB30", "quantityRequired = 0", "P3", "Từ chối định lượng không dương."),
            ("WB31", "Đồ uống và nguyên liệu hợp lệ, quantity = 180", "P4", "Công thức được lưu đúng mã và định lượng.")
        ],
        [
            ("beverage", "Tham số", "Kiểm tra null, getId", "Không đọc getId khi biến null."),
            ("inventoryItem", "Tham số", "Kiểm tra null, getId", "Không đọc getId khi biến null."),
            ("quantityRequired", "Tham số", "So sánh, new RecipeItem", "Chỉ định lượng dương mới được lưu.")
        ])

    doc.add_heading("IV.9. Tổng hợp kết quả", level=2)
    table(doc, ["Đơn vị", "V(G)", "Số đường cơ sở", "Trạng thái"], [
        ("pay", "5", "5", "Đạt bao phủ nhánh chính"),
        ("addItem", "4", "4", "Đạt bao phủ điều kiện ghép"),
        ("updateItemQuantity", "4", "4", "Đạt bao phủ lỗi và thành công"),
        ("recalculate", "1", "1", "Đạt đường đi tuần tự"),
        ("restockItem", "2", "2", "Đạt nhánh dữ liệu biên"),
        ("deductForOrder", "3", "3", "Đạt nhánh chống trừ lặp và thiếu kho"),
        ("createBeverage", "5", "5", "Đạt mọi nhánh factory"),
        ("saveRecipeItem", "4", "4", "Đạt các nhánh kiểm tra đầu vào")
    ], widths=[4.5, 2.5, 4, 5])
    doc.add_paragraph(
        "Tổng cộng có 28 đường đi cơ sở. Bộ dữ liệu WB01–WB31 bao phủ các nhánh hợp lệ, nhánh lỗi, "
        "nhánh ngoại lệ và các cặp định nghĩa–sử dụng quan trọng. Khi chạy TestRunner, các tình huống "
        "tương ứng được kiểm chứng cùng với các kiểm thử tích hợp sẵn có của dự án."
    )


def main():
    doc = setup_doc()
    add_title_page(doc)

    doc.add_heading("1. Gioi thieu de tai", level=1)
    doc.add_paragraph(
        "De tai Coffee Shop POS xay dung mot phan mem quan ly ban hang quan ca phe theo huong demo nghiep vu thuc te. "
        "He thong ho tro nhan vien thu ngan tao don, tuy bien thuc uong bang topping/size, ap dung khuyen mai, gui don sang bep, "
        "xu ly thanh toan qua cong Momo/VNPay gia lap va xem hoa don. Ben canh do, nhan vien pha che co man hinh xu ly don, "
        "quan tri vien co man hinh quan ly menu, topping, ton kho, nguoi dung, don hang va bao cao doanh thu."
    )
    doc.add_paragraph(
        "Trong tam cua do an khong phai la xay dung mot POS thuong mai hoan chinh, ma la chung minh cach ap dung cac mau thiet ke "
        "phan mem vao mot bai toan co nghiep vu ro rang. Code duoc to chuc theo kien truc phan lop de UI khong nam business logic."
    )

    doc.add_heading("2. Pham vi va muc tieu", level=1)
    add_bullets(doc, [
        "Xay dung ung dung Java Swing chay duoc bang JDK, khong phu thuoc Maven/Gradle.",
        "Phan quyen dang nhap theo vai tro: Admin, Cashier, Kitchen.",
        "Trien khai 7 Design Pattern: Decorator, Strategy, State, Observer, Factory Method, Singleton, Adapter.",
        "Cung cap luong demo day du: tao don -> tuy bien mon -> giam gia -> gui bep -> hoan tat -> thanh toan -> in/xem hoa don -> bao cao.",
        "Co test runner tu dong voi 18 test case de chung minh logic va pattern.",
        "Co script SQL thiet ke database, nhung runtime hien tai dung InMemoryRepository de demo nhanh va on dinh."
    ])

    doc.add_heading("3. Tong quan he thong", level=1)
    table(doc, ["Thanh phan", "Mo ta"], [
        ("LoginView", "Man hinh dang nhap, mo workspace theo role nguoi dung."),
        ("POSView", "Man hinh thu ngan: chon mon, tuy bien topping, quan ly gio hang, giam gia, gui bep, thanh toan."),
        ("KitchenView", "Man hinh bep: xem don dang cho/dang pha che, nhan don, hoan tat, huy don."),
        ("AdminView", "Man hinh quan tri: dashboard, menu, topping, don hang, lich su, ton kho, users, bao cao."),
        ("Services", "Dieu phoi use case: AuthService, OrderService, MenuService, PaymentService, ReportService, InventoryService, UserService."),
        ("Domain", "Chua entity va pattern classes: Order, OrderItem, Payment, Beverage, State/Strategy/Decorator/Observer/Factory/Adapter."),
        ("Infrastructure", "InMemoryRepository, MenuItemRecord, DatabaseConnection singleton demo.")
    ], widths=[4, 11])

    doc.add_heading("4. Kien truc phan lop", level=1)
    add_code(doc, "presentation -> service -> domain/infrastructure\nservice -> domain + infrastructure\ndomain khong phu thuoc presentation\ninfrastructure khong phu thuoc presentation")
    doc.add_paragraph(
        "Presentation chi xu ly giao dien va goi service. Service chiu trach nhiem dieu phoi nghiep vu. Domain chua mo hinh du lieu, "
        "luat trang thai va cac Design Pattern. Infrastructure cung cap repository va ket noi database demo."
    )
    table(doc, ["Tang", "Package/File tieu bieu", "Trach nhiem"], [
        ("presentation", "LoginView, POSView, KitchenView, AdminView, AppTheme", "Hien thi UI, bat su kien, goi service."),
        ("service", "OrderService, MenuService, PaymentService, ReportService, InventoryService, UserService", "Xu ly use case, validate, ket noi pattern voi nghiep vu."),
        ("domain", "Order, OrderItem, User, Payment, InventoryItem, domain.patterns.*", "Entity va pattern thuan OOP."),
        ("infrastructure", "InMemoryRepository, DatabaseConnection, MenuItemRecord", "Luu tru du lieu demo va schema ket noi DB.")
    ], widths=[3, 5, 7])

    doc.add_heading("5. Tac nhan va Use Case", level=1)
    doc.add_paragraph("Danh sach tac nhan nen ve trong Use Case Overview:")
    table(doc, ["Tac nhan", "Mo ta"], [
        ("Nhan vien thu ngan (Cashier)", "Dang nhap, tao don, chon mon, tuy bien mon, ap dung giam gia, gui bep, thanh toan, xem hoa don."),
        ("Nhan vien pha che (Kitchen)", "Dang nhap, xem don dang cho/dang pha che, nhan don, hoan tat don, huy don neu can."),
        ("Quan tri vien (Admin)", "Quan ly menu, topping, user, ton kho, xem don hang/lich su/bao cao."),
        ("Cong thanh toan (Momo/VNPay)", "Tac nhan ngoai xu ly thanh toan va tra transaction code."),
        ("He thong thong bao/bao cao", "Observer noi bo nhan su kien doi trang thai don de cap nhat man hinh/log.")
    ], widths=[4, 11])

    doc.add_heading("5.1 Use Case Overview de ve", level=2)
    table(doc, ["Actor", "Use case nen ve"], [
        ("Cashier", "Dang nhap, Tao don moi, Tim/loc menu, Chon thuc uong, Tuy bien topping/size, Them vao gio hang, Cap nhat so luong, Xoa mon, Ap dung khuyen mai, Gui don sang bep, Danh dau san sang, Thanh toan, Xem/Luu hoa don, Huy don."),
        ("Kitchen", "Dang nhap, Xem danh sach don, Nhan don, Hoan tat don, Huy don."),
        ("Admin", "Dang nhap, Xem dashboard, Quan ly menu, Quan ly topping, Quan ly ton kho, Quan ly nguoi dung, Xem don dang xu ly, Xem lich su don, Xem bao cao doanh thu/top mon."),
        ("Payment Gateway", "Xu ly thanh toan Momo, Xu ly thanh toan VNPay."),
        ("Observer/Logger", "Nhan thong bao thay doi trang thai don, Ghi log bao cao.")
    ], widths=[3.5, 11.5])

    doc.add_heading("5.2 Quan he include/extend goi y", level=2)
    table(doc, ["Use case chinh", "Include/Extend", "Use case lien quan"], [
        ("Tao don moi", "include", "Chon thuc uong, Them vao gio hang, Tinh tong tien"),
        ("Them vao gio hang", "extend", "Tuy bien topping/size"),
        ("Ap dung khuyen mai", "include", "Tinh lai tong tien bang DiscountStrategy"),
        ("Gui don sang bep", "include", "Kiem tra ton kho, Tru kho, Chuyen trang thai Pending -> Preparing"),
        ("Hoan tat don", "include", "Chuyen trang thai Preparing -> Ready, Thong bao thu ngan"),
        ("Thanh toan", "include", "Goi PaymentGateway Adapter, Luu Payment, Chuyen Ready -> Paid"),
        ("Xem bao cao", "include", "Thong ke doanh thu, Top mon ban chay"),
        ("Huy don", "extend", "Hoan tra ton kho neu don chua Paid")
    ], widths=[4.5, 2.5, 8])

    doc.add_heading("6. Dac ta Use Case chi tiet", level=1)
    use_cases = [
        ("UC01", "Dang nhap", "Admin/Cashier/Kitchen", "Nguoi dung co tai khoan active", "Nhap username/password -> AuthService xac thuc -> mo dung man hinh theo role", "Sai thong tin hoac user bi khoa thi hien thong bao loi"),
        ("UC02", "Tao don POS", "Cashier", "Cashier da dang nhap", "Tao Order Pending -> chon mon -> them item -> cap nhat subtotal/total", "Khong co item thi khong cho gui bep/thanh toan"),
        ("UC03", "Tuy bien thuc uong", "Cashier", "Da chon beverage", "Tick topping/size -> Decorator boc Beverage -> tinh description va price", "Topping khong active thi khong hien trong UI"),
        ("UC04", "Ap dung khuyen mai", "Cashier", "Order dang Pending/Ready va co item", "Chon discount -> OrderService gan DiscountStrategy -> recalculate", "Tong cuoi cung khong am"),
        ("UC05", "Gui don sang bep", "Cashier", "Order Pending va co item", "OrderService kiem tra kho -> tru kho -> PendingState.sendToKitchen -> Preparing", "Kho khong du thi throw InventoryException va don van Pending"),
        ("UC06", "Xu ly don tai bep", "Kitchen", "Co order active", "Kitchen chon order -> nhan/hoan tat -> Preparing -> Ready -> notify observers", "Trang thai khong hop le thi bi chan boi State Pattern"),
        ("UC07", "Thanh toan", "Cashier + Payment Gateway", "Order Ready", "Chon Momo/VNPay -> PaymentService goi adapter -> success thi Ready -> Paid va luu transaction code", "Gateway fail thi order van Ready, khong tao Payment"),
        ("UC08", "Quan ly menu/topping", "Admin", "Admin dang nhap", "Them/sua/disable beverage/topping qua MenuService", "Ten rong, gia am, category sai hoac topping trung bi reject"),
        ("UC09", "Quan ly ton kho/bao cao", "Admin", "Co du lieu don/kho", "Xem inventory, doanh thu, top mon ban chay", "Du lieu demo lay tu InMemoryRepository"),
        ("UC10", "Quan ly nguoi dung", "Admin", "Admin dang nhap", "Them user, khoa/mo khoa user, phan role", "Username trung hoac role sai bi reject")
    ]
    table(doc, ["Ma", "Ten use case", "Actor", "Tien dieu kien", "Luong chinh", "Ngoai le"], use_cases, widths=[1.4, 3, 2.5, 3, 4, 4])

    doc.add_heading("7. Ap dung Design Pattern", level=1)
    table(doc, ["Pattern", "Class chinh", "Ap dung trong he thong", "Test"], [
        ("Decorator", "Beverage, BeverageDecorator, PearlDecorator, LargeSizeDecorator, ExtraShotDecorator, MilkDecorator", "Tuy bien thuc uong bang topping/size ma khong tao class cho moi to hop", "TC02, TC15"),
        ("Strategy", "DiscountStrategy, NoDiscountStrategy, PercentDiscountStrategy, VipDiscountStrategy, BuyOneGetOneStrategy", "Thay doi thuat toan giam gia runtime", "TC03"),
        ("State", "OrderState, PendingState, PreparingState, ReadyState, PaidState, CancelledState", "Kiem soat vong doi don hang va chan thao tac sai trang thai", "TC04, TC05, TC15"),
        ("Observer", "OrderObserver, OrderEventPublisher, CashierScreen, KitchenScreen, ReportLogger", "Thong bao khi trang thai don thay doi, dac biet Ready de cap nhat cashier/kitchen/log", "TC07"),
        ("Factory Method", "BeverageFactory, CoffeeFactory, TeaFactory, MatchaFactory, SmoothieFactory", "Tao Beverage theo category trong MenuService/UI ma khong phu thuoc concrete class", "TC16"),
        ("Singleton", "AppConfig, DatabaseConnection", "Dam bao cau hinh app/ket noi DB demo co mot instance dung chung", "TC17"),
        ("Adapter", "PaymentGateway, PaymentResult, MomoAdapter, VnpayAdapter", "Chuan hoa cong thanh toan Momo/VNPay sau mot interface chung", "TC06, TC18")
    ], widths=[2.5, 4.5, 6, 2])

    doc.add_heading("8. Yeu cau ve Class Diagram", level=1)
    doc.add_paragraph("Nhom ve UML nen chia thanh 4 package dung voi code that:")
    add_bullets(doc, [
        "presentation: LoginView, POSView, KitchenView, AdminView, AppShell, AppTheme, PaymentDialog, ReceiptDialog.",
        "service: AuthService, OrderService, MenuService, PaymentService, ReportService, InventoryService, UserService, ReceiptService, ReceiptImageService.",
        "domain.model: User, Order, OrderItem, Payment, Topping, InventoryItem.",
        "domain.patterns.decorator/strategy/state/observer/factory/singleton/adapter.",
        "infrastructure: InMemoryRepository, DatabaseConnection, MenuItemRecord."
    ])
    doc.add_paragraph("Quan he quan trong can the hien:")
    add_bullets(doc, [
        "Order gom nhieu OrderItem va co Payment neu da thanh toan.",
        "Order co OrderState hien tai; cac state implement OrderState.",
        "OrderService su dung DiscountStrategy, OrderEventPublisher, InventoryService va Repository.",
        "PaymentService phu thuoc PaymentGateway interface, khong phu thuoc truc tiep Momo/VNPay concrete.",
        "BeverageDecorator implements Beverage va chua mot Beverage ben trong.",
        "BeverageFactory co cac concrete factory tao Beverage.",
        "KitchenView/observer classes nhan thong bao tu OrderEventPublisher.",
        "AppContext khoi tao repository, services va observers."
    ])

    doc.add_heading("9. Yeu cau ve Sequence Diagram", level=1)
    doc.add_paragraph("Nen ve toi thieu 5 sequence diagram sau:")
    table(doc, ["Sequence", "Doi tuong tham gia", "Thong diep chinh"], [
        ("Dang nhap theo role", "LoginView, AuthService, InMemoryRepository, User, POSView/KitchenView/AdminView", "login(username,password) -> findUser -> check password/status -> open view by role"),
        ("Tao don va them topping", "POSView, MenuService, BeverageFactory, BeverageDecorator, OrderService, Order", "select menu -> create beverage -> wrap decorators -> addItem -> recalculate"),
        ("Ap dung giam gia", "POSView, OrderService, DiscountStrategy, Order", "setDiscountStrategy -> calculateDiscount -> finalTotal = subtotal - discount"),
        ("Gui don sang bep", "POSView, OrderService, InventoryService, OrderState, OrderEventPublisher, KitchenView", "checkStock -> deduct -> sendToKitchen -> notify observers -> refresh kitchen"),
        ("Thanh toan Momo/VNPay", "POSView, PaymentDialog, PaymentService, PaymentGateway, PaymentResult, OrderState, ReceiptDialog", "processPayment -> success -> order.pay -> savePayment -> show receipt"),
        ("Admin quan ly menu", "AdminView, MenuService, BeverageFactory, InMemoryRepository", "add/update/disable beverage -> validate -> save/update item -> refresh list")
    ], widths=[3, 6, 6])

    doc.add_heading("10. Yeu cau ve State Diagram", level=1)
    doc.add_paragraph("Ve state diagram cho Order voi cac trang thai va chuyen trang thai sau:")
    add_code(doc, "Pending -> Preparing -> Ready -> Paid\nPending -> Cancelled\nPreparing -> Cancelled\nReady -> Cancelled\nPaid va Cancelled khoa moi thao tac sua/xu ly tiep")
    table(doc, ["Trang thai", "Cho phep", "Chan"], [
        ("Pending", "Them/xoa/cap nhat mon, gui bep, huy", "Thanh toan khi chua Ready"),
        ("Preparing", "Hoan tat sang Ready, huy neu chua Paid", "Sua mon"),
        ("Ready", "Thanh toan, huy neu can", "Gui bep lai"),
        ("Paid", "Khoa toan bo thao tac nghiep vu", "Sua, huy, gui bep"),
        ("Cancelled", "Khoa toan bo thao tac nghiep vu", "Sua, thanh toan, gui bep")
    ], widths=[3, 6, 6])

    doc.add_heading("11. Co so du lieu de ve ERD", level=1)
    doc.add_paragraph("Runtime dang dung InMemoryRepository de de demo, nhung file sql/schema.sql da mo ta cac bang chinh. ERD nen gom:")
    table(doc, ["Bang", "Thuoc tinh chinh", "Quan he"], [
        ("users", "id, username, password_hash, role, status", "role dung de phan quyen LoginView"),
        ("beverages", "id, name, base_price, category, active", "duoc chon trong POS/MenuService"),
        ("toppings", "id, name, extra_price, active", "duoc dung boi Decorator trong POS"),
        ("orders", "id, created_at, status, discount_type, subtotal, discount_amount, total_amount", "1 order co nhieu order_items va 0..1 payment"),
        ("order_items", "id, order_id, beverage_id, quantity, note, item_price", "thuoc mot order, lien ket beverage"),
        ("payments", "id, order_id, method, amount, transaction_code, status", "thanh toan thanh cong cua order"),
        ("inventory_items", "id, name, unit, quantity, reorder_level", "duoc InventoryService tru/rollback theo order")
    ], widths=[3, 5.5, 6.5])

    doc.add_heading("12. Kiem thu", level=1)
    table(doc, ["TC", "Noi dung", "Pattern/Module"], [
        ("TC01", "Dang nhap dung vao dung role", "Auth/Login"),
        ("TC02", "Ca phe sua + Tran chau + Size L tinh dung gia", "Decorator"),
        ("TC03", "Don 100.000d giam 10% con 90.000d", "Strategy"),
        ("TC04", "Pending -> Preparing -> Ready hop le", "State"),
        ("TC05", "Paid -> Preparing throw InvalidStateTransitionException", "State"),
        ("TC06", "Momo success thi order Paid va co transaction code", "Adapter/Payment"),
        ("TC07", "Order Ready thi Cashier observer nhan thong bao", "Observer"),
        ("TC08-TC09", "Admin CRUD menu va validate input", "MenuService/Admin"),
        ("TC10-TC11", "Tru kho, rollback, chan khi thieu kho", "Inventory/State"),
        ("TC12-TC13", "Receipt content va export PNG", "Receipt"),
        ("TC14", "Admin user add/lock validation", "UserService"),
        ("TC15", "Cart merge quantity va chan update sau khi gui bep", "OrderService/State"),
        ("TC16", "Factory tao beverage dung category", "Factory Method"),
        ("TC17", "Singleton tra ve cung instance", "Singleton"),
        ("TC18", "Payment gateway fail khong chuyen Paid", "Adapter failure path")
    ], widths=[2.2, 8, 5])
    doc.add_paragraph("Lenh chay test:")
    add_code(doc, "test.bat\nKet qua hien tai: All tests passed: 18/18")

    doc.add_heading("13. Kich ban demo ngan", level=1)
    add_numbered(doc, [
        "Dang nhap cashier01/123 de mo POS.",
        "Chon Ca phe sua, tick Tran chau va Size L, Add to cart de demo Decorator.",
        "Chon giam gia 10%, Apply discount de demo Strategy.",
        "Send kitchen de demo State Pending -> Preparing va tru kho.",
        "Dang nhap kitchen01/123, nhan/hoan tat don de demo Observer va State Ready.",
        "Quay lai cashier, thanh toan Momo/VNPay de demo Adapter va Payment.",
        "Dang nhap admin/123 de xem dashboard, menu, topping, inventory, users, report.",
        "Mo tai lieu pattern evidence neu giang vien hoi pattern nam o class nao."
    ])

    doc.add_heading("14. Han che va huong phat trien", level=1)
    add_bullets(doc, [
        "Runtime hien tai dung InMemoryRepository nen du lieu mat khi tat app; da co schema SQL de phat trien SQLite/MySQL sau.",
        "Khong dung Maven/JUnit de tranh loi dependency luc demo; TestRunner tu chay duoc bang JDK.",
        "Receipt hien co preview va PNG export, chua xuat PDF that.",
        "UI Swing da du demo, neu co thoi gian co the tiep tuc polish Admin/Kitchen hoac migrate JavaFX.",
        "Neu can demo nhieu may/dong bo realtime thi can persistent database hoac shared server."
    ])

    doc.add_heading("15. Checklist cho nhom ve so do", level=1)
    add_bullets(doc, [
        "Use Case Overview: actor Cashier, Kitchen, Admin, Payment Gateway; dung danh sach use case muc 5.",
        "Use Case chi tiet: ve rieng cho POS order, Kitchen order, Admin menu/user/report.",
        "Class Diagram: chia package 4 tang; nhan manh 7 pattern va cac service trung tam.",
        "Sequence Diagram: uu tien 5 flow o muc 9.",
        "State Diagram: ve vong doi Order o muc 10.",
        "ERD: ve bang users, beverages, toppings, orders, order_items, payments, inventory_items.",
        "Khi dat ten class trong so do, dung dung ten class trong tai lieu nay de khop code."
    ])

    add_white_box_chapter(doc)

    doc.add_page_break()
    doc.add_heading("Phu luc A - Tai khoan demo", level=1)
    table(doc, ["Username", "Password", "Role", "Man hinh"], [
        ("admin", "123", "ADMIN", "AdminView"),
        ("cashier01", "123", "CASHIER", "POSView"),
        ("kitchen01", "123", "KITCHEN", "KitchenView"),
    ], widths=[3, 3, 3, 5])

    doc.add_heading("Phu luc B - File/tai lieu can nop kem", level=1)
    add_bullets(doc, [
        "Source code GitHub: PhucQuan/Coffee_Shop_POS_DesignPattern",
        "docs/PATTERN_EVIDENCE_TABLE.md",
        "docs/DEMO_SCRIPT.md",
        "docs/agent-plan/STATE.md",
        "sql/schema.sql",
        "run.bat, test.bat, generate-assets.bat"
    ])

    doc.save(OUTPUT)
    print(OUTPUT)


if __name__ == "__main__":
    main()
