/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class ConfiguredEntityManagerFactory {

    private static String PERSISTENCE_UNIT;
    private static final EntityManagerFactory emf;

    private static final ThreadLocal<EntityManager> entityStorage;
    private static final List<EntityManager> entityManagerList;

    private static final Logger log = LogManager.getLogger();

    static {
        PERSISTENCE_UNIT = "de.hsosnabrueck.ecs.richwps_WPSMonitor_pu";
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        entityStorage = new ThreadLocal<EntityManager>();
        entityManagerList = new ArrayList<EntityManager>();
    }

    private final Object finalizerGuardian = new Object() {
        @Override
        protected void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
        }
    };

    /**
     * Closes the entitymanager factory and all used entitymanagers
     */
    public static void close() {
        for (EntityManager e : entityManagerList) {
            if (e.isOpen()) {
                log.debug("Close EntityManager...");
                e.close();
            }
        }

        if (emf.isOpen()) {
            log.debug("Close EntityManager Factory...");
            emf.close();
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            ConfiguredEntityManagerFactory.close();
        } finally {
            super.finalize();
        }
    }

    /**
     * Returns an {@link EntityManager} which is stored by the called Thread.
     * @return EntityManager instance
     */
    public static EntityManager getThreadEntityManager() {
        EntityManager em = entityStorage.get();

        if (em == null || !em.isOpen()) {
            em = createEntityManager();
            entityStorage.set(em);
            entityManagerList.add(em);
        }

        return em;
    }

    /**
     * Creates an {@link EntityManager}
     * 
     * @return EntityManager instance
     */
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Set the persistenceUnitName
     * 
     * @param persistenceUnitName 
     */
    public static void setPersistenceUnitName(String persistenceUnitName) {
        ConfiguredEntityManagerFactory.PERSISTENCE_UNIT = persistenceUnitName;
    }

    /**
     * Get PersistenceUnitName
     * 
     * @return Persistence unit name identifier
     */
    public static String getPersistenceUnitName() {
        return ConfiguredEntityManagerFactory.PERSISTENCE_UNIT;
    }
}
