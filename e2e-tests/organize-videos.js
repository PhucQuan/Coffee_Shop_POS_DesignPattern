const fs = require('fs');
const path = require('path');

const testResultsDir = path.join(__dirname, 'test-results');
const outputVideosDir = path.join(__dirname, 'videos');

if (!fs.existsSync(outputVideosDir)) {
  fs.mkdirSync(outputVideosDir, { recursive: true });
}

if (!fs.existsSync(testResultsDir)) {
  console.log('[WARN] Chưa có thư mục test-results. Vui lòng chạy test trước.');
  process.exit(0);
}

const entries = fs.readdirSync(testResultsDir, { withFileTypes: true });

const mapping = [
  { match: /(FULL_DEMO|TOAN_BO_QUY_TRINH)/i, name: 'DEMO_TOAN_BO_QUY_TRINH_COFFEE_POS.webm' },
  { match: /(TC01|Tim-kiem|Loc-Danh-muc)/i, name: 'TC01_GiaoDien_TimKiem_DanhMuc.webm' },
  { match: /(TC02|Decorator|Them-vao-Gio)/i, name: 'TC02_TuyBienMon_Decorator_ThemVaoGio.webm' },
  { match: /(TC03|Strategy|khuyen-mai|Chiet-khau)/i, name: 'TC03_KhuyenMai_StrategyPattern.webm' },
  { match: /(TC04|Adapter|Thanh-toan|Hoa-Don)/i, name: 'TC04_ThanhToan_AdapterPattern_HoaDon.webm' },
  { match: /(TC05|Kitchen|State|Quan-Tri|Admin)/i, name: 'TC05_KitchenKDS_StatePattern_Admin.webm' },
];

let copied = 0;
const usedDestinations = new Set();

for (const entry of entries) {
  if (entry.isDirectory()) {
    const dirName = entry.name;
    const videoFile = path.join(testResultsDir, dirName, 'video.webm');
    if (fs.existsSync(videoFile)) {
      for (const item of mapping) {
        if (item.match.test(dirName) && !usedDestinations.has(item.name)) {
          const dest = path.join(outputVideosDir, item.name);
          fs.copyFileSync(videoFile, dest);
          usedDestinations.add(item.name);
          console.log(`[OK] Đã xuất video: ${item.name}`);
          copied++;
          break;
        }
      }
    }
  }
}

// Fallback: Nếu còn video nào chưa được copy, copy theo thứ tự thời gian sửa đổi
if (copied < 5) {
  const videoFolders = entries
    .filter(e => e.isDirectory() && fs.existsSync(path.join(testResultsDir, e.name, 'video.webm')))
    .map(e => ({
      path: path.join(testResultsDir, e.name, 'video.webm'),
      time: fs.statSync(path.join(testResultsDir, e.name, 'video.webm')).mtimeMs
    }))
    .sort((a, b) => a.time - b.time);

  videoFolders.forEach((vf, idx) => {
    const fallbackName = mapping[idx] ? mapping[idx].name : `TC0${idx + 1}_Video.webm`;
    if (!usedDestinations.has(fallbackName)) {
      const dest = path.join(outputVideosDir, fallbackName);
      fs.copyFileSync(vf.path, dest);
      usedDestinations.add(fallbackName);
      console.log(`[OK] Đã xuất video (fallback mtime): ${fallbackName}`);
      copied++;
    }
  });
}

console.log(`\n======================================================`);
console.log(` Hoàn tất xuất ${copied} video kiểm thử vào thư mục:`);
console.log(` ${outputVideosDir}`);
console.log(`======================================================\n`);
