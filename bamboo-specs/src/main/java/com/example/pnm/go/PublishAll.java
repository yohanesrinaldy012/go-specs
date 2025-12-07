package com.example.pnm.go;

import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.SimpleCredentials;
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
        String host = requiredEnv("BAMBOO_HOST");
        String user = requiredEnv("BAMBOO_USER");
        String pass = requiredEnv("BAMBOO_PASS");

        BambooServer server = new BambooServer(host);
        server.setCredentials(new SimpleCredentials(user, pass));

        // Build plans
        Plan plan1 = new Plan_Go_App1().plan();
        Plan plan2 = new Plan_Go_App2().plan();
        Plan plan3 = new Plan_Go_App3().plan();

        // Deployment projects
        Deployment dep1 = new Deployment_Go_App1().deployment();
        Deployment dep2 = new Deployment_Go_App2().deployment();
        Deployment dep3 = new Deployment_Go_App3().deployment();

        server.publish(plan1);
        server.publish(plan2);
        server.publish(plan3);

        server.publish(dep1);
        server.publish(dep2);
        server.publish(dep3);

        System.out.println("Published plans GOA1/2/3 and deployments GOA1/2/3 to Bamboo at " + host);
    }

    private static String requiredEnv(String key) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            throw new IllegalArgumentException("Missing env var: " + key);
        }
        return val;
    }
}
