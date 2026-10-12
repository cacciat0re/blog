import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchOutboundRequests, retryOutboundRequest } from '../api/outbound';
import type { OutboundRequestQueryRequest } from '@/types';

export const useOutboundRequests = (query: OutboundRequestQueryRequest, page = 0, size = 20) =>
  useQuery({
    queryKey: ['admin-outbound-requests', query, page, size],
    queryFn: () => fetchOutboundRequests(query, page, size),
  });

export const useRetryOutboundRequest = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => retryOutboundRequest(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin-outbound-requests'] }),
  });
};