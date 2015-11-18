package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class FixerParser extends RatesParser {
    Logger logger = LoggerFactory.getLogger(FixerParser.class);

    public void parse() {
        try {
            JSONObject jsonObj = getJSONFromURL(url);
            JSONObject rates = jsonObj.getJSONObject("rates");
            Iterator<?> keys = rates.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                persistRate(key, rates.getDouble(key));
            }
        } catch (Exception e) {
            logger.error("Unable to parse fixer " + e);
        }
    }
}
