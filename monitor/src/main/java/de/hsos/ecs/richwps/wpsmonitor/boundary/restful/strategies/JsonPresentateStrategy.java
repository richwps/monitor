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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategies;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.Hide;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.PresentateStrategy;

/**
 * The prefered PresentateStrategy. It uses Gson.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class JsonPresentateStrategy implements PresentateStrategy {

    @Override
    public String presentate(Object presentateObjects) {
        // make new gson object to support multithreading environment
        Gson gson = new GsonBuilder().setExclusionStrategies(
                new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(Hide.class) != null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> arg0) {
                        return false;
                    }
                }
        ).setPrettyPrinting()
                .serializeNulls()
                .create();

        return gson.toJson(presentateObjects);
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }
}
