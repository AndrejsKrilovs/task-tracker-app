<template>
  <div class="task-card">
    <h3>{{ task.title }}</h3>
    <p class="status-label">
      <span>Created by {{ task.user }}</span><br/>
      <span>{{ task.createdAt}}</span>
    </p>
    <p class="description">{{ task.description }}</p>
    <div class="actions">
      <button>Change task status</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Task } from '@/assets/types'

const props = defineProps<{ task: Task }>()
</script>

<style scoped>
.task-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  width: 250px;
  flex: 0 0 auto;
}

.task-card h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
  color: #2d3748;
}

.status-label {
  color: #718096;
  font-style: italic;
  font-size: 1rem;
  margin: 0 0 0.5rem;
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

.actions button {
  background: #e2e8f0;
  color: #2d3748;
  border: none;
  border-radius: 4px;
  padding: 0.25rem 0.5rem;
  font-size: 0.85rem;
  cursor: pointer;
}

.actions button:hover {
  background: #cbd5e0;
}
</style>