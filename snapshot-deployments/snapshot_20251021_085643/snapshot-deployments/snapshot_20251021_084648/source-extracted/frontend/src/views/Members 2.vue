<template>
  <div class="members">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col">
          <h1 class="display-4 text-center mb-3">
            <i class="bi bi-people-fill text-success me-3"></i>
            Members
          </h1>
          <p class="lead text-center text-muted">
            Manage chapter members and their roles
          </p>
        </div>
      </div>

      <!-- Actions Bar -->
      <div class="row mb-4">
        <div class="col-md-6">
          <button 
            class="btn btn-success" 
            @click="showCreateModal = true"
          >
            <i class="bi bi-person-plus me-2"></i>
            Add New Member
          </button>
        </div>
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-search"></i>
            </span>
            <input
              type="text"
              class="form-control"
              placeholder="Search members..."
              v-model="searchTerm"
            >
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="row mb-4">
        <div class="col-md-4">
          <select class="form-select" v-model="selectedChapter">
            <option value="">All Chapters</option>
            <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
              {{ chapter.name }}
            </option>
          </select>
        </div>
        <div class="col-md-4">
          <select class="form-select" v-model="selectedRole">
            <option value="">All Roles</option>
            <option value="PRESIDENT">President</option>
            <option value="VICE_PRESIDENT">Vice President</option>
            <option value="SECRETARY">Secretary</option>
            <option value="TREASURER">Treasurer</option>
            <option value="MEMBER">Member</option>
          </select>
        </div>
        <div class="col-md-4">
          <select class="form-select" v-model="selectedStatus">
            <option value="">All Status</option>
            <option value="true">Active</option>
            <option value="false">Inactive</option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-success" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Members Table -->
      <div v-else class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-table me-2"></i>
            All Members ({{ filteredMembers.length }})
          </h5>
        </div>
        <div class="card-body p-0">
          <div v-if="filteredMembers.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-person-x display-4 mb-3"></i>
            <p>No members found</p>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Chapter</th>
                  <th>Role</th>
                  <th>Joined</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in filteredMembers" :key="member.id">
                  <td>
                    <strong>{{ member.firstName }} {{ member.lastName }}</strong>
                  </td>
                  <td>{{ member.email }}</td>
                  <td>
                    <router-link 
                      v-if="member.chapterId"
                      :to="`/chapters/${member.chapterId}`"
                      class="text-decoration-none"
                    >
                      {{ member.chapterName || 'Unknown Chapter' }}
                    </router-link>
                    <span v-else class="text-muted">No Chapter</span>
                  </td>
                  <td>
                    <span class="badge" :class="getRoleBadgeClass(member.role)">
                      {{ formatRole(member.role) }}
                    </span>
                  </td>
                  <td>{{ formatDate(member.joinDate) }}</td>
                  <td>
                    <span class="badge" :class="member.active ? 'bg-success' : 'bg-secondary'">
                      {{ member.active ? 'Active' : 'Inactive' }}
                    </span>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <button 
                        class="btn btn-sm btn-outline-secondary"
                        @click="editMember(member)"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(member)"
                      >
                        <i class="bi bi-trash"></i>
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

    <!-- Create/Edit Modal -->
    <div 
      class="modal fade" 
      :class="{ show: showCreateModal || showEditModal }"
      :style="{ display: showCreateModal || showEditModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ showEditModal ? 'Edit Member' : 'Add New Member' }}
            </h5>
            <button 
              type="button" 
              class="btn-close" 
              @click="closeModal"
            ></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveMember">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="firstName" class="form-label">First Name *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="firstName"
                    v-model="memberForm.firstName"
                    required
                  >
                </div>
                <div class="col-md-6 mb-3">
                  <label for="lastName" class="form-label">Last Name *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="lastName"
                    v-model="memberForm.lastName"
                    required
                  >
                </div>
              </div>
              <div class="mb-3">
                <label for="email" class="form-label">Email *</label>
                <input
                  type="email"
                  class="form-control"
                  id="email"
                  v-model="memberForm.email"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="phoneNumber" class="form-label">Phone Number</label>
                <input
                  type="tel"
                  class="form-control"
                  id="phoneNumber"
                  v-model="memberForm.phoneNumber"
                >
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="chapter" class="form-label">Chapter *</label>
                  <select
                    class="form-select"
                    id="chapter"
                    v-model="memberForm.chapterId"
                    required
                  >
                    <option value="">Select Chapter</option>
                    <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                      {{ chapter.name }}
                    </option>
                  </select>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="role" class="form-label">Role *</label>
                  <select
                    class="form-select"
                    id="role"
                    v-model="memberForm.role"
                    required
                  >
                    <option value="">Select Role</option>
                    <option value="PRESIDENT">President</option>
                    <option value="VICE_PRESIDENT">Vice President</option>
                    <option value="SECRETARY">Secretary</option>
                    <option value="TREASURER">Treasurer</option>
                    <option value="MEMBER">Member</option>
                  </select>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="joinDate" class="form-label">Join Date *</label>
                  <input
                    type="date"
                    class="form-control"
                    id="joinDate"
                    v-model="memberForm.joinDate"
                    required
                  >
                </div>
                <div class="col-md-6 mb-3">
                  <label for="active" class="form-label">Status</label>
                  <select
                    class="form-select"
                    id="active"
                    v-model="memberForm.active"
                  >
                    <option :value="true">Active</option>
                    <option :value="false">Inactive</option>
                  </select>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-success" 
              @click="saveMember"
              :disabled="saving"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ showEditModal ? 'Update Member' : 'Add Member' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div 
      class="modal fade" 
      :class="{ show: showDeleteModal }"
      :style="{ display: showDeleteModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" @click="showDeleteModal = false"></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete member <strong>{{ memberToDelete?.firstName }} {{ memberToDelete?.lastName }}</strong>?</p>
            <p class="text-danger">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteModal = false">
              Cancel
            </button>
            <button type="button" class="btn btn-danger" @click="deleteMember">
              Delete Member
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Backdrop -->
    <div 
      v-if="showCreateModal || showEditModal || showDeleteModal"
      class="modal-backdrop fade show"
      @click="closeModal"
    ></div>
  </div>
