package com.sergiienko.xrserver;

import java.util.HashMap;
import java.util.Map;

public class AppState {
    private static Map<Integer,Boolean> state = new HashMap<>();
    public static synchronized void updateState(Integer parserID, Boolean parserState) {
        state.put(parserID,parserState);
    }
    public static void clearState() {
        synchronized (AppState.class) {
            state = new HashMap<>();
        }
    }
    public static Map<Integer,Boolean> getState() {
        return state;
    }
}
