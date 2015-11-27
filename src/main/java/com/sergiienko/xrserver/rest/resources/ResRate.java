package com.sergiienko.xrserver.rest.resources;

import java.util.Date;

/**
 * Rate model for operating with some DB queries
 */
public class ResRate {
    /**
     * Currency name
     */
    private String name;

    /**
     * Currency exchange rate
     */
    private Double rate;

    /**
     * Time when the data was gathered
     */
    private Date time;

    /**
     * ID of the source we got this data from
     */
    private Integer source;

    /**
     * construct rate
     * @param iName rate name
     * @param iRate rate rate
     * @param iTime rate time
     * @param iSource source ID
     */
    public ResRate(final String iName, final Double iRate, final Date iTime, final Integer iSource) {
        this.name = iName;
        this.rate = iRate;
        this.time = iTime;
        this.source = iSource;
    }

    /**
     * Alternative constructor for admin page
     * @param iName rate name
     * @param iTime rate time
     * @param iSource rate source
     */
    public ResRate(final String iName, final Date iTime, final Integer iSource) {
        this.name = iName;
        this.time = iTime;
        this.source = iSource;
    }

    /**
     * get rate name
     * @return rate name
     */
    public final String getName() {
        return name;
    }

    /**
     * set new rate name
     * @param newName new rate name
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * get rate
     * @return rate
     */
    public final Double getRate() {
        return rate;
    }

    /**
     * set new exchange rate
     * @param newRate new rate
     */
    public final void setRate(final Double newRate) {
        this.rate = newRate;
    }

    /**
     * get rate's timestamp
     * @return rate time
     */
    public final Date getTime() {
        return time;
    }

    /**
     * set new time
     * @param newTime new time
     */
    public final void setTime(final Date newTime) {
        this.time = newTime;
    }

    /**
     * get rate ID
     * @return rate ID
     */
    public final Integer getSource() {
        return source;
    }

    /**
     *  set new source ID
     * @param newSource source ID
     */
    public final void setSource(final Integer newSource) {
        this.source = newSource;
    }
}