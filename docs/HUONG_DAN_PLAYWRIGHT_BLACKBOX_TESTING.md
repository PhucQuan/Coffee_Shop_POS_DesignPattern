# HƯỚNG DẪN CÀI ĐẶT & SỬ DỤNG PLAYWRIGHT (BLACK-BOX TESTING)
> **Dự án:** Coffee Shop POS (Hệ thống Bán Hàng & Quản Lý Quán Cà Phê)  
> **Dành cho:** Tất cả thành viên trong nhóm (Dễ hiểu cho người mới bắt đầu từ con số 0)  

---

## ☕ 1. TỔNG QUAN: PLAYWRIGHT & KIỂM THỬ HỘP ĐEN (BLACK-BOX)

### 1.1. Kiểm thử hộp đen (Black-box Testing) là gì?
- Kiểm thử hộp đen là phương pháp kiểm thử **từ góc nhìn của người dùng thực tế** (Khách hàng, Thu ngân, Pha chế, Quản lý).
- Bạn **không cần quan tâm code bên trong viết gì**, mà chỉ quan tâm:
  - Nhập dữ liệu đầu vào (Input): Click chọn món, gõ tên tìm kiếm, chọn mã giảm giá, ấn thanh toán.
  - Kết quả đầu ra (Output): Giá tiền có tính đúng không? Món có vào giỏ không? Đơn có nhảy sang màn hình Bếp không? Có thông báo lỗi khi thao tác sai không?

### 1.2. Playwright là gì?
- **Playwright** là công cụ tự động hóa trình duyệt web hàng đầu hiện nay của Microsoft.
- Playwright sẽ tự động bật trình duyệt (Chrome/Edge/Firefox), tự click chuột, tự gõ phím, tự kiểm tra giao diện đúng y như người thật đang thao tác với tốc độ cực nhanh và chính xác.

---

## 🛠️ 2. HƯỚNG DẪN CÀI ĐẶT TỪ A - Z (DÀNH CHO NGƯỜI MỚI)

Mọi thành viên trong nhóm chỉ cần làm tuần tự theo 3 bước sau:

