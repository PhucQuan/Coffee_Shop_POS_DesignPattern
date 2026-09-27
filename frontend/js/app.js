// Coffee Shop POS - Modern Frontend Application
const DEFAULT_MENU = [
  { id: 1, name: "Cà phê sữa", category: "COFFEE", price: 30000, img: "assets/drinks/ca-phe-sua.png", desc: "Cà phê Robusta Đắk Lắk pha phin truyền thống hòa quyện sữa đặc béo ngậy." },
  { id: 2, name: "Bạc xỉu", category: "COFFEE", price: 32000, img: "assets/drinks/bac-xiu.png", desc: "Nhiều sữa ít cà phê, béo thơm ngọt dịu, phù hợp người thích vị nhẹ." },
  { id: 3, name: "Trà đào cam sả", category: "TEA", price: 35000, img: "assets/drinks/tra-dao.png", desc: "Trà đen ủ lạnh kết hợp đào ngâm giòn ngọt và hương sả thơm mát." },
  { id: 4, name: "Trà sữa truyền thống", category: "TEA", price: 38000, img: "assets/drinks/tra-sua.png", desc: "Hồng trà đậm vị kết hợp sữa tươi thanh trùng béo ngậy chuẩn vị quán." },
  { id: 5, name: "Matcha Latte", category: "MATCHA", price: 42000, img: "assets/drinks/matcha-latte.png", desc: "Bột trà xanh Uji Nhật Bản nguyên chất hòa quyện sữa tươi hấp." },
  { id: 6, name: "Sinh tố xoài", category: "SMOOTHIE", price: 45000, img: "assets/drinks/sinh-to-xoai.png", desc: "Xoài cát chín mọng tươi ngon xay cùng sữa chua mát lạnh giàu vitamin." },
  { id: 7, name: "Espresso", category: "COFFEE", price: 28000, img: "assets/drinks/espresso.png", desc: "Chiết xuất nguyên chất áp suất cao, lớp crema dày óng ánh." },
  { id: 8, name: "Americano", category: "COFFEE", price: 30000, img: "assets/drinks/americano.png", desc: "Espresso pha loãng với nước tinh khiết, thanh nhẹ không gắt." },
  { id: 9, name: "Latte", category: "COFFEE", price: 42000, img: "assets/drinks/latte.png", desc: "Cà phê Ý nhẹ nhàng với tỷ lệ sữa tươi đánh bọt nghệ thuật." },
  { id: 10, name: "Cappuccino", category: "COFFEE", price: 42000, img: "assets/drinks/cappuccino.png", desc: "Bọt sữa mịn màng rắc bột cacao thơm lừng phong cách Ý." },
  { id: 11, name: "Cold Brew", category: "COFFEE", price: 45000, img: "assets/drinks/cold-brew.png", desc: "Cà phê ủ lạnh suốt 16 tiếng, vị thanh thoát mượt mà ít chua." },
  { id: 12, name: "Trà vải lài", category: "TEA", price: 39000, img: "assets/drinks/tra-vai.png", desc: "Trà lài ngát hương kết hợp thịt vải ngọt lịm mọng nước." },
  { id: 13, name: "Trà tắc mật ong", category: "TEA", price: 34000, img: "assets/drinks/tra-tac-mat-ong.png", desc: "Vị chua thanh mát của tắc tươi quyện cùng mật ong hoa nhãn rừng." },
  { id: 14, name: "Matcha đá xay", category: "MATCHA", price: 52000, img: "assets/drinks/matcha-da-xay.png", desc: "Matcha đá tuyết phủ lớp kem whipping béo ngậy thơm nồng." },
  { id: 15, name: "Sinh tố dâu tây", category: "SMOOTHIE", price: 48000, img: "assets/drinks/sinh-to-dau.png", desc: "Dâu tây Đà Lạt tươi mọng xay mát lạnh, chua ngọt tự nhiên." },
  { id: 16, name: "Cacao nóng", category: "COFFEE", price: 36000, img: "assets/drinks/cacao-nong.png", desc: "Bột cacao Đắk Lắk nguyên chất đậm đà sưởi ấm ngày mưa." }
];

