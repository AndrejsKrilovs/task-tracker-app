<template>
  <div class="register-container">
    <UserForm mode="register" />
  </div>
</template>

<script setup lang="ts">
import UserForm from '@/components/UserForm'
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
</style>
