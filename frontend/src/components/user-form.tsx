import { useState, type SubmitEventHandler } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { $api } from '@/lib/api-client'
import type { components } from '@/generated/api'
import type { FlashMessage } from '@/components/flash-message'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

type User = components['schemas']['User']

interface Props {
  editingUser: User | null
  onSuccess: () => void
  onFlash: (flash: FlashMessage) => void
}

export function UserForm({ editingUser, onSuccess, onFlash }: Props) {
  const queryClient = useQueryClient()
  const [name, setName] = useState(editingUser?.name ?? '')
  const [email, setEmail] = useState(editingUser?.email ?? '')
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})

  const invalidateUsers = () => {
    queryClient.invalidateQueries($api.queryOptions('get', '/api/users'))
    onSuccess()
  }

  const createMutation = $api.useMutation('post', '/api/users', {
    onSuccess: () => {
      onFlash({ type: 'success', message: 'ユーザーを作成しました' })
      invalidateUsers()
    },
    onError: (error) => {
      const errors = extractFieldErrors(error)
      if (errors) {
        setFieldErrors(errors)
      } else {
        onFlash({ type: 'error', message: 'ユーザーの作成に失敗しました' })
      }
    },
  })

  const updateMutation = $api.useMutation('put', '/api/users/{id}', {
    onSuccess: () => {
      onFlash({ type: 'success', message: 'ユーザーを更新しました' })
      invalidateUsers()
    },
    onError: (error) => {
      const errors = extractFieldErrors(error)
      if (errors) {
        setFieldErrors(errors)
      } else {
        onFlash({ type: 'error', message: 'ユーザーの更新に失敗しました' })
      }
    },
  })

  const isPending = createMutation.isPending || updateMutation.isPending

  const handleSubmit: SubmitEventHandler<HTMLFormElement> = (e) => {
    e.preventDefault()
    if (isPending) return
    setFieldErrors({})
    const body = { name, email }
    if (editingUser?.id !== undefined) {
      updateMutation.mutate({ params: { path: { id: editingUser.id } }, body })
    } else {
      createMutation.mutate({ body })
    }
  }

  return (
    <form onSubmit={handleSubmit} className="border rounded-lg p-6 space-y-4">
      <h2 className="text-lg font-semibold">
        {editingUser ? 'ユーザー編集' : 'ユーザー作成'}
      </h2>
      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="name">名前</Label>
          <Input
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          {fieldErrors.name && (
            <p className="text-destructive text-sm">{fieldErrors.name}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="email">メールアドレス</Label>
          <Input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          {fieldErrors.email && (
            <p className="text-destructive text-sm">{fieldErrors.email}</p>
          )}
        </div>
      </div>
      <div className="flex gap-2">
        <Button type="submit" disabled={isPending}>
          {editingUser ? '更新' : '作成'}
        </Button>
        {editingUser && (
          <Button type="button" variant="outline" onClick={onSuccess}>
            キャンセル
          </Button>
        )}
      </div>
    </form>
  )
}

function extractFieldErrors(error: unknown): Record<string, string> | null {
  if (
    error !== null &&
    typeof error === 'object' &&
    'errors' in error &&
    Array.isArray((error as { errors: unknown }).errors)
  ) {
    const result: Record<string, string> = {}
    for (const err of (
      error as { errors: { field?: string; message?: string }[] }
    ).errors) {
      if (err.field && err.message) {
        result[err.field] = err.message
      }
    }
    if (Object.keys(result).length > 0) return result
  }
  return null
}
