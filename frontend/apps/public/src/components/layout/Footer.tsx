import { useTranslation } from '../../context/TranslationContext';

export function Footer() {
  const { t } = useTranslation();

  return (
    <footer className="border-t-[0.5px] border-gray-200 dark:border-gray-800 mt-auto">
      <div className="max-w-4xl mx-auto px-6 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-12">
          <div>
            <h3 className="text-[10px] font-mono uppercase tracking-wider text-gray-400 mb-4">{t('app.title')}</h3>
            <p className="text-[11px] font-mono text-gray-500 dark:text-gray-400 leading-relaxed">
              {t('footer.brandDescription')}
            </p>
          </div>
          <div>
            <h3 className="text-[10px] font-mono uppercase tracking-wider text-gray-400 mb-4">{t('footer.navigation')}</h3>
            <ul className="space-y-2">
              <li><a href="/" className="text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-black dark:hover:text-white transition-colors">{t('nav.home')}</a></li>
              <li><a href="/articles" className="text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-black dark:hover:text-white transition-colors">{t('nav.articles')}</a></li>
              <li><a href="/archive" className="text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-black dark:hover:text-white transition-colors">{t('nav.archive')}</a></li>
              <li><a href="/gallery" className="text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-black dark:hover:text-white transition-colors">{t('nav.gallery')}</a></li>
            </ul>
          </div>
          <div>
            <h3 className="text-[10px] font-mono uppercase tracking-wider text-gray-400 mb-4">{t('footer.connect')}</h3>
            <ul className="space-y-2">
              <li><a href="https://github.com/cacc" target="_blank" rel="noopener noreferrer" className="text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-black dark:hover:text-white transition-colors">{t('footer.github')}</a></li>
              <li><a href="mailto:holmesamzish@outlook.com" className="text-[11px] font-mono text-gray-500 dark:text-gray-400 hover:text-black dark:hover:text-white transition-colors">{t('footer.email')}</a></li>
            </ul>
          </div>
        </div>
        <div className="mt-12 pt-6 border-t-[0.5px] border-gray-200 dark:border-gray-800 flex flex-col md:flex-row justify-between items-center gap-2">
          <p className="text-[10px] font-mono text-gray-400 dark:text-gray-500">&copy; 2023 - 2026 ARORMS. {t('footer.copyright')}</p>
          <p className="text-[10px] font-mono text-gray-400 dark:text-gray-500"><a href="https://github.com/cacciat0re/blog" target="_blank" rel="noopener noreferrer" className="hover:text-black dark:hover:text-white transition-colors">{t('footer.sourceCode')}</a></p>
        </div>
      </div>
    </footer>
  );
}