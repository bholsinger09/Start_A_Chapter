<template>
  <tr>
    <td>
      <div class="d-flex align-items-center">
        <div class="avatar-circle me-3">
          {{ initials }}
        </div>
        <div>
          <div class="fw-medium">{{ member.fullName }}</div>
          <div class="text-muted small">{{ member.major || 'No major specified' }}</div>
        </div>
      </div>
    </td>
    <td>
      <a :href="'mailto:' + member.email" class="text-decoration-none">
        {{ member.email }}
      </a>
    </td>
    <td>
      <span class="badge bg-light text-dark">
        {{ member.chapterName || 'No Chapter' }}
      </span>
    </td>
    <td>
      <span 
        class="badge" 
        :class="getRoleBadgeClass(member.role)"
      >
        {{ formatRole(member.role) }}
      </span>
    </td>
    <td>
      <span 
        class="badge" 
        :class="member.active ? 'bg-success' : 'bg-secondary'"
      >
        {{ member.active ? 'Active' : 'Inactive' }}
      </span>
    </td>
    <td>
      <div class="btn-group" role="group">
        <button 
          class="btn btn-sm btn-outline-primary" 
          @click="viewMember"
          title="View Details"
        >
          <i class="bi bi-eye"></i>
        </button>
        <button 
          class="btn btn-sm btn-outline-secondary" 
          @click="editMember"
          title="Edit Member"
        >
          <i class="bi bi-pencil"></i>
        </button>
        <button 
          class="btn btn-sm btn-outline-danger" 
          @click="deleteMember"
          title="Delete Member"
        >
          <i class="bi bi-trash"></i>
        </button>
      </div>
    </td>
  </tr>
</template>

<script>
/**
 * Focused component for displaying a single member row in a table.
 * Handles member display logic and action buttons with clear event emission.
 */
export default {
  name: 'MemberRow',
  props: {
    member: {
      type: Object,
      required: true,
      validator(value) {
        return value && 
               typeof value.id !== 'undefined' &&
               typeof value.firstName === 'string' &&
               typeof value.lastName === 'string' &&
               typeof value.email === 'string';
      }
    }
  },
  emits: ['view', 'edit', 'delete'],
  computed: {
    initials() {
      const first = this.member.firstName?.[0] || '';
      const last = this.member.lastName?.[0] || '';
      return (first + last).toUpperCase();
    }
  },
  methods: {
    formatRole(role) {
      if (!role) return 'Member';
      return role.replace('_', ' ')
                 .split(' ')
                 .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
                 .join(' ');
    },
    
    getRoleBadgeClass(role) {
      const roleClasses = {
        'PRESIDENT': 'bg-primary',
        'VICE_PRESIDENT': 'bg-info',
        'TREASURER': 'bg-warning',
        'SECRETARY': 'bg-success',
        'MEMBER': 'bg-secondary'
      };
      return roleClasses[role] || 'bg-secondary';
    },
    
    viewMember() {
      this.$emit('view', this.member);
    },
    
    editMember() {
      this.$emit('edit', this.member);
    },
    
    deleteMember() {
      this.$emit('delete', this.member);
    }
  }
};
</script>

<style scoped>
.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.btn-group .btn {
  border-radius: 0.375rem;
  margin-right: 2px;
}

.btn-group .btn:last-child {
  margin-right: 0;
}

.badge {
  font-size: 0.75rem;
}

.text-muted {
  font-size: 0.875rem;
}
</style>