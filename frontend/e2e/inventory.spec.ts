import { test, expect } from '@playwright/test'

/**
 * 库存管理模块 E2E 测试
 */
test.describe('库存管理模块', () => {
  test.beforeEach(async ({ page }) => {
    // 先登录
    await page.goto('/login')
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')
    await page.click('button:has-text("登录")')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('即时库存页面访问', async ({ page }) => {
    // 点击库存管理菜单
    await page.click('text=库存管理')
    await page.click('text=即时库存')

    // 验证页面加载
    await expect(page).toHaveURL(/.*inventory\/stock/)
    await expect(page.locator('.search-area')).toBeVisible()
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('库存流水页面访问', async ({ page }) => {
    // 点击库存管理菜单
    await page.click('text=库存管理')
    await page.click('text=库存流水')

    // 验证页面加载
    await expect(page).toHaveURL(/.*inventory\/transaction/)
    await expect(page.locator('.search-area')).toBeVisible()
  })

  test('调拨管理页面访问', async ({ page }) => {
    // 点击库存管理菜单
    await page.click('text=库存管理')
    await page.click('text=调拨管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*inventory\/transfer/)
    await expect(page.locator('.search-area')).toBeVisible()
  })

  test('盘点管理页面访问', async ({ page }) => {
    // 点击库存管理菜单
    await page.click('text=库存管理')
    await page.click('text=盘点管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*inventory\/stocktake/)
    await expect(page.locator('.search-area')).toBeVisible()
  })

  test('即时库存 - 搜索功能', async ({ page }) => {
    // 进入即时库存页面
    await page.click('text=库存管理')
    await page.click('text=即时库存')
    await expect(page).toHaveURL(/.*inventory\/stock/)

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 验证表格加载
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('即时库存 - 重置功能', async ({ page }) => {
    // 进入即时库存页面
    await page.click('text=库存管理')
    await page.click('text=即时库存')
    await expect(page).toHaveURL(/.*inventory\/stock/)

    // 输入搜索条件
    await page.fill('input[placeholder="请输入物料ID"]', '1')

    // 点击重置
    await page.click('button:has-text("重置")')

    // 验证搜索条件已清空
    await expect(page.locator('input[placeholder="请输入物料ID"]')).toHaveValue('')
  })
})
