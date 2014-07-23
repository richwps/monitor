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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DataAccessService {

    private final Map<String, Factory<? extends DataAccess>> dataAccessFactories;
    private static final ThreadLocal<DataAccess> threadDaos;

    static {
        threadDaos = new ThreadLocal<DataAccess>();
    }

    public DataAccessService() {
        dataAccessFactories = new HashMap<String, Factory<? extends DataAccess>>();
    }

    public void registerDataAccess(final String accessName, final Factory<? extends DataAccess> daoFactory) throws DataAccesNameAlreadyRegistred {
        if (dataAccessFactories.containsKey(accessName)) {
            throw new DataAccesNameAlreadyRegistred();
        }

        dataAccessFactories.put(accessName, Validate.notNull(daoFactory, "daoFactory"));
    }

    public Boolean isNameRegistred(final String accessName) {
        return dataAccessFactories.containsKey(accessName);
    }

    public <T> T getNewDataAcces(final String accessName) throws CreateException {
        DataAccess dao = dataAccessFactories
                .get(accessName)
                .create();

        return castSpecific(dao);
    }

    public <T> T getThreadDataAccess(final String accessName) throws CreateException {
        DataAccess dao = threadDaos.get();

        if (dao == null) {
            dao = getNewDataAcces(accessName);

            threadDaos.set(dao);
        }

        return castSpecific(dao);
    }

    private <T> T castSpecific(Object returnValue) {
        return (T) returnValue
                .getClass()
                .cast(returnValue);
    }
}
