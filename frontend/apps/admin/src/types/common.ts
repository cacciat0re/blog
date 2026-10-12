/**
 * Common enum types and shared infrastructure
 * Mirrors backend common module: enums and framework PageResponse
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

export const UserRole = {
  ADMIN: 'ADMIN' as const,
  PUBLISHER: 'PUBLISHER' as const,
  USER: 'USER' as const,
  GUEST: 'GUEST' as const,
};

export type UserRole = typeof UserRole[keyof typeof UserRole];

export const ProcessStatus = {
  PENDING: 'PENDING' as const,
  PROCESSING: 'PROCESSING' as const,
  SUCCEEDED: 'SUCCEEDED' as const,
  FAILED: 'FAILED' as const,
  DISCARDED: 'DISCARDED' as const,
};

export type ProcessStatus = typeof ProcessStatus[keyof typeof ProcessStatus];

export const OutboundTarget = {
  CLOUDFLARE_SYNC_STATISTICS: 'CLOUDFLARE_SYNC_STATISTICS' as const,
};

export type OutboundTarget = typeof OutboundTarget[keyof typeof OutboundTarget];

// --- Pagination wrapper (mirrors cn.arorms.framework.common.domain.PageResponse) ---

export interface PageResponse<T> {
  content: T[];
  total: number;
  page: number;
  size: number;
  totalPages: number;
  last: boolean;
}
