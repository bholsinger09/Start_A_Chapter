import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Chapters from '../views/Chapters.vue'
import ChapterCreate from '../views/ChapterCreate.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/chapters',
    name: 'Chapters',
    component: Chapters
  },
  {
    path: '/chapters/create',
    name: 'ChapterCreate',
    component: ChapterCreate
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