const TOPPINGS = [
  { id: 1, name: "Trân châu trắng", price: 10000 },
  { id: 2, name: "Pudding trứng", price: 9000 },
  { id: 3, name: "Kem cheese", price: 12000 },
  { id: 4, name: "Extra Espresso Shot", price: 8000 },
  { id: 5, name: "Kem muối", price: 7000 },
  { id: 6, name: "Thạch cà phê", price: 9000 },
  { id: 7, name: "Kem vani", price: 11000 },
  { id: 8, name: "Trân châu đường đen", price: 6000 }
];

const INVENTORY_DATA = [
  { name: "Hạt cà phê Robusta", unit: "kg", quantity: 4.8, min: 1.0, status: "Tốt" },
  { name: "Sữa tươi thanh trùng", unit: "lít", quantity: 18.5, min: 5.0, status: "Tốt" },
  { name: "Bột trà xanh Matcha Uji", unit: "kg", quantity: 1.2, min: 0.5, status: "Tốt" },
  { name: "Đào miếng đóng hộp", unit: "hộp", quantity: 8, min: 3, status: "Tốt" },
  { name: "Trân châu đen/trắng", unit: "kg", quantity: 3.5, min: 1.0, status: "Tốt" },
  { name: "Ly giấy & Nắp (Size M, L)", unit: "cái", quantity: 450, min: 100, status: "Tốt" }
];

// App State
let state = {
  activeTab: 'pos',
  selectedCategory: 'ALL',
  searchQuery: '',
  cart: [],
  discountType: 'NONE', // NONE, PERCENT_10, VIP, BOGO
  discountRate: 0,
  orders: [],
  orderCounter: 101,
  currentModalDrink: null,
  selectedSize: 'M',
  selectedToppings: [],
  itemNote: '',
  paymentMethod: 'CASH',
  orderType: 'DINE_IN' // DINE_IN, TAKE_AWAY
};

function formatVND(amount) {
  return new Intl.NumberFormat('vi-VN').format(Math.max(0, amount)) + ' ₫';
}

function initApp() {
  loadFromStorage();
  setupEventListeners();
  renderMenu();
  renderCart();
  renderKitchen();
  renderAdmin();
  checkBackendStatus();
}

function checkBackendStatus() {
  API.checkHealth().then(online => {
    const badge = document.getElementById('backendStatusBadge');
    if (badge) {
      if (online) {
        badge.innerHTML = `<span class="w-2.5 h-2.5 rounded-full bg-emerald-500 mr-2 pulse-badge"></span><span class="text-xs font-semibold text-emerald-800">Backend Connected</span>`;
      } else {
        badge.innerHTML = `<span class="w-2.5 h-2.5 rounded-full bg-amber-500 mr-2"></span><span class="text-xs font-semibold text-amber-800">Standalone Mode (FE Ready)</span>`;
      }
    }
  });
}

function saveToStorage() {
  localStorage.setItem('coffee_pos_orders', JSON.stringify(state.orders));
  localStorage.setItem('coffee_pos_cart', JSON.stringify(state.cart));
}

function loadFromStorage() {
  try {
    const savedOrders = localStorage.getItem('coffee_pos_orders');
    if (savedOrders) state.orders = JSON.parse(savedOrders);
    
    // Seed sample orders if empty for demo presentation
    if (state.orders.length === 0) {
      state.orders = [
        {
          id: 1001,
          createdAt: new Date(Date.now() - 15 * 60000).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
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
          createdAt: new Date(Date.now() - 5 * 60000).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
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
    }
  } catch (e) {
    console.error('Storage error', e);
  }
}

function setupEventListeners() {
  // Navigation tabs
  document.querySelectorAll('[data-tab-target]').forEach(btn => {
    btn.addEventListener('click', () => {
      const target = btn.getAttribute('data-tab-target');
      switchTab(target);
    });
  });

  // Search input
  const searchInput = document.getElementById('menuSearch');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      state.searchQuery = e.target.value.toLowerCase().trim();
      renderMenu();
    });
  }

  // Category buttons
  document.querySelectorAll('[data-category]').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('[data-category]').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      state.selectedCategory = btn.getAttribute('data-category');
      renderMenu();
    });
  });

  // Discount selector (Strategy Pattern)
  const discountSelect = document.getElementById('discountSelect');
  if (discountSelect) {
    discountSelect.addEventListener('change', (e) => {
      state.discountType = e.target.value;
      renderCart();
    });
  }
}

