import fs from 'node:fs/promises';
import { SpreadsheetFile, Workbook } from '@oai/artifact-tool';

const outputDir = 'docs';
const outputPath = `${outputDir}/TestCases_ThanhToan_Playwright.xlsx`;
const font = 'Arial';

const cases = [
  ['TC_PAY_01', 'Kiểm tra tạm tính cho một món', 'Kiểm tra tổng tiền', 'Đăng nhập thu ngân, giỏ hàng trống', 'Cà phê sữa, 30.000 ₫', ['Chọn Cà phê sữa.', 'Thêm món vào đơn.', 'Kiểm tra Tạm tính và Tổng thanh toán.'], ['Món được thêm vào giỏ.', 'Tạm tính là 30.000 ₫.', 'Tổng thanh toán là 30.000 ₫.']],
  ['TC_PAY_02', 'Cập nhật tổng tiền khi tăng số lượng', 'Kiểm tra tổng tiền', 'Giỏ có 1 Cà phê sữa', 'Tăng số lượng từ 1 lên 2', ['Nhấn nút + của món.', 'Kiểm tra số lượng.', 'Kiểm tra Tổng thanh toán.'], ['Số lượng là 2.', 'Tổng thanh toán là 60.000 ₫.', 'Không có dòng món trùng.']],
  ['TC_PAY_03', 'Áp dụng giảm giá thành viên 10%', 'Kiểm tra tổng tiền', 'Giỏ có Cà phê sữa 30.000 ₫', 'Khuyến mãi PERCENT_10', ['Chọn Giảm giá 10%.', 'Kiểm tra giảm giá.', 'Kiểm tra tổng thanh toán.'], ['Giảm giá là 3.000 ₫.', 'Tổng thanh toán là 27.000 ₫.', 'Giá trị cập nhật ngay.']],
  ['TC_PAY_04', 'Áp dụng giảm giá khách VIP 15%', 'Kiểm tra tổng tiền', 'Giỏ có Cà phê sữa 30.000 ₫', 'Khuyến mãi VIP', ['Chọn Khách VIP.', 'Kiểm tra giảm giá.', 'Kiểm tra tổng thanh toán.'], ['Giảm giá là 4.500 ₫.', 'Tổng thanh toán là 25.500 ₫.', 'Giá trị cập nhật ngay.']],
  ['TC_PAY_05', 'BOGO trừ món rẻ nhất', 'Kiểm tra tổng tiền', 'Giỏ hàng trống', 'Cà phê sữa 30.000 ₫; Bạc xỉu 32.000 ₫; BOGO', ['Thêm hai món khác nhau.', 'Chọn BOGO.', 'Kiểm tra giảm giá và tổng tiền.'], ['Tạm tính là 62.000 ₫.', 'Giảm giá là 30.000 ₫.', 'Tổng thanh toán là 32.000 ₫.']],
  ['TC_PAY_06', 'Áp dụng voucher COFFEE20K', 'Kiểm tra tổng tiền', 'Giỏ có Cà phê sữa 30.000 ₫', 'Voucher COFFEE20K', ['Nhập COFFEE20K.', 'Nhấn Áp Dụng.', 'Kiểm tra giảm giá và tổng.'], ['Thông báo áp dụng voucher.', 'Giảm giá là 20.000 ₫.', 'Tổng thanh toán là 10.000 ₫.']],
  ['TC_PAY_07', 'Checkout mặc định là tiền mặt', 'Chọn phương thức thanh toán', 'Giỏ có món, tổng > 0', 'Cà phê sữa 30.000 ₫', ['Nhấn Thanh Toán Đơn Hàng.', 'Quan sát phương thức đang chọn.', 'Kiểm tra QR.'], ['Modal checkout hiển thị.', 'Tiền mặt được chọn mặc định.', 'QR chưa hiển thị.']],
  ['TC_PAY_08', 'Chọn VNPay hiển thị QR', 'Chọn phương thức thanh toán', 'Modal checkout đang mở', 'Phương thức VNPAY', ['Chọn VNPay QR.', 'Kiểm tra vùng QR.', 'Kiểm tra số tiền trên QR.'], ['QR VNPay hiển thị.', 'Nội dung có VNPay.', 'QR hiển thị 30.000 ₫.']],
  ['TC_PAY_09', 'Chọn MoMo hiển thị QR', 'Chọn phương thức thanh toán', 'Modal checkout đang mở', 'Phương thức MOMO', ['Chọn Ví MoMo.', 'Kiểm tra vùng QR.', 'Kiểm tra tên phương thức.'], ['QR MoMo hiển thị.', 'Nội dung có MoMo.', 'Modal vẫn hiển thị đúng tổng tiền.']],
  ['TC_PAY_10', 'Chuyển về tiền mặt ẩn QR', 'Chọn phương thức thanh toán', 'VNPay QR đang được chọn', 'Chuyển VNPAY sang CASH', ['Chọn VNPay QR.', 'Chọn Tiền mặt.', 'Kiểm tra vùng QR.'], ['QR xuất hiện khi chọn VNPay.', 'Tiền mặt được chọn.', 'QR được ẩn.']],
  ['TC_PAY_11', 'Thanh toán tiền mặt thành công', 'Xử lý giao dịch', 'Giỏ có Cà phê sữa', 'Phương thức CASH', ['Mở checkout.', 'Chọn Tiền mặt.', 'Nhấn Xác Nhận Đã Thu Tiền.'], ['Thông báo thanh toán thành công.', 'Hóa đơn mở.', 'Hóa đơn ghi CASH.']],
  ['TC_PAY_12', 'Thanh toán bằng VNPay', 'Xử lý giao dịch', 'Giỏ có Cà phê sữa', 'Phương thức VNPAY', ['Mở checkout.', 'Chọn VNPay QR.', 'Xác nhận thanh toán.'], ['Hóa đơn mở.', 'Giao dịch thành công.', 'Hóa đơn ghi VNPAY.']],
  ['TC_PAY_13', 'Thanh toán bằng MoMo', 'Xử lý giao dịch', 'Giỏ có Cà phê sữa', 'Phương thức MOMO', ['Mở checkout.', 'Chọn Ví MoMo.', 'Xác nhận thanh toán.'], ['Hóa đơn mở.', 'Giao dịch thành công.', 'Hóa đơn ghi MOMO.']],
  ['TC_PAY_14', 'Xóa giỏ sau thanh toán', 'Xử lý giao dịch', 'Giỏ có Cà phê sữa', 'Phương thức CASH', ['Thanh toán thành công.', 'Đóng hóa đơn.', 'Kiểm tra giỏ và nút checkout.'], ['Giỏ hiển thị Chưa có món nào.', 'Nút Thanh Toán bị khóa.', 'Sẵn sàng tạo đơn mới.']],
  ['TC_PAY_15', 'Lưu đơn thanh toán vào localStorage', 'Lưu thanh toán', 'Giỏ có Cà phê sữa', 'Phương thức VNPAY', ['Thanh toán VNPay.', 'Đọc react_pos_orders.', 'Kiểm tra đơn mới nhất.'], ['Có đơn mới.', 'paymentMethod là VNPAY.', 'total là 30.000 và status là PREPARING.']],
  ['TC_PAY_16', 'Duy trì đơn sau tải lại trang', 'Lưu thanh toán', 'Đã thanh toán tiền mặt', 'Phương thức CASH', ['Thanh toán tiền mặt.', 'Tải lại trang.', 'Đọc react_pos_orders.'], ['Dữ liệu đơn vẫn tồn tại.', 'Phương thức là CASH.', 'Tổng tiền là 30.000.']],
  ['TC_PAY_17', 'Hóa đơn có đầy đủ thông tin', 'Hiển thị hóa đơn', 'Đã thanh toán một món', 'Cà phê sữa 30.000 ₫', ['Mở hóa đơn sau thanh toán.', 'Kiểm tra tên món.', 'Kiểm tra các dòng tiền.'], ['Có tên món.', 'Có Tạm tính và Giảm giá.', 'Có TỔNG CỘNG 30.000 ₫.']],
  ['TC_PAY_18', 'Hóa đơn phản ánh giảm giá', 'Hiển thị hóa đơn', 'Giỏ có Cà phê sữa', 'PERCENT_10', ['Chọn giảm giá 10%.', 'Thanh toán.', 'Kiểm tra hóa đơn.'], ['Giảm giá là -3.000 ₫.', 'Tổng cộng là 27.000 ₫.', 'Số liệu khớp giỏ hàng.']],
  ['TC_PAY_19', 'Tải hóa đơn điện tử TXT', 'In hoặc lưu hóa đơn', 'Hóa đơn đang mở', 'Hóa đơn đơn hàng bất kỳ', ['Nhấn nút Lưu.', 'Chờ sự kiện tải file.', 'Kiểm tra tên file.'], ['Trình duyệt tải file.', 'Tên file có receipt_order_.', 'Phần mở rộng là .txt.']],
  ['TC_PAY_20', 'Gọi lệnh in hóa đơn', 'In hoặc lưu hóa đơn', 'Hóa đơn đang mở', 'Hóa đơn đơn hàng bất kỳ', ['Nhấn In Hóa Đơn.', 'Theo dõi window.print.', 'Kiểm tra lệnh in.'], ['Nút In Hóa Đơn hoạt động.', 'window.print được gọi.', 'Không đóng hóa đơn ngoài ý muốn.']],
];

