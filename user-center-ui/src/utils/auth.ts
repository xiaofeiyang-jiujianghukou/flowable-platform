export interface SessionUser {
  userId: number
  username: string
  name: string
  departmentId: number | null
  deptName: string | null
  roles: string[]
  permissions: string[]
  menus: MenuItem[]
}

export interface MenuItem {
  id: number
  name: string
  path: string | null
  icon: string | null
  parentId: number
  permissionCode: string | null
  type: string
  children?: MenuItem[]
}

import { ref } from 'vue'

const TOKEN_KEY = 'token'
const USER_KEY = 'sessionUser'

// 响应式用户状态，解决 localStorage 非响应式问题
export const currentUser = ref<SessionUser | null>(null)

function initFromStorage() {
  const s = localStorage.getItem(USER_KEY)
  if (s) currentUser.value = JSON.parse(s)
}
initFromStorage()

export function getToken(): string | null { return localStorage.getItem(TOKEN_KEY) }
export function setToken(token: string) { localStorage.setItem(TOKEN_KEY, token) }
export function removeToken() { localStorage.removeItem(TOKEN_KEY) }

export function getUser(): SessionUser | null { return currentUser.value }

export function setUser(user: SessionUser) {
  currentUser.value = user
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUser() {
  currentUser.value = null
  localStorage.removeItem(USER_KEY)
}

export function isLoggedIn(): boolean { return !!getToken() }

export async function login(username: string, password: string): Promise<SessionUser> {
  const r = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })
  if (!r.ok) {
    const e = await r.json().catch(() => ({ error: '登录失败' }))
    throw new Error(e.error || '登录失败')
  }
  const d = await r.json()
  setToken(d.token)
  setUser(d.user)
  return d.user
}

export async function logout() {
  const token = getToken()
  if (token) {
    await fetch('/api/auth/logout', { method: 'POST', headers: { 'Authorization': 'Bearer ' + token } }).catch(() => {})
  }
  removeToken()
  removeUser()
}

export async function fetchAuth(path: string, init?: RequestInit): Promise<Response> {
  const token = getToken()
  const r = await fetch(path, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + (token || ''),
      ...init?.headers,
    },
  })
  if (r.status === 401) { removeToken(); removeUser(); location.hash = '#/login' }
  return r
}
