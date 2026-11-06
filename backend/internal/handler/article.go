package handler

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"strconv"

	"github.com/go-chi/chi/v5"

	"gitlab.arorms.cn/arorms/applications/blog/internal/dto"
	"gitlab.arorms.cn/arorms/applications/blog/internal/model"
	"gitlab.arorms.cn/arorms/applications/blog/internal/service"
)

type ArticleHandler struct {
	service service.IArticleService
}

func NewArticleHandler(service service.IArticleService) *ArticleHandler {
	return &ArticleHandler{service: service}
}

func (h *ArticleHandler) Create(w http.ResponseWriter, r *http.Request) {
	var article model.Article
	if err := json.NewDecoder(r.Body).Decode(&article); err != nil {
		http.Error(w, "invalid request body", http.StatusBadRequest)
		return
	}

	if err := h.service.Create(r.Context(), &article); err != nil {
		log.Printf("failed to create article: %v", err)
		http.Error(w, "internal server error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(article); err != nil {
		log.Printf("failed to encode created article %d: %v", article.ID, err)
	}
}

func (h *ArticleHandler) GetByID(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.ParseInt(chi.URLParam(r, "id"), 10, 64)
	if err != nil {
		http.Error(w, "invalid article id", http.StatusBadRequest)
		return
	}

	article, err := h.service.GetByID(r.Context(), id)
	if err != nil {
		if errors.Is(err, service.ErrArticleNotFound) {
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

func (h *ArticleHandler) GetAll(w http.ResponseWriter, r *http.Request) {
	var query dto.ArticleListRequest
	if err := json.NewDecoder(r.Body).Decode(&query); err != nil {
		http.Error(w, "invalid request body", http.StatusBadRequest)
		return
	}
	if !dto.IsSupportedLanguage(query.Language) {
		http.Error(w, "unsupported language", http.StatusBadRequest)
		return
	}

	result, err := h.service.ListPublished(r.Context(), query)
	if err != nil {
		log.Printf("failed to list published articles: %v", err)
		http.Error(w, "internal server error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(result); err != nil {
		log.Printf("failed to encode article list: %v", err)
	}
}

func (h *ArticleHandler) GetBySlug(w http.ResponseWriter, r *http.Request) {
	slug := chi.URLParam(r, "slug")
	lang := chi.URLParam(r, "lang")
	if !dto.IsSupportedLanguage(lang) {
		http.Error(w, "unsupported language", http.StatusBadRequest)
		return
	}

	article, err := h.service.GetBySlug(r.Context(), slug, lang)
	if err != nil {
		if errors.Is(err, service.ErrArticleNotFound) {
			http.Error(w, "article not found", http.StatusNotFound)
			return
		}
		log.Printf("failed to get article %q: %v", slug, err)
		http.Error(w, "internal server error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(article); err != nil {
		log.Printf("failed to encode article %q: %v", slug, err)
	}
}
