import React from 'react';
import { Printer, X } from 'lucide-react';
import { formatVND } from '../data/mockData';

export default function ReceiptModal({ order, onClose }) {
  if (!order) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl flex flex-col animate-in fade-in zoom-in-95 duration-200">
        {/* Printable Receipt */}
        <div id="receiptModalContent" className="p-6 bg-white font-mono text-xs">
          <div className="text-center pb-3 border-b border-dashed border-gray-300">
            <h2 className="text-base font-extrabold tracking-wider text-black">PURRCOFFEE POS</h2>
            <p className="text-[10px] text-gray-500">123 Đại Lộ Hoàng Hôn, Quận 1, TP. HCM</p>
            <p className="text-[10px] text-gray-500">Hotline: 1900 6868</p>
            <div className="mt-2 text-xs font-bold uppercase">HÓA ĐƠN THANH TOÁN</div>
          </div>

          <div className="py-2.5 border-b border-dashed border-gray-300 text-[11px] space-y-1">
            <div className="flex justify-between"><span>Mã đơn:</span><span className="font-bold">#{order.id}</span></div>
            <div className="flex justify-between"><span>Thời gian:</span><span>{new Date().toLocaleString('vi-VN')}</span></div>
            <div className="flex justify-between"><span>Phương thức:</span><span className="font-bold">{order.paymentMethod}</span></div>
            <div className="flex justify-between"><span>Hình thức:</span><span>{order.orderType === 'DINE_IN' ? 'Tại quán' : 'Mang đi'}</span></div>
          </div>

          {/* Items */}
          <div className="py-3 space-y-1.5">
            {order.items.map((i, idx) => (
              <div key={idx} className="flex justify-between text-xs py-1 border-b border-dashed border-gray-100">
                <div>
                  <div className="font-bold text-gray-800">{i.qty}x {i.name} (Size {i.size})</div>
                  {i.toppings && i.toppings.length > 0 && (
                    <div className="text-gray-500 text-[10px]">+ {i.toppings.join(', ')}</div>
                  )}
                  {i.note && <div className="text-gray-500 text-[10px]">Ghi chú: {i.note}</div>}
                </div>
                <div className="font-bold text-gray-800">{formatVND(i.unitPrice * i.qty)}</div>
              </div>
            ))}
          </div>

          {/* Total */}
          <div className="py-2.5 border-t border-dashed border-gray-300 space-y-1 text-[11px]">
            <div className="flex justify-between text-gray-600"><span>Tạm tính:</span><span>{formatVND(order.subtotal)}</span></div>
            <div className="flex justify-between text-gray-600">
              <span>Giảm giá:</span>
              <span>{order.discountAmount > 0 ? `-${formatVND(order.discountAmount)}` : '0 ₫'}</span>
            </div>
            <div className="flex justify-between text-sm font-extrabold text-black pt-1 border-t border-gray-300">
              <span>TỔNG CỘNG:</span>
              <span>{formatVND(order.total)}</span>
            </div>
          </div>

          <div className="text-center pt-3 border-t border-dashed border-gray-300 text-[10px] text-gray-500">
            <p>Cảm ơn quý khách và hẹn gặp lại!</p>
            <p>Wifi: purrcoffee2026</p>
          </div>
        </div>

        {/* Buttons */}
        <div className="p-4 border-t border-gray-100 bg-[#fffaf6] flex items-center gap-3 no-print">
          <button
            onClick={() => window.print()}
            className="flex-1 py-2.5 rounded-xl text-xs font-bold bg-[#3e200a] text-white flex items-center justify-center gap-1.5 hover:bg-[#583115]"
          >
            <Printer className="w-4 h-4" />
            In Hóa Đơn
          </button>
          <button
            id="btnCloseReceipt"
            onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-xs font-semibold bg-[#f4eae3] text-[#583115] hover:bg-[#ede0d7]"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}
