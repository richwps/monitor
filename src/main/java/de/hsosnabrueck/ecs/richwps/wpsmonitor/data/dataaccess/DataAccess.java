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

/**
 * Primary Interface for DataAccess-Objects
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 * @param <T> Type of Entity which should the dataaccess implementation take care
 */
public interface DataAccess<T> {
    /**
     * Trys to find an Object in the Database with the given PrimaryKey.
     * Otherwhise null will be returned.
     * 
     * @param primaryKey The Primarykey of the Object. Typically a Long value.
     * @return The Object that match the given primarykey. Otherwise null will be returned if no result is founded.
     */
    public T find(Object primaryKey);
    
    /**
     * Trys to persist the given object.
     * 
     * @param entityObject Entity instance
     * @return True if the object is sucessfully persisted, otherwise false will be returned
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
     * Selects all Elements in range of range of [offset, count]
     * @see Range
     * @param range Range instance
     * @return All founded elements, otherwise null or a empty list
     */
    public List<T> get(final Range range);
    
    /**
     * Commit the transaction.
     * If an exception occours, then commit will return false. An Exception
     * can occour if a primary key violation is happened or something else
     * 
     * @return false if an exception is happened.
     */
    public Boolean commit();
    
    /**
     * Rolls the transacton back
     */
    public void rollback();
    
    /**
     * Per default, alle actions will be autocommited. 
     * 
     * @param value false for deactivate autocommit, otherwise true
     */
    public void setAutoCommit(Boolean value);
}
