import {keepPreviousData, useQuery} from '@tanstack/react-query';
import {fetchDateTraffic} from '../api/statistics';

export const useDateTraffic = (from: string, to: string) =>
    useQuery<Record<string, number>, Error>({
        queryKey: ['admin-date-traffic', from, to],
        queryFn: () => fetchDateTraffic(from, to),
        enabled: !!from && !!to,
        placeholderData: keepPreviousData,
        staleTime: 10 * 60 * 1000,
    });