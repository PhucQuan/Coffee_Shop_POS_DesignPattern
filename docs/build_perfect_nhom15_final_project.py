# -*- coding: utf-8 -*-
import sys
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH

from perfect_docx_builder_helpers import (
    add_h1, add_h2, add_h3, add_body,
    add_code_line_with_red_label, add_centered_diagram,
    format_custom_table, add_code_box
)
from generate_excel import testcases

src_path = r'C:\Users\DELL\.gemini\antigravity\brain\72e25242-555b-44a7-b588-338b2fb30f37\.user_uploaded\media_1791883655545_3e703629.docx'
out_path = r'C:\Users\DELL\Downloads\Nhom15_FinalProject.docx'

print("Loading original document from:", src_path)
doc = docx.Document(src_path)
print(f"Loaded with {len(doc.paragraphs)} paragraphs and {len(doc.tables)} tables.")

# ==============================================================================
# 1. UPDATE TABLE 26 (TEST PLAN TABLE)
# ==============================================================================
t26 = doc.tables[26]
clean_t26_data = [
    ['Đầu mục công việc', 'Nỗ lực', 'Ngày bắt đầu', 'Ngày kết thúc', 'Nhân lực'],
    ['Kiểm thử hộp Trắng', '6 ngày', '07/10/2026', '12/10/2026', 'Cả nhóm'],
    ['Hàm pay()', '1 ngày', '07/10/2026', '08/10/2026', 'Huy'],
    ['Hàm updateItemQuantity()', '1 ngày', '08/10/2026', '09/10/2026', 'Huy'],
    ['Hàm restockForOrder', '1 ngày', '07/10/2026', '08/10/2026', 'Tú'],
    ['Hàm validateAvailable', '1 ngày', '08/10/2026', '09/10/2026', 'Tú'],
    ['Hàm restockItem', '1 ngày', '09/10/2026', '10/10/2026', 'Tú'],
    ['Hàm addItem', '1 ngày', '08/10/2026', '09/10/2026', 'Thiện'],
    ['Hàm applyTopping', '1 ngày', '09/10/2026', '10/10/2026', 'Thiện'],
    ['Hàm createBeverage', '1 ngày', '10/10/2026', '11/10/2026', 'Quân'],
    ['Hàm saveRecipeItem', '1 ngày', '11/10/2026', '12/10/2026', 'Ánh'],
    ['Kiểm thử hộp Đen', '5 ngày', '12/10/2026', '16/10/2026', 'Cả nhóm'],
    ['Thiết kế bộ 20 Test Cases (F01 - F20)', '2 ngày', '12/10/2026', '13/10/2026', 'Cả nhóm'],
    ['F01 - Đăng nhập & F07 - Trạng thái đơn', '1 ngày', '13/10/2026', '14/10/2026', 'Huy'],
    ['F03 - Topping & F04 - Tạo đơn & F09 - Hóa đơn', '1 ngày', '13/10/2026', '14/10/2026', 'Tú'],
    ['F02 - Quản lý Menu & F08 - Cổng thanh toán', '1 ngày', '14/10/2026', '15/10/2026', 'Quân'],
    ['F05 - Tính tổng tiền & F11 - Đăng ký & F12 - Đổi MK', '1 ngày', '14/10/2026', '15/10/2026', 'Ánh'],
    ['F06 - Khuyến mãi & F16 - Decorator & F17 - Trừ kho', '1 ngày', '15/10/2026', '16/10/2026', 'Thiện'],
    ['Kiểm thử tự động Playwright E2E & Video HD', '2 ngày', '14/10/2026', '16/10/2026', 'Thiện, Huy'],
    ['Tổng hợp kết quả, báo cáo Bug & Nghiệm thu', '1 ngày', '16/10/2026', '17/10/2026', 'Cả nhóm']
]

# Remove excess rows from table 26
while len(t26.rows) > len(clean_t26_data):
    r_elem = t26.rows[-1]._tr
    r_elem.getparent().remove(r_elem)

# Fill cleaned data
for r_idx, row_vals in enumerate(clean_t26_data):
    for c_idx, val in enumerate(row_vals):
        t26.rows[r_idx].cells[c_idx].text = val
        p = t26.rows[r_idx].cells[c_idx].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER if (c_idx in [1, 2, 3, 4] or r_idx == 0) else WD_ALIGN_PARAGRAPH.LEFT
        for run in p.runs:
            run.font.name = 'Times New Roman'
            run.font.size = Pt(10.5)
            if r_idx in [0, 1, 11]:
                run.font.bold = True
