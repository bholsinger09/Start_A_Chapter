<template>
  <div class="permission-list">
    <!-- Header with search and filters -->
    <div class="list-header">
      <div class="row align-items-center">
        <div class="col-md-6">
          <h4 class="mb-0">
            <i class="fas fa-key text-primary me-2"></i>
            Permissions Management
          </h4>
          <p class="text-muted mb-0">Manage system permissions and access control</p>
        </div>
        <div class="col-md-6 text-end">
          <button 
            v-if="canCreate"
            class="btn btn-primary"
            @click="$emit('create-permission')"
          >
            <i class="fas fa-plus me-2"></i>
            New Permission
          </button>
        </div>
      </div>
    </div>
    
    <!-- Search and Filters -->
    <div class="filters-section mt-4">
      <div class="row g-3">
        <div class="col-md-4">
          <div class="input-group">
            <span class="input-group-text">
              <i class="fas fa-search"></i>
            </span>
            <input
              v-model="searchTerm"
              type="text"
              class="form-control"
              placeholder="Search permissions..."
            />
          </div>
        </div>
        <div class="col-md-3">
          <select v-model="resourceFilter" class="form-select">
            <option value="">All Resources</option>
            <option v-for="resource in availableResources" :key="resource" :value="resource">
              {{ resource }}
            </option>
          </select>
        </div>
        <div class="col-md-3">
          <select v-model="actionFilter" class="form-select">
            <option value="">All Actions</option>
            <option v-for="action in availableActions" :key="action" :value="action">
              {{ action }}
            </option>
          </select>
        </div>
        <div class="col-md-2">
          <select v-model="typeFilter" class="form-select">
            <option value="">All Types</option>
            <option value="system">System</option>
            <option value="custom">Custom</option>
          </select>
        </div>
      </div>
      
      <!-- Active Filters Display -->
      <div class="active-filters mt-3" v-if="hasActiveFilters">
        <span class="filter-label">Active filters:</span>
        <span 
          v-if="searchTerm" 
          class="filter-badge"
          @click="searchTerm = ''"
        >
          Search: "{{ searchTerm }}"
          <i class="fas fa-times ms-1"></i>
        </span>
        <span 
          v-if="resourceFilter" 
          class="filter-badge"
          @click="resourceFilter = ''"
        >
          Resource: {{ resourceFilter }}
          <i class="fas fa-times ms-1"></i>
        </span>
        <span 
          v-if="actionFilter" 
          class="filter-badge"
          @click="actionFilter = ''"
        >
          Action: {{ actionFilter }}
          <i class="fas fa-times ms-1"></i>
        </span>
        <span 
          v-if="typeFilter" 
          class="filter-badge"
          @click="typeFilter = ''"
        >
          Type: {{ typeFilter }}
          <i class="fas fa-times ms-1"></i>
        </span>
        <button 
          class="btn btn-link btn-sm p-0 ms-2"
          @click="clearAllFilters"
        >
          Clear all
        </button>
      </div>
    </div>
    
    <!-- Sorting and View Options -->
    <div class="view-controls mt-3">
      <div class="row align-items-center">
        <div class="col-md-6">
          <div class="d-flex align-items-center gap-3">
            <div class="sort-controls">
              <label class="form-label mb-0 me-2">Sort by:</label>
              <select v-model="sortBy" class="form-select form-select-sm" style="width: auto;">
                <option value="name">Name</option>
                <option value="resource">Resource</option>
                <option value="action">Action</option>
                <option value="createdAt">Created Date</option>
                <option value="usage">Usage Count</option>
              </select>
              <button 
                class="btn btn-outline-secondary btn-sm ms-1"
                @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'"
              >
                <i :class="`fas fa-sort-${sortOrder === 'asc' ? 'up' : 'down'}`"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="col-md-6 text-end">
          <div class="view-toggle">
            <div class="btn-group" role="group">
              <button 
                class="btn btn-outline-secondary btn-sm"
                :class="{ active: viewMode === 'grid' }"
                @click="viewMode = 'grid'"
              >
                <i class="fas fa-th-large"></i>
              </button>
              <button 
                class="btn btn-outline-secondary btn-sm"
                :class="{ active: viewMode === 'list' }"
                @click="viewMode = 'list'"
              >
                <i class="fas fa-list"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading permissions...</span>
      </div>
      <p class="mt-2 text-muted">Loading permissions...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="filteredPermissions.length === 0" class="empty-state text-center py-5">
      <div class="empty-icon mb-3">
        <i class="fas fa-key fa-3x text-muted"></i>
      </div>
      <h5 class="text-muted">No permissions found</h5>
      <p class="text-muted">
        {{ permissions.length === 0 ? 'No permissions have been created yet.' : 'No permissions match your current filters.' }}
      </p>
      <button 
        v-if="canCreate && permissions.length === 0"
        class="btn btn-primary"
        @click="$emit('create-permission')"
      >
        <i class="fas fa-plus me-2"></i>
        Create First Permission
      </button>
      <button 
        v-else-if="hasActiveFilters"
        class="btn btn-outline-primary"
        @click="clearAllFilters"
      >
        Clear Filters
      </button>
    </div>
    
    <!-- Permissions Grid/List -->
    <div v-else class="permissions-container mt-4">
      <!-- Results Summary -->
      <div class="results-summary mb-3">
        <small class="text-muted">
          Showing {{ filteredPermissions.length }} of {{ permissions.length }} permissions
        </small>
      </div>
      
      <!-- Grid View -->
      <div v-if="viewMode === 'grid'" class="permissions-grid">
        <div class="row">
          <div 
            v-for="permission in paginatedPermissions" 
            :key="permission.id"
            class="col-lg-4 col-md-6 mb-3"
          >
            <PermissionCard
              :permission="permission"
              :roles="roles"
              :usage-stats="getUsageStats(permission)"
              :show-actions="showActions"
              :can-edit="canEdit"
              :can-delete="canDelete"
              @view-details="$emit('view-details', $event)"
              @edit="$emit('edit-permission', $event)"
              @delete="$emit('delete-permission', $event)"
              @role-clicked="$emit('role-clicked', $event)"
            />
          </div>
        </div>
      </div>
      
      <!-- List View -->
      <div v-else class="permissions-table">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead class="table-light">
              <tr>
                <th @click="setSortBy('name')" class="sortable">
                  Name
                  <i v-if="sortBy === 'name'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th @click="setSortBy('resource')" class="sortable">
                  Resource
                  <i v-if="sortBy === 'resource'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th @click="setSortBy('action')" class="sortable">
                  Action
                  <i v-if="sortBy === 'action'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th>Roles</th>
                <th @click="setSortBy('createdAt')" class="sortable">
                  Created
                  <i v-if="sortBy === 'createdAt'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th v-if="showActions">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="permission in paginatedPermissions" :key="permission.id">
                <td>
                  <div class="d-flex align-items-center">
                    <span class="fw-medium">{{ permission.name }}</span>
                    <span 
                      v-if="permission.isSystemPermission" 
                      class="badge bg-warning text-dark ms-2"
                      title="System Permission"
                    >
                      <i class="fas fa-shield-alt"></i>
                    </span>
                  </div>
                  <small class="text-muted d-block">{{ permission.description }}</small>
                </td>
                <td>
                  <span class="resource-tag">{{ permission.resource }}</span>
                </td>
                <td>
                  <span class="action-tag">{{ permission.action }}</span>
                </td>
                <td>
                  <div class="roles-preview">
                    <span 
                      v-for="role in getRolesUsingPermission(permission).slice(0, 2)" 
                      :key="role.id"
                      class="role-badge me-1"
                      :class="getRoleBadgeClass(role)"
                      @click="$emit('role-clicked', role)"
                    >
                      {{ role.name }}
                    </span>
                    <span 
                      v-if="getRolesUsingPermission(permission).length > 2"
                      class="text-muted small"
                    >
                      +{{ getRolesUsingPermission(permission).length - 2 }} more
                    </span>
                  </div>
                </td>
                <td>
                  <small>{{ formatDate(permission.createdAt) }}</small>
                </td>
                <td v-if="showActions">
                  <div class="action-buttons">
                    <button 
                      class="btn btn-outline-primary btn-sm me-1"
                      @click="$emit('view-details', permission)"
                      title="View Details"
                    >
                      <i class="fas fa-eye"></i>
                    </button>
                    <button 
                      v-if="canEdit"
                      class="btn btn-outline-secondary btn-sm me-1"
                      @click="$emit('edit-permission', permission)"
                      title="Edit Permission"
                    >
                      <i class="fas fa-edit"></i>
                    </button>
                    <button 
                      v-if="canDelete && !permission.isSystemPermission"
                      class="btn btn-outline-danger btn-sm"
                      @click="confirmDelete(permission)"
                      title="Delete Permission"
                    >
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <!-- Pagination -->
      <nav v-if="totalPages > 1" class="mt-4">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="goToPage(currentPage - 1)">Previous</button>
          </li>
          <li 
            v-for="page in visiblePages" 
            :key="page"
            class="page-item" 
            :class="{ active: page === currentPage }"
          >
            <button class="page-link" @click="goToPage(page)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="goToPage(currentPage + 1)">Next</button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import PermissionCard from './PermissionCard.vue'

