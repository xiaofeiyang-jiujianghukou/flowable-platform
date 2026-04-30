<template>
  <div class="page"><div class="toolbar"><h3>流程配置</h3><el-button type="primary" @click="showCreate=true">新建配置</el-button></div>
    <el-dialog v-model="showCreate" title="新建流程配置" width="420px">
      <el-form label-width="80px"><el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="流程定义"><el-select v-model="form.processDefinitionKey" placeholder="选择已部署流程" style="width:100%">
          <el-option v-for="d in definitions" :key="d.key" :label="(d.name || d.key) + ' (' + d.key + ')'" :value="d.key" /></el-select></el-form-item></el-form>
      <template #footer><el-button @click="showCreate=false">取消</el-button><el-button type="primary" @click="handleCreate">确定</el-button></template>
    </el-dialog>
    <el-dialog v-model="showAssignees" title="配置任务处理人" width="550px">
      <el-form v-for="(t,i) in editingAssignees" :key="i" label-width="100px" style="margin-bottom:16px">
        <el-divider content-position="left">{{ t.taskName }}</el-divider>
        <el-form-item label="处理人">
          <el-select v-model="t.assignee" filterable remote :remote-method="searchUsers" placeholder="搜索用户" style="width:100%">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.name + ' (' + u.username + ')'" :value="u.username" /></el-select></el-form-item>
        <el-form-item label="候选组"><el-input v-model="t.candidateGroupsStr" placeholder="逗号分隔，如lineManager" /></el-form-item></el-form>
      <template #footer><el-button @click="showAssignees=false">取消</el-button><el-button type="primary" @click="handleSaveAssignees">保存</el-button></template>
    </el-dialog>
    <el-table :data="configs" stripe>
      <el-table-column prop="name" label="名称" /><el-table-column prop="processName" label="流程" />
      <el-table-column prop="processDefinitionKey" label="Key" width="150" />
      <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="row.published?'success':'info'" size="small">{{ row.published ? '已发布' : '未发布' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="280"><template #default="{row}">
        <el-button size="small" @click="openAssignees(row)">配置人员</el-button>
        <el-button v-if="!row.published" size="small" type="success" @click="togglePublish(row, true)">发布</el-button>
        <el-button v-else size="small" type="warning" @click="togglePublish(row, false)">取消发布</el-button>
        <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { fetchAuth } from '../utils/auth'
import { ref, reactive, watch } from 'vue'; import { ElMessage } from 'element-plus'
const configs = ref([]); const definitions = ref<any[]>([]); const showCreate = ref(false); const showAssignees = ref(false)
const form = reactive({ name: '', processDefinitionKey: '' }); const editingAssignees = ref<any[]>([]); const editingConfigId = ref(0)
const userOptions = ref<any[]>([])
const load = () => fetchAuth('/api/process-configs').then(r => r.json()).then(d => configs.value = d)
const loadDefs = () => fetchAuth('/api/process-configs/definitions').then(r => r.json()).then(d => definitions.value = d)
const handleCreate = () => fetchAuth('/api/process-configs?processDefinitionKey=' + form.processDefinitionKey + '&name=' + form.name, { method: 'POST' }).then(load).then(() => { showCreate.value = false; ElMessage.success('创建成功') })
const handleDelete = (id: number) => fetchAuth('/api/process-configs/' + id, { method: 'DELETE' }).then(load)
const togglePublish = (row: any, pub: boolean) => fetchAuth('/api/process-configs/' + row.id + '/' + (pub ? 'publish' : 'unpublish'), { method: 'POST' }).then(load)
const openAssignees = (row: any) => { editingConfigId.value = row.id; editingAssignees.value = JSON.parse(JSON.stringify((row.taskAssignees ? JSON.parse(row.taskAssignees) : []).map((t: any) => ({ ...t, candidateGroupsStr: (t.candidateGroups || []).join(',') })))); showAssignees.value = true }
const handleSaveAssignees = () => {
  const data = editingAssignees.value.map((t: any) => ({ taskId: t.taskId, taskName: t.taskName, assignee: t.assignee, candidateGroups: t.candidateGroupsStr ? t.candidateGroupsStr.split(',').map((s: string) => s.trim()) : [] }))
  fetchAuth('/api/process-configs/' + editingConfigId.value + '/assignees', { method: 'PUT', body: JSON.stringify(data) }).then(() => { showAssignees.value = false; ElMessage.success('已保存') })
}
const searchUsers = (q: string) => fetchAuth('/api/users?keyword=' + q).then(r => r.json()).then(d => userOptions.value = d)

watch(showCreate, (v) => { if (v) loadDefs() })

load(); loadDefs()
</script>
<style scoped>.page{padding:20px}.toolbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}</style>
