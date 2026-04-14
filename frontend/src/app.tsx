import { useCallback, useState } from 'react'
import {
  FlashMessage,
  type FlashMessage as FlashMessageType,
} from '@/components/flash-message'
import { UserForm } from '@/components/user-form'
import { UserList } from '@/components/user-list'
import type { components } from '@/generated/api'

type User = components['schemas']['User']

export default function App() {
  const [editingUser, setEditingUser] = useState<User | null>(null)
  const [flash, setFlash] = useState<FlashMessageType | null>(null)

  const showFlash = useCallback((f: FlashMessageType) => setFlash(f), [])
  const dismissFlash = useCallback(() => setFlash(null), [])

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">ユーザー管理</h1>
      <FlashMessage flash={flash} onDismiss={dismissFlash} />
      <UserForm
        key={editingUser?.id ?? 'new'}
        editingUser={editingUser}
        onSuccess={() => setEditingUser(null)}
        onFlash={showFlash}
      />
      <UserList onEdit={setEditingUser} onFlash={showFlash} />
    </div>
  )
}
