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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class AbstractDataAccess<T>  {

    private static String PERSISTENCE_UNIT = "de.hsosnabrueck.ecs.richwps_WPSMonitor_pu";
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

    protected EntityManager em;

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

    public AbstractDataAccess() {
        em = AbstractDataAccess.emf.createEntityManager();
    }

    public static void setPersistenceUnitName(String persistenceUnitName) {
        AbstractDataAccess.PERSISTENCE_UNIT = persistenceUnitName;
    }

    public static String getPersistenceUnitName() {
        return AbstractDataAccess.PERSISTENCE_UNIT;
    }

    public Boolean persist(T o) {
        beginTransaction();

        try {
            em.persist(o);
        } catch (EntityExistsException e) {
            return false;
        }
        commit();

        return true;
    }

    public T update(T t) {
        beginTransaction();
        T merged = em.merge(t);
        commit();

        return merged;
    }

    public void remove(final T o) {
        beginTransaction();
        em.remove(o);
        commit();
    }

    protected void beginTransaction() {
        em.getTransaction().begin();
    }

    protected void commit() {
        em.getTransaction().commit();
    }

    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

    protected List<T> getBy(final String queryName, final Class c) {
        return getBy(queryName, null, c);
    }

    protected List<T> getBy(final String queryName, final Map<String, Object> parameters, final Class c) {
        return getBy(queryName, parameters, c, null, null);
    }

    protected List<T> getBy(final String queryName,
            final Map<String, Object> parameters,
            final Class c,
            final Integer start,
            final Integer count) {
        List<T> result = null;

        TypedQuery<T> query = em
                .createNamedQuery(queryName, c);

        if (parameters != null) {
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                query.setParameter(e.getKey(), e.getValue());
            }
        }

        try {
            result = query.getResultList();
        } catch (NoResultException ex) {

        }

        return result;
    }

    @Override
    public void finalize() throws Throwable {
        try {
            this.close();
        } finally {
            super.finalize();
        }
    }
}
