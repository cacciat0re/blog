package dto

import "time"

const (
	DefaultArticleListLimit = 20
	MaxArticleListLimit     = 100
)

const (
	LanguageZH = "ZH"
	LanguageEN = "EN"
)

func IsSupportedLanguage(language string) bool {
	return language == LanguageZH || language == LanguageEN
}

type ArticleListRequest struct {
	Language string `json:"language"`
	Key      string `json:"key"`
	Category string `json:"category"`
	Tag      string `json:"tag"`
	Limit    int    `json:"limit"`
	Offset   int    `json:"offset"`
}

type ArticleResponse struct {
	Slug             string    `json:"slug"`
	Language         string    `json:"language"`
	Title            string    `json:"title"`
	Summary          string    `json:"summary"`
	Content          string    `json:"content"`
	IsTranslatedByAI bool      `json:"is_translated_by_ai"`
	UpdatedAt        time.Time `json:"updated_at"`
}

type ArticleListItemResponse struct {
	Slug             string            `json:"slug"`
	Title            string            `json:"title"`
	Summary          string            `json:"summary"`
	IsTranslatedByAI bool              `json:"is_translated_by_ai"`
	Category         *CategoryResponse `json:"category,omitempty"`
	Tags             []TagResponse     `json:"tags"`
	UpdatedAt        time.Time         `json:"updated_at"`
}
