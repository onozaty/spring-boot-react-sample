import { describe, expect, it } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { server } from '@/test/server'
import { renderRoute } from '@/test/test-utils'

describe('UserList', () => {
  it('ユーザー一覧が表示される', async () => {
    // Act
    renderRoute({ initialEntries: ['/users'] })

    // Assert
    await waitFor(() => {
      expect(screen.getByText('山田太郎')).toBeInTheDocument()
      expect(screen.getByText('鈴木花子')).toBeInTheDocument()
      expect(screen.getByText('yamada@example.com')).toBeInTheDocument()
      expect(screen.getByText('suzuki@example.com')).toBeInTheDocument()
    })
  })

  it('読み込み中の表示がされる', async () => {
    // Arrange
    server.use(
      http.get('*/api/users', () => {
        return new Promise(() => {}) // 応答しない
      }),
    )

    // Act
    renderRoute({ initialEntries: ['/users'] })

    // Assert
    await waitFor(() => {
      expect(screen.getByText('読み込み中...')).toBeInTheDocument()
    })
  })

  it('エラー時にエラーメッセージが表示される', async () => {
    // Arrange
    server.use(
      http.get('*/api/users', () => {
        return HttpResponse.json(
            { title: 'Internal Server Error', status: 500 },
            { status: 500 },
          )
      }),
    )

    // Act
    renderRoute({ initialEntries: ['/users'] })

    // Assert
    await waitFor(() => {
      expect(
        screen.getByText('ユーザーの取得に失敗しました。'),
      ).toBeInTheDocument()
    })
  })

  it('削除ボタンをクリックすると確認ダイアログが表示される', async () => {
    // Arrange
    const user = userEvent.setup()
    renderRoute({ initialEntries: ['/users'] })
    await waitFor(() =>
      expect(screen.getByText('山田太郎')).toBeInTheDocument(),
    )

    // Act
    const deleteButtons = screen.getAllByRole('button', { name: '削除' })
    await user.click(deleteButtons[0])

    // Assert
    await waitFor(() => {
      expect(
        screen.getByText('ユーザーを削除しますか？'),
      ).toBeInTheDocument()
      expect(
        screen.getByText('山田太郎 を削除します。この操作は取り消せません。'),
      ).toBeInTheDocument()
    })
  })
})
