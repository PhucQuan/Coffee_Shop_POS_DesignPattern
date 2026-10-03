# TÀI LIỆU SLIDE THUYẾT TRÌNH & KỊCH BẢN NÓI (SPEAKER SCRIPT)
> **Đề tài:** Hệ Thống Quản Lý Bán Hàng Coffee Shop POS  
> **Chuyên đề:** Kiến Trúc Design Patterns & Tự Động Hóa Kiểm Thử Hộp Đen với Microsoft Playwright  
> **Tệp trình chiếu tương tác:** Mở trực tiếp file [`docs/slides_presentation.html`](file:///c:/Users/DELL/Downloads/Coffee_Shop_POS_DesignPattern/docs/slides_presentation.html) trên trình duyệt (dùng phím `←` `→` chuyển slide, phím `S` bật kịch bản nói).

---

## 🎙️ CẤU TRÚC BÀI THUYẾT TRÌNH (7 SLIDE CHUẨN)

### 📌 SLIDE 1: GIỚI THIỆU ĐỀ TÀI & TỔNG QUAN
* **Tiêu đề Slide:** HỆ THỐNG COFFEE SHOP POS - KIẾN TRÚC DESIGN PATTERNS & KIỂM THỬ PLAYWRIGHT E2E
* **Nội dung hiển thị trên Slide:**
  - Mô hình hệ thống: Desktop POS & Web POS hiện đại.
  - Kiến trúc phần mềm: 6 Design Patterns cốt lõi (Factory Method, Decorator, Strategy, Adapter, State, Observer).
  - Phương pháp kiểm thử: Kiểm thử hộp đen tự động toàn diện (End-to-End Black-box Testing).
  - Công nghệ kiểm thử: Microsoft Playwright, ghi hình HD 720p tự động, báo cáo HTML tương tác.
* **Kịch bản nói (Speaker Script):**
  > *"Kính chào Thầy/Cô và các bạn. Hôm nay nhóm em xin đại diện trình bày đồ án môn học với đề tài: **'Hệ thống Quản lý Bán hàng Coffee Shop POS kết hợp Kiến trúc Mẫu Design Patterns và Tự động hóa kiểm thử hộp đen với Microsoft Playwright'**.*  
  > *Đồ án của nhóm tập trung vào hai trụ cột chính: Một là hiện thực chuẩn mực 6 mẫu thiết kế cốt lõi trong bài toán nghiệp vụ quán cà phê; Hai là xây dựng quy trình kiểm thử tự động E2E chuyên nghiệp, tự động quay video màn hình HD để chứng minh độ ổn định và tính đúng đắn của phần mềm."*

---

### 📌 SLIDE 2: 6 MẪU DESIGN PATTERN CỐT LÕI
* **Tiêu đề Slide:** KIẾN TRÚC PHẦN MỀM - 6 DESIGN PATTERNS ÁP DỤNG
* **Nội dung hiển thị trên Slide:**
  1. **Factory Method:** Khởi tạo đồ uống đóng gói theo từng nhóm (Coffee, Tea, Matcha, Smoothie).
  2. **Decorator Pattern:** Tùy biến món uống linh hoạt (chọn Size L +10k, thêm Topping trân châu, kem cheese, ghi chú).
  3. **Strategy Pattern:** Thay đổi thuật toán chiết khấu linh hoạt tại thời điểm thanh toán (Giảm 10%, VIP 15%, BOGO).
  4. **Adapter Pattern:** Tích hợp đa cổng thanh toán (Tiền mặt Cash, VietQR VNPay, MoMo) qua interface thống nhất.
  5. **State Pattern:** Quản lý vòng đời trạng thái đơn hàng tại Bếp (Chờ Pha Chế ➔ Đang Pha Chế ➔ Sẵn Sàng).
  6. **Observer Pattern:** Đồng bộ dữ liệu tự động theo thời gian thực từ quầy Thu ngân sang Bếp và Màn hình Quản trị.
* **Kịch bản nói (Speaker Script):**
  > *"Về mặt kiến trúc, nhóm em không viết mã nguồn theo cách thông thường mà áp dụng chặt chẽ 6 mẫu thiết kế:*  
  > *- **Factory Method** giúp tách biệt logic tạo các dòng đồ uống.*  
  > *- **Decorator Pattern** cho phép khách hàng bọc thêm Size L hoặc Topping trân châu, kem cheese mà không cần sửa đổi class đồ uống gốc.*  
  > *- **Strategy Pattern** giúp thu ngân linh hoạt áp dụng các công thức giảm giá như Thành viên 10% hoặc VIP 15% ngay khi thanh toán.*  
  > *- **Adapter Pattern** chuẩn hóa mọi phương thức thanh toán tiền mặt hay quét mã QR VNPay về một chuẩn xử lý chung.*  
  > *- Cuối cùng là **State** và **Observer Pattern** giúp luân chuyển trạng thái đơn hàng trên màn hình Bếp và tự động nhảy doanh thu sang trang Quản trị."*

---

### 📌 SLIDE 3: TỔNG QUAN VỀ KIỂM THỬ PLAYWRIGHT
* **Tiêu đề Slide:** GIẢI PHÁP KIỂM THỬ TỰ ĐỘNG VỚI MICROSOFT PLAYWRIGHT
* **Nội dung hiển thị trên Slide:**
  - Vì sao chọn Playwright thay vì kiểm thử thủ công?
  - Mô phỏng người dùng thật 100%: Tự mở trình duyệt, tự click chọn món, gõ ghi chú, thanh toán.
  - Tự động ghi hình HD (1280x720): Không cần dùng phần mềm quay màn hình rời.
  - Tự động chạy ngầm Web Server: Cấu hình `webServer` trong `playwright.config.js` tránh lỗi thiếu server.
  - Báo cáo HTML trực quan: Tích hợp video, DOM snapshot và trace log từng thao tác.
* **Kịch bản nói (Speaker Script):**
  > *"Để đảm bảo hệ thống vận hành không lỗi, nhóm em quyết định áp dụng kiểm thử tự động bằng **Microsoft Playwright**.*  
  > *Thay vì con người phải ngồi click từng món kiểm tra thủ công vừa chậm vừa dễ sót, Playwright sẽ tự điều khiển trình duyệt Chrome thực hiện chính xác các thao tác của thu ngân.*  
  > *Đặc biệt, nhóm đã cấu hình ghi hình tự động chuẩn HD 720p với nhịp độ chuyển động tự nhiên (`slowMo: 400ms`), giúp mọi người có thể xem lại từng bước thực thi một cách trực quan và rõ nét."*

---

### 📌 SLIDE 4: CODE PLAYWRIGHT TEST THỰC CHẤT ĐANG TEST NHỮNG GÌ?
* **Tiêu đề Slide:** BẢN CHẤT CỦA CODE KIỂM THỬ PLAYWRIGHT TRONG DỰ ÁN
* **Nội dung hiển thị trên Slide:**
  - **1. Định vị thành phần (Locators):** Tìm kiếm selector `#menuSearch`, `#discountSelect`, `#btnOpenCheckout`.
  - **2. Hành động mô phỏng (Actions):** `page.click()`, `page.fill()`, `selectOption()`, `waitForTimeout()`.
  - **3. Khẳng định & Kiểm chứng (Assertions):**
    - Kiểm tra logic **Decorator**: Đơn giá Size L và Topping có cộng dồn chính xác không?
    - Kiểm tra logic **Strategy**: Mã VIP có trừ chuẩn 15% tổng tiền không?
    - Kiểm tra logic **Adapter**: Mã VietQR có hiển thị đúng số tiền cần trả không?
    - Kiểm tra logic **State**: Đơn hàng có xuất hiện ở cột Chờ pha chế và luân chuyển sang Đã xong không?
* **Kịch bản nói (Speaker Script):**
  > *"Nhiều người thường hỏi: **'Code Playwright test thực chất kiểm tra những cái gì?'**.*  
  > *Nhóm em xin giải thích: Code test không chỉ đơn thuần là bấm nút trên web, mà bản chất là **kiểm chứng các quy tắc nghiệp vụ và công thức toán học của các Design Pattern**:*  
  > *Thứ nhất, nó kiểm tra **Decorator** có cộng đúng 10.000đ khi chọn Size L và 10.000đ khi chọn Trân châu trắng hay không.*  
  > *Thứ hai, nó kiểm chứng **Strategy** có tính toán đúng chiết khấu 10% và 15% để trừ vào tổng bill hay không.*  
  > *Thứ ba, nó kiểm tra **Adapter** có sinh mã QR VietQR đúng số tiền đơn hàng và in hóa đơn đầy đủ chi tiết hay không.*  
  > *Mọi kết quả đều được hàm `expect` xác thực nghiêm ngặt, sai lệch dù chỉ 1 đồng cũng sẽ báo FAILED ngay lập tức."*

---

### 📌 SLIDE 5: BỘ 5 KỊCH BẢN KIỂM THỬ HỘP ĐEN (MA TRẬN TEST)
* **Tiêu đề Slide:** KẾT QUẢ THỰC THI 5 KỊCH BẢN TEST HỘP ĐEN
* **Nội dung hiển thị trên Slide:** Bảng kết quả 5 Test Cases:
  - **TC01:** Giao diện Thu Ngân, Tìm kiếm món & Lọc Danh mục ➔ **PASS 100% (12s)**
  - **TC02:** Tùy biến món uống (Decorator Pattern) & Giỏ hàng ➔ **PASS 100% (11s)**
  - **TC03:** Áp dụng mã khuyến mãi & Chiết khấu (Strategy Pattern) ➔ **PASS 100% (11s)**
  - **TC04:** Quy trình Thanh toán đa kênh (Adapter) & Xuất Hóa Đơn ➔ **PASS 100% (17s)**
  - **TC05:** Màn hình Bếp (State Pattern) & Quản Trị (Admin) ➔ **PASS 100% (14s)**
* **Kịch bản nói (Speaker Script):**
  > *"Nhóm đã xây dựng bộ 5 kịch bản kiểm thử hộp đen chuẩn mực bao phủ toàn bộ luồng hoạt động:*  
  > *Từ TC01 tìm kiếm Bạc xỉu và lọc danh mục trà; TC02 tùy biến món với Decorator; TC03 đổi các chiến lược giảm giá Strategy; TC04 quét mã VietQR và in hóa đơn; cho đến TC05 luân chuyển trạng thái ở Bếp KDS và kiểm tra doanh thu quản trị.*  
  > *Kết quả thực tế cho thấy toàn bộ 5/5 kịch bản đều đạt kết quả **PASS 100%** chỉ trong vòng 1.2 phút."*

---

### 📌 SLIDE 6: BỘ VIDEO DEMO & TIỆN ÍCH QUAY MÀN HÌNH
* **Tiêu đề Slide:** THÀNH QUẢ: HỆ THỐNG VIDEO DEMO & CÔNG CỤ TỰ ĐỘNG
* **Nội dung hiển thị trên Slide:**
  - **Video trọn vẹn 49s:** `DEMO_TOAN_BO_QUY_TRINH_COFFEE_POS.webm` (3.1 MB) quay liền mạch từ gọi món đến ra hóa đơn và pha chế.
  - **5 Video độc lập:** Tương ứng từ TC01 đến TC05 phục vụ đưa vào phụ lục báo cáo.
  - **Tiện ích 1-Click:** `run-e2e-tests.bat` tự chạy test và gom video vào `e2e-tests/videos/`.
  - **Hỗ trợ Bandicam:** `quay-video-bandicam.bat` tự bật Bandicam để quay trực tiếp màn hình desktop khi demo.
* **Kịch bản nói (Speaker Script):**
  > *"Điểm đặc biệt của đồ án là toàn bộ quá trình test đều được tự động lưu lại dưới dạng video HD sắc nét.*  
  > *Nhóm đã xuất sẵn trọn bộ video tại thư mục `e2e-tests/videos`, nổi bật là video toàn cảnh 49 giây ghi hình toàn bộ quy trình bán hàng.*  
  > *Đồng thời, nhóm viết sẵn các kịch bản 1-click như `quay-video-bandicam.bat` giúp bất kỳ ai cũng có thể mở phần mềm Bandicam và thực thi kiểm thử trực tiếp trên màn hình máy tính chỉ bằng một cú nhấp chuột."*

---

### 📌 SLIDE 7: KẾT LUẬN & CHUYỂN TIẾP SANG LIVE DEMO
* **Tiêu đề Slide:** KẾT LUẬN & SẴN SÀNG CHO PHẦN LIVE DEMO
* **Nội dung hiển thị trên Slide:**
  - Hoàn thành xuất sắc mục tiêu kết hợp Design Patterns & Tự động hóa kiểm thử.
  - Đảm bảo tính minh bạch, tái lập (reproducible) và chuyên nghiệp của đồ án.
  - Mã nguồn đồng bộ GitHub: `PhucQuan/Coffee_Shop_POS_DesignPattern`.
  - Bắt đầu phần chạy trực tiếp Playwright trên màn hình!
* **Kịch bản nói (Speaker Script):**
  > *"Tóm lại, đồ án Coffee Shop POS đã giải quyết trọn vẹn cả hai yêu cầu: Kiến trúc hướng đối tượng chuẩn mực và Quy trình kiểm thử tự động hiện đại.*  
  > *Ngay sau đây, nhóm xin phép được bắt đầu phần **Live Demo trực tiếp trên màn hình** để Thầy/Cô và các bạn cùng quan sát trình duyệt Chrome tự động thực thi kịch bản kiểm thử. Xin trân trọng cảm ơn!"*
