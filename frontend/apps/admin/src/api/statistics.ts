/**
 * Admin statistics API — aligns with backend StatisticsAdminController @RequestMapping("/api/admin/statistics")
 */
import { get } from './client';
import type { EntityCountVo } from '@/types';

/** GET /api/admin/statistics/entity-counts — aggregated counts computed in the database */
export const fetchEntityCounts = async (): Promise<EntityCountVo> =>
  get<EntityCountVo>('/api/admin/statistics/entity-counts');

/** GET /api/admin/statistics/date-traffic — visits keyed by date, days without data are absent */
export const fetchDateTraffic = async (from: string, to: string): Promise<Record<string, number>> =>
  get<Record<string, number>>('/api/admin/statistics/date-traffic', { params: { from, to } });