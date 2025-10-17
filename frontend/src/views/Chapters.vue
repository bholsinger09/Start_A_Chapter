<template><template><template><template>

  <div class="chapters">

    <div class="container">  <div class="chapters">

      <!-- Header -->

      <div class="row mb-4">    <div class="container">  <div class="chapters">  <div class="chapters">

        <div class="col">

          <h1 class="display-4 text-center mb-3">      <!-- Header -->

            <i class="bi bi-building text-primary me-3"></i>

            Chapters      <div class="row mb-4">    <div class="container">    <div class="container">

          </h1>

          <p class="lead text-center text-muted">        <div class="col">

            Manage university chapters across the network

          </p>          <h1 class="display-4 text-center mb-3">      <!-- Header -->      <!-- Header -->

        </div>

      </div>            <i class="bi bi-building text-primary me-3"></i>



      <!-- Enhanced Search Component -->            Chapters      <div class="row mb-4">      <div class="row mb-4">

      <div class="row mb-4">

        <div class="col">          </h1>

          <EnhancedSearch 

            ref="enhancedSearch"          <p class="lead text-center text-muted">        <div class="col">        <div class="col">

            :chapters="chapters"

            :available-states="availableStates"            Manage university chapters across the network

            :available-cities="availableCities"

            @search="handleSearch"          </p>          <h1 class="display-4 text-center mb-3">          <h1 class="display-4 text-center mb-3">

            @advanced-search="handleAdvancedSearch"

          />        </div>

        </div>

      </div>      </div>            <i class="bi bi-building text-primary me-3"></i>            <i class="bi bi-building text-primary me-3"></i>



      <!-- Analytics Section -->

      <div class="row mb-4">

        <div class="col">      <!-- Enhanced Search Component -->            Chapters            Chapters

          <div class="card bg-light border-0">

            <div class="card-body">      <div class="row mb-4">

              <div class="d-flex justify-content-between align-items-center mb-3">

                <h5 class="card-title mb-0">        <div class="col">          </h1>          </h1>

                  <i class="bi bi-graph-up text-success me-2"></i>

                  Chapter Analytics          <EnhancedSearch 

                </h5>

                <button             ref="enhancedSearch"          <p class="lead text-center text-muted">          <p class="lead text-center text-muted">

                  class="btn btn-outline-primary btn-sm"

                  @click="showAnalytics = !showAnalytics"            :chapters="chapters"

                >

                  <i :class="showAnalytics ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>            :available-states="availableStates"            Manage university chapters across the network            Manage university chapters across the network

                  {{ showAnalytics ? 'Hide' : 'Show' }} Analytics

                </button>            :available-cities="availableCities"

              </div>

                          @search="handleSearch"          </p>          </p>

              <div v-if="showAnalytics && !loading" class="mb-4">

                <ChapterAnalytics             @advanced-search="handleAdvancedSearch"

                  :chapters="searchResults" 

                  :members="members"          />        </div>        </div>

                />

              </div>        </div>

            </div>

          </div>      </div>      </div>      </div>

        </div>

      </div>



      <!-- View Controls & Sorting -->      <!-- Debug Info -->

      <div class="row mb-3">

        <div class="col-12">      <div class="alert alert-info mb-4">

          <div class="d-flex justify-content-between align-items-center">

            <div class="d-flex gap-2">        <h5>Debug Information:</h5>      <!-- Debug Info -->      <!-- Debug Info -->

              <select 

                class="form-select"        <p><strong>Loading:</strong> {{ loading }}</p>

                style="width: auto;"

                v-model="sortBy"        <p><strong>Chapters:</strong> {{ chapters.length }} items</p>      <div class="alert alert-info mb-4">      <div class="alert alert-info mb-4">

                @change="applySorting"

              >        <p><strong>Search Results:</strong> {{ searchResults.length }} items</p>

                <option value="name">Sort by Name</option>

                <option value="university">Sort by University</option>        <p><strong>Current View:</strong> {{ currentView }}</p>        <h5>Debug Information:</h5>        <h5>Debug Information:</h5>

                <option value="state">Sort by State</option>

                <option value="city">Sort by City</option>      </div>

              </select>

              <select         <p><strong>Loading:</strong> {{ loading }}</p>        <p><strong>Loading:</strong> {{ loading }}</p>

                class="form-select"

                style="width: auto;"      <!-- Loading State -->

                v-model="sortOrder"

                @change="applySorting"      <div v-if="loading" class="text-center py-4">        <p><strong>Chapters:</strong> {{ chapters.length }} items</p>        <p><strong>Chapters:</strong> {{ chapters.length }} items</p>

              >

                <option value="asc">A-Z</option>        <div class="spinner-border text-primary" role="status">

                <option value="desc">Z-A</option>

              </select>          <span class="visually-hidden">Loading...</span>        <p><strong>Search Results:</strong> {{ searchResults.length }} items</p>        <p><strong>Search Results:</strong> {{ searchResults.length }} items</p>

              <button 

                class="btn btn-outline-secondary"        </div>

                @click="toggleCurrentView"

                :title="currentView === 'table' ? 'Switch to Card View' : 'Switch to Table View'"        <p class="mt-2">Loading chapters...</p>        <p><strong>Current View:</strong> {{ currentView }}</p>        <p><strong>Current View:</strong> {{ currentView }}</p>

              >

                <i :class="currentView === 'table' ? 'bi bi-grid-3x3-gap' : 'bi bi-table'"></i>      </div>

              </button>

            </div>      </div>      </div>

            <div class="text-muted">

              {{ searchResults.length }} of {{ chapters.length }} chapters      <!-- Chapters Table -->

            </div>

          </div>      <div v-else class="card">

        </div>

      </div>        <div class="card-header bg-light">



      <!-- Debug Info -->          <h5 class="card-title mb-0">      <!-- Loading State -->      <!-- Loading State -->

      <div class="alert alert-info mb-4">

        <h5>Debug Information:</h5>            <i class="bi bi-table me-2"></i>

        <p><strong>Loading:</strong> {{ loading }}</p>

        <p><strong>Chapters:</strong> {{ chapters.length }} items</p>            Chapters ({{ searchResults.length }})      <div v-if="loading" class="text-center py-4">      <div v-if="loading" class="text-center py-4">

        <p><strong>Search Results:</strong> {{ searchResults.length }} items</p>

        <p><strong>Current View:</strong> {{ currentView }}</p>          </h5>

        <p><strong>Show Analytics:</strong> {{ showAnalytics }}</p>

      </div>        </div>        <div class="spinner-border text-primary" role="status">        <div class="spinner-border text-primary" role="status">



      <!-- Loading State -->        <div class="card-body p-0">

      <div v-if="loading" class="text-center py-4">

        <div class="spinner-border text-primary" role="status">          <div v-if="searchResults.length === 0" class="text-center py-4 text-muted">          <span class="visually-hidden">Loading...</span>          <span class="visually-hidden">Loading...</span>

          <span class="visually-hidden">Loading...</span>

        </div>            <i class="bi bi-inbox display-4 mb-3"></i>

        <p class="mt-2">Loading chapters...</p>

      </div>            <p>No chapters found</p>        </div>        </div>



      <!-- Chapters Content -->          </div>

      <div v-else class="card">

        <div class="card-header bg-light">                  <p class="mt-2">Loading chapters...</p>        <p class="mt-2">Loading chapters...</p>

          <h5 class="card-title mb-0">

            <i class="bi bi-table me-2"></i>          <!-- Simple Table View -->

            Chapters ({{ searchResults.length }})

          </h5>          <div v-else class="table-responsive">      </div>      </div>

        </div>

        <div class="card-body p-0">            <table class="table table-hover mb-0">

          <div v-if="searchResults.length === 0" class="text-center py-4 text-muted">

            <i class="bi bi-inbox display-4 mb-3"></i>              <thead class="table-light">

            <p>No chapters found matching your criteria</p>

          </div>                <tr>

          

          <!-- Table View -->                  <th>Chapter Name</th>      <!-- Chapters Table -->      <!-- Chapters Table -->

          <div v-else-if="currentView === 'table'" class="table-responsive">

            <table class="table table-hover mb-0">                  <th>University</th>

              <thead class="table-light">

                <tr>                  <th>Location</th>      <div v-else class="card">      <div v-else class="card">

                  <th>

                    <button                   <th>Actions</th>

                      class="btn btn-sm btn-link text-decoration-none p-0 fw-bold text-dark"

                      @click="sortChapters('name')"                </tr>        <div class="card-header bg-light">        <div class="card-header bg-light">

                    >

                      Chapter Name              </thead>

                      <i 

                        v-if="sortBy === 'name'"               <tbody>          <h5 class="card-title mb-0">          <h5 class="card-title mb-0">

                        :class="`bi ${sortOrder === 'asc' ? 'bi-arrow-up' : 'bi-arrow-down'}`"

                      ></i>                <tr v-for="chapter in searchResults" :key="chapter.id">

                    </button>

                  </th>                  <td>            <i class="bi bi-table me-2"></i>            <i class="bi bi-table me-2"></i>

                  <th>

                    <button                     <div class="fw-bold text-primary">{{ chapter.name }}</div>

                      class="btn btn-sm btn-link text-decoration-none p-0 fw-bold text-dark"

                      @click="sortChapters('university')"                    <small class="text-muted">{{ chapter.description || 'No description' }}</small>            Chapters ({{ searchResults.length }})            Chapters ({{ searchResults.length }})

                    >

                      University                  </td>

                      <i 

                        v-if="sortBy === 'university'"                   <td>{{ chapter.university || chapter.universityName }}</td>          </h5>          </h5>

                        :class="`bi ${sortOrder === 'asc' ? 'bi-arrow-up' : 'bi-arrow-down'}`"

                      ></i>                  <td>{{ chapter.city }}, {{ chapter.state }}</td>

                    </button>

                  </th>                  <td>        </div>        </div>

                  <th>

                    <button                     <button class="btn btn-sm btn-outline-primary me-1" title="View">

                      class="btn btn-sm btn-link text-decoration-none p-0 fw-bold text-dark"

                      @click="sortChapters('city')"                      <i class="bi bi-eye"></i>        <div class="card-body p-0">        <div class="card-body p-0">

                    >

                      Location                    </button>

                      <i 

                        v-if="sortBy === 'city'"                     <button class="btn btn-sm btn-outline-secondary me-1" title="Edit">          <div v-if="searchResults.length === 0" class="text-center py-4 text-muted">          <div v-if="searchResults.length === 0" class="text-center py-4 text-muted">

                        :class="`bi ${sortOrder === 'asc' ? 'bi-arrow-up' : 'bi-arrow-down'}`"

                      ></i>                      <i class="bi bi-pencil"></i>

                    </button>

                  </th>                    </button>            <i class="bi bi-inbox display-4 mb-3"></i>            <i class="bi bi-inbox display-4 mb-3"></i>

                  <th width="120">Health Score</th>

                  <th width="100">Members</th>                    <button class="btn btn-sm btn-outline-danger" title="Delete">

                  <th class="text-center">Actions</th>

                </tr>                      <i class="bi bi-trash"></i>            <p>No chapters found</p>            <p>No chapters found</p>

              </thead>

              <tbody>                    </button>

                <tr v-for="chapter in searchResults" :key="chapter.id">

                  <td>                  </td>          </div>          </div>

                    <div class="fw-bold text-primary">{{ chapter.name }}</div>

                    <small class="text-muted">{{ chapter.description || 'No description' }}</small>                </tr>

                  </td>

                  <td>{{ chapter.university || chapter.universityName }}</td>              </tbody>                    

                  <td>{{ chapter.city }}, {{ chapter.state }}</td>

                  <td>            </table>

                    <div class="d-flex align-items-center">

                      <div class="progress flex-grow-1 me-2" style="height: 8px;">          </div>          <!-- Simple Table View -->          <!-- Simple Table View -->

                        <div 

                          class="progress-bar"        </div>

                          :class="getHealthScoreClass(chapter.healthScore || 85)"

                          :style="{ width: (chapter.healthScore || 85) + '%' }"      </div>          <div v-else class="table-responsive">          <div v-else class="table-responsive">

                        ></div>

                      </div>

                      <small class="fw-bold">{{ chapter.healthScore || 85 }}%</small>

                    </div>      <!-- Action Buttons -->            <table class="table table-hover mb-0">            <table class="table table-hover mb-0">

                  </td>

                  <td>      <div class="row mt-4">

                    <span class="badge bg-primary rounded-pill">

                      {{ getChapterMemberCount(chapter.id) }}        <div class="col">              <thead class="table-light">              <thead class="table-light">

                    </span>

                  </td>          <button class="btn btn-primary">

                  <td class="text-center">

                    <div class="btn-group btn-group-sm">            <i class="bi bi-plus-circle me-2"></i>                <tr>                <tr>

                      <button 

                        class="btn btn-outline-primary"            Add New Chapter

                        @click="viewChapter(chapter)"

                        title="View Details"          </button>                  <th>Chapter Name</th>                  <th>Chapter Name</th>

                      >

                        <i class="bi bi-eye"></i>          <button class="btn btn-outline-success ms-2" @click="loadData">

                      </button>

                      <button             <i class="bi bi-arrow-clockwise me-2"></i>                  <th>University</th>                  <th>University</th>

                        class="btn btn-outline-secondary"

                        @click="editChapter(chapter)"            Refresh Data

                        title="Edit Chapter"

                      >          </button>                  <th>Location</th>                  <th>Location</th>

                        <i class="bi bi-pencil"></i>

                      </button>        </div>

                      <button 

                        class="btn btn-outline-danger"      </div>                  <th>Actions</th>                  <th>Actions</th>

                        @click="confirmDelete(chapter)"

                        title="Delete Chapter"    </div>

                      >

                        <i class="bi bi-trash"></i>  </div>                </tr>                </tr>

                      </button>

                    </div></template>

                  </td>

                </tr>              </thead>              </thead>

              </tbody>

            </table><script>

          </div>

