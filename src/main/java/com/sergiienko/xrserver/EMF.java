package com.sergiienko.xrserver;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Here we contain entity manager factory
 */
public final class EMF {
    /**
     * Entity manager factory object, used for getting entity manager objects
     */
    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY;
    static {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("com.sergiienko.xrserver.jpa");
    }

    /**
     * Dumb constructor
     */
    private EMF() {
    }

    /**
     * Close entity manager factory
     */
    public static void closeFactory() {
        ENTITY_MANAGER_FACTORY.close();
    }
}
