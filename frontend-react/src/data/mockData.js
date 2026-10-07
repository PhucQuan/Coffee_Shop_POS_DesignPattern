// Dữ liệu đồng bộ chuẩn 100% từ SQLite Database pos_data.db gốc của Bản Desktop App

export const DEFAULT_USERS = [
  {
    "id": 1,
    "username": "admin",
    "password": "123",
    "role": "ADMIN",
    "active": true
  },
  {
    "id": 2,
    "username": "cashier01",
    "password": "123",
    "role": "CASHIER",
    "active": true
  },
  {
    "id": 3,
    "username": "kitchen01",
    "password": "123",
    "role": "KITCHEN",
    "active": true
  }
];

export const DEFAULT_MENU = [
  {
    "id": 1,
    "name": "Cà phê sữa",
    "price": 30000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/ca-phe-sua.png",
    "desc": "Ca phe sua hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 2,
    "name": "Bạc xỉu",
    "price": 32000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/bac-xiu.png",
    "desc": "Bac xiu hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 3,
    "name": "Trà đào cam sả",
    "price": 35000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-dao.png",
    "desc": "Tra dao hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 4,
    "name": "Trà sữa truyền thống",
    "price": 38000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-sua.png",
    "desc": "Tra sua hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 5,
    "name": "Matcha Latte",
    "price": 42000,
    "category": "MATCHA",
    "active": true,
    "img": "/assets/drinks/matcha-latte.png",
    "desc": "Matcha latte hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 6,
    "name": "Sinh tố xoài",
    "price": 45000,
    "category": "SMOOTHIE",
    "active": true,
    "img": "/assets/drinks/sinh-to-xoai.png",
    "desc": "Sinh to xoai hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 7,
    "name": "Espresso",
    "price": 28000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/espresso.png",
    "desc": "Espresso hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 8,
    "name": "Americano",
    "price": 30000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/americano.png",
    "desc": "Americano hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 9,
    "name": "Latte",
    "price": 42000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/latte.png",
    "desc": "Latte hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 10,
    "name": "Cappuccino",
    "price": 42000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/cappuccino.png",
    "desc": "Cappuccino hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 11,
    "name": "Cold Brew",
    "price": 45000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/cold-brew.png",
    "desc": "Cold brew hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 12,
    "name": "Trà vải lài",
    "price": 39000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-vai.png",
    "desc": "Tra vai hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 13,
    "name": "Trà tắc mật ong",
    "price": 34000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-tac-mat-ong.png",
    "desc": "Tra tac mat ong hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 14,
    "name": "Matcha đá xay",
    "price": 52000,
    "category": "MATCHA",
    "active": true,
    "img": "/assets/drinks/matcha-da-xay.png",
    "desc": "Matcha da xay hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 15,
    "name": "Sinh tố dâu tây",
    "price": 48000,
    "category": "SMOOTHIE",
    "active": true,
    "img": "/assets/drinks/sinh-to-dau.png",
    "desc": "Sinh to dau hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 16,
    "name": "Cacao nóng",
    "price": 36000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/cacao-nong.png",
    "desc": "Cacao nong hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 17,
    "name": "Vanilla Latte",
    "price": 46000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/latte.png",
    "desc": "Vanilla latte hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 18,
    "name": "Caramel Macchiato",
    "price": 49000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/cappuccino.png",
    "desc": "Caramel macchiato hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 19,
    "name": "Mocha",
    "price": 47000,
    "category": "COFFEE",
    "active": true,
    "img": "/assets/drinks/ca-phe-sua.png",
    "desc": "Mocha hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 20,
    "name": "Hồng trà sữa",
    "price": 39000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-sua.png",
    "desc": "Hong tra sua hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 21,
    "name": "Ô long sữa",
    "price": 41000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-sua.png",
    "desc": "Oolong sua hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 22,
    "name": "Trà sen vàng",
    "price": 42000,
    "category": "TEA",
    "active": true,
    "img": "/assets/drinks/tra-dao.png",
    "desc": "Tra sen vang hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 23,
    "name": "Matcha Cream Cheese",
    "price": 55000,
    "category": "MATCHA",
    "active": true,
    "img": "/assets/drinks/matcha-latte.png",
    "desc": "Matcha cream cheese hảo hạng chuẩn công thức pha chế PurrCoffee."
  },
  {
    "id": 24,
    "name": "Sinh tố bơ",
    "price": 52000,
    "category": "SMOOTHIE",
    "active": true,
    "img": "/assets/drinks/sinh-to-xoai.png",
    "desc": "Sinh to bo hảo hạng chuẩn công thức pha chế PurrCoffee."
  }
];

