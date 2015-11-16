package com.sergiienko.xrserver.models;

import javax.persistence.*;

/**
 * Author: ${FULLNAME}
 * Date: 11/11/15
 * Time: 5:33 PM
 */
@Entity
@Table(name = "xml_parsers")
public class XMLParserModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @Column(name = "source")
    public Long source;

    @Column(name = "pattern_section")
    public String pattern_section;

    @Column(name = "pattern_currency")
    public String pattern_currency;

    @Column(name = "pattern_rate")
    public String pattern_rate;

    @Column(name = "attribute_currency")
    public String attribute_currency;

    @Column(name = "attribute_rate")
    public String attribute_rate;

    public XMLParserModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public String getPattern_section() {
        return pattern_section;
    }

    public void setPattern_section(String pattern_section) {
        this.pattern_section = pattern_section;
    }

    public String getPattern_currency() {
        return pattern_currency;
    }

    public void setPattern_currency(String pattern_currency) {
        this.pattern_currency = pattern_currency;
    }

    public String getPattern_rate() {
        return pattern_rate;
    }

    public void setPattern_rate(String pattern_rate) {
        this.pattern_rate = pattern_rate;
    }

    public String getAttribute_currency() {
        return attribute_currency;
    }

    public void setAttribute_currency(String attribute_currency) {
        this.attribute_currency = attribute_currency;
    }

    public String getAttribute_rate() {
        return attribute_rate;
    }

    public void setAttribute_rate(String attribute_rate) {
        this.attribute_rate = attribute_rate;
    }
}
