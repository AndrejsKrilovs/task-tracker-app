export interface Task {
  id: number
  title: string
  description: string
  status: string
  createdAt: string
  user: string
  assignTo: string
}

export interface User {
  createdAt: string
  email: string
  lastVisitAt: string
  role: string
  userPermissions: string[]
  username: string
  name: string
  surname: string
}