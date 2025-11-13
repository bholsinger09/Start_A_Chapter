<!--
  ChaptersHeader Component - Focused Header Component
  Single Responsibility: Display page header with actions
-->
<template>
  <div class="chapters-header">
    <div class="row mb-4">
      <div class="col-md-8">
        <h2 class="page-title">
          <i class="bi bi-building me-2"></i>
          Campus Chapters
        </h2>
        <p class="page-description text-muted">
          Explore and manage student organizations across universities.
        </p>
      </div>
      
      <div class="col-md-4 text-end">
        <div class="header-actions">
          <button
            type="button"
            class="btn btn-outline-secondary me-2"
            @click="handleRefresh"
            :disabled="loading"
            :title="refreshTooltip"
          >
            <i class="bi bi-arrow-clockwise me-1" :class="{ 'spinning': loading }"></i>
            <span class="d-none d-sm-inline">{{ refreshText }}</span>
          </button>
          
          <button
            type="button"
            class="btn btn-primary"
            @click="handleCreateChapter"
            :disabled="loading"
          >
            <i class="bi bi-plus-circle me-2"></i>
            Create Chapter
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'ChaptersHeader',
  
  props: {
    loading: {
      type: Boolean,
      default: false
    },
    
    totalChapters: {
      type: Number,
      default: 0
    }
  },

  emits: [
    'create-chapter',
    'refresh'
  ],

  setup(props, { emit }) {
    // Computed properties
    const refreshText = computed(() => {
      return props.loading ? 'Loading...' : 'Refresh'
    })

    const refreshTooltip = computed(() => {
      if (props.loading) return 'Loading chapters...'
      return `Refresh chapters list (${props.totalChapters} total)`
    })

    // Event handlers
    const handleCreateChapter = () => {
      emit('create-chapter')
    }

    const handleRefresh = () => {
      if (!props.loading) {
        emit('refresh')
      }
    }

    return {
      // Computed properties
      refreshText,
      refreshTooltip,
      
      // Event handlers
      handleCreateChapter,
      handleRefresh
    }
  }
}
</script>

<style scoped>
.chapters-header {
  margin-bottom: 2rem;
}

.page-title {
  color: var(--bs-dark);
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.page-description {
  margin-bottom: 0;
  font-size: 1.1rem;
}

.header-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.5rem;
}

/* Spinning animation for refresh button */
.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Responsive design */
@media (max-width: 768px) {
  .chapters-header .row {
    text-align: center;
  }
  
  .header-actions {
    justify-content: center;
    margin-top: 1rem;
  }
  
  .page-title {
    font-size: 1.75rem;
  }
  
  .page-description {
    font-size: 1rem;
  }
}

@media (max-width: 576px) {
  .header-actions .btn {
    flex: 1;
    max-width: 150px;
  }
  
  .header-actions .btn .d-none {
    display: inline !important;
  }
}

/* Hover effects */
.header-actions .btn {
  transition: all 0.2s ease-in-out;
}

.header-actions .btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-actions .btn:active {
  transform: translateY(0);
}

/* Focus states for accessibility */
.header-actions .btn:focus-visible {
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}
</style>