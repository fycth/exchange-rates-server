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

public class Worker implements Job {
    Logger logger = LoggerFactory.getLogger(Worker.class);
    EntityManager entityManager = EMF.entityManagerFactory.createEntityManager();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            AppState.clearState();
            entityManager.getTransaction().begin();
            Query q = entityManager.createQuery("from SourceModel where enabled = :arg1", SourceModel.class);
            q.setParameter("arg1",true);
            List<SourceModel> sources = q.getResultList();
            entityManager.getTransaction().commit();
            entityManager.close();
            for (SourceModel source : sources) {
                try {
                    Class<?> parser_class = Class.forName(source.getParserClassName());
                    Constructor<?> constructor = parser_class.getConstructor();
                    RatesParser parser_instance = (RatesParser) constructor.newInstance();
                    parser_instance.run(source.url, source.getId());
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
