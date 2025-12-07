package com.example.pnm.go.pipeline;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchIntegration;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.example.pnm.go.config.AppConfig;
import com.example.pnm.go.config.Defaults;
import com.example.pnm.go.governance.GoStages;

@BambooSpec
public class Plan_Go_App1 {
  private Plan build(AppConfig cfg) {
    Project project = new Project()
        .key(Defaults.PROJECT_KEY)
        .name(Defaults.PROJECT_NAME)
        .description("Go sample services pipelines.");

    return new Plan(project, cfg.planName(), cfg.planKey())
        .description("Go service " + cfg.serviceName() + " build/test + docker push (branch configurable).")
        .linkedRepositories(cfg.repoKey())
        .variables(
            new Variable("ENV", "dev"),
            new Variable("BRANCH_NAME", "main"),
            new Variable("REPO_URL", ""),
            new Variable("SERVICE_NAME", cfg.serviceName()),
            new Variable("APPLICATION_NAME", cfg.appName()),
            new Variable("SONAR_PROJECT_KEY", cfg.serviceName()),
            new Variable("ENABLE_SONAR", "false"),
            new Variable("ENABLE_QUALITY_GATE", "false"),
            new Variable("MAIN_GO_PATH", cfg.mainGoPath()),
            new Variable("DOCKER_REGISTRY", Defaults.DOCKER_REGISTRY),
            new Variable("DOCKER_NAMESPACE", Defaults.DOCKER_NAMESPACE),
            new Variable("IMAGE_TAG", ""),
            new Variable("GO_BASE_IMAGE", Defaults.GO_BASE_IMAGE),
            new Variable("ARGO_APP_URL", Defaults.ARGO_APP_URL))
        .stages(
            GoStages.buildAndTest(),
            GoStages.dockerBuildPush(),
            GoStages.triggerArgo())
        .planBranchManagement(new PlanBranchManagement()
            .createForVcsBranch()
            .branchIntegration(new BranchIntegration().integrationBranchKey(cfg.planKey()))
            .delete(new BranchCleanup().whenRemovedFromRepository(true)));
  }

  public Plan plan() {
    AppConfig cfg = Defaults.APPS[0];
    return build(cfg);
  }
}
