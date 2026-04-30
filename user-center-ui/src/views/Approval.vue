<template>
  <div class="page"><el-tabs v-model="tab">
    <el-tab-pane label="发起审批" name="start" />
    <el-tab-pane label="待审批" name="pending" />
    <el-tab-pane label="已审批" name="history" />
  </el-tabs>

  <div v-if="tab==='start'">
    <el-empty v-if="!configs.length" description="暂无已发布流程" />
    <el-card v-for="c in configs" :key="c.id" class="card">
      <template #header><span>{{ c.name }}</span><el-tag size="small" style="margin-left:8px">{{ c.processName }}</el-tag></template>
      <el-form label-width="80px">
        <el-form-item label="发起人"><el-input v-model="startForm[c.id].initiator" /></el-form-item>
        <el-form-item label="请假天数"><el-input-number v-model="startForm[c.id].leaveDays" :min="1" /></el-form-item>
        <el-form-item label="理由"><el-input v-model="startForm[c.id].reason" type="textarea" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleStart(c.id)">提交申请</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>

  <div v-if="tab==='pending'">
    <el-empty v-if="!pending.length" description="暂无待审批" />
    <el-table :data="pending"><el-table-column prop="name" label="任务" /><el-table-column prop="assignee" label="处理人" width="120" />
      <el-table-column label="操作" width="240"><template #default="{row}"><el-input v-model="comments[row.id]" placeholder="意见" size="small" style="width:100px" />
        <el-button size="small" type="success" @click="approve(row.id,true)">通过</el-button>
        <el-button size="small" type="danger" @click="approve(row.id,false)">驳回</el-button></template></el-table-column></el-table>
  </div>

  <div v-if="tab==='history'"><el-table :data="history"><el-table-column prop="name" label="任务" /><el-table-column prop="assignee" label="处理人" width="120" />
    <el-table-column label="耗时(秒)" width="100"><template #default="{row}">{{ (row.durationInMillis/1000).toFixed(1) }}</template></el-table-column></el-table>
  </div></div>
</template>

<script setup lang="ts">
import { fetchAuth } from '../utils/auth'
import { ref, reactive, watch } from 'vue'; import { ElMessage } from 'element-plus'
const tab = ref('start'); const configs = ref<any[]>([]); const pending = ref<any[]>([]); const history = ref<any[]>([])
const comments = reactive<Record<string,string>>({}); const startForm = reactive<Record<number,any>>({})
const loadConfigs = () => fetchAuth('/api/approval/published-configs').then(r=>r.json()).then(d=>{configs.value=d;d.forEach((c:any)=>{if(!startForm[c.id])startForm[c.id]={initiator:'当前用户',leaveDays:1,reason:''}})})
const loadPending = () => fetchAuth('/api/approval/pending').then(r=>r.json()).then(d=>pending.value=Array.isArray(d)?d:[])
const loadHistory = () => fetchAuth('/api/approval/history').then(r=>r.json()).then(d=>history.value=Array.isArray(d)?d:[])
const handleStart = (id:number) => { const f=startForm[id];fetch('/api/approval/start?configId='+id+'&initiator='+(f.initiator||'当前用户'),{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({leaveDays:f.leaveDays,reason:f.reason})}).then(()=>ElMessage.success('已提交')) }
const approve = (taskId:string,approved:boolean) => { fetchAuth('/api/approval/'+taskId+'/'+(approved?'approve':'reject'),{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({comment:comments[taskId]||''})}).then(loadPending).then(()=>ElMessage.success(approved?'通过':'驳回')) }
watch(tab,v=>{if(v==='start')loadConfigs();if(v==='pending')loadPending();if(v==='history')loadHistory()})
loadConfigs()
</script>
<style scoped>.page{padding:20px}.card{margin-bottom:16px}</style>
