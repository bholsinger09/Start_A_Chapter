<template>
  <div id="app">
    <!-- PWA Install Prompt -->
    <PWAInstaller />
    
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container">
        <router-link class="navbar-brand" to="/">
          <i class="bi bi-mortarboard-fill me-2"></i>
          Campus Chapter Organizer
        </router-link>
        
        <button 
          class="navbar-toggler" 
          type="button" 
          data-bs-toggle="collapse" 
          data-bs-target="#navbarNav"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/" exact-active-class="active">
                <i class="bi bi-speedometer2 me-1"></i>Dashboard
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chapters" active-class="active">
                <i class="bi bi-building me-1"></i>Chapters
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/members" active-class="active">
                <i class="bi bi-people-fill me-1"></i>Members
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/events" active-class="active">
                <i class="bi bi-calendar-event me-1"></i>Events
              </router-link>
            </li>
            
            <!-- Blog link - only visible when logged in -->
            <li v-if="currentUser" class="nav-item">
              <router-link class="nav-link" to="/blog" active-class="active">
                <i class="bi bi-journal-text me-1"></i>Blog
              </router-link>
            </li>
            
            <!-- About link - always visible -->
            <li class="nav-item">
              <router-link class="nav-link" to="/about" active-class="active">
                <i class="bi bi-info-circle me-1"></i>About
              </router-link>
            </li>
            
            <!-- Show login/register or user info based on authentication state -->
            <li v-if="!currentUser" class="nav-item">
              <router-link class="nav-link" to="/login" active-class="active">
                <i class="bi bi-box-arrow-in-right me-1"></i>Login
              </router-link>
            </li>
            <li v-if="!currentUser" class="nav-item">
              <router-link class="nav-link" to="/register" active-class="active">
                <i class="bi bi-person-plus me-1"></i>Register
              </router-link>
            </li>
            
            <!-- User info dropdown when logged in -->
            <li v-if="currentUser" class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                <i class="bi bi-person-circle me-1"></i>{{ currentUser.username }}
              </a>
              <ul class="dropdown-menu">
                <li><h6 class="dropdown-header">Welcome, {{ currentUser.username }}!</h6></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" @click="logout">
                  <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a></li>
              </ul>
            </li>
            
            <!-- Theme Toggle -->
            <li class="nav-item ms-2">
              <ThemeToggle />
            </li>
            
            <!-- Simple Theme Toggle for Testing -->
            <li class="nav-item ms-2">
              <SimpleThemeToggle />
            </li>
            
            <!-- Debug Theme Button -->
            <li class="nav-item ms-2">
              <button 
                class="btn btn-outline-light btn-sm" 
                @click="debugToggleTheme"
                style="font-size: 0.8rem;"
              >
                Debug Theme
              </button>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- Mobile Navigation (shown on mobile devices) -->
    <MobileNavigation :current-user="currentUser" @logout="logout" />

    <!-- Main Content -->
    <main class="container-fluid py-4">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-light py-4 mt-5">
      <div class="container">
        <div class="row">
          <div class="col-md-6">
            <p class="text-muted mb-0">
              © 2025 Campus Chapter Organizer. Built with Vue.js & Spring Boot.
            </p>
          </div>
          <div class="col-md-6 text-end">
            <p class="text-muted mb-0">
              <i class="bi bi-code-slash me-1"></i>
              Frontend: Vue.js | Backend: Spring Boot
            </p>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import ThemeToggle from './components/ThemeToggle.vue'
import SimpleThemeToggle from './components/SimpleThemeToggle.vue'
import PWAInstaller from './components/PWAInstaller.vue'
import MobileNavigation from './components/MobileNavigation.vue'
import { useTheme } from './composables/useTheme.js'
import { onMounted } from 'vue'

export default {
  name: 'App',
  components: {
    ThemeToggle,
    SimpleThemeToggle,
    PWAInstaller,
    MobileNavigation
  },
  setup() {
    const { initializeTheme, setTheme, currentTheme } = useTheme()
    
    onMounted(() => {
      // Initialize theme on app mount
      initializeTheme()
      
      // Add preload class to prevent theme transition on initial load
      document.body.classList.add('preload')
      setTimeout(() => {
        document.body.classList.remove('preload')
      }, 100)
    })
    
    // Debug method to test theme toggle
    const debugToggleTheme = () => {
      console.log('🔥 DEBUG: Current theme before toggle:', currentTheme.value)
      const newTheme = currentTheme.value === 'dark' ? 'light' : 'dark'
      console.log('🔥 DEBUG: Setting theme to:', newTheme)
      setTheme(newTheme)
      
      // Force check DOM after a short delay
      setTimeout(() => {
        const html = document.documentElement
        const body = document.body
        console.log('🔥 DEBUG: HTML data-theme:', html.getAttribute('data-theme'))
        console.log('🔥 DEBUG: Body data-theme:', body.getAttribute('data-theme'))
        console.log('🔥 DEBUG: HTML classes:', html.className)
        console.log('🔥 DEBUG: Body classes:', body.className)
        console.log('🔥 DEBUG: Body background color:', body.style.backgroundColor)
        console.log('🔥 DEBUG: Body color:', body.style.color)
      }, 100)
    }
    
    return {
      debugToggleTheme
    }
  },
  data() {
    return {
      currentUser: null
    }
  },
  mounted() {
    // Check for stored user on app load
    this.checkAuthState()
    
    // Listen for storage changes (when user logs in from another tab)
    window.addEventListener('storage', this.checkAuthState)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.checkAuthState)
  },
  methods: {
    checkAuthState() {
      const storedUser = localStorage.getItem('user')
      console.log('🔍 DEBUG - Checking auth state:', { storedUser, currentUser: this.currentUser })
      if (storedUser) {
        try {
          this.currentUser = JSON.parse(storedUser)
          console.log('✅ DEBUG - User authenticated:', this.currentUser)
        } catch (error) {
          console.error('Error parsing stored user:', error)
          localStorage.removeItem('user')
          this.currentUser = null
        }
      } else {
        this.currentUser = null
        console.log('❌ DEBUG - No user in localStorage')
      }
    },
    logout() {
      // Clear user data
      localStorage.removeItem('user')
      this.currentUser = null
      
      // Redirect to login page
      this.$router.push('/login')
    }
  },
  watch: {
    // Watch for route changes to update auth state
    '$route'() {
      this.checkAuthState()
    }
  }
}
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}

.router-link-active {
  font-weight: bold;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-link {
  transition: all 0.3s ease;
}

.nav-link:hover {
  transform: translateY(-1px);
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* Loading animation */
.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

/* Card hover effects */
.card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.12);
}

/* Mobile responsiveness */
@media (max-width: 768px) {
  /* Hide desktop navigation on mobile */
  .navbar {
    display: none !important;
  }
  
  /* Adjust main content padding for mobile navigation */
  main {
    padding-bottom: 100px !important; /* Space for bottom navigation */
  }
  
  /* Hide footer on mobile for better UX */
  footer {
    display: none;
  }
}

@media (min-width: 769px) {
  /* Hide mobile navigation on desktop */
  .mobile-navigation {
    display: none !important;
  }
}

/* Prevent theme transition flash */
.preload * {
  transition: none !important;
}
</style>