function switchTab(tabId) {
  state.activeTab = tabId;
  document.querySelectorAll('[data-tab-target]').forEach(btn => {
    if (btn.getAttribute('data-tab-target') === tabId) {
      btn.className = "flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold bg-[#3e200a] text-white shadow-sm transition-all";
    } else {
      btn.className = "flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-[#583115] hover:bg-[#ede4db] transition-all";
    }
  });

  document.querySelectorAll('.tab-page').forEach(page => {
    page.classList.add('hidden');
  });

  const activePage = document.getElementById(`page-${tabId}`);
  if (activePage) {
    activePage.classList.remove('hidden');
    activePage.classList.add('animate-fade-in');
  }

  if (tabId === 'kitchen') renderKitchen();
  if (tabId === 'admin') renderAdmin();
}

function renderMenu() {
  const container = document.getElementById('menuGridContainer');
  if (!container) return;

  const filtered = DEFAULT_MENU.filter(item => {
    const matchCat = state.selectedCategory === 'ALL' || item.category === state.selectedCategory;
    const matchSearch = !state.searchQuery || item.name.toLowerCase().includes(state.searchQuery);
    return matchCat && matchSearch;
  });

  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="col-span-full py-16 text-center text-[#8a7668]">
        <svg class="w-12 h-12 mx-auto mb-3 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
        <p class="font-medium">Không tìm thấy đồ uống phù hợp</p>
      </div>
    `;
    return;
  }

  container.innerHTML = filtered.map(item => `
    <div class="pos-card drink-card rounded-2xl overflow-hidden cursor-pointer flex flex-col justify-between" onclick="openCustomizeModal(${item.id})">
      <div class="relative bg-[#f6eee7] h-44 flex items-center justify-center p-3 overflow-hidden">
        <img src="${item.img}" alt="${item.name}" class="h-36 w-auto object-contain drop-shadow-md transition-transform duration-300 hover:scale-105" onerror="this.src='assets/drinks/ca-phe-sua.png'"/>
        <span class="absolute top-3 left-3 text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-[#583115] shadow-xs">
          ${item.category}
        </span>
      </div>
      <div class="p-4 flex-1 flex flex-col justify-between">
        <div>
          <h3 class="font-bold text-[#2b170c] text-base leading-tight mb-1 line-clamp-1">${item.name}</h3>
          <p class="text-xs text-[#8a7668] line-clamp-2 leading-relaxed mb-3">${item.desc}</p>
        </div>
        <div class="flex items-center justify-between pt-2 border-t border-[#f4eae3]">
          <span class="text-base font-extrabold text-[#7d4924]">${formatVND(item.price)}</span>
          <button class="w-8 h-8 rounded-full bg-[#f4eae3] hover:bg-[#3e200a] hover:text-white text-[#583115] flex items-center justify-center transition-all shadow-xs" title="Tùy chỉnh & Thêm">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4"></path></svg>
          </button>
        </div>
      </div>
    </div>
  `).join('');
}

// Decorator Pattern Modal Customizer
function openCustomizeModal(drinkId) {
  const drink = DEFAULT_MENU.find(d => d.id === drinkId);
  if (!drink) return;

  state.currentModalDrink = drink;
  state.selectedSize = 'M';
  state.selectedToppings = [];
  state.itemNote = '';

  document.getElementById('modalDrinkName').innerText = drink.name;
  document.getElementById('modalDrinkBasePrice').innerText = formatVND(drink.price);
  document.getElementById('modalDrinkImg').src = drink.img;

  // Reset Size radios
  document.querySelectorAll('input[name="modalSize"]').forEach(r => {
    r.checked = r.value === 'M';
  });

  // Render Toppings list
  const toppingListContainer = document.getElementById('modalToppingList');
  toppingListContainer.innerHTML = TOPPINGS.map(top => `
    <label class="flex items-center justify-between p-2.5 rounded-xl border border-[#ede4db] hover:border-[#c2917a] hover:bg-[#fffaf6] cursor-pointer transition-all">
      <div class="flex items-center gap-2.5">
        <input type="checkbox" value="${top.id}" onchange="toggleTopping(${top.id}, this.checked)" class="w-4 h-4 text-[#7d4924] rounded-sm focus:ring-0 border-gray-300">
        <span class="text-sm font-medium text-[#3e200a]">${top.name}</span>
      </div>
      <span class="text-xs font-bold text-[#a36538]">+${formatVND(top.price)}</span>
    </label>
  `).join('');

  document.getElementById('modalItemNote').value = '';
  updateModalTotal();

  document.getElementById('customizeModal').classList.remove('hidden');
}

function closeCustomizeModal() {
  document.getElementById('customizeModal').classList.add('hidden');
  state.currentModalDrink = null;
}

function selectModalSize(size) {
  state.selectedSize = size;
  updateModalTotal();
}

function toggleTopping(toppingId, isChecked) {
  const topping = TOPPINGS.find(t => t.id === toppingId);
  if (!topping) return;

  if (isChecked) {
    if (!state.selectedToppings.some(t => t.id === toppingId)) {
      state.selectedToppings.push(topping);
    }
  } else {
    state.selectedToppings = state.selectedToppings.filter(t => t.id !== toppingId);
  }
  updateModalTotal();
}

function updateModalTotal() {
  if (!state.currentModalDrink) return;
  let total = state.currentModalDrink.price;
  if (state.selectedSize === 'L') total += 10000;
  state.selectedToppings.forEach(t => total += t.price);
  document.getElementById('modalTotalCalculated').innerText = formatVND(total);
}

function confirmAddCart() {
  if (!state.currentModalDrink) return;

  const noteInput = document.getElementById('modalItemNote').value.trim();
  let unitPrice = state.currentModalDrink.price;
  if (state.selectedSize === 'L') unitPrice += 10000;
  state.selectedToppings.forEach(t => unitPrice += t.price);

  const cartItem = {
    cartItemId: Date.now() + Math.random().toString(36).substring(2, 6),
    drinkId: state.currentModalDrink.id,
    name: state.currentModalDrink.name,
    img: state.currentModalDrink.img,
    size: state.selectedSize,
    toppings: [...state.selectedToppings.map(t => t.name)],
    note: noteInput,
    unitPrice: unitPrice,
    qty: 1
  };

  // Check if identical item exists (same drink, size, toppings, and note)
  const existingIndex = state.cart.findIndex(i => 
    i.drinkId === cartItem.drinkId &&
    i.size === cartItem.size &&
    i.note === cartItem.note &&
    JSON.stringify(i.toppings) === JSON.stringify(cartItem.toppings)
  );

  if (existingIndex > -1) {
    state.cart[existingIndex].qty += 1;
  } else {
    state.cart.push(cartItem);
  }

  closeCustomizeModal();
  renderCart();
  showToast(`Đã thêm ${cartItem.name} vào đơn hàng!`);
  saveToStorage();
}

function renderCart() {
  const cartContainer = document.getElementById('cartItemsList');
  const cartBadge = document.getElementById('cartCountBadge');
  const emptyCartState = document.getElementById('emptyCartState');

  if (!cartContainer) return;

  const totalItems = state.cart.reduce((sum, item) => sum + item.qty, 0);
  if (cartBadge) cartBadge.innerText = totalItems;

  if (state.cart.length === 0) {
    cartContainer.innerHTML = '';
    if (emptyCartState) emptyCartState.classList.remove('hidden');
    updateSummary(0);
    return;
  }

  if (emptyCartState) emptyCartState.classList.add('hidden');

  cartContainer.innerHTML = state.cart.map((item, idx) => `
    <div class="p-3.5 rounded-xl border border-[#ede4db] bg-[#fffaf6] mb-2.5 flex items-start gap-3">
      <img src="${item.img}" class="w-12 h-12 object-contain bg-white rounded-lg p-1 border border-[#ede4db]" alt="${item.name}"/>
      <div class="flex-1 min-w-0">
        <div class="flex items-start justify-between gap-1">
          <h4 class="font-bold text-sm text-[#2b170c] leading-tight truncate">${item.name}</h4>
          <button onclick="removeCartItem('${item.cartItemId}')" class="text-gray-400 hover:text-red-500 transition-colors p-0.5" title="Xóa món">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        <div class="flex items-center gap-1.5 mt-0.5">
          <span class="text-[11px] font-bold px-1.5 py-0.5 rounded-sm bg-[#eed5c7] text-[#583115]">Size ${item.size}</span>
          ${item.toppings.length > 0 ? `<span class="text-[11px] text-[#7d4924] truncate">+ ${item.toppings.join(', ')}</span>` : ''}
        </div>
        ${item.note ? `<p class="text-[11px] text-[#a36538] italic mt-0.5 truncate">📝 ${item.note}</p>` : ''}
        
        <div class="flex items-center justify-between mt-2.5 pt-2 border-t border-[#f2e6dc]">
          <div class="flex items-center border border-[#d8c8bd] rounded-lg bg-white overflow-hidden shadow-2xs">
            <button onclick="changeQty('${item.cartItemId}', -1)" class="w-6 h-6 flex items-center justify-center text-[#583115] hover:bg-[#f7ebe3] font-bold text-xs">-</button>
            <span class="w-7 text-center font-bold text-xs text-[#2b170c]">${item.qty}</span>
            <button onclick="changeQty('${item.cartItemId}', 1)" class="w-6 h-6 flex items-center justify-center text-[#583115] hover:bg-[#f7ebe3] font-bold text-xs">+</button>
          </div>
          <span class="text-sm font-extrabold text-[#7d4924]">${formatVND(item.unitPrice * item.qty)}</span>
        </div>
      </div>
    </div>
  `).join('');

  // Calculate Subtotal & Discount Strategy
  const subtotal = state.cart.reduce((sum, item) => sum + (item.unitPrice * item.qty), 0);
  updateSummary(subtotal);
}

function changeQty(cartItemId, delta) {
  const item = state.cart.find(i => i.cartItemId === cartItemId);
  if (!item) return;

  item.qty += delta;
  if (item.qty <= 0) {
    state.cart = state.cart.filter(i => i.cartItemId !== cartItemId);
  }
  renderCart();
  saveToStorage();
}

function removeCartItem(cartItemId) {
  state.cart = state.cart.filter(i => i.cartItemId !== cartItemId);
  renderCart();
  saveToStorage();
}

function clearCart() {
  if (state.cart.length === 0) return;
  if (confirm("Bạn có chắc chắn muốn hủy giỏ hàng hiện tại?")) {
    state.cart = [];
    renderCart();
    saveToStorage();
    showToast("Đã xóa toàn bộ đơn hàng.");
  }
}

// Strategy Pattern for Discount Calculation
function updateSummary(subtotal) {
  let discountAmount = 0;
  if (state.discountType === 'PERCENT_10') {
    discountAmount = Math.round(subtotal * 0.1);
  } else if (state.discountType === 'VIP') {
    discountAmount = Math.round(subtotal * 0.15);
  } else if (state.discountType === 'BOGO') {
    // Buy One Get One: discount cheapest item price if count >= 2
    if (state.cart.length >= 2) {
      const prices = state.cart.map(i => i.unitPrice);
      discountAmount = Math.min(...prices);
    }
  }

  const finalTotal = Math.max(0, subtotal - discountAmount);

  document.getElementById('cartSubtotal').innerText = formatVND(subtotal);
  document.getElementById('cartDiscount').innerText = (discountAmount > 0 ? '-' : '') + formatVND(discountAmount);
  document.getElementById('cartTotal').innerText = formatVND(finalTotal);

  const btnCheckout = document.getElementById('btnOpenCheckout');
  if (btnCheckout) {
    btnCheckout.disabled = state.cart.length === 0;
  }
}

// Open Payment Dialog (Adapter Pattern)
function openCheckoutModal() {
  if (state.cart.length === 0) return;

  const subtotal = state.cart.reduce((sum, item) => sum + (item.unitPrice * item.qty), 0);
  let discountAmount = 0;
  if (state.discountType === 'PERCENT_10') discountAmount = Math.round(subtotal * 0.1);
  if (state.discountType === 'VIP') discountAmount = Math.round(subtotal * 0.15);
  if (state.discountType === 'BOGO' && state.cart.length >= 2) discountAmount = Math.min(...state.cart.map(i => i.unitPrice));
  const finalTotal = Math.max(0, subtotal - discountAmount);

  document.getElementById('checkoutTotalAmount').innerText = formatVND(finalTotal);
  selectPaymentGateway('CASH');

  document.getElementById('checkoutModal').classList.remove('hidden');
}

function closeCheckoutModal() {
  document.getElementById('checkoutModal').classList.add('hidden');
}

function selectPaymentGateway(gateway) {
  state.paymentMethod = gateway;
  document.querySelectorAll('[data-gateway]').forEach(b => {
    b.classList.remove('border-[#7d4924]', 'bg-[#fffaf6]');
    if (b.getAttribute('data-gateway') === gateway) {
      b.classList.add('border-[#7d4924]', 'bg-[#fffaf6]');
    }
  });

  const qrContainer = document.getElementById('qrCodeContainer');
  if (gateway === 'VNPAY' || gateway === 'MOMO') {
    qrContainer.classList.remove('hidden');
    document.getElementById('qrGatewayTitle').innerText = gateway === 'VNPAY' ? 'Quét mã VietQR / VNPay' : 'Quét mã MoMo QR';
  } else {
    qrContainer.classList.add('hidden');
  }
}

// Finalize Payment & Order Creation (State & Observer Pattern)
function processPaymentSuccess() {
  const subtotal = state.cart.reduce((sum, item) => sum + (item.unitPrice * item.qty), 0);
  let discountAmount = 0;
  if (state.discountType === 'PERCENT_10') discountAmount = Math.round(subtotal * 0.1);
  if (state.discountType === 'VIP') discountAmount = Math.round(subtotal * 0.15);
  if (state.discountType === 'BOGO' && state.cart.length >= 2) discountAmount = Math.min(...state.cart.map(i => i.unitPrice));
  const finalTotal = Math.max(0, subtotal - discountAmount);

  const newOrder = {
    id: state.orderCounter++,
    createdAt: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
    items: [...state.cart],
    subtotal: subtotal,
    discountAmount: discountAmount,
    discountType: state.discountType,
    total: finalTotal,
    status: 'PREPARING', // State transitioned: Order sent to kitchen upon payment!
    paymentMethod: state.paymentMethod,
    orderType: state.orderType
  };

  state.orders.unshift(newOrder);
  closeCheckoutModal();

  // Show Receipt Preview
  openReceiptModal(newOrder);

  // Clear current cart
  state.cart = [];
  renderCart();
  renderKitchen();
  renderAdmin();
  saveToStorage();

  showToast(`Thanh toán thành công Đơn #${newOrder.id}! Đã chuyển đến Bếp.`);
}

