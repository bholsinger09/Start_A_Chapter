<template>
  <div class="test-chapters">
    <h1>TEST - Simple Chapters Component</h1>
    <p>This is a test component to verify Vue routing and component rendering works.</p>
    <p>Loading: {{ loading }}</p>
    <p>Chapters length: {{ chapters.length }}</p>
    <p>Search Results length: {{ searchResults.length }}</p>
    
    <div v-if="searchResults.length > 0">
      <h2>Chapters Found:</h2>
      <ul>
        <li v-for="chapter in searchResults" :key="chapter.id">
          {{ chapter.name }} - {{ chapter.university }}
        </li>
      </ul>
    </div>
    <div v-else>
      <p>No chapters in searchResults</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TestChapters',
  data() {
    return {
      loading: false,
      chapters: [
        { id: 1, name: "Test Chapter 1", university: "Test University 1" },
        { id: 2, name: "Test Chapter 2", university: "Test University 2" }
      ],
      searchResults: [
        { id: 1, name: "Test Chapter 1", university: "Test University 1" },
        { id: 2, name: "Test Chapter 2", university: "Test University 2" }
      ]
    }
  },
  mounted() {
    console.log('TestChapters component mounted successfully!')
    console.log('Chapters:', this.chapters)
    console.log('Search Results:', this.searchResults)
  }
}
</script>