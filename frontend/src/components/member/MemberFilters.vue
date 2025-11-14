<!--
MemberFilters Component
Fixes: Large Classes smell - Extracted filtering logic from Members.vue
Single Responsibility: Handle member search and filtering options
-->
<template>
  <div class="member-filters">
    <!-- Search and Filters Row -->
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
            :value="searchTerm"
            @input="$emit('update:searchTerm', $event.target.value)"
          >
        </div>
      </div>
      <div class="col-md-3">
        <select 
          class="form-select" 
          :value="selectedChapter"
          @change="$emit('update:selectedChapter', $event.target.value)"
        >
          <option value="">All Chapters</option>
          <option 
            v-for="chapter in availableChapters" 
            :key="chapter.id" 
            :value="chapter.id"
          >
            {{ chapter.name }}
          </option>
        </select>
      </div>
      <div class="col-md-3">
        <select 
          class="form-select"
          :value="selectedRole"
          @change="$emit('update:selectedRole', $event.target.value)"
        >
          <option value="">All Roles</option>
          <option value="PRESIDENT">President</option>
          <option value="VICE_PRESIDENT">Vice President</option>
          <option value="TREASURER">Treasurer</option>
          <option value="SECRETARY">Secretary</option>
          <option value="MEMBER">Member</option>
        </select>
      </div>
    </div>

    <!-- Filter Summary (optional) -->
    <div v-if="hasActiveFilters" class="row mb-3">
      <div class="col-12">
        <div class="d-flex align-items-center flex-wrap gap-2">
          <span class="text-muted small">Active filters:</span>
          
          <span v-if="searchTerm" class="badge bg-light text-dark">
            Search: "{{ searchTerm }}"
            <button 
              type="button" 
              class="btn-close btn-close-sm ms-1"
              @click="$emit('update:searchTerm', '')"
            ></button>
          </span>
          
          <span v-if="selectedChapter" class="badge bg-light text-dark">
            Chapter: {{ getChapterName(selectedChapter) }}
            <button 
              type="button" 
              class="btn-close btn-close-sm ms-1"
              @click="$emit('update:selectedChapter', '')"
            ></button>
          </span>
          
          <span v-if="selectedRole" class="badge bg-light text-dark">
            Role: {{ formatRole(selectedRole) }}
            <button 
              type="button" 
              class="btn-close btn-close-sm ms-1"
              @click="$emit('update:selectedRole', '')"
            ></button>
          </span>
          
          <button 
            type="button" 
            class="btn btn-link btn-sm text-decoration-none p-0"
            @click="clearAllFilters"
          >
            Clear all
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { capitalizeWords } from '@/utils/ui';

export default {
  name: 'MemberFilters',
  props: {
    searchTerm: {
      type: String,
      default: ''
    },
    selectedChapter: {
      type: [String, Number],
      default: ''
    },
    selectedRole: {
      type: String,
      default: ''
    },
    availableChapters: {
      type: Array,
      required: true
    }
  },
  emits: [
    'update:searchTerm',
    'update:selectedChapter', 
    'update:selectedRole'
  ],
  computed: {
    hasActiveFilters() {
      return this.searchTerm || this.selectedChapter || this.selectedRole;
    }
  },
  methods: {
    formatRole(role) {
      if (!role) return 'Member';
      return capitalizeWords(role.replace(/_/g, ' '));
    },
    
    getChapterName(chapterId) {
      const chapter = this.availableChapters.find(c => c.id.toString() === chapterId.toString());
      return chapter ? chapter.name : 'Unknown';
    },
    
    clearAllFilters() {
      this.$emit('update:searchTerm', '');
      this.$emit('update:selectedChapter', '');
      this.$emit('update:selectedRole', '');
    }
  }
};
</script>

<style scoped>
.input-group-text {
  border-radius: 0.375rem 0 0 0.375rem;
}

.badge .btn-close-sm {
  width: 0.75em;
  height: 0.75em;
}

.member-filters .form-select,
.member-filters .form-control {
  border: 1px solid #ced4da;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.member-filters .form-select:focus,
.member-filters .form-control:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}
</style>