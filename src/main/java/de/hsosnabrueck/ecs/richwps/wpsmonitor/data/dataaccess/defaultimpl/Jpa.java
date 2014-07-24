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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
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
public final class Jpa {

    private EntityManagerFactory emf;

    private ThreadLocal<EntityManager> entityStorage;
    private List<EntityManager> entityManagerList;

    private static final Logger LOG = LogManager.getLogger();
    private final String puUnit;

    public Jpa(String puUnit) {
        this.puUnit = Validate.notNull(puUnit, "puUnit");
    }

    public void open() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(puUnit);
            entityStorage = new ThreadLocal<EntityManager>();
            entityManagerList = new ArrayList<EntityManager>();
        }
    }

    /**
     * Closes the entitymanager factory and all used entitymanagers
     */
    public void close() {
        for (EntityManager e : entityManagerList) {
            if (e.isOpen()) {
                LOG.debug("Close EntityManager...");
                e.close();
            }
        }

        if (emf.isOpen()) {
            LOG.debug("Close EntityManager Factory...");
            emf.close();

            entityStorage = null;
            entityManagerList = null;
        }
    }

    /**
     * Returns an {@link EntityManager} which is stored by the called Thread.
     *
     * @return EntityManager instance
     */
    public EntityManager getThreadEntityManager() {
        EntityManager em = entityStorage.get();

        if (em == null || !em.isOpen()) {
            em = createEntityManager();
            entityStorage.set(em);
            entityManagerList.add(em);
        }

        return em;
    }

    /**
     * Creates an {@link EntityManager}.
     *
     * @return EntityManager instance
     */
    public EntityManager createEntityManager() {
        if(!emf.isOpen()) {
            throw new AssertionError("EntityManagerFactory is not opened!");
        }
        
        return emf.createEntityManager();
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
}
