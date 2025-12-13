<template>
  <div class="task-tracker-container">
    <header class="header">
      <div class="welcome-text">
        Welcome, {{ user }}!
      </div>

      <div class="header-actions desktop-only">
        <select v-model="language" class="item-select">
          <option value="en">EN</option>
          <option value="ru">RU</option>
        </select>

        <button
          v-if="canCreateTask && !showCreateForm"
          class="main-btn"
          @click="showCreateForm = true"
        >
          Create New Task
        </button>

        <button class="main-btn" @click="logout">
          Logout
        </button>
      </div>

      <button class="burger-btn mobile-only" @click="mobileMenuOpen = !mobileMenuOpen">
        ☰
      </button>

      <div v-if="mobileMenuOpen" class="mobile-menu">
        <select v-model="language" class="item-select full-width">
          <option value="en">EN</option>
          <option value="ru">RU</option>
        </select>

        <button
          v-if="canCreateTask"
          class="main-btn full-width"
          @click="openCreateFromMobile"
        >
          Create New Task
        </button>

        <button class="main-btn full-width" @click="logout">
          Logout
        </button>
      </div>
    </header>

    <main class="main-content" :class="{ 'disabled-content': mobileMenuOpen }">
      <TaskModal
        v-if="showCreateForm"
        :task="{}"
        @cancel="showCreateForm = false"
      />

      <h1 class="main-title">Task board</h1>
      <TaskStatusPanel
        v-if="availableStatuses.length > 0"
        v-for="status in availableStatuses"
        :key="status"
        :status="status"
        :statusValue="statusDescriptions[status] || status"
      />
      <p v-else class="subtitle">
        No available tasks show for user with undefined role. <br/>
        Please contact Business analyst, Product owner or Scrum master
      </p>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import apiClient from '@/api/axios'
import TaskModal from './TaskModal'
import TaskStatusPanel from './TaskStatusPanel'
import { useUserStore } from '@/assets/store'

const router = useRouter()
const language = ref('en')
const userStore = useUserStore()
const mobileMenuOpen = ref(false)
const showCreateForm = ref(false)
const availableStatuses = ref<string[]>([])

const user = computed(() => userStore.user?.username ?? 'Guest')
const canCreateTask = computed(() => {
  const permissions = userStore.user?.userPermissions
  return permissions ? permissions.some((item) => item === 'CAN_CREATE_TASK') : false
})

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
  try {
    const { data } = await apiClient.get('/tasks/statuses')
    availableStatuses.value = data.statuses.filter((status: string) => status !== 'UNKNOWN')
  }
  catch(exception: any) {
    if (exception.response.status === 403) {
      availableStatuses.value = []
    }
  }

  router.replace('/tasks')
})

const logout = async () => {
  const response = await apiClient.get('/users/logout')
  if (response.status === 200) {
    userStore.clearUser()
    router.push('/logout')
  }
}

const openCreateFromMobile = () => {
  mobileMenuOpen.value = false
  showCreateForm.value = true
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
	position: relative;
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

.desktop-only {
  display: flex;
  gap: 1rem;
}

.mobile-only {
  display: none;
}

.burger-btn {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #2d3748;
}

.mobile-menu {
  position: absolute;
  top: 100%;
  right: 1rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 220px;
  z-index: 1000;
}

.full-width {
  width: 100%;
}

.disabled-content {
  pointer-events: none;
  user-select: none;
  filter: blur(1px);
  opacity: 0.6;
}

@media (max-width: 435px) {
  .desktop-only {
    display: none;
  }

  .mobile-only {
    display: block;
  }

  .header {
    padding: 0.75rem 1rem;
  }
}
</style>
