import { useParams, Link } from 'react-router-dom';
import { useArticleBySlug } from '../../hooks/useArticles';
import { useLanguage } from '../../context/LanguageContext';
import 'katex/dist/katex.min.css';
import { Calendar, Tag, ArrowLeft } from 'lucide-react';

const formatDate = (s: string | null): string => s ? new Date(s).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) : '---';

export const ArticleDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const { language } = useLanguage();
  const { data: article, isLoading, error } = useArticleBySlug(slug ?? null, language);

  if (isLoading) return (
    <div className="min-h-screen py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="border-[0.5px] border-gray-200 dark:border-gray-800 p-8 animate-pulse">
          <div className="h-8 bg-gray-100 dark:bg-gray-800 mb-4 w-3/4" />
          <div className="h-4 bg-gray-100 dark:bg-gray-800 mb-8 w-1/2" />
          <div className="space-y-3">
            <div className="h-3 bg-gray-100 dark:bg-gray-800" />
            <div className="h-3 bg-gray-100 dark:bg-gray-800" />
            <div className="h-3 bg-gray-100 dark:bg-gray-800 w-5/6" />
          </div>
        </div>
      </div>
    </div>
  );

  if (error || !article) return (
    <div className="min-h-screen py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="border-[0.5px] border-red-200 bg-red-50 dark:bg-red-950 dark:border-red-800 p-8 text-center">
          <p className="text-sm text-red-600 dark:text-red-400 font-mono mb-4">ERROR: {error?.message || 'Article not found'}</p>
          <Link to="/articles" className="inline-flex items-center gap-2 text-[11px] font-mono text-black dark:text-white hover:text-[#0047FF]"><ArrowLeft className="w-4 h-4" /> BACK TO ARTICLES</Link>
        </div>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <Link to="/articles" className="inline-flex items-center gap-2 text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-[#0047FF] transition-colors mb-8">
          <ArrowLeft className="w-4 h-4" /> BACK TO ARTICLES
        </Link>

        <header className="mb-8 pb-8 border-b-[0.5px] border-gray-200 dark:border-gray-800">
          {article.isAiTranslated && (
            <div className="mb-4 px-4 py-3 bg-blue-50 dark:bg-blue-950 dark:bg-opacity-30 border border-blue-200 dark:border-blue-800">
              <p className="text-sm text-blue-700 dark:text-blue-300 font-mono">
                {language === 'ZH' ? '本文由 AI 翻译仅供参考' : 'This article was translated by AI for reference only'}
              </p>
            </div>
          )}
          {article.category && (
            <Link to={`/articles?category=${article.category.id}`} className="inline-block px-3 py-1 text-[10px] font-mono uppercase tracking-wider border-[0.5px] border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-300 hover:border-[#0047FF] hover:text-[#0047FF] transition-colors mb-4">
              {article.category.name}
            </Link>
          )}
          <h1 className="text-3xl md:text-4xl font-bold tracking-tight text-black dark:text-white mb-6 leading-tight">{article.title || 'Untitled'}</h1>
          <div className="flex flex-wrap items-center gap-4 text-[11px] font-mono text-gray-500 dark:text-gray-400">
            <span className="flex items-center gap-1"><Calendar className="w-3 h-3" /> {formatDate(article.createdAt)}</span>
          </div>
        </header>

        <article className="prose prose-lg max-w-none">
          {article.content ? (
            // content is pre-rendered HTML (Markdown + KaTeX are rendered in the admin app on save)
            <div className="markdown-content" dangerouslySetInnerHTML={{ __html: article.content }} />
          ) : (
            <p className="text-gray-500 dark:text-gray-400 font-mono text-center py-12">NO CONTENT AVAILABLE</p>
          )}
        </article>

        {article.tags?.length > 0 && (
          <div className="mt-12 pt-8 border-t-[0.5px] border-gray-200 dark:border-gray-800">
            <div className="flex items-center gap-2 mb-3">
              <Tag className="w-4 h-4 text-gray-400" />
              <span className="text-[10px] font-mono uppercase tracking-wider text-gray-400">TAGS</span>
            </div>
            <div className="flex flex-wrap gap-2">
              {article.tags.map(tag => (
                <Link key={tag.id} to={`/articles?tag=${tag.id}`} className="px-3 py-1.5 text-[11px] font-mono border-[0.5px] border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-300 hover:border-[#0047FF] hover:text-[#0047FF] transition-colors">#{tag.name}</Link>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};