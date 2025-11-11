<template>
  <div class="chapter-create">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2><i class="bi bi-building-add me-2"></i>Create New Chapter</h2>
              <p class="text-muted">Start a new student organization chapter at your university.</p>
            </div>
            <button class="btn btn-outline-secondary" @click="goBack">
              <i class="bi bi-arrow-left me-2"></i>
              Back to Chapters
            </button>
          </div>
        </div>
      </div>

      <!-- Chapter Creation Form -->
      <div class="row">
        <div class="col-lg-8 col-md-10 mx-auto">
          <div class="card">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0">
                <i class="bi bi-info-circle me-2"></i>
                Chapter Information
              </h5>
            </div>
            <div class="card-body">
              <form @submit.prevent="createChapter">
                <!-- Chapter Name -->
                <div class="mb-3">
                  <label for="name" class="form-label">
                    Chapter Name <span class="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    class="form-control"
                    id="name"
                    v-model="form.name"
                    :disabled="isLoading"
                    placeholder="e.g., Alpha Beta Chapter"
                    required
                  >
                  <small class="form-text text-muted">
                    Choose a unique name for your chapter
                  </small>
                </div>

                <!-- University Name -->
                <div class="mb-3">
                  <label for="universityName" class="form-label">
                    University Name <span class="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    class="form-control"
                    id="universityName"
                    v-model="form.universityName"
                    :disabled="isLoading"
                    placeholder="e.g., University of California, Los Angeles"
                    required
                  >
                </div>

                <!-- State and City -->
                <div class="row">
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="state" class="form-label">
                        State <span class="text-danger">*</span>
                      </label>
                      <select
                        class="form-select"
                        id="state"
                        v-model="form.state"
                        :disabled="isLoading"
                        required
                      >
                        <option value="">Select a state</option>
                        <option v-for="state in states" :key="state" :value="state">
                          {{ state }}
                        </option>
                      </select>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="city" class="form-label">
                        City <span class="text-danger">*</span>
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="city"
                        v-model="form.city"
                        :disabled="isLoading"
                        placeholder="e.g., Los Angeles"
                        required
                      >
                    </div>
                  </div>
                </div>

                <!-- Description -->
                <div class="mb-3">
                  <label for="description" class="form-label">Description (Optional)</label>
                  <textarea
                    class="form-control"
                    id="description"
                    v-model="form.description"
                    :disabled="isLoading"
                    rows="3"
                    maxlength="500"
                    placeholder="Brief description of the chapter's mission and activities..."
                  ></textarea>
                  <small class="form-text text-muted">
                    {{ form.description ? form.description.length : 0 }}/500 characters
                  </small>
                </div>

                <!-- Error Message -->
                <div v-if="error" class="alert alert-danger">
                  <i class="bi bi-exclamation-triangle me-2"></i>
                  {{ error }}
                </div>

                <!-- Success Message -->
                <div v-if="success" class="alert alert-success">
                  <i class="bi bi-check-circle me-2"></i>
                  {{ success }}
                </div>

                <!-- Submit Button -->
                <div class="d-grid">
                  <button
                    type="submit"
                    class="btn btn-primary btn-lg"
                    :disabled="isLoading"
                  >
                    <span v-if="isLoading">
                      <i class="bi bi-hourglass-split me-2"></i>
                      Creating Chapter...
                    </span>
                    <span v-else>
                      <i class="bi bi-plus-circle me-2"></i>
                      Create Chapter
                    </span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

export default {
  name: 'ChapterCreate',
  setup() {
    const router = useRouter()
    
    const form = ref({
      name: '',
      universityName: '',
      state: '',
      city: '',
      description: ''
    })
    
    const isLoading = ref(false)
    const error = ref('')
    const success = ref('')

    // US States list
    const states = ref([
      'Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California', 'Colorado', 'Connecticut',
      'Delaware', 'Florida', 'Georgia', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa',
      'Kansas', 'Kentucky', 'Louisiana', 'Maine', 'Maryland', 'Massachusetts', 'Michigan',
      'Minnesota', 'Mississippi', 'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire',
      'New Jersey', 'New Mexico', 'New York', 'North Carolina', 'North Dakota', 'Ohio',
      'Oklahoma', 'Oregon', 'Pennsylvania', 'Rhode Island', 'South Carolina', 'South Dakota',
      'Tennessee', 'Texas', 'Utah', 'Vermont', 'Virginia', 'Washington', 'West Virginia',
      'Wisconsin', 'Wyoming'
    ])

    const createChapter = async () => {
      if (!form.value.name || !form.value.universityName || !form.value.state || !form.value.city) {
        error.value = 'Please fill in all required fields.'
        return
      }

      try {
        isLoading.value = true
        error.value = ''
        success.value = ''

        const chapterData = {
          name: form.value.name.trim(),
          universityName: form.value.universityName.trim(),
          state: form.value.state,
          city: form.value.city.trim(),
          description: form.value.description.trim() || null
        }

        await api.post('/api/chapters', chapterData)
        
        success.value = 'Chapter created successfully!'
        
        // Redirect after 2 seconds
        setTimeout(() => {
          router.push('/chapters')
        }, 2000)

      } catch (err) {
        console.error('Chapter creation error:', err)
        if (err.response?.data?.message) {
          error.value = err.response.data.message
        } else if (err.response?.status === 400) {
          error.value = 'Invalid chapter data. Please check your input and try again.'
        } else {
          error.value = 'Failed to create chapter. Please try again.'
        }
      } finally {
        isLoading.value = false
      }
    }

    const goBack = () => {
      router.push('/chapters')
    }

    return {
      form,
      isLoading,
      error,
      success,
      states,
      createChapter,
      goBack
    }
  }
}
</script>

<style scoped>
.chapter-create {
  padding: 20px 0;
}

.card {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border: none;
}

.card-header {
  border-bottom: none;
}

.form-label {
  font-weight: 600;
  color: #495057;
}

.text-danger {
  font-weight: bold;
}

.btn-lg {
  padding: 12px 24px;
  font-size: 1.1rem;
}

.alert {
  border: none;
  border-radius: 8px;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
}

.alert-success {
  background-color: #d1edff;
  color: #0c5460;
}
</style>
