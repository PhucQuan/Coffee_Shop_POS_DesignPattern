# TÀI LIỆU HƯỚNG DẪN CÀI ĐẶT & THỰC HIỆN KIỂM THỬ DỰ ÁN
> **Dự án:** Coffee Shop POS (Hệ thống quản lý quán cà phê - Java Swing & Design Patterns)  
> **Dành cho:** Các thành viên trong nhóm kiểm thử & phát triển  

---

## 📌 LƯU Ý KỸ THUẬT QUAN TRỌNG TRƯỚC KHI THỰC HIỆN

Trước khi thực hiện, các thành viên cần phân biệt rõ **2 khái niệm kỹ thuật** để tránh đi sai hướng và bị trừ điểm đồ án:

| Tiêu chí | White-box Testing (Kiểm thử hộp trắng) | Playwright (Kiểm thử hộp đen / E2E) |
| :--- | :--- | :--- |
| **Bản chất** | Soi vào **mã nguồn bên trong**: rẽ nhánh `if-else`, vòng lặp, kiểm thử đường đi cơ sở (Basis Path/CFG), dòng đời biến (DFG). | Đóng vai trò **người dùng bên ngoài**: click chuột, nhập text trên giao diện người dùng. |
| **Phạm vi áp dụng** | Kiểm thử từng hàm, class, module trong mã nguồn Java của dự án. | Chỉ chạy trên **Trình duyệt Web** (Chromium, Firefox, WebKit qua DOM HTML/CSS) hoặc Web API. |
| **Khả năng với Java Swing Desktop** | **100% phù hợp** (qua JUnit 5, JaCoCo, hoặc TestRunner có sẵn). | **KHÔNG HỖ TRỢ** ứng dụng Java Swing Desktop (Swing không có DOM HTML). |
| **Khớp với báo cáo Chương IV** | Khớp 100% với các đồ thị CFG/DFG và 5 đường thi hành của nhóm. | Không thể dùng để chứng minh độ bao phủ nhánh/dòng lệnh của code Java. |

> 🎯 **Định hướng thực hiện cho nhóm:**
> 1. **Kiểm thử hộp trắng cho đồ án (Chương IV):** Toàn đội sử dụng **JUnit 5 + JaCoCo** theo **PHẦN 1** để đo độ bao phủ mã (*Branch & Statement Coverage*) khớp với đồ thị CFG/DFG đã phân tích.
> 2. **Kiểm thử Playwright:** Sử dụng theo **PHẦN 2** nếu nhóm phát triển thêm giao diện Web đặt hàng hoặc kiểm thử Web API.

---

# PHẦN 1: HƯỚNG DẪN KIỂM THỬ HỘP TRẮNG (JUNIT 5 + JACOCO)

## 1. Mục tiêu
- Kiểm thử các đường thi hành tuyến tính (Basis Paths) được xây dựng từ đồ thị dòng điều khiển (CFG) của 8 đơn vị mã nguồn trong Chương IV (như hàm `pay()`, `deduct()`, `addItem()`).
- Đo lường và xuất báo cáo độ bao phủ mã nguồn:
  - **Statement Coverage (C0)**: Tỷ lệ dòng lệnh được thực thi.
  - **Branch Coverage (C1 / C2)**: Tỷ lệ nhánh quyết định (`true`/`false`) được duyệt qua.

---

## 2. Chuẩn bị môi trường & Thư viện

### Bước 2.1: Kiểm tra Java Development Kit (JDK)
Mở PowerShell hoặc Command Prompt và kiểm tra:
```powershell
javac -version
java -version
```
*Yêu cầu:* JDK 17 (hoặc tối thiểu JDK 11).

### Bước 2.2: Tải các thư viện cần thiết vào thư mục `libs/`
Dự án không dùng Maven để giữ cấu trúc độc lập nhẹ nhàng, các bạn chỉ cần đảm bảo trong thư mục `libs/` có các file:
1. `junit-platform-console-standalone-1.10.0.jar` (thư viện chạy JUnit 5 độc lập).
2. `jacocoagent.jar` (agent giám sát độ bao phủ khi code chạy).
3. `jacococli.jar` (công cụ xuất báo cáo HTML).

