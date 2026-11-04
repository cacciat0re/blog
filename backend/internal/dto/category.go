package dto

type CategoryResponse struct {
	Slug     string `json:"slug"`
	Name     string `json:"name"`
	ParentID *int64 `json:"parent_id"`
}
