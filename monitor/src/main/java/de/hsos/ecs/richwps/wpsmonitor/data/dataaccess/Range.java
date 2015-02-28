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

/**
 * Class to define a Range between "offset" and "count"; offset defines the
 * start value and count the total value of elements.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Range {

    /**
     * Start value
     */
    private final Integer offset;

    /**
     * Total number of elements
     */
    private final Integer count;

    /**
     * Constructor.
     *
     * @param offset Startvalue
     * @param count Total number of elements
     */
    public Range(final Integer offset, final Integer count) {
        if (offset != null && offset < 0
                || count != null && count < 0) {
            throw new IllegalArgumentException("Count and offset need to be greater than zero.");
        }

        this.offset = offset;
        this.count = count;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getCount() {
        return count;
    }
}
