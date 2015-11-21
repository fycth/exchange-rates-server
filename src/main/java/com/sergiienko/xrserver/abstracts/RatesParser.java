package com.sergiienko.xrserver.abstracts;

import com.sergiienko.xrserver.AppState;
import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.models.RateModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Exchange rates data abstract parser
 * Actuall parsers should extend from it
 */
public abstract class RatesParser {
    /**
     * Entity manager object, used for working with DB
     */
    private EntityManager em = EMF.ENTITY_MANAGER_FACTORY.createEntityManager();

    /**
     * Logger object, used for working with logs
     */
    private Logger logger = LoggerFactory.getLogger(RatesParser.class);

    /**
     * Source ID the parser can take data from
     */
    private Integer sourceID;

    /**
     * URL the parser can take data from (source's URL)
     */
    private String url;

    /**
     * Dumb constructor
     */
    public RatesParser() {
    }

    /**
     * Can be used by actual parser for getting source's URL
     * @return source's URL
     */
    protected final String getURL() {
        return url;
    }

    /**
     * Can be used by actual parser for getting source ID
     * @return source ID
     */
    protected final Integer getSourceID() {
        return sourceID;
    }

    /**
     * Can be used by actual parser for writing logs
     * @return logger object
     */
    protected final Logger getLogger() {
        return logger;
    }

    /**
     * Called by the worker
     * @param newURL URl of the source
     * @param newSourceID ID of the source
     */
    public final void run(final String newURL, final Integer newSourceID) {
        this.sourceID = newSourceID;
        this.url = newURL;
        em.getTransaction().begin();
        try {
            parse();
            AppState.updateState(sourceID, true);
        } catch (Exception e) {
            logger.error("Parser for source " + sourceID + " failed: " + e);
            AppState.updateState(sourceID, false);
        }
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Gets data from URL and tries to build JSON object.
     * Can be used by actual parsers for preparing JSON data.
     * @param jsonURL URL of the JSON resource
     * @return JSON object ready to be parsed
     * @throws IOException when IO goes wrong (i.e. HTTP error)
     * @throws JSONException when JSON object creation was failed
     */
    protected final JSONObject getJSONFromURL(final String jsonURL) throws IOException, JSONException {
        InputStream is = new URL(jsonURL).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(sb.toString());
        } finally {
            is.close();
        }
    }

    /**
     * Should be called from the actual parser to persist exchange rate data
     * @param currency currency name
     * @param rate currency rate
     */
    protected final void persistRate(final String currency, final String rate) {
        em.persist(new RateModel(currency, Double.parseDouble(rate), sourceID));
    }

    /**
     * Should be called from the actual parser to persist exchange rate data
     * @param currency currency name
     * @param rate currency rate
     */
    protected final void persistRate(final String currency, final Double rate) {
        em.persist(new RateModel(currency, rate, sourceID));
    }

    /**
     * Should be implemented for certain source.
     * Called to make appropriate actions regarding the source (get and parse data, etc)
     * @throws Exception when something goes wrong
     */
    protected abstract void parse() throws Exception;
}
