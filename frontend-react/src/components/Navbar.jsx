import React from 'react';
import { Coffee, UtensilsCrossed, BarChart3, Wifi, UserCheck } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab }) {
  return (
    <header className="h-16 bg-white border-b border-[#ede4db] px-6 flex items-center justify-between sticky top-0 z-30 shadow-xs">
      <div className="flex items-center gap-6">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-[#583115] to-[#2b170c] flex items-center justify-center text-white shadow-md">
            <Coffee className="w-5 h-5" />
          </div>
          <div>
            <h1 className="font-extrabold text-[#2b170c] text-lg leading-tight tracking-tight">PurrCoffee POS</h1>
            <p className="text-[11px] font-medium text-[#a36538]">React POS Edition</p>
          </div>
        </div>

        {/* Tab Switcher */}
        <nav className="hidden md:flex items-center gap-1.5 bg-[#f7ebe3] p-1 rounded-2xl">
          <button
            data-tab-target="pos"
            onClick={() => setActiveTab('pos')}
            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              activeTab === 'pos'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede4db]'
            }`}
          >
            <Coffee className="w-4 h-4" />
            Thu Ngân (POS)
          </button>
          <button
            data-tab-target="kitchen"
            onClick={() => setActiveTab('kitchen')}
            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              activeTab === 'kitchen'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede4db]'
            }`}
          >
            <UtensilsCrossed className="w-4 h-4" />
            Màn Hình Bếp (KDS)
          </button>
          <button
            data-tab-target="admin"
            onClick={() => setActiveTab('admin')}
            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold transition-all ${
              activeTab === 'admin'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede4db]'
            }`}
          >
            <BarChart3 className="w-4 h-4" />
            Báo Cáo & Quản Trị
          </button>
        </nav>
      </div>

      <div className="flex items-center gap-4">
        <div className="hidden sm:flex items-center px-3 py-1.5 rounded-full bg-emerald-50 border border-emerald-200">
          <span className="w-2 h-2 rounded-full bg-emerald-500 mr-2 animate-pulse"></span>
          <span className="text-xs font-semibold text-emerald-800">React 19 Active</span>
        </div>

        <div className="flex items-center gap-3 pl-4 border-l border-[#ede4db]">
          <div className="text-right hidden sm:block">
            <p className="text-xs font-bold text-[#2b170c]">Thu Ngân: Nguyễn Văn A</p>
            <p className="text-[10px] text-emerald-600 font-semibold flex items-center justify-end gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Ca làm việc • Trực tuyến
            </p>
          </div>
          <div className="w-9 h-9 rounded-full bg-[#f4eae3] border border-[#d8c8bd] flex items-center justify-center font-bold text-xs text-[#583115]">
            POS
          </div>
        </div>
      </div>
    </header>
  );
}
