import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { $api } from '@/lib/api-client'
import { UserForm } from '@/components/user-form'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/users/$id/edit')({
  component: EditUserPage,
})

function EditUserPage() {
  const { id } = Route.useParams()
  const navigate = useNavigate()

  const userId = Number(id)
  const { data: user, isPending, isError } = $api.useQuery('get', '/api/users/{id}', {
    params: { path: { id: userId } },
  })

  if (isPending)
    return (
      <div className="max-w-4xl mx-auto p-8">
        <p className="text-muted-foreground">読み込み中...</p>
      </div>
    )

  if (isError || !user)
    return (
      <div className="max-w-4xl mx-auto p-8">
        <p className="text-destructive">ユーザーが見つかりません。</p>
      </div>
    )

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="flex items-center gap-4 mb-8">
        <Button variant="outline" size="sm" asChild>
          <Link to="/users">← 戻る</Link>
        </Button>
        <h1 className="text-3xl font-bold">ユーザー編集</h1>
      </div>
      <UserForm
        editingUser={user}
        onSuccess={() => navigate({ to: '/users' })}
      />
    </div>
  )
}
