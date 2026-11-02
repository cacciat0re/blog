package dto

import "time"

type ArticleQuery struct {
	Keyword    string
	Language   string
	CategoryID int64
	TagID      int64
}

//type ArticleTranslationUpsertRequest struct {
//	Language        string `json:"language"`
//	Title           string `json:"title"`
//	Summary         string `json:"summary"`
//	MarkdownContent string `json:"markdown_content"`
//	HTMLContent     string `json:"html_content"`
//	IsSource        bool   `json:"is_source"`
//	IsAITranslate   bool   `json:"is_ai_translate"`
//}
//
//type ArticleUpsertRequest struct {
//	Slug         string                    `json:"slug"`
//	Translations []ArticleTranslationInput `json:"translations"`
//}

type ArticlePublicResponse struct {
	ID             int64     `json:"id"`
	Slug           string    `json:"slug"`
	Language       string    `json:"language"`
	Title          string    `json:"title"`
	Summary        string    `json:"summary"`
	Content        string    `json:"content"`
	UpdatedAt      time.Time `json:"updated_at"`
	IsAITranslated bool      `json:"is_ai_translated"`
}

type ArticleSummaryResponse struct {
	ID        int64     `json:"id"`
	Slug      string    `json:"slug"`
	Title     string    `json:"title"`
	Summary   string    `json:"summary"`
	UpdatedAt time.Time `json:"updated_at"`
}
