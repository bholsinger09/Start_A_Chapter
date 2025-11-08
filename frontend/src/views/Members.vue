<template>
  <div class="container-fluid">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-6 fw-bold text-primary mb-0">
              <i class="bi bi-people me-3"></i>Members Management
            </h1>
            <p class="lead text-muted">Manage chapter members and assignments</p>
          </div>
          <div>
            <router-link to="/members/create" class="btn btn-primary btn-lg">
              <i class="bi bi-person-plus me-2"></i>Add Member
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-3">
                <label for="chapterFilter" class="form-label">Filter by Chapter</label>
                <select v-model="selectedChapter" @change="loadMembers" class="form-select" id="chapterFilter">
                  <option value="">All Chapters</option>
                  <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                    {{ chapter.name }}
                  </option>
                </select>
              </div>
              <div class="col-md-3">
                <label for="roleFilter" class="form-label">Filter by Role</label>
                <select v-model="selectedRole" @change="loadMembers" class="form-select" id="roleFilter">
                  <option value="">All Roles</option>
                  <option value="PRESIDENT">President</option>
                  <option value="VICE_PRESIDENT">Vice President</option>
                  <option value="TREASURER">Treasurer</option>
                  <option value="SECRETARY">Secretary</option>
                  <option value="MEMBER">Member</option>
                </select>
              </div>
              <div class="col-md-4">
                <label for="searchInput" class="form-label">Search Members</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="bi bi-search"></i>
                  </span>
                  <input 
                    type="text" 
                    v-model="searchQuery" 
                    @input="searchMembers" 
                    class="form-control" 
                    id="searchInput"
                    placeholder="Search by name or email..."
                  >
                </div>
              </div>
              <div class="col-md-2">
                <label class="form-label">&nbsp;</label>
                <div class="d-grid">
                  <button @click="clearFilters" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-clockwise me-1"></i>Clear
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading members...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <!-- Members Grid -->
    <div v-else class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
              <i class="bi bi-people me-2"></i>
              Members ({{ members.length }})
            </h5>
            <div class="text-muted">
              <i class="bi bi-info-circle me-1"></i>
              Showing {{ filteredMembers.length }} of {{ members.length }} members
            </div>
          </div>
          
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                  <tr>
                    <th scope="col">
                      <i class="bi bi-person me-1"></i>Name
                    </th>
                    <th scope="col">
                      <i class="bi bi-envelope me-1"></i>Email
                    </th>
                    <th scope="col">
                      <i class="bi bi-building me-1"></i>Chapter
                    </th>
                    <th scope="col">
                      <i class="bi bi-award me-1"></i>Role
                    </th>
                    <th scope="col">
                      <i class="bi bi-calendar me-1"></i>Joined
                    </th>
                    <th scope="col">
                      <i class="bi bi-activity me-1"></i>Status
                    </th>
                    <th scope="col" class="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="member in paginatedMembers" :key="member.id">
                    <td>
                      <div class="d-flex align-items-center">
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3" 
                             style="width: 40px; height: 40px;">
                          {{ getInitials(member.firstName, member.lastName) }}
                        </div>
                        <div>
                          <div class="fw-semibold">{{ member.firstName }} {{ member.lastName }}</div>
                          <div class="text-muted small" v-if="member.username">@{{ member.username }}</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <a :href="`mailto:${member.email}`" class="text-decoration-none">
                        {{ member.email }}
                      </a>
                    </td>
                    <td>
                      <span v-if="member.chapterName" class="badge bg-info">
                        {{ member.chapterName }}
                      </span>
                      <span v-else class="text-muted">No Chapter</span>
                    </td>
                    <td>
                      <span class="badge" :class="getRoleBadgeClass(member.role)">
                        {{ formatRole(member.role) }}
                      </span>
                    </td>
                    <td>
                      <span class="text-muted">{{ formatDate(member.createdAt) }}</span>
                    </td>
                    <td>
                      <span class="badge" :class="member.active ? 'bg-success' : 'bg-secondary'">
                        {{ member.active ? 'Active' : 'Inactive' }}
                      </span>
                    </td>
                    <td class="text-end">
                      <div class="btn-group" role="group">
                        <router-link 
                          :to="`/members/${member.id}`" 
                          class="btn btn-sm btn-outline-primary"
                          title="View Details"
                        >
                          <i class="bi bi-eye"></i>
                        </router-link>
                        <router-link 
                          :to="`/members/${member.id}/edit`" 
                          class="btn btn-sm btn-outline-warning"
                          title="Edit Member"
                        >
                          <i class="bi bi-pencil"></i>
                        </router-link>
                        <button 
                          @click="toggleMemberStatus(member)"
                          class="btn btn-sm"
                          :class="member.active ? 'btn-outline-secondary' : 'btn-outline-success'"
                          :title="member.active ? 'Deactivate' : 'Activate'"
                        >
                          <i :class="member.active ? 'bi bi-pause' : 'bi bi-play'"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination -->
            <nav v-if="totalPages > 1" class="d-flex justify-content-center p-3">
              <ul class="pagination mb-0">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <a class="page-link" href="#" @click.prevent="goToPage(currentPage - 1)">
                    <i class="bi bi-chevron-left"></i>
                  </a>
                </li>
                <li v-for="page in visiblePages" :key="page" class="page-item" :class="{ active: page === currentPage }">
                  <a class="page-link" href="#" @click.prevent="goToPage(page)">{{ page }}</a>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                  <a class="page-link" href="#" @click.prevent="goToPage(currentPage + 1)">
                    <i class="bi bi-chevron-right"></i>
                  </a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!loading && !error && filteredMembers.length === 0" class="text-center py-5">
      <i class="bi bi-people display-1 text-muted"></i>
      <h3 class="text-muted mt-3">No members found</h3>
      <p class="text-muted mb-4">
        {{ members.length === 0 ? 'Get started by adding your first member.' : 'Try adjusting your filters or search terms.' }}
      </p>
      <router-link to="/members/create" class="btn btn-primary">
        <i class="bi bi-person-plus me-2"></i>Add First Member
      </router-link>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import axios from '../services/api'

