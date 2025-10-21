<template>
  <div v-if="showInstallPrompt" class="pwa-install-banner">
    <div class="container-fluid">
      <div class="alert alert-info alert-dismissible fade show mb-0" role="alert">
        <div class="d-flex align-items-center">
          <i class="bi bi-download me-3 fs-4"></i>
          <div class="flex-grow-1">
            <strong>Install App</strong>
            <p class="mb-0 small">Get the full experience with offline access and faster loading</p>
          </div>
          <div class="btn-group ms-3">
            <button class="btn btn-primary btn-sm" @click="installPWA">
              <i class="bi bi-plus-circle me-1"></i>
              Install
            </button>
            <button class="btn btn-outline-secondary btn-sm" @click="dismissPrompt">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'PWAInstaller',
  setup() {
    const showInstallPrompt = ref(false)
    const deferredPrompt = ref(null)

    const handleBeforeInstallPrompt = (e) => {
      // Prevent the mini-infobar from appearing on mobile
      e.preventDefault()
      // Stash the event so it can be triggered later
      deferredPrompt.value = e
      // Update UI notify the user they can install the PWA
      showInstallPrompt.value = true
    }

    const installPWA = async () => {
      if (!deferredPrompt.value) return

      // Show the install prompt
      deferredPrompt.value.prompt()
      
      // Wait for the user to respond to the prompt
      const { outcome } = await deferredPrompt.value.userChoice
      
      console.log(`PWA install outcome: ${outcome}`)
      
      // We've used the prompt, and can't use it again, throw it away
      deferredPrompt.value = null
      showInstallPrompt.value = false
    }

    const dismissPrompt = () => {
      showInstallPrompt.value = false
      // Remember user dismissed for this session
      sessionStorage.setItem('pwa-prompt-dismissed', 'true')
    }

    const handleAppInstalled = () => {
      console.log('PWA was installed')
      showInstallPrompt.value = false
      deferredPrompt.value = null
    }

    onMounted(() => {
      // Check if user already dismissed prompt this session
      if (sessionStorage.getItem('pwa-prompt-dismissed')) {
        return
      }

      // Listen for the beforeinstallprompt event
      window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt)
      
      // Listen for the appinstalled event
      window.addEventListener('appinstalled', handleAppInstalled)

      // Cleanup listeners when component is unmounted
      return () => {
        window.removeEventListener('beforeinstallprompt', handleBeforeInstallPrompt)
        window.removeEventListener('appinstalled', handleAppInstalled)
      }
    })

    return {
      showInstallPrompt,
      installPWA,
      dismissPrompt
    }
  }
}
</script>

<style scoped>
.pwa-install-banner {
  position: sticky;
  top: 0;
  z-index: 1030;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.alert {
  border-radius: 0;
  border: none;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .btn-group {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .btn {
    font-size: 0.8rem;
    padding: 0.375rem 0.75rem;
  }
}
</style>