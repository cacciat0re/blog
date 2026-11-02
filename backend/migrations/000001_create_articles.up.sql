CREATE TABLE IF NOT EXISTS articles
(
    id         BIGSERIAL PRIMARY KEY,
    slug       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_article_slug ON articles (slug);

CREATE TABLE IF NOT EXISTS article_translations
(
    id               BIGSERIAL PRIMARY KEY,
    article_id       BIGINT       NOT NULL REFERENCES articles (id) ON DELETE CASCADE,
    language         VARCHAR(255) NOT NULL,
    title            VARCHAR(255) NOT NULL,
    summary          VARCHAR(255) NOT NULL,
    markdown_content VARCHAR(255) NOT NULL,
    html_content     VARCHAR(255) NOT NULL,
    is_source        BOOLEAN      NOT NULL,
    is_ai_translate  BOOLEAN      NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_article_translations_article_id ON article_translations (article_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_article_translations_article_languag ON article_translations (article_id, language);
