package repository

import (
	"backend/dto"
	"backend/model"
	"context"
	"errors"

	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

var ErrNotFound = errors.New("record not found")

type IArticleRepository interface {
	Create(ctx context.Context, article *model.Article) error
	FindByID(ctx context.Context, id int64) (model.Article, error)
	FindBySlug(ctx context.Context, slug, lang string) (dto.ArticlePublicResponse, error)
}

type ArticleRepository struct {
	db *gorm.DB
}

var _ IArticleRepository = (*ArticleRepository)(nil)

func NewArticleRepository(db *gorm.DB) *ArticleRepository {
	return &ArticleRepository{db: db}
}

func (r *ArticleRepository) Create(ctx context.Context, article *model.Article) error {
	return r.db.WithContext(ctx).Create(article).Error
}

func (r *ArticleRepository) FindByID(ctx context.Context, id int64) (model.Article, error) {
	var article model.Article
	err := r.db.WithContext(ctx).
		Preload("Translations").
		Where("id = ?", id).
		First(&article).Error
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return model.Article{}, ErrNotFound
	}
	return article, err
}

func (r *ArticleRepository) FindBySlug(ctx context.Context, slug, lang string) (dto.ArticlePublicResponse, error) {
	var response dto.ArticlePublicResponse
	result := r.db.WithContext(ctx).
		Table("articles").
		Select(`articles.id,
			articles.slug,
			at.language,
			at.title,
			at.summary,
			at.html_content AS content,
			articles.updated_at,
			at.is_ai_translate AS is_ai_translated`).
		Joins("JOIN article_translations AS at ON at.article_id = articles.id").
		Where("articles.slug = ? AND articles.status = ?", slug, model.ArticleStatusPublished).
		Where("at.language IN (?, ?)", lang, "EN").
		Order(clause.Expr{SQL: "CASE WHEN at.language = ? THEN 0 ELSE 1 END", Vars: []interface{}{lang}}).
		Limit(1).
		Scan(&response)
	if result.Error != nil {
		return dto.ArticlePublicResponse{}, result.Error
	}
	if result.RowsAffected == 0 {
		return dto.ArticlePublicResponse{}, ErrNotFound
	}
	return response, nil
}
