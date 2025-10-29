import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Chapters from '../views/Chapters.vue'
import TestChapters from '../views/TestChapters.vue'
import Members from '../views/Members.vue'
import Events from '../views/Events.vue'
import ChapterDetail from '../views/ChapterDetail.vue'
import Login from '../views/Login.vue'
import Registration from '../views/Registration.vue'
import Blog from '../views/Blog.vue'
import BlogDetail from '../views/BlogDetail.vue'
import BlogCreate from '../views/BlogCreate.vue'
import About from '../views/About.vue'

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
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Registration',
    component: Registration
  },
  {
    path: '/blog',
    name: 'Blog',
    component: Blog
  },
  {
    path: '/blog/create',
    name: 'BlogCreate',
    component: BlogCreate
  },
  {
    path: '/blog/:id',
    name: 'BlogDetail',
    component: BlogDetail,
    props: true
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router