import { createRouter, createWebHistory } from 'vue-router'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'

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
  }
]

const router = createRouter({
  history: createWebHistory('/quinoa'), // если используешь Quinoa + Vite
  routes
})

export default router
