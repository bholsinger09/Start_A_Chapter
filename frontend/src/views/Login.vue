<template>
  <div class="login-container">
    <div class="card">
      <div class="card-header">
        <h3>Login</h3>
      </div>
      <div class="card-body">
        <div v-if="error" class="alert alert-danger" role="alert">
          {{ error }}
        </div>
        <div v-if="success" class="alert alert-success" role="alert">
          {{ success }}
        </div>
        <form @submit.prevent="login">
          <div class="mb-3">
            <label for="username" class="form-label">Username</label>
            <input 
              type="text" 
              class="form-control" 
              id="username" 
              v-model="credentials.username"
              required
            />
            <div class="form-text">Use: testuser</div>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input 
              type="password" 
              class="form-control" 
              id="password" 
              v-model="credentials.password"
              required
            />
            <div class="form-text">Use: password123</div>
          </div>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ loading ? 'Logging in...' : 'Login' }}
          </button>
        </form>
        
        <div class="mt-4" v-if="user">
          <h5>Current User</h5>
          <div class="card">
            <div class="card-body">
              <p><strong>Name:</strong> {{ user.firstName }} {{ user.lastName }}</p>
              <p><strong>Email:</strong> {{ user.email }}</p>
              <p><strong>Role:</strong> {{ user.role }}</p>
              <button @click="logout" class="btn btn-secondary">Logout</button>
            </div>
          </div>
        </div>
        
        <div class="mt-4">
          <h5>Test Secured Endpoint</h5>
          <button @click="testSecuredEndpoint" class="btn btn-info" :disabled="!user">
            Test Chapter Access with Authentication
          </button>
          <div v-if="securedTestResult" class="mt-2">
            <div class="alert" :class="securedTestResult.success ? 'alert-success' : 'alert-danger'">
              {{ securedTestResult.message }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '../services/api'

export default {
  name: 'Login',
  data() {
    return {
      credentials: {
        username: 'testuser',
        password: 'password123'
      },
      user: null,
      loading: false,
      error: null,
      success: null,
      securedTestResult: null
    }
  },
  methods: {
    async login() {
      this.loading = true
      this.error = null
      this.success = null
      
      try {
        const response = await apiClient.post('/auth/login', this.credentials)
        
        if (response.data.success) {
          this.user = response.data.user
          this.success = 'Login successful!'
          
          // Store user info in localStorage for persistence
          localStorage.setItem('user', JSON.stringify(this.user))
        } else {
          this.error = response.data.message || 'Login failed'
        }
      } catch (error) {
        console.error('Login error:', error)
        this.error = error.response?.data?.message || 'Login failed. Please try again.'
      } finally {
        this.loading = false
      }
    },
    
    async logout() {
      try {
        await apiClient.post('/auth/logout')
        this.user = null
        this.success = 'Logout successful!'
        this.securedTestResult = null
        localStorage.removeItem('user')
      } catch (error) {
        console.error('Logout error:', error)
        // Clear user anyway
        this.user = null
        localStorage.removeItem('user')
      }
    },
    
    async testSecuredEndpoint() {
      this.securedTestResult = null
      
      try {
        // Test getting current user info (requires authentication)
        const response = await apiClient.get('/auth/me')
        
        if (response.data.success) {
          this.securedTestResult = {
            success: true,
            message: `✅ Authenticated access successful! Current user: ${response.data.user.firstName} ${response.data.user.lastName}`
          }
        } else {
          this.securedTestResult = {
            success: false,
            message: `❌ Authentication failed: ${response.data.message}`
          }
        }
      } catch (error) {
        console.error('Secured endpoint test error:', error)
        this.securedTestResult = {
          success: false,
          message: `❌ Error testing secured endpoint: ${error.response?.data?.message || error.message}`
        }
      }
    }
  },
  
  mounted() {
    // Check if user is already logged in
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        this.user = JSON.parse(storedUser)
      } catch (error) {
        console.error('Error parsing stored user:', error)
        localStorage.removeItem('user')
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.card {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-text {
  color: #6c757d;
  font-size: 0.875em;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}
</style>