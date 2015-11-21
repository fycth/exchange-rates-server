package com.sergiienko.xrserver.models;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name="rates")
public class RateModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "source")
    private Integer source;

    @Column(name = "time")
    private Date time;

    public RateModel() {}

    public RateModel(String currency, Double rate, Integer source) {
        this.name = currency;
        this.rate = rate;
        this.source = source;
        this.time = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
