<template>
  <div class="about-page">
    <!-- Hero Section -->
    <section class="hero-section bg-primary text-white py-5">
      <div class="container">
        <div class="row justify-content-center text-center">
          <div class="col-lg-8">
            <h1 class="display-4 fw-bold mb-4">
              <i class="bi bi-info-circle me-3"></i>
              About Campus Chapter Organizer
            </h1>
            <p class="lead mb-4">
              Empowering student organizations with modern management tools
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-lg-10">
          
          <!-- Purpose Section -->
          <section class="mb-5">
            <div class="card shadow-sm border-0">
              <div class="card-body p-4">
                <h2 class="h3 text-primary mb-4">
                  <i class="bi bi-target me-2"></i>
                  Our Purpose
                </h2>
                <p class="mb-3">
                  Campus Chapter Organizer is an <strong>educational demonstration project</strong> designed to showcase modern web application development and student organization management capabilities. This platform serves as a learning tool and proof-of-concept for:
                </p>
                <ul class="list-unstyled ps-3">
                  <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i> Modern full-stack web development practices</li>
                  <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i> Student organization management systems</li>
                  <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i> Event coordination and member engagement tools</li>
                  <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i> Blog and communication features</li>
                  <li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i> Responsive design and user experience</li>
                </ul>
              </div>
            </div>
          </section>

          <!-- Important Disclaimer -->
          <section class="mb-5">
            <div class="card shadow-sm border-warning">
              <div class="card-header bg-warning bg-opacity-10">
                <h2 class="h3 text-warning mb-0">
                  <i class="bi bi-exclamation-triangle me-2"></i>
                  Important Disclaimer
                </h2>
              </div>
              <div class="card-body p-4">
                <div class="alert alert-warning d-flex align-items-center mb-4" role="alert">
                  <i class="bi bi-info-circle-fill me-3 fs-4"></i>
                  <div>
                    <strong>This is NOT an official Turning Point USA website or application.</strong>
                  </div>
                </div>
                
                <p class="mb-3">
                  Campus Chapter Organizer is an <strong>independent educational project</strong> created for demonstration and learning purposes. This application:
                </p>
                
                <ul class="mb-4">
                  <li class="mb-2">Is <strong>not affiliated with, endorsed by, or connected to Turning Point USA</strong> in any official capacity</li>
                  <li class="mb-2">Does not represent the views, policies, or positions of Turning Point USA</li>
                  <li class="mb-2">Is not authorized to collect real membership information or conduct official business</li>
                  <li class="mb-2">Uses placeholder data and simulated organizational structures for demonstration only</li>
                  <li class="mb-2">Should not be used for actual organizational management or official purposes</li>
                </ul>

                <div class="bg-light p-3 rounded">
                  <p class="mb-0 small">
                    <strong>For official Turning Point USA information, chapters, events, and membership:</strong><br>
                    Please visit the official website at 
                    <a href="https://www.tpusa.com" target="_blank" rel="noopener noreferrer" class="text-decoration-none">
                      <strong>www.tpusa.com</strong>
                      <i class="bi bi-box-arrow-up-right ms-1"></i>
                    </a>
                  </p>
                </div>
              </div>
            </div>
          </section>

          <!-- Technology Stack -->
          <section class="mb-5">
            <div class="card shadow-sm border-0">
              <div class="card-body p-4">
                <h2 class="h3 text-primary mb-4">
                  <i class="bi bi-gear me-2"></i>
                  Technology Stack
                </h2>
                <p class="mb-3">
                  This application demonstrates modern web development technologies and best practices:
                </p>
                
                <div class="row g-3">
                  <div class="col-md-6">
                    <div class="d-flex align-items-center p-3 bg-light rounded">
                      <i class="bi bi-browser-chrome text-primary me-3 fs-4"></i>
                      <div>
                        <h5 class="mb-1">Frontend</h5>
                        <small class="text-muted">Vue.js 3 & Bootstrap 5</small>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex align-items-center p-3 bg-light rounded">
                      <i class="bi bi-server text-success me-3 fs-4"></i>
                      <div>
                        <h5 class="mb-1">Backend</h5>
                        <small class="text-muted">Spring Boot & Java</small>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex align-items-center p-3 bg-light rounded">
                      <i class="bi bi-database text-info me-3 fs-4"></i>
                      <div>
                        <h5 class="mb-1">Database</h5>
                        <small class="text-muted">PostgreSQL</small>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex align-items-center p-3 bg-light rounded">
                      <i class="bi bi-cloud text-warning me-3 fs-4"></i>
                      <div>
                        <h5 class="mb-1">Deployment</h5>
                        <small class="text-muted">Docker & AWS</small>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Features -->
          <section class="mb-5">
            <div class="card shadow-sm border-0">
              <div class="card-body p-4">
                <h2 class="h3 text-primary mb-4">
                  <i class="bi bi-stars me-2"></i>
                  Demonstration Features
                </h2>
                <div class="row g-4">
                  <div class="col-md-6">
                    <div class="feature-item">
                      <h5 class="text-secondary">
                        <i class="bi bi-people text-primary me-2"></i>
                        Member Management
                      </h5>
                      <p class="mb-0 small text-muted">
                        Comprehensive member profiles, roles, and chapter assignments
                      </p>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="feature-item">
                      <h5 class="text-secondary">
                        <i class="bi bi-calendar-event text-primary me-2"></i>
                        Event Coordination
                      </h5>
                      <p class="mb-0 small text-muted">
                        Event planning, scheduling, and attendance tracking
                      </p>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="feature-item">
                      <h5 class="text-secondary">
                        <i class="bi bi-journal-text text-primary me-2"></i>
                        Blog Platform
                      </h5>
                      <p class="mb-0 small text-muted">
                        Community blogging with @mentions and comment system
                      </p>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="feature-item">
                      <h5 class="text-secondary">
                        <i class="bi bi-graph-up text-primary me-2"></i>
                        Analytics Dashboard
                      </h5>
                      <p class="mb-0 small text-muted">
                        Metrics and insights for organization management
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Legal & Contact -->
          <section class="mb-5">
            <div class="card shadow-sm border-0">
              <div class="card-body p-4">
                <h2 class="h3 text-primary mb-4">
                  <i class="bi bi-shield-check me-2"></i>
                  Legal Information
                </h2>
                
                <div class="row g-4">
                  <div class="col-md-6">
                    <h5 class="text-secondary">Educational Use</h5>
                    <p class="small text-muted mb-0">
                      This project is developed for educational and demonstration purposes. 
                      All data is simulated and not intended for production use.
                    </p>
                  </div>
                  <div class="col-md-6">
                    <h5 class="text-secondary">Privacy</h5>
                    <p class="small text-muted mb-0">
                      No real personal information is collected. All member data is 
                      generated for demonstration purposes only.
                    </p>
                  </div>
                  <div class="col-md-6">
                    <h5 class="text-secondary">Intellectual Property</h5>
                    <p class="small text-muted mb-0">
                      This application respects all intellectual property rights. 
                      Turning Point USA is a registered trademark of its respective owners.
                    </p>
                  </div>
                  <div class="col-md-6">
                    <h5 class="text-secondary">Contact</h5>
                    <p class="small text-muted mb-0">
                      This is a portfolio project. For questions about the technical 
                      implementation, please refer to the project documentation.
                    </p>
                  </div>
                </div>

                <hr class="my-4">

                <div class="text-center">
                  <h5 class="text-secondary mb-3">Official Turning Point USA</h5>
                  <p class="mb-3">
                    For authentic Turning Point USA information, official chapters, events, membership, and resources:
                  </p>
                  <a 
                    href="https://www.tpusa.com" 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    class="btn btn-primary btn-lg"
                  >
                    <i class="bi bi-globe me-2"></i>
                    Visit Official TPUSA Website
                    <i class="bi bi-box-arrow-up-right ms-2"></i>
                  </a>
                </div>
              </div>
            </div>
          </section>

          <!-- Footer Note -->
          <section class="text-center">
            <div class="card bg-light border-0">
              <div class="card-body p-4">
                <p class="mb-2">
                  <i class="bi bi-code-slash text-primary me-2"></i>
                  <strong>Campus Chapter Organizer</strong> - Educational Web Development Project
                </p>
                <small class="text-muted">
                  Built with Vue.js, Spring Boot, and modern web technologies • 
                  Not affiliated with any official organization
                </small>
              </div>
            </div>
          </section>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'About',
  setup() {
    // Set page title
    document.title = 'About - Campus Chapter Organizer'
    
    return {
      // No reactive data needed for this static page
    }
  }
}
</script>

<style scoped>
.hero-section {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.feature-item {
  padding: 1rem;
  border-left: 3px solid var(--bs-primary);
  background: #f8f9fa;
  border-radius: 0 0.375rem 0.375rem 0;
}

.card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.alert {
  border-left: 4px solid #ffc107;
}

.bg-light {
  background-color: #f8f9fa !important;
}

@media (max-width: 768px) {
  .display-4 {
    font-size: 2.5rem;
  }
  
  .hero-section {
    padding: 3rem 0;
  }
}
</style>
