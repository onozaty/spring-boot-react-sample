import createClient from 'openapi-fetch'
import createQueryClient from 'openapi-react-query'
import type { paths } from '../generated/api'

const client = createClient<paths>({
  baseUrl: window.location.origin,
  // globalThis.fetch をキャプチャせず毎回解決する
  // （fetch を差し替えるミドルウェアに対応するため）
  fetch: (...args) => globalThis.fetch(...args),
})

client.use({
  async onResponse({ response, request }) {
    if (
      response.status === 401 &&
      !request.url.endsWith('/api/auth/me') &&
      !window.location.pathname.startsWith('/login')
    ) {
      window.location.href = '/login'
    }
    return response
  },
})

export const $api = createQueryClient(client)
export { client }
