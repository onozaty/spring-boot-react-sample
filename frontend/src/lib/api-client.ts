import createClient from 'openapi-fetch'
import createQueryClient from 'openapi-react-query'
import type { paths } from '../generated/api'

const client = createClient<paths>({ baseUrl: window.location.origin })
export const $api = createQueryClient(client)
