<template>
  <div class="accordion">
    <div
      class="accordion-header"
      :style="statusStyle"
      @click="isOpen = !isOpen"
    >
      <strong>{{ status }}</strong>
      <span>{{ isOpen ? '▲' : '▼' }}</span>
    </div>

    <div v-show="isOpen" class="accordion-body">
      <p class="empty-message">No tasks in this status.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

defineProps<{ status: string }>()

const isOpen = ref(false)

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
    backgroundColor: colorMap[status] || '#e2e8f0',
    color: '#2d3748',
    padding: '0.75rem 1rem'
  }
})
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
</style>
