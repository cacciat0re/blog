package model

import "time"

const (
	ArticleStatusDraft     = "DRAFT"
	ArticleStatusPublished = "PUBLISHED"
	ArticleStatusArchived  = "ARCHIVED"
)

type Article struct {
	ID           int64                `json:"id" gorm:"primaryKey;autoIncrement"`
	Slug         string               `json:"slug" gorm:"column:slug;size:255;not null;uniqueIndex:uq_article_slug"`
	Status       string               `json:"status" gorm:"column:status;size:20;not null;default:DRAFT;index"`
	Translations []ArticleTranslation `json:"translations" gorm:"foreignKey:ArticleID"`
	CategoryID   *int64               `json:"category_id" gorm:"column:category_id;index"`
	Category     *Category            `json:"category,omitempty" gorm:"foreignKey:CategoryID"`
	Tags         []Tag                `json:"tags,omitempty" gorm:"many2many:article_tags;"`
	CreatedAt    time.Time            `json:"created_at" gorm:"column:created_at;not null"`
	UpdatedAt    time.Time            `json:"updated_at" gorm:"column:updated_at;not null"`
}

type ArticleTranslation struct {
	ID               int64  `json:"id" gorm:"primaryKey;autoIncrement"`
	ArticleID        int64  `json:"article_id" gorm:"column:article_id;not null"`
	Language         string `json:"language" gorm:"column:language;size:2;not null"`
	Title            string `json:"title" gorm:"column:title;size:255;not null"`
	Summary          string `json:"summary" gorm:"column:summary;type:varchar(500);not null"`
	MarkdownContent  string `json:"markdown_content" gorm:"column:markdown_content;type:text;not null"`
	HTMLContent      string `json:"html_content" gorm:"column:html_content;type:text;not null"`
	IsSource         bool   `json:"is_source" gorm:"column:is_source;not null"`
	IsTranslatedByAI bool   `json:"is_translated_by_ai" gorm:"column:is_translated_by_ai;not null"`
}
