package com.sergiienko.xrserver;

import java.util.HashMap;
import java.util.Map;

/**
 * State of parsers/sources
 * Mostly implemented for integrating with Zabbix
 */
public final class AppState {
    /**
     * State object, used for collecting last sources' states
     */
    private static Map<Integer, Boolean> state = new HashMap<>();

    /**
     * Dumb constructor
     */
    private AppState() {
    }

    /**
     *
     * @param parserID ID of the parser (actually, a source's ID)
     * @param parserState last state of the parser (related to the certain source, not to parser itself)
     */
    public static synchronized void updateState(final Integer parserID, final Boolean parserState) {
        state.put(parserID, parserState);
    }

    /**
     * Clear the state
     */
    public static void clearState() {
        synchronized (AppState.class) {
            state = new HashMap<>();
        }
    }

    /**
     * Get all sources' last state
     * @return sources' state
     */
    public static Map<Integer, Boolean> getState() {
        return state;
    }
}
