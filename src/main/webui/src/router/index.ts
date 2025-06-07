import { createRouter, createWebHistory } from 'vue-router'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'
import TaskTracker from '../views/TaskTracker.vue'

const routes = [
  {
      path: '/',
      redirect: '/login'
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: TaskTracker
  },
  {
    path: '/logout',
    name: 'Logout',
    beforeEnter: (to, from, next) => {
      // TODO: сброс токенов, сессий и т.п.
      next('/login')
    }
  }
]

const router = createRouter({
  history: createWebHistory('/quinoa'), // если используешь Quinoa + Vite
  routes
})

export default router
