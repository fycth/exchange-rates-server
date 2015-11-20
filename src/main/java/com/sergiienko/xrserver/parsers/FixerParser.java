package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class FixerParser extends RatesParser {
    public void parse() throws Exception {
        JSONObject jsonObj = getJSONFromURL(url);
        JSONObject rates = jsonObj.getJSONObject("rates");
        Iterator<?> keys = rates.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            persistRate(key, rates.getDouble(key));
        }
    }
}
