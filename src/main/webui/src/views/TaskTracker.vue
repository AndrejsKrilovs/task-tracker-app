<template>
  <div class="task-tracker-container">
    <header class="header">
      <div class="welcome-text">Welcome, {{ user }}!</div>
      <div class="header-actions">
        <select v-model="language" class="language-select">
          <option value="en">EN</option>
          <option value="ru">RU</option>
        </select>

        <button v-if="canCreateTask && !showCreateForm" class="main-btn" @click="showCreateForm = true">
          Create New Task
        </button>
        <button class="main-btn" @click="logout">Logout</button>
      </div>
    </header>

    <main class="main-content">
      <CreateTask v-if="showCreateForm" @cancel="showCreateForm = false" />

      <h1 class="main-title">Your Tasks</h1>
      <p class="subtitle">This is where your task list will be shown.</p>
      <!-- TODO: Replace with real task list -->
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/axios'
import CreateTask from './CreateTask'
import { hasTaskCreateAccess, extractUsername } from '@/utils/jwt'

const router = useRouter()
const language = ref('en')
const user = ref<string | null>(null)
const showCreateForm = ref(false)
const canCreateTask = ref(false)

onMounted(() => {
  const item = localStorage.getItem('loginResponse')
  const token = JSON.parse(item).token

  if (!item && !token) {
    router.replace('/login')
    return
  }

  user.value = extractUsername(token)
  canCreateTask.value = hasTaskCreateAccess(token)
  router.replace('/tasks')
})

async function logout() {
  const response = await apiClient.get('/users/logout')
  if (response.status === 200) {
    router.push('/logout')
  }
}
</script>

<style scoped>
.task-tracker-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(145deg, #f0f4f8, #d9e2ec);
  padding: 1rem;
  font-family: 'Segoe UI', sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 1rem 2rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  margin-bottom: 2rem;
}

.welcome-text {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d3748;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.language-select {
  padding: 0.5rem;
  border-radius: 8px;
  border: 1px solid #cbd5e0;
  background: #f7fafc;
  font-size: 1rem;
}

.main-content {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  flex-grow: 1;
}

.main-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #4a5568;
}
</style>
