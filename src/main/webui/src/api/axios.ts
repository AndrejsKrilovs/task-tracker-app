import axios from 'axios'

const apiClient = axios.create({
  withCredentials: true,
  baseURL: 'http://localhost:8080/api/v1/task-tracker',
  headers: {
    'Content-Type': 'application/json'
  }
})

export default apiClient