</template>

<script>
import { memberService } from '../services/memberService'
import { chapterService } from '../services/chapterService'

export default {
  name: 'Members',
  data() {
    return {
      loading: true,
      saving: false,
      members: [],
      chapters: [],
      searchTerm: '',
      selectedChapter: '',
      selectedRole: '',
      selectedStatus: '',
      showCreateModal: false,
      showEditModal: false,
      showDeleteModal: false,
      memberToDelete: null,
      memberForm: {
        id: null,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        chapterId: '',
        role: '',
        joinDate: '',
        active: true
      }
    }
  },
  computed: {
    filteredMembers() {
      let filtered = this.members

      // Filter by search term
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase()
        filtered = filtered.filter(member =>
          member.firstName.toLowerCase().includes(term) ||
          member.lastName.toLowerCase().includes(term) ||
          member.email.toLowerCase().includes(term) ||
          (member.chapterName && member.chapterName.toLowerCase().includes(term))
        )
      }

      // Filter by chapter
      if (this.selectedChapter) {
        filtered = filtered.filter(member => 
          member.chapterId === parseInt(this.selectedChapter)
        )
      }

      // Filter by role
      if (this.selectedRole) {
        filtered = filtered.filter(member => member.role === this.selectedRole)
      }

      // Filter by status
      if (this.selectedStatus !== '') {
        const isActive = this.selectedStatus === 'true'
        filtered = filtered.filter(member => member.active === isActive)
      }

      return filtered
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.loading = true
        const [membersData, chaptersData] = await Promise.all([
          memberService.getAllMembers(),
          chapterService.getAllChapters()
        ])
        this.members = membersData
        this.chapters = chaptersData
      } catch (error) {
        console.error('Error loading members:', error)
      } finally {
        this.loading = false
      }
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    },
    formatRole(role) {
      return role.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    },
    getRoleBadgeClass(role) {
      const classes = {
        'PRESIDENT': 'bg-danger',
        'VICE_PRESIDENT': 'bg-warning text-dark',
        'SECRETARY': 'bg-info',
        'TREASURER': 'bg-success',
        'MEMBER': 'bg-secondary'
      }
      return classes[role] || 'bg-secondary'
    },
    editMember(member) {
      this.memberForm = {
        id: member.id,
        firstName: member.firstName,
        lastName: member.lastName,
        email: member.email,
        phoneNumber: member.phoneNumber || '',
        chapterId: member.chapterId || '',
        role: member.role,
        joinDate: member.joinDate,
        active: member.active
      }
      this.showEditModal = true
    },
    confirmDelete(member) {
      this.memberToDelete = member
      this.showDeleteModal = true
    },
    async saveMember() {
      try {
        this.saving = true
        
        if (this.showEditModal) {
          // For updates, use the new format with chapterId
          const memberData = {
            ...this.memberForm,
            // Keep chapterId instead of creating chapter object
            chapterId: this.memberForm.chapterId
          }
          // Remove any chapter object if it exists
          delete memberData.chapter
          await memberService.updateMember(this.memberForm.id, memberData)
        } else {
          // For creates, use the CreateMemberRequest format with chapterId
          const memberData = {
            firstName: this.memberForm.firstName,
            lastName: this.memberForm.lastName,
            email: this.memberForm.email,
            chapterId: this.memberForm.chapterId,
            role: this.memberForm.role
          }
          await memberService.createMember(memberData)
        }
        
        await this.loadData()
        this.closeModal()
      } catch (error) {
        console.error('Error saving member:', error)
        alert('Error saving member. Please try again.')
      } finally {
        this.saving = false
      }
    },
    async deleteMember() {
      try {
        await memberService.deleteMember(this.memberToDelete.id)
        await this.loadData()
        this.showDeleteModal = false
        this.memberToDelete = null
      } catch (error) {
        console.error('Error deleting member:', error)
        alert('Error deleting member. Please try again.')
      }
    },
    closeModal() {
      this.showCreateModal = false
      this.showEditModal = false
      this.showDeleteModal = false
      this.memberForm = {
        id: null,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        chapterId: '',
        role: '',
        joinDate: '',
        active: true
      }
    }
  }
}
</script>

<style scoped>
.modal.show {
  animation: fadeIn 0.15s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>