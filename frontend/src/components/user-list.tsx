import { useQueryClient } from '@tanstack/react-query'
import { Link } from '@tanstack/react-router'
import { toast } from 'sonner'
import { $api } from '@/lib/api-client'
import type { components } from '@/generated/api'
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

export function UserList() {
  const queryClient = useQueryClient()

  const { data: users, isPending, isError } = $api.useQuery('get', '/api/users')

  const deleteMutation = $api.useMutation('delete', '/api/users/{id}', {
    onSuccess: () => {
      queryClient.invalidateQueries($api.queryOptions('get', '/api/users'))
      toast.success('ユーザーを削除しました')
    },
    onError: () => {
      toast.error('ユーザーの削除に失敗しました')
    },
  })

  if (isPending)
    return <p className="mt-8 text-muted-foreground">読み込み中...</p>
  if (isError)
    return (
      <p className="mt-8 text-destructive">ユーザーの取得に失敗しました。</p>
    )

  const handleDelete = (user: User) => {
    if (user.id === undefined) return
    deleteMutation.mutate({ params: { path: { id: user.id } } })
  }

  return (
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
              <Button variant="outline" size="sm" asChild>
                <Link to="/users/$id/edit" params={{ id: String(user.id) }}>
                  編集
                </Link>
              </Button>
              <Button
                variant="destructive"
                size="sm"
                disabled={deleteMutation.isPending}
                onClick={() => handleDelete(user)}
              >
                削除
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
