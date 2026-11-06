package service

import (
	"context"
	"errors"
	"fmt"

	"gitlab.arorms.cn/arorms/applications/blog/internal/dto"
	"gitlab.arorms.cn/arorms/applications/blog/internal/repository"
)

var ErrTagNotFound = errors.New("tag not found")

type ITagService interface {
	List(ctx context.Context) ([]dto.TagResponse, error)
	GetBySlug(ctx context.Context, slug string) (dto.TagResponse, error)
}

type TagService struct {
	repository repository.ITagRepository
}

func NewTagService(repo repository.ITagRepository) ITagService {
	return &TagService{repository: repo}
}

func (s *TagService) List(ctx context.Context) ([]dto.TagResponse, error) {
	items, err := s.repository.FindAll(ctx)
	if err != nil {
		return nil, fmt.Errorf("list tags: %w", err)
	}
	return items, nil
}

func (s *TagService) GetBySlug(ctx context.Context, slug string) (dto.TagResponse, error) {
	item, err := s.repository.FindBySlug(ctx, slug)
	if err != nil {
		if errors.Is(err, repository.ErrNotFound) {
			return dto.TagResponse{}, ErrTagNotFound
		}
		return dto.TagResponse{}, fmt.Errorf("find tag %q: %w", slug, err)
	}
	return item, nil
}
