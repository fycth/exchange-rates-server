package com.sergiienko.xrserver.models;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name="rates")
public class RateModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "name")
    public String name;

    @Column(name = "rate")
    public Double rate;

    @Column(name = "source")
    public Integer source;

    @Column(name = "time")
    public Date time;

    public RateModel() {}

    public RateModel(String currency, Double rate, Integer source) {
        this.name = currency;
        this.rate = rate;
        this.source = source;
        this.time = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
