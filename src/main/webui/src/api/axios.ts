import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1/task-tracker',
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use(config => {
  const item = localStorage.getItem('loginResponse')

  if (item) {
    const token = JSON.parse(item).token
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default apiClient