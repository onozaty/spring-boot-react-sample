import { useEffect } from 'react'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { CircleCheckIcon, OctagonXIcon } from 'lucide-react'

export type FlashMessage = {
  type: 'success' | 'error'
  message: string
}

interface Props {
  flash: FlashMessage | null
  onDismiss: () => void
}

export function FlashMessage({ flash, onDismiss }: Props) {
  useEffect(() => {
    if (!flash) return
    const timer = setTimeout(onDismiss, 4000)
    return () => clearTimeout(timer)
  }, [flash, onDismiss])

  if (!flash) return null

  return (
    <Alert variant={flash.type === 'error' ? 'destructive' : 'default'} className="mt-4">
      {flash.type === 'success' ? (
        <CircleCheckIcon className="size-4" />
      ) : (
        <OctagonXIcon className="size-4" />
      )}
      <AlertDescription>{flash.message}</AlertDescription>
    </Alert>
  )
}