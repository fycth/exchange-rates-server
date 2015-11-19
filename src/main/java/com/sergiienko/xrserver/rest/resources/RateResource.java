package com.sergiienko.xrserver.rest.resources;

import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.models.GroupModel;
import com.sergiienko.xrserver.models.RateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/rates")
public class RateResource {
    private EntityManager entityManager = EMF.entityManagerFactory.createEntityManager();
    private Logger logger = LoggerFactory.getLogger(RateResource.class);
    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /*
    Get all rates for current hour for all sources
    If from/to parameters are passed, get rates for the specified time limit
    from/to parameters be like 'yyyyMMdd[HHmm]'
     */
    // /rest/current ->
    @Path("/current")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<Integer, List<ResRate>> listCurrentRates(@QueryParam("from") String from, @QueryParam("to") String to) {
        List<ResRate> results = getRatesForSourceID(null,from,to);
        Map<Integer, List<ResRate>> m = new HashMap<>();
        for (ResRate r : results) {
            if (null == m.get(r.getSource())) m.put(r.getSource(), new ArrayList<ResRate>());
            m.get(r.getSource()).add(r);
        }
        return m;
    }

    /*
    Get all rates for the specific source ID
    If 'form' and/or 'to' parameters are passed, print rates for the specific time limit
    from/to parameters be like 'yyyyMMdd[HHmm]'
     */
    // /rest/current/{source} -> all rates for current hour for specific source
    @Path("/source/{source}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String listCurrentRates(@HeaderParam("accept") String accepts, @PathParam("source") Integer sourceID,
                                   @QueryParam("from") String from, @QueryParam("to") String to) {
        List<ResRate> results = getRatesForSourceID(sourceID, from, to);
        String rates;
        if (-1 != accepts.indexOf("xml")) {
            rates = "<?xml version=\"1.0\"?>" + "<rates><sources><source id=\"" + sourceID + "\">" +
                    rates2xml(results) + "</source></sources></rates>";
        } else {
            rates = "{\"rates\":{\"source\":" + sourceID + ",\"rates\":" + rates2json(results) + "}}";
        }
        return rates;
    }

    /*
    Puts currency and rate into DB with 'source' as (-1) and with the current timestamp
     */
    // /rest/put/{currency}/{rate}
    @Path("/put/{currency}/{rate}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RateModel newRate(@PathParam("currency") String currency, @PathParam("rate") String rate) {
        RateModel rm = new RateModel(currency,Double.parseDouble(rate),new Integer(-1));
        entityManager.getTransaction().begin();
        entityManager.persist(rm);
        entityManager.getTransaction().commit();
        entityManager.close();
        logger.info("Put new rate via API: " + currency + ":" + rate);
        return rm;
    }

    /*
    Get all rates for the current hour for the specified {groupID} group
    If 'from' or/and 'to' parameters are passed, return data for the given time limit
    from/to parameters be like 'yyyyMMdd[HHmm]'
     */
    // /rest/rates/group/{groupid}
    @Path("/group/{groupid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<List<ResRate>> listGroupRates(@PathParam("groupid") Integer groupid, @QueryParam("from") String from, @QueryParam("to") String to) {
        entityManager.getTransaction().begin();
        GroupModel group = entityManager.createQuery("from GroupModel where id=:arg1", GroupModel.class).
                setParameter("arg1", groupid).getSingleResult();
        entityManager.getTransaction().commit();
        List<List<ResRate>> results = new ArrayList<>();
        for (Integer sourceID : group.getSources()) {
            List<ResRate> rates = getRatesForSourceID(sourceID,from,to);
            results.add(rates);
        }
        return results;
    }

