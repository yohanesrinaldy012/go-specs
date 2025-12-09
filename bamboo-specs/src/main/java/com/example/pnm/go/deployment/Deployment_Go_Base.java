package com.example.pnm.go.deployment;

import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.example.pnm.go.config.AppConfig;
import com.example.pnm.go.config.Defaults;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;

/** Helper untuk membuat deployment project per app. */
public final class Deployment_Go_Base {
  private Deployment_Go_Base() {}

  public static Deployment build(AppConfig cfg) {
    PlanIdentifier sourcePlan = new PlanIdentifier(Defaults.PROJECT_KEY, cfg.planKey());

    Environment dev = new Environment("dev")
        .tasks(new ScriptTask()
            .description("Deploy dev (echo only)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "echo \"Deploying to DEV\"",
                "echo \"Service: " + cfg.serviceName() + "\"",
                "echo \"Release: ${bamboo.deploy.release} (from build)\"",
                "TAG=\"${bamboo_IMAGE_TAG:-$(date +%Y%m%d)-${bamboo.deploy.release}}\"",
                "echo \"Tag/IMAGE_TAG: ${TAG}\"")))
        .triggers(new AfterSuccessfulBuildPlanTrigger());

    Environment uat = new Environment("uat")
        .tasks(new ScriptTask()
            .description("Deploy uat (echo only)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "echo \"Deploying to UAT\"",
                "echo \"Service: " + cfg.serviceName() + "\"",
                "echo \"Release: ${bamboo.deploy.release}\"",
                "TAG=\"${bamboo_IMAGE_TAG:-$(date +%Y%m%d)-${bamboo.deploy.release}}\"",
                "echo \"Tag/IMAGE_TAG: ${TAG}\"")));

    Environment prod = new Environment("prod")
        .tasks(new ScriptTask()
            .description("Deploy prod (echo only)")
            .inlineBody(String.join("\n",
                "#!/bin/bash",
                "echo \"Deploying to PROD\"",
                "echo \"Service: " + cfg.serviceName() + "\"",
                "echo \"Release: ${bamboo.deploy.release}\"",
                "TAG=\"${bamboo_IMAGE_TAG:-$(date +%Y%m%d)-${bamboo.deploy.release}}\"",
                "echo \"Tag/IMAGE_TAG: ${TAG}\"")));

    return new Deployment(sourcePlan, cfg.planKey() + "-Deployment")
        .description("Deployment pipeline for " + cfg.planName())
        // oid hanya boleh lowercase/angka
        .oid((cfg.planKey() + "dep").toLowerCase())
        .releaseNaming(new ReleaseNaming("${bamboo.buildNumber}"))
        .environments(dev, uat, prod);
  }

  public static DeploymentPermissions buildProjectPermissions(AppConfig cfg) {
    String deploymentName = cfg.planKey() + "-Deployment";

    // Gabungkan permission supaya UAT Group & PROD Group bisa LIHAT projectnya
    Permissions projectPerms = new Permissions()
            .groupPermissions(cfg.uatGroup(), PermissionType.VIEW, PermissionType.EDIT)
            .groupPermissions(cfg.prodGroup(), PermissionType.VIEW, PermissionType.EDIT);

    return new DeploymentPermissions(deploymentName)
            .permissions(projectPerms);
  }

  public static EnvironmentPermissions[] buildEnvPermissions(AppConfig cfg) {
    String deploymentName = cfg.planKey() + "-Deployment";
    
    // 1. Permission UAT: Grup UAT boleh VIEW, EDIT, dan BUILD (Deploy)
    Permissions uatPerms = new Permissions()
            .groupPermissions(cfg.uatGroup(), 
                              PermissionType.VIEW, 
                              PermissionType.EDIT, 
                              PermissionType.BUILD); 

    // 2. Permission PROD: HANYA Grup PROD yang boleh BUILD (Deploy)
    // Ini adalah "Approval step 2": Hanya grup ini yang tombol deploy-nya aktif.
    Permissions prodPerms = new Permissions()
            .groupPermissions(cfg.prodGroup(), 
                              PermissionType.VIEW, 
                              PermissionType.EDIT, 
                              PermissionType.BUILD);

    return new EnvironmentPermissions[] {
        new EnvironmentPermissions(deploymentName)
            .environmentName("uat")
            .permissions(uatPerms),
            
        new EnvironmentPermissions(deploymentName)
            .environmentName("prod")
            .permissions(prodPerms)
    };
  }
}
