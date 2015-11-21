package com.sergiienko.xrserver.workers;

import com.sergiienko.xrserver.AppState;
import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.abstracts.RatesParser;
import com.sergiienko.xrserver.models.SourceModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * We start worker every amount of time
 * Worker starts parsers
 */
public class Worker implements Job {
    /**
     * Logger object, for writing logs
     */
    private Logger logger = LoggerFactory.getLogger(Worker.class);

    /**
     * Entity manager object, for working with DB
     */
    private EntityManager entityManager = EMF.ENTITY_MANAGER_FACTORY.createEntityManager();

    @Override
    public final void execute(final JobExecutionContext context) throws JobExecutionException {
        try {
            AppState.clearState();
            entityManager.getTransaction().begin();
            Query q = entityManager.createQuery("from SourceModel where enabled = :arg1", SourceModel.class);
            q.setParameter("arg1", true);
            List<SourceModel> sources = q.getResultList();
            entityManager.getTransaction().commit();
            entityManager.close();
            for (SourceModel source : sources) {
                try {
                    Class<?> parserClass = Class.forName(source.getParserClassName());
                    Constructor<?> constructor = parserClass.getConstructor();
                    RatesParser parserInstance = (RatesParser) constructor.newInstance();
                    parserInstance.run(source.getUrl(), source.getId());
                } catch (Exception e) {
                    logger.error("Can't update rates for source " + source.getId() + ", parser exception: " + e);
                }
            }
        } catch (Exception e) {
            JobExecutionException e2 = new JobExecutionException(e);
            e2.refireImmediately();
        }
    }
}
