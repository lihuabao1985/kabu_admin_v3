import type { PageKey } from './menuRegistry'

interface NavigatePageEventDetail {
  pageKey: PageKey
}

const NAVIGATE_PAGE_EVENT_NAME = 'app:navigate-page'

export const navigateToPage = (pageKey: PageKey) => {
  window.dispatchEvent(
    new CustomEvent<NavigatePageEventDetail>(NAVIGATE_PAGE_EVENT_NAME, {
      detail: { pageKey }
    })
  )
}

export const subscribePageNavigation = (handler: (pageKey: PageKey) => void): (() => void) => {
  const listener: EventListener = (event) => {
    const customEvent = event as CustomEvent<NavigatePageEventDetail>
    if (!customEvent.detail?.pageKey) {
      return
    }
    handler(customEvent.detail.pageKey)
  }

  window.addEventListener(NAVIGATE_PAGE_EVENT_NAME, listener)
  return () => window.removeEventListener(NAVIGATE_PAGE_EVENT_NAME, listener)
}
