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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.structures;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.GuiErrorException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Wps {
    private String identifier;
    private URI uri;

    public Wps(String identifier, URI uri) throws GuiErrorException {
        setIdentifier(identifier);
        setUri(uri);
    }
    
    public Wps(String identifier, String uri) throws GuiErrorException {
        setIdentifier(identifier);
        setUri(uri);
    }

    public String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(String identifier) throws GuiErrorException {
        String ident = Param.notNull(identifier, "identifier");
        
        if(ident.trim().equals("")) {
            throw new GuiErrorException("Identifier must not be empty");
        }
        
        this.identifier = ident;
    }

    public URI getUri() {
        return uri;
    }

    public final void setUri(URI uri) {
        this.uri = Param.notNull(uri, "uri");
    }
    
    public final void setUri(String uriStr) throws GuiErrorException {
        try {
            URL urlCheck = new URL(Param.notNull(uriStr, "wpsUri"));
            URI uri = new URI(urlCheck.toString());

            this.setUri(uri);
        } catch (MalformedURLException ex) {
            throw new GuiErrorException("The entered URI is not valid!");
        } catch (URISyntaxException ex) {
            throw new GuiErrorException("The entered URI is not valid!");
        }
    }
}
