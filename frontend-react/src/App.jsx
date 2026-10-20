import React, { useState, useEffect } from 'react';
import { Search, Check } from 'lucide-react';
import Navbar from './components/Navbar';
import DrinkCard from './components/DrinkCard';
import CartSidebar from './components/CartSidebar';
import CustomizeModal from './components/CustomizeModal';
import CheckoutModal from './components/CheckoutModal';
import ReceiptModal from './components/ReceiptModal';
import KitchenView from './components/KitchenView';
import AdminView from './components/AdminView';
import LoginView from './components/LoginView';
import {
  DEFAULT_USERS,
  DEFAULT_MENU,
  DEFAULT_TOPPINGS,
  DEFAULT_INVENTORY,
  INITIAL_INVENTORY_LOGS,
  DEFAULT_RECIPES,
  INITIAL_ORDERS
} from './data/mockData';

const demoDefects = new Set(
  new URLSearchParams(window.location.search)
    .get('demoDefects')
    ?.split(',')
    .map(value => value.trim().toUpperCase())
    .filter(Boolean) || []
);

export default function App() {
  // Authentication State
  const [currentUser, setCurrentUser] = useState(() => {
    // Check if Playwright or URL parameter requests bypass
    const params = new URLSearchParams(window.location.search);
    if (params.get('autologin') === 'true') {
      return DEFAULT_USERS[1];
    }
    return null;
  });

  const [activeTab, setActiveTab] = useState('pos');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [orderType, setOrderType] = useState('DINE_IN');

  // Master Data State (Synced across POS, Admin, Kitchen)
  const [menu, setMenu] = useState(DEFAULT_MENU);
  const [toppings, setToppings] = useState(DEFAULT_TOPPINGS);
  const [inventory, setInventory] = useState(DEFAULT_INVENTORY);
  const [inventoryLogs, setInventoryLogs] = useState(INITIAL_INVENTORY_LOGS);
  const [users, setUsers] = useState(DEFAULT_USERS);
  const [recipes, setRecipes] = useState(DEFAULT_RECIPES);
  const [dataReady, setDataReady] = useState(false);
  const [dataError, setDataError] = useState('');

  // Cart & Orders State
  const [cart, setCart] = useState([]);
  const [discountType, setDiscountType] = useState('NONE');
  const [voucherCode, setVoucherCode] = useState('');
  const [voucherDiscount, setVoucherDiscount] = useState(0);

  const [orders, setOrders] = useState(INITIAL_ORDERS);
  const [orderCounter, setOrderCounter] = useState(1003);

  // Modals & Notifications
  const [customizingDrink, setCustomizingDrink] = useState(null);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const [receiptOrder, setReceiptOrder] = useState(null);
  const [toastMessage, setToastMessage] = useState(null);

  // MySQL is the durable source of truth. Session/login/cart state intentionally
  // stays in memory and is not shared between cashier terminals.
  useEffect(() => {
    const controller = new AbortController();
    async function loadState() {
      try {
        const response = await fetch('/api/state', { signal: controller.signal });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const state = await response.json();
        if (state) {
          if (Array.isArray(state.menu)) setMenu(state.menu);
          if (Array.isArray(state.toppings)) setToppings(state.toppings);
          if (Array.isArray(state.inventory)) setInventory(state.inventory);
          if (Array.isArray(state.inventoryLogs)) setInventoryLogs(state.inventoryLogs);
          if (Array.isArray(state.users)) setUsers(state.users);
          if (Array.isArray(state.recipes)) setRecipes(state.recipes);
          if (Array.isArray(state.orders)) setOrders(state.orders);
          if (Number.isInteger(state.orderCounter)) setOrderCounter(state.orderCounter);
        }
        setDataReady(true);
      } catch (error) {
        if (error.name !== 'AbortError') {
          setDataError('Không thể kết nối MySQL qua máy chủ. Hãy kiểm tra MySQL và cấu hình MYSQL_* rồi tải lại trang.');
        }
      }
    }
    loadState();
    return () => controller.abort();
  }, []);

  useEffect(() => {
    if (!dataReady) return undefined;
    const timeoutId = window.setTimeout(async () => {
      try {
        const response = await fetch('/api/state', {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ menu, toppings, inventory, inventoryLogs, users, recipes, orders, orderCounter })
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        setDataError('');
      } catch {
        setDataError('Không thể lưu thay đổi vào MySQL. Dữ liệu mới chưa được lưu bền vững.');
      }
    }, 300);
    return () => window.clearTimeout(timeoutId);
  }, [dataReady, menu, toppings, inventory, inventoryLogs, users, recipes, orders, orderCounter]);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 2500);
  };

  // Auth Handlers
  const handleLoginSuccess = (user) => {
    setCurrentUser(user);
    if (user.role === 'ADMIN') setActiveTab('admin');
    else if (user.role === 'KITCHEN') setActiveTab('kitchen');
    else setActiveTab('pos');
    showToast(`Xin chào ${user.fullName || user.username}!`);
  };

  const handleLogout = () => {
    setCurrentUser(null);
    showToast("Đã đăng xuất ca làm việc.");
  };

  // Cart operations
  const handleAddToCart = (customizedItem) => {
    const newItem = {
      cartItemId: Date.now() + Math.random().toString(36).substring(2, 6),
      drinkId: customizedItem.drink.id,
      name: customizedItem.drink.name,
      img: customizedItem.drink.img,
      size: customizedItem.size,
      toppings: customizedItem.toppings,
      note: customizedItem.note,
      unitPrice: customizedItem.unitPrice,
      qty: 1
    };

    setCart(prev => {
      const idx = prev.findIndex(i =>
        i.drinkId === newItem.drinkId &&
        i.size === newItem.size &&
        i.note === newItem.note &&
        JSON.stringify(i.toppings) === JSON.stringify(newItem.toppings)
      );
      if (idx > -1) {
        const updated = [...prev];
        updated[idx].qty += 1;
        return updated;
      }
      return [...prev, newItem];
    });

    setCustomizingDrink(null);
    showToast(`Đã thêm ${newItem.name} vào đơn!`);
  };

  const handleChangeQty = (cartItemId, delta) => {
    setCart(prev =>
      prev
        .map(i => (i.cartItemId === cartItemId ? { ...i, qty: i.qty + delta } : i))
        .filter(i => i.qty > 0)
    );
  };

  const handleRemoveItem = (cartItemId) => {
    setCart(prev => prev.filter(i => i.cartItemId !== cartItemId));
  };

  const handleClearCart = () => {
    if (window.confirm("Bạn có chắc chắn muốn hủy giỏ hàng hiện tại?")) {
      setCart([]);
      showToast("Đã xóa giỏ hàng.");
    }
  };

  // Strategy Pattern Discounts & Voucher Code
  const handleApplyVoucher = () => {
    const code = voucherCode.trim().toUpperCase();
    if (code === 'COFFEE20K') {
      setVoucherDiscount(20000);
      showToast('Áp dụng mã COFFEE20K: Giảm 20.000 ₫!');
    } else if (code === 'CHAOBAN' || code === 'WELCOME') {
      setVoucherDiscount(15000);
      showToast('Áp dụng mã Chào bạn: Giảm 15.000 ₫!');
    } else if (code === '') {
      setVoucherDiscount(0);
      showToast('Đã xóa mã voucher.');
    } else {
      showToast('Mã voucher không hợp lệ hoặc đã hết hạn!');
    }
  };

  const subtotal = cart.reduce((sum, i) => sum + i.unitPrice * i.qty, 0);
  let discountAmount = 0;
  if (discountType === 'PERCENT_10') {
    discountAmount = Math.round(subtotal * (demoDefects.has('TC03') ? 0.05 : 0.1));
  }
  if (discountType === 'VIP') discountAmount = Math.round(subtotal * 0.15);
  if (discountType === 'BOGO' && cart.length >= 2) {
    discountAmount = Math.min(...cart.map(i => i.unitPrice));
  }
  discountAmount += voucherDiscount;
  discountAmount = Math.min(discountAmount, subtotal);
  let total = Math.max(0, subtotal - discountAmount);
  if (demoDefects.has('TC01') && cart.length === 1) total += 1000;

  // Payment Confirmation & Inventory Deduction
  const handleConfirmPayment = (gateway) => {
    const now = new Date();
    const newOrder = {
      id: orderCounter,
      createdAt: now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      orderDate: now.toISOString().split('T')[0],
      items: [...cart],
      subtotal,
      discountAmount,
      discountType,
      total,
      status: 'PREPARING',
      paymentMethod: gateway,
      orderType
    };

    // Auto deduct inventory based on recipe items
    cart.forEach(item => {
      const itemRecipes = recipes.filter(r => r.beverageId === item.drinkId);
      itemRecipes.forEach(rec => {
        const deductQty = rec.quantityRequired * item.qty;
        setInventory(prev => prev.map(inv => {
          if (inv.id === rec.inventoryId) {
            return { ...inv, quantity: Math.max(0, inv.quantity - deductQty) };
          }
          return inv;
        }));

        setInventoryLogs(prev => [
          {
            id: Date.now() + Math.random(),
            time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' ' + new Date().toLocaleDateString('vi-VN'),
            item: rec.inventoryName,
            delta: `-${deductQty} ${rec.unit}`,
            balance: `Đã trừ`,
            reason: `Pha chế Đơn #${newOrder.id}`
          },
          ...prev
        ]);
      });
    });

    setOrders(prev => [newOrder, ...prev]);
    setOrderCounter(c => c + 1);
    setIsCheckoutOpen(false);
    setCart([]);
    setReceiptOrder(newOrder);
    showToast(`Thanh toán đơn #${newOrder.id} thành công!`);
  };

  const handleTransitionState = (orderId, nextState) => {
    setOrders(prev =>
      prev.map(o => (o.id === orderId ? { ...o, status: nextState } : o))
    );
    showToast(`Đơn #${orderId} chuyển sang: ${nextState}`);
  };

  const handleCancelOrder = (orderId) => {
    if (window.confirm(`Bạn có chắc chắn muốn hủy đơn hàng #${orderId}?`)) {
      setOrders(prev =>
        prev.map(o => (o.id === orderId ? { ...o, status: 'CANCELLED' } : o))
      );
      showToast(`Đã hủy đơn #${orderId}!`);
    }
  };

  // Filter Menu (Only active drinks for POS)
  const filteredMenu = menu.filter(item => {
    const isActive = item.active !== false;
    const matchCat = selectedCategory === 'ALL' || item.category === selectedCategory;
    const matchSearch = !searchQuery || item.name.toLowerCase().includes(searchQuery.toLowerCase());
    return isActive && matchCat && matchSearch;
  });

  if (!dataReady) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#fffaf6] p-6 text-center">
        <div className="max-w-lg rounded-3xl border border-[#ede4db] bg-white p-8 shadow-lg">
          <h1 className="text-xl font-black text-[#2b170c]">{dataError ? 'Không thể tải dữ liệu' : 'Đang kết nối MySQL...'}</h1>
          <p className="mt-3 text-sm text-[#8a7668]">{dataError || 'PurrCoffee đang tải dữ liệu dùng chung từ máy chủ.'}</p>
          {dataError && (
            <button onClick={() => window.location.reload()} className="mt-5 rounded-xl bg-[#3e200a] px-5 py-2.5 text-sm font-bold text-white">
              Thử lại
            </button>
          )}
        </div>
      </div>
    );
  }

  // If not logged in, render Login View
  if (!currentUser) {
    return <LoginView users={users} onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="min-h-screen lg:h-screen lg:overflow-hidden flex flex-col bg-[#fffaf6]">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentUser={currentUser}
        onLogout={handleLogout}
      />

      <main className="flex-1 flex lg:min-h-0 overflow-x-hidden lg:overflow-hidden">
        {activeTab === 'pos' && (
          <div className="flex-1 flex flex-col lg:flex-row lg:min-h-0 overflow-visible lg:overflow-hidden">
            {/* Menu */}
            <section className="flex-1 flex flex-col p-6 lg:min-h-0 overflow-y-auto">
              <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4 mb-6">
                <div className="relative flex-1 max-w-md">
                  <Search className="w-5 h-5 absolute left-3.5 top-1/2 -translate-y-1/2 text-[#a36538]" />
                  <input
                    id="menuSearch"
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Tìm kiếm đồ uống (vd: bạc xỉu, trà đào)..."
                    className="w-full pl-11 pr-4 py-2.5 rounded-2xl bg-white border border-[#ede4db] focus:border-[#a36538] focus:ring-2 focus:ring-[#f7ebe3] text-sm text-[#2b170c] outline-none shadow-2xs"
                  />
                </div>

                <div className="flex items-center gap-1 bg-[#f4eae3] p-1 rounded-2xl self-start">
                  <button
                    onClick={() => setOrderType('DINE_IN')}
                    className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                      orderType === 'DINE_IN' ? 'bg-white text-[#2b170c] shadow-xs' : 'text-[#583115]'
                    }`}
                  >
                    Tại quán
                  </button>
                  <button
                    onClick={() => setOrderType('TAKE_AWAY')}
                    className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                      orderType === 'TAKE_AWAY' ? 'bg-white text-[#2b170c] shadow-xs' : 'text-[#583115]'
                    }`}
                  >
                    Mang đi
                  </button>
                </div>
              </div>

              {/* Category Filter Pills */}
              <div className="flex items-center gap-2 overflow-x-auto pb-2 mb-6 scrollbar-none">
                {[
                  { id: 'ALL', label: 'Tất cả món' },
                  { id: 'COFFEE', label: '☕ Cà phê' },
                  { id: 'TEA', label: '🍵 Trà trái cây' },
                  { id: 'MATCHA', label: '🍃 Matcha' },
                  { id: 'SMOOTHIE', label: '🥭 Sinh tố' }
                ].map(cat => (
                  <button
                    key={cat.id}
                    data-category={cat.id}
                    onClick={() => setSelectedCategory(cat.id)}
                    className={`px-5 py-2.5 rounded-2xl text-xs font-bold transition-all whitespace-nowrap shadow-xs border cursor-pointer ${
                      selectedCategory === cat.id
                        ? 'bg-[#3e200a] text-white border-[#3e200a]'
                        : 'bg-white text-[#583115] border-[#ede4db] hover:border-[#c2917a]'
                    }`}
                  >
                    {cat.label}
                  </button>
                ))}
              </div>

              {/* Grid */}
              <div id="menuGridContainer" className="grid grid-cols-2 sm:grid-cols-3 xl:grid-cols-4 gap-5">
                {filteredMenu.map(item => (
                  <DrinkCard key={item.id} item={item} onOpenModal={setCustomizingDrink} />
                ))}
              </div>
            </section>

            {/* Cart */}
            <CartSidebar
              cart={cart}
              onChangeQty={handleChangeQty}
              onRemoveItem={handleRemoveItem}
              onClearCart={handleClearCart}
              discountType={discountType}
              setDiscountType={setDiscountType}
              voucherCode={voucherCode}
              setVoucherCode={setVoucherCode}
              onApplyVoucher={handleApplyVoucher}
              voucherDiscount={voucherDiscount}
              subtotal={subtotal}
              discountAmount={discountAmount}
              total={total}
              onOpenCheckout={() => setIsCheckoutOpen(true)}
            />
          </div>
        )}

        {activeTab === 'kitchen' && (
          <KitchenView orders={orders} onTransitionState={handleTransitionState} />
        )}

        {activeTab === 'admin' && (
          <AdminView
            orders={orders}
            setOrders={setOrders}
            menu={menu}
            setMenu={setMenu}
            toppings={toppings}
            setToppings={setToppings}
            inventory={inventory}
            setInventory={setInventory}
            inventoryLogs={inventoryLogs}
            setInventoryLogs={setInventoryLogs}
            users={users}
            setUsers={setUsers}
            recipes={recipes}
            setRecipes={setRecipes}
            onCancelOrder={handleCancelOrder}
            onTransitionState={handleTransitionState}
          />
        )}
      </main>

      {/* Modals */}
      <CustomizeModal
        drink={customizingDrink}
        toppings={toppings}
        onClose={() => setCustomizingDrink(null)}
        onConfirm={handleAddToCart}
      />

      {isCheckoutOpen && (
        <CheckoutModal
          total={total}
          demoDefects={demoDefects}
          onClose={() => setIsCheckoutOpen(false)}
          onConfirmPayment={handleConfirmPayment}
        />
      )}

      <ReceiptModal
        order={receiptOrder}
        demoDefects={demoDefects}
        onClose={() => setReceiptOrder(null)}
      />

      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-5 right-5 z-50 bg-[#2b170c] text-white px-5 py-3 rounded-2xl shadow-xl text-sm font-semibold flex items-center gap-2 animate-in fade-in slide-in-from-bottom-5">
          <Check className="w-4 h-4 text-emerald-400" />
          <span>{toastMessage}</span>
        </div>
      )}

      {dataError && (
        <div className="fixed bottom-5 left-5 z-50 max-w-md rounded-2xl bg-red-700 px-5 py-3 text-sm font-semibold text-white shadow-xl">
          {dataError}
        </div>
      )}
    </div>
  );
}
