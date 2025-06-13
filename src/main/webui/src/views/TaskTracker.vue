<template>
  <div class="task-tracker-container">
    <header class="header">
      <div class="welcome-text">Welcome, {{ user }}!</div>
      <div class="header-actions">
        <select v-model="language" class="item-select">
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
      <TaskModal
        v-if="showCreateForm"
        @createRequest=true
        @updateRequest=false
        @cancel="showCreateForm = false"
      />

      <h1 class="main-title">Task board</h1>
      <TaskStatusPanel
        v-if="canShowTaskStatuses"
        v-for="status in availableStatuses"
        :key="status"
        :status="status"
        :statusValue="statusDescriptions[status] || status"
      />
      <p v-else class="subtitle"> {{ unauthorizedUserMessage }} </p>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/axios'
import TaskModal from './TaskModal'
import TaskStatusPanel from './TaskStatusPanel'
import { hasTaskCreateAccess, extractUsername, hasTaskStatusViewAccess } from '@/utils/jwt'

const router = useRouter()
const language = ref('en')
const canCreateTask = ref(false)
const showCreateForm = ref(false)
const user = ref<string | null>(null)
const canShowTaskStatuses = ref(false)
const availableStatuses = ref<string[]>([])
const unauthorizedUserMessage = ref<string>('')

const statusDescriptions: Record<string, string> = {
  READY_FOR_DEVELOPMENT: 'Ready for development tasks',
  IN_DEVELOPMENT: 'Tasks in development progress',
  CODE_REVIEW: 'Waiting for code review approve',
  READY_FOR_TEST: 'Tasks ready for test',
  IN_TESTING: 'Tasks in testing',
  REOPEN: 'Reopened tasks',
  COMPLETED: 'Completed tasks'
}

onMounted(async () => {
  user.value = extractUsername()
  canCreateTask.value = hasTaskCreateAccess()
  canShowTaskStatuses.value = hasTaskStatusViewAccess()

  try {
    const response = await apiClient.get('/tasks/statuses')
    if (response.status === 200) {
      availableStatuses.value = response.data.statuses
    }
  }
  catch(exception: any) {
    if (exception.response.status === 403) {
      unauthorizedUserMessage.value = `
        No available tasks show for user with undefined role.
        Please contact Business analyst, Product owner or Scrum master
      `
    }
  }

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

.item-select {
  padding: 0.5rem;
  border-radius: 8px;
  border: 1px solid #cbd5e0;
  background: #f7fafc;
  font-size: 1rem;
}

.main-content {
  background: white;
  padding: 1rem;
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
</style>
