/**
 * Site statistics API
 */
import { get } from './client';

/** GET /api/statistics/date-traffic — visits keyed by date, days without data are absent */
export const fetchDateTraffic = async (from: string, to: string): Promise<Record<string, number>> =>
  get<Record<string, number>>('/api/statistics/date-traffic', { params: { from, to } });