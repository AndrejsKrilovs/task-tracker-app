<template>
  <div class="modal-overlay">
    <div class="submit-task-form">
      <h2 class="form-title">Submit task</h2>
      <form
        class="form-content"
        @submit.prevent="handleSubmit"
      >
        <div class="form-group">
          <label for="title">Title</label>
          <input id="title" v-model="form.title" type="text" />
          <p v-if="fieldError.title" class="field-error">{{ fieldError.title }}</p>
        </div>

        <div v-if="emit.updateRequest" class="form-group">
          <label for="status">Status</label>
          <select id="status" v-model="form.status" class="item-select">
            <option value="en">EN</option>
            <option value="ru">RU</option>
          </select>
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea id="description" v-model="form.description" rows="5"></textarea>
        </div>

        <div class="btn-group">
          <button class="main-btn" type="submit">Submit Task</button>
          <button class="main-btn" type="button" @click="emit('cancel')">Cancel</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import apiClient from '@/api/axios'

const emit = defineEmits<{
  cancel: void
  createRequest: boolean
  updateRequest: boolean
}>()

const isUpdate = computed(() => !!emit.updateRequest)
const fieldError = reactive<{ [key: string]: string }>({})

const form = reactive({
  id: null,
  title: null,
  description: null
})

async function handleSubmit() {
  if (isUpdate.value) {
    await updateTask()
  } else {
    await createTask()
  }
}

async function createTask() {
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

async function updateTask() {
  console.log(form)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.submit-task-form {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
  background-color: #fff;
  width: 90%;
  max-width: 500px;
}

.form-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
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
