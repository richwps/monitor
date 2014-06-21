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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class AbstractDataAccess<T> {
    protected Boolean autoCommit;
    private final static Logger log = LogManager.getLogger();

    public AbstractDataAccess() {
        this.autoCommit = true;
    }
    
    protected EntityManager getEntityManager() {
        return ConfiguredEntityManagerFactory
                .getThreadEntityManager();
    }

    public Boolean persist(T o) {
        Boolean result = true;
        beginTransaction();

        try {
            getEntityManager()
                    .persist(o);
            
            requestCommit();
        } catch (EntityExistsException e) {
            log.debug(e);
            
            result = false;
        } 
        

        return result;
    }

    public T update(T t) {
        beginTransaction();
        T merged = getEntityManager()
                .merge(t);
        
        requestCommit();

        return merged;
    }

    public void remove(final T o) {
        beginTransaction();
        getEntityManager()
                .remove(o);
        
        requestCommit();
    }

    protected void beginTransaction() {
        if(!getEntityManager().getTransaction().isActive()) {
            getEntityManager()
                    .getTransaction()
                    .begin();
        }
    }

    public Boolean commit() {
        try {
            if(getEntityManager().getTransaction().isActive()) {
                getEntityManager()
                        .getTransaction()
                        .commit();
            }
        } catch(Exception ex) {
            log.debug(ex);
            
            return false;
        }
        
        return true;
    }
    
    protected Boolean requestCommit() {
        if(autoCommit) {
            return commit();
        }
        
        return null;
    }
    
    public void rollback() {
        if(getEntityManager().getTransaction().isActive()) {
            getEntityManager()
                    .getTransaction()
                    .rollback();
        }
    }
    
    public void setAutoCommit(Boolean value) {
        autoCommit = value;
    }

    protected List<T> getBy(final String queryName, final Class c) {
        return getBy(queryName, null, c);
    }

    protected List<T> getBy(final String queryName, final Class c, final Range range) {
        return getBy(queryName, null, c, range);
    }

    protected List<T> getBy(final String namedQueryIdentifier, final Map<String, Object> parameters, final Class typeClass) {
        return getBy(namedQueryIdentifier, parameters, typeClass, null);
    }

    protected List<T> getBy(final String namedQueryIdentifier,
            final Map<String, Object> parameters,
            final Class typeClass,
            final Range range) {

        List<T> result = null;

        TypedQuery<T> query = getEntityManager()
                .createNamedQuery(namedQueryIdentifier, typeClass);

        assignParameters(query, parameters);

        if (range != null) {
            if (range.getOffset() != null) {
                query.setFirstResult(range.getOffset());
            }

            if (range.getCount() != null) {
                query.setMaxResults(range.getCount());
            }
        }

        try {
            result = query.getResultList();
        } catch (NoResultException ex) {

        }

        return result;
    }

    protected Integer doNamedQuery(final String namedQueryIdentifier, final Map<String, Object> parameters) {
        beginTransaction();

        Query query = getEntityManager()
                .createNamedQuery(namedQueryIdentifier);

        assignParameters(query, parameters);

        Integer affectedRows = query.executeUpdate();
        requestCommit();
        
        return affectedRows;
    }

    private void assignParameters(Query query, final Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                query.setParameter(e.getKey(), e.getValue());
            }
        }
    }
}
