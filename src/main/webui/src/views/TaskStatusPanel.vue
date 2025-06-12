<template>
  <div class="accordion">
    <div class="accordion-header" :style="statusStyle" @click="statusSelect">
      <strong>{{ statusValue }}</strong>
      <span>{{ isOpen ? '▲' : '▼' }}</span>
    </div>

    <div v-show="isOpen" class="accordion-body">
      <div v-if="tasksForStatus.length > 0" class="task-list">
        <TaskCard
          v-for="task in tasksForStatus"
          :key="task.id"
          :task="task"
        />
      </div>
      <p v-else class="subtitle">No tasks in this status.</p>

      <div v-if="totalPages > 1" class="pagination">
        <button @click="currentPage--" :disabled="currentPage === 1">Prev</button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="currentPage++" :disabled="currentPage === totalPages">Next</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Task } from '@/assets/types'
import TaskCard from './TaskCard'
import apiClient from '@/api/axios'

const props = defineProps<{ statusValue: string; status: string }>()

const isOpen = ref(false)
const totalTasks = ref(0)
const currentPage = ref(1)
const cardsPerPage = ref(5)
const tasksForStatus = ref<Task[]>([])

const statusStyle = computed(() => {
  const colorMap: Record<string, string> = {
    READY_FOR_DEVELOPMENT: '#edf2ff',
    IN_DEVELOPMENT: '#e6fffa',
    CODE_REVIEW: '#fff5f5',
    READY_FOR_TEST: '#faf089',
    IN_TESTING: '#f0fff4',
    REOPEN: '#fef2f2',
    COMPLETED: '#c6f6d5'
  }
  return {
    backgroundColor: colorMap[props.status] || '#e2e8f0',
    color: '#2d3748',
    padding: '0.75rem 1rem'
  }
})

function updateCardsPerPage() {
  const cssValue = getComputedStyle(document.documentElement).getPropertyValue('--cards-per-page')
  cardsPerPage.value = parseInt(cssValue.trim()) || 5
}

onMounted(() => {
  updateCardsPerPage()
  window.addEventListener('resize', updateCardsPerPage)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateCardsPerPage)
})

async function fetchTasks() {
  const offset = (currentPage.value - 1) * cardsPerPage.value
  const limit = cardsPerPage.value

  const { data } = await apiClient.get(`/tasks/${props.status}`, {
    params: { offset, limit }
  })

  tasksForStatus.value = data.tasks
  totalTasks.value = data.tasksCount || 0
}

async function statusSelect() {
  isOpen.value = !isOpen.value

  if (isOpen.value) {
    currentPage.value = 1
    await fetchTasks()
  }
  else {
    tasksForStatus.value = []
  }
}

watch(currentPage, async () => {
  if (isOpen.value)
    await fetchTasks()
})

const totalPages = computed(() =>
  Math.ceil(totalTasks.value / cardsPerPage.value)
)
</script>

<style scoped>
.accordion {
  border: 1px solid #cbd5e0;
  border-radius: 8px;
  margin-bottom: 1rem;
  background: #f9fafb;
}

.accordion-header {
  font-weight: 600;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
}

.accordion-body {
  padding: 1rem;
  background: white;
}

.task-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 0.5rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: none;
  background-color: #edf2f7;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 500;
}

.pagination button:disabled {
  background-color: #e2e8f0;
  cursor: not-allowed;
}
</style>
