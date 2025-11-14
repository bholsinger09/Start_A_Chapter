<template>
  <div class="member-table">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-muted">{{ loadingMessage }}</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="members.length === 0" class="text-center py-5">
      <i class="bi bi-people display-1 text-muted"></i>
      <h4 class="mt-3">{{ emptyMessage }}</h4>
      <p class="text-muted">{{ emptyDescription }}</p>
      <button 
        v-if="showAddButton"
        class="btn btn-primary"
        @click="addMember"
      >
        <i class="bi bi-person-plus me-2"></i>
        Add First Member
      </button>
    </div>

    <!-- Members Table -->
    <div v-else class="card">
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
              <MemberRow
                v-for="member in members"
                :key="member.id"
                :member="member"
                @view="handleView"
                @edit="handleEdit"
                @delete="handleDelete"
              />
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <nav v-if="totalPages > 1" aria-label="Members pagination" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button 
            class="page-link"
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 1"
          >
            Previous
          </button>
        </li>
        <li 
          v-for="page in visiblePages"
          :key="page"
          class="page-item"
          :class="{ active: page === currentPage }"
        >
          <button 
            class="page-link"
            @click="changePage(page)"
          >
            {{ page }}
          </button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <button 
            class="page-link"
            @click="changePage(currentPage + 1)"
            :disabled="currentPage === totalPages"
          >
            Next
          </button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import MemberRow from './MemberRow.vue';

/**
 * Focused component for displaying members in a table format.
 * Handles pagination, loading states, and member actions with clear prop interfaces.
 */
export default {
  name: 'MemberTable',
  components: {
    MemberRow
  },
  props: {
    members: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    loadingMessage: {
      type: String,
      default: 'Loading members...'
    },
    emptyMessage: {
      type: String,
      default: 'No members found'
    },
    emptyDescription: {
      type: String,
      default: 'Try adjusting your filters or add new members to get started.'
    },
    showAddButton: {
      type: Boolean,
      default: true
    },
    currentPage: {
      type: Number,
      default: 1
    },
    totalPages: {
      type: Number,
      default: 1
    }
  },
  emits: ['view', 'edit', 'delete', 'add', 'page-change'],
  computed: {
    visiblePages() {
      const pages = [];
      const start = Math.max(1, this.currentPage - 2);
      const end = Math.min(this.totalPages, this.currentPage + 2);
      
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      
      return pages;
    }
  },
  methods: {
    handleView(member) {
      this.$emit('view', member);
    },
    
    handleEdit(member) {
      this.$emit('edit', member);
    },
    
    handleDelete(member) {
      this.$emit('delete', member);
    },
    
    addMember() {
      this.$emit('add');
    },
    
    changePage(page) {
      if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
        this.$emit('page-change', page);
      }
    }
  }
};
</script>

<style scoped>
.member-table {
  /* Component-specific styles */
}

.table thead th {
  border-bottom: 2px solid #dee2e6;
  font-weight: 600;
  color: #495057;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.pagination .page-link {
  border-color: #dee2e6;
  color: #6c757d;
}

.pagination .page-item.active .page-link {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
}

.pagination .page-link:hover {
  background-color: #e9ecef;
  border-color: #dee2e6;
}

.display-1 {
  font-size: 3rem;
}
</style>