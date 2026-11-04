ALTER TABLE articles DROP COLUMN IF EXISTS category_id;

DROP TABLE IF EXISTS article_tags;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS category_translations;
DROP TABLE IF EXISTS categories;