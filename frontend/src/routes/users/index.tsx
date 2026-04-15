import { createFileRoute, Link } from '@tanstack/react-router'
import { UserList } from '@/components/user-list'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/users/')({
  component: UsersPage,
})

function UsersPage() {
  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-bold">ユーザー管理</h1>
        <Button asChild>
          <Link to="/users/new">新規作成</Link>
        </Button>
      </div>
      <UserList />
    </div>
  )
}
