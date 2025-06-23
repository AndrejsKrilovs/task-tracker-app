import { User } from './types'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as User | null,
    isAuthenticated: false
  }),

  actions: {
    setUser(user: User) {
      this.user = user
    },

    authenticated() {
      this.isAuthenticated = true
    },

    clearUser() {
      this.user = null
      this.isAuthenticated = false
    }
  },

  persist: {
    enabled: true,
    strategies: [
      {
        storage: sessionStorage,
        paths: ['user']
      }
    ]
  }
})
