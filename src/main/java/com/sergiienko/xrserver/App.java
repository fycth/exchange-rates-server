package com.sergiienko.xrserver;

import com.sergiienko.xrserver.abstracts.RatesParser;
import com.sergiienko.xrserver.parsers.FixerParser;
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
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(App.class);

        logger.info("Started");

        // create embedded jetty server
        Server jettyServer = new Server();
        ServerConnector jettyConnector = new ServerConnector(jettyServer);
        jettyConnector.setName("xrserver");
        jettyConnector.setHost("0.0.0.0");
        jettyConnector.setPort(8189);
        jettyServer.addConnector(jettyConnector);

        // configure jetty REST servlets
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.sergiienko.xrserver.rest.resources");
        ServletContextHandler restServletContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        restServletContext.setContextPath("/rest");
        restServletContext.addServlet(sh, "/*");

        // configure jetty web servlets
        ServletHolder web_sh = new ServletHolder(ServletContainer.class);
        web_sh.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.sergiienko.xrserver.web.resources");
        ServletContextHandler webServletContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        webServletContext.setContextPath("/admin");
        webServletContext.addServlet(web_sh, "/*");

        // configure jetty static files handler
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(false);
//        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("src/main/webapp/static");
        ContextHandler ctx = new ContextHandler("/admin/static");
        ctx.setHandler(resource_handler);

        // set jetty handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { ctx, webServletContext, restServletContext });
        jettyServer.setHandler(handlers);

        // start jobs/workers
        JobKey jobKeyA = new JobKey("RatesWorker", "worker_group_1");
        JobDetail jobA = JobBuilder.newJob(Worker.class).withIdentity(jobKeyA).build();
        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("RatesWorkerTrigger", "worker_group_1")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever()).build();
//        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
//        scheduler.start();
//        scheduler.scheduleJob(jobA, trigger1);

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            EMF.Close();
            jettyServer.destroy();
            logger.info("Stopped");
        }
    }
}
