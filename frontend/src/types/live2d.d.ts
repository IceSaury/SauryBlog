declare module 'pixi-live2d-display' {
  import * as PIXI from 'pixi.js'

  export class Live2DModel extends PIXI.Container {
    static from(source: string, options?: any): Promise<Live2DModel>
    
    internalModel?: {
      motionManager?: {
        autoUpdate: boolean
        definitions?: Record<string, any[]>
        startMotion(group: string, index: number, priority?: number): void
      }
      coreModel?: {
        addParameterValueById(id: string, value: number): void
      }
    }
    
    anchor: PIXI.ObservablePoint
    x: number
    y: number
    width: number
    height: number
    visible: boolean
    
    on(event: string, fn: (hitAreas: string[]) => void): this
    destroy(): void
  }
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

