CREATE TABLE IF NOT EXISTS categories
(
    id        BIGSERIAL PRIMARY KEY,
    slug      VARCHAR(255) NOT NULL,
    parent_id BIGINT REFERENCES categories (id) ON DELETE SET NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_categories_slug ON categories (slug);
CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON categories (parent_id);

CREATE TABLE IF NOT EXISTS category_translations
(
    id          BIGSERIAL PRIMARY KEY,
    category_id BIGINT       NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    language    VARCHAR(2)   NOT NULL,
    content     VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_category_translations_category_id ON category_translations (category_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_category_translations_category_language
    ON category_translations (category_id, language);

CREATE TABLE IF NOT EXISTS tags
(
    id         BIGSERIAL PRIMARY KEY,
    slug       VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_tags_slug ON tags (slug);

CREATE TABLE IF NOT EXISTS article_tags
(
    article_id BIGINT NOT NULL REFERENCES articles (id) ON DELETE CASCADE,
    tag_id     BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_article_tags_tag_id ON article_tags (tag_id);

ALTER TABLE articles
    ADD COLUMN IF NOT EXISTS category_id BIGINT REFERENCES categories (id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_articles_category_id ON articles (category_id);