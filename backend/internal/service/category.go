package service

import (
	"context"
	"errors"
	"fmt"

	"github.com/cacciat0re/blog-go/internal/dto"
	"github.com/cacciat0re/blog-go/internal/repository"
)

var ErrCategoryNotFound = errors.New("category not found")

type ICategoryService interface {
	List(ctx context.Context, lang string) ([]dto.CategoryResponse, error)
	GetBySlug(ctx context.Context, slug, lang string) (dto.CategoryResponse, error)
}

type CategoryService struct {
	repository repository.ICategoryRepository
}

func NewCategoryService(repo repository.ICategoryRepository) ICategoryService {
	return &CategoryService{repository: repo}
}

func (s *CategoryService) List(ctx context.Context, lang string) ([]dto.CategoryResponse, error) {
	items, err := s.repository.FindAll(ctx, lang)
	if err != nil {
		return nil, fmt.Errorf("list categories: %w", err)
	}
	for i := range items {
		if items[i].Name == "" {
			items[i].Name = items[i].Slug
		}
	}
	return items, nil
}

func (s *CategoryService) GetBySlug(ctx context.Context, slug, lang string) (dto.CategoryResponse, error) {
	item, err := s.repository.FindBySlug(ctx, slug, lang)
	if err != nil {
		if errors.Is(err, repository.ErrNotFound) {
			return dto.CategoryResponse{}, ErrCategoryNotFound
		}
		return dto.CategoryResponse{}, fmt.Errorf("find category %q: %w", slug, err)
	}
	if item.Name == "" {
		item.Name = item.Slug
	}
	return item, nil
}