### Bước 2.1: Cài đặt Node.js (Môi trường chạy Playwright)
1. Tải bản **Node.js LTS (bản ổn định khuyến nghị)** từ trang chủ: [https://nodejs.org](https://nodejs.org/)
2. Chạy file cài đặt, bấm **Next -> Next -> Finish** (giữ nguyên mặc định).
3. Mở **PowerShell** hoặc **Terminal** kiểm tra xem đã cài thành công chưa:
   ```powershell
   node -v
   npm -v
   ```
   *(Nếu hiện ra phiên bản dạng `v20.x.x` hoặc `v18.x.x` là thành công!)*

---

### Bước 2.2: Khởi tạo thư mục kiểm thử Playwright trong dự án
Mở cửa sổ dòng lệnh tại thư mục gốc của dự án (`Coffee_Shop_POS_DesignPattern`), gõ các lệnh sau:

```powershell
# 1. Tạo thư mục chứa các bài test
mkdir e2e-tests
cd e2e-tests

# 2. Khởi tạo Playwright
npm init playwright@latest
```

Khi chạy lệnh `npm init playwright@latest`, màn hình sẽ hỏi 4 câu hỏi:
- *Do you want to use TypeScript or JavaScript?* ➜ Dùng phím mũi tên chọn **JavaScript** (cho dễ viết và dễ đọc).
- *Where to put your end-to-end tests?* ➜ Nhấn **Enter** (mặc định là thư mục `tests`).
- *Add a GitHub Actions workflow?* ➜ Chọn **false** (nhập `n` rồi Enter).
- *Install Playwright browsers?* ➜ Chọn **true** (nhập `y` rồi Enter, Playwright sẽ tự tải Chrome/Firefox về máy).

Sau khi chạy xong, thư mục `e2e-tests` của bạn sẽ có dạng:
```text
e2e-tests/
├── tests/              <- Nơi chứa các file kịch bản test
│   └── example.spec.js
├── node_modules/       <- Thư viện tự động tải về
├── playwright.config.js<- File cấu hình Playwright
└── package.json
```

---

### Bước 2.3: Cài tiện ích mở rộng trên VS Code (Khuyên dùng)
Nếu các thành viên dùng Visual Studio Code:
1. Mở mục **Extensions** (phím tắt `Ctrl + Shift + X`).
2. Tìm kiếm **Playwright Test for VSCode** (do Microsoft phát triển).
3. Bấm **Install**.  
*(Tiện ích này có nút bấm Run hình tam giác xanh ngay cạnh từng dòng test để chạy cực kỳ tiện lợi).*

---

## 🚀 3. KHỞI ĐỘNG HỆ THỐNG TRƯỚC KHI TEST

Để Playwright có thể tương tác với Web Coffee Shop POS, bạn cần chạy giao diện Web lên trước:

### Cách 1: Chạy WebServer có sẵn của dự án
Mở một cửa sổ PowerShell tại thư mục gốc và chạy:
```powershell
# Chạy Web Server của ứng dụng (cổng 8088)
javac -encoding UTF-8 -d build/classes (Get-ChildItem -Path src/main/java -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp "build/classes;libs/*" com.coffeeshop.api.WebServer
```
Trình duyệt sẽ mở địa chỉ: `http://localhost:8088`

### Cách 2: Chạy trực tiếp bằng VS Code Live Server
Nếu bạn có cài extension **Live Server** trong VS Code:
- Click chuột phải vào file `frontend/index.html` ➜ chọn **Open with Live Server** (mặc định mở tại `http://127.0.0.1:5500/frontend/index.html`).

---

## 🎯 4. VŨ KHÍ TỐI THƯỢNG CHO NGƯỜI MỚI: TỰ GHI CODE (CODEGEN)

> 💡 **Mẹo cực hay:** Nếu bạn chưa biết viết code Playwright như thế nào, Playwright có tính năng **Record (Tự ghi hình thao tác thành mã nguồn)**!

Các bạn mở terminal trong thư mục `e2e-tests` và gõ:
```powershell
npx playwright codegen http://localhost:8088
```
- Một cửa sổ trình duyệt và một cửa sổ code sẽ hiện lên song song.
- Bạn dùng chuột bấm chọn món, bấm nút thanh toán, gõ chữ tìm kiếm...
- **Playwright sẽ tự động sinh code JavaScript tương ứng theo từng cú click chuột của bạn!**
- Bạn chỉ việc copy đoạn code đó dán vào file test là xong!

---

## 📝 5. CÁC KỊCH BẢN TEST HỘP ĐEN MẪU CHO DỰ ÁN

Tạo một file mới tại: `e2e-tests/tests/coffee_pos_blackbox.spec.js` và dán toàn bộ nội dung dưới đây vào:

```javascript
// @ts-check
const { test, expect } = require('@playwright/test');

// Địa chỉ chạy web app
const BASE_URL = 'http://localhost:8088';

test.describe('BỘ KIỂM THỬ HỘP ĐEN - HỆ THỐNG COFFEE SHOP POS', () => {

    test.beforeEach(async ({ page }) => {
        // Trước mỗi bài test, tự động mở trang chủ POS
        await page.goto(BASE_URL);
        await page.waitForLoadState('networkidle');
    });

    // =========================================================================
    // TEST CASE 01: KIỂM THỬ TÌM KIẾM MÓN & LỌC DANH MỤC (MENU & SEARCH)
    // =========================================================================
    test('TC01 - Blackbox: Tìm kiếm đồ uống theo từ khóa và lọc danh mục', async ({ page }) => {
        // 1. Nhập từ khóa tìm kiếm "bạc xỉu" vào ô search
        const searchInput = page.locator('#menuSearch');
        await searchInput.fill('bạc xỉu');

        // 2. Kiểm tra xem trên màn hình có hiển thị món "Bạc xỉu" không
        const productTitle = page.locator('h3:has-text("Bạc xỉu")');
        await expect(productTitle).toBeVisible();

        // 3. Xóa tìm kiếm và thử bấm chuyển sang tab danh mục "Trà trái cây & Trà sữa"
        await searchInput.fill('');
        const teaCategoryTab = page.locator('button[data-category="TEA"]');
        await teaCategoryTab.click();

        // 4. Kiểm tra danh mục trà được lọc (ví dụ có Trà đào)
        await expect(page.locator('text=Trà đào')).toBeVisible();
    });

    // =========================================================================
    // TEST CASE 02: KIỂM THỬ CHỌN MÓN VÀO GIỎ HÀNG (CART MANAGEMENT)
    // =========================================================================
    test('TC02 - Blackbox: Chọn món đồ uống và kiểm tra giỏ hàng cập nhật tiền', async ({ page }) => {
        // 1. Chọn món đầu tiên có trên màn hình (nhấn vào card đồ uống)
        const firstDrink = page.locator('.drink-card').first();
        await firstDrink.click();

        // 2. Nếu có modal tùy chọn (Topping/Size), bấm nút "Thêm vào giỏ"
        const btnAddToCart = page.locator('#btnAddModalToCart, button:has-text("Thêm vào đơn")');
        if (await btnAddToCart.isVisible()) {
            await btnAddToCart.click();
        }

        // 3. Kiểm tra số lượng món trong giỏ hàng tăng lên >= 1
        const cartBadge = page.locator('#cartCountBadge');
        await expect(cartBadge).not.toHaveText('0');

        // 4. Kiểm tra nút thanh toán được kích hoạt (không còn bị disabled)
        const btnCheckout = page.locator('#btnOpenCheckout');
        await expect(btnCheckout).toBeEnabled();
    });

    // =========================================================================
    // TEST CASE 03: KIỂM THỬ CHIẾT KHẤU GIẢM GIÁ (STRATEGY DISCOUNT)
    // =========================================================================
    test('TC03 - Blackbox: Áp dụng mã giảm giá VIP/Thành viên và tính lại tiền', async ({ page }) => {
        // 1. Thêm một món vào giỏ
        await page.locator('.drink-card').first().click();
        const btnAdd = page.locator('#btnAddModalToCart, button:has-text("Thêm vào đơn")');
        if (await btnAdd.isVisible()) await btnAdd.click();

        // 2. Chọn mã giảm giá "Giảm giá 10% (Thành viên)"
        const discountSelect = page.locator('#discountSelect');
        await discountSelect.selectOption('PERCENT_10');

        // 3. Kiểm tra mục Khuyến mãi (Discount) không còn là 0 đ
        const discountText = page.locator('#cartDiscount');
        await expect(discountText).not.toHaveText('0 ₫');
    });

    // =========================================================================
    // TEST CASE 04: KIỂM THỬ QUY TRÌNH THANH TOÁN (CHECKOUT MODAL)
    // =========================================================================
    test('TC04 - Blackbox: Mở Modal Thanh Toán và thực hiện thanh toán tiền mặt', async ({ page }) => {
        // 1. Chọn món vào giỏ
        await page.locator('.drink-card').first().click();
        const btnAdd = page.locator('#btnAddModalToCart, button:has-text("Thêm vào đơn")');
        if (await btnAdd.isVisible()) await btnAdd.click();

        // 2. Nhấn nút "Thanh Toán Đơn Hàng"
        await page.locator('#btnOpenCheckout').click();

        // 3. Kiểm tra modal thanh toán hiển thị
        const checkoutModal = page.locator('#checkoutModal');
        await expect(checkoutModal).toBeVisible();

        // 4. Chọn hình thức thanh toán Tiền mặt (Cash) hoặc Momo
        const cashMethod = page.locator('input[value="CASH"], button:has-text("Tiền mặt")').first();
        if (await cashMethod.isVisible()) {
            await cashMethod.click();
        }

        // 5. Bấm nút hoàn tất thanh toán
        const btnConfirm = page.locator('#btnConfirmPayment, button:has-text("Xác nhận thanh toán")').first();
        if (await btnConfirm.isVisible()) {
            await btnConfirm.click();
        }
    });

    // =========================================================================
    // TEST CASE 05: KIỂM THỬ ĐIỀU HƯỚNG MÀN HÌNH BẾP (KITCHEN KDS)
    // =========================================================================
    test('TC05 - Blackbox: Chuyển sang Màn hình Pha Chế (Kitchen KDS)', async ({ page }) => {
        // 1. Nhấp tab "Màn Hình Bếp (KDS)" trên thanh Menu điều hướng trên cùng
        const kitchenTab = page.locator('button[data-tab-target="kitchen"]');
        await kitchenTab.click();

        // 2. Kiểm tra màn hình bếp hiển thị tiêu đề
        const kdsTitle = page.locator('h2:has-text("Màn Hình Pha Chế")');
        await expect(kdsTitle).toBeVisible();

        // 3. Kiểm tra có đủ 3 cột trạng thái Kanban: "Chờ Pha Chế", "Đang Pha Chế", "Sẵn Sàng"
        await expect(page.locator('text=Chờ Pha Chế')).toBeVisible();
        await expect(page.locator('text=Đang Pha Chế')).toBeVisible();
        await expect(page.locator('text=Sẵn Sàng Giao Khách')).toBeVisible();
    });
});
```

---

## ⚡ 6. CÁC CÂU LỆNH CHẠY TEST DÀNH CHO NHÓM

Di chuyển vào thư mục `e2e-tests` và chạy các lệnh tương ứng:

### 1. Chạy có mở giao diện trình duyệt để xem tận mắt (Khuyên dùng khi demo)
```powershell
npx playwright test --headed
```
*Trình duyệt Chrome sẽ tự động bật lên, tự click và chạy qua từng bước cho bạn xem!*

### 2. Chạy giao diện tương tác UI Mode (Cực đẹp & Dễ debug lỗi)
```powershell
npx playwright test --ui
```
*Lệnh này mở giao diện quản lý trực quan của Playwright, có nút Play từng test, xem lại lịch sử từng giây (Timeline) xem chuột đã click vào đâu.*

### 3. Chạy kiểm thử ngầm tốc độ cao (Headless Mode)
```powershell
npx playwright test
```

### 4. Xem báo cáo kết quả chi tiết (HTML Report)
Sau khi chạy xong, gõ lệnh:
```powershell
npx playwright show-report
```
*Trình duyệt sẽ hiển thị bảng báo cáo: Bao nhiêu test case PASS (Xanh), bao nhiêu test case FAILED (Đỏ), thời gian chạy, kèm video và ảnh chụp lúc gặp lỗi (nếu có).*

---

## ❓ 7. CÂU HỎI THƯỜNG GẶP (TROUBLESHOOTING)

### Q1: Bị lỗi `page.goto: net::ERR_CONNECTION_REFUSED at http://localhost:8088`?
👉 **Nguyên nhân:** Bạn chưa bật WebServer của dự án trước khi chạy test.  
👉 **Cách khắc phục:** Mở 1 cửa sổ terminal riêng, chạy `java -cp "build/classes;libs/*" com.coffeeshop.api.WebServer` (hoặc bật Live Server) rồi mới chạy `npx playwright test`.

### Q2: Muốn chụp ảnh màn hình (Screenshot) tự động khi test xong?
Thêm dòng này vào cuối test case:
```javascript
await page.screenshot({ path: 'screenshot-ket-qua.png', fullPage: true });
```
Ảnh chụp giao diện sẽ tự động lưu lại để bạn dán vào Word báo cáo!

### Q3: Làm sao để kiểm tra một nút hoặc chữ có hiển thị trên màn hình không?
Sử dụng hàm `expect`:
```javascript
await expect(page.locator('#ten-id')).toBeVisible(); // Kiểm tra hiển thị
await expect(page.locator('#tong-tien')).toHaveText('50,000 ₫'); // Kiểm tra đúng nội dung chữ
```
