package com.sergiienko.xrserver.rest.resources;

import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.models.CurrencyGroupModel;
import com.sergiienko.xrserver.models.GroupModel;
import com.sergiienko.xrserver.models.RateModel;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/**
 * Serves rates REST API
 */
@RequestScoped
@Path("/rates")
public class RateResource {
    /**
     * Entity manager object, for working with DB
     */
    private EntityManager entityManager = EMF.ENTITY_MANAGER_FACTORY.createEntityManager();

    /**
     * Logger object, for working with logs
     */
    private final Logger logger = LoggerFactory.getLogger(RateResource.class);

    /**
     * Data format, used for formatting time to human readable form in XML/JSON responses
     */
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * Data format, used for parsing 'to' and 'from' parameters in REST requests
     */
    private final DateFormat requestDF = new SimpleDateFormat("yyyyMMddHHmm");

    /**
     * Get all rates for current hour for all sources
     * If from/to parameters are passed, get rates for the specified time limit
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * REST /rest/current
     * @param accepts list of media types accepted by client
     * @param from from time point
     * @param to to time point
     * @return string of rates in appropriate format (JSON/XML)
     */
    @Path("/current")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public final String listCurrentRates(@HeaderParam("accept") final String accepts,
                                   @QueryParam("from") final String from,
                                   @QueryParam("to") final String to) {
        List<ResRate> results = getRatesForSourceID(null, from, to);
        Map<Integer, List<ResRate>> m = new HashMap<>();
        for (ResRate r : results) {
            if (null == m.get(r.getSource())) {
                m.put(r.getSource(), new ArrayList<ResRate>());
            }
            m.get(r.getSource()).add(r);
        }
        if (-1 != accepts.indexOf("xml")) {
            return rates2xml(m);
        } else {
            return rates2json(m);
        }
    }

    /**
     * Get all current hour rates for the specific source ID
     * If 'form' and/or 'to' parameters are passed, print rates for the specific time limit
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * REST /rest/current/{source}
     * @param accepts list of media types accepted by client
     * @param sourceID source ID
     * @param from from time point
     * @param to to time point
     * @param legacy if this parameter is passed, generate legacy XML (like ECB XML feed)
     * @return string of rates in appropriate format (JSON/XML)
     */
    @Path("/source/{source}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public final String listCurrentRates(@HeaderParam("accept") final String accepts,
                                         @PathParam("source") final Integer sourceID,
                                   @QueryParam("from") final String from,
                                   @QueryParam("to") final String to,
                                   @QueryParam("legacy") final String legacy) {
        List<ResRate> rates = getRatesForSourceID(sourceID, from, to);
        Map<Integer, List<ResRate>> m = new HashMap<>();
        m.put(sourceID, rates);
        if (null != legacy) {
            return rates2legacyXML(m);
        }
        if (-1 != accepts.indexOf("xml")) {
            return rates2xml(m);
        } else {
            return rates2json(m);
        }
    }

    /**
     * Puts currency and rate into DB with 'source' as (-1) and with the current timestamp
     * REST /rest/put/{currency}/{rate}
     * @param currency currency name
     * @param rate currency rate
     * @return new rate object formatted in JSON
     */
    @Path("/put/{currency}/{rate}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final RateModel newRate(@PathParam("currency") final String currency, @PathParam("rate") final String rate) {
        RateModel rm = new RateModel(currency, Double.parseDouble(rate), -1);
        entityManager.getTransaction().begin();
        entityManager.persist(rm);
        entityManager.getTransaction().commit();
        entityManager.close();
        logger.info("Put new rate via API: " + currency + ":" + rate);
        return rm;
    }

    /**
     * Get all rates for the current hour for the specified {groupID} group
     * If 'from' or/and 'to' parameters are passed, return data for the given time limit
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * REST /rest/rates/group/{groupid}
     * @param accepts list of media types accepted by client
     * @param groupid group ID
     * @param from from time point
     * @param to to time point
     * @return string of rates in appropriate format (JSON/XML)
     */
    @Path("/group/{groupid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public final String listGroupRates(@HeaderParam("accept") final String accepts,
                                              @PathParam("groupid") final Integer groupid,
                                              @QueryParam("from") final String from, @QueryParam("to") final String to) {
        entityManager.getTransaction().begin();
        GroupModel group = entityManager.createQuery("from GroupModel where id=:arg1", GroupModel.class).
                setParameter("arg1", groupid).getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        Map<Integer, List<ResRate>> rates = getRateForGroup(group, from, to);
        if (-1 != accepts.indexOf("xml")) {
            return rates2xml(rates);
        } else {
            return rates2json(rates);
        }
    }

