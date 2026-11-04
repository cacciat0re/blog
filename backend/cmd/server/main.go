package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"

	"github.com/cacciat0re/blog-go/internal/config"
	"github.com/cacciat0re/blog-go/internal/handler"
	"github.com/cacciat0re/blog-go/internal/repository"
	"github.com/cacciat0re/blog-go/internal/router"
	"github.com/cacciat0re/blog-go/internal/service"
)

func main() {
	log.SetOutput(os.Stdout)

	cfg, err := config.Load("config.yml")
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	dsn := fmt.Sprintf(
		"host=%s port=%d user=%s password=%s dbname=%s",
		cfg.Database.Host,
		cfg.Database.Port,
		cfg.Database.Username,
		cfg.Database.Password,
		cfg.Database.Name,
	)

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}

	//if err := db.AutoMigrate(&model.Article{}); err != nil {
	//	log.Fatalf("Failed to migrate table: %v", err)
	//}

	articleRepository := repository.NewArticleRepository(db)
	articleService := service.NewArticleService(articleRepository)
	articleHandler := handler.NewArticleHandler(articleService)

	r := router.New(router.Handlers{
		Article: articleHandler,
	}, cfg.CORS)

	log.Printf("Server is running on port %d...", cfg.Server.Port)
	if err := http.ListenAndServe(fmt.Sprintf(":%d", cfg.Server.Port), r); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
}
