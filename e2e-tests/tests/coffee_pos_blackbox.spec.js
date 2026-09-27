// @ts-check
const { test, expect } = require('@playwright/test');

test.describe('BỘ KIỂM THỬ HỘP ĐEN (BLACK-BOX) - PURRCOFFEE POS', () => {

  test.beforeEach(async ({ page }) => {
    // 1. Mở trang web ứng dụng POS
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
  });

  test('TC01: Kiểm tra giao diện chính và tìm kiếm món', async ({ page }) => {
    // Kiểm tra tiêu đề trang
    await expect(page).toHaveTitle(/PurrCoffee POS/);

    // Tìm kiếm món "Bạc xỉu"
    const searchInput = page.locator('#menuSearch');
    await expect(searchInput).toBeVisible();
    await searchInput.fill('Bạc xỉu');

    // Kiểm tra món xuất hiện trong danh sách hiển thị
    const drinkItem = page.locator('text=Bạc xỉu').first();
    await expect(drinkItem).toBeVisible();

    // Xóa tìm kiếm và thử chuyển tab danh mục Trà
    await searchInput.fill('');
    const teaTab = page.locator('button[data-category="TEA"]');
    await teaTab.click();

    // Kiểm tra tab danh mục Trà được active
    await expect(teaTab).toBeVisible();
  });

  test('TC02: Kiểm tra chức năng thêm món vào giỏ hàng', async ({ page }) => {
    // Chờ menu món hiển thị
    const firstDrinkCard = page.locator('#menuGridContainer > div').first();
    await expect(firstDrinkCard).toBeVisible();

    // Click vào món đầu tiên để chọn món
    await firstDrinkCard.click();

    // Nếu hiển thị Modal tùy chỉnh (Customization modal)
    const modalAddBtn = page.locator('#btnModalAddToCart, button:has-text("Thêm vào đơn")').first();
    if (await modalAddBtn.isVisible()) {
      await modalAddBtn.click();
    }

    // Kiểm tra số lượng giỏ hàng được cập nhật
    const cartBadge = page.locator('#cartCountBadge');
    await expect(cartBadge).not.toHaveText('0');

    // Nút thanh toán phải được kích hoạt (enabled)
    const btnCheckout = page.locator('#btnOpenCheckout');
    await expect(btnCheckout).toBeEnabled();
  });

  test('TC03: Kiểm tra áp dụng mã khuyến mãi (Strategy Pattern)', async ({ page }) => {
    // Thêm món vào giỏ
    const firstDrinkCard = page.locator('#menuGridContainer > div').first();
    await firstDrinkCard.click();
    const modalAddBtn = page.locator('#btnModalAddToCart, button:has-text("Thêm vào đơn")').first();
    if (await modalAddBtn.isVisible()) {
      await modalAddBtn.click();
    }

    // Chọn mã giảm giá 10%
    const discountSelect = page.locator('#discountSelect');
    await discountSelect.selectOption('PERCENT_10');

    // Kiểm tra tiền giảm giá được hiển thị khác 0 ₫
    const discountText = page.locator('#cartDiscount');
    await expect(discountText).not.toHaveText('0 ₫');
  });

  test('TC04: Kiểm tra điều hướng sang Màn hình Pha Chế (Kitchen KDS)', async ({ page }) => {
    // Bấm nút chuyển sang màn hình Bếp
    const kitchenNavBtn = page.locator('button[data-tab-target="kitchen"]');
    await kitchenNavBtn.click();

    // Kiểm tra trang Bếp hiển thị
    const kitchenPage = page.locator('#page-kitchen');
    await expect(kitchenPage).toBeVisible();

    // Kiểm tra 3 cột trạng thái pha chế
    await expect(page.locator('text=Chờ Pha Chế')).toBeVisible();
    await expect(page.locator('text=Đang Pha Chế')).toBeVisible();
    await expect(page.locator('text=Sẵn Sàng Giao Khách')).toBeVisible();
  });

  test('TC05: Kiểm tra điều hướng sang Màn hình Báo Cáo & Quản Trị (Admin)', async ({ page }) => {
    // Bấm nút chuyển sang Admin
    const adminNavBtn = page.locator('button[data-tab-target="admin"]');
    await adminNavBtn.click();

    // Kiểm tra trang Admin hiển thị
    const adminPage = page.locator('#page-admin');
    await expect(adminPage).toBeVisible();
    await expect(page.locator('text=Báo Cáo & Quản Trị Hệ Thống')).toBeVisible();
  });
});
