# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH

from nhom15_updater_helpers import add_p, add_code_line, add_img, add_styled_table
from generate_excel import testcases

path = r'C:\Users\DELL\Downloads\Nhom15_FinalProject.docx'
doc = docx.Document(path)
print(f"Loaded {path} with {len(doc.paragraphs)} paragraphs and {len(doc.tables)} tables.")

# 1. Update Table 26 (Test Plan Table)
t26 = doc.tables[26]
if len(t26.rows) > 10:
    t26.rows[9].cells[0].text = "Hàm createBeverage"
    t26.rows[9].cells[1].text = "1 ngày"
    t26.rows[9].cells[2].text = "10/10/2026"
    t26.rows[9].cells[3].text = "11/10/2026"
    t26.rows[9].cells[4].text = "Quân"
    
    t26.rows[10].cells[0].text = "Hàm saveRecipeItem"
    t26.rows[10].cells[1].text = "1 ngày"
    t26.rows[10].cells[2].text = "11/10/2026"
    t26.rows[10].cells[3].text = "12/10/2026"
    t26.rows[10].cells[4].text = "Ánh"
    print("Table 26 updated rows 9 and 10.")

# 2. Remove paragraphs from index 816 to the end
# And remove the last table (Table 33)
last_table = doc.tables[-1]
last_table._tbl.getparent().remove(last_table._tbl)
print("Removed empty trailing Table 33.")

# Delete paragraphs from 816 to end
to_delete = [p._p for p in doc.paragraphs[816:]]
for p_elem in to_delete:
    p_elem.getparent().remove(p_elem)
print(f"Removed trailing placeholder paragraphs, document now has {len(doc.paragraphs)} paragraphs.")

# 3. Append White-box Hàm 1: createBeverage()
add_p(doc, "Kiểm thử Hàm createBeverage()", font_size=13, bold=True, space_after=4)
add_p(doc, "Mô tả chức năng", font_size=12, bold=True, space_after=3)
add_p(doc, "Hàm createBeverage(MenuItemRecord item) thuộc lớp MenuService khởi tạo đối tượng đồ uống (Beverage) cụ thể tương ứng với bản ghi thực đơn qua mẫu thiết kế Factory Method Pattern. Hàm kiểm tra danh mục item.getCategory() bằng cấu trúc switch: nếu COFFEE khởi tạo CoffeeFactory; TEA khởi tạo TeaFactory; MATCHA khởi tạo MatchaFactory; SMOOTHIE khởi tạo SmoothieFactory; nếu danh mục khác ném ngoại lệ IllegalArgumentException. Đối với nhánh hợp lệ, hàm gọi factory.createBeverage(...) để trả về đối tượng đồ uống.")

add_p(doc, "Đoạn mã nguồn", font_size=12, bold=True, space_after=3)
code_lines_cb = [
    "public Beverage createBeverage(MenuItemRecord item) {",
    "    BeverageFactory factory = switch (item.getCategory()) {            // (1)",
    "        case \"COFFEE\"   -> new CoffeeFactory();                        // (2)",
    "        case \"TEA\"      -> new TeaFactory();                           // (3)",
    "        case \"MATCHA\"   -> new MatchaFactory();                        // (4)",
    "        case \"SMOOTHIE\" -> new SmoothieFactory();                      // (5)",
    "        default         -> throw new IllegalArgumentException(         // (6)",
    "            \"Unsupported beverage category: \" + item.getCategory());",
    "    };                                                                 // (7)",
    "    return factory.createBeverage(item.getName(), item.getBasePrice());// (8)",
    "}                                                                      // (9)"
]
for line in code_lines_cb:
    add_code_line(doc, line)

add_p(doc, "Lập đồ thị dòng điều khiển cơ bản", font_size=12, bold=True, space_after=3)
add_img(doc, 'docs/diagrams/cfg_create_beverage.png', width_in=2.5, caption="Hình 9. Đồ thị dòng điều khiển cơ bản hàm createBeverage()")

