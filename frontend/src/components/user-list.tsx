import { useQueryClient } from '@tanstack/react-query'
import { $api } from '@/lib/api-client'
import type { components } from '@/generated/api'
import type { FlashMessage } from '@/components/flash-message'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

type User = components['schemas']['User']

interface Props {
  onEdit: (user: User) => void
  onFlash: (flash: FlashMessage) => void
}

export function UserList({ onEdit, onFlash }: Props) {
  const queryClient = useQueryClient()

  const { data: users, isPending, isError } = $api.useQuery('get', '/api/users')

  const deleteMutation = $api.useMutation('delete', '/api/users/{id}', {
    onSuccess: () => {
      queryClient.invalidateQueries($api.queryOptions('get', '/api/users'))
      onFlash({ type: 'success', message: 'ユーザーを削除しました' })
    },
    onError: () => {
      onFlash({ type: 'error', message: 'ユーザーの削除に失敗しました' })
    },
  })

  if (isPending)
    return <p className="mt-8 text-muted-foreground">読み込み中...</p>
  if (isError)
    return (
      <p className="mt-8 text-destructive">ユーザーの取得に失敗しました。</p>
    )

  const handleDelete = (userId: number | undefined) => {
    if (userId === undefined) return
    deleteMutation.mutate({ params: { path: { id: userId } } })
  }

  return (
    <div className="mt-8">
      <h2 className="text-xl font-semibold mb-4">ユーザー一覧</h2>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>名前</TableHead>
            <TableHead>メールアドレス</TableHead>
            <TableHead>作成日時</TableHead>
            <TableHead />
          </TableRow>
        </TableHeader>
        <TableBody>
          {users?.map((user) => (
            <TableRow key={user.id}>
              <TableCell>{user.id}</TableCell>
              <TableCell>{user.name}</TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>{user.createdAt}</TableCell>
              <TableCell className="text-right space-x-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={deleteMutation.isPending}
                  onClick={() => onEdit(user)}
                >
                  編集
                </Button>
                <Button
                  variant="destructive"
                  size="sm"
                  disabled={deleteMutation.isPending}
                  onClick={() => handleDelete(user.id)}
                >
                  削除
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
