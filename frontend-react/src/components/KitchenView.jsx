import React from 'react';
import { RefreshCw, Play, CheckCircle2 } from 'lucide-react';

export default function KitchenView({ orders, onTransitionState }) {
  const pendingOrders = orders.filter(o => o.status === 'PENDING');
  const preparingOrders = orders.filter(o => o.status === 'PREPARING');
  const readyOrders = orders.filter(o => o.status === 'READY');

  const renderCard = (o, nextActionText, nextActionState, btnClass, icon) => (
    <div
      key={o.id}
      className={`pos-card rounded-2xl p-4 mb-3 border-l-4 ${
        o.status === 'PENDING'
          ? 'border-l-amber-500'
          : o.status === 'PREPARING'
          ? 'border-l-blue-500'
          : 'border-l-emerald-500'
      }`}
    >
      <div className="flex items-center justify-between border-b border-[#f4eae3] pb-2 mb-2">
        <span className="font-extrabold text-[#2b170c] text-base">Đơn #{o.id}</span>
        <span
          className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
            o.orderType === 'DINE_IN'
              ? 'bg-[#f4eae3] text-[#583115]'
              : 'bg-amber-100 text-amber-800'
          }`}
        >
          {o.orderType === 'DINE_IN' ? 'Tại quán' : 'Mang đi'} • {o.createdAt}
        </span>
      </div>
      <div className="space-y-1.5 my-2">
        {o.items.map((item, idx) => (
          <div key={idx} className="text-xs">
            <span className="font-bold text-[#3e200a] mr-1">{item.qty}x</span>
            <span className="font-semibold text-gray-800">{item.name} ({item.size})</span>
            {item.toppings && item.toppings.length > 0 && (
              <span className="text-[#8a7668]"> + {item.toppings.join(', ')}</span>
            )}
            {item.note && (
              <div className="text-[11px] text-amber-700 italic font-medium ml-4 mt-0.5">⚠️ {item.note}</div>
            )}
          </div>
        ))}
      </div>
      {nextActionText ? (
        <button
          onClick={() => onTransitionState(o.id, nextActionState)}
          className={`w-full mt-3 py-2 px-3 rounded-xl text-xs font-bold text-white ${btnClass} transition-all shadow-xs flex items-center justify-center gap-1.5 cursor-pointer`}
        >
          {icon}
          {nextActionText}
        </button>
      ) : (
        <div className="w-full mt-3 py-1.5 text-center text-xs font-bold text-emerald-700 bg-emerald-50 rounded-lg flex items-center justify-center gap-1">
          <CheckCircle2 className="w-3.5 h-3.5" />
          Đã sẵn sàng phục vụ
        </div>
      )}
    </div>
  );

  return (
    <div className="flex-1 p-6 overflow-y-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-black text-[#2b170c] tracking-tight">Màn Hình Pha Chế (Kitchen KDS)</h2>
          <p className="text-xs text-[#8a7668]">Theo dõi và cập nhật trạng thái đơn hàng thời gian thực (State Pattern).</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* PENDING */}
        <div className="bg-[#f7f0ea] rounded-3xl p-4 border border-[#ede4db]">
          <div className="flex items-center justify-between mb-3 px-1">
            <span className="font-extrabold text-sm text-amber-900 flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span> Chờ Pha Chế
            </span>
            <span className="w-6 h-6 rounded-full bg-amber-200 text-amber-900 font-extrabold text-xs flex items-center justify-center">
              {pendingOrders.length}
            </span>
          </div>
          <div className="space-y-3 min-h-[400px]">
            {pendingOrders.length ? (
              pendingOrders.map(o => renderCard(o, 'Bắt đầu pha chế', 'PREPARING', 'bg-blue-600 hover:bg-blue-700', <Play className="w-3.5 h-3.5" />))
            ) : (
              <p className="text-center py-10 text-xs text-gray-400">Không có đơn chờ</p>
            )}
          </div>
        </div>

        {/* PREPARING */}
        <div className="bg-[#eef4f9] rounded-3xl p-4 border border-blue-100">
          <div className="flex items-center justify-between mb-3 px-1">
            <span className="font-extrabold text-sm text-blue-900 flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-blue-500 animate-pulse"></span> Đang Pha Chế
            </span>
            <span className="w-6 h-6 rounded-full bg-blue-200 text-blue-900 font-extrabold text-xs flex items-center justify-center">
              {preparingOrders.length}
            </span>
          </div>
          <div className="space-y-3 min-h-[400px]">
            {preparingOrders.length ? (
              preparingOrders.map(o => renderCard(o, 'Hoàn thành món', 'READY', 'bg-emerald-600 hover:bg-emerald-700', <CheckCircle2 className="w-3.5 h-3.5" />))
            ) : (
              <p className="text-center py-10 text-xs text-gray-400">Không có đơn đang làm</p>
            )}
          </div>
        </div>

        {/* READY */}
        <div className="bg-[#edf6f0] rounded-3xl p-4 border border-emerald-100">
          <div className="flex items-center justify-between mb-3 px-1">
            <span className="font-extrabold text-sm text-emerald-900 flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500"></span> Đã Xong / Giao Khách
            </span>
            <span className="w-6 h-6 rounded-full bg-emerald-200 text-emerald-900 font-extrabold text-xs flex items-center justify-center">
              {readyOrders.length}
            </span>
          </div>
          <div className="space-y-3 min-h-[400px]">
            {readyOrders.length ? (
              readyOrders.map(o => renderCard(o, null, null, '', null))
            ) : (
              <p className="text-center py-10 text-xs text-gray-400">Chưa có món hoàn thành</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
