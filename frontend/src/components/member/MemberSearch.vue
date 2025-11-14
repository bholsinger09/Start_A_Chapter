<template>
  <div class="member-search">
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
            @input="updateSearchTerm"
          >
        </div>
      </div>
      <div class="col-md-3">
        <select 
          class="form-select" 
          :value="selectedChapter"
          @change="updateSelectedChapter"
        >
          <option value="">All Chapters</option>
          <option 
            v-for="chapter in chapters" 
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
          @change="updateSelectedRole"
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
  </div>
</template>

<script>
/**
 * Focused component for member search and filtering.
 * Handles search term input and filter selection with clear prop interfaces.
 */
export default {
  name: 'MemberSearch',
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
    chapters: {
      type: Array,
      default: () => []
    }
  },
  emits: [
    'update:searchTerm',
    'update:selectedChapter', 
    'update:selectedRole'
  ],
  methods: {
    updateSearchTerm(event) {
      this.$emit('update:searchTerm', event.target.value);
    },
    updateSelectedChapter(event) {
      this.$emit('update:selectedChapter', event.target.value);
    },
    updateSelectedRole(event) {
      this.$emit('update:selectedRole', event.target.value);
    }
  }
};
</script>

<style scoped>
.member-search {
  /* Component-specific styles */
}

.input-group-text {
  background-color: var(--bs-light);
  border-color: var(--bs-border-color);
}

.form-select, .form-control {
  border-color: var(--bs-border-color);
}

.form-select:focus, .form-control:focus {
  border-color: var(--bs-primary);
  box-shadow: 0 0 0 0.2rem rgba(var(--bs-primary-rgb), 0.25);
}
</style>