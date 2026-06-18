<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="not-found">
    <div class="content">
      <h1 class="title">404</h1>
      <p class="desc">抱歉，您访问的页面不存在</p>
      <el-button type="primary" size="large" @click="goHome">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}

.content {
  text-align: center;
}

.title {
  font-size: 120px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 20px;
}

.desc {
  font-size: 18px;
  color: #909399;
  margin-bottom: 30px;
}
</style>
