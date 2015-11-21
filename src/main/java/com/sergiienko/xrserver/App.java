package com.sergiienko.xrserver;

import com.sergiienko.xrserver.workers.Worker;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class
 */
public final class App {
    /**
     * Main app function
     * @param args command line arguments
     * @throws Exception when something is wrong
     */
    public static void main(final String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(App.class);
        final Integer appPort = 8189;

        logger.info("Started");

        // create embedded jetty server
        Server jettyServer = new Server();
        ServerConnector jettyConnector = new ServerConnector(jettyServer);
        jettyConnector.setName("xrserver");
        jettyConnector.setHost("0.0.0.0");
        jettyConnector.setPort(appPort);
        jettyServer.addConnector(jettyConnector);

        // configure jetty REST servlets
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.sergiienko.xrserver.rest.resources");
        ServletContextHandler restServletContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        restServletContext.setContextPath("/rest");
        restServletContext.addServlet(sh, "/*");

        // configure jetty web servlets
        ServletHolder webSH = new ServletHolder(ServletContainer.class);
        webSH.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.sergiienko.xrserver.web.resources");
        ServletContextHandler webServletContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        webServletContext.setContextPath("/admin");
        webServletContext.addServlet(webSH, "/*");

        // configure jetty static files handler
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setResourceBase("src/main/webapp/static");
        ContextHandler ctx = new ContextHandler("/admin/static");
        ctx.setHandler(resourceHandler);

        // set jetty handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {ctx, webServletContext, restServletContext});
        jettyServer.setHandler(handlers);

        // start jobs/workers
        JobKey jobKeyA = new JobKey("RatesWorker", "worker_group_1");
        JobDetail jobA = JobBuilder.newJob(Worker.class).withIdentity(jobKeyA).build();
        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("RatesWorkerTrigger", "worker_group_1")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever()).build();
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobA, trigger1);

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            EMF.closeFactory();
            jettyServer.destroy();
            logger.info("Stopped");
        }
    }

    /**
     * Dumb constructor
     */
    private App() {
    }
}
