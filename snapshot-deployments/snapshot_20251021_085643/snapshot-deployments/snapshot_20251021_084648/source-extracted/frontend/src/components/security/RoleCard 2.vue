<template>
  <div class="role-card" :class="{ 'system-role': role.isSystemRole }">
    <div class="card h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="role-info">
          <h6 class="card-title mb-1">{{ role.name }}</h6>
          <div class="role-meta">
            <span 
              class="hierarchy-badge"
              :class="getHierarchyBadgeClass(role.hierarchyLevel)"
            >
              Level {{ role.hierarchyLevel }}
            </span>
            <span 
              v-if="role.roleType"
              class="badge bg-secondary ms-2"
            >
              {{ formatRoleType(role.roleType) }}
            </span>
          </div>
        </div>
        <div class="role-badges">
          <span 
            v-if="role.isSystemRole" 
            class="badge bg-warning text-dark"
            title="System Role"
          >
            <i class="fas fa-shield-alt"></i> System
          </span>
          <span 
            class="badge bg-primary"
            :title="`${role.permissions?.length || 0} permission(s)`"
          >
            {{ role.permissions?.length || 0 }} perms
          </span>
          <span 
            class="badge bg-info"
            :title="`${usersWithRole.length} user(s) assigned`"
          >
            {{ usersWithRole.length }} users
          </span>
        </div>
      </div>
      
      <div class="card-body">
        <p class="card-text" v-if="role.description">
          {{ role.description }}
        </p>
        
        <div class="role-details">
          <div class="detail-row">
            <span class="detail-label">Type:</span>
            <span class="detail-value">
              <span class="type-tag">{{ formatRoleType(role.roleType) }}</span>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Hierarchy:</span>
            <span class="detail-value">
              <span class="hierarchy-info">
                Level {{ role.hierarchyLevel }}
                <small class="text-muted">({{ getHierarchyDescription(role.hierarchyLevel) }})</small>
              </span>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Created:</span>
            <span class="detail-value">{{ formatDate(role.createdAt) }}</span>
          </div>
          <div class="detail-row" v-if="role.updatedAt">
            <span class="detail-label">Updated:</span>
            <span class="detail-value">{{ formatDate(role.updatedAt) }}</span>
          </div>
        </div>
        
        <!-- Permissions Section -->
        <div class="permissions-section mt-3" v-if="role.permissions && role.permissions.length > 0">
          <h6 class="section-title">Permissions ({{ role.permissions.length }}):</h6>
          <div class="permissions-list">
            <span 
              v-for="permission in role.permissions.slice(0, showAllPermissions ? role.permissions.length : 4)" 
              :key="permission.id"
              class="permission-tag"
              @click="$emit('permission-clicked', permission)"
              :title="permission.description"
            >
              {{ permission.resource }}:{{ permission.action }}
            </span>
            <button 
              v-if="role.permissions.length > 4 && !showAllPermissions"
              class="btn btn-link btn-sm p-0"
              @click="showAllPermissions = true"
            >
              +{{ role.permissions.length - 4 }} more
            </button>
          </div>
        </div>
        
        <!-- Parent Roles -->
        <div class="parent-roles-section mt-3" v-if="parentRoles.length > 0">
          <h6 class="section-title">Inherits from:</h6>
          <div class="parent-roles-list">
            <span 
              v-for="parentRole in parentRoles" 
              :key="parentRole.id"
              class="role-badge"
              :class="getHierarchyBadgeClass(parentRole.hierarchyLevel)"
              @click="$emit('role-clicked', parentRole)"
            >
              {{ parentRole.name }}
              <small>({{ parentRole.hierarchyLevel }})</small>
            </span>
          </div>
        </div>
        
        <!-- Child Roles -->
        <div class="child-roles-section mt-3" v-if="childRoles.length > 0">
          <h6 class="section-title">Parent to:</h6>
          <div class="child-roles-list">
            <span 
              v-for="childRole in childRoles" 
              :key="childRole.id"
              class="role-badge"
              :class="getHierarchyBadgeClass(childRole.hierarchyLevel)"
              @click="$emit('role-clicked', childRole)"
            >
              {{ childRole.name }}
              <small>({{ childRole.hierarchyLevel }})</small>
            </span>
          </div>
        </div>
        
        <!-- Usage Statistics -->
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
            <div class="stat-item" v-if="usageStats.chapters">
              <span class="stat-label">Chapters:</span>
              <span class="stat-value">{{ usageStats.chapters }}</span>
            </div>
            <div class="stat-item" v-if="usageStats.expiredAssignments">
              <span class="stat-label">Expired:</span>
              <span class="stat-value text-warning">{{ usageStats.expiredAssignments }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="card-footer" v-if="showActions">
        <div class="d-flex justify-content-between align-items-center">
          <div class="role-actions-left">
            <button 
              class="btn btn-outline-info btn-sm me-2"
              @click="$emit('view-users', role)"
              title="View Users with this Role"
            >
              <i class="fas fa-users"></i>
              <span class="d-none d-md-inline ms-1">Users</span>
            </button>
            <button 
              class="btn btn-outline-secondary btn-sm"
              @click="$emit('view-permissions', role)"
              title="View Role Permissions"
            >
              <i class="fas fa-key"></i>
              <span class="d-none d-md-inline ms-1">Permissions</span>
            </button>
          </div>
          
          <div class="role-actions-right">
            <button 
              class="btn btn-outline-primary btn-sm me-1"
              @click="$emit('view-details', role)"
              title="View Details"
            >
              <i class="fas fa-eye"></i>
            </button>
            <button 
              v-if="canEdit"
              class="btn btn-outline-secondary btn-sm me-1"
              @click="$emit('edit', role)"
              title="Edit Role"
            >
              <i class="fas fa-edit"></i>
            </button>
            <button 
              v-if="canDelete && !role.isSystemRole"
              class="btn btn-outline-danger btn-sm"
              @click="confirmDelete"
              title="Delete Role"
            >
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'RoleCard',
  props: {
    role: {
      type: Object,
      required: true
    },
    allRoles: {
      type: Array,
      default: () => []
    },
    userRoles: {
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
  emits: [
    'view-details', 
    'edit', 
    'delete', 
    'role-clicked', 
    'permission-clicked',
    'view-users',
    'view-permissions'
  ],
  setup(props, { emit }) {
    const showAllPermissions = ref(false)
    
    const usersWithRole = computed(() => {
      return props.userRoles.filter(userRole => 
        userRole.roleId === props.role.id && 
        !userRole.isRevoked &&
        (!userRole.expiresAt || new Date(userRole.expiresAt) > new Date())
      )
    })
    
    const parentRoles = computed(() => {
      // Find roles with higher hierarchy levels that could be parents
      return props.allRoles.filter(role => 
        role.hierarchyLevel > props.role.hierarchyLevel &&
        role.id !== props.role.id
      ).slice(0, 3) // Limit to prevent UI overflow
    })
    
    const childRoles = computed(() => {
      // Find roles with lower hierarchy levels that could be children
      return props.allRoles.filter(role => 
        role.hierarchyLevel < props.role.hierarchyLevel &&
        role.id !== props.role.id
      ).slice(0, 3) // Limit to prevent UI overflow
    })
    
    const getHierarchyBadgeClass = (level) => {
      if (level >= 90) return 'badge bg-danger'
      if (level >= 70) return 'badge bg-warning text-dark'
      if (level >= 50) return 'badge bg-info'
      if (level >= 30) return 'badge bg-success'
      return 'badge bg-secondary'
    }
    
    const getHierarchyDescription = (level) => {
      if (level >= 90) return 'Executive'
      if (level >= 70) return 'Administrative'
      if (level >= 50) return 'Managerial'
      if (level >= 30) return 'Supervisory'
      if (level >= 10) return 'Standard'
      return 'Basic'
    }
    
    const formatRoleType = (type) => {
      if (!type) return 'Standard'
      return type.charAt(0).toUpperCase() + type.slice(1).toLowerCase()
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }
    
    const confirmDelete = () => {
      const userCount = usersWithRole.value.length
      let message = `Are you sure you want to delete the role "${props.role.name}"?`
      
      if (userCount > 0) {
        message += `\n\nThis role is currently assigned to ${userCount} user(s). Deleting it will remove these assignments.`
      }
      
      message += '\n\nThis action cannot be undone.'
      
      if (confirm(message)) {
        emit('delete', props.role)
      }
    }
    
    return {
      showAllPermissions,
      usersWithRole,
      parentRoles,
      childRoles,
      getHierarchyBadgeClass,
      getHierarchyDescription,
      formatRoleType,
      formatDate,
      confirmDelete
    }
  }
}
</script>

<style scoped>
.role-card {
  margin-bottom: 1rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.role-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.system-role {
  border-left: 4px solid #ffc107;
}

.card-title {
  font-weight: 600;
  color: #495057;
}

.role-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.hierarchy-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
}

.role-badges .badge {
  margin-left: 0.25rem;
  font-size: 0.7rem;
}

.role-details {
  background: #f8f9fa;
  border-radius: 0.375rem;
  padding: 0.75rem;
  margin-bottom: 1rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.875rem;
  flex-shrink: 0;
  width: 80px;
}

.detail-value {
  font-size: 0.875rem;
  color: #495057;
  text-align: right;
  flex-grow: 1;
}

.type-tag {
  background: #e9ecef;
  padding: 0.125rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.8rem;
  color: #495057;
}

.hierarchy-info {
  text-align: right;
}

.section-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #495057;
  margin-bottom: 0.5rem;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 0.25rem;
}

.permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.permission-tag {
  background: #d1ecf1;
  color: #0c5460;
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-family: 'Courier New', monospace;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.permission-tag:hover {
  background: #b6d7ff;
}

.parent-roles-list, .child-roles-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.role-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
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

.role-actions-left, .role-actions-right {
  display: flex;
  align-items: center;
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
  
  .detail-label {
    width: auto;
    margin-bottom: 0.25rem;
  }
  
  .detail-value {
    text-align: left;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .card-footer .d-flex {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .role-actions-left, .role-actions-right {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .permissions-list, .parent-roles-list, .child-roles-list {
    flex-direction: column;
  }
  
  .role-meta {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .role-badges {
    margin-top: 0.5rem;
  }
}
</style>