CREATE TABLE IF NOT EXISTS articles
(
    id         BIGSERIAL PRIMARY KEY,
    slug       VARCHAR(255) NOT NULL,
    status     VARCHAR(10)  NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_article_slug ON articles (slug);

CREATE INDEX IF NOT EXISTS idx_articles_status ON articles (status);

CREATE TABLE IF NOT EXISTS article_translations
(
    id               BIGSERIAL PRIMARY KEY,
    article_id       BIGINT       NOT NULL REFERENCES articles (id) ON DELETE CASCADE,
    language         VARCHAR(2)   NOT NULL,
    title            VARCHAR(255) NOT NULL,
    summary          VARCHAR(255) NOT NULL,
    markdown_content TEXT         NOT NULL,
    html_content     TEXT         NOT NULL,
    is_source        BOOLEAN      NOT NULL,
    is_translated_by_ai BOOLEAN   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_article_translations_article_id ON article_translations (article_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_article_translations_article_languag ON article_translations (article_id, language);