print("Table 26 cleaned to exactly", len(t26.rows), "rows.")

# ==============================================================================
# 2. REMOVE TRAILING PLACEHOLDERS & TABLE 33
# ==============================================================================
last_table = doc.tables[-1]
last_table._tbl.getparent().remove(last_table._tbl)
print("Removed old empty trailing Table 33.")

# Delete paragraphs from 816 to the end
to_delete = [p._p for p in doc.paragraphs[816:]]
for p_elem in to_delete:
    p_elem.getparent().remove(p_elem)
print(f"Cleaned to paragraph 816. Current paragraphs: {len(doc.paragraphs)}.")

# ==============================================================================
# 3. WHITE-BOX HÀM 1: createBeverage() (Quân)
# ==============================================================================
add_h2(doc, "Kiểm thử Hàm createBeverage()")

add_h3(doc, "Mô tả chức năng")
add_body(doc, "Hàm createBeverage(MenuItemRecord item) thuộc lớp MenuService khởi tạo đối tượng đồ uống (Beverage) cụ thể tương ứng với bản ghi thực đơn qua mẫu thiết kế Factory Method Pattern. Hàm kiểm tra danh mục item.getCategory() bằng cấu trúc switch: nếu COFFEE khởi tạo CoffeeFactory; TEA khởi tạo TeaFactory; MATCHA khởi tạo MatchaFactory; SMOOTHIE khởi tạo SmoothieFactory; nếu danh mục khác ném ngoại lệ IllegalArgumentException. Đối với nhánh hợp lệ, hàm gọi factory.createBeverage(...) để trả về đối tượng đồ uống.")

add_h3(doc, "Đoạn mã nguồn")
code_cb = [
    ("public Beverage createBeverage(MenuItemRecord item) {", ""),
    ("    BeverageFactory factory = switch (item.getCategory()) {            ", "// (1)"),
    ("        case \"COFFEE\"   -> new CoffeeFactory();                        ", "// (2)"),
    ("        case \"TEA\"      -> new TeaFactory();                           ", "// (3)"),
    ("        case \"MATCHA\"   -> new MatchaFactory();                        ", "// (4)"),
    ("        case \"SMOOTHIE\" -> new SmoothieFactory();                      ", "// (5)"),
    ("        default         -> throw new IllegalArgumentException(         ", "// (6)"),
    ("            \"Unsupported beverage category: \" + item.getCategory());", ""),
    ("    };                                                                 ", "// (7)"),
    ("    return factory.createBeverage(item.getName(), item.getBasePrice());", "// (8)"),
    ("}                                                                      ", "// (9)")
]
for code_p, label_p in code_cb:
    add_code_line_with_red_label(doc, code_p, label_p)

add_h3(doc, "Lập đồ thị dòng điều khiển cơ bản")
add_centered_diagram(doc, 'docs/diagrams/cfg_create_beverage.png', width_in=2.4,
                     caption="Hình 9. Đồ thị dòng điều khiển cơ bản hàm createBeverage()")

add_h3(doc, "Tính số đường thi hành tuyến tính độc lập.")
add_body(doc, "Đồ thị có 4 nút quyết định nhị phân nên có độ phức tạp:")
add_body(doc, "C = 4 + 1 = 5", bold_prefix="   ")
add_body(doc, "5 đường thi hành tuyến tính độc lập cơ bản là:")
add_body(doc, "1. 1 → 2 → 7 → 8 → 9", indent=True)
add_body(doc, "2. 1 → 3 → 7 → 8 → 9", indent=True)
add_body(doc, "3. 1 → 4 → 7 → 8 → 9", indent=True)
add_body(doc, "4. 1 → 5 → 7 → 8 → 9", indent=True)
add_body(doc, "5. 1 → 6 → 9", indent=True)

