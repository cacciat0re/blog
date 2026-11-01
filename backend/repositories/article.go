package repositories

import (
	"backend/models"
	"context"

	"gorm.io/gorm"
)

type ArticleRepository struct {
	db *gorm.DB
}

func NewArticleRepository(db *gorm.DB) *ArticleRepository {
	return &ArticleRepository{db: db}
}

func (r *ArticleRepository) FindByID(ctx context.Context, id int64) (models.Article, error) {
	var article models.Article
	err := r.db.WithContext(ctx).Where("id = ?", id).First(&article).Error
	return article, err
}