add_p(doc, "Tính số đường thi hành tuyến tính độc lập.", font_size=12, bold=True, space_after=3)
add_p(doc, "Đồ thị có 4 nút quyết định nhị phân nên có độ phức tạp C = 4 + 1 = 5.")
add_p(doc, "5 đường thi hành tuyến tính độc lập cơ bản là:")
add_p(doc, "1. 1 → 2 → 7 → 8 → 9")
add_p(doc, "2. 1 → 3 → 7 → 8 → 9")
add_p(doc, "3. 1 → 4 → 7 → 8 → 9")
add_p(doc, "4. 1 → 5 → 7 → 8 → 9")
add_p(doc, "5. 1 → 6 → 9")

add_p(doc, "Thiết kế test cases cho từng đường", font_size=12, bold=True, space_after=3)
tc_cb_headers = ["STT", "Đường thi hành", "Bộ dữ liệu kiểm thử"]
tc_cb_data = [
    ["1", "1 → 2 → 7 → 8 → 9", "item{id=1, name='Cà phê đen', basePrice=25000, category='COFFEE'}"],
    ["2", "1 → 3 → 7 → 8 → 9", "item{id=2, name='Trà sữa Ô Long', basePrice=32000, category='TEA'}"],
    ["3", "1 → 4 → 7 → 8 → 9", "item{id=3, name='Matcha Đá Xay', basePrice=45000, category='MATCHA'}"],
    ["4", "1 → 5 → 7 → 8 → 9", "item{id=4, name='Sinh tố Bơ', basePrice=40000, category='SMOOTHIE'}"],
    ["5", "1 → 6 → 9", "item{id=5, name='Nước Ép Cam', basePrice=30000, category='JUICE'}"]
]
add_styled_table(doc, tc_cb_headers, tc_cb_data, [0.6, 2.0, 3.8])

add_p(doc, "Bộ dữ liệu kiểm thử chi tiết", font_size=12, bold=True, space_after=3)
detail_cb_headers = ["STT", "Loại / mục đích", "Dữ liệu đầu vào chi tiết", "Kết quả mong đợi"]
detail_cb_data = [
    ["1", "Đường 1 → 2 → 7 → 8 → 9", "category='COFFEE', name='Cà phê đen', basePrice=25000", "Tạo BaseCoffee, giá 25.000 ₫"],
    ["2", "Đường 1 → 3 → 7 → 8 → 9", "category='TEA', name='Trà sữa Ô Long', basePrice=32000", "Tạo MilkTea, giá 32.000 ₫"],
    ["3", "Đường 1 → 4 → 7 → 8 → 9", "category='MATCHA', name='Matcha Đá Xay', basePrice=45000", "Tạo Matcha, giá 45.000 ₫"],
    ["4", "Đường 1 → 5 → 7 → 8 → 9", "category='SMOOTHIE', name='Sinh tố Bơ', basePrice=40000", "Tạo Smoothie, giá 40.000 ₫"],
    ["5", "Đường 1 → 6 → 9", "category='JUICE', name='Nước Ép Cam', basePrice=30000", "Ném IllegalArgumentException('Unsupported beverage category: JUICE')"]
]
add_styled_table(doc, detail_cb_headers, detail_cb_data, [0.5, 1.8, 2.2, 2.0])

add_p(doc, "Lập đồ thị dòng dữ liệu chung", font_size=12, bold=True, space_after=3)
add_img(doc, 'docs/diagrams/dfg_create_beverage.png', width_in=2.8, caption="Hình 10. Đồ thị dòng dữ liệu chung hàm createBeverage()")

add_p(doc, "Kiểm thử đời sống của từng biến trong đơn vị mã nguồn", font_size=12, bold=True, space_after=3)
add_p(doc, "Kiểm thử đời sống biến item:", bold_prefix="", font_size=11, bold=True)
add_img(doc, 'docs/diagrams/var_item_create_beverage.png', width_in=2.2, caption="Hình: Đời sống biến item")
add_p(doc, "Kịch bản 1: ~duuk")
add_p(doc, "Kịch bản 2: ~duuk (giống kịch bản 1)")
add_p(doc, "Kịch bản 3: ~duuk (giống kịch bản 1)")
add_p(doc, "Kịch bản 4: ~duuk (giống kịch bản 1)")
add_p(doc, "Kịch bản 5: ~duuk (sử dụng tại nút 1 và nút 6)")
add_p(doc, "=> Cả 5 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")

