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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.client.mok;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.ConnectionException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsClientMok implements WpsClient {

    @Override
    public WpsResponse execute(final WpsRequest request) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String responseBody = null;

        try {
            CloseableHttpResponse httpResponse = httpClient.execute(buildRequest(request));
            HttpEntity responseEntity = httpResponse.getEntity();
            responseBody = EntityUtils.toString(responseEntity);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WpsClientMok.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WpsClientMok.class.getName()).log(Level.SEVERE, null, ex);
        }

        WpsResponse response = new WpsResponse(responseBody);
        
        if (responseBody == null) {
            response.setException(new ConnectionException());
        } else if (isWpsException(responseBody)) {
            response.setException(new WpsException());
        }
        
        return response;
    }

    private HttpPost buildRequest(final WpsRequest request) throws UnsupportedEncodingException {
        HttpPost httpRequest = new HttpPost(request.getProcessInfo().getWpsUri());
        List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair("request", request.getRawRequest()));

        httpRequest.setEntity(new UrlEncodedFormEntity(body));

        return httpRequest;
    }
    
    private Boolean isWpsException(final String requestString) {
        return false;
    }
}