import { chapterService } from '../services/chapterService'              <tbody>              <tbody>

          <!-- Card View -->

          <div v-else class="row g-3 p-3">import { memberService } from '../services/memberService'

            <div 

              v-for="chapter in searchResults" import EnhancedSearch from '../components/EnhancedSearch.vue'                <tr v-for="chapter in searchResults" :key="chapter.id">                <tr v-for="chapter in searchResults" :key="chapter.id">

              :key="chapter.id"

              class="col-md-6 col-lg-4"

            >

              <div class="card h-100 chapter-card">export default {                  <td>                  <td>

                <div class="card-header bg-primary text-white">

                  <h6 class="card-title mb-0 text-truncate">{{ chapter.name }}</h6>  name: 'Chapters',

                </div>

                <div class="card-body">  components: {                    <div class="fw-bold text-primary">{{ chapter.name }}</div>                    <div class="fw-bold text-primary">{{ chapter.name }}</div>

                  <div class="mb-3">

                    <small class="text-muted d-block">University</small>    EnhancedSearch

                    <div class="fw-medium">{{ chapter.university || chapter.universityName }}</div>

                  </div>  },                    <small class="text-muted">{{ chapter.description || 'No description' }}</small>                    <small class="text-muted">{{ chapter.description || 'No description' }}</small>

                  

                  <div class="mb-3">  data() {

                    <small class="text-muted d-block">Location</small>

                    <div>    return {                  </td>                  </td>

                      <i class="bi bi-geo-alt text-muted me-1"></i>

                      {{ chapter.city }}, {{ chapter.state }}      loading: false, // Set to false for debugging

                    </div>

                  </div>      chapters: [                  <td>{{ chapter.university || chapter.universityName }}</td>                  <td>{{ chapter.university || chapter.universityName }}</td>



                  <div class="mb-3">        {

                    <small class="text-muted d-block">Health Score</small>

                    <div class="d-flex align-items-center">          id: 1,                  <td>{{ chapter.city }}, {{ chapter.state }}</td>                  <td>{{ chapter.city }}, {{ chapter.state }}</td>

                      <div class="progress flex-grow-1 me-2" style="height: 8px;">

                        <div           name: "University of California - Berkeley",

                          class="progress-bar"

                          :class="getHealthScoreClass(chapter.healthScore || 85)"          university: "UC Berkeley",                  <td>                  <td>

                          :style="{ width: (chapter.healthScore || 85) + '%' }"

                        ></div>          state: "California",

                      </div>

                      <small class="fw-bold">{{ chapter.healthScore || 85 }}%</small>          city: "Berkeley",                    <button class="btn btn-sm btn-outline-primary me-1" title="View">                    <button class="btn btn-sm btn-outline-primary me-1" title="View">

                    </div>

                  </div>          description: "Leading chapter in Northern California"



                  <div class="mb-3">        },                      <i class="bi bi-eye"></i>                      <i class="bi bi-eye"></i>

                    <small class="text-muted d-block">Members</small>

                    <span class="badge bg-primary rounded-pill">        {

                      {{ getChapterMemberCount(chapter.id) }}

                    </span>          id: 2,                    </button>                    </button>

                  </div>

          name: "Stanford University",

                  <div v-if="chapter.description" class="mb-3">

                    <small class="text-muted d-block">Description</small>          university: "Stanford University",                     <button class="btn btn-sm btn-outline-secondary me-1" title="Edit">                    <button class="btn btn-sm btn-outline-secondary me-1" title="Edit">

                    <p class="small text-muted mb-0">{{ chapter.description }}</p>

                  </div>          state: "California",

                </div>

              </div>          city: "Stanford",                      <i class="bi bi-pencil"></i>                      <i class="bi bi-pencil"></i>

            </div>

          </div>          description: "Premier Silicon Valley chapter"

        </div>

      </div>        },                    </button>                    </button>



      <!-- Action Buttons -->        {

      <div class="row mt-4">

        <div class="col">          id: 3,                    <button class="btn btn-sm btn-outline-danger" title="Delete">                    <button class="btn btn-sm btn-outline-danger" title="Delete">

          <button class="btn btn-primary" @click="showCreateModal = true">

            <i class="bi bi-plus-circle me-2"></i>          name: "University of Texas at Austin",

            Add New Chapter

          </button>          university: "UT Austin",                      <i class="bi bi-trash"></i>                      <i class="bi bi-trash"></i>

          <button class="btn btn-outline-success ms-2" @click="loadData">

            <i class="bi bi-arrow-clockwise me-2"></i>          state: "Texas",

            Refresh Data

          </button>          city: "Austin",                    </button>                    </button>

          <button class="btn btn-outline-primary ms-2" @click="exportChapters">

            <i class="bi bi-download me-2"></i>          description: "Largest chapter in Texas"

            Export Data

          </button>        }                  </td>                  </td>

        </div>

      </div>      ],

    </div>

  </div>      searchResults: [],                </tr>                </tr>

</template>

      availableStates: ['California', 'Texas', 'New York', 'Florida'],

<script>

import { chapterService } from '../services/chapterService'      availableCities: ['Berkeley', 'Stanford', 'Austin', 'Boston', 'Miami'],              </tbody>              </tbody>

import { memberService } from '../services/memberService'

import EnhancedSearch from '../components/EnhancedSearch.vue'      currentView: 'table'

import ChapterAnalytics from '../components/ChapterAnalytics.vue'

    }            </table>            </table>

export default {

  name: 'Chapters',  },

  components: {

    EnhancedSearch,  async mounted() {          </div>          </div>

    ChapterAnalytics

  },    console.log('🔍 CHAPTERS DEBUG: Component mounted with EnhancedSearch')

  data() {

    return {    this.searchResults = [...this.chapters]        </div>        </div>

      loading: false,

      chapters: [    // Uncomment to test real data loading

        {

          id: 1,    // await this.loadData()      </div>      </div>

          name: "University of California - Berkeley",

          university: "UC Berkeley",  },

          state: "California",

          city: "Berkeley",  methods: {

          description: "Leading chapter in Northern California",

          healthScore: 92    async loadData() {

        },

        {      try {      <!-- Action Buttons -->      <!-- Action Buttons -->

          id: 2,

          name: "Stanford University",        console.log('🔍 CHAPTERS DEBUG: Starting loadData()')

          university: "Stanford University", 

          state: "California",        this.loading = true      <div class="row mt-4">      <div class="row mt-4">

          city: "Stanford",

          description: "Premier Silicon Valley chapter",        

          healthScore: 88

        },        const [chaptersData, membersData] = await Promise.all([        <div class="col">        <div class="col">

        {

          id: 3,          chapterService.getAllChapters(),

          name: "University of Texas at Austin",

          university: "UT Austin",          memberService.getAllMembers()          <button class="btn btn-primary">          <button class="btn btn-primary">

          state: "Texas",

          city: "Austin",        ])

          description: "Largest chapter in Texas",

          healthScore: 76                    <i class="bi bi-plus-circle me-2"></i>            <i class="bi bi-plus-circle me-2"></i>

        },

        {        console.log('🔍 CHAPTERS DEBUG: Loaded chapters:', chaptersData)

          id: 4,

          name: "Harvard University",                    Add New Chapter            Add New Chapter

          university: "Harvard University",

          state: "Massachusetts",         this.chapters = chaptersData

          city: "Cambridge",

          description: "Prestigious East Coast chapter",        this.searchResults = chaptersData          </button>          </button>

          healthScore: 95

        },        

        {

          id: 5,        // Extract unique states and cities for filters          <button class="btn btn-outline-success ms-2" @click="loadData">          <button class="btn btn-outline-success ms-2" @click="loadData">

          name: "University of Miami",

          university: "University of Miami",        this.availableStates = [...new Set(chaptersData.map(chapter => chapter.state))].filter(Boolean).sort()

          state: "Florida",

          city: "Miami",         this.availableCities = [...new Set(chaptersData.map(chapter => chapter.city))].filter(Boolean).sort()            <i class="bi bi-arrow-clockwise me-2"></i>            <i class="bi bi-arrow-clockwise me-2"></i>

          description: "Dynamic Southern chapter",

          healthScore: 82        

        }

      ],        console.log('🔍 CHAPTERS DEBUG: Updated searchResults:', this.searchResults)            Refresh Data            Refresh Data

      members: [

        { id: 1, chapterId: 1, name: "John Doe" },        

        { id: 2, chapterId: 1, name: "Jane Smith" },

        { id: 3, chapterId: 2, name: "Bob Johnson" },      } catch (error) {          </button>          </button>

        { id: 4, chapterId: 3, name: "Alice Brown" },

        { id: 5, chapterId: 3, name: "Charlie Wilson" },        console.error('🔍 CHAPTERS DEBUG: Error loading chapters:', error)

        { id: 6, chapterId: 3, name: "Diana Davis" }

      ],      } finally {        </div>        </div>

      searchResults: [],

      availableStates: ['California', 'Texas', 'Massachusetts', 'Florida'],        this.loading = false

      availableCities: ['Berkeley', 'Stanford', 'Austin', 'Cambridge', 'Miami'],

      currentView: 'table',        console.log('🔍 CHAPTERS DEBUG: Loading complete')      </div>      </div>

      sortBy: 'name',

      sortOrder: 'asc',      }

      showAnalytics: false,

      showCreateModal: false    },    </div>    </div>

    }

  },

  async mounted() {

    console.log('🔍 CHAPTERS DEBUG: Enhanced component mounted')    handleSearch(searchData) {  </div>  </div>

    this.searchResults = [...this.chapters]

    this.applySorting()      console.log('🔍 SEARCH DEBUG: Received search:', searchData)

    // Uncomment to test real data loading

    // await this.loadData()      // Filter chapters based on search criteria</template></template>

  },

  methods: {      this.searchResults = this.chapters.filter(chapter => {

    async loadData() {

      try {        const matchesQuery = !searchData.query || 

        console.log('🔍 CHAPTERS DEBUG: Starting loadData()')

        this.loading = true          chapter.name.toLowerCase().includes(searchData.query.toLowerCase()) ||

        

        const [chaptersData, membersData] = await Promise.all([          chapter.university.toLowerCase().includes(searchData.query.toLowerCase())<script>      <!-- Enhanced Actions Bar -->

          chapterService.getAllChapters(),

          memberService.getAllMembers()        

        ])

                const matchesState = !searchData.state || chapter.state === searchData.stateimport { chapterService } from '../services/chapterService'      <div class="row mb-4">

        console.log('🔍 CHAPTERS DEBUG: Loaded chapters:', chaptersData)

                

        this.chapters = chaptersData

        this.members = membersData        return matchesQuery && matchesStateimport { memberService } from '../services/memberService'        <div class="col-lg-3 col-md-6 mb-3">

        this.searchResults = chaptersData

              })

        // Extract unique states and cities for filters

        this.availableStates = [...new Set(chaptersData.map(chapter => chapter.state))].filter(Boolean).sort()      console.log('🔍 SEARCH DEBUG: Filtered results:', this.searchResults.length)          <div class="d-flex gap-2">

        this.availableCities = [...new Set(chaptersData.map(chapter => chapter.city))].filter(Boolean).sort()

            },

        this.applySorting()

        export default {            <button 

      } catch (error) {

        console.error('🔍 CHAPTERS DEBUG: Error loading chapters:', error)    handleAdvancedSearch(filters) {

      } finally {

        this.loading = false      console.log('🔍 ADVANCED SEARCH DEBUG: Received filters:', filters)  name: 'Chapters',              class="btn btn-primary flex-fill" 

      }

    },      // Apply advanced filters



    handleSearch(searchData) {      this.searchResults = this.chapters.filter(chapter => {  data() {              @click="showCreateModal = true"

      console.log('🔍 SEARCH DEBUG: Received search:', searchData)

      this.searchResults = this.chapters.filter(chapter => {        const matchesUniversity = !filters.university || 

        const matchesQuery = !searchData.query || 

          chapter.name.toLowerCase().includes(searchData.query.toLowerCase()) ||          chapter.university.toLowerCase().includes(filters.university.toLowerCase())    return {            >

          chapter.university.toLowerCase().includes(searchData.query.toLowerCase())

                

        const matchesState = !searchData.state || chapter.state === searchData.state

                const matchesCity = !filters.city || chapter.city === filters.city      loading: false, // Set to false for debugging              <i class="bi bi-plus-circle me-2"></i>

        return matchesQuery && matchesState

      })        

      this.applySorting()

    },        return matchesUniversity && matchesCity      chapters: [              Add New Chapter



    handleAdvancedSearch(filters) {      })

      console.log('🔍 ADVANCED SEARCH DEBUG: Received filters:', filters)

      this.searchResults = this.chapters.filter(chapter => {      console.log('🔍 ADVANCED SEARCH DEBUG: Filtered results:', this.searchResults.length)        {            </button>

        const matchesUniversity = !filters.university || 

          chapter.university.toLowerCase().includes(filters.university.toLowerCase())    }

        

        const matchesCity = !filters.city || chapter.city === filters.city  }          id: 1,            <button 

        

        return matchesUniversity && matchesCity}

      })

      this.applySorting()</script>          name: "University of California - Berkeley",              class="btn btn-outline-primary" 

    },



    sortChapters(field) {

      if (this.sortBy === field) {<style scoped>          university: "UC Berkeley",              @click="toggleComparisonMode"

        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'

      } else {.chapters {

        this.sortBy = field

        this.sortOrder = 'asc'  min-height: 100vh;          state: "California",              :class="{ 'active': comparisonMode }"

      }

      this.applySorting()  padding: 20px 0;

    },

}          city: "Berkeley",              title="Compare Chapters"

    applySorting() {

      this.searchResults.sort((a, b) => {

        let valueA = a[this.sortBy] || ''

        let valueB = b[this.sortBy] || ''.card {          description: "Leading chapter in Northern California"            >



        if (typeof valueA === 'string') valueA = valueA.toLowerCase()  box-shadow: 0 2px 4px rgba(0,0,0,0.1);

        if (typeof valueB === 'string') valueB = valueB.toLowerCase()

}        },              <i class="bi bi-arrow-left-right"></i>

        if (this.sortOrder === 'asc') {

          return valueA < valueB ? -1 : valueA > valueB ? 1 : 0

        } else {

          return valueA > valueB ? -1 : valueA < valueB ? 1 : 0.table th {        {            </button>

        }

      })  border-top: none;

    },

  font-weight: 600;          id: 2,          </div>

    toggleCurrentView() {

      this.currentView = this.currentView === 'table' ? 'card' : 'table'}

    },

          name: "Stanford University",        </div>

    getHealthScoreClass(score) {

      if (score >= 80) return 'bg-success'.btn-group .btn {

      if (score >= 60) return 'bg-warning'

      return 'bg-danger'  border-radius: 0.25rem;          university: "Stanford University",         <div class="col-lg-9 col-md-12 mb-3">

    },

}

    getChapterMemberCount(chapterId) {

      return this.members.filter(member => member.chapterId === chapterId).length          state: "California",          <EnhancedSearch

    },

.alert-info {

    exportChapters() {

      const headers = ['Name', 'University', 'City', 'State', 'Health Score', 'Members', 'Description']  background-color: #e3f2fd;          city: "Stanford",            :chapters="chapters"

      let csvContent = headers.join(',') + '\n'

  border-color: #2196f3;

      this.searchResults.forEach(chapter => {

        const row = [  color: #1976d2;          description: "Premier Silicon Valley chapter"            :available-states="availableStates"

          `"${chapter.name || ''}"`,

          `"${chapter.university || chapter.universityName || ''}"`,}

          `"${chapter.city || ''}"`,

          `"${chapter.state || ''}"`,</style>        }            @search="handleEnhancedSearch"

          `"${chapter.healthScore || 85}"`,

          `"${this.getChapterMemberCount(chapter.id)}"`,      ],            @filter-change="handleFilterChange"

          `"${chapter.description || ''}"`

        ]      searchResults: [          />

        csvContent += row.join(',') + '\n'

      })        {        </div>



      const blob = new Blob([csvContent], { type: 'text/csv' })          id: 1,      </div>

      const url = window.URL.createObjectURL(blob)

      const link = document.createElement('a')          name: "University of California - Berkeley",

      link.href = url

      link.download = `chapters-export-${new Date().toISOString().split('T')[0]}.csv`          university: "UC Berkeley",      <!-- View Controls -->

      document.body.appendChild(link)

      link.click()          state: "California",      <div class="row mb-3">

      document.body.removeChild(link)

      window.URL.revokeObjectURL(url)          city: "Berkeley",        <div class="col-12">

    },

          description: "Leading chapter in Northern California"          <div class="d-flex justify-content-between align-items-center">

    // Placeholder methods for CRUD operations

    viewChapter(chapter) {        },            <div class="d-flex gap-2">

      console.log('View chapter:', chapter.name)

      // this.$router.push(`/chapters/${chapter.id}`)        {              <select 

    },

          id: 2,                class="form-select"

    editChapter(chapter) {

      console.log('Edit chapter:', chapter.name)          name: "Stanford University",                style="width: auto;"

      // Implementation coming next

    },          university: "Stanford University",                v-model="sortCriteria"



    confirmDelete(chapter) {          state: "California",                 @change="applySorting"

      console.log('Delete chapter:', chapter.name)

      // Implementation coming next          city: "Stanford",              >

    }

  }          description: "Premier Silicon Valley chapter"                <option value="name-asc">Name (A-Z)</option>

}

</script>        }                <option value="name-desc">Name (Z-A)</option>



<style scoped>      ],                <option value="members-desc">Most Members</option>

.chapters {

  min-height: 100vh;      currentView: 'table'                <option value="members-asc">Least Members</option>

  padding: 20px 0;

}    }                <option value="health-desc">Healthiest</option>



.card {  },                <option value="health-asc">Needs Attention</option>

  box-shadow: 0 2px 4px rgba(0,0,0,0.1);

  border: none;  async mounted() {                <option value="founded-desc">Newest</option>

  border-radius: 10px;

}    console.log('🔍 CHAPTERS DEBUG: Simplified component mounted()')                <option value="founded-asc">Oldest</option>



.table th {    console.log('🔍 CHAPTERS DEBUG: searchResults:', this.searchResults)              </select>

  border-top: none;

  font-weight: 600;    // Uncomment to test real data loading              <button 

}

    // await this.loadData()                class="btn btn-outline-secondary"

.btn-group .btn {

  border-radius: 0.25rem;  },                @click="toggleCurrentView"

}

  methods: {                :title="currentView === 'table' ? 'Switch to Card View' : 'Switch to Table View'"

.alert-info {

  background-color: #e3f2fd;    async loadData() {              >

  border-color: #2196f3;

  color: #1976d2;      try {                <i :class="currentView === 'table' ? 'bi bi-grid-3x3-gap' : 'bi bi-table'"></i>

}

        console.log('🔍 CHAPTERS DEBUG: Starting loadData()')              </button>

.chapter-card {

  transition: all 0.3s ease;        this.loading = true            </div>

  border: none;

  border-radius: 12px;                    <div class="text-muted">

  overflow: hidden;

}        const [chaptersData, membersData] = await Promise.all([              {{ searchResults.length }} of {{ chapters.length }} chapters



.chapter-card:hover {          chapterService.getAllChapters(),            </div>

  transform: translateY(-5px);

  box-shadow: 0 15px 40px rgba(0,0,0,0.15);          memberService.getAllMembers()          </div>

}

        ])        </div>

.progress {

  background-color: rgba(0,0,0,0.1);              </div>

}

        console.log('🔍 CHAPTERS DEBUG: Loaded chapters:', chaptersData)

.table-hover tbody tr:hover {

  background-color: rgba(102, 126, 234, 0.05);              <!-- Enhanced Advanced Search Card -->

}

</style>        this.chapters = chaptersData      <div class="row mb-4" v-if="showAdvancedSearch">

        this.searchResults = chaptersData        <div class="col">

                  <div class="card border-primary">

        console.log('🔍 CHAPTERS DEBUG: Updated searchResults:', this.searchResults)            <div class="card-header bg-primary text-white">

                      <div class="d-flex justify-content-between align-items-center">

      } catch (error) {                <h6 class="card-title mb-0">

        console.error('🔍 CHAPTERS DEBUG: Error loading chapters:', error)                  <i class="bi bi-funnel me-2"></i>

      } finally {                  Advanced Search & Filters

        this.loading = false                </h6>

        console.log('🔍 CHAPTERS DEBUG: Loading complete')                <button 

      }                  class="btn btn-sm btn-outline-light"

    }                  @click="saveCurrentSearch"

  }                  :disabled="!hasActiveFilters"

}                >

