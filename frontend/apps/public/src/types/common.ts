/**
 * Common enum types and shared infrastructure
 * Mirrors backend-legacy common module: enums and framework PageResponse
 */

// --- Enums as const objects ---

export const Language = {
  ZH: 'ZH' as const,
  EN: 'EN' as const,
};

export type Language = typeof Language[keyof typeof Language];

export const ArticleStatus = {
  DRAFT: 'DRAFT' as const,
  PUBLISHED: 'PUBLISHED' as const,
  ARCHIVED: 'ARCHIVED' as const,
};

export type ArticleStatus = typeof ArticleStatus[keyof typeof ArticleStatus];

// --- Pagination wrapper (mirrors cn.arorms.framework.common.domain.PageResponse) ---

export interface PageResponse<T> {
  content: T[];
  total: number;
  page: number;
  size: number;
  totalPages: number;
  last: boolean;
}
