<template>
  <nav aria-label="Member pagination" class="card-footer">
    <div class="row align-items-center">
      <div class="col-sm-6">
        <PaginationInfo 
          :current-page="currentPage"
          :total-pages="totalPages"
          :items-per-page="itemsPerPage"
          :total-items="totalItems"
        />
      </div>
      <div class="col-sm-6">
        <PaginationControls
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="$emit('page-change', $event)"
          @first="$emit('page-change', 1)"
          @last="$emit('page-change', totalPages)"
        />
      </div>
    </div>
  </nav>
</template>

<script>
import PaginationInfo from '../common/PaginationInfo.vue';
import PaginationControls from '../common/PaginationControls.vue';

/**
 * Focused component for member pagination.
 * Fixes: Large Class smell by extracting pagination logic.
 * Single Responsibility: Handle pagination display and navigation.
 */
export default {
  name: 'MemberPagination',
  components: {
    PaginationInfo,
    PaginationControls
  },
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    },
    itemsPerPage: {
      type: Number,
      default: 10
    },
    totalItems: {
      type: Number,
      default: 0
    }
  },
  emits: ['page-change']
};
</script>