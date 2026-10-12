import { useState } from 'react';
import { useOutboundRequests, useRetryOutboundRequest } from '../../hooks/useOutboundRequests';
import { OutboundTarget, ProcessStatus } from '@/types';
import type { OutboundRequestQueryRequest, OutboundRequestVo } from '@/types';
import { RotateCw, Send } from 'lucide-react';

const PAGE_SIZE = 20;

const selectClass =
  'bg-card border border-border rounded-xl px-3 py-2 text-[13px] text-foreground focus:outline-none focus:border-primary/40';

/** Shared by the Retry button, the Retried label and the hidden placeholder, so every row has the same action height */
const actionPillClass =
  'inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[12px] font-medium leading-none';

/** Column widths in percent, the table is fixed-layout so the Retry button never changes them */
const columns = [
  { label: 'ID', width: 'w-[6%]' },
  { label: 'Target', width: 'w-[26%]' },
  { label: 'Status', width: 'w-[12%]' },
  { label: 'Payload', width: 'w-[14%]' },
  { label: 'Created', width: 'w-[16%]' },
  { label: 'Expires', width: 'w-[16%]' },
  { label: 'Action', width: 'w-[10%]' },
];

const statusStyle: Record<ProcessStatus, string> = {
  PENDING: 'bg-muted text-muted-foreground',
  PROCESSING: 'bg-primary/10 text-primary',
  SUCCEEDED: 'bg-green-50 text-green-700 dark:bg-green-500/15 dark:text-green-400',
  FAILED: 'bg-red-50 text-red-600 dark:bg-red-500/15 dark:text-red-400',
  DISCARDED: 'bg-muted text-muted-foreground',
};

const formatDate = (value: string | null) => (value ? new Date(value).toLocaleString() : '—');

/** Payload of CLOUDFLARE_SYNC_STATISTICS is a JSON string of the UTC date */
const payloadLabel = (json: string) => {
  try {
    const value: unknown = JSON.parse(json);
    return typeof value === 'string' ? value : json;
  } catch {
    return json;
  }
};

const isExpired = (request: OutboundRequestVo) =>
  request.expiresAt !== null && new Date(request.expiresAt).getTime() < Date.now();

export function AdminOutboundRequestsPage() {
  const [page, setPage] = useState(0);
  const [status, setStatus] = useState<ProcessStatus | ''>('');
  const [target, setTarget] = useState<OutboundTarget | ''>('');

  const query: OutboundRequestQueryRequest = {
    status: status || undefined,
    target: target || undefined,
  };
  const { data, isLoading } = useOutboundRequests(query, page, PAGE_SIZE);
  const retryMutation = useRetryOutboundRequest();

  // Changing a filter moves the list back to the first page
  const changeFilter = (apply: () => void) => {
    apply();
    setPage(0);
  };

  const handleRetry = (id: number) => {
    retryMutation.mutate(id, {
      onError: (err: any) => alert(err?.response?.data || err.message || 'Retry failed'),
    });
  };

  return (
    <div className="space-y-5 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <h1 className="text-[22px] font-semibold tracking-tight text-foreground">Outbound</h1>
          <p className="text-[13px] text-muted-foreground mt-1">
            Outbound requests that failed. Retry re-runs the recorded operation.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <select
            value={status}
            onChange={(e) => changeFilter(() => setStatus(e.target.value as ProcessStatus | ''))}
            className={selectClass}
          >
            <option value="">All statuses</option>
            {Object.values(ProcessStatus).map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
          <select
            value={target}
            onChange={(e) => changeFilter(() => setTarget(e.target.value as OutboundTarget | ''))}
            className={selectClass}
          >
            <option value="">All targets</option>
            {Object.values(OutboundTarget).map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </div>
      </div>

      {isLoading ? (
        <div className="admin-card p-10 text-center text-[13px] text-muted-foreground">Loading…</div>
      ) : data?.content.length === 0 ? (
        <div className="admin-card p-10 text-center">
          <Send size={32} className="mx-auto text-muted-foreground/40 mb-3" />
          <p className="text-[13px] text-muted-foreground">No outbound requests.</p>
        </div>
      ) : (
        <>
          <div className="admin-card overflow-x-auto">
            <table className="w-full table-fixed text-[13px]">
              <colgroup>
                {columns.map((column) => (
                  <col key={column.label} className={column.width} />
                ))}
              </colgroup>
              <thead>
                <tr className="text-left text-[11px] font-medium tracking-wide text-muted-foreground border-b border-border">
                  {columns.map((column) => (
                    <th key={column.label} className="px-4 py-3 font-medium first:text-left last:text-right">
                      {column.label}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {data?.content.map((request) => {
                  const retrying = retryMutation.isPending && retryMutation.variables === request.id;
                  const expired = isExpired(request);
                  return (
                    <tr key={request.id} className="border-b border-border/60 last:border-0">
                      <td className="px-4 py-3 font-mono text-muted-foreground">{request.id}</td>
                      <td className="px-4 py-3 font-mono text-[12px] text-foreground truncate" title={request.target}>
                        {request.target}
                      </td>
                      <td className="px-4 py-3">
                        <span
                          className={`px-2 py-0.5 rounded-full text-[11px] font-medium whitespace-nowrap ${statusStyle[request.status]}`}
                        >
                          {request.status}
                        </span>
                      </td>
                      <td className="px-4 py-3 font-mono text-[12px] text-foreground truncate" title={payloadLabel(request.payloadJson)}>
                        {payloadLabel(request.payloadJson)}
                      </td>
                      <td className="px-4 py-3 text-[12px] text-muted-foreground whitespace-nowrap">
                        {formatDate(request.createdAt)}
                      </td>
                      <td className="px-4 py-3 text-[12px] text-muted-foreground whitespace-nowrap">
                        {formatDate(request.expiresAt)}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex justify-end">
                          {request.status === ProcessStatus.FAILED && request.retried ? (
                            <span className={`${actionPillClass} bg-muted text-muted-foreground`}>Retried</span>
                          ) : request.status === ProcessStatus.FAILED ? (
                            <button
                              onClick={() => handleRetry(request.id)}
                              disabled={retrying || expired}
                              title={expired ? 'Expired, can no longer be retried' : undefined}
                              className={`${actionPillClass} bg-primary text-primary-foreground hover:bg-primary/90 disabled:opacity-40`}
                            >
                              <RotateCw size={12} className={retrying ? 'animate-spin' : ''} />
                              Retry
                            </button>
                          ) : (
                            // Hidden placeholder keeps the row height identical to rows that have an action
                            <span aria-hidden className={`${actionPillClass} invisible`}>
                              Retry
                            </span>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {data && data.totalPages > 1 && (
            <div className="flex items-center justify-between">
              <span className="text-[12px] text-muted-foreground">
                {data.page + 1} / {data.totalPages} · {data.total} requests
              </span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="px-3 py-1.5 rounded-full bg-card border border-border text-[12px] font-medium disabled:opacity-40"
                >
                  Prev
                </button>
                <button
                  onClick={() => setPage((p) => Math.min(data.totalPages - 1, p + 1))}
                  disabled={page >= data.totalPages - 1}
                  className="px-3 py-1.5 rounded-full bg-card border border-border text-[12px] font-medium disabled:opacity-40"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}