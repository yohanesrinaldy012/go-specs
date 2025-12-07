package com.example.pnm.go;

import com.atlassian.bamboo.specs.util.BambooServer;
import com.example.pnm.go.deployment.Deployment_Go_App1;
import com.example.pnm.go.deployment.Deployment_Go_App2;
import com.example.pnm.go.deployment.Deployment_Go_App3;
import com.example.pnm.go.pipeline.Plan_Go_App1;
import com.example.pnm.go.pipeline.Plan_Go_App2;
import com.example.pnm.go.pipeline.Plan_Go_App3;

/**
 * Manual publisher for the Go sample plans & deployments.
 * Run with environment variables:
 * BAMBOO_HOST=http://<bamboo-host>
 * BAMBOO_USER=<username>
 * BAMBOO_PASS=<password or PAT>
 */
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
    }
}
