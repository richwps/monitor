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
package de.hsos.ecs.richwps.wpsmonitor.data.dataaccess;

import java.util.List;

/**
 * Primary Interface for {@link GenericDataAccess}-Objects
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 * @param <T> Type of Entity which the {@link GenericDataAccess} implementation should
 * take care of
 */
public interface GenericDataAccess<T> extends AutoCloseable {

    /**
     * Tries to find an Object in the Database with the given primary key.
     * Otherwhise null will be returned.
     *
     * @param primaryKey The primary key of the Object. Typically a Long value.
     * @return The Object that match the given primarykey. Otherwise null is
     * returned if no result is found.
     */
    public T find(Long primaryKey);

    /**
     * Tries to persist the given object.
     *
     * @param entityObject Entity instance
     * @return True if the object is sucessfully persisted, otherwise false will
     * be returned
     */
    public Boolean persist(T entityObject);

    /**
     * Updates the given Object in the Database.
     *
     * @param entityObject Entity instance
     * @return The updated object.
     */
    public T update(T entityObject);

    /**
     * Removed o from Database
     *
     * @param o Entity instance
     */
    public void remove(final T o);

    /**
     * Selects all elements in the given range [offset, count]
     *
     * @see Range
     * @param range Range instance
     * @return All found elements, otherwise null or an empty list
     */
    public List<T> get(final Range range);

    /**
     * Commit the transaction. If an exception occours, then commit will return
     * false. An Exception can occour if a primary key violation is happened or
     * something else
     *
     * @return If an exception has been thrown
     */
    public Boolean commit();

    /**
     * Rolls the transacton back
     */
    public void rollback();

    /**
     * By default all actions will be autocommited.
     *
     * @param value false to deactivate autocommit, otherwise true
     */
    public void setAutoCommit(Boolean value);

    /**
     * Should be called if the GenericDataAccess is no longer needed. The specific
 implementation of the GenericDataAccess interface need to decide what is to do.
     */
    @Override
    public void close();
}
