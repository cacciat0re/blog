/**
 * Article types — entity, Vo, summary, translation, mutation requests
 * Mirrors backend-legacy Article entity and common responses/requests
 */
import type {Language, ArticleStatus} from './common';
import type {CategoryVo} from './category';
import type {TagVo} from './tag';

// --- Translations ---

/** Mirrors backend-legacy ArticleTranslationAdminVo: markdown source only, no rendered HTML */
export interface ArticleTranslation {
    id: number | null;
    language: Language;
    title: string;
    summary: string | null;
    originalContent: string;
    isAiTranslated: boolean;
}

/** Mirrors backend-legacy ArticleTranslationUpsertRequest: content is the rendered HTML —
 * send it on the real save; send null for lightweight autosaves (e.g. language-switch)
 * to keep the previously rendered HTML server-side */
export interface ArticleTranslationUpsertRequest {
    id: number | null;
    language: Language | null;
    title: string;
    summary: string | null;
    originalContent: string;
    content: string | null;
    isAiTranslated: boolean;
}

// --- Article entity (admin detail, mirrors backend-legacy Article) ---
// the backend-legacy exposes translations as a language-keyed map on the entity
// (rendered HTML stays server-side); the rendered `content` field never
// travels back to the client

export interface Article {
    id: number | null;
    slug: string;
    createdAt: string;
    updatedAt: string;
    status: ArticleStatus | null;
    category: CategoryVo | null;
    authorId: string;
    tags: TagVo[];
    translations: Partial<Record<Language, ArticleTranslation>>;
}

// --- List/summary item (mirrors backend-legacy ArticleSummaryVo) ---

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

// --- Mutation request (metadata + translations in one body; create: id null,
// update: id set; mirrors backend-legacy ArticleUpsertRequest) ---

export interface ArticleUpsertRequest {
    id: number | null;
    slug: string;
    status: ArticleStatus;
    categoryId: number | null;
    tagIds: number[];
    translations: ArticleTranslationUpsertRequest[];
}