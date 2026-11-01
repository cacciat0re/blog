/**
 * Picture types — Vo
 * Mirrors backend-legacy PictureVo
 * @see backend-legacy/blog-common/src/main/kotlin/cn/arorms/blog/common/responses/PictureVo.kt
 */
import type { TagVo } from './tag';

export interface PictureDTO {
  id: number;
  filename: string;
  originalFilename: string;
  mimeType: string;
  size: number;
  url: string;
  thumbnailUrl: string | null;
  alt: string | null;
  showInGallery: boolean;
  tags: TagVo[];
  createdAt: string;
}
