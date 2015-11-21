package com.sergiienko.xrserver.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;

/**
 * rates data source model for DB
 */
@Entity
@Table(name = "sources")
public class SourceModel {
    /**
     * ID of the source
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Name of the source
     */
    @Column(name = "name")
    private String name;

    /**
     * URL of the source
     */
    @Column(name = "url")
    private String url;

    /**
     * description of the source
     */
    @Column(name = "descr")
    private String descr;

    /**
     * Class name of the source, which can serve the source
     */
    @Column(name = "parser_class_name")
    private String parserClassName;

    /**
     * If the source is enabled or not
     */
    @Column(name = "enabled")
    private Boolean enabled;

    /**
     * Construct new source
     */
    public SourceModel() {
    }

    /**
     * Get source ID
     * @return source ID
     */
    public final Integer getId() {
        return id;
    }

    /**
     * Set new source ID
     * @param newId new source ID
     */
    public final void setId(final Integer newId) {
        this.id = newId;
    }

    /**
     * Get source name
     * @return source name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set new source name
     * @param newName new name
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get source's URL
     * @return source URL
     */
    public final String getUrl() {
        return url;
    }

    /**
     * Set source URL
     * @param newUrl new source URL
     */
    public final void setUrl(final String newUrl) {
        this.url = newUrl;
    }

    /**
     * Get source description
     * @return source description
     */
    public final String getDescr() {
        return descr;
    }

    /**
     * Set description of the source
     * @param newDescr new source description
     */
    public final void setDescr(final String newDescr) {
        this.descr = newDescr;
    }

    /**
     * Get class name of the parser which can serve this source
     * @return parser class name
     */
    public final String getParserClassName() {
        return parserClassName;
    }

    /**
     * Set class name of the parser which can serve this source
     * @param newParserClassName new parser class name
     */
    public final void setParserClassName(final String newParserClassName) {
        this.parserClassName = newParserClassName;
    }

    /**
     * Is the source enabled
     * @return enabled value
     */
    public final Boolean getEnabled() {
        return enabled;
    }

    /**
     * Enable or disable source
     * @param newEnabled is the source enabled
     */
    public final void setEnabled(final Boolean newEnabled) {
        this.enabled = newEnabled;
    }
}
