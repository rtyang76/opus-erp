import { test, expect } from '@playwright/test'

/**
 * 销售管理模块 E2E 测试
 */
test.describe('销售管理模块', () => {
  test.beforeEach(async ({ page }) => {
    // 先登录
    await page.goto('/login')
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')
    await page.click('button:has-text("登录")')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('销售订单页面访问', async ({ page }) => {
    // 点击销售管理菜单
    await page.click('text=销售管理')
    await page.click('text=销售订单')

    // 验证页面加载
    await expect(page).toHaveURL(/.*sales\/order/)
    await expect(page.locator('.search-area')).toBeVisible()
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('销售出库页面访问', async ({ page }) => {
    // 点击销售管理菜单
    await page.click('text=销售管理')
    await page.click('text=销售出库')

    // 验证页面加载
    await expect(page).toHaveURL(/.*sales\/shipment/)
    await expect(page.locator('.search-area')).toBeVisible()
  })

  test('销售订单 - 搜索功能', async ({ page }) => {
    // 进入销售订单页面
    await page.click('text=销售管理')
    await page.click('text=销售订单')
    await expect(page).toHaveURL(/.*sales\/order/)

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 验证表格加载
    await expect(page.locator('.el-table')).toBeVisible()
  })
})
