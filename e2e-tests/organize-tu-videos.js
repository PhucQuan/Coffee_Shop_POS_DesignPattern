const fs = require('fs');
const path = require('path');

const testResultsDir = path.join(__dirname, 'test-results');
const outputVideosDir = path.join(__dirname, 'videos', 'tu-testcases');

if (!fs.existsSync(outputVideosDir)) {
  fs.mkdirSync(outputVideosDir, { recursive: true });
}

if (!fs.existsSync(testResultsDir)) {
  console.log('[WARN] Chưa có thư mục test-results. Vui lòng chạy test trước.');
  process.exit(0);
}

const entries = fs.readdirSync(testResultsDir, { withFileTypes: true });

const mapping = [
  { match: /PHAN_1_TC_DH/i, name: 'VIDEO_01_TC_DH_QuanLyDonHang.webm' },
  { match: /PHAN_2_TC_KH/i, name: 'VIDEO_02_TC_KH_QuanLyKhoTonKho.webm' },
  { match: /PHAN_3_TC_TD/i, name: 'VIDEO_03_TC_TD_QuanLyThucDon.webm' },
  { match: /PHAN_4_TC_BC/i, name: 'VIDEO_04_TC_BC_BaoCaoDoanhThu.webm' },
  { match: /PHAN_5_TC_TO/i, name: 'VIDEO_05_TC_TO_QuanLyToppingDecorator.webm' },
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

// Fallback: nếu chưa đủ 5 video theo regex, copy các file video mới nhất
if (copied < mapping.length) {
  const videoFolders = entries
    .filter(e => e.isDirectory() && fs.existsSync(path.join(testResultsDir, e.name, 'video.webm')))
    .map(e => ({
      path: path.join(testResultsDir, e.name, 'video.webm'),
      time: fs.statSync(path.join(testResultsDir, e.name, 'video.webm')).mtimeMs
    }))
    .sort((a, b) => b.time - a.time);

  videoFolders.slice(0, 5).forEach((vf, idx) => {
    const targetName = mapping[idx] ? mapping[idx].name : `VIDEO_0${idx + 1}_TuTestcase.webm`;
    if (!usedDestinations.has(targetName)) {
      const dest = path.join(outputVideosDir, targetName);
      fs.copyFileSync(vf.path, dest);
      usedDestinations.add(targetName);
      console.log(`[OK] Đã xuất video (fallback): ${targetName}`);
      copied++;
    }
  });
}

console.log(`\n======================================================`);
console.log(` [HOÀN TẤT] Đã xuất ${copied} video kiểm thử 40 Test Cases của Tú vào:`);
console.log(` ${outputVideosDir}`);
console.log(`======================================================\n`);
