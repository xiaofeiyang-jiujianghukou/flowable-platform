<template>
  <div id="app">
    <header class="app-header">
      <span class="logo" @click="$router.push('/')">Flowable 工作流平台</span>
      <nav class="nav-links">
        <router-link to="/">模型设计</router-link>
      </nav>
    </header>
    <main><router-view /></main>
  </div>
</template>

<script setup lang="ts">
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
#app { height: 100vh; display: flex; flex-direction: column; font-family: 'Helvetica Neue', sans-serif; }
.app-header { height: 48px; background: #001529; display: flex; align-items: center; padding: 0 20px; flex-shrink: 0; }
.app-header .logo { color: #fff; font-size: 16px; font-weight: 600; cursor: pointer; margin-right: 24px; }
.nav-links { display: flex; gap: 8px; }
.nav-links a { color: rgba(255,255,255,0.65); text-decoration: none; font-size: 14px; padding: 4px 12px; border-radius: 4px; }
.nav-links a:hover, .nav-links a.router-link-active { color: #fff; background: rgba(255,255,255,0.15); }
main { flex: 1; overflow: hidden; }
</style>
