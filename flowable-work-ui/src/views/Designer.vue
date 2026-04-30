<template>
  <div class="designer-page">
    <div class="toolbar">
      <el-button @click="$router.push('/')">← 返回列表</el-button>
      <span class="model-name">{{ modelName }}</span>
      <el-tag :type="deployed ? 'success' : 'info'" size="small">
        {{ deployed ? '已部署' : '未部署' }}
      </el-tag>
      <div style="display:flex;gap:8px">
        <el-button @click="handleSave">保存</el-button>
        <el-button type="primary" @click="handleDeploy">部署</el-button>
        <el-tag v-if="msg" :type="msgType">{{ msg }}</el-tag>
      </div>
    </div>
    <div ref="container" class="canvas" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getSource, saveSource, saveSourceExtra, deployModel } from '../api/client'

// bpmn.io 核心样式（工具栏、属性面板、上下文菜单依赖这些 CSS）
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'

import 'cmmn-js/dist/assets/cmmn-font/css/cmmn.css'

import 'dmn-js/dist/assets/dmn-js-drd.css'
import 'dmn-js/dist/assets/dmn-js-decision-table.css'
import 'dmn-js/dist/assets/dmn-js-shared.css'
import 'dmn-js/dist/assets/dmn-js-decision-table-controls.css'
import 'dmn-js/dist/assets/dmn-js-literal-expression.css'
import 'dmn-js/dist/assets/dmn-font/css/dmn.css'

const props = defineProps<{ id: string }>()

const container = ref<HTMLDivElement>()
const modelName = ref('')
const deployed = ref(false)
const msg = ref('')
const msgType = ref<'success' | 'danger'>('success')
let modeler: any = null

function showMsg(text: string, type: 'success' | 'danger' = 'success') {
  msg.value = text
  msgType.value = type
  setTimeout(() => msg.value = '', 5000)
}

onMounted(async () => {
  await nextTick()

  const modelInfo = await fetch(`/api/models/${props.id}`).then(r => r.json())
  modelName.value = modelInfo.name
  deployed.value = !!modelInfo.deploymentId
  const type = modelInfo.category || 'bpmn'

  let ModelerClass: any
  if (type === 'cmmn') ModelerClass = (await import('cmmn-js/lib/Modeler')).default
  else if (type === 'dmn') ModelerClass = (await import('dmn-js/lib/Modeler')).default
  else ModelerClass = (await import('bpmn-js/lib/Modeler')).default

  modeler = new ModelerClass({ container: container.value! })

  const { xml } = await getSource(props.id)
  if (xml) {
    await modeler.importXML(xml)
  } else if (type === 'dmn') {
    // DMN 需要 DI 元素才会显示 DRD 视图（可拖拽组件面板）
    await modeler.importXML(`<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="https://www.omg.org/spec/DMN/20191111/MODEL/"
  xmlns:dmndi="https://www.omg.org/spec/DMN/20191111/DMNDI/"
  xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/"
  id="definitions" namespace="http://dmn.io/schema/dmn">
  <dmndi:DMNDI>
    <dmndi:DMNDiagram id="DMNDiagram_1" />
  </dmndi:DMNDI>
</definitions>`)
  } else {
    modeler.createDiagram()
  }
})

onUnmounted(() => { modeler?.destroy() })

const handleSave = async () => {
  try {
    const { xml } = await modeler.saveXML({ format: true })
    const { svg } = await modeler.saveSVG()
    await saveSource(props.id, xml!)
    await saveSourceExtra(props.id, svg!)
    showMsg('已保存')
  } catch (e: any) {
    showMsg('保存失败: ' + e.message, 'danger')
  }
}

const handleDeploy = async () => {
  try {
    await handleSave()
    const result = await deployModel(props.id)
    deployed.value = true
    showMsg('已部署: ' + (result.name || ''))
  } catch (e: any) {
    showMsg('部署失败: ' + e.message, 'danger')
  }
}
</script>

<style scoped>
.designer-page { display: flex; flex-direction: column; height: 100%; }
.toolbar { height: 44px; display: flex; align-items: center; gap: 12px; padding: 0 12px;
  background: #fafafa; border-bottom: 1px solid #e8e8e8; flex-shrink: 0; }
.model-name { font-weight: 600; font-size: 14px; }
.canvas { flex: 1; }
</style>
