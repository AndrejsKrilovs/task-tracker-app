import axios from 'axios'

const apiClient = axios.create({
  withCredentials: true,
  baseURL: 'http://localhost:8080/api/v1/task-tracker',
  headers: {
    'Content-Type': 'application/json'
  }
})

export function hasTaskCreateAccess(): boolean {
  return true
}

export function hasTaskUpdateAccess(): boolean {
  return true
}

export default apiClient