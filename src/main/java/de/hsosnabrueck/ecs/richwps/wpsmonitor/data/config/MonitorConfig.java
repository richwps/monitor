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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Representate the configuration of a {@link Monitor} instance. For this
 * purpose, the MonitorConfig will be used a extended version of the
 * {@link Properties} class. All configurations will be read out of a
 * properties file. If the save-method is called, all changed properties will be
 * written back into the properties file.
 *
 * If a config is not valid, then the default properties is used as fallback.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class MonitorConfig {

    private Integer deleteIntervalInDays;
    private Calendar deleteTime;
    private Boolean deleteJobActiv;

    private final Properties properties;
    private final File propertiesFile;

    private final static Logger log;
    private final static Properties defaultProperties;

    static {
        log = LogManager.getLogger();
        defaultProperties = new Properties();

        initDefaultProperties();
    }

    private static void initDefaultProperties() {
        defaultProperties.setProperty("qos.delete.afterdays", "360");
        defaultProperties.setProperty("qos.delete.attime", "9:00");
        defaultProperties.setProperty("qos.delete", "true");
    }

    public MonitorConfig(final File propertiesFile) throws MonitorConfigException {
        this.properties = new Properties(defaultProperties);
        this.propertiesFile = propertiesFile == null && propertiesFile.exists() ? new File("monitor.properties") : propertiesFile;
        /* Using magic string because, if you use a valid propertie file, 
         then you can decide which name the propertie file has */

        initPropertiesObject();
        readProperties();
    }

    private void initPropertiesObject() {
        if (propertiesFile.exists()) {

            try {
                FileInputStream fileInputStream = new FileInputStream(propertiesFile);

                properties.load(fileInputStream);
            } catch (FileNotFoundException ex) {
                log.error(ex);
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }

    private void readProperties() throws MonitorConfigException {
        Integer afterDays = properties.getIntegerProperty("qos.delete.afterdays");
        Calendar atTime = properties.getCalendarProperty("qos.delete.attime", "HH:mm");
        Boolean cleanupJobActive = properties.getBooleanProperty("qos.delete");

        if (afterDays == null) {
            throw new MonitorConfigException("Properties error: qos.delete.afterdays need to be an integer value");
        }

        if (atTime == null) {
            throw new MonitorConfigException("Properties error: qos.delete.attime is not in the right format. (24h format; HH:mm).");
        }

        if (cleanupJobActive == null) {
            throw new MonitorConfigException("Properties error:qos.delete need to be a boolean value");
        }

        setDeleteIntervalInDays(afterDays);
        setDeleteJobActiv(cleanupJobActive);
        setDeleteTime(atTime);
    }

    private void assignVarsToPropertieObj() {
        Integer hour = deleteTime.get(Calendar.HOUR_OF_DAY);
        Integer minute = deleteTime.get(Calendar.MINUTE);

        properties.setProperty("qos.delete.afterdays", deleteIntervalInDays.toString());
        properties.setProperty("qos.delete.attime", hour.toString() + ":" + minute.toString());
        properties.setProperty("qos.delete", deleteJobActiv ? "true" : "false");
    }

    /**
     * Try to save the changed properties into the defined properties file
     */
    public void save() {
        assignVarsToPropertieObj();

        try {
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }

            properties.store(new FileOutputStream(propertiesFile), null);
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    /**
     * Value which indicates in which interval captured Qos measurement data
     * should be deleted
     *
     * @return Integer instance
     */
    public Integer getDeleteIntervalInDays() {
        return deleteIntervalInDays;
    }

    /**
     * Value which indicates in which interval captured Qos measurement data
     * should be deleted
     *
     * @param deleteIntervalInDays Integer instance
     */
    public synchronized void setDeleteIntervalInDays(Integer deleteIntervalInDays) {
        if (deleteIntervalInDays != null && deleteIntervalInDays > 0) {
            this.deleteIntervalInDays = deleteIntervalInDays;
        }
    }

    /**
     * Calendar instance which contains the point of time (HH:mm) when Qos measurement data
     * should be deleted (Trigger-time of the cleanup job is meant here).
     *
     * @return Calendar instance
     */
    public Calendar getDeleteTime() {
        return deleteTime;
    }

    /**
     * Calendar instance which contains the point of time (HH:mm) when Qos measurement data
     * should be deleted (Trigger-time of the cleanup job is meant here).
     *
     * @param deleteTime Calendar instance
     */
    public synchronized void setDeleteTime(Calendar deleteTime) {
        if (deleteTime != null) {
            this.deleteTime = deleteTime;
        }
    }

    /**
     * Checks if the cleanup job is active.
     *
     * @return true if active, otherwise false
     */
    public Boolean isDeleteJobActiv() {
        return deleteJobActiv;
    }

    /**
     * Sets the cleanup job to active.
     *
     * @param deleteJobActiv true for active, otherwise false
     */
    public synchronized void setDeleteJobActiv(Boolean deleteJobActiv) {
        if (deleteJobActiv != null) {
            this.deleteJobActiv = deleteJobActiv;
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
