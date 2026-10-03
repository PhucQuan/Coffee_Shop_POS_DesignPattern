# HƯỚNG DẪN KIỂM THỬ HỘP ĐEN & QUAY VIDEO VỚI PLAYWRIGHT
> **Dự án:** Coffee Shop POS (Hệ thống Bán Hàng & Quản Lý Quán Cà Phê)  
> **Áp dụng Design Patterns:** Factory Method, Decorator, Strategy, Adapter, State, Observer  
> **Dành cho:** Tất cả thành viên trong nhóm & Báo cáo đồ án môn học  

---

## 📋 THỨ TỰ LÀM & CÁCH SỬ DỤNG NHANH NHẤT (1-CLICK)

Nếu bạn muốn chạy kiểm thử ngay lập tức và tự động xuất trọn bộ video HD:

### Cách 1: Chạy 1-Click tự động (Khuyên dùng nhất)
1. Vào thư mục gốc của dự án `Coffee_Shop_POS_DesignPattern`.
2. **Nhấp đúp chuột (Double-click)** vào file:
   👉 **`run-e2e-tests.bat`** *(hoặc chuột phải chọn Run with PowerShell đối với `run-e2e-tests.ps1`)*.
3. Hệ thống sẽ tự động:
   - Cài đặt thư viện Playwright & Chromium (nếu chạy lần đầu).
   - Tự động mở trình duyệt và thực thi tuần tự **5 kịch bản kiểm thử**.
   - Tự động ghi hình toàn bộ thao tác màn hình với chất lượng HD (1280x720).
   - Tự động gom và đổi tên 5 file video vào thư mục **`e2e-tests/videos/`**.
   - Tự động bật thư mục chứa Video và mở trang **Báo cáo HTML trực quan** trên trình duyệt.

---

### Cách 2: Chạy bằng dòng lệnh (Dành cho thành viên muốn kiểm soát chi tiết)
Mở PowerShell tại thư mục `e2e-tests` và thực hiện theo thứ tự:

```powershell
# Bước 1: Di chuyển vào thư mục test
cd e2e-tests

# Bước 2: Cài đặt dependencies (chỉ cần làm lần đầu)
npm install
npx playwright install chromium

# Bước 3: Chạy test và tự động quay video (chế độ mở giao diện để quan sát)
npx playwright test --headed

# Bước 4: Trích xuất và đặt tên chuẩn cho toàn bộ video kiểm thử
node organize-videos.js

# Bước 5: Mở báo cáo kiểm thử HTML (có tích hợp video xem trực tiếp)
npx playwright show-report
```

---

## 🎥 NƠI LƯU TRỮ VÀ XEM VIDEO KIỂM THỬ

Sau khi chạy xong, tất cả video demo chất lượng cao đã sẵn sàng tại:
📂 **`e2e-tests/videos/`**

| Tên File Video | Kịch Bản Kiểm Thử | Design Pattern Liên Quan | Định Dạng |
| :--- | :--- | :--- | :---: |
| **`TC01_GiaoDien_TimKiem_DanhMuc.webm`** | Tìm kiếm món & Lọc danh mục | UI / Filter Strategy | 1280x720 HD |
| **`TC02_TuyBienMon_Decorator_ThemVaoGio.webm`** | Tùy biến Size L, Topping, Ghi chú | **Decorator Pattern** | 1280x720 HD |
| **`TC03_KhuyenMai_StrategyPattern.webm`** | Áp dụng mã giảm giá 10%, VIP 15%, BOGO | **Strategy Pattern** | 1280x720 HD |
| **`TC04_ThanhToan_AdapterPattern_HoaDon.webm`** | Thanh toán Tiền mặt, VNPay QR & In hóa đơn | **Adapter Pattern** | 1280x720 HD |
| **`TC05_KitchenKDS_StatePattern_Admin.webm`** | Luân chuyển đơn pha chế & Báo cáo doanh thu | **State & Observer Pattern** | 1280x720 HD |

> 💡 **Mẹo xem video:**  
> - Định dạng `.webm` chuẩn HTML5 có thể mở xem trực tiếp bằng bất kỳ trình duyệt nào (Chrome, Edge, Cốc Cốc) hoặc phần mềm VLC, Windows Media Player.  
> - Bạn có thể xem ngay trên trang Báo cáo Playwright (`npx playwright show-report`) bằng cách click vào từng bài test!

---

## 📊 BẢNG ĐẶC TẢ CHI TIẾT 5 KỊCH BẢN KIỂM THỬ HỘP ĐEN

Dưới đây là nội dung chuẩn dùng để đưa vào **Chương Kiểm Thử (Black-box Testing)** trong báo cáo đồ án Word / PDF:

