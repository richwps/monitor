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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Absract EntityConverter which is fill up with AbstractQosEntity for which
 * this converter is registred.
 * 
 * The assignation is evaluated over AbstractQosEntity's  getEntityName-method
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class EntityConverter {
    private final List<AbstractQosEntity> entities;
    
    public EntityConverter() {
        entities = new ArrayList<AbstractQosEntity>();
    }
    
    /**
     * Adds an abstractQosEntity instance to the converter
     * 
     * @param data AbstractQosEntity instance
     */
    public void add(final AbstractQosEntity data) {
        entities.add(data);
    }

    /**
     * Gets all added entities
     * 
     * @return List of AbstractQosEntity instances
     */
    public List<AbstractQosEntity> getEntities() {
        return entities;
    }
    
    /**
     * Execute the convert process
     * 
     * @return Object which will be outputet over toString or JAXB or JSON ..
     */
    public abstract Object convert();
}
