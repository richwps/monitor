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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.impl;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class AbstractDataAccess<T> {

    protected EntityManager em;

    public AbstractDataAccess(EntityManager em) {
        this.em = Param.notNull(em, "EntityManager em");
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
    
    protected List<T> getBy(final String queryName, final Class c, final Integer offset, final Integer maxResult) {
        return getBy(queryName, null, c, offset, maxResult);
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

        if (start != null) {
            query.setFirstResult(start);
        }

        if (count != null) {
            query.setMaxResults(count);
        }

        try {
            result = query.getResultList();
        } catch (NoResultException ex) {

        }

        return result;
    }
}
