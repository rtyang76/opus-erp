import { test, expect } from '@playwright/test'

// 测试前登录
test.beforeEach(async ({ page }) => {
  // 访问登录页
  await page.goto('/login')

  // 填写登录信息
  await page.fill('input[placeholder="请输入用户名"]', 'admin')
  await page.fill('input[placeholder="请输入密码"]', 'admin123')

  // 点击登录按钮
  await page.click('button:has-text("登录")')

  // 等待登录成功跳转
  await page.waitForURL('/dashboard')
})

test.describe('应收管理', () => {
  test('应收管理页面加载', async ({ page }) => {
    await page.goto('/finance/receivable')

    // 验证页面标题
    await expect(page.locator('.page-container')).toBeVisible()

    // 验证新增按钮存在
    await expect(page.locator('button:has-text("新增应收单")')).toBeVisible()

    // 验证表格加载
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('新增应收单', async ({ page }) => {
    await page.goto('/finance/receivable')

    // 点击新增按钮
    await page.click('button:has-text("新增应收单")')

    // 验证弹窗标题
    await expect(page.locator('.el-dialog__title:has-text("新增应收单")')).toBeVisible()

    // 选择客户（点击第一个选项）
    await page.click('.el-select:has-text("请选择客户")')
    await page.click('.el-select-dropdown__item:first-child')

    // 输入金额
    await page.fill('.el-input-number input', '10000')

    // 点击确定
    await page.click('.el-dialog__footer button:has-text("确定")')

    // 验证成功消息
    await expect(page.locator('.el-message:has-text("创建成功")')).toBeVisible()
  })

  test('编辑应收单', async ({ page }) => {
    await page.goto('/finance/receivable')

    // 等待表格加载
    await page.waitForSelector('.el-table__row')

    // 点击第一行的编辑按钮
    await page.click('.el-table__row:first-child button:has-text("编辑")')

    // 验证弹窗标题
    await expect(page.locator('.el-dialog__title:has-text("编辑应收单")')).toBeVisible()

    // 点击确定
    await page.click('.el-dialog__footer button:has-text("确定")')

    // 验证成功消息
    await expect(page.locator('.el-message:has-text("更新成功")')).toBeVisible()
  })

  test('删除应收单', async ({ page }) => {
    await page.goto('/finance/receivable')

    // 等待表格加载
    await page.waitForSelector('.el-table__row')

    // 点击第一行的删除按钮
    await page.click('.el-table__row:first-child button:has-text("删除")')

    // 确认删除
    await page.click('.el-message-box__btns button:has-text("确定")')

    // 验证成功消息
    await expect(page.locator('.el-message:has-text("删除成功")')).toBeVisible()
  })

  test('搜索应收单', async ({ page }) => {
    await page.goto('/finance/receivable')

    // 选择状态
    await page.click('.el-select:has-text("请选择")')
    await page.click('.el-select-dropdown__item:has-text("待收款")')

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 等待表格刷新
    await page.waitForTimeout(500)
  })
})

test.describe('应付管理', () => {
  test('应付管理页面加载', async ({ page }) => {
    await page.goto('/finance/payable')

    // 验证页面标题
    await expect(page.locator('.page-container')).toBeVisible()

    // 验证新增按钮存在
    await expect(page.locator('button:has-text("新增应付单")')).toBeVisible()

    // 验证表格加载
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('新增应付单', async ({ page }) => {
    await page.goto('/finance/payable')

    // 点击新增按钮
    await page.click('button:has-text("新增应付单")')

    // 验证弹窗标题
    await expect(page.locator('.el-dialog__title:has-text("新增应付单")')).toBeVisible()

    // 选择供应商（点击第一个选项）
    await page.click('.el-select:has-text("请选择供应商")')
    await page.click('.el-select-dropdown__item:first-child')

    // 输入金额
    await page.fill('.el-input-number input', '5000')

    // 点击确定
    await page.click('.el-dialog__footer button:has-text("确定")')

    // 验证成功消息
    await expect(page.locator('.el-message:has-text("创建成功")')).toBeVisible()
  })

  test('编辑应付单', async ({ page }) => {
    await page.goto('/finance/payable')

    // 等待表格加载
    await page.waitForSelector('.el-table__row')

    // 点击第一行的编辑按钮
    await page.click('.el-table__row:first-child button:has-text("编辑")')

    // 验证弹窗标题
    await expect(page.locator('.el-dialog__title:has-text("编辑应付单")')).toBeVisible()

    // 点击确定
    await page.click('.el-dialog__footer button:has-text("确定")')

    // 验证成功消息
    await expect(page.locator('.el-message:has-text("更新成功")')).toBeVisible()
  })

  test('删除应付单', async ({ page }) => {
    await page.goto('/finance/payable')

    // 等待表格加载
    await page.waitForSelector('.el-table__row')

    // 点击第一行的删除按钮
    await page.click('.el-table__row:first-child button:has-text("删除")')

    // 确认删除
    await page.click('.el-message-box__btns button:has-text("确定")')

    // 验证成功消息
    await expect(page.locator('.el-message:has-text("删除成功")')).toBeVisible()
  })

  test('搜索应付单', async ({ page }) => {
    await page.goto('/finance/payable')

    // 选择状态
    await page.click('.el-select:has-text("请选择")')
    await page.click('.el-select-dropdown__item:has-text("待付款")')

    // 点击搜索
    await page.click('button:has-text("搜索")')

    // 等待表格刷新
    await page.waitForTimeout(500)
  })
})