export const DEFAULT_TOPPINGS = [
  {
    "id": 1,
    "name": "Trân châu trắng",
    "price": 10000,
    "active": true
  },
  {
    "id": 2,
    "name": "Pudding",
    "price": 9000,
    "active": true
  },
  {
    "id": 3,
    "name": "Kem cheese",
    "price": 12000,
    "active": true
  },
  {
    "id": 4,
    "name": "Extra shot",
    "price": 8000,
    "active": true
  },
  {
    "id": 5,
    "name": "Kem muối",
    "price": 7000,
    "active": true
  },
  {
    "id": 6,
    "name": "Thach cafe",
    "price": 9000,
    "active": true
  },
  {
    "id": 7,
    "name": "Kem vani",
    "price": 11000,
    "active": true
  },
  {
    "id": 8,
    "name": "Trân châu đường đen",
    "price": 6000,
    "active": true
  }
];
export const TOPPINGS = DEFAULT_TOPPINGS;

export const DEFAULT_INVENTORY = [
  {
    "id": 1,
    "name": "Coffee beans",
    "unit": "g",
    "quantity": 5000.0,
    "min": 500.0
  },
  {
    "id": 2,
    "name": "Fresh milk",
    "unit": "ml",
    "quantity": 10000.0,
    "min": 1000.0
  },
  {
    "id": 3,
    "name": "Tea leaves",
    "unit": "g",
    "quantity": 3000.0,
    "min": 300.0
  },
  {
    "id": 4,
    "name": "Peach syrup",
    "unit": "ml",
    "quantity": 2500.0,
    "min": 300.0
  },
  {
    "id": 5,
    "name": "Matcha powder",
    "unit": "g",
    "quantity": 2000.0,
    "min": 200.0
  },
  {
    "id": 6,
    "name": "Mango",
    "unit": "g",
    "quantity": 5000.0,
    "min": 500.0
  },
  {
    "id": 7,
    "name": "Pearl",
    "unit": "g",
    "quantity": 4000.0,
    "min": 400.0
  },
  {
    "id": 8,
    "name": "Cup L",
    "unit": "pcs",
    "quantity": 300.0,
    "min": 30.0
  },
  {
    "id": 9,
    "name": "Cream cheese",
    "unit": "g",
    "quantity": 2500.0,
    "min": 250.0
  },
  {
    "id": 10,
    "name": "Avocado",
    "unit": "g",
    "quantity": 4500.0,
    "min": 450.0
  },
  {
    "id": 11,
    "name": "Brown sugar syrup",
    "unit": "ml",
    "quantity": 3000.0,
    "min": 300.0
  },
  {
    "id": 12,
    "name": "Vanilla syrup",
    "unit": "ml",
    "quantity": 2500.0,
    "min": 250.0
  },
  {
    "id": 13,
    "name": "Pudding",
    "unit": "g",
    "quantity": 3000.0,
    "min": 300.0
  },
  {
    "id": 14,
    "name": "Coffee jelly",
    "unit": "g",
    "quantity": 3000.0,
    "min": 300.0
  },
  {
    "id": 15,
    "name": "Vanilla cream",
    "unit": "g",
    "quantity": 2500.0,
    "min": 250.0
  },
  {
    "id": 16,
    "name": "Salted cream",
    "unit": "g",
    "quantity": 2500.0,
    "min": 250.0
  }
];
export const INVENTORY_DATA = DEFAULT_INVENTORY;

