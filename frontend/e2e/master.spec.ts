import { test, expect } from '@playwright/test'

/**
 * 基础资料模块 E2E 测试
 */
test.describe('基础资料模块', () => {
  test.beforeEach(async ({ page }) => {
    // 先登录
    await page.goto('/login')
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')
    await page.click('button:has-text("登录")')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('物料管理页面访问', async ({ page }) => {
    // 点击基础资料菜单
    await page.click('text=基础资料')
    await page.click('text=物料管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*master\/item/)
    await expect(page.locator('.search-area')).toBeVisible()
    await expect(page.locator('button:has-text("新增物料")')).toBeVisible()
  })

  test('物料管理 - 新增物料弹窗', async ({ page }) => {
    // 进入物料管理页面
    await page.click('text=基础资料')
    await page.click('text=物料管理')
    await expect(page).toHaveURL(/.*master\/item/)

    // 点击新增按钮
    await page.click('button:has-text("新增物料")')

    // 验证弹窗显示
    await expect(page.locator('.el-dialog')).toBeVisible()
    await expect(page.locator('.el-dialog__title')).toHaveText('新增物料')
  })

  test('仓库管理页面访问', async ({ page }) => {
    // 点击基础资料菜单
    await page.click('text=基础资料')
    await page.click('text=仓库管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*master\/warehouse/)
  })

  test('供应商管理页面访问', async ({ page }) => {
    // 点击基础资料菜单
    await page.click('text=基础资料')
    await page.click('text=供应商管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*master\/supplier/)
  })

  test('客户管理页面访问', async ({ page }) => {
    // 点击基础资料菜单
    await page.click('text=基础资料')
    await page.click('text=客户管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*master\/customer/)
  })
})
