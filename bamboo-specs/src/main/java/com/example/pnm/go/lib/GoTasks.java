package com.example.pnm.go.lib;

import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;

public final class GoTasks {
  private GoTasks() {}

  public static ScriptTask prepareCache() {
    return new ScriptTask()
        .description("Prepare Go cache dirs")
        .inlineBody(String.join("\n",
            "#!/bin/bash",
            "set -euo pipefail",
            "if [ -n \"${bamboo_BRANCH_NAME:-}\" ]; then",
            "  git fetch origin \"${bamboo_BRANCH_NAME}\" && git checkout \"${bamboo_BRANCH_NAME}\" && git reset --hard \"origin/${bamboo_BRANCH_NAME}\" || true",
            "fi",
            "mkdir -p \"$PWD/.jenkins-go-cache/mod\" \"$PWD/.jenkins-go-cache/build\""));
  }

  public static ScriptTask goDeps() {
    return new ScriptTask()
        .description("Go download deps (dockerized)")
        .inlineBody(String.join("\n",
            "#!/bin/bash",
            "set -euo pipefail",
            "IMAGE=\"${bamboo_GO_BASE_IMAGE:-nexusrepo.pnm.co.id/base-images/go-toolset:1.23.9-1749636489}\"",
            "echo \"Using base image: ${IMAGE}\"",
            "if [ -n \"${bamboo_BRANCH_NAME:-}\" ]; then",
            "  git fetch origin \"${bamboo_BRANCH_NAME}\" && git checkout \"${bamboo_BRANCH_NAME}\" && git reset --hard \"origin/${bamboo_BRANCH_NAME}\" || true",
            "fi",
            "docker run --rm --user 0 \\",
            "  -v \"$PWD:/app\" \\",
            "  -v \"$PWD/.jenkins-go-cache/mod:/gomod\" \\",
            "  -v \"$PWD/.jenkins-go-cache/build:/gocache\" \\",
            "  -w /app \\",
            "  \"$IMAGE\" \\",
            "  sh -c '",
            "    go env -w GO111MODULE=on && \\",
            "    go env -w GOMODCACHE=/gomod && \\",
            "    go env -w GOCACHE=/gocache && \\",
            "    go mod tidy && \\",
            "    if [ -d vendor ]; then go mod vendor; else go mod download; fi",
            "  '"));
  }

  public static ScriptTask goTest() {
    return new ScriptTask()
        .description("Go test (dockerized)")
        .inlineBody(String.join("\n",
            "#!/bin/bash",
            "set -euo pipefail",
            "IMAGE=\"${bamboo_GO_BASE_IMAGE:-nexusrepo.pnm.co.id/base-images/go-toolset:1.23.9-1749636489}\"",
            "docker run --rm --user 0 \\",
            "  -v \"$PWD:/app\" \\",
            "  -v \"$PWD/.jenkins-go-cache/mod:/gomod\" \\",
            "  -v \"$PWD/.jenkins-go-cache/build:/gocache\" \\",
            "  -w /app \\",
            "  \"$IMAGE\" \\",
            "  sh -c '",
            "    go env -w GOMODCACHE=/gomod && \\",
            "    go env -w GOCACHE=/gocache && \\",
            "    go test -v ./... -coverprofile=coverage.out",
            "  '"));
  }

  public static ScriptTask goBuild() {
    return new ScriptTask()
        .description("Go build binary (dockerized)")
        .inlineBody(String.join("\n",
            "#!/bin/bash",
            "set -euo pipefail",
            "IMAGE=\"${bamboo_GO_BASE_IMAGE:-nexusrepo.pnm.co.id/base-images/go-toolset:1.23.9-1749636489}\"",
            "MAIN_GO_PATH=\"${bamboo_MAIN_GO_PATH:-.}\"",
            "docker run --rm --user 0 \\",
            "  -v \"$PWD:/app\" \\",
            "  -v \"$PWD/.jenkins-go-cache/mod:/gomod\" \\",
            "  -v \"$PWD/.jenkins-go-cache/build:/gocache\" \\",
            "  -w /app \\",
            "  \"$IMAGE\" \\",
            "  sh -c '",
            "    go env -w GOMODCACHE=/gomod && \\",
            "    go env -w GOCACHE=/gocache && \\",
            "    CGO_ENABLED=0 GOOS=linux go build -ldflags=\"-s -w\" -buildvcs=false -o ./build/app ${MAIN_GO_PATH}",
            "  '"));
  }

