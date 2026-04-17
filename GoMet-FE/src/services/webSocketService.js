import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { useAuthStore } from '@/stores/auth'

// 🚀 1. KHỞI TẠO ÂM THANH (PRE-LOAD)
const globalMsgSound = new Audio('/sounds/ting.mp3');
const notificationSound = new Audio('/sounds/notification.mp3');

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.connecting = false
    this.subscriptions = new Map()
  }

  connect() {
    const authStore = useAuthStore()

    if (!authStore.isAuthenticated || !authStore.user?.accountID) {
      console.warn('Cannot connect to WebSocket: User not authenticated')
      return
    }

    if (this.connected && this.stompClient) {
      console.log('WebSocket already connected')
      return
    }

    if (this.connecting) {
      console.log('WebSocket connection is already being established')
      return
    }

    try {
      this.connecting = true

      const socket = new SockJS('http://localhost:8080/ws')
      this.stompClient = Stomp.over(socket)
      this.stompClient.debug = () => {} // Tắt log debug của Stomp

      const headers = {}
      if (authStore.user?.token) {
        headers.Authorization = `Bearer ${authStore.user.token}`
      }

      this.stompClient.connect(
        headers,
        () => {
          console.log('WebSocket connected successfully')
          this.connected = true
          this.connecting = false
          this.setupSubscriptions()
        },
        (error) => {
          console.error('WebSocket connection error:', error)
          this.connected = false
          this.connecting = false
          this.stompClient = null

          setTimeout(() => {
            console.log('Retrying WebSocket connection...')
            this.connect()
          }, 5000)
        }
      )
    } catch (error) {
      console.error('Error creating WebSocket connection:', error)
      this.connected = false
      this.connecting = false
      this.stompClient = null
      setTimeout(() => this.connect(), 5000)
    }
  }

  disconnect() {
    try {
      this.subscriptions.forEach((subscription) => {
        try {
          subscription?.unsubscribe?.()
        } catch {}
      })
      this.subscriptions.clear()

      if (this.stompClient) {
        this.stompClient.disconnect(() => {
          console.log('WebSocket disconnected')
        })
      }
    } catch (error) {
      console.error('Error disconnecting WebSocket:', error)
    } finally {
      this.connected = false
      this.connecting = false
      this.stompClient = null
    }
  }

  setupSubscriptions() {
    const authStore = useAuthStore()
    const accountId = authStore.user?.accountID

    if (!accountId || !this.stompClient) {
      console.warn('No authenticated user, cannot setup subscriptions')
      return
    }

    // Delay nhỏ để đảm bảo connection đã ổn định
    setTimeout(() => {
      try {
        // 🔔 1. Thông báo người dùng cụ thể
        this.subscribe('notifications', `/topic/notifications/${accountId}`, (message) => {
          const notification = JSON.parse(message.body)
          this.handleNotification(notification)
        })

        // 🛡️ 2. Thông báo Admin cụ thể
        this.subscribe('admin-notifications', `/topic/admin-notifications/${accountId}`, (message) => {
          const notification = JSON.parse(message.body)
          this.handleAdminNotification(notification)
        })

        // 📢 3. Cảnh báo Admin toàn hệ thống (Broadcast)
        this.subscribe('admin-alerts', '/topic/admin-alerts', (message) => {
          const alert = JSON.parse(message.body)
          this.handleAdminAlert(alert)
        })

        // 💬 4. Kênh Chat toàn cầu (Báo âm thanh khi có tin nhắn mới)
        this.subscribe('global-chat', `/topic/global-chat/${accountId}`, (message) => {
          const chatData = JSON.parse(message.body)
          this.handleGlobalChatAlert(chatData)
        })

        console.log('All WebSocket subscriptions setup successfully')
      } catch (error) {
        console.error('Error during subscription setup:', error)
        setTimeout(() => this.setupSubscriptions(), 2000)
      }
    }, 200)
  }

  subscribe(key, destination, handler) {
    if (!this.stompClient) return

    try {
      // Unsubscribe cái cũ nếu tồn tại
      if (this.subscriptions.has(key)) {
        this.subscriptions.get(key).unsubscribe()
      }
    } catch (e) {
        console.warn(`Error unsubscribing key ${key}:`, e)
    }

    const subscription = this.stompClient.subscribe(destination, (message) => {
      try {
        handler(message)
      } catch (error) {
        console.error(`Error handling WebSocket message for ${destination}:`, error)
      }
    })

    this.subscriptions.set(key, subscription)
    console.log(`Subscribed to: ${destination}`)
  }

  handleNotification(notificationDTO) {
    this.playNotificationSound()
    window.dispatchEvent(new CustomEvent('realtime-notification', { detail: notificationDTO }))
  }

  handleAdminNotification(notificationDTO) {
    this.playNotificationSound()
    window.dispatchEvent(new CustomEvent('admin-notification', { detail: notificationDTO }))
  }

  handleAdminAlert(alertDTO) {
    this.playNotificationSound()
    window.dispatchEvent(new CustomEvent('admin-alert', { detail: alertDTO }))
  }

  handleGlobalChatAlert(chatData) {
    this.playChatSound()
    window.dispatchEvent(new CustomEvent('global-chat-alert', { detail: chatData }))
  }

  playNotificationSound() {
    notificationSound.currentTime = 0
    notificationSound.volume = 0.5
    notificationSound.play().catch(err => console.warn('Notification sound blocked:', err))
  }

  playChatSound() {
    globalMsgSound.currentTime = 0
    globalMsgSound.play().catch(err => console.warn('Chat sound blocked:', err))
  }

  send(destination, body = {}) {
    if (this.stompClient && this.connected) {
      this.stompClient.send(destination, {}, JSON.stringify(body))
    } else {
      console.warn('WebSocket not connected, cannot send message')
    }
  }

  isConnected() {
    return this.connected
  }
}

const webSocketService = new WebSocketService()
export default webSocketService
