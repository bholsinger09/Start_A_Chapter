<template>
  <div class="members-page">
    <div class="container-fluid">
      <!-- Header -->
      <PageHeader
        title="Chapter Members"
        description="Connect with fellow students and chapter members."
        :loading="loading"
        @refresh="refreshData"
        @add="openAddMemberModal"
      />

      <!-- Search and Filters -->
      <MemberSearch
        v-model:searchTerm="searchTerm"
        v-model:selectedChapter="selectedChapter"
        v-model:selectedRole="selectedRole"
        :chapters="chapters"
      />

      <!-- Error Alert -->
      <ErrorAlert 
        v-if="error"
        :message="error"
        @dismiss="error = null"
      />

      <!-- Members Table -->
      <MemberTable
        :members="paginatedMembers"
        :loading="loading"
        :current-page="currentPage"
        :total-pages="totalPages"
        @view="handleViewMember"
        @edit="handleEditMember"
        @delete="handleDeleteMember"
        @add="openAddMemberModal"
        @page-change="changePage"
      />
    </div>

    <!-- Member Modal (for add/edit) -->
    <MemberModal
      v-if="showModal"
      :member="selectedMember"
      :chapters="chapters"
      :is-edit="isEditMode"
      @save="handleSaveMember"
      @cancel="closeModal"
    />

    <!-- Confirmation Modal -->
    <ConfirmationModal
      v-if="showConfirmation"
      :title="confirmationTitle"
      :message="confirmationMessage"
      @confirm="handleConfirmAction"
      @cancel="showConfirmation = false"
    />
  </div>
</template>

<script>
import { ref } from 'vue';
import { useMembers } from '@/composables/useMembers';
import MemberSearch from '@/components/member/MemberSearch.vue';
import MemberTable from '@/components/member/MemberTable.vue';
import PageHeader from '@/components/common/PageHeader.vue';
import ErrorAlert from '@/components/common/ErrorAlert.vue';
import MemberModal from '@/components/member/MemberModal.vue';
import ConfirmationModal from '@/components/common/ConfirmationModal.vue';

/**
 * Refactored Members page using smaller, focused components and composables.
 * Clean separation of concerns with reactive state management.
 */
export default {
  name: 'MembersPage',
  components: {
    MemberSearch,
    MemberTable,
    PageHeader,
    ErrorAlert,
    MemberModal,
    ConfirmationModal
  },
  setup() {
    // Use the members composable for state management
    const {
      // State
      members,
      chapters,
      loading,
      error,
      searchTerm,
      selectedChapter,
      selectedRole,
      currentPage,
      
      // Computed
      paginatedMembers,
      totalPages,
      
      // Methods
      createMember,
      updateMember,
      deleteMember,
      changePage,
      refreshData
    } = useMembers();

    // Modal state
    const showModal = ref(false);
    const selectedMember = ref(null);
    const isEditMode = ref(false);
    
    // Confirmation modal state
    const showConfirmation = ref(false);
    const confirmationTitle = ref('');
    const confirmationMessage = ref('');
    const pendingAction = ref(null);

    // Modal methods
    const openAddMemberModal = () => {
      selectedMember.value = null;
      isEditMode.value = false;
      showModal.value = true;
    };

    const closeModal = () => {
      showModal.value = false;
      selectedMember.value = null;
      isEditMode.value = false;
    };

    // Member action handlers
    const handleViewMember = (member) => {
      // Navigate to member detail view or show read-only modal
      console.log('View member:', member);
      // Could emit navigation event or use router.push
    };

    const handleEditMember = (member) => {
      selectedMember.value = { ...member }; // Clone to avoid direct mutation
      isEditMode.value = true;
      showModal.value = true;
    };

    const handleDeleteMember = (member) => {
      confirmationTitle.value = 'Delete Member';
      confirmationMessage.value = `Are you sure you want to delete ${member.fullName}? This action cannot be undone.`;
      pendingAction.value = () => deleteMember(member.id);
      showConfirmation.value = true;
    };

    const handleSaveMember = async (memberData) => {
      try {
        if (isEditMode.value) {
          await updateMember(selectedMember.value.id, memberData);
        } else {
          await createMember(memberData);
        }
        closeModal();
      } catch (err) {
        // Error is handled by the composable and shown in the error alert
        console.error('Failed to save member:', err);
      }
    };

    const handleConfirmAction = async () => {
      try {
        if (pendingAction.value) {
          await pendingAction.value();
        }
      } catch (err) {
        console.error('Failed to execute action:', err);
      } finally {
        showConfirmation.value = false;
        pendingAction.value = null;
      }
    };

    return {
      // State from composable
      members,
      chapters,
      loading,
      error,
      searchTerm,
      selectedChapter,
      selectedRole,
      currentPage,
      paginatedMembers,
      totalPages,
      
      // Modal state
      showModal,
      selectedMember,
      isEditMode,
      showConfirmation,
      confirmationTitle,
      confirmationMessage,
      
      // Methods
      refreshData,
      changePage,
      openAddMemberModal,
      closeModal,
      handleViewMember,
      handleEditMember,
      handleDeleteMember,
      handleSaveMember,
      handleConfirmAction
    };
  }
};
</script>

<style scoped>
.members-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.container-fluid {
  padding: 2rem 1.5rem;
}

@media (max-width: 768px) {
  .container-fluid {
    padding: 1rem;
  }
}
</style>