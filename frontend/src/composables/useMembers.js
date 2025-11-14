import { ref, computed, onMounted } from 'vue';
import { memberService } from '@/services/memberService';
import { chapterService } from '@/services/chapterService';

/**
 * Composable for member management operations.
 * Provides reactive state and methods for member CRUD operations with proper error handling.
 */
export function useMembers() {
  // Reactive state
  const members = ref([]);
  const chapters = ref([]);
  const loading = ref(false);
  const error = ref(null);
  
  // Search and filter state
  const searchTerm = ref('');
  const selectedChapter = ref('');
  const selectedRole = ref('');
  
  // Pagination state
  const currentPage = ref(1);
  const itemsPerPage = ref(10);
  
  // Computed properties
  const filteredMembers = computed(() => {
    let filtered = members.value;
    
    // Apply search filter
    if (searchTerm.value) {
      const term = searchTerm.value.toLowerCase();
      filtered = filtered.filter(member => 
        member.firstName?.toLowerCase().includes(term) ||
        member.lastName?.toLowerCase().includes(term) ||
        member.email?.toLowerCase().includes(term) ||
        member.fullName?.toLowerCase().includes(term)
      );
    }
    
    // Apply chapter filter
    if (selectedChapter.value) {
      filtered = filtered.filter(member => 
        member.chapterId === parseInt(selectedChapter.value)
      );
    }
    
    // Apply role filter
    if (selectedRole.value) {
      filtered = filtered.filter(member => 
        member.role === selectedRole.value
      );
    }
    
    return filtered;
  });
  
  const paginatedMembers = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    return filteredMembers.value.slice(start, end);
  });
  
  const totalPages = computed(() => {
    return Math.ceil(filteredMembers.value.length / itemsPerPage.value);
  });
  
  // Methods
  const loadMembers = async () => {
    try {
      loading.value = true;
      error.value = null;
      
      const response = await memberService.getAllMembers();
      members.value = response.data || [];
    } catch (err) {
      console.error('Failed to load members:', err);
      error.value = 'Failed to load members. Please try again.';
      members.value = [];
    } finally {
      loading.value = false;
    }
  };
  
  const loadChapters = async () => {
    try {
      const response = await chapterService.getAllChapters();
      chapters.value = response.data || [];
    } catch (err) {
      console.error('Failed to load chapters:', err);
      chapters.value = [];
    }
  };
  
  const createMember = async (memberData) => {
    try {
      loading.value = true;
      error.value = null;
      
      const response = await memberService.createMember(memberData);
      const newMember = response.data;
      
      members.value.push(newMember);
      return newMember;
    } catch (err) {
      console.error('Failed to create member:', err);
      error.value = 'Failed to create member. Please check the data and try again.';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  const updateMember = async (id, memberData) => {
    try {
      loading.value = true;
      error.value = null;
      
      const response = await memberService.updateMember(id, memberData);
      const updatedMember = response.data;
      
      const index = members.value.findIndex(m => m.id === id);
      if (index !== -1) {
        members.value[index] = updatedMember;
      }
      
      return updatedMember;
    } catch (err) {
      console.error('Failed to update member:', err);
      error.value = 'Failed to update member. Please check the data and try again.';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  const deleteMember = async (id) => {
    try {
      loading.value = true;
      error.value = null;
      
      await memberService.deleteMember(id);
      
      const index = members.value.findIndex(m => m.id === id);
      if (index !== -1) {
        members.value.splice(index, 1);
      }
      
      // Adjust current page if necessary
      if (paginatedMembers.value.length === 0 && currentPage.value > 1) {
        currentPage.value -= 1;
      }
    } catch (err) {
      console.error('Failed to delete member:', err);
      error.value = 'Failed to delete member. Please try again.';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  const transferMember = async (memberId, newChapterId) => {
    try {
      loading.value = true;
      error.value = null;
      
      const response = await memberService.transferMember(memberId, newChapterId);
      const updatedMember = response.data;
      
      const index = members.value.findIndex(m => m.id === memberId);
      if (index !== -1) {
        members.value[index] = updatedMember;
      }
      
      return updatedMember;
    } catch (err) {
      console.error('Failed to transfer member:', err);
      error.value = 'Failed to transfer member. Please try again.';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  const searchMembers = async (searchTerm) => {
    try {
      loading.value = true;
      error.value = null;
      
      const response = await memberService.searchMembers(searchTerm);
      return response.data || [];
    } catch (err) {
      console.error('Failed to search members:', err);
      error.value = 'Failed to search members. Please try again.';
      return [];
    } finally {
      loading.value = false;
    }
  };
  
  const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
      currentPage.value = page;
    }
  };
  
  const resetFilters = () => {
    searchTerm.value = '';
    selectedChapter.value = '';
    selectedRole.value = '';
    currentPage.value = 1;
  };
  
  const refreshData = async () => {
    await Promise.all([
      loadMembers(),
      loadChapters()
    ]);
  };
  
  // Initialize data on mount
  onMounted(() => {
    refreshData();
  });
  
  return {
    // State
    members,
    chapters,
    loading,
    error,
    searchTerm,
    selectedChapter,
    selectedRole,
    currentPage,
    itemsPerPage,
    
    // Computed
    filteredMembers,
    paginatedMembers,
    totalPages,
    
    // Methods
    loadMembers,
    loadChapters,
    createMember,
    updateMember,
    deleteMember,
    transferMember,
    searchMembers,
    changePage,
    resetFilters,
    refreshData
  };
}