package com.sergiienko.xrserver.rest.resources;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class ResRate {
    public String name;
    public Double rate;
    public Date time;
    public Long source;

    public ResRate(String name, Double rate, Date time, Long source) {
        this.name = name;
        this.rate = rate;
        this.time = time;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }
}