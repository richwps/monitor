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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.client.defaultimpl;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsConnectionException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SimpleWpsClient implements WpsClient {

    private String wpsExceptionMessage;

    @Override
    public WpsResponse execute(WpsRequest wpsRequest) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        String responseBody = null;
        Date responseTime = null;
        WpsResponse response = null;

        if (wpsRequest != null) {
            try {
                // build http request
                HttpPost httpRequest = buildRequest(wpsRequest);

                // prepare request (init requestTime)
                // and do request 
                wpsRequest.prepareRequest();
                CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
                responseTime = new Date();

                // get response body
                HttpEntity responseEntity = httpResponse.getEntity();
                responseBody = EntityUtils.toString(responseEntity);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SimpleWpsClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SimpleWpsClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            // create response Object
            response = new WpsResponse(responseBody, responseTime);

            // set exception if necessary
            if (responseBody == null) {
                response.setException(new WpsConnectionException());
            } else try {
                if (isWpsException(responseBody)) {
                    WpsException ex;
                    
                    if(wpsExceptionMessage == null || wpsExceptionMessage.equals("")) {
                        ex = new WpsException();
                    } else {
                        ex = new WpsException(wpsExceptionMessage);
                    }
                    
                    response.setException(ex);
                }
            } catch (NoWpsResponse ex) {
                response.setException(new WpsConnectionException());
            }
        }

        return response;
    }

    private HttpPost buildRequest(final WpsRequest wpsRequest) throws UnsupportedEncodingException {
        HttpPost httpRequest = new HttpPost(wpsRequest.getProcessInfo().getWpsUri());
        List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair("request", wpsRequest.getRawRequest()));

        httpRequest.setEntity(new UrlEncodedFormEntity(body));
       
        return httpRequest;
    }

    private Boolean isWpsException(final String responseBody) throws NoWpsResponse {
        try {
            Document doc = getDocumentBuilder().parse(new InputSource(new StringReader(responseBody)));
            NodeList nl = doc.getElementsByTagNameNS("*", "ExceptionText");

            if (nl.getLength() > 0) {
                StringBuilder strBuilder = new StringBuilder();

                for (int i = 0; i < nl.getLength(); i++) {
                    strBuilder.append(nl.item(i).getTextContent());
                    strBuilder.append("\n----\n");
                }

                wpsExceptionMessage = strBuilder.toString();
                
                return true;
            } else {
                return false;
            }
        } catch (SAXException ex) {
            Logger.getLogger(SimpleWpsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleWpsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SimpleWpsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch(DOMException ex) {
            Logger.getLogger(SimpleWpsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        throw new NoWpsResponse();
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        
        return docBuilderFactory.newDocumentBuilder();
    }
}
