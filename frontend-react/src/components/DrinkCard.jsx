import React from 'react';
import { Plus } from 'lucide-react';
import { formatVND } from '../data/mockData';

export default function DrinkCard({ item, onOpenModal }) {
  return (
    <div
      onClick={() => onOpenModal(item)}
      className="pos-card drink-card rounded-2xl overflow-hidden cursor-pointer flex flex-col justify-between"
    >
      <div className="relative bg-[#f6eee7] h-44 flex items-center justify-center p-3 overflow-hidden">
        <img
          src={item.img}
          alt={item.name}
          className="h-36 w-auto object-contain drop-shadow-md transition-transform duration-300 hover:scale-105"
          onError={(e) => { e.target.src = '/assets/drinks/ca-phe-sua.png'; }}
        />
        <span className="absolute top-3 left-3 text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full bg-white/90 backdrop-blur-sm text-[#583115] shadow-xs">
          {item.category}
        </span>
      </div>
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <h3 className="font-bold text-[#2b170c] text-base leading-tight mb-1 line-clamp-1">{item.name}</h3>
          <p className="text-xs text-[#8a7668] line-clamp-2 leading-relaxed mb-3">{item.desc}</p>
        </div>
        <div className="flex items-center justify-between pt-2 border-t border-[#f4eae3]">
          <span className="text-base font-extrabold text-[#7d4924]">{formatVND(item.price)}</span>
          <button
            className="w-8 h-8 rounded-full bg-[#f4eae3] hover:bg-[#3e200a] hover:text-white text-[#583115] flex items-center justify-center transition-all shadow-xs"
            title="Tùy chỉnh & Thêm"
          >
            <Plus className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
