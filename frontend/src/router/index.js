import { createRouter, createWebHistory } from 'vue-router'

// Lazy load components
const Dashboard = () => import('../views/Dashboard.vue')
const Chapters = () => import('../views/Chapters.vue')
const ChapterCreate = () => import('../views/ChapterCreate.vue')
const Members = () => import('../views/Members.vue')
const Events = () => import('../views/Events.vue')
const Blog = () => import('../views/Blog.vue')
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const Profile = () => import('../views/Profile.vue')
const Settings = () => import('../views/Settings.vue')

const routes = [
    {
        path: '/',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: 'Dashboard' }
    },
    {
        path: '/chapters',
        name: 'Chapters',
        component: Chapters,
        meta: { title: 'Chapters' }
    },
    {
        path: '/chapters/create',
        name: 'ChapterCreate',
        component: ChapterCreate,
        meta: { 
            title: 'Create Chapter',
            requiresAuth: true
        }
    },
    {
        path: '/members',
        name: 'Members',
        component: Members,
        meta: { title: 'Members' }
    },
    {
        path: '/events',
        name: 'Events',
        component: Events,
        meta: { title: 'Events' }
    },
    {
        path: '/blog',
        name: 'Blog',
        component: Blog,
        meta: {
            title: 'Blog',
            requiresAuth: true
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: {
            title: 'Login',
            hideForAuth: true
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: Register,
        meta: {
            title: 'Register',
            hideForAuth: true
        }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: Profile,
        meta: {
            title: 'Profile',
            requiresAuth: true
        }
    },
    {
        path: '/settings',
        name: 'Settings',
        component: Settings,
        meta: {
            title: 'Settings',
            requiresAuth: true
        }
    },
    // Catch all route - redirect to dashboard
    {
        path: '/:catchAll(.*)',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return { top: 0 }
        }
    }
})

// Navigation guards
router.beforeEach((to, from, next) => {
    // Check authentication status
    const isAuthenticated = () => {
        try {
            const user = localStorage.getItem('user')
            return user !== null
        } catch {
            return false
        }
    }

    // Set document title
    document.title = to.meta.title
        ? `${to.meta.title} - Campus Chapter Organizer`
        : 'Campus Chapter Organizer'

    // Handle authentication requirements
    if (to.meta.requiresAuth && !isAuthenticated()) {
        // Redirect to login if authentication is required but user is not authenticated
        next('/login')
    } else if (to.meta.hideForAuth && isAuthenticated()) {
        // Redirect authenticated users away from login/register pages
        next('/')
    } else {
        next()
    }
})

export default router
