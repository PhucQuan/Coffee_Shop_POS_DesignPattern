import React, { useState } from 'react';
import { Coffee, UtensilsCrossed, BarChart3, LogOut, Bell, KeyRound, X, Trash2, Send, Check } from 'lucide-react';

export default function Navbar({
  activeTab,
  setActiveTab,
  currentUser,
  onLogout,
  notifications = [],
  onDeleteNotification,
  onSendNotification,
  onMarkNotificationsRead,
  onChangePassword
}) {
  const role = currentUser?.role || 'ADMIN';
  const canAccessTab = (tab) => (
    role === 'ADMIN' ||
    (role === 'CASHIER' && tab === 'pos') ||
    (role === 'KITCHEN' && tab === 'kitchen')
  );
  const [showNotifMenu, setShowNotifMenu] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  // New notification draft state
  const [notifTitle, setNotifTitle] = useState('');
  const [notifMessage, setNotifMessage] = useState('');
  const [notifType, setNotifType] = useState('SYSTEM');

  // Change password form state
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordMsg, setPasswordMsg] = useState({ type: '', text: '' });

  const unreadCount = notifications.filter(n => !n.read).length;

  const handleSendCustomNotif = (e) => {
    e.preventDefault();
    if (!notifTitle.trim() || !notifMessage.trim()) return;
    if (onSendNotification) {
      onSendNotification(notifTitle.trim(), notifMessage.trim(), notifType);
      setNotifTitle('');
      setNotifMessage('');
    }
  };

  const handleSubmitPasswordChange = (e) => {
    e.preventDefault();
    setPasswordMsg({ type: '', text: '' });

    if (!oldPassword || !newPassword) {
      setPasswordMsg({ type: 'error', text: 'Vui lòng nhập đầy đủ thông tin.' });
      return;
    }
    if (newPassword.length < 3) {
      setPasswordMsg({ type: 'error', text: 'Mật khẩu mới tối thiểu 3 ký tự.' });
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordMsg({ type: 'error', text: 'Mật khẩu xác nhận không trùng khớp.' });
      return;
    }

    if (onChangePassword) {
      const ok = onChangePassword(oldPassword, newPassword);
      if (ok) {
        setPasswordMsg({ type: 'success', text: 'Đổi mật khẩu thành công!' });
        setTimeout(() => {
          setShowPasswordModal(false);
          setOldPassword('');
          setNewPassword('');
          setConfirmPassword('');
          setPasswordMsg({ type: '', text: '' });
        }, 1200);
      } else {
        setPasswordMsg({ type: 'error', text: 'Mật khẩu cũ không chính xác.' });
      }
    }
  };

  return (
    <>
      <header className="h-16 bg-white border-b border-[#ede4db] px-4 sm:px-6 flex items-center justify-between sticky top-0 z-30 shadow-xs">
        <div className="flex items-center gap-4 sm:gap-6">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-[#583115] to-[#2b170c] flex items-center justify-center text-white shadow-md">
              <Coffee className="w-5 h-5" />
            </div>
            <div>
              <h1 className="font-extrabold text-[#2b170c] text-base sm:text-lg leading-tight tracking-tight">PurrCoffee POS</h1>
              <p className="text-[10px] sm:text-[11px] font-medium text-[#a36538]">Hệ Thống Bán Hàng & Pha Chế</p>
            </div>
          </div>

          {/* Only show workspaces allowed for the signed-in role. */}
          <nav className="flex items-center gap-1 bg-[#f7ebe3] p-1 rounded-2xl overflow-x-auto">
            {canAccessTab('pos') && (
              <button
                data-tab-target="pos"
                onClick={() => setActiveTab('pos')}
                className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                  activeTab === 'pos' ? 'bg-[#3e200a] text-white shadow-xs' : 'text-[#583115] hover:bg-[#ede4db]'
                }`}
              >
                <Coffee className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
                <span>Thu Ngân (POS)</span>
              </button>
            )}

            {canAccessTab('kitchen') && (
              <button
                data-tab-target="kitchen"
                onClick={() => setActiveTab('kitchen')}
                className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                  activeTab === 'kitchen' ? 'bg-[#3e200a] text-white shadow-xs' : 'text-[#583115] hover:bg-[#ede4db]'
                }`}
              >
                <UtensilsCrossed className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
                <span>Màn Hình Bếp (KDS)</span>
              </button>
            )}

            {canAccessTab('admin') && (
              <button
                data-tab-target="admin"
                onClick={() => setActiveTab('admin')}
                className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                  activeTab === 'admin' ? 'bg-[#3e200a] text-white shadow-xs' : 'text-[#583115] hover:bg-[#ede4db]'
                }`}
              >
                <BarChart3 className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
                <span>Báo Cáo & Quản Trị</span>
              </button>
            )}
          </nav>
        </div>

        {/* Right actions: Notifications, Profile, Change Password, Logout */}
        <div className="flex items-center gap-2 sm:gap-3">
          {/* Notification Bell */}
          <div className="relative">
            <button
              id="btnNotificationBell"
              onClick={() => {
                setShowNotifMenu(!showNotifMenu);
                if (!showNotifMenu && onMarkNotificationsRead) onMarkNotificationsRead();
              }}
              title="Thông báo vận hành"
              className="relative p-2 rounded-xl text-[#583115] hover:bg-[#f7ebe3] transition-all cursor-pointer"
            >
              <Bell className="w-4 h-4 sm:w-5 sm:h-5" />
              {unreadCount > 0 && (
                <span className="absolute top-1 right-1 w-4 h-4 rounded-full bg-red-600 text-white text-[10px] font-black flex items-center justify-center animate-pulse">
                  {unreadCount}
                </span>
              )}
            </button>

            {/* Notification Dropdown Drawer */}
            {showNotifMenu && (
              <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-white rounded-3xl shadow-2xl border border-[#ede4db] overflow-hidden z-50 animate-in fade-in zoom-in-95">
                <div className="p-3.5 bg-[#fffaf6] border-b border-[#ede4db] flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Bell className="w-4 h-4 text-[#7d4924]" />
                    <span className="font-extrabold text-xs text-[#2b170c]">Trung Tâm Thông Báo ({notifications.length})</span>
                  </div>
                  <button
                    onClick={() => setShowNotifMenu(false)}
                    className="p-1 rounded-lg hover:bg-gray-200 text-gray-500 cursor-pointer"
                  >
                    <X className="w-3.5 h-3.5" />
                  </button>
                </div>

                <div className="max-h-64 overflow-y-auto divide-y divide-[#f7ebe3]">
                  {notifications.length === 0 ? (
                    <p className="p-6 text-center text-xs text-gray-400">Không có thông báo nào.</p>
                  ) : (
                    notifications.map(notif => (
                      <div key={notif.id} className="p-3 hover:bg-[#fffcf8] flex items-start justify-between gap-2 transition-colors">
                        <div className="space-y-0.5">
                          <div className="flex items-center gap-1.5">
                            <span className="font-bold text-xs text-[#2b170c]">{notif.title}</span>
                            <span className="text-[10px] text-gray-400 font-mono">({notif.time})</span>
                          </div>
                          <p className="text-[11px] text-[#583115] leading-relaxed">{notif.message}</p>
                        </div>
                        {onDeleteNotification && (
                          <button
                            onClick={() => onDeleteNotification(notif.id)}
                            title="Xóa thông báo này"
                            className="p-1 rounded-md text-gray-400 hover:text-red-600 hover:bg-red-50 transition-all cursor-pointer"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))
                  )}
                </div>

                {/* Send notification section */}
                <form onSubmit={handleSendCustomNotif} className="p-3 bg-[#fdf8f4] border-t border-[#ede4db] space-y-2">
                  <p className="text-[10px] font-bold text-[#8a7668] uppercase tracking-wider">Phát thông báo nội bộ</p>
                  <input
                    type="text"
                    value={notifTitle}
                    onChange={(e) => setNotifTitle(e.target.value)}
                    placeholder="Tiêu đề (ví dụ: Bếp chú ý)"
                    className="w-full px-2.5 py-1.5 rounded-lg border border-[#ede4db] bg-white text-xs text-[#2b170c] outline-none"
                  />
                  <div className="flex items-center gap-1.5">
                    <input
                      type="text"
                      value={notifMessage}
                      onChange={(e) => setNotifMessage(e.target.value)}
                      placeholder="Nội dung thông báo..."
                      className="flex-1 px-2.5 py-1.5 rounded-lg border border-[#ede4db] bg-white text-xs text-[#2b170c] outline-none"
                    />
                    <button
                      type="submit"
                      className="px-3 py-1.5 bg-[#3e200a] text-white rounded-lg text-xs font-bold flex items-center gap-1 cursor-pointer hover:bg-[#583115]"
                    >
                      <Send className="w-3 h-3" />
                      Gửi
                    </button>
                  </div>
                </form>
              </div>
            )}
          </div>

          {/* User profile & password button */}
          <div className="hidden md:flex items-center gap-2 pl-2 border-l border-[#ede4db]">
            <div className="text-right">
              <p className="text-xs font-bold text-[#2b170c]">{currentUser?.fullName || currentUser?.username || 'Thu Ngân'}</p>
              <p className="text-[10px] text-emerald-600 font-semibold flex items-center justify-end gap-1">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                {role === 'ADMIN' ? 'Quản trị viên' : role === 'KITCHEN' ? 'Bếp trưởng' : 'Thu ngân'} • Đang trực
              </p>
            </div>
            <button
              onClick={() => setShowPasswordModal(true)}
              title="Đổi mật khẩu tài khoản"
              className="w-8 h-8 rounded-full bg-[#f4eae3] border border-[#d8c8bd] hover:border-[#7d4924] flex items-center justify-center font-bold text-xs text-[#583115] cursor-pointer transition-all"
            >
              <KeyRound className="w-3.5 h-3.5" />
            </button>
          </div>

          <button
            onClick={onLogout}
            title="Đăng xuất ca làm việc"
            className="flex items-center gap-1 px-3 py-1.5 rounded-xl border border-red-200 bg-red-50 hover:bg-red-100 text-red-700 text-xs font-bold transition-all cursor-pointer"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Đăng Xuất</span>
          </button>
        </div>
      </header>

      {/* Change Password Modal */}
      {showPasswordModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl p-6 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <KeyRound className="w-4 h-4 text-[#7d4924]" />
                <h3 className="font-extrabold text-[#2b170c] text-sm">Thay Đổi Mật Khẩu Cá Nhân</h3>
              </div>
              <button
                onClick={() => setShowPasswordModal(false)}
                className="p-1 rounded-lg hover:bg-gray-100 text-gray-500 cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {passwordMsg.text && (
              <div className={`mb-3 p-2.5 rounded-xl text-xs font-bold flex items-center gap-1.5 ${
                passwordMsg.type === 'success' ? 'bg-emerald-50 text-emerald-800' : 'bg-red-50 text-red-700'
              }`}>
                {passwordMsg.type === 'success' ? <Check className="w-3.5 h-3.5" /> : null}
                <span>{passwordMsg.text}</span>
              </div>
            )}

            <form onSubmit={handleSubmitPasswordChange} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-[#583115] mb-1">Mật khẩu hiện tại</label>
                <input
                  type="password"
                  value={oldPassword}
                  onChange={(e) => setOldPassword(e.target.value)}
                  placeholder="Nhập mật khẩu cũ"
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-xs text-[#2b170c] outline-none"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-[#583115] mb-1">Mật khẩu mới</label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="Mật khẩu mới (tối thiểu 3 ký tự)"
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-xs text-[#2b170c] outline-none"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-[#583115] mb-1">Xác nhận mật khẩu mới</label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="Nhập lại mật khẩu mới"
                  className="w-full px-3 py-2 rounded-xl border border-[#ede4db] text-xs text-[#2b170c] outline-none"
                />
              </div>

              <div className="pt-2 flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setShowPasswordModal(false)}
                  className="flex-1 py-2 rounded-xl bg-gray-100 text-gray-700 font-bold text-xs cursor-pointer hover:bg-gray-200"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-[#3e200a] text-white font-bold text-xs cursor-pointer hover:bg-[#583115]"
                >
                  Lưu Mật Khẩu
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
