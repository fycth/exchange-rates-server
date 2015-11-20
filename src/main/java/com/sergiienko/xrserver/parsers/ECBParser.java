package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.apache.commons.digester3.Digester;

public class ECBParser extends RatesParser {
    public void parse() throws Exception {
        Digester digester = new Digester();
        digester.push(this);
        digester.addCallMethod("gesmes:Envelope/Cube/Cube/Cube", "addRate", 2);
        digester.addCallParam("gesmes:Envelope/Cube/Cube/Cube", 0, "currency");
        digester.addCallParam("gesmes:Envelope/Cube/Cube/Cube", 1, "rate");
        digester.parse(url);
    }

    // should be public - otherwise, digester won't be able to call it
    public void addRate(String currency, String rate) {
        persistRate(currency,rate);
    }
}
