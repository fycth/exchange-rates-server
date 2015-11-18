package com.sergiienko.xrserver.models;

import javax.persistence.*;

@Entity
@Table(name="sources")
public class SourceModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "name")
    public String name;

    @Column(name = "url")
    public String url;

    @Column(name = "descr")
    public String descr;

    @Column(name = "parser_class_name")
    public String parser_class_name;

    @Column(name = "enabled")
    public Boolean enabled;

    public SourceModel() {}

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

    public String getParserClassName() {
        return parser_class_name;
    }

    public void setParserClassName(String parser_class_name) {
        this.parser_class_name = parser_class_name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
