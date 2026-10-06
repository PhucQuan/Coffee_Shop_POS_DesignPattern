import json

with open('full_sqlite_data.json', 'r', encoding='utf-8') as f:
    d = json.load(f)

img_map = {
    1: '/assets/drinks/ca-phe-sua.png',
    2: '/assets/drinks/bac-xiu.png',
    3: '/assets/drinks/tra-dao.png',
    4: '/assets/drinks/tra-sua.png',
    5: '/assets/drinks/matcha-latte.png',
    6: '/assets/drinks/sinh-to-xoai.png',
    7: '/assets/drinks/espresso.png',
    8: '/assets/drinks/americano.png',
    9: '/assets/drinks/latte.png',
    10: '/assets/drinks/cappuccino.png',
    11: '/assets/drinks/cold-brew.png',
    12: '/assets/drinks/tra-vai.png',
    13: '/assets/drinks/tra-tac-mat-ong.png',
    14: '/assets/drinks/matcha-da-xay.png',
    15: '/assets/drinks/sinh-to-dau.png',
    16: '/assets/drinks/cacao-nong.png',
    17: '/assets/drinks/latte.png',
    18: '/assets/drinks/cappuccino.png',
    19: '/assets/drinks/ca-phe-sua.png',
    20: '/assets/drinks/tra-sua.png',
    21: '/assets/drinks/tra-sua.png',
    22: '/assets/drinks/tra-dao.png',
    23: '/assets/drinks/matcha-latte.png',
    24: '/assets/drinks/sinh-to-xoai.png',
}

js_content = f"""// Dữ liệu đồng bộ chuẩn 100% từ SQLite Database pos_data.db gốc của Bản Desktop App

export const DEFAULT_USERS = {json.dumps(d['users'], ensure_ascii=False, indent=2)};

export const DEFAULT_MENU = {json.dumps([
    {
        **b,
        'img': img_map.get(b['id'], '/assets/drinks/ca-phe-sua.png'),
        'desc': f"{b['name']} hảo hạng chuẩn công thức pha chế PurrCoffee."
    } for b in d['beverages']
], ensure_ascii=False, indent=2)};

export const DEFAULT_TOPPINGS = {json.dumps(d['toppings'], ensure_ascii=False, indent=2)};
export const TOPPINGS = DEFAULT_TOPPINGS;

export const DEFAULT_INVENTORY = {json.dumps(d['inventory'], ensure_ascii=False, indent=2)};
export const INVENTORY_DATA = DEFAULT_INVENTORY;

export const DEFAULT_RECIPES = {json.dumps(d['recipes'], ensure_ascii=False, indent=2)};

export const INITIAL_INVENTORY_LOGS = [
  {{ id: 1, time: "08:00 06/10", item: "Coffee beans", delta: "+5000 g", balance: "5000 g", reason: "Nhập kho đầu ca" }},
  {{ id: 2, time: "08:05 06/10", item: "Fresh milk", delta: "+10000 ml", balance: "10000 ml", reason: "Nhập kho đầu ca" }},
  {{ id: 3, time: "09:15 06/10", item: "Coffee beans", delta: "-36 g", balance: "4964 g", reason: "Pha chế Đơn #1001" }},
  {{ id: 4, time: "09:30 06/10", item: "Fresh milk", delta: "-80 ml", balance: "9920 ml", reason: "Pha chế Đơn #1001" }}
];

export const INITIAL_ORDERS = [
  {{
    id: 1001,
    createdAt: "10:15",
    items: [
      {{ name: "Ca phe sua", size: "L", toppings: ["Trân châu trắng"], note: "Nhiều đá, ít ngọt", qty: 2, price: 50000 }},
      {{ name: "Tra dao", size: "M", toppings: [], note: "", qty: 1, price: 35000 }}
    ],
    subtotal: 135000,
    discountAmount: 13500,
    discountType: "PERCENT_10",
    total: 121500,
    status: "PREPARING",
    paymentMethod: "VNPAY",
    orderType: "DINE_IN"
  }},
  {{
    id: 1002,
    createdAt: "10:25",
    items: [
      {{ name: "Matcha latte", size: "M", toppings: ["Kem cheese"], note: "Ít ngọt", qty: 1, price: 54000 }}
    ],
    subtotal: 54000,
    discountAmount: 0,
    discountType: "NONE",
    total: 54000,
    status: "PENDING",
    paymentMethod: "MOMO",
    orderType: "TAKE_AWAY"
  }}
];

export function formatVND(amount) {{
  return new Intl.NumberFormat('vi-VN').format(Math.max(0, amount)) + ' ₫';
}}
"""

with open('frontend-react/src/data/mockData.js', 'w', encoding='utf-8') as f:
    f.write(js_content)

print("Updated frontend-react/src/data/mockData.js successfully with SQLite data!")
