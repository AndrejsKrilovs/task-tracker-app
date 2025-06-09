<template>
  <div class="create-task-form">
    <h2 class="form-title">Create New Task</h2>
    <form @submit.prevent="submitTask">
      <div class="form-group">
        <label for="title">Title</label>
        <input id="title" v-model="task.title" type="text" />
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" v-model="task.description" rows="5"></textarea>
      </div>

      <div class="btn-group">
        <button class="main-btn" type="submit">Add Task</button>
        <button class="main-btn" type="button" @click="$emit('cancel')">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import apiClient from '@/api/axios'

const task = reactive({
  title: '',
  description: ''
})

async function submitTask() {
  console.log('Task to submit')
}
</script>

<style scoped>
.create-task-form {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.form-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.form-group input,
.form-group textarea {
  padding: 0.5rem;
  border: 1px solid #cbd5e0;
  border-radius: 8px;
  font-size: 1rem;
}

.btn-group {
  display: flex;
  justify-content: flex-end; /* или space-between / center по желанию */
  gap: 1rem;
  margin-top: 1rem;
}

.btn-group button {
  flex: 1;
}
</style>
