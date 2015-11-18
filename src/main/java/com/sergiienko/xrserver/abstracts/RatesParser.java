package com.sergiienko.xrserver.abstracts;

import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.models.RateModel;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public abstract class RatesParser {
    protected EntityManager em = EMF.entityManagerFactory.createEntityManager();
    protected Integer sourceID;
    protected String url;

    public RatesParser() {};

    public void run(String url, Integer sourceID) {
        this.sourceID = sourceID;
        this.url = url;
        em.getTransaction().begin();
        parse();
        em.getTransaction().commit();
        em.close();
    }

    protected JSONObject getJSONFromURL(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) sb.append((char) cp);
            return new JSONObject(sb.toString());
        } finally {
            is.close();
        }
    }

    protected void persistRate(String currency, String rate) {
        em.persist(new RateModel(currency, Double.parseDouble(rate), sourceID));
    }

    protected void persistRate(String currency, Double rate) {
        em.persist(new RateModel(currency, rate, sourceID));
    }

    protected abstract void parse();
}
