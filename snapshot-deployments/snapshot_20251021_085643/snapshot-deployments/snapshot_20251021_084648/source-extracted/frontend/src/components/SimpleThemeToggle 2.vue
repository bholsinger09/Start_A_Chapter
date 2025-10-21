<template>
  <div class="simple-theme-toggle d-flex gap-1">
    <button 
      class="btn btn-outline-light btn-sm" 
      @click="setTheme('light')"
      :class="{ 'active': currentTheme === 'light' }"
      title="Light Mode"
    >
      ☀️
    </button>
    <button 
      class="btn btn-outline-light btn-sm" 
      @click="setTheme('dark')"
      :class="{ 'active': currentTheme === 'dark' }"
      title="Dark Mode"
    >
      🌙
    </button>
    <button 
      class="btn btn-outline-light btn-sm" 
      @click="useSystemTheme"
      :class="{ 'active': isSystemTheme }"
      title="System Theme"
    >
      🔄
    </button>
  </div>
</template>

<script>
import { useTheme } from '../composables/useTheme.js'
import { onMounted, onUnmounted } from 'vue'

export default {
  name: 'SimpleThemeToggle',
  setup() {
    const {
      currentTheme,
      isSystemTheme,
      systemPrefersDark,
      useSystemTheme,
      setTheme,
      initializeTheme,
      watchSystemTheme
    } = useTheme()

    let unwatchSystemTheme

    onMounted(() => {
      console.log('🎯 SimpleThemeToggle: Component mounted')
      initializeTheme()
      unwatchSystemTheme = watchSystemTheme()
      console.log('🎯 SimpleThemeToggle: Initial theme:', currentTheme.value)
    })

    onUnmounted(() => {
      if (unwatchSystemTheme) {
        unwatchSystemTheme()
      }
    })

    // Wrapper for setTheme with debug logging
    const setThemeWithDebug = (theme) => {
      console.log('🎯 SimpleThemeToggle: Setting theme to:', theme)
      setTheme(theme)
      
      // Check result after a delay
      setTimeout(() => {
        console.log('🎯 SimpleThemeToggle: Theme after set:', currentTheme.value)
      }, 50)
    }

    // Wrapper for useSystemTheme with debug logging
    const useSystemThemeWithDebug = () => {
      console.log('🎯 SimpleThemeToggle: Using system theme')
      useSystemTheme()
      
      setTimeout(() => {
        console.log('🎯 SimpleThemeToggle: Theme after system:', currentTheme.value)
      }, 50)
    }

    return {
      currentTheme,
      isSystemTheme,
      systemPrefersDark,
      useSystemTheme: useSystemThemeWithDebug,
      setTheme: setThemeWithDebug
    }
  }
}
</script>

<style scoped>
.simple-theme-toggle .btn.active {
  background-color: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

.simple-theme-toggle .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
</style>