import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Chapters from '../views/Chapters.vue'
import ChapterCreate from '../views/ChapterCreate.vue'
import Members from '../views/Members.vue'
import MemberCreate from '../views/MemberCreate.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

// Authentication helper
const isAuthenticated = () => {
  const token = localStorage.getItem('authToken')
  const expiry = localStorage.getItem('tokenExpiry')
  
  if (!token || !expiry) return false
  
  return Date.now() < parseInt(expiry)
}

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/dashboard',
    redirect: '/'
  },
  {
    path: '/chapters',
    name: 'Chapters',
    component: Chapters,
    meta: { requiresAuth: true }
  },
  {
    path: '/chapters/create',
    name: 'ChapterCreate',
    component: ChapterCreate,
    meta: { requiresAuth: true }
  },
  {
    path: '/chapters/:id/edit',
    name: 'ChapterEdit',
    component: ChapterCreate,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/members',
    name: 'Members',
    component: Members,
    meta: { requiresAuth: true }
  },
  {
    path: '/members/create',
    name: 'MemberCreate',
    component: MemberCreate,
    meta: { requiresAuth: true }
  },
  {
    path: '/members/:id',
    name: 'MemberDetail',
    component: MemberCreate,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/members/:id/edit',
    name: 'MemberEdit',
    component: MemberCreate,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresGuest: true }
  },
  // Catch-all route for 404s
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authenticated = isAuthenticated()
  
  // Routes that require authentication
  if (to.meta.requiresAuth && !authenticated) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // Routes that require guest (not authenticated)
  if (to.meta.requiresGuest && authenticated) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
