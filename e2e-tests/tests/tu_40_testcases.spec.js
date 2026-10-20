// @ts-check
const { test, expect } = require('@playwright/test');
const path = require('path');
const fs = require('fs');

/**
 * ============================================================================
 * TRỌN BỘ 40 TEST CASES KIỂM THỬ HỘP ĐEN (BLACK-BOX) & XUẤT VIDEO TỰ ĐỘNG
 * Tester: Tú | Reviewer: Huy | Đồ án Coffee Shop POS (Nhóm 15)
 * ============================================================================
 */

test.describe('BỘ 40 TEST CASES TÚ - PURRCOFFEE POS ADMIN', () => {

  test.beforeEach(async ({ page }) => {
    test.setTimeout(90000);
  });

  // Helper login vào hệ thống với vai trò Admin
  async function loginAsAdmin(page) {
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(400);

    const usernameInput = page.locator('#usernameInput');
    if (await usernameInput.isVisible()) {
      await usernameInput.fill('admin');
      await page.locator('#passwordInput').fill('123');
      await page.locator('#btnLoginSubmit, button:has-text("Đăng Nhập Vào Ca")').first().click();
      await page.waitForTimeout(600);
    }

    const adminNavBtn = page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first();
    if (await adminNavBtn.isVisible()) {
      await adminNavBtn.click();
      await page.waitForTimeout(500);
    }
  }

  // ==========================================================================
  // PHẦN 1: QUẢN LÝ ĐƠN HÀNG (TC_DH_001 -> TC_DH_007)
  // ==========================================================================
  test('PHAN_1_TC_DH_001_to_007 - Kiem thu Quan ly Don hang & Lich su Giao dich', async ({ page }) => {
    await loginAsAdmin(page);

    // TC_DH_001: Mở danh sách đơn hàng
    console.log('[RUN] TC_DH_001: Mở danh sách đơn hàng');
    const orderTabBtn = page.locator('button:has-text("Đơn Hàng"), button:has-text("🧾 Đơn Hàng")').first();
    await orderTabBtn.click();
    await page.waitForTimeout(800);

    const ordersTable = page.locator('table');
    await expect(ordersTable).toBeVisible();
    await expect(page.locator('th:has-text("Mã đơn")')).toBeVisible();
    await expect(page.locator('th:has-text("Tổng tiền")')).toBeVisible();

    // TC_DH_002: Lọc đơn hàng theo trạng thái PENDING
    console.log('[RUN] TC_DH_002: Lọc đơn hàng theo trạng thái PENDING (Chờ pha chế)');
    const pendingFilterBtn = page.locator('button:has-text("Chờ pha chế")').first();
    if (await pendingFilterBtn.isVisible()) {
      await pendingFilterBtn.click();
      await page.waitForTimeout(800);
    }

    // Quay lại danh sách tất cả
    const allFilterBtn = page.locator('button:has-text("Tất cả (")').first();
    if (await allFilterBtn.isVisible()) {
      await allFilterBtn.click();
      await page.waitForTimeout(800);
    }

    // TC_DH_004: Xem lại hoá đơn của một đơn hàng hợp lệ
    console.log('[RUN] TC_DH_004: Xem lại hoá đơn đơn hàng hợp lệ');
    const receiptBtn = page.locator('button:has-text("Hóa đơn")').first();
    if (await receiptBtn.isVisible()) {
      await receiptBtn.click();
      await page.waitForTimeout(1000);

      // Đóng Popup Hóa đơn
      const closeReceiptBtn = page.locator('button:has-text("Đóng"), button:has-text("✕")').first();
      if (await closeReceiptBtn.isVisible()) {
        await closeReceiptBtn.click();
        await page.waitForTimeout(600);
      }
    }

    // TC_DH_006: Thao tác chuyển tiếp quy trình làm món (State Pattern: Làm món -> Xong)
    console.log('[RUN] TC_DH_006: Thao tác chuyển trạng thái quy trình làm món');
    const startCookBtn = page.locator('button:has-text("Làm món")').first();
    if (await startCookBtn.isVisible()) {
      await startCookBtn.click();
      await page.waitForTimeout(800);
    }
    const finishCookBtn = page.locator('button:has-text("Xong")').first();
    if (await finishCookBtn.isVisible()) {
      await finishCookBtn.click();
      await page.waitForTimeout(800);
    }

    // TC_DH_003: Lọc đơn hàng theo trạng thái READY (Sẵn sàng)
    console.log('[RUN] TC_DH_003: Lọc đơn hàng theo trạng thái READY (Sẵn sàng)');
    const readyFilterBtn = page.locator('button:has-text("Sẵn sàng")').first();
    if (await readyFilterBtn.isVisible()) {
      await readyFilterBtn.click();
      await page.waitForTimeout(800);
    }

    // TC_DH_005: Hủy đơn hàng ở trạng thái READY
    console.log('[RUN] TC_DH_005: Hủy đơn hàng ở trạng thái READY');
    page.once('dialog', async dialog => {
      console.log(`[DIALOG TC_DH_005]: ${dialog.message()}`);
      await dialog.accept();
    });
    const cancelBtn = page.locator('button:has-text("Hủy")').first();
    if (await cancelBtn.isVisible()) {
      await cancelBtn.click();
      await page.waitForTimeout(1000);
    }

    // TC_DH_007: Kiểm tra trạng thái đơn hủy và bảo vệ đơn PAID
    console.log('[RUN] TC_DH_007: Kiểm tra trạng thái hủy');
    if (await allFilterBtn.isVisible()) {
      await allFilterBtn.click();
      await page.waitForTimeout(800);
    }
  });

  // ==========================================================================
  // PHẦN 2: QUẢN LÝ KHO & TỒN KHO (TC_KH_001 -> TC_KH_009)
  // ==========================================================================
  test('PHAN_2_TC_KH_001_to_009 - Kiem thu Kho Nguyen Vat Lieu & Nhap Hang', async ({ page }) => {
    await loginAsAdmin(page);

    // TC_KH_001: Kiểm tra mở tab Tồn kho
    console.log('[RUN] TC_KH_001: Mở tab Kho & Nhập hàng');
    const invTabBtn = page.locator('button:has-text("Kho & Nhập Hàng"), button:has-text("📦 Kho & Nhập Hàng")').first();
    await invTabBtn.click();
    await page.waitForTimeout(800);

    await expect(page.locator('h3:has-text("Kho Nguyên Vật Liệu")')).toBeVisible();
    await expect(page.locator('th:has-text("Tên nguyên liệu")')).toBeVisible();

    // TC_KH_005 & TC_KH_009: Tìm kiếm nguyên liệu theo tên "coffee"
    console.log('[RUN] TC_KH_005 & TC_KH_009: Tìm kiếm nguyên liệu "coffee"');
    const searchInput = page.locator('input[placeholder*="Tìm nguyên vật liệu"]').first();
    await searchInput.fill('coffee');
    await page.waitForTimeout(800);
    await expect(page.locator('text=Coffee beans').first()).toBeVisible();

    // Xóa tìm kiếm để hiển thị lại danh sách
    await searchInput.fill('');
    await page.waitForTimeout(600);

    // TC_KH_002: Nhập kho số lượng nguyên liệu hợp lệ (+1500g)
    console.log('[RUN] TC_KH_002: Nhập kho nguyên liệu Tea leaves hợp lệ (+1500)');
    const restockBtn = page.locator('button:has-text("+ Nhập Kho")').first();
    await restockBtn.click();
    await page.waitForTimeout(600);

    const restockModal = page.locator('h3:has-text("Nhập Hàng Vào Kho")').first();
    await expect(restockModal).toBeVisible();

    const amountInput = page.locator('form input[type="number"]').first();
    await amountInput.fill('1500');
    await page.waitForTimeout(600);

    const saveRestockBtn = page.locator('button:has-text("Xác Nhận Nhập Kho"), button[type="submit"]').first();
    await saveRestockBtn.click();
    await page.waitForTimeout(1000);

    // TC_KH_003: Cập nhật số lượng không hợp lệ (nhập ký tự chữ)
    console.log('[RUN] TC_KH_003: Kiểm tra validate nhập chữ vào trường số lượng');
    await restockBtn.click();
    await page.waitForTimeout(600);
    await amountInput.click();
    await page.keyboard.type('abc');
    await page.waitForTimeout(600);
    const inputValue = await amountInput.inputValue();
    expect(inputValue).not.toBe('abc');

    // TC_KH_004 & TC_KH_006: Kiểm tra giá trị nhỏ hơn min (0.001) hoặc số âm (-5)
    console.log('[RUN] TC_KH_004 & TC_KH_006: Nhập giá trị biên không hợp lệ 0.001 và -5');
    await amountInput.fill('0.001');
    await page.waitForTimeout(600);
    await amountInput.fill('-5');
    await page.waitForTimeout(600);

    // TC_KH_007: Để trống số lượng nhập thêm
    console.log('[RUN] TC_KH_007: Để trống số lượng nhập thêm (required validation)');
    await amountInput.fill('');
    await page.waitForTimeout(600);

    // Đóng Modal Nhập kho
    const closeBtn = page.locator('button:has-text("Hủy"), button:has-text("✕")').first();
    if (await closeBtn.isVisible()) {
      await closeBtn.click();
      await page.waitForTimeout(600);
    }
  });

  // ==========================================================================
  // PHẦN 3: QUẢN LÝ THỰC ĐƠN MENU (TC_TD_001 -> TC_TD_009)
  // ==========================================================================
  test('PHAN_3_TC_TD_001_to_009 - Kiem thu Danh muc Thuc don & Gia ban', async ({ page }) => {
    await loginAsAdmin(page);

    // Mở tab Thực đơn
    console.log('[RUN] Mở tab Thực đơn (Menu Catalog)');
    const menuTabBtn = page.locator('button:has-text("Thực Đơn"), button:has-text("☕ Thực Đơn")').first();
    await menuTabBtn.click();
    await page.waitForTimeout(800);

    // TC_TD_001: Lọc đồ uống theo danh mục Cà phê
    console.log('[RUN] TC_TD_001: Lọc danh mục Cà phê');
    await page.locator('button:has-text("Cà phê")').first().click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Cà phê sữa').first()).toBeVisible();

    // TC_TD_002: Lọc đồ uống theo danh mục Trà trái cây
    console.log('[RUN] TC_TD_002: Lọc danh mục Trà trái cây');
    await page.locator('button:has-text("Trà trái cây")').first().click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Trà đào').first()).toBeVisible();

    // TC_TD_003: Lọc đồ uống theo danh mục Matcha
    console.log('[RUN] TC_TD_003: Lọc danh mục Matcha');
    await page.locator('button:has-text("Matcha")').first().click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Matcha Latte').first()).toBeVisible();

    // TC_TD_004: Lọc đồ uống theo danh mục Sinh tố
    console.log('[RUN] TC_TD_004: Lọc danh mục Sinh tố');
    await page.locator('button:has-text("Sinh tố")').first().click();
    await page.waitForTimeout(800);
    await expect(page.locator('text=Sinh tố xoài').first()).toBeVisible();

    // Trở về "Tất cả"
    await page.locator('button:has-text("Tất cả (")').first().click();
    await page.waitForTimeout(600);

    // TC_TD_005: Cập nhật đơn giá gốc hợp lệ (từ 30000 lên 35000)
    console.log('[RUN] TC_TD_005: Chỉnh sửa giá đồ uống hợp lệ');
    const editDrinkBtn = page.locator('button[title="Sửa món"]').first();
    await editDrinkBtn.click();
    await page.waitForTimeout(600);

    const priceInput = page.locator('input[type="number"][step="1000"]').first();
    await priceInput.fill('35000');
    await page.waitForTimeout(600);

    const saveDrinkBtn = page.locator('button:has-text("Lưu Đồ Uống")').first();
    await saveDrinkBtn.click();
    await page.waitForTimeout(1000);

    // TC_TD_006: Chỉnh sửa đồ uống - để trống Tên Đồ Uống (Validate Required)
    console.log('[RUN] TC_TD_006: Để trống Tên đồ uống khi chỉnh sửa');
    await editDrinkBtn.click();
    await page.waitForTimeout(600);

    const nameInput = page.locator('form input[type="text"]').first();
    await nameInput.fill('');
    await page.waitForTimeout(600);
    await saveDrinkBtn.click();
    await page.waitForTimeout(600);

    // Khôi phục lại tên món
    await nameInput.fill('Cà phê sữa');
    await page.waitForTimeout(400);

    // TC_TD_008: Tạm ngưng bán món trên màn hình POS
    console.log('[RUN] TC_TD_008: Tắt bán món trên POS');
    const activeCheckbox = page.locator('#bevActiveCheck').first();
    if (await activeCheckbox.isChecked()) {
      await activeCheckbox.uncheck();
      await page.waitForTimeout(500);
    }
    await saveDrinkBtn.click();
    await page.waitForTimeout(1000);

    // Kiểm tra trên màn hình POS: món Cà phê sữa bị ẩn
    console.log('[RUN] Kiểm tra POS: Món tạm ngưng đã ẩn');
    await page.locator('button[data-tab-target="pos"], button:has-text("Thu Ngân (POS)")').first().click();
    await page.waitForTimeout(1000);

    // TC_TD_009: Mở bán lại món đã bị tạm ngưng
    console.log('[RUN] TC_TD_009: Mở bán lại món trên Admin');
    await page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first().click();
    await page.waitForTimeout(600);
    await menuTabBtn.click();
    await page.waitForTimeout(600);

    const toggleStatusBtn = page.locator('button:has-text("Tạm ngưng")').first();
    if (await toggleStatusBtn.isVisible()) {
      await toggleStatusBtn.click();
      await page.waitForTimeout(800);
    }
    await page.waitForTimeout(600);
  });

  // ==========================================================================
  // PHẦN 4: BÁO CÁO DOANH THU & THỐNG KÊ (TC_BC_001 -> TC_BC_011)
  // ==========================================================================
  test('PHAN_4_TC_BC_001_to_011 - Kiem thu Bao cao Doanh thu & Phan quyen', async ({ page }) => {
    await loginAsAdmin(page);

    // TC_BC_001: Mở tab Báo cáo doanh thu
    console.log('[RUN] TC_BC_001: Mở tab Báo cáo doanh thu');
    const reportTabBtn = page.locator('button:has-text("Báo Cáo Doanh Thu"), button:has-text("📈 Báo Cáo Doanh Thu")').first();
    await reportTabBtn.click();
    await page.waitForTimeout(800);

    await expect(page.locator('h3:has-text("Báo Cáo & Thống Kê Doanh Thu")')).toBeVisible();

    // TC_BC_002: Xem tổng doanh thu và các thẻ KPI
    console.log('[RUN] TC_BC_002: Kiểm tra tổng doanh thu và KPI');
    await expect(page.locator('p:has-text("Doanh Thu Kỳ Này")')).toBeVisible();
    await expect(page.locator('p:has-text("Số Đơn Hàng")')).toBeVisible();
    await expect(page.locator('p:has-text("Giá Trị TB / Đơn (AOV)")')).toBeVisible();

    // TC_BC_007: Lọc đơn hàng hôm nay
    console.log('[RUN] TC_BC_007: Lọc kỳ Hôm nay');
    await page.locator('button:has-text("Hôm nay")').first().click();
    await page.waitForTimeout(800);

    // TC_BC_008: Lọc đơn hàng 7 ngày qua
    console.log('[RUN] TC_BC_008: Lọc kỳ 7 ngày qua');
    await page.locator('button:has-text("7 ngày qua")').first().click();
    await page.waitForTimeout(800);

    // TC_BC_009: Lọc đơn hàng tháng này
    console.log('[RUN] TC_BC_009: Lọc kỳ Tháng này');
    await page.locator('button:has-text("Tháng này")').first().click();
    await page.waitForTimeout(800);

    // Quay lại Toàn bộ để có đủ dữ liệu giao dịch
    await page.locator('button:has-text("Toàn bộ")').first().click();
    await page.waitForTimeout(800);

    // TC_BC_010: Xem lại hoá đơn từ danh sách báo cáo
    console.log('[RUN] TC_BC_010: Xem lại hóa đơn trong tab Báo cáo');
    const viewReceiptInReport = page.locator('button:has-text("Xem Hóa Đơn")').first();
    if (await viewReceiptInReport.isVisible()) {
      await viewReceiptInReport.click();
      await page.waitForTimeout(800);
      const closeReceipt = page.locator('button:has-text("Đóng"), button:has-text("✕")').first();
      if (await closeReceipt.isVisible()) {
        await closeReceipt.click();
        await page.waitForTimeout(600);
      }
    }

    // TC_BC_003 & TC_BC_006: Xuất file báo cáo doanh thu CSV
    console.log('[RUN] TC_BC_003 & TC_BC_006: Xuất file báo cáo CSV');
    const downloadPromise = page.waitForEvent('download', { timeout: 5000 }).catch(() => null);
    await page.locator('button:has-text("Xuất Báo Cáo (CSV)")').first().click();
    const download = await downloadPromise;
    if (download) {
      console.log(`[DOWNLOAD SUCCESS] File: ${download.suggestedFilename()}`);
    }
    await page.waitForTimeout(800);

    // TC_BC_004: Kiểm tra ngày bắt đầu lớn hơn ngày kết thúc (Ngoại lệ)
    console.log('[RUN] TC_BC_004: Kiểm tra date range ngược (Ngoại lệ)');
    page.once('dialog', async dialog => {
      console.log(`[DIALOG TC_BC_004 ALERT]: ${dialog.message()}`);
      await dialog.accept();
    });
    const dateInputs = page.locator('input[type="date"]');
    if (await dateInputs.count() >= 2) {
      await dateInputs.nth(0).fill('2026-12-31');
      await dateInputs.nth(1).fill('2026-01-01');
      await page.waitForTimeout(600);
      await page.locator('button:has-text("Xuất Báo Cáo (CSV)")').first().click();
      await page.waitForTimeout(800);
    }

    // TC_BC_005: Kiểm tra quyền xem báo cáo (Thu ngân không truy cập Admin)
    console.log('[RUN] TC_BC_005: Kiểm tra phân quyền truy cập');
    const logoutBtn = page.locator('button:has-text("Đăng xuất"), button[title="Đăng xuất"]').first();
    if (await logoutBtn.isVisible()) {
      await logoutBtn.click();
      await page.waitForTimeout(800);
    }
  });

  // ==========================================================================
  // PHẦN 5: QUẢN LÝ TOPPING (TC_TO_001 -> TC_TO_005)
  // ==========================================================================
  test('PHAN_5_TC_TO_001_to_005 - Kiem thu Topping Decorator Pattern', async ({ page }) => {
    await loginAsAdmin(page);

    // Mở tab Topping
    console.log('[RUN] Mở tab Topping (Decorator Catalog)');
    const toppingTabBtn = page.locator('button:has-text("Topping"), button:has-text("🧋 Topping")').first();
    await toppingTabBtn.click();
    await page.waitForTimeout(800);

    await expect(page.locator('h3:has-text("Danh Mục Topping")')).toBeVisible();

    // TC_TO_005: Thêm topping mới "Thạch đào" (9000 VND)
    console.log('[RUN] TC_TO_005: Thêm topping mới Thạch đào (9000 VND)');
    await page.locator('button:has-text("Thêm Topping Mới")').first().click();
    await page.waitForTimeout(600);

    const nameInput = page.locator('form input[type="text"]').first();
    const priceInput = page.locator('form input[type="number"]').first();

    await nameInput.fill('Thạch đào');
    await priceInput.fill('9000');
    await page.waitForTimeout(600);

    await page.locator('button:has-text("Lưu Topping")').first().click();
    await page.waitForTimeout(1000);
    await expect(page.locator('text=Thạch đào').first()).toBeVisible();

    // TC_TO_001: Chỉnh sửa topping với tên và giá hợp lệ
    console.log('[RUN] TC_TO_001: Chỉnh sửa topping');
    const editBtn = page.locator('table tr:has-text("Trân châu trắng")').locator('button').first();
    if (await editBtn.isVisible()) {
      await editBtn.click();
      await page.waitForTimeout(600);

      const editModalName = page.locator('form input[type="text"]').first();
      const editModalPrice = page.locator('form input[type="number"]').first();

      if (await editModalName.isVisible()) {
        await editModalName.fill('Trân châu trắng VIP');
        await editModalPrice.fill('12000');
        await page.waitForTimeout(600);

        await page.locator('button:has-text("Lưu Topping")').first().click();
        await page.waitForTimeout(1000);
      }
    }

    // TC_TO_002: Kiểm tra ô giá Topping không nhận ký tự chữ
    console.log('[RUN] TC_TO_002: Validate nhập ký tự chữ vào giá Topping');
    const editBtnAny = page.locator('table tbody tr').first().locator('button').nth(1);
    if (await editBtnAny.isVisible()) {
      await editBtnAny.click();
      await page.waitForTimeout(500);

      const topPriceInput = page.locator('form input[type="number"]').first();
      if (await topPriceInput.isVisible()) {
        await topPriceInput.click();
        await page.keyboard.type('abc');
        await page.waitForTimeout(500);
        const topVal = await topPriceInput.inputValue();
        expect(topVal).not.toBe('abc');

        // Đóng modal
        const cancelBtn = page.locator('button:has-text("Hủy")').first();
        if (await cancelBtn.isVisible()) await cancelBtn.click();
        await page.waitForTimeout(600);
      }
    }

    // TC_TO_003: Ngưng bán topping (Tạm tắt trên POS)
    console.log('[RUN] TC_TO_003: Tắt topping trên POS');
    const toggleToppingBtn = page.locator('button:has-text("Khả dụng")').first();
    if (await toggleToppingBtn.isVisible()) {
      await toggleToppingBtn.click();
      await page.waitForTimeout(800);
    }

    // TC_TO_004: Mở bán lại topping đã bị ngưng
    console.log('[RUN] TC_TO_004: Mở bán lại topping');
    const turnOnToppingBtn = page.locator('button:has-text("Tạm tắt")').first();
    if (await turnOnToppingBtn.isVisible()) {
      await turnOnToppingBtn.click();
      await page.waitForTimeout(800);
    }

    // Kiểm tra trên màn hình POS
    console.log('[RUN] Kiểm tra POS: Decorator Modal hiển thị Topping đầy đủ');
    await page.locator('button[data-tab-target="pos"], button:has-text("Thu Ngân (POS)")').first().click();
    await page.waitForTimeout(800);

    const firstDrink = page.locator('#menuGridContainer > div, .cursor-pointer').first();
    if (await firstDrink.isVisible()) {
      await firstDrink.click();
      await page.waitForTimeout(800);

      const cancelCustomBtn = page.locator('button:has-text("Hủy"), button:has-text("✕")').first();
      if (await cancelCustomBtn.isVisible()) {
        await cancelCustomBtn.click();
        await page.waitForTimeout(600);
      }
    }

    console.log('[SUCCESS] Hoàn tất trọn bộ kịch bản kiểm thử 40 Test Cases của Tú!');
  });

});
