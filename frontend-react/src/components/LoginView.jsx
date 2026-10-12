import React, { useState } from 'react';
import { Coffee, Lock, User, ArrowRight, ShieldCheck, Sparkles, ChefHat, LayoutDashboard, Monitor } from 'lucide-react';
import { DEFAULT_USERS } from '../data/mockData';

/**
 * LoginView - Tái hiện 100% giao diện HeroLoginPanel & AppTheme của bản Desktop Java Swing
 * - Hero background: /assets/backgrounds/coffee-hero.jpg
 * - Gradient veil: Warm cream fade (FDFBF7)
 * - Frosted left card (24px radius, soft shadow, AppTheme colors)
 * - Typography: Eyebrow "COFFEE SHOP POS", Title "PurrCoffee", Subtitle "Sign in to run cashier, kitchen, and admin workflows."
 * - Form: Username (cashier01), Password (123), Sign in (#C2917A), Exit button
 * - Demo accounts panel matching desktop card layout
 */
export default function LoginView({ onLoginSuccess }) {
  const [authMode, setAuthMode] = useState('login'); // 'login' | 'register'
  const [username, setUsername] = useState('cashier01');
  const [password, setPassword] = useState('123');
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Register state
  const [regFullName, setRegFullName] = useState('');
  const [regUsername, setRegUsername] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regConfirmPassword, setRegConfirmPassword] = useState('');
  const [regRole, setRegRole] = useState('CASHIER');

  const getAllUsers = () => {
    try {
      const saved = localStorage.getItem('purr_pos_users_list');
      if (saved) return JSON.parse(saved);
    } catch {
      // fallback
    }
    return DEFAULT_USERS;
  };

  const handleLogin = (e) => {
    if (e) e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');

    const trimmedUser = username.trim();
    if (!trimmedUser || !password) {
      setErrorMsg('Username and password are required.');
      return;
    }

    const allUsers = getAllUsers();
    const found = allUsers.find(
      u => u.username.toLowerCase() === trimmedUser.toLowerCase() && u.password === password
    ) || DEFAULT_USERS.find(
      u => u.username.toLowerCase() === trimmedUser.toLowerCase() && u.password === password
    );

    if (!found) {
      setErrorMsg('Invalid username or password.');
      return;
    }

    if (found.active === false) {
      setErrorMsg('Tài khoản đã bị tạm khóa. Vui lòng liên hệ Admin.');
      return;
    }

    onLoginSuccess(found);
  };

  const handleRegister = (e) => {
    if (e) e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');

    const trimmedUser = regUsername.trim();
    if (!trimmedUser || !regPassword) {
      setErrorMsg('Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.');
      return;
    }
    if (regPassword.length < 3) {
      setErrorMsg('Mật khẩu tối thiểu 3 ký tự.');
      return;
    }
    if (regPassword !== regConfirmPassword) {
      setErrorMsg('Mật khẩu xác nhận không khớp.');
      return;
    }

    const allUsers = getAllUsers();
    if (allUsers.some(u => u.username.toLowerCase() === trimmedUser.toLowerCase())) {
      setErrorMsg(`Tên tài khoản "${trimmedUser}" đã tồn tại. Vui lòng chọn tên khác.`);
      return;
    }

    const newUser = {
      id: Date.now(),
      username: trimmedUser,
      password: regPassword,
      fullName: regFullName.trim() || trimmedUser,
      role: regRole,
      active: true
    };

    const updated = [...allUsers, newUser];
    localStorage.setItem('purr_pos_users_list', JSON.stringify(updated));

    setSuccessMsg(`Đăng ký thành công tài khoản "${trimmedUser}" (${regRole})!`);
    setUsername(trimmedUser);
    setPassword(regPassword);
    setAuthMode('login');
    // Clear register form
    setRegFullName('');
    setRegUsername('');
    setRegPassword('');
    setRegConfirmPassword('');
  };

  const selectDemoAccount = (u, autoLogin = false) => {
    setUsername(u.username);
    setPassword(u.password);
    setErrorMsg('');
    setSuccessMsg('');
    if (autoLogin) {
      onLoginSuccess(u);
    }
  };

  return (
    <div 
      className="min-h-screen w-full relative flex items-center justify-between p-4 sm:p-8 lg:p-14 select-none overflow-hidden bg-[#f7f1eb]"
      style={{
        backgroundImage: "url('/assets/backgrounds/coffee-hero.jpg')",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat'
      }}
    >
      {/* Gradient Veil - Tái hiện chuẩn xác GradientPaint veil của Desktop HeroLoginPanel */}
      <div 
        className="absolute inset-0 pointer-events-none"
        style={{
          background: 'linear-gradient(90deg, rgba(253, 251, 247, 0.96) 0%, rgba(253, 251, 247, 0.90) 42%, rgba(253, 251, 247, 0.40) 78%, rgba(253, 251, 247, 0.15) 100%)'
        }}
      />

      {/* Decorative Warm Ambient Circles - Tái hiện g.setColor(new Color(232, 93, 4, 15)) của Swing */}
      <div className="absolute right-[15%] top-[12%] w-36 h-36 rounded-full bg-[#e85d04]/10 blur-2xl pointer-events-none" />
      <div className="absolute right-[10%] top-[30%] w-44 h-44 rounded-full bg-[#c2917a]/15 blur-2xl pointer-events-none" />
      <div className="absolute right-[18%] top-[52%] w-40 h-40 rounded-full bg-[#e85d04]/10 blur-2xl pointer-events-none" />
      <div className="absolute right-[12%] bottom-[15%] w-48 h-48 rounded-full bg-[#c2917a]/15 blur-3xl pointer-events-none" />

      {/* Form Panel Container - Frosted White Card Chuẩn Desktop (width ~480px, radius 24px) */}
      <div className="relative z-10 w-full max-w-[490px] bg-[#ffffff]/95 backdrop-blur-md rounded-[24px] border border-[#ebe6df] shadow-[0_20px_50px_rgba(75,52,39,0.12),0_4px_12px_rgba(0,0,0,0.04)] p-7 sm:p-10 my-auto transition-all">
        
        {/* Eyebrow Label */}
        <div className="flex items-center gap-2 mb-1.5">
          <span className="text-[12px] font-extrabold uppercase tracking-[0.2em] text-[#c2917a]">
            COFFEE SHOP POS
          </span>
          <span className="w-1.5 h-1.5 rounded-full bg-[#c2917a]/60"></span>
          <span className="text-[11px] font-bold text-[#8a7668]">v2.5</span>
        </div>

        {/* Title & Subtitle */}
        <h1 className="text-4xl sm:text-[46px] font-black text-[#4b3427] tracking-tight leading-none mb-2">
          PurrCoffee
        </h1>
        <p className="text-xs sm:text-[13px] text-[#8a7668] leading-relaxed mb-6">
          Sign in to run cashier, kitchen, and admin workflows.
        </p>

        {/* Auth Mode Switcher */}
        <div className="flex items-center gap-1.5 p-1 bg-[#f4eae3] rounded-xl mb-5">
          <button
            type="button"
            onClick={() => { setAuthMode('login'); setErrorMsg(''); }}
            className={`flex-1 py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer ${
              authMode === 'login'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede0d7]'
            }`}
          >
            Đăng Nhập (Sign in)
          </button>
          <button
            type="button"
            id="btnSwitchRegister"
            onClick={() => { setAuthMode('register'); setErrorMsg(''); }}
            className={`flex-1 py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer ${
              authMode === 'register'
                ? 'bg-[#3e200a] text-white shadow-xs'
                : 'text-[#583115] hover:bg-[#ede0d7]'
            }`}
          >
            Đăng Ký Mới (Register)
          </button>
        </div>

        {/* Inline Message / Error Label */}
        {errorMsg && (
          <div className="mb-4 p-3 rounded-xl bg-[#fdf2f1] border border-[#f5c6cb] text-[#c0726c] text-xs font-bold flex items-center gap-2 animate-in fade-in">
            <ShieldCheck className="w-4 h-4 shrink-0 text-[#c0726c]" />
            <span>{errorMsg}</span>
          </div>
        )}

        {successMsg && (
          <div className="mb-4 p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-bold flex items-center gap-2 animate-in fade-in">
            <Sparkles className="w-4 h-4 shrink-0 text-emerald-600" />
            <span>{successMsg}</span>
          </div>
        )}

        {/* Form Inputs: LOGIN MODE */}
        {authMode === 'login' && (
          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="block text-xs font-bold text-[#4b3427] mb-1.5 tracking-wide">
                Username
              </label>
              <div className="relative">
                <User className="w-4 h-4 text-[#8a7668] absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  id="usernameInput"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Username (e.g. cashier01)"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white focus:ring-2 focus:ring-[#f5e8df] text-sm text-[#4b3427] font-semibold outline-none transition-all"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-[#4b3427] mb-1.5 tracking-wide">
                Password
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 text-[#8a7668] absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  id="passwordInput"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white focus:ring-2 focus:ring-[#f5e8df] text-sm text-[#4b3427] font-semibold outline-none transition-all"
                />
              </div>
            </div>

            {/* Action Buttons: Sign in & Exit */}
            <div className="grid grid-cols-2 gap-3 pt-2">
              <button
                id="btnLoginSubmit"
                type="submit"
                className="w-full py-2.5 px-4 rounded-xl bg-[#c2917a] hover:bg-[#b07d67] active:scale-[0.98] text-white font-bold text-sm shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 cursor-pointer"
              >
                <span>Sign in</span>
                <ArrowRight className="w-4 h-4" />
              </button>

              <button
                type="button"
                onClick={() => { setUsername(''); setPassword(''); setErrorMsg(''); }}
                className="w-full py-2.5 px-4 rounded-xl bg-transparent hover:bg-[#f7f1eb] text-[#8a7668] hover:text-[#4b3427] font-bold text-sm border border-[#e5dad0] transition-all cursor-pointer text-center"
              >
                Exit
              </button>
            </div>
          </form>
        )}

        {/* Form Inputs: REGISTER MODE */}
        {authMode === 'register' && (
          <form onSubmit={handleRegister} className="space-y-3">
            <div>
              <label className="block text-xs font-bold text-[#4b3427] mb-1 tracking-wide">
                Họ và Tên
              </label>
              <input
                id="regFullNameInput"
                type="text"
                value={regFullName}
                onChange={(e) => setRegFullName(e.target.value)}
                placeholder="Ví dụ: Lê Thị Kim Oanh"
                className="w-full px-3.5 py-2 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white text-xs text-[#4b3427] font-semibold outline-none"
              />
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="block text-xs font-bold text-[#4b3427] mb-1 tracking-wide">
                  Tên Đăng Nhập
                </label>
                <input
                  id="regUsernameInput"
                  type="text"
                  value={regUsername}
                  onChange={(e) => setRegUsername(e.target.value)}
                  placeholder="e.g. cashier02"
                  className="w-full px-3.5 py-2 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white text-xs text-[#4b3427] font-semibold outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-[#4b3427] mb-1 tracking-wide">
                  Vai Trò (Role)
                </label>
                <select
                  value={regRole}
                  onChange={(e) => setRegRole(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white text-xs text-[#4b3427] font-bold outline-none cursor-pointer"
                >
                  <option value="CASHIER">Thu Ngân (Cashier)</option>
                  <option value="KITCHEN">Pha Chế (Kitchen)</option>
                  <option value="ADMIN">Quản Trị (Admin)</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="block text-xs font-bold text-[#4b3427] mb-1 tracking-wide">
                  Mật Khẩu
                </label>
                <input
                  id="regPasswordInput"
                  type="password"
                  value={regPassword}
                  onChange={(e) => setRegPassword(e.target.value)}
                  placeholder="Mật khẩu"
                  className="w-full px-3.5 py-2 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white text-xs text-[#4b3427] font-semibold outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-[#4b3427] mb-1 tracking-wide">
                  Xác Nhận Mật Khẩu
                </label>
                <input
                  id="regConfirmPasswordInput"
                  type="password"
                  value={regConfirmPassword}
                  onChange={(e) => setRegConfirmPassword(e.target.value)}
                  placeholder="Nhập lại"
                  className="w-full px-3.5 py-2 rounded-xl bg-[#fffcf8] border border-[#e5dad0] focus:border-[#c2917a] focus:bg-white text-xs text-[#4b3427] font-semibold outline-none"
                />
              </div>
            </div>

            <div className="pt-2">
              <button
                id="btnRegisterSubmit"
                type="submit"
                className="w-full py-2.5 px-4 rounded-xl bg-[#3e200a] hover:bg-[#583115] text-white font-bold text-xs shadow-md transition-all flex items-center justify-center gap-2 cursor-pointer"
              >
                <span>Xác Nhận Đăng Ký Tài Khoản</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </form>
        )}

        {/* Demo Accounts Card - Đúng định dạng demoAccountsPanel() của Java Swing */}
        <div className="mt-6 pt-5 border-t border-[#f2eae2]">
          <div className="p-3.5 rounded-2xl bg-[#fffcf8] border border-[#e5dad0] space-y-2">
            <div className="flex items-center justify-between mb-1">
              <span className="text-xs font-bold text-[#4b3427]">Demo accounts</span>
              <span className="text-[10px] text-[#8a7668] font-medium">Click để chọn</span>
            </div>

            <div className="space-y-1.5">
              {DEFAULT_USERS.map((u) => {
                const isSelected = username === u.username;
                const roleBadge = 
                  u.role === 'ADMIN' ? { label: 'Admin dashboard', icon: LayoutDashboard } :
                  u.role === 'KITCHEN' ? { label: 'Kitchen board', icon: ChefHat } :
                  { label: 'Cashier POS', icon: Monitor };

                const IconComponent = roleBadge.icon;

                return (
                  <button
                    key={u.username}
                    type="button"
                    onClick={() => selectDemoAccount(u, false)}
                    className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded-lg border text-left text-xs transition-all cursor-pointer ${
                      isSelected 
                        ? 'bg-[#f7ebe3] border-[#c2917a] font-bold text-[#4b3427]' 
                        : 'bg-white border-[#f0e7df] text-[#8a7668] hover:bg-[#faf4ee] hover:text-[#4b3427]'
                    }`}
                  >
                    <div className="flex items-center gap-2">
                      <IconComponent className="w-3.5 h-3.5 text-[#c2917a]" />
                      <span className="font-mono font-medium">{u.username} / {u.password}</span>
                    </div>
                    <span className="text-[11px] font-semibold text-[#8a7668]">
                      &rarr; {roleBadge.label}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

      </div>

      {/* Right Side Showcase Banner (Chỉ hiện trên màn hình lớn) */}
      <div className="hidden lg:flex flex-col justify-end p-8 max-w-md z-10">
        <div className="bg-white/85 backdrop-blur-md p-6 rounded-3xl border border-white/60 shadow-xl text-[#4b3427]">
          <div className="flex items-center gap-2 mb-2 text-[#c2917a] font-bold text-xs uppercase tracking-wider">
            <Sparkles className="w-4 h-4" />
            <span>PurrCoffee Specialty POS</span>
          </div>
          <h3 className="font-bold text-lg text-[#4b3427] mb-1">
            Hệ Thống Bán Hàng & Chế Biến Real-Time
          </h3>
          <p className="text-xs text-[#8a7668] leading-relaxed">
            Thiết kế theo chuẩn GoF Design Patterns kết hợp Real-time Kitchen KDS và Quản trị định lượng nguyên vật liệu.
          </p>
          <div className="mt-4 pt-3 border-t border-[#ede4db] flex items-center justify-between text-[11px] text-[#8a7668]">
            <span className="flex items-center gap-1.5">
              <Coffee className="w-3.5 h-3.5 text-[#c2917a]" />
              <span>Full-Stack Specialty</span>
            </span>
            <span className="font-mono text-[#c2917a] font-semibold">Ready to Serve</span>
          </div>
        </div>
      </div>

    </div>
  );
}