    /**
     * Get all rates for the current hour for the default group
     * If 'from' or/and 'to' parameters are passed, return data for the given time limit
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * REST /rest/rates/group
     * @param accepts list of media types accepted by client
     * @param from from time point
     * @param to to time point
     * @return string of rates in appropriate format (JSON/XML)
     */
    @Path("/group")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public final String listDefGroupRates(@HeaderParam("accept") final String accepts,
                                                 @QueryParam("from") final String from,
                                                 @QueryParam("to") final String to) {
        entityManager.getTransaction().begin();
        GroupModel group = entityManager.createQuery("from GroupModel where dflt=true", GroupModel.class).getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        Map<Integer, List<ResRate>> rates = getRateForGroup(group, from, to);
        if (-1 != accepts.indexOf("xml")) {
            return rates2xml(rates);
        } else {
            return rates2json(rates);
        }
    }

    /**
     * Get all rates for the current hour for the specified {groupID} currency group
     * If 'from' or/and 'to' parameters are passed, return data for the given time limit
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * REST /rest/rates/group/{groupid}
     * @param accepts list of media types accepted by client
     * @param groupid group ID
     * @param from from time point
     * @param to to time point
     * @param legacy if not null, return legacy XML (like ECB XML feed)
     * @return string of rates in appropriate format (JSON/XML)
     */
    @Path("/currencygroup/{groupid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public final String getCurrencyGroupRates(@HeaderParam("accept") final String accepts,
                                       @PathParam("groupid") final Integer groupid,
                                       @QueryParam("from") final String from, @QueryParam("to") final String to,
                                       @QueryParam("legacy") final String legacy) {
        entityManager.getTransaction().begin();
        CurrencyGroupModel group = entityManager.createQuery("from CurrencyGroupModel where id=:arg1", CurrencyGroupModel.class).
                setParameter("arg1", groupid).getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        List<ResRate> rates = getRateForCurrencyGroup(group, from, to);
        if (null != legacy) {
            return rates2legacyXML(rates);
        }
        if (-1 != accepts.indexOf("xml")) {
            return rates2xml(rates);
        } else {
            return rates2json(rates);
        }
    }

    /**
     * Get all rates for the current hour for the default currency group
     * If 'from' or/and 'to' parameters are passed, return data for the given time limit
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * REST /rest/rates/group
     * @param accepts list of media types accepted by client
     * @param from from time point
     * @param to to time point
     * @param legacy if not null, return legacy XML (like ECB XML feed)
     * @return string of rates in appropriate format (JSON/XML)
     */
    @Path("/currencygroup")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public final String getDefCurrencyGroupRates(@HeaderParam("accept") final String accepts,
                                          @QueryParam("from") final String from,
                                          @QueryParam("to") final String to,
                                          @QueryParam("legacy") final String legacy) {
        entityManager.getTransaction().begin();
        CurrencyGroupModel group = entityManager.createQuery("from CurrencyGroupModel where dflt=true", CurrencyGroupModel.class).getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        List<ResRate> rates = getRateForCurrencyGroup(group, from, to);
        if (null != legacy) {
            return rates2legacyXML(rates);
        }
        if (-1 != accepts.indexOf("xml")) {
            return rates2xml(rates);
        } else {
            return rates2json(rates);
        }
    }

    /**
     * Get rates for specific currency group
     * @param group currency group object
     * @param from from time point
     * @param to to time point
     * @return rates for specific group
     */
    private List<ResRate> getRateForCurrencyGroup(final CurrencyGroupModel group, final String from, final String to) {
        List<ResRate> rates = new ArrayList<>();
        String[] currencyNames = group.getCurrencies();
        Integer[] sources = group.getSources();
        for (int j = 0; j < sources.length; j++) {
            List<ResRate> temprates = getRatesForSourceID(sources[j], from, to);
            for (ResRate rate : temprates) {
                if (rate.getName().equals(currencyNames[j])) {
                    rates.add(rate);
                    break;
                }
            }
        }
        return rates;
    }

