<template>
  <div class="events">
    <div class="container-fluid">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
          <h2>
            <i class="bi bi-calendar-event me-2"></i>
            Events
          </h2>
          <p class="text-muted">Manage and discover campus events and activities.</p>
        </div>
      </div>

      <!-- Placeholder Content -->
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div class="card-body text-center py-5">
              <i class="bi bi-calendar-plus display-1 text-muted mb-4"></i>
              <h4 class="text-muted">Events Coming Soon</h4>
              <p class="text-muted">
                Event management functionality will be available in a future update.
              </p>
              <div class="mt-4">
                <button class="btn btn-primary me-2" disabled>
                  <i class="bi bi-plus-circle me-2"></i>
                  Create Event
                </button>
                <button class="btn btn-outline-primary" disabled>
                  <i class="bi bi-calendar-check me-2"></i>
                  View Calendar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Events'
}
</script>

<style scoped>
.card {
  border-radius: 0.5rem;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.display-1 {
  font-size: 4rem;
}
</style>