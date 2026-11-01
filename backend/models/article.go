package models

import "time"

type Article struct {
	ID           int64                `json:"id" gorm:"primary_key;auto_increment"`
	Slug         string               `json:"slug" gorm:"column:slug;size:255;not null;unique_index:uq_article_slug"`
	Translations []ArticleTranslation `json:"translations" gorm:"foreignkey:Slug"`
	CategoryID   int64                `json:"category_id" gorm:"column:category_id;not null"`
	Category     *Category            `json:"category" gorm:"foreignkey:CategoryID"`
	Tags         []Tag                `json:"tags" gorm:"many2many:article_tags;"`
	CreatedAt    time.Time            `json:"created_at" gorm:"column:created_at;not null"`
	UpdatedAt    time.Time            `json:"updated_at" gorm:"column:updated_at;not null"`
}

type ArticleTranslation struct {
	ID              int64  `json:"id" gorm:"primary_key;auto_increment"`
	ArticleId       int64  `json:"article_id" gorm:"column:article_id;not null"`
	Language        string `json:"language" gorm:"column:language;size:255;not null"`
	Title           string `json:"title" gorm:"column:title;size:255;not null"`
	Summary         string `json:"summary" gorm:"column:summary;size:255;not null"`
	MarkdownContent string `json:"markdown_content" gorm:"column:markdown_content;size:255;not null"`
	HTMLContent     string `json:"html_content" gorm:"column:html_content;size:255;not null"`
	IsSource        bool   `json:"is_source" gorm:"column:is_source;not null"`
	IsAITranslate   bool   `json:"is_ai_translate" gorm:"column:is_ai_translate;not null"`
}
