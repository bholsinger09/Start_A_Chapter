import { ref, onMounted, onUnmounted, nextTick, readonly } from 'vue'

// Global accessibility state
const reducedMotion = ref(false)
const highContrast = ref(false)
const fontSize = ref('normal')
const focusVisible = ref(true)

export function useAccessibility() {
  
  // Detect user preferences
  const detectPreferences = () => {
    // Check for reduced motion preference
    if (window.matchMedia) {
      const motionQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
      reducedMotion.value = motionQuery.matches
      
      motionQuery.addEventListener('change', (e) => {
        reducedMotion.value = e.matches
        updateMotionSettings()
      })
      
      // Check for high contrast preference
      const contrastQuery = window.matchMedia('(prefers-contrast: high)')
      highContrast.value = contrastQuery.matches
      
      contrastQuery.addEventListener('change', (e) => {
        highContrast.value = e.matches
        updateContrastSettings()
      })
    }
    
    // Load saved preferences from localStorage
    const savedFontSize = localStorage.getItem('accessibility-font-size')
    if (savedFontSize) {
      fontSize.value = savedFontSize
    }
  }
  
  // Update motion settings based on preference
  const updateMotionSettings = () => {
    const root = document.documentElement
    
    if (reducedMotion.value) {
      root.classList.add('reduce-motion')
    } else {
      root.classList.remove('reduce-motion')
    }
  }
  
  // Update contrast settings
  const updateContrastSettings = () => {
    const root = document.documentElement
    
    if (highContrast.value) {
      root.classList.add('high-contrast')
    } else {
      root.classList.remove('high-contrast')
    }
  }
  
  // Font size management
  const setFontSize = (size) => {
    fontSize.value = size
    const root = document.documentElement
    
    // Remove existing font size classes
    root.classList.remove('font-size-small', 'font-size-normal', 'font-size-large', 'font-size-xl')
    
    // Add new font size class
    if (size !== 'normal') {
      root.classList.add(`font-size-${size}`)
    }
    
    // Save preference
    localStorage.setItem('accessibility-font-size', size)
  }
  
  // Focus management
  const trapFocus = (element) => {
    const focusableElements = element.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    
    const firstElement = focusableElements[0]
    const lastElement = focusableElements[focusableElements.length - 1]
    
    const handleTabKey = (e) => {
      if (e.key === 'Tab') {
        if (e.shiftKey) {
          if (document.activeElement === firstElement) {
            lastElement.focus()
            e.preventDefault()
          }
        } else {
          if (document.activeElement === lastElement) {
            firstElement.focus()
            e.preventDefault()
          }
        }
      }
    }
    
    element.addEventListener('keydown', handleTabKey)
    
    // Return cleanup function
    return () => {
      element.removeEventListener('keydown', handleTabKey)
    }
  }
  
  // Announce to screen readers
  const announce = (message, priority = 'polite') => {
    const announcer = document.createElement('div')
    announcer.setAttribute('aria-live', priority)
    announcer.setAttribute('aria-atomic', 'true')
    announcer.setAttribute('class', 'sr-only')
    announcer.textContent = message
    
    document.body.appendChild(announcer)
    
    // Remove after announcement
    setTimeout(() => {
      document.body.removeChild(announcer)
    }, 1000)
  }
  
  // Keyboard navigation helper
  const handleArrowNavigation = (event, items, currentIndex, callback) => {
    let newIndex = currentIndex
    
    switch (event.key) {
      case 'ArrowDown':
        newIndex = currentIndex < items.length - 1 ? currentIndex + 1 : 0
        event.preventDefault()
        break
      case 'ArrowUp':
        newIndex = currentIndex > 0 ? currentIndex - 1 : items.length - 1
        event.preventDefault()
        break
      case 'Home':
        newIndex = 0
        event.preventDefault()
        break
      case 'End':
        newIndex = items.length - 1
        event.preventDefault()
        break
    }
    
    if (newIndex !== currentIndex) {
      callback(newIndex)
    }
  }
  
  // Skip link functionality
  const addSkipLink = () => {
    const skipLink = document.createElement('a')
    skipLink.href = '#main-content'
    skipLink.textContent = 'Skip to main content'
    skipLink.className = 'skip-link'
    
    // Insert at beginning of body
    document.body.insertBefore(skipLink, document.body.firstChild)
  }
  
  // Color contrast checker
  const checkContrast = (foreground, background) => {
    // Convert hex to RGB
    const hexToRgb = (hex) => {
      const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
      return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
      } : null
    }
    
    // Calculate relative luminance
    const getLuminance = (rgb) => {
      const { r, g, b } = rgb
      const [rs, gs, bs] = [r, g, b].map(c => {
        c = c / 255
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4)
      })
      return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs
    }
    
    const fgRgb = hexToRgb(foreground)
    const bgRgb = hexToRgb(background)
    
    if (!fgRgb || !bgRgb) return null
    
    const fgLum = getLuminance(fgRgb)
    const bgLum = getLuminance(bgRgb)
    
    const contrast = (Math.max(fgLum, bgLum) + 0.05) / (Math.min(fgLum, bgLum) + 0.05)
    
    return {
      ratio: contrast,
      AA: contrast >= 4.5,
      AAA: contrast >= 7,
      AALarge: contrast >= 3,
      AAALarge: contrast >= 4.5
    }
  }
  
  // ARIA label helper
  const setAriaLabel = (element, label) => {
    if (element && label) {
      element.setAttribute('aria-label', label)
    }
  }
  
  // Live region helper
  const createLiveRegion = (priority = 'polite') => {
    const region = document.createElement('div')
    region.setAttribute('aria-live', priority)
    region.setAttribute('aria-atomic', 'true')
    region.className = 'sr-only'
    region.id = `live-region-${Date.now()}`
    
    document.body.appendChild(region)
    
    const update = (message) => {
      region.textContent = message
    }
    
    const destroy = () => {
      if (region.parentNode) {
        region.parentNode.removeChild(region)
      }
    }
    
    return { update, destroy }
  }
  
  // Initialize accessibility features
  onMounted(() => {
    detectPreferences()
    updateMotionSettings()
    updateContrastSettings()
    setFontSize(fontSize.value)
    addSkipLink()
  })
  
  return {
    // State
    reducedMotion: readonly(reducedMotion),
    highContrast: readonly(highContrast),
    fontSize: readonly(fontSize),
    
    // Methods
    setFontSize,
    trapFocus,
    announce,
    handleArrowNavigation,
    checkContrast,
    setAriaLabel,
    createLiveRegion
  }
}