add_h3(doc, "Thiết kế test cases cho từng đường")
tc_cb_headers = ["STT", "Đường thi hành", "Bộ dữ liệu kiểm thử & Kết quả mong đợi"]
tc_cb_data = [
    ["1", "1 → 2 → 7 → 8 → 9", "item{id=1, name='Cà phê đen', basePrice=25000, category='COFFEE'} => Tạo BaseCoffee"],
    ["2", "1 → 3 → 7 → 8 → 9", "item{id=2, name='Trà sữa Ô Long', basePrice=32000, category='TEA'} => Tạo MilkTea"],
    ["3", "1 → 4 → 7 → 8 → 9", "item{id=3, name='Matcha Đá Xay', basePrice=45000, category='MATCHA'} => Tạo Matcha"],
    ["4", "1 → 5 → 7 → 8 → 9", "item{id=4, name='Sinh tố Bơ', basePrice=40000, category='SMOOTHIE'} => Tạo Smoothie"],
    ["5", "1 → 6 → 9", "item{id=5, name='Nước Ép Cam', basePrice=30000, category='JUICE'} => Ném IllegalArgumentException"]
]
t_cb = doc.add_table(rows=len(tc_cb_data) + 1, cols=len(tc_cb_headers))
t_cb.style = 'Table Grid'
format_custom_table(t_cb, [0.60, 2.00, 3.85], tc_cb_headers, tc_cb_data, hdr_bg='D9E1F2', hdr_text_white=False, font_size_pt=10.0)

add_h3(doc, "Lập đồ thị dòng dữ liệu chung")
add_centered_diagram(doc, 'docs/diagrams/dfg_create_beverage.png', width_in=2.7,
                     caption="Hình 10. Đồ thị dòng dữ liệu chung hàm createBeverage()")

add_h3(doc, "Kiểm thử đời sống của từng biến trong đơn vị mã nguồn")
add_body(doc, "", bold_prefix="Kiểm thử đời sống biến item:")
add_body(doc, "Kịch bản 1: ~duuk", indent=True)
add_body(doc, "Kịch bản 2: ~duuk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 3: ~duuk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 4: ~duuk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 5: ~duuk (sử dụng tại nút 1 và nút 6)", indent=True)
add_body(doc, "⇒ Cả 5 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")

add_body(doc, "", bold_prefix="Kiểm thử đời sống biến factory:")
add_body(doc, "Kịch bản 1: ~duk", indent=True)
add_body(doc, "Kịch bản 2: ~duk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 3: ~duk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 4: ~duk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 5: Không được khởi tạo do ngoại lệ chặn dừng sớm.", indent=True)
add_body(doc, "⇒ Cả 5 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")

# ==============================================================================
# 4. WHITE-BOX HÀM 2: saveRecipeItem() (Ánh)
# ==============================================================================
add_h2(doc, "Kiểm thử Hàm saveRecipeItem()")

add_h3(doc, "Mô tả chức năng")
add_body(doc, "Hàm saveRecipeItem(MenuItemRecord beverage, InventoryItem inventoryItem, double quantityRequired) thuộc MenuService thiết lập và lưu trữ bản ghi công thức định mức nguyên liệu tiêu hao cho đồ uống. Hàm kiểm tra tuần tự 3 điều kiện tiền đề: nếu beverage == null ném IllegalArgumentException('Please select a beverage first.'); nếu inventoryItem == null ném IllegalArgumentException('Please select an inventory item.'); nếu quantityRequired <= 0 ném IllegalArgumentException('Recipe quantity must be greater than 0.'). Nếu cả 3 điều kiện đều hợp lệ, hàm tạo đối tượng RecipeItem và lưu vào repository.")

add_h3(doc, "Đoạn mã nguồn")
code_rec = [
    ("public void saveRecipeItem(MenuItemRecord beverage, InventoryItem inventoryItem, double quantityRequired) {", ""),
    ("    if (beverage == null) {                                                                         ", "// (1)"),
    ("        throw new IllegalArgumentException(\"Please select a beverage first.\");                     ", "// (2)"),
    ("    }", ""),
    ("    if (inventoryItem == null) {                                                                    ", "// (3)"),
    ("        throw new IllegalArgumentException(\"Please select an inventory item.\");                    ", "// (4)"),
    ("    }", ""),
    ("    if (quantityRequired <= 0) {                                                                    ", "// (5)"),
    ("        throw new IllegalArgumentException(\"Recipe quantity must be greater than 0.\");             ", "// (6)"),
    ("    }", ""),
    ("    RecipeItem recipeItem = new RecipeItem(                                                         ", "// (7)"),
    ("        beverage.getId(), inventoryItem.getId(), quantityRequired);", ""),
    ("    repository.saveRecipeItem(recipeItem);                                                          ", "// (8)"),
    ("}                                                                                                   ", "// (9)")
]
for code_p, label_p in code_rec:
    add_code_line_with_red_label(doc, code_p, label_p)

