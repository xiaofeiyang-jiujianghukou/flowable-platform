import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', name: 'home', component: () => import('../views/ModelList.vue') },
    { path: '/design/:id', name: 'design', component: () => import('../views/Designer.vue'), props: true },
  ],
})

export default router
