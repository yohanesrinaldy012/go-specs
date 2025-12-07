package com.example.pnm.go.deployment;

import com.atlassian.bamboo.specs.api.builders.deployment.DeploymentProject;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseVersioning;
import com.atlassian.bamboo.specs.api.builders.deployment.triggers.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.task.ScriptTask;
import com.example.pnm.go.config.AppConfig;
import com.example.pnm.go.config.Defaults;

/** Helper untuk membuat deployment project per app. */
public final class Deployment_Go_Base {
  private Deployment_Go_Base() {}

  public static DeploymentProject build(AppConfig cfg) {
    PlanIdentifier sourcePlan = new PlanIdentifier(Defaults.PROJECT_KEY, cfg.planKey());

    Environment dev = new Environment("dev")
        .tasks(new ScriptTask()
            .description("Deploy dev (echo only)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "echo \"Deploying to DEV\"",
                "echo \"Service: " + cfg.serviceName() + "\"",
                "echo \"Release: ${bamboo.deploy.release} (from build)\"",
                "echo \"Tag/IMAGE_TAG (if set in build): ${bamboo_IMAGE_TAG:-auto}\"")))
        .triggers(new AfterSuccessfulBuildPlanTrigger());

    Environment uat = new Environment("uat")
        .tasks(new ScriptTask()
            .description("Deploy uat (echo only)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "echo \"Deploying to UAT\"",
                "echo \"Service: " + cfg.serviceName() + "\"",
                "echo \"Release: ${bamboo.deploy.release}\"",
                "echo \"Tag/IMAGE_TAG: ${bamboo_IMAGE_TAG:-auto}\"")));

    Environment prod = new Environment("prod")
        .tasks(new ScriptTask()
            .description("Deploy prod (echo only)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "echo \"Deploying to PROD\"",
                "echo \"Service: " + cfg.serviceName() + "\"",
                "echo \"Release: ${bamboo.deploy.release}\"",
                "echo \"Tag/IMAGE_TAG: ${bamboo_IMAGE_TAG:-auto}\"")));

    return new DeploymentProject()
        .name(cfg.planKey() + "-Deployment")
        .key(cfg.planKey() + "DEP")
        .description("Deployment pipeline for " + cfg.planName())
        .sourcePlan(sourcePlan)
        .releaseVersioning(new ReleaseVersioning()
            .format("${bamboo.buildNumber}"))
        .environments(dev, uat, prod);
  }
}
