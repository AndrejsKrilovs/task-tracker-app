<template>
  <main>
    <h1>Hello from Vue 3 + TypeScript!</h1>
    <button @click="count++">Count is {{ count }}</button>
  </main>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const count = ref(0)
</script>

<style>
main {
  text-align: center;
  padding: 2rem;
  font-family: sans-serif;
}
</style>
