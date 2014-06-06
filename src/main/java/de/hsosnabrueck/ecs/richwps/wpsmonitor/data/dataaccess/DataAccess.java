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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class DataAccess<T> {
    private static String PERSISTENCE_UNIT = "de.hsosnabrueck.ecs.richwps_WPSMonitor_pu";
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    protected EntityManager em;

    public DataAccess() {
        em =  DataAccess.emf.createEntityManager();
    }
    
    public static void setPersistenceUnitName(String persistenceUnitName) {
        DataAccess.PERSISTENCE_UNIT = persistenceUnitName;
    }
    
    public static String getPersistenceUnitName() {
        return DataAccess.PERSISTENCE_UNIT;
    }
    
    public abstract T find(Object primaryKey);

    public void persist(T o) {
        beginTransaction();
        em.persist(o);
        commit();
    }

    public T merge(T t) {
        beginTransaction();
        T merged = em.merge(t);
        commit();
        
        return merged;
    }

    public void remove(T o) {
        beginTransaction();
        em.remove(o);
        commit();
    }
    
    protected List<T> getBy(String queryName, Class c) {
        return getBy(queryName, null, c);
    }
    
    protected List<T> getBy(String queryName, Map<String, Object> parameters, Class c) {
        List<T> result = null;
        
        TypedQuery<T> query = em
                .createNamedQuery(queryName, c);
        
        if(parameters != null) {
            for(Map.Entry<String, Object> e : parameters.entrySet()) {
                query.setParameter(e.getKey(), e.getValue());
            }
        }
        
        try {
            result = query.getResultList();
        } catch(NoResultException ex) {
            
        }
        
        return result;
    }
    
    protected void beginTransaction() {
        em.getTransaction().begin();
    }
    
    protected void commit() {
        em.getTransaction().commit();
    }
}