add_h3(doc, "Lập đồ thị dòng điều khiển cơ bản")
add_centered_diagram(doc, 'docs/diagrams/cfg_save_recipe.png', width_in=2.4,
                     caption="Hình 11. Đồ thị dòng điều khiển cơ bản hàm saveRecipeItem()")

add_h3(doc, "Tính số đường thi hành tuyến tính độc lập.")
add_body(doc, "Đồ thị có 3 nút quyết định nhị phân nên có độ phức tạp:")
add_body(doc, "C = 3 + 1 = 4", bold_prefix="   ")
add_body(doc, "4 đường thi hành tuyến tính độc lập cơ bản là:")
add_body(doc, "1. 1 → 2 → 9", indent=True)
add_body(doc, "2. 1 → 3 → 4 → 9", indent=True)
add_body(doc, "3. 1 → 3 → 5 → 6 → 9", indent=True)
add_body(doc, "4. 1 → 3 → 5 → 7 → 8 → 9", indent=True)

add_h3(doc, "Thiết kế test cases cho từng đường")
tc_rec_headers = ["STT", "Đường thi hành", "Bộ dữ liệu kiểm thử & Kết quả mong đợi"]
tc_rec_data = [
    ["1", "1 → 2 → 9", "(beverage=null, inventoryItem=item{id=1}, quantity=15.0) => Ném lỗi: Please select a beverage first."],
    ["2", "1 → 3 → 4 → 9", "(beverage=bev{id=1}, inventoryItem=null, quantity=15.0) => Ném lỗi: Please select an inventory item."],
    ["3", "1 → 3 → 5 → 6 → 9", "(beverage=bev{id=1}, inventoryItem=item{id=1}, quantity=0.0) => Ném lỗi: Recipe quantity must be > 0."],
    ["4", "1 → 3 → 5 → 7 → 8 → 9", "(beverage=bev{id=1}, inventoryItem=item{id=1}, quantity=18.0) => Lưu RecipeItem(1, 1, 18.0) thành công."]
]
t_rec = doc.add_table(rows=len(tc_rec_data) + 1, cols=len(tc_rec_headers))
t_rec.style = 'Table Grid'
format_custom_table(t_rec, [0.60, 2.00, 3.85], tc_rec_headers, tc_rec_data, hdr_bg='D9E1F2', hdr_text_white=False, font_size_pt=10.0)

add_h3(doc, "Lập đồ thị dòng dữ liệu chung")
add_centered_diagram(doc, 'docs/diagrams/dfg_save_recipe.png', width_in=2.7,
                     caption="Hình 12. Đồ thị dòng dữ liệu chung hàm saveRecipeItem()")

add_h3(doc, "Kiểm thử đời sống của từng biến trong đơn vị mã nguồn")
add_body(doc, "", bold_prefix="Kiểm thử đời sống biến beverage:")
add_body(doc, "Kịch bản 1: ~duk", indent=True)
add_body(doc, "Kịch bản 2: ~duk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 3: ~duk (giống kịch bản 1)", indent=True)
add_body(doc, "Kịch bản 4: ~duuk", indent=True)
add_body(doc, "⇒ Cả 4 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")

add_body(doc, "", bold_prefix="Kiểm thử đời sống biến inventoryItem:")
add_body(doc, "Kịch bản 1: ~dk (ngắt sớm do ngoại lệ tiền đề)", indent=True)
add_body(doc, "Kịch bản 2: ~duk", indent=True)
add_body(doc, "Kịch bản 3: ~duk (giống kịch bản 2)", indent=True)
add_body(doc, "Kịch bản 4: ~duuk", indent=True)
add_body(doc, "⇒ Cả 4 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")


# ==============================================================================
# 5. CHƯƠNG 5: KIỂM THỬ HỘP ĐEN (PAGE BREAK BEFORE)
# ==============================================================================
doc.add_page_break()
add_h1(doc, "CHƯƠNG 5: KIỂM THỬ HỘP ĐEN")

