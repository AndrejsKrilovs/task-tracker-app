<template>
	<div class="profile-layout">
    <section class="profile-form">
      <UserForm mode="profile" />
    </section>
    <section v-show="false" class="profile-stats">
       <p class="subtitle">Statistics profile page (coming next)</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import UserForm from '@/components/UserForm'
</script>

<style scoped>
.profile-layout {
  display: grid;
  grid-template-columns: clamp(320px, 28%, 420px) 1fr;
  gap: 2rem;
  align-items: start;
}

.profile-form {
  width: 95%;
}

.profile-stats {
  background: #f8fafc;
  border-radius: 14px;
  padding: 1.5rem;
  min-height: 320px;
}

@media (max-width: 767px) {
  .profile-layout {
	  grid-template-columns: clamp(320px, 28%, 420px) 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 435px) {
  .profile-layout {
    grid-template-columns: 1fr;
    gap: 1.25rem;
  }

  .profile-form {
    width: 90%;
  }
}
</style>
