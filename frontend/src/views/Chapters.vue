<template>
  <div class="chapters">
    <div class="container">
      <h1>Chapters</h1>
      <p>Chapter management interface - Basic version</p>
      
      <div v-if="loading">Loading chapters...</div>
      <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
      <div v-else>
        <p>Found {{ chapters.length }} chapters</p>
        <div v-for="chapter in chapters" :key="chapter.id" class="card mb-2">
          <div class="card-body">
            <h5>{{ chapter.name }}</h5>
            <p>{{ chapter.university }} - {{ chapter.state }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'Chapters',
  setup() {
    const chapters = ref([])
    const loading = ref(true)
    const error = ref(null)

    const loadChapters = async () => {
      try {
        loading.value = true
        error.value = null
        
        // Mock data for now
        await new Promise(resolve => setTimeout(resolve, 1000))
        chapters.value = [
          { id: 1, name: 'Alpha Chapter', university: 'State University', state: 'CA' },
          { id: 2, name: 'Beta Chapter', university: 'Tech Institute', state: 'TX' },
          { id: 3, name: 'Gamma Chapter', university: 'City College', state: 'NY' }
        ]
      } catch (err) {
        error.value = 'Failed to load chapters'
        console.error('Error loading chapters:', err)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadChapters()
    })

    return {
      chapters,
      loading,
      error
    }
  }
}
</script>

<style scoped>
.chapters {
  padding: 2rem 0;
}
</style>
