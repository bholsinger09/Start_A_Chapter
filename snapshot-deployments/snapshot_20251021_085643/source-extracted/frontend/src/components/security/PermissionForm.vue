<template>
  <div class="permission-form">
    <form @submit.prevent="handleSubmit" novalidate>
      <div class="form-header mb-4">
        <h4 class="mb-2">
          <i class="fas fa-key text-primary me-2"></i>
          {{ isEditing ? 'Edit Permission' : 'Create New Permission' }}
        </h4>
        <p class="text-muted mb-0">
          {{ isEditing ? 'Update permission details and configuration' : 'Define a new permission for system access control' }}
        </p>
      </div>
      
      <!-- Basic Information -->
      <div class="form-section mb-4">
        <h5 class="section-title">
          <i class="fas fa-info-circle me-2"></i>
          Basic Information
        </h5>
        
        <div class="row g-3">
          <div class="col-md-6">
            <label for="name" class="form-label required">
              Permission Name
            </label>
            <input
              id="name"
              v-model="form.name"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': errors.name }"
              placeholder="e.g., Manage Chapter Events"
              maxlength="100"
              required
              :disabled="isEditing && permission?.isSystemPermission"
            />
            <div v-if="errors.name" class="invalid-feedback">
              {{ errors.name }}
            </div>
            <div class="form-text">
              A clear, descriptive name for this permission
            </div>
          </div>
          
          <div class="col-md-6">
            <label for="type" class="form-label">
              Permission Type
            </label>
            <select
              id="type"
              v-model="form.type"
              class="form-select"
              :disabled="isEditing"
            >
              <option value="FUNCTIONAL">Functional</option>
              <option value="DATA">Data Access</option>
              <option value="ADMINISTRATIVE">Administrative</option>
              <option value="SYSTEM">System</option>
            </select>
            <div class="form-text">
              Categorizes the permission type
            </div>
          </div>
        </div>
        
        <div class="mt-3">
          <label for="description" class="form-label">
            Description
          </label>
          <textarea
            id="description"
            v-model="form.description"
            class="form-control"
            :class="{ 'is-invalid': errors.description }"
            rows="3"
            placeholder="Describe what this permission allows users to do..."
            maxlength="500"
          ></textarea>
          <div v-if="errors.description" class="invalid-feedback">
            {{ errors.description }}
          </div>
          <div class="form-text">
            {{ (form.description?.length || 0) }}/500 characters
          </div>
        </div>
      </div>
      
      <!-- Resource and Action -->
      <div class="form-section mb-4">
        <h5 class="section-title">
          <i class="fas fa-cogs me-2"></i>
          Resource & Action
        </h5>
        
        <div class="row g-3">
          <div class="col-md-6">
            <label for="resource" class="form-label required">
              Resource
            </label>
            <div class="input-group">
              <input
                id="resource"
                v-model="form.resource"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.resource }"
                placeholder="e.g., chapter, event, member"
                list="resource-suggestions"
                required
                :disabled="isEditing && permission?.isSystemPermission"
              />
              <button
                v-if="!isEditing || !permission?.isSystemPermission"
                type="button"
                class="btn btn-outline-secondary dropdown-toggle"
                data-bs-toggle="dropdown"
              >
                <i class="fas fa-chevron-down"></i>
              </button>
              <ul class="dropdown-menu">
                <li v-for="resource in commonResources" :key="resource">
                  <button
                    type="button"
                    class="dropdown-item"
                    @click="form.resource = resource"
                  >
                    {{ resource }}
                  </button>
                </li>
              </ul>
            </div>
            <datalist id="resource-suggestions">
              <option v-for="resource in commonResources" :key="resource" :value="resource" />
            </datalist>
            <div v-if="errors.resource" class="invalid-feedback">
              {{ errors.resource }}
            </div>
            <div class="form-text">
              The system resource this permission controls
            </div>
          </div>
          
          <div class="col-md-6">
            <label for="action" class="form-label required">
              Action
            </label>
            <div class="input-group">
              <input
                id="action"
                v-model="form.action"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.action }"
                placeholder="e.g., create, read, update, delete"
                list="action-suggestions"
                required
                :disabled="isEditing && permission?.isSystemPermission"
              />
              <button
                v-if="!isEditing || !permission?.isSystemPermission"
                type="button"
                class="btn btn-outline-secondary dropdown-toggle"
                data-bs-toggle="dropdown"
              >
                <i class="fas fa-chevron-down"></i>
              </button>
              <ul class="dropdown-menu">
                <li v-for="action in commonActions" :key="action">
                  <button
                    type="button"
                    class="dropdown-item"
                    @click="form.action = action"
                  >
                    {{ action }}
                  </button>
                </li>
              </ul>
            </div>
            <datalist id="action-suggestions">
              <option v-for="action in commonActions" :key="action" :value="action" />
            </datalist>
            <div v-if="errors.action" class="invalid-feedback">
              {{ errors.action }}
            </div>
            <div class="form-text">
              The specific action this permission allows
            </div>
          </div>
        </div>
        
        <!-- Permission Preview -->
        <div class="permission-preview mt-3">
          <div class="alert alert-info">
            <strong>Permission Preview:</strong>
            <code>{{ form.resource || '[resource]' }}:{{ form.action || '[action]' }}</code>
            <div class="mt-1">
              <small>
                This permission will be referenced as: 
                <strong>{{ form.resource && form.action ? `${form.resource}:${form.action}` : 'resource:action' }}</strong>
              </small>
            </div>
          </div>
        </div>
      </div>
      
      <!-- System Permission Settings -->
      <div v-if="!isEditing" class="form-section mb-4">
        <h5 class="section-title">
          <i class="fas fa-shield-alt me-2"></i>
          System Settings
        </h5>
        
        <div class="form-check">
          <input
            id="isSystemPermission"
            v-model="form.isSystemPermission"
            type="checkbox"
            class="form-check-input"
          />
          <label for="isSystemPermission" class="form-check-label">
            <strong>System Permission</strong>
            <div class="form-text">
              System permissions cannot be deleted and are managed by administrators only. 
              Use this for core system functionality.
            </div>
          </label>
        </div>
      </div>
      
      <!-- Role Assignments (for editing) -->
      <div v-if="isEditing && availableRoles.length > 0" class="form-section mb-4">
        <h5 class="section-title">
          <i class="fas fa-users me-2"></i>
          Role Assignments
        </h5>
        
        <div class="roles-assignment">
          <p class="text-muted mb-3">
            Select which roles should have this permission:
          </p>
          
          <div class="row">
            <div v-for="role in availableRoles" :key="role.id" class="col-md-6 mb-2">
              <div class="form-check">
                <input
                  :id="`role-${role.id}`"
                  v-model="form.assignedRoles"
                  type="checkbox"
                  class="form-check-input"
                  :value="role.id"
                />
                <label :for="`role-${role.id}`" class="form-check-label">
                  <div class="d-flex align-items-center">
                    <span class="fw-medium">{{ role.name }}</span>
                    <span 
                      class="badge ms-2"
                      :class="getRoleBadgeClass(role)"
                    >
                      Level {{ role.hierarchyLevel }}
                    </span>
                  </div>
                  <small class="text-muted d-block">{{ role.description }}</small>
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Validation Summary -->
      <div v-if="hasErrors" class="form-section mb-4">
        <div class="alert alert-danger">
          <h6 class="alert-heading">
            <i class="fas fa-exclamation-triangle me-2"></i>
            Please fix the following errors:
          </h6>
          <ul class="mb-0">
            <li v-for="(error, field) in errors" :key="field">
              {{ error }}
            </li>
          </ul>
        </div>
      </div>
      
      <!-- Form Actions -->
      <div class="form-actions">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <button
              type="button"
              class="btn btn-outline-secondary"
              @click="$emit('cancel')"
            >
              <i class="fas fa-times me-2"></i>
              Cancel
            </button>
          </div>
          
          <div class="d-flex gap-2">
            <button
              v-if="isEditing"
              type="button"
              class="btn btn-outline-primary"
              @click="resetForm"
            >
              <i class="fas fa-undo me-2"></i>
              Reset
            </button>
            <button
              type="submit"
              class="btn btn-primary"
              :disabled="submitting || hasErrors"
            >
              <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else :class="`fas ${isEditing ? 'fa-save' : 'fa-plus'} me-2`"></i>
              {{ isEditing ? 'Update Permission' : 'Create Permission' }}
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'