### 1. Kịch bản TC01: Kiểm tra Giao diện Thu Ngân, Tìm kiếm món & Lọc Danh mục
- **Mã kịch bản:** TC_BB_01
- **Mục tiêu:** Kiểm tra khả năng tải giao diện POS, phản hồi tìm kiếm từ khóa thời gian thực và lọc theo từng tab nhóm đồ uống.
- **Tiền điều kiện:** Ứng dụng POS đang mở tại trang chủ.
- **Các bước thực hiện:**
  1. Truy cập vào trang chủ POS (`/`).
  2. Click vào ô tìm kiếm (`#menuSearch`), nhập từ khóa `"Bạc xỉu"`.
  3. Kiểm tra danh sách hiển thị chỉ còn món `"Bạc xỉu"`.
  4. Xóa từ khóa tìm kiếm.
  5. Click vào tab danh mục `"🍵 Trà trái cây"`.
  6. Click vào tab danh mục `"🍃 Matcha"`.
  7. Click vào tab `"Tất cả món"`.
- **Kết quả mong đợi:** Các món ăn hiển thị tức thì, đúng danh mục, không giật lag.
- **Kết quả thực tế:** **PASS (Đạt)** - Video: `TC01_GiaoDien_TimKiem_DanhMuc.webm`.

---

### 2. Kịch bản TC02: Tùy biến đồ uống (Decorator Pattern) & Thêm vào Giỏ hàng
- **Mã kịch bản:** TC_BB_02
- **Mục tiêu:** Kiểm thử tính năng bọc thêm các thành phần động cho sản phẩm đồ uống (chọn Size, Topping trân châu/thạch, ghi chú làm riêng) theo đúng nguyên lý **Decorator Pattern**.
- **Tiền điều kiện:** Menu đồ uống đang hiển thị đầy đủ.
- **Các bước thực hiện:**
  1. Click vào thẻ món `"Cà phê sữa"`.
  2. Modal *Decorator Customizer* mở ra.
  3. Chọn `"Size L (Lớn)"` (+10.000₫).
  4. Chọn Topping `"Trân châu trắng"` (+10.000₫).
  5. Nhập ghi chú: `"70% đường, nhiều đá"`.
  6. Kiểm tra tổng đơn giá món được Decorator cộng dồn chính xác.
  7. Nhấn nút `"+ Thêm Vào Đơn"`.
  8. Kiểm tra giỏ hàng: số lượng món tăng, nút `"Thanh Toán Đơn Hàng"` được kích hoạt (enabled).
- **Kết quả mong đợi:** Món được thêm chính xác vào giỏ hàng với đúng các option decorator đã chọn.
- **Kết quả thực tế:** **PASS (Đạt)** - Video: `TC02_TuyBienMon_Decorator_ThemVaoGio.webm`.

---

### 3. Kịch bản TC03: Áp dụng mã khuyến mãi & Chiết khấu (Strategy Pattern)
- **Mã kịch bản:** TC_BB_03
- **Mục tiêu:** Kiểm thử việc thay đổi thuật toán tính chiết khấu linh hoạt theo **Strategy Pattern** (Giảm 10%, VIP 15%, BOGO Mua 1 Tặng 1).
- **Tiền điều kiện:** Giỏ hàng đã có ít nhất một món đồ uống.
- **Các bước thực hiện:**
  1. Chọn dropdown mã giảm giá (`#discountSelect`).
  2. Chọn Strategy: `"Giảm giá 10% (Thành viên)"` (`PERCENT_10`).
  3. Kiểm tra dòng *Khuyến mãi* tự động trừ đúng 10% giá trị tạm tính.
  4. Chọn Strategy: `"Khách VIP (Giảm 15%)"` (`VIP`).
  5. Kiểm tra dòng *Khuyến mãi* cập nhật thành 15%.
  6. Chọn `"Không áp dụng (0%)"`.
  7. Kiểm tra dòng *Khuyến mãi* về `0 ₫`.
- **Kết quả mong đợi:** Tổng tiền thanh toán cập nhật chính xác theo từng Strategy được chọn trong thời gian thực.
- **Kết quả thực tế:** **PASS (Đạt)** - Video: `TC03_KhuyenMai_StrategyPattern.webm`.

---

### 4. Kịch bản TC04: Quy trình Thanh toán đa kênh (Adapter Pattern) & Xuất Hóa Đơn
- **Mã kịch bản:** TC_BB_04
- **Mục tiêu:** Kiểm thử việc tích hợp các cổng thanh toán khác nhau thông qua **Adapter Pattern** (Tiền mặt Cash, VNPay QR Code) và xuất hóa đơn hoàn tất giao dịch.
- **Tiền điều kiện:** Giỏ hàng có món và tổng tiền thanh toán > 0.
- **Các bước thực hiện:**
  1. Nhấn nút `"Thanh Toán Đơn Hàng"`.
  2. Modal Checkout mở ra hiển thị tổng tiền cần thanh toán.
  3. Chọn phương thức `"VNPay QR"` -> Kiểm tra hệ thống Adapter tự động sinh mã VietQR theo đúng số tiền đơn hàng.
  4. Chuyển sang phương thức `"Tiền mặt"`.
  5. Nhấn nút `"Xác Nhận Đã Thu Tiền"`.
  6. Kiểm tra Hóa đơn bán lẻ (Receipt Preview) xuất hiện với đầy đủ: Mã đơn, Thời gian, Chi tiết món, Tạm tính, Giảm giá, Tổng cộng.
  7. Nhấn `"Đóng"` hóa đơn.