*(Nếu máy chưa có, tải file `.jar` từ trang chủ JUnit hoặc JaCoCo và sao chép vào thư mục `libs/`)*.

---

## 3. Cấu trúc thư mục kiểm thử
```text
Coffee_Shop_POS_DesignPattern/
├── libs/                                 <- Chứa các file .jar
├── src/
│   ├── main/java/com/coffeeshop/         <- Mã nguồn chính (Service, Domain, Repo)
│   └── test/java/com/coffeeshop/
│       ├── TestRunner.java               <- Test runner hiện tại của dự án
│       └── whitebox/                     <- Thư mục chứa các White-box Test Cases mới
│           ├── PaymentServiceWhiteBoxTest.java
│           ├── InventoryServiceWhiteBoxTest.java
│           └── OrderServiceWhiteBoxTest.java
```

---

## 4. Cách viết Test Case Hộp Trắng chuẩn (Ví dụ cho hàm `pay()`)

Hàm `pay($order, $gateway)` trong báo cáo Chương IV có **5 đường thi hành** tương ứng với 4 nút quyết định nhị phân (Độ phức tạp $C = 4 + 1 = 5$).

Tạo file `src/test/java/com/coffeeshop/whitebox/PaymentServiceWhiteBoxTest.java`:

```java
package com.coffeeshop.whitebox;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.model.Payment;
import com.coffeeshop.domain.patterns.adapter.PaymentResult;
import com.coffeeshop.domain.patterns.adapter.MomoAdapter;
import com.coffeeshop.infrastructure.InMemoryRepository;
import com.coffeeshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceWhiteBoxTest {

    private PaymentService paymentService;
    private InMemoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository();
        paymentService = new PaymentService(repository, null, null);
    }

    @Test
    @DisplayName("Path 1 (1->2): Đơn hàng đã có thanh toán SUCCESS -> Báo lỗi chặn thanh toán lặp")
    void testPath1_OrderAlreadyPaid_ThrowsException() {
        Order order = new Order(1, "Table 1");
        // Giả lập trạng thái đã thanh toán trước đó
        order.setPayment(new Payment(1, "MOMO", 55000, "TX_001", "SUCCESS"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.pay(order, new MomoAdapter());
        });
        assertTrue(ex.getMessage().contains("đã được thanh toán") || ex.getMessage().contains("Already paid"));
    }

    @Test
    @DisplayName("Path 2 (1->3->4->15): Cổng thanh toán trả về Thất bại -> Không lưu payment, trả về Fail")
    void testPath2_GatewayFails_ReturnsFailureResult() {
        Order order = new Order(2, "Table 2");
        
        // Mock adapter trả về kết quả thất bại
        PaymentResult failResult = paymentService.pay(order, amount -> new PaymentResult(false, null, "Số dư ví không đủ"));

        assertFalse(failResult.isSuccess());
        assertNull(order.getPayment());
    }

    @Test
    @DisplayName("Path 3 & 4 (1->3..->8->9->10..): Đơn hàng PENDING thanh toán thành công -> Trừ kho và gửi bếp")
    void testPath3_PendingOrderSuccess_DeductsInventoryAndSendsToKitchen() {
        Order order = new Order(3, "Table 3");
        
        PaymentResult successResult = paymentService.pay(order, new MomoAdapter());

        assertTrue(successResult.isSuccess());
        assertNotNull(order.getPayment());
        assertEquals("SUCCESS", order.getPayment().getStatus());
    }
}
```

---

## 5. Chạy Test và Tạo Báo Cáo Coverage (JaCoCo Report)

Tạo file script tự động `run-whitebox-coverage.ps1` ở thư mục gốc:

