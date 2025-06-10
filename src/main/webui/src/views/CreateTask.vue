<template>
  <div class="create-task-form">
    <h2 class="form-title">Create New Task</h2>
    <form @submit.prevent="submitTask">
      <div class="form-group">
        <label for="title">Title</label>
        <input id="title" v-model="form.title" type="text" />
        <p v-if="fieldError.title" class="field-error">{{ fieldError.title }}</p>
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" v-model="form.description" rows="5"></textarea>
      </div>

      <div class="btn-group">
        <button class="main-btn" type="submit">Add Task</button>
        <button class="main-btn" type="button" @click="emit('cancel')">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import apiClient from '@/api/axios'

const emit = defineEmits(['cancel'])
const fieldError = reactive<{ [key: string]: string }>({})

const form = reactive({
  id: null,
  title: null,
  description: null
})

async function submitTask() {
  try {
    const response = await apiClient.post('/tasks/create', {
      id: form.id,
      title: form.title,
      description: form.description
    })

    if (response.status === 201) {
      alert(`Task '${response.data.title}' created`)
      form.title = null
      form.description = null
      emit('cancel')
    }
  }
  catch(exception: any) {
    if (exception.response.status === 400) {
      const errorEntry = Object.entries(exception.response.data)
        .filter(([key, value]) => key.includes('message'))
        .flatMap(([key, value]) => Object.entries(value))
        .find(([key, value]) => key.includes('title'))

      const [key, value] = errorEntry
      fieldError.title = value as string
    }
  }
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
