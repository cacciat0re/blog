/**
 * Outbound request API — aligns with backend OutboundRequestController @RequestMapping("/api/admin/outbound-request")
 * @see backend/blog-app/src/main/kotlin/cn/arorms/blog/app/controllers/admin/OutboundRequestController.kt
 */
import { get, post } from './client';
import type { OutboundRequestQueryRequest, OutboundRequestVo, PageResponse } from '@/types';

export const fetchOutboundRequests = async (
  query: OutboundRequestQueryRequest,
  page = 0,
  size = 20
): Promise<PageResponse<OutboundRequestVo>> => {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (query.status) params.set('status', query.status);
  if (query.target) params.set('target', query.target);
  return get<PageResponse<OutboundRequestVo>>(`/api/admin/outbound-request?${params}`);
};

export const fetchOutboundRequest = async (id: number): Promise<OutboundRequestVo> =>
  get<OutboundRequestVo>(`/api/admin/outbound-request/${id}`);

/** Re-runs a FAILED request. Returns 204 even when the re-run fails, which is then recorded as a new FAILED request. */
export const retryOutboundRequest = async (id: number): Promise<void> =>
  post<void>(`/api/admin/outbound-request/${id}/retry`);