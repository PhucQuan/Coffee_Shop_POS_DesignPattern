# Coffee Shop POS - Product Completion Plan

Muc tieu cua file nay la bien project tu demo pattern thanh mot app POS nhin co dau cuoi, co flow ro, co UI du thuyet phuc khi demo cuoi ky. Khong lam tat ca mot luc. Uu tien nhung phan thay ro tren man hinh va giup giai thich Design Pattern tot hon.

## Nguyen tac lam tiep

- Khong copy UI/code/assets tu repo tham khao.
- Khong doi sang JavaFX neu khong bat buoc, vi Swing hien tai de chay hon.
- Khong dua business logic vao View.
- Moi man hinh chi goi Service.
- Moi cai tien UI phai giu pattern demo duoc: Decorator, Strategy, State, Observer, Factory, Singleton, Adapter.
- Uu tien "nhin nhu app that" nhung code van don gian, sach, de thuyet trinh.

## Phase 1 - Login va App Shell

Muc tieu: mo app len da co cam giac la mot san pham hoan chinh, khong phai form Java mac dinh.

Status: login screen completed on 2026-06-10; shared app shell/header/sidebar still pending.

### Viec can lam

- Tao login screen co brand ro: `PurrCoffee POS` hoac `Coffee Shop POS`.
- Tach trai/phai:
  - Ben trai: logo, ten quan, tagline ngan.
  - Ben phai: form login.
- Them role hint nho: `admin / cashier01 / kitchen01`, password demo `123`.
- Them validation UI:
  - Khong nhap username/password thi bao loi ngay.
  - Sai tai khoan thi hien message dep, khong dung dialog mac dinh tho.
- Sau login, dieu huong vao dung man hinh:
  - ADMIN -> Admin dashboard.
  - CASHIER -> POS cashier.
  - KITCHEN -> Kitchen board.
- Them nut `Exit`.
- Done: branded two-column login, inline validation, role hints, Java2D coffee visual, Exit action.

### Acceptance criteria

- Login nhin nhu man hinh chao cua app.
- Khong thay UI mac dinh qua tho.
- AuthService van la noi xu ly login.
- Test TC01 van pass.

## Phase 2 - Main Navigation / App Shell Chung

Muc tieu: moi role vao app deu co header/sidebar nhat quan.

Status: shared AppShell header/sidebar/logout completed on 2026-06-10; deeper per-screen navigation actions can be improved later.

### Viec can lam

- Tao style chung trong `AppTheme`:
  - Mau chinh: coffee brown + cream + green/accent.
  - Button primary/secondary/danger.
  - Font size ro rang.
  - Border, spacing dong nhat.
- Tao header chung:
  - Ten app.
  - Role dang dang nhap.
  - Ngay/gio hien tai.
  - Nut logout.
- Sidebar theo role:
  - Cashier: POS, Orders, Receipt.
  - Kitchen: Queue, Ready, History.
  - Admin: Overview, Orders, History, Menu, Toppings, Inventory, Users, Reports.
- Done: `AppShell` wraps POS/Kitchen/Admin with consistent brand sidebar, role header, date, and Logout.

### Acceptance criteria

- Chuyen tab/man hinh khong roi rac.
- Cac view co cung visual language.
- Logout quay ve LoginView.

## Phase 3 - Cashier POS Screen

Muc tieu: day la man hinh quan trong nhat khi demo, can nhin giong POS that va thao tac nhanh.

Status: baseline polish completed on 2026-06-10; menu is now card-grid based, cart/actions are clearer. Payment dialog and custom cart rows remain future polish.

### Viec can lam

- Menu area:
  - Search bar ro.
  - Category chips: All, Coffee, Tea, Matcha, Smoothie.
  - Menu card co icon do uong, ten, gia, category.
  - Hien badge `Available` hoac `Low stock`.
- Done: search, category toggles, card-style menu with generated icons, price, category, and add button.
- Order/cart area:
  - Hien danh sach item theo dong.
  - Moi dong co ten mon, topping, so luong, gia.
  - Nut tang/giam/xoa item.
  - Hien subtotal, discount, final total.
- Topping/options:
  - Checkbox/segmented control cho Pearl, Size L, Extra shot.
  - Them note cho mon.
- Checkout:
  - Discount dropdown.
  - Payment method: Cash, Momo, VNPay.
  - VNPay/Momo hien QR simulation trong receipt/payment dialog.
- Hotkeys:
  - F1 add item.
  - F2 pay Momo.
  - F3 pay VNPay.
  - Esc clear selection.

### Pattern evidence

- Decorator: topping/size.
- Strategy: discount.
- Adapter: payment gateway.
- State: chi cho pay khi order Ready.

### Acceptance criteria

- Thu ngan co the tao order, them topping, ap discount, gui bep, thanh toan.
- UI khong bi chen chu/overlap.
- Tat ca action goi `OrderService`, `PaymentService`, `MenuService`.

## Phase 4 - Kitchen Board

Muc tieu: man hinh pha che nhin nhu queue board, khong phai list text don gian.

### Viec can lam

- Chia cot:
  - New/Pending.
  - Preparing.
  - Ready.
- Moi order la card:
  - Order number.
  - Thoi gian tao.
  - Danh sach mon.
  - Note/topping.
  - Trang thai.
- Nut thao tac:
  - Receive order.
  - Mark ready.
- Canh bao:
  - Don qua 15 phut doi mau warning.
- Auto refresh nhe hoac refresh button ro.

### Pattern evidence