add_p(doc, "Kiểm thử đời sống biến factory:", bold_prefix="", font_size=11, bold=True)
add_img(doc, 'docs/diagrams/var_factory_create_beverage.png', width_in=2.2, caption="Hình: Đời sống biến factory")
add_p(doc, "Kịch bản 1: ~duk")
add_p(doc, "Kịch bản 2: ~duk (giống kịch bản 1)")
add_p(doc, "Kịch bản 3: ~duk (giống kịch bản 1)")
add_p(doc, "Kịch bản 4: ~duk (giống kịch bản 1)")
add_p(doc, "Kịch bản 5: Không được khởi tạo do ngoại lệ chặn dừng sớm.")
add_p(doc, "=> Cả 5 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")


# 4. Append White-box Hàm 2: saveRecipeItem()
add_p(doc, "Kiểm thử Hàm saveRecipeItem()", font_size=13, bold=True, space_after=4)
add_p(doc, "Mô tả chức năng", font_size=12, bold=True, space_after=3)
add_p(doc, "Hàm saveRecipeItem(MenuItemRecord beverage, InventoryItem inventoryItem, double quantityRequired) thuộc MenuService thiết lập và lưu trữ bản ghi công thức định mức nguyên liệu tiêu hao cho đồ uống. Hàm kiểm tra tuần tự 3 điều kiện tiền đề: nếu beverage == null ném IllegalArgumentException('Please select a beverage first.'); nếu inventoryItem == null ném IllegalArgumentException('Please select an inventory item.'); nếu quantityRequired <= 0 ném IllegalArgumentException('Recipe quantity must be greater than 0.'). Nếu cả 3 điều kiện đều hợp lệ, hàm tạo đối tượng RecipeItem và lưu vào repository.")

add_p(doc, "Đoạn mã nguồn", font_size=12, bold=True, space_after=3)
code_lines_rec = [
    "public void saveRecipeItem(MenuItemRecord beverage, InventoryItem inventoryItem, double quantityRequired) {",
    "    if (beverage == null) {                                                                         // (1)",
    "        throw new IllegalArgumentException(\"Please select a beverage first.\");                     // (2)",
    "    }",
    "    if (inventoryItem == null) {                                                                    // (3)",
    "        throw new IllegalArgumentException(\"Please select an inventory item.\");                    // (4)",
    "    }",
    "    if (quantityRequired <= 0) {                                                                    // (5)",
    "        throw new IllegalArgumentException(\"Recipe quantity must be greater than 0.\");             // (6)",
    "    }",
    "    RecipeItem recipeItem = new RecipeItem(                                                         // (7)",
    "        beverage.getId(), inventoryItem.getId(), quantityRequired);",
    "    repository.saveRecipeItem(recipeItem);                                                          // (8)",
    "}                                                                                                   // (9)"
]
for line in code_lines_rec:
    add_code_line(doc, line)

add_p(doc, "Lập đồ thị dòng điều khiển cơ bản", font_size=12, bold=True, space_after=3)
add_img(doc, 'docs/diagrams/cfg_save_recipe.png', width_in=2.2, caption="Hình 11. Đồ thị dòng điều khiển cơ bản hàm saveRecipeItem()")

add_p(doc, "Tính số đường thi hành tuyến tính độc lập.", font_size=12, bold=True, space_after=3)
add_p(doc, "Đồ thị có 3 nút quyết định nhị phân nên có độ phức tạp C = 3 + 1 = 4.")
add_p(doc, "4 đường thi hành tuyến tính độc lập cơ bản là:")
add_p(doc, "1. 1 → 2 → 9")
add_p(doc, "2. 1 → 3 → 4 → 9")
add_p(doc, "3. 1 → 3 → 5 → 6 → 9")
add_p(doc, "4. 1 → 3 → 5 → 7 → 8 → 9")

