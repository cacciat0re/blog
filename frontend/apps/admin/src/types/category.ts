/**
 * Category types — Vo, entity, and mutation requests
 * Mirrors backend-legacy common CategoryVo / CategoryUpsertRequest and Category entity
 */
import type { Language } from './common';

// --- Category Vo (public-facing, flattened) ---

export interface CategoryVo {
  id: number | null;
  name: string;
  slug: string;
  parentId: number | null;
}

// --- Raw entity (admin, mirrors backend-legacy Category entity) ---

export interface CategoryEntity {
  id: number | null;
  names: Record<Language, string>;
  slug: string;
  parent: { id: number | null } | null;
}

// --- Mutation request (id is sent in the URL path, not the body) ---

export interface CategoryUpsertRequest {
  names: Record<Language, string>;
  slug: string;
  parentId: number | null;
}
