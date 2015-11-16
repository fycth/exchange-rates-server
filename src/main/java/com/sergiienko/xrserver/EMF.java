package com.sergiienko.xrserver;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Author: ${FULLNAME}
 * Date: 11/11/15
 * Time: 8:04 PM
 */
public class EMF {
    public static final EntityManagerFactory entityManagerFactory;
    static {
        entityManagerFactory = Persistence.createEntityManagerFactory("com.sergiienko.xrserver.jpa");
    }
    public static void Close() {
        entityManagerFactory.close();
    }
}
