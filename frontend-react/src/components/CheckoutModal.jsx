import React, { useState } from 'react';
import { X, Check } from 'lucide-react';
import { formatVND } from '../data/mockData';

export default function CheckoutModal({ total, onClose, onConfirmPayment }) {
  const [gateway, setGateway] = useState('CASH');

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl w-full max-w-md overflow-hidden shadow-2xl flex flex-col animate-in fade-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="p-6 border-b border-[#ede4db] bg-[#fffaf6] text-center relative">
          <button
            onClick={onClose}
            className="absolute top-5 right-5 w-8 h-8 rounded-full bg-white hover:bg-gray-100 flex items-center justify-center text-gray-400"
          >
            <X className="w-4 h-4" />
          </button>
          <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full bg-[#3e200a] text-white">
            Adapter Pattern Payment
          </span>
          <h3 className="text-xl font-black text-[#2b170c] mt-2">Thanh Toán Đơn Hàng</h3>
          <div className="mt-4 p-4 rounded-2xl bg-[#f7ebe3] border border-[#eed5c7]">
            <span className="text-xs text-[#8a7668]">Tổng số tiền cần trả:</span>
            <h2 className="text-3xl font-black text-[#7d4924] mt-0.5">{formatVND(total)}</h2>
          </div>
        </div>

        {/* Gateway Selection */}
        <div className="p-6 space-y-4">
          <div className="grid grid-cols-3 gap-3">
            <button
              onClick={() => setGateway('CASH')}
              className={`p-3 rounded-2xl border-2 text-center transition-all ${
                gateway === 'CASH'
                  ? 'border-[#7d4924] bg-[#fffaf6] shadow-xs'
                  : 'border-[#ede4db] hover:border-[#c2917a]'
              }`}
            >
              <span className="text-2xl block mb-1">💵</span>
              <span className="text-xs font-bold text-[#2b170c] block">Tiền mặt</span>
            </button>
            <button
              onClick={() => setGateway('VNPAY')}
              className={`p-3 rounded-2xl border-2 text-center transition-all ${
                gateway === 'VNPAY'
                  ? 'border-[#7d4924] bg-[#fffaf6] shadow-xs'
                  : 'border-[#ede4db] hover:border-[#c2917a]'
              }`}
            >
              <span className="text-2xl block mb-1">📱</span>
              <span className="text-xs font-bold text-[#2b170c] block">VNPay QR</span>
            </button>
            <button
              onClick={() => setGateway('MOMO')}
              className={`p-3 rounded-2xl border-2 text-center transition-all ${
                gateway === 'MOMO'
                  ? 'border-[#7d4924] bg-[#fffaf6] shadow-xs'
                  : 'border-[#ede4db] hover:border-[#c2917a]'
              }`}
            >
              <span className="text-2xl block mb-1">👛</span>
              <span className="text-xs font-bold text-[#2b170c] block">Ví MoMo</span>
            </button>
          </div>

          {/* Dynamic Simulated QR code for online gateways */}
          {(gateway === 'VNPAY' || gateway === 'MOMO') && (
            <div className="text-center p-4 bg-[#fffaf6] border border-[#ede4db] rounded-2xl animate-in fade-in duration-150">
              <p className="text-xs font-bold text-[#7d4924] mb-2">
                {gateway === 'VNPAY' ? 'Quét mã VietQR / VNPay để thanh toán' : 'Quét mã MoMo QR để thanh toán'}
              </p>
              <div className="w-40 h-40 mx-auto bg-white p-2 rounded-xl shadow-xs border border-gray-200 flex items-center justify-center">
                <svg className="w-36 h-36" viewBox="0 0 100 100" fill="#2b170c">
                  <rect x="0" y="0" width="30" height="30" fill="#2b170c"/>
                  <rect x="5" y="5" width="20" height="20" fill="white"/>
                  <rect x="10" y="10" width="10" height="10" fill="#2b170c"/>
                  <rect x="70" y="0" width="30" height="30" fill="#2b170c"/>
                  <rect x="75" y="5" width="20" height="20" fill="white"/>
                  <rect x="80" y="10" width="10" height="10" fill="#2b170c"/>
                  <rect x="0" y="70" width="30" height="30" fill="#2b170c"/>
                  <rect x="5" y="75" width="20" height="20" fill="white"/>
                  <rect x="10" y="80" width="10" height="10" fill="#2b170c"/>
                  <rect x="40" y="10" width="15" height="15" fill="#2b170c"/>
                  <rect x="40" y="40" width="20" height="20" fill="#2b170c"/>
                  <rect x="70" y="45" width="15" height="10" fill="#2b170c"/>
                  <rect x="40" y="75" width="20" height="15" fill="#2b170c"/>
                  <rect x="75" y="75" width="15" height="15" fill="#2b170c"/>
                </svg>
              </div>
              <p className="text-[11px] text-gray-500 mt-2">Mã QR tự động cập nhật đúng {formatVND(total)}</p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-6 border-t border-[#ede4db] bg-[#fffaf6] flex items-center gap-3">
          <button
            onClick={onClose}
            className="flex-1 py-3 rounded-xl text-xs font-bold bg-[#f4eae3] text-[#583115] hover:bg-[#ede0d7]"
          >
            Quay lại
          </button>
          <button
            onClick={() => onConfirmPayment(gateway)}
            className="flex-1 py-3 rounded-xl text-xs font-extrabold btn-primary flex items-center justify-center gap-1.5"
          >
            <Check className="w-4 h-4" />
            Xác Nhận Đã Thu Tiền
          </button>
        </div>
      </div>
    </div>
  );
}
