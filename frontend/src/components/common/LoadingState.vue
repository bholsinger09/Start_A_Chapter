<template>
  <div class="loading-state text-center py-5">
    <div class="spinner-border text-primary mb-3" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
    <p class="text-muted mb-0">{{ message }}</p>
  </div>
</template>

<script>
/**
 * Reusable loading state component.
 * Fixes: Duplicated Code smell across multiple views.
 * Single Responsibility: Display loading states consistently.
 */
export default {
  name: 'LoadingState',
  props: {
    message: {
      type: String,
      default: 'Loading...'
    }
  }
};
</script>

<style scoped>
.loading-state {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}
</style>