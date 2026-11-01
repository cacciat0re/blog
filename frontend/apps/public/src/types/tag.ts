/**
 * Tag types — Vo (public-facing)
 * Mirrors backend-legacy common TagVo
 */

export interface TagVo {
  id: number | null;
  name: string;
  slug: string;
}
