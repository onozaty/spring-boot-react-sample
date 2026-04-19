import { describe, expect, it } from 'vitest'
import { screen, waitFor, within } from '@testing-library/react'
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
      expect(screen.getByText('ユーザーを削除しますか？')).toBeInTheDocument()
      expect(
        screen.getByText('山田太郎 を削除します。この操作は取り消せません。'),
      ).toBeInTheDocument()
    })
  })

  it('ユーザーが存在しない場合、ヘッダのみ表示され行はない', async () => {
    // Arrange
    server.use(
      http.get('*/api/users', () => {
        return HttpResponse.json([])
      }),
    )

    // Act
    renderRoute({ initialEntries: ['/users'] })

    // Assert
    await waitFor(() => {
      expect(screen.getByText('名前')).toBeInTheDocument()
    })
    expect(screen.queryByText('山田太郎')).not.toBeInTheDocument()
    expect(screen.queryByText('鈴木花子')).not.toBeInTheDocument()
  })

  it('削除を確定すると一覧から消え、成功 toast が表示される', async () => {
    // Arrange — DELETE 後に GET が空を返すよう差し替え
    const singleUser = {
      id: 1,
      name: '山田太郎',
      email: 'yamada@example.com',
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
    }
    let deleted = false
    server.use(
      http.get('*/api/users', () => {
        return HttpResponse.json(deleted ? [] : [singleUser])
      }),
      http.delete('*/api/users/:id', () => {
        deleted = true
        return new HttpResponse(null, { status: 204 })
      }),
    )
    const user = userEvent.setup()
    renderRoute({ initialEntries: ['/users'] })
    await waitFor(() =>
      expect(screen.getByText('山田太郎')).toBeInTheDocument(),
    )

    // Act
    await user.click(screen.getByRole('button', { name: '削除' }))
    const dialog = await screen.findByRole('alertdialog')
    await user.click(within(dialog).getByRole('button', { name: '削除' }))

    // Assert
    expect(
      await screen.findByText('ユーザーを削除しました。'),
    ).toBeInTheDocument()
    await waitFor(() => {
      expect(screen.queryByText('山田太郎')).not.toBeInTheDocument()
    })
  })

  it('削除失敗時にエラー toast が表示される', async () => {
    // Arrange
    server.use(
      http.delete('*/api/users/:id', () => {
        return HttpResponse.json(
          { title: 'Internal Server Error', status: 500 },
          { status: 500 },
        )
      }),
    )
    const user = userEvent.setup()
    renderRoute({ initialEntries: ['/users'] })
    await waitFor(() =>
      expect(screen.getByText('山田太郎')).toBeInTheDocument(),
    )

    // Act
    const deleteButtons = screen.getAllByRole('button', { name: '削除' })
    await user.click(deleteButtons[0])
    const dialog = await screen.findByRole('alertdialog')
    await user.click(within(dialog).getByRole('button', { name: '削除' }))

    // Assert
    expect(
      await screen.findByText('ユーザーの削除に失敗しました。'),
    ).toBeInTheDocument()
  })
})