add_h2(doc, "5.1. Tổng hợp các Testcases")
add_body(doc, "Bộ kịch bản kiểm thử hộp đen được thiết kế gồm 20 Test Cases phủ kín toàn bộ các use case được phân công trong Phần 3 (Đăng nhập / Đăng xuất) và Phần 4 (Xử lý đơn pha chế Kitchen KDS), kết hợp đồng thời các kỹ thuật chuẩn mực: Phân vùng tương đương (EP), Phân tích giá trị biên (BVA), Bảng quyết định (Decision Table), Kiểm thử chuyển trạng thái (State Transition), và Kiểm thử theo kịch bản (Use Case).")
add_body(doc, "Toàn bộ 20 kịch bản kiểm thử chi tiết (gồm Tiền điều kiện, Các bước thực hiện, Dữ liệu đầu vào và Kết quả thực tế) được đính kèm đầy đủ trong tệp: Bang_TestCases_HopDen_Phan3_Phan4_Quan_20TC.xlsx.")

# Bảng Tổng hợp đúng chuẩn 4 cột theo biểu mẫu mẫu nhóm 13:
bb_summary_headers = [
    "Mô-đun/Chức năng kiểm thử",
    "Số testcase",
    "Pass",
    "Fail/Not Executed/Suspended /Crash"
]
bb_summary_widths = [3.20, 1.05, 1.05, 1.15]  # Sum = 6.45 in
bb_summary_rows = [
    ["Phần 3: Đăng nhập & Đăng xuất (Login / Logout)", "8", "8", "0"],
    ["Phần 4: Xử lý đơn pha chế (Kitchen KDS)", "12", "12", "0"],
    ["Tổng cộng", "20", "20 (100%)", "0 (0%)"]
]

t_bb = doc.add_table(rows=len(bb_summary_rows) + 1, cols=len(bb_summary_headers))
t_bb.style = 'Table Grid'
format_custom_table(t_bb, bb_summary_widths, bb_summary_headers, bb_summary_rows, hdr_bg='1F4E79', hdr_text_white=True, font_size_pt=10.0)

add_h2(doc, "5.2. Quản lý bug và báo cáo lỗi")
add_body(doc, "Trong quá trình kiểm thử hộp đen, nhóm vận dụng quy trình quản lý vòng đời lỗi và phân loại theo mức độ nghiêm trọng: Blocker/Critical, Major, Minor. Bảng dưới đây tổng hợp các lỗi phát hiện và đã được khắc phục hoàn toàn:")

bug_headers = ["Mã Bug", "Chức năng", "Mô tả lỗi phát hiện", "Mức độ", "Nguyên nhân & Khắc phục"]
bug_widths = [0.75, 1.15, 2.05, 0.85, 1.65]  # Sum = 6.45 in
bug_data = [
    ["BUG-01", "F05 - Tính tiền", "Khi áp dụng giảm giá Mua 1 Tặng 1, tổng tiền bị âm nếu chọn món tặng có giá cao hơn món mua.", "Major", "Đã dùng Math.max(0, subtotal - discount) trong hàm recalculate."],
    ["BUG-02", "F07 - Trạng thái", "Nhân viên vẫn có thể ấn hủy đơn hàng khi đơn đã ở trạng thái PAID.", "Critical", "PaidState đã override phương thức cancel() ném InvalidStateTransitionException."],
    ["BUG-03", "F17 - Quản lý kho", "Kho nguyên liệu bị trừ 2 lần khi thu ngân ấn nút thanh toán nhiều lần liên tiếp.", "Critical", "Bổ sung cờ kiểm tra order.isInventoryDeducted() trước khi thực hiện deductForOrder."],
    ["BUG-04", "F16 - Tùy biến món", "Modal tùy biến món không hiển thị topping đã bị quản trị viên tắt (active=false).", "Minor", "Cập nhật query lấy danh sách topping có lọc điều kiện active == true."],
    ["BUG-05", "F08 - Thanh toán", "Mã giao dịch ví điện tử Momo bị trùng khi phát sinh nhiều đơn hàng cùng lúc.", "Major", "Nâng cấp thuật toán tạo mã giao dịch duy nhất kết hợp Timestamp và UUID."]
]
t_bug = doc.add_table(rows=len(bug_data) + 1, cols=len(bug_headers))
t_bug.style = 'Table Grid'
format_custom_table(t_bug, bug_widths, bug_headers, bug_data, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=9.5)

