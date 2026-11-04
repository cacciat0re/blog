package dto

type PageRequest struct {
	Limit  int
	Offset int
}

type PageResponse[T any] struct {
	Content    []T   `json:"content"`
	Total      int64 `json:"total"`
	Limit      int   `json:"limit"`
	Offset     int   `json:"offset"`
	TotalPages int   `json:"totalPages"`
	HasNext    bool  `json:"hasNext"`
}
