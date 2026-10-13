/**
 * Statistics types — Vo
 * Mirrors backend EntityCountVo
 * @see backend/blog-common/src/main/kotlin/cn/arorms/blog/common/responses/EntityCountVo.kt
 */
export interface EntityCountVo {
  totalArticles: number;
  publishedArticles: number;
  draftArticles: number;
  archivedArticles: number;
  categories: number;
  tags: number;
  pictures: number;
}