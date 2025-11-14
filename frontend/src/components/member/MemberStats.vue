<!--
MemberStats Component  
Fixes: Large Classes smell - Extracted statistics logic from Members.vue
Single Responsibility: Display member statistics and counts
-->
<template>
  <div class="member-stats">
    <div class="row g-3">
      <!-- Total Members -->
      <div class="col-6 col-md-3">
        <div class="stat-card text-center">
          <div class="stat-icon text-primary">
            <i class="bi bi-people"></i>
          </div>
          <div class="stat-number">{{ totalMembers }}</div>
          <div class="stat-label">Total Members</div>
        </div>
      </div>

      <!-- Active Members -->
      <div class="col-6 col-md-3">
        <div class="stat-card text-center">
          <div class="stat-icon text-success">
            <i class="bi bi-person-check"></i>
          </div>
          <div class="stat-number">{{ activeMembers }}</div>
          <div class="stat-label">Active Members</div>
        </div>
      </div>

      <!-- Chapters -->
      <div class="col-6 col-md-3">
        <div class="stat-card text-center">
          <div class="stat-icon text-info">
            <i class="bi bi-building"></i>
          </div>
          <div class="stat-number">{{ uniqueChapters }}</div>
          <div class="stat-label">Chapters</div>
        </div>
      </div>

      <!-- Officers -->
      <div class="col-6 col-md-3">
        <div class="stat-card text-center">
          <div class="stat-icon text-warning">
            <i class="bi bi-star"></i>
          </div>
          <div class="stat-number">{{ officerCount }}</div>
          <div class="stat-label">Officers</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MemberStats',
  props: {
    members: {
      type: Array,
      required: true
    }
  },
  computed: {
    totalMembers() {
      return this.members.length;
    },
    
    activeMembers() {
      return this.members.filter(member => member.active).length;
    },
    
    uniqueChapters() {
      const chapterIds = new Set(
        this.members
          .map(member => member.chapterId)
          .filter(Boolean)
      );
      return chapterIds.size;
    },
    
    officerCount() {
      return this.members.filter(member => 
        member.role && !['MEMBER'].includes(member.role)
      ).length;
    }
  }
};
</script>

<style scoped>
.stat-card {
  background: white;
  border-radius: 0.5rem;
  padding: 1.5rem 1rem;
  border: 1px solid #e9ecef;
  transition: all 0.2s ease-in-out;
  height: 100%;
}

.stat-card:hover {
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 0.25rem;
  color: #212529;
}

.stat-label {
  font-size: 0.875rem;
  color: #6c757d;
  font-weight: 500;
}

@media (max-width: 576px) {
  .stat-card {
    padding: 1rem 0.5rem;
  }
  
  .stat-icon {
    font-size: 1.5rem;
  }
  
  .stat-number {
    font-size: 1.75rem;
  }
  
  .stat-label {
    font-size: 0.75rem;
  }
}
</style>