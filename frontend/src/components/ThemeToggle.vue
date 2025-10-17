<template>
  <div class="dropdown">
    <button 
      class="btn btn-outline-light theme-toggle dropdown-toggle" 
      type="button" 
      id="themeDropdown" 
      data-bs-toggle="dropdown" 
      aria-expanded="false"
      :title="`Current theme: ${currentTheme}`"
    >
      <i :class="getCurrentIcon()"></i>
    </button>
    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="themeDropdown">
      <li>
        <h6 class="dropdown-header">
          <i class="bi bi-palette me-2"></i>
          Choose Theme
        </h6>
      </li>
      <li>
        <button 
          class="dropdown-item d-flex align-items-center"
          @click="useSystemTheme"
          :class="{ 'active': isSystemTheme }"
        >
          <i class="bi bi-circle-half me-2"></i>
          <span class="flex-grow-1">System</span>
          <small class="text-muted ms-2">
            {{ systemPrefersDark ? 'Dark' : 'Light' }}
          </small>
        </button>
      </li>
      <li>
        <button 
          class="dropdown-item d-flex align-items-center"
          @click="setTheme('light')"
          :class="{ 'active': !isSystemTheme && currentTheme === 'light' }"
        >
          <i class="bi bi-sun me-2"></i>
          Light
        </button>
      </li>
      <li>
        <button 
          class="dropdown-item d-flex align-items-center"
          @click="setTheme('dark')"
          :class="{ 'active': !isSystemTheme && currentTheme === 'dark' }"
        >
          <i class="bi bi-moon-stars me-2"></i>
          Dark
        </button>
      </li>
      <li><hr class="dropdown-divider"></li>
      <li>
        <div class="px-3 py-2">
          <small class="text-muted">
            <i class="bi bi-info-circle me-1"></i>
            System theme follows your OS preference
          </small>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import { useTheme } from '../composables/useTheme.js'
import { onMounted, onUnmounted } from 'vue'

export default {
  name: 'ThemeToggle',
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
      initializeTheme()
      unwatchSystemTheme = watchSystemTheme()
    })

    onUnmounted(() => {
      if (unwatchSystemTheme) {
        unwatchSystemTheme()
      }
    })

    const getCurrentIcon = () => {
      if (isSystemTheme.value) {
        return 'bi bi-circle-half'
      }
      return currentTheme.value === 'dark' ? 'bi bi-moon-stars' : 'bi bi-sun'
    }

    // Wrapper for setTheme with debug logging
    const setThemeWithDebug = (theme) => {
      console.log('🎨 ThemeToggle: Setting theme to:', theme)
      console.log('🎨 ThemeToggle: Current theme before:', currentTheme.value)
      setTheme(theme)
      console.log('🎨 ThemeToggle: Current theme after:', currentTheme.value)
    }

    // Wrapper for useSystemTheme with debug logging
    const useSystemThemeWithDebug = () => {
      console.log('🎨 ThemeToggle: Setting to system theme')
      console.log('🎨 ThemeToggle: System prefers dark:', systemPrefersDark.value)
      useSystemTheme()
      console.log('🎨 ThemeToggle: Current theme after system:', currentTheme.value)
    }

    return {
      currentTheme,
      isSystemTheme,
      systemPrefersDark,
      useSystemTheme: useSystemThemeWithDebug,
      setTheme: setThemeWithDebug,
      getCurrentIcon
    }
  }
}
</script>

<style scoped>
.theme-toggle {
  border-radius: 0.375rem;
  padding: 0.375rem 0.75rem;
  transition: all 0.15s ease-in-out;
}

.theme-toggle:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.dropdown-item.active {
  background-color: var(--bs-primary);
  color: white;
}

.dropdown-item:not(.active):hover {
  background-color: var(--color-bg-secondary);
}

.dropdown-header {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-primary);
}
</style>