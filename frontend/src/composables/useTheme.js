import { ref, computed, watch, onMounted } from 'vue'

// Theme state
const isDark = ref(false)
const isSystemTheme = ref(true)

// Theme composable
export function useTheme() {
  // Check system preference
  const systemPrefersDark = computed(() => {
    if (typeof window !== 'undefined') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    return false
  })

  // Current theme
  const currentTheme = computed(() => {
    if (isSystemTheme.value) {
      return systemPrefersDark.value ? 'dark' : 'light'
    }
    return isDark.value ? 'dark' : 'light'
  })

  // Initialize theme
  const initializeTheme = () => {
    // Load saved preference or default to system
    const savedTheme = localStorage.getItem('theme-preference')
    const savedIsSystem = localStorage.getItem('theme-is-system')

    if (savedTheme && savedIsSystem !== null) {
      isSystemTheme.value = savedIsSystem === 'true'
      if (!isSystemTheme.value) {
        isDark.value = savedTheme === 'dark'
      }
    } else {
      // Default to system preference
      isSystemTheme.value = true
    }

    applyTheme()
  }

  // Apply theme to DOM
  const applyTheme = () => {
    const html = document.documentElement
    const body = document.body
    const theme = currentTheme.value

    console.log('Applying theme:', theme) // Debug log
    
    // Update data attribute for CSS targeting
    html.setAttribute('data-theme', theme)
    body.setAttribute('data-theme', theme)
    
    // Update Bootstrap theme classes
    if (theme === 'dark') {
      html.setAttribute('data-bs-theme', 'dark')
      body.setAttribute('data-bs-theme', 'dark')
      html.classList.add('dark-theme')
      body.classList.add('dark-theme')
    } else {
      html.removeAttribute('data-bs-theme')
      body.removeAttribute('data-bs-theme')
      html.classList.remove('dark-theme')
      body.classList.remove('dark-theme')
    }

    // Force a style update
    body.style.backgroundColor = theme === 'dark' ? '#1a1d23' : '#ffffff'
    body.style.color = theme === 'dark' ? '#f7fafc' : '#212529'

    // Store preference
    localStorage.setItem('theme-preference', theme)
    localStorage.setItem('theme-is-system', isSystemTheme.value.toString())
    
    console.log('Theme applied. HTML data-theme:', html.getAttribute('data-theme')) // Debug log
  }

  // Toggle between light and dark
  const toggleTheme = () => {
    if (isSystemTheme.value) {
      // Switch to manual mode and toggle
      isSystemTheme.value = false
      isDark.value = !systemPrefersDark.value
    } else {
      isDark.value = !isDark.value
    }
  }

  // Set to system theme
  const useSystemTheme = () => {
    isSystemTheme.value = true
  }

  // Set specific theme
  const setTheme = (theme) => {
    isSystemTheme.value = false
    isDark.value = theme === 'dark'
  }

  // Watch for system theme changes
  const watchSystemTheme = () => {
    if (typeof window !== 'undefined') {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      
      const handleSystemThemeChange = (e) => {
        if (isSystemTheme.value) {
          applyTheme()
        }
      }

      mediaQuery.addEventListener('change', handleSystemThemeChange)
      
      return () => {
        mediaQuery.removeEventListener('change', handleSystemThemeChange)
      }
    }
  }

  // Watch for theme changes
  watch([currentTheme], () => {
    applyTheme()
  })

  // Theme options for UI
  const themeOptions = [
    { value: 'system', label: 'System', icon: 'bi-circle-half' },
    { value: 'light', label: 'Light', icon: 'bi-sun' },
    { value: 'dark', label: 'Dark', icon: 'bi-moon-stars' }
  ]

  return {
    isDark: computed(() => currentTheme.value === 'dark'),
    currentTheme,
    isSystemTheme,
    systemPrefersDark,
    themeOptions,
    initializeTheme,
    toggleTheme,
    useSystemTheme,
    setTheme,
    watchSystemTheme
  }
}