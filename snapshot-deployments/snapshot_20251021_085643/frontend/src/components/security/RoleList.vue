<template>
  <div class="role-list">
    <!-- Header with search and filters -->
    <div class="list-header">
      <div class="row align-items-center">
        <div class="col-md-6">
          <h4 class="mb-0">
            <i class="fas fa-users-cog text-primary me-2"></i>
            Role Management
          </h4>
          <p class="text-muted mb-0">Manage user roles and hierarchy</p>
        </div>
        <div class="col-md-6 text-end">
          <button 
            v-if="canCreate"
            class="btn btn-primary"
            @click="$emit('create-role')"
          >
            <i class="fas fa-plus me-2"></i>
            New Role
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
              placeholder="Search roles..."
            />
          </div>
        </div>
        <div class="col-md-3">
          <select v-model="typeFilter" class="form-select">
            <option value="">All Types</option>
            <option value="CHAPTER_ADMIN">Chapter Admin</option>
            <option value="CHAPTER_OFFICER">Chapter Officer</option>
            <option value="CHAPTER_MEMBER">Chapter Member</option>
            <option value="SYSTEM_ADMIN">System Admin</option>
            <option value="GUEST">Guest</option>
          </select>
        </div>
        <div class="col-md-3">
          <select v-model="hierarchyFilter" class="form-select">
            <option value="">All Levels</option>
            <option value="executive">Executive (90+)</option>
            <option value="administrative">Administrative (70-89)</option>
            <option value="managerial">Managerial (50-69)</option>
            <option value="supervisory">Supervisory (30-49)</option>
            <option value="standard">Standard (10-29)</option>
            <option value="basic">Basic (0-9)</option>
          </select>
        </div>
        <div class="col-md-2">
          <select v-model="systemFilter" class="form-select">
            <option value="">All Roles</option>
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
          v-if="typeFilter" 
          class="filter-badge"
          @click="typeFilter = ''"
        >
          Type: {{ formatRoleType(typeFilter) }}
          <i class="fas fa-times ms-1"></i>
        </span>
        <span 
          v-if="hierarchyFilter" 
          class="filter-badge"
          @click="hierarchyFilter = ''"
        >
          Level: {{ formatHierarchyFilter(hierarchyFilter) }}
          <i class="fas fa-times ms-1"></i>
        </span>
        <span 
          v-if="systemFilter" 
          class="filter-badge"
          @click="systemFilter = ''"
        >
          Type: {{ systemFilter }}
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
                <option value="hierarchyLevel">Hierarchy Level</option>
                <option value="roleType">Role Type</option>
                <option value="createdAt">Created Date</option>
                <option value="userCount">User Count</option>
                <option value="permissionCount">Permission Count</option>
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
              <button 
                class="btn btn-outline-secondary btn-sm"
                :class="{ active: viewMode === 'hierarchy' }"
                @click="viewMode = 'hierarchy'"
              >
                <i class="fas fa-sitemap"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading roles...</span>
      </div>
      <p class="mt-2 text-muted">Loading roles...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="filteredRoles.length === 0" class="empty-state text-center py-5">
      <div class="empty-icon mb-3">
        <i class="fas fa-users-cog fa-3x text-muted"></i>
      </div>
      <h5 class="text-muted">No roles found</h5>
      <p class="text-muted">
        {{ roles.length === 0 ? 'No roles have been created yet.' : 'No roles match your current filters.' }}
      </p>
      <button 
        v-if="canCreate && roles.length === 0"
        class="btn btn-primary"
        @click="$emit('create-role')"
      >
        <i class="fas fa-plus me-2"></i>
        Create First Role
      </button>
      <button 
        v-else-if="hasActiveFilters"
        class="btn btn-outline-primary"
        @click="clearAllFilters"
      >
        Clear Filters
      </button>
    </div>
    
    <!-- Roles Display -->
    <div v-else class="roles-container mt-4">
      <!-- Results Summary -->
      <div class="results-summary mb-3">
        <small class="text-muted">
          Showing {{ filteredRoles.length }} of {{ roles.length }} roles
        </small>
      </div>
      
      <!-- Grid View -->
      <div v-if="viewMode === 'grid'" class="roles-grid">
        <div class="row">
          <div 
            v-for="role in paginatedRoles" 
            :key="role.id"
            class="col-lg-4 col-md-6 mb-3"
          >
            <RoleCard
              :role="role"
              :all-roles="roles"
              :user-roles="userRoles"
              :usage-stats="getUsageStats(role)"
              :show-actions="showActions"
              :can-edit="canEdit"
              :can-delete="canDelete"
              @view-details="$emit('view-details', $event)"
              @edit="$emit('edit-role', $event)"
              @delete="$emit('delete-role', $event)"
              @role-clicked="$emit('role-clicked', $event)"
              @permission-clicked="$emit('permission-clicked', $event)"
              @view-users="$emit('view-users', $event)"
              @view-permissions="$emit('view-permissions', $event)"
            />
          </div>
        </div>
      </div>
      
      <!-- List View -->
      <div v-else-if="viewMode === 'list'" class="roles-table">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead class="table-light">
              <tr>
                <th @click="setSortBy('name')" class="sortable">
                  Name
                  <i v-if="sortBy === 'name'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th @click="setSortBy('roleType')" class="sortable">
                  Type
                  <i v-if="sortBy === 'roleType'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th @click="setSortBy('hierarchyLevel')" class="sortable">
                  Level
                  <i v-if="sortBy === 'hierarchyLevel'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th>Permissions</th>
                <th>Users</th>
                <th @click="setSortBy('createdAt')" class="sortable">
                  Created
                  <i v-if="sortBy === 'createdAt'" :class="`fas fa-sort-${sortOrder}`"></i>
                </th>
                <th v-if="showActions">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="role in paginatedRoles" :key="role.id">
                <td>
                  <div class="d-flex align-items-center">
                    <span class="fw-medium">{{ role.name }}</span>
                    <span 
                      v-if="role.isSystemRole" 
                      class="badge bg-warning text-dark ms-2"
                      title="System Role"
                    >
                      <i class="fas fa-shield-alt"></i>
                    </span>
                  </div>
                  <small class="text-muted d-block">{{ role.description }}</small>
                </td>
                <td>
                  <span class="type-tag">{{ formatRoleType(role.roleType) }}</span>
                </td>
                <td>
                  <span 
                    class="hierarchy-badge"
                    :class="getHierarchyBadgeClass(role.hierarchyLevel)"
                  >
                    {{ role.hierarchyLevel }}
                  </span>
                  <small class="text-muted d-block">{{ getHierarchyDescription(role.hierarchyLevel) }}</small>
                </td>
                <td>
                  <span class="badge bg-primary">
                    {{ role.permissions?.length || 0 }}
                  </span>
                </td>
                <td>
                  <span class="badge bg-info">
                    {{ getUsersWithRole(role).length }}
                  </span>
                </td>
                <td>
                  <small>{{ formatDate(role.createdAt) }}</small>
                </td>
                <td v-if="showActions">
                  <div class="action-buttons">
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
                      @click="$emit('edit-role', role)"
                      title="Edit Role"
                    >
                      <i class="fas fa-edit"></i>
                    </button>
                    <button 
                      v-if="canDelete && !role.isSystemRole"
                      class="btn btn-outline-danger btn-sm"
                      @click="confirmDelete(role)"
                      title="Delete Role"
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
      
      <!-- Hierarchy View -->
      <div v-else-if="viewMode === 'hierarchy'" class="roles-hierarchy">
        <div class="hierarchy-container">
          <div 
            v-for="level in hierarchyLevels" 
            :key="level.name"
            class="hierarchy-level"
          >
            <h6 class="level-title">
              <span 
                class="level-badge"
                :class="level.badgeClass"
              >
                {{ level.name }} ({{ level.range }})
              </span>
              <small class="text-muted ms-2">{{ level.roles.length }} roles</small>
            </h6>
            <div class="level-roles">
              <div class="row">
                <div 
                  v-for="role in level.roles" 
                  :key="role.id"
                  class="col-lg-4 col-md-6 mb-2"
                >
                  <div 
                    class="role-hierarchy-card"
                    @click="$emit('role-clicked', role)"
                  >
                    <div class="d-flex justify-content-between align-items-center">
                      <div>
                        <span class="fw-medium">{{ role.name }}</span>
                        <span 
                          v-if="role.isSystemRole" 
                          class="badge bg-warning text-dark ms-1"
                          title="System Role"
                        >
                          <i class="fas fa-shield-alt"></i>
                        </span>
                      </div>
                      <div class="role-stats">
                        <small class="text-muted">
                          {{ role.permissions?.length || 0 }}p / {{ getUsersWithRole(role).length }}u
                        </small>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Pagination -->
      <nav v-if="totalPages > 1 && viewMode !== 'hierarchy'" class="mt-4">
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
import RoleCard from './RoleCard.vue'

