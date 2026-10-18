# MỤC 1.2: TRIỂN KHAI KIỂM THỬ TỰ ĐỘNG CHO PHÂN HỆ BACKEND
*(Tài liệu phục vụ tổng hợp nội dung Báo cáo Đồ án - Phần thực hiện của Quân)*

---

### 1.2. TRIỂN KHAI KIỂM THỬ TỰ ĐỘNG CHO PHÂN HỆ BACKEND (UNIT & INTEGRATION TESTING VỚI JACOCO)

#### 1.2.1. Đặt vấn đề và Giải pháp thiết kế Bộ điều phối kiểm thử (Test Harness)
Trong quá trình phát triển hệ thống Coffee Shop POS, toàn bộ mã nguồn nghiệp vụ, xử lý luồng đơn hàng, tính toán tài chính và các mẫu thiết kế phần mềm (Design Patterns) đều tập trung ở tầng Backend (viết bằng Java 17). Nếu chỉ kiểm thử thủ công trên giao diện thì rất dễ bỏ sót các nhánh rẽ điều kiện (branch), không bao quát được hết các trường hợp dữ liệu biên, và mỗi lần sửa code sẽ tốn rất nhiều thời gian để test lại từ đầu.

Để phục vụ kiểm thử hồi quy liên tục mà vẫn giữ cho dự án nhẹ, có thể biên dịch và chạy trực tiếp trên bất kỳ máy tính nào bằng JDK 17 (không bắt buộc cài đặt các công cụ build nặng như Maven hay Gradle), thành viên phụ trách Backend (Quân) đã xây dựng bộ điều phối kiểm thử tự động nội bộ gồm các thành phần sau:

1. **Bộ Test Runner độc lập (`TestRunner.java`)**: 
   * Tự hiện thực các hàm kiểm chứng logic (`assertEquals`, `assertTrue`, `assertThrows`) tương tự JUnit nhưng không phát sinh thư viện phụ thuộc bên ngoài.
   * Toàn bộ 24 ca kiểm thử được chạy tuần tự, có cơ chế đếm số lượng test pass/fail và tổng hợp báo cáo trực quan ra màn hình console chỉ trong **chưa đầy 0.5 giây**.
2. **Tách biệt rạch ròi 2 mức độ kiểm thử**:
   * `UnitTestRunner.java`: Chuyên chạy 18 ca kiểm thử đơn vị độc lập trên bộ nhớ RAM.
   * `IntegrationTestRunner.java`: Chuyên chạy 6 ca kiểm thử tích hợp ghi/đọc CSDL SQLite thật.
   * Đi kèm là các script batch 1-click (`test-unit.bat`, `test-integration.bat`, `test.bat`) để các thành viên khác trong nhóm có thể kích hoạt nhanh bất cứ lúc nào.
3. **Kỹ thuật cô lập kiểm thử (Test Doubles & Isolated Database)**:
   * **In-Memory Repository**: Sử dụng lớp `InMemoryRepository` lưu trữ dữ liệu tạm thời trên RAM để phục vụ tầng Unit Test. Nhờ đó, logic nghiệp vụ của các Service được kiểm tra độc lập 100%, không phụ thuộc vào trạng thái file CSDL trên ổ cứng.
   * **Stub Cổng thanh toán (`FakeFailingGateway`)**: Tạo một lớp giả lập cổng thanh toán để chủ động trả về kết quả thất bại, giúp kiểm chứng khả năng bắt ngoại lệ của hệ thống mà không cần ngắt mạng internet thật.
   * **Cơ chế CSDL tạm cho Integration Test**: Mỗi bài test tích hợp sẽ tự tạo một file SQLite riêng biệt bằng hàm `File.createTempFile("coffee-shop-pos-test-", ".db")` và tự giải phóng khi kết thúc (`deleteOnExit()`). Cơ chế này đảm bảo các bài test không bị xung đột dữ liệu lẫn nhau và không làm ảnh hưởng đến file `pos_data.db` chính của ứng dụng.

---

#### 1.2.2. Chi tiết các thành phần nghiệp vụ và Design Patterns đưa vào kiểm thử
Quân đã thiết kế tổng cộng **24 ca kiểm thử tự động**, chia thành hai tầng cụ thể:

##### A. Bộ kiểm thử đơn vị (Unit Testing - 18 Test Cases từ TC01 đến TC18)
Tầng này tập trung kiểm tra tính đúng đắn của từng Class, từng thuật toán và sự phối hợp giữa các đối tượng trong môi trường cô lập:
* **Kiểm chứng 6 Mẫu thiết kế phần mềm (Design Patterns)**:
  * **Decorator Pattern (TC02)**: Kiểm tra công thức tính lũy tiến giá đồ uống khi bọc liên tiếp các lớp topping và kích cỡ. Cụ thể: Ly `BaseCoffee` (30.000đ) bọc thêm `PearlDecorator` (+7.000đ) và `LargeSizeDecorator` (+10.000đ) phải ra đúng chính xác 47.000đ.
  * **Strategy Pattern (TC03)**: Kiểm tra việc gán động chiến lược giảm giá `PercentDiscountStrategy(10)` vào đơn hàng tại runtime; tổng tiền đơn 100.000đ sau khi tính toán lại phải giảm còn đúng 90.000đ.
  * **State Pattern (TC04, TC05)**: TC04 kiểm tra luân chuyển trạng thái hợp lệ (*Pending $\rightarrow$ Preparing $\rightarrow$ Ready*). TC05 kiểm tra nhánh chặn: khi đơn hàng đã chuyển sang trạng thái đã thanh toán (`PaidState`), nếu có hành vi cố tình gọi `startPreparing()` thì hệ thống phải ném ra ngoại lệ `InvalidStateTransitionException` để bảo vệ đơn.
  * **Adapter Pattern (TC06, TC18)**: TC06 kiểm tra việc thanh toán qua `MomoAdapter` thành công, đơn chuyển trạng thái sang `PAID` và sinh mã giao dịch định dạng `MOMO-xxxx`. TC18 kiểm tra nhánh lỗi: khi cổng thanh toán đối tác trả về thất bại (thông qua `FakeFailingGateway`), đơn hàng bắt buộc phải giữ nguyên trạng thái `READY` và không được phép sinh bản ghi thanh toán.
  * **Observer Pattern (TC07)**: Kiểm tra cơ chế bắn thông báo thời gian thực; khi Barista đánh dấu đơn hàng đã xong (`markReady`), màn hình thu ngân (`CashierScreen`) đăng ký lắng nghe trước đó phải nhận được đúng chuỗi thông báo *"ready for pickup"*.
  * **Factory Method (TC16)**: Kiểm tra các lớp `CoffeeFactory`, `TeaFactory`, `MatchaFactory` khởi tạo đúng các đối tượng đồ uống tương ứng, bảo toàn đúng tên gọi và đơn giá gốc.
  * **Singleton Pattern (TC17)**: Kiểm tra việc gọi lặp lại `AppConfig.getInstance()` và `DatabaseConnection.getInstance()` phải trả về cùng một tham chiếu đối tượng trong bộ nhớ (toán tử so sánh bằng `==` trả về true).

* **Kiểm chứng Logic nghiệp vụ và Xử lý dữ liệu biên**:
  * **Xác thực tài khoản (TC01)**: Đăng nhập tài khoản `cashier01` với mật khẩu `123` qua `AuthService`, kiểm tra trả về đúng vai trò `CASHIER`.
  * **Quản trị Menu & Chặn dữ liệu bẩn (TC08, TC09)**: TC08 kiểm tra thêm món mới, cập nhật giá và tắt kích hoạt món nước. TC09 kiểm thử các trường hợp dữ liệu không hợp lệ: tên món để rỗng, đơn giá âm (`-1`), sai nhóm danh mục, hoặc thêm topping bị trùng tên; hệ thống phải ném `IllegalArgumentException` để chặn đứng dữ liệu sai ngay từ đầu.
  * **Quản lý kho nguyên liệu (TC10, TC11)**: TC10 kiểm tra nghiệp vụ trừ kho tự động khi đơn vào bếp (1 ly cà phê sữa tiêu hao 18g hạt) và cơ chế hoàn kho (rollback) đủ 18g khi hủy đơn chưa thanh toán. TC11 kiểm tra điều kiện chặn: nếu kho chỉ còn 5g cà phê (không đủ 18g định mức), hệ thống phải ném `InventoryException` và giữ nguyên trạng thái đơn ở mức `PENDING`.
  * **Cấu trúc hóa đơn & Xuất ảnh (TC12, TC13)**: TC12 kiểm tra nội dung text của hóa đơn có đầy đủ tên quán, tên món, tổng tiền và mã thanh toán. TC13 kiểm tra `ReceiptImageService` xuất thành công tệp ảnh đồ họa `test-receipt.png` với dung lượng lớn hơn 0 byte phục vụ in ấn.
  * **Quản trị người dùng (TC14)**: Kiểm tra nghiệp vụ tạo tài khoản nhân viên mới, khóa tài khoản (`active = false`), và kiểm tra tính năng chống tạo trùng tên đăng nhập.
  * **Quy trình giỏ hàng POS (TC15)**: Khi thu ngân click chọn cùng một món 2 lần, giỏ hàng phải tự động gộp số lượng thành 1 dòng duy nhất thay vì tạo 2 dòng riêng biệt; cho phép chỉnh sửa số lượng khi đơn còn `PENDING` và khóa cứng không cho sửa số lượng khi đơn đã chuyển vào bếp pha chế.