// Composable for form accessibility
export function useFormAccessibility() {
  
  // Validate form field and announce errors
  const validateField = (field, rules, liveRegion) => {
    const errors = []
    
    rules.forEach(rule => {
      if (!rule.validator(field.value)) {
        errors.push(rule.message)
      }
    })
    
    // Update ARIA attributes
    if (errors.length > 0) {
      field.setAttribute('aria-invalid', 'true')
      field.setAttribute('aria-describedby', `${field.id}-error`)
      
      // Announce error to screen readers
      if (liveRegion) {
        liveRegion.update(`Error in ${field.getAttribute('aria-label') || field.name}: ${errors[0]}`)
      }
    } else {
      field.setAttribute('aria-invalid', 'false')
      field.removeAttribute('aria-describedby')
    }
    
    return errors
  }
  
  // Add required field indicators
  const markRequiredFields = (form) => {
    const requiredFields = form.querySelectorAll('[required]')
    
    requiredFields.forEach(field => {
      field.setAttribute('aria-required', 'true')
      
      // Add visual indicator
      const label = form.querySelector(`label[for="${field.id}"]`)
      if (label && !label.querySelector('.required-indicator')) {
        const indicator = document.createElement('span')
        indicator.className = 'required-indicator'
        indicator.textContent = ' *'
        indicator.setAttribute('aria-label', 'required')
        label.appendChild(indicator)
      }
    })
  }
  
  return {
    validateField,
    markRequiredFields
  }
}

// Keyboard shortcuts helper
export function useKeyboardShortcuts() {
  const shortcuts = new Map()
  
  const addShortcut = (key, callback, options = {}) => {
    const { ctrl = false, alt = false, shift = false } = options
    const shortcutKey = `${ctrl ? 'ctrl+' : ''}${alt ? 'alt+' : ''}${shift ? 'shift+' : ''}${key.toLowerCase()}`
    
    shortcuts.set(shortcutKey, callback)
  }
  
  const handleKeydown = (event) => {
    const key = event.key.toLowerCase()
    const shortcutKey = `${event.ctrlKey ? 'ctrl+' : ''}${event.altKey ? 'alt+' : ''}${event.shiftKey ? 'shift+' : ''}${key}`
    
    const callback = shortcuts.get(shortcutKey)
    if (callback) {
      event.preventDefault()
      callback(event)
    }
  }
  
  const removeShortcut = (key, options = {}) => {
    const { ctrl = false, alt = false, shift = false } = options
    const shortcutKey = `${ctrl ? 'ctrl+' : ''}${alt ? 'alt+' : ''}${shift ? 'shift+' : ''}${key.toLowerCase()}`
    
    shortcuts.delete(shortcutKey)
  }
  
  // Add default shortcuts
  onMounted(() => {
    document.addEventListener('keydown', handleKeydown)
    
    // Add common shortcuts
    addShortcut('/', (e) => {
      // Focus search input
      const searchInput = document.querySelector('input[type="search"], input[placeholder*="search" i]')
      if (searchInput) {
        searchInput.focus()
      }
    })
    
    addShortcut('escape', (e) => {
      // Close modals, dropdowns, etc.
      const closeButtons = document.querySelectorAll('[data-bs-dismiss], .btn-close')
      if (closeButtons.length > 0) {
        closeButtons[0].click()
      }
    })
  })
  
  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeydown)
  })
  
  return {
    addShortcut,
    removeShortcut
  }
}