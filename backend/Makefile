.PHONY: run build build-debug fmt tidy clean migrate-up migrate-down

run:
	go run ./cmd/server

build:
	CGO_ENABLED=0 GOOS=$(GOOS) GOARCH=$(GOARCH) go build -trimpath -ldflags="$(LDFLAGS)" -o bin/ ./cmd/server blog

fmt:
	gofmt -w .

tidy:
	go mod tidy

clean:
	rm -rf bin

migrate-up:
	migrate -path migrations -database "postgres://postgres:$$DB_PASSWORD@localhost:5432/blog_go_db?sslmode=disable" up

migrate-down:
	migrate -path migrations -database "postgres://postgres:$$DB_PASSWORD@localhost:5432/blog_go_db?sslmode=disable" down 1