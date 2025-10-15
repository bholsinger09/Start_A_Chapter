import { ref, computed } from 'vue'
import { useEventAPI } from './useEventAPI.js'

/**
 * Vue 3 Composition API for Security Management
 * Handles permissions, roles, user assignments, and security monitoring
 */
export function useSecurityAPI() {
  const { apiCall } = useEventAPI()
  
  // Reactive state
  const permissions = ref([])
  const roles = ref([])
  const userRoles = ref([])
  const securitySummary = ref(null)
  const auditLogs = ref([])
  const policyDecisions = ref([])
  const loading = ref(false)
  const error = ref(null)
  
  // Computed properties
  const systemRoles = computed(() => 
    roles.value.filter(role => role.isSystemRole)
  )
  
  const chapterRoles = computed(() => 
    roles.value.filter(role => !role.isSystemRole)
  )
  
  const activeUserRoles = computed(() => 
    userRoles.value.filter(ur => ur.isActive && !ur.isExpired && !ur.isRevoked)
  )
  
  const expiredUserRoles = computed(() => 
    userRoles.value.filter(ur => ur.isExpired)
  )
  
  const revokedUserRoles = computed(() => 
    userRoles.value.filter(ur => ur.isRevoked)
  )
  
  // Permission Management
  const fetchPermissions = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/permissions', 'GET')
      permissions.value = response.data || []
      return response
    } catch (err) {
      error.value = err.message || 'Failed to fetch permissions'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const createPermission = async (permissionData) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/permissions', 'POST', permissionData)
      if (response.data) {
        permissions.value.push(response.data)
      }
      return response
    } catch (err) {
      error.value = err.message || 'Failed to create permission'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const updatePermission = async (id, permissionData) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/permissions/${id}`, 'PUT', permissionData)
      if (response.data) {
        const index = permissions.value.findIndex(p => p.id === id)
        if (index !== -1) {
          permissions.value[index] = response.data
        }
      }
      return response
    } catch (err) {
      error.value = err.message || 'Failed to update permission'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const deletePermission = async (id) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/permissions/${id}`, 'DELETE')
      permissions.value = permissions.value.filter(p => p.id !== id)
      return response
    } catch (err) {
      error.value = err.message || 'Failed to delete permission'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Role Management
  const fetchRoles = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/roles', 'GET')
      roles.value = response.data || []
      return response
    } catch (err) {
      error.value = err.message || 'Failed to fetch roles'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const createRole = async (roleData) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/roles', 'POST', roleData)
      if (response.data) {
        roles.value.push(response.data)
      }
      return response
    } catch (err) {
      error.value = err.message || 'Failed to create role'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const updateRole = async (id, roleData) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/roles/${id}`, 'PUT', roleData)
      if (response.data) {
        const index = roles.value.findIndex(r => r.id === id)
        if (index !== -1) {
          roles.value[index] = response.data
        }
      }
      return response
    } catch (err) {
      error.value = err.message || 'Failed to update role'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const deleteRole = async (id) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/roles/${id}`, 'DELETE')
      roles.value = roles.value.filter(r => r.id !== id)
      return response
    } catch (err) {
      error.value = err.message || 'Failed to delete role'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const assignPermissionsToRole = async (roleId, permissionIds) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/roles/${roleId}/permissions`, 'POST', {
        permissionIds
      })
      
      // Update the role's permissions in the local state
      const roleIndex = roles.value.findIndex(r => r.id === roleId)
      if (roleIndex !== -1 && response.data) {
        roles.value[roleIndex] = response.data
      }
      
      return response
    } catch (err) {
      error.value = err.message || 'Failed to assign permissions to role'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // User Role Assignment
  const fetchUserRoles = async (userId = null, chapterId = null) => {
    loading.value = true
    error.value = null
    try {
      let url = '/api/security/user-roles'
      const params = new URLSearchParams()
      if (userId) params.append('userId', userId)
      if (chapterId) params.append('chapterId', chapterId)
      if (params.toString()) url += '?' + params.toString()
      
      const response = await apiCall(url, 'GET')
      userRoles.value = response.data || []
      return response
    } catch (err) {
      error.value = err.message || 'Failed to fetch user roles'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const assignRoleToUser = async (assignmentData) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/user-roles', 'POST', assignmentData)
      if (response.data) {
        userRoles.value.push(response.data)
      }
      return response
    } catch (err) {
      error.value = err.message || 'Failed to assign role to user'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const revokeUserRole = async (userRoleId, reason) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/user-roles/${userRoleId}/revoke`, 'POST', {
        reason
      })
      
      // Update the user role in local state
      const index = userRoles.value.findIndex(ur => ur.id === userRoleId)
      if (index !== -1 && response.data) {
        userRoles.value[index] = response.data
      }
      
      return response
    } catch (err) {
      error.value = err.message || 'Failed to revoke user role'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const extendUserRole = async (userRoleId, newExpiryDate) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/user-roles/${userRoleId}/extend`, 'POST', {
        expiresAt: newExpiryDate
      })
      
      // Update the user role in local state
      const index = userRoles.value.findIndex(ur => ur.id === userRoleId)
      if (index !== -1 && response.data) {
        userRoles.value[index] = response.data
      }
      
      return response
    } catch (err) {
      error.value = err.message || 'Failed to extend user role'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Security Monitoring
  const fetchSecuritySummary = async (userId = null) => {
    loading.value = true
    error.value = null
    try {
      let url = '/api/security/summary'
      if (userId) url += `?userId=${userId}`
      
      const response = await apiCall(url, 'GET')
      securitySummary.value = response.data
      return response
    } catch (err) {
      error.value = err.message || 'Failed to fetch security summary'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const fetchAuditLogs = async (filters = {}) => {
    loading.value = true
    error.value = null
    try {
      const params = new URLSearchParams()
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value)
      })
      
      const url = `/api/security/audit-logs${params.toString() ? '?' + params.toString() : ''}`
      const response = await apiCall(url, 'GET')
      auditLogs.value = response.data || []
      return response
    } catch (err) {
      error.value = err.message || 'Failed to fetch audit logs'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const checkUserPermissions = async (userId, permissions) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/check-permissions', 'POST', {
        userId,
        permissions
      })
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to check permissions'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // ABAC Policy Management
  const evaluatePolicy = async (policyId, context) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/policies/evaluate', 'POST', {
        policyId,
        context
      })
      
      if (response.data) {
        policyDecisions.value.push({
          policyId,
          context,
          decision: response.data,
          timestamp: new Date()
        })
      }
      
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to evaluate policy'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const evaluateMultiplePolicies = async (policyIds, context, algorithm = 'DENY_OVERRIDES') => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/policies/evaluate-multiple', 'POST', {
        policyIds,
        context,
        algorithm
      })
      
      if (response.data) {
        policyDecisions.value.push({
          policyIds,
          context,
          algorithm,
          decision: response.data,
          timestamp: new Date()
        })
      }
      
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to evaluate multiple policies'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const getAvailablePolicies = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/policies', 'GET')
      return response.data || []
    } catch (err) {
      error.value = err.message || 'Failed to fetch available policies'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Security Analytics
  const getSecurityAnalytics = async (timeRange = '7d') => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall(`/api/security/analytics?range=${timeRange}`, 'GET')
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to fetch security analytics'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const getRoleUsageStats = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/roles/stats', 'GET')
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to fetch role usage statistics'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const getPermissionUsageStats = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await apiCall('/api/security/permissions/stats', 'GET')
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to fetch permission usage statistics'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Utility functions
  const clearError = () => {
    error.value = null
  }
  
  const formatHierarchyLevel = (level) => {
    if (level >= 90) return 'System Level'
    if (level >= 70) return 'Organizational Level'
    if (level >= 50) return 'Chapter Leadership'
    if (level >= 30) return 'Chapter Management'
    if (level >= 10) return 'Member Level'
    return 'Guest Level'
  }
  
  const getPermissionsByResource = (resource) => {
    return permissions.value.filter(p => p.resource === resource)
  }
  
  const getRolesByHierarchy = (minLevel, maxLevel) => {
    return roles.value.filter(r => 
      r.hierarchyLevel >= minLevel && r.hierarchyLevel <= maxLevel
    )
  }
  
  return {
    // State
    permissions,
    roles,
    userRoles,
    securitySummary,
    auditLogs,
    policyDecisions,
    loading,
    error,
    
    // Computed
    systemRoles,
    chapterRoles,
    activeUserRoles,
    expiredUserRoles,
    revokedUserRoles,
    
    // Permission Management
    fetchPermissions,
    createPermission,
    updatePermission,
    deletePermission,
    
    // Role Management
    fetchRoles,
    createRole,
    updateRole,
    deleteRole,
    assignPermissionsToRole,
    
    // User Role Assignment
    fetchUserRoles,
    assignRoleToUser,
    revokeUserRole,
    extendUserRole,
    
    // Security Monitoring
    fetchSecuritySummary,
    fetchAuditLogs,
    checkUserPermissions,
    
    // ABAC Policy Management
    evaluatePolicy,
    evaluateMultiplePolicies,
    getAvailablePolicies,
    
    // Security Analytics
    getSecurityAnalytics,
    getRoleUsageStats,
    getPermissionUsageStats,
    
    // Utilities
    clearError,
    formatHierarchyLevel,
    getPermissionsByResource,
    getRolesByHierarchy
  }
}