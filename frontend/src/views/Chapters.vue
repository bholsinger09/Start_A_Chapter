<template>
  <div class="chapters">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col">
          <h1 class="display-4 text-center mb-3">
            <i class="bi bi-building text-primary me-3"></i>
            Chapters
          </h1>
          <p class="lead text-center text-muted">
            Manage university chapters across the network
          </p>
        </div>
      </div>

      <!-- Actions Bar -->
      <div class="row mb-4">
        <div class="col-md-6">
          <button 
            class="btn btn-primary" 
            @click="showCreateModal = true"
          >
            <i class="bi bi-plus-circle me-2"></i>
            Add New Chapter
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
              placeholder="Search chapters..."
              v-model="searchTerm"
            >
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Chapters Table -->
      <div v-else class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-table me-2"></i>
            All Chapters ({{ filteredChapters.length }})
          </h5>
        </div>
        <div class="card-body p-0">
          <div v-if="filteredChapters.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-inbox display-4 mb-3"></i>
            <p>No chapters found</p>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Chapter Name</th>
                  <th>University</th>
                  <th>Location</th>
                  <th>Founded</th>
                  <th>Members</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="chapter in filteredChapters" :key="chapter.id">
                  <td>
                    <strong>{{ chapter.name }}</strong>
                  </td>
                  <td>{{ chapter.universityName }}</td>
                  <td>{{ chapter.city }}, {{ chapter.state }}</td>
                  <td>{{ formatDate(chapter.foundedDate) }}</td>
                  <td>
                    <span class="badge bg-info">
                      {{ getMemberCount(chapter.id) }}
                    </span>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <router-link 
                        :to="`/chapters/${chapter.id}`" 
                        class="btn btn-sm btn-outline-primary"
                      >
                        <i class="bi bi-eye"></i>
                      </router-link>
                      <button 
                        class="btn btn-sm btn-outline-secondary"
                        @click="editChapter(chapter)"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(chapter)"
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
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ showEditModal ? 'Edit Chapter' : 'Create New Chapter' }}
            </h5>
            <button 
              type="button" 
              class="btn-close" 
              @click="closeModal"
            ></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveChapter">
              <div class="mb-3">
                <label for="chapterName" class="form-label">Chapter Name *</label>
                <input
                  type="text"
                  class="form-control"
                  id="chapterName"
                  v-model="chapterForm.name"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="universityName" class="form-label">University Name *</label>
                <input
                  type="text"
                  class="form-control"
                  id="universityName"
                  v-model="chapterForm.universityName"
                  required
                >
              </div>
              <div class="row">
                <div class="col-md-8 mb-3">
                  <label for="city" class="form-label">City *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="city"
                    v-model="chapterForm.city"
                    required
                  >
                </div>
                <div class="col-md-4 mb-3">
                  <label for="state" class="form-label">State *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="state"
                    v-model="chapterForm.state"
                    required
                    maxlength="2"
                    placeholder="CA"
                  >
                </div>
              </div>
              <div class="mb-3">
                <label for="foundedDate" class="form-label">Founded Date *</label>
                <input
                  type="date"
                  class="form-control"
                  id="foundedDate"
                  v-model="chapterForm.foundedDate"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="chapterForm.description"
                  rows="3"
                ></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-primary" 
              @click="saveChapter"
              :disabled="saving"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ showEditModal ? 'Update Chapter' : 'Create Chapter' }}
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
            <p>Are you sure you want to delete the chapter <strong>{{ chapterToDelete?.name }}</strong>?</p>
            <p class="text-danger">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteModal = false">
              Cancel
            </button>
            <button type="button" class="btn btn-danger" @click="deleteChapter">
              Delete Chapter
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
import { chapterService } from '../services/chapterService'
import { memberService } from '../services/memberService'

export default {
  name: 'Chapters',
  data() {
    return {
      loading: true,
      saving: false,
      chapters: [],
      members: [],
      searchTerm: '',
      showCreateModal: false,
      showEditModal: false,
      showDeleteModal: false,
      chapterToDelete: null,
      chapterForm: {
        id: null,
        name: '',
        universityName: '',
        city: '',
        state: '',
        foundedDate: '',
        description: ''
      }
    }
  },
  computed: {
    filteredChapters() {
      if (!this.searchTerm) return this.chapters
      
      const term = this.searchTerm.toLowerCase()
      return this.chapters.filter(chapter =>
        chapter.name.toLowerCase().includes(term) ||
        chapter.universityName.toLowerCase().includes(term) ||
        chapter.city.toLowerCase().includes(term) ||
        chapter.state.toLowerCase().includes(term)
      )
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.loading = true
        const [chaptersData, membersData] = await Promise.all([
          chapterService.getAllChapters(),
          memberService.getAllMembers()
        ])
        this.chapters = chaptersData
        this.members = membersData
      } catch (error) {
        console.error('Error loading chapters:', error)
      } finally {
        this.loading = false
      }
    },
    getMemberCount(chapterId) {
      return this.members.filter(member => member.chapter?.id === chapterId).length
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    },
    editChapter(chapter) {
      this.chapterForm = {
        id: chapter.id,
        name: chapter.name,
        universityName: chapter.universityName,
        city: chapter.city,
        state: chapter.state,
        foundedDate: chapter.foundedDate,
        description: chapter.description || ''
      }
      this.showEditModal = true
    },
    confirmDelete(chapter) {
      this.chapterToDelete = chapter
      this.showDeleteModal = true
    },
    async saveChapter() {
      try {
        this.saving = true
        
        if (this.showEditModal) {
          await chapterService.updateChapter(this.chapterForm.id, this.chapterForm)
        } else {
          await chapterService.createChapter(this.chapterForm)
        }
        
        await this.loadData()
        this.closeModal()
      } catch (error) {
        console.error('Error saving chapter:', error)
        alert('Error saving chapter. Please try again.')
      } finally {
        this.saving = false
      }
    },
    async deleteChapter() {
      try {
        await chapterService.deleteChapter(this.chapterToDelete.id)
        await this.loadData()
        this.showDeleteModal = false
        this.chapterToDelete = null
      } catch (error) {
        console.error('Error deleting chapter:', error)
        alert('Error deleting chapter. Please try again.')
      }
    },
    closeModal() {
      this.showCreateModal = false
      this.showEditModal = false
      this.showDeleteModal = false
      this.chapterForm = {
        id: null,
        name: '',
        universityName: '',
        city: '',
        state: '',
        foundedDate: '',
        description: ''
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