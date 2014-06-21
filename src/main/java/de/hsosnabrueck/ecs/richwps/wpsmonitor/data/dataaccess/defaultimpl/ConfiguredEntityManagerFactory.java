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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class ConfiguredEntityManagerFactory {
    private static String PERSISTENCE_UNIT;
    private static final EntityManagerFactory emf;
    
    private static ThreadLocal<EntityManager> entityStorage;
    
    static {
        PERSISTENCE_UNIT = "de.hsosnabrueck.ecs.richwps_WPSMonitor_pu";
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        entityStorage = new ThreadLocal<EntityManager>();
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
    
    public static void close() {
        if(emf.isOpen()) {
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

    public static EntityManager getThreadEntityManager() {
        EntityManager em = entityStorage.get();
        
        if(em == null || !em.isOpen()) {
            em = createEntityManager();
            entityStorage.set(em);
        }
        
        return em;
    }
    
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public static void setPersistenceUnitName(String persistenceUnitName) {
        ConfiguredEntityManagerFactory.PERSISTENCE_UNIT = persistenceUnitName;
    }

    public static String getPersistenceUnitName() {
        return ConfiguredEntityManagerFactory.PERSISTENCE_UNIT;
    }
}
