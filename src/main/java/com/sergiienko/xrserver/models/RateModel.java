package com.sergiienko.xrserver.models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;

/**
 * Exchange rate model for DB
 */
@Entity
@Table(name = "rates")
public class RateModel {
    /**
     * ID of the rate
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the currency
     */
    @Column(name = "name")
    private String name;

    /**
     * Exchange rate of the currency
     */
    @Column(name = "rate")
    private Double rate;

    /**
     * ID of the source we get this data from
     */
    @Column(name = "source")
    private Integer source;

    /**
     * Time when we got this data
     */
    @Column(name = "time")
    private Date time;

    /**
     * Create rate object
     */
    public RateModel() {
    }

    /**
     * Construct new rate object
     * @param iCurrency currency name
     * @param iRate exchange rate
     * @param iSource data source ID
     */
    public RateModel(final String iCurrency, final Double iRate, final Integer iSource) {
        this.name = iCurrency;
        this.rate = iRate;
        this.source = iSource;
        this.time = new Date();
    }

    /**
     * Get rate ID
     * @return rate ID
     */
    public final Long getId() {
        return id;
    }

    /**
     * Set rate ID
     * @param newId rate ID
     */
    public final void setId(final Long newId) {
        this.id = newId;
    }

    /**
     * Get currency name
     * @return currency name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set new currency name
     * @param newName new currency name
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get source ID
     * @return source ID
     */
    public final Integer getSource() {
        return source;
    }

    /**
     * Set new rate source ID
     * @param newSource new source ID
     */
    public final void setSource(final Integer newSource) {
        this.source = newSource;
    }

    /**
     * Get rate
     * @return rate value
     */
    public final Double getRate() {
        return rate;
    }

    /**
     * Set new rate
     * @param newRate new rate
     */
    public final void setRate(final Double newRate) {
        this.rate = newRate;
    }

    /**
     * Get rate time
     * @return rate time
     */
    public final Date getTime() {
        return time;
    }

    /**
     * Set new rate time
     * @param newTime new rate time
     */
    public final void setTime(final Date newTime) {
        this.time = newTime;
    }
}
