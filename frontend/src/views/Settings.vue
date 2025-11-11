<template>
  <div class="settings">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
          <h2>
            <i class="bi bi-gear me-2"></i>
            Settings
          </h2>
          <p class="text-muted">Configure your application preferences and account settings.</p>
        </div>
      </div>

      <div class="row">
        <!-- Application Settings -->
        <div class="col-md-6 mb-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-sliders me-2"></i>
                Application Settings
              </h5>
            </div>
            <div class="card-body">
              <!-- Theme -->
              <div class="mb-3">
                <label class="form-label">Theme</label>
                <select class="form-select" disabled>
                  <option selected>Light Theme</option>
                  <option>Dark Theme</option>
                  <option>Auto</option>
                </select>
                <small class="form-text text-muted">Theme switching coming soon</small>
              </div>

              <!-- Language -->
              <div class="mb-3">
                <label class="form-label">Language</label>
                <select class="form-select" disabled>
                  <option selected>English</option>
                  <option>Spanish</option>
                  <option>French</option>
                </select>
                <small class="form-text text-muted">Multi-language support coming soon</small>
              </div>

              <!-- Notifications -->
              <div class="mb-3">
                <label class="form-label">Notifications</label>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" id="emailNotifications" disabled>
                  <label class="form-check-label" for="emailNotifications">
                    Email notifications
                  </label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" id="pushNotifications" disabled>
                  <label class="form-check-label" for="pushNotifications">
                    Push notifications
                  </label>
                </div>
                <small class="form-text text-muted">Notification system coming soon</small>
              </div>
            </div>
          </div>
        </div>

        <!-- Account Settings -->
        <div class="col-md-6 mb-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-person-gear me-2"></i>
                Account Settings
              </h5>
            </div>
            <div class="card-body">
              <!-- Privacy -->
              <div class="mb-3">
                <label class="form-label">Privacy</label>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" id="profileVisibility" disabled>
                  <label class="form-check-label" for="profileVisibility">
                    Make profile public
                  </label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" id="showEmail" disabled>
                  <label class="form-check-label" for="showEmail">
                    Show email to other members
                  </label>
                </div>
                <small class="form-text text-muted">Privacy controls coming soon</small>
              </div>

              <!-- Data Export -->
              <div class="mb-3">
                <label class="form-label">Data Management</label>
                <div class="d-grid gap-2">
                  <button class="btn btn-outline-primary" disabled>
                    <i class="bi bi-download me-2"></i>
                    Export My Data
                  </button>
                  <button class="btn btn-outline-warning" disabled>
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Delete Account
                  </button>
                </div>
                <small class="form-text text-muted">Data management features coming soon</small>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- System Information -->
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-info-circle me-2"></i>
                System Information
              </h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <h6>Application Details</h6>
                  <ul class="list-unstyled">
                    <li><strong>Version:</strong> 1.0.0</li>
                    <li><strong>Build:</strong> Production</li>
                    <li><strong>Last Updated:</strong> November 2024</li>
                  </ul>
                </div>
                <div class="col-md-6">
                  <h6>Your Session</h6>
                  <ul class="list-unstyled" v-if="currentUser">
                    <li><strong>Login Method:</strong> {{ currentUser.action }}</li>
                    <li><strong>Session Start:</strong> {{ formatLoginTime(currentUser.loginTime) }}</li>
                    <li><strong>User ID:</strong> {{ currentUser.id }}</li>
                  </ul>
                  <p v-else class="text-muted">Not logged in</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="row mt-4">
        <div class="col-12">
          <div class="card">
            <div class="card-body text-center">
              <h6 class="card-title">Need Help?</h6>
              <p class="card-text text-muted">
                Contact support or check our documentation for assistance.
              </p>
              <div class="d-flex gap-2 justify-content-center">
                <button class="btn btn-outline-primary" disabled>
                  <i class="bi bi-question-circle me-2"></i>
                  Help Center
                </button>
                <button class="btn btn-outline-secondary" disabled>
                  <i class="bi bi-chat-dots me-2"></i>
                  Contact Support
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'Settings',
  setup() {
    const currentUser = computed(() => {
      try {
        const user = localStorage.getItem('user')
        return user ? JSON.parse(user) : null
      } catch {
        return null
      }
    })

    const formatLoginTime = (timestamp) => {
      if (!timestamp) return 'Unknown'
      try {
        return new Date(timestamp).toLocaleString()
      } catch {
        return 'Invalid date'
      }
    }

    return {
      currentUser,
      formatLoginTime
    }
  }
}
</script>

<style scoped>
.card {
  border-radius: 0.5rem;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.form-control:disabled,
.form-select:disabled,
.form-check-input:disabled {
  background-color: #f8f9fa;
  opacity: 0.65;
}

.btn:disabled {
  opacity: 0.65;
}

.list-unstyled li {
  margin-bottom: 0.25rem;
}
</style>