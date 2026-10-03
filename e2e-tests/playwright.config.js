// @ts-check
const { defineConfig, devices } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './tests',
  fullyParallel: false, // Run sequentially for clear video recordings
  forbidOnly: !!process.env.CI,
  retries: 0,
  workers: 1, // 1 worker ensures deterministic execution and orderly videos
  reporter: [
    ['html', { open: 'never' }],
    ['list']
  ],
  use: {
    baseURL: 'http://localhost:8088',
    trace: 'on',
    screenshot: 'on',
    video: {
      mode: 'on',
      size: { width: 1280, height: 720 },
    },
    viewport: { width: 1280, height: 720 },
    launchOptions: {
      slowMo: 400, // Thêm độ trễ 400ms giữa các thao tác để video quay mượt mà, dễ quan sát
    },
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'python -m http.server 8088 --directory ../frontend-react/dist',
    port: 8088,
    reuseExistingServer: true,
    timeout: 15000,
  },
});

