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

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.PresentateStrategy;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A not useful implementation of a JAXB presentate strategy. The problem is,
 * that you must registered als used classes. Maybe a algorithmen to gather
 * the classes will be usefull.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class JaxbPresentateStrategy implements PresentateStrategy {

    private static final Logger LOG = LogManager.getLogger();
    private JAXBContext xmlContext;

    public JaxbPresentateStrategy(Class[] classes) {
        try {
            xmlContext = JAXBContext.newInstance(classes);
        } catch (JAXBException ex) {
            LOG.error("Can't create JAXBContext in Strategy.", ex);
        }
    }

    @Override
    public String presentate(Object presentate) {
        try {
            StringWriter writer = new StringWriter();
            Marshaller m = xmlContext.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(presentate, writer);

            return writer.toString();
        } catch (PropertyException ex) {
            LOG.error("Can't set property of Marshaller.", ex);
        } catch (JAXBException ex) {
            LOG.error("Can't create Marshaller or marshalling the Object instace to presentate.", ex);
        }

        return null;
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