  public static ScriptTask dockerBuildPush() {
    return new ScriptTask()
        .description("Docker build & push")
        .inlineBody(String.join("\n",
            "#!/bin/bash",
            "set -euo pipefail",
            "REGISTRY=\"${bamboo_DOCKER_REGISTRY}\"",
            "NAMESPACE=\"${bamboo_DOCKER_NAMESPACE}\"",
            "ENV=\"${bamboo_ENV}\"",
            "SERVICE=\"${bamboo_SERVICE_NAME}\"",
            "TAG_DATE=$(date +%Y%m%d)",
            "TAG=\"${bamboo_IMAGE_TAG:-${TAG_DATE}-${bamboo.buildNumber}}\"",
            "IMAGE=\"${REGISTRY}/${NAMESPACE}/${ENV}/${SERVICE}:${TAG}\"",
            "USER=\"${bamboo_DOCKER_USER:-}\"",
            "TOKEN=\"${bamboo_DOCKER_TOKEN:-}\"",
            "if [ -n \"${USER}\" ] && [ -n \"${TOKEN}\" ]; then",
            "  echo \"Logging in to ${REGISTRY} as ${USER}\"",
            "  echo \"${TOKEN}\" | docker login \"${REGISTRY}\" -u \"${USER}\" --password-stdin",
            "fi",
            "if [ -n \"${bamboo_BRANCH_NAME:-}\" ]; then",
            "  git fetch origin \"${bamboo_BRANCH_NAME}\" && git checkout \"${bamboo_BRANCH_NAME}\" && git reset --hard \"origin/${bamboo_BRANCH_NAME}\"",
            "fi",
            "echo \"Building ${IMAGE}\"",
            "docker build -t \"${IMAGE}\" .",
            "echo \"Pushing ${IMAGE}\"",
            "docker push \"${IMAGE}\""));
  }

  public static ScriptTask promoteImage() {
    return new ScriptTask()
        .description("Retag & push existing image (no rebuild)")
        .inlineBody(String.join("\n",
            "#!/bin/bash",
            "set -euo pipefail",
            "REGISTRY=\"${bamboo_DOCKER_REGISTRY}\"",
            "NAMESPACE=\"${bamboo_DOCKER_NAMESPACE}\"",
            "ENV=\"${bamboo_ENV}\"",
            "SOURCE_ENV=\"${bamboo_SOURCE_ENV}\"",
            "SERVICE=\"${bamboo_SERVICE_NAME}\"",
            "TAG=\"${bamboo_IMAGE_TAG}\"",
            "SRC_IMAGE=\"${REGISTRY}/${NAMESPACE}/${SOURCE_ENV}/${SERVICE}:${TAG}\"",
            "DEST_IMAGE=\"${REGISTRY}/${NAMESPACE}/${ENV}/${SERVICE}:${TAG}\"",
            "USER=\"${bamboo_DOCKER_USER:-}\"",
            "TOKEN=\"${bamboo_DOCKER_TOKEN:-}\"",
            "if [ -n \"${USER}\" ] && [ -n \"${TOKEN}\" ]; then",
            "  echo \"Logging in to ${REGISTRY} as ${USER}\"",
            "  echo \"${TOKEN}\" | docker login \"${REGISTRY}\" -u \"${USER}\" --password-stdin",
            "fi",
            "echo \"Promoting ${SRC_IMAGE} -> ${DEST_IMAGE}\"",
            "docker pull \"${SRC_IMAGE}\"",
            "docker tag \"${SRC_IMAGE}\" \"${DEST_IMAGE}\"",
            "docker push \"${DEST_IMAGE}\""));
  }

  public static Task<?, ?>[] triggerArgo() {
    return new Task[]{
        new ScriptTask()
            .description("Trigger ArgoCD (placeholder)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "set -euo pipefail",
                "echo \"TODO: call ArgoCD API for ${bamboo_APP_NAME} in ${bamboo_ENV}\"",
                "echo \"Argo URL: ${bamboo_ARGO_APP_URL}\""))
    };
  }
}
