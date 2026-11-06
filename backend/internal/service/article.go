package service

import (
	"context"
	"errors"
	"fmt"
	"strings"

	"gitlab.arorms.cn/arorms/applications/blog/internal/dto"
	"gitlab.arorms.cn/arorms/applications/blog/internal/model"
	"gitlab.arorms.cn/arorms/applications/blog/internal/repository"
)

var ErrArticleNotFound = errors.New("article not found")

type IArticleService interface {
	Create(ctx context.Context, article *model.Article) error
	GetByID(ctx context.Context, id int64) (model.Article, error)
	GetBySlug(ctx context.Context, slug, lang string) (dto.ArticleResponse, error)
	GetAllPublished(ctx context.Context, query dto.ArticleListRequest) (dto.PageResponse[dto.ArticleListItemResponse], error)
}

type ArticleService struct {
	repository repository.IArticleRepository
}

func NewArticleService(repo repository.IArticleRepository) IArticleService {
	return &ArticleService{repository: repo}
}

func (s *ArticleService) Create(ctx context.Context, article *model.Article) error {
	article.Status = strings.ToUpper(strings.TrimSpace(article.Status))
	if article.Status == "" {
		article.Status = model.ArticleStatusDraft
	}
	for i := range article.Translations {
		article.Translations[i].Language = strings.ToUpper(strings.TrimSpace(article.Translations[i].Language))
	}

	if err := s.repository.Create(ctx, article); err != nil {
		return fmt.Errorf("create article: %w", err)
	}
	return nil
}

func (s *ArticleService) GetByID(ctx context.Context, id int64) (model.Article, error) {
	article, err := s.repository.FindByID(ctx, id)
	if err != nil {
		if errors.Is(err, repository.ErrNotFound) {
			return model.Article{}, ErrArticleNotFound
		}
		return model.Article{}, fmt.Errorf("find article %d: %w", id, err)
	}
	return article, nil
}

func (s *ArticleService) GetBySlug(ctx context.Context, slug, lang string) (dto.ArticleResponse, error) {
	response, err := s.repository.FindBySlug(ctx, slug, lang)
	if err != nil {
		if errors.Is(err, repository.ErrNotFound) {
			return dto.ArticleResponse{}, ErrArticleNotFound
		}
		return dto.ArticleResponse{}, fmt.Errorf("find published article %q: %w", slug, err)
	}
	return response, nil
}

func (s *ArticleService) ListPublished(ctx context.Context, query dto.ArticleListRequest) (dto.PageResponse[dto.ArticleListItemResponse], error) {
	query = normalizeArticleListRequest(query)

	items, total, err := s.repository.FindPublished(ctx, query)
	if err != nil {
		return dto.PageResponse[dto.ArticleListItemResponse]{}, fmt.Errorf("list published articles: %w", err)
	}
	for i := range items {
		if items[i].Category != nil && items[i].Category.Name == "" {
			items[i].Category.Name = items[i].Category.Slug
		}
	}

	totalPages := int((total + int64(query.Limit) - 1) / int64(query.Limit))

	return dto.PageResponse[dto.ArticleListItemResponse]{
		Content:    items,
		Total:      total,
		Limit:      query.Limit,
		Offset:     query.Offset,
		TotalPages: totalPages,
		HasNext:    int64(query.Offset+len(items)) < total,
	}, nil
}

func normalizeArticleListRequest(query dto.ArticleListRequest) dto.ArticleListRequest {
	query.Key = strings.TrimSpace(query.Key)
	query.Category = strings.TrimSpace(query.Category)
	query.Tag = strings.TrimSpace(query.Tag)
	if query.Limit <= 0 {
		query.Limit = dto.DefaultArticleListLimit
	}
	if query.Limit > dto.MaxArticleListLimit {
		query.Limit = dto.MaxArticleListLimit
	}
	if query.Offset < 0 {
		query.Offset = 0
	}
	return query
}
