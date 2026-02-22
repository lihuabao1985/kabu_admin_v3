import { navigateToPage } from '../navigation/pageNavigation'

const DRILLDOWN_RIGHTS_LAST_DAY_KEY = 'stockDividendConfirmed.drilldown.rightsLastDay'

export const drilldownToStockDividendConfirmed = (rightsLastDay: string) => {
  sessionStorage.setItem(DRILLDOWN_RIGHTS_LAST_DAY_KEY, rightsLastDay)
  navigateToPage('stockDividendConfirmed')
}

export const consumeStockDividendConfirmedDrilldownRightsLastDay = (): string | undefined => {
  const value = sessionStorage.getItem(DRILLDOWN_RIGHTS_LAST_DAY_KEY)
  if (!value) {
    return undefined
  }
  sessionStorage.removeItem(DRILLDOWN_RIGHTS_LAST_DAY_KEY)
  return value
}
