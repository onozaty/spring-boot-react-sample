import { Link } from '@tanstack/react-router'
import { useAuth } from '@/hooks/use-auth'
import { Button } from '@/components/ui/button'

export function AppHeader() {
  const { user, logout } = useAuth()

  return (
    <header className="border-b px-8 py-3 flex items-center justify-between">
      <span className="font-semibold">サンプルアプリ</span>
      {user && (
        <div className="flex items-center gap-4">
          <span className="text-sm text-muted-foreground">{user.name}</span>
          <Button variant="ghost" size="sm" asChild>
            <Link to="/account/password">パスワード変更</Link>
          </Button>
          <Button variant="outline" size="sm" onClick={() => logout()}>
            ログアウト
          </Button>
        </div>
      )}
    </header>
  )
}
