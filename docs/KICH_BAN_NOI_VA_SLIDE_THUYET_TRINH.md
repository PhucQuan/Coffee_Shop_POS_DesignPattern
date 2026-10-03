# BẢN THUYẾT TRÌNH: CÀI ĐẶT & TRIỂN KHAI PLAYWRIGHT TEST
> **Trọng tâm bài nói:** Đi thẳng vào kỹ thuật thực tế — Cách cài đặt môi trường, cấu hình Playwright, cách viết code kiểm thử và các lệnh chạy demo xuất video.  
> **Tệp trình chiếu trực quan:** Mở trực tiếp file [`docs/slides_presentation.html`](file:///c:/Users/DELL/Downloads/Coffee_Shop_POS_DesignPattern/docs/slides_presentation.html) trên trình duyệt (dùng phím `←` `→` chuyển slide, phím `S` bật lời thoại MC).

---

## 🎙️ NỘI DUNG 5 SLIDE CỐT LÕI & LỜI THOẠI NÓI (SPEAKER SCRIPT)

### 📌 SLIDE 1: TỔNG QUAN & MỤC TIÊU
* **Nội dung slide:**
  - Tiêu đề: Cài Đặt & Triển Khai Kiểm Thử Tự Động Với Microsoft Playwright.
  - Mục tiêu: Tự động hóa kiểm thử hộp đen từ góc nhìn người dùng thực tế trên ứng dụng Web Coffee Shop POS.
  - Ba trụ cột: Cài đặt môi trường ➔ Cấu hình tự quay video ➔ Thực thi kiểm thử và xuất báo cáo.
* **Kịch bản nói (Đọc tự nhiên):**
  > *"Kính chào Thầy/Cô và các bạn. Hôm nay em xin trình bày ngắn gọn về chuyên đề: **Cài đặt và triển khai kiểm thử tự động hộp đen bằng Microsoft Playwright** cho ứng dụng Web Coffee Shop POS.*  
  > *Bài báo cáo sẽ tập trung thẳng vào 4 bước kỹ thuật thực tế: Cách cài đặt môi trường, cách cấu hình runner, cấu trúc một file code test gồm những gì và các câu lệnh chạy demo để tự động xuất ra video và báo cáo kiểm thử."*

---

### 📌 SLIDE 2: BƯỚC 1 - CÀI ĐẶT MÔI TRƯỜNG & THƯ VIỆN
* **Nội dung slide:**
  - 1. Chuẩn bị Node.js LTS (tải từ `nodejs.org` để có runtime và `npm`).
  - 2. Di chuyển vào thư mục test và cài thư viện:
    ```powershell
    cd e2e-tests
    npm install
    ```
  - 3. Tải trình duyệt Chromium kiểm thử độc lập:
    ```powershell
    npx playwright install chromium
    ```
  - Ưu điểm: Chromium chạy độc lập trong sandbox, không đụng chạm hay xung đột với trình duyệt Chrome cá nhân của người dùng.
* **Kịch bản nói (Đọc tự nhiên):**
  > *"Bước đầu tiên là Cài đặt môi trường. Yêu cầu duy nhất trên máy là có cài sẵn Node.js bản LTS.*  
  > *Sau đó, ta chỉ cần mở cửa sổ dòng lệnh tại thư mục `e2e-tests` và chạy đúng hai câu lệnh:*  
  > *- Thứ nhất là `npm install` để tải thư viện Playwright Test.*  
  > *- Thứ hai là `npx playwright install chromium` để tải trình duyệt Chromium sạch về máy.*  
  > *Ưu điểm là Chromium này chạy hoàn toàn độc lập, tách biệt với Chrome cá nhân, giúp môi trường kiểm thử luôn sạch sẽ và nhất quán trên mọi máy tính."*

---

### 📌 SLIDE 3: BƯỚC 2 - CẤU HÌNH TỰ BẬT WEB & QUAY VIDEO HD
* **Nội dung slide:**
  - File cấu hình cốt lõi: `e2e-tests/playwright.config.js`.
  - **Tự quay video (`video: 'on'`):** Độ phân giải chuẩn HD 720p (1280x720), tự xuất file `.webm` mà không cần phần mềm quay màn hình ngoài (như Bandicam/OBS).
  - **Nhịp độ quan sát (`slowMo: 400ms`):** Thêm độ trễ vừa phải giữa các cú click để người xem nhìn rõ thao tác.
  - **Tự động chạy Web Server (`webServer`):** Playwright tự động bật web app ngầm tại cổng 8088 nếu người dùng quên bật trước, ngăn ngừa triệt để lỗi mất kết nối.
* **Kịch bản nói (Đọc tự nhiên):**
  > *"Bước thứ hai là Cấu hình kiểm thử trong file `playwright.config.js`. Tại đây, em thiết lập 3 thông số kỹ thuật then chốt:*  
  > *Một là cấu hình `video: 'on'` với độ phân giải HD 1280x720. Nhờ đó, Playwright sẽ tự động quay phim lại toàn bộ màn hình của từng bài test thành file video `.webm` mà ta không cần phải bật phần mềm quay màn hình rời bên ngoài.*  
  > *Hai là tùy chọn `slowMo: 400ms` tạo độ trễ tự nhiên giữa các thao tác để khi chiếu video hay demo trực tiếp, người xem nhìn rõ chuột click vào đâu.*  
  > *Và ba là khối `webServer` giúp Playwright tự động khởi động web app ở cổng 8088 nếu trước đó server chưa được bật."*

---

### 📌 SLIDE 4: BƯỚC 3 - CẤU TRÚC CODE TEST (CODE TEST NHỮNG CÁI GÌ?)
* **Nội dung slide:**
  - 1 file code test chuẩn của Playwright gồm 3 phần:
    1. **Locators (Định vị):** Tìm nút, ô nhập bằng ID hoặc Text (`page.locator('#menuSearch')`, `page.locator('text=Bạc xỉu')`).
    2. **Actions (Thao tác):** Mô phỏng hành vi người dùng (`fill('Bạc xỉu')`, `click()`, `selectOption('VIP')`).
    3. **Assertions (Xác thực):** Kiểm tra kết quả trả về bằng hàm `expect(...)`:
       - `expect(item).toBeVisible()` : Kiểm tra món hiển thị.
       - `expect(btn).toBeEnabled()` : Kiểm tra nút thanh toán mở khóa.
       - `expect(total).not.toHaveText('0 ₫')` : Kiểm tra tiền đã được tính toán.
* **Kịch bản nói (Đọc tự nhiên):**
  > *"Bước thứ ba là Cách viết code kiểm thử. Một bài test Playwright thực chất gồm 3 thành phần rất dễ hiểu:*  
  > *- Thứ nhất là **Locators**: Định vị phần tử trên giao diện thông qua ID hoặc nội dung chữ, ví dụ tìm ô tìm kiếm `#menuSearch` hoặc nút thanh toán `#btnOpenCheckout`.*  
  > *- Thứ hai là **Actions**: Mô phỏng các thao tác bấm chuột và gõ phím của thu ngân, như gõ chữ tìm món, click chọn Size L, chọn mã giảm giá VIP.*  
  > *- Và quan trọng nhất là **Assertions**: Sử dụng hàm `expect` để kiểm tra kết quả. Ví dụ sau khi thêm món thì giỏ hàng có tăng số lượng không? Chọn giảm giá thì tiền có trừ đúng không? Nếu có bất kỳ sai lệch nào, Playwright sẽ báo lỗi ngay lập tức."*

---

### 📌 SLIDE 5: BƯỚC 4 - CÁC LỆNH THỰC THI & BÁO CÁO KẾT QUẢ
* **Nội dung slide:**
  - **Chạy có mở trình duyệt:** `npx playwright test --headed` (xem Chrome tự thao tác trên màn hình).
  - **Chạy giao diện UI tương tác:** `npx playwright test --ui` (soi timeline, time-travel debug).
  - **Xem báo cáo trực quan:** `npx playwright show-report` (mở trang HTML có đính kèm video và trace).
  - **Tiện ích 1-Click tự động:** Nhấp đúp `run-e2e-tests.bat` (hoặc `quay-video-bandicam.bat`).
  - **Thư mục video kết quả:** `e2e-tests/videos/` đã có sẵn 6 video HD sắc nét.
* **Kịch bản nói (Đọc tự nhiên):**
  > *"Cuối cùng là cách Thực thi kiểm thử. Ta có thể chạy lệnh `npx playwright test --headed` để nhìn thấy tận mắt trình duyệt Chrome tự động thao tác trên màn hình máy tính; hoặc chạy lệnh `test --ui` để soi lại lịch sử từng giây xem chuột đã bấm vào đâu.*  
  > *Sau khi chạy xong, lệnh `npx playwright show-report` sẽ mở trang báo cáo HTML trực quan hiển thị kết quả Xanh 100% kèm video xem online.*  
  > *Để tiện nhất cho người dùng, nhóm đã đóng gói sẵn file 1-click `run-e2e-tests.bat`, chỉ cần nhấp đúp là tự chạy toàn bộ và gom video vào thư mục `videos`. Sau đây, em xin phép chạy demo trực tiếp trên máy để Thầy Cô cùng quan sát. Em xin cảm ơn!"*
