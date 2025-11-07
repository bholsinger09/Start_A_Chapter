<template>
  <div id="app">
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container">
        <router-link class="navbar-brand" to="/">
          <i class="bi bi-mortarboard-fill me-2"></i>
          Campus Chapter Organizer
        </router-link>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/" exact-active-class="active">
                <i class="bi bi-speedometer2 me-1"></i>Dashboard
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chapters" active-class="active">
                <i class="bi bi-building me-1"></i>Chapters
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chapters/create" active-class="active">
                <i class="bi bi-plus-circle me-1"></i>Create Chapter
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="container-fluid py-4">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-light py-4 mt-5">
      <div class="container">
        <div class="row">
          <div class="col-md-6">
            <p class="text-muted mb-0">
              © 2025 Campus Chapter Organizer. Built with Vue.js & Spring Boot.
            </p>
          </div>
          <div class="col-md-6 text-end">
            <p class="text-muted mb-0">
              <i class="bi bi-code-slash me-1"></i>
              Frontend: Vue.js | Backend: Spring Boot
            </p>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.navbar-brand {
  font-weight: bold;
}

.nav-link.active {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 0.375rem;
}

.btn {
  border-radius: 0.375rem;
}

.card {
  border-radius: 0.5rem;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}
</style>
