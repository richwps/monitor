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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extension of the {@link java.util.Properties} class at convert methods.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Properties extends java.util.Properties {

    protected Properties defaultProperties;

    private static final Logger LOG = LogManager.getLogger();

    public Properties() {
        super();
    }

    public Properties(Properties defaultProperties) {
        super(defaultProperties);

        this.defaultProperties = defaultProperties;
    }

    /**
     * Tries to convert the property that matches the propertyKey into a Integer
     * value.
     *
     * @param propertyKey Index of the property
     * @return Integer instance
     */
    public Integer getIntegerProperty(final String propertyKey) {
        String propertyValue = super.getProperty(propertyKey);
        Integer intValue = null;

        try {
            intValue = Integer.parseInt(propertyValue);
        } catch (NumberFormatException ex) {
            try {
                LOG.warn("Can't convert Property with Key \"{}\" to Integer. Try to convert default Property.", propertyKey);

                propertyValue = defaultProperties.getProperty(propertyKey);
                intValue = Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                LOG.error("Can't convert Default-Property with Key \"{}\" to Integer.", propertyKey);
            }
        } catch (NullPointerException ex) {
            LOG.error("Propertie was null. Exception was: {}", ex);
        }

        return intValue;
    }

    /**
     * Tries to convert the property that matches the propertyKey into a Boolean
     * value.
     *
     * @param propertyKey Index of the property
     * @return Boolean instance
     */
    public Boolean getBooleanProperty(final String propertyKey) {
        Boolean boolValue = null;

        try {
            boolValue = _getBooleanProperty(this, propertyKey);

            if (boolValue == null) {
                boolValue = _getBooleanProperty(defaultProperties, propertyKey);
            }
        } catch (NullPointerException ex) {
            LOG.error("Propertie was null. Exception was: {}", ex);
        }

        return boolValue;
    }

    private Boolean _getBooleanProperty(Properties prop, String propertyKey) {
        String propertyValue = prop.getProperty(propertyKey);
        Boolean boolValue = null;

        if ("true".equalsIgnoreCase(propertyValue)) {
            boolValue = true;
        } else if ("false".equalsIgnoreCase(propertyValue)) {
            boolValue = false;
        }

        return boolValue;
    }

    /**
     * Tries to convert the property that matches the propertyKey into a Date
     * instance.
     *
     * @param propertyKey Index of the property
     * @param dateFormat date-Format e.g. HH:mm:ss
     * @return Date instance
     */
    public Date getDateProperty(final String propertyKey, final String dateFormat) {
        Date result = null;

        try {
            result = _getDateProperty(this, propertyKey, dateFormat);

            if (result == null) {
                result = _getDateProperty(defaultProperties, propertyKey, dateFormat);
            }
        } catch (NullPointerException ex) {
            LOG.error("Propertie was null. Exception was: {}", ex);
        }

        return result;
    }

    /**
     * Tries to convert the property that matches the propertyKey into a
     * Calendar instance.
     *
     * @param propertyKey Index of the property
     * @param dateFormat date-Format e.g. HH:mm:ss
     * @return Calendar instance
     */
    public Calendar getCalendarProperty(final String propertyKey, final String dateFormat) {
        Calendar result = null;

        try {
            Date dateProperty = getDateProperty(propertyKey, dateFormat);

            result = Calendar.getInstance();
            result.setTime(dateProperty);
        } catch (NullPointerException ex) {
            LOG.error("Propertie was null. Exception was: {}", ex);
        }

        return result;
    }

    private Date _getDateProperty(Properties prob, String propertyKey, String dateFormat) {
        String property = prob.getProperty(propertyKey);
        Date result = null;

        try {
            result = new SimpleDateFormat(dateFormat).parse(property);
        } catch (ParseException ex) {
            LOG.warn("Can't parse the Date. Exception was: {}", ex);
        }

        return result;
    }
}
