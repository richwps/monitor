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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy;

/**
 * Strategy pattern interface for the representation of the data inside a
 * webbrowser.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface PresentateStrategy {

    /**
     * Transforms the given Object instance into a String.
     *
     * @param presentate Object instance
     * @return String
     */
    public String presentate(Object presentate);

    /**
     * Returns the mimeTime of the specific strategy.
     *
     * @return String
     */
    public String getMimeType();
}
