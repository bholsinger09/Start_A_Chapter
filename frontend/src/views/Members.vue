<template>
  <div class="members">
    <div class="container-fluid">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-md-8">
          <h2>
            <i class="bi bi-people me-2"></i>
            Chapter Members
          </h2>
          <p class="text-muted">Connect with fellow students and chapter members.</p>
        </div>
        <div class="col-md-4 text-end">
          <button class="btn btn-primary" disabled>
            <i class="bi bi-person-plus me-2"></i>
            Add Member
          </button>
        </div>
      </div>

      <!-- Search -->
      <div class="row mb-4">
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-search"></i>
            </span>
            <input 
              type="text" 
              class="form-control" 
              placeholder="Search members by name or email..."
              v-model="searchTerm"
            >
          </div>
        </div>
        <div class="col-md-3">
          <select class="form-select" v-model="selectedChapter">
            <option value="">All Chapters</option>
            <option v-for="chapter in availableChapters" :key="chapter.id" :value="chapter.id">
              {{ chapter.name }}
            </option>
          </select>
        </div>
        <div class="col-md-3">
          <select class="form-select" v-model="selectedRole">
            <option value="">All Roles</option>
            <option value="PRESIDENT">President</option>
            <option value="VICE_PRESIDENT">Vice President</option>
            <option value="TREASURER">Treasurer</option>
            <option value="SECRETARY">Secretary</option>
            <option value="MEMBER">Member</option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div class="row" v-if="loading">
        <div class="col-12">
          <div class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3 text-muted">Loading members...</p>
          </div>
        </div>
      </div>

      <!-- Members List -->
      <div class="row" v-else-if="filteredMembers.length > 0">
        <div class="col-12">
          <div class="card">
            <div class="card-body p-0">
              <div class="table-responsive">
                <table class="table table-hover mb-0">
                  <thead class="table-light">
                    <tr>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Chapter</th>
                      <th>Role</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="member in filteredMembers" :key="member.id">
                      <td>
                        <div class="d-flex align-items-center">
                          <div class="avatar-circle me-3">
                            {{ getInitials(member.firstName, member.lastName) }}
                          </div>
                          <div>
                            <div class="fw-bold">{{ member.firstName }} {{ member.lastName }}</div>
                            <small class="text-muted">ID: {{ member.id }}</small>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span class="text-break">{{ member.email }}</span>
                        <br>
                        <small class="text-muted" v-if="member.phoneNumber">
                          <i class="bi bi-telephone me-1"></i>{{ member.phoneNumber }}
                        </small>
                      </td>
                      <td>
                        <div v-if="member.chapter">
                          <div class="fw-bold">{{ member.chapter.name }}</div>
                          <small class="text-muted">{{ member.chapter.universityName }}</small>
                        </div>
                        <span v-else class="text-muted">No Chapter</span>
                      </td>
                      <td>
                        <span :class="getRoleBadgeClass(member.role)" class="badge">
                          {{ formatRole(member.role) }}
                        </span>
                      </td>
                      <td>
                        <span :class="member.active ? 'badge bg-success' : 'badge bg-secondary'">
                          {{ member.active ? 'Active' : 'Inactive' }}
                        </span>
                      </td>
                      <td>
                        <div class="btn-group btn-group-sm">
                          <button class="btn btn-outline-primary" disabled>
                            <i class="bi bi-eye"></i>
                          </button>
                          <button class="btn btn-outline-secondary" disabled>
                            <i class="bi bi-envelope"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- No Results -->
      <div class="row" v-else>
        <div class="col-12">
          <div class="text-center py-5">
            <i class="bi bi-people display-1 text-muted mb-3"></i>
            <h4 class="text-muted">No members found</h4>
            <p class="text-muted">
              {{ searchTerm || selectedChapter || selectedRole ? 'Try adjusting your search criteria.' : 'No members available at the moment.' }}
            </p>
          </div>
        </div>
      </div>

      <!-- Stats -->
      <div class="row mt-4" v-if="members.length > 0">
        <div class="col-12">
          <div class="card bg-light">
            <div class="card-body">
              <div class="row text-center">
                <div class="col-md-3">
                  <h4 class="text-primary">{{ members.length }}</h4>
                  <p class="text-muted mb-0">Total Members</p>
                </div>
                <div class="col-md-3">
                  <h4 class="text-success">{{ activeMembers }}</h4>
                  <p class="text-muted mb-0">Active Members</p>
                </div>
                <div class="col-md-3">
                  <h4 class="text-info">{{ uniqueChapters }}</h4>
                  <p class="text-muted mb-0">Chapters Represented</p>
                </div>
                <div class="col-md-3">
                  <h4 class="text-warning">{{ officerCount }}</h4>
                  <p class="text-muted mb-0">Officers</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'

