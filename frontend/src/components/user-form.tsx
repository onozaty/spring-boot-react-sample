import { useState, type SubmitEventHandler } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { $api } from '@/lib/api-client'
import type { components } from '@/generated/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

type User = components['schemas']['User']

interface Props {
  editingUser: User | null
  onSuccess: () => void
}

export function UserForm({ editingUser, onSuccess }: Props) {
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
      toast.success('ユーザーを作成しました')
      invalidateUsers()
    },
    onError: (error) => {
      const errors = extractFieldErrors(error)
      if (errors) {
        setFieldErrors(errors)
      } else {
        toast.error('ユーザーの作成に失敗しました')
      }
    },
  })

  const updateMutation = $api.useMutation('put', '/api/users/{id}', {
    onSuccess: () => {
      toast.success('ユーザーを更新しました')
      invalidateUsers()
    },
    onError: (error) => {
      const errors = extractFieldErrors(error)
      if (errors) {
        setFieldErrors(errors)
      } else {
        toast.error('ユーザーの更新に失敗しました')
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
      <Button type="submit" disabled={isPending}>
        {editingUser ? '更新' : '作成'}
      </Button>
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
