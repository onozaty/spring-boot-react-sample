import { createFileRoute, Link } from '@tanstack/react-router'

export const Route = createFileRoute('/')({
  component: TopPage,
})

function TopPage() {
  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">ホーム</h1>
      <ul className="space-y-2">
        <li>
          <Link
            to="/users"
            className="text-primary underline underline-offset-4 hover:opacity-80"
          >
            ユーザー管理
          </Link>
        </li>
      </ul>
    </div>
  )
}
