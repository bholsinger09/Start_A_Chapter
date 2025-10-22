import { ref, onMounted, onUnmounted, readonly, computed } from 'vue'

// WebSocket connection state
const socket = ref(null)
const isConnected = ref(false)
const connectionError = ref(null)
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5

// Real-time data stores
const liveData = ref({
  activeUsers: 0,
  ongoingEvents: 0,
  newNotifications: [],
  systemHealth: 100,
  recentActivities: []
})

// Event listeners
const eventListeners = new Map()

export function useWebSocket() {
  const connect = () => {
    try {
      // Determine WebSocket URL based on environment
      const isProduction = typeof window !== 'undefined' && window.location.hostname.includes('onrender.com')
      const wsUrl = import.meta.env.VITE_WS_URL || 
                   (isProduction ? 'wss://chapter-organizer-backend.onrender.com/ws' : 'ws://localhost:8080/ws')
      
      socket.value = new WebSocket(wsUrl)
      
      socket.value.onopen = () => {
        console.log('✅ WebSocket connected')
        isConnected.value = true
        connectionError.value = null
        reconnectAttempts.value = 0
        
        // Send initial handshake
        sendMessage({
          type: 'HANDSHAKE',
          payload: {
            clientId: generateClientId(),
            timestamp: Date.now()
          }
        })
      }
      
      socket.value.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          handleMessage(data)
        } catch (error) {
          console.error('❌ Failed to parse WebSocket message:', error)
        }
      }
      
      socket.value.onclose = (event) => {
        console.log('🔌 WebSocket disconnected:', event.code, event.reason)
        isConnected.value = false
        
        // Auto-reconnect if not a clean close
        if (event.code !== 1000 && reconnectAttempts.value < maxReconnectAttempts) {
          setTimeout(() => {
            reconnectAttempts.value++
            console.log(`🔄 Reconnecting... Attempt ${reconnectAttempts.value}/${maxReconnectAttempts}`)
            connect()
          }, Math.pow(2, reconnectAttempts.value) * 1000) // Exponential backoff
        }
      }
      
      socket.value.onerror = (error) => {
        console.error('❌ WebSocket error:', error)
        connectionError.value = 'WebSocket connection failed'
      }
      
    } catch (error) {
      console.error('❌ Failed to create WebSocket connection:', error)
      connectionError.value = error.message
    }
  }
  
  const disconnect = () => {
    if (socket.value) {
      socket.value.close(1000, 'Client disconnect')
      socket.value = null
    }
  }
  
  const sendMessage = (message) => {
    if (socket.value && isConnected.value) {
      socket.value.send(JSON.stringify(message))
    } else {
      console.warn('⚠️ Cannot send message - WebSocket not connected')
    }
  }
  
  const handleMessage = (data) => {
    console.log('📨 Received WebSocket message:', data)
    
    switch (data.type) {
      case 'LIVE_DATA_UPDATE':
        updateLiveData(data.payload)
        break
        
      case 'NEW_NOTIFICATION':
        addNotification(data.payload)
        break
        
      case 'MEMBER_ACTIVITY':
        addRecentActivity(data.payload)
        break
        
      case 'SYSTEM_STATUS':
        updateSystemStatus(data.payload)
        break
        
      case 'EVENT_UPDATE':
        handleEventUpdate(data.payload)
        break
        
      default:
        console.log('🤷 Unknown message type:', data.type)
    }
    
    // Emit to registered listeners
    const listeners = eventListeners.get(data.type)
    if (listeners) {
      listeners.forEach(callback => callback(data.payload))
    }
  }
  
  const updateLiveData = (payload) => {
    Object.assign(liveData.value, payload)
  }
  
  const addNotification = (notification) => {
    liveData.value.newNotifications.unshift({
      ...notification,
      id: Date.now(),
      timestamp: new Date()
    })
    
    // Keep only last 50 notifications
    if (liveData.value.newNotifications.length > 50) {
      liveData.value.newNotifications = liveData.value.newNotifications.slice(0, 50)
    }
    
    // Show browser notification if permissions granted
    if (Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.message,
        icon: '/favicon.ico',
        tag: 'chapter-organizer'
      })
    }
  }
  
  const addRecentActivity = (activity) => {
    liveData.value.recentActivities.unshift({
      ...activity,
      timestamp: new Date()
    })
    
    // Keep only last 20 activities
    if (liveData.value.recentActivities.length > 20) {
      liveData.value.recentActivities = liveData.value.recentActivities.slice(0, 20)
    }
  }
  
  const updateSystemStatus = (status) => {
    liveData.value.systemHealth = status.health || 100
  }
  
  const handleEventUpdate = (eventData) => {
    // Emit custom event for components to listen to
    window.dispatchEvent(new CustomEvent('event-update', {
      detail: eventData
    }))
  }
  
  const generateClientId = () => {
    return 'client_' + Math.random().toString(36).substr(2, 9) + '_' + Date.now()
  }
  
  // Subscribe to specific message types
  const subscribe = (messageType, callback) => {
    if (!eventListeners.has(messageType)) {
      eventListeners.set(messageType, new Set())
    }
    eventListeners.get(messageType).add(callback)
    
    // Return unsubscribe function
    return () => {
      const listeners = eventListeners.get(messageType)
      if (listeners) {
        listeners.delete(callback)
      }
    }
  }
  
  // Request browser notification permissions
  const requestNotificationPermission = async () => {
    if ('Notification' in window) {
      const permission = await Notification.requestPermission()
      return permission === 'granted'
    }
    return false
  }
  
  // Send keep-alive ping
  const sendKeepAlive = () => {
    sendMessage({
      type: 'PING',
      payload: { timestamp: Date.now() }
    })
  }
  
  // Start keep-alive interval
  let keepAliveInterval = null
  const startKeepAlive = () => {
    keepAliveInterval = setInterval(sendKeepAlive, 30000) // 30 seconds
  }
  
  const stopKeepAlive = () => {
    if (keepAliveInterval) {
      clearInterval(keepAliveInterval)
      keepAliveInterval = null
    }
  }
  
  // Auto-connect on mount
  onMounted(() => {
    connect()
    startKeepAlive()
  })
  
  // Clean up on unmount
  onUnmounted(() => {
    stopKeepAlive()
    disconnect()
  })
  
  return {
    // Connection state
    isConnected: readonly(isConnected),
    connectionError: readonly(connectionError),
    reconnectAttempts: readonly(reconnectAttempts),
    
    // Live data
    liveData: readonly(liveData),
    
    // Methods
    connect,
    disconnect,
    sendMessage,
    subscribe,
    requestNotificationPermission,
    
    // Utility methods
    sendKeepAlive
  }
}

