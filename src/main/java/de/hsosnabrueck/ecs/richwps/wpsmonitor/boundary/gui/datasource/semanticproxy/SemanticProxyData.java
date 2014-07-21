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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.semanticproxy;

import de.hsos.richwps.sp.client.CommunicationException;
import de.hsos.richwps.sp.client.InternalSPException;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.ResourceNotFoundException;
import de.hsos.richwps.sp.client.rdf.RDFID;
import de.hsos.richwps.sp.client.wps.Network;
import de.hsos.richwps.sp.client.wps.Process;
import de.hsos.richwps.sp.client.wps.SPClient;
import de.hsos.richwps.sp.client.wps.Vocabulary;
import de.hsos.richwps.sp.client.wps.WPS;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataDriver;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSource;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsDescription;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsProcessDescription;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SemanticProxyData extends DataDriver implements DataSource {

    private static final String SP_VOCABULARY_PATH = "/semanticproxy/resources/vocab";
    private static final String SP_ROOT = "/semanticproxy/resources";

    private static final Logger log = LogManager.getLogger();
    private SPClient spClient;
    private DataDriver driver;
    private String resource;

    @Override
    public void init(final DataDriver driver, final String resource) throws DataSourceException {
        this.driver = driver;
        this.resource = resource;
        
        connect();
    }
    
    private void connect() throws DataSourceException {
        try {
            Vocabulary.init(new URL(resource + SP_VOCABULARY_PATH));

            spClient = SPClient.getInstance();
            spClient.setRootURL(resource + SP_ROOT);

        } catch (MalformedURLException ex) {
            log.error(ex);

            throw new DataSourceException("The given URL is not valid!");
        } catch (Exception ex) {
            log.error(ex);

            throw new DataSourceException("Uknow Exception occured:\n " + ex.toString());
        }
    }

    @Override
    public List<WpsDescription> getWpsList() {
        List<WpsDescription> result = new ArrayList<WpsDescription>();

        try {
            Network net = spClient.getNetwork();

            for (WPS wps : net.getWPSs()) {
                URI uri = new URI(wps.getEndpoint());
                Set<WpsProcessDescription> processes = new HashSet<WpsProcessDescription>();
                
                for(Process process : wps.getProcesses()) {
                    processes.add(getDescription(process));
                }
                
                WpsDescription wpsDescription = new WpsDescription(uri, processes);

                result.add(wpsDescription);
            }
        } catch (ResourceNotFoundException ex) {
            log.error(ex);
        } catch (InternalSPException ex) {
            log.error(ex);
        } catch (CommunicationException ex) {
            log.error(ex);
        } catch (RDFException ex) {
            log.error(ex);
        } catch (URISyntaxException ex) {
            log.error(ex);
        }

        return result;
    }

    private WpsProcessDescription getDescription(Process process) {
        WpsProcessDescription result = null;
        
        try {
            String title = process.getTitle();
            String strAbstract = process.getAbstract();
            String identifier = process.getIdentifier();
            String version = process.getProcessVersion();
            
            result = new WpsProcessDescription(identifier, title, strAbstract, version);
        } catch (RDFException ex) {
            java.util.logging.Logger.getLogger(SemanticProxyData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    @Override
    public String getDriverName() {
        return "SemanticProxy-Client";
    }

    @Override
    public String getExpectedResourceType() {
        return "URI";
    }

    @Override
    protected DataSource createDataSource() {
        return new SemanticProxyData();
    }

    @Override
    public String getUsedDriver() {
        return driver.getDriverName();
    }

    @Override
    public String getRessource() {
        return resource;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.spClient != null ? this.spClient.hashCode() : 0);
        hash = 97 * hash + (this.driver != null ? this.driver.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SemanticProxyData other = (SemanticProxyData) obj;
        if (this.spClient != other.spClient && (this.spClient == null || !this.spClient.equals(other.spClient))) {
            return false;
        }
        if (this.driver != other.driver && (this.driver == null || !this.driver.equals(other.driver))) {
            return false;
        }
        return true;
    }
}
