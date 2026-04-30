<template>
  <div class="model-list-page">
    <el-tabs v-model="tab" @tab-change="load">
      <el-tab-pane v-for="t in tabs" :key="t" :label="MODEL_LABELS[t].name" :name="t" />
    </el-tabs>

    <div class="toolbar">
      <div>
        <h3>{{ MODEL_LABELS[tab].name }}</h3>
        <p class="hint">{{ MODEL_LABELS[tab].desc }}</p>
      </div>
      <el-button type="primary" @click="showCreate = true">+ 新建模型</el-button>
    </div>

    <el-dialog v-model="showCreate" title="新建模型" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="name" placeholder="如：请假审批" />
        </el-form-item>
        <el-form-item label="Key">
          <el-input v-model="key" placeholder="如：leave-approval" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :disabled="!name || !key">确定</el-button>
      </template>
    </el-dialog>

    <el-table :data="models" stripe height="calc(100vh - 200px)">
      <el-table-column prop="name" label="名称">
        <template #default="{ row }">
          <el-link type="primary" @click="$router.push(`/design/${row.id}`)">{{ row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="key" label="Key" width="180" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.deploymentId ? 'success' : 'info'" size="small">
            {{ row.deploymentId ? '已部署' : '未部署' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="version" label="版本" width="70">
        <template #default="{ row }"><el-tag size="small">v{{ row.version }}</el-tag></template>
      </el-table-column>
      <el-table-column label="更新时间" width="180">
        <template #default="{ row }">{{ new Date(row.lastUpdateTime).toLocaleString() }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/design/${row.id}`)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ModelInfo, ModelType } from '../types'
import { MODEL_LABELS } from '../types'
import { listModels, createModel, deleteModel } from '../api/client'

const tabs: ModelType[] = ['bpmn', 'cmmn', 'dmn']
const tab = ref<ModelType>('bpmn')
const models = ref<ModelInfo[]>([])
const showCreate = ref(false)
const name = ref('')
const key = ref('')

const load = () => listModels(tab.value).then(r => { models.value = r })
load()

const handleCreate = async () => {
  await createModel(name.value, key.value, tab.value)
  showCreate.value = false
  name.value = ''
  key.value = ''
  load()
}

const handleDelete = async (id: string) => {
  await deleteModel(id)
  load()
}
</script>

<style scoped>
.model-list-page { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin: 16px 0; }
.toolbar h3 { font-size: 18px; }
.hint { color: #999; font-size: 13px; margin-top: 4px; }
</style>