export default {
  name: 'RoleList',
  components: {
    RoleCard
  },
  props: {
    roles: {
      type: Array,
      default: () => []
    },
    userRoles: {
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
    'create-role',
    'edit-role',
    'delete-role',
    'view-details',
    'role-clicked',
    'permission-clicked',
    'view-users',
    'view-permissions'
  ],
  setup(props, { emit }) {
    const searchTerm = ref('')
    const typeFilter = ref('')
    const hierarchyFilter = ref('')
    const systemFilter = ref('')
    const sortBy = ref('hierarchyLevel')
    const sortOrder = ref('desc')
    const viewMode = ref('grid')
    const currentPage = ref(1)
    
    // Filter logic
    const hasActiveFilters = computed(() => {
      return searchTerm.value || typeFilter.value || hierarchyFilter.value || systemFilter.value
    })
    
    const filteredRoles = computed(() => {
      let filtered = [...props.roles]
      
      // Search filter
      if (searchTerm.value) {
        const term = searchTerm.value.toLowerCase()
        filtered = filtered.filter(role =>
          role.name.toLowerCase().includes(term) ||
          role.description?.toLowerCase().includes(term) ||
          role.roleType?.toLowerCase().includes(term)
        )
      }
      
      // Type filter
      if (typeFilter.value) {
        filtered = filtered.filter(role => role.roleType === typeFilter.value)
      }
      
      // Hierarchy filter
      if (hierarchyFilter.value) {
        const levelRanges = {
          executive: [90, 100],
          administrative: [70, 89],
          managerial: [50, 69],
          supervisory: [30, 49],
          standard: [10, 29],
          basic: [0, 9]
        }
        const [min, max] = levelRanges[hierarchyFilter.value] || [0, 100]
        filtered = filtered.filter(role => 
          role.hierarchyLevel >= min && role.hierarchyLevel <= max
        )
      }
      
      // System filter
      if (systemFilter.value) {
        if (systemFilter.value === 'system') {
          filtered = filtered.filter(role => role.isSystemRole)
        } else if (systemFilter.value === 'custom') {
          filtered = filtered.filter(role => !role.isSystemRole)
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
          case 'hierarchyLevel':
            aVal = a.hierarchyLevel
            bVal = b.hierarchyLevel
            break
          case 'roleType':
            aVal = a.roleType || ''
            bVal = b.roleType || ''
            break
          case 'createdAt':
            aVal = new Date(a.createdAt)
            bVal = new Date(b.createdAt)
            break
          case 'userCount':
            aVal = getUsersWithRole(a).length
            bVal = getUsersWithRole(b).length
            break
          case 'permissionCount':
            aVal = a.permissions?.length || 0
            bVal = b.permissions?.length || 0
            break
          default:
            aVal = a.hierarchyLevel
            bVal = b.hierarchyLevel
        }
        
        if (aVal < bVal) return sortOrder.value === 'asc' ? -1 : 1
        if (aVal > bVal) return sortOrder.value === 'asc' ? 1 : -1
        return 0
      })
      
      return filtered
    })
    
    // Pagination
    const totalPages = computed(() => {
      return Math.ceil(filteredRoles.value.length / props.itemsPerPage)
    })
    
    const paginatedRoles = computed(() => {
      if (viewMode.value === 'hierarchy') return filteredRoles.value
      
      const start = (currentPage.value - 1) * props.itemsPerPage
      const end = start + props.itemsPerPage
      return filteredRoles.value.slice(start, end)
    })
    
    const visiblePages = computed(() => {
      const pages = []
      const maxVisible = 5
      const half = Math.floor(maxVisible / 2)
      
      let start = Math.max(1, currentPage.value - half)
      let end = Math.min(totalPages.value, start + maxVisible - 1)
      
      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1)
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      
      return pages
    })
    
    // Hierarchy view
    const hierarchyLevels = computed(() => {
      const levels = [
        {
          name: 'Executive',
          range: '90-100',
          badgeClass: 'badge bg-danger',
          roles: filteredRoles.value.filter(r => r.hierarchyLevel >= 90)
        },
        {
          name: 'Administrative',
          range: '70-89',
          badgeClass: 'badge bg-warning text-dark',
          roles: filteredRoles.value.filter(r => r.hierarchyLevel >= 70 && r.hierarchyLevel < 90)
        },
        {
          name: 'Managerial',
          range: '50-69',
          badgeClass: 'badge bg-info',
          roles: filteredRoles.value.filter(r => r.hierarchyLevel >= 50 && r.hierarchyLevel < 70)
        },
        {
          name: 'Supervisory',
          range: '30-49',
          badgeClass: 'badge bg-success',
          roles: filteredRoles.value.filter(r => r.hierarchyLevel >= 30 && r.hierarchyLevel < 50)
        },
        {
          name: 'Standard',
          range: '10-29',
          badgeClass: 'badge bg-secondary',
          roles: filteredRoles.value.filter(r => r.hierarchyLevel >= 10 && r.hierarchyLevel < 30)
        },
        {
          name: 'Basic',
          range: '0-9',
          badgeClass: 'badge bg-light text-dark',
          roles: filteredRoles.value.filter(r => r.hierarchyLevel < 10)
        }
      ]
      
      return levels.filter(level => level.roles.length > 0)
    })
    
    // Helper functions
    const getUsersWithRole = (role) => {
      return props.userRoles.filter(userRole => 
        userRole.roleId === role.id && 
        !userRole.isRevoked &&
        (!userRole.expiresAt || new Date(userRole.expiresAt) > new Date())
      )
    }
    
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
      return type.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase())
    }
    
    const formatHierarchyFilter = (filter) => {
      return filter.charAt(0).toUpperCase() + filter.slice(1)
    }
    
    const getUsageStats = (role) => {
      return props.usageStats[role.id] || null
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
      typeFilter.value = ''
      hierarchyFilter.value = ''
      systemFilter.value = ''
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
    
    const confirmDelete = (role) => {
      const userCount = getUsersWithRole(role).length
      let message = `Are you sure you want to delete the role "${role.name}"?`
      
      if (userCount > 0) {
        message += `\n\nThis role is currently assigned to ${userCount} user(s). Deleting it will remove these assignments.`
      }
      
      message += '\n\nThis action cannot be undone.'
      
      if (confirm(message)) {
        emit('delete-role', role)
      }
    }
    
    // Reset page when filters change
    watch([searchTerm, typeFilter, hierarchyFilter, systemFilter], () => {
      currentPage.value = 1
    })
    
    return {
      searchTerm,
      typeFilter,
      hierarchyFilter,
      systemFilter,
      sortBy,
      sortOrder,
      viewMode,
      currentPage,
      hasActiveFilters,
      filteredRoles,
      totalPages,
      paginatedRoles,
      visiblePages,
      hierarchyLevels,
      getUsersWithRole,
      getHierarchyBadgeClass,
      getHierarchyDescription,
      formatRoleType,
      formatHierarchyFilter,
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
.role-list {
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

.roles-grid .row {
  margin: 0;
}

.roles-grid .row > [class*="col-"] {
  padding: 0.5rem;
}

.roles-table .table th.sortable {
  cursor: pointer;
  user-select: none;
  position: relative;
}

.roles-table .table th.sortable:hover {
  background-color: #e9ecef;
}

.type-tag {
  background: #e9ecef;
  padding: 0.125rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.8rem;
  color: #495057;
}

.hierarchy-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 0.25rem;
}

.results-summary {
  padding: 0.5rem 0;
  border-bottom: 1px solid #dee2e6;
}

/* Hierarchy View Styles */
.roles-hierarchy {
  background: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
}

.hierarchy-level {
  margin-bottom: 2rem;
  border-left: 3px solid #dee2e6;
  padding-left: 1rem;
}

.level-title {
  margin-bottom: 1rem;
  font-weight: 600;
}

.level-badge {
  font-size: 0.875rem;
  padding: 0.375rem 0.75rem;
  border-radius: 0.375rem;
}

.role-hierarchy-card {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 0.75rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.role-hierarchy-card:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.role-stats {
  font-size: 0.75rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .role-list {
    padding: 1rem;
  }
  
  .view-controls .row {
    flex-direction: column;
    gap: 1rem;
  }
  
  .view-controls .col-md-6 {
    text-align: left !important;
  }
  
  .roles-grid .row > [class*="col-"] {
    padding: 0.25rem;
  }
  
  .roles-table {
    font-size: 0.875rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .hierarchy-level {
    padding-left: 0.5rem;
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
  
  .view-toggle {
    display: flex;
    justify-content: center;
  }
}
</style>