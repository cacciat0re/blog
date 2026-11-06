package router

import (
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/cors"

	"gitlab.arorms.cn/arorms/applications/blog/internal/config"
	"gitlab.arorms.cn/arorms/applications/blog/internal/handler"
)

type Handlers struct {
	Article *handler.ArticleHandler
}

func New(handlers Handlers, cfg config.CORSConfig) http.Handler {
	r := chi.NewRouter()
	r.Use(middleware.RequestID)
	r.Use(middleware.Recoverer)
	r.Use(cors.Handler(cors.Options{
		AllowedOrigins:   cfg.AllowedOrigins,
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type", "X-Requested-With"},
		ExposedHeaders:   []string{"Link"},
		AllowCredentials: true,
		MaxAge:           3600,
	}))

	r.Get("/api/articles/{slug}", handlers.Article.GetBySlug)
	r.Post("/api/articles", handlers.Article.GetAll)

	r.Post("/api/admin/articles", handlers.Article.Create)
	r.Get("/api/admin/articles/", handlers.Article.GetByID)

	return r
}
