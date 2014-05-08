/*
 * Copyright 2014 ESHA Research, Inc. All rights reserved.
 */
package example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.jetty.servlets.ProxyServlet;
import org.hibernate.validator.constraints.NotEmpty;

import static example.ExampleService.ExampleConfiguration;

public final class ExampleService extends Service<ExampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ExampleService().run(args);
    }

    @Override
    public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {
        bootstrap.setName("example");
    }

    @Override
    public void run(final ExampleConfiguration configuration, final Environment environment) throws Exception {
        environment.addResource(new ExampleResource());

        final String proxyPrefix = configuration.getProxyPrefix();
        final String proxyTo = configuration.getProxyTo();
        environment.addServlet(ProxyServlet.Transparent.class, proxyPrefix + "/*")
            .setInitParam("Prefix", proxyPrefix)
            .setInitParam("ProxyTo", proxyTo);
    }

    public static class ExampleConfiguration extends Configuration {
        @NotEmpty
        private String proxyPrefix = "/proxy";

        @NotEmpty
        private String proxyTo = "http://www.google.com/";

        @JsonProperty
        public String getProxyPrefix() {
            return this.proxyPrefix;
        }

        @JsonProperty
        public String getProxyTo() {
            return this.proxyTo;
        }
    }

    @Path("/")
    public static class ExampleResource {
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "ok";
        }
    }
}
