package main

import (
	"errors"
	"flag"
	"fmt"
	"log"
	"net"
	"net/url"
	"os"
	"strconv"

	"github.com/golang-migrate/migrate/v4"
	_ "github.com/golang-migrate/migrate/v4/database/pgx/v5" // registers driver "pgx5"
	"github.com/golang-migrate/migrate/v4/source/iofs"

	"gitlab.arorms.cn/arorms/applications/blog/internal/config"
	"gitlab.arorms.cn/arorms/applications/blog/migrations"
)

const (
	defaultConfigPath = "config.yml"
	noSteps           = -1
)

type options struct {
	direction   string
	steps       int
	showVersion bool
}

func main() {
	log.SetOutput(os.Stdout)

	if err := run(); err != nil {
		log.Fatal(err)
	}
}

// run exists so deferred cleanup executes before process exit: log.Fatal and
// os.Exit skip deferred calls.
func run() error {
	opts, err := parseFlags()
	if err != nil {
		return err
	}

	cfg, err := config.Load(defaultConfigPath)
	if err != nil {
		return fmt.Errorf("load config %s: %w", defaultConfigPath, err)
	}

	src, err := iofs.New(migrations.FS, ".")
	if err != nil {
		return fmt.Errorf("load embedded migrations: %w", err)
	}

	m, err := migrate.NewWithSourceInstance("iofs", src, databaseURL(cfg.Database))
	if err != nil {
		// NewWithSourceInstance does not close the source instance when the
		// database fails to open.
		_ = src.Close()
		return fmt.Errorf("connect to database: %w", err)
	}
	defer func() {
		if srcErr, dbErr := m.Close(); srcErr != nil || dbErr != nil {
			log.Printf("close migrate: source=%v database=%v", srcErr, dbErr)
		}
	}()

	if opts.showVersion {
		return printVersion(m)
	}

	return applyMigrations(m, opts)
}

func parseFlags() (options, error) {
	var opts options

	// The standard flag package accepts both -flag and --flag, so -d and
	// --direction are both valid.
	flag.StringVar(&opts.direction, "d", "up", "migration direction: up or down")
	flag.StringVar(&opts.direction, "direction", "up", "migration direction: up or down")
	flag.IntVar(&opts.steps, "steps", noSteps, "number of migrations to run (up: default all; down: default 1)")
	flag.BoolVar(&opts.showVersion, "v", false, "print current database migration version")
	flag.BoolVar(&opts.showVersion, "version", false, "print current database migration version")
	flag.Parse()

	if flag.NArg() > 0 {
		return options{}, fmt.Errorf("unexpected arguments: %q", flag.Args())
	}
	if opts.direction != "up" && opts.direction != "down" {
		return options{}, fmt.Errorf("invalid direction %q: must be up or down", opts.direction)
	}
	if opts.steps < noSteps {
		return options{}, fmt.Errorf("invalid --steps %d: must be >= 0", opts.steps)
	}

	return opts, nil
}

func printVersion(m *migrate.Migrate) error {
	version, dirty, err := m.Version()
	if errors.Is(err, migrate.ErrNilVersion) {
		fmt.Println("version: none")
		fmt.Println("dirty: false")
		return nil
	}
	if err != nil {
		return fmt.Errorf("read migration version: %w", err)
	}

	fmt.Printf("version: %d\ndirty: %t\n", version, dirty)
	return nil
}

func applyMigrations(m *migrate.Migrate, opts options) error {
	var (
		err  error
		what string
	)

	switch opts.direction {
	case "up":
		if opts.steps == noSteps {
			err, what = m.Up(), "up (all pending)"
		} else {
			err, what = m.Steps(opts.steps), fmt.Sprintf("up %d step(s)", opts.steps)
		}
	case "down":
		// Negative n means down. Never call m.Down(): it unwinds everything.
		n := 1
		if opts.steps != noSteps {
			n = opts.steps
		}
		err, what = m.Steps(-n), fmt.Sprintf("down %d step(s)", n)
	}

	return report(err, what)
}

func report(err error, what string) error {
	switch {
	case err == nil:
		log.Printf("%s: done", what)
		return nil
	case errors.Is(err, migrate.ErrNoChange):
		log.Printf("%s: no change", what)
		return nil
	}

	// ErrDirty is a struct, not a sentinel value.
	var dirty migrate.ErrDirty
	if errors.As(err, &dirty) {
		return fmt.Errorf("%s: database is dirty at version %d; fix the failed migration before retrying: %w", what, dirty.Version, err)
	}

	// Steps() reports "not enough migrations" with os.ErrNotExist when none
	// were applied and ErrShortLimit when some were.
	if errors.Is(err, os.ErrNotExist) {
		log.Printf("%s: no migrations available", what)
		return nil
	}

	var short migrate.ErrShortLimit
	if errors.As(err, &short) {
		log.Printf("%s: fewer migrations available than requested (%d short)", what, short.Short)
		return nil
	}

	return fmt.Errorf("%s: %w", what, err)
}

// databaseURL builds the DSN for migrate's pgx/v5 driver. The scheme must be
// "pgx5" because migrate dispatches on it; the driver rewrites it to
// "postgres" internally before handing it to pgx.
func databaseURL(db config.DatabaseConfig) string {
	u := &url.URL{
		Scheme: "pgx5",
		Host:   net.JoinHostPort(db.Host, strconv.Itoa(db.Port)),
		Path:   "/" + db.Name,
	}
	if db.Username != "" || db.Password != "" {
		u.User = url.UserPassword(db.Username, db.Password)
	}

	q := url.Values{}
	q.Set("sslmode", "disable") // parity with the old Makefile targets
	u.RawQuery = q.Encode()

	return u.String()
}
