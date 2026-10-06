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

export default function App() {
  // Authentication State
  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem('purr_pos_user');
    return saved ? JSON.parse(saved) : DEFAULT_USERS[1]; // default cashier01 for instant demo compatibility
  });

  const [activeTab, setActiveTab] = useState('pos');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [orderType, setOrderType] = useState('DINE_IN');

  // Master Data State (Synced across POS, Admin, Kitchen)
  const [menu, setMenu] = useState(() => {
    const saved = localStorage.getItem('purr_pos_menu');
    return saved ? JSON.parse(saved) : DEFAULT_MENU;
  });

  const [toppings, setToppings] = useState(() => {
    const saved = localStorage.getItem('purr_pos_toppings');
    return saved ? JSON.parse(saved) : DEFAULT_TOPPINGS;
  });

  const [inventory, setInventory] = useState(() => {
    const saved = localStorage.getItem('purr_pos_inventory');
    return saved ? JSON.parse(saved) : DEFAULT_INVENTORY;
  });

  const [inventoryLogs, setInventoryLogs] = useState(() => {
    const saved = localStorage.getItem('purr_pos_inv_logs');
    return saved ? JSON.parse(saved) : INITIAL_INVENTORY_LOGS;
  });

  const [users, setUsers] = useState(() => {
    const saved = localStorage.getItem('purr_pos_users_list');
    return saved ? JSON.parse(saved) : DEFAULT_USERS;
  });

  const [recipes, setRecipes] = useState(() => {
    const saved = localStorage.getItem('purr_pos_recipes');
    return saved ? JSON.parse(saved) : DEFAULT_RECIPES;
  });

  // Cart & Orders State
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem('react_pos_cart');
    return saved ? JSON.parse(saved) : [];
  });
  const [discountType, setDiscountType] = useState('NONE');
  const [orders, setOrders] = useState(() => {
    const saved = localStorage.getItem('react_pos_orders');
    return saved ? JSON.parse(saved) : INITIAL_ORDERS;
  });
  const [orderCounter, setOrderCounter] = useState(1003);

  // Modals & Notifications
  const [customizingDrink, setCustomizingDrink] = useState(null);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const [receiptOrder, setReceiptOrder] = useState(null);
  const [toastMessage, setToastMessage] = useState(null);

  // Save to localStorage
  useEffect(() => {
    if (currentUser) {
      localStorage.setItem('purr_pos_user', JSON.stringify(currentUser));
    } else {
      localStorage.removeItem('purr_pos_user');
    }
  }, [currentUser]);

  useEffect(() => {
    localStorage.setItem('purr_pos_menu', JSON.stringify(menu));
  }, [menu]);

  useEffect(() => {
    localStorage.setItem('purr_pos_toppings', JSON.stringify(toppings));
  }, [toppings]);

  useEffect(() => {
    localStorage.setItem('purr_pos_inventory', JSON.stringify(inventory));
  }, [inventory]);

  useEffect(() => {
    localStorage.setItem('purr_pos_inv_logs', JSON.stringify(inventoryLogs));
  }, [inventoryLogs]);

  useEffect(() => {
    localStorage.setItem('purr_pos_users_list', JSON.stringify(users));
  }, [users]);

  useEffect(() => {
    localStorage.setItem('react_pos_cart', JSON.stringify(cart));
  }, [cart]);

  useEffect(() => {
    localStorage.setItem('react_pos_orders', JSON.stringify(orders));
  }, [orders]);

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

  // Strategy Pattern Discounts
  const subtotal = cart.reduce((sum, i) => sum + i.unitPrice * i.qty, 0);
  let discountAmount = 0;
  if (discountType === 'PERCENT_10') discountAmount = Math.round(subtotal * 0.1);
  if (discountType === 'VIP') discountAmount = Math.round(subtotal * 0.15);
  if (discountType === 'BOGO' && cart.length >= 2) {
    discountAmount = Math.min(...cart.map(i => i.unitPrice));
  }
  const total = Math.max(0, subtotal - discountAmount);

  // Payment Confirmation & Inventory Deduction
  const handleConfirmPayment = (gateway) => {
    const newOrder = {
      id: orderCounter,
      createdAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
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

  // If not logged in, render Login View
  if (!currentUser) {
    return <LoginView onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="min-h-screen flex flex-col bg-[#fffaf6]">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentUser={currentUser}
        onLogout={handleLogout}
      />

      <main className="flex-1 flex overflow-hidden">
        {activeTab === 'pos' && (
          <div className="flex-1 flex flex-col lg:flex-row overflow-hidden">
            {/* Menu */}
            <section className="flex-1 flex flex-col p-6 overflow-y-auto">
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
          />
        )}
      </main>

      {/* Modals */}
      <CustomizeModal
        drink={customizingDrink}
        onClose={() => setCustomizingDrink(null)}
        onConfirm={handleAddToCart}
      />

      {isCheckoutOpen && (
        <CheckoutModal
          total={total}
          onClose={() => setIsCheckoutOpen(false)}
          onConfirmPayment={handleConfirmPayment}
        />
      )}

      <ReceiptModal
        order={receiptOrder}
        onClose={() => setReceiptOrder(null)}
      />

      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-5 right-5 z-50 bg-[#2b170c] text-white px-5 py-3 rounded-2xl shadow-xl text-sm font-semibold flex items-center gap-2 animate-in fade-in slide-in-from-bottom-5">
          <Check className="w-4 h-4 text-emerald-400" />
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
}
