export function parseJwtPayload(token: string): any | null {
  try {
    const base64Url = token.split('.')[1]
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
    return null
  }
}

export function hasTaskCreateAccess(token: string): boolean {
  const payload = parseJwtPayload(token)
  const allowedRoles = ['BUSINESS_ANALYST', 'PRODUCT_OWNER', 'SCRUM_MASTER']
  if (!payload || !payload.groups) return false

  const groups = Array.isArray(payload.groups) ? payload.groups : [payload.groups]
  return groups.some((role: string) => allowedRoles.includes(role))
}

export function extractUsername(token: string): string | null {
  const payload = parseJwtPayload(token)
  return payload?.upn || null
}