// Receipt Modal View
function openReceiptModal(order) {
  document.getElementById('receiptOrderId').innerText = '#' + order.id;
  document.getElementById('receiptDate').innerText = new Date().toLocaleString('vi-VN');
  document.getElementById('receiptPaymentMethod').innerText = order.paymentMethod;
  document.getElementById('receiptOrderType').innerText = order.orderType === 'DINE_IN' ? 'Dùng tại quán' : 'Mang đi';

  const itemsContainer = document.getElementById('receiptItemsBody');
  itemsContainer.innerHTML = order.items.map(i => `
    <div class="flex justify-between text-xs py-1 border-b border-dashed border-gray-200">
      <div>
        <div class="font-bold text-gray-800">${i.qty}x ${i.name} (Size ${i.size})</div>
        ${i.toppings.length ? `<div class="text-gray-500 text-[10px]">+ ${i.toppings.join(', ')}</div>` : ''}
        ${i.note ? `<div class="text-gray-500 text-[10px]">Ghi chú: ${i.note}</div>` : ''}
      </div>
      <div class="font-bold text-gray-800">${formatVND(i.unitPrice * i.qty)}</div>
    </div>
  `).join('');

  document.getElementById('receiptSubtotal').innerText = formatVND(order.subtotal);
  document.getElementById('receiptDiscount').innerText = (order.discountAmount > 0 ? '-' : '') + formatVND(order.discountAmount);
  document.getElementById('receiptTotal').innerText = formatVND(order.total);

  document.getElementById('receiptModal').classList.remove('hidden');
}

