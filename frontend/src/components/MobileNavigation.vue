<template>
  <div class="mobile-navigation">
    <!-- Mobile Header -->
    <div class="mobile-header d-md-none">
      <div class="container-fluid">
        <div class="d-flex justify-content-between align-items-center py-3">
          <div class="mobile-logo">
            <i class="bi bi-mortarboard-fill text-primary me-2"></i>
            <span class="fw-bold">Chapter</span>
          </div>
          
          <div class="d-flex align-items-center">
            <!-- Notifications -->
            <div class="position-relative me-3">
              <button class="btn btn-link p-1 text-muted" @click="showNotifications = !showNotifications">
                <i class="bi bi-bell fs-5"></i>
                <span v-if="notificationCount" class="notification-badge">{{ notificationCount }}</span>
              </button>
            </div>
            
            <!-- Menu Toggle -->
            <button 
              class="btn btn-link p-1 text-muted"
              @click="toggleMobileMenu"
            >
              <i class="bi" :class="showMobileMenu ? 'bi-x-lg' : 'bi-list'" style="font-size: 1.5rem;"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Mobile Menu Overlay -->
    <div 
      v-if="showMobileMenu"
      class="mobile-menu-overlay d-md-none"
      @click="showMobileMenu = false"
    ></div>

    <!-- Mobile Menu -->
    <div 
      class="mobile-menu d-md-none"
      :class="{ 'show': showMobileMenu }"
    >
      <!-- User Profile Section -->
      <div v-if="currentUser" class="user-profile-section">
        <div class="d-flex align-items-center mb-3">
          <div class="user-avatar me-3">
            <i class="bi bi-person-circle text-primary" style="font-size: 3rem;"></i>
          </div>
          <div>
            <div class="fw-semibold">{{ currentUser.username }}</div>
            <div class="text-muted small">{{ currentUser.role }}</div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions mb-4">
        <div class="row g-2">
          <div class="col-6">
            <button class="btn btn-primary btn-sm w-100" @click="navigateTo('/members/create')">
              <i class="bi bi-person-plus me-1"></i>
              Add Member
            </button>
          </div>
          <div class="col-6">
            <button class="btn btn-success btn-sm w-100" @click="navigateTo('/events/create')">
              <i class="bi bi-calendar-plus me-1"></i>
              New Event
            </button>
          </div>
        </div>
      </div>

      <!-- Navigation Links -->
      <nav class="mobile-nav">
        <router-link 
          v-for="item in navigationItems" 
          :key="item.path"
          :to="item.path"
          class="mobile-nav-item"
          :class="{ active: $route.path === item.path }"
          @click="showMobileMenu = false"
        >
          <i class="bi" :class="item.icon"></i>
          <span>{{ item.label }}</span>
          <span v-if="item.badge" class="badge bg-danger ms-auto">{{ item.badge }}</span>
        </router-link>
      </nav>

      <!-- Settings Section -->
      <div class="settings-section mt-4">
        <div class="section-divider"></div>
        
        <!-- Theme Toggle -->
        <div class="setting-item">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <i class="bi bi-palette me-3"></i>
              <span>Dark Mode</span>
            </div>
            <div class="form-check form-switch">
              <input 
                class="form-check-input" 
                type="checkbox" 
                :checked="isDarkMode"
                @change="toggleTheme"
              >
            </div>
          </div>
        </div>

        <!-- Notifications Toggle -->
        <div class="setting-item">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <i class="bi bi-bell me-3"></i>
              <span>Notifications</span>
            </div>
            <div class="form-check form-switch">
              <input 
                class="form-check-input" 
                type="checkbox" 
                v-model="notificationsEnabled"
              >
            </div>
          </div>
        </div>

        <!-- Other Settings -->
        <router-link to="/settings" class="setting-item" @click="showMobileMenu = false">
          <i class="bi bi-gear me-3"></i>
          <span>Settings</span>
          <i class="bi bi-chevron-right ms-auto"></i>
        </router-link>

        <router-link to="/help" class="setting-item" @click="showMobileMenu = false">
          <i class="bi bi-question-circle me-3"></i>
          <span>Help & Support</span>
          <i class="bi bi-chevron-right ms-auto"></i>
        </router-link>

        <!-- Logout -->
        <div v-if="currentUser" class="setting-item logout-item" @click="handleLogout">
          <i class="bi bi-box-arrow-right me-3 text-danger"></i>
          <span class="text-danger">Logout</span>
        </div>
      </div>
    </div>

    <!-- Bottom Navigation (Tab Bar) -->
    <div class="bottom-navigation d-md-none">
      <div class="container-fluid p-0">
        <div class="row g-0">
          <div 
            v-for="item in bottomNavItems" 
            :key="item.path"
            class="col"
          >
            <router-link 
              :to="item.path"
              class="bottom-nav-item"
              :class="{ active: $route.path === item.path }"
            >
              <i class="bi" :class="item.icon"></i>
              <span class="nav-label">{{ item.label }}</span>
              <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Notification Panel -->
    <div 
      v-if="showNotifications"
      class="notification-panel d-md-none"
      @click.self="showNotifications = false"
    >
      <div class="notification-content">
        <div class="notification-header">
          <h6 class="mb-0">Notifications</h6>
          <button class="btn btn-link p-0 text-muted" @click="showNotifications = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        
        <div class="notification-list">
          <div 
            v-for="notification in notifications" 
            :key="notification.id"
            class="notification-item"
            :class="{ unread: !notification.read }"
          >
            <div class="notification-icon">
              <i class="bi" :class="notification.icon"></i>
            </div>
            <div class="notification-content">
              <div class="notification-title">{{ notification.title }}</div>
              <div class="notification-text">{{ notification.message }}</div>
              <div class="notification-time">{{ formatTime(notification.time) }}</div>
            </div>
          </div>
          
          <div v-if="notifications.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-bell-slash display-4 mb-3"></i>
            <p>No notifications</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTheme } from '../composables/useTheme'