export default {
  name: 'Members',
  setup() {
    const members = ref([])
    const chapters = ref([])
    const loading = ref(true)
    const error = ref('')
    
    // Filters
    const selectedChapter = ref('')
    const selectedRole = ref('')
    const searchQuery = ref('')
    
    // Pagination
    const currentPage = ref(1)
    const itemsPerPage = 20

    // Computed properties
    const filteredMembers = computed(() => {
      let filtered = members.value

      // Filter by chapter
      if (selectedChapter.value) {
        filtered = filtered.filter(member => member.chapterId == selectedChapter.value)
      }

      // Filter by role
      if (selectedRole.value) {
        filtered = filtered.filter(member => member.role === selectedRole.value)
      }

      // Search filter
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(member => 
          `${member.firstName} ${member.lastName}`.toLowerCase().includes(query) ||
          member.email.toLowerCase().includes(query) ||
          (member.username && member.username.toLowerCase().includes(query))
        )
      }

      return filtered
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredMembers.value.length / itemsPerPage)
    })

    const paginatedMembers = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return filteredMembers.value.slice(start, end)
    })

    const visiblePages = computed(() => {
      const pages = []
      const total = totalPages.value
      const current = currentPage.value
      
      // Always show first page
      pages.push(1)
      
      // Show pages around current page
      for (let i = Math.max(2, current - 2); i <= Math.min(total - 1, current + 2); i++) {
        pages.push(i)
      }
      
      // Always show last page if > 1
      if (total > 1 && !pages.includes(total)) {
        pages.push(total)
      }
      
      return pages
    })

    // Methods
    const loadMembers = async () => {
      try {
        loading.value = true
        const response = await axios.get('/api/members')
        members.value = response.data
        error.value = ''
      } catch (err) {
        error.value = 'Failed to load members'
        console.error('Error loading members:', err)
      } finally {
        loading.value = false
      }
    }

    const loadChapters = async () => {
      try {
        const response = await axios.get('/api/chapters')
        chapters.value = response.data
      } catch (err) {
        console.error('Error loading chapters:', err)
      }
    }

    const clearFilters = () => {
      selectedChapter.value = ''
      selectedRole.value = ''
      searchQuery.value = ''
      currentPage.value = 1
    }

    const searchMembers = () => {
      currentPage.value = 1 // Reset to first page when searching
    }

    const goToPage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
      }
    }

    const toggleMemberStatus = async (member) => {
      try {
        const newStatus = !member.active
        await axios.put(`/api/members/${member.id}`, {
          ...member,
          active: newStatus
        })
        member.active = newStatus
      } catch (err) {
        error.value = 'Failed to update member status'
        console.error('Error updating member status:', err)
      }
    }

    // Utility methods
    const getInitials = (firstName, lastName) => {
      return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase()
    }

    const getRoleBadgeClass = (role) => {
      const classes = {
        'PRESIDENT': 'bg-danger',
        'VICE_PRESIDENT': 'bg-warning text-dark',
        'TREASURER': 'bg-success',
        'SECRETARY': 'bg-info',
        'MEMBER': 'bg-secondary'
      }
      return classes[role] || 'bg-secondary'
    }

    const formatRole = (role) => {
      return role?.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase()) || 'Member'
    }

    const formatDate = (dateArray) => {
      if (!dateArray) return 'Unknown'
      try {
        const [year, month, day] = dateArray
        return new Date(year, month - 1, day).toLocaleDateString()
      } catch {
        return 'Unknown'
      }
    }

    // Lifecycle
    onMounted(() => {
      loadMembers()
      loadChapters()
    })

    return {
      members,
      chapters,
      loading,
      error,
      selectedChapter,
      selectedRole,
      searchQuery,
      currentPage,
      filteredMembers,
      totalPages,
      paginatedMembers,
      visiblePages,
      loadMembers,
      clearFilters,
      searchMembers,
      goToPage,
      toggleMemberStatus,
      getInitials,
      getRoleBadgeClass,
      formatRole,
      formatDate
    }
  }
}
</script>

<style scoped>
.table th {
  border-top: none;
  font-weight: 600;
}

.pagination {
  --bs-pagination-active-bg: var(--bs-primary);
  --bs-pagination-active-border-color: var(--bs-primary);
}

.btn-group .btn {
  border-radius: 0.375rem;
}

.btn-group .btn:not(:last-child) {
  margin-right: 2px;
}
</style>
