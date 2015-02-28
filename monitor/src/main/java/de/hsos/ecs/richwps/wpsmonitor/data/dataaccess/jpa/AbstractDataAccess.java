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
package de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa;

import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.GenericDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public abstract class AbstractDataAccess<T> implements GenericDataAccess<T> {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Indicates if auto commit is active By default, all operations start a
     * transaction and commit the transaction after all actions are down
     */
    protected Boolean autoCommit;
    protected Class<T> typeParameterClass;
    private final Jpa jpa;

    /**
     * Default constructor
     *
     * @param jpa JPA instance
     * @param typeParameterClass
     */
    public AbstractDataAccess(final Jpa jpa, final Class<T> typeParameterClass) {
        this.autoCommit = true;
        this.jpa = Validate.notNull(jpa, "JPA Instance");
        this.typeParameterClass = Validate.notNull(typeParameterClass, "typeParameterClass");
    }

    @Override
    public T find(final Long primaryKey) {
        return getEntityManager().find(typeParameterClass, primaryKey);
    }

    /**
     * Gets an EntityManager instance. The Jpa ensures that every thread gets
     * its own EntityManager instance
     *
     * @return EntityManager instance
     */
    protected EntityManager getEntityManager() {
        return jpa.getThreadEntityManager();
    }

    /**
     * Stores an object.
     *
     * @param o Entity instance to store
     * @return True if successfully stored, otherwise false
     */
    @Override
    public Boolean persist(T o) {
        Boolean result = true;
        beginTransaction();

        getEntityManager()
                .persist(o);

        Boolean commitResult = requestCommit();

        if (commitResult != null) {
            result = commitResult;
        }

        return result;
    }

    /**
     * Merges/updates an object
     *
     * @param t Entity instance to update/merge
     * @return The updated Entity-instance
     */
    @Override
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
    @Override
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
     * @return Should be alway true, if an exception occurs, false is returned
     */
    @Override
    public Boolean commit() {
        try {
            if (getEntityManager().getTransaction().isActive()) {
                getEntityManager()
                        .getTransaction()
                        .commit();
            }
        } catch (Exception ex) {
            LOG.debug("Exception occourd at commiting Changes to the database.", ex);

            return false;
        }

        return true;
    }

    /**
     * Requests a commit; if autoCommit is active, then it will be commited.
     *
     * @return true or false, depends on commit return, or null if autocommit is
     * disabled
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
    @Override
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
    @Override
    public void setAutoCommit(Boolean value) {
        if (value != null) {
            autoCommit = value;
        }
    }

    /**
     * Executs a named query which is identified by queryName. Only 1000 Rows
     * would be fetched (starts at row 0). For more control about the selected
     * range of rows, call getBy(queryName : String, range : Range)
     *
     * @param queryName Name of the named query
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName) {
        return getBy(queryName, new Range(0, 1000));
    }

    /**
     * Helper Method to execute a named query.
     *
     * @param queryName Name of the named query
     * @param range Range instance
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName, final Range range) {
        return getBy(queryName, null, range);
    }

    /**
     * Executs a named query which is identified by queryName. Only 1000 Rows
     * would be fetched (starts at row 0). You can put Parameters as a HashMap.
     * The : before a parameters identifier is not necessary. For more control
     * about the selected range of rows, call getBy(queryName : String,
     * parameters : Map<>, range : Range)
     *
     * @param queryName Name of the named query
     * @param parameters Parameter Map with &lt;ParameterNameInNamedQuery,
     * Parameter>
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName, final Map<String, Object> parameters) {
        return getBy(queryName, parameters, new Range(0, 1000));
    }

    /**
     * Helper Method to execute a named query.
     *
     * @param queryName Name of the named query
     * @param parameters Parameter Map with &lt;ParameterNameInNamedQuery,
     * Parameter>
     * @param range Range instance
     * @return List of entity instances results
     */
    protected List<T> getBy(final String queryName,
            final Map<String, Object> parameters,
            final Range range) {

        List<T> result;

        TypedQuery<T> query = getEntityManager()
                .createNamedQuery(queryName, typeParameterClass);

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
            result = new ArrayList<>();
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
     * Calls the close-Method on the used EntityManager-Instance, if the
     * EntityManager instance is open
     */
    @Override
    public void close() {
        if (getEntityManager().isOpen()) {
            getEntityManager().close();
        }
    }
}
