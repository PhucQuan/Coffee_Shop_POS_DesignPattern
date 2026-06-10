# Coffee Shop POS - Design Pattern Mapping

File nay dung de thuyet trinh nhanh: moi pattern giai quyet van de gi trong POS, code nam o dau, va demo bang thao tac nao.

## Tong quan

| Pattern | Trang thai | Ap dung trong project | Package/Class chinh | Cach demo |
| --- | --- | --- | --- | --- |
| Decorator | Da co | Tuy bien topping/size cho thuc uong | `domain.patterns.decorator` | Chon Ca phe sua, tick Pearl + Size L, gia tang dung |
| Strategy | Da co | Thay doi thuat toan khuyen mai/giam gia | `domain.patterns.strategy` | Chon No discount/10%/VIP/Buy 1 Get 1 |
| State | Da co them | Vong doi don hang | `domain.patterns.state` | Pending -> Preparing -> Ready -> Paid |
| Observer | Da co | Thong bao thay doi trang thai don cho man hinh/lop logger | `domain.patterns.observer` | Send to kitchen/Mark ready, quan sat notification/log |
| Factory Method | Da co | Tao beverage theo danh muc menu | `domain.patterns.factory` | Refresh/menu CRUD tao do uong theo category |
| Singleton | Da co | Dung chung cau hinh app va ket noi DB demo | `AppConfig`, `DatabaseConnection` | Mo code, goi `getInstance()` |
| Adapter | Da co them | Chuan hoa cong thanh toan MoMo/VNPay | `domain.patterns.adapter` | Bam Pay Momo/Pay VNPay, nhan transaction code |

## Doi chieu voi 5 goi y tham khao

### 1. Decorator Pattern - topping va size

Project da co dung tinh than textbook:

- Component: `Beverage`
- Concrete components: `BaseCoffee`, `MilkTea`, `Matcha`
- Decorator base: `BeverageDecorator`
- Concrete decorators: `PearlDecorator`, `ExtraShotDecorator`, `LargeSizeDecorator`, `MilkDecorator`

Trong bao cao co the noi:

> Decorator duoc dung de cong them topping/size vao thuc uong goc ma khong tao class cho tung to hop san pham.

### 2. Strategy Pattern - khuyen mai

Doan tham khao goi y dung Strategy cho thanh toan. Project hien tai dung Strategy cho khuyen mai, con thanh toan dung Adapter. Cach nay hop ly hon voi POS vi:

- Khuyen mai la cac thuat toan thay the nhau: khong giam, giam %, VIP, mua 1 tang 1.
- Cong thanh toan MoMo/VNPay la he thong ngoai, nen dung Adapter de dua ve cung interface `PaymentGateway`.

Trong bao cao nen trinh bay:

> Strategy duoc dung cho module giam gia vi he thong can thay doi cong thuc tinh discount linh hoat ma khong sua `OrderService`.

### 3. Factory Method Pattern - tao beverage theo danh muc

Project da co Factory Method, nhung dung cho tao do uong thay vi tao hoa don:

- `BeverageFactory`
- `CoffeeFactory`
- `TeaFactory`
- `MatchaFactory`
- `SmoothieFactory`

Huong nay van dung bai toan POS, vi UI/Admin chi can chon category, service se giao viec tao doi tuong cho factory tuong ung.

### 4. Observer Pattern - dong bo trang thai order

Project da co:

- Subject/publisher: `OrderEventPublisher`
- Observers: `CashierScreen`, `KitchenScreen`, `ReportLogger`

Khi trang thai don thay doi, service goi publisher de thong bao cho cac observer. Day la diem thuyet trinh tot vi gan truc tiep voi luong cashier -> kitchen -> cashier.

### 5. Singleton Pattern - cau hinh va DB connection

Project da co:

- `DatabaseConnection`: mo phong ket noi DB dung chung.
- `AppConfig`: cau hinh ung dung dung chung.

Trong demo co the mo code cho giang vien xem `private constructor`, `static instance`, va `getInstance()`.

## Pattern them de an diem

### State Pattern

Day la pattern nen nhan manh nhat sau Decorator, vi no lam don hang "co doi song" that:

- `PendingState`: duoc them/xoa mon, gui bep, huy.
- `PreparingState`: khong cho sua mon.
- `ReadyState`: cho thanh toan.
- `PaidState`: khoa thao tac.
- `CancelledState`: khoa thao tac.

Neu thao tac sai, code nem `InvalidStateTransitionException` voi message ro rang. Day la diem cong lon vi demo duoc tinh dung sai nghiep vu.

### Adapter Pattern

Thanh toan MoMo/VNPay nen giu o Adapter:

- POS chi goi `PaymentGateway.processPayment(amount)`.
- Tung adapter an chi tiet sinh ma giao dich va ket qua thanh toan.
- Sau nay them ZaloPay/BankQR chi can tao adapter moi.

## Ket luan thuyet trinh

Project hien tai khong chi co 5 pattern tham khao, ma co 7 pattern ro rang:

1. Decorator - topping/size.
2. Strategy - khuyen mai.
3. State - vong doi don hang.
4. Observer - thong bao order.
5. Factory Method - tao beverage.
6. Singleton - cau hinh/DB connection.
7. Adapter - cong thanh toan.

Huong nay sach hon viec ep tat ca vi du tham khao vao dung y chang, vi moi pattern duoc gan voi mot van de nghiep vu that cua Coffee Shop POS.