- Observer: kitchen nhan thong bao order moi/status change.
- State: Pending -> Preparing -> Ready.
- Inventory: tru kho khi vao Preparing.

### Acceptance criteria

- KitchenView co the demo State Pattern rat ro.
- Thao tac sai hien message ro rang tu `InvalidStateTransitionException`.

## Phase 5 - Admin Dashboard

Muc tieu: admin nhin duoc tong quan quan cafe va quan ly du lieu co ban.

### Viec can lam

- Overview:
  - Total orders.
  - Revenue.
  - Pending orders.
  - Top item.
  - Chart doanh thu/top selling.
- Orders:
  - Bang active orders.
  - Xem chi tiet order.
  - Cancel/mark ready neu hop le.
- History:
  - Loc theo status.
  - Loc theo ngay demo.
  - Xem payment transaction.
- Menu:
  - CRUD beverage.
  - Active/inactive.
  - Category.
- Toppings:
  - CRUD topping.
- Inventory:
  - Ton kho.
  - Low stock highlight.
  - Manual restock.
- Users:
  - Add user.
  - Lock/unlock user.
  - Role.

### Pattern evidence

- Factory Method: tao beverage theo category.
- Singleton: AppConfig/DatabaseConnection.
- Service layer: AdminView khong chua business rule.

### Acceptance criteria

- Admin co du cam giac back-office.
- Moi CRUD co validation trong Service.
- Khong cho duplicate/price am/name rong.

## Phase 6 - Receipt va Payment Dialog

Muc tieu: bill/thanh toan nhin thuyet phuc hon khi demo.

### Viec can lam

- Payment dialog:
  - Amount.
  - Method.
  - QR simulation cho Momo/VNPay.
  - Loading state khi xu ly payment async.
  - Success/fail result.
- Receipt preview:
  - Ten quan.
  - Order ID.
  - Items + topping.
  - Subtotal/discount/final total.
  - Payment method + transaction code.
  - QR code.
  - Export PNG.

### Pattern evidence

- Adapter: payment gateway.
- Async payment: khong lam freeze UI.

### Acceptance criteria

- Co the demo thanh toan xong mo receipt preview va export PNG.
- Payment fail khong chuyen order sang Paid.

## Phase 7 - Data va Persistence

Muc tieu: chuan bi cau tra loi khi giang vien hoi "co database khong?".

### Viec can lam

- Giu in-memory la default de demo chay ngay.
- Hoan thien `schema.sql`:
  - Users.
  - Beverages.
  - Toppings.
  - Orders.
  - Order items.
  - Payments.
  - Inventory.
  - Recipes.
- Them repository interface:
  - `UserRepository`.
  - `MenuRepository`.
  - `OrderRepository`.
  - `InventoryRepository`.
- Optional:
  - SQLite repository neu con thoi gian.

### Acceptance criteria

- Bao cao giai thich duoc runtime demo dung in-memory, nhung DB schema da san sang.
- Khong can cai MySQL de chay demo.

## Phase 8 - Tests va Demo Script

Muc tieu: co bang chung code chay dung, pattern dung.

### Viec can lam

- Giu `test.bat` pass.
- Them test:
  - Login roles.
  - Decorator topping price.
  - Strategy discount.
  - State valid/invalid transition.
  - Observer notify.
  - Adapter payment success/fail.
  - Admin CRUD validation.
  - Inventory deduct/rollback.
  - User add/lock.
- Tao demo script:
  1. Login cashier.
  2. Tao order co topping.
  3. Ap discount.
  4. Send to kitchen.
  5. Login/open kitchen, mark ready.
  6. Pay VNPay/Momo.
  7. Receipt preview.
  8. Admin xem report/inventory/history.

### Acceptance criteria

- Test pass truoc khi demo.
- Demo script di duoc tu dau den cuoi trong 5-7 phut.

## Phase 9 - Bao cao va UML

Muc tieu: bao cao khop 100% voi code that.

### Viec can lam

- Cap nhat class diagram theo package hien tai.
- Cap nhat sequence diagram:
  - Login.
  - Add item with topping.
  - Send order to kitchen.
  - Payment.
  - Admin CRUD.
- Them pattern evidence table:
  - Pattern.
  - Problem.
  - Class.
  - Demo step.
- Them screenshots:
  - Login.
  - POS.
  - Kitchen.
  - Admin Overview.
  - Receipt.

### Acceptance criteria

- Moi pattern trong bao cao co class that trong source code.
- Use case/UML khong noi qua kha nang app.

## Uu tien lam truoc

Nen lam theo thu tu nay:

1. Phase 1 - Login va App Shell.
2. Phase 3 - Cashier POS polish.
3. Phase 4 - Kitchen Board.
4. Phase 5 - Admin Dashboard polish.
5. Phase 6 - Receipt/Payment dialog.
6. Phase 8/9 - Demo script, screenshots, report alignment.

## OpenGSD phase loop

Ke hoach nay nen duoc thuc hien theo vong lap cua `open-gsd/gsd-core`:

```text
Discuss -> UI design -> Plan -> Execute -> Verify -> Ship
```

Chi tiet cach ap dung cho project nam trong `docs/OPEN_GSD_CORE_REFERENCE.md`.

## Definition of Done chung

- `test.bat` pass.
- App chay duoc bang `run.bat`.
- Khong co business logic moi trong View.
- Khong them dependency phuc tap neu khong can.
- Giao dien khong overlap tren man hinh 1366x768.
- Demo duoc it nhat 5 pattern bang thao tac UI.
