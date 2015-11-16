package com.sergiienko.xrserver;

import com.sergiienko.xrserver.workers.XMLWorker;
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

        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.sergiienko.xrserver.rest.resources");

        Server jettyServer = new Server(8189);
        ServletContextHandler context = new ServletContextHandler(jettyServer,"/",ServletContextHandler.NO_SESSIONS);
        context.addServlet(sh, "/*");

        JobKey jobKeyA = new JobKey("XMLWorker", "worker_group1");
        JobDetail jobA = JobBuilder.newJob(XMLWorker.class).withIdentity(jobKeyA).build();
        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("XMLWorkerTrigger", "worker_group1")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever()).build();
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobA, trigger1);

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