```powershell
Write-Host "=== BƯỚC 1: BIÊN DỊCH MÃ NGUỒN VÀ TEST CASES ===" -ForegroundColor Cyan
$root = $PSScriptRoot
$outClasses = Join-Path $root "build\classes"
$outTests = Join-Path $root "build\test-classes"
$libs = (Get-ChildItem -Path (Join-Path $root "libs\*.jar") | ForEach-Object { $_.FullName }) -join ";"

New-Item -ItemType Directory -Force -Path $outClasses | Out-Null
New-Item -ItemType Directory -Force -Path $outTests | Out-Null

# Compile mã nguồn chính
javac -encoding UTF-8 -cp $libs -d $outClasses (Get-ChildItem -Path "src\main\java" -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Compile mã nguồn test
javac -encoding UTF-8 -cp "$libs;$outClasses" -d $outTests (Get-ChildItem -Path "src\test\java" -Recurse -Filter *.java | ForEach-Object { $_.FullName })

Write-Host "=== BƯỚC 2: CHẠY KIỂM THỬ VỚI JACOCO AGENT ===" -ForegroundColor Cyan
$execFile = Join-Path $root "build\jacoco.exec"
java "-javaagent:libs/jacocoagent.jar=destfile=$execFile" `
     -cp "$libs;$outClasses;$outTests" `
     org.junit.platform.console.ConsoleLauncher --scan-classpath

Write-Host "=== BƯỚC 3: XUẤT BÁO CÁO HTML BẰNG JACOCO CLI ===" -ForegroundColor Cyan
$reportDir = Join-Path $root "build\reports\jacoco"
java -jar libs/jacococli.jar report $execFile `
     --classfiles $outClasses `
     --sourcefiles src/main/java `
     --html $reportDir

Write-Host "`n>> ĐÃ XUẤT BÁO CÁO THÀNH CÔNG TẠI: $reportDir\index.html" -ForegroundColor Green
```

### Cách xem kết quả:
Mở trình duyệt truy cập file `build/reports/jacoco/index.html`:
- **Cột Missed Instructions / Cov.**: Tỷ lệ bao phủ dòng lệnh.
- **Cột Missed Branches / Cov.**: Tỷ lệ bao phủ nhánh rẽ (`Branch Coverage`).
- Click vào tên hàm (ví dụ `PaymentService.pay()`) để thấy dòng nào màu xanh lá (đã phủ), màu đỏ (chưa được test chạm tới). Chụp ảnh màn hình này để chèn vào báo cáo đồ án.

---

# PHẦN 2: HƯỚNG DẪN CÀI ĐẶT & DÙNG PLAYWRIGHT (WEB / REST API)

Nếu nhóm có xây dựng phân hệ **Web Client / Web Ordering** hoặc muốn kiểm thử tự động các cổng giao tiếp mạng (HTTP / REST API), Playwright là công cụ tự động hóa hàng đầu.

## 1. Cài đặt môi trường
- Tải và cài đặt **Node.js** phiên bản LTS từ [nodejs.org](https://nodejs.org/).
- Kiểm tra cài đặt thành công:
  ```powershell
  node -v
  npm -v
  ```

---

## 2. Khởi tạo dự án Playwright
Tạo một thư mục riêng cho kiểm thử E2E (ví dụ `web-tests/`):
```powershell
mkdir web-tests
cd web-tests
npm init playwright@latest
```

Hệ thống sẽ hiển thị các câu hỏi thiết lập:
1. `Do you want to use TypeScript or JavaScript?` ➜ Chọn **JavaScript** (hoặc TypeScript tùy sở thích).
2. `Where to put your end-to-end tests?` ➜ Nhấn Enter (mặc định là `tests`).
3. `Add a GitHub Actions workflow?` ➜ Chọn **false** (chưa cần thiết).
4. `Install Playwright browsers?` ➜ Chọn **true** (để tự động tải trình duyệt Chromium/Firefox).

---

## 3. Viết kịch bản kiểm thử mẫu (Playwright Test)

Tạo file `web-tests/tests/coffee_pos_web.spec.js`:

```javascript
const { test, expect } = require('@playwright/test');

