package com.example.pnm.go.governance;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.builders.plan.artifact.ArtifactDependency;
import com.example.pnm.go.lib.Checkout;
import com.example.pnm.go.lib.GoTasks;

public final class GoStages {
  private GoStages() {}

  public static Stage buildAndTest() {
    return new Stage("Build and Test")
        .jobs(new Job("Go Build Test", "GOBT")
            .tasks(Checkout.defaultRepo(),
                GoTasks.prepareCache(),
                GoTasks.goDeps(),
                GoTasks.goTest(),
                GoTasks.goBuild())
            .artifacts(new Artifact()
                .name("go-binary")
                .copyPattern("build/app")
                .shared(true)));
  }

  public static Stage dockerBuildPush() {
    return new Stage("Docker Image")
        .jobs(new Job("Build Push Image", "GOIMG")
            .tasks(Checkout.defaultRepo(),
                GoTasks.dockerBuildPush())
            .artifactDependencies(new ArtifactDependency()
                .name("go-binary")));
  }

  public static Stage promoteImage() {
    return new Stage("Promote Image")
        .jobs(new Job("Promote", "GOPROM")
            .tasks(Checkout.defaultRepo(),
                GoTasks.promoteImage()));
  }

  public static Stage triggerArgo() {
    return new Stage("Trigger ArgoCD")
        .jobs(new Job("Argo Sync", "GOARGO")
            .tasks(GoTasks.triggerArgo()));
  }
}