export default {
  name: 'MobileNavigation',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const { currentTheme, setTheme } = useTheme()
    
    const showMobileMenu = ref(false)
    const showNotifications = ref(false)
    const notificationsEnabled = ref(true)
    
    // Mock user data
    const currentUser = ref({
      username: 'John Doe',
      role: 'Chapter President'
    })
    
    // Navigation items
    const navigationItems = ref([
      { path: '/', label: 'Dashboard', icon: 'bi-speedometer2' },
      { path: '/chapters', label: 'Chapters', icon: 'bi-building' },
      { path: '/members', label: 'Members', icon: 'bi-people-fill', badge: 5 },
      { path: '/events', label: 'Events', icon: 'bi-calendar-event' },
      { path: '/analytics', label: 'Analytics', icon: 'bi-graph-up' },
      { path: '/reports', label: 'Reports', icon: 'bi-file-earmark-bar-graph' }
    ])
    
    // Bottom navigation items (most used)
    const bottomNavItems = ref([
      { path: '/', label: 'Home', icon: 'bi-house-fill' },
      { path: '/chapters', label: 'Chapters', icon: 'bi-building' },
      { path: '/members', label: 'Members', icon: 'bi-people-fill' },
      { path: '/events', label: 'Events', icon: 'bi-calendar-event', badge: 3 }
    ])
    
    // Mock notifications
    const notifications = ref([
      {
        id: 1,
        title: 'New Member Request',
        message: 'Sarah Johnson wants to join UC Berkeley chapter',
        icon: 'bi-person-plus-fill',
        time: new Date(Date.now() - 5 * 60000),
        read: false
      },
      {
        id: 2,
        title: 'Event Reminder',
        message: 'Weekly meeting starts in 30 minutes',
        icon: 'bi-calendar-event',
        time: new Date(Date.now() - 30 * 60000),
        read: false
      },
      {
        id: 3,
        title: 'System Update',
        message: 'New features have been added to the dashboard',
        icon: 'bi-info-circle-fill',
        time: new Date(Date.now() - 2 * 60 * 60000),
        read: true
      }
    ])
    
    // Computed
    const notificationCount = computed(() => {
      return notifications.value.filter(n => !n.read).length
    })
    
    const isDarkMode = computed(() => currentTheme.value === 'dark')
    
    // Methods
    const toggleMobileMenu = () => {
      showMobileMenu.value = !showMobileMenu.value
      if (showMobileMenu.value) {
        showNotifications.value = false
      }
    }
    
    const navigateTo = (path) => {
      router.push(path)
      showMobileMenu.value = false
    }
    
    const toggleTheme = () => {
      const newTheme = currentTheme.value === 'dark' ? 'light' : 'dark'
      setTheme(newTheme)
    }
    
    const handleLogout = () => {
      // Implement logout logic
      currentUser.value = null
      showMobileMenu.value = false
      router.push('/login')
    }
    
    const formatTime = (time) => {
      const now = new Date()
      const diff = now - time
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(minutes / 60)
      const days = Math.floor(hours / 24)
      
      if (minutes < 1) return 'Just now'
      if (minutes < 60) return `${minutes}m ago`
      if (hours < 24) return `${hours}h ago`
      return `${days}d ago`
    }
    
    // Close mobile menu on route change
    router.afterEach(() => {
      showMobileMenu.value = false
      showNotifications.value = false
    })
    
    return {
      showMobileMenu,
      showNotifications,
      notificationsEnabled,
      currentUser,
      navigationItems,
      bottomNavItems,
      notifications,
      notificationCount,
      isDarkMode,
      toggleMobileMenu,
      navigateTo,
      toggleTheme,
      handleLogout,
      formatTime
    }
  }
}
</script>

