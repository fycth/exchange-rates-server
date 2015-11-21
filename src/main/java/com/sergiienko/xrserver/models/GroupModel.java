package com.sergiienko.xrserver.models;

import org.hibernate.annotations.Type;
import javax.persistence.*;

@Entity
@Table(name = "groups")
public class GroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String descr;

    @Column(name = "sources")
    @Type(type = "com.sergiienko.xrserver.hibernate.IntegerArrayType")
    private Integer[] sources;

    @Column(name = "dflt")
    private Boolean defaultGroup;

    public GroupModel() {}

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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Integer[] getSources() {
        return sources;
    }

    public void setSources(Integer[] sources) {
        this.sources = sources;
    }

    public Boolean getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(Boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