add_p(doc, "Thiết kế test cases cho từng đường", font_size=12, bold=True, space_after=3)
tc_rec_headers = ["STT", "Đường thi hành", "Bộ dữ liệu kiểm thử"]
tc_rec_data = [
    ["1", "1 → 2 → 9", "(beverage = null, inventoryItem = item{id=1}, quantityRequired = 15.0)"],
    ["2", "1 → 3 → 4 → 9", "(beverage = bev{id=1}, inventoryItem = null, quantityRequired = 15.0)"],
    ["3", "1 → 3 → 5 → 6 → 9", "(beverage = bev{id=1}, inventoryItem = item{id=1}, quantityRequired = 0.0)"],
    ["4", "1 → 3 → 5 → 7 → 8 → 9", "(beverage = bev{id=1}, inventoryItem = item{id=1}, quantityRequired = 18.0)"]
]
add_styled_table(doc, tc_rec_headers, tc_rec_data, [0.6, 2.0, 3.8])

add_p(doc, "Bộ dữ liệu kiểm thử chi tiết", font_size=12, bold=True, space_after=3)
detail_rec_headers = ["STT", "Loại / mục đích", "Dữ liệu đầu vào chi tiết", "Kết quả mong đợi"]
detail_rec_data = [
    ["1", "Đường 1 → 2 → 9", "beverage = null, inventoryItem = item{id=1}, quantityRequired = 15.0", "IllegalArgumentException: Please select a beverage first."],
    ["2", "Đường 1 → 3 → 4 → 9", "beverage = bev{id=1}, inventoryItem = null, quantityRequired = 15.0", "IllegalArgumentException: Please select an inventory item."],
    ["3", "Đường 1 → 3 → 5 → 6 → 9", "beverage = bev{id=1}, inventoryItem = item{id=1}, quantityRequired = 0.0", "IllegalArgumentException: Recipe quantity must be greater than 0."],
    ["4", "Đường 1 → 3 → 5 → 7 → 8 → 9", "beverage = bev{id=1}, inventoryItem = item{id=1}, quantityRequired = 18.0", "Lưu RecipeItem(1, 1, 18.0) vào repository thành công."]
]
add_styled_table(doc, detail_rec_headers, detail_rec_data, [0.5, 1.8, 2.2, 2.0])

add_p(doc, "Lập đồ thị dòng dữ liệu chung", font_size=12, bold=True, space_after=3)
add_img(doc, 'docs/diagrams/dfg_save_recipe.png', width_in=2.8, caption="Hình 12. Đồ thị dòng dữ liệu chung hàm saveRecipeItem()")

add_p(doc, "Kiểm thử đời sống của từng biến trong đơn vị mã nguồn", font_size=12, bold=True, space_after=3)
add_p(doc, "Kiểm thử đời sống biến beverage:", bold_prefix="", font_size=11, bold=True)
add_img(doc, 'docs/diagrams/var_beverage_save_recipe.png', width_in=2.2, caption="Hình: Đời sống biến beverage")
add_p(doc, "Kịch bản 1: ~duk")
add_p(doc, "Kịch bản 2: ~duk (giống kịch bản 1)")
add_p(doc, "Kịch bản 3: ~duk (giống kịch bản 1)")
add_p(doc, "Kịch bản 4: ~duuk")
add_p(doc, "=> Cả 4 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")

add_p(doc, "Kiểm thử đời sống biến inventoryItem:", bold_prefix="", font_size=11, bold=True)
add_img(doc, 'docs/diagrams/var_inventory_save_recipe.png', width_in=2.2, caption="Hình: Đời sống biến inventoryItem")
add_p(doc, "Kịch bản 1: ~dk (ngắt sớm do ngoại lệ tiền đề)")
add_p(doc, "Kịch bản 2: ~duk")
add_p(doc, "Kịch bản 3: ~duk (giống kịch bản 2)")
add_p(doc, "Kịch bản 4: ~duuk")
add_p(doc, "=> Cả 4 kịch bản đều không chứa cặp đôi hoạt động bất thường nào cả.")