<style scoped>
/* Mobile Header */
.mobile-header {
  background: white;
  border-bottom: 1px solid #dee2e6;
  position: sticky;
  top: 0;
  z-index: 1030;
}

.mobile-logo {
  font-size: 1.1rem;
  font-weight: 600;
}

.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #dc3545;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Mobile Menu */
.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1040;
}

.mobile-menu {
  position: fixed;
  top: 0;
  right: -100%;
  width: 320px;
  max-width: 85vw;
  height: 100vh;
  background: white;
  z-index: 1050;
  transition: right 0.3s ease-in-out;
  overflow-y: auto;
  padding: 2rem 1.5rem;
}

.mobile-menu.show {
  right: 0;
}

.user-profile-section {
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-actions {
  margin-bottom: 1.5rem;
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 0;
  text-decoration: none;
  color: #495057;
  border-bottom: 1px solid #f8f9fa;
  transition: color 0.2s;
}

.mobile-nav-item:hover,
.mobile-nav-item.active {
  color: #0d6efd;
  text-decoration: none;
}

.mobile-nav-item i {
  width: 24px;
  margin-right: 1rem;
}

.section-divider {
  height: 1px;
  background: #dee2e6;
  margin: 1rem 0;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 0;
  text-decoration: none;
  color: #495057;
  cursor: pointer;
  transition: color 0.2s;
}

.setting-item:hover {
  color: #0d6efd;
  text-decoration: none;
}

.logout-item:hover {
  color: #dc3545 !important;
}

/* Bottom Navigation */
.bottom-navigation {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #dee2e6;
  z-index: 1020;
  padding: 0.5rem 0;
}

.bottom-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0.5rem;
  text-decoration: none;
  color: #6c757d;
  transition: color 0.2s;
  position: relative;
}

.bottom-nav-item:hover,
.bottom-nav-item.active {
  color: #0d6efd;
  text-decoration: none;
}

.bottom-nav-item i {
  font-size: 1.2rem;
  margin-bottom: 0.25rem;
}

.nav-label {
  font-size: 0.7rem;
  font-weight: 500;
}

.nav-badge {
  position: absolute;
  top: 0.25rem;
  right: 0.75rem;
  background: #dc3545;
  color: white;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  font-size: 0.6rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Notification Panel */
.notification-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1060;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 4rem;
}

.notification-content {
  background: white;
  width: 90%;
  max-width: 400px;
  border-radius: 12px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.notification-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: between;
  align-items: center;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
}

.notification-item {
  display: flex;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f8f9fa;
}

.notification-item.unread {
  background: #f8f9fa;
  border-left: 3px solid #0d6efd;
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e3f2fd;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 0.75rem;
  flex-shrink: 0;
}

.notification-icon i {
  color: #0d6efd;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.notification-text {
  font-size: 0.8rem;
  color: #6c757d;
  line-height: 1.3;
  margin-bottom: 0.25rem;
}

.notification-time {
  font-size: 0.7rem;
  color: #adb5bd;
}

/* Dark Mode */
[data-theme="dark"] .mobile-header,
[data-theme="dark"] .mobile-menu,
[data-theme="dark"] .bottom-navigation,
[data-theme="dark"] .notification-content {
  background: var(--bs-dark);
  color: var(--bs-light);
}

[data-theme="dark"] .mobile-header,
[data-theme="dark"] .bottom-navigation {
  border-color: var(--bs-gray-700);
}

[data-theme="dark"] .mobile-nav-item,
[data-theme="dark"] .setting-item {
  color: var(--bs-gray-300);
}

[data-theme="dark"] .mobile-nav-item:hover,
[data-theme="dark"] .mobile-nav-item.active,
[data-theme="dark"] .bottom-nav-item:hover,
[data-theme="dark"] .bottom-nav-item.active {
  color: var(--bs-primary);
}

[data-theme="dark"] .notification-item.unread {
  background: rgba(255, 255, 255, 0.05);
}

/* Responsive adjustments */
@media (max-width: 576px) {
  .mobile-menu {
    width: 100%;
    max-width: 100%;
  }
  
  .bottom-nav-item {
    padding: 0.25rem;
  }
  
  .bottom-nav-item i {
    font-size: 1.1rem;
  }
  
  .nav-label {
    font-size: 0.65rem;
  }
}

/* Add padding to body to account for bottom navigation */
@media (max-width: 768px) {
  body {
    padding-bottom: 70px;
  }
}
</style>