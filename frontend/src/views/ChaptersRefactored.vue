<!--
  Chapters View - Refactored using Clean Code and Successive Refinement Principles
  Demonstrates separation of concerns and component composition
-->
<template>
  <div class="chapters-view">
    <div class="container-fluid">
      <!-- Page Header -->
      <ChaptersHeader 
        @create-chapter="navigateToCreateChapter"
      />

      <!-- Search and Filters -->
      <ChaptersFilters
        v-model:search-term="searchTerm"
        v-model:selected-state="selectedState"
        :available-states="availableStates"
        :search-stats="searchStats"
        @search="handleSearch"
        @filter-change="handleFilterChange"
      />

      <!-- Loading State -->
      <LoadingSpinner
        v-if="isLoading"
        :loading="true"
        message="Loading chapters..."
        size="lg"
        centered
      />

      <!-- Chapters Content -->
      <div v-else-if="hasChapters">
        <!-- Chapters Grid -->
        <ChaptersGrid 
          :chapters="filteredChapters"
          @chapter-click="handleChapterClick"
        />

        <!-- Statistics -->
        <ChaptersStats
          v-if="chapterStatistics"
          :statistics="chapterStatistics"
          class="mt-5"
        />
      </div>

      <!-- Empty State -->
      <EmptyState
        v-else
        icon="bi-search"
        :title="emptyStateTitle"
        :message="emptyStateMessage"
        :show-action="!hasActiveSearch"
        action-text="Create Chapter"
        @action-click="navigateToCreateChapter"
      />

      <!-- Error State -->
      <ErrorBoundary
        v-if="hasError"
        :error="error"
        @retry="handleRetry"
        @dismiss="clearError"
      />
    </div>
  </div>
</template>

<script>
import { computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

// Composables
import { useChapters } from '@/composables/data/useChapters.js'
import { useSearch } from '@/composables/ui/useSearch.js'

// Components
import ChaptersHeader from '@/components/chapter/ChaptersHeader.vue'
import ChaptersFilters from '@/components/chapter/ChaptersFilters.vue'
import ChaptersGrid from '@/components/chapter/ChaptersGrid.vue'
import ChaptersStats from '@/components/chapter/ChaptersStats.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ErrorBoundary from '@/components/common/ErrorBoundary.vue'

export default {
  name: 'ChaptersView',
  
  components: {
    ChaptersHeader,
    ChaptersFilters,
    ChaptersGrid,
    ChaptersStats,
    LoadingSpinner,
    EmptyState,
    ErrorBoundary
  },

  setup() {
    const router = useRouter()

    // Composables
    const {
      chapters,
      isLoading,
      hasChapters,
      hasError,
      error,
      availableStates,
      chapterStatistics,
      loadChapters,
      clearError
    } = useChapters()

    const {
      searchTerm,
      filters,
      processItems,
      setFilter,
      hasActiveSearch,
      getSearchStats
    } = useSearch({
      searchFields: ['name', 'universityName', 'city', 'state'],
      debounceDelay: 300,
      minSearchLength: 2
    })

    // Local reactive state
    const selectedState = computed({
      get: () => filters.value.state || '',
      set: (value) => setFilter('state', value)
    })

    // Computed properties
    const filteredChapters = computed(() => {
      return processItems(chapters.value)
    })

    const searchStats = computed(() => {
      return getSearchStats(chapters.value.length, filteredChapters.value.length)
    })

    const emptyStateTitle = computed(() => {
      if (hasActiveSearch.value) {
        return 'No matching chapters'
      }
      return 'No chapters found'
    })

    const emptyStateMessage = computed(() => {
      if (hasActiveSearch.value) {
        return 'Try adjusting your search criteria or filters to find chapters.'
      }
      return 'Get started by creating your first chapter to connect with students.'
    })

    // Methods
    const navigateToCreateChapter = () => {
      router.push('/chapters/create')
    }

    const handleChapterClick = (chapter) => {
      router.push(`/chapters/${chapter.id}`)
    }

    const handleSearch = (term) => {
      searchTerm.value = term
    }

    const handleFilterChange = () => {
      // Trigger reactive updates
      // The search composable will handle the filtering automatically
    }

    const handleRetry = () => {
      loadChapters()
    }

    // Lifecycle hooks
    onMounted(() => {
      loadChapters()
    })

    // Watchers for reactive updates
    watch([searchTerm, selectedState], () => {
      // Filters are automatically applied via computed properties
      // This watcher is for any side effects if needed
    })

    return {
      // State
      searchTerm,
      selectedState,
      
      // Computed properties
      filteredChapters,
      availableStates,
      chapterStatistics,
      isLoading,
      hasChapters,
      hasError,
      error,
      hasActiveSearch,
      searchStats,
      emptyStateTitle,
      emptyStateMessage,
      
      // Methods
      navigateToCreateChapter,
      handleChapterClick,
      handleSearch,
      handleFilterChange,
      handleRetry,
      clearError
    }
  }
}
</script>

<style scoped>
.chapters-view {
  min-height: calc(100vh - 200px);
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .chapters-view .container-fluid {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

/* Animation for content transitions */
.chapters-view > * {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>