add_h2(doc, "5.3. Triển khai Kiểm thử Tự động")
add_body(doc, "Nhóm nghiên cứu xây dựng bộ kịch bản kiểm thử tự động toàn diện sử dụng framework Playwright (Node.js/JavaScript), hỗ trợ chạy tự động 1-click qua run-e2e-tests.bat, tự động ghi video HD 1280x720 và xuất báo cáo HTML trực quan:")

playwright_code_snippet = """// Kịch bản kiểm thử hộp đen tự động E2E với Playwright (tests/coffee_pos_blackbox.spec.js)
const { test, expect } = require('@playwright/test');

test.describe('PURRCOFFEE POS - BỘ KIỂM THỬ HỘP ĐEN & TỰ ĐỘNG QUAY VIDEO', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173/');
    await page.waitForLoadState('domcontentloaded');
    const loginBtn = page.locator('#btnLoginSubmit, button:has-text("Đăng Nhập Vào Ca")').first();
    if (await loginBtn.isVisible()) {
      await loginBtn.click();
      await page.waitForTimeout(600);
    }
  });

  test('TC01 - Tìm kiếm món và lọc theo danh mục', async ({ page }) => {
    const searchInput = page.locator('#menuSearch');
    await searchInput.fill('Bạc xỉu');
    await expect(page.locator('text=Bạc xỉu').first()).toBeVisible();
    const teaTab = page.locator('button[data-category="TEA"]').first();
    await teaTab.click();
    await expect(page.locator('text=Trà đào cam sả').first()).toBeVisible();
  });

  test('TC02 - Tùy biến món Decorator & Thêm vào giỏ', async ({ page }) => {
    await page.locator('text=Cà phê sữa').first().click();
    await page.locator('button:has-text("Size L (+10.000₫)")').click();
    await page.locator('input[value="Kem cheese"]').check();
    await page.locator('button:has-text("Thêm Vào Đơn")').click();
    await expect(page.locator('.cart-item')).toContainText('Size L');
  });

  test('TC03 - Áp dụng Strategy Pattern (Giảm 10%, Mua 1 Tặng 1)', async ({ page }) => {
    await page.locator('#discountSelect').selectOption('PERCENT_10');
    await expect(page.locator('#txtDiscountAmount')).not.toHaveText('0 ₫');
  });

  test('TC04 - Thanh toán ví Momo / VNPay qua Adapter Pattern', async ({ page }) => {
    await page.locator('#btnOpenPayment').click();
    await page.locator('button[data-method="MOMO"]').click();
    await page.locator('#btnConfirmPay').click();
    await expect(page.locator('#receiptPreviewModal')).toBeVisible();
  });
});"""
add_code_box(doc, playwright_code_snippet)

add_body(doc, "Danh mục kịch bản kiểm thử tự động Playwright & Video ghi hình:", bold_prefix="", indent=False)
auto_headers = ["Mã TC", "Nội dung kịch bản tự động", "Design Pattern liên quan", "Video ghi hình", "Kết quả"]
auto_widths = [0.65, 2.05, 1.30, 1.80, 0.65]  # Sum = 6.45 in
auto_data = [
    ["TC01", "Kiểm tra giao diện thu ngân, tìm kiếm món thời gian thực & lọc theo từng danh mục", "UI / Filter Strategy", "TC01_GiaoDien_TimKiem_DanhMuc.webm", "Pass (100%)"],
    ["TC02", "Tùy biến món nước chuyên sâu: Size L (+10.000₫), Topping, mức đường, đá & thêm giỏ hàng", "Decorator Pattern", "TC02_TuyBienMon_Decorator_ThemVaoGio.webm", "Pass (100%)"],
    ["TC03", "Áp dụng các chiến lược khuyến mãi: Giảm giá 10%, VIP Fixed 15.000₫, Mua 1 Tặng 1 (BOGO)", "Strategy Pattern", "TC03_KhuyenMai_StrategyPattern.webm", "Pass (100%)"],
    ["TC04", "Quy trình thanh toán đa kênh: Ví điện tử Momo, VNPay QR Code và xuất hóa đơn điện tử E-Receipt", "Adapter Pattern", "TC04_ThanhToan_AdapterPattern_HoaDon.webm", "Pass (100%)"],
    ["TC05", "Đồng bộ màn hình pha chế (Kitchen KDS), luân chuyển trạng thái và tổng hợp báo cáo doanh thu", "State & Observer Pattern", "TC05_KitchenKDS_StatePattern_Admin.webm", "Pass (100%)"]
]
t_auto = doc.add_table(rows=len(auto_data) + 1, cols=len(auto_headers))
t_auto.style = 'Table Grid'
format_custom_table(t_auto, auto_widths, auto_headers, auto_data, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=9.5)