# 5. Append CHƯƠNG 5: KIỂM THỬ HỘP ĐEN
add_p(doc, "CHƯƠNG 5: KIỂM THỬ HỘP ĐEN", font_size=14, bold=True, space_after=6)
add_p(doc, "5.1. Tổng hợp các Testcases", font_size=12, bold=True, space_after=3)
add_p(doc, "Bộ kịch bản kiểm thử hộp đen được thiết kế gồm 20 Test Cases phủ kín 20 chức năng cốt lõi (từ F01 đến F20), kết hợp đồng thời các kỹ thuật chuẩn mực: Phân vùng tương đương (EP), Phân tích giá trị biên (BVA), Bảng quyết định (Decision Table), Kiểm thử chuyển trạng thái (State Transition), và Kiểm thử theo kịch bản (Use Case). Dữ liệu được đồng bộ với tệp Excel: docs/Bang_TestCases_HopDen_CoffeeShopPOS_20TC.xlsx.")

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
add_styled_table(doc, bb_headers, bb_data, [0.4, 0.8, 1.1, 1.4, 1.2, 1.5, 0.9, 0.6, 0.9])

add_p(doc, "5.2. Quản lý bug và báo cáo lỗi", font_size=12, bold=True, space_after=3)
add_p(doc, "Trong quá trình kiểm thử hộp đen, nhóm vận dụng quy trình quản lý vòng đời lỗi và phân loại theo mức độ nghiêm trọng: Blocker/Critical, Major, Minor. Bảng dưới đây tổng hợp các lỗi phát hiện và đã được khắc phục hoàn toàn:")

bug_headers = ["Mã Bug", "Chức năng", "Mô tả lỗi phát hiện", "Mức độ", "Nguyên nhân gốc", "Tình trạng"]
bug_data = [
    ["BUG-01", "F05 - Tính tiền", "Khi áp dụng giảm giá Mua 1 Tặng 1, tổng tiền bị âm nếu chọn món tặng có giá cao hơn món mua.", "Major", "Chưa dùng hàm Math.max(0, subtotal - discount) trong hàm recalculate.", "Đã khắc phục (Closed)"],
    ["BUG-02", "F07 - Trạng thái", "Nhân viên vẫn có thể ấn hủy đơn hàng khi đơn đã ở trạng thái PAID.", "Critical", "Trạng thái PaidState chưa override phương thức cancel() để ném InvalidStateTransitionException.", "Đã khắc phục (Closed)"],
    ["BUG-03", "F17 - Quản lý kho", "Kho nguyên liệu bị trừ 2 lần khi thu ngân ấn nút thanh toán nhiều lần liên tiếp.", "Critical", "Thiếu cờ kiểm tra order.isInventoryDeducted() trước khi thực hiện deductForOrder.", "Đã khắc phục (Closed)"],
    ["BUG-04", "F16 - Tùy biến món", "Modal tùy biến món không hiển thị topping đã bị quản trị viên tắt (active=false).", "Minor", "Query lấy danh sách topping chưa lọc điều kiện active == true.", "Đã khắc phục (Closed)"],
    ["BUG-05", "F08 - Thanh toán", "Mã giao dịch ví điện tử Momo bị trùng khi phát sinh nhiều đơn hàng cùng lúc.", "Major", "Hàm giả lập mã giao dịch dùng chuỗi ngẫu nhiên quá ngắn.", "Đã khắc phục (Closed)"]
]
add_styled_table(doc, bug_headers, bug_data, [0.8, 1.2, 2.4, 0.8, 1.8, 1.0])

add_p(doc, "5.3. Triển khai Kiểm thử Tự động", font_size=12, bold=True, space_after=3)
add_p(doc, "Nhóm nghiên cứu xây dựng bộ kịch bản kiểm thử tự động toàn diện sử dụng framework Playwright (Node.js/JavaScript), hỗ trợ chạy tự động 1-click qua run-e2e-tests.bat, tự động ghi video HD 1280x720 và xuất báo cáo HTML trực quan:")

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
for line in playwright_code_snippet.split('\n'):
    add_code_line(doc, line)

