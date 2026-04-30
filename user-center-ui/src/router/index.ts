import { createRouter, createWebHashHistory } from 'vue-router'
import { isLoggedIn } from '../utils/auth'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/login', component: () => import('../views/LoginView.vue') },
    { path: '/', component: () => import('../views/UserList.vue') },
    { path: '/departments', component: () => import('../views/DepartmentList.vue') },
    { path: '/roles', component: () => import('../views/RoleList.vue') },
    { path: '/process-config', component: () => import('../views/ProcessConfig.vue') },
    { path: '/approval', component: () => import('../views/Approval.vue') },
    { path: '/menus', component: () => import('../views/MenuManage.vue') },
  ],
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !isLoggedIn()) return '/login'
  return true
})

export default router
