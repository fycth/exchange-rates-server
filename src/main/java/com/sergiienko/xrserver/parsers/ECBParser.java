package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.apache.commons.digester3.Digester;

/**
 * Parser for ECB data
 */
public class ECBParser extends RatesParser {
    /**
     * parse source data and put them in DB
     * @throws Exception when failed
     */
    public final void parse() throws Exception {
        Digester digester = new Digester();
        digester.push(this);
        digester.addCallMethod("gesmes:Envelope/Cube/Cube/Cube", "addRate", 2);
        digester.addCallParam("gesmes:Envelope/Cube/Cube/Cube", 0, "currency");
        digester.addCallParam("gesmes:Envelope/Cube/Cube/Cube", 1, "rate");
        digester.parse(getURL());
    }

    /**
     * Called by digister when something is found by pattern
     * Should be public - otherwise, digester won't be able to call it
     * @param currency currency name
     * @param rate currency rate
     */
    public final void addRate(final String currency, final String rate) {
        persistRate(currency, rate);
    }
}
