/**
 * Picture API — aligns with backend-legacy PictureAdminController @RequestMapping("/api/admin/pictures")
 * @see backend-legacy/blog-app/src/main/kotlin/cn/arorms/blog/app/controllers/admin/PictureAdminController.kt
 */
import { get, post, put, del } from './client';
import type { PictureDTO, PageResponse } from '@/types';

export const fetchPictures = async (page = 0, size = 20): Promise<PageResponse<PictureDTO>> => {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return get<PageResponse<PictureDTO>>(`/api/admin/pictures?${params}`);
};

export const fetchPicture = async (id: number): Promise<PictureDTO> =>
  get<PictureDTO>(`/api/admin/pictures/${id}`);

export const uploadPicture = async (
  file: File,
  alt?: string,
  tagIds?: number[],
  showInGallery: boolean = false
): Promise<PictureDTO> => {
  const formData = new FormData();
  formData.append('file', file);
  if (alt) formData.append('alt', alt);
  if (tagIds?.length) tagIds.forEach((id) => formData.append('tagIds', String(id)));
  formData.append('showInGallery', String(showInGallery));
  // do not set Content-Type manually — let axios/browser set boundary
  return post<PictureDTO>('/api/admin/pictures/upload', formData);
};

export const updatePictureMetadata = async (
  id: number,
  data: { alt?: string; tagIds?: number[]; showInGallery?: boolean }
): Promise<PictureDTO> => put<PictureDTO>(`/api/admin/pictures/${id}`, data);

export const deletePicture = async (id: number): Promise<void> =>
  del<void>(`/api/admin/pictures/${id}`);
