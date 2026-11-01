/**
 * Statistics types — Vo
 * Mirrors backend-legacy EntityCountVo
 * @see backend-legacy/blog-common/src/main/kotlin/cn/arorms/blog/common/responses/EntityCountVo.kt
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