import React, { useState, useEffect } from 'react';
import { X, Plus } from 'lucide-react';
import { TOPPINGS, formatVND } from '../data/mockData';

export default function CustomizeModal({ drink, onClose, onConfirm }) {
  const [size, setSize] = useState('M');
  const [selectedToppings, setSelectedToppings] = useState([]);
  const [note, setNote] = useState('');

  useEffect(() => {
    setSize('M');
    setSelectedToppings([]);
    setNote('');
  }, [drink]);

  if (!drink) return null;

  const basePrice = drink.price;
  const sizePrice = size === 'L' ? 10000 : 0;
  const toppingTotal = selectedToppings.reduce((sum, t) => sum + t.price, 0);
  const currentTotal = basePrice + sizePrice + toppingTotal;

  const handleToggleTopping = (topping) => {
    if (selectedToppings.some(t => t.id === topping.id)) {
      setSelectedToppings(selectedToppings.filter(t => t.id !== topping.id));
    } else {
      setSelectedToppings([...selectedToppings, topping]);
    }
  };

  const handleConfirm = () => {
    onConfirm({
      drink,
      size,
      toppings: selectedToppings.map(t => t.name),
      note: note.trim(),
      unitPrice: currentTotal
    });
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl w-full max-w-lg overflow-hidden shadow-2xl flex flex-col max-h-[90vh] animate-in fade-in zoom-in-95 duration-200">
        {/* Header Preview */}
        <div className="p-6 bg-gradient-to-r from-[#f7ebe3] to-[#fffaf6] border-b border-[#ede4db] flex items-center justify-between">
          <div className="flex items-center gap-5">
            <img
              src={drink.img}
              className="w-20 h-20 object-contain drop-shadow-md bg-white rounded-2xl p-1.5 border border-[#ede4db]"
              alt={drink.name}
              onError={(e) => { e.target.src = '/assets/drinks/ca-phe-sua.png'; }}
            />
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full bg-[#3e200a] text-white">
                Decorator Customizer
              </span>
              <h3 className="text-xl font-black text-[#2b170c] mt-1">{drink.name}</h3>
              <p className="text-sm font-extrabold text-[#7d4924]">{formatVND(drink.price)}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white hover:bg-gray-100 flex items-center justify-center text-gray-500"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Customization Options */}
        <div className="p-6 overflow-y-auto space-y-5 flex-1">
          {/* Size */}
          <div>
            <label className="block text-xs font-bold text-[#583115] uppercase tracking-wider mb-2">
              1. Chọn Kích Cỡ (Size):
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label
                onClick={() => setSize('M')}
                className={`flex items-center justify-between p-3 rounded-2xl border cursor-pointer transition-all ${
                  size === 'M' ? 'border-[#7d4924] bg-[#fffaf6]' : 'border-[#ede4db] bg-white'
                }`}
              >
                <div className="flex items-center gap-2">
                  <input type="radio" checked={size === 'M'} readOnly className="w-4 h-4 text-[#7d4924]" />
                  <span className="text-sm font-bold text-[#2b170c]">Size M (Vừa)</span>
                </div>
                <span className="text-xs font-semibold text-gray-500">+0 ₫</span>
              </label>
              <label
                onClick={() => setSize('L')}
                className={`flex items-center justify-between p-3 rounded-2xl border cursor-pointer transition-all ${
                  size === 'L' ? 'border-[#7d4924] bg-[#fffaf6]' : 'border-[#ede4db] bg-white'
                }`}
              >
                <div className="flex items-center gap-2">
                  <input type="radio" checked={size === 'L'} readOnly className="w-4 h-4 text-[#7d4924]" />
                  <span className="text-sm font-bold text-[#2b170c]">Size L (Lớn)</span>
                </div>
                <span className="text-xs font-bold text-[#7d4924]">+10.000 ₫</span>
              </label>
            </div>
          </div>

          {/* Toppings (Decorator Pattern) */}
          <div>
            <label className="block text-xs font-bold text-[#583115] uppercase tracking-wider mb-2">
              2. Thêm Topping (Tùy chọn):
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {TOPPINGS.map((top) => {
                const isSelected = selectedToppings.some(t => t.id === top.id);
                return (
                  <label
                    key={top.id}
                    onClick={() => handleToggleTopping(top)}
                    className={`flex items-center justify-between p-2.5 rounded-xl border cursor-pointer transition-all ${
                      isSelected ? 'border-[#7d4924] bg-[#fffaf6]' : 'border-[#ede4db] bg-white'
                    }`}
                  >
                    <div className="flex items-center gap-2.5">
                      <input type="checkbox" checked={isSelected} readOnly className="w-4 h-4 text-[#7d4924] rounded-sm" />
                      <span className="text-sm font-medium text-[#3e200a]">{top.name}</span>
                    </div>
                    <span className="text-xs font-bold text-[#a36538]">+{formatVND(top.price)}</span>
                  </label>
                );
              })}
            </div>
          </div>

          {/* Notes */}
          <div>
            <label className="block text-xs font-bold text-[#583115] uppercase tracking-wider mb-2">
              3. Ghi Chú Đặc Biệt:
            </label>
            <input
              type="text"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="Ví dụ: Ít đá, 50% đường, nhiều sữa..."
              className="w-full p-3 rounded-xl border border-[#ede4db] text-sm text-[#2b170c] focus:outline-none focus:border-[#7d4924] bg-[#fffaf6]"
            />
          </div>
        </div>

        {/* Footer */}
        <div className="p-5 border-t border-[#ede4db] bg-[#fffaf6] flex items-center justify-between">
          <div>
            <p className="text-[11px] text-[#8a7668]">Thành tiền món này:</p>
            <p className="text-xl font-black text-[#7d4924]">{formatVND(currentTotal)}</p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={onClose}
              className="px-5 py-2.5 rounded-xl text-xs font-bold bg-[#f4eae3] text-[#583115] hover:bg-[#ede0d7]"
            >
              Hủy
            </button>
            <button
              onClick={handleConfirm}
              className="px-6 py-2.5 rounded-xl text-xs font-extrabold btn-primary"
            >
              + Thêm Vào Đơn
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
