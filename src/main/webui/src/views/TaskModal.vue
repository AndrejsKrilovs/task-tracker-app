<template>
	<Teleport to="body">
    <div class="modal-overlay" @click.self="emit('cancel')">
      <div class="submit-task-form">
        <h2 class="form-title">Submit task</h2>

        <form class="form-content" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="title">Title</label>
            <input
              id="title"
              v-model="form.title"
              type="text"
              :disabled="!canAddUpdateTask"
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
              :disabled="!canAddUpdateTask"
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
  </Teleport>
</template>

<script setup lang="ts">
import { Task } from '@/assets/types'
import { useUserStore } from '@/assets/store'
import { reactive, ref, onMounted, computed } from 'vue'
import apiClient from '@/api/axios'

const emit = defineEmits<{ cancel: void; submitted: void }>()
const props = defineProps<{ task: Task | {} }>()

const userStore = useUserStore()
const task = props.task as Task
const isUpdate = computed(() => !!task.id)
const canAddUpdateTask = computed(() => {
  const permissions = userStore.user?.userPermissions
  return permissions ? permissions.some((item) => item === 'CAN_CREATE_TASK') : false
})

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
  padding: 1rem;
  z-index: 999;
  overflow-y: auto;
}

.submit-task-form {
  background: #fff;
  padding: 2rem;
  border-radius: 14px;
  width: 100%;
  max-width: 500px;
  max-height: calc(100dvh - 2rem);
  box-sizing: border-box;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.item-select {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}

.form-title {
  font-size: 1.6rem;
  font-weight: 600;
  margin-bottom: 1rem;
  text-align: center;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 500;
  margin-bottom: 0.4rem;
}

.form-group input,
.form-group textarea {
  padding: 0.6rem 0.75rem;
  border: 1px solid #cbd5e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: 0.2s border-color;
}

.form-group input:focus,
.form-group textarea:focus,
.item-select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.25);
}

.btn-group {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-group button {
  flex: 1;
  padding: 0.75rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  color: white;
  font-weight: 600;
  font-size: 1rem;
  transition: background 0.2s ease;
}

.btn-group button:hover {
  background-color: #3182ce;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 480px) {
  .submit-task-form {
    padding: 1.25rem;
    border-radius: 12px;
    max-width: 95%;
    max-height: calc(100dvh - 1.5rem);
  }

  .form-title {
    font-size: 1.35rem;
    margin-bottom: 0.8rem;
  }

  .form-group label {
    font-size: 0.9rem;
  }

  .form-group input,
  .form-group textarea,
  .item-select {
    font-size: 0.9rem;
    padding: 0.55rem;
  }

  .btn-group button {
    padding: 0.65rem;
    font-size: 0.95rem;
  }
}

@media (max-width: 768px) {
  .submit-task-form {
    padding: 1.6rem;
    max-width: 420px;
  }

  .form-title {
    font-size: 1.45rem;
  }
}

@media (min-width: 1440px) {
  .submit-task-form {
    max-width: 550px;
    padding: 2.2rem;
  }

  .form-title {
    font-size: 1.75rem;
  }
}
</style>
