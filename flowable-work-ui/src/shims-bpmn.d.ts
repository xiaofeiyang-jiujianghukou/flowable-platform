declare module 'bpmn-js/lib/Modeler' {
  export default class BpmnModeler {
    constructor(options: { container: HTMLElement })
    importXML(xml: string): Promise<{ warnings: string[] }>
    saveXML(options?: { format?: boolean }): Promise<{ xml?: string }>
    saveSVG(): Promise<{ svg?: string }>
    destroy(): void
  }
}

declare module 'cmmn-js/lib/Modeler' {
  export default class CmmnModeler {
    constructor(options: { container: HTMLElement })
    importXML(xml: string): Promise<{ warnings: string[] }>
    saveXML(options?: { format?: boolean }): Promise<{ xml?: string }>
    saveSVG(): Promise<{ svg?: string }>
    destroy(): void
  }
}

declare module 'dmn-js/lib/Modeler' {
  export default class DmnModeler {
    constructor(options: { container: HTMLElement })
    importXML(xml: string): Promise<{ warnings: string[] }>
    saveXML(options?: { format?: boolean }): Promise<{ xml?: string }>
    saveSVG(): Promise<{ svg?: string }>
    destroy(): void
  }
}
