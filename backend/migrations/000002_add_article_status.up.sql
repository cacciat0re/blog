ALTER TABLE articles
    ADD COLUMN IF NOT EXISTS status varchar(20) NOT NULL DEFAULT 'DRAFT';

CREATE INDEX IF NOT EXISTS idx_articles_status ON articles (status);