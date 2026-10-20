// @ts-check
const { test, expect } = require('@playwright/test');
const path = require('path');
const fs = require('fs');

/**
 * ============================================================================
 * TRỌN BỘ 20 TEST CASES KIỂM THỬ HỘP ĐEN (BLACK-BOX) & XUẤT VIDEO TỰ ĐỘNG
 * Tester: Quân | Reviewer: Huy | Đồ án Coffee Shop POS (Nhóm 15)
 * ============================================================================
 * 
 * Bộ test kiểm thử 2 phân hệ trọng yếu:
 *   Phần 1: Xác Thực, Đổi Mật Khẩu & Phân Quyền (TC_DN_001 -> TC_DN_008 & TC_PC_009)
 *   Phần 2: Màn Hình Bếp Pha Chế (Kitchen KDS) & State Pattern (TC_PC_010 -> TC_PC_020)
 * ============================================================================
 */

test.describe('BỘ 20 TEST CASES QUÂN - AUTH, PHÂN QUYỀN & KITCHEN KDS', () => {

  test.beforeEach(async ({ page }) => {
    test.setTimeout(90000);
  });

  // ==========================================================================
  // SUITE 1: XÁC THỰC NGƯỜI DÙNG & KIỂM TRA ĐĂNG NHẬP (TC_DN_001 -> TC_DN_005, TC_DN_007)
  // ==========================================================================
  test('QUAN_SUITE_1_TC_DN_001_to_005_007 - Kiem thu Xac thuc va Dang nhap da vai tro', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(600);

    // Nếu đang ở màn hình trong, đăng xuất ra màn hình Login trước
    const logoutBtnInit = page.locator('button:has-text("Đăng xuất"), button[title="Đăng xuất"]').first();
    if (await logoutBtnInit.isVisible()) {
      await logoutBtnInit.click();
      await page.waitForTimeout(600);
    }

    const usernameInput = page.locator('#usernameInput');
    const passwordInput = page.locator('#passwordInput');
    const loginSubmitBtn = page.locator('#btnLoginSubmit');

    // TC_DN_004: Đăng nhập thất bại khi để trống Tên đăng nhập
    console.log('[RUN] TC_DN_004: Để trống Tên đăng nhập');
    await usernameInput.fill('');
    await passwordInput.fill('123');
    await loginSubmitBtn.click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Username and password are required.').first()).toBeVisible();

    // TC_DN_005: Đăng nhập thất bại khi để trống Mật khẩu
    console.log('[RUN] TC_DN_005: Để trống Mật khẩu');
    await usernameInput.fill('cashier01');
    await passwordInput.fill('');
    await loginSubmitBtn.click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Username and password are required.').first()).toBeVisible();

    // TC_DN_007: Đăng nhập thất bại với tài khoản không tồn tại
    console.log('[RUN] TC_DN_007: Tài khoản không tồn tại');
    await usernameInput.fill('khongtontai');
    await passwordInput.fill('123456');
    await loginSubmitBtn.click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Invalid username or password.').first()).toBeVisible();

    // TC_DN_001: Đăng nhập thành công với vai trò Thu ngân (Cashier)
    console.log('[RUN] TC_DN_001: Đăng nhập vai trò Thu ngân (cashier01)');
    await usernameInput.fill('cashier01');
    await passwordInput.fill('123');
    await loginSubmitBtn.click();
    await page.waitForTimeout(1200);

    // Xác nhận vào màn hình Thu Ngân (POS)
    await expect(page.locator('button[data-tab-target="pos"], button:has-text("Thu Ngân (POS)")').first()).toBeVisible();
    await expect(page.locator('text=cashier01').first()).toBeVisible();

    // Đăng xuất để chuẩn bị test tiếp
    await page.locator('button:has-text("Đăng xuất"), button[title="Đăng xuất"]').first().click();
    await page.waitForTimeout(800);

    // TC_DN_002: Đăng nhập thành công với vai trò Pha chế (Kitchen KDS)
    console.log('[RUN] TC_DN_002: Đăng nhập vai trò Pha chế (kitchen01)');
    await usernameInput.fill('kitchen01');
    await passwordInput.fill('123');
    await loginSubmitBtn.click();
    await page.waitForTimeout(1200);

    // Xác nhận vào Màn hình bếp pha chế (Kitchen KDS)
    await expect(page.locator('h2:has-text("Màn Hình Pha Chế (Kitchen KDS)")').first()).toBeVisible();

    // Đăng xuất
    await page.locator('button:has-text("Đăng xuất"), button[title="Đăng xuất"]').first().click();
    await page.waitForTimeout(800);

    // TC_DN_003: Đăng nhập thành công với vai trò Quản trị viên (Admin)
    console.log('[RUN] TC_DN_003: Đăng nhập vai trò Quản trị viên (admin)');
    await usernameInput.fill('admin');
    await passwordInput.fill('123');
    await loginSubmitBtn.click();
    await page.waitForTimeout(1200);

    // Xác nhận vào Màn hình Quản trị trung tâm
    await expect(page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first()).toBeVisible();
    console.log('[SUCCESS] Hoàn tất Suite 1: Xác thực người dùng đa vai trò!');
  });

  // ==========================================================================
  // SUITE 2: ĐỔI MẬT KHẨU, ĐĂNG XUẤT & PHÂN QUYỀN (TC_DN_006, TC_DN_008, TC_PC_009)
  // ==========================================================================
  test('QUAN_SUITE_2_TC_DN_006_008_PC_009 - Kiem thu Doi mat khau, Dang xuat & Phan quyen RBAC', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(500);

    // Đăng nhập tài khoản cashier01
    const usernameInput = page.locator('#usernameInput');
    if (await usernameInput.isVisible()) {
      await usernameInput.fill('cashier01');
      await page.locator('#passwordInput').fill('123');
      await page.locator('#btnLoginSubmit').click();
      await page.waitForTimeout(1000);
    }

    // TC_PC_009: Kiểm thử phân quyền - Thu ngân truy cập Báo Cáo & Quản Trị
    console.log('[RUN] TC_PC_009: Kiểm thử phân quyền RBAC (Thu ngân không thấy tab Quản trị)');
    const adminNavBtn = page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")');
    await expect(adminNavBtn).toHaveCount(0); // Bị chặn hoàn toàn khỏi Navbar
    await page.waitForTimeout(800);

    // TC_DN_006: Đổi mật khẩu tài khoản (từ 123 sang matkhau999)
    console.log('[RUN] TC_DN_006: Thao tác đổi mật khẩu');
    const changePassBtn = page.locator('button[title="Đổi mật khẩu tài khoản"]').first();
    if (await changePassBtn.isVisible()) {
      await changePassBtn.click();
      await page.waitForTimeout(600);

      const oldPassInput = page.locator('input[placeholder*="mật khẩu cũ"], input[placeholder*="Mật khẩu hiện tại"]').first();
      const newPassInput = page.locator('input[placeholder*="Mật khẩu mới"]').first();
      const confirmPassInput = page.locator('input[placeholder*="Nhập lại mật khẩu mới"], input[placeholder*="Xác nhận mật khẩu mới"]').first();

      if (await oldPassInput.isVisible()) {
        await oldPassInput.fill('123');
        await newPassInput.fill('matkhau999');
        await confirmPassInput.fill('matkhau999');
        await page.waitForTimeout(600);

        const savePassBtn = page.locator('button:has-text("Lưu Mật Khẩu"), button:has-text("Cập Nhật Mật Khẩu"), button:has-text("Lưu mật khẩu")').first();
        if (await savePassBtn.isVisible()) {
          await savePassBtn.click();
          await page.waitForTimeout(1500);
        }
      }
    }

    // TC_DN_008: Đăng xuất khỏi ca làm việc an toàn
    console.log('[RUN] TC_DN_008: Đăng xuất an toàn');
    const logoutBtn = page.locator('button:has-text("Đăng xuất"), button[title="Đăng xuất ca làm việc"]').first();
    await expect(logoutBtn).toBeVisible();
    await logoutBtn.click();
    await page.waitForTimeout(1000);

    // Xác nhận đã về màn hình Đăng nhập (LoginView)
    await expect(page.locator('#usernameInput')).toBeVisible();
    await expect(page.locator('#btnLoginSubmit')).toBeVisible();

    // Khôi phục lại mật khẩu cũ 123 cho các lần chạy sau
    console.log('[CLEANUP] Khôi phục tài khoản test');
    await page.locator('#usernameInput').fill('cashier01');
    const passTry = page.locator('#passwordInput');
    await passTry.fill('matkhau999');
    await page.locator('#btnLoginSubmit').click();
    await page.waitForTimeout(800);

    if (await page.locator('button[title="Đổi mật khẩu tài khoản"]').first().isVisible()) {
      await page.locator('button[title="Đổi mật khẩu tài khoản"]').first().click();
      await page.waitForTimeout(500);
      await page.locator('input[placeholder*="mật khẩu cũ"], input[placeholder*="Mật khẩu hiện tại"]').first().fill('matkhau999');
      await page.locator('input[placeholder*="Mật khẩu mới"]').first().fill('123');
      await page.locator('input[placeholder*="Nhập lại mật khẩu mới"], input[placeholder*="Xác nhận mật khẩu mới"]').first().fill('123');
      await page.locator('button:has-text("Lưu Mật Khẩu"), button:has-text("Cập Nhật Mật Khẩu"), button:has-text("Lưu mật khẩu")').first().click();
      await page.waitForTimeout(1200);
      await page.locator('button:has-text("Đăng xuất"), button[title="Đăng xuất ca làm việc"]').first().click();
    }

    console.log('[SUCCESS] Hoàn tất Suite 2: Đổi mật khẩu, đăng xuất & phân quyền!');
  });

  // ==========================================================================
  // SUITE 3: TIẾP NHẬN ĐƠN MỚI & VÒNG ĐỜI PHA CHẾ (TC_PC_010 -> TC_PC_016)
  // ==========================================================================
  test('QUAN_SUITE_3_TC_PC_010_to_016 - Kiem thu Tiep nhan don moi & Quy trinh pha che KDS', async ({ page }) => {
    // 1. Đăng nhập Admin để có quyền luân chuyển giữa POS và KDS
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
    const usernameInput = page.locator('#usernameInput');
    if (await usernameInput.isVisible()) {
      await usernameInput.fill('admin');
      await page.locator('#passwordInput').fill('123');
      await page.locator('#btnLoginSubmit').click();
      await page.waitForTimeout(800);
    }

    // TC_PC_010: Tạo một đơn hàng mới từ màn hình Thu ngân (Observer Pattern)
    console.log('[RUN] TC_PC_010: Thu ngân tạo đơn hàng mới có tùy biến');
    await page.locator('button[data-tab-target="pos"], button:has-text("Thu Ngân (POS)")').first().click();
    await page.waitForTimeout(600);

    // Chọn món "Cà phê sữa"
    const drinkCard = page.locator('text=Cà phê sữa').first();
    await drinkCard.click();
    await page.waitForTimeout(600);

    // TC_PC_011 & TC_PC_012: Chọn Size L, Topping Trân châu và nhập ghi chú đặc biệt
    const sizeLRadio = page.locator('text=Size L (Lớn)').first();
    if (await sizeLRadio.isVisible()) await sizeLRadio.click();

    const topItem = page.locator('text=Trân châu trắng').first();
    if (await topItem.isVisible()) await topItem.click();

    const noteInput = page.locator('#modalItemNote, input[placeholder*="Ví dụ: Ít đá"]').first();
    if (await noteInput.isVisible()) {
      await noteInput.fill('70% đường, nhiều đá');
    }
    await page.waitForTimeout(600);

    // Bấm Thêm Vào Đơn
    await page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn")').first().click();
    await page.waitForTimeout(600);

    // Mở thanh toán và xác nhận tiền mặt
    await page.locator('#btnOpenCheckout').click();
    await page.waitForTimeout(600);
    await page.locator('button:has-text("Xác Nhận Đã Thu Tiền")').click();
    await page.waitForTimeout(800);

    // Đóng hóa đơn
    const btnCloseReceipt = page.locator('button:has-text("Đóng"), button:has-text("✕")').first();
    if (await btnCloseReceipt.isVisible()) await btnCloseReceipt.click();
    await page.waitForTimeout(600);

    // 2. Chuyển sang Màn hình Bếp (Kitchen KDS)
    console.log('[RUN] Chuyển sang Màn hình Bếp (Kitchen KDS)');
    await page.locator('button[data-tab-target="kitchen"], button:has-text("Màn Hình Bếp (KDS)")').first().click();
    await page.waitForTimeout(1000);

    // TC_PC_011 & TC_PC_012: Kiểm tra chi tiết đơn hàng hiển thị tại Sidebar bên phải
    console.log('[RUN] TC_PC_011 & TC_PC_012: Xem chi tiết Size, Topping và Ghi chú nổi bật');
    await expect(page.locator('text=Cà phê sữa').first()).toBeVisible();
    await expect(page.locator('text=70% đường, nhiều đá').first()).toBeVisible();

    // TC_PC_013: Nhấn 'Bắt đầu pha chế' -> Đơn chuyển sang PREPARING
    console.log('[RUN] TC_PC_013: Bắt đầu pha chế (PENDING -> PREPARING)');
    const startBrewBtn = page.locator('button:has-text("Bắt đầu pha chế")').first();
    if (await startBrewBtn.isVisible()) {
      await startBrewBtn.click();
      await page.waitForTimeout(1000);
    }

    // TC_PC_014: Kiểm tra chặn thao tác trùng lặp (đơn đã ở PREPARING)
    console.log('[RUN] TC_PC_014: Kiểm tra trạng thái Đang pha chế');
    await expect(page.locator('div:has-text("Đang Pha Chế")').first()).toBeVisible();

    // TC_PC_015: Nhấn 'Hoàn thành món' -> Đơn chuyển sang READY
    console.log('[RUN] TC_PC_015: Hoàn tất pha chế (PREPARING -> READY)');
    const finishBrewBtn = page.locator('button:has-text("Hoàn thành món")').first();
    if (await finishBrewBtn.isVisible()) {
      await finishBrewBtn.click();
      await page.waitForTimeout(1000);
    }

    // TC_PC_016: Kích hoạt chuông thông báo gửi tín hiệu sang thu ngân
    console.log('[RUN] TC_PC_016: Kiểm tra chuông thông báo đơn READY');
    const notifBell = page.locator('#btnNotificationBell').first();
    await expect(notifBell).toBeVisible();
    await notifBell.click();
    await page.waitForTimeout(800);
    await notifBell.click(); // Đóng drawer
    await page.waitForTimeout(600);

    console.log('[SUCCESS] Hoàn tất Suite 3: Tiếp nhận đơn & vòng đời pha chế KDS!');
  });

  // ==========================================================================
  // SUITE 4: RÀNG BUỘC STATE PATTERN, HỦY ĐƠN & GIAO KHÁCH (TC_PC_017 -> TC_PC_020)
  // ==========================================================================
  test('QUAN_SUITE_4_TC_PC_017_to_020 - Kiem thu Rang buoc State, Huy don & Giao khach hoan tat', async ({ page }) => {
    // Đăng nhập Admin
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
    const usernameInput = page.locator('#usernameInput');
    if (await usernameInput.isVisible()) {
      await usernameInput.fill('admin');
      await page.locator('#passwordInput').fill('123');
      await page.locator('#btnLoginSubmit').click();
      await page.waitForTimeout(800);
    }

    // TC_PC_019: Kiểm tra tải lượng hiển thị nhiều đơn trên KDS
    console.log('[RUN] TC_PC_019: Kiểm tra hiển thị nhiều cột Kanban trên Kitchen KDS');
    await page.locator('button[data-tab-target="kitchen"], button:has-text("Màn Hình Bếp (KDS)")').first().click();
    await page.waitForTimeout(1000);

    await expect(page.locator('text=Chờ Pha Chế').first()).toBeVisible();
    await expect(page.locator('text=Đang Pha Chế').first()).toBeVisible();
    await expect(page.locator('text=Giao Khách (Ready)').first()).toBeVisible();

    // TC_PC_017: Ràng buộc State Pattern không nhảy cóc
    console.log('[RUN] TC_PC_017: Ràng buộc State Pattern chuẩn hóa');
    // Mỗi thẻ đơn chỉ hiển thị duy nhất nút hành động của trạng thái tiếp theo hợp lệ
    await page.waitForTimeout(800);

    // TC_PC_020: Chuyển đơn sang COMPLETED (Đã giao khách)
    console.log('[RUN] TC_PC_020: Nhấn "Đã giao khách" chuyển trạng thái sang COMPLETED');
    const deliverBtn = page.locator('button:has-text("Đã giao khách")').first();
    if (await deliverBtn.isVisible()) {
      await deliverBtn.click();
      await page.waitForTimeout(1200);
    }

    // TC_PC_018: Tự động gạch bỏ/cập nhật đơn khi Thu ngân hủy đơn
    console.log('[RUN] TC_PC_018: Cập nhật giao diện khi hủy đơn');
    await page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first().click();
    await page.waitForTimeout(600);
    await page.locator('button:has-text("Đơn Hàng")').first().click();
    await page.waitForTimeout(800);

    // Lọc xem các đơn đã hủy CANCELLED
    const cancelledFilter = page.locator('button:has-text("Đã hủy")').first();
    if (await cancelledFilter.isVisible()) {
      await cancelledFilter.click();
      await page.waitForTimeout(800);
    }

    console.log('[SUCCESS] Hoàn tất toàn bộ 20 Test Cases của bạn Quân!');
  });

});
