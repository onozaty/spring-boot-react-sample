import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { UserForm } from '@/components/user-form'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/users/new')({
  component: NewUserPage,
})

function NewUserPage() {
  const navigate = useNavigate()

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="flex items-center gap-4 mb-8">
        <Button variant="outline" size="sm" asChild>
          <Link to="/users">← 戻る</Link>
        </Button>
        <h1 className="text-3xl font-bold">ユーザー作成</h1>
      </div>
      <UserForm
        editingUser={null}
        onSuccess={() => navigate({ to: '/users' })}
      />
    </div>
  )
}