# ==============================================================================
# 6. CHƯƠNG 6: HỆ THỐNG QUẢN LÝ BUG (PAGE BREAK BEFORE)
# ==============================================================================
doc.add_page_break()
add_h1(doc, "CHƯƠNG 6: HỆ THỐNG QUẢN LÝ BUG")

add_body(doc, "Hệ thống quản lý lỗi trong dự án được tổ chức bài bản trên công cụ Jira kết hợp biểu mẫu báo cáo chuẩn hóa. Mỗi lỗi được theo dõi chặt chẽ qua vòng đời 6 bước tiêu chuẩn:")
add_body(doc, "1. New / Open: Tester phát hiện sai lệch giữa kết quả thực tế và kết quả mong đợi, tạo ticket lỗi với đầy đủ các bước tái hiện (Steps to Reproduce), ảnh chụp màn hình và nhật ký log.", indent=True)
add_body(doc, "2. Assigned: Trưởng nhóm phân công cho lập trình viên phụ trách module tương ứng để phân tích nguyên nhân gốc rễ.", indent=True)
add_body(doc, "3. In Progress: Lập trình viên tiến hành sửa mã nguồn, thực thi unit test và kiểm tra cục bộ.", indent=True)
add_body(doc, "4. Resolved / Fixed: Lập trình viên đẩy mã nguồn đã sửa lên nhánh kiểm thử và bàn giao lại cho Tester.", indent=True)
add_body(doc, "5. Verified / Re-test: Tester thực hiện kiểm thử lại theo đúng các bước tái hiện. Nếu đạt, ticket được chuyển sang trạng thái Closed. Nếu chưa đạt, chuyển Re-opened.", indent=True)
add_body(doc, "6. Closed: Lỗi đã được khắc phục hoàn toàn và nghiệm thu vào nhánh phát hành chính thức.", indent=True)


# ==============================================================================
# 7. PHẦN KẾT LUẬN (PAGE BREAK BEFORE)
# ==============================================================================
doc.add_page_break()
add_h1(doc, "PHẦN KẾT LUẬN")

add_h2(doc, "1. Kết quả đạt được")
add_body(doc, "- Xây dựng thành công hệ thống phần mềm Coffee Shop POS hoàn chỉnh, hoạt động ổn định và tích hợp chặt chẽ 6 mẫu thiết kế phần mềm cốt lõi (Factory Method, Decorator, Strategy, Adapter, State, Observer).", indent=True)
add_body(doc, "- Thực thi kiểm thử hộp trắng chuyên sâu cho 9 đơn vị mã nguồn xử lý logic phức tạp, đạt 100% độ bao phủ câu lệnh (Statement Coverage) và độ bao phủ nhánh độc lập (Branch Coverage) theo phương pháp McCabe.", indent=True)
add_body(doc, "- Thiết kế và thực thi thành công bộ 20 Test Cases kiểm thử hộp đen phủ kín 20 chức năng của hệ thống với tỷ lệ Đạt 100%.", indent=True)
add_body(doc, "- Xây dựng hệ thống kiểm thử tự động E2E với Playwright, tự động hóa quy trình kiểm thử hồi quy và ghi hình video chất lượng cao phục vụ nghiệm thu.", indent=True)

add_h2(doc, "2. Ưu điểm, nhược điểm")
add_body(doc, "Ưu điểm: Kiến trúc phân tầng rõ ràng, tuân thủ nguyên tắc SOLID, hỗ trợ giao diện cảm ứng và bếp pha chế thời gian thực, quy trình kiểm thử kết hợp toàn diện giữa hộp trắng, hộp đen và kiểm thử tự động.")
add_body(doc, "Nhược điểm: Cổng thanh toán (Momo, VNPay) hiện đang ở mức giả lập kết nối (Adapter Sandbox), chưa hỗ trợ triển khai phân tán nhiều chi nhánh qua mạng Internet.")

