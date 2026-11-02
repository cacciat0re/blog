package service

import (
	"backend/model"
	"backend/repository"
	"context"
	"fmt"
)

type ArticleService struct {
	repository repository.IArticleRepository
}

type IArticleService interface {
	Create(ctx context.Context, article *model.Article) error
	GetByID(ctx context.Context, id int64) (model.Article, error)
}

func NewArticleService(repo repository.IArticleRepository) IArticleService {
	return &ArticleService{repository: repo}
}

func (s *ArticleService) Create(ctx context.Context, article *model.Article) error {
	if err := s.repository.Create(ctx, article); err != nil {
		return fmt.Errorf("create article: %w", err)
	}
	return nil
}

func (s *ArticleService) GetByID(ctx context.Context, id int64) (model.Article, error) {
	article, err := s.repository.FindByID(ctx, id)
	if err != nil {
		return model.Article{}, err
	}
	return article, nil
}
