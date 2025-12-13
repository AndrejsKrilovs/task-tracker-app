<template>
  <div class="task-tracker-container">
    <AppHeader
      :user="user"
      :can-create-task="canCreateTask"
      :show-create-form="showCreateForm"
      v-model:language="language"
      @create="openCreateTask"
      @logout="logout"
      @mobileMenu="mobileMenuOpen = $event"
    />

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
        :modal-open-signal="modalOpenSignal"
        @openTask="openUpdateTask"
      />
      <p v-else class="subtitle">
        No available tasks show for user with undefined role. <br/>
        Please contact Business analyst, Product owner or Scrum master
      </p>
    </main>
  </div>

  <TaskModal
    v-if="showUpdateForm"
    :task="selectedTask!"
    @cancel="showUpdateForm = false"
    @submitted="showUpdateForm = false"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import apiClient from '@/api/axios'
import TaskModal from './TaskModal'
import TaskStatusPanel from './TaskStatusPanel'
import AppHeader from '@/components/AppHeader'
import { useUserStore } from '@/assets/store'

const router = useRouter()
const language = ref('en')
const modalOpenSignal = ref(0)
const userStore = useUserStore()
const showUpdateForm = ref(false)
const mobileMenuOpen = ref(false)
const showCreateForm = ref(false)
const selectedTask = ref<Task | null>(null)
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

const openCreateTask = () => {
  modalOpenSignal.value++
  showCreateForm.value = true
}

const openUpdateTask = (task: Task) => {
  modalOpenSignal.value++
  selectedTask.value = task
  showUpdateForm.value = true
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

.disabled-content {
  pointer-events: none;
  user-select: none;
  filter: blur(1px);
  opacity: 0.6;
}
</style>
