import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      component: () => import('@/layouts/DefaultLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '首页', icon: 'HomeFilled' },
        },
        // 系统管理
        {
          path: 'system/user',
          name: 'system-user',
          component: () => import('@/views/system/user/index.vue'),
          meta: { title: '用户管理', icon: 'User', permission: 'sys:user:list' },
        },
        {
          path: 'system/role',
          name: 'system-role',
          component: () => import('@/views/system/role/index.vue'),
          meta: { title: '角色管理', icon: 'UserFilled', permission: 'sys:role:list' },
        },
        {
          path: 'system/menu',
          name: 'system-menu',
          component: () => import('@/views/system/menu/index.vue'),
          meta: { title: '菜单管理', icon: 'Menu', permission: 'sys:menu:list' },
        },
        {
          path: 'system/dict',
          name: 'system-dict',
          component: () => import('@/views/system/dict/index.vue'),
          meta: { title: '字典管理', icon: 'Collection', permission: 'sys:dict:list' },
        },
        // 基础资料
        {
          path: 'master/item',
          name: 'master-item',
          component: () => import('@/views/master/item/index.vue'),
          meta: { title: '物料管理', icon: 'Goods', permission: 'mdm:item:list' },
        },
        {
          path: 'master/warehouse',
          name: 'master-warehouse',
          component: () => import('@/views/master/warehouse/index.vue'),
          meta: { title: '仓库管理', icon: 'House', permission: 'mdm:warehouse:list' },
        },
        {
          path: 'master/supplier',
          name: 'master-supplier',
          component: () => import('@/views/master/supplier/index.vue'),
          meta: { title: '供应商管理', icon: 'Avatar', permission: 'mdm:supplier:list' },
        },
        {
          path: 'master/customer',
          name: 'master-customer',
          component: () => import('@/views/master/customer/index.vue'),
          meta: { title: '客户管理', icon: 'User', permission: 'mdm:customer:list' },
        },
        // 采购管理
        {
          path: 'purchase/order',
          name: 'purchase-order',
          component: () => import('@/views/purchase/order/index.vue'),
          meta: { title: '采购订单', icon: 'Document', permission: 'po:order:list' },
        },
        {
          path: 'purchase/receipt',
          name: 'purchase-receipt',
          component: () => import('@/views/purchase/receipt/index.vue'),
          meta: { title: '采购入库', icon: 'DocumentAdd', permission: 'po:receipt:list' },
        },
        // 销售管理
        {
          path: 'sales/order',
          name: 'sales-order',
          component: () => import('@/views/sales/order/index.vue'),
          meta: { title: '销售订单', icon: 'Document', permission: 'so:order:list' },
        },
        {
          path: 'sales/shipment',
          name: 'sales-shipment',
          component: () => import('@/views/sales/shipment/index.vue'),
          meta: { title: '销售出库', icon: 'DocumentRemove', permission: 'so:shipment:list' },
        },
        // 库存管理
        {
          path: 'inventory/stock',
          name: 'inventory-stock',
          component: () => import('@/views/inventory/stock/index.vue'),
          meta: { title: '即时库存', icon: 'Box', permission: 'inv:stock:list' },
        },
        {
          path: 'inventory/transaction',
          name: 'inventory-transaction',
          component: () => import('@/views/inventory/transaction/index.vue'),
          meta: { title: '库存流水', icon: 'List', permission: 'inv:transaction:list' },
        },
        {
          path: 'inventory/transfer',
          name: 'inventory-transfer',
          component: () => import('@/views/inventory/transfer/index.vue'),
          meta: { title: '调拨管理', icon: 'Sort', permission: 'inv:transfer:list' },
        },
        {
          path: 'inventory/stocktake',
          name: 'inventory-stocktake',
          component: () => import('@/views/inventory/stocktake/index.vue'),
          meta: { title: '盘点管理', icon: 'Notebook', permission: 'inv:stocktake:list' },
        },
        // 生产管理
        {
          path: 'production/bom',
          name: 'production-bom',
          component: () => import('@/views/production/bom/index.vue'),
          meta: { title: 'BOM管理', icon: 'Tickets', permission: 'pp:bom:list' },
        },
        {
          path: 'production/workorder',
          name: 'production-workorder',
          component: () => import('@/views/production/workorder/index.vue'),
          meta: { title: '生产工单', icon: 'Document', permission: 'pp:workorder:list' },
        },
        {
          path: 'production/material',
          name: 'production-material',
          component: () => import('@/views/production/material/index.vue'),
          meta: { title: '领料管理', icon: 'DocumentRemove', permission: 'pp:material:list' },
        },
        // 财务管理
        {
          path: 'finance/receivable',
          name: 'finance-receivable',
          component: () => import('@/views/finance/receivable/index.vue'),
          meta: { title: '应收管理', icon: 'Money', permission: 'fin:receivable:list' },
        },
        {
          path: 'finance/payable',
          name: 'finance-payable',
          component: () => import('@/views/finance/payable/index.vue'),
          meta: { title: '应付管理', icon: 'Wallet', permission: 'fin:payable:list' },
        },
        // 报表中心
        {
          path: 'report',
          name: 'report',
          component: () => import('@/views/report/index.vue'),
          meta: { title: '报表中心', icon: 'DataLine', permission: 'report:list' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/404.vue'),
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 不需要认证的页面
  if (to.meta.requiresAuth === false) {
    // 如果已登录，跳转到首页
    if (authStore.isLoggedIn && to.name === 'login') {
      next({ name: 'dashboard' })
      return
    }
    next()
    return
  }

  // 需要认证的页面
  if (!authStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  // 检查权限
  if (to.meta.permission && !authStore.hasPermission(to.meta.permission as string)) {
    next({ name: 'dashboard' })
    return
  }

  next()
})

export default router
