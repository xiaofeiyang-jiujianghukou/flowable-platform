import type { ModelInfo, ModelType } from '../types'

const BASE = '/api/models'

function api(path: string, init?: RequestInit) {
  return fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...init,
  }).then(r => {
    if (!r.ok) throw new Error(`${r.status}`)
    return r.json()
  })
}

export function listModels(type?: ModelType): Promise<ModelInfo[]> {
  const p = type ? `?type=${type}` : ''
  return api(p)
}

export function createModel(name: string, key: string, type: ModelType) {
  const params = new URLSearchParams({ name, key, type })
  return api(`?${params}`, { method: 'POST' })
}

export function deleteModel(id: string) {
  return api(`/${id}`, { method: 'DELETE' })
}

export function getSource(id: string): Promise<{ xml: string }> {
  return api(`/${id}/source`)
}

export function saveSource(id: string, xml: string) {
  return api(`/${id}/source`, { method: 'PUT', body: xml })
}

export function saveSourceExtra(id: string, svg: string) {
  return api(`/${id}/source-extra`, { method: 'PUT', body: svg })
}

export function deployModel(id: string) {
  return api(`/${id}/deploy`, { method: 'POST' })
}