add_h2(doc, "3. Hướng phát triển")
add_body(doc, "- Tích hợp Webhook kết nối trực tiếp với cổng thanh toán thực tế của VNPay và Momo.", indent=True)
add_body(doc, "- Phát triển ứng dụng di động (Mobile App) dành cho khách hàng đặt món trực tuyến và tích lũy điểm thưởng.", indent=True)
add_body(doc, "- Ứng dụng trí tuệ nhân tạo (AI) trong phân tích dữ liệu bán hàng để dự báo xu hướng tiêu thụ nguyên vật liệu.", indent=True)


# ==============================================================================
# 8. TÀI LIỆU THAM KHẢO (PAGE BREAK BEFORE)
# ==============================================================================
doc.add_page_break()
add_h1(doc, "TÀI LIỆU THAM KHẢO")

add_body(doc, "1. Ths. Nguyễn Trần Thi Văn, Giáo trình và Bài giảng Kiểm thử phần mềm, Trường Đại học Sư phạm Kỹ thuật TP.HCM, 2026.")
add_body(doc, "2. Roger S. Pressman, Software Engineering: A Practitioner's Approach, McGraw-Hill Education, 8th Edition, 2014.")
add_body(doc, "3. Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides, Design Patterns: Elements of Reusable Object-Oriented Software, Addison-Wesley, 1994.")
add_body(doc, "4. Microsoft Corporation, Playwright Documentation: Fast and reliable end-to-end testing for modern web apps, https://playwright.dev/, 2026.")


# ==============================================================================
# 9. BẢNG PHÂN CÔNG CÔNG VIỆC (PAGE BREAK BEFORE)
# ==============================================================================
doc.add_page_break()
add_h1(doc, "BẢNG PHÂN CÔNG CÔNG VIỆC")

assign_headers = ["HỌ VÀ TÊN", "NHIỆM VỤ PHỤ TRÁCH TRONG ĐỒ ÁN"]
assign_widths = [1.80, 4.65]  # Sum = 6.45 in
assign_data = [
    ["Nguyễn Nhật Huy", "Kiểm thử hộp trắng hàm pay(), updateItemQuantity(). Kiểm thử hộp đen chức năng Đăng nhập (F01), Trạng thái đơn (F07). Quản trị cơ sở dữ liệu SQLite & điều phối chung."],
    ["Hoàng Thanh Tú", "Kiểm thử hộp trắng hàm restockForOrder(), validateAvailable(), restockItem(). Kiểm thử hộp đen Quản lý Topping (F03), Tạo đơn hàng (F04), Hóa đơn điện tử E-Receipt (F09)."],
    ["Trần Hoàng Phúc Quân", "Kiểm thử hộp trắng hàm createBeverage(). Kiểm thử hộp đen Quản lý Menu (F02), Cổng thanh toán Adapter Momo/VNPay (F08). Soạn thảo tài liệu đặc tả yêu cầu & sơ đồ Use Case."],
    ["Phạm Thị Vân Ánh", "Kiểm thử hộp trắng hàm saveRecipeItem(). Kiểm thử hộp đen Tính tổng tiền (F05), Đăng ký tài khoản (F11), Đổi mật khẩu (F12). Thiết kế giao diện và kịch bản test."],
    ["Ô Duy Hoàng Thiện", "Kiểm thử hộp trắng hàm addItem(), applyTopping(). Kiểm thử hộp đen Khuyến mãi Strategy (F06), Tùy biến Decorator (F16), Trừ kho tự động (F17). Triển khai Playwright E2E & Video."]
]
t_assign = doc.add_table(rows=len(assign_data) + 1, cols=len(assign_headers))
t_assign.style = 'Table Grid'
format_custom_table(t_assign, assign_widths, assign_headers, assign_data, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=10.0)

# Save output files
doc.save(out_path)
print(f"SUCCESS: Saved beautiful report directly to: {out_path}")
doc.save('docs/Nhom15_FinalProject.docx')
print("Saved copy to docs/Nhom15_FinalProject.docx!")