function writeCase(sheet, tc) {
  const [id, description, useCase, prerequisite, data, steps, expected] = tc;
  sheet.showGridLines = false;
  const border = { preset: 'all', style: 'thin', color: '#808080' };
  const header = { fill: '#FFF2CC', font: { name: font, bold: true }, borders: border, verticalAlignment: 'center' };
  const bodyRows = Array.from({ length: 4 }, (_, index) => [
    index < steps.length ? index + 1 : index + 1,
    index < steps.length ? steps[index] : '',
    index < expected.length ? expected[index] : '',
    '',
    'Not executed',
  ]);

  // Header metadata — matches the user-provided "Chuẩn" sheet layout A:J.
  sheet.getRange('A1:J6').format.font = { name: font, size: 12 };
  sheet.mergeCells('A1:B1'); sheet.mergeCells('D1:E1'); sheet.mergeCells('F1:G1');
  sheet.mergeCells('A2:B2'); sheet.mergeCells('D2:E2'); sheet.mergeCells('F2:G2'); sheet.mergeCells('H2:I2');
  sheet.mergeCells('A4:B4'); sheet.mergeCells('C4:J4');
  sheet.mergeCells('A6:B6'); sheet.mergeCells('D6:E6'); sheet.mergeCells('F6:G6'); sheet.mergeCells('H6:I6');
  sheet.getRange('A1:J6').values = [
    ['Test Case ID', '', id, 'Test Case Description', '', description, '', '', '', ''],
    ['Created By', '', 'Huy', 'Reviewed By', '', '', '', 'Version', '', '1.0'],
    ['', '', '', '', '', '', '', '', '', ''],
    ["QA Tester's Log", '', 'Review comments from Bill incorporate in version 2.1', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', '', '', ''],
    ["Tester's Name", '', 'Huy', 'Date Tested', '', '12/10/2026', '', 'Test Case (Pass/Fail/Not Executed)', '', 'Not executed'],
  ];
  sheet.getRange('A1:J2').format.borders = border;
  sheet.getRange('A4:J4').format.borders = border;
  sheet.getRange('A6:J6').format.borders = border;
  sheet.getRange('A1:B2').format.fill = '#FFF2CC';
  sheet.getRange('D1:E2').format.fill = '#FFF2CC';
  sheet.getRange('H2:I2').format.fill = '#FFF2CC';
  sheet.getRange('A4:B4').format.fill = '#FFF2CC';
  sheet.getRange('A6:B6').format.fill = '#FFF2CC';
  sheet.getRange('D6:E6').format.fill = '#FFF2CC';
  sheet.getRange('H6:I6').format.fill = '#FFF2CC';
  sheet.getRange('A1:B2').format.font = { name: font, bold: true };
  sheet.getRange('D1:E2').format.font = { name: font, bold: true };
  sheet.getRange('H2:I2').format.font = { name: font, bold: true };
  sheet.getRange('A4:B4').format.font = { name: font, bold: true, underline: 'single' };
  sheet.getRange('A6:B6').format.font = { name: font, bold: true };
  sheet.getRange('D6:E6').format.font = { name: font, bold: true };
  sheet.getRange('H6:I6').format.font = { name: font, bold: true };
  sheet.getRange('J6').format.fill = '#FFF200';
  sheet.getRange('J6').dataValidation = { rule: { type: 'list', values: ['Pass', 'Fail', 'Not executed', 'Suspended', 'Crashed'] } };

  // Prerequisites and test data tables.
  sheet.mergeCells('B8:C8'); sheet.mergeCells('G8:J8');
  sheet.getRange('A8:J8').values = [['S #', 'Prerequisites:', '', '', '', 'S #', 'Test Data', '', '', '']];
  sheet.getRange('A8:C8').format = header;
  sheet.getRange('F8:J8').format = header;
  sheet.getRange('A9:C12').format.borders = border;
  sheet.getRange('F9:J12').format.borders = border;
  sheet.mergeCells('B9:C9'); sheet.mergeCells('G9:J9');
  sheet.getRange('A9:J12').values = [
    [1, prerequisite, '', '', '', 1, data, '', '', ''],
    [2, '', '', '', '', 2, '', '', '', ''],
    [3, '', '', '', '', 3, '', '', '', ''],
    [4, '', '', '', '', 4, '', '', '', ''],
  ];
  sheet.getRange('B9:C9').format.wrapText = true;
  sheet.getRange('G9:J9').format.wrapText = true;

  // Scenario and test steps.
  sheet.mergeCells('A14:A15'); sheet.mergeCells('B14:J15');
  sheet.getRange('A14:J15').values = [['Test\nScenario', description, '', '', '', '', '', '', '', ''], ['', '', '', '', '', '', '', '', '', '']];
  sheet.getRange('A14:J15').format.borders = border;
  sheet.getRange('A14').format = { font: { name: font, bold: true, underline: 'single' }, wrapText: true, verticalAlignment: 'center' };
  sheet.getRange('B14').format.wrapText = true;
  sheet.mergeCells('B16:C16'); sheet.mergeCells('D16:E16'); sheet.mergeCells('F16:H16'); sheet.mergeCells('I16:J16');
  sheet.getRange('A16:J16').values = [['Step #', 'Step Details', '', 'Expected Results', '', 'Actual Results', '', '', 'Pass / Fail / Not executed / Suspended', '']];
  sheet.getRange('A16:J16').format = { ...header, horizontalAlignment: 'center', wrapText: true };
  for (let row = 18; row <= 21; row += 1) {
    sheet.mergeCells(`B${row}:C${row}`); sheet.mergeCells(`D${row}:E${row}`); sheet.mergeCells(`F${row}:H${row}`); sheet.mergeCells(`I${row}:J${row}`);
  }
  sheet.getRange('A18:J21').values = bodyRows.flatMap(row => [[row[0], row[1], '', row[2], '', row[3], '', '', row[4], '']]);
  sheet.getRange('A18:J21').format.borders = border;
  sheet.getRange('A18:J21').format.wrapText = true;
  sheet.getRange('I18:I21').dataValidation = { rule: { type: 'list', values: ['Pass', 'Fail', 'Not executed', 'Suspended', 'Crashed'] } };

  sheet.getRange('A:A').format.columnWidth = 9;
  sheet.getRange('B:B').format.columnWidth = 24;
  sheet.getRange('C:C').format.columnWidth = 7;
  sheet.getRange('D:D').format.columnWidth = 22;
  sheet.getRange('E:E').format.columnWidth = 7;
  sheet.getRange('F:F').format.columnWidth = 25;
  sheet.getRange('G:G').format.columnWidth = 12;
  sheet.getRange('H:H').format.columnWidth = 16;
  sheet.getRange('I:I').format.columnWidth = 24;
  sheet.getRange('J:J').format.columnWidth = 13;
  sheet.getRange('4:4').format.rowHeight = 22;
  sheet.getRange('9:12').format.rowHeight = 23;
  sheet.getRange('14:15').format.rowHeight = 26;
  sheet.getRange('16:16').format.rowHeight = 36;
  sheet.getRange('18:21').format.rowHeight = 26;
}

const workbook = Workbook.create();
const standardSheet = workbook.worksheets.add('Chuẩn');
writeCase(standardSheet, cases[0]);
standardSheet.getRange('F18:H20').values = [['như mong đợi', '', ''], ['như mong đợi', '', ''], ['như mong đợi', '', '']];
standardSheet.getRange('I18:I20').values = [['Pass'], ['Pass'], ['Pass']];
standardSheet.getRange('J6').values = [['Pass']];
for (const tc of cases) {
  const sheet = workbook.worksheets.add(tc[0]);
  writeCase(sheet, tc);
}
workbook.recalculate();
await fs.mkdir(outputDir, { recursive: true });
const preview = await workbook.render({ sheetName: 'TC_PAY_01', range: 'A1:J22', scale: 1.2, format: 'png' });
await fs.writeFile('docs/TestCases_ThanhToan_Playwright_preview.png', new Uint8Array(await preview.arrayBuffer()));
const exported = await SpreadsheetFile.exportXlsx(workbook);
await exported.save(outputPath);
console.log(`Created ${outputPath} with ${cases.length} worksheets.`);
