import React, { useState } from 'react';
import { RefreshCw, Play, CheckCircle2, Clock, AlertTriangle, Coffee } from 'lucide-react';
import { formatVND } from '../data/mockData';

export default function KitchenView({ orders, onTransitionState }) {
  const [selectedOrderId, setSelectedOrderId] = useState(null);

  // States
  const pendingOrders = orders.filter(o => o.status === 'PENDING');
  const preparingOrders = orders.filter(o => o.status === 'PREPARING');
  const readyOrders = orders.filter(o => o.status === 'READY');

  const selectedOrder = orders.find(o => o.id === selectedOrderId) || preparingOrders[0] || pendingOrders[0] || readyOrders[0];

  const renderCard = (o, nextActionText, nextActionState, btnClass, icon) => {
    const isSelected = selectedOrder?.id === o.id;

    return (
      <div
        key={o.id}
        onClick={() => setSelectedOrderId(o.id)}
        className={`pos-card rounded-2xl p-4 mb-3 border-l-4 cursor-pointer transition-all ${
          isSelected ? 'ring-2 ring-[#7d4924] shadow-md bg-amber-50/20' : ''
        } ${
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
            onClick={(e) => {
              e.stopPropagation();
              onTransitionState(o.id, nextActionState);
            }}
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
  };

  return (
    <div className="flex-1 p-6 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-black text-[#2b170c] tracking-tight">Màn Hình Pha Chế (Kitchen KDS)</h2>
          <p className="text-xs text-[#8a7668]">
            Kanban Board theo dõi tiến độ pha chế thời gian thực (State Pattern: Pending → Preparing → Ready).
          </p>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs font-bold px-3 py-1.5 rounded-xl bg-[#f7ebe3] text-[#583115]">
            Tổng đơn trong bếp: {pendingOrders.length + preparingOrders.length + readyOrders.length}
          </span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Kanban Board Columns (8 cols) */}
        <div className="lg:col-span-8 grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* PENDING */}
          <div className="bg-[#f7f0ea] rounded-3xl p-4 border border-[#ede4db] flex flex-col">
            <div className="flex items-center justify-between mb-3 px-1">
              <span className="font-extrabold text-sm text-amber-900 flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span> Chờ Pha Chế
              </span>
              <span className="w-6 h-6 rounded-full bg-amber-200 text-amber-900 font-extrabold text-xs flex items-center justify-center">
                {pendingOrders.length}
              </span>
            </div>
            <div className="space-y-3 flex-1 overflow-y-auto max-h-[650px] pr-1">
              {pendingOrders.length ? (
                pendingOrders.map(o => renderCard(o, 'Bắt đầu pha chế', 'PREPARING', 'bg-blue-600 hover:bg-blue-700', <Play className="w-3.5 h-3.5" />))
              ) : (
                <p className="text-center py-12 text-xs text-gray-400">Không có đơn chờ</p>
              )}
            </div>
          </div>

          {/* PREPARING */}
          <div className="bg-[#eef4f9] rounded-3xl p-4 border border-blue-100 flex flex-col">
            <div className="flex items-center justify-between mb-3 px-1">
              <span className="font-extrabold text-sm text-blue-900 flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-blue-500 animate-pulse"></span> Đang Pha Chế
              </span>
              <span className="w-6 h-6 rounded-full bg-blue-200 text-blue-900 font-extrabold text-xs flex items-center justify-center">
                {preparingOrders.length}
              </span>
            </div>
            <div className="space-y-3 flex-1 overflow-y-auto max-h-[650px] pr-1">
              {preparingOrders.length ? (
                preparingOrders.map(o => renderCard(o, 'Hoàn thành món', 'READY', 'bg-emerald-600 hover:bg-emerald-700', <CheckCircle2 className="w-3.5 h-3.5" />))
              ) : (
                <p className="text-center py-12 text-xs text-gray-400">Không có đơn đang làm</p>
              )}
            </div>
          </div>

          {/* READY */}
          <div className="bg-[#edf6f0] rounded-3xl p-4 border border-emerald-100 flex flex-col">
            <div className="flex items-center justify-between mb-3 px-1">
              <span className="font-extrabold text-sm text-emerald-900 flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-500"></span> Giao Khách (Ready)
              </span>
              <span className="w-6 h-6 rounded-full bg-emerald-200 text-emerald-900 font-extrabold text-xs flex items-center justify-center">
                {readyOrders.length}
              </span>
            </div>
            <div className="space-y-3 flex-1 overflow-y-auto max-h-[650px] pr-1">
              {readyOrders.length ? (
                readyOrders.map(o => renderCard(o, null, null, '', null))
              ) : (
                <p className="text-center py-12 text-xs text-gray-400">Chưa có món xong</p>
              )}
            </div>
          </div>
        </div>

        {/* Selected Order Detail Sidebar (4 cols - Exact match to Java Swing KitchenView detail panel) */}
        <div className="lg:col-span-4 bg-white rounded-3xl p-5 border border-[#ede4db] shadow-xs flex flex-col justify-between">
          <div>
            <div className="border-b border-[#f4eae3] pb-4 mb-4">
              <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-[#f7ebe3] text-[#583115]">
                Chi Tiết Đơn Pha Chế
              </span>
              <h3 className="text-xl font-black text-[#2b170c] mt-1.5">
                {selectedOrder ? `Đơn hàng #${selectedOrder.id}` : 'Chọn một đơn hàng'}
              </h3>
              <p className="text-xs text-[#8a7668]">
                {selectedOrder
                  ? `Trạng thái: ${selectedOrder.status} • Giờ tạo: ${selectedOrder.createdAt} (${selectedOrder.orderType === 'DINE_IN' ? 'Tại quán' : 'Mang đi'})`
                  : 'Nhấp vào đơn bất kỳ ở bảng Kanban để xem chi tiết.'}
              </p>
              {selectedOrder && (
                <p className="text-lg font-black text-[#7d4924] mt-2">{formatVND(selectedOrder.total)}</p>
              )}
            </div>

            {selectedOrder ? (
              <div className="space-y-3 max-h-[350px] overflow-y-auto pr-1">
                <p className="text-xs font-bold text-[#583115] uppercase tracking-wider">Danh sách món & công thức:</p>
                {selectedOrder.items.map((it, idx) => (
                  <div key={idx} className="p-3 rounded-2xl bg-[#fffaf6] border border-[#ede4db]">
                    <div className="flex justify-between items-start">
                      <h4 className="font-bold text-sm text-[#2b170c]">
                        {it.qty}x {it.name} <span className="text-xs font-semibold text-[#8a7668]">({it.size})</span>
                      </h4>
                      <span className="text-xs font-black text-[#7d4924]">{formatVND(it.unitPrice * it.qty)}</span>
                    </div>
                    {it.toppings && it.toppings.length > 0 && (
                      <p className="text-xs text-[#7d4924] mt-1">Topping: {it.toppings.join(', ')}</p>
                    )}
                    {it.note && (
                      <p className="text-xs text-amber-700 italic mt-0.5 font-medium">⚠️ Ghi chú: {it.note}</p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <div className="py-20 text-center text-gray-400 text-xs">
                <Coffee className="w-10 h-10 mx-auto mb-2 opacity-30 text-[#7d4924]" />
                <p>Chưa chọn đơn hàng nào</p>
              </div>
            )}
          </div>

          {/* Quick Action Buttons */}
          {selectedOrder && (
            <div className="pt-4 border-t border-[#f4eae3] space-y-2">
              {selectedOrder.status === 'PENDING' && (
                <button
                  onClick={() => onTransitionState(selectedOrder.id, 'PREPARING')}
                  className="w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs shadow-xs flex items-center justify-center gap-1.5 cursor-pointer"
                >
                  <Play className="w-4 h-4" />
                  Bắt đầu pha chế đơn này
                </button>
              )}
              {selectedOrder.status === 'PREPARING' && (
                <button
                  onClick={() => onTransitionState(selectedOrder.id, 'READY')}
                  className="w-full py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-xs flex items-center justify-center gap-1.5 cursor-pointer"
                >
                  <CheckCircle2 className="w-4 h-4" />
                  Hoàn thành món (Chuyển Giao Khách)
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
