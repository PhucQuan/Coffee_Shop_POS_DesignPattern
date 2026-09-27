export const DEFAULT_MENU = [
  { id: 1, name: "Cà phê sữa", category: "COFFEE", price: 30000, img: "/assets/drinks/ca-phe-sua.png", desc: "Cà phê Robusta Đắk Lắk pha phin truyền thống hòa quyện sữa đặc béo ngậy." },
  { id: 2, name: "Bạc xỉu", category: "COFFEE", price: 32000, img: "/assets/drinks/bac-xiu.png", desc: "Nhiều sữa ít cà phê, béo thơm ngọt dịu, phù hợp người thích vị nhẹ." },
  { id: 3, name: "Trà đào cam sả", category: "TEA", price: 35000, img: "/assets/drinks/tra-dao.png", desc: "Trà đen ủ lạnh kết hợp đào ngâm giòn ngọt và hương sả thơm mát." },
  { id: 4, name: "Trà sữa truyền thống", category: "TEA", price: 38000, img: "/assets/drinks/tra-sua.png", desc: "Hồng trà đậm vị kết hợp sữa tươi thanh trùng béo ngậy chuẩn vị quán." },
  { id: 5, name: "Matcha Latte", category: "MATCHA", price: 42000, img: "/assets/drinks/matcha-latte.png", desc: "Bột trà xanh Uji Nhật Bản nguyên chất hòa quyện sữa tươi hấp." },
  { id: 6, name: "Sinh tố xoài", category: "SMOOTHIE", price: 45000, img: "/assets/drinks/sinh-to-xoai.png", desc: "Xoài cát chín mọng tươi ngon xay cùng sữa chua mát lạnh giàu vitamin." },
  { id: 7, name: "Espresso", category: "COFFEE", price: 28000, img: "/assets/drinks/espresso.png", desc: "Chiết xuất nguyên chất áp suất cao, lớp crema dày óng ánh." },
  { id: 8, name: "Americano", category: "COFFEE", price: 30000, img: "/assets/drinks/americano.png", desc: "Espresso pha loãng với nước tinh khiết, thanh nhẹ không gắt." },
  { id: 9, name: "Latte", category: "COFFEE", price: 42000, img: "/assets/drinks/latte.png", desc: "Cà phê Ý nhẹ nhàng với tỷ lệ sữa tươi đánh bọt nghệ thuật." },
  { id: 10, name: "Cappuccino", category: "COFFEE", price: 42000, img: "/assets/drinks/cappuccino.png", desc: "Bọt sữa mịn màng rắc bột cacao thơm lừng phong cách Ý." },
  { id: 11, name: "Cold Brew", category: "COFFEE", price: 45000, img: "/assets/drinks/cold-brew.png", desc: "Cà phê ủ lạnh suốt 16 tiếng, vị thanh thoát mượt mà ít chua." },
  { id: 12, name: "Trà vải lài", category: "TEA", price: 39000, img: "/assets/drinks/tra-vai.png", desc: "Trà lài ngát hương kết hợp thịt vải ngọt lịm mọng nước." },
  { id: 13, name: "Trà tắc mật ong", category: "TEA", price: 34000, img: "/assets/drinks/tra-tac-mat-ong.png", desc: "Vị chua thanh mát của tắc tươi quyện cùng mật ong hoa nhãn rừng." },
  { id: 14, name: "Matcha đá xay", category: "MATCHA", price: 52000, img: "/assets/drinks/matcha-da-xay.png", desc: "Matcha đá tuyết phủ lớp kem whipping béo ngậy thơm nồng." },
  { id: 15, name: "Sinh tố dâu tây", category: "SMOOTHIE", price: 48000, img: "/assets/drinks/sinh-to-dau.png", desc: "Dâu tây Đà Lạt tươi mọng xay mát lạnh, chua ngọt tự nhiên." },
  { id: 16, name: "Cacao nóng", category: "COFFEE", price: 36000, img: "/assets/drinks/cacao-nong.png", desc: "Bột cacao Đắk Lắk nguyên chất đậm đà sưởi ấm ngày mưa." }
];

export const TOPPINGS = [
  { id: 1, name: "Trân châu trắng", price: 10000 },
  { id: 2, name: "Pudding trứng", price: 9000 },
  { id: 3, name: "Kem cheese", price: 12000 },
  { id: 4, name: "Extra Espresso Shot", price: 8000 },
  { id: 5, name: "Kem muối", price: 7000 },
  { id: 6, name: "Thạch cà phê", price: 9000 },
  { id: 7, name: "Kem vani", price: 11000 },
  { id: 8, name: "Trân châu đường đen", price: 6000 }
];

export const INVENTORY_DATA = [
  { name: "Hạt cà phê Robusta", unit: "kg", quantity: 4.8, min: 1.0 },
  { name: "Sữa tươi thanh trùng", unit: "lít", quantity: 18.5, min: 5.0 },
  { name: "Bột trà xanh Matcha Uji", unit: "kg", quantity: 1.2, min: 0.5 },
  { name: "Đào miếng đóng hộp", unit: "hộp", quantity: 8, min: 3 },
  { name: "Trân châu đen/trắng", unit: "kg", quantity: 3.5, min: 1.0 },
  { name: "Ly giấy & Nắp (Size M, L)", unit: "cái", quantity: 450, min: 100 }
];

export const INITIAL_ORDERS = [
  {
    id: 1001,
    createdAt: "10:15",
    items: [
      { name: "Cà phê sữa", size: "L", toppings: ["Trân châu trắng"], note: "Nhiều đá, ít ngọt", qty: 2, price: 50000 },
      { name: "Trà đào cam sả", size: "M", toppings: [], note: "", qty: 1, price: 35000 }
    ],
    subtotal: 135000,
    discountAmount: 13500,
    total: 121500,
    status: "PREPARING",
    paymentMethod: "VNPAY",
    orderType: "DINE_IN"
  },
  {
    id: 1002,
    createdAt: "10:25",
    items: [
      { name: "Matcha Latte", size: "M", toppings: ["Kem cheese"], note: "Ít ngọt", qty: 1, price: 54000 }
    ],
    subtotal: 54000,
    discountAmount: 0,
    total: 54000,
    status: "PENDING",
    paymentMethod: "MOMO",
    orderType: "TAKE_AWAY"
  }
];

export function formatVND(amount) {
  return new Intl.NumberFormat('vi-VN').format(Math.max(0, amount)) + ' ₫';
}
