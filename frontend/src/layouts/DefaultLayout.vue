<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()

const isCollapse = ref(false)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await authStore.logout()
    router.push('/login')
  } catch {
    // 取消操作
  }
}

const menuList = [
  {
    path: '/dashboard',
    title: '首页',
    icon: 'HomeFilled',
  },
  {
    title: '系统管理',
    icon: 'Setting',
    children: [
      { path: '/system/user', title: '用户管理', icon: 'User' },
      { path: '/system/role', title: '角色管理', icon: 'UserFilled' },
      { path: '/system/menu', title: '菜单管理', icon: 'Menu' },
      { path: '/system/dict', title: '字典管理', icon: 'Collection' },
    ],
  },
  {
    title: '基础资料',
    icon: 'Document',
    children: [
      { path: '/master/item', title: '物料管理', icon: 'Goods' },
      { path: '/master/warehouse', title: '仓库管理', icon: 'House' },
      { path: '/master/supplier', title: '供应商管理', icon: 'Avatar' },
      { path: '/master/customer', title: '客户管理', icon: 'User' },
    ],
  },
  {
    title: '采购管理',
    icon: 'ShoppingCart',
    children: [
      { path: '/purchase/order', title: '采购订单', icon: 'Document' },
      { path: '/purchase/receipt', title: '采购入库', icon: 'DocumentAdd' },
    ],
  },
  {
    title: '销售管理',
    icon: 'Sell',
    children: [
      { path: '/sales/order', title: '销售订单', icon: 'Document' },
      { path: '/sales/shipment', title: '销售出库', icon: 'DocumentRemove' },
    ],
  },
  {
    title: '库存管理',
    icon: 'Box',
    children: [
      { path: '/inventory/stock', title: '即时库存', icon: 'Box' },
      { path: '/inventory/transaction', title: '库存流水', icon: 'List' },
      { path: '/inventory/transfer', title: '调拨管理', icon: 'Sort' },
      { path: '/inventory/stocktake', title: '盘点管理', icon: 'Notebook' },
    ],
  },
  {
    title: '生产管理',
    icon: 'SetUp',
    children: [
      { path: '/production/bom', title: 'BOM管理', icon: 'Tickets' },
      { path: '/production/workorder', title: '生产工单', icon: 'Document' },
    ],
  },
  {
    title: '财务管理',
    icon: 'Money',
    children: [
      { path: '/finance/receivable', title: '应收管理', icon: 'Money' },
      { path: '/finance/payable', title: '应付管理', icon: 'Wallet' },
    ],
  },
]
</script>

<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <h1 v-if="!isCollapse">Opus ERP</h1>
        <h1 v-else>OP</h1>
      </div>

      <el-menu
        :default-active="$route.path"
        :collapse="isCollapse"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <template v-for="item in menuList" :key="item.path">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="item.children" :index="item.path || item.title">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单 -->
          <el-menu-item v-else :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleSidebar">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ authStore.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item icon="User">个人中心</el-dropdown-item>
                <el-dropdown-item icon="SwitchButton" divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #263445;
}

.logo h1 {
  color: #fff;
  font-size: 20px;
  margin: 0;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.sidebar-menu::-webkit-scrollbar {
  width: 0;
}

.main-container {
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #606266;
}

.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
