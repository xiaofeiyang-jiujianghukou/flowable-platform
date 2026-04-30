<template>
  <div class="page"><div class="toolbar"><h3>角色管理</h3><el-button type="primary" @click="openCreate">新增角色</el-button></div>
    <el-table :data="roles" stripe>
      <el-table-column prop="code" label="编码" width="150" />
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="userCount" label="人数" width="80" />
      <el-table-column label="操作" width="240">
        <template #default="{row}"><el-button size="small" @click="openUsers(row.code)">分配用户</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.code)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="visible" title="新增角色" width="400px">
      <el-form label-width="80px"><el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item></el-form>
      <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="userVisible" :title="'分配用户 - ' + currentRole" width="500px">
      <el-input v-model="userKeyword" placeholder="搜索用户" style="margin-bottom:8px" clearable />
      <el-table :data="allUsers" stripe @selection-change="(sel:any) => selectedUsers = sel.map((s:any) => s.id)">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="username" label="用户名" /><el-table-column prop="name" label="姓名" />
      </el-table>
      <template #footer><el-button @click="userVisible=false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { fetchAuth } from '../utils/auth'
import { ref, reactive, watch } from 'vue'; import { ElMessage } from 'element-plus'
const roles = ref([]); const visible = ref(false); const userVisible = ref(false); const currentRole = ref('')
const form = reactive({ code: '', name: '', description: '' }); const allUsers = ref([]); const userKeyword = ref('')
const selectedUsers = ref<number[]>([])
const load = () => fetchAuth('/api/roles').then(r => r.json()).then(d => roles.value = d)
const loadUsers = () => fetchAuth(`/api/roles/${currentRole.value}/users?keyword=${userKeyword.value}`).then(r => r.json()).then(d => allUsers.value = d)
const openCreate = () => { Object.assign(form, { code: '', name: '', description: '' }); visible.value = true }
const handleSave = () => fetchAuth('/api/roles', { method: 'POST', body: JSON.stringify(form) }).then(load).then(() => { visible.value = false; ElMessage.success('创建成功') })
const handleDelete = (code: string) => fetchAuth('/api/roles/' + code, { method: 'DELETE' }).then(load)
const openUsers = (code: string) => { currentRole.value = code; loadUsers(); userVisible.value = true }
watch(userKeyword, loadUsers); watch(userVisible, (v) => { if (v) loadUsers() })
watch(selectedUsers, (ids) => { ids.forEach(id => fetchAuth(`/api/roles/${currentRole.value}/users/` + id, { method: 'POST' }).then(() => ElMessage.success('已分配'))) })
load()
</script>
<style scoped>.page{padding:20px}.toolbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}</style>
