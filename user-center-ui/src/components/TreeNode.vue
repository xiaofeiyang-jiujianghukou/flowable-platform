<template>
  <div>
    <div
      class="tn-row"
      :class="{ selected: selectedId === node.id }"
      :style="{ paddingLeft: (level - 1) * 20 + 'px' }"
      @click="$emit('select', node.id)"
    >
      <span class="tn-arrow" :class="{ hidden: !hasChildren }" @click.stop="toggle">
        {{ expanded ? '▾' : '▸' }}
      </span>
      <span class="tn-name">{{ node.name }}</span>
    </div>
    <TreeNode
      v-if="expanded && hasChildren"
      v-for="child in node.children"
      :key="child.id"
      :node="child"
      :selected-id="selectedId"
      :init-expanded="initExpanded"
      :manual-expanded="manualExpanded"
      :filter="filter"
      :level="level + 1"
      @select="id => $emit('select', id)"
      @toggle="id => $emit('toggle', id)"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  node: any
  selectedId: number | null
  initExpanded: Set<number>
  manualExpanded: Set<number>
  filter: string
  level: number
}>()

const emit = defineEmits<{ select: [id: number]; toggle: [id: number] }>()

const hasChildren = computed(() => props.node.children?.length > 0)

// 展开判定：策略初始展开 或 用户手动展开
const expanded = computed(() =>
  props.initExpanded.has(props.node.id) || props.manualExpanded.has(props.node.id)
)

const toggle = () => {
  if (!hasChildren.value) return
  emit('toggle', props.node.id)
}
</script>

<style scoped>
.tn-row { display: flex; align-items: center; gap: 4px; padding: 4px 8px; cursor: pointer; font-size: 14px; border-radius: 4px; }
.tn-row:hover { background: #f5f7fa; }
.tn-row.selected { color: #409eff; background: #ecf5ff; }
.tn-arrow { width: 16px; text-align: center; font-size: 12px; color: #909399; flex-shrink: 0; }
.tn-arrow.hidden { visibility: hidden; }
.tn-name { flex: 1; }
</style>
