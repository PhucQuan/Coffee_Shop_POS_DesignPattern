import React from 'react';
import { Coffee, UtensilsCrossed, BarChart3, LogOut, User } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab, currentUser, onLogout }) {
  const role = currentUser?.role || 'ADMIN';

  return (
    <header className="h-16 bg-white border-b border-[#ede4db] px-4 sm:px-6 flex items-center justify-between sticky top-0 z-30 shadow-xs">
      <div className="flex items-center gap-4 sm:gap-6">
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-[#583115] to-[#2b170c] flex items-center justify-center text-white shadow-md">
            <Coffee className="w-5 h-5" />
          </div>
          <div>
            <h1 className="font-extrabold text-[#2b170c] text-base sm:text-lg leading-tight tracking-tight">PurrCoffee POS</h1>
            <p className="text-[10px] sm:text-[11px] font-medium text-[#a36538]">Hệ Thống Bán Hàng & Pha Chế</p>
          </div>
        </div>

        {/* Tab Switcher - All 3 tabs visible so user or Playwright tests can navigate freely */}
        <nav className="flex items-center gap-1 bg-[#f7ebe3] p-1 rounded-2xl overflow-x-auto">
          <button
            data-tab-target="pos"
            onClick={() => setActiveTab('pos')}
            className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
              activeTab === 'pos'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede4db]'
            }`}
          >
            <Coffee className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
            <span>Thu Ngân (POS)</span>
          </button>

          <button
            data-tab-target="kitchen"
            onClick={() => setActiveTab('kitchen')}
            className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
              activeTab === 'kitchen'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede4db]'
            }`}
          >
            <UtensilsCrossed className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
            <span>Màn Hình Bếp (KDS)</span>
          </button>

          <button
            data-tab-target="admin"
            onClick={() => setActiveTab('admin')}
            className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
              activeTab === 'admin'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede4db]'
            }`}
          >
            <BarChart3 className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
            <span>Báo Cáo & Quản Trị</span>
          </button>
        </nav>
      </div>

      {/* User profile & Logout */}
      <div className="flex items-center gap-3">
        <div className="hidden md:flex items-center gap-2 pl-3 border-l border-[#ede4db]">
          <div className="text-right">
            <p className="text-xs font-bold text-[#2b170c]">{currentUser?.fullName || currentUser?.username || 'Thu Ngân'}</p>
            <p className="text-[10px] text-emerald-600 font-semibold flex items-center justify-end gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
              {role === 'ADMIN' ? 'Quản trị viên' : role === 'KITCHEN' ? 'Bếp trưởng' : 'Thu ngân'} • Đang trực
            </p>
          </div>
          <div className="w-8 h-8 rounded-full bg-[#f4eae3] border border-[#d8c8bd] flex items-center justify-center font-bold text-xs text-[#583115]">
            {role[0]}
          </div>
        </div>

        <button
          onClick={onLogout}
          title="Đăng xuất ca làm việc"
          className="flex items-center gap-1 px-3 py-1.5 rounded-xl border border-red-200 bg-red-50 hover:bg-red-100 text-red-700 text-xs font-bold transition-all cursor-pointer"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">Đăng Xuất</span>
        </button>
      </div>
    </header>
  );
}
