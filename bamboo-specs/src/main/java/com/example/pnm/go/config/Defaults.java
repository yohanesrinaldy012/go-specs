package com.example.pnm.go.config;

public final class Defaults {
  private Defaults() {}

  public static final String PROJECT_KEY = "PGO3";
  public static final String PROJECT_NAME = "PNM Go Sample Pipelines";

  public static final String DOCKER_REGISTRY = "ghcr.io";
  public static final String DOCKER_NAMESPACE = "yohanesrinaldy012";
  // Go toolset image used for build/test steps (perintah user).
  public static final String GO_BASE_IMAGE = "registry.access.redhat.com/ubi9/go-toolset:9.7";
  public static final String ARGO_APP_URL = "https://argocd.example.com";

  public static final AppConfig[] APPS = new AppConfig[]{
      new AppConfig("Go App1", "GOA1", "go-app1", "go-app1", "go-app1", "./main.go", "UAT-Deployer"),
      new AppConfig("Go App2", "GOA2", "go-app2", "go-app2", "go-app2", "./cmd/app/main.go", "UAT-Deployer"),
      new AppConfig("Go App3", "GOA3", "go-app3", "go-app3", "go-app3", "./main.go", "UAT-Deployer")
  };
}
