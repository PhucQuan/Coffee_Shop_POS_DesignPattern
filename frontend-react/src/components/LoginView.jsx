import React, { useState } from 'react';
import { Coffee, Lock, User, ArrowRight, ShieldCheck, Sparkles } from 'lucide-react';
import { DEFAULT_USERS } from '../data/mockData';

export default function LoginView({ onLoginSuccess }) {
  const [username, setUsername] = useState('cashier01');
  const [password, setPassword] = useState('123');
  const [errorMsg, setErrorMsg] = useState('');

  const handleLogin = (e) => {
    if (e) e.preventDefault();
    setErrorMsg('');

    const trimmedUser = username.trim();
    if (!trimmedUser || !password) {
      setErrorMsg('Vui lòng nhập tên đăng nhập và mật khẩu.');
      return;
    }

    const found = DEFAULT_USERS.find(
      u => u.username.toLowerCase() === trimmedUser.toLowerCase() && u.password === password
    );

    if (!found) {
      setErrorMsg('Tên đăng nhập hoặc mật khẩu không chính xác.');
      return;
    }

    if (!found.active) {
      setErrorMsg('Tài khoản này đã bị khóa. Vui lòng liên hệ quản trị viên.');
      return;
    }

    onLoginSuccess(found);
  };

  const handleQuickLogin = (user) => {
    setUsername(user.username);
    setPassword(user.password);
    setErrorMsg('');
    onLoginSuccess(user);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#2b170c] via-[#3e200a] to-[#1a0e07] p-4 sm:p-6 select-none relative overflow-hidden">
      {/* Decorative background glow circles */}
      <div className="absolute top-1/4 -left-20 w-96 h-96 bg-[#7d4924]/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 -right-20 w-96 h-96 bg-[#e85d04]/10 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-4xl w-full grid grid-cols-1 md:grid-cols-12 bg-white/95 backdrop-blur-md rounded-3xl shadow-2xl border border-white/20 overflow-hidden z-10">
        
        {/* Left Hero Branding Section */}
        <div className="md:col-span-5 bg-gradient-to-br from-[#3e200a] via-[#583115] to-[#2b170c] p-8 text-white flex flex-col justify-between relative overflow-hidden">
          <div className="relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 text-amber-200 text-xs font-semibold mb-6 border border-white/10">
              <Sparkles className="w-3.5 h-3.5" />
              <span>PurrCoffee POS System</span>
            </div>

            <h1 className="text-3xl sm:text-4xl font-black tracking-tight leading-tight mb-3">
              Quản Lý Bán Hàng & Bếp
            </h1>
            <p className="text-xs text-[#ede4db] leading-relaxed">
              Giải pháp POS chuẩn kiến trúc Design Patterns kết hợp Realtime Kitchen KDS và Báo Cáo Doanh Thu Chuyên Nghiệp.
            </p>
          </div>

          <div className="my-8 relative z-10">
            <div className="w-20 h-20 rounded-2xl bg-white/10 backdrop-blur-sm border border-white/20 flex items-center justify-center text-amber-300 shadow-xl mb-4">
              <Coffee className="w-10 h-10" />
            </div>
            <div className="space-y-1">
              <p className="text-sm font-bold text-white">Chất Lượng Vị Trí Số 1</p>
              <p className="text-[11px] text-[#ede4db]">Hệ thống vận hành trơn tru từ Order đến Bếp.</p>
            </div>
          </div>

          <div className="pt-4 border-t border-white/10 relative z-10 text-[11px] text-white/60 flex items-center justify-between">
            <span>Phiên bản Web React 19</span>
            <span>PurrCoffee v2.5</span>
          </div>
        </div>

        {/* Right Form Section */}
        <div className="md:col-span-7 p-8 sm:p-10 flex flex-col justify-between bg-white">
          <div>
            <div className="mb-6">
              <h2 className="text-2xl font-black text-[#2b170c] tracking-tight">Đăng Nhập Hệ Thống</h2>
              <p className="text-xs text-[#8a7668] mt-1">
                Nhập thông tin xác thực để bắt đầu ca làm việc của bạn.
              </p>
            </div>

            {errorMsg && (
              <div className="mb-5 p-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs font-semibold flex items-center gap-2 animate-in fade-in">
                <ShieldCheck className="w-4 h-4 text-red-500 shrink-0" />
                <span>{errorMsg}</span>
              </div>
            )}

            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1.5 uppercase tracking-wider">
                  Tài Khoản (Username)
                </label>
                <div className="relative">
                  <User className="w-4 h-4 text-[#8a7668] absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    id="usernameInput"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Nhập tên đăng nhập..."
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#faf6f2] border border-[#ede4db] focus:border-[#a36538] focus:bg-white focus:ring-2 focus:ring-[#f7ebe3] text-sm text-[#2b170c] font-medium outline-none transition-all"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-[#583115] mb-1.5 uppercase tracking-wider">
                  Mật Khẩu (Password)
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-[#8a7668] absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    id="passwordInput"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Nhập mật khẩu..."
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#faf6f2] border border-[#ede4db] focus:border-[#a36538] focus:bg-white focus:ring-2 focus:ring-[#f7ebe3] text-sm text-[#2b170c] font-medium outline-none transition-all"
                  />
                </div>
              </div>

              <button
                id="btnLoginSubmit"
                type="submit"
                className="w-full mt-2 py-3 rounded-xl bg-[#3e200a] hover:bg-[#583115] active:scale-[0.99] text-white font-bold text-sm transition-all shadow-md flex items-center justify-center gap-2 cursor-pointer"
              >
                <span>Đăng Nhập Vào Ca</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </form>
          </div>

          {/* Quick Demo Switchers */}
          <div className="mt-8 pt-6 border-t border-[#f4eae3]">
            <p className="text-[11px] font-bold text-[#8a7668] uppercase tracking-wider mb-2.5">
              🚀 Tài khoản Demo nhanh (Click để đăng nhập ngay):
            </p>
            <div className="grid grid-cols-3 gap-2">
              {DEFAULT_USERS.map((user) => (
                <button
                  key={user.username}
                  type="button"
                  onClick={() => handleQuickLogin(user)}
                  className="p-2 rounded-xl border border-[#ede4db] bg-[#fffaf6] hover:bg-[#f7ebe3] hover:border-[#a36538] transition-all text-left group cursor-pointer"
                >
                  <p className="text-xs font-bold text-[#2b170c] group-hover:text-[#583115] flex items-center justify-between">
                    <span>{user.username}</span>
                    <span className="text-[10px] font-semibold px-1.5 py-0.5 rounded bg-[#f4eae3] text-[#583115]">
                      {user.role}
                    </span>
                  </p>
                  <p className="text-[10px] text-[#8a7668] truncate mt-0.5">{user.fullName}</p>
                </button>
              ))}
            </div>
          </div>

        </div>

      </div>
    </div>
  );
}