function closeReceiptModal() {
  document.getElementById('receiptModal').classList.add('hidden');
}

// Kitchen KDS Rendering (State Pattern)
function renderKitchen() {
  const pendingCol = document.getElementById('kdsPendingOrders');
  const preparingCol = document.getElementById('kdsPreparingOrders');
  const readyCol = document.getElementById('kdsReadyOrders');

  if (!pendingCol || !preparingCol || !readyCol) return;

  const pendingList = state.orders.filter(o => o.status === 'PENDING');
  const preparingList = state.orders.filter(o => o.status === 'PREPARING');
  const readyList = state.orders.filter(o => o.status === 'READY');

  document.getElementById('kdsPendingCount').innerText = pendingList.length;
  document.getElementById('kdsPreparingCount').innerText = preparingList.length;
  document.getElementById('kdsReadyCount').innerText = readyList.length;

  const renderKdsCard = (o, nextActionText, nextActionState, btnClass) => `
    <div class="pos-card rounded-2xl p-4 mb-3 border-l-4 ${o.status === 'PENDING' ? 'border-l-amber-500' : (o.status === 'PREPARING' ? 'border-l-blue-500' : 'border-l-emerald-500')}">
      <div class="flex items-center justify-between border-b border-[#f4eae3] pb-2 mb-2">
        <span class="font-extrabold text-[#2b170c] text-base">Đơn #${o.id}</span>
        <span class="text-xs font-semibold px-2 py-0.5 rounded-full ${o.orderType === 'DINE_IN' ? 'bg-[#f4eae3] text-[#583115]' : 'bg-amber-100 text-amber-800'}">
          ${o.orderType === 'DINE_IN' ? 'Tại quán' : 'Mang đi'} • ${o.createdAt}
        </span>
      </div>
      <div class="space-y-1.5 my-2">
        ${o.items.map(item => `
          <div class="text-xs">
            <span class="font-bold text-[#3e200a]">${item.qty}x</span>
            <span class="font-semibold text-gray-800">${item.name} (${item.size})</span>
            ${item.toppings.length ? `<span class="text-[#8a7668]"> + ${item.toppings.join(', ')}</span>` : ''}
            ${item.note ? `<div class="text-[11px] text-amber-700 italic font-medium ml-4">⚠️ ${item.note}</div>` : ''}
          </div>
        `).join('')}
      </div>
      ${nextActionText ? `
        <button onclick="transitionOrderState(${o.id}, '${nextActionState}')" class="w-full mt-3 py-2 px-3 rounded-xl text-xs font-bold text-white ${btnClass} transition-all shadow-xs flex items-center justify-center gap-1.5">
          ${nextActionText}
        </button>
      ` : `
        <div class="w-full mt-3 py-1.5 text-center text-xs font-bold text-emerald-700 bg-emerald-50 rounded-lg">
          ✓ Đã sẵn sàng phục vụ
        </div>
      `}
    </div>
  `;

  pendingCol.innerHTML = pendingList.length ? pendingList.map(o => renderKdsCard(o, 'Bắt đầu làm', 'PREPARING', 'bg-blue-600 hover:bg-blue-700')).join('') : emptyKdsPlaceholder('Không có đơn chờ');
  preparingCol.innerHTML = preparingList.length ? preparingList.map(o => renderKdsCard(o, 'Hoàn thành pha chế', 'READY', 'bg-emerald-600 hover:bg-emerald-700')).join('') : emptyKdsPlaceholder('Không có đơn đang làm');
  readyCol.innerHTML = readyList.length ? readyList.map(o => renderKdsCard(o, null, null, '')).join('') : emptyKdsPlaceholder('Chưa có món hoàn thành');
}

