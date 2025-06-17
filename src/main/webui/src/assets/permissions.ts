import { useRouter } from 'vue-router'

export function hasTaskCreateAccess(): boolean {
  return true
}

export function hasTaskUpdateAccess(): boolean {
  return true
}

export function hasTaskStatusViewAccess(): boolean {
  return true
}

export function extractUsername(): string | null {
  return 'Login from jwt'
}
