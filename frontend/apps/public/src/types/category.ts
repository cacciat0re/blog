/**
 * Category types — Vo and tree node
 * Mirrors backend-legacy common CategoryVo / CategoryTreeNode
 */

// --- Category Vo (public-facing, flattened) ---

export interface CategoryVo {
  id: number | null;
  name: string;
  slug: string;
  parentId: number | null;
}

// --- Tree node (recursive, for category tree widget) ---

export interface CategoryTreeNode {
  id: number;
  name: string;
  slug: string;
  children: Array<CategoryTreeNode>;
}
