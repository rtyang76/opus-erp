import { test, expect } from '@playwright/test'

/**
 * 认证模块 E2E 测试
 */
test.describe('认证模块', () => {
  test.beforeEach(async ({ page }) => {
    // 访问登录页面
    await page.goto('/login')
  })

  test('登录页面显示正常', async ({ page }) => {
    // 验证页面标题
    await expect(page.locator('h1')).toHaveText('Opus ERP')
    await expect(page.locator('.login-subtitle')).toHaveText('企业资源计划系统')

    // 验证表单元素
    await expect(page.locator('input[placeholder="请输入用户名"]')).toBeVisible()
    await expect(page.locator('input[placeholder="请输入密码"]')).toBeVisible()
    await expect(page.locator('button:has-text("登录")')).toBeVisible()
  })

  test('用户名为空时显示错误提示', async ({ page }) => {
    // 点击登录按钮
    await page.click('button:has-text("登录")')

    // 验证错误提示
    await expect(page.locator('.el-form-item__error')).toHaveText('请输入用户名')
  })

  test('密码为空时显示错误提示', async ({ page }) => {
    // 输入用户名
    await page.fill('input[placeholder="请输入用户名"]', 'admin')

    // 点击登录按钮
    await page.click('button:has-text("登录")')

    // 验证错误提示
    await expect(page.locator('.el-form-item__error')).toHaveText('请输入密码')
  })

  test('登录成功后跳转到首页', async ({ page }) => {
    // 输入用户名和密码
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')

    // 点击登录按钮
    await page.click('button:has-text("登录")')

    // 验证跳转到首页
    await expect(page).toHaveURL(/.*dashboard/)
    await expect(page.locator('.welcome h2')).toContainText('欢迎回来')
  })

  test('登录失败显示错误提示', async ({ page }) => {
    // 输入错误的用户名和密码
    await page.fill('input[placeholder="请输入用户名"]', 'wrong')
    await page.fill('input[placeholder="请输入密码"]', 'wrong')

    // 点击登录按钮
    await page.click('button:has-text("登录")')

    // 验证错误提示
    await expect(page.locator('.el-message--error')).toBeVisible()
  })

  test('退出登录后跳转到登录页', async ({ page }) => {
    // 先登录
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[placeholder="请输入密码"]', 'admin123')
    await page.click('button:has-text("登录")')

    // 等待跳转到首页
    await expect(page).toHaveURL(/.*dashboard/)

    // 点击用户下拉菜单
    await page.click('.user-info')

    // 点击退出登录
    await page.click('text=退出登录')

    // 确认退出
    await page.click('button:has-text("确定")')

    // 验证跳转到登录页
    await expect(page).toHaveURL(/.*login/)
  })
})
