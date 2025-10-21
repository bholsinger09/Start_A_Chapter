<template>
  <div class="skeleton-loader">
    <!-- Card skeleton for dashboard -->
    <div v-if="type === 'card'" class="card border-0 shadow-sm">
      <div class="card-body">
        <div class="d-flex align-items-center">
          <div class="skeleton-circle me-3"></div>
          <div class="flex-grow-1">
            <div class="skeleton-line mb-2" style="width: 60%"></div>
            <div class="skeleton-line" style="width: 40%"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Table skeleton for members/events -->
    <div v-if="type === 'table'" class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead class="table-light">
              <tr>
                <th><div class="skeleton-line" style="width: 80%"></div></th>
                <th><div class="skeleton-line" style="width: 60%"></div></th>
                <th><div class="skeleton-line" style="width: 70%"></div></th>
                <th><div class="skeleton-line" style="width: 50%"></div></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="n in rows" :key="n">
                <td><div class="skeleton-line" style="width: 90%"></div></td>
                <td><div class="skeleton-line" style="width: 70%"></div></td>
                <td><div class="skeleton-line" style="width: 85%"></div></td>
                <td><div class="skeleton-line" style="width: 60%"></div></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- List skeleton -->
    <div v-if="type === 'list'">
      <div v-for="n in rows" :key="n" class="d-flex align-items-center mb-3">
        <div class="skeleton-circle me-3"></div>
        <div class="flex-grow-1">
          <div class="skeleton-line mb-1" style="width: 75%"></div>
          <div class="skeleton-line" style="width: 45%"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SkeletonLoader',
  props: {
    type: {
      type: String,
      default: 'card',
      validator: value => ['card', 'table', 'list'].includes(value)
    },
    rows: {
      type: Number,
      default: 3
    }
  }
}
</script>

<style scoped>
.skeleton-line {
  height: 12px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  border-radius: 6px;
  animation: skeleton-loading 1.5s infinite;
}

.skeleton-circle {
  width: 48px;
  height: 48px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  border-radius: 50%;
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

[data-theme="dark"] .skeleton-line,
[data-theme="dark"] .skeleton-circle {
  background: linear-gradient(90deg, #2a2a2a 25%, #3a3a3a 50%, #2a2a2a 75%);
  background-size: 200% 100%;
}
</style>