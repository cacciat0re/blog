package repository

import (
	"context"

	"gorm.io/gorm"

	"gitlab.arorms.cn/arorms/applications/blog/internal/dto"
)

type ITagRepository interface {
	FindAll(ctx context.Context) ([]dto.TagResponse, error)
	FindBySlug(ctx context.Context, slug string) (dto.TagResponse, error)
}

type TagRepository struct {
	db *gorm.DB
}

var _ ITagRepository = (*TagRepository)(nil)

func NewTagRepository(db *gorm.DB) *TagRepository {
	return &TagRepository{db: db}
}

func (r *TagRepository) FindAll(ctx context.Context) ([]dto.TagResponse, error) {
	items := make([]dto.TagResponse, 0)
	err := r.db.WithContext(ctx).
		Table("tags").
		Select("slug, name").
		Order("name ASC").
		Order("slug ASC").
		Scan(&items).Error
	return items, err
}

func (r *TagRepository) FindBySlug(ctx context.Context, slug string) (dto.TagResponse, error) {
	var item dto.TagResponse
	result := r.db.WithContext(ctx).
		Table("tags").
		Select("slug, name").
		Where("slug = ?", slug).
		Scan(&item)
	if result.Error != nil {
		return dto.TagResponse{}, result.Error
	}
	if result.RowsAffected == 0 {
		return dto.TagResponse{}, ErrNotFound
	}
	return item, nil
}
