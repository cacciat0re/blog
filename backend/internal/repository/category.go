package repository

import (
	"context"

	"gorm.io/gorm"

	"github.com/cacciat0re/blog-go/internal/dto"
)

type ICategoryRepository interface {
	FindAll(ctx context.Context, lang string) ([]dto.CategoryResponse, error)
	FindBySlug(ctx context.Context, slug, lang string) (dto.CategoryResponse, error)
}

type CategoryRepository struct {
	db *gorm.DB
}

var _ ICategoryRepository = (*CategoryRepository)(nil)

func NewCategoryRepository(db *gorm.DB) *CategoryRepository {
	return &CategoryRepository{db: db}
}

func (r *CategoryRepository) FindAll(ctx context.Context, lang string) ([]dto.CategoryResponse, error) {
	items := make([]dto.CategoryResponse, 0)
	err := r.db.WithContext(ctx).
		Table("categories AS c").
		Select("c.slug, COALESCE(ct.content, '') AS name, c.parent_id").
		Joins("LEFT JOIN category_translations AS ct ON ct.category_id = c.id AND ct.language = ?", lang).
		Order("c.id ASC").
		Scan(&items).Error
	return items, err
}

func (r *CategoryRepository) FindBySlug(ctx context.Context, slug, lang string) (dto.CategoryResponse, error) {
	var item dto.CategoryResponse
	result := r.db.WithContext(ctx).
		Table("categories AS c").
		Select("c.slug, COALESCE(ct.content, '') AS name, c.parent_id").
		Joins("LEFT JOIN category_translations AS ct ON ct.category_id = c.id AND ct.language = ?", lang).
		Where("c.slug = ?", slug).
		Scan(&item)
	if result.Error != nil {
		return dto.CategoryResponse{}, result.Error
	}
	if result.RowsAffected == 0 {
		return dto.CategoryResponse{}, ErrNotFound
	}
	return item, nil
}
