package com.sergiienko.xrserver.workers;

/**
 * Author: ${FULLNAME}
 * Date: 11/12/15
 * Time: 7:49 PM
 */

import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.models.SourceModel;
import com.sergiienko.xrserver.models.XMLParserModel;
import com.sergiienko.xrserver.parsers.XMLParser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class XMLWorker implements Job {
    Logger logger = LoggerFactory.getLogger(XMLWorker.class);
    EntityManager entityManager = EMF.entityManagerFactory.createEntityManager();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            entityManager.getTransaction().begin();
            List<SourceModel> sources = entityManager.createQuery("from SourceModel", SourceModel.class).getResultList();
            for (SourceModel source : sources) {
                Query q = entityManager.createQuery("from XMLParserModel xpm where xpm.source=:arg1", XMLParserModel.class);
                q.setParameter("arg1", source.getId());
                XMLParserModel parser = (XMLParserModel) q.getSingleResult();
                XMLParser x = new XMLParser(source.getId());
                try {
                    x.run(source.url, parser.pattern_section, parser.pattern_currency, parser.pattern_rate, parser.attribute_currency, parser.attribute_rate);
                } catch (Exception e) {
                    logger.error("Can't update rates for source " + source.getId() + ", parser exception: " + e);
                }
            }
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            JobExecutionException e2 = new JobExecutionException(e);
            e2.refireImmediately();
        }
    }
}
