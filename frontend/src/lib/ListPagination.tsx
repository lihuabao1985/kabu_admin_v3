import { Button, Select, Space, Typography } from 'antd'
import { useMemo } from 'react'

const DEFAULT_PAGE_SIZE_OPTIONS = [10, 20, 50, 100, 200]

interface ListPaginationProps {
  total: number
  page: number
  size: number
  disabled?: boolean
  pageSizeOptions?: number[]
  onPageChange: (page: number) => void
  onSizeChange: (size: number) => void
}

const clamp = (value: number, min: number, max: number): number => Math.min(Math.max(value, min), max)

export function ListPagination({
  total,
  page,
  size,
  disabled = false,
  pageSizeOptions = DEFAULT_PAGE_SIZE_OPTIONS,
  onPageChange,
  onSizeChange
}: ListPaginationProps) {
  const availablePageSizes = useMemo(() => {
    const normalized = pageSizeOptions.filter((option) => Number.isInteger(option) && option > 0)
    const unique = Array.from(new Set(normalized))
    return unique.length > 0 ? unique.sort((left, right) => left - right) : DEFAULT_PAGE_SIZE_OPTIONS
  }, [pageSizeOptions])

  const currentSize = availablePageSizes.includes(size) ? size : availablePageSizes[0]
  const totalPages = Math.max(1, Math.ceil(Math.max(total, 0) / currentSize))
  const currentPage = clamp(page, 1, totalPages)
  const windowStart = Math.floor((currentPage - 1) / 10) * 10 + 1
  const windowEnd = Math.min(windowStart + 9, totalPages)
  const pageNumbers = Array.from({ length: windowEnd - windowStart + 1 }, (_, index) => windowStart + index)

  const jumpTo = (targetPage: number) => onPageChange(clamp(targetPage, 1, totalPages))

  return (
    <Space direction="vertical" size={8} style={{ width: '100%', marginTop: 16 }}>
      <Space wrap>
        <Button onClick={() => jumpTo(1)} disabled={disabled || currentPage === 1}>
          First
        </Button>
        <Button onClick={() => jumpTo(currentPage - 1)} disabled={disabled || currentPage === 1}>
          Prev
        </Button>
        {pageNumbers.map((pageNumber) => (
          <Button
            key={pageNumber}
            type={pageNumber === currentPage ? 'primary' : 'default'}
            onClick={() => jumpTo(pageNumber)}
            disabled={disabled}
          >
            {pageNumber}
          </Button>
        ))}
        <Button onClick={() => jumpTo(currentPage + 1)} disabled={disabled || currentPage === totalPages}>
          Next
        </Button>
        <Button onClick={() => jumpTo(totalPages)} disabled={disabled || currentPage === totalPages}>
          Last
        </Button>
        <Select
          style={{ minWidth: 150 }}
          value={currentSize}
          options={availablePageSizes.map((option) => ({
            label: `${option} / page`,
            value: option
          }))}
          disabled={disabled}
          onChange={(nextSize) => onSizeChange(nextSize)}
        />
      </Space>
      <Typography.Text type="secondary">
        Total {total} records, page {currentPage} of {totalPages}
      </Typography.Text>
    </Space>
  )
}
