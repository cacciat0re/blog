package services

import (
	"backend/models"
	"backend/repositories"
	"context"
	"errors"
	"fmt"

	"gorm.io/gorm"
)

var ErrArticleNotFound = errors.New("article not found")

type IArticleService interface {
	GetByID(ctx context.Context, id int64) (models.Article, error)
}

type ArticleService struct {
	repository *repositories.ArticleRepository
}

var _ IArticleService = (*ArticleService)(nil)

func NewArticleService(articleRepo *repositories.ArticleRepository) *ArticleService {
	return &ArticleService{repository: articleRepo}
}

func (s *ArticleService) GetByID(ctx context.Context, id int64) (models.Article, error) {
	article, err := s.repository.FindByID(ctx, id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return models.Article{}, ErrArticleNotFound
		}
		return models.Article{}, fmt.Errorf("find article %d: %w", id, err)
	}
	return article, nil
}
