// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * BỘ KIỂM THỬ HỘP ĐEN THUẦN TÚY (PURE BLACK-BOX TESTING SUITE)
 * Không sử dụng bất kỳ thông tin nào về mã nguồn nội bộ.
 * Chỉ kiểm thử thông qua giao diện trình duyệt (UI), hành vi người dùng, 
 * và các kỹ thuật Black-box: BVA, EP, Keyboard Usability, Fuzzing.
 */

test.describe('PURE BLACK-BOX TESTING - KIỂM THỬ HỘP ĐEN THUẦN TÚY', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/?autologin=true');
    await page.waitForLoadState('domcontentloaded');
  });

  test('BB-BUG-01: [BVA] Mua 1 Tặng 1 (BOGO) không giảm tiền khi mua 2 ly cùng loại', async ({ page }) => {
    // 1. Thêm 1 món đồ uống vào giỏ (Cà phê sữa 30.000đ)
    const firstDrink = page.locator('#menuGridContainer > div').first();
    await firstDrink.click();
    await page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn")').first().click();

    // 2. Bấm nút '+' tăng số lượng lên 2 ly (Tổng tiền = 60.000đ)
    await page.locator('button:has-text("+")').first().click();
    await expect(page.locator('#cartCountBadge')).toHaveText('2');

    // 3. Chọn chiến lược giảm giá BOGO
    const discountSelect = page.locator('#discountSelect');
    await discountSelect.selectOption('BOGO');

    // 4. Kiểm tra giảm giá thực tế trên UI
    const cartDiscount = page.locator('#cartDiscount');
    const discountText = await cartDiscount.innerText();
    
    // Kỳ vọng nghiệp vụ: Mua 2 ly thì 1 ly được miễn phí (giảm khác "0 ₫")
    expect(discountText).not.toBe('0 ₫');
  });

  test('BB-BUG-02: [Negative State] Giảm bớt số lượng món làm phá vỡ điều kiện đơn tối thiểu của Voucher', async ({ page }) => {
    // 1. Thêm món và tăng lên 2 ly (Tạm tính = 60.000đ >= 50.000đ)
    const firstDrink = page.locator('#menuGridContainer > div').first();
    await firstDrink.click();
    await page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn")').first().click();
    await page.locator('button:has-text("+")').first().click();

    // 2. Nhập mã COFFEE20K (yêu cầu đơn từ 50.000đ) -> Áp dụng
    const voucherInput = page.locator('input[placeholder*="Mã voucher"]');
    await voucherInput.fill('COFFEE20K');
    await page.locator('button:has-text("Áp Dụng")').click();

    // 3. Giảm số lượng xuống 1 ly (Tạm tính chỉ còn 30.000đ < 50.000đ)
    await page.locator('button:has-text("-")').first().click();
    await expect(page.locator('#cartCountBadge')).toHaveText('1');

    // 4. Kiểm tra: Mã voucher có bị hủy khi đơn hàng không còn đủ 50.000đ?
    const discountText = await page.locator('#cartDiscount').innerText();
    
    // Kỳ vọng: Giảm giá phải bị hủy về 0 ₫ do đơn dưới 50.000đ
    expect(discountText).toBe('0 ₫');
  });

  test('BB-BUG-03: [Usability] Gõ mã voucher và nhấn phím Enter trên bàn phím không có tác dụng', async ({ page }) => {
    // 1. Thêm món vào giỏ
    const firstDrink = page.locator('#menuGridContainer > div').first();
    await firstDrink.click();
    await page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn")').first().click();

    // 2. Gõ mã và gõ phím Enter
    const voucherInput = page.locator('input[placeholder*="Mã voucher"]');
    await voucherInput.fill('CHAOBAN');
    await voucherInput.press('Enter');
    await page.waitForTimeout(500);

    // 3. Kiểm tra mã có được áp dụng không
    const discountText = await page.locator('#cartDiscount').innerText();
    expect(discountText).not.toBe('0 ₫');
  });

  test('BB-BUG-04: [Fuzzing Input] Tìm kiếm có khoảng trắng đầu/cuối (" bạc xỉu ") làm biến mất menu', async ({ page }) => {
    const searchInput = page.locator('#menuSearch');
    await searchInput.fill(' bạc xỉu ');
    await page.waitForTimeout(500);

    const drinkItem = page.locator('text=Bạc xỉu').first();
    const isVisible = await drinkItem.isVisible();
    expect(isVisible).toBe(true);
  });

  test('BB-BUG-05: [POS Usability] Thu ngân gõ nhanh không dấu ("bac xiu") không tìm thấy món', async ({ page }) => {
    const searchInput = page.locator('#menuSearch');
    await searchInput.fill('bac xiu');
    await page.waitForTimeout(500);

    const drinkItem = page.locator('text=Bạc xỉu').first();
    const isVisible = await drinkItem.isVisible();
    expect(isVisible).toBe(true);
  });

});
