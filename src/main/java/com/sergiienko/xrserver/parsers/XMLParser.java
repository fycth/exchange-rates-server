package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.models.RateModel;
import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import javax.persistence.EntityManager;
import java.io.IOException;

public class XMLParser {
    Logger logger = LoggerFactory.getLogger(XMLParser.class);
    private EntityManager em = EMF.entityManagerFactory.createEntityManager();
    private Long source;
    public XMLParser(Long source) {
        this.source = source;
    }
    public void run(String url,
                     String pattern_section,
                     String pattern_currency,
                     String pattern_rate,
                     String attribute_currency,
                     String attribute_rate) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.push(this);
        digester.addCallMethod(pattern_section, "addRate", 2);
        if (0 == attribute_currency.length()) digester.addCallParam(pattern_currency, 0);
        else digester.addCallParam(pattern_currency, 0, attribute_currency);
        if (0 == attribute_rate.length()) digester.addCallParam(pattern_rate, 1);
        else digester.addCallParam(pattern_rate, 1, attribute_rate);
        em.getTransaction().begin();
        digester.parse(url);
        em.getTransaction().commit();
        em.close();
    }
    public void addRate(String currency, String rate) {
        em.persist(new RateModel(currency,rate,source));
        logger.debug("GET ---> currency: " + currency + " rate: " + rate);
    }

}