export default {
  name: 'PermissionList',
  components: {
    PermissionCard
  },
  props: {
    permissions: {
      type: Array,
      default: () => []
    },
    roles: {
      type: Array,
      default: () => []
    },
    usageStats: {
      type: Object,
      default: () => ({})
    },
    loading: {
      type: Boolean,
      default: false
    },
    showActions: {
      type: Boolean,
      default: true
    },
    canCreate: {
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
    },
    itemsPerPage: {
      type: Number,
      default: 12
    }
  },
  emits: [
    'create-permission',
    'edit-permission',
    'delete-permission',
    'view-details',
    'role-clicked'
  ],
  setup(props, { emit }) {
    const searchTerm = ref('')
    const resourceFilter = ref('')
    const actionFilter = ref('')
    const typeFilter = ref('')
    const sortBy = ref('name')
    const sortOrder = ref('asc')
    const viewMode = ref('grid')
    const currentPage = ref(1)
    
    // Available filter options
    const availableResources = computed(() => {
      const resources = new Set(props.permissions.map(p => p.resource))
      return Array.from(resources).sort()
    })
    
    const availableActions = computed(() => {
      const actions = new Set(props.permissions.map(p => p.action))
      return Array.from(actions).sort()
    })
    
    // Filter logic
    const hasActiveFilters = computed(() => {
      return searchTerm.value || resourceFilter.value || actionFilter.value || typeFilter.value
    })
    
    const filteredPermissions = computed(() => {
      let filtered = [...props.permissions]
      
      // Search filter
      if (searchTerm.value) {
        const term = searchTerm.value.toLowerCase()
        filtered = filtered.filter(permission =>
          permission.name.toLowerCase().includes(term) ||
          permission.description?.toLowerCase().includes(term) ||
          permission.resource.toLowerCase().includes(term) ||
          permission.action.toLowerCase().includes(term)
        )
      }
      
      // Resource filter
      if (resourceFilter.value) {
        filtered = filtered.filter(permission => 
          permission.resource === resourceFilter.value
        )
      }
      
      // Action filter
      if (actionFilter.value) {
        filtered = filtered.filter(permission => 
          permission.action === actionFilter.value
        )
      }
      
      // Type filter
      if (typeFilter.value) {
        if (typeFilter.value === 'system') {
          filtered = filtered.filter(permission => permission.isSystemPermission)
        } else if (typeFilter.value === 'custom') {
          filtered = filtered.filter(permission => !permission.isSystemPermission)
        }
      }
      
      // Sort
      filtered.sort((a, b) => {
        let aVal, bVal
        
        switch (sortBy.value) {
          case 'name':
            aVal = a.name.toLowerCase()
            bVal = b.name.toLowerCase()
            break
          case 'resource':
            aVal = a.resource.toLowerCase()
            bVal = b.resource.toLowerCase()
            break
          case 'action':
            aVal = a.action.toLowerCase()
            bVal = b.action.toLowerCase()
            break
          case 'createdAt':
            aVal = new Date(a.createdAt)
            bVal = new Date(b.createdAt)
            break
          case 'usage':
            aVal = getRolesUsingPermission(a).length
            bVal = getRolesUsingPermission(b).length
            break
          default:
            aVal = a.name.toLowerCase()
            bVal = b.name.toLowerCase()
        }
        
        if (aVal < bVal) return sortOrder.value === 'asc' ? -1 : 1
        if (aVal > bVal) return sortOrder.value === 'asc' ? 1 : -1
        return 0
      })
      
      return filtered
    })
    
    // Pagination
    const totalPages = computed(() => {
      return Math.ceil(filteredPermissions.value.length / props.itemsPerPage)
    })
    
    const paginatedPermissions = computed(() => {
      const start = (currentPage.value - 1) * props.itemsPerPage
      const end = start + props.itemsPerPage
      return filteredPermissions.value.slice(start, end)
    })
    
    const visiblePages = computed(() => {
      const pages = []
      const maxVisible = 5
      const half = Math.floor(maxVisible / 2)
      
      let start = Math.max(1, currentPage.value - half)
      let end = Math.min(totalPages.value, start + maxVisible - 1)
      
      // Adjust start if we're near the end
      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1)
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      
      return pages
    })
    
    // Helper functions
    const getRolesUsingPermission = (permission) => {
      return props.roles.filter(role =>
        role.permissions && role.permissions.some(p => p.id === permission.id)
      )
    }
    
    const getRoleBadgeClass = (role) => {
      if (role.hierarchyLevel >= 90) return 'badge bg-danger'
      if (role.hierarchyLevel >= 70) return 'badge bg-warning text-dark'
      if (role.hierarchyLevel >= 50) return 'badge bg-info'
      if (role.hierarchyLevel >= 30) return 'badge bg-success'
      return 'badge bg-secondary'
    }
    
    const getUsageStats = (permission) => {
      return props.usageStats[permission.id] || null
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }
    
    const clearAllFilters = () => {
      searchTerm.value = ''
      resourceFilter.value = ''
      actionFilter.value = ''
      typeFilter.value = ''
      currentPage.value = 1
    }
    
    const setSortBy = (field) => {
      if (sortBy.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
      } else {
        sortBy.value = field
        sortOrder.value = 'asc'
      }
      currentPage.value = 1
    }
    
    const goToPage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
      }
    }
    
    const confirmDelete = (permission) => {
      if (confirm(`Are you sure you want to delete the permission "${permission.name}"? This action cannot be undone.`)) {
        emit('delete-permission', permission)
      }
    }
    
    // Reset page when filters change
    watch([searchTerm, resourceFilter, actionFilter, typeFilter], () => {
      currentPage.value = 1
    })
    
    return {
      searchTerm,
      resourceFilter,
      actionFilter,
      typeFilter,
      sortBy,
      sortOrder,
      viewMode,
      currentPage,
      availableResources,
      availableActions,
      hasActiveFilters,
      filteredPermissions,
      totalPages,
      paginatedPermissions,
      visiblePages,
      getRolesUsingPermission,
      getRoleBadgeClass,
      getUsageStats,
      formatDate,
      clearAllFilters,
      setSortBy,
      goToPage,
      confirmDelete
    }
  }
}
</script>

