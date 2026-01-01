import axios from 'axios'
import router from '@/router'
import { useUserStore } from '@/assets/store'

const apiClient = axios.create({
  withCredentials: true,
  baseURL: 'http://localhost:8080/api/v1/task-tracker',
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status

    if (status === 401 || status === 403) {
      const userStore = useUserStore()

      if (userStore.user) {
        userStore.clearUser()
        router.push('/login')
      }
    }

    return Promise.reject(error)
  }
)

export default apiClient