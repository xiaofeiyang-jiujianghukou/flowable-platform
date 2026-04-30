export type ModelType = 'bpmn' | 'cmmn' | 'dmn'

export interface ModelInfo {
  id: string
  name: string
  key: string
  category: string
  version: number
  deploymentId: string | null
  metaInfo: string | null
  createTime: string
  lastUpdateTime: string
  hasEditorSource: boolean
}

export const MODEL_LABELS: Record<ModelType, { name: string; desc: string }> = {
  bpmn: { name: '流程引擎 BPMN', desc: '业务流程建模：审批流、工单流转、任务分配' },
  cmmn: { name: '案例引擎 CMMN', desc: '案例管理建模：客户投诉、保险理赔、灵活应对' },
  dmn: { name: '决策引擎 DMN', desc: '业务规则决策表：自动定价、风险评级、资格判定' },
}
