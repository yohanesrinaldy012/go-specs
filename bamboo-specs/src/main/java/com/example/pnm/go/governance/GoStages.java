package com.example.pnm.go.governance;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.example.pnm.go.lib.Checkout;
import com.example.pnm.go.lib.GoTasks;

public final class GoStages {
  private GoStages() {}

  /** Stage untuk build & test Go. */
  public static Stage buildAndTest() {
    return new Stage("Build and Test")
        .jobs(new Job("Go Build Test", "GOBT")
            .tasks(Checkout.defaultRepo(),
                GoTasks.prepareCache(),
                GoTasks.goDeps(),
                GoTasks.goTest(),
                GoTasks.goBuild()));
  }

  /** Stage build image (re-run build supaya binary ada di workspace). */
  public static Stage dockerBuildPush() {
    return new Stage("Docker Image")
        .jobs(new Job("Build Push Image", "GOIMG")
            .tasks(Checkout.defaultRepo(),
                GoTasks.prepareCache(),
                GoTasks.goDeps(),
                GoTasks.goBuild(),
                GoTasks.dockerBuildPush()));
  }

  /** Stage promote image (retag push tanpa rebuild). */
  public static Stage promoteImage() {
    return new Stage("Promote Image")
        .jobs(new Job("Promote", "GOPROM")
            .tasks(Checkout.defaultRepo(),
                GoTasks.promoteImage()));
  }
}
