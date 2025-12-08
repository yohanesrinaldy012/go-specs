package com.example.pnm.go;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.example.pnm.go.config.AppConfig;
import com.example.pnm.go.config.Defaults;
import com.example.pnm.go.deployment.Deployment_Go_App1;
import com.example.pnm.go.deployment.Deployment_Go_App2;
import com.example.pnm.go.deployment.Deployment_Go_App3;
import com.example.pnm.go.deployment.Deployment_Go_Base;
import com.example.pnm.go.pipeline.Plan_Go_App1;
import com.example.pnm.go.pipeline.Plan_Go_App2;
import com.example.pnm.go.pipeline.Plan_Go_App3;

/**
 * Manual publisher for the Go sample plans & deployments.
 * Kredensial dibaca dari file .credentials di root repo (username=..., password=...),
 * sama seperti contoh express.
 */
@BambooSpec
public class PublishAll {
    public static void main(String[] args) throws Exception {
        BambooServer server = new BambooServer("http://18.143.196.145:8085");

        server.publish(new Plan_Go_App1().plan());
        server.publish(new Plan_Go_App2().plan());
        server.publish(new Plan_Go_App3().plan());

        server.publish(new Deployment_Go_App1().deploymentProject());
        server.publish(new Deployment_Go_App2().deploymentProject());
        server.publish(new Deployment_Go_App3().deploymentProject());

        System.out.println("Published GO plans & deployments to Bamboo.");

        for (AppConfig app : Defaults.APPS) {
            // Panggil helper yang kita buat di Deployment_Go_Base
            EnvironmentPermissions[] envPerms = Deployment_Go_Base.buildEnvPermissions(app);
            
            for (EnvironmentPermissions ep : envPerms) {
                server.publish(ep);
            }
        }
    }
}