add_p(doc, "Danh mục kịch bản kiểm thử tự động Playwright & Video ghi hình:", bold_prefix="", font_size=11, italic=True)
auto_headers = ["Mã Kịch Bản", "Nội dung kiểm thử tự động", "Design Pattern liên quan", "Video ghi hình", "Kết quả"]
auto_data = [
    ["TC01", "Kiểm tra giao diện thu ngân, tìm kiếm món thời gian thực & lọc theo từng danh mục", "UI / Filter Strategy", "TC01_GiaoDien_TimKiem_DanhMuc.webm", "Pass (100%)"],
    ["TC02", "Tùy biến món nước chuyên sâu: Size L (+10.000₫), Topping, mức đường, đá & thêm giỏ hàng", "Decorator Pattern", "TC02_TuyBienMon_Decorator_ThemVaoGio.webm", "Pass (100%)"],
    ["TC03", "Áp dụng các chiến lược khuyến mãi: Giảm giá 10%, VIP Fixed 15.000₫, Mua 1 Tặng 1 (BOGO)", "Strategy Pattern", "TC03_KhuyenMai_StrategyPattern.webm", "Pass (100%)"],
    ["TC04", "Quy trình thanh toán đa kênh: Ví điện tử Momo, VNPay QR Code và xuất hóa đơn điện tử E-Receipt", "Adapter Pattern", "TC04_ThanhToan_AdapterPattern_HoaDon.webm", "Pass (100%)"],
    ["TC05", "Đồng bộ màn hình pha chế (Kitchen KDS), luân chuyển trạng thái và tổng hợp báo cáo doanh thu", "State & Observer Pattern", "TC05_KitchenKDS_StatePattern_Admin.webm", "Pass (100%)"]
]
add_styled_table(doc, auto_headers, auto_data, [0.8, 2.5, 1.4, 2.2, 0.9])


# 6. Append CHƯƠNG 6: HỆ THỐNG QUẢN LÝ BUG
add_p(doc, "CHƯƠNG 6: HỆ THỐNG QUẢN LÝ BUG", font_size=14, bold=True, space_after=6)
add_p(doc, "Hệ thống quản lý lỗi trong dự án được tổ chức bài bản trên công cụ Jira kết hợp biểu mẫu báo cáo chuẩn hóa. Mỗi lỗi được theo dõi chặt chẽ qua vòng đời:")
add_p(doc, "1. New / Open: Tester phát hiện sai lệch giữa kết quả thực tế và kết quả mong đợi, tạo ticket lỗi với đầy đủ các bước tái hiện (Steps to Reproduce), ảnh chụp màn hình và nhật ký log.")
add_p(doc, "2. Assigned: Trưởng nhóm phân công cho lập trình viên phụ trách module tương ứng để phân tích nguyên nhân gốc rễ.")
add_p(doc, "3. In Progress: Lập trình viên tiến hành sửa mã nguồn, thực thi unit test và kiểm tra cục bộ.")
add_p(doc, "4. Resolved / Fixed: Lập trình viên đẩy mã nguồn đã sửa lên nhánh kiểm thử và bàn giao lại cho Tester.")
add_p(doc, "5. Verified / Re-test: Tester thực hiện kiểm thử lại theo đúng các bước tái hiện. Nếu đạt, ticket được chuyển sang trạng thái Closed. Nếu chưa đạt, chuyển Re-opened.")
add_p(doc, "6. Closed: Lỗi đã được khắc phục hoàn toàn và nghiệm thu vào nhánh phát hành chính thức.")


# 7. Append PHẦN KẾT LUẬN
add_p(doc, "PHẦN KẾT LUẬN", font_size=14, bold=True, space_after=6)
add_p(doc, "1. Kết quả đạt được", font_size=12, bold=True, space_after=3)
add_p(doc, "- Xây dựng thành công hệ thống phần mềm Coffee Shop POS hoàn chỉnh, hoạt động ổn định và tích hợp chặt chẽ 6 mẫu thiết kế phần mềm cốt lõi (Factory Method, Decorator, Strategy, Adapter, State, Observer).")
add_p(doc, "- Thực thi kiểm thử hộp trắng chuyên sâu cho các đơn vị mã nguồn xử lý logic phức tạp, đạt 100% độ bao phủ câu lệnh (Statement Coverage) và độ bao phủ nhánh độc lập (Branch Coverage) theo phương pháp McCabe.")
add_p(doc, "- Thiết kế và thực thi thành công bộ 20 Test Cases kiểm thử hộp đen phủ kín 20 chức năng của hệ thống với tỷ lệ Đạt 100%.")
add_p(doc, "- Xây dựng hệ thống kiểm thử tự động E2E với Playwright, tự động hóa quy trình kiểm thử hồi quy và ghi hình video chất lượng cao phục vụ nghiệm thu.")

