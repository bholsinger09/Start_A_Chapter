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
          <button class="btn btn-outline-secondary me-2" @click="loadData" :disabled="loading" title="Refresh member list">
            <i class="bi bi-arrow-clockwise me-1"></i>
            <span v-if="loading">Loading...</span>
            <span v-else>Refresh</span>
          </button>
          <button class="btn btn-primary" @click="openAddMemberModal" :disabled="loading">
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
                        <div v-if="member.chapterName">
                          <div class="fw-bold">{{ member.chapterName }}</div>
                          <small class="text-muted">{{ member.universityName }}</small>
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

    <!-- Add Member Modal -->
    <div class="modal fade" id="addMemberModal" tabindex="-1" aria-labelledby="addMemberModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="addMemberModalLabel">
              <i class="bi bi-person-plus me-2"></i>Add New Member
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="addMember">
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="firstName" class="form-label">First Name <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="firstName" v-model="newMember.firstName" required>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="lastName" class="form-label">Last Name <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="lastName" v-model="newMember.lastName" required>
                  </div>
                </div>
              </div>
              
              <div class="mb-3">
                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" id="email" v-model="newMember.email" required>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" class="form-control" id="username" v-model="newMember.username">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="phoneNumber" class="form-label">Phone Number</label>
                    <input type="tel" class="form-control" id="phoneNumber" v-model="newMember.phoneNumber">
                  </div>
                </div>
              </div>

              <div class="mb-3">
                <label for="chapterSelect" class="form-label">Chapter <span class="text-danger">*</span></label>
                <select class="form-select" id="chapterSelect" v-model="newMember.chapterId" required>
                  <option value="">Select a chapter</option>
                  <option v-for="chapter in availableChapters" :key="chapter.id" :value="chapter.id">
                    {{ chapter.name }} - {{ chapter.universityName }}
                  </option>
                </select>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="role" class="form-label">Role</label>
                    <select class="form-select" id="role" v-model="newMember.role">
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
                    <input type="text" class="form-control" id="major" v-model="newMember.major">
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="graduationYear" class="form-label">Graduation Year</label>
                    <input type="text" class="form-control" id="graduationYear" v-model="newMember.graduationYear" placeholder="2024">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="password" v-model="newMember.password" required>
                  </div>
                </div>
              </div>

              <div class="alert alert-danger" v-if="memberError">
                <i class="bi bi-exclamation-triangle me-2"></i>
                {{ memberError }}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-primary" @click="addMember" :disabled="memberLoading">
              <span v-if="memberLoading">
                <i class="bi bi-hourglass-split me-2"></i>Adding...
              </span>
              <span v-else>
                <i class="bi bi-person-plus me-2"></i>Add Member
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onActivated } from 'vue'
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
    
    // Member creation state
    const memberLoading = ref(false)
    const memberError = ref('')
    const newMember = ref({
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
    })

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
          member.chapterId?.toString() === selectedChapter.value
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
      const chapterIds = new Set(members.value.map(member => member.chapterId).filter(Boolean))
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

    // Member management functions
    const openAddMemberModal = () => {
      // Reset form
      newMember.value = {
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
      }
      memberError.value = ''
      
      // Show modal (Bootstrap 5)
      const modal = new window.bootstrap.Modal(document.getElementById('addMemberModal'))
      modal.show()
    }

    const addMember = async () => {
      if (!newMember.value.firstName || !newMember.value.lastName || !newMember.value.email || 
          !newMember.value.chapterId || !newMember.value.password) {
        memberError.value = 'Please fill in all required fields.'
        return
      }

      try {
        memberLoading.value = true
        memberError.value = ''

        const memberData = {
          firstName: newMember.value.firstName.trim(),
          lastName: newMember.value.lastName.trim(),
          email: newMember.value.email.trim(),
          username: newMember.value.username?.trim() || null,
          phoneNumber: newMember.value.phoneNumber?.trim() || null,
          role: newMember.value.role,
          major: newMember.value.major?.trim() || null,
          graduationYear: newMember.value.graduationYear?.trim() || null,
          password: newMember.value.password,
          active: true
        }

        // Add member to specific chapter
        await api.post(`/api/members/chapter/${newMember.value.chapterId}`, memberData)
        
        // Reload members
        await loadData()
        
        // Close modal
        const modal = window.bootstrap.Modal.getInstance(document.getElementById('addMemberModal'))
        modal.hide()
        
      } catch (err) {
        console.error('Member creation error:', err)
        if (err.response?.data?.message) {
          memberError.value = err.response.data.message
        } else if (err.response?.status === 400) {
          memberError.value = 'Invalid member data. Please check your input and try again.'
        } else {
          memberError.value = 'Failed to create member. Please try again.'
        }
      } finally {
        memberLoading.value = false
      }
    }

    // Load data on component mount
    onMounted(() => {
      loadData()
    })

    // Refresh data when component is activated (navigated back to)
    onActivated(() => {
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
      getRoleBadgeClass,
      // Member management
      memberLoading,
      memberError,
      newMember,
      openAddMemberModal,
      addMember,
      // Data refresh
      loadData
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