<style scoped>
.permission-list {
  padding: 1.5rem;
}

.list-header {
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 1rem;
}

.filters-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 0.5rem;
  border: 1px solid #dee2e6;
}

.active-filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
}

.filter-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #495057;
}

.filter-badge {
  background: #007bff;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  font-size: 0.75rem;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  transition: background-color 0.2s ease;
}

.filter-badge:hover {
  background: #0056b3;
}

.view-controls {
  background: white;
  padding: 1rem;
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
}

.sort-controls {
  display: flex;
  align-items: center;
}

.view-toggle .btn-group .btn {
  padding: 0.375rem 0.75rem;
}

.empty-state {
  background: #f8f9fa;
  border-radius: 0.5rem;
  padding: 3rem 1.5rem;
  margin: 2rem 0;
}

.empty-icon {
  opacity: 0.5;
}

.permissions-grid .row {
  margin: 0;
}

.permissions-grid .row > [class*="col-"] {
  padding: 0.5rem;
}

.permissions-table .table th.sortable {
  cursor: pointer;
  user-select: none;
  position: relative;
}

.permissions-table .table th.sortable:hover {
  background-color: #e9ecef;
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

.roles-preview {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.25rem;
}

.action-buttons {
  display: flex;
  gap: 0.25rem;
}

.results-summary {
  padding: 0.5rem 0;
  border-bottom: 1px solid #dee2e6;
}

/* Responsive design */
@media (max-width: 768px) {
  .permission-list {
    padding: 1rem;
  }
  
  .view-controls .row {
    flex-direction: column;
    gap: 1rem;
  }
  
  .view-controls .col-md-6 {
    text-align: left !important;
  }
  
  .permissions-grid .row > [class*="col-"] {
    padding: 0.25rem;
  }
  
  .permissions-table {
    font-size: 0.875rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}

@media (max-width: 576px) {
  .filters-section .row {
    gap: 0.5rem;
  }
  
  .filters-section .row > [class*="col-"] {
    flex: 0 0 100%;
    max-width: 100%;
  }
  
  .list-header .row {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
}
</style>