export default {
  name: 'PermissionForm',
  props: {
    permission: {
      type: Object,
      default: null
    },
    availableRoles: {
      type: Array,
      default: () => []
    },
    submitting: {
      type: Boolean,
      default: false
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const form = ref({
      name: '',
      description: '',
      resource: '',
      action: '',
      type: 'FUNCTIONAL',
      isSystemPermission: false,
      assignedRoles: []
    })
    
    const errors = ref({})
    
    const isEditing = computed(() => !!props.permission)
    
    const hasErrors = computed(() => Object.keys(errors.value).length > 0)
    
    const commonResources = [
      'chapter',
      'event',
      'member',
      'role',
      'permission',
      'user',
      'report',
      'notification',
      'setting',
      'audit'
    ]
    
    const commonActions = [
      'create',
      'read',
      'update',
      'delete',
      'manage',
      'view',
      'edit',
      'list',
      'assign',
      'revoke',
      'approve',
      'reject'
    ]
    
    const validateForm = () => {
      errors.value = {}
      
      // Name validation
      if (!form.value.name?.trim()) {
        errors.value.name = 'Permission name is required'
      } else if (form.value.name.length < 3) {
        errors.value.name = 'Permission name must be at least 3 characters'
      } else if (form.value.name.length > 100) {
        errors.value.name = 'Permission name cannot exceed 100 characters'
      }
      
      // Resource validation
      if (!form.value.resource?.trim()) {
        errors.value.resource = 'Resource is required'
      } else if (!/^[a-z][a-z0-9_]*$/.test(form.value.resource)) {
        errors.value.resource = 'Resource must start with a letter and contain only lowercase letters, numbers, and underscores'
      }
      
      // Action validation
      if (!form.value.action?.trim()) {
        errors.value.action = 'Action is required'
      } else if (!/^[a-z][a-z0-9_]*$/.test(form.value.action)) {
        errors.value.action = 'Action must start with a letter and contain only lowercase letters, numbers, and underscores'
      }
      
      // Description validation
      if (form.value.description && form.value.description.length > 500) {
        errors.value.description = 'Description cannot exceed 500 characters'
      }
      
      return Object.keys(errors.value).length === 0
    }
    
    const getRoleBadgeClass = (role) => {
      if (role.hierarchyLevel >= 90) return 'bg-danger'
      if (role.hierarchyLevel >= 70) return 'bg-warning text-dark'
      if (role.hierarchyLevel >= 50) return 'bg-info'
      if (role.hierarchyLevel >= 30) return 'bg-success'
      return 'bg-secondary'
    }
    
    const resetForm = () => {
      if (props.permission) {
        // Reset to original permission values
        form.value = {
          name: props.permission.name || '',
          description: props.permission.description || '',
          resource: props.permission.resource || '',
          action: props.permission.action || '',
          type: props.permission.type || 'FUNCTIONAL',
          isSystemPermission: props.permission.isSystemPermission || false,
          assignedRoles: []
        }
        
        // Set assigned roles if available
        if (props.availableRoles.length > 0) {
          const assignedRoleIds = props.availableRoles
            .filter(role => role.permissions && role.permissions.some(p => p.id === props.permission.id))
            .map(role => role.id)
          form.value.assignedRoles = assignedRoleIds
        }
      } else {
        // Reset to empty form
        form.value = {
          name: '',
          description: '',
          resource: '',
          action: '',
          type: 'FUNCTIONAL',
          isSystemPermission: false,
          assignedRoles: []
        }
      }
      errors.value = {}
    }
    
    const handleSubmit = () => {
      if (validateForm()) {
        emit('submit', { ...form.value })
      }
    }
    
    // Watch for validation as user types
    watch(
      () => [form.value.name, form.value.resource, form.value.action, form.value.description],
      () => {
        if (Object.keys(errors.value).length > 0) {
          validateForm()
        }
      },
      { deep: true }
    )
    
    // Initialize form when component mounts or permission prop changes
    watch(
      () => props.permission,
      () => {
        resetForm()
      },
      { immediate: true }
    )
    
    onMounted(() => {
      resetForm()
    })
    
    return {
      form,
      errors,
      isEditing,
      hasErrors,
      commonResources,
      commonActions,
      validateForm,
      getRoleBadgeClass,
      resetForm,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.permission-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.form-header {
  text-align: center;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 1.5rem;
}

.form-section {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  padding: 1.5rem;
}

.section-title {
  color: #495057;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #dee2e6;
}

.required::after {
  content: ' *';
  color: #dc3545;
}

.permission-preview {
  background: white;
  border-radius: 0.375rem;
  padding: 1rem;
}

.permission-preview code {
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.9rem;
}

.roles-assignment {
  background: white;
  border-radius: 0.375rem;
  padding: 1rem;
}

.form-check-label {
  cursor: pointer;
  width: 100%;
}

.form-actions {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  padding: 1.5rem;
  margin-top: 2rem;
}

.input-group .dropdown-menu {
  z-index: 1050;
}

.dropdown-item {
  cursor: pointer;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
}

.dropdown-item:hover {
  background-color: #e9ecef;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}

/* Form validation styles */
.is-invalid {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}

.invalid-feedback {
  display: block;
  width: 100%;
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #dc3545;
}

.form-text {
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #6c757d;
}

/* Responsive design */
@media (max-width: 768px) {
  .permission-form {
    padding: 1rem;
  }
  
  .form-section {
    padding: 1rem;
  }
  
  .form-actions .d-flex {
    flex-direction: column;
    gap: 1rem;
  }
  
  .form-actions .d-flex > div {
    text-align: center;
  }
}

@media (max-width: 576px) {
  .form-actions .d-flex .d-flex {
    flex-direction: column;
    width: 100%;
  }
  
  .form-actions .btn {
    width: 100%;
    margin-bottom: 0.5rem;
  }
}
</style>