/**
 * Article types — public-facing Vo, summary, page response, archive tree
 * Mirrors backend-legacy common responses: ArticleVo / ArticleSummaryVo
 */
import type { Language, ArticleStatus } from './common';
import type { CategoryVo } from './category';
import type { TagVo } from './tag';

// List/summary item
export interface ArticleSummaryVo {
  id: number;
  slug: string;
  title: string;
  summary: string | null;
  status: ArticleStatus | null;
  createdAt: string | null;
  updatedAt: string | null;
  category: CategoryVo | null;
  tags: Array<TagVo> | null;
}

// Article Vo
export interface ArticleVo {
  id: number;
  slug: string;
  createdAt: string;
  updatedAt: string;
  isAiTranslated: boolean;
  title: string;
  summary: string;
  content: string;
  language: Language;
  category: CategoryVo;
  tags: Array<TagVo>;
}
