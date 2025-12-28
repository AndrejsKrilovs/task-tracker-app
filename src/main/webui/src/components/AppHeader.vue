<template>
  <header class="header">
    <div class="welcome-text">
      Welcome, {{ user }}!
    </div>

    <div v-if="!isMobile" class="header-actions">
      <HeaderActions
        :can-create-task="canCreateTask"
        :show-create-form="showCreateForm"
        :view-mode="viewMode"
        v-model:language="language"
        @create="emit('create')"
        @logout="emit('logout')"
        @profile="emit('profile')"
        @tasks="emit('tasks')"
      />
    </div>

    <div v-else>
      <button class="burger-btn" @click="toggleMenu">
        ☰
      </button>

      <div v-if="mobileMenuOpen" class="mobile-menu">
        <HeaderActions
          mobile
          full-width
          :can-create-task="canCreateTask"
          :show-create-form="showCreateForm"
          :view-mode="viewMode"
          v-model:language="language"
          @create="handleMobileCreate"
          @logout="emit('logout')"
          @profile="emit('profile')"
          @tasks="emit('tasks')"
        />
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import HeaderActions from './HeaderActions.vue'

const emit = defineEmits<{
  create: void
  logout: void
  profile: void
  tasks: void
  mobileMenu(open: boolean): void
}>()

defineProps<{
  user: string
  canCreateTask: boolean
  showCreateForm: boolean
  viewMode: 'tasks' | 'profile'
}>()

const isMobile = ref(false)
const mobileMenuOpen = ref(false)
const language = defineModel<string>('language')

const toggleMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
  emit('mobileMenu', mobileMenuOpen.value)
}

const handleMobileCreate = () => {
  mobileMenuOpen.value = false
  emit('mobileMenu', false)
  emit('create')
}

const checkScreen = () => {
  isMobile.value = window.innerWidth <= 435
}

onMounted(() => {
  checkScreen()
  window.addEventListener('resize', checkScreen)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkScreen)
})
</script>

<style scoped>
.header {
	position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 1rem 2rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  margin-bottom: 2rem;
}

.welcome-text {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d3748;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.item-select {
  padding: 0.5rem;
  border-radius: 8px;
  border: 1px solid #cbd5e0;
  background: #f7fafc;
  font-size: 1rem;
}

.desktop-only {
  display: flex;
  gap: 1rem;
}

.mobile-only {
  display: none;
}

.burger-btn {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #2d3748;
}

.mobile-menu {
  position: absolute;
  top: 100%;
  right: 1rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 220px;
  z-index: 1000;
}

@media (max-width: 435px) {
  .desktop-only {
    display: none;
  }

  .mobile-only {
    display: block;
  }

  .header {
    padding: 0.75rem 1rem;
  }
}
</style>