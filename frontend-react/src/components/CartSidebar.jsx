import React from 'react';
import { ShoppingBag, Trash2, CreditCard } from 'lucide-react';
import { formatVND } from '../data/mockData';

export default function CartSidebar({
  cart,
  onChangeQty,
  onRemoveItem,
  onClearCart,
  discountType,
  setDiscountType,
  subtotal,
  discountAmount,
  total,
  onOpenCheckout
}) {
  const totalQty = cart.reduce((sum, item) => sum + item.qty, 0);

  return (
    <aside className="w-full lg:w-96 bg-white border-l border-[#ede4db] flex flex-col shadow-lg z-20">
      {/* Header */}
      <div className="p-5 border-b border-[#ede4db] flex items-center justify-between bg-[#fffaf6]">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-xl bg-[#583115] text-white flex items-center justify-center font-bold text-xs">
            <ShoppingBag className="w-4 h-4" />
          </div>
          <div>
            <h2 className="font-extrabold text-[#2b170c] text-base leading-tight">Đơn hàng hiện tại</h2>
            <p className="text-[11px] text-[#a36538]">
              Số lượng: <span id="cartCountBadge" className="font-bold text-[#583115]">{totalQty}</span> món
            </p>
          </div>
        </div>
        {cart.length > 0 && (
          <button
            onClick={onClearCart}
            className="text-xs font-semibold text-gray-400 hover:text-red-600 transition-colors"
          >
            Xóa hết
          </button>
        )}
      </div>

      {/* Items Scroll Area */}
      <div className="flex-1 p-4 overflow-y-auto">
        {cart.length === 0 ? (
          <div className="py-20 text-center text-[#a36538] flex flex-col items-center justify-center">
            <div className="w-16 h-16 rounded-full bg-[#f7ebe3] flex items-center justify-center mb-3 text-[#7d4924]">
              <ShoppingBag className="w-8 h-8 opacity-60" />
            </div>
            <p className="font-bold text-sm text-[#3e200a]">Chưa có món nào</p>
            <p className="text-xs text-[#8a7668] mt-1 max-w-[200px]">Chọn đồ uống từ menu bên trái để bắt đầu tạo đơn.</p>
          </div>
        ) : (
          <div className="space-y-2.5">
            {cart.map((item) => (
              <div key={item.cartItemId} className="p-3.5 rounded-xl border border-[#ede4db] bg-[#fffaf6] flex items-start gap-3">
                <img
                  src={item.img}
                  className="w-12 h-12 object-contain bg-white rounded-lg p-1 border border-[#ede4db]"
                  alt={item.name}
                  onError={(e) => { e.target.src = '/assets/drinks/ca-phe-sua.png'; }}
                />
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-1">
                    <h4 className="font-bold text-sm text-[#2b170c] leading-tight truncate">{item.name}</h4>
                    <button
                      onClick={() => onRemoveItem(item.cartItemId)}
                      className="text-gray-400 hover:text-red-500 transition-colors p-0.5"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                  <div className="flex items-center gap-1.5 mt-0.5">
                    <span className="text-[11px] font-bold px-1.5 py-0.5 rounded-sm bg-[#eed5c7] text-[#583115]">
                      Size {item.size}
                    </span>
                    {item.toppings.length > 0 && (
                      <span className="text-[11px] text-[#7d4924] truncate">+ {item.toppings.join(', ')}</span>
                    )}
                  </div>
                  {item.note && (
                    <p className="text-[11px] text-[#a36538] italic mt-0.5 truncate">📝 {item.note}</p>
                  )}

                  <div className="flex items-center justify-between mt-2.5 pt-2 border-t border-[#f2e6dc]">
                    <div className="flex items-center border border-[#d8c8bd] rounded-lg bg-white overflow-hidden shadow-2xs">
                      <button
                        onClick={() => onChangeQty(item.cartItemId, -1)}
                        className="w-6 h-6 flex items-center justify-center text-[#583115] hover:bg-[#f7ebe3] font-bold text-xs"
                      >
                        -
                      </button>
                      <span className="w-7 text-center font-bold text-xs text-[#2b170c]">{item.qty}</span>
                      <button
                        onClick={() => onChangeQty(item.cartItemId, 1)}
                        className="w-6 h-6 flex items-center justify-center text-[#583115] hover:bg-[#f7ebe3] font-bold text-xs"
                      >
                        +
                      </button>
                    </div>
                    <span className="text-sm font-extrabold text-[#7d4924]">{formatVND(item.unitPrice * item.qty)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Summary & Checkout */}
      <div className="p-5 border-t border-[#ede4db] bg-[#fffaf6] space-y-3">
        {/* Strategy Pattern Discount Selector */}
        <div className="flex items-center justify-between text-xs">
          <span className="font-semibold text-[#583115] flex items-center gap-1">
            <span>🏷️</span> Mã giảm giá:
          </span>
          <select
            id="discountSelect"
            value={discountType}
            onChange={(e) => setDiscountType(e.target.value)}
            className="bg-white border border-[#ede4db] rounded-xl px-2.5 py-1.5 text-xs font-semibold text-[#2b170c] focus:outline-none focus:border-[#7d4924]"
          >
            <option value="NONE">Không áp dụng (0%)</option>
            <option value="PERCENT_10">Giảm giá 10% (Thành viên)</option>
            <option value="VIP">Khách VIP (Giảm 15%)</option>
            <option value="BOGO">Mua 1 Tặng 1 (Tặng món thấp nhất)</option>
          </select>
        </div>

        <div className="space-y-1.5 pt-2 border-t border-dashed border-[#ede4db]">
          <div className="flex justify-between text-xs text-[#8a7668]">
            <span>Tạm tính (Subtotal):</span>
            <span id="cartSubtotal" className="font-bold text-[#2b170c]">{formatVND(subtotal)}</span>
          </div>
          <div className="flex justify-between text-xs text-emerald-600 font-semibold">
            <span>Khuyến mãi (Discount):</span>
            <span id="cartDiscount">{discountAmount > 0 ? `-${formatVND(discountAmount)}` : '0 ₫'}</span>
          </div>
          <div className="flex justify-between items-baseline pt-2 border-t border-[#ede4db]">
            <span className="font-bold text-sm text-[#2b170c]">Tổng thanh toán:</span>
            <span id="cartTotal" className="text-xl font-extrabold text-[#7d4924]">{formatVND(total)}</span>
          </div>
        </div>

        <button
          id="btnOpenCheckout"
          onClick={onOpenCheckout}
          disabled={cart.length === 0}
          className="w-full py-3.5 px-4 rounded-2xl text-sm font-extrabold btn-primary flex items-center justify-center gap-2 cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed"
        >
          <CreditCard className="w-5 h-5" />
          Thanh Toán Đơn Hàng
        </button>

      </div>
    </aside>
  );
}
