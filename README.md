# Go Bamboo Specs (sample, parameterized)

Folder ini terpisah dari specs lain. Berisi 3 pipeline Go sample (port 3031/3032/3033) dan bisa ditambah dengan menambah entry config di `GoPlans`.

## Struktur
- `pom.xml` : parent `bamboo-specs-parent` 10.2.10.
- `src/main/java/com/example/pnm/go/GoPlans.java` : project `PGO3`, generate 3 plan sample (go-app1..3) dari satu Specs.
- `src/main/java/com/example/pnm/go/GoStages.java` : helper stage/job; semua task dockerized (agent cukup Docker+Git).

## Plan yang di-generate (1 Specs, 3 plan Go)
- **Project**: key `PGO3`, name `PNM Go Sample Pipelines`.
- Tiga plan dev build (checkout + go deps/test/build + docker build/push + Argo placeholder):
  - Go App1: key `GOA1`, linked repo `go-app1`, service `go-app1`, port 3031.
  - Go App2: key `GOA2`, linked repo `go-app2`, service `go-app2`, port 3032.
  - Go App3: key `GOA3`, linked repo `go-app3`, service `go-app3`, port 3033.

### Default variables per plan (bisa di-override saat run)
- `ENV=dev`
- `BRANCH_NAME=main` (script fetch/checkout branch ini setelah checkout)
- `SERVICE_NAME=go-appX`
- `APPLICATION_NAME=go-appX`
- `MAIN_GO_PATH=./main.go`
- `DOCKER_REGISTRY=ghcr.io` (ubah sesuai registry)
- `DOCKER_NAMESPACE=yohanesrinaldy012` (ubah ke org/namespace)
- `GO_BASE_IMAGE=ghcr.io/base-images/go-toolset:1.23.9-1749636489`
- `ARGO_APP_URL=https://argocd.example.com` (placeholder)
- `IMAGE_TAG` kosong → default `yyyyMMdd-<buildNumber>`
- Optional: `DOCKER_USER`, `DOCKER_TOKEN` (login registry)
- Optional: `SONAR_PROJECT_KEY`, `ENABLE_SONAR=false`, `ENABLE_QUALITY_GATE=false` (belum ada task sonar)

## Catatan task
- Checkout pakai linked repo default. Script akan `git fetch/checkout` ke `BRANCH_NAME` jika diset.
- Go build/test pakai kontainer `GO_BASE_IMAGE`, cache di `.jenkins-go-cache` (lokal workspace).
- Docker build/push pakai registry dari variable; login jika `DOCKER_USER/TOKEN` di-set.
- Argo step masih placeholder (echo) – ganti ke curl/argocd CLI jika perlu.

## Menambah app baru
- Tambah entry `new AppCfg(...)` di array `APPS` dalam `GoPlans` (plan name/key, linked repo key, service/app name, main.go path).
- Scan Specs di Bamboo, plan baru akan muncul tanpa mengganggu plan lain.

## Sample apps
- `tutorial/go-sample-apps/app1` (port 3031), `app2` (3032), `app3` (3033) sudah disiapkan lengkap dengan Dockerfile.
