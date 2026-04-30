<template>
  <div class="page">
    <div class="toolbar"><h3>菜单管理</h3></div>
    <el-tabs v-model="tab">
      <el-tab-pane label="菜单列表" name="list">
        <el-button type="primary" size="small" @click="openCreate()" style="margin-bottom:12px">新增菜单</el-button>
        <el-table :data="menus" stripe>
          <el-table-column prop="name" label="名称" width="150" />
          <el-table-column prop="path" label="路径" />
          <el-table-column prop="permissionCode" label="权限编码" width="160" />
          <el-table-column prop="type" label="类型" width="80" />
          <el-table-column prop="sortOrder" label="排序" width="60" />
          <el-table-column label="操作" width="160">
            <template #default="{row}"><el-button size="small" @click="openEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="角色授权" name="grant">
        <el-select v-model="grantRole" placeholder="选择角色" @change="loadRoleMenus" style="width:200px">
          <el-option v-for="r in roles" :key="r.code" :label="r.name+'('+r.code+')'" :value="r.code" /></el-select>
        <el-tree v-if="grantRole" :data="menuTree" show-checkbox node-key="id"
          :default-checked-keys="checkedMenus" :props="{children:'children',label:'name'}" @check="onCheck"
          style="margin-top:12px;max-width:500px" />
        <el-button v-if="grantRole" type="primary" @click="saveRoleMenus" style="margin-top:12px">保存权限</el-button>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dlg" :title="editing.id?'编辑菜单':'新增菜单'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="editing.name" /></el-form-item>
        <el-form-item label="路径"><el-input v-model="editing.path" /></el-form-item>
        <el-form-item label="权限编码"><el-input v-model="editing.permissionCode" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="editing.type"><el-option label="菜单" value="menu" /><el-option label="按钮" value="button" /></el-select></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="editing.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg=false">取消</el-button><el-button type="primary" @click="saveMenu">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchAuth } from '../utils/auth'

const tab = ref('list')
const menus = ref<any[]>([])
const roles = ref<any[]>([])
const dlg = ref(false)
const editing = reactive<any>({})
const grantRole = ref('')
const checkedMenus = ref<number[]>([])
const allMenus = ref<any[]>([])

const load = () => fetchAuth('/api/menus').then(r => r.json()).then(d => menus.value = d)
const loadRoles = () => fetchAuth('/api/roles').then(r => r.json()).then(d => roles.value = d)
const loadAll = () => { load(); loadRoles() }

const buildTree = (list: any[]): any[] => list.filter(m => !m.parentId || m.parentId === 0).map(m => ({ ...m, children: buildTree(list.filter(c => c.parentId === m.id)) }))
const menuTree = computed(() => buildTree(allMenus.value))

const loadRoleMenus = () => {
  fetchAuth('/api/menus').then(r => r.json()).then(d => allMenus.value = d)
  fetchAuth('/api/menus/roles/' + grantRole.value).then(r => r.json()).then(d => checkedMenus.value = d)
}

const onCheck = (_: any, data: any) => { checkedMenus.value = [...data.checkedKeys, ...data.halfCheckedKeys] }

const saveRoleMenus = () => fetchAuth('/api/menus/roles/' + grantRole.value, { method: 'PUT', body: JSON.stringify(checkedMenus.value) }).then(() => ElMessage.success('已保存'))

const openCreate = () => { for(const k of Object.keys(editing)) delete editing[k]; editing.type='menu'; editing.sortOrder=0; dlg.value=true }
const openEdit = (row: any) => { Object.assign(editing, row); dlg.value=true }
const saveMenu = () => {
  const m = editing.id
    ? fetchAuth('/api/menus/'+editing.id, {method:'PUT', body:JSON.stringify(editing)})
    : fetchAuth('/api/menus', {method:'POST', body:JSON.stringify(editing)})
  m.then(load).then(() => { dlg.value=false; ElMessage.success('保存成功') })
}
const handleDelete = (id: number) => fetchAuth('/api/menus/'+id, {method:'DELETE'}).then(load)

loadAll()
</script>
<style scoped>.page{padding:20px}.toolbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}</style>