    /*
    Get all rates for the current hour for the default group
    If 'from' or/and 'to' parameters are passed, return data for the given time limit
    from/to parameters be like 'yyyyMMdd[HHmm]'
    */
    // /rest/rates/group
    @Path("/group")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<List<ResRate>> listDefGroupRates(@QueryParam("from") String from, @QueryParam("to") String to) {
        entityManager.getTransaction().begin();
        GroupModel group = entityManager.createQuery("from GroupModel where dflt=true", GroupModel.class).getSingleResult();
        entityManager.getTransaction().commit();
        List<List<ResRate>> results = new ArrayList<>();
        for (Integer sourceID : group.getSources()) {
            List<ResRate> rates = getRatesForSourceID(sourceID, from, to);
            results.add(rates);
        }
        return results;
    };

    /*
    Gets time in a yyyyMMdd[HHmm] format, rounds it up to the beginning of the hour
    Example: 201501022234 -> 201501022200
    Returns Date
     */
     private Date get_t_min(String from) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        if (null != from) {
            from += "0000".substring(0,12 - from.length());
            try {
                return(df.parse(from));
            } catch (Exception e) {
                logger.error("Unable to parse '" + from + "' from-string. Will use current hour. Exception is: " + e);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    /*
    Gets time in a yyyyMMdd[HHmm] format, rounds it up to the end of the hour
    Example: 201501022234 -> 201501022259
    Returns Date
     */
    private Date get_t_max(String to) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        if (null != to) {
            if (8 == to.length()) to += "2359";
            else if (10 == to.length()) to += "59";
            try {
                return (df.parse(to));
            } catch (Exception e) {
                logger.error("Unable to parse '" + to + "' to-string. Will use current hour. Exception is:  " + e);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 59);
        return calendar.getTime();
    }

    /*
    Get all rates for the specified sourceID.
    If sourceID is null, get rates for all sources
    If from/to parameters are not null, get rates for the appropriate time limit,
    otherwise, get rates for the current hour
    from/to parameters be like 'yyyyMMdd[HHmm]'
     */
    private List<ResRate> getRatesForSourceID(Integer sourceID, String from, String to) {
        Date t_min = get_t_min(from);
        Date t_max = get_t_max(to);
        entityManager.getTransaction().begin();
        Query q;
        if (null == sourceID) {
            q = entityManager.createQuery("SELECT NEW com.sergiienko.xrserver.rest.resources.ResRate(name,rate,MAX(time),source) FROM RateModel WHERE time < :t_max AND time > :t_min GROUP BY name,source,rate", ResRate.class);
        } else {
            q = entityManager.createQuery("SELECT NEW com.sergiienko.xrserver.rest.resources.ResRate(name,rate,MAX(time),source) FROM RateModel WHERE time < :t_max AND time > :t_min AND source = :src GROUP BY name,rate,source", ResRate.class);
            q.setParameter("src", sourceID);
        }
        q.setParameter("t_min", t_min);
        q.setParameter("t_max", t_max);
        List<ResRate> rates = q.getResultList();
        entityManager.getTransaction().commit();
        return rates;
    }

    /*
    Gets list of rates
    Returns JSON string
     */
    private String rates2json(List<ResRate> rates) {
        StringBuilder sb = new StringBuilder("[");
        for (ResRate rate : rates) {
            if (sb.length() > 1) sb.append(",");
            sb.append("{\"currency\":\"" + rate.getName() + "\",\"rate\":" + rate.getRate() + ",\"timestamp\":" +
            rate.getTime().getTime() + ",\"humantime\":\"" + df.format(rate.getTime()) + "\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    /*
    Gets list of rates
    Returns XML string
     */
    private String rates2xml(List<ResRate> rates) {
        StringBuilder sb = new StringBuilder("<currencies>");
        for (ResRate rate : rates) {
            sb.append("<currency>");
            sb.append("<name>" + rate.getName() + "</name>");
            sb.append("<rate>" + rate.getRate() + "</rate>");
            sb.append("<timestamp>" + rate.getTime().getTime() + "</timestamp>");
            sb.append("<humantime>" + df.format(rate.getTime()) + "</humantime>");
            sb.append("</currency>");
        }
        sb.append("</currencies>");
        return sb.toString();
    }
}