    /**
     * Get rates for specific group
     * @param group group object
     * @param from from time point
     * @param to to time point
     * @return rates for specific group
     */
    private Map<Integer, List<ResRate>> getRateForGroup(final GroupModel group, final String from, final String to) {
        Map<Integer, List<ResRate>> m = new HashMap<>();
        for (Integer sourceID : group.getSources()) {
            List<ResRate> rates = getRatesForSourceID(sourceID, from, to);
            for (ResRate r : rates) {
                if (null == m.get(r.getSource())) {
                    m.put(r.getSource(), new ArrayList<ResRate>());
                }
                m.get(r.getSource()).add(r);
            }
        }
        return m;
    }

    /**
     * Gets time in a yyyyMMdd[HHmm] format, rounds it up to the beginning of the hour
     * Example: 201501022234 -> 201501022200
     * @param from from time limit
     * @return calculated Date
     */
     private Date getTimeMin(final String from) {
         final Integer maxTimeLen = 12;
        if (null != from) {
            String actualFrom = from;
            actualFrom += "0000".substring(0, maxTimeLen - from.length());
            try {
                return requestDF.parse(actualFrom);
            } catch (Exception e) {
                logger.error("Unable to parse '" + actualFrom + "' from-string. Will use current hour. Exception is: " + e);
            }
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.HOUR, -1); // take previous hour as well - parser might be starting in the middle of the hour
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    /**
     * Gets time in a yyyyMMdd[HHmm] format, rounds it up to the end of the hour
     * Example: 201501022234 -> 201501022259
     * @param to to time limit
     * @return calculated Date
     */
    private Date getTimeMax(final String to) {
        final Integer maxMinute = 59;
        final Integer maxDateLength = 10;
        final Integer minDateLength = 8;

        if (null != to) {
            String actualTo = to;
            if (minDateLength == to.length()) {
                actualTo += "2359";
            } else if (maxDateLength == to.length()) {
                actualTo += "59";
            }
            try {
                return requestDF.parse(actualTo);
            } catch (Exception e) {
                logger.error("Unable to parse '" + actualTo + "' to-string. Will use current hour. Exception is:  " + e);
            }
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, maxMinute);
        return calendar.getTime();
    }

    /**
     * Get all rates for the specified sourceID.
     * If sourceID is null, get rates for all sources
     * If from/to parameters are not null, get rates for the appropriate time limit,
     * otherwise, get rates for the current hour
     * from/to parameters be like 'yyyyMMdd[HHmm]'
     * @param sourceID ID of the source we need rates from
     * @param from from time point
     * @param to to time point
     * @return list of rates
     */
    public final List<ResRate> getRatesForSourceID(final Integer sourceID, final String from, final String to) {
        Date tMin = getTimeMin(from);
        Date tMax = getTimeMax(to);
        entityManager.getTransaction().begin();
        Query q;
        if (null == sourceID) {
            q = entityManager.createQuery("SELECT NEW com.sergiienko.xrserver.rest.resources.ResRate(name,rate,MAX(time),source) FROM RateModel WHERE time < :t_max AND time > :t_min GROUP BY name,source,rate", ResRate.class);
        } else {
            q = entityManager.createQuery("SELECT NEW com.sergiienko.xrserver.rest.resources.ResRate(name,rate,MAX(time),source) FROM RateModel WHERE time < :t_max AND time > :t_min AND source = :src GROUP BY name,rate,source", ResRate.class);
            q.setParameter("src", sourceID);
        }
        q.setParameter("t_min", tMin);
        q.setParameter("t_max", tMax);
        List<ResRate> rates = q.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return rates;
    }

    /**
     * Convert rates into XML
     * @param rates map of list of rates
     * @return XML string
     */
    private String rates2xml(final Map<Integer, List<ResRate>> rates) {
        StringBuilder res = new StringBuilder("<?xml version=\"1.0\"?><rates><sources>");
        for (Map.Entry<Integer, List<ResRate>> entry : rates.entrySet()) {
            res.append("<source id=\"" + entry.getKey() + "\"><currencies>");
            for (ResRate rate : entry.getValue()) {
                res.append("<currency>");
                res.append("<name>" + rate.getName() + "</name>");
                res.append("<rate>" + rate.getRate() + "</rate>");
                res.append("<timestamp>" + rate.getTime().getTime() + "</timestamp>");
                res.append("<humantime>" + df.format(rate.getTime()) + "</humantime>");
                res.append("</currency>");
            }
            res.append("</currencies></source>");
        }
        res.append("</sources></rates>");
        return res.toString();
    }

    /**
     * Convert rates into XML
     * @param rates list of rates
     * @return XML string
     */
    private String rates2xml(final List<ResRate> rates) {
        StringBuilder res = new StringBuilder("<?xml version=\"1.0\"?><rates><currencies>");
        for (ResRate rate : rates) {
            res.append("<currency>");
            res.append("<name>" + rate.getName() + "</name>");
            res.append("<rate>" + rate.getRate() + "</rate>");
            res.append("<timestamp>" + rate.getTime().getTime() + "</timestamp>");
            res.append("<humantime>" + df.format(rate.getTime()) + "</humantime>");
            res.append("</currency>");
        }
        res.append("</currencies></rates>");
        return res.toString();
    }

    /**
     * Convert rates into JSON
     * @param rates map of list of rates
     * @return JSON string
     */
    private String rates2json(final Map<Integer, List<ResRate>> rates) {
        StringBuilder res = new StringBuilder("{\"rates\": {\"sources\": {");
        int j = 0;
        for (Map.Entry<Integer, List<ResRate>> entry : rates.entrySet()) {
            if (j > 0) {
                res.append(",");
            } else {
                j++;
            }
            res.append("\"" + entry.getKey() + "\":[");
            int i = 0;
            for (ResRate rate : entry.getValue()) {
                if (i > 0) {
                    res.append(",");
                } else {
                    i++;
                }
                res.append("\"" + rate.getName() + "\":{\"rate\":" + rate.getRate() + ",\"timestamp\":"
                        + rate.getTime().getTime() + ",\"humantime\":\"" + df.format(rate.getTime()) + "\"}");
            }
            res.append("]");
        }
        res.append("}}}");
        return res.toString();
    }

    /**
     * Convert rates into JSON
     * @param rates list of rates
     * @return JSON string
     */
    private String rates2json(final List<ResRate> rates) {
        StringBuilder res = new StringBuilder("{\"rates\": {");
        int i = 0;
        for (ResRate rate : rates) {
            if (i > 0) {
                res.append(",");
            } else {
                i++;
            }
            res.append("\"" + rate.getName() + "\":{\"rate\":" + rate.getRate() + ",\"timestamp\":"
                        + rate.getTime().getTime() + ",\"humantime\":\"" + df.format(rate.getTime()) + "\"}");
        }
        res.append("}}");
        return res.toString();
    }

    /**
     * Convert rates into legacy XML (ECB XML feed)
     * @param rates map of list of rates
     * @return legacy XML
     */
    private String rates2legacyXML(final Map<Integer, List<ResRate>> rates) {
        StringBuilder res = new StringBuilder("<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n");
        res.append("<gesmes:subject>Reference rates</gesmes:subject>\n");
        res.append("<gesmes:Sender>\n" + "<gesmes:name>European Central Bank</gesmes:name>\n</gesmes:Sender>");
        Calendar calendar = GregorianCalendar.getInstance();
        DateFormat xmldf = new SimpleDateFormat("yyyy-MM-dd");
        res.append("<Cube>\n<Cube time=\"" + xmldf.format(calendar.getTime()) + "\">");
        for (Map.Entry<Integer, List<ResRate>> entry : rates.entrySet()) {
            for (ResRate rate : entry.getValue()) {
                res.append("<Cube currency=\"" + rate.getName() + "\" rate=\"" + rate.getRate() + "\" />");
            }
            res.append("</Cube>\n</Cube>\n</gesmes:Envelope>");
        }
        return res.toString();
    }

    /**
     * Convert rates into legacy XML (ECB XML feed)
     * @param rates list of rates
     * @return legacy XML
     */
    private String rates2legacyXML(final List<ResRate> rates) {
        StringBuilder res = new StringBuilder("<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n");
        res.append("<gesmes:subject>Reference rates</gesmes:subject>\n");
        res.append("<gesmes:Sender>\n" + "<gesmes:name>European Central Bank</gesmes:name>\n</gesmes:Sender>");
        Calendar calendar = GregorianCalendar.getInstance();
        DateFormat xmldf = new SimpleDateFormat("yyyy-MM-dd");
        res.append("<Cube>\n<Cube time=\"" + xmldf.format(calendar.getTime()) + "\">");
        for (ResRate rate : rates) {
            res.append("<Cube currency=\"" + rate.getName() + "\" rate=\"" + rate.getRate() + "\" />");
        }
        res.append("</Cube>\n</Cube>\n</gesmes:Envelope>");
        return res.toString();
    }
}
