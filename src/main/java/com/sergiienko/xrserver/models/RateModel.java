package com.sergiienko.xrserver.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Author: ${FULLNAME}
 * Date: 11/11/15
 * Time: 5:33 PM
 */
@Entity
@Table(name="rates")
@XmlRootElement()
public class RateModel implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "rate")
    public Double rate;

    @Column(name = "source")
    public Long source;

    @Column(name = "time")
    public Date time;

    public RateModel() {}

    public RateModel(String currency, String rate, Long source) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
        this.name = currency;
        this.rate = Double.parseDouble(rate);
        this.source = source;
//        this.time = calendar.getTime();
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

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
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
