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
package de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.defaultimpl;

import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.simple.SimpleWpsClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsProcessInfo;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsRequest;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsResponse;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SimpleWpsClientTest {

    private static final String WPS_URI = "http://localhost:8080/wps/WebProcessingService";
    private static final String TEST_REQUEST_FILE = "/request.xml";
    private static final String TEST_RESPONSE_EXCEPTION_FILE = "/exception.xml";
    private static final String TEST_VALID_RESPONSE_FILE = "/response.xml";
    
    private static WpsClientFactory wpsClientFactory;

    private static String rawRequest;
    private static String exceptionResponse;

    private static Path getFilePath(String fileName) {
        String strPath = SimpleWpsClientTest.class.getResource(fileName).getPath();

        // Windows Path fix
        if (strPath.charAt(2) == ':') {
            strPath = strPath.substring(1);
        }

        return Paths.get(strPath);
    }
    
    private static String getFileContent(String fileName) {
        StringBuilder buf = new StringBuilder();

        try(Scanner scan = new Scanner(getFilePath(fileName), StandardCharsets.UTF_8.name())) {
            while (scan.hasNextLine()) {
                buf.append(scan.nextLine());
            }
        } catch (IOException ex) {
            Logger.getLogger(SimpleWpsClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return buf.toString();
    }

    @BeforeClass
    public static void setUpClass() {
        wpsClientFactory = new WpsClientFactory(new SimpleWpsClientFactory());
        
        
        rawRequest = getFileContent(TEST_REQUEST_FILE);
        exceptionResponse = getFileContent(TEST_RESPONSE_EXCEPTION_FILE);
    }

    @AfterClass
    public static void tearDownClass() {
    }
    private WpsProcessInfo info;
    private WpsClient client;

    public SimpleWpsClientTest() {
    }

    @Before
    public void setUp() {
        try {
            client = wpsClientFactory.create();
        } catch (CreateException ex) {
            fail("can't create WPS");
        }
        if (rawRequest == null || rawRequest.isEmpty()) {
            fail("Can't load testrequest from file");
        }
        try {
            info = new WpsProcessInfo(new URL(WPS_URI), rawRequest);
        } catch (MalformedURLException ex) {
            fail("Invalid URI");
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class SimpleWpsClient.
     */
    @Test
    public void testValidExecute() {
        WpsResponse response = doDefaultRequest();
        Assert.assertTrue(response.isConnectionException() || !response.isException() && response.getResponseBody() != null && !response.getResponseBody().isEmpty());
    }

    private WpsResponse doDefaultRequest() {
        WpsRequest request = new WpsRequest(rawRequest, info);

        return client.execute(request);
    }

    @Test
    public void testRequestDateIsSetFromClient() {
        WpsRequest request = new WpsRequest(rawRequest, info);
        request.prepareRequest();

        Date dateRef = request.getRequestTime();

        client.execute(request);

        Assert.assertTrue(!request.getRequestTime().equals(dateRef));
    }

    @Test
    public void responseDateIsSet() {
        WpsResponse response = doDefaultRequest();

        Assert.assertTrue(response.isConnectionException() || response.getResponseTime() != null);
    }

    @Test
    public void testExceptionIdentificationIdentifyException() {
        WpsResponse response = new WpsResponse(exceptionResponse, new Date());
        
        try {
            Method method = client.getClass().getDeclaredMethod("lookupForExceptions", WpsResponse.class);
            method.setAccessible(true);
            method.invoke(client, response);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            fail(ex.getMessage());
        }
        
        Assert.assertTrue(response.isWpsException());
    }
    
    @Test
    public void testExceptionIdentificationDoNotIdentifyExceptionAtValidResponse() {
        WpsResponse response = new WpsResponse(getFileContent(TEST_VALID_RESPONSE_FILE), new Date());
        
        try {
            Method method = client.getClass().getDeclaredMethod("lookupForExceptions", WpsResponse.class);
            method.setAccessible(true);
            method.invoke(client, response);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            fail(ex.getMessage());
        }
        
        Assert.assertTrue(!response.isWpsException());
    }

    @Test
    public void testExceptionIdentificationDoNotIdentifyExceptionAtUnvalidResponse() {
        WpsResponse response = new WpsResponse("Hello World", new Date());
        
        try {
            Method method = client.getClass().getDeclaredMethod("lookupForExceptions", WpsResponse.class);
            method.setAccessible(true);
            method.invoke(client, response);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            fail(ex.getMessage());
        }
        
        Assert.assertTrue(response.isConnectionException() && !response.isWpsException());
    }
}
