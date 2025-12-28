<template>
  <form class="profile-form" @submit.prevent="onSubmit">
    <h2 class="form-title">
      {{ isProfile ? '' : 'Create account' }}
    </h2>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div class="form-group">
      <label for="username">Username</label>
      <input
        id="username"
        v-model="form.username"
        type="text"
        :disabled="isProfile"
      />
      <p v-if="fieldErrors.username" class="field-error">
        {{ fieldErrors.username }}
      </p>
    </div>
    <div v-if="isProfile" class="form-group">
      <label for="name">Name</label>
      <input id="name" v-model="form.name" />
      <p v-if="fieldErrors.name" class="field-error">
        {{ fieldErrors.name }}
      </p>
    </div>
    <div v-if="isProfile" class="form-group">
      <label for="surname">Surname</label>
      <input id="surname" v-model="form.surname" />
      <p v-if="fieldErrors.surname" class="field-error">
        {{ fieldErrors.surname }}
      </p>
    </div>
    <div v-if="!isProfile" class="form-group">
      <label for="password">
        {{ isProfile ? 'New password (optional)' : 'Password' }}
      </label>
      <input
        id="password"
        v-model="form.password"
        type="password"
      />
      <p v-if="fieldErrors.password" class="field-error">
        {{ fieldErrors.password }}
      </p>
    </div>
    <div v-if="!isProfile" class="form-group">
      <label for="confirmPassword">Confirm password</label>
      <input
        id="confirmPassword"
        v-model="form.confirmPassword"
        type="password"
      />
      <p v-if="fieldErrors.confirmPassword" class="field-error">
        {{ fieldErrors.confirmPassword }}
      </p>
    </div>
    <div class="form-group">
      <label for="email">Email</label>
      <input id="email" v-model="form.email" />
      <p v-if="fieldErrors.email" class="field-error">
        {{ fieldErrors.email }}
      </p>
    </div>
    <div class="form-group">
      <label for="role">Role</label>
      <select
        v-model="form.role"
        class="item-select"
        :class="{ 'input-error': fieldErrors.role }"
        :disabled="isProfile && !canChangeProfileRole"
      >
        <option disabled :value="null">Select role</option>
        <option
          v-for="(label, value) in roleOptions"
          :key="value"
          :value="value"
        >
          {{ label }}
        </option>
      </select>
      <p v-if="fieldErrors.role" class="field-error">
        {{ fieldErrors.role }}
      </p>
    </div>
    <div class="form-group">
      <button :class="isProfile ? 'main-btn' : 'submit-btn'" type="submit">
        {{ isProfile ? 'Update profile' : 'Register' }}
      </button>
    </div>
    <RouterLink
      to="/login"
      class="submit-btn mt-2"
      custom
      v-slot="{ navigate }"
      v-if="!isProfile"
    >
      <button @click="navigate" class="submit-btn w-full">
        Login
      </button>
    </RouterLink>
  </form>
</template>

<script setup lang="ts">
import apiClient from '@/api/axios'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/assets/store'
import { ref, reactive, computed, watch } from 'vue'

const props = defineProps<{
  mode: 'register' | 'profile'
}>()

const roleOptions: Record<string, string> = {
  PRODUCT_OWNER: 'Product Owner',
  BUSINESS_ANALYST: 'Business Analyst',
  SCRUM_MASTER: 'Scrum Master',
  SOFTWARE_DEVELOPER: 'Software Developer',
  QA_SPECIALIST: 'QA Specialist'
}

const router = useRouter()
const userStore = useUserStore()
const errorMessage = ref<string | null>(null)
const isProfile = computed(() => props.mode === 'profile')
const fieldErrors = reactive<{ [key: string]: string }>({})
const form = reactive({
  username: null,
  name: null,
  surname: null,
  email: null,
  password: null,
  confirmPassword: null,
  role: null
})

const canChangeProfileRole = computed(() => {
  const permissions = userStore.user?.userPermissions
  return permissions ? permissions.some((item) => item === 'CAN_CHANGE_PROFILE_ROLE') : false
})

watch(
  () => userStore.user,
  (user) => {
    if (isProfile.value && user) {
      form.username = user.username
      form.email = user.email
      form.role = user.role
      form.password = null
      form.name = user.name ?? null
      form.surname = user.surname ?? null
    }
  },
  { immediate: true }
)

const onSubmit = async () => {
  errorMessage.value = null
  Object.keys(fieldErrors).forEach(key => delete fieldErrors[key])

  if (
    (!isProfile.value || form.password) &&
    form.password !== form.confirmPassword
  ) {
    fieldErrors.confirmPassword = 'Passwords do not match'
    return
  }

  try {
    const url = isProfile.value
      ? '/users/profile'
      : '/users/register'

		const payload: any = {
      username: form.username,
      password: form.password,
      email: form.email,
      role: form.role,
      name: form.name,
      surname: form.surname
    }

		const method = isProfile.value ? 'put' : 'post'
    const response = await apiClient[method](url, payload)
    if (response.status === 201) {
      alert(`User '${response.data.username}' successfully registered`)
      router.push('/login')
    }
    else {
      userStore.setUser(response.data)
      alert('Profile updated successfully')
    }
  }
  catch (exception: any) {
    if (exception.response.status === 409) {
      errorMessage.value = exception.response.data.message
    }

    if (exception.response.status === 400) {
      console.log(exception.response.data)
      const errorEntries = Object.entries(exception.response.data)
        .filter(([key, value]) => key.includes('message'))
        .flatMap(([key, value]) => Object.entries(value))

      errorEntries.forEach(([key, value]) => {
        if (key.includes('username')) fieldErrors.username = value as string
        if (key.includes('password')) fieldErrors.password = value as string
        if (key.includes('surname')) fieldErrors.surname = value as string
        if (key.includes('email')) fieldErrors.email = value as string
        if (key.includes('name')) fieldErrors.name = value as string
        if (key.includes('role')) fieldErrors.role = value as string
      })
    }
  }
}
</script>

<style scoped>
.profile-form {
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

.error-message {
  background-color: #fed7d7;
  color: #c53030;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-weight: 500;
  text-align: center;
  box-shadow: 0 0 0 1px #feb2b2;
}

.field-error {
  color: #e53e3e;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.input-error {
  border-color: #e53e3e;
  box-shadow: 0 0 0 1px rgba(229, 62, 62, 0.6);
}
</style>
