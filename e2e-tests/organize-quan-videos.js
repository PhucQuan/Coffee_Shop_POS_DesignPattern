const fs = require('fs');
const path = require('path');

const testResultsDir = path.join(__dirname, 'test-results');
const outputVideosDir = path.join(__dirname, 'videos', 'quan-testcases');

if (!fs.existsSync(outputVideosDir)) {
  fs.mkdirSync(outputVideosDir, { recursive: true });
}

if (!fs.existsSync(testResultsDir)) {
  console.log('[WARN] Chưa có thư mục test-results. Vui lòng chạy test trước.');
  process.exit(0);
}

const entries = fs.readdirSync(testResultsDir, { withFileTypes: true });

const mapping = [
  { match: /QUAN_SUITE_1/i, name: 'VIDEO_QUAN_01_Auth_DangNhap.webm' },
  { match: /QUAN_SUITE_2/i, name: 'VIDEO_QUAN_02_DoiMatKhau_PhanQuyen.webm' },
  { match: /QUAN_SUITE_3/i, name: 'VIDEO_QUAN_03_TiepNhanDon_QuyTrinhKDS.webm' },
  { match: /QUAN_SUITE_4/i, name: 'VIDEO_QUAN_04_StatePattern_GiaoKhach.webm' },
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

// Fallback: nếu chưa đủ 4 video theo regex, copy các file video mới nhất
if (copied < mapping.length) {
  const videoFolders = entries
    .filter(e => e.isDirectory() && fs.existsSync(path.join(testResultsDir, e.name, 'video.webm')))
    .map(e => ({
      path: path.join(testResultsDir, e.name, 'video.webm'),
      time: fs.statSync(path.join(testResultsDir, e.name, 'video.webm')).mtimeMs
    }))
    .sort((a, b) => b.time - a.time);

  videoFolders.slice(0, 4).forEach((vf, idx) => {
    const targetName = mapping[idx] ? mapping[idx].name : `VIDEO_QUAN_0${idx + 1}.webm`;
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
console.log(` [HOÀN TẤT] Đã xuất ${copied} video kiểm thử 20 Test Cases của Quân vào:`);
console.log(` ${outputVideosDir}`);
console.log(`======================================================\n`);
