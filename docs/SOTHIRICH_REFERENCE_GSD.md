# Sothirich CoffeeShopSystemManagement - Reference Decision

Reference repo: `Sothirich/CoffeeShopSystemManagement`.

Muc tieu: dung repo nay de tham khao luong man hinh va do "giong app that", khong copy code/UI/assets.

## Nhung diem hoc tu repo tham khao

- Welcome/login flow ro rang.
- Customer/cashier order flow co cart va order summary.
- Admin dashboard gom sales, customers, earning.
- Accept/Pending order flow.
- Update drink.
- Check history.
- Add user/change password.
- MySQL schema rieng.

## Quyet dinh ap dung vao project nay

| Reference feature | Project decision | Ly do |
| --- | --- | --- |
| JavaFX UI | Khong doi framework, tiep tuc Swing | De chay bang JDK thuong, phu hop demo Design Pattern |
| Customer self-order | Khong uu tien | De tai POS tap trung cashier/kitchen/admin |
| Admin overview | Da bo sung | Lam app giong he thong quan ly hon |
| Accept/Pending order | Da bo sung trong Admin Orders + KitchenView | Gan voi State/Observer |
| Update drink | Da co trong Admin Menu | Gan voi Factory/MenuService |
| Check history | Da bo sung tab History | Can cho demo back-office |
| Add user | Da bo sung UserService + Admin Users | Hoan thien role management |
| MySQL | Giu `schema.sql`, runtime van in-memory | Demo chay nhanh, van co du thiet ke DB |

## Mapping theo BMAD/GSD

### Brief

Coffee Shop POS la app Java Swing demo Design Patterns trong nghiep vu ban cafe. App can du hoan chinh de thuyet trinh, nhung core value la clean code va pattern evidence.

### Architecture

- `presentation`: login, POS, kitchen, admin.
- `service`: order/menu/payment/report/inventory/user.
- `domain`: entities va pattern implementations.
- `infrastructure`: repository va DB connection demo.

### Backlog uu tien

1. Hoan thien back-office: overview, order queue, history, users. Done.
2. Giu pattern core on dinh: Decorator, Strategy, State, Observer, Factory, Singleton, Adapter. Done.
3. Tang chat luong demo: receipt PNG, QR simulation, inventory report. Done.
4. Neu con thoi gian: them screenshot vao bao cao va ve lai UML theo code that.

### Acceptance criteria

- `test.bat` pass.
- AdminView co Overview, Orders, History, Menu, Topping, Inventory, Users, Revenue report.
- UserService quan ly user, khong de AdminView sua truc tiep repository.
- Order actions trong Admin/Kitchen di qua OrderService de State/Observer/Inventory van duoc kich hoat.
- Report/docs ghi ro vi sao khong copy JavaFX/MySQL tu repo tham khao.

## Nhung viec khong nen lam luc nay

- Khong migrate sang JavaFX neu deadline gan, vi se doi build/dependency va khong tang diem pattern nhieu.
- Khong copy anh/assets tu repo tham khao.
- Khong dua SQL connection that vao UI.
- Khong tao them qua nhieu pattern chi de "cho nhieu", vi se lam bao cao kho bao ve.
