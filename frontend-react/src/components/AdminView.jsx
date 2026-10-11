import React, { useState } from 'react';
import {
  TrendingUp, ShoppingBag, CheckCircle, Package, Users, Coffee,
  Plus, Edit, Trash2, Shield, RefreshCw, X, AlertTriangle, FileSpreadsheet, RotateCcw,
  Search, Calendar, FileText, Download, Printer, Filter, Eye, Check, Clock, CreditCard,
  PieChart, BarChart2, ArrowRight
} from 'lucide-react';
import { formatVND } from '../data/mockData';
import ReceiptModal from './ReceiptModal';

export default function AdminView({
  orders = [],
  setOrders,
  menu = [],
  setMenu,
  toppings = [],
  setToppings,
  inventory = [],
  setInventory,
  inventoryLogs = [],
  setInventoryLogs,
  users = [],
  setUsers,
  recipes = [],
  setRecipes,
  onCancelOrder,
  onTransitionState
}) {
  const [adminTab, setAdminTab] = useState('overview'); // overview | orders | reports | menu | topping | inventory | recipes | users | backup

  // Modal xem lại hóa đơn
  const [selectedReceiptOrder, setSelectedReceiptOrder] = useState(null);

  // Filter state for Orders tab (Yêu cầu 1: Lọc đơn hàng theo trạng thái & tìm kiếm)
  const [orderStatusFilter, setOrderStatusFilter] = useState('ALL'); // ALL | PENDING | PREPARING | READY | CANCELLED
  const [orderSearchQuery, setOrderSearchQuery] = useState('');

  // Filter state for Menu tab (Yêu cầu 2: Tìm kiếm trong thực đơn)
  const [menuSearchQuery, setMenuSearchQuery] = useState('');
  const [menuCategoryFilter, setMenuCategoryFilter] = useState('ALL'); // ALL | COFFEE | TEA | MATCHA | SMOOTHIE

  // Filter state for Inventory tab (Yêu cầu 4: Tìm kiếm trong Kho & Nhập hàng)
  const [inventorySearchQuery, setInventorySearchQuery] = useState('');
  const [inventoryStatusFilter, setInventoryStatusFilter] = useState('ALL'); // ALL | LOW | NORMAL

  // Date helper functions for Reports (Yêu cầu 6: Báo cáo & Chọn khoảng thời gian)
  const toDateString = (d) => {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const todayStr = toDateString(new Date());
  const [datePreset, setDatePreset] = useState('7DAYS'); // TODAY | YESTERDAY | 7DAYS | THIS_MONTH | ALL | CUSTOM
  const [dateFrom, setDateFrom] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() - 6);
    return toDateString(d);
  });
  const [dateTo, setDateTo] = useState(todayStr);

  const handleApplyDatePreset = (preset) => {
    setDatePreset(preset);
    const now = new Date();
    if (preset === 'TODAY') {
      setDateFrom(todayStr);
      setDateTo(todayStr);
    } else if (preset === 'YESTERDAY') {
      const y = new Date();
      y.setDate(y.getDate() - 1);
      const yStr = toDateString(y);
      setDateFrom(yStr);
      setDateTo(yStr);
    } else if (preset === '7DAYS') {
      const d = new Date();
      d.setDate(d.getDate() - 6);
      setDateFrom(toDateString(d));
      setDateTo(todayStr);
    } else if (preset === 'THIS_MONTH') {
      const m = new Date(now.getFullYear(), now.getMonth(), 1);
      setDateFrom(toDateString(m));
      setDateTo(todayStr);
    } else if (preset === 'ALL') {
      setDateFrom('2020-01-01');
      setDateTo(todayStr);
    }
  };

  // Helper lấy ngày của đơn hàng
  const getOrderDate = (o) => {
    if (o.orderDate) return o.orderDate;
    if (o.date) return o.date;
    if (o.createdAt && o.createdAt.includes('/')) {
      const parts = o.createdAt.split(' ');
      const datePart = parts.find(p => p.includes('/'));
      if (datePart) {
        const dParts = datePart.split('/');
        if (dParts.length === 3) return `${dParts[2]}-${dParts[1].padStart(2, '0')}-${dParts[0].padStart(2, '0')}`;
      }
    }
    return todayStr;
  };

  // Modals state for CRUD
  const [editingBeverage, setEditingBeverage] = useState(null);
  const [beverageModalOpen, setBeverageModalOpen] = useState(false);
  const [bevForm, setBevForm] = useState({ name: '', price: 30000, category: 'COFFEE', active: true });

  const [editingTopping, setEditingTopping] = useState(null);
  const [toppingModalOpen, setToppingModalOpen] = useState(false);
  const [toppingForm, setToppingForm] = useState({ name: '', price: 10000, active: true });

  const [restockModalOpen, setRestockModalOpen] = useState(false);
  const [selectedInventoryItem, setSelectedInventoryItem] = useState(null);
  const [restockAmount, setRestockAmount] = useState(1000);

  const [userModalOpen, setUserModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [userForm, setUserForm] = useState({ username: '', password: '123', role: 'CASHIER', fullName: '' });

  // Calculation for Overview Tab
  const totalRevenue = orders.reduce((sum, o) => sum + (o.status !== 'CANCELLED' ? o.total : 0), 0);
  const paidOrders = orders.filter(o => o.status === 'READY' || o.status === 'PREPARING').length;

  const drinkSales = {};
  orders.forEach(o => {
    if (o.status !== 'CANCELLED') {
      o.items.forEach(i => {
        drinkSales[i.name] = (drinkSales[i.name] || 0) + i.qty;
      });
    }
  });
  const topDrinks = Object.entries(drinkSales).sort((a, b) => b[1] - a[1]);

  // Orders Tab Filtering
  const filteredOrders = orders.filter(o => {
    const matchStatus = orderStatusFilter === 'ALL' || o.status === orderStatusFilter;
    const query = orderSearchQuery.toLowerCase().trim();
    const matchSearch = !query ||
      String(o.id).includes(query.replace('#', '')) ||
      (o.paymentMethod && o.paymentMethod.toLowerCase().includes(query)) ||
      (o.orderType && o.orderType.toLowerCase().includes(query)) ||
      (o.items && o.items.some(i => i.name.toLowerCase().includes(query)));
    return matchStatus && matchSearch;
  });

  // Menu Tab Filtering
  const filteredMenu = menu.filter(b => {
    const matchCat = menuCategoryFilter === 'ALL' || b.category === menuCategoryFilter;
    const matchSearch = !menuSearchQuery || b.name.toLowerCase().includes(menuSearchQuery.toLowerCase().trim());
    return matchCat && matchSearch;
  });

  // Inventory Tab Filtering
  const filteredInventory = inventory.filter(inv => {
    const query = inventorySearchQuery.toLowerCase().trim();
    const matchSearch = !query ||
      inv.name.toLowerCase().includes(query) ||
      inv.unit.toLowerCase().includes(query);
    const isLow = inv.quantity <= inv.min;
    const matchStatus = inventoryStatusFilter === 'ALL' ||
      (inventoryStatusFilter === 'LOW' && isLow) ||
      (inventoryStatusFilter === 'NORMAL' && !isLow);
    return matchSearch && matchStatus;
  });

  // Report Orders & Calculations for Selected Date Range
  const reportOrders = orders.filter(o => {
    const oDate = getOrderDate(o);
    return oDate >= dateFrom && oDate <= dateTo;
  });

  const periodRevenue = reportOrders.reduce((sum, o) => sum + (o.status !== 'CANCELLED' ? o.total : 0), 0);
  const periodSuccessOrders = reportOrders.filter(o => o.status !== 'CANCELLED').length;
  const periodCancelledOrders = reportOrders.filter(o => o.status === 'CANCELLED').length;
  const periodAOV = periodSuccessOrders > 0 ? Math.round(periodRevenue / periodSuccessOrders) : 0;
  const periodDiscounts = reportOrders.reduce((sum, o) => sum + (o.discountAmount || 0), 0);

  // Report breakdowns
  const paymentBreakdown = { CASH: 0, VNPAY: 0, MOMO: 0, BANKING: 0 };
  reportOrders.forEach(o => {
    if (o.status !== 'CANCELLED') {
      const pm = (o.paymentMethod || 'CASH').toUpperCase();
      paymentBreakdown[pm] = (paymentBreakdown[pm] || 0) + o.total;
    }
  });

  const categoryBreakdown = { COFFEE: 0, TEA: 0, MATCHA: 0, SMOOTHIE: 0 };
  reportOrders.forEach(o => {
    if (o.status !== 'CANCELLED') {
      o.items.forEach(it => {
        const foundItem = menu.find(m => m.name.toLowerCase() === it.name.toLowerCase() || m.id === it.drinkId);
        const cat = foundItem ? foundItem.category : 'COFFEE';
        categoryBreakdown[cat] = (categoryBreakdown[cat] || 0) + (it.unitPrice * it.qty);
      });
    }
  });

  const periodDrinkSales = {};
  reportOrders.forEach(o => {
    if (o.status !== 'CANCELLED') {
      o.items.forEach(i => {
        periodDrinkSales[i.name] = (periodDrinkSales[i.name] || 0) + i.qty;
      });
    }
  });
  const periodTopDrinks = Object.entries(periodDrinkSales).sort((a, b) => b[1] - a[1]);

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

  // User Handlers (Yêu cầu 5: ĐÃ BỎ chức năng khóa tài khoản)
  const handleOpenAddUser = () => {
    setEditingUser(null);
    setUserForm({ username: '', password: '123', role: 'CASHIER', fullName: '' });
    setUserModalOpen(true);
  };

  const handleOpenEditUser = (u) => {
    setEditingUser(u);
    setUserForm({ username: u.username, password: u.password, role: u.role, fullName: u.fullName || u.username });
    setUserModalOpen(true);
  };

  const handleSaveUser = (e) => {
    e.preventDefault();
    if (!userForm.username.trim()) return;

    if (editingUser) {
      setUsers(prev => prev.map(u => u.id === editingUser.id ? { ...u, ...userForm, active: true } : u));
    } else {
      const newId = Math.max(...users.map(u => u.id || 0), 0) + 1;
      const newUser = {
        id: newId,
        username: userForm.username.trim(),
        password: userForm.password,
        role: userForm.role,
        fullName: userForm.fullName || userForm.username,
        active: true
      };
      setUsers(prev => [...prev, newUser]);
    }
    setUserModalOpen(false);
  };

  const handleDeleteUser = (userId) => {
    if (users.length <= 1) {
      alert("Hệ thống phải có ít nhất 1 tài khoản nhân viên.");
      return;
    }
    if (window.confirm("Bạn có chắc chắn muốn xóa tài khoản này?")) {
      setUsers(prev => prev.filter(u => u.id !== userId));
    }
  };

  // Quản lý trạng thái đơn hàng (Use case 6)
  const handleUpdateOrderStatus = (orderId, nextState) => {
    if (onTransitionState) {
      onTransitionState(orderId, nextState);
    } else if (setOrders) {
      setOrders(prev => prev.map(o => o.id === orderId ? { ...o, status: nextState } : o));
    }
  };

  // Xuất file Báo cáo CSV theo khoảng thời gian đã chọn (Yêu cầu 6)
  const handleExportReportCSV = () => {
    let csv = `\uFEFFBÁO CÁO DOANH THU PURRCOFFEE POS\n`;
    csv += `Thời gian báo cáo:,"Từ ngày ${dateFrom} Đến ngày ${dateTo}"\n`;
    csv += `Tổng doanh thu:,"${formatVND(periodRevenue)}"\n`;
    csv += `Số đơn thành công:,"${periodSuccessOrders}"\n`;
    csv += `Số đơn hủy:,"${periodCancelledOrders}"\n`;
    csv += `Tổng chiết khấu/khuyến mãi:,"${formatVND(periodDiscounts)}"\n\n`;
    csv += `Mã Đơn,Ngày,Giờ,Hình Thức,Cổng TT,Tạm Tính (VND),Giảm Giá (VND),Tổng Tiền (VND),Trạng Thái,Chi Tiết Món\n`;

    reportOrders.forEach(o => {
      const itemsStr = o.items ? o.items.map(i => `${i.qty}x ${i.name} (${i.size})`).join('; ') : '';
      csv += `${o.id},"${getOrderDate(o)}","${o.createdAt}","${o.orderType}","${o.paymentMethod || 'CASH'}",${o.subtotal || o.total},${o.discountAmount || 0},${o.total},"${o.status}","${itemsStr.replace(/"/g, '""')}"\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `BaoCao_DoanhThu_${dateFrom}_den_${dateTo}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="flex-1 p-4 sm:p-6 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-black text-[#2b170c] tracking-tight">Hệ Thống Quản Trị Trung Tâm</h2>
          <p className="text-xs text-[#8a7668]">
            Đồng bộ chuẩn Use Case: Đơn Hàng, Thực Đơn, Topping, Kho Hàng, Báo Cáo & Nhân Viên.
          </p>
        </div>

        {/* 9 Tab Switcher */}
        <div className="flex items-center gap-1 bg-[#f4eae3] p-1.5 rounded-2xl overflow-x-auto scrollbar-none">
          {[
            { id: 'overview', label: '📊 Tổng Quan' },
            { id: 'orders', label: '🧾 Đơn Hàng' },
            { id: 'reports', label: '📈 Báo Cáo Doanh Thu' },
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
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer ${
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

      {/* ========================================================================= */}
      {/* TAB 1: OVERVIEW */}
      {/* ========================================================================= */}
      {adminTab === 'overview' && (
        <div className="space-y-6">
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
                      className="px-3 py-1.5 rounded-lg bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold shadow-2xs cursor-pointer"
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

      {/* ========================================================================= */}
      {/* TAB 2: ORDERS (Có lọc trạng thái, tìm kiếm, cập nhật trạng thái, xem lại hóa đơn) */}
      {/* ========================================================================= */}
      {adminTab === 'orders' && (
        <div className="pos-card rounded-3xl p-5 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">
                Quản Lý Đơn Hàng & Lịch Sử Giao Dịch
              </h3>
              <p className="text-xs text-[#8a7668]">
                Xem lại hóa đơn, lọc trạng thái, cập nhật quy trình làm món và hủy đơn hợp lệ.
              </p>
            </div>
            <span className="text-xs font-bold text-[#583115] bg-[#f7ebe3] px-3 py-1.5 rounded-xl self-start">
              Hiển thị: {filteredOrders.length} / {orders.length} đơn
            </span>
          </div>

          {/* Controls Bar: Search & Status Filter */}
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 pt-2 pb-1 border-t border-[#f4eae3]">
            {/* Status Pills */}
            <div className="flex items-center gap-1.5 overflow-x-auto scrollbar-none pb-1">
              {[
                { id: 'ALL', label: `Tất cả (${orders.length})` },
                { id: 'PENDING', label: `Chờ pha chế (${orders.filter(o => o.status === 'PENDING').length})`, color: 'text-amber-800' },
                { id: 'PREPARING', label: `Đang làm (${orders.filter(o => o.status === 'PREPARING').length})`, color: 'text-blue-800' },
                { id: 'READY', label: `Sẵn sàng (${orders.filter(o => o.status === 'READY').length})`, color: 'text-emerald-800' },
                { id: 'CANCELLED', label: `Đã hủy (${orders.filter(o => o.status === 'CANCELLED').length})`, color: 'text-red-800' }
              ].map(st => (
                <button
                  key={st.id}
                  onClick={() => setOrderStatusFilter(st.id)}
                  className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer ${
                    orderStatusFilter === st.id
                      ? 'bg-[#3e200a] text-white shadow-xs'
                      : 'bg-white border border-[#ede4db] text-[#583115] hover:bg-[#fffaf6]'
                  }`}
                >
                  {st.label}
                </button>
              ))}
            </div>

            {/* Search Input */}
            <div className="relative w-full md:w-72">
              <Search className="w-4 h-4 text-[#8a7668] absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={orderSearchQuery}
                onChange={(e) => setOrderSearchQuery(e.target.value)}
                placeholder="Tìm mã đơn, món, cổng TT..."
                className="w-full pl-9 pr-3 py-2 rounded-xl bg-white border border-[#ede4db] focus:border-[#a36538] text-xs text-[#2b170c] outline-none"
              />
            </div>
          </div>

          {/* Orders Table */}
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2.5 px-3">Mã đơn</th>
                  <th className="py-2.5 px-3">Thời gian</th>
                  <th className="py-2.5 px-3">Chi tiết món</th>
                  <th className="py-2.5 px-3">Cổng TT</th>
                  <th className="py-2.5 px-3">Tổng tiền</th>
                  <th className="py-2.5 px-3">Trạng thái</th>
                  <th className="py-2.5 px-3 text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {filteredOrders.length > 0 ? (
                  filteredOrders.map((o) => (
                    <tr key={o.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                      <td className="py-3 px-3 text-sm font-bold text-[#583115]">#{o.id}</td>
                      <td className="py-3 px-3 text-xs text-gray-500 whitespace-nowrap">
                        <div>{o.createdAt}</div>
                        <div className="text-[10px] text-gray-400 font-mono">{getOrderDate(o)}</div>
                      </td>
                      <td className="py-3 px-3 text-xs font-medium text-gray-800 max-w-xs">
                        {o.items.map(i => `${i.qty}x ${i.name} (${i.size})`).join(', ')}
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className="text-[11px] font-bold px-2 py-0.5 rounded bg-gray-100 text-gray-700">
                          {o.paymentMethod || 'CASH'}
                        </span>
                      </td>
                      <td className="py-3 px-3 text-sm font-black text-[#7d4924] whitespace-nowrap">{formatVND(o.total)}</td>
                      <td className="py-3 px-3 whitespace-nowrap">
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
                      <td className="py-3 px-3 text-right whitespace-nowrap">
                        <div className="flex items-center justify-end gap-1.5">
                          {/* Nút Xem Lại Hóa Đơn (Yêu cầu 3) */}
                          <button
                            onClick={() => setSelectedReceiptOrder(o)}
                            className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-amber-50 text-amber-900 hover:bg-amber-100 border border-amber-200 transition-all flex items-center gap-1 cursor-pointer"
                            title="In / Xem lại hóa đơn"
                          >
                            <FileText className="w-3.5 h-3.5 text-[#a36538]" />
                            <span>Hóa đơn</span>
                          </button>

                          {/* Cập nhật trạng thái chuyển giao (Use case 6) */}
                          {o.status === 'PENDING' && (
                            <button
                              onClick={() => handleUpdateOrderStatus(o.id, 'PREPARING')}
                              className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200 transition-all cursor-pointer"
                              title="Bắt đầu pha chế"
                            >
                              Làm món
                            </button>
                          )}

                          {o.status === 'PREPARING' && (
                            <button
                              onClick={() => handleUpdateOrderStatus(o.id, 'READY')}
                              className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200 transition-all cursor-pointer"
                              title="Hoàn tất đơn"
                            >
                              Xong
                            </button>
                          )}

                          {/* Hủy đơn hợp lệ */}
                          {o.status !== 'CANCELLED' && (
                            <button
                              onClick={() => onCancelOrder(o.id)}
                              className="px-2 py-1 rounded-lg text-xs font-semibold bg-red-50 text-red-700 hover:bg-red-100 border border-red-200 transition-all cursor-pointer"
                              title="Hủy đơn hàng"
                            >
                              Hủy
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={7} className="py-8 text-center text-xs text-gray-400">
                      Không tìm thấy đơn hàng nào phù hợp với bộ lọc.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 3: REPORTS (Báo cáo doanh thu & Chọn khoảng thời gian - Yêu cầu 6) */}
      {/* ========================================================================= */}
      {adminTab === 'reports' && (
        <div className="space-y-6">
          {/* Header & Date Range Selector */}
          <div className="pos-card rounded-3xl p-5 space-y-4">
            <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
              <div>
                <h3 className="font-extrabold text-[#2b170c] text-lg flex items-center gap-2">
                  <BarChart2 className="w-5 h-5 text-[#a36538]" />
                  <span>Báo Cáo & Thống Kê Doanh Thu Theo Khoảng Thời Gian</span>
                </h3>
                <p className="text-xs text-[#8a7668] mt-0.5">
                  Chọn mốc thời gian để xem phân tích số liệu, cơ cấu doanh thu và xuất báo cáo CSV/Excel.
                </p>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-2">
                <button
                  onClick={handleExportReportCSV}
                  className="px-4 py-2 rounded-xl bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold transition-all shadow-xs flex items-center gap-1.5 cursor-pointer"
                  title="Xuất file CSV theo khoảng thời gian"
                >
                  <Download className="w-4 h-4" />
                  <span>Xuất Báo Cáo (CSV)</span>
                </button>
                <button
                  onClick={() => window.print()}
                  className="px-3.5 py-2 rounded-xl bg-white border border-[#ede4db] hover:bg-[#fffaf6] text-[#583115] text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer"
                  title="In báo cáo"
                >
                  <Printer className="w-4 h-4" />
                  <span>In Báo Cáo</span>
                </button>
              </div>
            </div>

            {/* Date Range Controls */}
            <div className="p-4 rounded-2xl bg-[#fffaf6] border border-[#ede4db] flex flex-col md:flex-row md:items-center justify-between gap-4">
              {/* Preset Buttons */}
              <div className="flex items-center gap-1.5 overflow-x-auto scrollbar-none">
                <span className="text-xs font-bold text-[#8a7668] mr-1 flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5" />
                  <span>Kỳ:</span>
                </span>
                {[
                  { id: 'TODAY', label: 'Hôm nay' },
                  { id: 'YESTERDAY', label: 'Hôm qua' },
                  { id: '7DAYS', label: '7 ngày qua' },
                  { id: 'THIS_MONTH', label: 'Tháng này' },
                  { id: 'ALL', label: 'Toàn bộ' }
                ].map(p => (
                  <button
                    key={p.id}
                    onClick={() => handleApplyDatePreset(p.id)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                      datePreset === p.id
                        ? 'bg-[#583115] text-white shadow-xs'
                        : 'bg-white border border-[#ede4db] text-[#583115] hover:bg-[#f7ebe3]'
                    }`}
                  >
                    {p.label}
                  </button>
                ))}
              </div>

              {/* Date From - To Pickers */}
              <div className="flex items-center gap-2 text-xs">
                <span className="text-[#8a7668] font-bold">Từ:</span>
                <input
                  type="date"
                  value={dateFrom}
                  onChange={(e) => {
                    setDateFrom(e.target.value);
                    setDatePreset('CUSTOM');
                  }}
                  className="px-3 py-1.5 rounded-xl border border-[#ede4db] bg-white text-xs font-semibold text-[#2b170c] outline-none focus:border-[#a36538]"
                />
                <span className="text-[#8a7668] font-bold">Đến:</span>
                <input
                  type="date"
                  value={dateTo}
                  onChange={(e) => {
                    setDateTo(e.target.value);
                    setDatePreset('CUSTOM');
                  }}
                  className="px-3 py-1.5 rounded-xl border border-[#ede4db] bg-white text-xs font-semibold text-[#2b170c] outline-none focus:border-[#a36538]"
                />
              </div>
            </div>
          </div>

          {/* Period Summary KPI Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <div className="pos-card rounded-2xl p-4 border-l-4 border-l-emerald-600 bg-white">
              <p className="text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">Doanh Thu Kỳ Này</p>
              <h3 className="text-xl sm:text-2xl font-black text-[#2b170c] mt-1">{formatVND(periodRevenue)}</h3>
              <p className="text-[10px] text-emerald-700 font-semibold mt-1">Từ {dateFrom} đến {dateTo}</p>
            </div>
            <div className="pos-card rounded-2xl p-4 border-l-4 border-l-blue-600 bg-white">
              <p className="text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">Số Đơn Hàng</p>
              <h3 className="text-xl sm:text-2xl font-black text-blue-900 mt-1">{reportOrders.length} đơn</h3>
              <p className="text-[10px] text-blue-700 font-semibold mt-1">{periodSuccessOrders} thành công, {periodCancelledOrders} hủy</p>
            </div>
            <div className="pos-card rounded-2xl p-4 border-l-4 border-l-amber-600 bg-white">
              <p className="text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">Giá Trị TB / Đơn (AOV)</p>
              <h3 className="text-xl sm:text-2xl font-black text-amber-900 mt-1">{formatVND(periodAOV)}</h3>
              <p className="text-[10px] text-amber-700 font-semibold mt-1">Bình quân mỗi giao dịch</p>
            </div>
            <div className="pos-card rounded-2xl p-4 border-l-4 border-l-purple-600 bg-white">
              <p className="text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">Tổng Giảm Giá</p>
              <h3 className="text-xl sm:text-2xl font-black text-purple-900 mt-1">{formatVND(periodDiscounts)}</h3>
              <p className="text-[10px] text-purple-700 font-semibold mt-1">Khuyến mãi & Voucher</p>
            </div>
          </div>

          {/* Revenue Breakdown by Payment Method & Categories */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* By Payment Method */}
            <div className="pos-card rounded-3xl p-5 bg-white">
              <h4 className="font-extrabold text-[#2b170c] text-sm mb-3 flex items-center justify-between">
                <span>💳 Theo Cổng Thanh Toán</span>
                <span className="text-[11px] text-[#8a7668]">Adapter Pattern</span>
              </h4>
              <div className="space-y-3">
                {[
                  { name: 'Tiền mặt (CASH)', amount: paymentBreakdown.CASH || 0, color: 'bg-emerald-500' },
                  { name: 'VNPay QR (VNPAY)', amount: paymentBreakdown.VNPAY || 0, color: 'bg-blue-500' },
                  { name: 'Ví MoMo (MOMO)', amount: paymentBreakdown.MOMO || 0, color: 'bg-pink-500' },
                  { name: 'Chuyển khoản (BANKING)', amount: paymentBreakdown.BANKING || 0, color: 'bg-purple-500' }
                ].map(pm => {
                  const pct = periodRevenue > 0 ? Math.round((pm.amount / periodRevenue) * 100) : 0;
                  return (
                    <div key={pm.name} className="space-y-1">
                      <div className="flex justify-between text-xs font-semibold">
                        <span className="text-[#2b170c]">{pm.name}</span>
                        <span className="text-[#583115] font-bold">{formatVND(pm.amount)} ({pct}%)</span>
                      </div>
                      <div className="w-full bg-gray-100 h-2 rounded-full overflow-hidden">
                        <div className={`h-full ${pm.color}`} style={{ width: `${pct}%` }} />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* By Category */}
            <div className="pos-card rounded-3xl p-5 bg-white">
              <h4 className="font-extrabold text-[#2b170c] text-sm mb-3 flex items-center justify-between">
                <span>☕ Theo Nhóm Đồ Uống</span>
                <span className="text-[11px] text-[#8a7668]">Factory Method</span>
              </h4>
              <div className="space-y-3">
                {[
                  { name: 'Cà phê (COFFEE)', amount: categoryBreakdown.COFFEE || 0, color: 'bg-[#7d4924]' },
                  { name: 'Trà trái cây (TEA)', amount: categoryBreakdown.TEA || 0, color: 'bg-amber-500' },
                  { name: 'Matcha (MATCHA)', amount: categoryBreakdown.MATCHA || 0, color: 'bg-emerald-600' },
                  { name: 'Sinh tố (SMOOTHIE)', amount: categoryBreakdown.SMOOTHIE || 0, color: 'bg-rose-500' }
                ].map(cat => {
                  const totalCatRev = Object.values(categoryBreakdown).reduce((a, b) => a + b, 0);
                  const pct = totalCatRev > 0 ? Math.round((cat.amount / totalCatRev) * 100) : 0;
                  return (
                    <div key={cat.name} className="space-y-1">
                      <div className="flex justify-between text-xs font-semibold">
                        <span className="text-[#2b170c]">{cat.name}</span>
                        <span className="text-[#583115] font-bold">{formatVND(cat.amount)} ({pct}%)</span>
                      </div>
                      <div className="w-full bg-gray-100 h-2 rounded-full overflow-hidden">
                        <div className={`h-full ${cat.color}`} style={{ width: `${pct}%` }} />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Top 5 Drinks in Selected Period */}
            <div className="pos-card rounded-3xl p-5 bg-white">
              <h4 className="font-extrabold text-[#2b170c] text-sm mb-3 flex items-center justify-between">
                <span>🔥 Top Món Bán Chạy Trong Kỳ</span>
                <span className="text-[11px] text-[#8a7668]">{periodTopDrinks.length} món</span>
              </h4>
              <div className="space-y-2.5">
                {periodTopDrinks.length > 0 ? (
                  periodTopDrinks.slice(0, 5).map(([name, qty], idx) => (
                    <div key={idx} className="flex items-center justify-between p-2 rounded-xl bg-[#fffaf6] border border-[#ede4db]">
                      <span className="text-xs font-bold text-[#2b170c] truncate max-w-[140px]">{name}</span>
                      <span className="text-xs font-black text-[#7d4924] bg-white px-2 py-0.5 rounded border border-[#eed5c7]">
                        {qty} ly
                      </span>
                    </div>
                  ))
                ) : (
                  <p className="text-xs text-gray-400 py-6 text-center">Không có đơn hàng trong khoảng ngày này.</p>
                )}
              </div>
            </div>
          </div>

          {/* Transactions Breakdown for Selected Date Range */}
          <div className="pos-card rounded-3xl p-5 bg-white">
            <div className="flex items-center justify-between mb-4">
              <h4 className="font-extrabold text-[#2b170c] text-base">
                Danh Sách Giao Dịch Trong Khoảng Thời Gian Đã Chọn
              </h4>
              <span className="text-xs text-[#8a7668]">Tổng cộng {reportOrders.length} giao dịch</span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                    <th className="py-2 px-3">Mã đơn</th>
                    <th className="py-2 px-3">Ngày</th>
                    <th className="py-2 px-3">Giờ</th>
                    <th className="py-2 px-3">Chi tiết món</th>
                    <th className="py-2 px-3">Cổng TT</th>
                    <th className="py-2 px-3">Giảm giá</th>
                    <th className="py-2 px-3">Tổng tiền</th>
                    <th className="py-2 px-3">Trạng thái</th>
                    <th className="py-2 px-3 text-right">Xem Hóa Đơn</th>
                  </tr>
                </thead>
                <tbody>
                  {reportOrders.length > 0 ? (
                    reportOrders.map(o => (
                      <tr key={o.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] text-xs">
                        <td className="py-2.5 px-3 font-bold text-[#583115]">#{o.id}</td>
                        <td className="py-2.5 px-3 font-mono text-gray-500">{getOrderDate(o)}</td>
                        <td className="py-2.5 px-3 text-gray-500">{o.createdAt}</td>
                        <td className="py-2.5 px-3 font-medium text-gray-800 max-w-xs">
                          {o.items ? o.items.map(i => `${i.qty}x ${i.name}`).join(', ') : ''}
                        </td>
                        <td className="py-2.5 px-3">
                          <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-gray-100 text-gray-700">
                            {o.paymentMethod || 'CASH'}
                          </span>
                        </td>
                        <td className="py-2.5 px-3 text-red-600 font-semibold">
                          {o.discountAmount > 0 ? `-${formatVND(o.discountAmount)}` : '0 ₫'}
                        </td>
                        <td className="py-2.5 px-3 font-black text-[#7d4924]">{formatVND(o.total)}</td>
                        <td className="py-2.5 px-3">
                          <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
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
                        <td className="py-2.5 px-3 text-right">
                          <button
                            onClick={() => setSelectedReceiptOrder(o)}
                            className="px-2 py-1 rounded-lg text-xs font-semibold bg-amber-50 text-amber-900 hover:bg-amber-100 border border-amber-200 transition-all cursor-pointer"
                          >
                            Xem Hóa Đơn
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={9} className="py-6 text-center text-xs text-gray-400">
                        Không có đơn hàng nào trong khoảng thời gian này.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 4: MENU CATALOG (Có tìm kiếm món trong thực đơn - Yêu cầu 2) */}
      {/* ========================================================================= */}
      {adminTab === 'menu' && (
        <div className="pos-card rounded-3xl p-5 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">Danh Mục Đồ Uống (Beverage Catalog)</h3>
              <p className="text-xs text-[#8a7668]">Quản lý danh sách đồ uống, giá bán, tìm kiếm và phân loại (Factory Method).</p>
            </div>
            <button
              onClick={handleOpenAddBeverage}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-[#3e200a] text-white font-bold text-xs hover:bg-[#583115] shadow-xs cursor-pointer self-start"
            >
              <Plus className="w-4 h-4" />
              Thêm Đồ Uống Mới
            </button>
          </div>

          {/* Controls Bar: Search & Category Filter (Yêu cầu 2) */}
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 pt-2 pb-1 border-t border-[#f4eae3]">
            {/* Category Pills */}
            <div className="flex items-center gap-1.5 overflow-x-auto scrollbar-none pb-1">
              {[
                { id: 'ALL', label: `Tất cả (${menu.length})` },
                { id: 'COFFEE', label: 'Cà phê' },
                { id: 'TEA', label: 'Trà trái cây' },
                { id: 'MATCHA', label: 'Matcha' },
                { id: 'SMOOTHIE', label: 'Sinh tố' }
              ].map(cat => (
                <button
                  key={cat.id}
                  onClick={() => setMenuCategoryFilter(cat.id)}
                  className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer ${
                    menuCategoryFilter === cat.id
                      ? 'bg-[#3e200a] text-white shadow-xs'
                      : 'bg-white border border-[#ede4db] text-[#583115] hover:bg-[#fffaf6]'
                  }`}
                >
                  {cat.label}
                </button>
              ))}
            </div>

            {/* Search Input for Menu */}
            <div className="relative w-full md:w-72">
              <Search className="w-4 h-4 text-[#8a7668] absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={menuSearchQuery}
                onChange={(e) => setMenuSearchQuery(e.target.value)}
                placeholder="Tìm kiếm món trong thực đơn..."
                className="w-full pl-9 pr-3 py-2 rounded-xl bg-white border border-[#ede4db] focus:border-[#a36538] text-xs text-[#2b170c] outline-none"
              />
            </div>
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
                {filteredMenu.length > 0 ? (
                  filteredMenu.map((b) => (
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
                            title="Sửa món"
                          >
                            <Edit className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleDeleteBeverage(b.id)}
                            className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 cursor-pointer"
                            title="Xóa món"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={6} className="py-8 text-center text-xs text-gray-400">
                      Không tìm thấy món nào phù hợp với từ khóa "{menuSearchQuery}".
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 5: TOPPING CATALOG */}
      {/* ========================================================================= */}
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
                        {t.active ? 'Khả dụng' : 'Tạm tắt'}
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

      {/* ========================================================================= */}
      {/* TAB 6: INVENTORY & STOCK LEDGER (Có tìm kiếm nguyên vật liệu - Yêu cầu 4) */}
      {/* ========================================================================= */}
      {adminTab === 'inventory' && (
        <div className="space-y-6">
          <div className="pos-card rounded-3xl p-5 space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div>
                <h3 className="font-extrabold text-[#2b170c] text-base">Kho Nguyên Vật Liệu (Inventory Items)</h3>
                <p className="text-xs text-[#8a7668]">Tự động trừ kho theo định lượng Recipe khi làm món. Tìm kiếm và nhập kho.</p>
              </div>
              <span className="text-xs font-bold text-[#583115] bg-[#f7ebe3] px-3 py-1.5 rounded-xl self-start">
                Hiển thị: {filteredInventory.length} / {inventory.length} nguyên liệu
              </span>
            </div>

            {/* Controls Bar: Search & Status Filter (Yêu cầu 4) */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 pt-2 pb-1 border-t border-[#f4eae3]">
              {/* Filter Pills */}
              <div className="flex items-center gap-1.5 overflow-x-auto scrollbar-none pb-1">
                {[
                  { id: 'ALL', label: `Tất cả (${inventory.length})` },
                  { id: 'LOW', label: `⚠️ Cảnh báo thiếu (${inventory.filter(i => i.quantity <= i.min).length})` },
                  { id: 'NORMAL', label: `✅ Đủ hàng (${inventory.filter(i => i.quantity > i.min).length})` }
                ].map(st => (
                  <button
                    key={st.id}
                    onClick={() => setInventoryStatusFilter(st.id)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer ${
                      inventoryStatusFilter === st.id
                        ? 'bg-[#3e200a] text-white shadow-xs'
                        : 'bg-white border border-[#ede4db] text-[#583115] hover:bg-[#fffaf6]'
                    }`}
                  >
                    {st.label}
                  </button>
                ))}
              </div>

              {/* Search Input for Inventory */}
              <div className="relative w-full md:w-72">
                <Search className="w-4 h-4 text-[#8a7668] absolute left-3 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  value={inventorySearchQuery}
                  onChange={(e) => setInventorySearchQuery(e.target.value)}
                  placeholder="Tìm nguyên vật liệu theo tên, đơn vị..."
                  className="w-full pl-9 pr-3 py-2 rounded-xl bg-white border border-[#ede4db] focus:border-[#a36538] text-xs text-[#2b170c] outline-none"
                />
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
                  {filteredInventory.length > 0 ? (
                    filteredInventory.map((inv) => (
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
                    ))
                  ) : (
                    <tr>
                      <td colSpan={6} className="py-8 text-center text-xs text-gray-400">
                        Không tìm thấy nguyên liệu nào phù hợp với "{inventorySearchQuery}".
                      </td>
                    </tr>
                  )}
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

      {/* ========================================================================= */}
      {/* TAB 7: RECIPES */}
      {/* ========================================================================= */}
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

      {/* ========================================================================= */}
      {/* TAB 8: USERS (ĐÃ XÓA tính năng khóa tài khoản - Yêu cầu 5) */}
      {/* ========================================================================= */}
      {adminTab === 'users' && (
        <div className="pos-card rounded-3xl p-5">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-extrabold text-[#2b170c] text-base">Quản Lý Tài Khoản Nhân Viên (Users & Roles)</h3>
              <p className="text-xs text-[#8a7668]">Phân quyền hệ thống: Quản trị (ADMIN), Thu ngân (CASHIER), Bếp (KITCHEN).</p>
            </div>
            <button
              onClick={handleOpenAddUser}
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
                  <th className="py-2.5 px-4 text-right">Thao tác</th>
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
                      <span className="text-xs font-bold px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800">
                        Hoạt động
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => handleOpenEditUser(u)}
                          className="p-1.5 rounded-lg text-blue-600 hover:bg-blue-50 cursor-pointer"
                          title="Sửa nhân viên"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteUser(u.id)}
                          className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 cursor-pointer"
                          title="Xóa nhân viên"
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

      {/* ========================================================================= */}
      {/* TAB 9: SQLITE BACKUP & RESTORE */}
      {/* ========================================================================= */}
      {adminTab === 'backup' && (
        <div className="pos-card rounded-3xl p-6 space-y-6">
          <div>
            <h3 className="font-extrabold text-[#2b170c] text-lg">Hệ Thống Sao Lưu & Phục Hồi Dữ Liệu SQLite</h3>
            <p className="text-xs text-[#8a7668] mt-1">
              Xuất tệp sao lưu dữ liệu toàn diện (JSON) hoặc đặt lại dữ liệu gốc của hệ thống.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
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
                Tải Xuống Bản Backup (.json)
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
                  if (window.confirm("Bạn có chắc chắn muốn đặt lại toàn bộ hệ thống về dữ liệu mặc định ban đầu?")) {
                    localStorage.clear();
                    window.location.reload();
                  }
                }}
                className="mt-4 w-full py-2.5 rounded-xl bg-red-600 hover:bg-red-700 text-white text-xs font-bold transition-all shadow-xs cursor-pointer"
              >
                Khôi Phục Dữ Liệu Mặc Định
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* MODALS */}
      {/* ========================================================================= */}

      {/* MODAL 1: BEVERAGE ADD / EDIT */}
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
                <label className="block text-xs font-bold text-[#583115] mb-1">Tên Đồ Uống:</label>
                <input
                  type="text"
                  required
                  value={bevForm.name}
                  onChange={(e) => setBevForm({ ...bevForm, name: e.target.value })}
                  placeholder="ví dụ: Trà hoa cúc mật ong"
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Phân Loại (Category):</label>
                <select
                  value={bevForm.category}
                  onChange={(e) => setBevForm({ ...bevForm, category: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-sm outline-none focus:border-[#7d4924]"
                >
                  <option value="COFFEE">COFFEE (Cà phê)</option>
                  <option value="TEA">TEA (Trà hoa quả)</option>
                  <option value="MATCHA">MATCHA (Trà xanh)</option>
                  <option value="SMOOTHIE">SMOOTHIE (Sinh tố)</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1">Đơn Giá Gốc (Size M - VND):</label>
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
              <div className="flex items-center gap-2 pt-2">
                <input
                  type="checkbox"
                  id="bevActiveCheck"
                  checked={bevForm.active}
                  onChange={(e) => setBevForm({ ...bevForm, active: e.target.checked })}
                  className="w-4 h-4 text-[#7d4924]"
                />
                <label htmlFor="bevActiveCheck" className="text-xs font-bold text-[#2b170c]">
                  Mở bán trực tiếp trên màn hình POS
                </label>
              </div>
              <div className="flex items-center justify-end gap-2 pt-4 border-t border-[#f4eae3]">
                <button
                  type="button"
                  onClick={() => setBeverageModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700 cursor-pointer"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#3e200a] text-white hover:bg-[#583115] cursor-pointer"
                >
                  Lưu Đồ Uống
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: TOPPING ADD / EDIT */}
      {toppingModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-md overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
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
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700 cursor-pointer"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#3e200a] text-white hover:bg-[#583115] cursor-pointer"
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
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700 cursor-pointer"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-emerald-700 text-white hover:bg-emerald-800 cursor-pointer"
                >
                  Xác Nhận Nhập Kho
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 4: USER ADD / EDIT */}
      {userModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-[#ede4db] mb-3">
              <h3 className="font-extrabold text-base text-[#2b170c]">
                {editingUser ? 'Chỉnh Sửa Nhân Viên' : 'Thêm Tài Khoản Nhân Viên'}
              </h3>
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
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-gray-100 text-gray-700 cursor-pointer"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#3e200a] text-white hover:bg-[#583115] cursor-pointer"
                >
                  {editingUser ? 'Lưu Thay Đổi' : 'Tạo Tài Khoản'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 5: XEM LẠI HÓA ĐƠN TRONG ADMIN (Yêu cầu 3 - Use case 6) */}
      {selectedReceiptOrder && (
        <ReceiptModal
          order={selectedReceiptOrder}
          onClose={() => setSelectedReceiptOrder(null)}
        />
      )}

    </div>
  );
}