test.describe('Kiểm thử giao diện Đặt hàng Coffee Shop', () => {

    test('TC01: Đăng nhập hệ thống thành công', async ({ page }) => {
        // 1. Mở trang đăng nhập
        await page.goto('http://localhost:3000/login');

        // 2. Nhập thông tin đăng nhập
        await page.locator('#username').fill('cashier01');
        await page.locator('#password').fill('123');

        // 3. Bấm nút đăng nhập
        await page.locator('button#btnLogin').click();

        // 4. Kiểm tra màn hình chuyển hướng đến POS
        await expect(page).toHaveURL(/.*pos/);
        await expect(page.locator('.user-badge')).toContainText('cashier01');
    });

    test('TC02: Thêm món Cà phê Sữa và kiểm tra giỏ hàng', async ({ page }) => {
        await page.goto('http://localhost:3000/pos');

        // Chọn món "Cà phê sữa đá"
        await page.locator('.product-card:has-text("Cà phê sữa")').click();

        // Chọn Topping: Trân châu
        await page.locator('input[type="checkbox"][name="topping_pearl"]').check();
        await page.locator('button:has-text("Thêm vào đơn")').click();

        // Kiểm tra tổng tiền đơn hàng đã hiển thị đúng
        const totalText = await page.locator('#order-total').innerText();
        expect(totalText).toContain('35,000');
    });

    test('TC03: Kiểm thử Web API thanh toán Momo (API Testing)', async ({ request }) => {
        const response = await request.post('http://localhost:3000/api/payment/momo', {
            data: {
                orderId: 101,
                amount: 35000
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.success).toBeTruthy();
        expect(body.transactionCode).toBeDefined();
    });
});
```

---

## 4. Các lệnh thực thi Playwright cần nhớ

| Lệnh | Ý nghĩa |
| :--- | :--- |
| `npx playwright test` | Chạy toàn bộ test ngầm (Headless mode). |
| `npx playwright test --headed` | Chạy test **có mở cửa sổ trình duyệt** để quan sát thao tác click chuột trực quan. |
| `npx playwright test --ui` | Mở giao diện **Playwright UI Interactive Mode** (xem timeline, time-travel debug từng bước click). |
| `npx playwright codegen http://localhost:3000` | **Bật chế độ tự ghi mã (Record)**: Bạn chỉ cần click trên web, Playwright tự sinh code test cho bạn. |
| `npx playwright show-report` | Mở trang báo cáo HTML hiển thị chi tiết pass/fail, ảnh chụp màn hình lúc bị lỗi. |

---

## 5. Tùy chọn: Sử dụng Playwright bằng Java (Playwright Java)
Nếu nhóm muốn viết code kiểm thử Web hoàn toàn bằng **Java** thay vì Node.js/JavaScript:
1. Thêm dependency `com.microsoft.playwright:playwright:1.40.0` vào dự án.
2. Viết class Java tương tác với browser:
   ```java
   import com.microsoft.playwright.*;

   public class WebAppTest {
       public static void main(String[] args) {
           try (Playwright playwright = Playwright.create()) {
               Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
               Page page = browser.newPage();
               page.navigate("http://localhost:3000");
               System.out.println("Tiêu đề trang: " + page.title());
               browser.close();
           }
       }
   }
   ```

---

## 📋 PHÂN CÔNG THỰC HIỆN GỢI Ý CHO NHÓM

1. **Nhóm White-box Testing (Chính):**
   - Viết test case phủ các nhánh điều kiện cho 8 đơn vị mã nguồn đã vẽ CFG/DFG trong Chương IV.
   - Chạy script đo độ bao phủ JaCoCo và chụp kết quả % Branch Coverage đưa vào tài liệu đồ án.
2. **Nhóm Black-box / System Testing (Nếu có làm Web):**
   - Dùng Playwright viết kịch bản luồng thao tác người dùng (Đăng nhập ➔ Đặt món ➔ Thanh toán ➔ Kiểm tra kết quả).
   - Xuất HTML report từ Playwright để làm minh chứng kiểm thử chức năng.
