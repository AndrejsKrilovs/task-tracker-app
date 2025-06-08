<template>
  <div class="register-container">
    <form class="register-form" @submit.prevent="onSubmit">
      <h2 class="form-title">Create Account</h2>
      <div class="form-group">
        <label for="username">Username</label>
        <input id="username" v-model="form.username" type="text" required />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input id="password" v-model="form.password" type="password" required />
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input id="email" v-model="form.email" type="email" />
      </div>
      <div class="form-group">
        <label for="role">Role</label>
        <select v-model="form.role" class="role-select">
          <option :value="null">Default role</option>
          <option
            v-for="(label, value) in roleOptions"
            :key="value"
            :value="value"
          >
            {{ label }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <button class="submit-btn" type="submit">Register</button>
      </div>
      <RouterLink
        to="/login"
        class="submit-btn mt-2"
        custom
        v-slot="{ navigate }"
      >
        <button @click="navigate" class="submit-btn w-full">
          Login
        </button>
      </RouterLink>

    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/axios'

const roleOptions: Record<string, string> = {
  PRODUCT_OWNER: 'Product Owner',
  BUSINESS_ANALYST: 'Business Analyst',
  SCRUM_MASTER: 'Scrum Master',
  SOFTWARE_DEVELOPER: 'Software Developer',
  QA_SPECIALIST: 'QA Specialist'
}

const router = useRouter()
const form = reactive({
  username: null,
  email: null,
  password: null,
  role: null
})

async function onSubmit() {
  const response = await apiClient.post('/users/register', {
    username: form.username,
    password: form.password,
    email: form.email,
    role: form.role
  })

  if (response.status === 201) {
    alert(`User '${response.data.username}' successfully registered`)
    router.push('/login')
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(145deg, #f0f4f8, #d9e2ec);
  padding: 1rem;
}

.register-form {
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

.role-select {
  padding: 0.5rem;
  border-radius: 8px;
  border: 1px solid #cbd5e0;
  background: #f7fafc;
  font-size: 1rem;
}
</style>
