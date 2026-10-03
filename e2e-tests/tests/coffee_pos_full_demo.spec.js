// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * KỊCH BẢN DEMO TOÀN DIỆN TỪ ĐẦU ĐẾN CUỐI (END-TO-END FULL FLOW DEMO)
 * Thích hợp quay video trình chiếu báo cáo đồ án, chạy tự động trên Bandicam hoặc trích xuất video trực tiếp từ Playwright.
 * Quy trình:
 * 1. Khởi động Giao diện POS Thu Ngân
 * 2. Tìm kiếm món & Lọc các danh mục (Menu & Categories)
 * 3. Tùy biến món uống (Decorator Pattern: chọn Size L, thêm Topping, ghi chú làm món)
 * 4. Thêm vào giỏ & Áp dụng mã khuyến mãi (Strategy Pattern: Giảm 10%, VIP 15%)
 * 5. Thanh toán đa kênh (Adapter Pattern: Mã VietQR VNPay & Tiền mặt)
 * 6. Xuất Hóa Đơn Bán Lẻ (Receipt Preview)
 * 7. Điều hướng Màn hình Bếp (Kitchen KDS - State Pattern luân chuyển món sang Ready)
 * 8. Điều hướng Màn hình Quản trị (Admin Dashboard & Tồn kho)
 * 9. Quay lại Thu Ngân hoàn tất ca làm việc.
 */

test('DEMO_TOAN_BO_QUY_TRINH - Coffee Shop POS End to End Demo', async ({ page }) => {
  // Cho phép kịch bản chạy đủ thời gian (90s) để quay video chậm rãi, mượt mà
  test.setTimeout(90000);

  // 1. Mở trang web POS

  await page.goto('/');
  await page.waitForLoadState('domcontentloaded');
  await page.waitForTimeout(1000);

  // 2. Tìm kiếm món "Bạc xỉu"
  const searchInput = page.locator('#menuSearch');
  await searchInput.click();
  await searchInput.fill('Bạc xỉu');
  await page.waitForTimeout(1500);

  // Xóa tìm kiếm và thử các tab danh mục
  await searchInput.fill('');
  await page.waitForTimeout(600);

  const teaTab = page.locator('button[data-category="TEA"]').first();
  await teaTab.click();
  await page.waitForTimeout(1000);

  const matchaTab = page.locator('button[data-category="MATCHA"]').first();
  await matchaTab.click();
  await page.waitForTimeout(1000);

  const allTab = page.locator('button[data-category="ALL"]').first();
  await allTab.click();
  await page.waitForTimeout(1000);

  // 3. Tùy biến món bằng Decorator Pattern
  const drinkCard = page.locator('text=Cà phê sữa').first();
  await drinkCard.click();
  await page.waitForTimeout(1200);

  // Chọn Size L (+10.000₫)
  const sizeLRadio = page.locator('text=Size L (Lớn)').first();
  if (await sizeLRadio.isVisible()) {
    await sizeLRadio.click();
    await page.waitForTimeout(800);
  }

  // Thêm Topping: Trân châu trắng (+10.000₫)
  const toppingItem = page.locator('text=Trân châu trắng').first();
  if (await toppingItem.isVisible()) {
    await toppingItem.click();
    await page.waitForTimeout(800);
  }

  // Nhập ghi chú đặc biệt cho barista
  const noteInput = page.locator('#modalItemNote, input[placeholder*="Ví dụ: Ít đá"]').first();
  await noteInput.fill('Ít đá, 50% đường, nhiều sữa');
  await page.waitForTimeout(1000);

  // Bấm "+ Thêm Vào Đơn"
  const addBtn = page.locator('#btnModalAddToCart, button:has-text("Thêm Vào Đơn")').first();
  await addBtn.click();
  await page.waitForTimeout(1200);

  // 4. Áp dụng mã khuyến mãi (Strategy Pattern)
  const discountSelect = page.locator('#discountSelect');
  await discountSelect.selectOption('PERCENT_10');
  await page.waitForTimeout(1200);

  await discountSelect.selectOption('VIP');
  await page.waitForTimeout(1200);

  // 5. Thanh toán đơn hàng (Adapter Pattern)
  const btnCheckout = page.locator('#btnOpenCheckout');
  await btnCheckout.click();
  await page.waitForTimeout(1500);

  // Hiển thị VNPay QR Code VietQR động
  const vnpayBtn = page.locator('button[data-gateway="VNPAY"], button:has-text("VNPay QR")').first();
  if (await vnpayBtn.isVisible()) {
    await vnpayBtn.click();
    await page.waitForTimeout(2000); // Dừng lại để người xem quan sát mã VietQR
  }

  // Chọn Tiền mặt và Xác nhận thanh toán
  const cashBtn = page.locator('button[data-gateway="CASH"], button:has-text("Tiền mặt")').first();
  await cashBtn.click();
  await page.waitForTimeout(1000);

  const confirmPaymentBtn = page.locator('#btnConfirmPayment, button:has-text("Xác Nhận Đã Thu Tiền")').first();
  await confirmPaymentBtn.click();
  await page.waitForTimeout(2000);

  // 6. Xem Hóa đơn thanh toán bán lẻ
  const receiptContent = page.locator('#receiptModalContent');
  await expect(receiptContent).toBeVisible();
  await page.waitForTimeout(2500); // Dừng lại để video quay rõ hóa đơn

  const btnCloseReceipt = page.locator('#btnCloseReceipt, button:has-text("Đóng")').first();
  if (await btnCloseReceipt.isVisible()) {
    await btnCloseReceipt.click();
    await page.waitForTimeout(1000);
  }

  // 7. Chuyển sang Màn hình Bếp (Kitchen KDS - State Pattern)
  const kitchenNavBtn = page.locator('button[data-tab-target="kitchen"], button:has-text("Màn Hình Bếp")').first();
  await kitchenNavBtn.click();
  await page.waitForTimeout(2000);

  // Luân chuyển trạng thái món: Bắt đầu làm -> Hoàn thành
  const startPrepBtn = page.locator('button:has-text("Bắt đầu pha chế")').first();
  if (await startPrepBtn.isVisible()) {
    await startPrepBtn.click();
    await page.waitForTimeout(1500);
  }

  const readyBtn = page.locator('button:has-text("Hoàn thành món")').first();
  if (await readyBtn.isVisible()) {
    await readyBtn.click();
    await page.waitForTimeout(1500);
  }

  // 8. Chuyển sang Màn hình Quản Trị & Báo Cáo
  const adminNavBtn = page.locator('button[data-tab-target="admin"], button:has-text("Báo Cáo & Quản Trị")').first();
  await adminNavBtn.click();
  await page.waitForTimeout(2500); // Xem doanh thu, đơn hàng, tồn kho

  // 9. Quay về Thu Ngân
  const posNavBtn = page.locator('button[data-tab-target="pos"], button:has-text("Thu Ngân")').first();
  await posNavBtn.click();
  await page.waitForTimeout(1500);
});
