package handler

import (
	"backend/services"
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"strconv"

	"github.com/go-chi/chi/v5"
)

type ArticleHandler struct {
	service services.IArticleService
}

func NewArticleHandler(service services.IArticleService) *ArticleHandler {
	return &ArticleHandler{service: service}
}

func (h *ArticleHandler) GetByID(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.ParseInt(chi.URLParam(r, "id"), 10, 64)
	if err != nil {
		http.Error(w, "invalid article id", http.StatusBadRequest)
		return
	}

	article, err := h.service.GetByID(r.Context(), id)
	if err != nil {
		if errors.Is(err, services.ErrArticleNotFound) {
			http.Error(w, "article not found", http.StatusNotFound)
			return
		}
		log.Printf("failed to get article %d: %v", id, err)
		http.Error(w, "internal server error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(article); err != nil {
		log.Printf("failed to encode article %d: %v", id, err)
	}
}
