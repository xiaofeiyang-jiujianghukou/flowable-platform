<template>
  <div id="app">
    <header v-if="route.path !== '/login'" class="app-header">
      <span class="logo">用户中心</span>
      <nav class="nav-links">
        <router-link v-for="m in menus" :key="m.id" :to="m.path || '/'">{{ m.name }}</router-link>
      </nav>
      <div class="user-area">
        <span class="user-name">{{ currentUser?.name }}</span>
        <a class="logout-btn" @click="handleLogout">退出</a>
      </div>
    </header>
    <main><router-view /></main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { currentUser, logout } from './utils/auth'

const route = useRoute()
const router = useRouter()

const menus = computed(() => currentUser.value?.menus?.filter((m: any) => m.type === 'menu' && m.path) || [])

const handleLogout = async () => {
  await logout()
  router.replace('/login')
}
</script>

<style>
*{margin:0;padding:0;box-sizing:border-box}
#app{height:100vh;display:flex;flex-direction:column;font-family:'Helvetica Neue',sans-serif}
.app-header{height:48px;background:#001529;display:flex;align-items:center;padding:0 20px;flex-shrink:0}
.app-header .logo{color:#fff;font-size:16px;font-weight:600;margin-right:24px}
.nav-links{display:flex;gap:8px;flex:1}
.nav-links a{color:rgba(255,255,255,0.65);text-decoration:none;font-size:14px;padding:4px 12px;border-radius:4px}
.nav-links a:hover,.nav-links a.router-link-active{color:#fff;background:rgba(255,255,255,0.15)}
.user-area{display:flex;align-items:center;gap:12px}
.user-name{color:rgba(255,255,255,0.85);font-size:13px}
.logout-btn{color:rgba(255,255,255,0.5);font-size:12px;cursor:pointer}
.logout-btn:hover{color:#fff}
main{flex:1;overflow:auto}
</style>
