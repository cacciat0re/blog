/**
 * User types — Vo
 * Mirrors backend-legacy common UserVo
 */
import type { UserRole } from './common';

export interface UserVo {
  id: number | null;
  username: string;
  email: string;
  displayName: string | null;
  bio: string | null;
  avatar: string | null;
  role: UserRole;
  isEnabled: boolean;
  createdAt: string;
}
