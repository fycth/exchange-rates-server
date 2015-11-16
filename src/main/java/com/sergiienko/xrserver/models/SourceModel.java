package com.sergiienko.xrserver.models;

import javax.persistence.*;

/**
 * Author: ${FULLNAME}
 * Date: 11/11/15
 * Time: 5:33 PM
 */
@Entity
@Table(name="sources")
public class SourceModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "url")
    public String url;

    @Column(name = "descr")
    public String descr;

    @Column(name = "type")
    public int type;

    public SourceModel() {}

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
