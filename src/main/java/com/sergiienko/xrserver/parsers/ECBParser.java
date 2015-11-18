package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ECBParser extends RatesParser {
    Logger logger = LoggerFactory.getLogger(ECBParser.class);
    public void parse() {
        Digester digester = new Digester();
        digester.push(this);
        digester.addCallMethod("gesmes:Envelope/Cube/Cube/Cube", "addRate", 2);
        digester.addCallParam("gesmes:Envelope/Cube/Cube/Cube", 0, "currency");
        digester.addCallParam("gesmes:Envelope/Cube/Cube/Cube", 1, "rate");
        try {
            digester.parse(url);
        } catch (Exception e) {
            logger.error("Unable to update rates from source ID " + sourceID + " -> " + e);
        }
    }

    // should be public - otherwise, digester won't be able to call it
    public void addRate(String currency, String rate) {
        persistRate(currency,rate);
        logger.debug("GET ---> currency: " + currency + " rate: " + rate);
    }
}
