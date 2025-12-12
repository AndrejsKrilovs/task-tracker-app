<template>
  <div class="task-card" @click="showUpdateForm = true">
    <h3>{{ task.title }}</h3>
    <p class="subtitle">
      <span>Last modified by {{ task.user }}</span><br/>
      <span>{{ task.modifiedAt ?? task.createdAt }}</span>
    </p>
    <p class="description">{{ task.description }}</p>
  </div>

  <TaskModal
    v-if="showUpdateForm"
    :task="props.task"
    @cancel="showUpdateForm = false"
    @submitted="handleSubmitted"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Task } from '@/assets/types'
import TaskModal from './TaskModal'

const emit = defineEmits<{ submitted: void }>()
const props = defineProps<{ task: Task }>()
const showUpdateForm = ref(false)

function handleSubmitted() {
  showUpdateForm.value = false
  emit('submitted')
}
</script>

<style scoped>
.task-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  width: 230px;
  flex: 0 0 auto;
}

.task-card h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
  color: #2d3748;
}

.subtitle {
  color: #4a5568;
}

.description {
  font-size: 0.95rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
</style>