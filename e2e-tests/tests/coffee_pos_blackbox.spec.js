// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * BỘ KIỂM THỬ HỘP ĐEN TỰ ĐỘNG VÀ QUAY VIDEO (BLACK-BOX E2E TESTING)
 * Dự án: Coffee Shop POS (Hệ thống Quản lý Bán hàng & Pha chế Quán Cà phê)
 * Áp dụng Design Patterns: Factory Method, Decorator, Strategy, Adapter, State, Observer
 */

test.describe('PURRCOFFEE POS - BỘ KIỂM THỬ HỘP ĐEN & QUAY VIDEO DEMO', () => {

  test.beforeEach(async ({ page }) => {
    // Truy cập ứng dụng POS
    await page.goto('/');
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(600); // Tạm dừng để video quay mở đầu mượt mà
  });

  test('TC01 - Kiem tra Giao dien Thu Ngan, Tim kiem & Loc Danh muc', async ({ page }) => {
    // 1. Kiểm tra tiêu đề trang
    await expect(page).toHaveTitle(/PurrCoffee POS/);

    // 2. Tìm kiếm món "Bạc xỉu"
    const searchInput = page.locator('#menuSearch');
    await expect(searchInput).toBeVisible();
    await searchInput.click();
    await searchInput.fill('Bạc xỉu');
    await page.waitForTimeout(1000); // Để video ghi lại kết quả tìm kiếm

    // Món "Bạc xỉu" phải xuất hiện trên menu
    const drinkItem = page.locator('text=Bạc xỉu').first();
    await expect(drinkItem).toBeVisible();

    // 3. Xóa tìm kiếm và chuyển qua tab danh mục "Trà trái cây"
    await searchInput.fill('');
    await page.waitForTimeout(500);

    const teaTab = page.locator('button[data-category="TEA"]').first();
    await teaTab.click();
    await page.waitForTimeout(1000);

    // Kiểm tra các món thuộc danh mục Trà xuất hiện
    await expect(page.locator('text=Trà đào cam sả').first()).toBeVisible();

    // 4. Chuyển qua tab danh mục "Matcha"
    const matchaTab = page.locator('button[data-category="MATCHA"]').first();
    await matchaTab.click();
    await page.waitForTimeout(1000);
    await expect(page.locator('text=Matcha Latte').first()).toBeVisible();

    // 5. Quay lại xem "Tất cả món"
    const allTab = page.locator('button[data-category="ALL"]').first();
    await allTab.click();
    await page.waitForTimeout(1000);
  });

  test('TC02 - Tuy bien mon uong (Decorator Pattern) & Them vao Gio hang', async ({ page }) => {
    // 1. Chọn món "Cà phê sữa" từ menu để mở Modal Decorator Customizer
    const drinkCard = page.locator('text=Cà phê sữa').first();
    await expect(drinkCard).toBeVisible();
    await drinkCard.click();
    await page.waitForTimeout(1000);

    // 2. Kiểm tra Modal tùy biến món mở ra
    const customizeModal = page.locator('#modalItemNote, input[placeholder*="Ví dụ: Ít đá"]').first();
    await expect(customizeModal).toBeVisible();

    // 3. Tùy chọn Size L (+10.000₫)
    const sizeLRadio = page.locator('text=Size L (Lớn)').first();
    if (await sizeLRadio.isVisible()) {
      await sizeLRadio.click();
      await page.waitForTimeout(600);
    }

    // 4. Chọn Topping: Trân châu trắng
    const toppingItem = page.locator('text=Trân châu trắng').first();
    if (await toppingItem.isVisible()) {
      await toppingItem.click();
      await page.waitForTimeout(600);
    }

    // 5. Nhập ghi chú đặc biệt cho barista
    await customizeModal.fill('70% đường, nhiều đá');
    await page.waitForTimeout(800);

    // 6. Nhấn nút "+ Thêm Vào Đơn"
    const addBtn = page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn"), button:has-text("Thêm vào đơn")').first();
    await addBtn.click();
    await page.waitForTimeout(1000);

    // 7. Xác thực giỏ hàng đã có món vừa thêm
    const cartBadge = page.locator('#cartCountBadge');
    await expect(cartBadge).not.toHaveText('0');

    // Nút Thanh Toán phải được kích hoạt (enabled)
    const btnCheckout = page.locator('#btnOpenCheckout');
    await expect(btnCheckout).toBeEnabled();
    await page.waitForTimeout(1000);
  });

  test('TC03 - Ap dung ma khuyen mai & Chiet khau (Strategy Pattern)', async ({ page }) => {
    // 1. Thêm món đầu tiên vào giỏ
    const firstDrink = page.locator('#menuGridContainer > div').first();
    await firstDrink.click();
    await page.waitForTimeout(600);

    const addBtn = page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn"), button:has-text("Thêm vào đơn")').first();
    if (await addBtn.isVisible()) {
      await addBtn.click();
      await page.waitForTimeout(800);
    }

    // 2. Kiểm tra phần chọn Strategy khuyến mãi
    const discountSelect = page.locator('#discountSelect');
    await expect(discountSelect).toBeVisible();

    // 3. Chiến lược 1: Giảm giá 10% (PERCENT_10)
    await discountSelect.selectOption('PERCENT_10');
    await page.waitForTimeout(1000);

    const discountText = page.locator('#cartDiscount');
    await expect(discountText).not.toHaveText('0 ₫');

    // 4. Chiến lược 2: Khách VIP (Giảm 15%)
    await discountSelect.selectOption('VIP');
    await page.waitForTimeout(1000);
    await expect(discountText).not.toHaveText('0 ₫');

    // 5. Quay về không áp dụng mã
    await discountSelect.selectOption('NONE');
    await page.waitForTimeout(800);
    await expect(discountText).toHaveText('0 ₫');
  });

  test('TC04 - Quy trinh Thanh toan da kenh (Adapter Pattern) & Xuat Hoa Don', async ({ page }) => {
    // 1. Chọn món đưa vào giỏ hàng
    const drinkCard = page.locator('#menuGridContainer > div').first();
    await drinkCard.click();
    await page.waitForTimeout(600);

    const addBtn = page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn"), button:has-text("Thêm vào đơn")').first();
    if (await addBtn.isVisible()) {
      await addBtn.click();
      await page.waitForTimeout(800);
    }

    // 2. Mở Modal Thanh toán
    const btnCheckout = page.locator('#btnOpenCheckout');
    await expect(btnCheckout).toBeEnabled();
    await btnCheckout.click();
    await page.waitForTimeout(1000);

    // 3. Kiểm tra các cổng thanh toán (Adapter Pattern)
    // Thử chọn VNPay QR để hiển thị mã VietQR động
    const vnpayBtn = page.locator('button[data-gateway="VNPAY"], button:has-text("VNPay QR")').first();
    if (await vnpayBtn.isVisible()) {
      await vnpayBtn.click();
      await page.waitForTimeout(1200); // Xem QR Code
    }

    // Chuyển sang thanh toán Tiền mặt (Cash)
    const cashBtn = page.locator('button[data-gateway="CASH"], button:has-text("Tiền mặt")').first();
    await cashBtn.click();
    await page.waitForTimeout(800);

    // 4. Bấm "Xác Nhận Đã Thu Tiền"
    const confirmPaymentBtn = page.locator('#btnConfirmPayment, button:has-text("Xác Nhận Đã Thu Tiền")').first();
    await confirmPaymentBtn.click();
    await page.waitForTimeout(1200);

    // 5. Xác thực Hóa đơn thanh toán (Receipt) hiển thị
    const receiptContent = page.locator('#receiptModalContent');
    await expect(receiptContent).toBeVisible();
    await page.waitForTimeout(1500); // Tạm dừng để video quay rõ hóa đơn

    // 6. Đóng Hóa đơn
    const btnCloseReceipt = page.locator('#btnCloseReceipt, button:has-text("Đóng")').first();
    if (await btnCloseReceipt.isVisible()) {
      await btnCloseReceipt.click();
      await page.waitForTimeout(800);
    }
  });

  test('TC05 - Man hinh Bep (Kitchen KDS State Pattern) & Quan Tri (Admin)', async ({ page }) => {

    // 1. Chuyển sang Màn hình Pha Chế (Kitchen KDS)
    const kitchenNavBtn = page.locator('button[data-tab-target="kitchen"], button:has-text("Màn Hình Bếp")').first();
    await expect(kitchenNavBtn).toBeVisible();
    await kitchenNavBtn.click();
    await page.waitForTimeout(1200);

    // 2. Xác thực 3 cột trạng thái theo State Pattern
    await expect(page.locator('text=Chờ Pha Chế').first()).toBeVisible();
    await expect(page.locator('text=Đang Pha Chế').first()).toBeVisible();
    await expect(page.locator('text=Giao Khách').first()).toBeVisible();

    // Thử thao tác chuyển trạng thái món nếu có đơn hàng
    const startPrepBtn = page.locator('button:has-text("Bắt đầu pha chế"), button:has-text("Pha chế")').first();
    if (await startPrepBtn.isVisible()) {
      await startPrepBtn.click();
      await page.waitForTimeout(1000);
    }

    const readyBtn = page.locator('button:has-text("Hoàn thành"), button:has-text("Đã xong")').first();
    if (await readyBtn.isVisible()) {
      await readyBtn.click();
      await page.waitForTimeout(1000);
    }

    // 3. Chuyển sang Màn hình Báo Cáo & Quản Trị
    const adminNavBtn = page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first();
    await adminNavBtn.click();
    await page.waitForTimeout(1500);

    // Xác thực các chỉ số KPI & Tồn kho
    await expect(page.locator('text=Tổng Doanh Thu').first()).toBeVisible();
    await expect(page.locator('text=Tồn Kho Nguyên Liệu').first()).toBeVisible();
    await page.waitForTimeout(1000);

    // 4. Quay trở về Màn hình Thu Ngân POS
    const posNavBtn = page.locator('button[data-tab-target="pos"], button:has-text("Thu Ngân")').first();
    await posNavBtn.click();
    await page.waitForTimeout(1000);
  });

});
