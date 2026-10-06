import React, { useState } from 'react';
import {
  TrendingUp, ShoppingBag, CheckCircle, Package, Users, Coffee,
  Plus, Edit, Trash2, Shield, Lock, Unlock, RefreshCw, X, AlertTriangle, FileSpreadsheet, RotateCcw
} from 'lucide-react';
import { formatVND } from '../data/mockData';

export default function AdminView({
  orders,
  menu,
  setMenu,
  toppings,
  setToppings,
  inventory,
  setInventory,
  inventoryLogs,
  setInventoryLogs,
  users,
  setUsers,
  recipes,
  setRecipes,
  onCancelOrder
}) {
  const [adminTab, setAdminTab] = useState('overview'); // overview | orders | menu | topping | inventory | users

  // Modals state
  const [editingBeverage, setEditingBeverage] = useState(null); // null or obj
  const [beverageModalOpen, setBeverageModalOpen] = useState(false);
  const [bevForm, setBevForm] = useState({ name: '', price: 30000, category: 'COFFEE', active: true });

  const [editingTopping, setEditingTopping] = useState(null);
  const [toppingModalOpen, setToppingModalOpen] = useState(false);
  const [toppingForm, setToppingForm] = useState({ name: '', price: 10000, active: true });

  const [restockModalOpen, setRestockModalOpen] = useState(false);
  const [selectedInventoryItem, setSelectedInventoryItem] = useState(null);
  const [restockAmount, setRestockAmount] = useState(1000);

  const [userModalOpen, setUserModalOpen] = useState(false);
  const [userForm, setUserForm] = useState({ username: '', password: '123', role: 'CASHIER', fullName: '' });

  // Calculation for Overview
  const totalRevenue = orders.reduce((sum, o) => sum + (o.status !== 'CANCELLED' ? o.total : 0), 0);
  const paidOrders = orders.filter(o => o.status === 'READY' || o.status === 'PREPARING').length;
  const cancelledOrders = orders.filter(o => o.status === 'CANCELLED').length;

  // Top selling drink calculation
  const drinkSales = {};
  orders.forEach(o => {
    if (o.status !== 'CANCELLED') {
      o.items.forEach(i => {
        drinkSales[i.name] = (drinkSales[i.name] || 0) + i.qty;
      });
    }
  });
  const topDrinks = Object.entries(drinkSales).sort((a, b) => b[1] - a[1]);

  // Beverage Handlers
  const handleOpenAddBeverage = () => {
    setEditingBeverage(null);
    setBevForm({ name: '', price: 30000, category: 'COFFEE', active: true });
    setBeverageModalOpen(true);
  };

  const handleOpenEditBeverage = (b) => {
    setEditingBeverage(b);
    setBevForm({ name: b.name, price: b.price, category: b.category, active: b.active });
    setBeverageModalOpen(true);
  };

  const handleSaveBeverage = (e) => {
    e.preventDefault();
    if (!bevForm.name.trim()) return;

    if (editingBeverage) {
      setMenu(prev => prev.map(m => m.id === editingBeverage.id ? { ...m, ...bevForm } : m));
    } else {
      const newId = Math.max(...menu.map(m => m.id), 0) + 1;
      const newBev = {
        id: newId,
        ...bevForm,
        img: '/assets/drinks/ca-phe-sua.png',
        desc: `${bevForm.name} mới thêm vào thực đơn.`
      };
      setMenu(prev => [newBev, ...prev]);
    }
    setBeverageModalOpen(false);
  };

  const handleToggleBeverageActive = (id) => {
    setMenu(prev => prev.map(m => m.id === id ? { ...m, active: !m.active } : m));
  };

  const handleDeleteBeverage = (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa món này khỏi thực đơn?")) {
      setMenu(prev => prev.filter(m => m.id !== id));
    }
  };

  // Topping Handlers
  const handleOpenAddTopping = () => {
    setEditingTopping(null);
    setToppingForm({ name: '', price: 10000, active: true });
    setToppingModalOpen(true);
  };

  const handleOpenEditTopping = (t) => {
    setEditingTopping(t);
    setToppingForm({ name: t.name, price: t.price, active: t.active });
    setToppingModalOpen(true);
  };

  const handleSaveTopping = (e) => {
    e.preventDefault();
    if (!toppingForm.name.trim()) return;

    if (editingTopping) {
      setToppings(prev => prev.map(t => t.id === editingTopping.id ? { ...t, ...toppingForm } : t));
    } else {
      const newId = Math.max(...toppings.map(t => t.id), 0) + 1;
      setToppings(prev => [...prev, { id: newId, ...toppingForm }]);
    }
    setToppingModalOpen(false);
  };

  const handleToggleToppingActive = (id) => {
    setToppings(prev => prev.map(t => t.id === id ? { ...t, active: !t.active } : t));
  };

  const handleDeleteTopping = (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa topping này?")) {
      setToppings(prev => prev.filter(t => t.id !== id));
    }
  };

  // Inventory Restock Handlers
  const handleOpenRestock = (item) => {
    setSelectedInventoryItem(item);
    setRestockAmount(item.unit === 'g' || item.unit === 'ml' ? 1000 : 10);
    setRestockModalOpen(true);
  };

  const handleConfirmRestock = (e) => {
    e.preventDefault();
    if (!selectedInventoryItem || restockAmount <= 0) return;

    const amount = Number(restockAmount);
    setInventory(prev => prev.map(inv => {
      if (inv.id === selectedInventoryItem.id) {
        return { ...inv, quantity: inv.quantity + amount };
      }
      return inv;
    }));

    // Ghi nhật ký kho (Inventory Transaction Ledger)
    const newLog = {
      id: Date.now(),
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' ' + new Date().toLocaleDateString('vi-VN'),
      item: selectedInventoryItem.name,
      delta: `+${amount} ${selectedInventoryItem.unit}`,
      balance: `${selectedInventoryItem.quantity + amount} ${selectedInventoryItem.unit}`,
      reason: 'Nhập hàng bổ sung (Manual Restock)'
    };
    setInventoryLogs(prev => [newLog, ...prev]);

    setRestockModalOpen(false);
  };

  // User Handlers
  const handleSaveUser = (e) => {
    e.preventDefault();
    if (!userForm.username.trim()) return;

    const newId = Math.max(...users.map(u => u.id), 0) + 1;
    const newUser = {
      id: newId,
      username: userForm.username.trim(),
      password: userForm.password,
      role: userForm.role,
      fullName: userForm.fullName || userForm.username,
      active: true
    };
    setUsers(prev => [...prev, newUser]);
    setUserModalOpen(false);
  };

  const handleToggleUserActive = (id) => {
    setUsers(prev => prev.map(u => u.id === id ? { ...u, active: !u.active } : u));
  };

  return (
    <div className="flex-1 p-6 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-black text-[#2b170c] tracking-tight">Hệ Thống Quản Trị Trung Tâm</h2>
          <p className="text-xs text-[#8a7668]">Đồng bộ dữ liệu chuẩn SQLite Desktop App: Menu, Topping, Kho, Đơn Hàng & Tài Khoản.</p>
        </div>

        {/* 8 Tab Switcher */}
        <div className="flex items-center gap-1 bg-[#f4eae3] p-1.5 rounded-2xl overflow-x-auto scrollbar-none">
          {[
            { id: 'overview', label: '📊 Tổng Quan' },
            { id: 'orders', label: '🧾 Đơn Hàng' },
            { id: 'menu', label: '☕ Thực Đơn' },
            { id: 'topping', label: '🧋 Topping' },
            { id: 'inventory', label: '📦 Kho & Nhập Hàng' },
            { id: 'recipes', label: '🧪 Công Thức Định Lượng' },
            { id: 'users', label: '👥 Nhân Viên' },
            { id: 'backup', label: '💾 Sao Lưu SQLite' }
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setAdminTab(tab.id)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer ${
                adminTab === tab.id
                  ? 'bg-[#3e200a] text-white shadow-xs'
                  : 'text-[#583115] hover:bg-[#ede4db]'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* TAB 1: OVERVIEW */}
      {adminTab === 'overview' && (
        <div className="space-y-6">
          {/* KPI Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <div className="pos-card rounded-2xl p-5 border-l-4 border-l-[#7d4924]">
              <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Tổng Doanh Thu</p>
              <h3 className="text-2xl font-black text-[#2b170c]">{formatVND(totalRevenue)}</h3>
              <p className="text-[10px] text-emerald-600 font-semibold mt-1">Đã trừ đơn hủy</p>
            </div>
            <div className="pos-card rounded-2xl p-5 border-l-4 border-l-blue-600">
              <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Tổng Đơn Hàng</p>
              <h3 className="text-2xl font-black text-blue-900">{orders.length}</h3>
              <p className="text-[10px] text-blue-600 font-semibold mt-1">{paidOrders} đơn thành công</p>
            </div>
            <div className="pos-card rounded-2xl p-5 border-l-4 border-l-amber-600">
              <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Nguyên Liệu Cảnh Báo</p>
              <h3 className="text-2xl font-black text-amber-900">
                {inventory.filter(i => i.quantity <= i.min).length}
              </h3>
              <p className="text-[10px] text-amber-600 font-semibold mt-1">Dưới mức tồn tối thiểu</p>
            </div>
            <div className="pos-card rounded-2xl p-5 border-l-4 border-l-purple-600">
              <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Món Trong Menu</p>
              <h3 className="text-2xl font-black text-purple-900">{menu.length} món</h3>
              <p className="text-[10px] text-purple-600 font-semibold mt-1">{toppings.length} loại topping</p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Top Selling Drinks */}
            <div className="pos-card rounded-3xl p-5">
              <h3 className="font-extrabold text-[#2b170c] text-base mb-4 flex items-center justify-between">
                <span>🔥 Món Bán Chạy Nhất</span>
                <span className="text-xs font-normal text-[#8a7668]">Dựa trên số lượng đã order</span>
              </h3>
              <div className="space-y-3">
                {topDrinks.length > 0 ? (
                  topDrinks.slice(0, 6).map(([name, qty], idx) => (
                    <div key={idx} className="flex items-center justify-between p-3 rounded-xl bg-[#fffaf6] border border-[#ede4db]">
                      <div className="flex items-center gap-3">
                        <span className={`w-6 h-6 rounded-full flex items-center justify-center font-bold text-xs ${
                          idx === 0 ? 'bg-amber-400 text-white' : idx === 1 ? 'bg-gray-300 text-gray-800' : 'bg-[#ede4db] text-[#583115]'
                        }`}>
                          {idx + 1}
                        </span>
                        <span className="text-sm font-bold text-[#2b170c]">{name}</span>
                      </div>
                      <span className="text-xs font-extrabold text-[#7d4924] bg-white px-2.5 py-1 rounded-lg border border-[#eed5c7]">
                        {qty} ly đã bán
                      </span>
                    </div>
                  ))
                ) : (
                  <p className="text-xs text-gray-400 py-6 text-center">Chưa có đơn hàng nào.</p>
                )}
              </div>
            </div>

            {/* Low Stock Warning */}
            <div className="pos-card rounded-3xl p-5">
              <h3 className="font-extrabold text-[#2b170c] text-base mb-4 flex items-center justify-between">
                <span>📦 Tồn Kho Nguyên Liệu</span>
                <span className="text-xs font-normal text-[#8a7668]">Cần nhập thêm</span>
              </h3>
              <div className="space-y-2.5">
                {inventory.filter(i => i.quantity <= i.min * 1.5).slice(0, 6).map((inv) => (
                  <div key={inv.id} className="flex items-center justify-between p-3 rounded-xl bg-amber-50/50 border border-amber-200">
                    <div>
                      <h4 className="text-xs font-bold text-amber-900">{inv.name}</h4>
                      <p className="text-[11px] text-amber-700">Tồn: {inv.quantity} {inv.unit} (Mức tối thiểu: {inv.min} {inv.unit})</p>
                    </div>
                    <button
                      onClick={() => handleOpenRestock(inv)}
                      className="px-3 py-1.5 rounded-lg bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold shadow-2xs"
                    >
                      Nhập thêm
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: ORDERS */}
      {adminTab === 'orders' && (
        <div className="pos-card rounded-3xl p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-extrabold text-[#2b170c] text-base">
              Quản Lý Đơn Hàng & Lịch Sử Giao Dịch
            </h3>
            <span className="text-xs text-[#8a7668]">Tổng số: {orders.length} đơn</span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2.5 px-4">Mã đơn</th>
                  <th className="py-2.5 px-4">Thời gian</th>
                  <th className="py-2.5 px-4">Chi tiết món</th>
                  <th className="py-2.5 px-4">Cổng TT</th>
                  <th className="py-2.5 px-4">Tổng tiền</th>
                  <th className="py-2.5 px-4">Trạng thái</th>
                  <th className="py-2.5 px-4 text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((o) => (
                  <tr key={o.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 text-sm font-bold text-[#583115]">#{o.id}</td>
                    <td className="py-3 px-4 text-xs text-gray-500">{o.createdAt}</td>
                    <td className="py-3 px-4 text-xs font-medium text-gray-800">
                      {o.items.map(i => `${i.qty}x ${i.name} (${i.size})`).join(', ')}
                    </td>
                    <td className="py-3 px-4">
                      <span className="text-[11px] font-bold px-2 py-0.5 rounded bg-gray-100 text-gray-700">
                        {o.paymentMethod || 'CASH'}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-sm font-black text-[#7d4924]">{formatVND(o.total)}</td>
                    <td className="py-3 px-4">
                      <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${
                        o.status === 'READY'
                          ? 'bg-emerald-100 text-emerald-800'
                          : o.status === 'PREPARING'
                          ? 'bg-blue-100 text-blue-800'
                          : o.status === 'CANCELLED'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}>
                        {o.status}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      {o.status !== 'CANCELLED' ? (
                        <button
                          onClick={() => onCancelOrder(o.id)}
                          className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-red-50 text-red-700 hover:bg-red-100 border border-red-200 transition-all cursor-pointer"
                        >
                          Hủy đơn
                        </button>
                      ) : (
                        <span className="text-xs text-gray-400 italic">Đã hủy</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: MENU CATALOG */}
      {adminTab === 'menu' && (
        <div className="pos-card rounded-3xl p-5">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">Danh Mục Đồ Uống (Beverage Catalog)</h3>
              <p className="text-xs text-[#8a7668]">Quản lý danh sách đồ uống, giá bán và phân loại (Factory Method).</p>
            </div>
            <button
              onClick={handleOpenAddBeverage}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-[#3e200a] text-white font-bold text-xs hover:bg-[#583115] shadow-xs cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              Thêm Đồ Uống Mới
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2.5 px-4">ID</th>
                  <th className="py-2.5 px-4">Tên đồ uống</th>
                  <th className="py-2.5 px-4">Phân loại</th>
                  <th className="py-2.5 px-4">Đơn giá gốc</th>
                  <th className="py-2.5 px-4">Trạng thái bán</th>
                  <th className="py-2.5 px-4 text-right">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {menu.map((b) => (
                  <tr key={b.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 text-xs font-bold text-gray-400">#{b.id}</td>
                    <td className="py-3 px-4 font-bold text-sm text-[#2b170c]">
                      <div className="flex items-center gap-2">
                        <img src={b.img} className="w-8 h-8 rounded-lg object-contain bg-white p-0.5 border border-[#ede4db]" alt="" />
                        <span>{b.name}</span>
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-[#f4eae3] text-[#583115]">
                        {b.category}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-bold text-sm text-[#7d4924]">{formatVND(b.price)}</td>
                    <td className="py-3 px-4">
                      <button
                        onClick={() => handleToggleBeverageActive(b.id)}
                        className={`text-xs font-bold px-2.5 py-0.5 rounded-full cursor-pointer ${
                          b.active ? 'bg-emerald-100 text-emerald-800' : 'bg-gray-200 text-gray-600'
                        }`}
                      >
                        {b.active ? 'Đang mở bán' : 'Tạm ngưng'}
                      </button>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => handleOpenEditBeverage(b)}
                          className="p-1.5 rounded-lg text-blue-600 hover:bg-blue-50 cursor-pointer"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteBeverage(b.id)}
                          className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 cursor-pointer"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: TOPPING CATALOG */}
      {adminTab === 'topping' && (
        <div className="pos-card rounded-3xl p-5">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">Danh Mục Topping (Decorator Catalog)</h3>
              <p className="text-xs text-[#8a7668]">Topping mở rộng cho đồ uống theo mẫu thiết kế Decorator Pattern.</p>
            </div>
            <button
              onClick={handleOpenAddTopping}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-[#3e200a] text-white font-bold text-xs hover:bg-[#583115] shadow-xs cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              Thêm Topping Mới
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2.5 px-4">ID</th>
                  <th className="py-2.5 px-4">Tên Topping</th>
                  <th className="py-2.5 px-4">Giá phụ thu</th>
                  <th className="py-2.5 px-4">Trạng thái</th>
                  <th className="py-2.5 px-4 text-right">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {toppings.map((t) => (
                  <tr key={t.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 text-xs font-bold text-gray-400">#{t.id}</td>
                    <td className="py-3 px-4 font-bold text-sm text-[#2b170c]">{t.name}</td>
                    <td className="py-3 px-4 font-extrabold text-sm text-[#7d4924]">+{formatVND(t.price)}</td>
                    <td className="py-3 px-4">
                      <button
                        onClick={() => handleToggleToppingActive(t.id)}
                        className={`text-xs font-bold px-2.5 py-0.5 rounded-full cursor-pointer ${
                          t.active ? 'bg-emerald-100 text-emerald-800' : 'bg-gray-200 text-gray-600'
                        }`}
                      >
                        {t.active ? 'Khả dụng' : 'Khóa'}
                      </button>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => handleOpenEditTopping(t)}
                          className="p-1.5 rounded-lg text-blue-600 hover:bg-blue-50 cursor-pointer"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteTopping(t.id)}
                          className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 cursor-pointer"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 5: INVENTORY & STOCK LEDGER */}
      {adminTab === 'inventory' && (
        <div className="space-y-6">
          <div className="pos-card rounded-3xl p-5">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h3 className="font-extrabold text-[#2b170c] text-base">Kho Nguyên Vật Liệu (Inventory Items)</h3>
                <p className="text-xs text-[#8a7668]">Tự động trừ kho theo định lượng Recipe khi làm món. Hỗ trợ nhập kho thủ công.</p>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                    <th className="py-2.5 px-4">Mã</th>
                    <th className="py-2.5 px-4">Tên nguyên liệu</th>
                    <th className="py-2.5 px-4">Số lượng tồn</th>
                    <th className="py-2.5 px-4">Mức tối thiểu</th>
                    <th className="py-2.5 px-4">Trạng thái</th>
                    <th className="py-2.5 px-4 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {inventory.map((inv) => (
                    <tr key={inv.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                      <td className="py-3 px-4 text-xs font-bold text-gray-400">#{inv.id}</td>
                      <td className="py-3 px-4 font-bold text-sm text-[#2b170c]">{inv.name}</td>
                      <td className="py-3 px-4 font-black text-sm text-gray-800">{inv.quantity} {inv.unit}</td>
                      <td className="py-3 px-4 text-xs text-gray-500">{inv.min} {inv.unit}</td>
                      <td className="py-3 px-4">
                        <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
                          inv.quantity > inv.min ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'
                        }`}>
                          {inv.quantity > inv.min ? 'Đủ hàng' : 'Cần nhập gấp'}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <button
                          onClick={() => handleOpenRestock(inv)}
                          className="px-3 py-1.5 rounded-xl bg-[#583115] hover:bg-[#3e200a] text-white text-xs font-bold shadow-2xs cursor-pointer"
                        >
                          + Nhập Kho
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Audit Ledger */}
          <div className="pos-card rounded-3xl p-5">
            <h3 className="font-extrabold text-[#2b170c] text-base mb-3 flex items-center justify-between">
              <span>📜 Nhật Ký Biến Động Kho (Stock Ledger)</span>
              <span className="text-xs font-normal text-[#8a7668]">Kiểm toán thời gian thực</span>
            </h3>
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                    <th className="py-2 px-4">Thời gian</th>
                    <th className="py-2 px-4">Nguyên vật liệu</th>
                    <th className="py-2 px-4">Biến động</th>
                    <th className="py-2 px-4">Tồn sau giao dịch</th>
                    <th className="py-2 px-4">Lý do</th>
                  </tr>
                </thead>
                <tbody>
                  {inventoryLogs.map((log) => (
                    <tr key={log.id} className="border-b border-[#f4eae3] text-xs">
                      <td className="py-2.5 px-4 text-gray-500 font-mono">{log.time}</td>
                      <td className="py-2.5 px-4 font-semibold text-[#2b170c]">{log.item}</td>
                      <td className={`py-2.5 px-4 font-extrabold ${log.delta.startsWith('+') ? 'text-emerald-700' : 'text-red-700'}`}>
                        {log.delta}
                      </td>
                      <td className="py-2.5 px-4 font-bold text-gray-700">{log.balance}</td>
                      <td className="py-2.5 px-4 text-gray-600">{log.reason}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 6: USERS */}
      {adminTab === 'users' && (
        <div className="pos-card rounded-3xl p-5">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">Quản Lý Tài Khoản Nhân Viên (Users & Roles)</h3>
              <p className="text-xs text-[#8a7668]">Phân quyền hệ thống: Quản trị (ADMIN), Thu ngân (CASHIER), Bếp (KITCHEN).</p>
            </div>
            <button
              onClick={() => {
                setUserForm({ username: '', password: '123', role: 'CASHIER', fullName: '' });
                setUserModalOpen(true);
              }}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-[#3e200a] text-white font-bold text-xs hover:bg-[#583115] shadow-xs cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              Thêm Nhân Viên
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2.5 px-4">Tên đăng nhập</th>
                  <th className="py-2.5 px-4">Họ và tên</th>
                  <th className="py-2.5 px-4">Vai trò (Role)</th>
                  <th className="py-2.5 px-4">Mật khẩu</th>
                  <th className="py-2.5 px-4">Trạng thái</th>
                  <th className="py-2.5 px-4 text-right">Khóa/Mở</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id || u.username} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 font-bold text-sm text-[#2b170c]">{u.username}</td>
                    <td className="py-3 px-4 text-sm text-gray-700">{u.fullName || u.username}</td>
                    <td className="py-3 px-4">
                      <span className={`text-xs font-extrabold px-2.5 py-1 rounded-full ${
                        u.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : u.role === 'KITCHEN' ? 'bg-amber-100 text-amber-800' : 'bg-blue-100 text-blue-800'
                      }`}>
                        {u.role}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-mono text-xs text-gray-400">••••••</td>
                    <td className="py-3 px-4">
                      <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
                        u.active ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {u.active ? 'ACTIVE' : 'LOCKED'}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => handleToggleUserActive(u.id)}
                        className={`px-3 py-1 rounded-lg text-xs font-bold transition-all cursor-pointer ${
                          u.active
                            ? 'bg-red-50 text-red-700 hover:bg-red-100 border border-red-200'
                            : 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200'
                        }`}
                      >
                        {u.active ? 'Khóa' : 'Kích hoạt'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 7: RECIPES */}
      {adminTab === 'recipes' && (
        <div className="pos-card rounded-3xl p-5">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">Công Thức Định Lượng Pha Chế (Recipe Items)</h3>
              <p className="text-xs text-[#8a7668]">Quy định lượng nguyên liệu tiêu hao tự động trừ vào kho cho mỗi ly đồ uống.</p>
            </div>
            <span className="text-xs font-bold text-[#583115] bg-[#f7ebe3] px-3 py-1.5 rounded-xl">
              {recipes.length} công thức định lượng
            </span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2.5 px-4">Tên Đồ Uống</th>
                  <th className="py-2.5 px-4">Nguyên Liệu Cần</th>
                  <th className="py-2.5 px-4">Định Lượng / 1 Ly</th>
                  <th className="py-2.5 px-4">Đơn vị</th>
                  <th className="py-2.5 px-4">Trạng thái cấu hình</th>
                </tr>
              </thead>
              <tbody>
                {recipes.map((r, idx) => (
                  <tr key={idx} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 font-bold text-sm text-[#2b170c]">{r.beverageName}</td>
                    <td className="py-3 px-4 text-sm font-semibold text-[#583115]">{r.inventoryName}</td>
                    <td className="py-3 px-4 font-black text-sm text-[#7d4924]">{r.quantityRequired}</td>
                    <td className="py-3 px-4 text-xs font-semibold text-gray-500">{r.unit}</td>
                    <td className="py-3 px-4">
                      <span className="text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800">
                        Chuẩn công thức
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 8: SQLITE BACKUP & RESTORE */}
      {adminTab === 'backup' && (
        <div className="pos-card rounded-3xl p-6 space-y-6">
          <div>
            <h3 className="font-extrabold text-[#2b170c] text-lg">Hệ Thống Sao Lưu & Phục Hồi Dữ Liệu SQLite</h3>
            <p className="text-xs text-[#8a7668] mt-1">
              Xuất tệp sao lưu dữ liệu toàn diện (JSON / SQLite schema) hoặc tải xuống báo cáo doanh thu Excel / CSV.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            <div className="p-5 rounded-2xl border border-[#ede4db] bg-[#fffaf6] flex flex-col justify-between">
              <div>
                <span className="text-2xl mb-2 block">💾</span>
                <h4 className="font-bold text-sm text-[#2b170c]">Sao Lưu Toàn Bộ Dữ Liệu</h4>
                <p className="text-xs text-[#8a7668] mt-1">Tải về bản sao lưu đầy đủ Menu, Topping, Kho, và Đơn Hàng dạng JSON.</p>
              </div>
              <button
                onClick={() => {
                  const backupObj = { menu, toppings, inventory, inventoryLogs, users, recipes, orders, exportTime: new Date().toISOString() };
                  const blob = new Blob([JSON.stringify(backupObj, null, 2)], { type: 'application/json' });
                  const url = URL.createObjectURL(blob);
                  const a = document.createElement('a');
                  a.href = url;
                  a.download = `purrcoffee_backup_${Date.now()}.json`;
                  a.click();
                  URL.revokeObjectURL(a);
                }}
                className="mt-4 w-full py-2.5 rounded-xl bg-[#3e200a] hover:bg-[#583115] text-white text-xs font-bold transition-all shadow-xs cursor-pointer"
              >
                Tải Xuống Bản Backup
              </button>
            </div>

            <div className="p-5 rounded-2xl border border-[#ede4db] bg-[#fffaf6] flex flex-col justify-between">
              <div>
                <span className="text-2xl mb-2 block">📊</span>
                <h4 className="font-bold text-sm text-[#2b170c]">Xuất Báo Cáo Doanh Thu CSV</h4>
                <p className="text-xs text-[#8a7668] mt-1">Xuất danh sách đơn hàng và doanh thu thành tệp CSV để mở bằng Excel.</p>
              </div>
              <button
                onClick={() => {
                  let csv = "MaDon,ThoiGian,HinhThuc,CongTT,TongTien,TrangThai\n";
                  orders.forEach(o => {
                    csv += `${o.id},"${o.createdAt}","${o.orderType}","${o.paymentMethod || 'CASH'}",${o.total},"${o.status}"\n`;
                  });
                  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
                  const url = URL.createObjectURL(blob);
                  const a = document.createElement('a');
                  a.href = url;
                  a.download = `bao_cao_doanh_thu_${Date.now()}.csv`;
                  a.click();
                  URL.revokeObjectURL(a);
                }}
                className="mt-4 w-full py-2.5 rounded-xl bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold transition-all shadow-xs cursor-pointer"
              >
                Xuất Báo Cáo Excel/CSV
              </button>
            </div>

            <div className="p-5 rounded-2xl border border-[#ede4db] bg-[#fffaf6] flex flex-col justify-between">
              <div>
                <span className="text-2xl mb-2 block">🔄</span>
                <h4 className="font-bold text-sm text-[#2b170c]">Khôi Phục Về Dữ Liệu Gốc</h4>
                <p className="text-xs text-[#8a7668] mt-1">Xóa bộ nhớ đệm và nạp lại toàn bộ dữ liệu SQLite pos_data.db ban đầu.</p>
              </div>
              <button
                onClick={() => {
                  if (window.confirm("Bạn có chắc chắn muốn đặt lại tất cả dữ liệu về mặc định ban đầu?")) {
                    localStorage.clear();
                    window.location.reload();
                  }
                }}
                className="mt-4 w-full py-2.5 rounded-xl bg-red-600 hover:bg-red-700 text-white text-xs font-bold transition-all shadow-xs cursor-pointer"
              >
                Đặt Lại Gốc (Reset Data)
              </button>
            </div>
          </div>
        </div>
      )}
      {beverageModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-md overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-4 border-b border-[#ede4db] mb-4">
              <h3 className="font-extrabold text-base text-[#2b170c]">
                {editingBeverage ? 'Chỉnh Sửa Đồ Uống' : 'Thêm Đồ Uống Mới'}
              </h3>
              <button onClick={() => setBeverageModalOpen(false)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleSaveBeverage} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Tên món:</label>
                <input
                  type="text"
                  required
                  value={bevForm.name}
                  onChange={(e) => setBevForm({ ...bevForm, name: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-[#583115] mb-1">Giá bán (VND):</label>
                  <input
                    type="number"
                    step="1000"
                    min="0"
                    required
                    value={bevForm.price}
                    onChange={(e) => setBevForm({ ...bevForm, price: Number(e.target.value) })}
                    className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-[#583115] mb-1">Phân loại:</label>
                  <select
                    value={bevForm.category}
                    onChange={(e) => setBevForm({ ...bevForm, category: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                  >
                    <option value="COFFEE">COFFEE</option>
                    <option value="TEA">TEA</option>
                    <option value="MATCHA">MATCHA</option>
                    <option value="SMOOTHIE">SMOOTHIE</option>
                  </select>
                </div>
              </div>
              <div className="flex items-center gap-2 pt-2">
                <input
                  type="checkbox"
                  id="bevActiveCheck"
                  checked={bevForm.active}
                  onChange={(e) => setBevForm({ ...bevForm, active: e.target.checked })}
                  className="w-4 h-4 text-[#7d4924]"
                />
                <label htmlFor="bevActiveCheck" className="text-xs font-bold text-[#2b170c]">
                  Kích hoạt mở bán ngay trên POS
                </label>
              </div>
              <div className="flex items-center justify-end gap-2 pt-4 border-t border-[#f4eae3]">
                <button
                  type="button"
                  onClick={() => setBeverageModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#3e200a] text-white hover:bg-[#583115]"
                >
                  Lưu Thông Tin
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: TOPPING ADD/EDIT */}
      {toppingModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-4 border-b border-[#ede4db] mb-4">
              <h3 className="font-extrabold text-base text-[#2b170c]">
                {editingTopping ? 'Chỉnh Sửa Topping' : 'Thêm Topping Mới'}
              </h3>
              <button onClick={() => setToppingModalOpen(false)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleSaveTopping} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Tên Topping:</label>
                <input
                  type="text"
                  required
                  value={toppingForm.name}
                  onChange={(e) => setToppingForm({ ...toppingForm, name: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Giá phụ thu (VND):</label>
                <input
                  type="number"
                  step="1000"
                  min="0"
                  required
                  value={toppingForm.price}
                  onChange={(e) => setToppingForm({ ...toppingForm, price: Number(e.target.value) })}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div className="flex items-center gap-2 pt-2">
                <input
                  type="checkbox"
                  id="topActiveCheck"
                  checked={toppingForm.active}
                  onChange={(e) => setToppingForm({ ...toppingForm, active: e.target.checked })}
                  className="w-4 h-4 text-[#7d4924]"
                />
                <label htmlFor="topActiveCheck" className="text-xs font-bold text-[#2b170c]">
                  Kích hoạt lựa chọn trên POS
                </label>
              </div>
              <div className="flex items-center justify-end gap-2 pt-4 border-t border-[#f4eae3]">
                <button
                  type="button"
                  onClick={() => setToppingModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#3e200a] text-white hover:bg-[#583115]"
                >
                  Lưu Topping
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 3: INVENTORY RESTOCK */}
      {restockModalOpen && selectedInventoryItem && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-[#ede4db] mb-3">
              <h3 className="font-extrabold text-base text-[#2b170c]">Nhập Hàng Vào Kho</h3>
              <button onClick={() => setRestockModalOpen(false)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>
            <p className="text-xs text-[#8a7668] mb-4">
              Nhập bổ sung cho nguyên liệu <strong className="text-[#2b170c]">{selectedInventoryItem.name}</strong> (Tồn hiện tại: {selectedInventoryItem.quantity} {selectedInventoryItem.unit}).
            </p>
            <form onSubmit={handleConfirmRestock} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">
                  Số lượng nhập thêm ({selectedInventoryItem.unit}):
                </label>
                <input
                  type="number"
                  min="0.1"
                  step="any"
                  required
                  value={restockAmount}
                  onChange={(e) => setRestockAmount(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924] font-bold"
                />
              </div>
              <div className="flex items-center justify-end gap-2 pt-3 border-t border-[#f4eae3]">
                <button
                  type="button"
                  onClick={() => setRestockModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-emerald-700 text-white hover:bg-emerald-800"
                >
                  Xác Nhận Nhập Kho
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 4: USER ADD */}
      {userModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-[#ede4db] mb-3">
              <h3 className="font-extrabold text-base text-[#2b170c]">Thêm Tài Khoản Nhân Viên</h3>
              <button onClick={() => setUserModalOpen(false)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleSaveUser} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Tên đăng nhập:</label>
                <input
                  type="text"
                  required
                  value={userForm.username}
                  onChange={(e) => setUserForm({ ...userForm, username: e.target.value })}
                  placeholder="ví dụ: cashier02"
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Họ và tên hiển thị:</label>
                <input
                  type="text"
                  required
                  value={userForm.fullName}
                  onChange={(e) => setUserForm({ ...userForm, fullName: e.target.value })}
                  placeholder="ví dụ: Lê Thị C"
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Mật khẩu:</label>
                <input
                  type="password"
                  required
                  value={userForm.password}
                  onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Vai trò hệ thống:</label>
                <select
                  value={userForm.role}
                  onChange={(e) => setUserForm({ ...userForm, role: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                >
                  <option value="CASHIER">CASHIER (Thu Ngân)</option>
                  <option value="KITCHEN">KITCHEN (Màn Hình Bếp)</option>
                  <option value="ADMIN">ADMIN (Quản Trị Viên)</option>
                </select>
              </div>
              <div className="flex items-center justify-end gap-2 pt-3 border-t border-[#f4eae3]">
                <button
                  type="button"
                  onClick={() => setUserModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#3e200a] text-white hover:bg-[#583115]"
                >
                  Tạo Tài Khoản
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
}
