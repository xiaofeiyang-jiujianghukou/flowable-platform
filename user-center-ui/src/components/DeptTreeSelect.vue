<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-start"
    :width="320"
    trigger="click"
    :show-arrow="false"
    :hide-after="0"
  >
    <template #reference>
      <el-input v-model="displayText" readonly :placeholder="placeholder" style="cursor:pointer">
        <template #suffix>
          <el-icon v-show="modelValue" class="clear-icon" @click.stop="clear"><CircleClose /></el-icon>
          <el-icon><ArrowDown /></el-icon>
        </template>
      </el-input>
    </template>
    <el-input v-model="filterText" placeholder="搜索部门" size="small" clearable style="margin-bottom:8px" />
    <div class="tree-scroll">
      <div v-for="node in filteredTree" :key="node.id">
        <TreeNode :node="node" :selected-id="modelValue" :init-expanded="expandedSet" :manual-expanded="manualExpanded" :filter="filterText" :level="1" @select="onSelect" @toggle="onToggle" />
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TreeNode from './TreeNode.vue'
import { CircleClose, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps<{ modelValue: number | null; placeholder?: string }>()
const emit = defineEmits<{ 'update:modelValue': [value: number | null] }>()

const visible = ref(false)
const filterText = ref('')
const depts = ref<any[]>([])
const manualExpanded = ref(new Set<number>())

onMounted(async () => {
  depts.value = await fetch('/api/departments').then(r => r.json())
})

const buildTree = (list: any[], parentId = 0): any[] =>
  list.filter(d => (d.parentId || 0) === parentId)
    .sort((a,b) => (a.sortOrder||0)-(b.sortOrder||0))
    .map(d => ({ ...d, children: buildTree(list, d.id) }))

const tree = computed(() => buildTree(depts.value))

const displayText = computed(() => {
  if (props.modelValue === null || props.modelValue === undefined) return ''
  if (props.modelValue === 0) return '根部门'
  const d = depts.value.find(x => x.id === props.modelValue)
  return d ? d.name : ''
})

// 已选时展开祖先链路（不含自身），未选时仅展开一级
const expandedSet = computed(() => {
  const s = new Set<number>()
  if (props.modelValue) {
    let d = depts.value.find(x => x.id === props.modelValue)
    if (d) d = depts.value.find(x => x.id === (d.parentId || 0))
    while (d) { s.add(d.id); d = depts.value.find(x => x.id === (d!.parentId || 0)) }
  } else {
    depts.value.filter(d => (d.parentId || 0) === 0).forEach(d => s.add(d.id))
  }
  return s
})

const filterTree = (list: any[], kw: string): any[] => {
  if (!kw) return list
  return list.reduce((acc: any[], d: any) => {
    const children = filterTree(d.children || [], kw)
    if (d.name.includes(kw) || children.length) acc.push({ ...d, children })
    return acc
  }, [])
}

const filteredTree = computed(() => filterTree(tree.value, filterText.value))
const clear = () => emit('update:modelValue', null)
const onSelect = (id: number) => { emit('update:modelValue', id); visible.value = false }
const onToggle = (id: number) => {
  const s = new Set(manualExpanded.value)
  s.has(id) ? s.delete(id) : s.add(id)
  manualExpanded.value = s
}
</script>

<style scoped>
.tree-scroll { max-height: 340px; overflow: auto; }
.clear-icon { cursor: pointer; color: #c0c4cc; }
.clear-icon:hover { color: #909399; }
</style>