- **Kết quả mong đợi:** Giao dịch hoàn tất, hóa đơn in chuẩn, giỏ hàng được làm rỗng để sẵn sàng cho đơn tiếp theo.
- **Kết quả thực tế:** **PASS (Đạt)** - Video: `TC04_ThanhToan_AdapterPattern_HoaDon.webm`.

---

### 5. Kịch bản TC05: Màn hình Bếp (Kitchen KDS - State Pattern) & Quản Trị (Admin)
- **Mã kịch bản:** TC_BB_05
- **Mục tiêu:** Kiểm thử việc đồng bộ trạng thái đơn hàng thời gian thực theo **State Pattern** (Chờ Pha Chế ➔ Đang Pha Chế ➔ Đã Xong/Giao Khách) và đồng bộ doanh thu sang Màn hình Quản trị (**Observer Pattern**).
- **Tiền điều kiện:** Đơn hàng đã được thanh toán từ quầy thu ngân.
- **Các bước thực hiện:**
  1. Bấm tab `"Màn Hình Bếp (KDS)"` trên thanh điều hướng.
  2. Kiểm tra giao diện Kanban gồm 3 cột trạng thái: *Chờ Pha Chế*, *Đang Pha Chế*, *Đã Xong / Giao Khách*.
  3. Thao tác nút chuyển trạng thái đơn hàng (ví dụ bấm *"Bắt đầu pha chế"*, *"Hoàn thành món"*).
  4. Bấm tab `"Báo Cáo & Quản Trị"`.
  5. Kiểm tra các thẻ KPI: Tổng Doanh Thu, Tổng Đơn Hàng, Đơn Hoàn Thành.
  6. Kiểm tra bảng *Tồn Kho Nguyên Liệu* và *Lịch Sử Giao Dịch*.
  7. Bấm quay lại tab `"Thu Ngân (POS)"`.
- **Kết quả mong đợi:** Trạng thái đơn luân chuyển chính xác theo vòng đời (State Lifecycle), doanh thu được cập nhật tự động.
- **Kết quả thực tế:** **PASS (Đạt)** - Video: `TC05_KitchenKDS_StatePattern_Admin.webm`.

---

## ⚙️ CẤU HÌNH QUAY VIDEO HD TRONG `playwright.config.js`

Để Playwright tự động ghi hình toàn bộ quá trình kiểm thử với chất lượng sắc nét nhất, cấu hình trong file `e2e-tests/playwright.config.js` đã được thiết lập:

```javascript
module.exports = defineConfig({
  testDir: './tests',
  fullyParallel: false, // Chạy tuần tự để video quay mượt mà, không bị chồng chéo
  workers: 1,
  reporter: [
    ['html', { open: 'never' }],
    ['list']
  ],
  use: {
    baseURL: 'http://localhost:8088',
    trace: 'on',
    screenshot: 'on',
    video: {
      mode: 'on',                     // Luôn luôn ghi video cho tất cả bài test
      size: { width: 1280, height: 720 }, // Độ phân giải chuẩn HD 720p sắc nét
    },
    viewport: { width: 1280, height: 720 },
    launchOptions: {
      slowMo: 400,                   // Độ trễ 400ms giữa các thao tác để video tự nhiên, dễ theo dõi
    },
  },
  webServer: {
    command: 'python -m http.server 8088 --directory ../frontend-react/dist',
    port: 8088,
    reuseExistingServer: true,       // Tự động kết nối nếu server đã mở
    timeout: 15000,
  },
});
```

---

## 💡 CÁCH CHUYỂN ĐỔI SANG `.MP4` HOẶC CẮT GHÉP VIDEO (NẾU CẦN)

Mặc định Playwright xuất định dạng `.webm` (chuẩn quốc tế, dung lượng nhẹ, chất lượng cao). Nếu bạn cần nộp bài định dạng `.mp4`:

1. **Xem trực tiếp:** Kéo thả file `.webm` vào trình duyệt Chrome/Edge là xem và nghe được ngay.
2. **Đổi sang MP4 online (miễn phí):** Sử dụng các trang như CloudConvert hoặc Convertio.
3. **Đổi bằng VLC Media Player:** Mở VLC -> *Media* -> *Convert / Save* -> Chọn file `.webm` -> Chọn profile *Video - H.264 + MP3 (MP4)* -> Start.
