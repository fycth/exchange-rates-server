package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Parser for fixer.io data
 */
public class YahooFinParser extends RatesParser {
    /**
     * Parse source data and store them in DB
     * @throws Exception when failed
     */
    public final void parse() throws Exception {
        JSONObject jsonObj = getJSONFromURL(getURL());
        JSONObject rateObj = jsonObj.getJSONObject("query").getJSONObject("results").getJSONObject("rate");
        String rateID = rateObj.getString("id");
        String rateVal = rateObj.getString("Rate");
        String currencyName = rateID.substring(3);
        persistRate(currencyName, rateVal);
    }
}
