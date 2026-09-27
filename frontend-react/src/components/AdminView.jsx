import React from 'react';
import { INVENTORY_DATA, formatVND } from '../data/mockData';

export default function AdminView({ orders }) {
  const totalRevenue = orders.reduce((sum, o) => sum + (o.status !== 'CANCELLED' ? o.total : 0), 0);
  const totalOrders = orders.length;
  const completedOrders = orders.filter(o => o.status === 'READY').length;

  return (
    <div className="flex-1 p-6 overflow-y-auto">
      <div className="mb-6">
        <h2 className="text-2xl font-black text-[#2b170c] tracking-tight">Báo Cáo & Quản Trị Hệ Thống</h2>
        <p className="text-xs text-[#8a7668]">Tổng hợp doanh thu, đơn hàng và tình trạng tồn kho nguyên vật liệu.</p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5 mb-8">
        <div className="pos-card rounded-2xl p-5 border-l-4 border-l-[#7d4924]">
          <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Tổng Doanh Thu</p>
          <h3 className="text-2xl font-black text-[#2b170c]">{formatVND(totalRevenue)}</h3>
        </div>
        <div className="pos-card rounded-2xl p-5 border-l-4 border-l-blue-600">
          <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Tổng Đơn Đã Tạo</p>
          <h3 className="text-2xl font-black text-blue-900">{totalOrders}</h3>
        </div>
        <div className="pos-card rounded-2xl p-5 border-l-4 border-l-emerald-600">
          <p className="text-xs font-bold text-[#8a7668] uppercase tracking-wider mb-1">Đơn Hoàn Thành</p>
          <h3 className="text-2xl font-black text-emerald-900">{completedOrders}</h3>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Inventory Stock */}
        <div className="pos-card rounded-3xl p-5">
          <h3 className="font-extrabold text-[#2b170c] text-base mb-4 flex items-center justify-between">
            <span>📦 Tồn Kho Nguyên Liệu</span>
            <span className="text-xs font-normal text-[#8a7668]">Trừ tự động khi pha chế</span>
          </h3>
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2 px-4">Tên nguyên liệu</th>
                  <th className="py-2 px-4">Tồn hiện tại</th>
                  <th className="py-2 px-4">Mức tối thiểu</th>
                  <th className="py-2 px-4">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {INVENTORY_DATA.map((inv, idx) => (
                  <tr key={idx} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 text-sm font-semibold text-[#2b170c]">{inv.name}</td>
                    <td className="py-3 px-4 text-sm text-gray-600">{inv.quantity} {inv.unit}</td>
                    <td className="py-3 px-4 text-sm text-gray-400">{inv.min} {inv.unit}</td>
                    <td className="py-3 px-4">
                      <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
                        inv.quantity > inv.min ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {inv.quantity > inv.min ? 'Đủ hàng' : 'Cần nhập'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Transactions Table */}
        <div className="pos-card rounded-3xl p-5">
          <h3 className="font-extrabold text-[#2b170c] text-base mb-4 flex items-center justify-between">
            <span>📜 Lịch Sử Giao Dịch</span>
            <span className="text-xs font-normal text-[#8a7668]">Thời gian thực</span>
          </h3>
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b border-[#ede4db] text-[11px] font-bold text-[#8a7668] uppercase tracking-wider">
                  <th className="py-2 px-4">Mã đơn</th>
                  <th className="py-2 px-4">Thời gian</th>
                  <th className="py-2 px-4">Món đã gọi</th>
                  <th className="py-2 px-4">Tổng tiền</th>
                  <th className="py-2 px-4">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((o) => (
                  <tr key={o.id} className="border-b border-[#f4eae3] hover:bg-[#fffaf6] transition-colors">
                    <td className="py-3 px-4 text-sm font-bold text-[#583115]">#{o.id}</td>
                    <td className="py-3 px-4 text-xs text-gray-500">{o.createdAt}</td>
                    <td className="py-3 px-4 text-sm font-medium text-gray-700">
                      {o.items.map(i => `${i.qty}x ${i.name}`).join(', ')}
                    </td>
                    <td className="py-3 px-4 text-sm font-extrabold text-[#7d4924]">{formatVND(o.total)}</td>
                    <td className="py-3 px-4">
                      <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
                        o.status === 'READY'
                          ? 'bg-emerald-100 text-emerald-800'
                          : o.status === 'PREPARING'
                          ? 'bg-blue-100 text-blue-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}>
                        {o.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
