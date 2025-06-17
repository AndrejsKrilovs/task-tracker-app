<template>
  <div class="modal-overlay">
    <div class="submit-task-form">
      <h2 class="form-title">Submit task</h2>

      <form class="form-content" @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="title">Title</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            :disabled="!canUpdateTask"
            placeholder="Enter task title"
          />
          <p v-if="fieldError.title" class="field-error">
            {{ fieldError.title }}
          </p>
        </div>

        <div v-if="props.task.status" class="form-group">
          <label for="status">Status</label>
          <select
            id="status"
            v-model="form.status"
            class="item-select"
          >
            <option
              v-for="status in taskStatusesFromApi"
              :key="status"
              :value="status"
            >
              {{ status }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea
            id="description"
            v-model="form.description"
            rows="5"
            :disabled="!canUpdateTask"
            placeholder="Enter task details"
          />
        </div>

        <div class="btn-group">
          <button class="main-btn" type="submit">
            Submit Task
          </button>
          <button
            class="main-btn"
            type="button"
            @click="emit('cancel')"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Task } from '@/api/types'
import { hasTaskUpdateAccess } from '@/utils/jwt'
import { reactive, ref, onMounted, computed } from 'vue'
import apiClient from '@/api/axios'

const emit = defineEmits<{ cancel: void; submitted: void }>()
const props = defineProps<{ task: Task | {} }>()

const task = props.task as Task
const isUpdate = computed(() => !!task.id)

const canUpdateTask = ref(false)
const fieldError = reactive<{ [key: string]: string }>({})
const taskStatusesFromApi = ref<string[]>([])

const form = reactive({
  id: task.id || '',
  title: task.title || '',
  status: task.status || '',
  description: task.description || ''
})

onMounted(async () => {
  if (task.status) {
    canUpdateTask.value = hasTaskUpdateAccess()
    const { data } = await apiClient.get(`/tasks/statusesToChange/${task.status}`)
    taskStatusesFromApi.value = [task.status, ...data.statuses]
  }
})

function isUnchanged(): boolean {
  return (
    task.title === form.title.trim() &&
    task.status === form.status &&
    task.description === form.description.trim()
  )
}

function handleSubmit() {
  if (isUpdate.value && !isUnchanged()) {
    updateTask()
  }
  if (isUnchanged()) {
    emit('cancel')
  }
  if (!isUpdate.value) {
    createTask()
  }
}

async function createTask() {
  try {
    const { data } = await apiClient.post('/tasks/create', {
      title: form.title.trim(),
      description: form.description?.trim()
    })

    emit('submitted')
    alert(`Task '${data.title}' created`)
    resetForm()
    emit('cancel')
  }
  catch (exception: any) {
    handleValidationError(exception)
  }
}

async function updateTask() {
  try {
    const { data } = await apiClient.put(`/tasks/update/${task.id}`, {
      title: form.title.trim(),
      description: form.description?.trim(),
      status: form.status
    })

    emit('submitted')
    alert(`Task '${data.title}' updated`)
    resetForm()
    emit('cancel')
  }
  catch (exception: any) {
    handleValidationError(exception)
  }
}

function resetForm() {
  form.title = ''
  form.description = ''
  form.status = ''
}

function handleValidationError(exception: any) {
  if (exception.response?.status === 400) {
    const errorEntry = Object.entries(exception.response.data)
      .filter(([key]) => key.includes('message'))
      .flatMap(([, value]) => Object.entries(value))
      .find(([key]) => key.includes('title'))

    if (errorEntry) {
      const [key, value] = errorEntry
      fieldError.title = value as string
    }
  }
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