export const DEFAULT_RECIPES = [
  {
    "beverageId": 1,
    "beverageName": "Ca phe sua",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 18.0,
    "unit": "g"
  },
  {
    "beverageId": 2,
    "beverageName": "Bac xiu",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 15.0,
    "unit": "g"
  },
  {
    "beverageId": 2,
    "beverageName": "Bac xiu",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 80.0,
    "unit": "ml"
  },
  {
    "beverageId": 3,
    "beverageName": "Tra dao",
    "inventoryId": 3,
    "inventoryName": "Tea leaves",
    "quantityRequired": 8.0,
    "unit": "g"
  },
  {
    "beverageId": 3,
    "beverageName": "Tra dao",
    "inventoryId": 4,
    "inventoryName": "Peach syrup",
    "quantityRequired": 40.0,
    "unit": "ml"
  },
  {
    "beverageId": 4,
    "beverageName": "Tra sua",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 100.0,
    "unit": "ml"
  },
  {
    "beverageId": 4,
    "beverageName": "Tra sua",
    "inventoryId": 3,
    "inventoryName": "Tea leaves",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 5,
    "beverageName": "Matcha latte",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 120.0,
    "unit": "ml"
  },
  {
    "beverageId": 5,
    "beverageName": "Matcha latte",
    "inventoryId": 5,
    "inventoryName": "Matcha powder",
    "quantityRequired": 12.0,
    "unit": "g"
  },
  {
    "beverageId": 6,
    "beverageName": "Sinh to xoai",
    "inventoryId": 6,
    "inventoryName": "Mango",
    "quantityRequired": 150.0,
    "unit": "g"
  },
  {
    "beverageId": 7,
    "beverageName": "Espresso",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 18.0,
    "unit": "g"
  },
  {
    "beverageId": 8,
    "beverageName": "Americano",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 16.0,
    "unit": "g"
  },
  {
    "beverageId": 9,
    "beverageName": "Latte",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 18.0,
    "unit": "g"
  },
  {
    "beverageId": 9,
    "beverageName": "Latte",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 120.0,
    "unit": "ml"
  },
  {
    "beverageId": 10,
    "beverageName": "Cappuccino",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 18.0,
    "unit": "g"
  },
  {
    "beverageId": 10,
    "beverageName": "Cappuccino",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 120.0,
    "unit": "ml"
  },
  {
    "beverageId": 11,
    "beverageName": "Cold brew",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 22.0,
    "unit": "g"
  },
  {
    "beverageId": 12,
    "beverageName": "Tra vai",
    "inventoryId": 3,
    "inventoryName": "Tea leaves",
    "quantityRequired": 8.0,
    "unit": "g"
  },
  {
    "beverageId": 12,
    "beverageName": "Tra vai",
    "inventoryId": 4,
    "inventoryName": "Peach syrup",
    "quantityRequired": 20.0,
    "unit": "ml"
  },
  {
    "beverageId": 13,
    "beverageName": "Tra tac mat ong",
    "inventoryId": 3,
    "inventoryName": "Tea leaves",
    "quantityRequired": 8.0,
    "unit": "g"
  },
  {
    "beverageId": 14,
    "beverageName": "Matcha da xay",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 100.0,
    "unit": "ml"
  },
  {
    "beverageId": 14,
    "beverageName": "Matcha da xay",
    "inventoryId": 5,
    "inventoryName": "Matcha powder",
    "quantityRequired": 15.0,
    "unit": "g"
  },
  {
    "beverageId": 15,
    "beverageName": "Sinh to dau",
    "inventoryId": 6,
    "inventoryName": "Mango",
    "quantityRequired": 120.0,
    "unit": "g"
  },
  {
    "beverageId": 16,
    "beverageName": "Cacao nong",
    "inventoryId": 2,
    "inventoryName": "Fresh milk",
    "quantityRequired": 150.0,
    "unit": "ml"
  },
  {
    "beverageId": 17,
    "beverageName": "Vanilla latte",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 18,
    "beverageName": "Caramel macchiato",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 19,
    "beverageName": "Mocha",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 20,
    "beverageName": "Hong tra sua",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 21,
    "beverageName": "Oolong sua",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 22,
    "beverageName": "Tra sen vang",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 23,
    "beverageName": "Matcha cream cheese",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  },
  {
    "beverageId": 24,
    "beverageName": "Sinh to bo",
    "inventoryId": 1,
    "inventoryName": "Coffee beans",
    "quantityRequired": 10.0,
    "unit": "g"
  }
];

export const INITIAL_INVENTORY_LOGS = [
  { id: 1, time: "08:00 06/10", item: "Coffee beans", delta: "+5000 g", balance: "5000 g", reason: "Nhập kho đầu ca" },
  { id: 2, time: "08:05 06/10", item: "Fresh milk", delta: "+10000 ml", balance: "10000 ml", reason: "Nhập kho đầu ca" },
  { id: 3, time: "09:15 06/10", item: "Coffee beans", delta: "-36 g", balance: "4964 g", reason: "Pha chế Đơn #1001" },
  { id: 4, time: "09:30 06/10", item: "Fresh milk", delta: "-80 ml", balance: "9920 ml", reason: "Pha chế Đơn #1001" }
];

export const INITIAL_ORDERS = [
  {
    id: 1001,
    createdAt: "10:15",
    orderDate: "2026-10-07",
    items: [
      { name: "Ca phe sua", size: "L", toppings: ["Trân châu trắng"], note: "Nhiều đá, ít ngọt", qty: 2, price: 50000 },
      { name: "Tra dao", size: "M", toppings: [], note: "", qty: 1, price: 35000 }
    ],
    subtotal: 135000,
    discountAmount: 13500,
    discountType: "PERCENT_10",
    total: 121500,
    status: "PREPARING",
    paymentMethod: "VNPAY",
    orderType: "DINE_IN"
  },
  {
    id: 1002,
    createdAt: "10:25",
    orderDate: "2026-10-07",
    items: [
      { name: "Matcha latte", size: "M", toppings: ["Kem cheese"], note: "Ít ngọt", qty: 1, price: 54000 }
    ],
    subtotal: 54000,
    discountAmount: 0,
    discountType: "NONE",
    total: 54000,
    status: "PENDING",
    paymentMethod: "MOMO",
    orderType: "TAKE_AWAY"
  }
];

export function formatVND(amount) {
  return new Intl.NumberFormat('vi-VN').format(Math.max(0, amount)) + ' ₫';
}
