<template>
  <div class="login-container">
    <form class="login-form" @submit.prevent="handleLogin">
      <h2 class="form-title">Welcome form</h2>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div class="form-group">
        <label for="username">Username</label>
        <input id="username" v-model="form.username" type="text" />
        <p v-if="fieldErrors.username" class="field-error">{{ fieldErrors.username }}</p>
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input id="password" v-model="form.password" type="password" />
        <p v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</p>
      </div>
      <div class="form-group">
        <button class="submit-btn" type="submit">Login</button>
      </div>
      <RouterLink
        to="/register"
        class="submit-btn mt-2"
        custom
        v-slot="{ navigate }"
      >
        <button @click="navigate" class="submit-btn w-full">
          Register
        </button>
      </RouterLink>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/axios'

const errorMessage = ref<string | null>(null)
const fieldErrors = reactive<{ [key: string]: string }>({})
const router = useRouter()
const form = reactive({
  username: null,
  password: null
})

async function handleLogin() {
  errorMessage.value = null
  Object.keys(fieldErrors).forEach(key => delete fieldErrors[key])

  try {
    const { data } = await apiClient.post('/users/login', {
      username: form.username,
      password: form.password
    })

    localStorage.setItem('isAuthenticated', 'true')
    router.push('/tasks')
  }
  catch (exception: any) {
    if (exception.response.status === 401) {
      errorMessage.value = exception.response.data.message
    }

    if (exception.response.status === 400) {
      const errorEntries = Object.entries(exception.response.data)
        .filter(([key, value]) => key.includes('message'))
        .flatMap(([key, value]) => Object.entries(value))

      errorEntries.forEach(([key, value]) => {
        if (key.includes('username')) fieldErrors.username = value as string
        if (key.includes('password')) fieldErrors.password = value as string
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(145deg, #f0f4f8, #d9e2ec);
  padding: 1rem;
}

.login-form {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-title {
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #1a202c;
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2d3748;
}

.form-group input {
  padding: 0.5rem 0.75rem;
  border: 1px solid #cbd5e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.3);
}

.submit-btn {
  background-color: #4299e1;
  color: white;
  padding: 0.75rem;
  width: 100%;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #3182ce;
}
</style>