const { test, expect } = require("@playwright/test");

/*
 * Bộ testcase thanh toán độc lập với coffee_pos_blackbox.spec.js.
 * Chạy riêng: npx playwright test tests/payment_20_testcases.spec.js
 */

test.describe("Payment – 20 test cases", () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      // addInitScript chạy lại sau page.reload(). Chỉ reset dữ liệu ở lần tải đầu
      // của mỗi browser context để TC_PAY_16 có thể kiểm tra persistence thật sự.
      if (!sessionStorage.getItem('__payment_test_initialized')) {
        localStorage.clear();
        sessionStorage.setItem('__payment_test_initialized', 'true');
      }
      window.__printCalled = false;
      window.print = () => {
        window.__printCalled = true;
      };
    });
    await page.goto("/?autologin=true");
    await expect(page.locator("#menuGridContainer")).toBeVisible();
  });

  async function addDrink(page, name = "Cà phê sữa", note = "") {
    await page
      .locator("#menuGridContainer")
      .getByText(name, { exact: true })
      .click();
    await expect(page.locator("#btnModalAddToCart")).toBeVisible();
    if (note) await page.locator("#modalItemNote").fill(note);
    await page.locator("#btnModalAddToCart").click();
    await expect(page.locator("#cartCountBadge")).toHaveText(/1|2/);
  }

  async function openCheckout(page) {
    await page.locator("#btnOpenCheckout").click();
    await expect(
      page.getByRole("heading", { name: "Thanh Toán Đơn Hàng" }),
    ).toBeVisible();
  }

  async function pay(page, gateway = "CASH") {
    await openCheckout(page);
    await page.locator(`button[data-gateway="${gateway}"]`).click();
    await page.locator("#btnConfirmPayment").click();
    await expect(page.locator("#receiptModalContent")).toBeVisible();
  }

  test("TC_PAY_01 - Tính tạm tính đúng cho một món", async ({ page }) => {
    await addDrink(page);
    await expect(page.locator("#cartSubtotal")).toHaveText("30.000 ₫");
    await expect(page.locator("#cartTotal")).toHaveText("30.000 ₫");
  });

  test("TC_PAY_02 - Cập nhật tổng tiền khi tăng số lượng món", async ({
    page,
  }) => {
    await addDrink(page);
    await page.locator("button").filter({ hasText: "+" }).last().click();
    await expect(page.locator("#cartCountBadge")).toHaveText("2");
    await expect(page.locator("#cartTotal")).toHaveText("60.000 ₫");
  });

  test("TC_PAY_03 - Giảm giá thành viên 10 phần trăm", async ({ page }) => {
    await addDrink(page);
    await page.locator("#discountSelect").selectOption("PERCENT_10");
    await expect(page.locator("#cartDiscount")).toHaveText("-3.000 ₫");
    await expect(page.locator("#cartTotal")).toHaveText("27.000 ₫");
  });

  test("TC_PAY_04 - Giảm giá khách VIP 15 phần trăm", async ({ page }) => {
    await addDrink(page);
    await page.locator("#discountSelect").selectOption("VIP");
    await expect(page.locator("#cartDiscount")).toHaveText("-4.500 ₫");
    await expect(page.locator("#cartTotal")).toHaveText("25.500 ₫");
  });

  test("TC_PAY_05 - BOGO trừ đúng món có giá thấp nhất", async ({ page }) => {
    await addDrink(page, "Cà phê sữa", "món 1");
    await addDrink(page, "Bạc xỉu", "món 2");
    await page.locator("#discountSelect").selectOption("BOGO");
    await expect(page.locator("#cartSubtotal")).toHaveText("62.000 ₫");
    await expect(page.locator("#cartDiscount")).toHaveText("-30.000 ₫");
    await expect(page.locator("#cartTotal")).toHaveText("32.000 ₫");
  });

  test("TC_PAY_06 - Voucher COFFEE20K được áp dụng", async ({ page }) => {
    await addDrink(page);
    await page
      .getByPlaceholder("Mã voucher (vd: COFFEE20K, CHAOBAN)")
      .fill("coffee20k");
    await page.getByRole("button", { name: "Áp Dụng" }).click();
    await expect(page.locator("#cartDiscount")).toHaveText("-20.000 ₫");
    await expect(page.locator("#cartTotal")).toHaveText("10.000 ₫");
  });

  test("TC_PAY_07 - Checkout mặc định chọn tiền mặt và hiển thị đúng tổng", async ({
    page,
  }) => {
    await addDrink(page);
    await openCheckout(page);
    await expect(page.locator('button[data-gateway="CASH"]')).toHaveClass(
      /border-\[#7d4924\]/,
    );
    await expect(page.getByText("30.000 ₫").last()).toBeVisible();
    await expect(page.locator("#qrCodeContainer")).toHaveCount(0);
  });

  test("TC_PAY_08 - Chọn VNPay hiển thị QR", async ({ page }) => {
    await addDrink(page);
    await openCheckout(page);
    await page.locator('button[data-gateway="VNPAY"]').click();
    await expect(page.locator("#qrCodeContainer")).toBeVisible();
    await expect(page.locator("#qrCodeContainer")).toContainText("VNPay");
    await expect(page.locator("#qrCodeContainer")).toContainText("30.000 ₫");
  });

  test("TC_PAY_09 - Chọn MoMo hiển thị QR", async ({ page }) => {
    await addDrink(page);
    await openCheckout(page);
    await page.locator('button[data-gateway="MOMO"]').click();
    await expect(page.locator("#qrCodeContainer")).toBeVisible();
    await expect(page.locator("#qrCodeContainer")).toContainText("MoMo");
  });

  test("TC_PAY_10 - Chuyển lại tiền mặt ẩn QR", async ({ page }) => {
    await addDrink(page);
    await openCheckout(page);
    await page.locator('button[data-gateway="VNPAY"]').click();
    await expect(page.locator("#qrCodeContainer")).toBeVisible();
    await page.locator('button[data-gateway="CASH"]').click();
    await expect(page.locator("#qrCodeContainer")).toHaveCount(0);
  });

  test("TC_PAY_11 - Thanh toán tiền mặt thành công mở hóa đơn", async ({
    page,
  }) => {
    await addDrink(page);
    await pay(page, "CASH");
    await expect(
      page.getByText(/Thanh toán đơn #\d+ thành công/),
    ).toBeVisible();
    await expect(page.locator("#receiptModalContent")).toContainText("CASH");
  });

  test("TC_PAY_12 - Thanh toán VNPay lưu phương thức trên hóa đơn", async ({
    page,
  }) => {
    await addDrink(page);
    await pay(page, "VNPAY");
    await expect(page.locator("#receiptModalContent")).toContainText("VNPAY");
  });

  test("TC_PAY_13 - Thanh toán MoMo lưu phương thức trên hóa đơn", async ({
    page,
  }) => {
    await addDrink(page);
    await pay(page, "MOMO");
    await expect(page.locator("#receiptModalContent")).toContainText("MOMO");
  });

  test("TC_PAY_14 - Thanh toán xóa giỏ hàng và khóa checkout", async ({
    page,
  }) => {
    await addDrink(page);
    await pay(page);
    await page.locator("#btnCloseReceipt").click();
    await expect(page.getByText("Chưa có món nào")).toBeVisible();
    await expect(page.locator("#btnOpenCheckout")).toBeDisabled();
  });

  test("TC_PAY_15 - Tìm kiếm thực đơn hiển thị đúng món", async ({ page }) => {
    await page.locator("#menuSearch").fill("Matcha Latte");
    await expect(
      page.locator("#menuGridContainer").getByText("Matcha Latte", { exact: true }),
    ).toBeVisible();
    await expect(
      page.locator("#menuGridContainer").getByText("Cà phê sữa", { exact: true }),
    ).toBeHidden();
  });

  test("TC_PAY_16 - Tùy biến size và topping cập nhật giá món", async ({ page }) => {
    await page
      .locator("#menuGridContainer")
      .getByText("Cà phê sữa", { exact: true })
      .click();

    const sizeL = page.getByRole("radio", { name: /Size L/ });
    const whitePearl = page.getByRole("checkbox", { name: /Trân châu trắng/ });
    await sizeL.check();
    await expect(sizeL).toBeChecked();
    await whitePearl.check();
    await expect(whitePearl).toBeChecked();
    await expect(page.getByText("50.000 ₫", { exact: true })).toBeVisible();
    await page.locator("#btnModalAddToCart").click();

    await expect(page.locator("#cartCountBadge")).toHaveText("1");
    await expect(page.locator("#cartTotal")).toHaveText("50.000 ₫");
    await expect(page.locator("aside").getByText("Size L", { exact: true })).toBeVisible();
    await expect(page.locator("aside").getByText(/Trân châu trắng/)).toBeVisible();
  });

  test("TC_PAY_17 - Hóa đơn hiển thị đủ chi tiết món và số tiền", async ({
    page,
  }) => {
    await addDrink(page);
    await pay(page);
    const receipt = page.locator("#receiptModalContent");
    await expect(receipt).toContainText("Cà phê sữa");
    await expect(receipt).toContainText("Tạm tính:");
    await expect(receipt).toContainText("Giảm giá:");
    await expect(receipt).toContainText("TỔNG CỘNG:");
    await expect(receipt).toContainText("30.000 ₫");
  });

  test("TC_PAY_18 - Hóa đơn phản ánh tổng tiền sau giảm giá", async ({
    page,
  }) => {
    await addDrink(page);
    await page.locator("#discountSelect").selectOption("PERCENT_10");
    await pay(page);
    const receipt = page.locator("#receiptModalContent");
    await expect(receipt).toContainText("-3.000 ₫");
    await expect(receipt).toContainText("27.000 ₫");
  });

  test("TC_PAY_19 - Có thể tải file hóa đơn điện tử", async ({ page }) => {
    await addDrink(page);
    await pay(page);
    const downloadPromise = page.waitForEvent("download");
    await page.getByTitle("Lưu file hóa đơn điện tử").click();
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toMatch(/^receipt_order_\d+\.txt$/);
  });

  test("TC_PAY_20 - Nút in hóa đơn gọi lệnh in", async ({ page }) => {
    await addDrink(page);
    await pay(page);
    await page.getByRole("button", { name: "In Hóa Đơn" }).click();
    await expect
      .poll(() => page.evaluate(() => window.__printCalled))
      .toBe(true);
  });
});