##### B. Bộ kiểm thử tích hợp (Integration Testing - 6 Test Cases từ TC19 đến TC24)
Tầng này kiểm tra sự phối hợp đồng bộ giữa tầng Dịch vụ (Service), tầng Truy xuất dữ liệu (`SqliteRepository`) và Hệ quản trị cơ sở dữ liệu SQLite vật lý:
* **TC19 (Lưu trữ và phục hồi vòng đời đơn hàng)**: Tạo đơn hàng hoàn chỉnh gồm các tùy biến Decorator (Size L, Trân châu), thanh toán qua Momo, lưu xuống file SQLite. Sau đó mở một kết nối mới độc lập để truy vấn lại, kiểm tra trạng thái `PAID`, số lượng món và các topping đi kèm được khôi phục chính xác 100%.
* **TC20 (Tích hợp Admin CRUD với CSDL)**: Gọi các hàm thêm mới món nước, vô hiệu hóa topping, khóa tài khoản nhân viên thông qua `MenuService` và `UserService`. Đóng kết nối, mở lại từ file SQLite và xác nhận các thay đổi dữ liệu đã được lưu trữ bền vững.
* **TC21 (Kiểm tra Nhật ký kiểm toán - Audit Trail)**: Sau khi đơn hàng trải qua quy trình đặt món và trừ kho, kiểm tra trực tiếp trong CSDL bằng câu lệnh SQL: bảng `order_status_history` phải ghi nhận đủ $\ge 4$ trạng thái luân chuyển và bảng `inventory_transactions` phải ghi nhận việc trừ số lượng nguyên liệu tương ứng.
* **TC22 (Truy vấn dữ liệu vận hành)**: Kiểm tra `OperationsService` có truy vấn và bóc tách chính xác lịch sử trạng thái đơn hàng và nhật ký biến động kho từ các bảng dữ liệu thật hay không.
* **TC23 (Quy trình Sao lưu và Phục hồi CSDL)**: Gọi hàm `backupDatabase()` để kết xuất một file CSDL snapshot dạng `.db`. Kiểm tra file backup được tạo thành công trên đĩa cứng, sau đó mở file này bằng một Repository mới để xác thực toàn bộ dữ liệu đơn hàng bên trong vẫn nguyên vẹn.
* **TC24 (Cấu hình định mức công thức pha chế)**: Tích hợp việc lưu định mức nguyên liệu tiêu hao của món nước (`RecipeItem`) vào SQLite (ví dụ: gán 180g quả bơ cho món sinh tố) và kiểm tra thao tác xóa công thức hoạt động chuẩn xác trong CSDL.

---

#### 1.2.3. Đo lường Độ bao phủ mã nguồn (Code Coverage) bằng công cụ JaCoCo
Để đánh giá chất lượng của bộ kiểm thử một cách khách quan theo tiêu chuẩn công nghiệp chứ không chỉ dừng lại ở việc đếm số bài test passed, Quân đã tích hợp công cụ **JaCoCo (Java Code Coverage)** phiên bản 0.8.14 vào quy trình kiểm thử tự động.

Quá trình đo lường được thực hiện bằng cách gắn `JaCoCo JavaAgent` trực tiếp vào máy ảo JVM khi chạy `TestRunner`, thu thập dữ liệu thực thi vào file nhị phân `jacoco.exec`, sau đó dùng `jacococli.jar` để phân tích và xuất báo cáo trực quan dưới dạng HTML.

##### 1. Kết quả độ bao phủ tổng thể
Dưới đây là số liệu đo lường thực tế trên mã nguồn dự án:

| Phân vùng kiểm thử | Line Coverage (Bao phủ dòng lệnh) | Branch Coverage (Bao phủ nhánh điều kiện) | Instruction Coverage (Bao phủ mã máy) | Ghi chú đánh giá |
| :--- | :---: | :---: | :---: | :--- |
| **Toàn bộ Core Backend** *(Domain + Patterns + Service + Database)* | **74.3%** <br>*(1,627 / 2,189 dòng)* | **51.0%** <br>*(275 / 539 nhánh)* | **74.1%** <br>*(7,796 / 10,527 mã máy)* | **Đạt chuẩn công nghiệp** *(Mức khuyến nghị cho backend nghiệp vụ là $\ge 70\%$)* |
| **8 Đơn vị mã nguồn Hộp trắng trọng yếu** *(Chương 4)* | **100%** | **100%** | **100%** | **Đạt tối đa** *(Bao phủ 28/28 đường cơ sở McCabe và 100% cặp định nghĩa - sử dụng DU-chains)* |
| **Toàn bộ Dự án** *(Tính gộp cả mã giao diện đồ họa Swing Desktop)* | **32.7%** <br>*(1,627 / 4,978 dòng)* | **22.9%** <br>*(275 / 1,200 nhánh)* | **30.4%** | *Tầng UI (~2,800 dòng) không test bằng Java headless mà được kiểm thử bằng Playwright E2E* |

