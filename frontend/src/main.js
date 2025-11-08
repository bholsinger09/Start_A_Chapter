import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from './services/api'

// Import Bootstrap CSS and JS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'

const app = createApp(App)

// Make axios available globally
app.config.globalProperties.$http = axios

app.use(router)
app.mount('#app')
