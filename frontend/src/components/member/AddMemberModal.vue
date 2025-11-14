<!--
AddMemberModal Component
Fixes: Large Classes smell - Extracted modal logic from Members.vue  
Single Responsibility: Handle adding new members with validation
-->
<template>
  <div 
    class="modal fade" 
    :id="modalId" 
    tabindex="-1" 
    :aria-labelledby="`${modalId}Label`" 
    aria-hidden="true"
  >
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" :id="`${modalId}Label`">
            <i class="bi bi-person-plus me-2"></i>Add New Member
          </h5>
          <button 
            type="button" 
            class="btn-close" 
            data-bs-dismiss="modal" 
            aria-label="Close"
          ></button>
        </div>
        
        <form @submit.prevent="handleSubmit">
          <div class="modal-body">
            <!-- Error Alert -->
            <div v-if="error" class="alert alert-danger d-flex align-items-center">
              <i class="bi bi-exclamation-triangle me-2"></i>
              {{ error }}
            </div>

            <!-- Personal Information -->
            <div class="row mb-3">
              <div class="col-12">
                <h6 class="text-muted mb-3">
                  <i class="bi bi-person me-1"></i>Personal Information
                </h6>
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="firstName" class="form-label">
                    First Name <span class="text-danger">*</span>
                  </label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="firstName" 
                    v-model="form.firstName"
                    :class="{ 'is-invalid': fieldErrors.firstName }"
                    required
                  >
                  <div v-if="fieldErrors.firstName" class="invalid-feedback">
                    {{ fieldErrors.firstName }}
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="lastName" class="form-label">
                    Last Name <span class="text-danger">*</span>
                  </label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="lastName" 
                    v-model="form.lastName"
                    :class="{ 'is-invalid': fieldErrors.lastName }"
                    required
                  >
                  <div v-if="fieldErrors.lastName" class="invalid-feedback">
                    {{ fieldErrors.lastName }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Contact Information -->
            <div class="row mb-3">
              <div class="col-12">
                <h6 class="text-muted mb-3">
                  <i class="bi bi-envelope me-1"></i>Contact Information
                </h6>
              </div>
            </div>
            
            <div class="mb-3">
              <label for="email" class="form-label">
                Email <span class="text-danger">*</span>
              </label>
              <input 
                type="email" 
                class="form-control" 
                id="email" 
                v-model="form.email"
                :class="{ 'is-invalid': fieldErrors.email }"
                required
              >
              <div v-if="fieldErrors.email" class="invalid-feedback">
                {{ fieldErrors.email }}
              </div>
            </div>

            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="username" class="form-label">Username</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="username" 
                    v-model="form.username"
                    :class="{ 'is-invalid': fieldErrors.username }"
                  >
                  <div v-if="fieldErrors.username" class="invalid-feedback">
                    {{ fieldErrors.username }}
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="phoneNumber" class="form-label">Phone Number</label>
                  <input 
                    type="tel" 
                    class="form-control" 
                    id="phoneNumber" 
                    v-model="form.phoneNumber"
                    :class="{ 'is-invalid': fieldErrors.phoneNumber }"
                  >
                  <div v-if="fieldErrors.phoneNumber" class="invalid-feedback">
                    {{ fieldErrors.phoneNumber }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Chapter Information -->
            <div class="row mb-3">
              <div class="col-12">
                <h6 class="text-muted mb-3">
                  <i class="bi bi-building me-1"></i>Chapter Information
                </h6>
              </div>
            </div>

            <div class="mb-3">
              <label for="chapterSelect" class="form-label">
                Chapter <span class="text-danger">*</span>
              </label>
              <select 
                class="form-select" 
                id="chapterSelect" 
                v-model="form.chapterId"
                :class="{ 'is-invalid': fieldErrors.chapterId }"
                required
              >
                <option value="">Select a chapter</option>
                <option 
                  v-for="chapter in availableChapters" 
                  :key="chapter.id" 
                  :value="chapter.id"
                >
                  {{ chapter.name }} - {{ chapter.universityName }}
                </option>
              </select>
              <div v-if="fieldErrors.chapterId" class="invalid-feedback">
                {{ fieldErrors.chapterId }}
              </div>
            </div>

            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="role" class="form-label">Role</label>
                  <select class="form-select" id="role" v-model="form.role">
                    <option value="MEMBER">Member</option>
                    <option value="OFFICER">Officer</option>
                    <option value="SECRETARY">Secretary</option>
                    <option value="TREASURER">Treasurer</option>
                    <option value="VICE_PRESIDENT">Vice President</option>
                    <option value="PRESIDENT">President</option>
                  </select>
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="major" class="form-label">Major</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="major" 
                    v-model="form.major"
                  >
                </div>
              </div>
            </div>

            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="graduationYear" class="form-label">Graduation Year</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="graduationYear" 
                    v-model="form.graduationYear" 
                    placeholder="2024"
                  >
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="password" class="form-label">
                    Password <span class="text-danger">*</span>
                  </label>
                  <input 
                    type="password" 
                    class="form-control" 
                    id="password" 
                    v-model="form.password"
                    :class="{ 'is-invalid': fieldErrors.password }"
                    required
                  >
                  <div v-if="fieldErrors.password" class="invalid-feedback">
                    {{ fieldErrors.password }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button 
              type="button" 
              class="btn btn-secondary" 
              data-bs-dismiss="modal"
              :disabled="loading"
            >
              Cancel
            </button>
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-person-plus me-2"></i>
              {{ loading ? 'Adding...' : 'Add Member' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { validateFields, ValidationRules } from '@/utils/validation';

export default {
  name: 'AddMemberModal',
  props: {
    modalId: {
      type: String,
      default: 'addMemberModal'
    },
    availableChapters: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['submit'],
  data() {
    return {
      form: this.getInitialForm(),
      error: '',
      fieldErrors: {}
    };
  },
  methods: {
    getInitialForm() {
      return {
        firstName: '',
        lastName: '',
        email: '',
        username: '',
        phoneNumber: '',
        chapterId: '',
        role: 'MEMBER',
        major: '',
        graduationYear: '',
        password: ''
      };
    },

    resetForm() {
      this.form = this.getInitialForm();
      this.error = '';
      this.fieldErrors = {};
    },

    validateForm() {
      const validationRules = {
        firstName: [ValidationRules.name('First Name')],
        lastName: [ValidationRules.name('Last Name')], 
        email: [ValidationRules.email('Email')],
        password: [ValidationRules.password('Password')],
        chapterId: [ValidationRules.required('Chapter')]
      };

      const validation = validateFields(this.form, validationRules);
      this.fieldErrors = validation.errors;
      
      return validation.isValid;
    },

    handleSubmit() {
      // Reset previous errors
      this.error = '';
      this.fieldErrors = {};

      // Validate form
      if (!this.validateForm()) {
        this.error = 'Please fix the validation errors below.';
        return;
      }

      // Prepare member data
      const memberData = {
        firstName: this.form.firstName.trim(),
        lastName: this.form.lastName.trim(),
        email: this.form.email.trim(),
        username: this.form.username?.trim() || null,
        phoneNumber: this.form.phoneNumber?.trim() || null,
        role: this.form.role,
        major: this.form.major?.trim() || null,
        graduationYear: this.form.graduationYear?.trim() || null,
        password: this.form.password,
        active: true
      };

      // Emit submit event with member data and chapter ID
      this.$emit('submit', {
        memberData,
        chapterId: this.form.chapterId
      });
    },

    show() {
      this.resetForm();
      const modal = new window.bootstrap.Modal(document.getElementById(this.modalId));
      modal.show();
    },

    hide() {
      const modal = window.bootstrap.Modal.getInstance(document.getElementById(this.modalId));
      if (modal) {
        modal.hide();
      }
    },

    setError(errorMessage) {
      this.error = errorMessage;
    }
  }
};
</script>

<style scoped>
.modal-dialog {
  max-width: 600px;
}

.modal-header {
  border-bottom: 1px solid #dee2e6;
  background-color: #f8f9fa;
}

.modal-footer {
  border-top: 1px solid #dee2e6;
  background-color: #f8f9fa;
}

.form-label {
  font-weight: 500;
  color: #495057;
}

.text-danger {
  color: #dc3545 !important;
}

.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  display: block;
}

h6.text-muted {
  border-bottom: 1px solid #e9ecef;
  padding-bottom: 0.5rem;
}

.alert {
  border-radius: 0.375rem;
  margin-bottom: 1rem;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}
</style>