// Composable for real-time notifications
export function useRealTimeNotifications() {
  const { subscribe, liveData } = useWebSocket()
  
  const notifications = computed(() => liveData.value.newNotifications)
  
  const markAsRead = (notificationId) => {
    const notification = liveData.value.newNotifications.find(n => n.id === notificationId)
    if (notification) {
      notification.read = true
    }
  }
  
  const markAllAsRead = () => {
    liveData.value.newNotifications.forEach(n => n.read = true)
  }
  
  const unreadCount = computed(() => {
    return liveData.value.newNotifications.filter(n => !n.read).length
  })
  
  return {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead
  }
}

// Composable for real-time activity feed
export function useRealTimeActivity() {
  const { subscribe, liveData } = useWebSocket()
  
  const activities = computed(() => liveData.value.recentActivities)
  
  const getActivityIcon = (type) => {
    const icons = {
      'member_joined': 'bi-person-plus-fill',
      'event_created': 'bi-calendar-plus',
      'event_updated': 'bi-calendar-event',
      'member_updated': 'bi-person-gear',
      'chapter_created': 'bi-building-add',
      'login': 'bi-box-arrow-in-right',
      'logout': 'bi-box-arrow-right'
    }
    return icons[type] || 'bi-info-circle'
  }
  
  const getActivityColor = (type) => {
    const colors = {
      'member_joined': 'success',
      'event_created': 'primary',
      'event_updated': 'info',
      'member_updated': 'warning',
      'chapter_created': 'success',
      'login': 'success',
      'logout': 'secondary'
    }
    return colors[type] || 'secondary'
  }
  
  return {
    activities,
    getActivityIcon,
    getActivityColor
  }
}

// Composable for real-time metrics
export function useRealTimeMetrics() {
  const { liveData } = useWebSocket()
  
  const metrics = computed(() => ({
    activeUsers: liveData.value.activeUsers,
    ongoingEvents: liveData.value.ongoingEvents,
    systemHealth: liveData.value.systemHealth
  }))
  
  const isHealthy = computed(() => liveData.value.systemHealth >= 80)
  
  return {
    metrics,
    isHealthy
  }
}