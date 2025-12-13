<template>
  <div class="actions" :class="{ column: mobile }">
    <select v-model="language" class="item-select" :class="{ 'full-width': fullWidth }">
      <option value="en">EN</option>
      <option value="ru">RU</option>
    </select>

    <button
      v-if="canCreateTask"
      class="main-btn"
      :class="{ 'full-width': fullWidth }"
      @click="$emit('create')"
    >
      Create New Task
    </button>

    <button
      class="main-btn"
      :class="{ 'full-width': fullWidth }"
      @click="$emit('logout')"
    >
      Logout
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  canCreateTask: boolean
  mobile?: boolean
  fullWidth?: boolean
}>()

defineEmits<{
  create: void
  logout: void
}>()

const language = defineModel<string>('language')
</script>

<style scoped>
.actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.column {
  flex-direction: column;
}

.full-width {
  width: 100%;
}
</style>