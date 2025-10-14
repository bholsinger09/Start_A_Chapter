import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Chapters from '../views/Chapters.vue'
import Members from '../views/Members.vue'
import Events from '../views/Events.vue'
import ChapterDetail from '../views/ChapterDetail.vue'

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
    path: '/chapters/:id',
    name: 'ChapterDetail',
    component: ChapterDetail,
    props: true
  },
  {
    path: '/members',
    name: 'Members',
    component: Members
  },
  {
    path: '/events',
    name: 'Events',
    component: Events
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router