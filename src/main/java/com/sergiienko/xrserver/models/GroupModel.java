package com.sergiienko.xrserver.models;

import org.hibernate.annotations.Type;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;

/**
 * Sources group model for DB
 */
@Entity
@Table(name = "groups")
public class GroupModel {
    /**
     * Group ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Name of the group
     */
    @Column(name = "name")
    private String name;

    /**
     * Group description
     */
    @Column(name = "descr")
    private String descr;

    /**
     * List of sources arranged to this group
     */
    @Column(name = "sources")
    @Type(type = "com.sergiienko.xrserver.hibernate.IntegerArrayType")
    private Integer[] sources;

    /**
     * If the group is default
     */
    @Column(name = "dflt")
    private Boolean defaultGroup;

    /**
     * Create new group object
     */
    public GroupModel() {
    }

    /**
     * Get group ID
     * @return group ID
     */
    public final Integer getId() {
        return id;
    }

    /**
     * Set new group ID
     * @param newId new group ID
     */
    public final void setId(final Integer newId) {
        this.id = newId;
    }

    /**
     * Get group name
     * @return group name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set group name
     * @param newName group name
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get group description
     * @return group description
     */
    public final String getDescr() {
        return descr;
    }

    /**
     * Set group description
     * @param newDescr group description
     */
    public final void setDescr(final String newDescr) {
        this.descr = newDescr;
    }

    /**
     * Get list of group sources
     * @return list of group sources arranged in the group
     */
    public final Integer[] getSources() {
        return sources;
    }

    /**
     * Set list of sources arranged in the group
     * @param newSources list of sources
     */
    public final void setSources(final Integer[] newSources) {
        this.sources = newSources;
    }

    /**
     * Check if the group is the default group
     * @return default value
     */
    public final Boolean getDefaultGroup() {
        return defaultGroup;
    }

    /**
     * Set group default value
     * @param newDefaultGroup default value
     */
    public final void setDefaultGroup(final Boolean newDefaultGroup) {
        this.defaultGroup = newDefaultGroup;
    }
}
