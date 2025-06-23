import { createRouter, createWebHistory } from 'vue-router'
import Register from '@/views/Register'
import Login from '@/views/Login'
import TaskTracker from '@/views/TaskTracker'
import { useUserStore } from '@/assets/store'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresGuest: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresGuest: true }
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: TaskTracker,
    meta: { requiresAuth: true }
  },
  {
    path: '/logout',
    name: 'Logout',
    beforeEnter: (to, from, next) => {
      next('/login')
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory('/quinoa'),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    return next('/login')
  }

  if (to.meta.requiresGuest && userStore.isAuthenticated) {
    return next('/tasks')
  }

  next()
})

export default router
