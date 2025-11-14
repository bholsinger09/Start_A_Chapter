<template>
  <div class="empty-state text-center py-5">
    <div class="empty-state-icon mb-3">
      <i :class="`${icon} text-muted`" style="font-size: 3rem;"></i>
    </div>
    <h4 class="text-muted mb-2">{{ title }}</h4>
    <p class="text-muted mb-4">{{ message }}</p>
    <slot name="actions">
      <!-- Optional action buttons can be inserted here -->
    </slot>
  </div>
</template>

<script>
/**
 * Reusable empty state component.
 * Fixes: Duplicated Code smell for empty states.
 * Single Responsibility: Display consistent empty states.
 */
export default {
  name: 'EmptyState',
  props: {
    icon: {
      type: String,
      default: 'bi-inbox'
    },
    title: {
      type: String,
      required: true
    },
    message: {
      type: String,
      required: true
    }
  }
};
</script>

<style scoped>
.empty-state {
  min-height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.empty-state-icon {
  opacity: 0.6;
}
</style>