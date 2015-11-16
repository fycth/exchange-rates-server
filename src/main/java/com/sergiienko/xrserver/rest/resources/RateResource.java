package com.sergiienko.xrserver.rest.resources;

import com.sergiienko.xrserver.EMF;
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
    EntityManager entityManager = EMF.entityManagerFactory.createEntityManager();
    Logger logger = LoggerFactory.getLogger(RateResource.class);

    @Path("/list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RateModel> find2() {
        entityManager.getTransaction().begin();
        List<RateModel> results = entityManager.createQuery( "from RateModel", RateModel.class ).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return results;
    }

// /rest/current -> all rates for current hour along all groups (sources)
// from/to -> YYYYMMDDHH
    @Path("/current")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<Long, List<ResRate>> listCurrentRates(@QueryParam("from") String from, @QueryParam("to") String to) {
        Date t_min = get_t_min(from);
        Date t_max = get_t_max(to);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("SELECT NEW com.sergiienko.xrserver.rest.resources.ResRate(name,rate,MAX(time),source) FROM RateModel WHERE time < :t_max AND time > :t_min GROUP BY name,source,rate", ResRate.class);
        q.setParameter("t_min",t_min);
        q.setParameter("t_max",t_max);
        List<ResRate> results = q.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        Map<Long, List<ResRate>> m = new HashMap<Long,List<ResRate>>();
        for (ResRate r : results) {
            if (null == m.get(r.getSource())) m.put(r.getSource(), new ArrayList<ResRate>());
            m.get(r.getSource()).add(r);
        }
        return m;
    }

    // /rest/current/{source} -> all rates for current hour for specific source
    @Path("/current/{source}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResRate> listCurrentRates(@PathParam("source") Long source, @QueryParam("from") String from, @QueryParam("to") String to) {
        Date t_min = get_t_min(from);
        Date t_max = get_t_max(to);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("SELECT NEW com.sergiienko.xrserver.rest.resources.ResRate(name,rate,MAX(time),source) FROM RateModel WHERE time < :t_max AND time > :t_min AND source = :src GROUP BY name,rate,source", ResRate.class);
        q.setParameter("t_min",t_min);
        q.setParameter("t_max",t_max);
        q.setParameter("src",source);
        List<ResRate> results = q.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return results;
    }

    // /rest/put/{currency}/{rate} -> manually put currency rate
    @Path("/put/{currency}/{rate}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RateModel newRate(@PathParam("currency") String currency, @PathParam("rate") String rate) {
        RateModel rm = new RateModel(currency,rate,new Long(-1));
        entityManager.getTransaction().begin();
        entityManager.persist(rm);
        entityManager.getTransaction().commit();
        entityManager.close();
        logger.info("Put new rate via API: " + currency + ":" + rate);
        return rm;
    }

    private Date get_t_min(String from) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        if (null != from) {
            from += "0000".substring(0,12 - from.length());
            try {
                return(df.parse(from));
            } catch (Exception e) {
                logger.error("Unable to parse '" + from + "' from-string: " + e);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    private Date get_t_max(String to) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        if (null != to) {
            if (8 == to.length()) to += "2359";
            else if (10 == to.length()) to += "59";
            try {
                return (df.parse(to));
            } catch (Exception e) {
                logger.error("Unable to parse '" + to + "' to-string: " + e);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 59);
        return calendar.getTime();
    }
}
