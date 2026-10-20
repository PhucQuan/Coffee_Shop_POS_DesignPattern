# Coffee Shop POS Web

Coffee Shop POS gồm React frontend, Java SE web server/API, MySQL và bộ kiểm thử Playwright. Phần giao diện Java Swing/Desktop đã được loại bỏ.

## Công nghệ

- Frontend: React 19, Vite 8, Tailwind CSS 4
- Web server/API: Java 17 `com.sun.net.httpserver.HttpServer`
- Database backend: MySQL 8+ qua JDBC
- Website tải/lưu dữ liệu bền vững qua API `/api/state`; không lưu dữ liệu nghiệp vụ trong `localStorage`
- E2E testing: Playwright

## Cấu trúc chính

- `frontend-react/`: giao diện web React
- `src/main/java/com/coffeeshop/api/`: Java web server và API
- `src/main/java/com/coffeeshop/domain/`: domain model và design patterns
- `src/main/java/com/coffeeshop/service/`: business services
- `src/main/java/com/coffeeshop/infrastructure/`: cấu hình JDBC và MySQL state repository
- `e2e-tests/`: kiểm thử Playwright
- `sql/schema.sql`: schema MySQL đầy đủ và dữ liệu mẫu

## Chuẩn bị MySQL

1. Cài và chạy MySQL Server 8.0 trở lên.
2. Mở `config/mysql.properties` và sửa thông tin kết nối một lần:

```properties
mysql.host=localhost
mysql.port=3306
mysql.database=coffee_shop_pos
mysql.user=root
mysql.password=mat-khau-mysql-cua-ban
```

File này đã được `.gitignore`, do đó mật khẩu cục bộ không bị commit. File `config/mysql.properties.example` là mẫu có thể chia sẻ. Sau khi lưu cấu hình, chỉ cần chạy `run-web.ps1` hoặc `run-web.bat` mà không phải nhập lại.

Nếu cần ghi đè tạm thời, vẫn có thể đặt biến môi trường trong PowerShell; biến môi trường được ưu tiên hơn file cấu hình:

```powershell
$env:MYSQL_HOST = "localhost"
$env:MYSQL_PORT = "3306"
$env:MYSQL_DATABASE = "coffee_shop_pos"
$env:MYSQL_USER = "root"
$env:MYSQL_PASSWORD = "mat-khau-mysql-cua-ban"
```

Nếu tài khoản có quyền `CREATE DATABASE`, backend sẽ tự tạo database và bảng `web_app_state` ở lần chạy đầu. Nếu tài khoản ứng dụng không có quyền này, hãy tạo schema trước bằng MySQL client:

```powershell
cmd /c "mysql -u root -p < sql\schema.sql"
```

Có thể dùng `MYSQL_URL` để ghi đè toàn bộ JDBC URL, ví dụ khi kết nối MySQL trên máy chủ khác. Khi dùng `MYSQL_URL`, database cần tồn tại sẵn. Không commit mật khẩu vào mã nguồn.

## Chạy ứng dụng web

PowerShell:

```powershell
.\run-web.ps1
```

Command Prompt:

```bat
run-web.bat
```

Script sẽ tự cài npm packages khi cần, build React, biên dịch Java backend và mở `http://localhost:8088`. MySQL Connector/J chính thức đã nằm trong `libs/`.

## Chạy React development server

```powershell
.\run-react.ps1
```

Development server chạy tại `http://localhost:3000`.

## Chạy kiểm thử backend Java

```powershell
.\test.ps1
```

## Chạy kiểm thử Playwright

```powershell
.\run-e2e-tests.ps1
```

Hoặc chạy riêng file payment:

```powershell
cd e2e-tests
npx playwright test tests/payment_20_testcases.spec.js --project=chromium
```

## Lưu ý dữ liệu

Menu, topping, kho, nhật ký kho, tài khoản, công thức, đơn hàng và bộ đếm đơn được lưu trong MySQL dưới dạng JSON hợp lệ ở bảng `web_app_state`. Backend tự tạo/cập nhật một bản ghi có khóa `coffee-pos`. Giỏ hàng đang thao tác và phiên đăng nhập chỉ tồn tại trong bộ nhớ của tab, nên không bị chia sẻ nhầm giữa các quầy.

API liên quan:

- `GET /api/state`: tải dữ liệu từ MySQL.
- `PUT /api/state`: lưu snapshot mới vào MySQL.
- `DELETE /api/state`: xóa snapshot để nạp lại dữ liệu mẫu.
- `GET /api/health`: kiểm tra cả web server và kết nối MySQL.

`SqliteRepository` và `pos_data.db` cũ chỉ còn phục vụ kiểm thử hồi quy/chuyển đổi dữ liệu, không còn là nơi lưu dữ liệu của website.