export default {
  name: 'Members',
  setup() {
    const members = ref([])
    const chapters = ref([])
    const loading = ref(true)
    const searchTerm = ref('')
    const selectedChapter = ref('')
    const selectedRole = ref('')

    // Load members and chapters from API
    const loadData = async () => {
      try {
        loading.value = true
        
        // Load members and chapters in parallel
        const [membersResponse, chaptersResponse] = await Promise.all([
          api.get('/api/members'),
          api.get('/api/chapters')
        ])

        members.value = membersResponse.data
        chapters.value = chaptersResponse.data
      } catch (error) {
        console.error('Error loading data:', error)
        // Try loading just chapters if members endpoint fails
        try {
          const chaptersResponse = await api.get('/api/chapters')
          chapters.value = chaptersResponse.data
        } catch (chaptersError) {
          console.error('Error loading chapters:', chaptersError)
        }
      } finally {
        loading.value = false
      }
    }

    // Computed properties for filtering and stats
    const filteredMembers = computed(() => {
      let filtered = members.value

      if (searchTerm.value) {
        const search = searchTerm.value.toLowerCase()
        filtered = filtered.filter(member => 
          member.firstName.toLowerCase().includes(search) ||
          member.lastName.toLowerCase().includes(search) ||
          member.email.toLowerCase().includes(search)
        )
      }

      if (selectedChapter.value) {
        filtered = filtered.filter(member => 
          member.chapter?.id?.toString() === selectedChapter.value
        )
      }

      if (selectedRole.value) {
        filtered = filtered.filter(member => 
          member.role === selectedRole.value
        )
      }

      return filtered
    })

    const availableChapters = computed(() => {
      return chapters.value.slice(0, 20) // Limit for performance
    })

    const activeMembers = computed(() => {
      return members.value.filter(member => member.active).length
    })

    const uniqueChapters = computed(() => {
      const chapterIds = new Set(members.value.map(member => member.chapter?.id).filter(Boolean))
      return chapterIds.size
    })

    const officerCount = computed(() => {
      return members.value.filter(member => 
        member.role && !['MEMBER'].includes(member.role)
      ).length
    })

    // Utility functions
    const getInitials = (firstName, lastName) => {
      return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`.toUpperCase()
    }

    const formatRole = (role) => {
      if (!role) return 'Member'
      return role.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase())
    }

    const getRoleBadgeClass = (role) => {
      const roleClasses = {
        'PRESIDENT': 'badge bg-primary',
        'VICE_PRESIDENT': 'badge bg-info', 
        'TREASURER': 'badge bg-success',
        'SECRETARY': 'badge bg-warning',
        'MEMBER': 'badge bg-secondary'
      }
      return roleClasses[role] || 'badge bg-secondary'
    }

    // Load data on component mount
    onMounted(() => {
      loadData()
    })

    return {
      members,
      loading,
      searchTerm,
      selectedChapter,
      selectedRole,
      filteredMembers,
      availableChapters,
      activeMembers,
      uniqueChapters,
      officerCount,
      getInitials,
      formatRole,
      getRoleBadgeClass
    }
  }
}
</script>

<style scoped>
.avatar-circle {
  width: 40px;
  height: 40px;
  background-color: #0d6efd;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
}

.card {
  border-radius: 0.5rem;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.table th {
  border-bottom: 2px solid #dee2e6;
  font-weight: 600;
}

.table-hover tbody tr:hover {
  background-color: rgba(13, 110, 253, 0.05);
}

.display-1 {
  font-size: 4rem;
}

.input-group-text {
  border-radius: 0.375rem 0 0 0.375rem;
}

.badge {
  font-size: 0.7rem;
}

.btn-group-sm > .btn {
  padding: 0.25rem 0.5rem;
}
</style>
