package com.example.pnm.go.deployment;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.deployment.DeploymentProject;
import com.example.pnm.go.config.AppConfig;
import com.example.pnm.go.config.Defaults;

@BambooSpec
public class Deployment_Go_App2 {
  public DeploymentProject deploymentProject() {
    AppConfig cfg = Defaults.APPS[1];
    return Deployment_Go_Base.build(cfg);
  }
}
