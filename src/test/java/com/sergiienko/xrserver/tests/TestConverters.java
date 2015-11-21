package com.sergiienko.xrserver.tests;

import com.sergiienko.xrserver.rest.resources.RateResource;
import com.sergiienko.xrserver.rest.resources.ResRate;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import static org.junit.Assert.assertEquals;

/*
Here we test how rates are converted to XML and JSON
 */
public class TestConverters {
    private final String XML_string = "<?xml version=\"1.0\"?><rates><sources></sources></rates>";
    private final String JSON_string = "{\"rates\": {\"sources\": {}}}";

    @Test
    public void testEmptyXML() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Map<Integer, List<ResRate>> rates = new HashMap<>();
        Class cl = Class.forName("com.sergiienko.xrserver.rest.resources.RateResource");
        Object ob = cl.newInstance();
        Method m = RateResource.class.getDeclaredMethod("rates2xml", Map.class);
        m.setAccessible(true);
        assertEquals("Testing XML converter", XML_string, m.invoke(ob, rates));
    }

    @Test
    public void testEmptyJSON() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Map<Integer, List<ResRate>> rates = new HashMap<>();
        Class cl = Class.forName("com.sergiienko.xrserver.rest.resources.RateResource");
        Object ob = cl.newInstance();
        Method m = RateResource.class.getDeclaredMethod("rates2json", Map.class);
        m.setAccessible(true);
        assertEquals("Testing JSON converter", JSON_string, m.invoke(ob, rates));
    }
}
