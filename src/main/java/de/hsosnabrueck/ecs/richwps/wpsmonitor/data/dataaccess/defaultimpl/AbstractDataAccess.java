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
 * Implementation of some default operations with an EntityManager instance.
 *
 * @see EntityManager
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class AbstractDataAccess<T> {

    /**
     * Indicates if auto commit is active By default, all operations start a
     * transaction and commit the transaction after all actions are down
     */
    protected Boolean autoCommit;
    protected final static Logger log = LogManager.getLogger();

    /**
     * Default constructor
     */
    public AbstractDataAccess() {
        this.autoCommit = true;
    }

    /**
     * Gets an EntityManager instance. The InitJpa
 ensures that every thread gets its own EntityManager instance
     * 
     * @return EntityManager instance
     */
    protected EntityManager getEntityManager() {
        return InitJpa
                .getThreadEntityManager();
    }

    /**
     * Stores an object.
     *
     * @param o Entity instance to store
     * @return True if successfully stored, otherwise false
     */
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

    /**
     * Merges/updates an object
     *
     * @param t Entity instance to update/merge
     * @return The updated Entity-instance
     */
    public T update(T t) {
        beginTransaction();
        T merged = getEntityManager()
                .merge(t);

        requestCommit();

        return merged;
    }

    /**
     * Removes the given entity instance
     *
     * @param o Entity instance to remove
     */
    public void remove(final T o) {
        beginTransaction();
        // remanage the entity
        T mergedEntity = getEntityManager()
                .merge(o);

        getEntityManager()
                .remove(mergedEntity);

        requestCommit();
    }

    /**
     * Starts a transaction if no current transaction is active.
     */
    protected void beginTransaction() {
        if (!getEntityManager().getTransaction().isActive()) {
            getEntityManager()
                    .getTransaction()
                    .begin();
        }
    }

    /**
     * Commits a transaction.
     *
     * @return Should be alway true, if an exception occurs, false is
     * returned
     */
    public Boolean commit() {
        try {
            if (getEntityManager().getTransaction().isActive()) {
                getEntityManager()
                        .getTransaction()
                        .commit();
            }
        } catch (Exception ex) {
            log.debug(ex);

            return false;
        }

        return true;
    }

    /**
     * Requests a commit; if autoCommit is active, then it will be commited.
     *
     * @return true or false, depends on commit return, or null if autocommit
     * is disabled
     */
    protected Boolean requestCommit() {
        if (autoCommit) {
            return commit();
        }

        return null;
    }

    /**
     * If a transaction is active, the result will be resetted.
     */
    public void rollback() {
        if (getEntityManager().getTransaction().isActive()) {
            getEntityManager()
                    .getTransaction()
                    .rollback();
        }
    }

    /**
     * Sets autoCommit on or off.
     *
     * @param value true or fals for enable or disable autoCommit behavior
     */
    public void setAutoCommit(Boolean value) {
        if (value == null) {
            value = false;
        }

        autoCommit = value;
    }

    /**
     * Helper method to execute a named query.
     *
     * @param queryName Name of the named query
     * @param returnTypeClass Result class datatype
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName, final Class returnTypeClass) {
        return getBy(queryName, null, returnTypeClass);
    }

    /**
     * Helper Method to execute a named query.
     *
     * @param queryName Name of the named query
     * @param returnTypeClass Result class datatype
     * @param range Range instance
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName, final Class returnTypeClass, final Range range) {
        return getBy(queryName, null, returnTypeClass, range);
    }

    /**
     * Helper Method to execute a named query.
     *
     * @param queryName Name of the named query
     * @param parameters Parameter Map with &lt;ParameterNameInNamedQuery,
     * Parameter>
     * @param returnTypeClass Result class datatype
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName, final Map<String, Object> parameters, final Class returnTypeClass) {
        return getBy(queryName, parameters, returnTypeClass, null);
    }

    /**
     * Helper Method to execute a named query.
     *
     * @param queryName Name of the named query
     * @param parameters Parameter Map with &lt;ParameterNameInNamedQuery,
     * Parameter>
     * @param returnTypeClass Result class datatype
     * @param range Range instance
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName,
            final Map<String, Object> parameters,
            final Class returnTypeClass,
            final Range range) {

        List<T> result = null;

        TypedQuery<T> query = getEntityManager()
                .createNamedQuery(queryName, returnTypeClass);

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

    /**
     * Helper Method to do a named query operation, like update or delete.
     *
     * @param queryName Name of the named query
     * @param parameters Parameter Map with &lt;ParameterNameInNamedQuery,
     * Parameter>
     * @return Number of effected rows
     */
    protected Integer doNamedQuery(final String queryName, final Map<String, Object> parameters) {
        beginTransaction();

        Query query = getEntityManager()
                .createNamedQuery(queryName);

        assignParameters(query, parameters);

        Integer affectedRows = query.executeUpdate();
        
        
        //getEntityManager().flush();
        
        requestCommit();
        
        return affectedRows;
    }

    /**
     * Helper method to assign the parameter map to a query.
     *
     * @param queryName Name of the named query
     * @param parameters Parameter Map with &lt;ParameterNameInNamedQuery,
     * Parameter>
     */
    private void assignParameters(Query query, final Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                query.setParameter(e.getKey(), e.getValue());
            }
        }
    }

    /**
     *
     */
    public void close() {
        if (getEntityManager().isOpen()) {
            getEntityManager().close();
        }
    }
}
