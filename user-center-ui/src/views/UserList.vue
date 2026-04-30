<template>
  <div class="page">
    <div class="toolbar"><h3>用户管理</h3><el-button type="primary" @click="openCreate">新增用户</el-button></div>
    <el-input v-model="keyword" placeholder="搜索用户名/姓名" style="width:240px;margin-bottom:12px" clearable @input="load" />
    <el-table :data="users" stripe>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="deptName" label="部门" width="120" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="visible" :title="editing.id?'编辑用户':'新增用户'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="用户名"><el-input v-model="editing.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="editing.name" /></el-form-item>
        <el-form-item label="部门">
          <DeptTreeSelect v-model="editing.departmentId" placeholder="选择部门" />
        </el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editing.email" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="editing.phone" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { fetchAuth } from '../utils/auth'
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import DeptTreeSelect from '../components/DeptTreeSelect.vue'

const API = '/api/users'
const users = ref([])
const keyword = ref('')
const visible = ref(false)
const editing = reactive<any>({})

const load = () => fetchAuth(API + '?keyword=' + keyword.value).then(r => r.json()).then(d => users.value = d)

const openCreate = () => { for (const k of Object.keys(editing)) delete editing[k]; visible.value = true }
const openEdit = (row: any) => { for (const k of Object.keys(editing)) delete editing[k]; Object.assign(editing, row); visible.value = true }

const handleSave = () => {
  const body = { ...editing }
  const m = body.id
    ? fetchAuth(API + '/' + body.id, { method: 'PUT', body: JSON.stringify(body) })
    : fetchAuth(API, { method: 'POST', body: JSON.stringify(body) })
  m.then(load).then(() => { visible.value = false; ElMessage.success('保存成功') })
}

const handleDelete = (id: number) => fetchAuth(API + '/' + id, { method: 'DELETE' }).then(load).then(() => ElMessage.success('已删除'))

load()
</script>
<style scoped>.page{padding:20px}.toolbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}</style>
