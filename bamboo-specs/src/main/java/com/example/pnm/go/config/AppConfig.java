package com.example.pnm.go.config;

public record AppConfig(String planName, String planKey, String repoKey,
                        String serviceName, String appName, String mainGoPath,
                        String deploymentGroup) {}
