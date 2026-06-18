import { test, expect } from '@playwright/test'

/**
 * 采购管理模块 E2E 测试
 */
test.describe('采购管理模块', () => {
  test.beforeEach(async ({ page }) => {
    // 先登录
    await page.goto('/login')
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')
    await page.click('button:has-text("登录")')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('采购订单页面访问', async ({ page }) => {
    // 点击采购管理菜单
    await page.click('text=采购管理')
    await page.click('text=采购订单')

    // 验证页面加载
    await expect(page).toHaveURL(/.*purchase\/order/)
    await expect(page.locator('.search-area')).toBeVisible()
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('采购入库页面访问', async ({ page }) => {
    // 点击采购管理菜单
    await page.click('text=采购管理')
    await page.click('text=采购入库')

    // 验证页面加载
    await expect(page).toHaveURL(/.*purchase\/receipt/)
    await expect(page.locator('.search-area')).toBeVisible()
  })

  test('采购订单 - 搜索功能', async ({ page }) => {
    // 进入采购订单页面
    await page.click('text=采购管理')
    await page.click('text=采购订单')
    await expect(page).toHaveURL(/.*purchase\/order/)

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 验证表格加载
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('采购订单 - 状态筛选', async ({ page }) => {
    // 进入采购订单页面
    await page.click('text=采购管理')
    await page.click('text=采购订单')
    await expect(page).toHaveURL(/.*purchase\/order/)

    // 选择状态筛选
    await page.click('.el-select:has-text("请选择")')
    await page.click('.el-select-dropdown__item:has-text("草稿")')

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 验证表格加载
    await expect(page.locator('.el-table')).toBeVisible()
  })
})
