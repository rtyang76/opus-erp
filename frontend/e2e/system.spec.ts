import { test, expect } from '@playwright/test'

/**
 * 系统管理模块 E2E 测试
 */
test.describe('系统管理模块', () => {
  test.beforeEach(async ({ page }) => {
    // 先登录
    await page.goto('/login')
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')
    await page.click('button:has-text("登录")')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('用户管理页面访问', async ({ page }) => {
    // 点击系统管理菜单
    await page.click('text=系统管理')
    await page.click('text=用户管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*system\/user/)
    await expect(page.locator('.search-area')).toBeVisible()
    await expect(page.locator('button:has-text("新增用户")')).toBeVisible()
  })

  test('用户管理 - 搜索功能', async ({ page }) => {
    // 进入用户管理页面
    await page.click('text=系统管理')
    await page.click('text=用户管理')
    await expect(page).toHaveURL(/.*system\/user/)

    // 输入搜索条件
    await page.fill('input[placeholder="请输入用户名"]', 'admin')

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 验证搜索结果
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('用户管理 - 新增用户弹窗', async ({ page }) => {
    // 进入用户管理页面
    await page.click('text=系统管理')
    await page.click('text=用户管理')
    await expect(page).toHaveURL(/.*system\/user/)

    // 点击新增按钮
    await page.click('button:has-text("新增用户")')

    // 验证弹窗显示
    await expect(page.locator('.el-dialog')).toBeVisible()
    await expect(page.locator('.el-dialog__title')).toHaveText('新增用户')
  })

  test('角色管理页面访问', async ({ page }) => {
    // 点击系统管理菜单
    await page.click('text=系统管理')
    await page.click('text=角色管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*system\/role/)
  })

  test('菜单管理页面访问', async ({ page }) => {
    // 点击系统管理菜单
    await page.click('text=系统管理')
    await page.click('text=菜单管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*system\/menu/)
  })

  test('字典管理页面访问', async ({ page }) => {
    // 点击系统管理菜单
    await page.click('text=系统管理')
    await page.click('text=字典管理')

    // 验证页面加载
    await expect(page).toHaveURL(/.*system\/dict/)
  })
})
