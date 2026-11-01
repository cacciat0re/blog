import {useState, useEffect, useRef} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {fetchArticleById, createArticle, updateArticle} from '../../api/article';
import {fetchArticleTranslations, upsertArticleTranslation, translateArticleTitle, translateArticleSummary, translateArticleContent} from '../../api/articleTranslation';
import {fetchCategories} from '../../api/category';
import {fetchTags} from '../../api/tag';
import {ARTICLES_QUERY} from '../../hooks/useArticles';
import {MarkdownView, renderMarkdownToHtml} from '../../lib/markdown';
import type {Article, ArticleUpsertRequest, ArticleTranslationUpsertRequest, Language} from '@/types';
import {Save, ArrowLeft, Eye, EyeOff, Languages, Loader2} from 'lucide-react';

type TranslationForm = { title: string; summary: string; originalContent: string; isAiTranslated: boolean };
const LANGUAGES: Language[] = ['EN', 'ZH'];
const EMPTY_TRANSLATION: TranslationForm = {title: '', summary: '', originalContent: '', isAiTranslated: false};

export function AdminArticleEditPage() {
    const {id} = useParams<{ id: string }>();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const isEdit = !!id;
    const articleId = isEdit ? Number(id) : null;

    const [translations, setTranslations] = useState<Record<Language, TranslationForm>>({
        ZH: EMPTY_TRANSLATION,
        EN: EMPTY_TRANSLATION,
    });
    const [translationIds, setTranslationIds] = useState<Record<Language, number | null>>({ZH: null, EN: null});
    const translationsHydrated = useRef(false);
    const [slug, setSlug] = useState('');
    const [status, setStatus] = useState<'DRAFT' | 'PUBLISHED' | 'ARCHIVED'>('DRAFT');
    const [categoryId, setCategoryId] = useState<number | null>(null);
    const [tagIds, setTagIds] = useState<number[]>([]);
    const [activeTab, setActiveTab] = useState<Language>('EN');
    const [previewMode, setPreviewMode] = useState(false);
    // a lightweight save of the previous tab may still be in flight while the AI
    // translate button on the new tab is already clickable — block it until done
    const [autosavePending, setAutosavePending] = useState(false);

    // one aggregate call: metadata + translations in a single article body
    const {data: article} = useQuery<Article | undefined>({
        queryKey: ['article', id],
        queryFn: () => fetchArticleById(Number(id)),
        enabled: isEdit,
    });
    const {data: categories} = useQuery({queryKey: ['categories'], queryFn: () => fetchCategories()});
    const {data: tags} = useQuery({queryKey: ['tags'], queryFn: () => fetchTags()});

    useEffect(() => {
        // Hydrate once only: a background refetch must not clobber unsaved edits
        if (!article || translationsHydrated.current) return;
        translationsHydrated.current = true;
        setSlug(article.slug);
        setStatus(article.status ?? 'DRAFT');
        setCategoryId(article.category?.id ?? null);
        setTagIds(article.tags?.map((t) => t.id as number) || []);
        const next: Record<Language, TranslationForm> = {ZH: EMPTY_TRANSLATION, EN: EMPTY_TRANSLATION};
        const nextIds: Record<Language, number | null> = {ZH: null, EN: null};
        Object.values(article.translations ?? {}).forEach((trans) => {
            next[trans.language] = {
                title: trans.title,
                summary: trans.summary || '',
                originalContent: trans.originalContent || '',
                isAiTranslated: trans.isAiTranslated || false,
            };
            nextIds[trans.language] = trans.id;
        });
        setTranslations(next);
        setTranslationIds(nextIds);
    }, [article]);

    const generateSlug = (title: string) =>
        title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');

    /** render: also produce the HTML (real save); false = lightweight autosave */
    const buildTranslationRequest = (lang: Language, render: boolean): ArticleTranslationUpsertRequest => ({
        id: translationIds[lang],
        language: lang,
        title: translations[lang].title,
        summary: translations[lang].summary || null,
        originalContent: translations[lang].originalContent,
        content: render ? renderMarkdownToHtml(translations[lang].originalContent) : null,
        isAiTranslated: translations[lang].isAiTranslated,
    });

    // after saving a translation whose id we did not know yet, reload the ids so
    // the next save updates the same row instead of violating the
    // (article_id, language) unique constraint
    const refreshTranslationIds = async (targetId: number) => {
        const list = await queryClient.fetchQuery({
            queryKey: ['article-translation-ids', targetId],
            queryFn: () => fetchArticleTranslations(targetId),
        });
        setTranslationIds((prev) => {
            const next = {...prev};
            list.forEach((t) => {
                next[t.language] = t.id;
            });
            return next;
        });
    };

    // One button, one call: metadata plus every translation that has content in
    // a single request body; markdown → HTML rendering happens only here
    const saveArticleMutation = useMutation({
        mutationFn: async () => {
            const requestSlug = slug || generateSlug(translations.EN.title) || generateSlug(translations[activeTab].title);
            const request: ArticleUpsertRequest = {
                id: isEdit ? Number(id) : null,
                slug: requestSlug,
                status,
                categoryId,
                tagIds,
                translations: LANGUAGES
                    .filter((lang) => translations[lang].title.trim() || translations[lang].originalContent.trim())
                    .map((lang) => buildTranslationRequest(lang, true)),
            };
            if (isEdit) {
                await updateArticle(Number(id), request);
            } else {
                await createArticle(request);
            }
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [ARTICLES_QUERY]});
            queryClient.invalidateQueries({queryKey: ['article', id]});
            navigate('/admin/articles');
        },
    });

    // Switching the language tab autosaves the tab being left (lightweight:
    // original markdown only, no HTML rendering) so AI translate on the new tab
    // can read the other language's original content from the server
    const handleTabSwitch = (lang: Language) => {
        if (lang === activeTab) return;
        if (articleId !== null) {
            const current = translations[activeTab];
            if (current.title.trim() || current.originalContent.trim()) {
                setAutosavePending(true);
                upsertArticleTranslation(articleId, buildTranslationRequest(activeTab, false))
                    .then(() => refreshTranslationIds(articleId))
                    .catch(() => undefined) // stay silent; the real save persists everything
                    .finally(() => setAutosavePending(false));
            }
        }
        setActiveTab(lang);
    };

    // AI translation: title and summary are translated with two non-streaming
    // calls, the content streams into the editor chunk by chunk; everything is
    // persisted by the single save button (with rendering) at the end
    const [isTranslating, setIsTranslating] = useState(false);
    const [translateError, setTranslateError] = useState(false);
    const contentEditorRef = useRef<HTMLTextAreaElement>(null);

    // keep the editor scrolled to the bottom while the translation streams in
    useEffect(() => {
        if (isTranslating) {
            const el = contentEditorRef.current;
            if (el) el.scrollTop = el.scrollHeight;
        }
    }, [translations[activeTab].originalContent, isTranslating, activeTab]);

    const handleSave = () => saveArticleMutation.mutate();

    const handleAiTranslate = async () => {
        if (articleId === null || isTranslating) return;
        setIsTranslating(true);
        setTranslateError(false);
        // clear the editor: the translation streams in from scratch
        setTranslations((prev) => ({...prev, [activeTab]: {...prev[activeTab], originalContent: ''}}));
        try {
            await Promise.all([
                translateArticleTitle(articleId, activeTab).then((title) =>
                    setTranslations((prev) => ({...prev, [activeTab]: {...prev[activeTab], title}}))),
                translateArticleSummary(articleId, activeTab).then((summary) =>
                    setTranslations((prev) => ({...prev, [activeTab]: {...prev[activeTab], summary}}))),
                translateArticleContent(articleId, activeTab, (chunk) =>
                    setTranslations((prev) => ({
                        ...prev,
                        [activeTab]: {...prev[activeTab], originalContent: prev[activeTab].originalContent + chunk},
                    }))),
            ]);
            setTranslations((prev) => ({...prev, [activeTab]: {...prev[activeTab], isAiTranslated: true}}));
        } catch {
            setTranslateError(true);
        } finally {
            setIsTranslating(false);
        }
    };

    const handleTranslationChange = (field: keyof TranslationForm, value: string) =>
        setTranslations((prev) => ({...prev, [activeTab]: {...prev[activeTab], [field]: value}}));

    const handleTagToggle = (tagId: number) =>
        setTagIds((prev) => (prev.includes(tagId) ? prev.filter((t) => t !== tagId) : [...prev, tagId]));

    const currentTranslation = translations[activeTab];
    // no translation-state gating here: the backend-legacy resolves the original
    // translation and fails the request if none exists — surfacing the error
    // is more reliable than second-guessing the autosave state client-side

    const handleAiTranslateClick = () => {
        const hasContent = currentTranslation.title.trim() || currentTranslation.originalContent.trim();
        if (hasContent && !confirm(`AI translate will replace the current ${activeTab} content in the editor. Continue?`)) {
            return;
        }
        void handleAiTranslate();
    };

    return (
        <div className="max-w-[1120px] mx-auto space-y-5 animate-fade-in">
            <div className="flex items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                    <button
                        onClick={() => navigate('/admin/articles')}
                        className="w-9 h-9 rounded-full bg-card border border-border flex items-center justify-center hover:bg-muted transition-colors"
                    >
                        <ArrowLeft size={16} className="text-foreground"/>
                    </button>
                    <div>
                        <h1 className="text-[18px] font-semibold tracking-tight text-foreground leading-none">
                            {isEdit ? 'Edit article' : 'New article'}
                        </h1>
                        <p className="text-[12px] text-muted-foreground mt-1">{isEdit ? 'Update content and metadata' : 'Write in Markdown. Keep it simple.'}</p>
                    </div>
                </div>
                <button
                    onClick={() => setPreviewMode(!previewMode)}
                    className="inline-flex items-center gap-2 px-3 py-2 rounded-full bg-card border border-border text-[12px] font-medium text-foreground hover:bg-muted"
                >
                    {previewMode ? <EyeOff size={14}/> : <Eye size={14}/>}
                    {previewMode ? 'Edit' : 'Preview'}
                </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-5 items-start">
                <div
                    className="admin-card p-5 flex flex-col lg:h-[calc(100vh-170px)]">
                    <div className="flex items-center justify-between mb-5">
                        <div className="inline-flex p-1 rounded-full bg-muted border border-border/60">
                            {LANGUAGES.map((lang) => (
                                <button
                                    key={lang}
                                    type="button"
                                    onClick={() => handleTabSwitch(lang)}
                                    className={`px-4 py-1.5 rounded-full text-[12px] font-medium transition-colors ${
                                        activeTab === lang ? 'bg-foreground text-background shadow-sm' : 'text-muted-foreground hover:text-foreground'
                                    }`}
                                >
                                    {lang === 'ZH' ? '中文' : 'English'}
                                </button>
                            ))}
                        </div>
                        <button
                            type="button"
                            onClick={handleAiTranslateClick}
                            disabled={isTranslating || autosavePending || articleId === null}
                            title={articleId === null ? 'Save the article first' : undefined}
                            className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-card border border-border text-[12px] font-medium text-foreground hover:bg-muted disabled:opacity-60 transition-colors"
                        >
                            {isTranslating
                                ? <Loader2 size={14} className="animate-spin"/>
                                : <Languages size={14}/>}
                            {isTranslating ? 'Translating…' : 'AI translate'}
                        </button>
                    </div>

                    <div className="space-y-4 flex-1 min-h-0 flex flex-col">
                        <div>
                            <label
                                className="block text-[11px] font-medium tracking-wide text-muted-foreground mb-2">Title
                                · {activeTab}</label>
                            <input
                                type="text"
                                value={currentTranslation.title}
                                onChange={(e) => handleTranslationChange('title', e.target.value)}
                                className="w-full text-[18px] font-medium text-foreground placeholder:text-muted-foreground bg-transparent border-0 border-b border-border rounded-none px-0 py-2 focus:outline-none focus:border-primary"
                                placeholder="Enter title…"
                            />
                        </div>

                        <div>
                            <label
                                className="block text-[11px] font-medium tracking-wide text-muted-foreground mb-2">Summary</label>
                            <textarea
                                value={currentTranslation.summary}
                                onChange={(e) => handleTranslationChange('summary', e.target.value)}
                                rows={2}
                                className="w-full text-[13px] text-foreground placeholder:text-muted-foreground bg-muted border border-transparent focus:bg-card focus:border-border rounded-xl px-3 py-2.5 focus:outline-none resize-none"
                                placeholder="One-line summary…"
                            />
                        </div>

                        <div className="flex-1 min-h-[320px] lg:min-h-0 flex flex-col">
                            <div className="flex items-center justify-between mb-2">
                                <label className="text-[11px] font-medium tracking-wide text-muted-foreground">Content
                                    · Markdown</label>
                                <label
                                    className="inline-flex items-center gap-2 text-[11px] text-muted-foreground cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={currentTranslation.isAiTranslated}
                                        onChange={(e) =>
                                            setTranslations((prev) => ({
                                                ...prev,
                                                [activeTab]: {...prev[activeTab], isAiTranslated: e.target.checked}
                                            }))
                                        }
                                        className="w-3.5 h-3.5 rounded border-border text-primary focus:ring-primary/20"
                                    />
                                    AI translated
                                </label>
                            </div>
                            {previewMode ? (
                                <div
                                    className="flex-1 min-h-[320px] lg:min-h-0 overflow-auto rounded-xl bg-muted border border-border p-4">
                                    {currentTranslation.originalContent
                                        ? <MarkdownView markdown={currentTranslation.originalContent}/>
                                        : <span className="text-[13px] text-muted-foreground">No content yet…</span>}
                                </div>
                            ) : (
                                <textarea
                                    ref={contentEditorRef}
                                    value={currentTranslation.originalContent}
                                    onChange={(e) => handleTranslationChange('originalContent', e.target.value)}
                                    disabled={isTranslating}
                                    className="flex-1 w-full min-h-[320px] lg:min-h-0 text-[13px] leading-relaxed text-foreground placeholder:text-muted-foreground bg-card border border-border rounded-xl px-3 py-3 focus:outline-none focus:border-primary/40 focus:ring-4 focus:ring-primary/10 resize-none font-mono disabled:opacity-70"
                                    placeholder="Write your article in Markdown…"
                                />
                            )}
                        </div>
                    </div>
                    {translateError && (
                        <p className="text-[12px] text-red-600 mt-3">Translation failed. Check the LLM service and try again.</p>
                    )}
                    {saveArticleMutation.isError && (
                        <p className="text-[12px] text-red-600 mt-3">
                            {(saveArticleMutation.error as Error).message || 'Failed to save article.'}
                        </p>
                    )}
                </div>

                <div className="space-y-4 lg:sticky lg:top-[76px]">
                    <div className="admin-card p-4">
                        <label
                            className="block text-[11px] font-medium tracking-wide text-muted-foreground mb-2">Slug</label>
                        <input
                            type="text"
                            value={slug}
                            onChange={(e) => setSlug(e.target.value)}
                            className="w-full text-[13px] bg-muted border border-transparent focus:bg-card focus:border-border rounded-xl px-3 py-2.5 focus:outline-none"
                            placeholder="article-url-slug"
                        />
                        <p className="text-[11px] text-muted-foreground mt-2">Auto-generated if empty.</p>
                    </div>

                    <div className="admin-card p-4">
                        <label
                            className="block text-[11px] font-medium tracking-wide text-muted-foreground mb-2">Status</label>
                        <select
                            value={status}
                            onChange={(e) => setStatus(e.target.value as typeof status)}
                            className="w-full text-[13px] bg-muted border border-transparent focus:bg-card focus:border-border rounded-xl px-3 py-2.5 focus:outline-none"
                        >
                            <option value="DRAFT">Draft</option>
                            <option value="PUBLISHED">Published</option>
                            <option value="ARCHIVED">Archived</option>
                        </select>
                    </div>

                    <div className="admin-card p-4">
                        <label
                            className="block text-[11px] font-medium tracking-wide text-muted-foreground mb-2">Category</label>
                        <select
                            value={categoryId || ''}
                            onChange={(e) => setCategoryId(e.target.value ? Number(e.target.value) : null)}
                            className="w-full text-[13px] bg-muted border border-transparent focus:bg-card focus:border-border rounded-xl px-3 py-2.5 focus:outline-none"
                        >
                            <option value="">No category</option>
                            {categories?.map((cat) => (
                                <option key={cat.id} value={String(cat.id)}>
                                    {cat.names.EN || cat.names.ZH || ''}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="admin-card p-4">
                        <label
                            className="block text-[11px] font-medium tracking-wide text-muted-foreground mb-2">Tags</label>
                        <div className="flex flex-wrap gap-2">
                            {tags?.map((tag) => (
                                <button
                                    key={tag.id}
                                    type="button"
                                    onClick={() => handleTagToggle(tag.id as number)}
                                    className={`px-2.5 py-1 rounded-full text-[12px] font-medium border transition-colors ${
                                        tagIds.includes(tag.id as number)
                                            ? 'bg-primary text-white border-primary'
                                            : 'bg-card text-muted-foreground border-border hover:border-border'
                                    }`}
                                >
                                    {tag.name}
                                </button>
                            ))}
                            {tags?.length === 0 &&
                                <span className="text-[12px] text-muted-foreground">No tags yet.</span>}
                        </div>
                    </div>

                    <button
                        type="button"
                        onClick={handleSave}
                        disabled={saveArticleMutation.isPending}
                        className="w-full inline-flex items-center justify-center gap-2 px-4 py-3 bg-primary text-white text-[13px] font-medium rounded-full hover:bg-primary disabled:opacity-40 disabled:cursor-not-allowed transition-colors shadow-sm"
                    >
                        {saveArticleMutation.isPending ? <Loader2 size={14} className="animate-spin"/> : <Save size={14}/>}
                        {saveArticleMutation.isPending ? 'Saving…' : isEdit ? 'Update article' : 'Create article'}
                    </button>
                </div>
            </div>
        </div>
    );
}