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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Properties extends java.util.Properties {

    protected Properties defaultProperties;

    private final static Logger log = LogManager.getLogger();

    public Properties() {
        super();
    }

    public Properties(Properties defaultProperties) {
        super(defaultProperties);

        this.defaultProperties = defaultProperties;
    }

    public Integer getIntegerProperty(final String propertyKey) {
        String propertyValue = super.getProperty(propertyKey);
        Integer intValue = null;

        try {
            intValue = Integer.parseInt(propertyValue);
        } catch (NumberFormatException ex) {
            try {
                log.warn(ex);

                propertyValue = defaultProperties.getProperty(propertyKey);
                intValue = Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                log.error(ex);
            }
        }

        return intValue;
    }

    public Boolean getBooleanProperty(final String propertyKey) {
        Boolean boolValue = _getBooleanProperty(this, propertyKey);

        if (boolValue == null) {
            boolValue = _getBooleanProperty(defaultProperties, propertyKey);
        }

        return boolValue;
    }

    private Boolean _getBooleanProperty(Properties prop, String propertyKey) {
        String propertyValue = prop.getProperty(propertyKey);
        Boolean boolValue = null;

        if (propertyValue.equalsIgnoreCase("true")) {
            boolValue = true;
        } else if (propertyValue.equalsIgnoreCase("false")) {
            boolValue = false;
        }

        return boolValue;
    }

    public Date getDateProperty(final String propertyKey, final String dateFormat) {
        Date result = _getDateProperty(this, propertyKey, dateFormat);

        if (result == null) {
            result = _getDateProperty(defaultProperties, propertyKey, dateFormat);
        }

        return result;
    }
    
    public Calendar getCalendarProperty(final String propertyKey, final String dateFormat) {
        Date dateProperty = getDateProperty(propertyKey, dateFormat);
        
        Calendar result = Calendar.getInstance();
        result.setTime(dateProperty);
        
        return result;
    }

    private Date _getDateProperty(Properties prob, String propertyKey, String dateFormat) {
        String property = prob.getProperty(propertyKey);
        Date result = null;

        try {
            result = new SimpleDateFormat(dateFormat).parse(property);
        } catch (ParseException ex) {
            log.warn(ex);
        }

        return result;
    }
}
