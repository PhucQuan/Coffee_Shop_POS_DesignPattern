# Web defect-demo guide

The React web application is correct by default. The optional `demoDefects`
query parameter enables an isolated, deliberate defect only for the current
browser session. Do not use it for a normal presentation or release.

Start the web app with `run-web.bat` (or `run-web.ps1`), then append one scenario to the URL:

```text
http://localhost:8088/?autologin=true&demoDefects=TC01
```

Replace `TC01` with `TC03`, `TC05`, or `TC15`. Use one scenario at a time when
capturing evidence, so the screenshot represents one bug only.

| Scenario | Test case | Reproduction | Expected | Actual in demo mode |
| --- | --- | --- | --- | --- |
| `TC01` | 1. Total for one item | Add exactly one drink. | Total equals the item price. | Total is 1,000 VND too high. |
| `TC03` | 3. Discount 10% | Add drink(s), choose **Giảm 10%**. | 10% of subtotal is deducted. | Only 5% is deducted. |
| `TC05` | 5. Default payment method | Open checkout. | **Tiền mặt** is selected by default. | **VNPay QR** is selected by default. |
| `TC15` | 15. Receipt content | Pay successfully, then inspect/download receipt. | Receipt includes subtotal, discount, and total. | The discount line is missing. |

For TC05, capture the payment modal immediately after it opens. For TC15, capture the receipt modal after payment. Removing
`demoDefects` from the URL returns the app to normal behavior.
