import request from '../utils/request'

export interface ChatRequest {
  message: string
  sessionId?: string
}

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  timestamp: number
}

export interface ChatHistory {
  sessionId: string
  messages: ChatMessage[]
}

/**
 * 发送消息 - 流式响应（简化版本）
 */
export async function sendMessageStream(
  data: ChatRequest,
  onMessage: (content: string) => void,
  onDone: () => void,
  onError: (error: string) => void
) {
  const token = localStorage.getItem('token')
  const baseURL = 'http://localhost:8088'
  
  try {
    // 使用 URLSearchParams 构建查询参数
    const params = new URLSearchParams({
      message: data.message
    })
    if (data.sessionId) {
      params.append('sessionId', data.sessionId)
    }
    
    // 确保token格式正确
    let authHeader = token || ''
    if (token && !token.startsWith('Bearer ')) {
      authHeader = `Bearer ${token}`
    }
    
    const response = await fetch(`${baseURL}/api/chat/send/stream?${params}`, {
      method: 'POST',
      headers: {
        'Authorization': authHeader
      }
    })
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const reader = response.body?.getReader()
    const decoder = new TextDecoder()
    
    if (!reader) {
      onError('无法读取响应流')
      return
    }
    
    // 读取文本流
    while (true) {
      const { done, value } = await reader.read()
      
      if (done) {
        onDone()
        break
      }
      
      // 解码并发送每个chunk
      const chunk = decoder.decode(value, { stream: true })
      if (chunk) {
        onMessage(chunk)
      }
    }
  } catch (error: any) {
    console.error('流式请求失败', error)
    onError(error.message || '发送失败')
  }
}

/**
 * 获取聊天历史
 */
export function getChatHistory(sessionId: string) {
  return request<ChatHistory>({
    url: `/chat/history/${sessionId}`,
    method: 'get'
  })
}

/**
 * 清除聊天历史
 */
export function clearChatHistory(sessionId: string) {
  return request<void>({
    url: `/chat/history/${sessionId}`,
    method: 'delete'
  })
}

/**
 * 获取用户的所有会话列表
 */
export function getUserSessions() {
  return request<string[]>({
    url: '/chat/sessions',
    method: 'get'
  })
}

