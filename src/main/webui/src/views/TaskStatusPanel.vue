<template>
  <div class="accordion">
    <div
      class="accordion-header"
      :style="statusStyle"
      @click="statusSelect"
    >
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
      <p v-else class="empty-message">No tasks in this status.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Task } from '@/assets/types'
import TaskCard from './TaskCard'
import apiClient from '@/api/axios'

const isOpen = ref(false)
const tasksForStatus = ref<Task[]>([])
const props = defineProps<{ statusValue: string, status: string }>()

const statusStyle = computed(() => {
  const colorMap: Record<string, string> = {
    READY_FOR_DEVELOPMENT: '#edf2ff',
    IN_DEVELOPMENT: '#e6fffa',
    CODE_REVIEW: '#fff5f5',
    READY_FOR_TEST: '#faf089',
    IN_TESTING: '#f0fff4',
    REOPEN: '#fef2f2',
    COMPLETED: '#c6f6d5',
    UNKNOWN: '#e2e8f0'
  }
  return {
    backgroundColor: colorMap[props.status] || '#e2e8f0',
    color: '#2d3748',
    padding: '0.75rem 1rem'
  }
})

async function statusSelect() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    await apiClient.get(`/tasks/${props.status}`)
      .then(response => response.data)
      .then(json => tasksForStatus.value = json.tasks)
  }
  if (!isOpen.value) {
    tasksForStatus.value = []
  }
}
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

.empty-message {
  color: #718096;
  font-style: italic;
}

.task-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 1rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.task-list::-webkit-scrollbar {
    height: 6px;
}

.task-list::-webkit-scrollbar-thumb {
    background: #cbd5e0;
    border-radius: 4px;
}
</style>
