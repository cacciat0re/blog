/**
 * Site traffic API
 */
import { get } from './client';

/** Visits keyed by country code */
export const fetchCountryTraffic = async (timeRange = 30): Promise<Record<string, number>> =>
  get<Record<string, number>>('/api/statistics/country-traffic', { params: { timeRange } });
