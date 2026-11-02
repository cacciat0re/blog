package repository

import (
	"backend/model"
	"context"

	"gorm.io/gorm"
)

type ArticleRepository struct {
	db *gorm.DB
}

type IArticleRepository interface {
	Create(ctx context.Context, article *model.Article) error
	FindByID(ctx context.Context, id int64) (model.Article, error)
}

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
	return article, err
}
