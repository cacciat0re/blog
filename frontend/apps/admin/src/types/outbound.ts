/**
 * Outbound request types — Vo and query request
 * Mirrors backend common OutboundRequestVo / OutboundRequestQueryRequest
 * @see backend/blog-common/src/main/kotlin/cn/arorms/blog/common/responses/OutboundRequestVo.kt
 */
import type { OutboundTarget, ProcessStatus } from './common';

export interface OutboundRequestVo {
  id: number;
  target: OutboundTarget;
  status: ProcessStatus;
  /** true once a manual retry has run, a request can be retried at most once */
  retried: boolean;
  /** JSON-encoded payload, for CLOUDFLARE_SYNC_STATISTICS a JSON string of the UTC date */
  payloadJson: string;
  expiresAt: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface OutboundRequestQueryRequest {
  status?: ProcessStatus;
  target?: OutboundTarget;
}