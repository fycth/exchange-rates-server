package com.sergiienko.xrserver.tests;

import static org.junit.Assert.assertEquals;

import com.sergiienko.xrserver.rest.resources.RateResource;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TestBasic {

    @Test
    public void getTmax() throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException, ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 59);
        Date now = calendar.getTime();

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        Date d1 = df.parse("201501012359");
        Date d2 = df.parse("201501011059");
        Date d3 = df.parse("201501011020");

        Class cl = Class.forName("com.sergiienko.xrserver.rest.resources.RateResource");
        Object ob = cl.newInstance();
        Method m = RateResource.class.getDeclaredMethod("get_t_max", String.class);
        m.setAccessible(true);
        assertEquals("Testing null parameter", now, m.invoke(ob, (String)null));
        assertEquals("Testing yyyyMMdd", d1, m.invoke(ob, new String("20150101")));
        assertEquals("Testing yyyyMMddHH", d2, m.invoke(ob, new String("2015010110")));
        assertEquals("Testing yyyyMMddHHmm" + d3, d3, m.invoke(ob, new String("201501011020")));
    }

    @Test
    public void getTmin() throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException, ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date now = calendar.getTime();

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        Date d1 = df.parse("201501010000");
        Date d2 = df.parse("201501011000");
        Date d3 = df.parse("201501011020");

        Class cl = Class.forName("com.sergiienko.xrserver.rest.resources.RateResource");
        Object ob = cl.newInstance();
        Method m = RateResource.class.getDeclaredMethod("get_t_min", String.class);
        m.setAccessible(true);
        assertEquals("Testing null parameter", now, m.invoke(ob, (String)null));
        assertEquals("Testing yyyyMMdd", d1, m.invoke(ob, new String("20150101")));
        assertEquals("Testing yyyyMMddHH", d2, m.invoke(ob, new String("2015010110")));
        assertEquals("Testing yyyyMMddHHmm" + d3, d3, m.invoke(ob, new String("201501011020")));
    }

}
