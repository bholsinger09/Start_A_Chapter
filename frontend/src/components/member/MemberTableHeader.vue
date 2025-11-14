<template>
  <thead class="table-light">
    <tr>
      <th scope="col">
        <SortableHeader 
          field="name" 
          :current-sort="currentSort"
          @sort="$emit('sort', $event)"
        >
          Name
        </SortableHeader>
      </th>
      <th scope="col">Email</th>
      <th scope="col">
        <SortableHeader 
          field="chapter" 
          :current-sort="currentSort"
          @sort="$emit('sort', $event)"
        >
          Chapter
        </SortableHeader>
      </th>
      <th scope="col">
        <SortableHeader 
          field="role" 
          :current-sort="currentSort"
          @sort="$emit('sort', $event)"
        >
          Role
        </SortableHeader>
      </th>
      <th scope="col">Status</th>
      <th scope="col" class="text-center">Actions</th>
    </tr>
  </thead>
</template>

<script>
import SortableHeader from '../common/SortableHeader.vue';

/**
 * Focused component for member table header.
 * Fixes: Duplicated Code smell by extracting header logic.
 * Single Responsibility: Handle table headers and sorting.
 */
export default {
  name: 'MemberTableHeader',
  components: {
    SortableHeader
  },
  props: {
    currentSort: {
      type: Object,
      default: () => ({ field: null, direction: 'asc' })
    }
  },
  emits: ['sort']
};
</script>