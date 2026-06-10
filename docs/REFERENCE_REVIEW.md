# Review cac tai lieu tham khao Coffee Shop POS

File nay doi chieu cac goi y tham khao voi project hien tai, de khi bao cao co the giai thich vi sao minh chon kien truc va pattern nhu hien tai.

## 1. MVC / Layered Architecture

Tai lieu tham khao goi y chia theo MVC:

- Model: entity va business logic.
- View: JavaFX/Swing UI.
- Controller: xu ly su kien UI.
- Database: MySQL/SQLite.

Project hien tai chon kien truc phan lop 4 tang:

- `presentation`: Swing views, chi xu ly hien thi va event UI.
- `service`: nghiep vu POS, order, payment, report, inventory.
- `domain`: entity va design pattern.
- `infrastructure`: repository, DB connection demo, data source in-memory.

Ket luan: phan lop hien tai chuyen nghiep hon MVC co ban cho mon Design Pattern, vi pattern nam rieng trong domain va business logic khong bi nhot trong UI.

## 2. JavaFX hay Swing

Tai lieu tham khao khuyen JavaFX vi hien dai hon. Project hien tai dung Swing vi:

- De chay tren may khong can cai them JavaFX SDK.
- Phu hop demo cuoi ky, chi can JDK la build/run duoc.
- Code UI don gian, tap trung vao pattern va service layer.

Ket luan: Swing van hop ly. Diem chinh cua mon la Design Pattern, khong phai framework UI.

## 3. Database

Tai lieu tham khao khuyen MySQL/SQLite de luu hoa don. Project hien tai co:

- `sql/schema.sql`: schema day du bang users, beverages, toppings, orders, payments, inventory.
- `DatabaseConnection`: Singleton mo phong diem ket noi DB.
- `InMemoryRepository`: repository demo de app chay ngay, khong can cai DB.

Ket luan: khong bat buoc ket noi DB that cho demo pattern. Neu giang vien yeu cau, schema da san sang de nang cap sang SQLite/MySQL.

## 4. Cashier Module

Tai lieu tham khao yeu cau:

- Danh sach mon co hinh.
- Bam chon mon.
- Tuy chinh topping/size.
- Tinh tong tien.
- Thanh toan.

Project hien tai da co:

- POS menu co filter/search/category.
- Icon do uong Java2D.
- Topping Pearl, Size L, Extra shot.
- Discount strategy.
- MoMo/VNPay adapter.
- Receipt preview va export PNG.

Ket luan: da dat muc demo POS hoan chinh.

## 5. Inventory

Tai lieu tham khao goi y tru nguyen lieu khi ban. Project hien tai da co:

- `InventoryService`.
- `InventoryItem`.
- Recipe-based stock deduct khi gui don sang bep.
- Rollback stock khi huy don truoc khi thanh toan.
- AdminView co tab inventory.

Ket luan: day la diem cong, vi nhieu demo Design Pattern thuong khong xu ly kho.

## 6. Receipt / In bill

Tai lieu tham khao goi y in bill hoac xuat file. Project hien tai da co:

- `ReceiptService`: tao noi dung hoa don.
- `ReceiptPreviewDialog`: xem bill tren UI.
- `ReceiptImageService`: xuat anh PNG.
- QR simulation cho VNPay/MoMo.

Ket luan: khong can may in that. Mo phong bill bang preview/PNG la du cho demo.

## 7. Report

Tai lieu tham khao goi y bao cao doanh thu va mon ban chay. Project hien tai da co:

- `ReportService`.
- AdminView co tab report.
- Bar chart Java2D cho top mon ban chay.

Ket luan: da dap ung phan back-office co ban.

## 8. Cac repo tham khao

Cac repo duoc nhac trong tai lieu nhu `iluwatar/java-design-patterns`, `Head-First-Design-Patterns`, va cac POS JavaFX nen dung de tham khao cach viet class nho, ro vai tro. Khong nen copy kien truc y nguyen vi project minh can giai quyet nghiep vu Coffee Shop POS cu the.

Nguyen tac ap dung:

- Lay tinh than pattern tu repo tham khao.
- Dat pattern vao dung bai toan cua app.
- Giu service layer dieu phoi nghiep vu.
- Khong de UI chua business logic.

## Ket luan

So voi tai lieu tham khao, project hien tai khong bi thieu phan cot loi. Nhung diem can nhan manh khi bao ve:

1. App khong dung DB that vi muc tieu la demo pattern, nhung co schema SQL va Singleton DBConnection san sang mo rong.
2. Payment dung Adapter thay vi Strategy vi MoMo/VNPay la cong ngoai.
3. Strategy duoc dung cho discount, noi co nhieu thuat toan tinh tien thay the nhau.
4. State va Inventory lam app thuc te hon mot demo pattern thong thuong.
5. UI dung Swing de de chay, khong anh huong chat luong kien truc.
