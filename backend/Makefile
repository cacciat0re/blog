.PHONY: run build build-debug fmt tidy clean migrate-up migrate-down migrate-version

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
	go run ./cmd/migrate -d up

migrate-down:
	go run ./cmd/migrate -d down

migrate-version:
	go run ./cmd/migrate -v