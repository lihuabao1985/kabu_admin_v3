import type { ComponentType } from 'react'
import { PermissionManagementPage } from '../permission/PermissionManagementPage'
import { RoleManagementPage } from '../role/RoleManagementPage'
import { RolePermissionBindingPage } from '../rbac/RolePermissionBindingPage'
import { UserRoleBindingPage } from '../rbac/UserRoleBindingPage'
import { StockFavoriteManagementPage } from '../stock/StockFavoriteManagementPage'
import { StockManagementPage } from '../stock/StockManagementPage'
import { StockPriceChangeRankingPage } from '../stock/StockPriceChangeRankingPage'
import { StockRealtimeChangePage } from '../stock/StockRealtimeChangePage'
import { StockDividendConfirmedManagementPage } from '../stockDividendConfirmed/StockDividendConfirmedManagementPage'
import { StockDividendRightsLastDayStatsPage } from '../stockDividendConfirmed/StockDividendRightsLastDayStatsPage'
import { StockPriceHistoryManagementPage } from '../stockPriceHistory/StockPriceHistoryManagementPage'
import { UserManagementPage } from '../user/UserManagementPage'

export type PageKey =
  | 'users'
  | 'roles'
  | 'permissions'
  | 'userRoles'
  | 'rolePermissions'
  | 'stocks'
  | 'stockDividendConfirmed'
  | 'stockDividendRightsLastDayStats'
  | 'stockPriceHistory'
  | 'stockPriceChangeRanking'
  | 'stockRealtimeChange'
  | 'stockFavorites'

type MenuModuleKey = 'system' | 'rbac' | 'stock'

export interface MenuPageConfig {
  key: PageKey
  label: string
  requiredAuthorities: string[]
  component: ComponentType
}

export interface MenuModuleConfig {
  key: MenuModuleKey
  label: string
  pages: MenuPageConfig[]
}

export const menuModules: MenuModuleConfig[] = [
  {
    key: 'system',
    label: '系统管理',
    pages: [
      { key: 'users', label: '用户管理', requiredAuthorities: ['ROLE_ADMIN', 'USER:MANAGE'], component: UserManagementPage },
      { key: 'roles', label: '角色管理', requiredAuthorities: ['ROLE_ADMIN', 'ROLE:MANAGE'], component: RoleManagementPage },
      {
        key: 'permissions',
        label: '权限管理',
        requiredAuthorities: ['ROLE_ADMIN', 'PERMISSION:MANAGE'],
        component: PermissionManagementPage
      }
    ]
  },
  {
    key: 'rbac',
    label: 'RBAC',
    pages: [
      {
        key: 'userRoles',
        label: '用户角色绑定',
        requiredAuthorities: ['ROLE_ADMIN', 'RBAC:ASSIGN_ROLE'],
        component: UserRoleBindingPage
      },
      {
        key: 'rolePermissions',
        label: '角色权限绑定',
        requiredAuthorities: ['ROLE_ADMIN', 'RBAC:ASSIGN_PERMISSION'],
        component: RolePermissionBindingPage
      }
    ]
  },
  {
    key: 'stock',
    label: '股票模块',
    pages: [
      {
        key: 'stocks',
        label: '股票一览',
        requiredAuthorities: ['ROLE_ADMIN', 'STOCK:VIEW', 'STOCK:MANAGE'],
        component: StockManagementPage
      },
      {
        key: 'stockDividendConfirmed',
        label: '股票配当',
        requiredAuthorities: [
          'ROLE_ADMIN',
          'STOCK_DIVIDEND_CONFIRMED:VIEW',
          'STOCK_DIVIDEND_CONFIRMED:MANAGE',
          'STOCK_DIVIDEND_CONFIRMED:CONFIRM',
          'STOCK_DIVIDEND_CONFIRMED:IMPORT'
        ],
        component: StockDividendConfirmedManagementPage
      },
      {
        key: 'stockDividendRightsLastDayStats',
        label: '股票配当统计',
        requiredAuthorities: [
          'ROLE_ADMIN',
          'STOCK_DIVIDEND_CONFIRMED:VIEW',
          'STOCK_DIVIDEND_CONFIRMED:MANAGE',
          'STOCK_DIVIDEND_CONFIRMED:CONFIRM',
          'STOCK_DIVIDEND_CONFIRMED:IMPORT'
        ],
        component: StockDividendRightsLastDayStatsPage
      },
      {
        key: 'stockPriceHistory',
        label: '股票历史股价',
        requiredAuthorities: ['ROLE_ADMIN', 'STOCK_PRICE_HISTORY:VIEW', 'STOCK_PRICE_HISTORY:MANAGE'],
        component: StockPriceHistoryManagementPage
      },
      {
        key: 'stockPriceChangeRanking',
        label: '排行榜',
        requiredAuthorities: [
          'ROLE_ADMIN',
          'STOCK:VIEW',
          'STOCK:MANAGE',
          'STOCK_PRICE_HISTORY:VIEW',
          'STOCK_PRICE_HISTORY:MANAGE'
        ],
        component: StockPriceChangeRankingPage
      },
      {
        key: 'stockRealtimeChange',
        label: '实时涨跌幅查询',
        requiredAuthorities: ['ROLE_ADMIN', 'STOCK:VIEW', 'STOCK:MANAGE'],
        component: StockRealtimeChangePage
      },
      {
        key: 'stockFavorites',
        label: '股票收藏一览',
        requiredAuthorities: ['ROLE_ADMIN', 'STOCK:VIEW', 'STOCK:MANAGE'],
        component: StockFavoriteManagementPage
      }
    ]
  }
]

export const canAccessPage = (authorities: string[], requiredAuthorities: string[]): boolean =>
  requiredAuthorities.some((required) => authorities.includes(required))

export const getVisibleMenuModules = (authorities: string[]): MenuModuleConfig[] =>
  menuModules
    .map((module) => ({
      ...module,
      pages: module.pages.filter((page) => canAccessPage(authorities, page.requiredAuthorities))
    }))
    .filter((module) => module.pages.length > 0)

export const flattenVisiblePages = (modules: MenuModuleConfig[]): MenuPageConfig[] =>
  modules.flatMap((module) => module.pages)
