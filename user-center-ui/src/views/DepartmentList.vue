<template>
  <div class="page">
    <div class="toolbar">
      <h3>部门管理</h3>
      <el-button type="primary" @click="openCreate()">新增部门</el-button>
    </div>
    <el-tree
      :data="treeData"
      :props="{ children: 'children', label: 'name' }"
      node-key="id"
      default-expand-all
      highlight-current
      style="max-width:600px"
    >
      <template #default="{ data }">
        <span class="tree-node">
          <span class="node-name">{{ data.name }}</span>
          <span class="node-count">({{ data.userCount ?? 0 }}人)</span>
          <span class="node-actions">
            <el-button size="small" @click.stop="openCreate(data.id, data.name)">添加子部门</el-button>
            <el-button size="small" @click.stop="openEdit(data)">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="handleDelete(data.id)">删除</el-button>
          </span>
        </span>
      </template>
    </el-tree>

    <el-dialog v-model="visible" :title="dialogTitle" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="editing.name" placeholder="如：技术部" /></el-form-item>
        <el-form-item label="上级部门">
          <template v-if="parentFixed">
            <el-input :model-value="parentName" disabled />
          </template>
          <template v-else>
            <DeptTreeSelect v-model="editing.parentId" placeholder="根部门（不选则为顶级）" />
          </template>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="editing.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { fetchAuth } from '../utils/auth'
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import DeptTreeSelect from '../components/DeptTreeSelect.vue'

const API = '/api/departments'
const depts = ref<any[]>([])
const visible = ref(false)
const editing = reactive<any>({})
const parentFixed = ref(false)
const parentName = ref('')

const isEdit = computed(() => !!editing.id)
const dialogTitle = computed(() => isEdit.value ? '编辑部门' : '新增部门')

const load = () => fetchAuth(API).then(r => r.json()).then(d => { depts.value = d })

const buildTree = (list: any[], parentId = 0): any[] =>
  list.filter(d => (d.parentId || 0) === parentId)
    .map(d => ({ ...d, children: buildTree(list, d.id) }))

const treeData = computed(() => buildTree(depts.value))

// parentId=0 → 顶级新增（可选上级）；parentId>0 → 添加子部门（上级固定）
const openCreate = (parentId = 0, pName = '') => {
  for (const k of Object.keys(editing)) delete editing[k]
  editing.parentId = parentId
  editing.sortOrder = 0
  parentFixed.value = parentId > 0
  parentName.value = pName
  visible.value = true
}

const openEdit = (row: any) => {
  for (const k of Object.keys(editing)) delete editing[k]
  Object.assign(editing, row)
  parentFixed.value = false
  visible.value = true
}

const handleSave = () => {
  const body = { name: editing.name, parentId: editing.parentId || 0, sortOrder: editing.sortOrder || 0 }
  const m = editing.id
    ? fetchAuth(API + '/' + editing.id, { method: 'PUT', body: JSON.stringify(body) })
    : fetchAuth(API, { method: 'POST', body: JSON.stringify(body) })
  m.then(load).then(() => { visible.value = false; ElMessage.success('保存成功') })
}

const handleDelete = (id: number) => {
  fetchAuth(API + '/' + id, { method: 'DELETE' }).then(load).then(() => ElMessage.success('已删除'))
}

load()
</script>

<style scoped>
.page { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.tree-node { flex: 1; display: flex; align-items: center; gap: 8px; font-size: 14px; }
.node-name { font-weight: 500; }
.node-count { color: #999; font-size: 12px; }
.node-actions { margin-left: auto; display: flex; gap: 4px; }
</style>
