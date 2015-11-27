package com.sergiienko.xrserver.parsers;

import com.sergiienko.xrserver.abstracts.RatesParser;

/**
 * Parser for fixer.io data
 */
public class CDTStaticParser extends RatesParser {
    /**
     * Parse source data and store them in DB
     * @throws Exception when failed
     */
    public final void parse() throws Exception {
        final String CURRENCY_NAME = "CDT";

        persistRate(CURRENCY_NAME, 0.0);
    }
}
