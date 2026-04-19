import { describe, expect, it } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { server } from '@/test/server'
import { renderRoute } from '@/test/test-utils'

describe('UserForm', () => {
  describe('新規作成', () => {
    it('フォームが空の状態で表示され、入力して送信できる', async () => {
      // Arrange
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/new'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toBeInTheDocument(),
      )
      expect(screen.getByLabelText('名前')).toHaveValue('')
      expect(screen.getByLabelText('メールアドレス')).toHaveValue('')
      expect(screen.getByRole('button', { name: '作成' })).toBeInTheDocument()

      await user.type(screen.getByLabelText('名前'), 'テストユーザー')
      await user.type(
        screen.getByLabelText('メールアドレス'),
        'test@example.com',
      )
      await user.type(screen.getByLabelText('パスワード'), 'password123')
      await user.click(screen.getByRole('button', { name: '作成' }))

      // Assert — 成功後にユーザー一覧に遷移し、成功 toast が表示される
      await waitFor(() => {
        expect(screen.getByText('ユーザー管理')).toBeInTheDocument()
      })
      expect(
        await screen.findByText('ユーザーを作成しました。'),
      ).toBeInTheDocument()
    })

    it('メールアドレス重複エラーが表示される', async () => {
      // Arrange
      server.use(
        http.post('*/api/users', () => {
          return HttpResponse.json(
            { title: 'Conflict', status: 409 },
            { status: 409 },
          )
        }),
      )
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/new'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toBeInTheDocument(),
      )
      await user.type(screen.getByLabelText('名前'), 'テスト')
      await user.type(
        screen.getByLabelText('メールアドレス'),
        'duplicate@example.com',
      )
      await user.type(screen.getByLabelText('パスワード'), 'password123')
      await user.click(screen.getByRole('button', { name: '作成' }))

      // Assert
      await waitFor(() => {
        expect(
          screen.getByText('このメールアドレスはすでに使用されています。'),
        ).toBeInTheDocument()
      })
    })

    it('バリデーションエラーが表示される', async () => {
      // Arrange
      server.use(
        http.post('*/api/users', () => {
          return HttpResponse.json(
            {
              title: 'Bad Request',
              status: 400,
              errors: [
                { field: 'email', message: 'メールアドレスの形式が不正です' },
              ],
            },
            { status: 400 },
          )
        }),
      )
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/new'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toBeInTheDocument(),
      )
      await user.type(screen.getByLabelText('名前'), 'テスト')
      await user.type(
        screen.getByLabelText('メールアドレス'),
        'invalid@example',
      )
      await user.type(screen.getByLabelText('パスワード'), 'password123')
      await user.click(screen.getByRole('button', { name: '作成' }))

      // Assert
      await waitFor(() => {
        expect(
          screen.getByText('メールアドレスの形式が不正です'),
        ).toBeInTheDocument()
      })
    })

    it('複数フィールドのバリデーションエラーが同時に表示される', async () => {
      // Arrange
      server.use(
        http.post('*/api/users', () => {
          return HttpResponse.json(
            {
              title: 'Bad Request',
              status: 400,
              errors: [
                { field: 'name', message: '名前は必須です' },
                { field: 'email', message: 'メールアドレスの形式が不正です' },
              ],
            },
            { status: 400 },
          )
        }),
      )
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/new'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toBeInTheDocument(),
      )
      await user.type(screen.getByLabelText('名前'), 'x')
      await user.type(
        screen.getByLabelText('メールアドレス'),
        'invalid@example',
      )
      await user.type(screen.getByLabelText('パスワード'), 'password123')
      await user.click(screen.getByRole('button', { name: '作成' }))

      // Assert
      await waitFor(() => {
        expect(screen.getByText('名前は必須です')).toBeInTheDocument()
        expect(
          screen.getByText('メールアドレスの形式が不正です'),
        ).toBeInTheDocument()
      })
    })

    it('予期せぬエラー時にエラー toast が表示される', async () => {
      // Arrange
      server.use(
        http.post('*/api/users', () => {
          return HttpResponse.json(
            { title: 'Internal Server Error', status: 500 },
            { status: 500 },
          )
        }),
      )
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/new'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toBeInTheDocument(),
      )
      await user.type(screen.getByLabelText('名前'), 'テスト')
      await user.type(
        screen.getByLabelText('メールアドレス'),
        'test@example.com',
      )
      await user.type(screen.getByLabelText('パスワード'), 'password123')
      await user.click(screen.getByRole('button', { name: '作成' }))

      // Assert
      expect(
        await screen.findByText('ユーザーの作成に失敗しました。'),
      ).toBeInTheDocument()
    })
  })

  describe('編集', () => {
    it('既存のユーザー情報が表示され、更新できる', async () => {
      // Arrange
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/1/edit'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toHaveValue('山田太郎'),
      )
      expect(screen.getByLabelText('メールアドレス')).toHaveValue(
        'yamada@example.com',
      )
      expect(screen.getByRole('button', { name: '更新' })).toBeInTheDocument()

      await user.clear(screen.getByLabelText('名前'))
      await user.type(screen.getByLabelText('名前'), '山田次郎')
      await user.click(screen.getByRole('button', { name: '更新' }))

      // Assert — 成功後にユーザー一覧に遷移し、成功 toast が表示される
      await waitFor(() => {
        expect(screen.getByText('ユーザー管理')).toBeInTheDocument()
      })
      expect(
        await screen.findByText('ユーザーを更新しました。'),
      ).toBeInTheDocument()
    })

    it('更新失敗時にエラー toast が表示される', async () => {
      // Arrange
      server.use(
        http.put('*/api/users/:id', () => {
          return HttpResponse.json(
            { title: 'Internal Server Error', status: 500 },
            { status: 500 },
          )
        }),
      )
      const user = userEvent.setup()
      renderRoute({ initialEntries: ['/users/1/edit'] })

      // Act
      await waitFor(() =>
        expect(screen.getByLabelText('名前')).toHaveValue('山田太郎'),
      )
      await user.clear(screen.getByLabelText('名前'))
      await user.type(screen.getByLabelText('名前'), '山田次郎')
      await user.click(screen.getByRole('button', { name: '更新' }))

      // Assert
      expect(
        await screen.findByText('ユーザーの更新に失敗しました。'),
      ).toBeInTheDocument()
    })
  })
})
