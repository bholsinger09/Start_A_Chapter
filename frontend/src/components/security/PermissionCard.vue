<template>
  <div class="permission-card" :class="{ 'system-permission': permission.isSystemPermission }">
    <div class="card h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="permission-info">
          <h6 class="card-title mb-1">{{ permission.name }}</h6>
          <small class="text-muted">{{ permission.resource }}:{{ permission.action }}</small>
        </div>
        <div class="permission-badges">
          <span 
            v-if="permission.isSystemPermission" 
            class="badge bg-warning text-dark"
            title="System Permission"
          >
            <i class="fas fa-shield-alt"></i> System
          </span>
          <span 
            class="badge bg-primary"
            :title="`Used by ${rolesUsingPermission.length} role(s)`"
          >
            {{ rolesUsingPermission.length }} roles
          </span>
        </div>
      </div>
      
      <div class="card-body">
        <p class="card-text" v-if="permission.description">
          {{ permission.description }}
        </p>
        
        <div class="permission-details">
          <div class="detail-row">
            <span class="detail-label">Resource:</span>
            <span class="detail-value">
              <span class="resource-tag">{{ permission.resource }}</span>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Action:</span>
            <span class="detail-value">
              <span class="action-tag">{{ permission.action }}</span>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Created:</span>
            <span class="detail-value">{{ formatDate(permission.createdAt) }}</span>
          </div>
          <div class="detail-row" v-if="permission.updatedAt">
            <span class="detail-label">Updated:</span>
            <span class="detail-value">{{ formatDate(permission.updatedAt) }}</span>
          </div>
        </div>
        
        <!-- Roles using this permission -->
        <div class="roles-section mt-3" v-if="rolesUsingPermission.length > 0">
          <h6 class="section-title">Used by Roles:</h6>
          <div class="roles-list">
            <span 
              v-for="role in rolesUsingPermission.slice(0, showAllRoles ? rolesUsingPermission.length : 3)" 
              :key="role.id"
              class="role-badge"
              :class="getRoleBadgeClass(role)"
              @click="$emit('role-clicked', role)"
            >
              {{ role.name }}
              <small v-if="role.hierarchyLevel">({{ role.hierarchyLevel }})</small>
            </span>
            <button 
              v-if="rolesUsingPermission.length > 3 && !showAllRoles"
              class="btn btn-link btn-sm p-0"
              @click="showAllRoles = true"
            >
              +{{ rolesUsingPermission.length - 3 }} more
            </button>
          </div>
        </div>
        
        <!-- Usage statistics -->
        <div class="usage-stats mt-3" v-if="usageStats">
          <h6 class="section-title">Usage Statistics:</h6>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-label">Active Users:</span>
              <span class="stat-value">{{ usageStats.activeUsers }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Total Assignments:</span>
              <span class="stat-value">{{ usageStats.totalAssignments }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="card-footer" v-if="showActions">
        <div class="d-flex justify-content-end gap-2">
          <button 
            class="btn btn-outline-primary btn-sm"
            @click="$emit('view-details', permission)"
            title="View Details"
          >
            <i class="fas fa-eye"></i>
          </button>
          <button 
            v-if="canEdit"
            class="btn btn-outline-secondary btn-sm"
            @click="$emit('edit', permission)"
            title="Edit Permission"
          >
            <i class="fas fa-edit"></i>
          </button>
          <button 
            v-if="canDelete && !permission.isSystemPermission"
            class="btn btn-outline-danger btn-sm"
            @click="confirmDelete"
            title="Delete Permission"
          >
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'PermissionCard',
  props: {
    permission: {
      type: Object,
      required: true
    },
    roles: {
      type: Array,
      default: () => []
    },
    usageStats: {
      type: Object,
      default: null
    },
    showActions: {
      type: Boolean,
      default: true
    },
    canEdit: {
      type: Boolean,
      default: true
    },
    canDelete: {
      type: Boolean,
      default: true
    }
  },
  emits: ['view-details', 'edit', 'delete', 'role-clicked'],
  setup(props, { emit }) {
    const showAllRoles = ref(false)
    
    const rolesUsingPermission = computed(() => {
      return props.roles.filter(role => 
        role.permissions && role.permissions.some(p => p.id === props.permission.id)
      )
    })
    
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }
    
    const getRoleBadgeClass = (role) => {
      if (role.hierarchyLevel >= 90) return 'badge bg-danger'
      if (role.hierarchyLevel >= 70) return 'badge bg-warning text-dark'
      if (role.hierarchyLevel >= 50) return 'badge bg-info'
      if (role.hierarchyLevel >= 30) return 'badge bg-success'
      return 'badge bg-secondary'
    }
    
    const confirmDelete = () => {
      if (confirm(`Are you sure you want to delete the permission "${props.permission.name}"? This action cannot be undone.`)) {
        emit('delete', props.permission)
      }
    }
    
    return {
      showAllRoles,
      rolesUsingPermission,
      formatDate,
      getRoleBadgeClass,
      confirmDelete
    }
  }
}
</script>

<style scoped>
.permission-card {
  margin-bottom: 1rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.permission-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.system-permission {
  border-left: 4px solid #ffc107;
}

.card-title {
  font-weight: 600;
  color: #495057;
}

.permission-badges .badge {
  margin-left: 0.25rem;
  font-size: 0.75rem;
}

.permission-details {
  background: #f8f9fa;
  border-radius: 0.375rem;
  padding: 0.75rem;
  margin-bottom: 1rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.875rem;
}

.detail-value {
  font-size: 0.875rem;
  color: #495057;
}

.resource-tag, .action-tag {
  background: #e9ecef;
  padding: 0.125rem 0.5rem;
  border-radius: 0.25rem;
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
}

.resource-tag {
  background: #d1ecf1;
  color: #0c5460;
}

.action-tag {
  background: #d4edda;
  color: #155724;
}

.section-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #495057;
  margin-bottom: 0.5rem;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 0.25rem;
}

.roles-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.role-badge {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.role-badge:hover {
  opacity: 0.8;
}

.usage-stats {
  background: #f8f9fa;
  border-radius: 0.375rem;
  padding: 0.75rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 500;
}

.stat-value {
  font-size: 0.875rem;
  color: #495057;
  font-weight: 600;
}

.card-footer {
  background: transparent;
  border-top: 1px solid #dee2e6;
  padding: 0.75rem 1.25rem;
}

.btn-sm {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
}

.btn-link {
  color: #007bff;
  text-decoration: none;
}

.btn-link:hover {
  text-decoration: underline;
}

/* Responsive design */
@media (max-width: 768px) {
  .detail-row {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .roles-list {
    flex-direction: column;
  }
}
</style>