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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasources;

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSource;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceCreator;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsDescription;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsProcessDescription;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.Vocabulary;
import de.hsos.richwps.sp.client.ows.gettypes.Network;
import de.hsos.richwps.sp.client.ows.gettypes.Process;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SemanticProxy client implementation of DataSourceCreator and DataSource.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SemanticProxyData extends DataSourceCreator implements DataSource {

    private static final String SP_VOCABULARY_PATH = "/semanticproxy/resources/vocab";
    private static final String SP_ROOT = "/semanticproxy/resources";

    private static final Logger LOG = LogManager.getLogger();

    private SPClient spClient;
    private DataSourceCreator creator;
    private String resource;

    @Override
    public void init(final DataSourceCreator creator, final String resource) throws DataSourceException {
        this.creator = creator;
        this.resource = resource;

        connect();
    }

    private void connect() throws DataSourceException {
        try {
            Vocabulary.init(new URL(resource + SP_VOCABULARY_PATH));

            spClient = SPClient.getInstance();
            spClient.setRootURL(resource + SP_ROOT);

        } catch (MalformedURLException ex) {
            LOG.error("Can't execute connect-Method. The given URL is not valid.", ex);

            throw new DataSourceException("The given URL is not valid!");
        } catch (Exception ex) {
            LOG.error("Can't execute connect-Method. Uknow Exception occours.", ex);

            throw new DataSourceException("Uknow Exception occured:\n " + ex.toString());
        }
    }

    @Override
    public List<WpsDescription> getWpsList() throws DataSourceException {
        List<WpsDescription> result = new ArrayList<>();

        try {
            Network net = spClient.getNetwork();

            for (WPS wps : net.getWPSs()) {
                URL uri = new URL(wps.getEndpoint());
                Set<WpsProcessDescription> processes = new HashSet<>();

                for (Process process : wps.getProcesses()) {
                    processes.add(getDescription(process));
                }

                String wpsIdentifier = generateWpsIdentifier(uri.toString());
                WpsDescription wpsDescription = new WpsDescription(uri, processes);

                result.add(wpsDescription);
            }     
        } catch (Exception ex) {
            throw new DataSourceException("Exception at getWpsList-method implementation of SemanticProxy-Client.", ex);
        }

        return result;
    }

    private String generateWpsIdentifier(String uri) {
        String withoutHttp = uri.substring(7);

        return withoutHttp.substring(0, withoutHttp.indexOf('/'));
    }

    private WpsProcessDescription getDescription(Process process) throws RDFException {
        String title = process.getTitle();
        String strAbstract = process.getAbstract();
        String identifier = process.getIdentifier(); 
        String version = process.getProcessVersion();

        return new WpsProcessDescription(identifier, title, strAbstract, version);
    }

    @Override
    public String getCreatorName() {
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
        return creator.getCreatorName();
    }

    @Override
    public String getRessource() {
        return resource;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.spClient != null ? this.spClient.hashCode() : 0);
        hash = 97 * hash + (this.creator != null ? this.creator.hashCode() : 0);
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
        return this.creator == other.creator || (this.creator != null && this.creator.equals(other.creator));
    }
}
