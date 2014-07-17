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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.strategies;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.PresentateStrategy;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class JaxbPresentateStrategy implements PresentateStrategy {

    private JAXBContext xmlContext;

    final static Logger log = LogManager.getLogger();

    public JaxbPresentateStrategy(Class[] classes) {
        try {
            xmlContext = JAXBContext.newInstance(classes);
        } catch (JAXBException ex) {
            log.error(ex);
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
            log.error(ex);
        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(JaxbPresentateStrategy.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