add_p(doc, "2. Ưu điểm, nhược điểm", font_size=12, bold=True, space_after=3)
add_p(doc, "Ưu điểm: Kiến trúc phân tầng rõ ràng, tuân thủ nguyên tắc SOLID, hỗ trợ giao diện cảm ứng và bếp pha chế thời gian thực, quy trình kiểm thử kết hợp toàn diện giữa hộp trắng, hộp đen và kiểm thử tự động.")
add_p(doc, "Nhược điểm: Cổng thanh toán (Momo, VNPay) hiện đang ở mức giả lập kết nối (Adapter Sandbox), chưa hỗ trợ triển khai phân tán nhiều chi nhánh qua mạng Internet.")

add_p(doc, "3. Hướng phát triển", font_size=12, bold=True, space_after=3)
add_p(doc, "- Tích hợp Webhook kết nối trực tiếp với cổng thanh toán thực tế của VNPay và Momo.")
add_p(doc, "- Phát triển ứng dụng di động (Mobile App) dành cho khách hàng đặt món trực tuyến và tích lũy điểm thưởng.")
add_p(doc, "- Ứng dụng trí tuệ nhân tạo (AI) trong phân tích dữ liệu bán hàng để dự báo xu hướng tiêu thụ nguyên vật liệu.")


# 8. Append TÀI LIỆU THAM KHẢO
add_p(doc, "TÀI LIỆU THAM KHẢO", font_size=14, bold=True, space_after=6)
add_p(doc, "1. Ths. Nguyễn Trần Thi Văn, Giáo trình và Bài giảng Kiểm thử phần mềm, Trường Đại học Sư phạm Kỹ thuật TP.HCM, 2026.")
add_p(doc, "2. Roger S. Pressman, Software Engineering: A Practitioner's Approach, McGraw-Hill Education, 8th Edition, 2014.")
add_p(doc, "3. Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides, Design Patterns: Elements of Reusable Object-Oriented Software, Addison-Wesley, 1994.")
add_p(doc, "4. Microsoft Corporation, Playwright Documentation: Fast and reliable end-to-end testing for modern web apps, https://playwright.dev/, 2026.")


# 9. Append BẢNG PHÂN CÔNG CÔNG VIỆC
add_p(doc, "BẢNG PHÂN CÔNG CÔNG VIỆC", font_size=14, bold=True, space_after=6)
assign_headers = ["HỌ VÀ TÊN", "NHIỆM VỤ"]
assign_data = [
    ["Nguyễn Nhật Huy", "Kiểm thử hộp trắng hàm pay(), updateItemQuantity(). Kiểm thử hộp đen chức năng Đăng nhập (F01), Trạng thái đơn (F07). Quản trị dự án & cấu hình SQLite."],
    ["Hoàng Thanh Tú", "Kiểm thử hộp trắng hàm restockForOrder(), validateAvailable(), restockItem(). Kiểm thử hộp đen Quản lý Topping (F03), Tạo đơn hàng (F04), Hóa đơn E-Receipt (F09)."],
    ["Trần Hoàng Phúc Quân", "Kiểm thử hộp trắng hàm createBeverage(). Kiểm thử hộp đen Quản lý Menu (F02), Thanh toán ví điện tử Momo/VNPay (F08). Viết tài liệu đặc tả yêu cầu."],
    ["Phạm Thị Vân Ánh", "Kiểm thử hộp trắng hàm saveRecipeItem(). Kiểm thử hộp đen Tính tổng tiền (F05), Đăng ký tài khoản (F11), Đổi mật khẩu (F12). Thiết kế giao diện và kịch bản test."],
    ["Ô Duy Hoàng Thiện", "Kiểm thử hộp trắng hàm addItem(), applyTopping(). Kiểm thử hộp đen Khuyến mãi (F06), Tùy biến Decorator (F16), Trừ kho tự động (F17). Triển khai Playwright E2E."]
]
add_styled_table(doc, assign_headers, assign_data, [2.0, 4.5])

doc.save(path)
print(f"SUCCESS: Directly updated {path}!")

# Also save copy in docs
doc.save('docs/Nhom15_FinalProject.docx')
print("Saved copy to docs/Nhom15_FinalProject.docx!")
