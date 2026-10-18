// @ts-check
const { test } = require('@playwright/test');
const path = require('path');

test.describe('CAPTURE JIRA BUG EVIDENCE SCREENSHOTS', () => {

  test('Capture Screenshot 1: Broken Access Control (Cashier accessing Admin)', async ({ page }) => {
    // 1. Mở trang đăng nhập
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');

    // 2. Đăng nhập với tài khoản cashier01
    await page.fill('#usernameInput', 'cashier01');
    await page.fill('#passwordInput', '123');
    await page.click('#btnLoginSubmit');
    await page.waitForTimeout(1000);

    // 3. Lúc này đang là cashier01 (Thu ngân). Bấm vào tab "Báo Cáo & Quản Trị" trên Navbar
    const btnAdminTab = page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first();
    await btnAdminTab.click();
    await page.waitForTimeout(1000);

    // 4. Chụp ảnh màn hình toàn cảnh chứng minh cashier01 đang ở giao diện Quản trị doanh thu & nhân sự
    const outPath = path.resolve(__dirname, '..', 'jira-screenshots', 'evidence-TC_PC_01-cashier-access-admin.png');
    await page.screenshot({ path: outPath, fullPage: false });
    console.log(`[CAPTURED] Screenshot 1 saved to: ${outPath}`);
  });

  test('Capture Screenshot 2: TC_PC_20 Missing Mark Completed Button on Kitchen KDS', async ({ page }) => {
    // 1. Mở app với autologin
    await page.goto('/?autologin=true');
    await page.waitForLoadState('domcontentloaded');

    // 2. Thêm 1 món và thanh toán để tạo đơn
    const firstDrink = page.locator('#menuGridContainer > div').first();
    await firstDrink.click();
    await page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn")').first().click();
    await page.locator('#btnOpenCheckout').click();
    await page.locator('button:has-text("Xác Nhận Đã Thu Tiền")').click();

    // Đóng hóa đơn
    const btnClose = page.locator('button:has-text("Đóng")').first();
    if (await btnClose.isVisible()) await btnClose.click();

    // 3. Chuyển sang Màn hình Bếp (KDS)
    await page.locator('button[data-tab-target="kitchen"], button:has-text("Màn Hình Bếp (KDS)")').click();
    await page.waitForTimeout(800);

    // Bấm Bắt đầu pha chế (nếu có đơn PENDING)
    const btnStart = page.locator('button:has-text("Bắt đầu pha chế")').first();
    if (await btnStart.isVisible()) {
      await btnStart.click();
      await page.waitForTimeout(600);
    }

    // Bấm Hoàn thành món
    const btnReady = page.locator('button:has-text("Hoàn thành món")').first();
    if (await btnReady.isVisible()) {
      await btnReady.click();
      await page.waitForTimeout(800);
    }

    // 4. Chụp ảnh cột READY chỉ có nhãn tĩnh "Đã sẵn sàng phục vụ", thiếu nút "Đã giao khách"
    const outPath = path.resolve(__dirname, '..', 'jira-screenshots', 'evidence-TC_PC_20-missing-completed-button.png');
    await page.screenshot({ path: outPath, fullPage: false });
    console.log(`[CAPTURED] Screenshot 2 saved to: ${outPath}`);
  });

});