##### 2. Chi tiết độ bao phủ theo từng Package Backend
Báo cáo JaCoCo phân tích chi tiết mức độ bao phủ của từng gói chức năng như sau:
* `com.coffeeshop.domain.patterns.singleton`: **90.9%** Line, **75.0%** Branch (Bao phủ gần như tuyệt đối logic quản lý instance).
* `com.coffeeshop` *(Ngữ cảnh và cấu hình hệ thống)*: **84.8%** Line, **57.1%** Branch.
* `com.coffeeshop.infrastructure` *(Kho lưu trữ SQLite & In-Memory)*: **82.4%** Line, **60.0%** Branch (Các hàm mapping dữ liệu SQL, transaction và rollback được kiểm tra rất kỹ).
* `com.coffeeshop.domain.patterns.factory`: **77.8%** Line (Bao phủ toàn bộ các phương thức tạo đồ uống).
* `com.coffeeshop.domain.model`: **77.2%** Line, **33.3%** Branch (Kiểm tra đầy đủ các thực thể Đơn hàng, Món, Nguyên liệu, Người dùng).
* `com.coffeeshop.service` *(Các lớp nghiệp vụ then chốt)*: **75.7%** Line, **57.8%** Branch (Bao phủ các dịch vụ Order, Payment, Menu, Inventory, Operations, User).
* `com.coffeeshop.domain.patterns.decorator`: **66.7%** Line.
* `com.coffeeshop.domain.patterns.state`: **63.3%** Line.
* `com.coffeeshop.domain.patterns.observer`: **53.3%** Line, **66.7%** Branch.
* `com.coffeeshop.domain.patterns.adapter`: **53.8%** Line, **25.0%** Branch.
* `com.coffeeshop.domain.patterns.strategy`: **41.7%** Line, **35.7%** Branch.

*(Lưu ý: Riêng gói `com.coffeeshop.presentation` có tỷ lệ 0% trong báo cáo JaCoCo vì đây là tầng giao diện đồ họa Java Swing desktop, không chạy qua Unit Test headless mà được nhóm kiểm thử tự động toàn diện từ góc nhìn người dùng bằng công cụ Microsoft Playwright).*

##### 3. Giao diện báo cáo JaCoCo HTML trực quan
Toàn bộ dữ liệu chi tiết của từng class và từng dòng lệnh đã được xuất ra thư mục:
`build/jacoco-report/index.html`

Khi mở file này trên trình duyệt web, người chấm bài hoặc thành viên nhóm có thể click xem mã nguồn chi tiết:
* **Các dòng code được tô màu xanh lá cây**: Dòng lệnh đã được thực thi và kiểm chứng bởi test case.
* **Các dòng code được tô màu vàng**: Dòng lệnh chứa biểu thức điều kiện (if/else) nhưng mới chỉ kiểm tra được một nhánh (ví dụ: mới test nhánh true, chưa test nhánh false).
* **Các dòng code được tô màu đỏ**: Dòng lệnh chưa có test case nào chạm tới (chủ yếu rơi vào các hàm getter/setter phụ hoặc các đoạn code hiển thị UI).

---

#### 1.2.4. Kết luận về hiệu quả kiểm thử Backend
Việc xây dựng bộ kiểm thử tự động nội bộ kết hợp đo lường độ bao phủ JaCoCo đã mang lại các kết quả thực tế rõ ràng:
1. **Phát hiện và ngăn chặn lỗi sớm**: Nhờ các test case biên như TC05 (chặn sửa đơn đã thanh toán), TC09 (chặn giá âm/tên rỗng) và TC11 (chặn vào bếp khi thiếu nguyên liệu), nhóm đã xử lý dứt điểm các lỗi logic tiềm ẩn ngay từ tầng Service trước khi kết nối lên giao diện.
2. **Đảm bảo tính toàn vẹn kiến trúc Design Pattern**: Bộ 18 Unit Test đóng vai trò như một bộ rào chắn kỹ thuật (Safety Net), đảm bảo rằng mỗi khi có thành viên trong nhóm sửa code hay tối ưu hóa thuật toán thì các nguyên lý của 6 mẫu thiết kế phần mềm vẫn luôn chạy đúng theo thiết kế ban đầu.
3. **Minh chứng số liệu thuyết phục cho báo cáo**: Thay vì chỉ mô tả định tính, nhóm đã có số liệu định lượng cụ thể với **100% test case vượt qua (24/24 Pass)** và **độ bao phủ dòng lệnh tầng Core Backend đạt 74.3%**, đáp ứng tốt các tiêu chí đánh giá khắt khe của học phần Kiểm thử & Đảm bảo chất lượng phần mềm.
