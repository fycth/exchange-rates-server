package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.json.JSONObject;
import java.util.Iterator;

/**
 * Parser for fixer.io data
 */
public class FixerParser extends RatesParser {
    /**
     * Parse source data and store them in DB
     * @throws Exception when failed
     */
    public final void parse() throws Exception {
        JSONObject jsonObj = getJSONFromURL(getURL());
        JSONObject rates = jsonObj.getJSONObject("rates");
        Iterator<?> keys = rates.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            persistRate(key, rates.getDouble(key));
        }
    }
}