</script>                  <i class="bi bi-bookmark-plus me-1"></i>

                  Save Search

<style scoped>                </button>

.chapters {              </div>

  min-height: 100vh;            </div>

  padding: 20px 0;            <div class="card-body">

}              <!-- Basic Filters Row -->

              <div class="row mb-3">

.card {                <div class="col-md-3">

  box-shadow: 0 2px 4px rgba(0,0,0,0.1);                  <label class="form-label fw-semibold">University Name</label>

}                  <input

                    type="text"

.table th {                    class="form-control"

  border-top: none;                    placeholder="Search universities..."

  font-weight: 600;                    v-model="advancedFilters.university"

}                    @input="performSearch"

                    list="universitySuggestions"

.btn-group .btn {                  >

  border-radius: 0.25rem;                  <datalist id="universitySuggestions">

}                    <option v-for="uni in uniqueUniversities" :key="uni" :value="uni"></option>

                  </datalist>

.alert-info {                </div>

  background-color: #e3f2fd;                <div class="col-md-3">

  border-color: #2196f3;                  <label class="form-label fw-semibold">City</label>

  color: #1976d2;                  <select 

}                    class="form-select" 

</style>                    v-model="advancedFilters.city"
                    @change="performSearch"
                  >
                    <option value="">All Cities</option>
                    <option v-for="city in availableCities" :key="city" :value="city">
                      {{ city }}
                    </option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Status</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.active"
                    @change="performSearch"
                  >
                    <option value="">All Status</option>
                    <option :value="true">Active</option>
                    <option :value="false">Inactive</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Health Score</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.healthScore"
                    @change="performSearch"
                  >
                    <option value="">All Health Levels</option>
                    <option value="excellent">Excellent (90-100%)</option>
                    <option value="good">Good (70-89%)</option>
                    <option value="average">Average (50-69%)</option>
                    <option value="needs-attention">Needs Attention (<50%)</option>
                  </select>
                </div>
              </div>

              <!-- Advanced Filters Row -->
              <div class="row mb-3">
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Member Count</label>
                  <div class="d-flex gap-2">
                    <input
                      type="number"
                      class="form-control form-control-sm"
                      placeholder="Min"
                      v-model.number="advancedFilters.memberCountMin"
                      @input="performSearch"
                      min="0"
                    >
                    <input
                      type="number"
                      class="form-control form-control-sm"
                      placeholder="Max"
                      v-model.number="advancedFilters.memberCountMax"
                      @input="performSearch"
                      min="0"
                    >
                  </div>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Founded Date</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.foundedPeriod"
                    @change="performSearch"
                  >
                    <option value="">Any Time</option>
                    <option value="last-year">Last Year</option>
                    <option value="last-2-years">Last 2 Years</option>
                    <option value="last-5-years">Last 5 Years</option>
                    <option value="over-5-years">Over 5 Years Ago</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Activity Level</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.activityLevel"
                    @change="performSearch"
                  >
                    <option value="">All Activity Levels</option>
                    <option value="high">High Activity</option>
                    <option value="medium">Medium Activity</option>
                    <option value="low">Low Activity</option>
                    <option value="inactive">Inactive</option>
                  </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                  <button 
                    class="btn btn-outline-danger w-100"
                    @click="clearFilters"
                    :disabled="!hasActiveFilters"
                  >
                    <i class="bi bi-x-circle me-1"></i>
                    Clear All Filters
                  </button>
                </div>
              </div>

              <!-- Active Filters Display -->
              <div v-if="hasActiveFilters" class="border-top pt-3">
                <div class="d-flex flex-wrap gap-2 align-items-center">
                  <small class="text-muted fw-semibold">Active Filters:</small>
                  <span v-if="searchTerm" class="badge bg-primary">
                    Search: "{{ searchTerm }}"
                    <button class="btn-close btn-close-white ms-1" @click="searchTerm = ''; performSearch()"></button>
                  </span>
                  <span v-if="selectedState" class="badge bg-info">
                    State: {{ selectedState }}
                    <button class="btn-close btn-close-white ms-1" @click="selectedState = ''; performSearch()"></button>
                  </span>
                  <span v-if="advancedFilters.university" class="badge bg-success">
                    University: {{ advancedFilters.university }}
                    <button class="btn-close btn-close-white ms-1" @click="advancedFilters.university = ''; performSearch()"></button>
                  </span>
                  <span v-if="advancedFilters.healthScore" class="badge bg-warning">
                    Health: {{ getHealthScoreLabel(advancedFilters.healthScore) }}
                    <button class="btn-close btn-close-white ms-1" @click="advancedFilters.healthScore = ''; performSearch()"></button>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Saved Searches Panel -->
      <div class="row mb-3" v-if="showSavedSearches">
        <div class="col">
          <div class="card border-secondary">
            <div class="card-header">
              <h6 class="card-title mb-0">
                <i class="bi bi-bookmarks me-2"></i>
                Saved Searches
              </h6>
            </div>
            <div class="card-body">
              <div v-if="savedSearches.length === 0" class="text-muted text-center py-2">
                No saved searches yet. Apply filters and click "Save Search" to create one.
              </div>
              <div v-else class="d-flex flex-wrap gap-2">
                <div 
                  v-for="search in savedSearches" 
                  :key="search.id"
                  class="saved-search-item"
                >
                  <button 
                    class="btn btn-sm btn-outline-secondary flex-grow-1 text-start"
                    @click="loadSavedSearch(search)"
                  >
                    {{ search.name }}
                  </button>
                  <button 
                    class="btn btn-sm btn-outline-danger ms-1" 
                    @click="deleteSavedSearch(search.id)"
                    title="Delete search"
                  >
                    <i class="bi bi-trash3"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Comparison Mode Banner -->
      <div v-if="comparisonMode" class="row mb-3">
        <div class="col">
          <div class="alert alert-info d-flex justify-content-between align-items-center">
            <div>
              <i class="bi bi-info-circle me-2"></i>
              <strong>Comparison Mode:</strong> Select chapters to compare ({{ selectedChapters.length }}/3 selected)
            </div>
            <div class="d-flex gap-2">
              <button 
                class="btn btn-success btn-sm"
                @click="compareSelectedChapters"
                :disabled="selectedChapters.length < 2"
              >
                <i class="bi bi-bar-chart me-1"></i>
                Compare ({{ selectedChapters.length }})
              </button>
              <button 
                class="btn btn-outline-secondary btn-sm"
                @click="exitComparisonMode"
              >
                Exit Comparison
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Results Summary -->
      <div class="row mb-3">
        <div class="col">
          <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-3">
              <span class="text-muted">
                Showing <strong>{{ searchResults.length }}</strong> of <strong>{{ chapters.length }}</strong> chapters
              </span>
              <div class="d-flex gap-2">
                <span v-if="healthStats.excellent > 0" class="badge bg-success" title="Excellent Health">
                  {{ healthStats.excellent }} Excellent
                </span>
                <span v-if="healthStats.good > 0" class="badge bg-primary" title="Good Health">
                  {{ healthStats.good }} Good
                </span>
                <span v-if="healthStats.needsAttention > 0" class="badge bg-warning" title="Needs Attention">
                  {{ healthStats.needsAttention }} Needs Attention
                </span>
              </div>
            </div>
            <div class="d-flex gap-2">
              <button 
                class="btn btn-sm btn-outline-secondary"
                @click="showAdvancedSearch = !showAdvancedSearch"
              >
                <i class="bi bi-funnel me-1"></i>
                {{ showAdvancedSearch ? 'Hide' : 'Show' }} Filters
              </button>
              <button 
                class="btn btn-sm btn-outline-secondary"
                @click="exportResults"
                :disabled="searchResults.length === 0"
              >
                <i class="bi bi-download me-1"></i>
                Export
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Analytics Dashboard -->
      <div v-if="showAnalytics && !loading" class="mb-4">
        <ChapterAnalytics 
          :chapters="searchResults" 
          :is-dark-mode="isDarkMode"
        />
      </div>

      <!-- Analytics Toggle -->
      <div class="row mb-3">
        <div class="col-12">
          <button 
            class="btn btn-outline-info btn-sm"
            @click="toggleAnalytics"
            :class="{ 'active': showAnalytics }"
          >
            <i :class="showAnalytics ? 'bi bi-eye-slash' : 'bi bi-bar-chart'"></i>
            {{ showAnalytics ? 'Hide Analytics' : 'Show Analytics' }}
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Chapters Table -->
      <div v-else class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-table me-2"></i>
            Chapters ({{ searchResults.length }})
          </h5>
        </div>
        <div class="card-body p-0">
          <div v-if="searchResults.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-inbox display-4 mb-3"></i>
            <p>No chapters found matching your criteria</p>
            <button 
              v-if="hasActiveFilters" 
              class="btn btn-outline-primary btn-sm"
              @click="clearFilters"
            >
              Clear Filters
            </button>
          </div>
          <!-- Table View -->
          <div v-else-if="currentView === 'table'" class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light sticky-top">
                <tr>
                  <th v-if="comparisonMode" width="40">
                    <input 
                      type="checkbox" 
                      class="form-check-input"
                      @change="toggleAllSelection"
                      :checked="allSelected"
                    >
                  </th>
                  <th class="sortable" @click="setSortCriteria('name')">
                    Chapter Name
                    <i class="bi" :class="getSortIcon('name')"></i>
                  </th>
                  <th class="sortable" @click="setSortCriteria('university')">
                    University
                    <i class="bi" :class="getSortIcon('university')"></i>
                  </th>
                  <th>Location</th>
                  <th class="text-center">Health Score</th>
                  <th class="sortable text-center" @click="setSortCriteria('members')">
                    Members
                    <i class="bi" :class="getSortIcon('members')"></i>
                  </th>
                  <th class="sortable text-center" @click="setSortCriteria('founded')">
                    Founded
                    <i class="bi" :class="getSortIcon('founded')"></i>
                  </th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="chapter in searchResults" 
                  :key="chapter.id"
                  :class="{ 'table-active': selectedChapters.includes(chapter.id) }"
                >
                  <td v-if="comparisonMode">
                    <input 
                      type="checkbox" 
                      class="form-check-input"
                      :checked="selectedChapters.includes(chapter.id)"
                      @change="toggleChapterSelection(chapter.id)"
                      :disabled="!selectedChapters.includes(chapter.id) && selectedChapters.length >= 3"
                    >
                  </td>
                  <td>
                    <div class="d-flex align-items-center">
                      <div>
                        <strong>{{ chapter.name }}</strong>
                        <small v-if="chapter.description" class="text-muted d-block">
                          {{ truncateText(chapter.description, 50) }}
                        </small>
                      </div>
                    </div>
                  </td>
                  <td>{{ chapter.universityName }}</td>
                  <td>
                    <div>
                      <i class="bi bi-geo-alt text-muted me-1"></i>
                      {{ chapter.city }}, {{ chapter.state }}
                    </div>
                  </td>
                  <td class="text-center">
                    <div class="d-flex flex-column align-items-center">
                      <span 
                        class="badge"
                        :class="getHealthScoreBadgeClass(getChapterHealthScore(chapter))"
                      >
                        {{ getChapterHealthScore(chapter) }}%
                      </span>
                      <small class="text-muted">
                        {{ getHealthScoreStatus(getChapterHealthScore(chapter)) }}
                      </small>
                    </div>
                  </td>
                  <td class="text-center">
                    <span 
                      class="badge"
                      :class="getMemberCountBadgeClass(memberCounts[chapter.id] || 0)"
                    >
                      {{ memberCounts[chapter.id] || 0 }}
                    </span>
                  </td>
                  <td class="text-center">
                    <small class="text-muted">
                      {{ formatDate(chapter.foundedDate) }}
                    </small>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <router-link 
                        :to="`/chapters/${chapter.id}`" 
                        class="btn btn-sm btn-outline-primary"
                        title="View Details"
                      >
                        <i class="bi bi-eye"></i>
                      </router-link>
                      <a 
                        :href="getCampusLabsUrl(chapter)" 
                        target="_blank"
                        rel="noopener noreferrer"
                        class="btn btn-sm btn-outline-success"
                        :title="getButtonTooltip(chapter)"
                        :aria-label="`${getButtonText(chapter)} for ${chapter.universityName} (opens in new tab)`"
                        v-if="getCampusLabsUrl(chapter)"
                        @click="trackLinkClick(chapter)"
                      >
                        <i class="bi bi-link-45deg me-1"></i>
                        <small>{{ getButtonText(chapter) }}</small>
                      </a>
                      <button 
                        class="btn btn-sm btn-outline-secondary"
                        @click="editChapter(chapter)"
                        title="Edit Chapter"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(chapter)"
                        title="Delete Chapter"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Card View -->
          <div v-else class="row g-4">
            <div 
              v-for="chapter in searchResults" 
              :key="chapter.id" 
              class="col-lg-4 col-md-6"
            >
              <div 
                class="card h-100 chapter-card"
                :class="{ 
                  'border-primary': selectedChapters.includes(chapter.id),
                  'shadow-sm': !selectedChapters.includes(chapter.id)
                }"
                @click="comparisonMode ? toggleChapterSelection(chapter.id) : null"
                :style="{ cursor: comparisonMode ? 'pointer' : 'default' }"
              >
                <!-- Card Header with Health Score -->
                <div class="card-header d-flex justify-content-between align-items-center">
                  <div class="d-flex align-items-center">
                    <input 
                      v-if="comparisonMode"
                      type="checkbox" 
                      class="form-check-input me-2"
                      :checked="selectedChapters.includes(chapter.id)"
                      :disabled="!selectedChapters.includes(chapter.id) && selectedChapters.length >= 3"
                    >
                    <h6 class="card-title mb-0">{{ chapter.name }}</h6>
                  </div>
                  <span 
                    class="badge"
                    :class="getHealthScoreBadgeClass(getChapterHealthScore(chapter))"
                  >
                    {{ getChapterHealthScore(chapter) }}%
                  </span>
                </div>

                <!-- Card Body -->
                <div class="card-body">
                  <!-- University Info -->
                  <div class="mb-3">
                    <h6 class="text-primary mb-1">{{ chapter.universityName }}</h6>
                    <p class="text-muted small mb-0">
                      <i class="bi bi-geo-alt me-1"></i>
                      {{ chapter.city }}, {{ chapter.state }}
                    </p>
                  </div>

                  <!-- Stats Row -->
                  <div class="row text-center mb-3">
                    <div class="col-4">
                      <div class="stat-item">
                        <span 
                          class="badge d-block"
                          :class="getMemberCountBadgeClass(memberCounts[chapter.id] || 0)"
                        >
                          {{ memberCounts[chapter.id] || 0 }}
                        </span>
                        <small class="text-muted">Members</small>
                      </div>
                    </div>
                    <div class="col-4">
                      <div class="stat-item">
                        <span class="badge bg-info d-block">
                          {{ getChapterEventCount(chapter.id) }}
                        </span>
                        <small class="text-muted">Events</small>
                      </div>
                    </div>
                    <div class="col-4">
                      <div class="stat-item">
                        <span class="badge bg-secondary d-block">
                          {{ getYearsSinceFounded(chapter.foundedDate) }}y
                        </span>
                        <small class="text-muted">Age</small>
                      </div>
                    </div>
                  </div>

                  <!-- Health Indicators -->
                  <div class="health-indicators mb-3">
                    <small class="text-muted d-block mb-1">Health Indicators:</small>
                    <div class="d-flex gap-1">
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('members', chapter)"
                        title="Member Count Health"
                      ></span>
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('activity', chapter)"
                        title="Activity Level Health"
                      ></span>
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('growth', chapter)"
                        title="Growth Health"
                      ></span>
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('engagement', chapter)"
                        title="Engagement Health"
                      ></span>
                    </div>
                  </div>

                  <!-- Description -->
                  <p v-if="chapter.description" class="text-muted small mb-3">
                    {{ truncateText(chapter.description, 100) }}
                  </p>
                </div>

                <!-- Card Footer -->
                <div class="card-footer bg-transparent">
                  <div class="d-flex justify-content-between align-items-center">
                    <small class="text-muted">
                      Founded {{ formatDate(chapter.foundedDate) }}
                    </small>
                    <div class="btn-group btn-group-sm" role="group">
                      <button 
                        class="btn btn-outline-primary"
                        @click.stop="viewChapterDetails(chapter)"
                        title="View Details"
                      >
                        <i class="bi bi-eye"></i>
                      </button>
                      <button 
                        class="btn btn-outline-secondary"
                        @click.stop="editChapter(chapter)"
                        title="Edit Chapter"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-outline-danger"
                        @click.stop="confirmDelete(chapter)"
                        title="Delete Chapter"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div 
      class="modal fade" 
      :class="{ show: showCreateModal || showEditModal }"
      :style="{ display: showCreateModal || showEditModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ showEditModal ? 'Edit Chapter' : 'Create New Chapter' }}
            </h5>
            <button 
              type="button" 
              class="btn-close" 
              @click="closeModal"
            ></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveChapter">
              <div class="mb-3">
                <label for="chapterName" class="form-label">Chapter Name *</label>
                <input
                  type="text"
                  class="form-control"
                  id="chapterName"
                  v-model="chapterForm.name"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="universityName" class="form-label">University Name *</label>
                <input
                  type="text"
                  class="form-control"
                  id="universityName"
                  v-model="chapterForm.universityName"
                  required
                >
              </div>
              <div class="row">
                <div class="col-md-8 mb-3">
                  <label for="city" class="form-label">City *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="city"
                    v-model="chapterForm.city"
                    required
                  >
                </div>
                <div class="col-md-4 mb-3">
                  <label for="state" class="form-label">State *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="state"
                    v-model="chapterForm.state"
                    required
                    maxlength="2"
                    placeholder="CA"
                  >
                </div>
              </div>
              <div class="mb-3">
                <label for="foundedDate" class="form-label">Founded Date *</label>
                <input
                  type="date"
                  class="form-control"
                  id="foundedDate"
                  v-model="chapterForm.foundedDate"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="chapterForm.description"
                  rows="3"
                ></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-primary" 
              @click="saveChapter"
              :disabled="saving"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ showEditModal ? 'Update Chapter' : 'Create Chapter' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div 
      class="modal fade" 
      :class="{ show: showDeleteModal }"
      :style="{ display: showDeleteModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" @click="showDeleteModal = false"></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete the chapter <strong>{{ chapterToDelete?.name }}</strong>?</p>
            <p class="text-danger">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteModal = false">
              Cancel
            </button>
            <button type="button" class="btn btn-danger" @click="deleteChapter">
              Delete Chapter
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Chapter Comparison Modal -->
    <div v-if="showComparisonModal" class="modal fade show d-block" tabindex="-1">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-bar-chart me-2"></i>
              Chapter Comparison
            </h5>
            <button type="button" class="btn-close" @click="closeComparisonModal"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div 
                v-for="chapter in comparisonChapters" 
                :key="chapter.id"
                class="col-md-4"
              >
                <div class="card h-100">
                  <div class="card-header text-center">
                    <h6 class="card-title mb-1">{{ chapter.name }}</h6>
                    <small class="text-muted">{{ chapter.universityName }}</small>
                  </div>
                  <div class="card-body">
                    <!-- Health Score -->
                    <div class="text-center mb-3">
                      <div 
                        class="health-score-circle mx-auto mb-2"
                        :style="{ 
                          '--score': getChapterHealthScore(chapter),
                          background: `conic-gradient(${getHealthScoreColor(getChapterHealthScore(chapter))} ${getChapterHealthScore(chapter)}%, #e9ecef 0%)` 
                        }"
                      >
                        <span class="h4 mb-0">{{ getChapterHealthScore(chapter) }}%</span>
                      </div>
                      <small class="text-muted">Overall Health Score</small>
                    </div>

                    <!-- Key Metrics -->
                    <div class="comparison-metrics">
                      <div class="metric-row">
                        <span class="metric-label">Members</span>
                        <span class="metric-value">{{ memberCounts[chapter.id] || 0 }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Events</span>
                        <span class="metric-value">{{ getChapterEventCount(chapter.id) }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Founded</span>
                        <span class="metric-value">{{ formatDate(chapter.foundedDate) }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Location</span>
                        <span class="metric-value">{{ chapter.city }}, {{ chapter.state }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Age</span>
                        <span class="metric-value">{{ getYearsSinceFounded(chapter.foundedDate) }} years</span>
                      </div>
                    </div>

                    <!-- Performance Indicators -->
                    <div class="mt-3">
                      <small class="text-muted d-block mb-2">Performance Indicators</small>
                      <div class="performance-bars">
                        <div class="performance-item">
                          <span class="small">Member Growth</span>
                          <div class="progress progress-sm">
                            <div 
                              class="progress-bar bg-success"
                              :style="{ width: getPerformanceMetric(chapter, 'growth') + '%' }"
                            ></div>
                          </div>
                        </div>
                        <div class="performance-item">
                          <span class="small">Activity Level</span>
                          <div class="progress progress-sm">
                            <div 
                              class="progress-bar bg-info"
                              :style="{ width: getPerformanceMetric(chapter, 'activity') + '%' }"
                            ></div>
                          </div>
                        </div>
                        <div class="performance-item">
                          <span class="small">Engagement</span>
                          <div class="progress progress-sm">
                            <div 
                              class="progress-bar bg-warning"
                              :style="{ width: getPerformanceMetric(chapter, 'engagement') + '%' }"
                            ></div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeComparisonModal">
              Close
            </button>
            <button type="button" class="btn btn-primary" @click="exportComparison">
              <i class="bi bi-download me-1"></i>
              Export Comparison
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Backdrop -->
    <div 
      v-if="showCreateModal || showEditModal || showDeleteModal || showComparisonModal"
      class="modal-backdrop fade show"
      @click="closeModal"
    ></div>
  </div>
</template>

<script>
import { chapterService } from '../services/chapterService'
import { memberService } from '../services/memberService'

export default {
  name: 'Chapters',
  data() {
    return {
      loading: false, // Set to false for debugging
      chapters: [
        {
          id: 1,
          name: "University of California - Berkeley",
          university: "UC Berkeley",
          state: "California",
          city: "Berkeley",
          description: "Leading chapter in Northern California"
        },
        {
          id: 2,
          name: "Stanford University",
          university: "Stanford University", 
          state: "California",
          city: "Stanford",
          description: "Premier Silicon Valley chapter"
        }
      ],
      searchResults: [
        {
          id: 1,
          name: "University of California - Berkeley",
          university: "UC Berkeley",
          state: "California",
          city: "Berkeley",
          description: "Leading chapter in Northern California"
        },
        {
          id: 2,
          name: "Stanford University",
          university: "Stanford University",
          state: "California", 
          city: "Stanford",
          description: "Premier Silicon Valley chapter"
        }
      ],
      currentView: 'table'
    }
  },
  async mounted() {
    console.log('🔍 CHAPTERS DEBUG: Simplified component mounted()')
    console.log('🔍 CHAPTERS DEBUG: searchResults:', this.searchResults)
    // Uncomment to test real data loading
    // await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        console.log('🔍 CHAPTERS DEBUG: Starting loadData()')
        this.loading = true
        
        const [chaptersData, membersData] = await Promise.all([
          chapterService.getAllChapters(),
          memberService.getAllMembers()
        ])
        
        console.log('🔍 CHAPTERS DEBUG: Loaded chapters:', chaptersData)
        
        this.chapters = chaptersData
        this.searchResults = chaptersData
        
        console.log('🔍 CHAPTERS DEBUG: Updated searchResults:', this.searchResults)
        
      } catch (error) {
        console.error('🔍 CHAPTERS DEBUG: Error loading chapters:', error)
      } finally {
        this.loading = false
        console.log('🔍 CHAPTERS DEBUG: Loading complete')
      }
    }
  }
}
</script>
  setup() {
    // Use the chapter links composable for consistent functionality
    const { getCampusLabsUrl, getButtonText, getButtonTooltip, trackLinkClick } = useChapterLinks()
    
    // Use theme composable
    const { currentTheme } = useTheme()
    
    return {
      getCampusLabsUrl,
      getButtonText,
      getButtonTooltip,
      trackLinkClick,
      isDarkModeComputed: computed(() => currentTheme.value === 'dark')
    }
  },
  data() {
    return {
      loading: false, // Temporarily set to false for debugging
      saving: false,
      chapters: [
        {
          id: 1,
          name: "Test Chapter",
          university: "Test University",
          state: "California",
          city: "Test City",
          description: "Test description"
        }
      ],
      members: [],
      searchResults: [
        {
          id: 1,
          name: "Test Chapter",
          university: "Test University",
          state: "California",
          city: "Test City",
          description: "Test description"
        }
      ],
      availableStates: [],
      availableCities: [],
      searchTerm: '',
      selectedState: '',
      showAdvancedSearch: false,
      showCreateModal: false,
      showEditModal: false,
      showDeleteModal: false,
      chapterToDelete: null,
      advancedFilters: {
        university: '',
        city: '',
        active: ''
      },
      chapterForm: {
        id: null,
        name: '',
        universityName: '',
        city: '',
        state: '',
        foundedDate: '',
        description: ''
      },
      // New properties for enhanced features
      currentView: 'table',
      sortBy: 'name',
      sortOrder: 'asc',
      comparisonMode: false,
      selectedChapters: [],
      showComparisonModal: false,
      savedSearches: [],
      showAnalytics: false,
      isDarkMode: false,
      searchSuggestions: [],
      showSavedSearches: false,
      advancedSearchFilters: {
        university: '',
        city: '',
        healthScoreMin: 0,
        healthScoreMax: 100,
        memberCountMin: 0,
        memberCountMax: 1000,
        foundedDateStart: '',
        foundedDateEnd: '',
        activityLevel: ''
      },
      activeFilters: []
    }
  },
  computed: {
    hasActiveFilters() {
      return this.searchTerm || 
             this.selectedState || 
             this.advancedFilters.university || 
             this.advancedFilters.city || 
             this.advancedFilters.active !== ''
    },
    // Performance optimization: pre-calculate member counts to avoid repeated filtering
    memberCounts() {
      const counts = {}
      this.members.forEach(member => {
        if (member.chapter?.id) {
          counts[member.chapter.id] = (counts[member.chapter.id] || 0) + 1
        }
      })
      return counts
    },
    
    comparisonChapters() {
      return this.chapters.filter(chapter => this.selectedChapters.includes(chapter.id))
    },
    
    sortedAndFilteredChapters() {
      let results = [...this.searchResults]
      
      // Apply sorting
      results.sort((a, b) => {
        let aValue, bValue
        
        switch (this.sortBy) {
          case 'name':
            aValue = a.name.toLowerCase()
            bValue = b.name.toLowerCase()
            break
          case 'university':
            aValue = a.universityName.toLowerCase()
            bValue = b.universityName.toLowerCase()
            break
          case 'members':
            aValue = this.memberCounts[a.id] || 0
            bValue = this.memberCounts[b.id] || 0
            break
          case 'founded':
            aValue = new Date(a.foundedDate)
            bValue = new Date(b.foundedDate)
            break
          case 'health':
            aValue = this.getChapterHealthScore(a)
            bValue = this.getChapterHealthScore(b)
            break
          case 'location':
            aValue = `${a.city}, ${a.state}`.toLowerCase()
            bValue = `${b.city}, ${b.state}`.toLowerCase()
            break
          default:
            return 0
        }
        
        if (aValue < bValue) return this.sortOrder === 'asc' ? -1 : 1
        if (aValue > bValue) return this.sortOrder === 'asc' ? 1 : -1
        return 0
      })
      
      return results
    },

    uniqueUniversities() {
      return [...new Set(this.chapters.map(c => c.universityName).filter(Boolean))].sort()
    },

    healthStats() {
      const stats = {
        excellent: 0,
        good: 0,
        needsAttention: 0
      }
      
      this.searchResults.forEach(chapter => {
        const score = this.getChapterHealthScore(chapter)
        if (score >= 80) {
          stats.excellent++
        } else if (score >= 60) {
          stats.good++
        } else {
          stats.needsAttention++
        }
      })
      
      return stats
    },

    allSelected() {
      return this.selectedChapters.length > 0 && 
             this.selectedChapters.length === Math.min(3, this.searchResults.length)
    }
  },
  async mounted() {
    console.log('🔍 CHAPTERS DEBUG: Component mounted()')
    // Check if there are route query parameters
    if (this.$route.query.state) {
      this.selectedState = this.$route.query.state
    }
    
    // Load saved searches from localStorage
    const saved = localStorage.getItem('savedSearches')
    if (saved) {
      this.savedSearches = JSON.parse(saved)
    }
    
    console.log('🔍 CHAPTERS DEBUG: About to call loadData()')
    await this.loadData()
    console.log('🔍 CHAPTERS DEBUG: loadData() completed')
  },
  methods: {
    async loadData() {
      try {
        console.log('🔍 CHAPTERS DEBUG: Starting loadData()')
        this.loading = true
        console.log('🔍 CHAPTERS DEBUG: Loading set to true')
        
        const [chaptersData, membersData] = await Promise.all([
          chapterService.getAllChapters(),
          memberService.getAllMembers()
        ])
        
        console.log('🔍 CHAPTERS DEBUG: Loaded chapters:', chaptersData)
        console.log('🔍 CHAPTERS DEBUG: Chapters length:', chaptersData.length)
        console.log('🔍 CHAPTERS DEBUG: Loaded members:', membersData)
        
        this.chapters = chaptersData
        this.members = membersData
        this.searchResults = chaptersData
        
        console.log('🔍 CHAPTERS DEBUG: Set this.chapters to:', this.chapters)
        console.log('🔍 CHAPTERS DEBUG: Set this.searchResults to:', this.searchResults)
        
        // Extract unique states and cities for filters
        this.availableStates = [...new Set(chaptersData.map(c => c.state).filter(Boolean))].sort()
        this.availableCities = [...new Set(chaptersData.map(c => c.city).filter(Boolean))].sort()
        
        console.log('🔍 CHAPTERS DEBUG: Available states:', this.availableStates)
        
      } catch (error) {
        console.error('🔍 CHAPTERS DEBUG: Error loading chapters:', error)
      } finally {
        this.loading = false
        console.log('🔍 CHAPTERS DEBUG: Loading set to false')
      }
    },

    toggleCurrentView() {
      this.currentView = this.currentView === 'table' ? 'card' : 'table'
    },

    async performSearch() {
      try {
        // Build search parameters
        const searchParams = {}
        
        if (this.searchTerm) searchParams.name = this.searchTerm
        if (this.selectedState) searchParams.state = this.selectedState
        if (this.advancedFilters.university) searchParams.university = this.advancedFilters.university
        if (this.advancedFilters.city) searchParams.city = this.advancedFilters.city
        if (this.advancedFilters.active !== '') searchParams.active = this.advancedFilters.active
        
        console.log('Search parameters:', searchParams)
        console.log('Available chapters:', this.chapters.length)
        console.log('Chapters by state:', this.chapters.map(c => ({ name: c.name, state: c.state })))
        
        // If no search parameters, show all chapters
        if (Object.keys(searchParams).length === 0) {
          this.searchResults = this.chapters
          return
        }
        
        // Perform backend search
        console.log('Calling backend search with params:', searchParams)
        this.searchResults = await chapterService.searchChapters(searchParams)
        console.log('Backend search returned:', this.searchResults.length, 'chapters')
        console.log('Search results:', this.searchResults.map(c => ({ name: c.name, state: c.state })))
        
        // If backend returns empty but we know chapters exist for this state, use fallback
        if (this.searchResults.length === 0 && this.selectedState) {
          const expectedCount = this.getStateChapterCount(this.selectedState)
          if (expectedCount > 0) {
            console.log(`Backend returned 0 results but ${expectedCount} chapters exist for ${this.selectedState}, using fallback`)
            this.performLocalSearch()
            return
          }
        }
        
      } catch (error) {
        console.error('Error searching chapters:', error)
        console.log('Backend search failed, falling back to local search')
        // Fallback to local filtering if backend search fails
        this.performLocalSearch()
      }
    },

    performLocalSearch() {
      console.log('Performing local search...')
      let results = this.chapters
      console.log('Starting with', results.length, 'chapters')
      
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase()
        results = results.filter(chapter =>
          chapter.name.toLowerCase().includes(term) ||
          chapter.universityName.toLowerCase().includes(term)
        )
        console.log('After search term filter:', results.length, 'chapters')
      }
      
      if (this.selectedState) {
        console.log('Filtering by state:', this.selectedState)
        console.log('Available states in chapters:', [...new Set(results.map(c => c.state))])
        results = results.filter(chapter => chapter.state === this.selectedState)
        console.log('After state filter:', results.length, 'chapters')
      }
      
      if (this.advancedFilters.university) {
        const university = this.advancedFilters.university.toLowerCase()
        results = results.filter(chapter =>
          chapter.universityName.toLowerCase().includes(university)
        )
      }
      
      if (this.advancedFilters.city) {
        results = results.filter(chapter => chapter.city === this.advancedFilters.city)
      }
      
      if (this.advancedFilters.active !== '') {
        results = results.filter(chapter => chapter.active === this.advancedFilters.active)
      }
      
      console.log('Local search completed, setting results:', results.length, 'chapters')
      this.searchResults = results
    },

    clearFilters() {
      this.searchTerm = ''
      this.selectedState = ''
      this.advancedFilters = {
        university: '',
        city: '',
        active: ''
      }
      this.advancedSearchFilters = {
        university: '',
        city: '',
        healthScoreMin: 0,
        healthScoreMax: 100,
        memberCountMin: 0,
        memberCountMax: 1000,
        foundedDateStart: '',
        foundedDateEnd: '',
        activityLevel: ''
      }
      this.activeFilters = []
      this.searchResults = this.chapters
    },

    // New methods for enhanced features
    toggleView(view) {
      this.currentView = view
    },

    toggleSort(column) {
      if (this.sortBy === column) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortBy = column
        this.sortOrder = 'asc'
      }
    },

    toggleComparisonMode() {
      this.comparisonMode = !this.comparisonMode
      if (!this.comparisonMode) {
        this.selectedChapters = []
      }
    },

    toggleChapterSelection(chapterId) {
      const index = this.selectedChapters.indexOf(chapterId)
      if (index > -1) {
        this.selectedChapters.splice(index, 1)
      } else if (this.selectedChapters.length < 3) { // Limit to 3 chapters for comparison
        this.selectedChapters.push(chapterId)
      }
    },

    openComparisonModal() {
      if (this.selectedChapters.length > 0) {
        this.showComparisonModal = true
      }
    },

    closeComparisonModal() {
      this.showComparisonModal = false
    },

    getChapterHealthScore(chapter) {
      const memberCount = this.memberCounts[chapter.id] || 0
      const age = this.getYearsSinceFounded(chapter.foundedDate)
      const eventCount = this.getChapterEventCount(chapter.id)
      
      // Calculate health score based on multiple factors
      let score = 0
      
      // Member count factor (30% of score)
      if (memberCount >= 50) score += 30
      else if (memberCount >= 25) score += 20
      else if (memberCount >= 10) score += 15
      else score += 5
      
      // Activity factor (40% of score) - based on events
      if (eventCount >= 10) score += 40
      else if (eventCount >= 5) score += 30
      else if (eventCount >= 2) score += 20
      else if (eventCount >= 1) score += 10
      
      // Longevity factor (20% of score)
      if (age >= 3) score += 20
      else if (age >= 2) score += 15
      else if (age >= 1) score += 10
      else score += 5
      
      // Status factor (10% of score)
      if (chapter.active) score += 10
      
      return Math.min(score, 100)
    },

    getHealthScoreColor(score) {
      if (score >= 80) return '#28a745'      // Green
      if (score >= 60) return '#ffc107'      // Yellow
      if (score >= 40) return '#fd7e14'      // Orange
      return '#dc3545'                       // Red
    },

    getHealthScoreLabel(score) {
      if (score >= 80) return 'Excellent'
      if (score >= 60) return 'Good'
      if (score >= 40) return 'Fair'
      return 'Needs Attention'
    },

    getHealthScoreBadgeClass(score) {
      if (score >= 80) return 'bg-success'      // Green
      if (score >= 60) return 'bg-primary'      // Blue  
      if (score >= 40) return 'bg-warning'      // Yellow
      return 'bg-danger'                        // Red
    },

    getHealthScoreStatus(score) {
      if (score >= 80) return 'Excellent'
      if (score >= 60) return 'Good'
      if (score >= 40) return 'Fair'
      return 'Needs Attention'
    },

    getHealthDotClass(metric, chapter) {
      const score = this.getPerformanceMetric(chapter, metric)
      if (score >= 80) return 'bg-success'
      if (score >= 60) return 'bg-primary'
      if (score >= 40) return 'bg-warning'
      return 'bg-danger'
    },

    getMemberCountBadgeClass(memberCount) {
      if (memberCount >= 100) return 'badge bg-success'    // Green for large chapters
      if (memberCount >= 50) return 'badge bg-primary'     // Blue for medium chapters
      if (memberCount >= 20) return 'badge bg-warning'     // Yellow for small chapters
      if (memberCount >= 1) return 'badge bg-info'         // Light blue for new chapters
      return 'badge bg-secondary'                          // Gray for empty chapters
    },

    getYearsSinceFounded(foundedDate) {
      const now = new Date()
      const founded = new Date(foundedDate)
      return Math.floor((now - founded) / (365.25 * 24 * 60 * 60 * 1000))
    },

    getChapterEventCount(chapterId) {
      // This would typically come from events data
      // For now, return a calculated estimate
      const memberCount = this.memberCounts[chapterId] || 0
      return Math.floor(memberCount / 5) + Math.floor(Math.random() * 5)
    },

    getPerformanceMetric(chapter, type) {
      const memberCount = this.memberCounts[chapter.id] || 0
      const age = this.getYearsSinceFounded(chapter.foundedDate)
      
      switch (type) {
        case 'growth':
          // Estimate growth based on member count and age
          if (age === 0) return 50
          return Math.min((memberCount / age) * 2, 100)
        
        case 'activity':
          // Estimate activity based on events and members
          const eventCount = this.getChapterEventCount(chapter.id)
          return Math.min((eventCount / Math.max(memberCount / 10, 1)) * 20, 100)
        
        case 'engagement':
          // Estimate engagement based on member-to-event ratio
          const events = this.getChapterEventCount(chapter.id)
          if (memberCount === 0) return 0
          return Math.min((events / memberCount) * 100, 100)
        
        default:
          return 0
      }
    },

    applyAdvancedSearch() {
      let results = this.chapters
      
      // Apply all advanced filters
      if (this.advancedSearchFilters.university) {
        const term = this.advancedSearchFilters.university.toLowerCase()
        results = results.filter(c => 
          c.universityName.toLowerCase().includes(term)
        )
      }
      
      if (this.advancedSearchFilters.city) {
        results = results.filter(c => c.city === this.advancedSearchFilters.city)
      }
      
      // Health score range
      results = results.filter(c => {
        const score = this.getChapterHealthScore(c)
        return score >= this.advancedSearchFilters.healthScoreMin && 
               score <= this.advancedSearchFilters.healthScoreMax
      })
      
      // Member count range
      results = results.filter(c => {
        const count = this.memberCounts[c.id] || 0
        return count >= this.advancedSearchFilters.memberCountMin && 
               count <= this.advancedSearchFilters.memberCountMax
      })
      
      // Founded date range
      if (this.advancedSearchFilters.foundedDateStart) {
        const startDate = new Date(this.advancedSearchFilters.foundedDateStart)
        results = results.filter(c => new Date(c.foundedDate) >= startDate)
      }
      
      if (this.advancedSearchFilters.foundedDateEnd) {
        const endDate = new Date(this.advancedSearchFilters.foundedDateEnd)
        results = results.filter(c => new Date(c.foundedDate) <= endDate)
      }
      
      // Activity level
      if (this.advancedSearchFilters.activityLevel) {
        results = results.filter(c => {
          const score = this.getChapterHealthScore(c)
          const level = this.advancedSearchFilters.activityLevel
          
          switch (level) {
            case 'high': return score >= 80
            case 'medium': return score >= 40 && score < 80
            case 'low': return score < 40
            default: return true
          }
        })
      }
      
      this.searchResults = results
      this.updateActiveFilters()
    },

    updateActiveFilters() {
      this.activeFilters = []
      
      if (this.searchTerm) {
        this.activeFilters.push({
          type: 'search',
          label: `Search: "${this.searchTerm}"`,
          value: this.searchTerm
        })
      }
      
      if (this.selectedState) {
        this.activeFilters.push({
          type: 'state',
          label: `State: ${this.selectedState}`,
          value: this.selectedState
        })
      }
      
      if (this.advancedSearchFilters.university) {
        this.activeFilters.push({
          type: 'university',
          label: `University: ${this.advancedSearchFilters.university}`,
          value: this.advancedSearchFilters.university
        })
      }
      
      if (this.advancedSearchFilters.city) {
        this.activeFilters.push({
          type: 'city',
          label: `City: ${this.advancedSearchFilters.city}`,
          value: this.advancedSearchFilters.city
        })
      }
      
      const healthMin = this.advancedSearchFilters.healthScoreMin
      const healthMax = this.advancedSearchFilters.healthScoreMax
      if (healthMin > 0 || healthMax < 100) {
        this.activeFilters.push({
          type: 'health',
          label: `Health Score: ${healthMin}-${healthMax}%`,
          value: `${healthMin}-${healthMax}`
        })
      }
      
      const memberMin = this.advancedSearchFilters.memberCountMin
      const memberMax = this.advancedSearchFilters.memberCountMax
      if (memberMin > 0 || memberMax < 1000) {
        this.activeFilters.push({
          type: 'members',
          label: `Members: ${memberMin}-${memberMax}`,
          value: `${memberMin}-${memberMax}`
        })
      }
    },

    removeFilter(filter) {
      switch (filter.type) {
        case 'search':
          this.searchTerm = ''
          break
        case 'state':
          this.selectedState = ''
          break
        case 'university':
          this.advancedSearchFilters.university = ''
          break
        case 'city':
          this.advancedSearchFilters.city = ''
          break
        case 'health':
          this.advancedSearchFilters.healthScoreMin = 0
          this.advancedSearchFilters.healthScoreMax = 100
          break
        case 'members':
          this.advancedSearchFilters.memberCountMin = 0
          this.advancedSearchFilters.memberCountMax = 1000
          break
      }
      this.applyAdvancedSearch()
    },

    saveSearch() {
      const searchName = prompt('Enter a name for this search:')
      if (searchName) {
        const search = {
          id: Date.now(), // Simple ID generation
          name: searchName,
          filters: { ...this.advancedSearchFilters },
          searchTerm: this.searchTerm,
          selectedState: this.selectedState,
          timestamp: new Date().toISOString()
        }
        this.savedSearches.push(search)
        localStorage.setItem('savedSearches', JSON.stringify(this.savedSearches))
      }
    },

    loadSavedSearch(search) {
      this.advancedSearchFilters = { ...search.filters }
      this.searchTerm = search.searchTerm
      this.selectedState = search.selectedState
      this.applyAdvancedSearch()
    },

    deleteSavedSearch(searchId) {
      const index = this.savedSearches.findIndex(s => s.id === searchId)
      if (index > -1) {
        this.savedSearches.splice(index, 1)
        localStorage.setItem('savedSearches', JSON.stringify(this.savedSearches))
      }
    },

    exportComparison() {
      const data = this.comparisonChapters.map(chapter => ({
        name: chapter.name,
        university: chapter.universityName,
        location: `${chapter.city}, ${chapter.state}`,
        members: this.memberCounts[chapter.id] || 0,
        healthScore: this.getChapterHealthScore(chapter),
        founded: chapter.foundedDate,
        age: this.getYearsSinceFounded(chapter.foundedDate)
      }))
      
      const csv = [
        ['Name', 'University', 'Location', 'Members', 'Health Score', 'Founded', 'Age (Years)'],
        ...data.map(row => Object.values(row))
      ].map(row => row.join(',')).join('\n')
      
      const blob = new Blob([csv], { type: 'text/csv' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'chapter-comparison.csv'
      a.click()
      window.URL.revokeObjectURL(url)
    },

    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    },

    
    getAlternateCampusLabsSearch(chapter) {
      // Provide a fallback search URL if the direct link doesn't work
      const searchTerm = encodeURIComponent(`${chapter.universityName} turning point usa`)
      return `https://www.google.com/search?q=site:campuslabs.com+${searchTerm}`
    },
    editChapter(chapter) {
      this.chapterForm = {
        id: chapter.id,
        name: chapter.name,
        universityName: chapter.universityName,
        city: chapter.city,
        state: chapter.state,
        foundedDate: chapter.foundedDate,
        description: chapter.description || ''
      }
      this.showEditModal = true
    },
    confirmDelete(chapter) {
      this.chapterToDelete = chapter
      this.showDeleteModal = true
    },
    async saveChapter() {
      try {
        this.saving = true
        
        if (this.showEditModal) {
          await chapterService.updateChapter(this.chapterForm.id, this.chapterForm)
        } else {
          await chapterService.createChapter(this.chapterForm)
        }
        
        await this.loadData()
        await this.performSearch() // Refresh search results
        this.closeModal()
      } catch (error) {
        console.error('Error saving chapter:', error)
        alert('Error saving chapter. Please try again.')
      } finally {
        this.saving = false
      }
    },
    async deleteChapter() {
      try {
        await chapterService.deleteChapter(this.chapterToDelete.id)
        await this.loadData()
        await this.performSearch() // Refresh search results
        this.showDeleteModal = false
        this.chapterToDelete = null
      } catch (error) {
        console.error('Error deleting chapter:', error)
        alert('Error deleting chapter. Please try again.')
      }
    },

    getStateChapterCount(state) {
      return this.chapters.filter(chapter => chapter.state === state).length
    },

    getSortIcon(column) {
      if (this.sortBy !== column) {
        return 'bi-chevron-expand'
      }
      return this.sortOrder === 'asc' ? 'bi-chevron-up' : 'bi-chevron-down'
    },

    setSortCriteria(column) {
      if (this.sortBy === column) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortBy = column
        this.sortOrder = 'asc'
      }
    },

    truncateText(text, maxLength) {
      if (!text) return ''
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    },

    toggleAllSelection() {
      if (this.allSelected) {
        this.selectedChapters = []
      } else {
        this.selectedChapters = this.searchResults.slice(0, 3).map(chapter => chapter.id)
      }
    },

    viewChapterDetails(chapter) {
      // Navigate to chapter detail view or open modal with detailed information
      this.$router.push(`/chapters/${chapter.id}`)
    },

    closeModal() {
      this.showCreateModal = false
      this.showEditModal = false
      this.showDeleteModal = false
      this.chapterForm = {
        id: null,
        name: '',
        universityName: '',
        city: '',
        state: '',
        foundedDate: '',
        description: ''
      }
    },

    toggleAnalytics() {
      this.showAnalytics = !this.showAnalytics
    },

    handleEnhancedSearch(searchParams) {
      console.log('Enhanced search params:', searchParams)
      
      // Convert the enhanced search params to our existing search format
      this.searchTerm = searchParams.query || ''
      this.selectedState = searchParams.state || ''
      this.advancedFilters = {
        university: '',
        city: searchParams.city || '',
        active: searchParams.active || ''
      }
      
      // Apply the search using existing logic
      this.performSearch()
    },

    handleFilterChange(filters) {
      console.log('Filter change:', filters)
      // Update internal filters and trigger search
      this.advancedFilters = {
        ...this.advancedFilters,
        city: filters.city || '',
        active: filters.active || ''
      }
      this.selectedState = filters.state || ''
    }
  },

  computed: {
    isDarkMode() {
      return this.isDarkModeComputed
    }
  }
}
</script>

