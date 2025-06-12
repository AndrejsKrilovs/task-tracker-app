import { useRouter } from 'vue-router'

function parseJwtPayload(): any | null {
  try {
    const item = localStorage.getItem('loginResponse')
    const base64Url = JSON.parse(item).token.split('.')[1]
    if (!base64Url) return null

    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    )

    return JSON.parse(jsonPayload)
  } catch (e) {
    console.error('Invalid JWT:', e)
    const router = useRouter()
    router.replace('/tasks')
    return null
  }
}

export function hasTaskCreateAccess(): boolean {
  const payload = parseJwtPayload()
  const allowedRoles = ['BUSINESS_ANALYST', 'PRODUCT_OWNER', 'SCRUM_MASTER']
  if (!payload || !payload.groups) return false

  const groups = Array.isArray(payload.groups) ? payload.groups : [payload.groups]
  return groups.some((role: string) => allowedRoles.includes(role))
}

export function hasTaskStatusViewAccess(): boolean {
  const payload = parseJwtPayload()
  const allowedRoles = ['BUSINESS_ANALYST', 'PRODUCT_OWNER', 'SCRUM_MASTER', 'SOFTWARE_DEVELOPER', 'QA_SPECIALIST']
  if (!payload || !payload.groups) return false

  const groups = Array.isArray(payload.groups) ? payload.groups : [payload.groups]
  return groups.some((role: string) => allowedRoles.includes(role))
}

export function extractUsername(): string | null {
  const payload = parseJwtPayload()
  return payload?.upn || null
}
