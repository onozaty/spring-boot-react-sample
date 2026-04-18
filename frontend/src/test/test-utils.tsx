import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import {
  createMemoryHistory,
  createRouter,
  RouterProvider,
} from '@tanstack/react-router'
import { render, type RenderOptions } from '@testing-library/react'
import { routeTree } from '../routeTree.gen'

interface Options extends Omit<RenderOptions, 'wrapper'> {
  initialEntries?: string[]
}

export function renderRoute(options: Options = {}) {
  const { initialEntries = ['/'], ...renderOptions } = options

  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        gcTime: 0,
      },
      mutations: {
        retry: false,
      },
    },
  })

  const router = createRouter({
    routeTree,
    history: createMemoryHistory({ initialEntries }),
    defaultPendingMinMs: 0,
  })

  const result = render(
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>,
    renderOptions,
  )

  return { ...result, router, queryClient }
}