<style scoped>
.modal.show {
  animation: fadeIn 0.15s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Enhanced Chapter Management Styles */

/* Actions Bar */
.actions-bar {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.5rem;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.actions-bar .form-control,
.actions-bar .form-select {
  border-radius: 0.375rem;
}

/* Advanced Search */
.advanced-search-card {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  background: #ffffff;
}

.advanced-search-card .card-header {
  background: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.filter-row {
  display: flex;
  gap: 1rem;
  align-items: end;
  margin-bottom: 1rem;
}

.filter-row .form-group {
  flex: 1;
}

.range-inputs {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.range-inputs input {
  flex: 1;
}

/* Active Filters */
.active-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.filter-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: #e3f2fd;
  color: #1976d2;
  border: 1px solid #bbdefb;
  border-radius: 1rem;
  padding: 0.25rem 0.75rem;
  font-size: 0.875rem;
}

.filter-badge .btn-close {
  --bs-btn-close-bg: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%231976d2'%3e%3cpath d='m.293.293.707.707L8 7.586 15.293 1.293l.707.707L8.707 9.5l7.293 7.293-.707.707L8 9.707.707 16A.707.707 0 010 15.293L7.293 8 .293.707A.707.707 0 01.293.293z'/%3e%3c/svg%3e");
  width: 0.75em;
  height: 0.75em;
}

/* Saved Searches */
.saved-searches {
  border-top: 1px solid #dee2e6;
  padding-top: 1rem;
  margin-top: 1rem;
}

.saved-search-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.saved-search-item button {
  border-radius: 0.375rem;
}

/* Comparison Mode */
.comparison-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 0.5rem;
  padding: 1rem;
  margin-bottom: 1rem;
}

/* Health Score Indicators */
.health-indicator {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.health-score-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 1rem;
  font-weight: 500;
  font-size: 0.75rem;
  color: white;
}

.health-score-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 3px solid #e9ecef;
}

.health-score-circle::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  background: var(--bg, #e9ecef);
}

.health-score-circle span {
  position: relative;
  z-index: 1;
  font-weight: bold;
}

/* Sortable Table Headers */
.sortable-header {
  cursor: pointer;
  user-select: none;
  position: relative;
  padding-right: 1.5rem !important;
}

.sortable-header:hover {
  background-color: #f8f9fa;
}

.sort-icon {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.5;
}

.sortable-header.active .sort-icon {
  opacity: 1;
}

/* Card View */
.chapter-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.chapter-card {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  transition: all 0.2s ease;
  position: relative;
  background: white;
}

.chapter-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.chapter-card.selected {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.card-selection-checkbox {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 2;
}

.chapter-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0.75rem 0;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #495057;
}

.stat-label {
  font-size: 0.75rem;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Comparison Modal */
.comparison-metrics {
  border: 1px solid #e9ecef;
  border-radius: 0.375rem;
  overflow: hidden;
}

.metric-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.metric-row:last-child {
  border-bottom: none;
}

.metric-label {
  font-weight: 500;
  color: #495057;
}

.metric-value {
  font-weight: 600;
  color: #212529;
}

/* Performance Bars */
.performance-bars {
  background: #f8f9fa;
  border-radius: 0.375rem;
  padding: 0.75rem;
}

.performance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.performance-item:last-child {
  margin-bottom: 0;
}

.performance-item span {
  min-width: 80px;
  font-size: 0.75rem;
}

.progress-sm {
  height: 4px;
  flex: 1;
  margin-left: 0.75rem;
}

/* View Toggle Buttons */
.view-toggle-btn {
  border: 1px solid #dee2e6;
  background: white;
  color: #6c757d;
  transition: all 0.2s ease;
}

.view-toggle-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.view-toggle-btn.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

/* Responsive Design */
@media (max-width: 768px) {
  .actions-bar {
    padding: 0.75rem;
  }
  
  .filter-row {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .chapter-cards {
    grid-template-columns: 1fr;
  }
  
  .comparison-banner {
    text-align: center;
  }
  
  .health-score-circle {
    width: 60px;
    height: 60px;
  }
  
  .performance-item {
    flex-direction: column;
    align-items: stretch;
    gap: 0.25rem;
  }
  
  .performance-item .progress-sm {
    margin-left: 0;
  }
}

/* Health Score Colors */
.health-excellent { background-color: #28a745 !important; }
.health-good { background-color: #ffc107 !important; }
.health-fair { background-color: #fd7e14 !important; }
.health-poor { background-color: #dc3545 !important; }

/* Animation for loading states */
.loading-pulse {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
</style>