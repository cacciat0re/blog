/**
 * Picture types — Vo
 * Mirrors backend-legacy PictureVo
 */
import type { TagVo } from './tag';

export interface PictureVo {
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

// Keep alias for backward compat
export type PictureDTO = PictureVo;