function emptyKdsPlaceholder(text) {
  return `<div class="text-center py-8 text-xs text-gray-400 font-medium">${text}</div>`;
}

// Transition state via State Pattern
function transitionOrderState(orderId, newState) {
  const order = state.orders.find(o => o.id === orderId);
  if (!order) return;

  order.status = newState;
  renderKitchen();
  saveToStorage();
  showToast(`Đơn #${orderId} chuyển trạng thái: ${newState}`);
}

// Admin View Rendering
function renderAdmin() {
  const totalRevenue = state.orders.reduce((sum, o) => sum + (o.status !== 'CANCELLED' ? o.total : 0), 0);
  const totalOrders = state.orders.length;
  const completedOrders = state.orders.filter(o => o.status === 'READY').length;

  document.getElementById('adminTotalRevenue').innerText = formatVND(totalRevenue);
  document.getElementById('adminTotalOrders').innerText = totalOrders;
  document.getElementById('adminCompletedOrders').innerText = completedOrders;

  // Inventory Table
  const invBody = document.getElementById('adminInventoryBody');
  if (invBody) {
    invBody.innerHTML = INVENTORY_DATA.map(inv => `
      <tr class="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
        <td class="py-3 px-4 text-sm font-semibold text-[#2b170c]">${inv.name}</td>
        <td class="py-3 px-4 text-sm text-gray-600">${inv.quantity} ${inv.unit}</td>
        <td class="py-3 px-4 text-sm text-gray-400">${inv.min} ${inv.unit}</td>
        <td class="py-3 px-4">
          <span class="text-xs font-bold px-2 py-0.5 rounded-full ${inv.quantity > inv.min ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'}">
            ${inv.quantity > inv.min ? 'Đủ hàng' : 'Cần nhập thêm'}
          </span>
        </td>
      </tr>
    `).join('');
  }

  // Orders Table
  const ordersBody = document.getElementById('adminOrdersBody');
  if (ordersBody) {
    ordersBody.innerHTML = state.orders.map(o => `
      <tr class="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
        <td class="py-3 px-4 text-sm font-bold text-[#583115]">#${o.id}</td>
        <td class="py-3 px-4 text-xs text-gray-500">${o.createdAt}</td>
        <td class="py-3 px-4 text-sm font-medium text-gray-700">${o.items.map(i => `${i.qty}x ${i.name}`).join(', ')}</td>
        <td class="py-3 px-4 text-sm font-extrabold text-[#7d4924]">${formatVND(o.total)}</td>
        <td class="py-3 px-4">
          <span class="text-xs font-bold px-2 py-0.5 rounded-full ${o.status === 'READY' ? 'bg-emerald-100 text-emerald-800' : (o.status === 'PREPARING' ? 'bg-blue-100 text-blue-800' : 'bg-amber-100 text-amber-800')}">
            ${o.status}
          </span>
        </td>
      </tr>
    `).join('');
  }
}

// Toast notification
function showToast(message) {
  let toast = document.getElementById('posToast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'posToast';
    toast.className = 'fixed bottom-5 right-5 z-50 bg-[#2b170c] text-white px-5 py-3 rounded-2xl shadow-xl text-sm font-semibold flex items-center gap-2 transform translate-y-20 opacity-0 transition-all duration-300';
    document.body.appendChild(toast);
  }

  toast.innerHTML = `<svg class="w-4 h-4 text-emerald-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg><span>${message}</span>`;
  toast.classList.remove('translate-y-20', 'opacity-0');

  setTimeout(() => {
    toast.classList.add('translate-y-20', 'opacity-0');
  }, 2500);
}

// Run on page load
document.addEventListener('DOMContentLoaded', initApp);
