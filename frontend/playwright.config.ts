import { defineConfig, devices } from '@playwright/test'

/**
 * Playwright 测试配置
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './e2e',
  /* 每个测试的最大运行时间 */
  timeout: 30000,
  /* 每个 expect 的超时时间 */
  expect: {
    timeout: 5000,
  },
  /* 测试报告 */
  reporter: [
    ['html', { outputFolder: 'playwright-report' }],
    ['list'],
  ],
  /* 全局设置 */
  use: {
    /* 基础 URL */
    baseURL: 'http://localhost:5173',
    /* 浏览器上下文选项 */
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  /* 浏览器配置 */
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  /* 开发服务器配置 */
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
    timeout: 120000,
  },
})
